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
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.Localization;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/applicationContext.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class CenterStatusChangeIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PersonnelDao personnelDao;

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

        Locale locale = Localization.getInstance().getMainLocale();
        AuditConfigurtion.init(locale);

        existingUser = IntegrationTestObjectMother.testUser();
        existingLoanOfficer = IntegrationTestObjectMother.testUser();
        existingOffice = IntegrationTestObjectMother.sampleBranchOffice();

        existingMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(existingMeeting);
    }

    @Test
    public void auditLogingTracksStatusChangeOfCenterFromActiveToInactive() throws Exception {

        // setup
        CenterBO existingCenter = new CenterBuilder().withName("Center-IntegrationTest")
                                            .withMeeting(existingMeeting)
                                            .with(existingOffice)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withUserContext()
                                            .build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);

        GroupBO existingPendingGroup = new GroupBuilder().withName("newGroup")
                                            .withStatus(CustomerStatus.GROUP_CANCELLED)
                                            .withParentCustomer(existingCenter)
                                            .formedBy(existingUser).build();
        IntegrationTestObjectMother.createGroup(existingPendingGroup, existingMeeting);

        ClientBO existingCancelledClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_CANCELLED).withParentCustomer(existingPendingGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingCancelledClient, existingMeeting);

        existingCenter = this.customerDao.findCenterBySystemId(existingCenter.getGlobalCustNum());
        existingPendingGroup = this.customerDao.findGroupBySystemId(existingPendingGroup.getGlobalCustNum());
        existingCancelledClient = this.customerDao.findClientBySystemId(existingCancelledClient.getGlobalCustNum());

        existingCenter.setUserContext(TestUtils.makeUserWithLocales());

        CustomerStatusFlag customerStatusFlag = null;
        CustomerNoteEntity customerNote = new CustomerNoteEntity("go inactive", new Date(), existingCenter.getPersonnel(), existingCenter);

        // exercise test
        customerService.updateCenterStatus(existingCenter, CustomerStatus.CENTER_INACTIVE, customerStatusFlag, customerNote);

        // verification
        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.CENTER, existingCenter.getCustomerId());
        assertThat(auditLogList.size(), is(1));
        assertThat(auditLogList.get(0).getEntityTypeAsEnum(), is(EntityType.CENTER));
    }

    @Test
    public void shouldValidateStatusWithActiveGroups() throws Exception {

        // setup
        CenterBO existingCenter = new CenterBuilder().withName("Center-IntegrationTest")
                                            .withMeeting(existingMeeting)
                                            .with(existingOffice)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withUserContext()
                                            .build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);

        GroupBO existingActiveGroup = new GroupBuilder().withName("newGroup")
                                            .withStatus(CustomerStatus.GROUP_ACTIVE)
                                            .withParentCustomer(existingCenter)
                                            .formedBy(existingUser).build();
        IntegrationTestObjectMother.createGroup(existingActiveGroup, existingMeeting);

        ClientBO existingCancelledClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_CANCELLED).withParentCustomer(existingActiveGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingCancelledClient, existingMeeting);

        existingCenter = this.customerDao.findCenterBySystemId(existingCenter.getGlobalCustNum());
        existingCenter.setUserContext(TestUtils.makeUserWithLocales());

        existingActiveGroup = this.customerDao.findGroupBySystemId(existingActiveGroup.getGlobalCustNum());
        existingCancelledClient = this.customerDao.findClientBySystemId(existingCancelledClient.getGlobalCustNum());

        CustomerStatusFlag customerStatusFlag = null;
        CustomerNoteEntity customerNote = new CustomerNoteEntity("go inactive", new Date(), existingCenter.getPersonnel(), existingCenter);

        // exercise test
        try {
            customerService.updateCenterStatus(existingCenter, CustomerStatus.CENTER_INACTIVE, customerStatusFlag, customerNote);
            fail("should fail validation");
        } catch (CustomerException expected) {
            // verification
            assertThat(expected.getKey(), is(CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION));
            assertThat(existingCenter.getStatus(), is(CustomerStatus.CENTER_ACTIVE));
        }
    }

    @Test
    public void givenCenterIsInactiveAndAssignedLoanOfficerIsInactiveShouldNotPassValidationWhenTryingToTranistionClientToActive() throws Exception {

        // setup
        CenterBO existingCenter = new CenterBuilder().withMeeting(existingMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(existingOffice)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withUserContext()
                                            .build();
        existingCenter.updateCustomerStatus(CustomerStatus.CENTER_INACTIVE);
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);

        existingLoanOfficer = this.personnelDao.findPersonnelById(existingLoanOfficer.getPersonnelId());
        updatePersonnel(existingLoanOfficer, PersonnelLevel.LOAN_OFFICER, PersonnelStatus.INACTIVE, existingOffice);

        GroupBO existingPartialGroup = new GroupBuilder().withName("newGroup")
                                                 .withStatus(CustomerStatus.GROUP_PARTIAL)
                                                 .withParentCustomer(existingCenter)
                                                 .formedBy(existingUser).build();
        IntegrationTestObjectMother.createGroup(existingPartialGroup, existingMeeting);

        ClientBO existingPartialClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_PARTIAL).withParentCustomer(
                existingPartialGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingPartialClient, existingMeeting);

        existingCenter = this.customerDao.findCenterBySystemId(existingCenter.getGlobalCustNum());
        existingCenter.setUserContext(TestUtils.makeUser());
        existingPartialGroup = this.customerDao.findGroupBySystemId(existingPartialGroup.getGlobalCustNum());
        existingPartialClient = this.customerDao.findClientBySystemId(existingPartialClient.getGlobalCustNum());
        existingPartialClient.setUserContext(TestUtils.makeUser());

        CustomerStatusFlag customerStatusFlag = null;
        CustomerNoteEntity customerNote = new CustomerNoteEntity("go active", new Date(), existingCenter.getPersonnel(), existingCenter);

        // exercise test
        try {
            customerService.updateCenterStatus(existingCenter, CustomerStatus.CENTER_ACTIVE, customerStatusFlag, customerNote);
            fail("should fail validation");
        } catch (CustomerException expected) {
            assertThat(expected.getKey(), is(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION));
            assertThat(existingCenter.getStatus(), is(CustomerStatus.CENTER_INACTIVE));
        }
    }

    private void updatePersonnel(PersonnelBO loanOfficer, PersonnelLevel personnelLevel, PersonnelStatus newStatus, OfficeBO office)
            throws Exception {
        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Name name = new Name("XYZ", null, null, "Last Name");
        loanOfficer.update(newStatus, personnelLevel, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD",
                "rajendersaini@yahoo.com", null, null, name, Integer.valueOf("1"), Integer.valueOf("1"), address, Short
                        .valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
    }
}