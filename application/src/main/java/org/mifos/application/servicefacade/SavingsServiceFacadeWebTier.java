/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.servicefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.interest.CalendarPeriod;
import org.mifos.accounts.savings.interest.CalendarPeriodHelper;
import org.mifos.accounts.savings.interest.EndOfDayDetail;
import org.mifos.accounts.savings.interest.InterestCalculationPeriodCalculator;
import org.mifos.accounts.savings.interest.InterestCalculationPeriodDetail;
import org.mifos.accounts.savings.interest.InterestCalculationPeriodResult;
import org.mifos.accounts.savings.interest.InterestCalculator;
import org.mifos.accounts.savings.interest.InterestPostingPeriodResult;
import org.mifos.accounts.savings.interest.NonCompoundingInterestCalculator;
import org.mifos.accounts.savings.interest.SavingsInterestCalculatorFactory;
import org.mifos.accounts.savings.interest.SavingsInterestDetail;
import org.mifos.accounts.savings.interest.SavingsProductHistoricalInterestDetail;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.SavingsInterestScheduledEventFactory;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.util.helpers.AccountPaymentData;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.config.AccountingRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.dto.domain.SavingsAccountClosureDto;
import org.mifos.dto.domain.SavingsAdjustmentDto;
import org.mifos.dto.domain.SavingsDepositDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

public class SavingsServiceFacadeWebTier implements SavingsServiceFacade {

    private static final Logger logger = LoggerFactory.getLogger(SavingsServiceFacadeWebTier.class);

    private final SavingsDao savingsDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();
    private CalendarPeriodHelper interestCalculationIntervalHelper = new CalendarPeriodHelper();
    private SavingsInterestScheduledEventFactory savingsInterestScheduledEventFactory = new SavingsInterestScheduledEventFactory();

    public SavingsServiceFacadeWebTier(SavingsDao savingsDao, PersonnelDao personnelDao, CustomerDao customerDao) {
        this.savingsDao = savingsDao;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
    }

