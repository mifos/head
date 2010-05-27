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

package org.mifos.framework.components.batchjobs.helpers;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Date;
import java.util.Set;

import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.batchjobs.MifosTask;
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
                                    "/org/mifos/config/resources/hibernate-daos.xml"})
public class ApplyCustomerFeeChangesBatchJobIntegrationTest {

    // class under test
    private ApplyCustomerFeeChangesHelper applyCustomerFeeChanges;

    // collaborators
    private CenterBO center;
    private AmountFeeBO weeklyPeriodicFeeForCenterOnly;

    @Autowired
    private CustomerDao customerDao;

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

        DateTime eightWeeksInPast = new DateTime().minusWeeks(8);
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).withStartDate(eightWeeksInPast).build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        weeklyPeriodicFeeForCenterOnly = new FeeBuilder().appliesToCenterOnly()
                                                                     .withFeeAmount("100.0")
                                                                     .withName("Center Weekly Periodic Fee")
                                                                     .withSameRecurrenceAs(weeklyMeeting)
                                                                     .with(sampleBranchOffice())
                                                                     .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForCenterOnly);

        center = new CenterBuilder().withName("Center1")
                                    .with(weeklyMeeting)
                                    .with(sampleBranchOffice())
                                    .withLoanOfficer(testUser())
                                    .build();
        IntegrationTestObjectMother.createCenter(center, weeklyMeeting, weeklyPeriodicFeeForCenterOnly);

        MifosTask mifosTask = null;
        applyCustomerFeeChanges = new ApplyCustomerFeeChangesHelper(mifosTask);
    }

    @Test
    public void givenFeeAmountIsChangedShouldApplyFeeChangesToUpcomingAndFutureAccountFees() throws Exception {

        weeklyPeriodicFeeForCenterOnly.setFeeAmount(TestUtils.createMoney("52"));
        weeklyPeriodicFeeForCenterOnly.updateFeeChangeType(FeeChangeType.AMOUNT_UPDATED);
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForCenterOnly);
        long timeInMillis = new Date().getTime();

        // pre-verification
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        assertThatEachScheduleHasFeesDueOf(center.getCustomerAccount().getAccountActionDates(), 100.0);

        // exercise test
        applyCustomerFeeChanges.execute(timeInMillis);

        // verification
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        assertThatFutureScheduleHasFeesDueOf(center.getCustomerAccount().getAccountActionDates(), 52.0);
    }

    @Test
    public void givenFeeStatusIsChangedShouldApplyFeeChangesToUpcomingAndFutureAccountFees() throws Exception {

        weeklyPeriodicFeeForCenterOnly.updateDetails(TestUtils.makeUser());
        weeklyPeriodicFeeForCenterOnly.updateFeeChangeType(FeeChangeType.STATUS_UPDATED);
        weeklyPeriodicFeeForCenterOnly.updateStatus(FeeStatus.INACTIVE);
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForCenterOnly);
        long timeInMillis = new Date().getTime();

        // pre-verification
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        assertThatEachScheduleHasFeesDueOf(center.getCustomerAccount().getAccountActionDates(), 100.0);

        // exercise test
        applyCustomerFeeChanges.execute(timeInMillis);

        // verification
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
//        assertThatFutureScheduleHasFeesDueOf(center.getCustomerAccount().getAccountActionDates(), 52.0);
    }

    private void assertThatEachScheduleHasFeesDueOf(Set<AccountActionDateEntity> customerSchedules, double feesDue) {
        for (AccountActionDateEntity accountActionDateEntity : customerSchedules) {
            CustomerScheduleEntity customerSchedule = (CustomerScheduleEntity) center.getCustomerAccount().getAccountActionDate(accountActionDateEntity.getInstallmentId());
            assertThat(customerSchedule.getTotalFees().getAmountDoubleValue(), is(feesDue));
        }
    }

    private void assertThatFutureScheduleHasFeesDueOf(Set<AccountActionDateEntity> customerSchedules,  double feesDue) {
        for (AccountActionDateEntity accountActionDateEntity : customerSchedules) {
            CustomerScheduleEntity customerSchedule = (CustomerScheduleEntity) center.getCustomerAccount().getAccountActionDate(accountActionDateEntity.getInstallmentId());

            DateTime scheduledDate = new DateTime(customerSchedule.getActionDate());

            if (scheduledDate.isAfter(new DateTime().minusDays(1))) {
                assertThat(customerSchedule.getTotalFees().getAmountDoubleValue(), is(feesDue));
            }
        }
    }
}
