/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.accounts.loan.schedule.domain;

import org.apache.commons.lang.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;

import static org.mifos.accounts.loan.schedule.utils.Utilities.isGreaterThanZero;

public class Installment implements Comparable<Installment> {
    private Integer id;
    private Date dueDate;
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal fees;
    private BigDecimal overdueInterest;
    private BigDecimal effectiveInterest;
    private InstallmentPayments payments;

    public Installment(Integer id, Date dueDate, BigDecimal principal, BigDecimal interest, BigDecimal fees) {
        this.id = id;
        this.dueDate = dueDate;
        this.principal = principal;
        this.interest = interest;
        this.fees = fees;
        this.payments = new InstallmentPayments();
    }

    public Integer getId() {
        return id;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public BigDecimal getOverdueInterest() {
        return overdueInterest == null ? BigDecimal.ZERO : overdueInterest;
    }

    public void setOverdueInterest(BigDecimal overdueInterest) {
        this.overdueInterest = overdueInterest;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public boolean isDue() {
        return isFeesDue() || isInterestDue() || isPrincipalDue() || isOverdueInterestDue();
    }

    public int compareTo(Installment installment) {
        return this.getId().compareTo(installment.getId());
    }

    public BigDecimal getFeesDue() {
        return fees.subtract(getFeesPaid());
    }

    public BigDecimal getInterestDue() {
        return hasEffectiveInterest() ? effectiveInterest : interest.subtract(getInterestPaid());
    }

    public BigDecimal getPrincipalDue() {
        return principal.subtract(getPrincipalPaid());
    }

    public BigDecimal getOverdueInterestDue() {
        return overdueInterest.subtract(getOverdueInterestPaid());
    }

    public boolean isFeesDue() {
        return isGreaterThanZero(getFeesDue());
    }

    public boolean isInterestDue() {
        return isGreaterThanZero(getInterestDue());
    }

    public boolean isOverdueInterestDue() {
        return isGreaterThanZero(getOverdueInterestDue());
    }

    public boolean isPrincipalDue() {
        return isGreaterThanZero(getPrincipalDue());
    }

    public BigDecimal pay(BigDecimal amount, Date transactionDate) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(transactionDate);
        amount = payOverdueInterest(amount, installmentPayment);
        amount = payFees(amount, installmentPayment);
        amount = payInterest(amount, installmentPayment);
        amount = payPrincipal(amount, installmentPayment);
        payments.addPayment(installmentPayment);
        return amount;
    }

    private BigDecimal payFees(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = getLowestOf(getFeesDue(), amount);
        installmentPayment.setFeesPaid(installmentPayment.getFeesPaid().add(payable));
        return amount.subtract(payable);
    }

    private BigDecimal payInterest(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = getLowestOf(getInterestDue(), amount);
        installmentPayment.setInterestPaid(installmentPayment.getInterestPaid().add(payable));
        return amount.subtract(payable);
    }

    public BigDecimal payInterestDueTillDate(BigDecimal amount, Date transactionDate, BigDecimal interestDueTillDate) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(transactionDate);
        BigDecimal payable = getLowestOf(interestDueTillDate, amount);
        installmentPayment.setInterestPaid(installmentPayment.getInterestPaid().add(payable));
        payments.addPayment(installmentPayment);
        return amount.subtract(payable);
    }

    private BigDecimal payOverdueInterest(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = getLowestOf(getOverdueInterestDue(), amount);
        installmentPayment.setOverdueInterestPaid(installmentPayment.getOverdueInterestPaid().add(payable));
        return amount.subtract(payable);
    }

    private BigDecimal getLowestOf(BigDecimal overdueInterest, BigDecimal amount) {
        return amount.compareTo(overdueInterest) > 0 ? overdueInterest : amount;
    }

    private BigDecimal payPrincipal(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = getLowestOf(getPrincipalDue(), amount);
        installmentPayment.setPrincipalPaid(installmentPayment.getPrincipalPaid().add(payable));
        return amount.subtract(payable);
    }

    public BigDecimal payPrincipal(BigDecimal amount, Date transactionDate) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(transactionDate);
        payments.addPayment(installmentPayment);
        return payPrincipal(amount, installmentPayment);
    }

    public BigDecimal payOverdueInterest(BigDecimal amount, Date transactionDate) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(transactionDate);
        payments.addPayment(installmentPayment);
        return payOverdueInterest(amount, installmentPayment);
    }

    public BigDecimal getPrincipalPaid() {
        return payments.getPrincipalPaid();
    }

    public Date getEarliestPaidDate() {
        return (Date) ObjectUtils.min(getTotalPrincipalPaymentDate(), dueDate);
    }

    private Date getTotalPrincipalPaymentDate() {
        return isPrincipalDue() ? this.dueDate : getRecentPartialPaymentDate();
    }

    public Date getRecentPartialPaymentDate() {
        return payments.getRecentPartialPaymentDate();
    }

    public boolean isAnyPrincipalPaid() {
        return isGreaterThanZero(getPrincipalPaid());
    }

    public Date getRecentPrincipalPaidDate() {
        return payments.getRecentPrincipalPaidDate();
    }

    public void addOverdueInterest(BigDecimal overdueInterest) {
        setOverdueInterest(getOverdueInterest().add(overdueInterest));
    }

    public BigDecimal getRecentPrincipalPayment() {
        InstallmentPayment installmentPayment = payments.getRecentPrincipalPayment();
        return installmentPayment == null ? BigDecimal.ZERO : installmentPayment.getPrincipalPaid();
    }

    public BigDecimal getInterestPaid() {
        return payments.getInterestPaid();
    }

    public BigDecimal getOverdueInterestPaid() {
        return payments.getOverdueInterestPaid();
    }

    public BigDecimal getFeesPaid() {
        return payments.getFeesPaid();
    }

    public BigDecimal getEffectiveInterest() {
        return effectiveInterest;
    }

    public void setEffectiveInterest(BigDecimal effectiveInterest) {
        this.effectiveInterest = effectiveInterest;
    }

    public boolean hasEffectiveInterest() {
        return effectiveInterest != null && isGreaterThanZero(effectiveInterest);
    }

    public BigDecimal getApplicableInterest() {
        return hasEffectiveInterest() ? effectiveInterest : interest;
    }
}
