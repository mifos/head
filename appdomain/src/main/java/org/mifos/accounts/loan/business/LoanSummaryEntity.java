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

package org.mifos.accounts.loan.business;

import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.config.AccountingRules;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;

public class LoanSummaryEntity extends AbstractEntity {

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private Integer accountId;

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private LoanBO loan;

    private Money originalPrincipal;

    private Money originalInterest;

    private Money originalFees;

    private Money originalPenalty;

    private Money principalPaid;

    private Money interestPaid;

    private Money feesPaid;

    private Money penaltyPaid;

    private Money rawAmountTotal;

    public Money getRawAmountTotal() {
        return rawAmountTotal;
    }

    public void setRawAmountTotal(Money rawAmountTotal) {
        this.rawAmountTotal = rawAmountTotal;
    }

    protected LoanSummaryEntity() {
        super();
        this.accountId = null;
        this.loan = null;
    }

    public LoanSummaryEntity(LoanBO loan, Money originalPrincipal, Money originalInterest, Money originalFees,
            Money rawAmountTotal) {
        super();
        this.accountId = null;
        this.loan = loan;
        this.originalPrincipal = originalPrincipal;
        this.originalInterest = originalInterest;
        this.originalFees = originalFees;
        this.rawAmountTotal = rawAmountTotal;
        this.originalPenalty = new Money(loan.getCurrency());
        this.principalPaid = new Money(loan.getCurrency());
        this.interestPaid = new Money(loan.getCurrency());
        this.feesPaid = new Money(loan.getCurrency());
        this.penaltyPaid = new Money(loan.getCurrency());
    }

    public Money getFeesPaid() {
        return feesPaid;
    }

    void setFeesPaid(Money feesPaid) {
        this.feesPaid = feesPaid;
    }

    public Money getInterestPaid() {
        return interestPaid;
    }

    void setInterestPaid(Money interestPaid) {
        this.interestPaid = interestPaid;
    }

    public Money getOriginalFees() {
        return originalFees;
    }

    void setOriginalFees(Money originalFees) {
        this.originalFees = originalFees;
    }

    public Money getOriginalInterest() {
        return originalInterest;
    }

    void setOriginalInterest(Money originalInterest) {
        this.originalInterest = originalInterest;
    }

    public Money getOriginalPenalty() {
        return originalPenalty;
    }

    void setOriginalPenalty(Money originalPenalty) {
        this.originalPenalty = originalPenalty;
    }

    public Money getOriginalPrincipal() {
        return originalPrincipal;
    }

    void setOriginalPrincipal(Money originalPrincipal) {
        this.originalPrincipal = originalPrincipal;
    }

    public Money getPenaltyPaid() {
        return penaltyPaid;
    }

    void setPenaltyPaid(Money penaltyPaid) {
        this.penaltyPaid = penaltyPaid;
    }

    public Money getPrincipalPaid() {
        return principalPaid;
    }

    void setPrincipalPaid(Money principalPaid) {
        this.principalPaid = principalPaid;
    }

    void updateFeePaid(Money totalPayment) {
        feesPaid = feesPaid.add(totalPayment);
    }

    public Money getPrincipalDue() {

        if (loanIsWrittenOffOrRescheduled()) {
            return new Money(this.getOriginalPrincipal().getCurrency());
        }
        return getOriginalPrincipal().subtract(getPrincipalPaid());
    }

    public Money getInterestDue() {
        if (loanIsWrittenOffOrRescheduled()) {
            return new Money(this.getOriginalPrincipal().getCurrency());
        }
        if (loan.isDecliningBalanceInterestRecalculation()) {
            Money extraInterest = new Money(loan.getCurrency());
            for (LoanScheduleEntity loanScheduleEntity : loan.getLoanScheduleEntities()) {
                extraInterest = extraInterest.add(loanScheduleEntity.getExtraInterest());
            }
            Money interestDue = getOriginalInterest().subtract(getInterestPaid()).add(extraInterest);
            if (Money.round(interestDue, interestDue.getCurrency().getRoundingAmount(),
                    AccountingRules.getCurrencyRoundingMode()).isZero()) {
                interestDue = Money.zero();
            }
            return interestDue;
        } else {
            return getOriginalInterest().subtract(getInterestPaid());
        }
    }

