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

package org.mifos.customers.business.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.domain.builders.CenterBuilder;
import org.mifos.domain.builders.MeetingBuilder;
import org.mifos.domain.builders.PersonnelBuilder;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CenterUpdate;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerPositionDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * I test the update of {@link CenterBO}'s using the {@link CustomerService} implementation.
 *
 * This can move from being a 'integrated test' to a 'unit test' once usage of {@link StaticHibernateUtil} is removed from {@link CustomerService}.
 * Then there will only be the need for 'integrated test' at {@link CustomerDao} level.
 */
public class CenterUpdateUsingCustomerServiceIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    // test data
    private CenterBO center;
    private PersonnelBO otherLoanOfficer;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {
        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void cleanDatabaseTables() {
        databaseCleaner.clean();

        // setup
        String centerName = "Center-IntegrationTest";
        OfficeBO existingBranch = sampleBranchOffice();
        PersonnelBO existingLoanOfficer = testUser();
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        DateTime today = new DateTime();
        Address noAddress = null;
        String noExternalId = null;

        center = new CenterBuilder().withName(centerName)
                                            .with(weeklyMeeting)
                                            .with(existingBranch)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withMfiJoiningDate(today)
                                            .with(noAddress)
                                            .withExternalId(noExternalId)
                                            .withUserContext()
                                            .build();
        IntegrationTestObjectMother.createCenter(center, center.getCustomerMeetingValue());

        otherLoanOfficer = new PersonnelBuilder().withUsername("CenterUpdateUsingCustomerServiceIntegrationTest").withDisplayName("otherLoanOfficer").with(existingBranch).build();

        IntegrationTestObjectMother.createPersonnel(otherLoanOfficer);
    }

    @Test
    public void canUpdateCenterWithDifferentLoanOfficer() throws Exception {

        // setup
        String externalId = center.getExternalId();
        String mfiJoiningDate = new SimpleDateFormat("dd/MM/yyyy").format(center.getMfiJoiningDate());
        AddressDto address = null;
        if (center.getAddress() != null) {
            address = Address.toDto(center.getAddress());
        }
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        List<CustomerPositionDto> customerPositions = new ArrayList<CustomerPositionDto>();
        UserContext userContext = TestUtils.makeUser();
        otherLoanOfficer.setPreferredLocale(new SupportedLocalesEntity(userContext.getLocaleId()));
        IntegrationTestObjectMother.createPersonnel(otherLoanOfficer);
        String updatedDisplayName = "Center "+RandomStringUtils.randomAlphanumeric(5);
        CenterUpdate centerUpdate = new CenterUpdate(center.getCustomerId(), updatedDisplayName, center.getVersionNo(),
                                                    otherLoanOfficer.getPersonnelId(), externalId,
                                                    mfiJoiningDate, address, customFields, customerPositions);


        // exercise test
        customerService.updateCenter(userContext, centerUpdate);

        // verification
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        assertThat(center.getPersonnel().getDisplayName(), is(otherLoanOfficer.getDisplayName()));
    }

    @Test
    public void canUpdateCenterWithNoLoanOfficerWhenCenterIsInactive() throws Exception {

        // setup
        CustomerStatusFlag centerStatusFlag = null;
        CustomerNoteEntity customerNote = null;
        customerService.updateCenterStatus(center, CustomerStatus.CENTER_INACTIVE, centerStatusFlag, customerNote);
        StaticHibernateUtil.flushAndClearSession();
        Short loanOfficerId = null;
        String externalId = center.getExternalId();
        String mfiJoiningDate = new SimpleDateFormat("dd/MM/yyyy").format(center.getMfiJoiningDate());
        AddressDto address = null;
        if (center.getAddress() != null) {
            address = Address.toDto(center.getAddress());
        }
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        List<CustomerPositionDto> customerPositions = new ArrayList<CustomerPositionDto>();
        String updatedDisplayName = "Center "+RandomStringUtils.randomAlphanumeric(5);
        CenterUpdate centerUpdate = new CenterUpdate(center.getCustomerId(), updatedDisplayName, center.getVersionNo(),
                                       loanOfficerId, externalId, mfiJoiningDate, address, customFields, customerPositions);

        UserContext userContext = TestUtils.makeUser();

        // exercise test
        customerService.updateCenter(userContext, centerUpdate);

        // verification
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        assertThat(center.getPersonnel(), is(nullValue()));
    }

    @Test
    public void canUpdateCenterWithDifferentMfiJoiningDateInPastOrFuture() throws Exception {

        // setup
        Short loanOfficerId = center.getPersonnel().getPersonnelId();
        String externalId = center.getExternalId();

        LocalDate dateInPast = new LocalDate(center.getMfiJoiningDate()).minusWeeks(4);
        String mfiJoiningDate = new SimpleDateFormat("dd/MM/yyyy").format(dateInPast.toDateMidnight().toDate());

        AddressDto address = null;
        if (center.getAddress() != null) {
            address = Address.toDto(center.getAddress());
        }
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        List<CustomerPositionDto> customerPositions = new ArrayList<CustomerPositionDto>();

        String updatedDisplayName = "Center "+RandomStringUtils.randomAlphanumeric(5);
        CenterUpdate centerUpdate = new CenterUpdate(center.getCustomerId(), updatedDisplayName,
                                                    center.getVersionNo(), loanOfficerId, externalId,
                                                    mfiJoiningDate, address, customFields, customerPositions);

        UserContext userContext = TestUtils.makeUser();

        // exercise test
        customerService.updateCenter(userContext, centerUpdate);

        // verification
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        assertThat(center.getMfiJoiningDate(), is(dateInPast.toDateMidnight().toDate()));
    }

    @Test
    public void canUpdateCenterWithExternalId() throws Exception {

        // setup
        Short loanOfficerId = center.getPersonnel().getPersonnelId();
        String newExternalId = "ext123";

        LocalDate dateInPast = new LocalDate(center.getMfiJoiningDate()).minusWeeks(4);
        String mfiJoiningDate = new SimpleDateFormat("dd/MM/yyyy").format(dateInPast.toDateMidnight().toDate());

        AddressDto newAddress = null;
        if (center.getAddress() != null) {
            newAddress = Address.toDto(center.getAddress());
        }
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        List<CustomerPositionDto> customerPositions = new ArrayList<CustomerPositionDto>();
        String updatedDisplayName = "Center "+RandomStringUtils.randomAlphanumeric(5);
        CenterUpdate centerUpdate = new CenterUpdate(center.getCustomerId(), updatedDisplayName, center.getVersionNo(),
                                                     loanOfficerId, newExternalId, mfiJoiningDate, newAddress, customFields, customerPositions);

        UserContext userContext = TestUtils.makeUser();

        // exercise test
        customerService.updateCenter(userContext, centerUpdate);

        // verification
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        assertThat(center.getExternalId(), is(newExternalId));
    }
}