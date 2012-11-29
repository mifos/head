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

package org.mifos.accounts.savings.business;

import java.sql.Date;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.util.helpers.Money;

public class SavingsScheduleEntity extends AccountActionDateEntity {
    private Money deposit;

    private Money depositPaid;

    private int versionNo;

    protected SavingsScheduleEntity() {
        super(null, null, null, null, null);
    }

    // only used for active savings account
    public SavingsScheduleEntity(CustomerBO customer, Short installmentId, Date actionDate, PaymentStatus paymentStatus, Money deposit, MifosCurrency mifosCurrency) {
        super(null, customer, installmentId, actionDate, paymentStatus);
        this.deposit = deposit;
        this.depositPaid = new Money(mifosCurrency);
    }

    public SavingsScheduleEntity(AccountBO account, CustomerBO customer, Short installmentId, Date actionDate,
            PaymentStatus paymentStatus, Money deposit) {
        super(account, customer, installmentId, actionDate, paymentStatus);
        this.deposit = deposit;
        if (account.getAccountState().getId().equals(AccountState.SAVINGS_INACTIVE.getValue())) {
            this.deposit = new Money(account.getCurrency());
        }
        this.depositPaid = new Money(account.getCurrency());
    }

    public Money getDeposit() {
        return deposit;
    }

    public void setDeposit(Money deposit) {
        this.deposit = deposit;
    }

    public Money getDepositPaid() {
        return depositPaid;
    }

    public void setDepositPaid(Money depositPaid) {
        this.depositPaid = depositPaid;
    }

    public Money getTotalDepositDue() {
        return getDeposit().subtract(getDepositPaid());
    }

    void setPaymentDetails(Money depositAmount, PaymentStatus paymentStatus, Date paymentDate) {
        this.depositPaid = this.depositPaid.add(depositAmount);
        this.paymentStatus = paymentStatus.getValue();
        this.paymentDate = paymentDate;
    }

    void waiveDepositDue() {
        Money depositDue = getTotalDepositDue();
        deposit = deposit.subtract(depositDue);
        setPaymentStatus(PaymentStatus.PAID);
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public int getVersionNo() {
        return versionNo;
    }

}
