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

import java.util.Date;

import org.joda.time.DateTime;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class AccountPaymentEntityBuilder {

    private AccountBO account;
    private Money amount = TestUtils.createMoney("2");
    private String receiptNumber = "xxx-123456";
    private Date receiptDate = new DateTime().toDate();
    private PaymentTypeEntity paymentType;
    private Date paymentDate = new DateTime().toDate();

    public AccountPaymentEntity build() {
        return new AccountPaymentEntity(account, amount, receiptNumber, receiptDate, paymentType, paymentDate);
    }

    public AccountPaymentEntityBuilder with(AccountBO account) {
        this.account = account;
        return this;
    }

    public AccountPaymentEntityBuilder with(Money withPayment) {
        this.amount = withPayment;
        return this;
    }
}