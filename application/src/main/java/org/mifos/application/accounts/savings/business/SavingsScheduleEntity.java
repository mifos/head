/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.accounts.savings.business;

import java.sql.Date;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.util.helpers.Money;

public class SavingsScheduleEntity extends AccountActionDateEntity {
    protected Money deposit;

    protected Money depositPaid;

    protected SavingsScheduleEntity() {
        super(null, null, null, null, null);
    }

    public SavingsScheduleEntity(AccountBO account, CustomerBO customer, Short installmentId, Date actionDate,
            PaymentStatus paymentStatus, Money deposit) {
        super(account, customer, installmentId, actionDate, paymentStatus);
        this.deposit = deposit;
        this.depositPaid = new Money();
    }

    public Money getDeposit() {
        return deposit;
    }

    void setDeposit(Money deposit) {
        this.deposit = deposit;
    }

    public Money getDepositPaid() {
        return depositPaid;
    }

    void setDepositPaid(Money depositPaid) {
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

}
