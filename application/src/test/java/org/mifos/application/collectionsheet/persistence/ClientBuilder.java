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

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientDetailEntity;
import org.mifos.customers.client.business.ClientDetailView;
import org.mifos.customers.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.customers.client.business.ClientNameDetailEntity;
import org.mifos.customers.client.business.ClientNameDetailView;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class ClientBuilder {

    private final CustomerAccountBuilder customerAccountBuilder = new CustomerAccountBuilder();
    private String name = "Test Center";
    private MeetingBO meeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
    private OfficeBO office = new OfficeBuilder().withGlobalOfficeNum("xxxx-112").build();
    private PersonnelBO loanOfficer;

    private String searchId = null;
    private CustomerStatus customerStatus = CustomerStatus.CLIENT_ACTIVE;
    private DateTime activationDate = new DateTime().toDateMidnight().toDateTime();
    private UserContext userContext;
    private DateTime mfiJoiningDate = new DateTime();
    private PersonnelBO formedBy;
    private List<CustomerCustomFieldEntity> customerCustomFields = new ArrayList<CustomerCustomFieldEntity>();

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
    private Blob pictureAsBlob;
    private List<ClientInitialSavingsOfferingEntity> associatedOfferings = new ArrayList<ClientInitialSavingsOfferingEntity>();

    private CustomerBO parentCustomer;

    public ClientBO buildForIntegrationTests() {

        if (searchId == null) {
            setSearchId();
        }

        final ClientBO client = ClientBO.createNewInGroupHierarchy(userContext, name, customerStatus, mfiJoiningDate,
                parentCustomer, formedBy, customerCustomFields, clientNameDetailEntity, dateOfBirth, governmentId,
                trained, trainedDate, groupFlag, clientFirstName, clientLastName, secondLastName,
                spouseFatherNameDetailEntity, clientDetailEntity, pictureAsBlob, associatedOfferings);
        client.setMeeting(this.meeting);
        client.setCustomerActivationDate(activationDate.toDate());

        return client;
    }

    public ClientBO buildForUnitTests() {

        ClientDetailView clientDetailView = new ClientDetailView(Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1),
                Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1).shortValue(), Integer.valueOf(1).shortValue(), Integer.valueOf(1).shortValue());
        this.clientDetailEntity = new ClientDetailEntity(null, clientDetailView);

        ClientNameDetailView clientNameDetailView = new ClientNameDetailView();
        this.clientNameDetailEntity = new ClientNameDetailEntity(null, null, clientNameDetailView);

        CenterBO center = new CenterBuilder().withLoanOfficer(loanOfficer).withMeeting(meeting).withOffice(office)
                .build();
        parentCustomer = new GroupBuilder().withParentCustomer(center).build();

        final ClientBO client = ClientBO.createNewInGroupHierarchy(userContext, name, customerStatus, mfiJoiningDate,
                parentCustomer, formedBy, customerCustomFields, clientNameDetailEntity, dateOfBirth, governmentId,
                trained, trainedDate, groupFlag, clientFirstName, clientLastName, secondLastName,
                spouseFatherNameDetailEntity, clientDetailEntity, pictureAsBlob, associatedOfferings);
        client.setCustomerActivationDate(activationDate.toDate());

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

    public ClientBuilder withCustomerActivationDate(DateTime withActivationDate) {
        this.activationDate = withActivationDate;
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
}
