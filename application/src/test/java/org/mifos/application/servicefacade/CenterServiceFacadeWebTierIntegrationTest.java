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

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.domain.builders.MeetingBuilder;
import org.mifos.domain.builders.OfficeBuilder;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CenterCreationDetail;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.security.AuthenticationAuthorizationServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;

public class CenterServiceFacadeWebTierIntegrationTest extends MifosIntegrationTestCase {

    // class under test
    @Autowired
    private CenterServiceFacade centerServiceFacade;

    @Autowired private AuthenticationAuthorizationServiceFacade authenticationAuthorizationService;
    @Autowired private CustomerDao customerDao;

    private OfficeBO areaOffice1;
    private OfficeBO branch1;

    @Before
    public void setup () {
        authenticationAuthorizationService.reloadUserDetailsForSecurityContext("mifos");
    }

    @Test
    public void shouldCreateCenterWithExpectedSearchId() {

        MeetingBO meeting = new MeetingBuilder().withStartDate(new DateTime().minusWeeks(2)).build();
        MeetingDto meetingDto = meeting.toDto();

        String displayName = "testCenter";
        String externalId = null;
        AddressDto addressDto = new AddressDto("here", "", "", "", "", "", "", "");

        PersonnelBO user = IntegrationTestObjectMother.findPersonnelById(Short.valueOf("1"));
        Short loanOfficerId = user.getPersonnelId();
        List<CreateAccountFeeDto> feesToApply = new ArrayList<CreateAccountFeeDto>();
        CustomerStatus.GROUP_ACTIVE.getValue();
        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));

        // setup
        createOfficeHierarchyUnderHeadOffice(headOffice);

        Short officeId = branch1.getOfficeId();
        DateTime mfiJoiningDate = new DateTime().minusWeeks(2);
        new DateTime().minusWeeks(1);
        CenterCreationDetail centerCreationDetail = new CenterCreationDetail(mfiJoiningDate.toLocalDate(), displayName, externalId,
                addressDto, loanOfficerId, officeId, feesToApply);
        // exercise test
        CustomerDetailsDto newlyCreatedCenterDetails = centerServiceFacade.createNewCenter(centerCreationDetail, meetingDto);

        // verification
        CenterBO center = customerDao.findCenterBySystemId(newlyCreatedCenterDetails.getGlobalCustNum());
        Assert.assertThat(center.getSearchId(), is("1.1"));
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
