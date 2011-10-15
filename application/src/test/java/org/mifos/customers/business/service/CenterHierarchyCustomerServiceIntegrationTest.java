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
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.domain.builders.CenterBuilder;
import org.mifos.domain.builders.GroupBuilder;
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
public class CenterHierarchyCustomerServiceIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    // test data
    private CenterBO center;
    private PersonnelBO otherLoanOfficer;

    private GroupBO group;

    private GroupBO group2;

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

        group = new GroupBuilder().withName("group1").withParentCustomer(center).active().formedBy(center.getPersonnel()).build();
        IntegrationTestObjectMother.createGroup(group, group.getCustomerMeetingValue());

        group2 = new GroupBuilder().withName("group2").withParentCustomer(center).active().formedBy(center.getPersonnel()).build();
        IntegrationTestObjectMother.createGroup(group2, group2.getCustomerMeetingValue());

        otherLoanOfficer = new PersonnelBuilder().withDisplayName("otherLoanOfficer").with(existingBranch).build();
        IntegrationTestObjectMother.createPersonnel(otherLoanOfficer);
    }

    @Test
    public void updatingCenterWithDifferentLoanOfficerUpdatesAllChildrenInHierarchyIncludingTheirAssociatedLoansSavingsAndCustomerAccounts() throws Exception {

        // setup
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
                                                     otherLoanOfficer.getPersonnelId(), externalId, mfiJoiningDate,
                                                     address, customFields, customerPositions);

        UserContext userContext = TestUtils.makeUser();

        // exercise test
        customerService.updateCenter(userContext, centerUpdate);
        StaticHibernateUtil.flushAndClearSession();
        // verification
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        group = customerDao.findGroupBySystemId(group.getGlobalCustNum());
        group2 = customerDao.findGroupBySystemId(group2.getGlobalCustNum());
        assertThat(center.getDisplayName(), is(updatedDisplayName));
        assertThat(center.getPersonnel().getDisplayName(), is(otherLoanOfficer.getDisplayName()));
        assertThat(group.getPersonnel().getDisplayName(), is(otherLoanOfficer.getDisplayName()));
        assertThat(group2.getPersonnel().getDisplayName(), is(otherLoanOfficer.getDisplayName()));
    }
}