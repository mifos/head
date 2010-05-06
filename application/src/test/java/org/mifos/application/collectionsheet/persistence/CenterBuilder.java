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
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.business.util.Address;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class CenterBuilder {

    private CenterBO center;
    private final CustomerAccountBuilder customerAccountBuilder = new CustomerAccountBuilder();
    private String name = "Test Center";
    private OfficeBO office = new OfficeBuilder().withGlobalOfficeNum("xxx-9999").build();
    private PersonnelBO loanOfficer;
    private UserContext userContext = new UserContext();
    private DateTime mfiJoiningDate = new DateTime();
    private int numberOfCustomersInOfficeAlready = 0;
    private List<CustomerCustomFieldEntity> customerCustomFields = new ArrayList<CustomerCustomFieldEntity>();
    private Address address = null;
    private String externalId = null;
    private MeetingBuilder meetingBuilder = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday();
    private MeetingBO meeting;
    private CustomerStatus status = CustomerStatus.CENTER_ACTIVE;

    /**
     * do not update this value for integration tests.
     */
    private Integer versionNumber = null;

    public CenterBO build() {

        if (meeting == null && meetingBuilder != null) {
            meeting = meetingBuilder.build();
        }

        center = CenterBO.createNew(userContext, name, mfiJoiningDate, meeting, loanOfficer, office,
                numberOfCustomersInOfficeAlready, customerCustomFields, address, externalId);
        center.updateCustomerStatus(status);

        if (versionNumber != null) {
            center.setVersionNo(versionNumber);
        }

        return center;
    }

    public CenterBuilder withName(final String withName) {
        this.name = withName;
        return this;
    }

    public CenterBuilder with(MeetingBuilder withMeetingBuilder) {
        this.meetingBuilder = withMeetingBuilder;
        return this;
    }

    public CenterBuilder with(final MeetingBO withMeeting) {
        this.meeting = withMeeting;
        return this;
    }

    public CenterBuilder with(final OfficeBO withOffice) {
        this.office = withOffice;
        return this;
    }

    public CenterBuilder withLoanOfficer(final PersonnelBO withLoanOfficer) {
        this.loanOfficer = withLoanOfficer;
        return this;
    }

    public CenterBuilder withFee(final AmountFeeBO withFee) {
        customerAccountBuilder.withFee(withFee);
        return this;
    }

    public CenterBuilder withNumberOfExistingCustomersInOffice(final int withNumberOfExistingCustomersInOffice) {
        this.numberOfCustomersInOfficeAlready = withNumberOfExistingCustomersInOffice;
        return this;
    }

    public CenterBuilder with(Address withAddress) {
        this.address = withAddress;
        return this;
    }

    public CenterBuilder withUserContext() {
        if (loanOfficer != null) {
            userContext.setId(loanOfficer.getCreatedBy());
        }

        if (office != null) {
            userContext.setBranchGlobalNum(office.getGlobalOfficeNum());
            userContext.setBranchId(office.getOfficeId());
        }
        return this;
    }

    public CenterBuilder with(UserContext withUserContext) {
        this.userContext = withUserContext;
        return this;
    }

    public CenterBuilder inActive() {
        this.status = CustomerStatus.CENTER_INACTIVE;
        return this;
    }

    public CenterBuilder withMfiJoiningDate(DateTime withMfiJoiningDate) {
        this.mfiJoiningDate = withMfiJoiningDate;
        return this;
    }

    public CenterBuilder withExternalId(String withExternalId) {
        this.externalId = withExternalId;
        return this;
    }

    /**
     * do not update this when building centers for integration tests
     */
    public CenterBuilder withVersion(int withVersionNumber) {
        this.versionNumber = withVersionNumber;
        return this;
    }
}
