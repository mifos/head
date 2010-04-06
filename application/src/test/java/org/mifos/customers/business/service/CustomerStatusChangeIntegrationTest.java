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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml", "/hibernate-daos.xml", "/services.xml" })
public class CustomerStatusChangeIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    private PersonnelBO existingUser;
    private PersonnelBO existingLoanOfficer;
    private OfficeBO existingOffice;
    private MeetingBO existingMeeting;
    private CenterBO existingCenter;
    private GroupBO existingPendingGroup;
    private ClientBO existingPartialClient;
    private ClientBO existingPendingClient;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
        DatabaseSetup.initializeHibernate();
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
    public void cleanDatabaseTables() throws Exception {
        databaseCleaner.clean();

        existingUser = IntegrationTestObjectMother.testUser();
        existingLoanOfficer = IntegrationTestObjectMother.testUser();
        existingOffice = IntegrationTestObjectMother.sampleBranchOffice();

        existingMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(existingMeeting);

        existingCenter = new CenterBuilder().withMeeting(existingMeeting).withName("Center-IntegrationTest")
                .withOffice(existingOffice).withLoanOfficer(existingLoanOfficer).withUserContext().build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);

        existingPendingGroup = new GroupBuilder().withName("newGroup")
                                            .withStatus(CustomerStatus.GROUP_PENDING)
                                            .withParentCustomer(existingCenter)
                                            .formedBy(existingUser).build();
        IntegrationTestObjectMother.createGroup(existingPendingGroup, existingMeeting);

        existingPartialClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_PARTIAL).withParentCustomer(existingPendingGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingPartialClient, existingMeeting);

        existingPendingClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_PENDING).withParentCustomer(existingPendingGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingPendingClient, existingMeeting);
    }

    @Test
    public void givenGroupIsPendingAndContainsClientsInPartialStateThenWhenTransitioningToCancelledStateAllClientsAreTransitionedToPartial() throws Exception {

        // setup
        existingPendingGroup = this.customerDao.findGroupBySystemId(existingPendingGroup.getGlobalCustNum());
        existingPartialClient = this.customerDao.findClientBySystemId(existingPartialClient.getGlobalCustNum());
        existingPendingClient = this.customerDao.findClientBySystemId(existingPendingClient.getGlobalCustNum());

        // exercise test
        customerService.updateGroupStatus(existingPendingGroup, existingPendingGroup.getStatus(), CustomerStatus.GROUP_CANCELLED);

        // verification
        assertThat(existingPendingGroup.getStatus(), is(CustomerStatus.GROUP_CANCELLED));
        assertThat(existingPendingClient.getStatus(), is(CustomerStatus.CLIENT_PARTIAL));
        assertThat(existingPartialClient.getStatus(), is(CustomerStatus.CLIENT_PARTIAL));
    }
}