package org.mifos.clientportfolio.newloan.domain;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;

public class LoanRepaymentTotals {

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
    Money runningMiscPenalties = null;

    private final MifosCurrency currency;

    public LoanRepaymentTotals(MifosCurrency currency) {
        this.currency = currency;
        runningPayments = new Money(currency, "0");
        runningAccountFees = new Money(currency, "0");
        runningPrincipal = new Money(currency, "0");
        runningMiscFees = new Money(currency, "0");
        runningPenalties = new Money(currency, "0");
        runningMiscPenalties = new Money(currency, "0");
    }

    public Money getRoundedPaymentsDue() {
        return this.roundedPaymentsDue;
    }

    public void setRoundedPaymentsDue(Money roundedPaymentsDue) {
        this.roundedPaymentsDue = roundedPaymentsDue;
    }

    public Money getRoundedInterestDue() {
        return this.roundedInterestDue;
    }

    public void setRoundedInterestDue(Money roundedInterestDue) {
        this.roundedInterestDue = roundedInterestDue;
    }

    public Money getRoundedAccountFeesDue() {
        return this.roundedAccountFeesDue;
    }

    public void setRoundedAccountFeesDue(Money roundedAccountFeesDue) {
        this.roundedAccountFeesDue = roundedAccountFeesDue;
    }

    public Money getRoundedMiscFeesDue() {
        return this.roundedMiscFeesDue;
    }

    public void setRoundedMiscFeesDue(Money roundedMiscFeesDue) {
        this.roundedMiscFeesDue = roundedMiscFeesDue;
    }

    public Money getRoundedPenaltiesDue() {
        return this.roundedPenaltiesDue;
    }

    public void setRoundedPenaltiesDue(Money roundedPenaltiesDue) {
        this.roundedPenaltiesDue = roundedPenaltiesDue;
    }

    public Money getRoundedMiscPenaltiesDue() {
        return this.roundedMiscPenaltiesDue;
    }

    public void setRoundedMiscPenaltiesDue(Money roundedMiscPenaltiesDue) {
        this.roundedMiscPenaltiesDue = roundedMiscPenaltiesDue;
    }

    public Money getRoundedPrincipalDue() {
        return this.roundedPrincipalDue;
    }

    public void setRoundedPrincipalDue(Money roundedPrincipalDue) {
        this.roundedPrincipalDue = roundedPrincipalDue;
    }

    public Money getRunningPayments() {
        return this.runningPayments;
    }

    public void setRunningPayments(Money runningPayments) {
        this.runningPayments = runningPayments;
    }

    public Money getRunningAccountFees() {
        return this.runningAccountFees;
    }

    public void setRunningAccountFees(Money runningAccountFees) {
        this.runningAccountFees = runningAccountFees;
    }

    public Money getRunningPrincipal() {
        return this.runningPrincipal;
    }

    public void setRunningPrincipal(Money runningPrincipal) {
        this.runningPrincipal = runningPrincipal;
    }

    public Money getRunningMiscFees() {
        return this.runningMiscFees;
    }

    public void setRunningMiscFees(Money runningMiscFees) {
        this.runningMiscFees = runningMiscFees;
    }

    public Money getRunningPenalties() {
        return this.runningPenalties;
    }

    public void setRunningPenalties(Money runningPenalties) {
        this.runningPenalties = runningPenalties;
    }

    public Money getRunningMiscPenalties() {
        return this.runningMiscPenalties;
    }

    public void setRunningMiscPenalties(Money runningMiscPenalties) {
        this.runningMiscPenalties = runningMiscPenalties;
    }

    public MifosCurrency getCurrency() {
        return this.currency;
    }
}