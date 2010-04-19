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
import static org.junit.Assert.fail;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanProductBuilder;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.LoanAccountBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.framework.TestUtils;
import org.mifos.framework.spring.SpringUtil;
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
public class ClientStatusChangeIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    private PersonnelBO existingUser;
    private PersonnelBO existingLoanOfficer;
    private OfficeBO existingOffice;
    private MeetingBO existingMeeting;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
        DatabaseSetup.initializeHibernate();

        // so messageSource bean is available.
        SpringUtil.initializeSpring();
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
    }

    @Test
    public void givenGroupIsInCancelledStateShouldNotPassValidationWhenTryingToTranistionClientToActive() throws Exception {

        // setup
        CenterBO existingCenter = new CenterBuilder().withMeeting(existingMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(existingOffice)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withUserContext().build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);

        GroupBO existingCancelledGroup = new GroupBuilder().withName("newGroup")
                                                 .withStatus(CustomerStatus.GROUP_CANCELLED)
                                                 .withParentCustomer(existingCenter)
                                                 .formedBy(existingUser).build();
        IntegrationTestObjectMother.createGroup(existingCancelledGroup, existingMeeting);

        ClientBO existingPartialClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_PARTIAL).withParentCustomer(
                existingCancelledGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingPartialClient, existingMeeting);

        existingCancelledGroup = this.customerDao.findGroupBySystemId(existingCancelledGroup.getGlobalCustNum());
        existingPartialClient = this.customerDao.findClientBySystemId(existingPartialClient.getGlobalCustNum());
        existingPartialClient.setUserContext(TestUtils.makeUser());

        CustomerStatusFlag customerStatusFlag = null;
        CustomerNoteEntity customerNote = new CustomerNoteEntity("go active", new Date(), existingPartialClient.getPersonnel(), existingPartialClient);

        // exercise test
        try {
            customerService.updateClientStatus(existingPartialClient, existingPartialClient.getStatus(), CustomerStatus.CLIENT_ACTIVE, customerStatusFlag, customerNote);
            fail("should fail validation");
        } catch (CustomerException expected) {
            assertThat(expected.getKey(), is(ClientConstants.ERRORS_GROUP_CANCELLED));
            assertThat(existingPartialClient.getStatus(), is(CustomerStatus.CLIENT_PARTIAL));
        }
    }

    @Test
    public void givenGroupIsInPartialStateShouldNotPassValidationWhenTryingToTranistionClientToActive() throws Exception {

        // setup
        CenterBO existingCenter = new CenterBuilder().withMeeting(existingMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(existingOffice)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withUserContext().build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);

        GroupBO existingCancelledGroup = new GroupBuilder().withName("newGroup")
                                                 .withStatus(CustomerStatus.GROUP_PARTIAL)
                                                 .withParentCustomer(existingCenter)
                                                 .formedBy(existingUser).build();
        IntegrationTestObjectMother.createGroup(existingCancelledGroup, existingMeeting);

        ClientBO existingPartialClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_PARTIAL).withParentCustomer(
                existingCancelledGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingPartialClient, existingMeeting);

        existingCancelledGroup = this.customerDao.findGroupBySystemId(existingCancelledGroup.getGlobalCustNum());
        existingPartialClient = this.customerDao.findClientBySystemId(existingPartialClient.getGlobalCustNum());
        existingPartialClient.setUserContext(TestUtils.makeUser());

        CustomerStatusFlag customerStatusFlag = null;
        CustomerNoteEntity customerNote = new CustomerNoteEntity("go active", new Date(), existingPartialClient.getPersonnel(), existingPartialClient);

        // exercise test
        try {
            customerService.updateClientStatus(existingPartialClient, existingPartialClient.getStatus(), CustomerStatus.CLIENT_ACTIVE, customerStatusFlag, customerNote);
            fail("should fail validation");
        } catch (CustomerException expected) {
            assertThat(expected.getKey(), is(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION));
            assertThat(existingPartialClient.getStatus(), is(CustomerStatus.CLIENT_PARTIAL));
        }
    }

    @Test
    public void givenClientHasActiveAccountsShouldNotPassValidationWhenTryingToTranistionClientToClosed() throws Exception {

        // setup
        CenterBO existingCenter = new CenterBuilder().withMeeting(existingMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(existingOffice)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withUserContext().build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);

        GroupBO existingActiveGroup = new GroupBuilder().withName("newGroup")
                                                 .withStatus(CustomerStatus.GROUP_ACTIVE)
                                                 .withParentCustomer(existingCenter)
                                                 .formedBy(existingUser).build();
        IntegrationTestObjectMother.createGroup(existingActiveGroup, existingMeeting);

        ClientBO existingActiveClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_ACTIVE)
                                                            .withParentCustomer(existingActiveGroup)
                                                            .buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingActiveClient, existingMeeting);

        DateTime startDate = new DateTime().minusDays(14);
        LoanOfferingBO clientLoanProduct = new LoanProductBuilder().appliesToClientsOnly().withGlobalProductNumber("XXX-00002")
                                                                    .active()
                                                                    .withMeeting(existingMeeting)
                                                                    .withName("Loan-client").withShortName("dsvd")
                                                                    .withStartDate(startDate)
                                                                    .withDefaultInterest(1.2)
                                                                    .buildForIntegrationTests();
        IntegrationTestObjectMother.createProduct(clientLoanProduct);

        LoanBO clientLoan = new LoanAccountBuilder().withLoanProduct(clientLoanProduct).withCustomer(existingActiveClient).withOriginalLoanAmount(600.0).build();
        IntegrationTestObjectMother.saveLoanAccount(clientLoan);

        existingActiveGroup = this.customerDao.findGroupBySystemId(existingActiveGroup.getGlobalCustNum());
        existingActiveClient = this.customerDao.findClientBySystemId(existingActiveClient.getGlobalCustNum());
        existingActiveClient.setUserContext(TestUtils.makeUser());

        CustomerStatusFlag customerStatusFlag = null;
        CustomerNoteEntity customerNote = new CustomerNoteEntity("go active", new Date(), existingActiveClient.getPersonnel(), existingActiveClient);

        // exercise test
        try {
            customerService.updateClientStatus(existingActiveClient, existingActiveClient.getStatus(), CustomerStatus.CLIENT_CLOSED, customerStatusFlag, customerNote);
            fail("should fail validation");
        } catch (CustomerException expected) {
            assertThat(expected.getKey(), is(CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION));
            assertThat(existingActiveClient.getStatus(), is(CustomerStatus.CLIENT_ACTIVE));
        }
    }
}