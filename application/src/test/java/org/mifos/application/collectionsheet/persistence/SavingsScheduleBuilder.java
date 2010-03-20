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
package org.mifos.application.collectionsheet.persistence;

import java.util.Date;

import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class SavingsScheduleBuilder {

    private AccountBO account;
    private CustomerBO customer;
    private Short installmentNumber = Short.valueOf("1");
    private final Date actionDate = new DateTime().minusDays(1).toDate();
    private final PaymentStatus paymentStatus = PaymentStatus.UNPAID;
    private Money depositDue = new Money(Money.getDefaultCurrency(),"25.0");

    public SavingsScheduleEntity build() {

        final SavingsScheduleEntity savingsScheduleEntity = new SavingsScheduleEntity(account, customer,
                installmentNumber, new java.sql.Date(actionDate.getTime()), paymentStatus, depositDue);

        return savingsScheduleEntity;
    }

    public SavingsScheduleBuilder withCustomer(final CustomerBO withCustomer) {
        this.customer = withCustomer;
        return this;
    }

    public SavingsScheduleBuilder withAccount(final SavingsBO withSavingsAccount) {
        this.account = withSavingsAccount;
        return this;
    }

    public SavingsScheduleBuilder withInstallmentNumber(final Integer withInstallmentNumber) {
        this.installmentNumber = withInstallmentNumber.shortValue();
        return this;
    }

    public SavingsScheduleBuilder withDepositDue(final Money withDepositDue) {
        this.depositDue = withDepositDue;
        return this;
    }
}