    public Money getPenaltyDue() {
        if (loanIsWrittenOffOrRescheduled()) {
            return new Money(this.getOriginalPrincipal().getCurrency());
        }
        return getOriginalPenalty().subtract(getPenaltyPaid());
    }

    public Money getFeesDue() {
        if (loanIsWrittenOffOrRescheduled()) {
            return new Money(this.getOriginalPrincipal().getCurrency());
        }
        return getOriginalFees().subtract(getFeesPaid());
    }

    public Money getTotalAmntDue() {
        if (loanIsWrittenOffOrRescheduled()) {
            return new Money(this.getOriginalPrincipal().getCurrency());
        }
        return getPrincipalDue().add(getInterestDue()).add(getPenaltyDue()).add(getFeesDue());
    }

    public Money getTotalLoanAmnt() {
        return getOriginalPrincipal().add(getOriginalFees()).add(getOriginalInterest()).add(getOriginalPenalty());
    }

    public Money getTotalAmntPaid() {
        return getPrincipalPaid().add(getFeesPaid()).add(getInterestPaid()).add(getPenaltyPaid());
    }

    public Money getOutstandingBalance() {
        Money totalAmount = new Money(loan.getCurrency());
        totalAmount = totalAmount.add(getOriginalPrincipal()).subtract(getPrincipalPaid());
        totalAmount = totalAmount.add(getOriginalInterest()).subtract(getInterestPaid());
        totalAmount = totalAmount.add(getOriginalPenalty()).subtract(getPenaltyPaid());
        totalAmount = totalAmount.add(getOriginalFees()).subtract(getFeesPaid());
        return totalAmount;
    }

    public void updatePaymentDetails(PaymentAllocation paymentAllocation) {
        updatePaymentDetails(paymentAllocation.getPrincipalPaid(), paymentAllocation.getTotalInterestPaid(),
                paymentAllocation.getTotalPenaltyPaid(), paymentAllocation.getTotalAndMiscFeesPaid());
    }

    public void updatePaymentDetails(Money principalPaid, Money interestPaid, Money totalPenaltyPaid, Money totalAndMiscFeesPaid) {
        this.principalPaid = this.principalPaid.add(principalPaid);
        this.interestPaid = this.interestPaid.add(interestPaid);
        penaltyPaid = penaltyPaid.add(totalPenaltyPaid);
        feesPaid = feesPaid.add(totalAndMiscFeesPaid);
    }

    public void updatePaymentDetails(LoanTrxnDetailEntity loanReverseTrxn) {
        Money penaltyPaid = loanReverseTrxn.totalPenaltyPaid();
        Money totalAndMiscFeesPaid = loanReverseTrxn.totalAndMiscFeesPaid();
        updatePaymentDetails(loanReverseTrxn.getPrincipalAmount(), loanReverseTrxn.getInterestAmount(), penaltyPaid, totalAndMiscFeesPaid);
    }

    // John W -  to contra the decreaseBy (used when making early repayment) when adjusting the fully paid loan
    void increaseBy(Money principal, Money interest, Money penalty, Money fees) {
        originalPrincipal = originalPrincipal.add(principal);
        originalFees = originalFees.add(fees);
        originalPenalty = originalPenalty.add(penalty);
        originalInterest = originalInterest.add(interest);
        rawAmountTotal = rawAmountTotal.add(interest.add(fees));
    }

    void decreaseBy(Money principal, Money interest, Money penalty, Money fees) {
        originalPrincipal = originalPrincipal.subtract(principal);
        originalFees = originalFees.subtract(fees);
        originalPenalty = originalPenalty.subtract(penalty);
        originalInterest = originalInterest.subtract(interest);
        rawAmountTotal = rawAmountTotal.subtract(interest.add(fees));
    }

    void updateOriginalFees(Money charge) {
        setOriginalFees(getOriginalFees().add(charge));
        rawAmountTotal = rawAmountTotal.add(charge);
    }

    void updateOriginalPenalty(Money charge) {
        setOriginalPenalty(getOriginalPenalty().add(charge));
    }

    private boolean loanIsWrittenOffOrRescheduled() {
        if ((this.loan.getAccountState().getId().equals(AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue()))
                || (this.loan.getAccountState().getId().equals(AccountState.LOAN_CLOSED_RESCHEDULED.getValue()))) {
            return true;
        }
        return false;
    }
}
