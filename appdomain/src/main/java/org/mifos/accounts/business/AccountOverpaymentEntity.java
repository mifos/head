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

package org.mifos.accounts.business;

import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;

public class AccountOverpaymentEntity extends AbstractEntity {
    private final Integer overpaymentId = null;

    private final AccountBO account;

    private final AccountPaymentEntity payment;

    private Money originalOverpaymentAmount;

    private Money actualOverpaymentAmount;

    protected Short overpaymentStatus;

    public AccountOverpaymentEntity(AccountBO account, AccountPaymentEntity paymentType, Money originalOverpaymentAmount, Short overPaymentStatus) {
        this.account = account;
        this.payment = paymentType;
        this.originalOverpaymentAmount = originalOverpaymentAmount;
        this.actualOverpaymentAmount = originalOverpaymentAmount;
        this.overpaymentStatus = overPaymentStatus;
    }

    public Integer getOverpaymentId() {
        return overpaymentId;
    }

    public AccountBO getAccount() {
        return account;
    }

    public AccountPaymentEntity getPayment() {
        return payment;
    }

    public Money getOriginalOverpaymentAmount() {
        return originalOverpaymentAmount;
    }

    public void setOriginalOverpaymentAmount(Money originalOverpaymentAmount) {
        this.originalOverpaymentAmount = originalOverpaymentAmount;
    }

    public Money getActualOverpaymentAmount() {
        return actualOverpaymentAmount;
    }

    public void setActualOverpaymentAmount(Money actualOverpaymentAmount) {
        this.actualOverpaymentAmount = actualOverpaymentAmount;
    }

    public Short getOverpaymentStatus() {
        return overpaymentStatus;
    }

    public void setOverpaymentStatus(Short overpaymentStatus) {
        this.overpaymentStatus = overpaymentStatus;
    }
}
