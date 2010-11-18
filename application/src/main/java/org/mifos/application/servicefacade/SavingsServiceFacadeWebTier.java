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
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.savings.business.SavingsAccountTypeInspector;
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
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountPaymentData;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.calendar.CalendarEvent;
import org.mifos.config.AccountingRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.SavingsAccountClosureDto;
import org.mifos.dto.domain.SavingsAccountCreationDto;
import org.mifos.dto.domain.SavingsAccountStatusDto;
import org.mifos.dto.domain.SavingsAccountUpdateStatus;
import org.mifos.dto.domain.SavingsAdjustmentDto;
import org.mifos.dto.domain.SavingsDepositDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.dto.screen.DepositWithdrawalReferenceDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.SavingsAccountDepositDueDto;
import org.mifos.dto.screen.SavingsAdjustmentReferenceDto;
import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
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
    private final SavingsProductDao savingsProductDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private final HolidayDao holidayDao;
    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();
    private CalendarPeriodHelper interestCalculationIntervalHelper = new CalendarPeriodHelper();
    private SavingsInterestScheduledEventFactory savingsInterestScheduledEventFactory = new SavingsInterestScheduledEventFactory();

    public SavingsServiceFacadeWebTier(SavingsDao savingsDao, SavingsProductDao savingsProductDao, PersonnelDao personnelDao, CustomerDao customerDao, HolidayDao holidayDao) {
        this.savingsDao = savingsDao;
        this.savingsProductDao = savingsProductDao;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
        this.holidayDao = holidayDao;
    }

    @Override
    public void deposit(SavingsDepositDto savingsDeposit) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());

        SavingsBO savingsAccount = this.savingsDao.findById(savingsDeposit.getSavingsId());
        savingsAccount.updateDetails(userContext);

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
            this.transactionHelper.beginAuditLoggingFor(savingsAccount);
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
        savingsAccount.updateDetails(userContext);
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

        List<EndOfDayDetail> allEndOfDayDetailsForAccount = savingsDao.retrieveAllEndOfDayDetailsFor(savingsAccount.getCurrency(), savingsWithdrawal.getSavingsId());
        MifosCurrency currencyInUse = savingsAccount.getCurrency();
        LocalDate dateOfWithdrawal = new LocalDate(payment.getTransactionDate());
        Money balanceOnDateOfWithdrawal = calculateAccountBalanceOn(dateOfWithdrawal.plusDays(1), allEndOfDayDetailsForAccount, currencyInUse);
        if (payment.getTotalAmount().isGreaterThan(balanceOnDateOfWithdrawal)) {
            throw new BusinessRuleException("errors.insufficentbalance", new String[] { savingsAccount.getGlobalAccountNum()});
        }

        try {
            this.transactionHelper.startTransaction();
            this.transactionHelper.beginAuditLoggingFor(savingsAccount);
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
            this.transactionHelper.beginAuditLoggingFor(savingsAccount);
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

    private Money calculateAccountBalanceOn(LocalDate date, List<EndOfDayDetail> allEndOfDayDetailsForAccount, MifosCurrency currency) {

        Money balance = Money.zero(currency);
        for (EndOfDayDetail endOfDayDetail : allEndOfDayDetailsForAccount) {
            if (endOfDayDetail.getDate().isBefore(date)) {
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
        SavingsProductHistoricalInterestDetail closestMatch = null;
        for (SavingsProductHistoricalInterestDetail historicalInterestDetail : historicalInterestDetails) {
            if (calendarPeriod.contains(historicalInterestDetail.getStartDate())) {
                match = historicalInterestDetail;
            }

            if (calendarPeriod.getEndDate().isBefore(historicalInterestDetail.getStartDate())) {
                closestMatch = historicalInterestDetail;
            }
        }

        if (match == null) {
            match = closestMatch;
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
    public SavingsAccountClosureDto retrieveClosingDetails(Long savingsId, LocalDate closureDate, Short localeId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());
        userContext.setLocaleId(localeId);

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

        List<ListElement> depositPaymentTypes = retrieveDepositPaymentTypes(userContext);

        return new SavingsAccountClosureDto(new LocalDate(), endOfAccountBalance.toString(), interestAmountAtClosure.toString(), depositPaymentTypes);
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
                List<CustomerBO> clientList = new CustomerPersistence().getActiveAndOnHoldChildren(savingsAccount.getCustomer().getSearchId(), savingsAccount.getCustomer().getOfficeId(), CustomerLevel.CLIENT);

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
            this.transactionHelper.beginAuditLoggingFor(savingsAccount);

            savingsAccount.closeAccount(closeAccount, notesEntity, customer, createdBy);
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (PersistenceException e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public DepositWithdrawalReferenceDto retrieveDepositWithdrawalReferenceData(Long savingsId, Integer customerId, Short localeId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());
        userContext.setLocaleId(localeId);

        try {
            SavingsBO savingsAccount = savingsDao.findById(savingsId);

            String depositDue = savingsAccount.getTotalPaymentDue(customerId).toString();
            String withdrawalDue = "0";

            List<ListElement> clients = new ArrayList<ListElement>();
            if (savingsAccount.isGroupModelWithIndividualAccountability()) {
                List<CustomerBO> activeAndOnHoldClients = new CustomerPersistence().getActiveAndOnHoldChildren(savingsAccount.getCustomer().getSearchId(), savingsAccount.getCustomer().getOfficeId(), CustomerLevel.CLIENT);
                for (CustomerBO client : activeAndOnHoldClients) {
                    clients.add(new ListElement(client.getCustomerId(), client.getDisplayName()));
                }
            }

            List<AccountActionEntity> trxnTypes = new ArrayList<AccountActionEntity>();
            trxnTypes.add(new AccountBusinessService().getAccountAction(AccountActionTypes.SAVINGS_DEPOSIT.getValue(),userContext.getLocaleId()));
            trxnTypes.add(new AccountBusinessService().getAccountAction(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(),userContext.getLocaleId()));

            List<ListElement> transactionTypes = new ArrayList<ListElement>();
            for (AccountActionEntity accountActionEntity : trxnTypes) {
                transactionTypes.add(new ListElement(accountActionEntity.getId().intValue(), accountActionEntity.getLookUpValue().getMessageText()));
            }

            List<ListElement> depositPaymentTypes = retrieveDepositPaymentTypes(userContext);

            List<ListElement> withdrawalPaymentTypes = new ArrayList<ListElement>();
            List<PaymentTypeEntity> withdrawalPaymentEntityTypes = new AcceptedPaymentTypePersistence().getAcceptedPaymentTypesForATransaction(userContext.getLocaleId(), TrxnTypes.savings_withdrawal.getValue());
            for (PaymentTypeEntity paymentTypeEntity : withdrawalPaymentEntityTypes) {
                withdrawalPaymentTypes.add(new ListElement(paymentTypeEntity.getId().intValue(), paymentTypeEntity.getLookUpValue().getMessageText()));
            }

            boolean backDatedTransactionsAllowed = AccountingRules.isBackDatedTxnAllowed();
            LocalDate defaultTransactionDate = new LocalDate();

            return new DepositWithdrawalReferenceDto(transactionTypes, depositPaymentTypes, withdrawalPaymentTypes, clients, backDatedTransactionsAllowed, defaultTransactionDate, depositDue, withdrawalDue);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private List<ListElement> retrieveDepositPaymentTypes(UserContext userContext) {
        try {
            List<ListElement> depositPaymentTypes = new ArrayList<ListElement>();
            List<PaymentTypeEntity> acceptedPaymentEntityTypes = new AcceptedPaymentTypePersistence().getAcceptedPaymentTypesForATransaction(userContext.getLocaleId(), TrxnTypes.savings_deposit.getValue());
            for (PaymentTypeEntity paymentTypeEntity : acceptedPaymentEntityTypes) {
                depositPaymentTypes.add(new ListElement(paymentTypeEntity.getId().intValue(), paymentTypeEntity.getLookUpValue().getMessageText()));
            }
            return depositPaymentTypes;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public SavingsAdjustmentReferenceDto retrieveAdjustmentReferenceData(Long savingsId, Short localeId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());
        userContext.setLocaleId(localeId);

        SavingsBO savings = savingsDao.findById(savingsId);

        AccountPaymentEntity lastPayment = savings.findMostRecentPaymentByPaymentDate();
        String clientName = null;
        String amount = null;
        boolean depositOrWithdrawal = false;
        if (!savings.getCustomer().isClient() && lastPayment != null) {
            amount = lastPayment.getAmount().toString();
            depositOrWithdrawal = lastPayment.isSavingsDepositOrWithdrawal();
            CustomerBO customer = null;
            for (AccountTrxnEntity accountTrxn : lastPayment.getAccountTrxns()) {
                customer = accountTrxn.getCustomer();
                break;
            }
            if (customer != null && customer.isClient()) {
                clientName = customer.getDisplayName();
            }
        }

       return new SavingsAdjustmentReferenceDto(clientName, amount, depositOrWithdrawal);
    }

    @Override
    public List<PrdOfferingDto> retrieveApplicableSavingsProductsForCustomer(Integer customerId) {

        try {
            List<PrdOfferingDto> applicableSavingsProducts = new ArrayList<PrdOfferingDto>();

            CustomerBO customer = this.customerDao.findCustomerById(customerId);

            applicableSavingsProducts = new SavingsPersistence().getSavingsProducts(customer.getCustomerLevel());

            return applicableSavingsProducts;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public SavingsProductReferenceDto retrieveSavingsProductReferenceData(Integer productId) {

        SavingsOfferingBO savingsProduct = this.savingsProductDao.findById(productId);

        List<ListElement> interestCalcTypeOptions = new ArrayList<ListElement>();
        List<InterestCalcTypeEntity> interestCalculationTypes = this.savingsProductDao.retrieveInterestCalculationTypes();
        for (InterestCalcTypeEntity entity : interestCalculationTypes) {
            interestCalcTypeOptions.add(new ListElement(entity.getId().intValue(), entity.getName()));
        }

        boolean savingsPendingApprovalEnabled = ProcessFlowRules.isSavingsPendingApprovalStateEnabled();

        return new SavingsProductReferenceDto(interestCalcTypeOptions, savingsProduct.toFullDto(), savingsPendingApprovalEnabled);
    }

    @Override
    public Long createSavingsAccount(SavingsAccountCreationDto savingsAccountCreation) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LocalDate createdDate = new LocalDate();
        Integer createdById = user.getUserId();
        PersonnelBO createdBy = this.personnelDao.findPersonnelById(createdById.shortValue());

        CustomerBO customer = this.customerDao.findCustomerById(savingsAccountCreation.getCustomerId());
        SavingsOfferingBO savingsProduct = this.savingsProductDao.findById(savingsAccountCreation.getProductId());

        Money recommendedOrMandatory = new Money(savingsProduct.getCurrency(), savingsAccountCreation.getRecommendedOrMandatoryAmount());
        AccountState savingsAccountState = AccountState.fromShort(savingsAccountCreation.getAccountState());

        // NOTE - doesn't look like we create custom fields like this anymore but with questionaire API
//        List<CustomerCustomFieldEntity> savingsCustomFields = CustomerCustomFieldEntity.fromDto(savingsAccountCreation.getCustomFields(), null);

        CalendarEvent calendarEvents = this.holidayDao.findCalendarEventsForThisYearAndNext(customer.getOfficeId());

        SavingsAccountTypeInspector savingsAccountWrapper = new SavingsAccountTypeInspector(customer, savingsProduct.getRecommendedAmntUnit());

        try {
            SavingsBO savingsAccount = null;
            if (savingsAccountWrapper.isIndividualSavingsAccount()) {

                savingsAccount = SavingsBO.createIndividalSavingsAccount(customer, savingsProduct, recommendedOrMandatory, savingsAccountState,
                        createdDate, createdById, calendarEvents, createdBy);

            } else if (savingsAccountWrapper.isJointSavingsAccountWithClientTracking()) {

                List<CustomerBO> activeAndOnHoldClients = new CustomerPersistence().getActiveAndOnHoldChildren(customer.getSearchId(),
                        customer.getOfficeId(), CustomerLevel.CLIENT);
                savingsAccount = SavingsBO.createJointSavingsAccount(customer, savingsProduct, recommendedOrMandatory, savingsAccountState,
                        createdDate, createdById, calendarEvents, createdBy, activeAndOnHoldClients);
            }

            this.transactionHelper.startTransaction();
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.flushSession();
            savingsAccount.generateSystemId(createdBy.getOffice().getGlobalOfficeNum());
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
            return savingsAccount.getAccountId().longValue();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public SavingsAccountStatusDto retrieveAccountStatuses(Long savingsId, Short localeId) {
        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);

        try {
            List<ListElement> savingsStatesList = new ArrayList<ListElement>();
            AccountStateMachines.getInstance().initializeSavingsStates();

            List<AccountStateEntity> statusList = AccountStateMachines.getInstance().getSavingsStatusList(savingsAccount.getAccountState());
            for (AccountStateEntity accountState : statusList) {
                accountState.setLocaleId(localeId);
                savingsStatesList.add(new ListElement(accountState.getId().intValue(), accountState.getName()));
            }

            return new SavingsAccountStatusDto(savingsStatesList);
        } catch (StatesInitializationException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void updateSavingsAccountStatus(SavingsAccountUpdateStatus updateStatus, Short localeId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());
        userContext.setLocaleId(localeId);

        SavingsBO savingsAccount = this.savingsDao.findById(updateStatus.getSavingsId());
        savingsAccount.updateDetails(userContext);
        try {
            this.transactionHelper.startTransaction();
            this.transactionHelper.beginAuditLoggingFor(savingsAccount);
            AccountState newStatus = AccountState.fromShort(updateStatus.getNewStatusId());

            // FIXME - keithw - refactor savings specific logic out of changeStatus and create savings statue machine wrapper.
            savingsAccount.changeStatus(newStatus, updateStatus.getFlagId(), updateStatus.getComment());
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public SavingsAccountDepositDueDto retrieveDepositDueDetails(String globalAccountNum, Short localeId) {
        SavingsBO savingsAccount = this.savingsDao.findBySystemId(globalAccountNum);

        List<AccountActionDateEntity> scheduledDeposits = savingsAccount.getAccountActionDatesSortedByInstallmentId();
        for (AccountActionDateEntity scheduledDeposit : scheduledDeposits) {
//            AccountDepositDto depositDto = scheduledDeposit.toDto();
        }

        List<AccountNotesEntity> savingsAccountNotes = new ArrayList<AccountNotesEntity>(savingsAccount.getAccountNotes());
        for (AccountNotesEntity accountNotesEntity : savingsAccountNotes) {

        }

        for (AccountFlagMapping accountFlagMapping : savingsAccount.getAccountFlags()) {
            accountFlagMapping.getFlag().setLocaleId(localeId);
        }

        AccountStateEntity accountStateEntity = savingsAccount.getAccountState();
        accountStateEntity.setLocaleId(localeId);

        return null;
    }
}