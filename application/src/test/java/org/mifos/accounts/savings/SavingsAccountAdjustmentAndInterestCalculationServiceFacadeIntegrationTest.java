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
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.calendar.DayOfWeek;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.domain.builders.MifosUserBuilder;
import org.mifos.dto.domain.SavingsAdjustmentDto;
import org.mifos.dto.domain.SavingsDepositDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
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

        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
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
        SavingsBO savingsAccount = new SavingsAccountBuilder().mandatory()
                                                              .active()
                                                              .withActivationDate(mondayTwoWeeksAgo())
                                                              .withSavingsProduct(savingsProduct)
                                                              .withCustomer(client)
                                                              .build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);

        Long savingsId = Long.valueOf(savingsAccount.getAccountId());
        Long customerId = Long.valueOf(client.getCustomerId());
        LocalDate dateOfDeposit = new LocalDate();
        Double amount = Double.valueOf(20);
        Integer modeOfPayment = Integer.valueOf(PaymentTypes.CASH.getValue().intValue());
        String receiptId = "";
        LocalDate dateOfReceipt = new LocalDate();
        Locale preferredLocale = Locale.getDefault();
        SavingsDepositDto savingsDeposit = new SavingsDepositDto(savingsId, customerId, dateOfDeposit, amount, modeOfPayment, receiptId, dateOfReceipt, preferredLocale);

        this.savingsServiceFacade.deposit(savingsDeposit);

        Double adjustedAmount = Double.valueOf("35");
        String note = "I entered 20 but it should of being 35 which is an overpayment of the mandatory sum.";
        SavingsAdjustmentDto savingsAdjustment = new SavingsAdjustmentDto(savingsId, adjustedAmount, note);
        this.savingsServiceFacade.adjustTransaction(savingsAdjustment);

        savingsAccount = IntegrationTestObjectMother.findSavingsAccountById(savingsId);
        assertThat(savingsAccount.getSavingsBalance().getAmountDoubleValue(), is(Double.valueOf("35.0")));

        savingsAccount.updateInterestAccrued();

        assertThat(savingsAccount.getSavingsBalance().getAmountDoubleValue(), is(Double.valueOf("35.0")));
    }
}