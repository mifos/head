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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.framework.util.helpers.Money;

public class LoanPaymentData extends AccountPaymentData {

    private final Money principalPaid;

    private final Money interestPaid;

    private final Money penaltyPaid;

    private final Money miscFeePaid;

    private final Money miscPenaltyPaid;

    private final Map<Short, Money> feesPaid;

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

    public LoanPaymentData(AccountActionDateEntity accountActionDate) {
        super(accountActionDate);
        LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
        principalPaid = loanScheduleEntity.getPrincipalDue();
        interestPaid = loanScheduleEntity.getInterestDue();
        penaltyPaid = loanScheduleEntity.getPenalty().subtract(loanScheduleEntity.getPenaltyPaid());
        miscFeePaid = loanScheduleEntity.getMiscFeeDue();
        miscPenaltyPaid = loanScheduleEntity.getMiscPenaltyDue();
        Map<Short, Money> feesPaid = new HashMap<Short, Money>();
        Set<AccountFeesActionDetailEntity> accountFeesActionDetails = loanScheduleEntity.getAccountFeesActionDetails();
        if (accountFeesActionDetails != null && accountFeesActionDetails.size() > 0) {
            for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {
                if (accountFeesActionDetailEntity.getFeeAmount() != null
                        && accountFeesActionDetailEntity.getFeeAmount().isNonZero()) {
                    feesPaid.put(accountFeesActionDetailEntity.getFee().getFeeId(), accountFeesActionDetailEntity
                            .getFeeDue());
                }
            }
        }
        this.feesPaid = feesPaid;
        setPaymentStatus(PaymentStatus.PAID.getValue());
    }

    public LoanPaymentData(AccountActionDateEntity accountActionDate, Money totalPayment) {
        super(accountActionDate);
        LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
        if (totalPayment.isGreaterThanOrEqual(loanScheduleEntity.getTotalDueWithFees())) {
            setPaymentStatus(PaymentStatus.PAID.getValue());
        } else {
            setPaymentStatus(PaymentStatus.UNPAID.getValue());
        }
        miscPenaltyPaid = getLowest(totalPayment, loanScheduleEntity.getMiscPenaltyDue());
        Money total = totalPayment.subtract(getMiscPenaltyPaid());

        penaltyPaid = getLowest(total, (loanScheduleEntity.getPenalty().subtract(loanScheduleEntity.getPenaltyPaid())));
        total = total.subtract(getPenaltyPaid());

        miscFeePaid = getLowest(total, loanScheduleEntity.getMiscFeeDue());
        total = total.subtract(getMiscFeePaid());

        Map<Short, Money> feesPaid = new HashMap<Short, Money>();
        Set<AccountFeesActionDetailEntity> accountFeesActionDetails = loanScheduleEntity.getAccountFeesActionDetails();
        if (accountFeesActionDetails != null && accountFeesActionDetails.size() > 0) {
            for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {
                Money feeAmount = getLowest(total, accountFeesActionDetailEntity.getFeeDue());
                feesPaid.put(accountFeesActionDetailEntity.getFee().getFeeId(), feeAmount);
                total = total.subtract(feeAmount);
            }
        }
        this.feesPaid = feesPaid;

        interestPaid = getLowest(total, loanScheduleEntity.getInterestDue());
        total = total.subtract(getInterestPaid());

        principalPaid = getLowest(total, loanScheduleEntity.getPrincipalDue());
    }

    public Money getAmountPaidWithFeeForInstallment() {
        return getAmountPaidWithoutFeeForInstallment().add(getFeeAmountPaidForInstallment());
    }

    public Money getAmountPaidWithoutFeeForInstallment() {
        return getInterestPaid().add(getPenaltyPaid()).add(getPrincipalPaid()).add(getMiscFeePaid())
                .add(getMiscPenaltyPaid());
    }

    public Money getFeeAmountPaidForInstallment() {
        Money totalFeePaid = new Money(getPrincipalPaid().getCurrency());
        if (!getFeesPaid().isEmpty()) {
            for (Short feeId : getFeesPaid().keySet()) {
                totalFeePaid = totalFeePaid.add(getFeesPaid().get(feeId));
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
}
