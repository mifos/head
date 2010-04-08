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

package org.mifos.accounts.acceptedpaymenttype.business;

import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.framework.business.AbstractEntity;

public class AcceptedPaymentType extends AbstractEntity {

    private Short acceptedPaymentTypeId;
    private TransactionTypeEntity transactionTypeEntity;
    private PaymentTypeEntity paymentTypeEntity;

    public Short getAcceptedPaymentTypeId() {
        return acceptedPaymentTypeId;
    }

    public void setAcceptedPaymentTypeId(Short acceptedPaymentTypeId) {
        this.acceptedPaymentTypeId = acceptedPaymentTypeId;
    }

    public PaymentTypeEntity getPaymentTypeEntity() {
        return paymentTypeEntity;
    }

    public void setPaymentTypeEntity(PaymentTypeEntity paymentTypeEntity) {
        this.paymentTypeEntity = paymentTypeEntity;
    }

    public TransactionTypeEntity getTransactionTypeEntity() {
        return transactionTypeEntity;
    }

    public void setTransactionTypeEntity(TransactionTypeEntity transactionTypeEntity) {
        this.transactionTypeEntity = transactionTypeEntity;
    }

}
