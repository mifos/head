/*
 * Copyright Grameen Foundation USA
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.calendar.DayOfWeek;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.domain.builders.CenterBuilder;
import org.mifos.domain.builders.ClientBuilder;
import org.mifos.domain.builders.GroupBuilder;
import org.mifos.domain.builders.MeetingBuilder;
import org.mifos.domain.builders.SavingsAccountBuilder;
import org.mifos.domain.builders.SavingsProductBuilder;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;

public class SavingsIntPostingHelperIntegrationTest extends MifosIntegrationTestCase {

    private CenterBO center;
    private GroupBO group;
    private ClientBO client;

    private SavingsBO savings1;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void setUp() throws Exception {
        databaseCleaner.clean();

        DateTime startOfFiscalYear = new DateTime().withDate(2010, 1, 1);
        MeetingBO meetingFrequency = new MeetingBuilder().customerMeeting()
                                                         .startingFrom(startOfFiscalYear.toDate())
                                                         .monthly()
                                                         .every(1)
                                                         .onDayOfMonth(1)
                                                         .build();
        createCenterGroupClientHierarchy(meetingFrequency);

        MeetingBO interestCalculationFrequency = new MeetingBuilder().savingsInterestCalulationSchedule().monthly().every(2).build();
        IntegrationTestObjectMother.saveMeeting(interestCalculationFrequency);

        MeetingBO interestPostingFrequency = new MeetingBuilder().savingsInterestPostingSchedule().monthly().every(2).build();
        IntegrationTestObjectMother.saveMeeting(interestPostingFrequency);

        SavingsOfferingBO mandatoryMinimumBalance = new SavingsProductBuilder().appliesToClientsOnly()
                                                    .withName("mandatoryMinimumBalance1")
                                                    .withShortName("mm01")
                                                    .mandatory()
                                                    .minimumBalance()
                                                    .withInterestCalculationSchedule(interestPostingFrequency)
                                                    .withInterestPostingSchedule(interestPostingFrequency)
                                                    .withMandatoryAmount("300")
                                                    .withInterestRate(Double.valueOf("12"))
                                                    .withMaxWithdrawalAmount(TestUtils.createMoney("200"))
                                                    .withMinAmountRequiredForInterestCalculation("200")
                                                    .buildForIntegrationTests();
        IntegrationTestObjectMother.saveSavingsProducts(mandatoryMinimumBalance);

        DateTime nextInterestPostingDate = new DateTime().minusDays(1);
        savings1 = new SavingsAccountBuilder().active()
                                              .withSavingsProduct(mandatoryMinimumBalance)
                                              .withCustomer(client)
                                              .withCreatedBy(IntegrationTestObjectMother.testUser())
                                              .withActivationDate(startOfFiscalYear)
                                              .withNextInterestPostingDateOf(nextInterestPostingDate)
                                              .withBalanceOf(TestUtils.createMoney("0"))
                                              .withDepositOn("300", startOfFiscalYear)
                                              .withDepositOn("200", startOfFiscalYear.plusMonths(1))
                                              .build();
        IntegrationTestObjectMother.saveSavingsAccount(savings1);
    }

    private void createCenterGroupClientHierarchy(MeetingBO meetingFrequency) throws CustomerException {
        center = new CenterBuilder().withName("Savings Center")
                                    .with(meetingFrequency)
                                    .with(sampleBranchOffice())
                                    .withLoanOfficer(testUser())
                                    .withActivationDate(mondayTwoWeeksAgo())
                                    .build();
        IntegrationTestObjectMother.createCenter(center, meetingFrequency);

        group = new GroupBuilder().withName("Group")
                                  .withMeeting(meetingFrequency)
                                  .withOffice(sampleBranchOffice())
                                  .withLoanOfficer(testUser())
                                  .withParentCustomer(center)
                                  .build();
        IntegrationTestObjectMother.createGroup(group, meetingFrequency);

        client = new ClientBuilder().withName("Client 1").active()
                                    .withMeeting(meetingFrequency)
                                    .withOffice(sampleBranchOffice())
                                    .withLoanOfficer(testUser())
                                    .withParentCustomer(group)
                                    .buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(client, meetingFrequency);
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
    public void givenActiveSavingsAccountIsDueForPostingSavingsInterestPostingBatchJobShouldPopulateLastInterestPostingDate() throws Exception {

        // pre verification
        assertNull(savings1.getLastIntPostDate());
        assertNotNull(savings1.getNextIntPostDate());

        // exercise test
        SavingsIntPostingTask savingsIntPostingTask = new SavingsIntPostingTask();
        ((SavingsIntPostingHelper) savingsIntPostingTask.getTaskHelper()).execute(new DateTime().getMillis());

        // verification
        savings1 = IntegrationTestObjectMother.findSavingsAccountById(savings1.getAccountId().longValue());
        assertNotNull(savings1.getLastIntPostDate());
        assertNotNull(savings1.getNextIntPostDate());
    }
}