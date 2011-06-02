/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.clientportfolio.newloan.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.loan.business.LoanFeeScheduleEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.util.helpers.InstallmentPrincipalAndInterest;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.FeeInstallment;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.service.BusinessRuleException;

public class IndividualLoanScheduleFactory implements LoanScheduleFactory {

    private FeeDao feeDao;
    
    public IndividualLoanScheduleFactory(FeeDao feeDao) {
        this.feeDao = feeDao;
    }

    @Override
    public LoanSchedule create(LocalDate disbursementDate, List<DateTime> loanScheduleDates, List<Number> totalInstallmentAmounts, LoanOfferingBO loanProduct, 
            CustomerBO customer, MeetingBO loanMeeting, Money loanAmount, Double interestRate, Integer interestDays, Integer gracePeriodDuration, 
            List<AccountFeesEntity> accountFees) {

        GraceType graceType = loanProduct.getGraceType();
        InterestType interestType = loanProduct.getInterestType();
        boolean variableInstallmentLoanProduct = loanProduct.isVariableInstallmentsAllowed();
        Integer numberOfInstallments = loanScheduleDates.size();

        RecurringScheduledEventFactory scheduledEventFactory = new RecurringScheduledEventFactoryImpl();
        ScheduledEvent meetingScheduledEvent = scheduledEventFactory.createScheduledEventFrom(loanMeeting);

        Integer installmentNumber = 1;
        List<InstallmentDate> dueInstallmentDates = new ArrayList<InstallmentDate>();
        for (DateTime scheduledDate : loanScheduleDates) {
            dueInstallmentDates.add(new InstallmentDate(installmentNumber.shortValue(), scheduledDate.toLocalDate()
                    .toDateMidnight().toDate()));
            installmentNumber++;
        }

        if (loanProduct.isPrinDueLastInst()) {
            // Principal due on last installment has been cut, so throw an exception if we reach this code.
            throw new BusinessRuleException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
        }

        // loan interest calculation for various interest calculation algorithms
        LoanDecliningInterestAnnualPeriodCalculator decliningInterestAnnualPeriodCalculator = new LoanDecliningInterestAnnualPeriodCalculatorFactory().create(loanMeeting.getRecurrenceType());
        Double decliningInterestAnnualPeriod = decliningInterestAnnualPeriodCalculator.calculate(loanMeeting.getRecurAfter().intValue(), interestDays);
        Double interestFractionalRatePerInstallment = interestRate / decliningInterestAnnualPeriod / 100;

        LoanDurationInAccountingYearsCalculator loanDurationInAccountingYearsCalculator = new LoanDurationInAccountingYearsCalculatorFactory().create(loanMeeting.getRecurrenceType());

        Double durationInYears = loanDurationInAccountingYearsCalculator.calculate(loanMeeting.getRecurAfter().intValue(), numberOfInstallments, interestDays);

        List<Money> totalInstallmentAmountsAsMoney = new ArrayList<Money>();
        for (Number totalInstallmentAmount : totalInstallmentAmounts) {
            Money totalAmount = new Money(loanAmount.getCurrency(), BigDecimal.valueOf(totalInstallmentAmount.doubleValue()));
            totalInstallmentAmountsAsMoney.add(totalAmount);
        }
        
        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetails(loanAmount, interestRate, graceType, gracePeriodDuration,
                numberOfInstallments, durationInYears, interestFractionalRatePerInstallment, disbursementDate, loanScheduleDates);
        loanInterestCalculationDetails.setTotalInstallmentAmounts(totalInstallmentAmountsAsMoney);

        LoanInterestCalculatorFactory loanInterestCalculatorFactory = new LoanInterestCalculatorFactoryImpl();
        LoanInterestCalculator loanInterestCalculator = loanInterestCalculatorFactory.create(interestType, variableInstallmentLoanProduct);
        Money loanInterest = loanInterestCalculator.calculate(loanInterestCalculationDetails);
        // end of loan Interest creation

        EqualInstallmentGeneratorFactory equalInstallmentGeneratorFactory = new EqualInstallmentGeneratorFactoryImpl();
        PrincipalWithInterestGenerator equalInstallmentGenerator = equalInstallmentGeneratorFactory.create(interestType, loanInterest, variableInstallmentLoanProduct);

        // FIXME - add EMIInstallments for daily interest
        List<InstallmentPrincipalAndInterest> EMIInstallments = equalInstallmentGenerator.generateEqualInstallments(loanInterestCalculationDetails);
        List<LoanScheduleEntity> unroundedLoanSchedules = createUnroundedLoanSchedulesFromInstallments(dueInstallmentDates, loanInterest, loanAmount, 
                meetingScheduledEvent, EMIInstallments, accountFees, customer);

        Money rawAmount = calculateTotalFeesAndInterestForLoanSchedules(unroundedLoanSchedules, loanAmount.getCurrency(), accountFees);
        
        List<LoanScheduleEntity> allExistingLoanSchedules = new ArrayList<LoanScheduleEntity>();
        
        LoanScheduleRounderHelper loanScheduleRounderHelper = new DefaultLoanScheduleRounderHelper();
        LoanScheduleRounder loanScheduleInstallmentRounder = new DefaultLoanScheduleRounder(loanScheduleRounderHelper);

        List<LoanScheduleEntity> roundedLoanSchedules = loanScheduleInstallmentRounder.round(graceType, gracePeriodDuration.shortValue(), loanAmount,
        		interestType, unroundedLoanSchedules, allExistingLoanSchedules);
        
        return new LoanSchedule(roundedLoanSchedules, rawAmount);
    }
    
