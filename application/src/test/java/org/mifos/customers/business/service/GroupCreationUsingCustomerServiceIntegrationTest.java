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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
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
public class GroupCreationUsingCustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    private PersonnelBO existingUser;
    private PersonnelBO existingLoanOfficer;
    private OfficeBO existingOffice;
    private MeetingBO existingMeeting;
    private CenterBO existingCenter;
    private List<AccountFeesEntity> noAccountFees;

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
    public void cleanDatabaseTables() {
        databaseCleaner.clean();

        noAccountFees = new ArrayList<AccountFeesEntity>();

        existingUser = IntegrationTestObjectMother.testUser();
        existingLoanOfficer = IntegrationTestObjectMother.testUser();
        existingOffice = IntegrationTestObjectMother.sampleBranchOffice();

        existingMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(existingMeeting);

        existingCenter = new CenterBuilder().withMeeting(existingMeeting).withName("Center-IntegrationTest")
                .withOffice(existingOffice).withLoanOfficer(existingLoanOfficer).withUserContext().build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);
    }

    @Test
    public void shouldCreateCenterAndRelatedCustomerAccountWithGlobalCustomerNumbersGenerated() throws Exception {

        // setup
        GroupBO group = new GroupBuilder().withParentCustomer(existingCenter).formedBy(existingUser).build();

        // exercise test
        customerService.createGroup(group, existingMeeting, noAccountFees);

        // verification
        assertThat(group.getCustomerId(), is(notNullValue()));
        assertThat(group.getGlobalCustNum(), is(notNullValue()));
        assertThat(group.getCustomerAccount(), is(notNullValue()));
        assertThat(group.getCustomerAccount().getAccountId(), is(notNullValue()));
        assertThat(group.getCustomerAccount().getGlobalAccountNum(), is(notNullValue()));
    }

    @Test
    public void shouldSaveAllDetailsToDoWithGroup() throws Exception {

        // setup
        Address address = new Address("line1", "line2", "line3", "city", "state", "country", "zip", "phonenumber");

        GroupBO group = new GroupBuilder().withName("newGroup").withAddress(address).withStatus(
                CustomerStatus.GROUP_ACTIVE).withParentCustomer(existingCenter).formedBy(existingUser).build();

        // exercise test
        customerService.createGroup(group, existingMeeting, noAccountFees);

        // verification
        assertThat(group.getDisplayName(), is("newGroup"));
        assertThat(group.getAddress().getDisplayAddress(), is("line1, line2, line3"));
        assertThat(group.getSearchId(), is("1.1.1"));
        assertThat(group.getStatus(), is(CustomerStatus.GROUP_ACTIVE));
        assertThat(group.getGroupPerformanceHistory().getGroup().getCustomerId(), is(group.getCustomerId()));
        assertThat(group.getCustomFields().size(), is(0));
        assertThat(group.getMaxChildCount(), is(0));
        assertThat(existingCenter.getMaxChildCount(), is(1));
    }

    @Test
    public void shouldCreateGroupAsTopOfHierarchy() throws Exception {

        // setup
        GroupBO group = new GroupBuilder().withName("group-on-top-of-hierarchy")
                                            .withStatus(CustomerStatus.GROUP_ACTIVE)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withOffice(existingOffice)
                                            .withMeeting(existingMeeting)
                                            .formedBy(existingUser)
                                            .isNotTrained()
                                            .trainedOn(null)
                                            .buildAsTopOfHierarchy();

        // exercise test
        this.customerService.createGroup(group, existingMeeting, noAccountFees);

        // verification
        assertThat(group.getDisplayName(), is("group-on-top-of-hierarchy"));
        assertThat(group.getSearchId(), is("1.1"));
        assertThat(group.getStatus(), is(CustomerStatus.GROUP_ACTIVE));
        assertThat(group.getGroupPerformanceHistory().getGroup().getCustomerId(), is(group.getCustomerId()));
        assertThat(group.getCustomFields().size(), is(0));
        assertThat(group.getMaxChildCount(), is(0));
    }
}