    @Override
    public void deposit(SavingsDepositDto savingsDeposit) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());

        SavingsBO savingsAccount = this.savingsDao.findById(savingsDeposit.getSavingsId());

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(Short.valueOf((short) user.getUserId()));

        CustomerBO customer = this.customerDao.findCustomerById(savingsDeposit.getCustomerId().intValue());

        Money totalAmount = new Money(savingsAccount.getCurrency(), BigDecimal.valueOf(savingsDeposit.getAmount()));
        PaymentData payment = PaymentData.createPaymentData(totalAmount, createdBy, savingsDeposit.getModeOfPayment()
                .shortValue(), savingsDeposit.getDateOfDeposit().toDateMidnight().toDate());
        if (savingsDeposit.getDateOfReceipt() != null) {
            payment.setReceiptDate(savingsDeposit.getDateOfReceipt().toDateMidnight().toDate());
        }
        payment.setReceiptNum(savingsDeposit.getReceiptId());
        payment.setCustomer(customer);

        for (AccountActionDateEntity installment : savingsAccount.getTotalInstallmentsDue(savingsDeposit
                .getCustomerId().intValue())) {
            AccountPaymentData accountPaymentData = new SavingsPaymentData(installment);
            payment.addAccountPaymentData(accountPaymentData);
        }

        try {
            this.transactionHelper.startTransaction();
            savingsAccount.applyPayment(payment);

            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (AccountException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    public void setTransactionHelper(HibernateTransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public void withdraw(SavingsWithdrawalDto savingsWithdrawal) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());

        SavingsBO savingsAccount = this.savingsDao.findById(savingsWithdrawal.getSavingsId());

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(Short.valueOf((short) user.getUserId()));

        CustomerBO customer = this.customerDao.findCustomerById(savingsWithdrawal.getCustomerId().intValue());

        Money totalAmount = new Money(savingsAccount.getCurrency(), BigDecimal.valueOf(savingsWithdrawal.getAmount()));
        PaymentData payment = PaymentData.createPaymentData(totalAmount, createdBy, savingsWithdrawal
                .getModeOfPayment().shortValue(), savingsWithdrawal.getDateOfWithdrawal().toDateMidnight().toDate());
        if (savingsWithdrawal.getDateOfReceipt() != null) {
            payment.setReceiptDate(savingsWithdrawal.getDateOfReceipt().toDateMidnight().toDate());
        }
        payment.setReceiptNum(savingsWithdrawal.getReceiptId());
        payment.setCustomer(customer);

        try {
            this.transactionHelper.startTransaction();
            savingsAccount.withdraw(payment, false);

            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (AccountException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public void adjustTransaction(SavingsAdjustmentDto savingsAdjustment) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());

        PersonnelBO updatedBy = this.personnelDao.findPersonnelById(userContext.getId());
        SavingsBO savingsAccount = this.savingsDao.findById(savingsAdjustment.getSavingsId());
        savingsAccount.updateDetails(userContext);

        Money amountAdjustedTo = new Money(savingsAccount.getCurrency(), BigDecimal.valueOf(savingsAdjustment
                .getAdjustedAmount()));

        try {
            this.transactionHelper.startTransaction();

            savingsAccount.adjustLastUserAction(amountAdjustedTo, savingsAdjustment.getNote(), updatedBy);

            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e.getMessage(), e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    public void setInterestCalculationIntervalHelper(CalendarPeriodHelper interestCalculationIntervalHelper) {
        this.interestCalculationIntervalHelper = interestCalculationIntervalHelper;
    }

    public void setSavingsInterestScheduledEventFactory(
            SavingsInterestScheduledEventFactory savingsInterestScheduledEventFactory) {
        this.savingsInterestScheduledEventFactory = savingsInterestScheduledEventFactory;
    }

    /**
     * This method is responsible for posting interest for the last posting period only.
     *
     * It assumes that interest calculation and interest posting frequencies cannot change on savings product of savings account.
     * It assumes that the interest posting date is correct and valid with respect to the interest posting frequency of the product/account.
     */
    @Override
    public void postInterestForLastPostingPeriod(LocalDate dateBatchJobIsScheduled) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());

        List<Integer> accountIds = this.savingsDao.retrieveAllActiveAndInActiveSavingsAccountsPendingInterestPostingOn(dateBatchJobIsScheduled);
        for (Integer savingsId : accountIds) {
            SavingsBO savingsAccount = this.savingsDao.findById(Long.valueOf(savingsId));
            savingsAccount.updateDetails(userContext);

            List<EndOfDayDetail> allEndOfDayDetailsForAccount = savingsDao.retrieveAllEndOfDayDetailsFor(savingsAccount.getCurrency(), Long.valueOf(savingsId));

            LocalDate interestPostingDate = new LocalDate(savingsAccount.getNextIntPostDate());

            InterestScheduledEvent postingSchedule = savingsInterestScheduledEventFactory.createScheduledEventFrom(savingsAccount.getInterestPostingMeeting());
            LocalDate startOfPeriod = postingSchedule.findFirstDateOfPeriodForMatchingDate(interestPostingDate);
            CalendarPeriod lastInterestPostingPeriod = new CalendarPeriod(startOfPeriod, interestPostingDate);

            InterestPostingPeriodResult interestPostingPeriodResult = determinePostingPeriodResult(lastInterestPostingPeriod, savingsAccount, allEndOfDayDetailsForAccount);
            savingsAccount.postInterest(postingSchedule, interestPostingPeriodResult, createdBy);

            StringBuilder postingInfoMessage = new StringBuilder().append("account id: ").append(
                    savingsAccount.getAccountId()).append("posting interest: ").append(interestPostingPeriodResult);

            logger.info(postingInfoMessage.toString());

            try {
                this.transactionHelper.startTransaction();

                this.savingsDao.save(savingsAccount);
                this.transactionHelper.commitTransaction();
            } catch (Exception e) {
                this.transactionHelper.rollbackTransaction();
                throw new BusinessRuleException(savingsAccount.getAccountId().toString(), e);
            } finally {
                this.transactionHelper.closeSession();
            }
        }
    }

    private Money calculateAccountBalanceOn(LocalDate startOfPeriod, List<EndOfDayDetail> allEndOfDayDetailsForAccount, MifosCurrency currency) {

        Money balance = Money.zero(currency);
        for (EndOfDayDetail endOfDayDetail : allEndOfDayDetailsForAccount) {
            if (endOfDayDetail.getDate().isBefore(startOfPeriod)) {
                balance = balance.add(endOfDayDetail.getResultantAmountForDay());
            }
        }
        return balance;
    }

    // TODO - move into InterestPostingPeriodCalculator
    private InterestPostingPeriodResult doCalculateInterestForPostingPeriod(CalendarPeriod interestPostingPeriod,
            Money startingBalanceForPeriod, List<SavingsProductHistoricalInterestDetail> historicalInterestDetails,
            List<EndOfDayDetail> allEndOfDayDetailsForAccount, SavingsInterestDetail interestDetail, InterestScheduledEvent interestCalculationEvent) {

        InterestPostingPeriodResult postingPeriodResult = new InterestPostingPeriodResult(interestPostingPeriod);
        Money runningBalance = startingBalanceForPeriod;

        if (!allEndOfDayDetailsForAccount.isEmpty()) {

            List<CalendarPeriod> interestCalculationPeriods = new ArrayList<CalendarPeriod>();

            // 1. determine all valid interest calculation periods that fall within this posting period and create a
            //    interest calculation period calculator for each one (to handle possible different interest rates)
            LocalDate firstActivityDate = allEndOfDayDetailsForAccount.get(0).getDate();

            if (interestPostingPeriod.contains(firstActivityDate)) {
                interestCalculationPeriods = this.interestCalculationIntervalHelper.determineAllPossiblePeriods(firstActivityDate, interestCalculationEvent, interestPostingPeriod.getEndDate());
            } else {
                interestCalculationPeriods = this.interestCalculationIntervalHelper.determineAllPossiblePeriods(interestPostingPeriod.getStartDate(), interestCalculationEvent, interestPostingPeriod.getEndDate());
            }

            for (CalendarPeriod calendarPeriod : interestCalculationPeriods) {
                NonCompoundingInterestCalculator interestCalculationPeriodCalculator = createInterestCalculationPeriodCalculator(interestDetail, interestCalculationEvent);
                SavingsProductHistoricalInterestDetail historicalInterestDetail = findMatchingHistoricalInterestDetail(historicalInterestDetails, calendarPeriod);
                if (historicalInterestDetail != null) {
                    int accountingNumberOfInterestDaysInYear = AccountingRules.getNumberOfInterestDays();
                    SavingsInterestDetail historicalSavingsInterestDetail = new SavingsInterestDetail(interestDetail.getInterestCalcType(), historicalInterestDetail.getInterestRate(),
                            accountingNumberOfInterestDaysInYear, historicalInterestDetail.getMinAmntForInt());
                    interestCalculationPeriodCalculator = createInterestCalculationPeriodCalculator(historicalSavingsInterestDetail, interestCalculationEvent);
                }

                // 2. populate InterestCalculationPeriodDetail with valid end of day details for calculation period
                InterestCalculationPeriodDetail interestCalculationPeriodDetail = InterestCalculationPeriodDetail
                .populatePeriodDetailBasedOnInterestCalculationInterval(calendarPeriod, allEndOfDayDetailsForAccount, runningBalance);

                // 3. calculate average principal, total principal and interest details for calculation period.
                InterestCalculationPeriodResult calculationPeriodResult = interestCalculationPeriodCalculator.calculateCalculationPeriodDetail(interestCalculationPeriodDetail);

                // 4. only sum the total principal as 'interest calculation periods are non-compounding'
                runningBalance = runningBalance.add(calculationPeriodResult.getTotalPrincipal());

                postingPeriodResult.add(calculationPeriodResult);
            }
        }
        postingPeriodResult.setPeriodBalance(runningBalance);

        return postingPeriodResult;
    }

    private SavingsProductHistoricalInterestDetail findMatchingHistoricalInterestDetail(List<SavingsProductHistoricalInterestDetail> historicalInterestDetails, CalendarPeriod calendarPeriod) {
        SavingsProductHistoricalInterestDetail match = null;
        for (SavingsProductHistoricalInterestDetail historicalInterestDetail : historicalInterestDetails) {
            if (calendarPeriod.contains(historicalInterestDetail.getStartDate())) {
                match = historicalInterestDetail;
            }
        }
        return match;
    }

    private NonCompoundingInterestCalculator createInterestCalculationPeriodCalculator(SavingsInterestDetail interestDetail,
            InterestScheduledEvent interestCalculationEvent) {
        InterestCalculator interestCalculator = SavingsInterestCalculatorFactory.create(interestDetail);

        NonCompoundingInterestCalculator interestCalculationPeriodCalculator = new InterestCalculationPeriodCalculator(
                interestCalculator, interestCalculationEvent, interestCalculationIntervalHelper);
        return interestCalculationPeriodCalculator;
    }

    @Override
    public SavingsAccountClosureDto retrieveClosingDetails(Long savingsId, LocalDate closureDate) {

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);

        InterestScheduledEvent postingSchedule = savingsInterestScheduledEventFactory.createScheduledEventFrom(savingsAccount.getInterestPostingMeeting());
        LocalDate nextPostingDate = new LocalDate(savingsAccount.getNextIntPostDate());
        LocalDate startOfPostingPeriod = postingSchedule.findFirstDateOfPeriodForMatchingDate(nextPostingDate);

        CalendarPeriod postingPeriodAtClosure;
        if (startOfPostingPeriod.isAfter(closureDate)) {
            postingPeriodAtClosure = new CalendarPeriod(closureDate, closureDate);
        } else {
            postingPeriodAtClosure = new CalendarPeriod(startOfPostingPeriod, closureDate);
        }

        List<EndOfDayDetail> allEndOfDayDetailsForAccount = savingsDao.retrieveAllEndOfDayDetailsFor(savingsAccount.getCurrency(), Long.valueOf(savingsId));

        InterestPostingPeriodResult postingPeriodAtClosureResult = determinePostingPeriodResult(postingPeriodAtClosure, savingsAccount, allEndOfDayDetailsForAccount);

        Money endOfAccountBalance = postingPeriodAtClosureResult.getPeriodBalance();
        Money interestAmountAtClosure = postingPeriodAtClosureResult.getPeriodInterest();

        return new SavingsAccountClosureDto(new LocalDate(), endOfAccountBalance.toString(), interestAmountAtClosure.toString());
    }

    private InterestPostingPeriodResult determinePostingPeriodResult(CalendarPeriod postingPeriod, SavingsBO savingsAccount, List<EndOfDayDetail> allEndOfDayDetailsForAccount) {

        List<SavingsProductHistoricalInterestDetail>  historicalInterestDetails = savingsAccount.getHistoricalInterestDetailsForPeriod(postingPeriod);

        MifosCurrency currencyInUse = savingsAccount.getCurrency();
        Money startingBalanceForPeriod = calculateAccountBalanceOn(postingPeriod.getStartDate(), allEndOfDayDetailsForAccount, currencyInUse);

        InterestCalcType interestCalcType = savingsAccount.getInterestCalcType();
        int accountingNumberOfInterestDaysInYear = AccountingRules.getNumberOfInterestDays();
        SavingsInterestDetail interestDetail = new SavingsInterestDetail(interestCalcType, savingsAccount.getInterestRate(),
                accountingNumberOfInterestDaysInYear, savingsAccount.getMinAmntForInt());

        InterestScheduledEvent interestCalculationEvent = new SavingsInterestScheduledEventFactory().createScheduledEventFrom(savingsAccount.getInterestCalculationMeeting());

        return doCalculateInterestForPostingPeriod(postingPeriod, startingBalanceForPeriod, historicalInterestDetails,
                allEndOfDayDetailsForAccount, interestDetail, interestCalculationEvent);
    }

    @Override
    public void closeSavingsAccount(Long savingsId, String notes, SavingsWithdrawalDto closeAccountDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);
        savingsAccount.updateDetails(userContext);

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
        LocalDate closureDate = closeAccountDto.getDateOfWithdrawal();

        // Assumption that all previous interest postings occured correctly
        InterestScheduledEvent postingSchedule = savingsInterestScheduledEventFactory.createScheduledEventFrom(savingsAccount.getInterestPostingMeeting());
        LocalDate nextPostingDate = new LocalDate(savingsAccount.getNextIntPostDate());
        LocalDate startOfPostingPeriod = postingSchedule.findFirstDateOfPeriodForMatchingDate(nextPostingDate);

        CalendarPeriod postingPeriodAtClosure;
        if (startOfPostingPeriod.isAfter(closureDate)) {
            postingPeriodAtClosure = new CalendarPeriod(closureDate, closureDate);
        } else {
            postingPeriodAtClosure = new CalendarPeriod(startOfPostingPeriod, closureDate);
        }

        List<EndOfDayDetail> allEndOfDayDetailsForAccount = savingsDao.retrieveAllEndOfDayDetailsFor(savingsAccount.getCurrency(), Long.valueOf(savingsId));

        InterestPostingPeriodResult postingPeriodAtClosureResult = determinePostingPeriodResult(postingPeriodAtClosure, savingsAccount, allEndOfDayDetailsForAccount);
        savingsAccount.postInterest(postingSchedule, postingPeriodAtClosureResult, createdBy);

        AccountNotesEntity notesEntity = new AccountNotesEntity(new DateTimeService().getCurrentJavaSqlDate(), notes, createdBy, savingsAccount);

        try {
            CustomerBO customer = savingsAccount.getCustomer();
            if (closeAccountDto.getCustomerId() != null) {
                List<CustomerBO> clientList = savingsAccount.getCustomer().getChildren(CustomerLevel.CLIENT, ChildrenStateType.ACTIVE_AND_ONHOLD);
                for (CustomerBO client : clientList) {
                    if (closeAccountDto.getCustomerId().intValue() == client.getCustomerId().intValue()) {
                        customer = client;
                        break;
                    }
                }
            }

            Money amount = new Money(savingsAccount.getCurrency(), closeAccountDto.getAmount().toString());
            PaymentTypeEntity paymentType = new PaymentTypeEntity(closeAccountDto.getModeOfPayment().shortValue());
            Date receiptDate = null;
            if (closeAccountDto.getDateOfReceipt() != null) {
                receiptDate = closeAccountDto.getDateOfReceipt().toDateMidnight().toDate();
            }
            AccountPaymentEntity closeAccount = new AccountPaymentEntity(savingsAccount, amount, closeAccountDto.getReceiptId(), receiptDate, paymentType, closeAccountDto.getDateOfWithdrawal().toDateMidnight().toDate());

            this.transactionHelper.startTransaction();

            savingsAccount.closeAccount(closeAccount, notesEntity, customer, createdBy);
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (CustomerException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }
}