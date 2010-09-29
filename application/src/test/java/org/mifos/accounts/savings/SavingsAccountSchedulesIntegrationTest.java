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

package org.mifos.accounts.savings;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.calendar.DayOfWeek;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This suite of tests is concerned with verifying schedule generation given the various customer hierarchies that can
 * be possible when creating a savings account.
 */
public class SavingsAccountSchedulesIntegrationTest extends MifosIntegrationTestCase {

    private MeetingBO aWeeklyMeeting;
    private CenterBO center;
    private GroupBO group;
    private ClientBO client;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void cleanDatabaseTables() throws Exception {
        databaseCleaner.clean();

        aWeeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).withStartDate(mondayTwoWeeksAgo()).build();
        IntegrationTestObjectMother.saveMeeting(aWeeklyMeeting);
    }

    private void createCenterAndGroupHierarchyWithNoClients(MeetingBO aWeeklyMeeting) {
        center = new CenterBuilder().withName("Savings Center")
                .with(aWeeklyMeeting)
                .with(sampleBranchOffice())
                .withLoanOfficer(testUser())
                .withActivationDate(mondayTwoWeeksAgo())
                .build();
        IntegrationTestObjectMother.createCenter(center, aWeeklyMeeting);

        group = new GroupBuilder().withName("Group")
              .withMeeting(aWeeklyMeeting)
              .withOffice(sampleBranchOffice())
              .withLoanOfficer(testUser())
              .withParentCustomer(center)
              .build();
        IntegrationTestObjectMother.createGroup(group, aWeeklyMeeting);
    }

    private void createCenterGroupClientHierarchy(MeetingBO aWeeklyMeeting) throws CustomerException {
        center = new CenterBuilder().withName("Savings Center")
                                    .with(aWeeklyMeeting)
                                    .with(sampleBranchOffice())
                                    .withLoanOfficer(testUser())
                                    .withActivationDate(mondayTwoWeeksAgo())
                                    .build();
        IntegrationTestObjectMother.createCenter(center, aWeeklyMeeting);

        group = new GroupBuilder().withName("Group")
                                  .withMeeting(aWeeklyMeeting)
                                  .withOffice(sampleBranchOffice())
                                  .withLoanOfficer(testUser())
                                  .withParentCustomer(center)
                                  .build();
        IntegrationTestObjectMother.createGroup(group, aWeeklyMeeting);

        client = new ClientBuilder().withName("Client 1").active()
                                    .withMeeting(aWeeklyMeeting)
                                    .withOffice(sampleBranchOffice())
                                    .withLoanOfficer(testUser())
                                    .withParentCustomer(group)
                                    .buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(client, aWeeklyMeeting);
    }

    private DateTime mondayTwoWeeksAgo() {
        DateTime mondayTwoWeeksAgo = new DateTime().withDayOfWeek(DayOfWeek.monday());
        if (mondayTwoWeeksAgo.isAfter(new DateTime()) || mondayTwoWeeksAgo.equals(new DateTime())) {
            mondayTwoWeeksAgo = mondayTwoWeeksAgo.minusWeeks(2);
        } else {
            mondayTwoWeeksAgo = mondayTwoWeeksAgo.minusWeeks(1);
        }
        return mondayTwoWeeksAgo;
    }

    @Test
    public void shouldGenerateSavingsAccountSchedulesForActiveClientsOfCenter() throws Exception {

        createCenterGroupClientHierarchy(aWeeklyMeeting);

        SavingsOfferingBO savingsProduct = new SavingsProductBuilder().mandatory().withMandatoryAmount("33.0").appliesToCentersOnly().buildForIntegrationTests();
        SavingsBO savingsAccount = new SavingsAccountBuilder().mandatory()
                                                              .active()
                                                              .withActivationDate(mondayTwoWeeksAgo())
                                                              .withSavingsProduct(savingsProduct)
                                                              .withCustomer(center)
                                                              .build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);

        List<AccountActionDateEntity> savingSchedules = savingsAccount.getAccountActionDatesSortedByInstallmentId();
        for (AccountActionDateEntity savingSchedule : savingSchedules) {
            assertThat("saving schedule should be generated for active client belong to center savings account", savingSchedule.getCustomer().getCustomerId(), is(client.getCustomerId()));
        }
    }

    @Test
    public void shouldGenerateSavingsAccountSchedulesForActiveClientsOfGroupWhenUsingPerIndividualSetting() throws Exception {

        createCenterGroupClientHierarchy(aWeeklyMeeting);

        SavingsOfferingBO savingsProduct = new SavingsProductBuilder().mandatory().withMandatoryAmount("33.0").appliesToGroupsOnly().trackedPerIndividual().buildForIntegrationTests();
        SavingsBO savingsAccount = new SavingsAccountBuilder().mandatory()
                                                              .active()
                                                              .withActivationDate(mondayTwoWeeksAgo())
                                                              .withSavingsProduct(savingsProduct)
                                                              .withCustomer(group)
                                                              .build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);

        List<AccountActionDateEntity> savingSchedules = savingsAccount.getAccountActionDatesSortedByInstallmentId();
        for (AccountActionDateEntity savingSchedule : savingSchedules) {
            assertThat("saving schedule should be generated for active client belong to group savings account", savingSchedule.getCustomer().getCustomerId(), is(client.getCustomerId()));
        }
    }

    @Test
    public void shouldGenerateSavingsAccountSchedulesForGroupOnlyWhenUsingCompleteGroupSetting() throws Exception {

        createCenterGroupClientHierarchy(aWeeklyMeeting);

        SavingsOfferingBO savingsProduct = new SavingsProductBuilder().mandatory().withMandatoryAmount("33.0").appliesToGroupsOnly().trackedOnCompleteGroup().buildForIntegrationTests();
        SavingsBO savingsAccount = new SavingsAccountBuilder().mandatory()
                                                              .active()
                                                              .withActivationDate(mondayTwoWeeksAgo())
                                                              .withSavingsProduct(savingsProduct)
                                                              .withCustomer(group)
                                                              .build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);

        List<AccountActionDateEntity> savingSchedules = savingsAccount.getAccountActionDatesSortedByInstallmentId();
        for (AccountActionDateEntity savingSchedule : savingSchedules) {
            assertThat("saving schedule should be generated for group only and not any of its clients", savingSchedule.getCustomer().getCustomerId(), is(group.getCustomerId()));
        }
    }

    @Test
    public void shouldGenerateSavingsAccountSchedulesForClient() throws Exception {

        createCenterGroupClientHierarchy(aWeeklyMeeting);

        SavingsOfferingBO savingsProduct = new SavingsProductBuilder().mandatory().withMandatoryAmount("33.0").appliesToClientsOnly().buildForIntegrationTests();
        SavingsBO savingsAccount = new SavingsAccountBuilder().mandatory()
                                                              .active()
                                                              .withActivationDate(mondayTwoWeeksAgo())
                                                              .withSavingsProduct(savingsProduct)
                                                              .withCustomer(client)
                                                              .build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);

        List<AccountActionDateEntity> savingSchedules = savingsAccount.getAccountActionDatesSortedByInstallmentId();
        for (AccountActionDateEntity savingSchedule : savingSchedules) {
            assertThat("saving schedule should be generated for individual client savings account", savingSchedule.getCustomer().getCustomerId(), is(client.getCustomerId()));
        }
    }

    @Test
    public void shouldNotGenerateSavingsAccountSchedulesForCenterWithoutActiveClients() throws Exception {

        createCenterAndGroupHierarchyWithNoClients(aWeeklyMeeting);

        SavingsOfferingBO savingsProduct = new SavingsProductBuilder().mandatory().withMandatoryAmount("33.0").appliesToCentersOnly().buildForIntegrationTests();
        SavingsBO savingsAccount = new SavingsAccountBuilder().mandatory()
                                                              .active()
                                                              .withActivationDate(mondayTwoWeeksAgo())
                                                              .withSavingsProduct(savingsProduct)
                                                              .withCustomer(center)
                                                              .build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);

        List<AccountActionDateEntity> savingSchedules = savingsAccount.getAccountActionDatesSortedByInstallmentId();
        assertTrue(savingSchedules.isEmpty());
    }

    @Test
    public void shouldNotGenerateSavingsAccountSchedulesForGroupWithoutActiveClients() throws Exception {

        createCenterAndGroupHierarchyWithNoClients(aWeeklyMeeting);

        SavingsOfferingBO savingsProduct = new SavingsProductBuilder().mandatory().withMandatoryAmount("33.0").appliesToGroupsOnly().trackedPerIndividual().buildForIntegrationTests();
        SavingsBO savingsAccount = new SavingsAccountBuilder().mandatory()
                                                              .active()
                                                              .withActivationDate(mondayTwoWeeksAgo())
                                                              .withSavingsProduct(savingsProduct)
                                                              .withCustomer(group)
                                                              .build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);

        List<AccountActionDateEntity> savingSchedules = savingsAccount.getAccountActionDatesSortedByInstallmentId();
        assertTrue(savingSchedules.isEmpty());
    }

}