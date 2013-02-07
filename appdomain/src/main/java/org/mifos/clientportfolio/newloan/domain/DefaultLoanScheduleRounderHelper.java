package org.mifos.clientportfolio.newloan.domain;

import java.util.List;
import java.util.Set;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.RepaymentTotals;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;

public class DefaultLoanScheduleRounderHelper implements LoanScheduleRounderHelper {
	
	@Override
	public RepaymentTotals calculateInitialTotals_v2(
			List<LoanScheduleEntity> unroundedLoanSchedules, Money loanAmount,
			List<LoanScheduleEntity> allInstallments) {
		
		RepaymentTotals totals = new RepaymentTotals(loanAmount.getCurrency());

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

        for (Object element : unroundedLoanSchedules) {
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
	
	@Override
	public boolean isGraceInstallment_v2(int installmentNum, GraceType graceType, Short gracePeriodDuration) {
		return graceType.equals(GraceType.PRINCIPALONLYGRACE) && installmentNum <= gracePeriodDuration;
	}
	
	@Override
    public void updateRunningTotals_v2(final RepaymentTotals totals, final LoanScheduleEntity currentInstallment) {

        totals.setRunningPayments(totals.getRunningPayments().add(currentInstallment.getTotalPaymentDue()));

        totals.setRunningPrincipal(totals.getRunningPrincipal().add(currentInstallment.getPrincipalDue()));
        totals.setRunningAccountFees(totals.getRunningAccountFees().add(currentInstallment.getTotalFeesDue()));
        totals.setRunningMiscFees(totals.getRunningMiscFees().add(currentInstallment.getMiscFeeDue()));
        totals.setRunningPenalties(totals.getRunningPenalties().add(currentInstallment.getPenaltyDue()));
    }

	
	/**
     * See JavaDoc comment for applyRounding_v2. TODO: handle fees
     */
	@Override
    public void roundAndAdjustLastInstallment_v2(final LoanScheduleEntity lastInstallment, final RepaymentTotals totals) {

        roundInstallmentAccountFeesDue_v2(lastInstallment);
        Money installmentPayment = MoneyUtils.finalRound(totals.getRoundedPaymentsDue().subtract(totals.getRunningPayments()));
        lastInstallment.setPrincipal(MoneyUtils.currencyRound(totals.getRoundedPrincipalDue().subtract(totals.getRunningPrincipal())));
        adjustLastInstallmentFees_v2(lastInstallment, totals, installmentPayment.getCurrency());
        
        lastInstallment.setInterest(MoneyUtils.currencyRound(installmentPayment.subtract(
                lastInstallment.getPrincipalDue()).subtract(lastInstallment.getTotalFeeDueWithMiscFeeDue()).subtract(
                lastInstallment.getPenaltyDue())));
    }

    /**
     * adjust the first fee in the installment's set of fees
     */
    private void adjustLastInstallmentFees_v2(final LoanScheduleEntity lastInstallment, final RepaymentTotals totals, MifosCurrency mifosCurrency) {
        Set<AccountFeesActionDetailEntity> feeDetails = lastInstallment.getAccountFeesActionDetails();
        if (!(feeDetails == null) && !feeDetails.isEmpty()) {
            Money lastInstallmentFeeSum = new Money(mifosCurrency);
            for (AccountFeesActionDetailEntity e : feeDetails) {
                lastInstallmentFeeSum = lastInstallmentFeeSum.add(e.getFeeAmount());
            }
            for (Object element : feeDetails) {
                AccountFeesActionDetailEntity e = (AccountFeesActionDetailEntity) element;
                e.adjustFeeAmount(totals.getRoundedAccountFeesDue().subtract(totals.getRunningAccountFees()).subtract(
                        lastInstallmentFeeSum));
                // just adjust the first fee
                return;
            }
        }
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
    @Override
    public void roundAndAdjustButLastNonGraceInstallment_v2(final LoanScheduleEntity installment) {
        Money roundedTotalInstallmentPaymentDue = MoneyUtils.initialRound(installment.getTotalPaymentDue());
        roundInstallmentAccountFeesDue_v2(installment);

        installment.setInterest(MoneyUtils.currencyRound(installment.getInterest()));
        installment.setPrincipal(roundedTotalInstallmentPaymentDue.subtract(installment.getInterestDue()).subtract(
                installment.getTotalFeeDueWithMiscFeeDue()).subtract(installment.getPenaltyDue()).add(
                installment.getPrincipalPaid()));
    }
    
    @Override
    public void roundAndAdjustNonGraceInstallmentForNewGLIM_v2(final LoanScheduleEntity installment) {
        roundInstallmentAccountFeesDue_v2(installment);
        Money roundedTotalInstallmentPaymentDue = MoneyUtils.initialRound(installment.getTotalDue()).add(installment.getTotalFeesDue());

        installment.setInterest(MoneyUtils.currencyRound(installment.getInterest()));
        // TODO: above comment applies to principal
        installment.setPrincipal(roundedTotalInstallmentPaymentDue.subtract(installment.getInterestDue()).subtract(
                installment.getTotalFeeDueWithMiscFeeDue()).subtract(installment.getPenaltyDue()).add(
                installment.getPrincipalPaid()));
    }

    @Override
    public void roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(final LoanScheduleEntity installment) {
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
    @Override
    public LoanScheduleEntity roundAndAdjustGraceInstallment_v2(final LoanScheduleEntity installment) {
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
}