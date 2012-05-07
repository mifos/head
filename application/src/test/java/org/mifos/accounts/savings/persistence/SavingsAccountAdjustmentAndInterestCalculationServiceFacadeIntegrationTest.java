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

package org.mifos.accounts.savings.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.builders.MifosUserBuilder;
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
import org.mifos.dto.domain.SavingsAdjustmentDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.security.AuthenticationAuthorizationServiceFacade;
import org.mifos.security.MifosUser;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 * This suite of tests is concerned with verifying schedule generation given the various customer hierarchies that can
 * be possible when creating a savings account.
 */
public class SavingsAccountAdjustmentAndInterestCalculationServiceFacadeIntegrationTest extends MifosIntegrationTestCase {

    private MeetingBO aWeeklyMeeting;
    private CenterBO center;
    private GroupBO group;
    private ClientBO client;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private SavingsServiceFacade savingsServiceFacade;

    @Autowired 
    private AuthenticationAuthorizationServiceFacade authenticationAuthorizationService;
    
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
        authenticationAuthorizationService.reloadUserDetailsForSecurityContext("mifos");
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
    public void shouldHaveCorrectBalanceAfterDepositAndAdjustment() throws Exception {

        createCenterGroupClientHierarchy(aWeeklyMeeting);

        SavingsOfferingBO savingsProduct = new SavingsProductBuilder().mandatory().withMandatoryAmount("33.0").appliesToClientsOnly().buildForIntegrationTests();
        SavingsBO savingsAccount = new SavingsAccountBuilder().active()
                                                              .withActivationDate(mondayTwoWeeksAgo())
                                                              .withSavingsProduct(savingsProduct)
                                                              .withCustomer(client)
                                                              .withCreatedBy(IntegrationTestObjectMother.testUser())
                                                              .withBalanceOf(TestUtils.createMoney("0"))
                                                              .withDepositOn("20", new DateTime())
                                                              .build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);

        Long savingsId = Long.valueOf(savingsAccount.getAccountId());
        Double adjustedAmount = Double.valueOf("35");
        String note = "I entered 20 but it should of being 35 which is an overpayment of the mandatory sum.";
        SavingsAdjustmentDto savingsAdjustment = new SavingsAdjustmentDto(savingsId, adjustedAmount, note, savingsAccount.getLastPmnt().getPaymentId());

        // exercise test
        this.savingsServiceFacade.adjustTransaction(savingsAdjustment);

        // verification
        savingsAccount = IntegrationTestObjectMother.findSavingsAccountById(savingsId);
        assertThat(savingsAccount.getSavingsBalance(), is(TestUtils.createMoney("35")));
    }
}