    private List<LoanScheduleEntity> createUnroundedLoanSchedulesFromInstallments(List<InstallmentDate> installmentDates,
            Money loanInterest, Money loanAmount, ScheduledEvent meetingScheduledEvent,
            List<InstallmentPrincipalAndInterest> principalWithInterestInstallments, List<AccountFeesEntity> accountFees, CustomerBO customer) {

        List<LoanScheduleEntity> unroundedLoanSchedules = new ArrayList<LoanScheduleEntity>();
        List<AccountFeesEntity> accountFeesWithNoTimeOfDibursementFees = new ArrayList<AccountFeesEntity>();
        List<FeeInstallment> feeInstallments = new ArrayList<FeeInstallment>();
        
        if (!accountFees.isEmpty()) {
            InstallmentFeeCalculatorFactory installmentFeeCalculatorFactory = new InstallmentFeeCalculatorFactoryImpl();

            for (AccountFeesEntity accountFeesEntity : accountFees) {

                RateAmountFlag feeType = accountFeesEntity.getFees().getFeeType();
                InstallmentFeeCalculator installmentFeeCalculator = installmentFeeCalculatorFactory.create(this.feeDao, feeType);

                Double feeAmountOrRate = accountFeesEntity.getFeeAmount();
                Money accountFeeAmount = installmentFeeCalculator.calculate(feeAmountOrRate, loanAmount, loanInterest, accountFeesEntity.getFees());
                accountFeesEntity.setAccountFeeAmount(accountFeeAmount);
                
                if (!accountFeesEntity.isTimeOfDisbursement()) {
                    accountFeesWithNoTimeOfDibursementFees.add(accountFeesEntity);
                }
            }
            feeInstallments = FeeInstallment.createMergedFeeInstallments(meetingScheduledEvent, accountFeesWithNoTimeOfDibursementFees, installmentDates.size());
        }

        int installmentIndex = 0;
        for (InstallmentDate installmentDate1 : installmentDates) {

            InstallmentPrincipalAndInterest em = principalWithInterestInstallments.get(installmentIndex);

            LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(null, customer, installmentDate1
                    .getInstallmentId(), new java.sql.Date(installmentDate1.getInstallmentDueDate().getTime()),
                    PaymentStatus.UNPAID, em.getPrincipal(), em.getInterest());

            for (FeeInstallment feeInstallment : feeInstallments) {
                if (feeInstallment.getInstallmentId().equals(installmentDate1.getInstallmentId())) {
                        LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity(loanScheduleEntity,
                                feeInstallment.getAccountFeesEntity().getFees(), feeInstallment.getAccountFeesEntity(),
                                feeInstallment.getAccountFee());
                        loanScheduleEntity.addAccountFeesAction(loanFeeScheduleEntity);
                }
            }

            unroundedLoanSchedules.add(loanScheduleEntity);
            installmentIndex++;
        }

        return unroundedLoanSchedules;
    }

    
    private Money calculateTotalFeesAndInterestForLoanSchedules(List<LoanScheduleEntity> unroundedLoanSchedules, MifosCurrency currencyInUse, List<AccountFeesEntity> accountFees) {

        Money zero = new Money(currencyInUse);
        Money interest = zero;
        Money fees = zero;

        for (LoanScheduleEntity unroundedLoanSchedule : unroundedLoanSchedules) {
            interest = interest.add(unroundedLoanSchedule.getInterest());
            fees = fees.add(unroundedLoanSchedule.getTotalFeesDueWithMiscFee());
        }

        Money feeDisbursementAmount = zero;
        for (AccountFeesEntity accountFeesEntity : accountFees) {
            if (accountFeesEntity.getFees().isTimeOfDisbursement()) {
                feeDisbursementAmount = fees.add(accountFeesEntity.getAccountFeeAmount());
            }
        }

        fees = fees.add(feeDisbursementAmount);
        fees = MoneyUtils.currencyRound(fees);
        interest = MoneyUtils.currencyRound(interest);

        Money rawAmount = interest.add(fees);
        return rawAmount;
    }
}