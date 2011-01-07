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

package org.mifos.customers.business.service;

import java.util.List;

import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.calendar.CalendarEvent;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;

/**
 * Default implementation of {@link CustomerAccountFactory} that uses static factory methods on {@link CustomerAccountBO} for creation.
 */
public class DefaultCustomerAccountFactory implements CustomerAccountFactory {

    public static CustomerAccountFactory createNew() {
        return new DefaultCustomerAccountFactory();
    }

    @Override
    public CustomerAccountBO create(CustomerBO customer, List<AccountFeesEntity> accountFees, MeetingBO meeting,
            CalendarEvent upcomingCalendarEvents) {
        return CustomerAccountBO.createNew(customer, accountFees, meeting, upcomingCalendarEvents);
    }
}
