package org.mifos.clientportfolio.newloan.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.RepaymentTotals;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.config.AccountingRules;
import org.mifos.framework.util.helpers.Money;

/**
 * I am responsible for applying rounding rules from AccountingRules.
 *
 * <h2>Summary of rounding rules</h2> There are two factors that make up a rounding rule: precision and mode.
 *
 * <dl>
 * <dt> <em>Precision</em>
 * <dd>specifies the degree of rounding, to the closest decimal place. Example: 1 (closest Rupee), 0.5 (closest
 * half-dollar, for example), 0.1, 0.01 (closest US penny) 0.001, etc. Precision is limited by the currency being
 * used by the application. For example, US dollars limit the precision to two decimal places (closest penny).
 *
 * <dt> <em>Mode</em>
 * <dd>specfies how rounding occurs. Currently three modes are supported: HALF_UP, FLOOR, CEILING.
 * </dl>
 *
 * Three installment-rounding conventions apply to loan-installment payments. Each specifies the precision and mode
 * to be applied in certain contexts:
 *
 * <dl>
 * <dt> <em>Currency-rounding</em>
 * <dd>Round to the precision of the currency being used by the application.
 *
 * <dt> <em>Initial rounding</em>
 * <dd>Round all installment's total payment but the last.
 *
 * <dt> <em>Final rounding</em>
 * <dd>Round the last payment to a specified precision.
 * </dl>
 *
 *
 * <h2>Summary of rounding and adjustment logic</h2>
 *
 * Assume we've calculated exact values for each installment's principal, interest, and fees payment, and
 * installment's total payment (their sum).
 * <p/>
 * The concept here is that exact values will be rounded and the amounts that the customer actually pays will drift
 * away from what's actually due, resulting in the components of each installment not exactly adding up to the total
 * payment.
 * <p/>
 * Generally, within each installment but the last, the principal payment is the "fall guy", making up for any
 * difference. For the last installment, the interest payment is the fall guy.
 * <p/>
 * Differences in total paid across all installments are made up in the last installment.
 * <p/>
 * <h4>Rounding and adjusting total payments</h4>
 * First compute the rounded and adjusted totals for the loan. These are used to adjust the final installment's
 * payments.
 * <ul>
 * <li>Round the loan's exact total payments (sum of exact principal, exact interest, exact fees) using final
 * rounding.
 * <li>No need to round the principal, since it is entered using precision of the prevailing currency.
 * <li>Round total fees using currency rounding
 * <li>Adjust the total interest so that rounded fees, principal, and adjusted interest sum to the rounded total
 * payments.
 * </ul>
 * </ul>
 * <h4>Non-grace-period installments except the last:</h4>
 * <ul>
 * <li>Round the installment's exact total payment using initial rounding.
 * <li>Round the installment's exact interest and fee payments using currency rounding.
 * <li>Round each of the installment's account fees using currency rounding.
 * <li>Adjust the installment's principal to make up the difference between the installment's rounded total payment
 * and its rounded interest and fee payments.
 * <li>After rounding and adjusting, the installment's (rounded) total payment is exactly the sum of (rounded)
 * principal, interest and fees.
 * </ul>
 *
 * <h4>The last installment:</h4>
 * <ul>
 * <li>Correct for over- or underpayment of prior installment's payments due to rounding:
 * <ul>
 * <li>Compute the loan's exact total payment as the sum of all installment's exact principal, interest and fees.
 * <li>Round the loan's exact total payment using final rounding. This is what the customer must pay to pay off the
 * loan.
 * <li>Set the final installment's total payment to the difference between the loan's rounded total payment and the
 * sum of all prior installments' (already rounded) payments.
 * </ul>
 * <li>Correct for over or underpayment of principal. Set the last installment's principal payment to the difference
 * between the loan amount and the sum of all prior installment's principal payments, then round it using currency
 * rounding rules.
 * <li>Correct for over- or underpayment of fees:
 * <ul>
 * <li>Round the exact total fees using Currency rounding rules.
 * <li>Set one of last installment's fee payments to the difference between the rounded total fees and the sum of
 * all prior installments' (already rounded) fee payments.
 * </ul>
 * <li>Finally, adjust the last installment's interest payment as the difference between the last installment's
 * total payment and the sum of the last installment's principal and fee payments.
 * </ul>
 *
 * <h4>Principal-only grace-period installments</h4>
 *
 * The principal is always zero, and only interest and fees are paid. Here, interest is the "fall guy", absorbing
 * any rounding discrepancies:
 * <ul>
 * <li>Round the installment's total payments as above.
 * <li>Round the installment's fee payment as above.
 * <li>Adjust the interest to force interest and fee payments to add up to the installment's total payment.
 * </ul>
 * <h4>Principal + interest grace-period installments</h4>
 *
 * Calculations are the same as if there were no grace, since the zero-payment installments are not included in the
 * installment list at all.
 */
public class DefaultLoanScheduleRounder implements LoanScheduleRounder {

	private final LoanScheduleRounderHelper loanScheduleInstallmentRounder;

	public DefaultLoanScheduleRounder(LoanScheduleRounderHelper loanScheduleInstallmentRounder) {
		this.loanScheduleInstallmentRounder = loanScheduleInstallmentRounder;
	}
	
	@Override
	public List<LoanScheduleEntity> round(GraceType graceType, Short gracePeriodDuration, Money loanAmount,
			InterestType interestType,
			List<LoanScheduleEntity> unroundedLoanSchedules,
			List<LoanScheduleEntity> allExistingLoanSchedules) {
		
		Collections.sort(unroundedLoanSchedules);
        List<LoanScheduleEntity> roundedLoanSchedules = new ArrayList<LoanScheduleEntity>();
        RepaymentTotals totals = loanScheduleInstallmentRounder.calculateInitialTotals_v2(unroundedLoanSchedules, loanAmount, allExistingLoanSchedules);
        int installmentNum = 0;
        for (Iterator<LoanScheduleEntity> it = unroundedLoanSchedules.iterator(); it.hasNext();) {
            LoanScheduleEntity currentInstallment = it.next();
            LoanScheduleEntity roundedInstallment = currentInstallment;
            installmentNum++;
            if (it.hasNext()) { // handle all but the last installment
                if (loanScheduleInstallmentRounder.isGraceInstallment_v2(installmentNum, graceType, gracePeriodDuration)) {
                    roundedInstallment = loanScheduleInstallmentRounder.roundAndAdjustGraceInstallment_v2(roundedInstallment);
                } else if (interestType.equals(InterestType.DECLINING_EPI)) {
                	loanScheduleInstallmentRounder.roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(roundedInstallment);
                } else {
                    loanScheduleInstallmentRounder.roundAndAdjustButLastNonGraceInstallment_v2(roundedInstallment);
                }
                loanScheduleInstallmentRounder.updateRunningTotals_v2(totals, roundedInstallment);
            } else {
            	loanScheduleInstallmentRounder.roundAndAdjustLastInstallment_v2(roundedInstallment, totals);
            }
            roundedLoanSchedules.add(roundedInstallment);
        } // for
		return roundedLoanSchedules;
	}
}