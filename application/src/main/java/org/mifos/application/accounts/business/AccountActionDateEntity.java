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

package org.mifos.application.accounts.business;

import java.sql.Date;

import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.business.PersistentObject;

public abstract class AccountActionDateEntity extends PersistentObject implements Comparable<AccountActionDateEntity> {

    protected final Integer actionDateId;

    protected final AccountBO account;

    protected final CustomerBO customer;

    protected final Short installmentId;

    protected Date actionDate;

    protected Short paymentStatus;

    protected Date paymentDate;

    protected AccountActionDateEntity(final AccountBO account, final CustomerBO customer, final Short installmentId, final Date actionDate,
            final PaymentStatus paymentStatus) {
        this.actionDateId = null;
        this.account = account;
        this.customer = customer;
        this.installmentId = installmentId;
        this.actionDate = actionDate;
        if (paymentStatus != null) {
            this.paymentStatus = paymentStatus.getValue();
        }
    }

    public Integer getActionDateId() {
        return actionDateId;
    }

    public AccountBO getAccount() {
        return account;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public Short getInstallmentId() {
        return installmentId;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(final Date actionDate) {
        this.actionDate = actionDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(final Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Most callers will want to call {@link #isPaid()} instead.
     */
    Short getPaymentStatus() {
        return paymentStatus;
    }

    private PaymentStatus getPaymentStatusAsEnum() {
        return PaymentStatus.fromInt(paymentStatus);
    }

    void setPaymentStatus(final Short paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentStatus(final PaymentStatus status) {
        this.paymentStatus = status.getValue();
    }

    public int compareDate(final java.util.Date date) {
        return getActionDate().compareTo(date);
    }
    
    public boolean isPaid() {
        return getPaymentStatusAsEnum() == PaymentStatus.PAID;
    }

    public int compareTo(final AccountActionDateEntity obj) {
        return this.getInstallmentId().compareTo(obj.getInstallmentId());
    }

}
