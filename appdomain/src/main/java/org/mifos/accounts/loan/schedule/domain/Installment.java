/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static org.mifos.accounts.loan.schedule.utils.Utilities.isGreaterThanZero;
import static org.mifos.framework.util.helpers.NumberUtils.max;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.mifos.framework.util.helpers.NumberUtils;

public class Installment implements Comparable<Installment> {
    private Integer id;
    private Date dueDate;
    private InstallmentPayments previousPayments;
    private InstallmentPayment currentPayment;
    private Map<InstallmentComponent, BigDecimal> actualAmounts;

    /**
     * @deprecated This constructor is intended to be used from unit test builders. It does only partial initialization
     * of the new instance.
     */
    @Deprecated
    Installment() {
        actualAmounts = new LinkedHashMap<InstallmentComponent, BigDecimal>();
        previousPayments = new InstallmentPayments();
        resetCurrentPayment();
        resetPaymentComponents();
    }

    public Installment(Integer id, Date dueDate, BigDecimal principal, BigDecimal interest, BigDecimal extraInterest,
                       BigDecimal fees, BigDecimal miscFees, BigDecimal penalty, BigDecimal miscPenalty) {
        this();
        this.id = id;
        this.dueDate = dueDate;
        actualAmounts.put(InstallmentComponent.PRINCIPAL, principal);
        actualAmounts.put(InstallmentComponent.INTEREST, interest);
        actualAmounts.put(InstallmentComponent.EXTRA_INTEREST, extraInterest);
        actualAmounts.put(InstallmentComponent.FEES, fees);
        actualAmounts.put(InstallmentComponent.MISC_FEES, miscFees);
        actualAmounts.put(InstallmentComponent.PENALTY, penalty);
        actualAmounts.put(InstallmentComponent.MISC_PENALTY, miscPenalty);
    }

    private void resetPaymentComponents() {
        actualAmounts.put(InstallmentComponent.PRINCIPAL, BigDecimal.ZERO);
        actualAmounts.put(InstallmentComponent.INTEREST, BigDecimal.ZERO);
        actualAmounts.put(InstallmentComponent.EXTRA_INTEREST, BigDecimal.ZERO);
        actualAmounts.put(InstallmentComponent.EFFECTIVE_INTEREST, BigDecimal.ZERO);
        actualAmounts.put(InstallmentComponent.FEES, BigDecimal.ZERO);
        actualAmounts.put(InstallmentComponent.MISC_FEES, BigDecimal.ZERO);
        actualAmounts.put(InstallmentComponent.PENALTY, BigDecimal.ZERO);
        actualAmounts.put(InstallmentComponent.MISC_PENALTY, BigDecimal.ZERO);
    }

