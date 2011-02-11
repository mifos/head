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

package org.mifos.clientportfolio.newloan.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.util.helpers.EMIInstallment;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.config.AccountingRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;
import org.mifos.service.BusinessRuleException;

public class IndividualLoanScheduleFactory implements LoanScheduleFactory {

    @Override
    public IndividualLoanSchedule create(List<DateTime> loanScheduleDates, LoanOfferingBO loanProduct) {

        GraceType graceType = loanProduct.getGraceType();
        Integer gracePeriodDuration = Integer.valueOf(0);
        if (loanProduct.getGracePeriodDuration() != null) {
            gracePeriodDuration = loanProduct.getGracePeriodDuration().intValue();
        }
        MeetingBO loanMeeting = loanProduct.getLoanOfferingMeetingValue();
        Double interestRate = Double.valueOf("10.0");
        Integer interestDays = Integer.valueOf(AccountingRules.getNumberOfInterestDays().intValue());
        InterestType interestType = loanProduct.getInterestType();

        Money loanAmountDisbursed = Money.zero(loanProduct.getCurrency());
        CustomerBO customer = null;
        Integer numberOfInstallments = loanScheduleDates.size();

        List<LoanScheduleEntity> scheduledLoanRepayments = new ArrayList<LoanScheduleEntity>();

        Integer installmentNumber = 1;
        List<InstallmentDate> dueInstallmentDates = new ArrayList<InstallmentDate>();
        for (DateTime scheduledDate : loanScheduleDates) {
            dueInstallmentDates.add(new InstallmentDate(installmentNumber.shortValue(), scheduledDate.toLocalDate().toDateMidnight().toDate()));
            installmentNumber++;
        }

        try {
            Money loanInterest = getLoanInterest_v2(loanMeeting, graceType, gracePeriodDuration, loanAmountDisbursed, numberOfInstallments, interestRate, interestDays, interestType);

            if (loanProduct.isPrinDueLastInst()) {
                // Principal due on last installment has been cut, so throw an exception if we reach this code.
                throw new BusinessRuleException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
            }

            List<EMIInstallment> EMIInstallments = generateEMI_v2(loanInterest, loanMeeting, graceType, gracePeriodDuration, loanAmountDisbursed, numberOfInstallments, interestRate, interestDays, interestType);

            // create loanSchedules
            int installmentIndex = 0;
            for (InstallmentDate installmentDate : dueInstallmentDates) {
                EMIInstallment em = EMIInstallments.get(installmentIndex);
                LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(null, customer, installmentDate
                        .getInstallmentId(), new java.sql.Date(installmentDate.getInstallmentDueDate().getTime()),
                        PaymentStatus.UNPAID, em.getPrincipal(), em.getInterest());
                scheduledLoanRepayments.add(loanScheduleEntity);
                installmentIndex++;
            }

            applyRounding_v2(scheduledLoanRepayments, graceType, gracePeriodDuration, loanAmountDisbursed, numberOfInstallments, interestType);

            List<LoanScheduleRepaymentItem> loanScheduleItems = new ArrayList<LoanScheduleRepaymentItem>();
            for (LoanScheduleEntity loanScheduleEntity : scheduledLoanRepayments) {

                LoanScheduleRepaymentItemImpl loanScheduleItem = new LoanScheduleRepaymentItemImpl(
                        loanScheduleEntity.getInstallmentId().intValue(),
                        new LocalDate(loanScheduleEntity.getActionDate()),
                        loanScheduleEntity.getPrincipal(),
                        loanScheduleEntity.getInterest());

                loanScheduleItems.add(loanScheduleItem);
            }

            return new IndividualLoanScheduleImpl(loanScheduleItems);
        } catch (AccountException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    private Money getLoanInterest_v2(MeetingBO loanMeeting, GraceType graceType, Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, Double interestRate, Integer interestDays, InterestType interestType) throws AccountException {

        Double durationInYears = getTotalDurationInYears_v2(numberOfInstallments, loanMeeting, interestDays);

        Money interest = null;

        switch (interestType) {
        case FLAT:
            interest = loanAmount.multiply(interestRate).multiply(durationInYears).divide(new BigDecimal("100"));
            break;
        case DECLINING:
            interest = getDecliningInterestAmount_v2(loanMeeting, graceType, gracePeriodDuration, numberOfInstallments, loanAmount, interestRate, interestDays);
            break;
        case DECLINING_EPI:
            interest = getDecliningEPIAmount_v2(loanMeeting, graceType, gracePeriodDuration, numberOfInstallments, loanAmount, interestRate, interestDays);
            break;
        default:
            break;
        }

        return interest;
    }

    private Double getTotalDurationInYears_v2(Integer numOfInstallments, MeetingBO loanMeeting, Integer interestDays) throws AccountException {
        int daysInWeek = 7;
        int daysInMonth = 30;
        int duration = numOfInstallments * loanMeeting.getMeetingDetails().getRecurAfter();

        Double durationInYears = Double.valueOf("0");
        RecurrenceType recurrenceType = loanMeeting.getMeetingDetails().getRecurrenceTypeEnum();
        switch (recurrenceType) {
        case MONTHLY:
            double totalMonthDays = duration * daysInMonth;
            durationInYears = totalMonthDays / AccountConstants.INTEREST_DAYS_360;
            break;
        case WEEKLY:

            if (interestDays != AccountConstants.INTEREST_DAYS_360 && interestDays != AccountConstants.INTEREST_DAYS_365) {
                throw new AccountException(AccountConstants.NOT_SUPPORTED_INTEREST_DAYS);
            }

            double totalWeekDays = duration * daysInWeek;
            durationInYears = totalWeekDays / interestDays;

            break;
        case DAILY:
            throw new AccountException(AccountConstants.NOT_SUPPORTED_DURATION_TYPE);
        default:
            throw new AccountException(AccountConstants.NOT_SUPPORTED_DURATION_TYPE);
        }
        return durationInYears;
    }

    /**
     * Compute the total interest due on a declining-interest loan. Interest during a principal-only grace period is
     * calculated differently from non-grace-periods.
     * <p>
     * The formula is as follows:
     * <p>
     * The total interest paid is I = Ig + In where Ig = interest paid during any principal-only grace periods In =
     * interest paid during regular payment periods In = A - P A = total amount paid across regular payment periods The
     * formula for computing A is A = p * n where A = total amount paid p = payment per installment n = number of
     * regular (non-grace) installments P = principal i = interest per period
     */
    private Money getDecliningInterestAmount_v2(MeetingBO loanMeeting, GraceType graceType, Integer gracePeriodDuration, Integer numOfInstallments, Money loanAmount, Double interestRate, Integer interestDays) {

        Money nonGraceInterestPayments = getDecliningInterestAmountNonGrace_v2(numOfInstallments - gracePeriodDuration, loanAmount, interestRate, interestDays, loanMeeting);
        Money interest = nonGraceInterestPayments;
        if (graceType.equals(GraceType.PRINCIPALONLYGRACE)) {
            Money graceInterestPayments = getDecliningInterestAmountGrace_v2(loanMeeting, loanAmount, gracePeriodDuration, interestRate, interestDays);
            interest = graceInterestPayments.add(nonGraceInterestPayments);
        }
        return interest;
    }

    private Money getDecliningInterestAmountNonGrace_v2(final int numNonGraceInstallments, Money loanAmount, Double interestRate, Integer interestDays, MeetingBO loanMeeting) {
        Money paymentPerPeriod = getPaymentPerPeriodForDecliningInterest_v2(numNonGraceInstallments, interestRate, interestDays, loanAmount, loanMeeting);
        Money totalPayments = paymentPerPeriod.multiply((double) numNonGraceInstallments);
        return totalPayments.subtract(loanAmount);
    }

    private Money getDecliningInterestAmountGrace_v2(MeetingBO loanMeeting, Money loanAmount, Integer gracePeriodDuration, Double interestRate, Integer interestDays) {
        Double interest = getInterestFractionalRatePerInstallment_v2(loanMeeting, interestRate, interestDays);
        return loanAmount.multiply(interest).multiply(Double.valueOf(gracePeriodDuration.toString()));
    }

    /*
     * Calculates equal payments per period for fixed payment, declining-interest loan type. Uses formula from
     * http://confluence.mifos.org :9090/display/Main/Declining+Balance+Example+Calcs The formula is copied here: EMI =
     * P * i / [1- (1+i)^-n] where p = principal (amount of loan) i = rate of interest per installment period as a
     * decimal (not percent) n = no. of installments
     *
     * Translated into program variables and method calls:
     *
     * paymentPerPeriod = interestFractionalRatePerPeriod * getLoanAmount() / ( 1 - (1 +
     * interestFractionalRatePerPeriod) ^ (-getNoOfInstallments()))
     *
     * NOTE: Use double here, not BigDecimal, to calculate the factor that getLoanAmount() is multiplied by. Since
     * calculations all involve small quantities, 64-bit precision is sufficient. It is is more accurate to use
     * floating-point, for quantities of small magnitude (say for very small interest rates)
     *
     * NOTE: These calculations do not take into account EPI or grace period adjustments.
     */
    private Money getPaymentPerPeriodForDecliningInterest_v2(final int numInstallments, Double interestRate,
            Integer interestDays, Money loanAmount, MeetingBO loanMeeting) {
        double factor = 0.0;
        if (interestRate == 0.0) {
            Money paymentPerPeriod = loanAmount.divide(numInstallments);
            return paymentPerPeriod;
        }

        Double interestFractionalRatePerInstallment = getInterestFractionalRatePerInstallment_v2(loanMeeting,
                interestRate, interestDays);

        factor = interestFractionalRatePerInstallment
                / (1.0 - Math.pow(1.0 + interestFractionalRatePerInstallment, -numInstallments));

        Money paymentPerPeriod = loanAmount.multiply(factor);
        return paymentPerPeriod;
    }

    private double getInterestFractionalRatePerInstallment_v2(MeetingBO loanMeeting, Double interestRate, Integer interestDays) {
        return interestRate / getDecliningInterestAnnualPeriods_v2(loanMeeting, interestDays) / 100;
    }

    /**
     * Corrects two defects:
     * <ul>
     * <li>period was being rounded to the closest integer because all of the factors involved in the calculation are
     * integers. First, convert the factors to double values.
     * <li>calculation uses the wrong formula for monthly installments. Whether fiscal year is 360 or 365, just consider
     * a month to be 1/12 of a year.
     */
    private double getDecliningInterestAnnualPeriods_v2(MeetingBO loanMeeting, Integer interestDays) {
        RecurrenceType meetingFrequency = loanMeeting.getMeetingDetails().getRecurrenceTypeEnum();

        short recurAfter = loanMeeting.getMeetingDetails().getRecurAfter();

        double period = 0;

        if (meetingFrequency.equals(RecurrenceType.WEEKLY)) {
            period = Double.valueOf(interestDays.toString()) / (7 * recurAfter);
        }
        /*
         * The use of monthly interest here does not distinguish between the 360 (with equal 30 day months) and the 365
         * day year cases. Should it?
         */
        else if (meetingFrequency.equals(RecurrenceType.MONTHLY)) {
            period = recurAfter * 12;
        }

        return period;
    }

    private Money getDecliningEPIAmount_v2(MeetingBO loanMeeting, GraceType graceType, Integer gracePeriodDuration, Integer numberOfInstallments, Money loanAmount, Double interestRate, Integer interestDays) {

        Money interest = new Money(loanAmount.getCurrency(), "0");
        if (graceType.equals(GraceType.PRINCIPALONLYGRACE)) {
            Money graceInterestPayments = getDecliningEPIAmountGrace_v2(loanMeeting, loanAmount, gracePeriodDuration, interestRate, interestDays);
            Money nonGraceInterestPayments = getDecliningEPIAmountNonGrace_v2(numberOfInstallments - gracePeriodDuration, loanAmount, loanMeeting, interestRate, interestDays);
            interest = graceInterestPayments.add(nonGraceInterestPayments);
        } else {
            interest = getDecliningEPIAmountNonGrace_v2(numberOfInstallments, loanAmount, loanMeeting, interestRate, interestDays);
        }
        return interest;
    }

    // the business rules for DecliningEPI for grace periods are the same as
    // Declining's
    private Money getDecliningEPIAmountGrace_v2(MeetingBO loanMeeting, Money loanAmount, Integer gracePeriodDuration, Double interestRate, Integer interestDays) {
        return getDecliningInterestAmountGrace_v2(loanMeeting, loanAmount, gracePeriodDuration, interestRate, interestDays);
    }

    // the decliningEPI amount = sum of interests for all installments
    private Money getDecliningEPIAmountNonGrace_v2(final int numNonGraceInstallments, Money loanAmount, MeetingBO loanMeeting, Double productInterestRate, Integer interestDays) {
        Money principalBalance = loanAmount;
        Money principalPerPeriod = principalBalance.divide(new BigDecimal(numNonGraceInstallments));
        Double interestRate = getInterestFractionalRatePerInstallment_v2(loanMeeting, productInterestRate, interestDays);
        Money totalInterest = new Money(loanAmount.getCurrency(), "0");
        for (int i = 0; i < numNonGraceInstallments; i++) {
            Money interestThisPeriod = principalBalance.multiply(interestRate);
            totalInterest = totalInterest.add(interestThisPeriod);
            principalBalance = principalBalance.subtract(principalPerPeriod);
        }

        return totalInterest;
    }

    private List<EMIInstallment> generateEMI_v2(Money loanInterest, MeetingBO loanMeeting, GraceType graceType,
            Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, Double interestRate,
            Integer interestDays, InterestType interestType) {


        switch (interestType) {
        case FLAT:
            return allFlatInstallments_v2(loanInterest, graceType, gracePeriodDuration, loanAmount, numberOfInstallments);
        case DECLINING:
        case DECLINING_PB:
            return allDecliningInstallments_v2(graceType, gracePeriodDuration, loanAmount, numberOfInstallments, loanMeeting, interestRate, interestDays);
        case DECLINING_EPI:
            return allDecliningEPIInstallments_v2(graceType, gracePeriodDuration, loanAmount, numberOfInstallments,
                    loanMeeting, interestRate, interestDays);
        default:
            try {
                throw new AccountException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
            } catch (AccountException e) {
                throw new BusinessRuleException(e.getKey(), e);
            }
        }
    }

    /**
     * Generate flat-interest installment variants based on the type of grace period.
     * <ul>
     * <li>If grace period is none, or applies to both principal and interest, the loan calculations are the same.
     * <li>If grace period is for principal only, don't add new installments. The first grace installments are
     * interest-only, and principal is paid off with the remaining installments. NOTE: Principal-only grace period
     * should be disable for release 1.1.
     * </ul>
     */
    private List<EMIInstallment> allFlatInstallments_v2(final Money loanInterest, GraceType graceType, Integer gracePeriodDuration, Money loanAmount, Integer numOfInstallments) {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();

        if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateFlatInstallmentsNoGrace_v2(loanInterest, loanAmount, numOfInstallments);
        } else {
            // getGraceType() == GraceType.PRINCIPALONLYGRACE which is disabled.
            emiInstallments = generateFlatInstallmentsInterestOnly_v2(loanInterest, numOfInstallments, gracePeriodDuration);
            emiInstallments.addAll(generateFlatInstallmentsAfterInterestOnlyGraceInstallments_v2(loanInterest, gracePeriodDuration, loanAmount, numOfInstallments));
        }
        return emiInstallments;
    }

    /**
     * Calculate the installments after grace period, in the case of principal-only grace type for a flat-interest loan.
     * Divide interest evenly among all installments, but divide principle evenly among installments after the grace
     * period.
     */
    private List<EMIInstallment> generateFlatInstallmentsAfterInterestOnlyGraceInstallments_v2(final Money loanInterest, Integer gracePeriodDuration, Money loanAmount, Integer numOfInstallments) {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money principalPerInstallment = loanAmount.divide(numOfInstallments - gracePeriodDuration);
        Money interestPerInstallment = loanInterest.divide(numOfInstallments);
        for (int i = gracePeriodDuration; i < numOfInstallments; i++) {
            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());
            installment.setPrincipal(principalPerInstallment);
            installment.setInterest(interestPerInstallment);
            emiInstallments.add(installment);
        }
        return emiInstallments;
    }

