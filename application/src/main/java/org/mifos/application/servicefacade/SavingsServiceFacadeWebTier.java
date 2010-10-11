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
import java.util.List;

import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.interest.EndOfDayDetail;
import org.mifos.accounts.savings.interest.InterestCalculationInterval;
import org.mifos.accounts.savings.interest.InterestCalculationIntervalHelper;
import org.mifos.accounts.savings.interest.InterestCalculationPeriodDetail;
import org.mifos.accounts.savings.interest.InterestCalculationPeriodResult;
import org.mifos.accounts.savings.interest.InterestCalculator;
import org.mifos.accounts.savings.interest.InterestPostingPeriodResult;
import org.mifos.accounts.savings.interest.SavingsInterestCalculatorFactory;
import org.mifos.accounts.savings.interest.SavingsInterestDetail;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.SavingsInterestScheduledEventFactory;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.util.helpers.AccountPaymentData;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.SavingsPaymentData;
import org.mifos.config.AccountingRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.SavingsAdjustmentDto;
import org.mifos.dto.domain.SavingsDepositDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SavingsServiceFacadeWebTier implements SavingsServiceFacade {

    private final SavingsDao savingsDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();
    private InterestCalculationIntervalHelper interestCalculationIntervalHelper = new InterestCalculationIntervalHelper();
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

        SavingsBO savingsAccount = this.savingsDao.findById(savingsAdjustment.getSavingsId());
        savingsAccount.updateDetails(userContext);

        Money amountAdjustedTo = new Money(savingsAccount.getCurrency(), BigDecimal.valueOf(savingsAdjustment
                .getAdjustedAmount()));

        try {
            this.transactionHelper.startTransaction();

            savingsAccount.adjustLastUserAction(amountAdjustedTo, savingsAdjustment.getNote());

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
    public void batchPostInterestToSavingsAccount(LocalDate dateBatchJobIsScheduled) {

        List<Integer> accountIds = this.savingsDao.retrieveAllActiveAndInActiveSavingsAccountsPendingInterestPostingOn(dateBatchJobIsScheduled);
        for (Integer savingsId : accountIds) {

            SavingsBO savingsAccount = this.savingsDao.findById(Long.valueOf(savingsId));
            LocalDate interestPostingDate = new LocalDate(savingsAccount.getNextIntPostDate());

            InterestScheduledEvent postingSchedule = savingsInterestScheduledEventFactory.createScheduledEventFrom(savingsAccount.getInterestPostingMeeting());

            // FIXME - keithw - some integration tests setup account with 'incorrect' next posting date
            // should I cater for this possibility here and in savingsAccount.postInterest?
            // is it possible customer could have this problem.

            // FIXME - what about authentication for batch jobs? what user is batch job?
            try {
                this.transactionHelper.startTransaction();

                LocalDate startDate = postingSchedule.findFirstDateOfPeriodForMatchingDate(interestPostingDate);

                // FIXME - keithw - the more I look at the use of Interval within InterestCalculationInterval it seems redundant as contains
                // method of interval is not 'inclusive' of end datetime and we really want to use LocalDate anyway.
                Interval postingInterval = new InterestCalculationInterval(startDate, interestPostingDate).getInterval();
                // FIXME - keithw - comment out for now to spike a entire account approach to interest posting (and as such interest calaculation).
//                calculateInterestForPostingInterval(Long.valueOf(savingsId), postingInterval);
                List<InterestPostingPeriodResult> postingResults = recalculateInterestAndPosting(savingsAccount, interestPostingDate, postingSchedule);

                boolean interestPosted = savingsAccount.postInterest(postingSchedule);

                if (interestPosted) {
                    this.savingsDao.save(savingsAccount);
                }
                this.transactionHelper.commitTransaction();
            } catch (Exception e) {
                this.transactionHelper.rollbackTransaction();
                throw new BusinessRuleException(savingsId.toString(), e);
            } finally {
                this.transactionHelper.closeSession();
            }
        }
    }

    private List<InterestPostingPeriodResult> recalculateInterestAndPosting(SavingsBO savingsAccount, LocalDate interestPostingDate, InterestScheduledEvent postingSchedule) {
        List<InterestPostingPeriodResult> allInterestPostings = new ArrayList<InterestPostingPeriodResult>();

        List<EndOfDayDetail> allEndOfDayDetailsForAccount = savingsDao.retrieveAllEndOfDayDetailsFor(savingsAccount.getCurrency(), Long.valueOf(savingsAccount.getAccountId()));
        if (!allEndOfDayDetailsForAccount.isEmpty()) {
            LocalDate firstAccountActivityDate = allEndOfDayDetailsForAccount.get(0).getDate();
            List<InterestCalculationInterval> allPossiblePostingPeriods = interestCalculationIntervalHelper.determineAllPossiblePeriods(firstAccountActivityDate, postingSchedule, interestPostingDate);

            Money zero = Money.zero(savingsAccount.getCurrency());
            Money periodAccountBalance = zero;

            for (InterestCalculationInterval postingPeriod : allPossiblePostingPeriods) {
                InterestPostingPeriodResult postingPeriodResult = calculateInterestForPosting(postingPeriod, allEndOfDayDetailsForAccount, periodAccountBalance, savingsAccount);
                allInterestPostings.add(postingPeriodResult);
                periodAccountBalance = periodAccountBalance.add(postingPeriodResult.getPeriodBalance());
            }
        }

        return allInterestPostings;
    }

    private InterestPostingPeriodResult calculateInterestForPosting(InterestCalculationInterval postingPeriod,
            List<EndOfDayDetail> allEndOfDayDetailsForAccount, Money periodAccountBalace, SavingsBO savingsAccount) {

        InterestPostingPeriodResult interestPostingPeriodResult = new InterestPostingPeriodResult();
        if (!allEndOfDayDetailsForAccount.isEmpty()) {
            InterestCalcType interestCalcType = InterestCalcType.fromInt(savingsAccount.getInterestCalcType().getId());

            int accountingNumberOfInterestDaysInYear = AccountingRules.getNumberOfInterestDays();
            SavingsInterestDetail interestDetail = new SavingsInterestDetail(interestCalcType, savingsAccount.getInterestRate(), accountingNumberOfInterestDaysInYear, savingsAccount.getMinAmntForInt());
            InterestCalculator interestCalculator = SavingsInterestCalculatorFactory.create(interestDetail);

            InterestScheduledEvent interestCalculationEvent = savingsInterestScheduledEventFactory.createScheduledEventFrom(savingsAccount.getTimePerForInstcalc());

            Money totalBalanceBeforeInterestCalculation = periodAccountBalace;
            List<InterestCalculationInterval> allPossible = interestCalculationIntervalHelper.determineAllPossiblePeriods(postingPeriod.getStartDate(), interestCalculationEvent, postingPeriod.getEndDate());
            for (InterestCalculationInterval interval : allPossible) {

                InterestCalculationPeriodDetail interestCalculationPeriodDetail = InterestCalculationPeriodDetail.populatePeriodDetailBasedOnInterestCalculationInterval(interval, allEndOfDayDetailsForAccount, totalBalanceBeforeInterestCalculation);

                InterestCalculationPeriodResult calculationPeriodResult = interestCalculator.calculateSavingsDetailsForPeriod(interestCalculationPeriodDetail);

                totalBalanceBeforeInterestCalculation = totalBalanceBeforeInterestCalculation.add(calculationPeriodResult.getTotalPrincipal());
                interestPostingPeriodResult.add(calculationPeriodResult);
            }
            interestPostingPeriodResult.setPeriodBalance(totalBalanceBeforeInterestCalculation);
        }
        return interestPostingPeriodResult;
    }

    @Override
    public void calculateInterestForPostingInterval(Long savingsId, Interval postingInterval) {

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);

        Money interestCalculatedTillDate = new Money(savingsAccount.getCurrency());

        List<EndOfDayDetail> allEndOfDayDetailsForAccount = savingsDao.retrieveAllEndOfDayDetailsFor(savingsAccount.getCurrency(), savingsId);

        if (!allEndOfDayDetailsForAccount.isEmpty()) {

            InterestCalcType interestCalcType = InterestCalcType.fromInt(savingsAccount.getInterestCalcType().getId());

            int accountingNumberOfInterestDaysInYear = AccountingRules.getNumberOfInterestDays();

            SavingsInterestDetail interestDetail = new SavingsInterestDetail(interestCalcType, savingsAccount.getInterestRate(), accountingNumberOfInterestDaysInYear, savingsAccount.getMinAmntForInt());

            InterestCalculator interestCalculator = SavingsInterestCalculatorFactory.create(interestDetail);

            InterestScheduledEvent interestCalculationEvent = savingsInterestScheduledEventFactory.createScheduledEventFrom(savingsAccount.getTimePerForInstcalc());

            LocalDate firstDepositDate = allEndOfDayDetailsForAccount.get(0).getDate();

            List<InterestCalculationInterval> calculationIntervals;

            if (postingInterval == null) {
                // FIXME This condition is just for Interest Calculation Batch Job, this should be removed later when we
                // get rid of Interest calculation batch job
                calculationIntervals = new ArrayList<InterestCalculationInterval>();
                if (savingsAccount.getLastIntCalcDate() == null) {
                    savingsAccount.setLastIntCalcDate(savingsAccount.getActivationDate());
                }
                LocalDate startDate = new LocalDate(savingsAccount.getLastIntCalcDate());
                LocalDate endDate = interestCalculationEvent.nextMatchingDateFromAlreadyMatchingDate(startDate);
                calculationIntervals.add(new InterestCalculationInterval(startDate, endDate));
            } else {
                calculationIntervals = interestCalculationIntervalHelper.determineInterestCalculationPeriods(postingInterval, firstDepositDate, interestCalculationEvent);
            }

            for (InterestCalculationInterval interval : calculationIntervals) {

            Money totalBalanceBeforePeriod = Money.zero(savingsAccount.getCurrency());
            InterestCalculationPeriodDetail interestCalculationPeriodDetail = createInterestCalculationPeriodDetail(interval, allEndOfDayDetailsForAccount, totalBalanceBeforePeriod);

            InterestCalculationPeriodResult periodResults = interestCalculator.calculateSavingsDetailsForPeriod(interestCalculationPeriodDetail);

            interestCalculatedTillDate = interestCalculatedTillDate.add(periodResults.getInterest());
                if (postingInterval == null) {
                    // FIXME This condition is just for Interest Calculation Batch Job, this should be removed later
                    // when we get rid of Interest calculation batch job
                    savingsAccount.setLastIntCalcDate(interval.getEndDate().toDateMidnight().toDate());
                }
            }
        }
        savingsAccount.setInterestToBePosted(interestCalculatedTillDate);
    }

    private InterestCalculationPeriodDetail createInterestCalculationPeriodDetail(InterestCalculationInterval interval,
            List<EndOfDayDetail> allEndOfDayDetailsForAccount, Money balanceBeforeInterval) {

        Money balance = balanceBeforeInterval;

        InterestCalculationInterval intervalExcludingLastDayOfLastMonth = new InterestCalculationInterval(interval.getStartDate().plusDays(1),interval.getEndDate());

        List<EndOfDayDetail> applicableDailyDetailsForPeriod = new ArrayList<EndOfDayDetail>();

        for (EndOfDayDetail endOfDayDetail : allEndOfDayDetailsForAccount) {
            if (intervalExcludingLastDayOfLastMonth.getStartDate().isAfter(endOfDayDetail.getDate())) {
                balance = balance.add(endOfDayDetail.getResultantAmountForDay());
            }
            if (intervalExcludingLastDayOfLastMonth.dateFallsWithin(endOfDayDetail.getDate())) {
                applicableDailyDetailsForPeriod.add(endOfDayDetail);
            }
        }

        return new InterestCalculationPeriodDetail(interval, applicableDailyDetailsForPeriod, balance);
    }

    public void setInterestCalculationIntervalHelper(InterestCalculationIntervalHelper interestCalculationIntervalHelper) {
        this.interestCalculationIntervalHelper = interestCalculationIntervalHelper;
    }

    public void setSavingsInterestScheduledEventFactory(
            SavingsInterestScheduledEventFactory savingsInterestScheduledEventFactory) {
        this.savingsInterestScheduledEventFactory = savingsInterestScheduledEventFactory;
    }
}