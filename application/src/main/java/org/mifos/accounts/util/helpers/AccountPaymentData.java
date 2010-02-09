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

package org.mifos.accounts.util.helpers;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;

public abstract class AccountPaymentData {

    private Short installmentId;

    private Short paymentStatus;

    private AccountActionDateEntity accountActionDateEntity = null;

    public Short getInstallmentId() {
        return installmentId;
    }

    protected void setInstallmentId(Short installmentId) {
        this.installmentId = installmentId;
    }

    public void setAccountActionDate(AccountActionDateEntity accountActionDateEntity) {
        this.accountActionDateEntity = accountActionDateEntity;
    }

    public AccountActionDateEntity getAccountActionDate() {
        return accountActionDateEntity;
    }

    public Short getPaymentStatus() {
        return paymentStatus;
    }

    protected void setPaymentStatus(Short paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public AccountPaymentData(AccountActionDateEntity accountActionDate) {
        if (accountActionDate != null)
            setInstallmentId(accountActionDate.getInstallmentId());
        this.accountActionDateEntity = accountActionDate;
    }

    public AccountPaymentData(CollectionSheetEntryInstallmentView bulkEntryAccountAction) {
        if (bulkEntryAccountAction != null)
            setInstallmentId(bulkEntryAccountAction.getInstallmentId());
        setPaymentStatus(PaymentStatus.PAID.getValue());
    }

    public boolean isPaid() {
        return paymentStatus.shortValue() == PaymentStatus.PAID.getValue().shortValue();
    }
}
