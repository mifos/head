package org.mifos.clientportfolio.newloan.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;

import edu.emory.mathcs.backport.java.util.Collections;

public class DefaultLoanScheduleRounder implements LoanScheduleRounder {

    @Override
    public List<LoanScheduleEntity> round(List<LoanScheduleEntity> unroundedLoanSchedules, List<LoanScheduleEntity> allExistingLoanSchedules, GraceType graceType, Integer gracePeriodDuration, Money loanAmount, InterestType interestType) {
        Collections.sort(unroundedLoanSchedules);
        List<LoanScheduleEntity> roundedLoanSchedules = new ArrayList<LoanScheduleEntity>();
        LoanRepaymentTotals totals = calculateInitialTotals_v2(unroundedLoanSchedules, loanAmount, allExistingLoanSchedules);
        int installmentNum = 0;
        for (Iterator<LoanScheduleEntity> it = unroundedLoanSchedules.iterator(); it.hasNext();) {
            LoanScheduleEntity currentInstallment = it.next();
            LoanScheduleEntity roundedInstallment = currentInstallment;
            installmentNum++;
            if (it.hasNext()) { // handle all but the last installment
                if (isGraceInstallment_v2(installmentNum, graceType, gracePeriodDuration)) {
                    roundedInstallment = roundAndAdjustGraceInstallment_v2(roundedInstallment);
                } else if (interestType.equals(InterestType.DECLINING_EPI)) {
                    roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(roundedInstallment);
                } else {
                    roundAndAdjustButLastNonGraceInstallment_v2(roundedInstallment);
                }
                updateRunningTotals_v2(totals, roundedInstallment);
            } else {
                roundAndAdjustLastInstallment_v2(roundedInstallment, totals);
            }
            roundedLoanSchedules.add(roundedInstallment);
        } // for

        return roundedLoanSchedules;
    }

    /**
     * See JavaDoc comment for applyRounding_v2. TODO: handle fees
     */
    private void roundAndAdjustLastInstallment_v2(final LoanScheduleEntity lastInstallment, final LoanRepaymentTotals totals) {

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
    private void adjustLastInstallmentFees_v2(final LoanScheduleEntity lastInstallment, final LoanRepaymentTotals totals) {
        Set<AccountFeesActionDetailEntity> feeDetails = lastInstallment.getAccountFeesActionDetails();
        if (!(feeDetails == null) && !feeDetails.isEmpty()) {
            Money lastInstallmentFeeSum = null;
            for (AccountFeesActionDetailEntity e : feeDetails) {
                if (lastInstallmentFeeSum == null) {
                    lastInstallmentFeeSum = e.getFeeAmount();
                } else {
                    lastInstallmentFeeSum = lastInstallmentFeeSum.add(e.getFeeAmount());
                }
            }
            for (Object element : feeDetails) {
                AccountFeesActionDetailEntity e = (AccountFeesActionDetailEntity) element;
                e.adjustFeeAmount(totals.roundedAccountFeesDue.subtract(totals.runningAccountFees).subtract(lastInstallmentFeeSum));
                // just adjust the first fee
                return;
            }
        }
    }

    private void updateRunningTotals_v2(final LoanRepaymentTotals totals, final LoanScheduleEntity currentInstallment) {

        totals.runningPayments = totals.runningPayments.add(currentInstallment.getTotalPaymentDue());

        totals.runningPrincipal = totals.runningPrincipal.add(currentInstallment.getPrincipalDue());
        totals.runningAccountFees = totals.runningAccountFees.add(currentInstallment.getTotalFeesDue());
        totals.runningMiscFees = totals.runningMiscFees.add(currentInstallment.getMiscFeeDue());
        totals.runningPenalties = totals.runningPenalties.add(currentInstallment.getPenaltyDue());
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

    private void roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(final LoanScheduleEntity installment) {
        Money roundedTotalInstallmentPaymentDue = MoneyUtils.initialRound(installment.getTotalPaymentDue());
        roundInstallmentAccountFeesDue_v2(installment);
        installment.setPrincipal(MoneyUtils.currencyRound(installment.getPrincipal()));
        // TODO: above comment applies to principal
        installment.setInterest(roundedTotalInstallmentPaymentDue.subtract(installment.getPrincipalDue()).subtract(
                installment.getTotalFeeDueWithMiscFeeDue()).subtract(installment.getPenaltyDue()));
    }

    /**
     * For principal-only grace installments, adjust the interest to account for rounding discrepancies.
     */
    private LoanScheduleEntity roundAndAdjustGraceInstallment_v2(final LoanScheduleEntity installment) {
        Money roundedInstallmentTotalPaymentDue = MoneyUtils.initialRound(installment.getTotalPaymentDue());

        LoanScheduleEntity roundedInstallment = roundInstallmentAccountFeesDue_v2(installment);

        roundedInstallment.setInterest(roundedInstallmentTotalPaymentDue.subtract(roundedInstallment.getTotalFeeDueWithMiscFeeDue())
                .subtract(roundedInstallment.getPenaltyDue()));

        return roundedInstallment;
    }

    private LoanScheduleEntity roundInstallmentAccountFeesDue_v2(final LoanScheduleEntity installment) {

        for (Object element : installment.getAccountFeesActionDetails()) {
            AccountFeesActionDetailEntity e = (AccountFeesActionDetailEntity) element;
            e.roundFeeAmount(MoneyUtils.currencyRound(e.getFeeDue().add(e.getFeeAmountPaid())));
        }
        return installment;
    }


    /**
     * A grace-period installment can appear in the loan schedule only if the loan is setup with principal-only grace.
     */
    private boolean isGraceInstallment_v2(final int installmentNum, GraceType graceType, int gracePeriodDuration) {
        return graceType.equals(GraceType.PRINCIPALONLYGRACE) && installmentNum <= gracePeriodDuration;
    }

    private LoanRepaymentTotals calculateInitialTotals_v2(final List<? extends AccountActionDateEntity> installmentsToBeRounded, Money loanAmount, final List<? extends AccountActionDateEntity> allInstallments) {

        LoanRepaymentTotals totals = new LoanRepaymentTotals(loanAmount.getCurrency());

        Money exactTotalInterestDue = new Money(loanAmount.getCurrency(), "0");
        Money exactTotalAccountFeesDue = new Money(loanAmount.getCurrency(), "0");
        Money exactTotalMiscFeesDue = new Money(loanAmount.getCurrency(), "0");
        Money exactTotalMiscPenaltiesDue = new Money(loanAmount.getCurrency(), "0");

        // principal due = loan amount less any payments on principal
        Money exactTotalPrincipalDue = loanAmount;
        for (AccountActionDateEntity e : allInstallments) {
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
}