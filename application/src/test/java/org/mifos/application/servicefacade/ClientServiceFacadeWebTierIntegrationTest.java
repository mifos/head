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

package org.mifos.application.servicefacade;

import static org.hamcrest.Matchers.is;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.NameType;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.domain.builders.MeetingBuilder;
import org.mifos.domain.builders.OfficeBuilder;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.ClientCreationDetail;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.screen.ClientFamilyDetailDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPersonalDetailDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.security.AuthenticationAuthorizationServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientServiceFacadeWebTierIntegrationTest extends MifosIntegrationTestCase {

    // class under test
    @Autowired
    private ClientServiceFacade clientServiceFacade;

    @Autowired private AuthenticationAuthorizationServiceFacade authenticationAuthorizationService;
    @Autowired private CustomerDao customerDao;

    private OfficeBO areaOffice1;
    private OfficeBO branch1;

    @Before
    public void setup () {
        authenticationAuthorizationService.reloadUserDetailsForSecurityContext("mifos");
    }



    @Test
    public void shouldCreateGroupWithExpectedSearchId() {

        // setup
        boolean centerHierarchyExistsOriginal = ClientRules.getCenterHierarchyExists();

        MeetingBO meeting = new MeetingBuilder().withStartDate(new DateTime().minusWeeks(2)).build();
        MeetingDto meetingDto = meeting.toDto();

        String externalId = null;
        AddressDto addressDto = new AddressDto("here", "", "", "", "", "", "", "");

        PersonnelBO user = IntegrationTestObjectMother.findPersonnelById(Short.valueOf("1"));
        Short loanOfficerId = user.getPersonnelId();
        List<ApplicableAccountFeeDto> feesToApply = new ArrayList<ApplicableAccountFeeDto>();
        CustomerStatus.CLIENT_ACTIVE.getValue();
        boolean trained = false;
        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));

        // setup
        createOfficeHierarchyUnderHeadOffice(headOffice);

        Short officeId = branch1.getOfficeId();
        DateTime mfiJoiningDate = new DateTime().minusWeeks(2);
        DateTime activationDate = new DateTime().minusWeeks(1);
        List<Short> selectedSavingProducts = new ArrayList<Short>();
        String clientName = "client";

        Short formedBy = loanOfficerId;
        Date dateOfBirth = new LocalDate(1990, 1, 1).toDateMidnight().toDate();
        String governmentId ="";
        Date trainedDate = new LocalDate(2000, 1, 1).toDateMidnight().toDate();
        Short groupFlag = YesNoFlag.NO.getValue(); // not in a group
        Integer salutation = 1;
        String firstName = "first";
        String middleName = null;
        String lastName = "last";
        String secondLastName = null;
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                salutation, firstName, middleName, lastName, secondLastName);

        // magic numbers from default data
        Integer ethnicity = 218;
        Integer citizenship = 130;
        Integer handicapped = 138;
        Integer businessActivities = 225;
        Integer educationLevel = 226;
        Short numChildren = 0;
        Short gender = 49;
        Short povertyStatus = 41;
        Integer maritalStatus = ClientPersonalDetailDto.MARRIED;
        Short clientStatus = 3; // active

        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(ethnicity,
                citizenship , handicapped , businessActivities , maritalStatus , educationLevel ,
                numChildren , gender, povertyStatus);
        ClientNameDetailDto spouseFatherName = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                salutation, firstName, middleName, lastName, secondLastName);
        InputStream picture = null;
        String parentGroupId = null;
        List<ClientNameDetailDto> familyNames = null;
        List<ClientFamilyDetailDto> familyDetails = null;

        ClientCreationDetail clientCreationDetail = new ClientCreationDetail(selectedSavingProducts,
                clientName , clientStatus, mfiJoiningDate.toDate(), externalId,
                addressDto, formedBy, dateOfBirth , governmentId , trained, trainedDate , groupFlag,
                clientNameDetailDto, clientPersonalDetailDto, spouseFatherName, picture, feesToApply,
                parentGroupId, familyNames , familyDetails , loanOfficerId, officeId, activationDate.toLocalDate());

        // exercise test
        ClientRules.setCenterHierarchyExists(false);
        List<SavingsDetailDto> allowedSavingProducts = new ArrayList<SavingsDetailDto>();
        CustomerDetailsDto newlyCreatedCustomerDetails = clientServiceFacade.createNewClient(
                clientCreationDetail, meetingDto, allowedSavingProducts);

        // verification
        ClientRules.setCenterHierarchyExists(centerHierarchyExistsOriginal);
        ClientBO client = customerDao.findClientBySystemId(newlyCreatedCustomerDetails.getGlobalCustNum());
        Assert.assertThat(client.getSearchId(), is("1." + client.getCustomerId()));
    }

    private void createOfficeHierarchyUnderHeadOffice(OfficeBO headOffice) {
        areaOffice1 = new OfficeBuilder().areaOffice()
                                                  .withParentOffice(headOffice)
                                                  .withName("areaOffice1")
                                                  .withGlobalOfficeNum("x002")
                                                  .withSearchId("1.1.2")
                                                  .build();
        IntegrationTestObjectMother.createOffice(areaOffice1);

        branch1 = new OfficeBuilder().branchOffice()
                                              .withParentOffice(areaOffice1)
                                              .withName("branch1")
                                              .withGlobalOfficeNum("x005")
                                              .withSearchId("1.1.2.1")
                                              .build();
        IntegrationTestObjectMother.createOffice(branch1);
    }
}
