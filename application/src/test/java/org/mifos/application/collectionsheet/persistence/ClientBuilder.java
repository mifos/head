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

import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.util.helpers.Constants;

/**
 *
 */
public class ClientBuilder {
    
    private final CustomerAccountBuilder customerAccountBuilder = new CustomerAccountBuilder();
    private final CustomerLevel customerLevel = CustomerLevel.CLIENT;
    private String name = "Test Center";
    private MeetingBO meeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
    private OfficeBO office = new OfficeBuilder().withGlobalOfficeNum("xxxx-112").build();
    private PersonnelBO loanOfficer;
    private String searchId = null;
    private final Short updatedFlag = Constants.NO;
    private CustomerStatus customerStatus = CustomerStatus.CLIENT_ACTIVE;
    private CustomerBO parentCustomer; 
    
    
    public ClientBO buildForIntegrationTests() {
        
        final CustomerMeetingEntity customerMeeting = new CustomerMeetingEntity(meeting, updatedFlag);
        if (searchId == null) {
            setSearchId();
        }
        
        final ClientBO client = new ClientBO(customerLevel, customerStatus, name, office, loanOfficer, customerMeeting,
                searchId, parentCustomer); 
        CustomerAccountBO customerAccount = customerAccountBuilder.withCustomer(client).withOffice(office).withLoanOfficer(loanOfficer)
                .buildForIntegrationTests();
        
        client.setCustomerAccount(customerAccount);
        
        return client;
    }
    
    public ClientBO buildForUnitTests() {

        final CustomerMeetingEntity customerMeeting = new CustomerMeetingEntity(meeting, updatedFlag);
        final ClientBO client = new ClientBO(customerLevel, customerStatus, name, office, loanOfficer, customerMeeting,
                searchId, parentCustomer);

        customerAccountBuilder.withCustomer(client).withOffice(office).withLoanOfficer(loanOfficer).buildForUnitTests();
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
    
    public ClientBuilder withFee(final AmountFeeBO withFee) {
        customerAccountBuilder.withFee(withFee);
        return this;
    }
    
    public ClientBuilder withParentCustomer(final CustomerBO withParentCustomer) {
        this.parentCustomer = withParentCustomer;
        return this;
    }
    
    public ClientBuilder active() {
        this.customerStatus = CustomerStatus.CLIENT_ACTIVE;
        return this;
    }

    public ClientBuilder inActive() {
        this.customerStatus = CustomerStatus.CENTER_INACTIVE;
        return this;
    }

    public ClientBuilder withSearchId(String withSearchId) {
        this.searchId = withSearchId;
        return this;
    }

    private void setSearchId() {

        Integer childCount = 1;
        if (parentCustomer.getChildren() != null) {
            childCount = parentCustomer.getChildren().size() + 1;
        }

        this.searchId = parentCustomer.getSearchId() + "." + childCount;
    }
}