    /**
     * Generate declining-interest installment variants based on the type of grace period.
     * <ul>
     * <li>If grace period is none, or applies to both principal and interest, the loan calculations are the same.
     * <li>If grace period is for principal only, don't add new installments. The first grace installments are
     * interest-only, and principal is paid off with the remaining installments.
     * </ul>
     */
    private List<EMIInstallment> allDecliningInstallments_v2(GraceType graceType, Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, MeetingBO loanMeeting, Double interestRate, Integer interestDays) {
        List<EMIInstallment> emiInstallments;

        if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateDecliningInstallmentsNoGrace_v2(numberOfInstallments, interestRate, interestDays, loanAmount, loanMeeting);
        } else {

            // getGraceType() == GraceType.PRINCIPALONLYGRACE which is disabled.

            emiInstallments = generateDecliningInstallmentsInterestOnly_v2(gracePeriodDuration, interestRate, interestDays, loanAmount, loanMeeting);
            emiInstallments.addAll(generateDecliningInstallmentsAfterInterestOnlyGraceInstallments_v2(gracePeriodDuration, numberOfInstallments, interestRate, interestDays, loanAmount, loanMeeting));
        }
        return emiInstallments;
    }

    /**
     * Return the list if payment installments for declining interest method, for the number of installments specified.
     */
    private List<EMIInstallment> generateDecliningInstallmentsNoGrace_v2(final int numInstallments, Double interestRate, Integer interestDays, Money loanAmount, MeetingBO loanMeeting) {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();

        Money paymentPerPeriod = getPaymentPerPeriodForDecliningInterest_v2(numInstallments, interestRate, interestDays, loanAmount, loanMeeting);

        // Now calculate the details of each installment. These are the exact
        // values, and have not been
        // adjusted for rounding and precision factors.

        Money principalBalance = loanAmount;

        double interestRateFractional = getInterestFractionalRatePerInstallment_v2(loanMeeting, interestRate, interestDays);
        for (int i = 0; i < numInstallments; i++) {

            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());

            Money interestThisPeriod = principalBalance.multiply(interestRateFractional);
            Money principalThisPeriod = paymentPerPeriod.subtract(interestThisPeriod);

            installment.setInterest(interestThisPeriod);
            installment.setPrincipal(principalThisPeriod);
            principalBalance = principalBalance.subtract(principalThisPeriod);

            emiInstallments.add(installment);
        }

        return emiInstallments;
    }

    /**
     * Generate interest-only payments for the duration of the grace period. Interest paid is on the outstanding
     * balance, which during the grace period is the entire principal amount.
     */
    private List<EMIInstallment> generateDecliningInstallmentsInterestOnly_v2(Integer gracePeriodDuration, Double interestRate, Integer interestDays, Money loanAmount, MeetingBO loanMeeting) {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money zero = MoneyUtils.zero(loanAmount.getCurrency());
        double interestRateFractional = getInterestFractionalRatePerInstallment_v2(loanMeeting, interestRate, interestDays);
        for (int i = 0; i < gracePeriodDuration; i++) {
            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());
            installment.setInterest(loanAmount.multiply(interestRateFractional));
            installment.setPrincipal(zero);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }

    private List<EMIInstallment> allDecliningEPIInstallments_v2(GraceType graceType, Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, MeetingBO loanMeeting, Double interestRate, Integer interestDays) {

        List<EMIInstallment> emiInstallments;
        if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateDecliningEPIInstallmentsNoGrace_v2(numberOfInstallments, loanAmount, loanMeeting, interestRate, interestDays);
        } else {
            emiInstallments = generateDecliningEPIInstallmentsInterestOnly_v2(gracePeriodDuration, loanAmount, loanMeeting, interestRate, interestDays);
            emiInstallments.addAll(generateDecliningEPIInstallmentsAfterInterestOnlyGraceInstallments_v2(gracePeriodDuration, loanAmount, numberOfInstallments, loanMeeting, interestRate, interestDays));
        }
        return emiInstallments;
    }

    private List<EMIInstallment> generateDecliningEPIInstallmentsNoGrace_v2(final int numInstallments, Money loanAmount, MeetingBO loanMeeting, Double interestRate, Integer interestDays){

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money principalBalance = loanAmount;
        Money principalPerPeriod = principalBalance.divide(new BigDecimal(numInstallments));
        double interestRateFractional = getInterestFractionalRatePerInstallment_v2(loanMeeting, interestRate, interestDays);

        for (int i = 0; i < numInstallments; i++) {
            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());
            Money interestThisPeriod = principalBalance.multiply(interestRateFractional);
            installment.setInterest(interestThisPeriod);
            installment.setPrincipal(principalPerPeriod);
            principalBalance = principalBalance.subtract(principalPerPeriod);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }

    // same as Declining
    private List<EMIInstallment> generateDecliningEPIInstallmentsInterestOnly_v2(Integer gracePeriodDuration, Money loanAmount, MeetingBO loanMeeting, Double interestRate, Integer interestDays) {

        return generateDecliningInstallmentsInterestOnly_v2(gracePeriodDuration, interestRate, interestDays, loanAmount, loanMeeting);
    }

    private List<EMIInstallment> generateDecliningEPIInstallmentsAfterInterestOnlyGraceInstallments_v2(Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, MeetingBO loanMeeting, Double interestRate, Integer interestDays) {

        return generateDecliningEPIInstallmentsNoGrace_v2(numberOfInstallments - gracePeriodDuration, loanAmount, loanMeeting, interestRate, interestDays);
    }

    /**
     * Divide principal and interest evenly among all installments, no grace period
     */
    private List<EMIInstallment> generateFlatInstallmentsNoGrace_v2(final Money loanInterest, Money loanAmount, Integer numOfInstallments) {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money principalPerInstallment = loanAmount.divide(numOfInstallments);
        Money interestPerInstallment = loanInterest.divide(numOfInstallments);
        for (int i = 0; i < numOfInstallments; i++) {
            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());
            installment.setPrincipal(principalPerInstallment);
            installment.setInterest(interestPerInstallment);
            emiInstallments.add(installment);
        }
        return emiInstallments;
    }

    /**
     * Generate interest-only payments for the duration of the grace period. Interest is divided evenly among all
     * installments, but only interest is paid during the grace period.
     */
    private List<EMIInstallment> generateFlatInstallmentsInterestOnly_v2(final Money loanInterest, Integer numOfInstallments, Integer gracePeriodDuration) {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money zero = MoneyUtils.zero(loanInterest.getCurrency());

        Money interestPerInstallment = loanInterest.divide(numOfInstallments);

        for (int i = 0; i < gracePeriodDuration; i++) {
            EMIInstallment installment = new EMIInstallment(loanInterest.getCurrency());
            installment.setInterest(interestPerInstallment);
            installment.setPrincipal(zero);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }

    /**
     * Calculate the installments after grace period, in the case of principal-only grace type for a declining-interest
     * loan. Calculation is identical to the no-grace scenario except that the number of installments is reduced by the
     * grace period.
     */
    private List<EMIInstallment> generateDecliningInstallmentsAfterInterestOnlyGraceInstallments_v2(Integer gracePeriodDuration, Integer numberOfInstallments, Double interestRate, Integer interestDays, Money loanAmount, MeetingBO loanMeeting) {

        return generateDecliningInstallmentsNoGrace_v2(numberOfInstallments - gracePeriodDuration, interestRate, interestDays, loanAmount, loanMeeting);
    }

    private void applyRounding_v2(List<? extends AccountActionDateEntity> installments, GraceType graceType, Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, InterestType interestType) {

        RepaymentTotals totals = calculateInitialTotals_v2(installments, loanAmount);
        int installmentNum = 0;
        for (Iterator<? extends AccountActionDateEntity> it = installments.iterator(); it.hasNext();) {
            LoanScheduleEntity currentInstallment = (LoanScheduleEntity) it.next();
            installmentNum++;
            if (it.hasNext()) { // handle all but the last installment
                if (isGraceInstallment_v2(installmentNum, graceType, gracePeriodDuration)) {
                    roundAndAdjustGraceInstallment_v2(currentInstallment);
                } else if (interestType.equals(InterestType.DECLINING_EPI.getValue())) {
                    roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(currentInstallment);
                } else {
                    roundAndAdjustButLastNonGraceInstallment_v2(currentInstallment);
                }
                updateRunningTotals_v2(totals, currentInstallment);
            } else {
                roundAndAdjustLastInstallment_v2(currentInstallment, totals);
            }
        }
    }

    private RepaymentTotals calculateInitialTotals_v2(final List<? extends AccountActionDateEntity> installmentsToBeRounded, Money loanAmount) {

        RepaymentTotals totals = new RepaymentTotals(loanAmount.getCurrency());

        Money exactTotalInterestDue = new Money(loanAmount.getCurrency(), "0");
        Money exactTotalAccountFeesDue = new Money(loanAmount.getCurrency(), "0");
        Money exactTotalMiscFeesDue = new Money(loanAmount.getCurrency(), "0");
        Money exactTotalMiscPenaltiesDue = new Money(loanAmount.getCurrency(), "0");

        // principal due = loan amount less any payments on principal
        Money exactTotalPrincipalDue = loanAmount;
        for (AccountActionDateEntity e : installmentsToBeRounded) {
            LoanScheduleEntity installment = (LoanScheduleEntity) e;
            exactTotalPrincipalDue = exactTotalPrincipalDue.subtract(installment.getPrincipalPaid());
        }

        for (Object element : installmentsToBeRounded) {
            LoanScheduleEntity currentInstallment = (LoanScheduleEntity) element;
            exactTotalInterestDue = exactTotalInterestDue.add(currentInstallment.getInterestDue());
            exactTotalAccountFeesDue = exactTotalAccountFeesDue.add(currentInstallment.getTotalFeesDue());
            exactTotalMiscFeesDue = exactTotalMiscFeesDue.add(currentInstallment.getMiscFeeDue());
            exactTotalMiscPenaltiesDue = exactTotalMiscPenaltiesDue.add(currentInstallment.getMiscPenaltyDue());
        }
        Money exactTotalPaymentsDue = exactTotalInterestDue.add(exactTotalAccountFeesDue).add(exactTotalMiscFeesDue)
                .add(exactTotalMiscPenaltiesDue).add(exactTotalPrincipalDue);

        totals.setRoundedPaymentsDue(MoneyUtils.finalRound(exactTotalPaymentsDue));
        totals.setRoundedAccountFeesDue(MoneyUtils.currencyRound(exactTotalAccountFeesDue));
        totals.setRoundedMiscFeesDue(MoneyUtils.currencyRound(exactTotalMiscFeesDue));
        totals.setRoundedMiscPenaltiesDue(MoneyUtils.currencyRound(exactTotalMiscPenaltiesDue));
        totals.setRoundedPrincipalDue(exactTotalPrincipalDue);

        // Adjust interest to account for rounding discrepancies
        totals.setRoundedInterestDue(totals.getRoundedPaymentsDue().subtract(totals.getRoundedAccountFeesDue())
                .subtract(totals.getRoundedMiscFeesDue()).subtract(totals.getRoundedPenaltiesDue()).subtract(
                        totals.getRoundedMiscPenaltiesDue()).subtract(totals.getRoundedPrincipalDue()));
        return totals;
    }

    /**
     * A grace-period installment can appear in the loan schedule only if the loan is setup with principal-only grace.
     */
    private boolean isGraceInstallment_v2(final int installmentNum, GraceType graceType, Integer gracePeriodDuration) {
        return graceType.equals(GraceType.PRINCIPALONLYGRACE) && installmentNum <= gracePeriodDuration;
    }

    /**
     * For principal-only grace installments, adjust the interest to account for rounding discrepancies.
     */
    private void roundAndAdjustGraceInstallment_v2(final LoanScheduleEntity installment) {
        Money roundedInstallmentTotalPaymentDue = MoneyUtils.initialRound(installment.getTotalPaymentDue());
        roundInstallmentAccountFeesDue_v2(installment);
        installment.setInterest(roundedInstallmentTotalPaymentDue.subtract(installment.getTotalFeeDueWithMiscFeeDue())
                .subtract(installment.getPenaltyDue()));
    }

    private void roundInstallmentAccountFeesDue_v2(final LoanScheduleEntity installment) {

        for (AccountFeesActionDetailEntity e : installment.getAccountFeesActionDetails()) {
            e.roundFeeAmount(MoneyUtils.currencyRound(e.getFeeDue().add(e.getFeeAmountPaid())));
        }
    }

    private void roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(final LoanScheduleEntity installment) {
        Money roundedTotalInstallmentPaymentDue = MoneyUtils.initialRound(installment.getTotalPaymentDue());
        roundInstallmentAccountFeesDue_v2(installment);
        installment.setPrincipal(MoneyUtils.currencyRound(installment.getPrincipal()));
        // TODO: above comment applies to principal
        installment.setInterest(roundedTotalInstallmentPaymentDue.subtract(installment.getPrincipalDue()).subtract(
                installment.getTotalFeeDueWithMiscFeeDue()).subtract(installment.getPenaltyDue()));
    }

    /**
     * See Javadoc comment for method applyRounding() for business rules for rounding and adjusting all installments but
     * the last. LoanScheduleEntity does not store the total payment due, directly, but it is the sum of principal,
     * interest, and non-miscellaneous fees.
     * <p>
     *
     * how to set rounded fee for installment?????? This is what I want to do: currentInstallment.setFee
     * (currencyRound_v2 (currentInstallment.getFee));
     *
     * Then I want to adjust principal, but need to extract the rounded fee, like this:
     * currentInstallment.setPrincipal(installmentRoundedTotalPayment .subtract (currentInstallment.getInterest()
     * .subtract (currentInstallment.getFee());
     */
    private void roundAndAdjustButLastNonGraceInstallment_v2(final LoanScheduleEntity installment) {
        Money roundedTotalInstallmentPaymentDue = MoneyUtils.initialRound(installment.getTotalPaymentDue());
        roundInstallmentAccountFeesDue_v2(installment);

        installment.setInterest(MoneyUtils.currencyRound(installment.getInterest()));
        // TODO: above comment applies to principal
        installment.setPrincipal(roundedTotalInstallmentPaymentDue.subtract(installment.getInterestDue()).subtract(
                installment.getTotalFeeDueWithMiscFeeDue()).subtract(installment.getPenaltyDue()).add(
                installment.getPrincipalPaid()));
    }

    private void updateRunningTotals_v2(final RepaymentTotals totals, final LoanScheduleEntity currentInstallment) {

        totals.runningPayments = totals.runningPayments.add(currentInstallment.getTotalPaymentDue());

        totals.runningPrincipal = totals.runningPrincipal.add(currentInstallment.getPrincipalDue());
        totals.runningAccountFees = totals.runningAccountFees.add(currentInstallment.getTotalFeesDue());
        totals.runningMiscFees = totals.runningMiscFees.add(currentInstallment.getMiscFeeDue());
        totals.runningPenalties = totals.runningPenalties.add(currentInstallment.getPenaltyDue());
    }

    /**
     * See JavaDoc comment for applyRounding_v2. TODO: handle fees
     */
    private void roundAndAdjustLastInstallment_v2(final LoanScheduleEntity lastInstallment, final RepaymentTotals totals) {

        roundInstallmentAccountFeesDue_v2(lastInstallment);
        Money installmentPayment = MoneyUtils.finalRound(totals.roundedPaymentsDue.subtract(totals.runningPayments));
        lastInstallment.setPrincipal(MoneyUtils.currencyRound(totals.getRoundedPrincipalDue().subtract(
                totals.runningPrincipal)));
        adjustLastInstallmentFees_v2(lastInstallment, totals);
        lastInstallment.setInterest(MoneyUtils.currencyRound(installmentPayment.subtract(
                lastInstallment.getPrincipalDue()).subtract(lastInstallment.getTotalFeeDueWithMiscFeeDue()).subtract(
                lastInstallment.getPenaltyDue())));
    }

    /**
     * adjust the first fee in the installment's set of fees
     */
    private void adjustLastInstallmentFees_v2(final LoanScheduleEntity lastInstallment, final RepaymentTotals totals) {
        Set<AccountFeesActionDetailEntity> feeDetails = lastInstallment.getAccountFeesActionDetails();
        if (!(feeDetails == null) && !feeDetails.isEmpty()) {
            Money lastInstallmentFeeSum = new Money(lastInstallment.getCurrency());
            for (AccountFeesActionDetailEntity e : feeDetails) {
                lastInstallmentFeeSum = lastInstallmentFeeSum.add(e.getFeeAmount());
            }
            for (Object element : feeDetails) {
                AccountFeesActionDetailEntity e = (AccountFeesActionDetailEntity) element;
                e.adjustFeeAmount(totals.roundedAccountFeesDue.subtract(totals.runningAccountFees).subtract(
                        lastInstallmentFeeSum));
                // just adjust the first fee
                return;
            }
        }
    }

    /**
     * A struct to hold totals that can be passed around during rounding computations.
     */
    private class RepaymentTotals {
        // rounded or adjusted totals prior to rounding installments
        Money roundedPaymentsDue;
        Money roundedInterestDue;
        Money roundedAccountFeesDue;
        Money roundedMiscFeesDue;
        Money roundedPenaltiesDue;
        Money roundedMiscPenaltiesDue;
        Money roundedPrincipalDue;

        // running totals as installments are rounded
        Money runningPayments = null;
        Money runningAccountFees = null;
        Money runningPrincipal = null;
        Money runningMiscFees = null;
        Money runningPenalties = null;

        public RepaymentTotals(MifosCurrency currency) {
            this.runningPayments = new Money(currency, "0");
            this.runningAccountFees = new Money(currency, "0");
            this.runningPrincipal = new Money(currency, "0");
            this.runningMiscFees = new Money(currency, "0");
            this.runningPenalties = new Money(currency, "0");
        }

        Money getRoundedPaymentsDue() {
            return roundedPaymentsDue;
        }

        void setRoundedPaymentsDue(final Money roundedPaymentsDue) {
            this.roundedPaymentsDue = roundedPaymentsDue;
        }

        void setRoundedInterestDue(final Money roundedInterestDue) {
            this.roundedInterestDue = roundedInterestDue;
        }

        Money getRoundedAccountFeesDue() {
            return roundedAccountFeesDue;
        }

        void setRoundedAccountFeesDue(final Money roundedAccountFeesDue) {
            this.roundedAccountFeesDue = roundedAccountFeesDue;
        }

        Money getRoundedMiscFeesDue() {
            return roundedMiscFeesDue;
        }

        void setRoundedMiscFeesDue(final Money roundedMiscFeesDue) {
            this.roundedMiscFeesDue = roundedMiscFeesDue;
        }

        Money getRoundedPenaltiesDue() {
            return roundedPenaltiesDue;
        }

        Money getRoundedMiscPenaltiesDue() {
            return roundedMiscPenaltiesDue;
        }

        void setRoundedMiscPenaltiesDue(final Money roundedMiscPenaltiesDue) {
            this.roundedMiscPenaltiesDue = roundedMiscPenaltiesDue;
        }

        Money getRoundedPrincipalDue() {
            return roundedPrincipalDue;
        }

        void setRoundedPrincipalDue(final Money roundedPrincipalDue) {
            this.roundedPrincipalDue = roundedPrincipalDue;
        }

        public Money getRoundedInterestDue() {
            return this.roundedInterestDue;
        }
    }
}