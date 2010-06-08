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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.calendar.CalendarEvent;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.domain.builders.CalendarEventBuilder;
import org.mifos.framework.TestUtils;

/**
 *
 */
public class CustomerAccountBuilder {

    private CustomerAccountBO customerAccount;
    private CustomerBO customer;
    private List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
    private final Set<AmountFeeBO> fees = new HashSet<AmountFeeBO>();

    private CalendarEvent applicableCalendarEvents = new CalendarEventBuilder().build();

    public CustomerAccountBO buildForUnitTests() {

        if (customer.getUserContext() == null) {
            customer.setUserContext(TestUtils.makeUser());
        }

        customerAccount = CustomerAccountBO.createNew(customer, accountFees, customer.getCustomerMeetingValue(), applicableCalendarEvents);

        return customerAccount;
    }

    public CustomerAccountBuilder withCustomer(final CustomerBO withCustomer) {
        this.customer = withCustomer;
        return this;
    }

    public CustomerAccountBuilder withFee(final AmountFeeBO withFee) {
        fees.add(withFee);
        return this;
    }
}