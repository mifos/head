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

import static org.mifos.accounts.loan.schedule.utils.Utilities.getDaysInBetween;
import static org.mifos.framework.util.helpers.NumberUtils.max;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mifos.accounts.loan.business.RepaymentResultsHolder;
import org.mifos.accounts.loan.schedule.utils.Utilities;

public class Schedule {
    private Map<Integer, Installment> installments;
    private Date disbursementDate;
    private Double dailyInterestRate;
    private BigDecimal loanAmount;

    public Schedule(Date disbursementDate, Double dailyInterestRate, BigDecimal loanAmount, List<Installment> installments) {
        this.disbursementDate = disbursementDate;
        this.dailyInterestRate = dailyInterestRate;
        this.loanAmount = loanAmount;
        this.installments = new TreeMap<Integer, Installment>();
        setInstallments(installments);
    }

    private void setInstallments(List<Installment> installments) {
        for (Installment installment : installments) {
            this.installments.put(installment.getId(), installment);
        }
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public Double getDailyInterestRate() {
        return dailyInterestRate;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public BigDecimal payDueInstallments(Date transactionDate, BigDecimal amount) {
        for (Installment dueInstallment : getInstallmentsOnOrBefore(transactionDate)) {
            amount = dueInstallment.pay(amount, transactionDate);
        }
        return amount;
    }

    public void adjustFutureInstallments(BigDecimal balance, Date transactionDate) {
        List<Installment> futureInstallments = getInstallmentsAfter(transactionDate);
        BigDecimal principalOutstanding = adjustPrincipalsForInstallments(balance, transactionDate, futureInstallments);
        adjustInterestForInstallments(futureInstallments, principalOutstanding);
    }

    private void adjustInterestForInstallments(List<Installment> futureInstallments, BigDecimal principalOutstanding) {
        for (Installment installment : futureInstallments) {
            if (installment.getCurrentPayment().isPrincipalPayment()) {
                long duration = getDurationForAdjustment(installment, installment.getDueDate());
                if (duration <= 0) continue;
                BigDecimal principalForInterest = computePrincipalForInterest(principalOutstanding, installment);
                installment.setEffectiveInterest(computeInterest(principalForInterest, duration));
            }
        }
    }

    private long getDurationForAdjustment(Installment installment, Date toDate) {
        Installment previousInstallment = getPreviousInstallment(installment);
        Date prevDueDate = previousInstallment != null ? previousInstallment.getDueDate() : this.disbursementDate;
        prevDueDate = max(prevDueDate, installment.getRecentPrincipalPaidDate());
        return getDaysInBetween(toDate, prevDueDate);
    }

    private BigDecimal getPrincipalDueTill(Installment installment) {
        BigDecimal principalDue = BigDecimal.ZERO;
        for (Installment _installment : installments.values()) {
            if (_installment.compareTo(installment) >= 0) break;
            principalDue = principalDue.add(_installment.getPrincipalDue());
        }
        return principalDue;
    }

    private BigDecimal adjustPrincipalsForInstallments(BigDecimal balance, Date transactionDate, List<Installment> futureInstallments) {
        BigDecimal principalOutstanding = this.loanAmount.subtract(getPrincipalPaid());
        for (Installment installment : futureInstallments) {
            balance = installment.payExtraInterest(balance, transactionDate);
            balance = installment.payInterestDueTillDate(balance, transactionDate,
                    computeInterestTillDueDate(transactionDate, principalOutstanding, installment));
            BigDecimal earlierBalance = balance;
            balance = installment.payPrincipal(balance, transactionDate);
            if (earlierBalance.compareTo(balance) > 0) {
                principalOutstanding = principalOutstanding.subtract(earlierBalance.subtract(balance));
            }
            installment.recordCurrentPayment();
        }
        return principalOutstanding;
    }

    private BigDecimal computeInterestTillDueDate(Date transactionDate, BigDecimal principalOutstanding, Installment installment) {
        long duration = getDurationForAdjustment(installment, transactionDate);
        if (duration <= 0) return BigDecimal.ZERO;
        BigDecimal principalForInterest = computePrincipalForInterest(principalOutstanding, installment);
        return computeAndAdjustInterest(installment, duration, principalForInterest);
    }

    private BigDecimal computePrincipalForInterest(BigDecimal principalOutstanding, Installment installment) {
        return principalOutstanding.subtract(getPrincipalDueTill(installment));
    }

    private List<Installment> getInstallmentsOnOrBefore(Date date) {
        List<Installment> result = new ArrayList<Installment>();
        for (Installment installment : installments.values()) {
            Date dueDate = installment.getDueDate();
            // TODO Refine this date comparison logic
            if (dueDate.compareTo(date) <= 0) result.add(installment);
        }
        return result;
    }

    private List<Installment> getInstallmentsBefore(Date date) {
        List<Installment> result = new ArrayList<Installment>();
        for (Installment installment : installments.values()) {
            Date dueDate = installment.getDueDate();
            // TODO Refine this date comparison logic
            if (dueDate.compareTo(date) < 0) result.add(installment);
        }
        return result;
    }

    private List<Installment> getInstallmentsAfter(Date date) {
        List<Installment> result = new ArrayList<Installment>();
        for (Installment installment : installments.values()) {
            Date dueDate = installment.getDueDate();
            // TODO Refine this date comparison logic
            if (dueDate.compareTo(date) > 0) result.add(installment);
        }
        return result;
    }

    private List<Installment> getInstallmentsOnOrAfter(Date date) {
        List<Installment> result = new ArrayList<Installment>();
        for (Installment installment : installments.values()) {
            Date dueDate = installment.getDueDate();
            // TODO Refine this date comparison logic
            if (dueDate.compareTo(date) >= 0) result.add(installment);
        }
        return result;
    }

    private BigDecimal computeAndAdjustInterest(Installment installment, long duration, BigDecimal principalForInterest) {
        if (duration <= 0) return BigDecimal.ZERO;
        BigDecimal computedInterest = computeInterest(principalForInterest, duration);
        BigDecimal interestPaid = installment.getInterestPaid();
        return computedInterest.compareTo(interestPaid) > 0 ? computedInterest.subtract(interestPaid) : computedInterest;
    }

    private BigDecimal computeInterest(BigDecimal principalOutstanding, long duration) {
        BigDecimal computedInterest = principalOutstanding.multiply(BigDecimal.valueOf(duration)).multiply(BigDecimal.valueOf(this.dailyInterestRate));
        return Utilities.round(computedInterest);
    }

    private BigDecimal getPrincipalPaid() {
        BigDecimal principalPaid = BigDecimal.ZERO;
        for (Installment installment : installments.values()) {
            principalPaid = principalPaid.add(installment.getPrincipalPaid());
        }
        return principalPaid;
    }

    private Installment getPreviousInstallment(Installment installment) {
        return installments.get(installment.getId() - 1);
    }

    private Installment getNextInstallment(Installment installment) {
        return installments.get(installment.getId() + 1);
    }

    public void computeExtraInterest(Date transactionDate) {
        for (Installment installment : installments.values()) {
            Installment nextInstallment = getNextInstallment(installment);
            if (installment.isPrincipalDue()) {
                BigDecimal principalDue = installment.getPrincipalDue();
                long duration = getDaysInBetween(transactionDate, installment.fromDateForOverdueComputation());
                if (installment.isAnyPrincipalPaid()) {
                    updateExtraInterest(installment, nextInstallment, principalDue, duration);
                } else {
                    setExtraInterest(installment, nextInstallment, principalDue, duration);
                }
            }
        }
    }

    private void setExtraInterest(Installment installment, Installment nextInstallment, BigDecimal principalDue, long duration) {
        if (duration <= 0) return;
        if (nextInstallment != null) {
            nextInstallment.setExtraInterest(computeInterest(principalDue, duration));
        }
        else {
            installment.addExtraInterest(computeInterest(principalDue, duration));
        }
    }

    private void updateExtraInterest(Installment installment, Installment nextInstallment, BigDecimal principalDue, long duration) {
        if (duration <= 0) return;
        if (nextInstallment != null) {
            nextInstallment.addExtraInterest(computeInterest(principalDue, duration));
        }
        else {
            installment.addExtraInterest(computeInterest(principalDue, duration));
        }
    }

    public Map<Integer, Installment> getInstallments() {
        return installments;
    }

    public void resetCurrentPayment() {
        for (Installment installment : installments.values()) {
            installment.resetCurrentPayment();
        }
    }

    public BigDecimal computePayableAmountForDueInstallments(Date asOfDate) {
        BigDecimal repaymentAmount = BigDecimal.ZERO;
        for (Installment installment : getInstallmentsBefore(asOfDate)) {
            repaymentAmount = repaymentAmount.add(installment.getTotalDue());
        }
        return repaymentAmount;
    }

    public RepaymentResultsHolder computePayableAmountForFutureInstallments(Date asOfDate) {
        List<Installment> futureInstallments = getInstallmentsOnOrAfter(asOfDate);
        RepaymentResultsHolder repaymentResultsHolder = new RepaymentResultsHolder();
        BigDecimal payableAmount = BigDecimal.ZERO;
        repaymentResultsHolder.setWaiverAmount(BigDecimal.ZERO);
        if(!futureInstallments.isEmpty()){
            BigDecimal outstandingPrincipal = getOutstandingPrincipal(asOfDate);
            Installment firstFutureInstallment = futureInstallments.get(0);
            BigDecimal interestDue = computeAndAdjustInterest(firstFutureInstallment,
                    getDurationForAdjustment(firstFutureInstallment, asOfDate), outstandingPrincipal);
            repaymentResultsHolder.setWaiverAmount(interestDue);
            payableAmount = payableAmount.add(interestDue).add(firstFutureInstallment.getExtraInterestDue())
                    .add(firstFutureInstallment.getFeesDue()).add(firstFutureInstallment.getMiscFeesDue())
                    .add(firstFutureInstallment.getPenaltyDue()).add(firstFutureInstallment.getMiscPenaltyDue());
            for (Installment futureInstallment : futureInstallments) {
                payableAmount = payableAmount.add(futureInstallment.getPrincipalDue());
            }
        }
        repaymentResultsHolder.setTotalRepaymentAmount(payableAmount);
        return repaymentResultsHolder;
    }

    private BigDecimal getOutstandingPrincipal(Date asOfDate) {
        BigDecimal outstandingPrincipal = BigDecimal.ZERO;
        for (Installment pastInstallment : getInstallmentsBefore(asOfDate)) {
            outstandingPrincipal = outstandingPrincipal.add(pastInstallment.getPrincipal());
        }
        return loanAmount.subtract(outstandingPrincipal);
    }
}
