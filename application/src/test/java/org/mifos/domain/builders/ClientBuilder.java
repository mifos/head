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
package org.mifos.domain.builders;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientDetailEntity;
import org.mifos.customers.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.customers.client.business.ClientNameDetailEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPersonalDetailDto;
import org.mifos.framework.business.util.Address;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class ClientBuilder {

    private String name = "TestBuilderClient";
    private MeetingBO meeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
    private OfficeBO office = new OfficeBuilder().withGlobalOfficeNum("xxxx-112").build();
    private PersonnelBO loanOfficer;

    private String searchId = null;
    private CustomerStatus customerStatus = CustomerStatus.CLIENT_ACTIVE;
    private DateTime activationDate = new DateTime().toDateMidnight().toDateTime();
    private UserContext userContext;
    private DateTime mfiJoiningDate = new DateTime();
    private PersonnelBO formedBy;

    private DateTime dateOfBirth = new DateTime();
    private String governmentId;
    private boolean trained;
    private DateTime trainedDate = new DateTime();
    private Short groupFlag = YesNoFlag.YES.getValue();
    private String clientFirstName;
    private String clientLastName;
    private String secondLastName;

    private ClientNameDetailEntity clientNameDetailEntity;
    private ClientNameDetailEntity spouseFatherNameDetailEntity;
    private ClientDetailEntity clientDetailEntity;
    private List<ClientInitialSavingsOfferingEntity> associatedOfferings = new ArrayList<ClientInitialSavingsOfferingEntity>();

    private CustomerBO parentCustomer;
    private String externalId;
    private Address address;
    private Integer versionNumber = 1;

    private boolean underBranch = false;

    public ClientBO buildForIntegrationTests() {

        if (searchId == null) {
            setSearchId();
        }

        final ClientBO client = ClientBO.createNewInGroupHierarchy(userContext, name, customerStatus, mfiJoiningDate,
                parentCustomer, formedBy, clientNameDetailEntity, dateOfBirth, governmentId,
                trained, trainedDate, groupFlag, clientFirstName, clientLastName, secondLastName,
                spouseFatherNameDetailEntity, clientDetailEntity, associatedOfferings, externalId, address, new LocalDate(activationDate));
        client.setMeeting(this.meeting);

        return client;
    }

    public ClientBO buildForUnitTests() {

        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1),
                Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1).shortValue(), Integer.valueOf(1).shortValue(), Integer.valueOf(1).shortValue());
        this.clientDetailEntity = new ClientDetailEntity();
        this.clientDetailEntity.updateClientDetails(clientPersonalDetailDto);

        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto();
        clientNameDetailDto.setNames("first_name,middle_name,last_name,second_last_name".split(","));
        this.clientNameDetailEntity = new ClientNameDetailEntity(null, null, clientNameDetailDto);

        if (underBranch) {
            ClientBO client = ClientBO.createNewOutOfGroupHierarchy(
                    userContext, name, customerStatus, mfiJoiningDate,
                    office, loanOfficer, meeting, formedBy, clientNameDetailEntity, dateOfBirth, governmentId,
                    trained, trainedDate, groupFlag, clientFirstName, clientLastName, secondLastName,
                    spouseFatherNameDetailEntity, clientDetailEntity, associatedOfferings, externalId,
                    address, 10);
            client.setVersionNo(versionNumber);
            return client;
        }

        if (parentCustomer == null) {
            CenterBO center = new CenterBuilder().withLoanOfficer(loanOfficer).with(meeting).with(office).build();
            parentCustomer = new GroupBuilder().withParentCustomer(center).build();
        }

        ClientBO client = ClientBO.createNewInGroupHierarchy(userContext, name, customerStatus, mfiJoiningDate,
                parentCustomer, formedBy, clientNameDetailEntity, dateOfBirth, governmentId,
                trained, trainedDate, groupFlag, clientFirstName, clientLastName, secondLastName,
                spouseFatherNameDetailEntity, clientDetailEntity, associatedOfferings, externalId, address, new LocalDate(activationDate));
        client.setVersionNo(versionNumber);
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

    public ClientBuilder withParentCustomer(final CustomerBO withParentCustomer) {
        this.parentCustomer = withParentCustomer;
        if (withParentCustomer != null) {
            this.meeting = withParentCustomer.getCustomerMeetingValue();
            this.office = withParentCustomer.getOffice();
            this.loanOfficer = withParentCustomer.getPersonnel();
        }
        return this;
    }

    public ClientBuilder withNoParent() {
        underBranch = true;
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

    public ClientBuilder withCustomerActivationDate(DateTime withActivationDate) {
        this.activationDate = withActivationDate;
        return this;
    }

    public ClientBuilder pendingApproval() {
        this.customerStatus = CustomerStatus.CLIENT_PENDING;
        return this;
    }

    public ClientBuilder active() {
        this.customerStatus = CustomerStatus.CLIENT_ACTIVE;
        return this;
    }

    public ClientBuilder withStatus(CustomerStatus withClientStatus) {
        this.customerStatus = withClientStatus;
        return this;
    }

    public ClientBuilder withGovernmentId(String clientGovernmentId) {
        this.governmentId = clientGovernmentId;
        return this;
    }

    public ClientBuilder withVersion(int version) {
        this.versionNumber = version;
        return this;
    }
}