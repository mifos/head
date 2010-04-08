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

package org.mifos.accounts.business;

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;

public class AccountFeesActionDetailEntity extends AbstractEntity {

    private final Integer accountFeesActionDetailId;

    private final AccountActionDateEntity accountActionDate;

    private final Short installmentId;

    private final FeeBO fee;

    private final AccountFeesEntity accountFee;

    private Money feeAmount;

    private Money feeAmountPaid;

    protected AccountFeesActionDetailEntity(AccountActionDateEntity accountActionDate, FeeBO fee,
            AccountFeesEntity accountFee, Money feeAmount) {
        this.accountFeesActionDetailId = null;
        this.accountActionDate = accountActionDate;
        if (accountActionDate != null) {
            this.installmentId = accountActionDate.getInstallmentId();
            this.feeAmountPaid = new Money(accountActionDate.getAccount().getCurrency());
        }
        else {
            this.installmentId = null;
        }
        this.fee = fee;
        this.accountFee = accountFee;
        this.feeAmount = feeAmount;
    }

    public AccountActionDateEntity getAccountActionDate() {
        return accountActionDate;
    }

    public AccountFeesEntity getAccountFee() {
        return accountFee;
    }

    public Integer getAccountFeesActionDetailId() {
        return accountFeesActionDetailId;
    }

    public FeeBO getFee() {
        return fee;
    }

    public Money getFeeAmount() {
        return feeAmount;
    }

    protected void setFeeAmount(Money feeAmount) {
        this.feeAmount = feeAmount;
    }

    public Money getFeeAmountPaid() {
        return feeAmountPaid;
    }

    protected void setFeeAmountPaid(Money feeAmountPaid) {
        this.feeAmountPaid = feeAmountPaid;
    }

    public Short getInstallmentId() {
        return installmentId;
    }

    protected void makePayment(Money feePaid) {
        if (getFeeAmountPaid() == null) {
            setFeeAmountPaid(new Money(accountFee.getAccount().getCurrency()));
        }
        this.feeAmountPaid = getFeeAmountPaid().add(feePaid);
    }

    public Money getFeeDue() {

        return getFeeAmount().subtract(getFeeAmountPaid());
    }

    protected void makeRepaymentEnteries(String payFullOrPartial) {
        if (payFullOrPartial.equals(LoanConstants.PAY_FEES_PENALTY_INTEREST)) {
            setFeeAmountPaid(getFeeAmountPaid().add(getFeeDue()));
        } else {
            setFeeAmount(getFeeAmountPaid());
        }
    }

    protected Money waiveCharges() {
        Money chargeWaived = new Money(accountFee.getAccount().getCurrency());
        chargeWaived = chargeWaived.add(getFeeDue());
        setFeeAmount(getFeeAmountPaid());
        return chargeWaived;
    }

    /**
     * Since the amount of a rate-based fee carries more precision than the
     * prevailing currency can handle, round the exact amount when determining
     * the actual fee that must be paid.
     */
    public void roundFeeAmount(Money roundedAmount) {
        setFeeAmount(roundedAmount);
    }

    /**
     * Adjust for rounding fees paid in prior installments, so that the total
     * fees paid adds up properly
     */
    public void adjustFeeAmount(Money difference) {
        setFeeAmount(getFeeAmount().add(difference));
    }
}
