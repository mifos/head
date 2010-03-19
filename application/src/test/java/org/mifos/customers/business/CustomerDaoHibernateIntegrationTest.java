/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.List;

import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerDaoHibernate;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;

/**
 *
 */
public class CustomerDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    public CustomerDaoHibernateIntegrationTest() throws Exception {
        super();
    }

    // class under test
    private CustomerDao customerDao;

    // collaborators
    private final GenericDao genericDao = new GenericDaoHibernate();

    // test data
    private MeetingBO weeklyMeeting;
    private AmountFeeBO weeklyPeriodicFeeForCenterOnly;
    private AmountFeeBO weeklyPeriodicFeeForGroupOnly;
    private AmountFeeBO weeklyPeriodicFeeForClientsOnly;
    private AmountFeeBO weeklyPeriodicFeeForSecondClient;
    private CustomerBO center;
    private GroupBO group;
    private ClientBO activeClient;
    private ClientBO inActiveClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();

        weeklyPeriodicFeeForCenterOnly = new FeeBuilder().appliesToCenterOnly().withFeeAmount("100.0").withName(
                "Center Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).withOffice(sampleBranchOffice())
                .build();

        center = new CenterBuilder().withMeeting(weeklyMeeting).withName("Center").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withFee(weeklyPeriodicFeeForCenterOnly).build();

        weeklyPeriodicFeeForGroupOnly = new FeeBuilder().appliesToGroupsOnly().withFeeAmount("50.0").withName(
                "Group Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).withOffice(sampleBranchOffice())
                .build();

        group = new GroupBuilder().withMeeting(weeklyMeeting).withName("Group").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withFee(weeklyPeriodicFeeForGroupOnly).withParentCustomer(center).build();

        weeklyPeriodicFeeForClientsOnly = new FeeBuilder().appliesToClientsOnly().withFeeAmount("10.0").withName(
                "Client Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).withOffice(sampleBranchOffice())
                .build();

        activeClient = new ClientBuilder().active().withMeeting(weeklyMeeting).withName("Active Client").withOffice(
                sampleBranchOffice()).withLoanOfficer(testUser()).withFee(weeklyPeriodicFeeForClientsOnly)
                .withParentCustomer(group).buildForIntegrationTests();

        weeklyPeriodicFeeForSecondClient = new FeeBuilder().appliesToClientsOnly().withFeeAmount("10.0").withName(
                "Inactive Client Periodic Fee").withSameRecurrenceAs(weeklyMeeting).withOffice(sampleBranchOffice())
                .build();

        inActiveClient = new ClientBuilder().inActive().withMeeting(weeklyMeeting).withName("Inactive Client")
                .withOffice(sampleBranchOffice()).withLoanOfficer(testUser()).withFee(weeklyPeriodicFeeForSecondClient)
                .withParentCustomer(group).buildForIntegrationTests();

        IntegrationTestObjectMother.saveCustomerHierarchyWithMeetingAndFees(center, group, activeClient,
                inActiveClient, weeklyMeeting, weeklyPeriodicFeeForCenterOnly, weeklyPeriodicFeeForGroupOnly,
                weeklyPeriodicFeeForClientsOnly, weeklyPeriodicFeeForSecondClient);

        customerDao = new CustomerDaoHibernate(genericDao);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        IntegrationTestObjectMother.cleanCustomerHierarchyWithMultipleClientsMeetingsAndFees(activeClient,
                inActiveClient, group, center, weeklyMeeting);
    }

    public void testShouldFindCustomerByIdGivenCustomerExists() {

        // exercise test
        CustomerBO returnedCustomer = customerDao.findCustomerById(center.getCustomerId());

        // verification
        assertNotNull(returnedCustomer);
        assertThat(returnedCustomer.getDisplayName(), is("Center"));
    }

    public void testShouldReturnNullWhenNoCustomerExistsWithId() {

        // exercise test
        CustomerBO returnedCustomer = customerDao.findCustomerById(Integer.valueOf(-1));

        // verification
        assertNull(returnedCustomer);
    }

    public void testShouldReturnOnlyActiveClientsUnderGroup() {

        // exercise test
        List<ClientBO> activeClients = customerDao.findActiveClientsUnderGroup(group);

        // verification
        assertThat(activeClients.size(), is(1));
        assertThat(activeClients.get(0).getCustomerId(), is(activeClient.getCustomerId()));
    }

    public void testShouldFindGroupWithParentInitialisedBySystemId() {

        // exercise test
        GroupBO activeGroup = customerDao.findGroupBySystemId(group.getGlobalCustNum());

        // verification
        assertThat(activeGroup, is(notNullValue()));
        assertThat(activeGroup.getParentCustomer(), is(notNullValue()));
        assertThat(activeGroup.getParentCustomer().getDisplayName(), is(notNullValue()));
    }
}
