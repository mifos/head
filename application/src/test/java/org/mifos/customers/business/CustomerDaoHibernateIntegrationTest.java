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
package org.mifos.customers.business;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class CustomerDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    // class under test
    @Autowired
    private CustomerDao customerDao;

    // test data
    private MeetingBO weeklyMeeting;
    private AmountFeeBO weeklyPeriodicFeeForCenterOnly;
    private AmountFeeBO weeklyPeriodicFeeForGroupOnly;
    private AmountFeeBO weeklyPeriodicFeeForClientsOnly;
    private AmountFeeBO weeklyPeriodicFeeForSecondClient;
    private CustomerBO center;
    private GroupBO group;
    private ClientBO activeClient;
    private ClientBO pendingClient;

    @Autowired
    private DatabaseCleaner databaseCleaner;

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
    public void setUp() throws Exception {
        databaseCleaner.clean();

        weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        weeklyPeriodicFeeForCenterOnly = new FeeBuilder().appliesToCenterOnly().withFeeAmount("100.0").withName(
                "Center Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).with(sampleBranchOffice())
                .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForCenterOnly);

        center = new CenterBuilder().with(weeklyMeeting).withName("Center").with(sampleBranchOffice())
                .withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.createCenter((CenterBO)center, weeklyMeeting);

        weeklyPeriodicFeeForGroupOnly = new FeeBuilder().appliesToGroupsOnly().withFeeAmount("50.0").withName(
                "Group Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).with(sampleBranchOffice())
                .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForGroupOnly);

        group = new GroupBuilder().withMeeting(weeklyMeeting).withName("Group").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withFee(weeklyPeriodicFeeForGroupOnly).withParentCustomer(center).build();
        IntegrationTestObjectMother.createGroup(group, weeklyMeeting);

        weeklyPeriodicFeeForClientsOnly = new FeeBuilder().appliesToClientsOnly().withFeeAmount("10.0").withName(
                "Client Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).with(sampleBranchOffice())
                .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForClientsOnly);

        activeClient = new ClientBuilder().active().withMeeting(weeklyMeeting).withName("Active Client").withOffice(
                sampleBranchOffice()).withLoanOfficer(testUser()).withParentCustomer(group).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(activeClient, weeklyMeeting);

        weeklyPeriodicFeeForSecondClient = new FeeBuilder().appliesToClientsOnly().withFeeAmount("10.0").withName(
                "Inactive Client Periodic Fee").withSameRecurrenceAs(weeklyMeeting).with(sampleBranchOffice())
                .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForSecondClient);

        pendingClient = new ClientBuilder().pendingApproval().withMeeting(weeklyMeeting).withName("Pending Client")
                .withOffice(sampleBranchOffice()).withLoanOfficer(testUser()).withParentCustomer(group).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(pendingClient, weeklyMeeting);
    }

    @Test
    public void shouldFindCustomerByIdGivenCustomerExists() {

        // exercise test
        CustomerBO returnedCustomer = customerDao.findCustomerById(center.getCustomerId());

        // verification
        assertNotNull(returnedCustomer);
        assertThat(returnedCustomer.getDisplayName(), is("Center"));
    }

    @Test
    public void shouldReturnNullWhenNoCustomerExistsWithId() {

        // exercise test
        CustomerBO returnedCustomer = customerDao.findCustomerById(Integer.valueOf(-1));

        // verification
        assertNull(returnedCustomer);
    }

    @Test
    public void shouldReturnOnlyActiveClientsUnderGroup() {

        // exercise test
        List<ClientBO> activeClients = customerDao.findActiveClientsUnderGroup(group);

        // verification
        assertThat(activeClients.size(), is(1));
        assertThat(activeClients.get(0).getCustomerId(), is(activeClient.getCustomerId()));
    }

    @Test
    public void shouldFindGroupWithParentInitialisedBySystemId() {

        // exercise test
        GroupBO activeGroup = customerDao.findGroupBySystemId(group.getGlobalCustNum());

        // verification
        assertThat(activeGroup, is(notNullValue()));
        assertThat(activeGroup.getParentCustomer(), is(notNullValue()));
        assertThat(activeGroup.getParentCustomer().getDisplayName(), is(notNullValue()));
    }

    @Test
    public void shouldFindActiveCenters() {

        // exercise test
        List<CustomerDetailDto> activeCenters = customerDao.findActiveCentersUnderUser(center.getPersonnel());

        // verification
        assertThat(activeCenters.size(), is(1));
    }

    @Test
    public void shouldFindActiveGroups() {

        // exercise test
        List<CustomerDetailDto> activeGroups = customerDao.findGroupsUnderUser(group.getPersonnel());

        // verification
        assertThat(activeGroups.size(), is(1));
    }

    @Test
    public void shouldNotFailValidationWhenCenterNameDoesNotAlreadyExist() throws Exception {

        // exercise test
        customerDao.validateCenterNameIsNotTakenForOffice("nameDoesNotExist", center.getOfficeId());
    }

    @Test(expected=CustomerException.class)
    public void shouldThrowCustomerExceptionWhenValidationFailsDueToCenterNameAlreadyExistingForOffice() throws Exception {

        // exercise test
        customerDao.validateCenterNameIsNotTakenForOffice(center.getDisplayName(), center.getOfficeId());
    }
}