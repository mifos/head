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
package org.mifos.application.collectionsheet.persistence;

import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerMeetingEntity;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.util.helpers.Constants;

/**
 *
 */
public class ClientBuilder {
    
    private final CustomerLevel customerLevel = CustomerLevel.CLIENT;
    private String name = "Test Center";
    private MeetingBO meeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
    private OfficeBO office;
    private CustomerAccountBO customerAccount;
    private PersonnelBO loanOfficer;
    private final String searchId = "1.1.1.1";
    private final Short updatedFlag = Constants.NO;
    
    // persistence classes for mocking out in unit tests
    private CustomerPersistence customerPersistence;
    
    public ClientBO build() {
        
        final CustomerMeetingEntity customerMeeting = new CustomerMeetingEntity(meeting, updatedFlag);
        final ClientBO client = new ClientBO(customerLevel, name, office, loanOfficer, customerMeeting,
                customerAccount,
                searchId);
        client.setCustomerPersistence(customerPersistence);
        
        return client;
    }
    
    public ClientBuilder withName(final String withName) {
        this.name = withName;
        return this;
    }

    public ClientBuilder withMeeting(final MeetingBO withMeeting) {
        this.meeting = withMeeting;
        return this;
    }

    public ClientBuilder withOffice(final OfficeBO withOffice) {
        this.office = withOffice;
        return this;
    }

    public ClientBuilder withLoanOfficer(final PersonnelBO withLoanOfficer) {
        this.loanOfficer = withLoanOfficer;
        return this;
    }

    public ClientBuilder withCustomerPersistence(final CustomerPersistence customerPersistence) {
        this.customerPersistence = customerPersistence;
        return this;
    }
}