    public Integer getId() {
        return id;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public BigDecimal getPrincipal() {
        return actualAmounts.get(InstallmentComponent.PRINCIPAL);
    }

    public BigDecimal getInterest() {
        return actualAmounts.get(InstallmentComponent.INTEREST);
    }

    public BigDecimal getExtraInterest() {
        return actualAmounts.get(InstallmentComponent.EXTRA_INTEREST);
    }

    public void setExtraInterest(BigDecimal extraInterest) {
        actualAmounts.put(InstallmentComponent.EXTRA_INTEREST, extraInterest);
    }

    public BigDecimal getMiscPenalty() {
        return actualAmounts.get(InstallmentComponent.MISC_PENALTY);
    }

    public BigDecimal getPenalty() {
        return actualAmounts.get(InstallmentComponent.PENALTY);
    }

    public BigDecimal getMiscFees() {
        return actualAmounts.get(InstallmentComponent.MISC_FEES);
    }

    public BigDecimal getFees() {
        return actualAmounts.get(InstallmentComponent.FEES);
    }

    public BigDecimal getTotalDue() {
        return getMiscPenaltyDue().add(getPenaltyDue()).add(getMiscFeesDue()).add(getFeesDue()).
                add(getExtraInterestDue()).add(getInterestDue()).add(getPrincipalDue());
    }

    @Override
	public int compareTo(Installment installment) {
        return this.getId().compareTo(installment.getId());
    }

    public BigDecimal getMiscPenaltyDue() {
        return getMiscPenalty().subtract(getMiscPenaltyPaid());
    }

    public BigDecimal getPenaltyDue() {
        return getPenalty().subtract(getPenaltyPaid());
    }

    public BigDecimal getMiscFeesDue() {
        return getMiscFees().subtract(getMiscFeesPaid());
    }

    public BigDecimal getFeesDue() {
        return getFees().subtract(getFeesPaid());
    }

    public BigDecimal getInterestDue() {
        return hasEffectiveInterest() ? getEffectiveInterest() : getInterest().subtract(getInterestPaid());
    }

    public BigDecimal getPrincipalDue() {
        return getPrincipal().subtract(getPrincipalPaid());
    }

    public BigDecimal getExtraInterestDue() {
        return getExtraInterest().subtract(getExtraInterestPaid());
    }

    public boolean isMiscPenaltyDue() {
        return isGreaterThanZero(getMiscPenaltyDue());
    }

    public boolean isPenaltyDue() {
        return isGreaterThanZero(getPenaltyDue());
    }

    public boolean isMiscFeesDue() {
        return isGreaterThanZero(getMiscPenaltyDue());
    }

    public boolean isFeesDue() {
        return isGreaterThanZero(getFeesDue());
    }

    public boolean isInterestDue() {
        return isGreaterThanZero(getInterestDue());
    }

    public boolean isExtraInterestDue() {
        return isGreaterThanZero(getExtraInterestDue());
    }

    public boolean isPrincipalDue() {
        return isGreaterThanZero(getPrincipalDue());
    }

    public BigDecimal pay(BigDecimal amount, Date transactionDate) {
        currentPayment.setPaidDate(transactionDate);
        amount = payMiscPenalty(amount, currentPayment);
        amount = payPenalty(amount, currentPayment);
        amount = payMiscFees(amount, currentPayment);
        amount = payFees(amount, currentPayment);
        amount = payExtraInterest(amount, currentPayment);
        amount = payInterest(amount, currentPayment);
        amount = payPrincipal(amount, currentPayment);
        recordCurrentPayment();
        return amount;
    }
    
    public BigDecimal payInterest(BigDecimal amount, Date transactionDate) {
        currentPayment.setPaidDate(transactionDate);
        amount = payInterest(amount, currentPayment);
        amount = payExtraInterest(amount, currentPayment);
        recordCurrentPayment();
        return amount;
    }

    private BigDecimal payMiscPenalty(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = NumberUtils.min(amount, getMiscPenaltyDue());
        installmentPayment.setMiscPenaltyPaid(installmentPayment.getMiscPenaltyPaid().add(payable));
        return amount.subtract(payable);
    }

    private BigDecimal payPenalty(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = NumberUtils.min(amount, getPenaltyDue());
        installmentPayment.setPenaltyPaid(installmentPayment.getPenaltyPaid().add(payable));
        return amount.subtract(payable);
    }

    private BigDecimal payMiscFees(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = NumberUtils.min(amount, getMiscFeesDue());
        installmentPayment.setMiscFeesPaid(installmentPayment.getMiscFeesPaid().add(payable));
        return amount.subtract(payable);
    }

    private BigDecimal payFees(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = NumberUtils.min(amount, getFeesDue());
        installmentPayment.setFeesPaid(installmentPayment.getFeesPaid().add(payable));
        return amount.subtract(payable);
    }

    private BigDecimal payInterest(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = NumberUtils.min(amount, getInterestDue());
        installmentPayment.setInterestPaid(installmentPayment.getInterestPaid().add(payable));
        return amount.subtract(payable);
    }

    public BigDecimal payInterestDueTillDate(BigDecimal amount, Date transactionDate, BigDecimal interestDueTillDate) {
        currentPayment.setPaidDate(transactionDate);
        BigDecimal payable = NumberUtils.min(amount, interestDueTillDate);
        currentPayment.setInterestPaid(currentPayment.getInterestPaid().add(payable));
        return amount.subtract(payable);
    }

    private BigDecimal payExtraInterest(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = NumberUtils.min(amount, getExtraInterestDue());
        installmentPayment.setExtraInterestPaid(installmentPayment.getExtraInterestPaid().add(payable));
        return amount.subtract(payable);
    }

    private BigDecimal payPrincipal(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = NumberUtils.min(amount, getPrincipalDue());
        installmentPayment.setPrincipalPaid(installmentPayment.getPrincipalPaid().add(payable));
        return amount.subtract(payable);
    }

    public BigDecimal payPrincipal(BigDecimal amount, Date transactionDate) {
        currentPayment.setPaidDate(transactionDate);
        return payPrincipal(amount, currentPayment);
    }

    public BigDecimal payExtraInterest(BigDecimal amount, Date transactionDate) {
        currentPayment.setPaidDate(transactionDate);
        return payExtraInterest(amount, currentPayment);
    }

    public BigDecimal getPrincipalPaid() {
        return previousPayments.getPrincipalPaid();
    }

    public boolean isAnyPrincipalPaid() {
        return isGreaterThanZero(getPrincipalPaid());
    }

    public Date getRecentPrincipalPaidDate() {
        return previousPayments.getRecentPrincipalPaidDate();
    }

    public void addExtraInterest(BigDecimal extraInterest) {
        setExtraInterest(getExtraInterest().add(extraInterest));
    }

    public BigDecimal getInterestPaid() {
        return previousPayments.getInterestPaid();
    }

    public BigDecimal getExtraInterestPaid() {
        return previousPayments.getExtraInterestPaid();
    }

    public BigDecimal getMiscPenaltyPaid() {
        return previousPayments.getMiscPenaltyPaid();
    }

    public BigDecimal getPenaltyPaid() {
        return previousPayments.getPenaltyPaid();
    }

    public BigDecimal getMiscFeesPaid() {
        return previousPayments.getMiscFeesPaid();
    }

    public BigDecimal getFeesPaid() {
        return previousPayments.getFeesPaid();
    }

    public BigDecimal getEffectiveInterest() {
        return actualAmounts.get(InstallmentComponent.EFFECTIVE_INTEREST);
    }

    void setEffectiveInterest(BigDecimal effectiveInterest) {
        actualAmounts.put(InstallmentComponent.EFFECTIVE_INTEREST, effectiveInterest);
    }

    /**
    * Assumption: Make payment should not payoff all outstanding principal as repay does
     * When assumption is wrong, effective interest can be zero, which in turn corrupts
     * applicable interest
    */
    public boolean hasEffectiveInterest() {
        return isGreaterThanZero(getEffectiveInterest());
    }

    public BigDecimal getApplicableInterest() {
        return hasEffectiveInterest() ? getEffectiveInterest() : getInterest();
    }

    public void addPayment(InstallmentPayment installmentPayment){
        previousPayments.addPayment(installmentPayment);
    }

    void setId(Integer id) {
        this.id = id;
    }

    void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
        * @deprecated Use the corresponding setters for assigning installment components
        */
    @Deprecated
    void setAmount(InstallmentComponent installmentComponent, BigDecimal amount){
        actualAmounts.put(installmentComponent, amount);
    }

    public InstallmentPayment getCurrentPayment() {
        return currentPayment;
    }

    public void recordCurrentPayment() {
        previousPayments.addPayment(currentPayment);
    }

    public void resetCurrentPayment() {
        currentPayment = new InstallmentPayment();
    }

    public Date fromDateForOverdueComputation() {
        return max(getDueDate(), getRecentPrincipalPaidDate());
    }

    public BigDecimal getCurrentPrincipalPaid() {
        return currentPayment.getPrincipalPaid();
    }

    public BigDecimal getCurrentInterestPaid() {
        return currentPayment.getInterestPaid();
    }

    public BigDecimal getCurrentExtraInterestPaid() {
        return currentPayment.getExtraInterestPaid();
    }

    public BigDecimal getCurrentFeesPaid() {
        return currentPayment.getFeesPaid();
    }

    public BigDecimal getCurrentMiscFeesPaid() {
        return currentPayment.getMiscFeesPaid();
    }

    public BigDecimal getCurrentPenaltyPaid() {
        return currentPayment.getPenaltyPaid();
    }

    public BigDecimal getCurrentMiscPenaltyPaid() {
        return currentPayment.getMiscPenaltyPaid();
    }
}
