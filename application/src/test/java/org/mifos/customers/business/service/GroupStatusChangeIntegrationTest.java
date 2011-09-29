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

package org.mifos.customers.business.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.Localization;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.domain.builders.CenterBuilder;
import org.mifos.domain.builders.ClientBuilder;
import org.mifos.domain.builders.GroupBuilder;
import org.mifos.domain.builders.MeetingBuilder;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.util.helpers.AuditConfiguration;
import org.mifos.framework.hibernate.helper.AuditTransactionForTests;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;

public class GroupStatusChangeIntegrationTest extends MifosIntegrationTestCase {

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
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
        StaticHibernateUtil.flushSession();
    }

    @Before
    public void cleanDatabaseTables() throws Exception {
        databaseCleaner.clean();

        // For audit log interceptor
        Locale locale = Localization.getInstance().getConfiguredLocale();
        AuditConfiguration.init(locale);

        existingUser = IntegrationTestObjectMother.testUser();
        existingLoanOfficer = IntegrationTestObjectMother.testUser();
        existingOffice = IntegrationTestObjectMother.sampleBranchOffice();

        existingMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(existingMeeting);
    }

    @Test
    public void auditLogingTracksStatusChangeOfGroupFromActiveToCancelled() throws Exception {

        // setup
        CenterBO existingCenter = new CenterBuilder().withName("Center-IntegrationTest")
                                            .with(existingMeeting)
                                            .with(existingOffice)
                                            .withLoanOfficer(existingLoanOfficer).withUserContext().build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);

        GroupBO existingActiveGroup = new GroupBuilder().withName("newGroup").withStatus(CustomerStatus.GROUP_ACTIVE)
                .withParentCustomer(existingCenter).formedBy(existingUser).build();
        IntegrationTestObjectMother.createGroup(existingActiveGroup, existingMeeting);

        ClientBO existingCancelledClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_CANCELLED).withParentCustomer(existingActiveGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingCancelledClient, existingMeeting);

        existingActiveGroup = this.customerDao.findGroupBySystemId(existingActiveGroup.getGlobalCustNum());
        existingActiveGroup.setUserContext(TestUtils.makeUserWithLocales());

        CustomerStatusFlag customerStatusFlag = CustomerStatusFlag.GROUP_CANCEL_BLACKLISTED;
        CustomerNoteEntity customerNote = new CustomerNoteEntity("Made Inactive", new java.util.Date(), existingActiveGroup.getPersonnel(), existingActiveGroup);

        // exercise test
        customerService.updateGroupStatus(existingActiveGroup, existingActiveGroup.getStatus(), CustomerStatus.GROUP_CANCELLED, customerStatusFlag, customerNote);
        StaticHibernateUtil.getInterceptor().afterTransactionCompletion(new AuditTransactionForTests());
        // verification
        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.GROUP, existingActiveGroup.getCustomerId());
        assertThat(auditLogList.size(), is(1));
        assertThat(auditLogList.get(0).getEntityTypeAsEnum(), is(EntityType.GROUP));
    }

    @Test
    public void givenGroupIsPendingAndContainsClientsInPartialStateThenWhenTransitioningToCancelledStateAllClientsAreTransitionedToPartial()
            throws Exception {

        // setup
        CenterBO existingCenter = new CenterBuilder().with(existingMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(existingOffice)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withUserContext().build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);

        GroupBO existingPendingGroup = new GroupBuilder().withName("newGroup")
                                                 .withStatus(CustomerStatus.GROUP_PENDING)
                                                 .withParentCustomer(existingCenter)
                                                 .formedBy(existingUser).build();
        IntegrationTestObjectMother.createGroup(existingPendingGroup, existingMeeting);

        ClientBO existingPartialClient = new ClientBuilder().withName("partialClient").withStatus(CustomerStatus.CLIENT_PARTIAL).withParentCustomer(
                existingPendingGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingPartialClient, existingMeeting);

        ClientBO existingPendingClient = new ClientBuilder().withName("pendingClient").withStatus(CustomerStatus.CLIENT_PENDING).withParentCustomer(
                existingPendingGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingPendingClient, existingMeeting);

        existingPendingGroup = this.customerDao.findGroupBySystemId(existingPendingGroup.getGlobalCustNum());
        existingPendingGroup.setUserContext(TestUtils.makeUser());
        StaticHibernateUtil.startTransaction();
        existingPartialClient = this.customerDao.findClientBySystemId(existingPartialClient.getGlobalCustNum());
        existingPendingClient = this.customerDao.findClientBySystemId(existingPendingClient.getGlobalCustNum());

        CustomerStatusFlag customerStatusFlag = CustomerStatusFlag.GROUP_CANCEL_BLACKLISTED;
        CustomerNoteEntity customerNote = new CustomerNoteEntity("Made Inactive", new java.util.Date(), existingPendingGroup.getPersonnel(), existingPendingGroup);

        // exercise test
        customerService.updateGroupStatus(existingPendingGroup, existingPendingGroup.getStatus(), CustomerStatus.GROUP_CANCELLED, customerStatusFlag, customerNote);

        // verification
        assertThat(existingPendingGroup.getStatus(), is(CustomerStatus.GROUP_CANCELLED));
        assertThat(existingPendingClient.getStatus(), is(CustomerStatus.CLIENT_PENDING));
        assertThat(existingPartialClient.getStatus(), is(CustomerStatus.CLIENT_PARTIAL));
    }
}