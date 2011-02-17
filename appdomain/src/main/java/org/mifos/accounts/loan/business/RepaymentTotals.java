package org.mifos.accounts.loan.business;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;

public class RepaymentTotals {

	// rounded or adjusted totals prior to rounding installments
    private Money roundedPaymentsDue;
    private Money roundedInterestDue;
    private Money roundedAccountFeesDue;
    private Money roundedMiscFeesDue;
    private Money roundedPenaltiesDue;
    private Money roundedMiscPenaltiesDue;
    private Money roundedPrincipalDue;

    // running totals as installments are rounded
    private Money runningPayments = null;
    private Money runningAccountFees = null;
    private Money runningPrincipal = null;
    private Money runningMiscFees = null;
    private Money runningPenalties = null;
    private Money runningMiscPenalties = null;
    
	private final MifosCurrency currency;

    public RepaymentTotals(MifosCurrency currency) {
        this.currency = currency;
        runningPayments = new Money(currency, "0");
        runningAccountFees = new Money(currency, "0");
        runningPrincipal = new Money(currency, "0");
        runningMiscFees = new Money(currency, "0");
        runningPenalties = new Money(currency, "0");
        runningMiscPenalties = new Money(currency, "0");
    }

    public Money getRoundedPaymentsDue() {
		return roundedPaymentsDue;
	}

	public void setRoundedPaymentsDue(Money roundedPaymentsDue) {
		this.roundedPaymentsDue = roundedPaymentsDue;
	}

	public Money getRoundedInterestDue() {
		return roundedInterestDue;
	}

	public void setRoundedInterestDue(Money roundedInterestDue) {
		this.roundedInterestDue = roundedInterestDue;
	}

	public Money getRoundedAccountFeesDue() {
		return roundedAccountFeesDue;
	}

	public void setRoundedAccountFeesDue(Money roundedAccountFeesDue) {
		this.roundedAccountFeesDue = roundedAccountFeesDue;
	}

	public Money getRoundedMiscFeesDue() {
		return roundedMiscFeesDue;
	}

	public void setRoundedMiscFeesDue(Money roundedMiscFeesDue) {
		this.roundedMiscFeesDue = roundedMiscFeesDue;
	}

	public Money getRoundedPenaltiesDue() {
		return roundedPenaltiesDue;
	}

	public void setRoundedPenaltiesDue(Money roundedPenaltiesDue) {
		this.roundedPenaltiesDue = roundedPenaltiesDue;
	}

	public Money getRoundedMiscPenaltiesDue() {
		return roundedMiscPenaltiesDue;
	}

	public void setRoundedMiscPenaltiesDue(Money roundedMiscPenaltiesDue) {
		this.roundedMiscPenaltiesDue = roundedMiscPenaltiesDue;
	}

	public Money getRoundedPrincipalDue() {
		return roundedPrincipalDue;
	}

	public void setRoundedPrincipalDue(Money roundedPrincipalDue) {
		this.roundedPrincipalDue = roundedPrincipalDue;
	}

	public Money getRunningPayments() {
		return runningPayments;
	}

	public void setRunningPayments(Money runningPayments) {
		this.runningPayments = runningPayments;
	}

	public Money getRunningAccountFees() {
		return runningAccountFees;
	}

	public void setRunningAccountFees(Money runningAccountFees) {
		this.runningAccountFees = runningAccountFees;
	}

	public Money getRunningPrincipal() {
		return runningPrincipal;
	}

	public void setRunningPrincipal(Money runningPrincipal) {
		this.runningPrincipal = runningPrincipal;
	}

	public Money getRunningMiscFees() {
		return runningMiscFees;
	}

	public void setRunningMiscFees(Money runningMiscFees) {
		this.runningMiscFees = runningMiscFees;
	}

	public Money getRunningPenalties() {
		return runningPenalties;
	}

	public void setRunningPenalties(Money runningPenalties) {
		this.runningPenalties = runningPenalties;
	}

	public Money getRunningMiscPenalties() {
		return runningMiscPenalties;
	}

	public void setRunningMiscPenalties(Money runningMiscPenalties) {
		this.runningMiscPenalties = runningMiscPenalties;
	}

	public MifosCurrency getCurrency() {
		return currency;
	}
}