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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.domain.builders.AddressBuilder.anAddress;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
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
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class CenterCreationUsingCustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

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
    }

    @Test
    public void canCreateCenterWithMinimalDetails() throws Exception {

        // setup
        // minimal details
        String centerName = "Center-IntegrationTest";
        OfficeBO existingBranch = sampleBranchOffice();
        PersonnelBO existingLoanOfficer = testUser();
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        DateTime today = new DateTime();
        Address noAddress = null;
        String noExternalId = null;

        CenterBO center = new CenterBuilder().withName(centerName)
                                            .with(weeklyMeeting)
                                            .with(existingBranch)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withMfiJoiningDate(today)
                                            .with(noAddress)
                                            .withExternalId(noExternalId)
                                            .withUserContext()
                                            .build();

        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        // verification
        assertThat(center.getCustomerId(), is(notNullValue()));
        assertThat(center.getGlobalCustNum(), is(notNullValue()));
        assertThat(center.getExternalId(), is(nullValue()));
        assertThat(center.getAddress(), is(nullValue()));
    }

    @Test
    public void canCreateCenterWithMfiJoiningDateInPast() throws Exception {

        // setup
        // minimal details
        String centerName = "Center-IntegrationTest";
        OfficeBO existingBranch = sampleBranchOffice();
        PersonnelBO existingLoanOfficer = testUser();
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        DateTime aWeekAgo = new DateTime().minusWeeks(1);

        CenterBO center = new CenterBuilder().withName(centerName)
                                            .with(weeklyMeeting)
                                            .with(existingBranch)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withMfiJoiningDate(aWeekAgo)
                                            .withUserContext()
                                            .build();

        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        // verification
        assertThat(center.getCustomerId(), is(notNullValue()));
        assertThat(center.getGlobalCustNum(), is(notNullValue()));
    }

    @Test
    public void canCreateCenterWithMfiJoiningDateInFuture() throws Exception {

        // setup
        // minimal details
        String centerName = "Center-IntegrationTest";
        OfficeBO existingBranch = sampleBranchOffice();
        PersonnelBO existingLoanOfficer = testUser();
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        DateTime aWeekFromNow = new DateTime().plusWeeks(1);

        CenterBO center = new CenterBuilder().withName(centerName)
                                            .with(weeklyMeeting)
                                            .with(existingBranch)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withMfiJoiningDate(aWeekFromNow)
                                            .withUserContext()
                                            .build();

        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        // verification
        assertThat(center.getCustomerId(), is(notNullValue()));
        assertThat(center.getGlobalCustNum(), is(notNullValue()));
    }

    @Test
    public void canCreateCenterWithAddress() throws Exception {

        // minimal details
        MeetingBuilder aWeeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday();
        String centerName = "Center-IntegrationTest";
        OfficeBO anExistingBranch = sampleBranchOffice();
        PersonnelBO existingLoanOfficer = testUser();
        DateTime aWeekFromNow = new DateTime().plusWeeks(1);

        CenterBO center = new CenterBuilder().withName(centerName)
                                            .with(aWeeklyMeeting)
                                            .with(anExistingBranch)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withMfiJoiningDate(aWeekFromNow)
                                            .with(anAddress())
                                            .withUserContext()
                                            .build();

        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, center.getCustomerMeetingValue(), noAccountFees);

        // verification
        assertThat(center.getCustomerId(), is(notNullValue()));
        assertThat(center.getGlobalCustNum(), is(notNullValue()));
        assertThat(center.getAddress(), is(notNullValue()));
    }

    @Test
    public void canCreateCenterWithFeeThatMatchesMeetingPeriod() throws Exception {

        // minimal details
        MeetingBuilder aWeeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday();
        String centerName = "Center-IntegrationTest";
        OfficeBO anExistingBranch = sampleBranchOffice();
        PersonnelBO existingLoanOfficer = testUser();
        DateTime aWeekFromNow = new DateTime().plusWeeks(1);

        CenterBO center = new CenterBuilder().withName(centerName)
                                            .with(aWeeklyMeeting)
                                            .with(anExistingBranch)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withMfiJoiningDate(aWeekFromNow)
                                            .withUserContext()
                                            .build();

        // setup
        AmountFeeBO existingWeeklyFee = new FeeBuilder().with(aWeeklyMeeting).appliesToCenterOnly().with(anExistingBranch).build();
        IntegrationTestObjectMother.saveFee(existingWeeklyFee);

        AccountFeesEntity accountFee = new AccountFeesEntity(null, existingWeeklyFee, existingWeeklyFee.getFeeAmount().getAmountDoubleValue());
        List<AccountFeesEntity> centerAccountFees = new ArrayList<AccountFeesEntity>();
        centerAccountFees.add(accountFee);

        // exercise test
        customerService.createCenter(center, center.getCustomerMeetingValue(), centerAccountFees);

        // verification
        assertThat(center.getCustomerId(), is(notNullValue()));
        assertThat(center.getGlobalCustNum(), is(notNullValue()));
        assertThat(center.getCustomerAccount().getAccountFees().isEmpty(), is(false));
    }

    @Test
    public void shouldCreateCenterAndRelatedCustomerAccountWithGlobalCustomerNumbersGenerated() throws Exception {

        // setup
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();

        CenterBO center = new CenterBuilder().with(weeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();

        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        // verification
        assertThat(center.getCustomerId(), is(notNullValue()));
        assertThat(center.getGlobalCustNum(), is(notNullValue()));
        assertThat(center.getCustomerAccount(), is(notNullValue()));
        assertThat(center.getCustomerAccount().getAccountId(), is(notNullValue()));
        assertThat(center.getCustomerAccount().getGlobalAccountNum(), is(notNullValue()));
    }
}