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

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanProductBuilder;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.LoanAccountBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
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
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class GroupPerformanceHistoryUsingCustomerServiceIntegrationTest {

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
    private GroupBO existingGroup;
    private ClientBO existingActiveClient;
    private ClientBO existingOnHoldClient;
    private ClientBO existingCancelledClient;
    private LoanBO account1;
    private LoanBO account2;

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

        existingCenter = new CenterBuilder().with(existingMeeting).withName("Center-IntegrationTest")
                .with(existingOffice).withLoanOfficer(existingLoanOfficer).withUserContext().build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);

        existingGroup = new GroupBuilder().withName("newGroup")
                                            .withStatus(CustomerStatus.GROUP_ACTIVE)
                                            .withParentCustomer(existingCenter)
                                            .formedBy(existingUser).build();
        IntegrationTestObjectMother.createGroup(existingGroup, existingMeeting);

        existingActiveClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_ACTIVE).withParentCustomer(existingGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingActiveClient, existingMeeting);

        existingOnHoldClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_HOLD).withParentCustomer(existingGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingOnHoldClient, existingMeeting);

        existingCancelledClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_CANCELLED).withParentCustomer(existingGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingCancelledClient, existingMeeting);

        DateTime startDate = new DateTime().toDateMidnight().toDateTime();

        LoanOfferingBO groupLoanProduct = new LoanProductBuilder().appliesToGroupsOnly()
                                                                .active()
                                                                .withMeeting(existingMeeting).withName("Loandsdasd").withShortName("fsad")
                                                                .withStartDate(startDate).withDefaultInterest(1.2)
                                                                .buildForIntegrationTests();
        IntegrationTestObjectMother.createProduct(groupLoanProduct);

        account1 = new LoanAccountBuilder().withLoanProduct(groupLoanProduct).withCustomer(existingGroup).withOriginalLoanAmount(400.0).build();
        IntegrationTestObjectMother.saveLoanAccount(account1);

        LoanOfferingBO clientLoanProduct = new LoanProductBuilder().appliesToClientsOnly().withGlobalProductNumber("XXX-00002")
                                                                    .active()
                                                                    .withMeeting(existingMeeting)
                                                                    .withName("Loan-client").withShortName("dsvd")
                                                                    .withStartDate(startDate)
                                                                    .withDefaultInterest(1.2).buildForIntegrationTests();
        IntegrationTestObjectMother.createProduct(clientLoanProduct);

        account2 = new LoanAccountBuilder().withLoanProduct(clientLoanProduct).withCustomer(existingActiveClient).withOriginalLoanAmount(600.0).build();
        IntegrationTestObjectMother.saveLoanAccount(account2);
    }

    @Test
    public void shouldReturnTotalOutstandingOfAllLoansApplicableToGroupAndItsClientMembers() throws Exception {

        existingGroup = customerDao.findGroupBySystemId(existingGroup.getGlobalCustNum());

        // exercise test
        Money totalOutstandingLoanAmount = existingGroup.getGroupPerformanceHistory().getTotalOutStandingLoanAmount();

        // verification
        assertThat(totalOutstandingLoanAmount.getAmountDoubleValue(), is(Double.valueOf("1000.0")));
    }

    @Test
    public void shouldReturnAverageOfAllLoansApplicableToGroupAndItsClientMembers() throws Exception {

        existingGroup = customerDao.findGroupBySystemId(existingGroup.getGlobalCustNum());

        // exercise test
        Money totalOutstandingLoanAmount = existingGroup.getGroupPerformanceHistory().getAvgLoanAmountForMember();

        // verification
        assertThat(totalOutstandingLoanAmount.getAmountDoubleValue(), is(Double.valueOf("600.0")));
    }

    /**
     * generate portfolio at risk test requires loan account to be set up with loan_schedule.
     *
     * The first/next installment due should be set so portfolio at risk calculates.
     */
    @Ignore
    @Test
    public void portfolioAtRisk() throws Exception {

        existingGroup = customerDao.findGroupBySystemId(existingGroup.getGlobalCustNum());
        existingActiveClient = customerDao.findClientBySystemId(existingActiveClient.getGlobalCustNum());

        // exercise test
        existingGroup.getGroupPerformanceHistory().generatePortfolioAtRisk();
        Money portfolioAtRisk = existingGroup.getGroupPerformanceHistory().getPortfolioAtRisk();

        // verification
        assertThat(portfolioAtRisk.getAmountDoubleValue(), is(Double.valueOf("0.0")));
    }

    @Test
    public void shouldCountClientsThatAreActiveOrOnHold() throws Exception {
        existingGroup = customerDao.findGroupBySystemId(existingGroup.getGlobalCustNum());

        // exercise test
        Integer activeClientsInGroup = existingGroup.getGroupPerformanceHistory().getActiveClientCount();

        // verification
        assertThat(activeClientsInGroup, is(2));
    }

    @Test
    public void shouldGetTotalSavingsBalance() throws Exception {

        // setup
        SavingsOfferingBO groupSavingsProduct = new SavingsProductBuilder().appliesToGroupsOnly().voluntary().withName(
                "groupSavings").withShortName("GSP").buildForIntegrationTests();
        IntegrationTestObjectMother.createProduct(groupSavingsProduct);

        SavingsBO groupSavingsAccount = new SavingsAccountBuilder().voluntary()
                                                            .withSavingsProduct(groupSavingsProduct)
                                                            .withCustomer(existingActiveClient)
                                                            .withSavingsOfficer(existingLoanOfficer)
                                                            .withBalanceOf(new Money(Money.getDefaultCurrency(), "200.0"))
                                                            .build();
        IntegrationTestObjectMother.saveSavingsAccount(groupSavingsAccount);

        SavingsOfferingBO clientSavingsProduct = new SavingsProductBuilder().appliesToClientsOnly().voluntary()
                .withName("clientSavings").withShortName("CSP").buildForIntegrationTests();
        IntegrationTestObjectMother.createProduct(clientSavingsProduct);

        SavingsBO clientSavingsAccount = new SavingsAccountBuilder().voluntary()
                                                            .withSavingsProduct(clientSavingsProduct)
                                                            .withCustomer(existingActiveClient)
                                                            .withSavingsOfficer(existingLoanOfficer).withBalanceOf(new Money(Money.getDefaultCurrency(), "550.0"))
                                                            .build();
        IntegrationTestObjectMother.saveSavingsAccount(clientSavingsAccount);

        existingGroup = customerDao.findGroupBySystemId(existingGroup.getGlobalCustNum());

        // exercise test
        Money savingsAmount = existingGroup.getGroupPerformanceHistory().getTotalSavingsAmount();

        // verification
        assertThat(savingsAmount.getAmountDoubleValue(), is(750.0));
    }
}