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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.util.helpers.InstallmentPrincipalAndInterest;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.AccountingRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;
import org.mifos.service.BusinessRuleException;

public class IndividualLoanScheduleFactory implements LoanScheduleFactory {

    @Override
    public IndividualLoanSchedule create(List<DateTime> loanScheduleDates, LoanOfferingBO loanProduct, Money loanAmount) {

        GraceType graceType = loanProduct.getGraceType();
        Integer gracePeriodDuration = Integer.valueOf(0);
        if (loanProduct.getGracePeriodDuration() != null) {
            gracePeriodDuration = loanProduct.getGracePeriodDuration().intValue();
        }
        MeetingBO loanMeeting = loanProduct.getLoanOfferingMeetingValue();
        Double interestRate = Double.valueOf("10.0");
        Integer interestDays = Integer.valueOf(AccountingRules.getNumberOfInterestDays().intValue());
        InterestType interestType = loanProduct.getInterestType();

        CustomerBO customer = null;
        Integer numberOfInstallments = loanScheduleDates.size();

        List<LoanScheduleEntity> scheduledLoanRepayments = new ArrayList<LoanScheduleEntity>();

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

        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetails(loanAmount, interestRate, graceType, gracePeriodDuration,
                numberOfInstallments, durationInYears, interestFractionalRatePerInstallment);

        LoanInterestCalculatorFactory loanInterestCalculatorFactory = new LoanInterestCalculatorFactoryImpl();
        LoanInterestCalculator loanInterestCalculator = loanInterestCalculatorFactory.create(interestType);
        Money loanInterest = loanInterestCalculator.calculate(loanInterestCalculationDetails);
        // end of loan Interest creation

        EqualInstallmentGeneratorFactory equalInstallmentGeneratorFactory = new EqualInstallmentGeneratorFactoryImpl();
        PrincipalWithInterestGenerator equalInstallmentGenerator = equalInstallmentGeneratorFactory.create(interestType, loanInterest);

        List<InstallmentPrincipalAndInterest> EMIInstallments = equalInstallmentGenerator.generateEqualInstallments(loanInterestCalculationDetails);

        // create loanSchedules
        int installmentIndex = 0;
        for (InstallmentDate installmentDate : dueInstallmentDates) {
            InstallmentPrincipalAndInterest em = EMIInstallments.get(installmentIndex);
            LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(null, customer, installmentDate
                    .getInstallmentId(), new java.sql.Date(installmentDate.getInstallmentDueDate().getTime()),
                    PaymentStatus.UNPAID, em.getPrincipal(), em.getInterest());
            scheduledLoanRepayments.add(loanScheduleEntity);
            installmentIndex++;
        }

        applyRounding_v2(scheduledLoanRepayments, graceType, gracePeriodDuration, loanAmount,
                numberOfInstallments, interestType);

        List<LoanScheduleRepaymentItem> loanScheduleItems = new ArrayList<LoanScheduleRepaymentItem>();
        for (LoanScheduleEntity loanScheduleEntity : scheduledLoanRepayments) {

            LoanScheduleRepaymentItemImpl loanScheduleItem = new LoanScheduleRepaymentItemImpl(loanScheduleEntity
                    .getInstallmentId().intValue(), new LocalDate(loanScheduleEntity.getActionDate()),
                    loanScheduleEntity.getPrincipal(), loanScheduleEntity.getInterest());

            loanScheduleItems.add(loanScheduleItem);
        }

        return new IndividualLoanScheduleImpl(loanScheduleItems);
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