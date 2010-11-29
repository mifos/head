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

package org.mifos.accounts.util.helpers;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.LoanSummaryEntity;
import org.mifos.framework.util.helpers.Money;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoanPaymentData extends AccountPaymentData {

    private Money principalPaid;

    private Money interestPaid;

    private Money penaltyPaid;

    private Money miscFeePaid;

    private Money miscPenaltyPaid;

    private Map<Short, Money> feesPaid;

    public Map<Short, Money> getFeesPaid() {
        return feesPaid;
    }

    public Money getInterestPaid() {
        return interestPaid;
    }

    public Money getPenaltyPaid() {
        return penaltyPaid;
    }

    public Money getPrincipalPaid() {
        return principalPaid;
    }

    public Money getMiscFeePaid() {
        return miscFeePaid;
    }

    public Money getMiscPenaltyPaid() {
        return miscPenaltyPaid;
    }

    public LoanPaymentData(AccountActionDateEntity accountActionDate, final Money paymentAmount) {
        super(accountActionDate);
        LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
        setPaymentStatus(paymentAmount, loanScheduleEntity.getTotalDueWithFees());
        pay(paymentAmount, loanScheduleEntity);
    }

    private Money pay(final Money paymentAmount, LoanScheduleEntity loanScheduleEntity) {
        Money balanceAmount = paymentAmount;
        balanceAmount = payMiscPenalty(loanScheduleEntity, balanceAmount);
        balanceAmount = payPenalty(loanScheduleEntity, balanceAmount);
        balanceAmount = payMiscFees(loanScheduleEntity, balanceAmount);
        balanceAmount = payFees(loanScheduleEntity, balanceAmount);
        balanceAmount = payInterest(loanScheduleEntity, balanceAmount);
        balanceAmount = payPrincipal(loanScheduleEntity, balanceAmount);
        return balanceAmount;
    }

    private void setPaymentStatus(Money paymentAmount, Money totalDues) {
        if (paymentAmount.isGreaterThanOrEqual(totalDues)) {
            setPaymentStatus(PaymentStatus.PAID.getValue());
        } else {
            setPaymentStatus(PaymentStatus.UNPAID.getValue());
        }
    }

    private Money payPrincipal(LoanScheduleEntity loanScheduleEntity, final Money amount) {
        principalPaid = getLowest(amount, loanScheduleEntity.getPrincipalDue());
        return amount.subtract(principalPaid);
    }

    private Money payInterest(LoanScheduleEntity loanScheduleEntity, final Money amount) {
        interestPaid = getLowest(amount, loanScheduleEntity.getInterestDue());
        return amount.subtract(interestPaid);
    }

    private Money payFees(LoanScheduleEntity loanScheduleEntity, final Money amount) {
        this.feesPaid = new HashMap<Short, Money>();
        Set<AccountFeesActionDetailEntity> accountFeesActionDetails = loanScheduleEntity.getAccountFeesActionDetails();
        Money balance = amount;
        if (accountFeesActionDetails != null && accountFeesActionDetails.size() > 0) {
            for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {
                Money feeAmount = getLowest(amount, accountFeesActionDetailEntity.getFeeDue());
                feesPaid.put(accountFeesActionDetailEntity.getFee().getFeeId(), feeAmount);
                balance = balance.subtract(feeAmount);
            }
        }
        return balance;
    }

    private Money payMiscFees(LoanScheduleEntity loanScheduleEntity, final Money amount) {
        miscFeePaid = getLowest(amount, loanScheduleEntity.getMiscFeeDue());
        return amount.subtract(miscFeePaid);
    }

    private Money payPenalty(LoanScheduleEntity loanScheduleEntity, final Money amount) {
        penaltyPaid = getLowest(amount, (loanScheduleEntity.getPenalty().subtract(loanScheduleEntity.getPenaltyPaid())));
        return amount.subtract(penaltyPaid);
    }

    private Money payMiscPenalty(LoanScheduleEntity loanScheduleEntity, final Money totalPayment) {
        miscPenaltyPaid = getLowest(totalPayment, loanScheduleEntity.getMiscPenaltyDue());
        return totalPayment.subtract(miscPenaltyPaid);
    }

    public Money getAmountPaidWithFeeForInstallment() {
        return getAmountPaidWithoutFeeForInstallment().add(getFeeAmountPaidForInstallment());
    }

    public Money getAmountPaidWithoutFeeForInstallment() {
        return interestPaid.add(penaltyPaid).add(principalPaid).add(miscFeePaid)
                .add(miscPenaltyPaid);
    }

    public Money getFeeAmountPaidForInstallment() {
        Money totalFeePaid = new Money(principalPaid.getCurrency());
        if (!feesPaid.isEmpty()) {
            for (Short feeId : feesPaid.keySet()) {
                totalFeePaid = totalFeePaid.add(feesPaid.get(feeId));
            }
        }
        return totalFeePaid;
    }

    private Money getLowest(Money money1, Money money2) {
        if (money1.isGreaterThan(money2)) {
            return money2;
        }
        return money1;
    }

    public void updateLoanSummary(LoanSummaryEntity loanSummary) {
        loanSummary.updatePaymentDetails(principalPaid, interestPaid, penaltyPaid.add(miscPenaltyPaid),
                getFeeAmountPaidForInstallment().add(miscFeePaid));
    }
}
