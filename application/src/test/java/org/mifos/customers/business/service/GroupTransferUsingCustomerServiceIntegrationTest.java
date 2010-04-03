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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/integration-test-context.xml", "/hibernate-daos.xml", "/services.xml"})
public class GroupTransferUsingCustomerServiceIntegrationTest {

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

    // FIXME - keithw - fill in all tests
    @Test
    public void givenSuccessfullTransferToCenterInSameBranchShouldExecuteLogging() throws Exception {
//        createObjectsForTranferToCenterInSameBranch();
//        StaticHibernateUtil.closeSession();
//
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        group.setUserContext(center.getUserContext());
//        StaticHibernateUtil.getSessionTL();
//        StaticHibernateUtil.getInterceptor().createInitialValueMap(group);
//        group.transferToCenter(center1);
//        StaticHibernateUtil.commitTransaction();
//        StaticHibernateUtil.closeSession();
//
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        center = TestObjectFactory.getCenter(center.getCustomerId());
//        center1 = TestObjectFactory.getCenter(center1.getCustomerId());
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        group1 = TestObjectFactory.getGroup(group1.getCustomerId());
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        client1 = TestObjectFactory.getClient(client1.getCustomerId());
//        client2 = TestObjectFactory.getClient(client2.getCustomerId());
//
//        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.GROUP, group.getCustomerId());
//        Assert.assertEquals(1, auditLogList.size());
//        Assert.assertEquals(EntityType.GROUP.getValue(), auditLogList.get(0).getEntityType());
//        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
//            if (auditLogRecord.getFieldName().equalsIgnoreCase("Kendra Name")) {
//                Assert.assertEquals("Center", auditLogRecord.getOldValue());
//                Assert.assertEquals("toTransfer", auditLogRecord.getNewValue());
//            } else {
//                // TODO: Kendra versus Center?
//                // Assert.fail();
//            }
//        }
//        TestObjectFactory.cleanUpChangeLog();
    }

    @Test
    public void givenUpdateBranchFailure_OfficeNULL() throws Exception {
//        StaticHibernateUtil.startTransaction();
//        group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
//        try {
//            group.transferToBranch(null);
//            Assert.assertTrue(false);
//        } catch (CustomerException ce) {
//            Assert.assertTrue(true);
//            Assert.assertEquals(CustomerConstants.INVALID_OFFICE, ce.getKey());
//        }
    }

    @Test
    public void givenUpdateBranchFailure_TransferInSameOffice() throws Exception {
//        StaticHibernateUtil.startTransaction();
//        group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
//        try {
//            group.transferToBranch(group.getOffice());
//            Assert.assertTrue(false);
//        } catch (CustomerException ce) {
//            Assert.assertTrue(true);
//            Assert.assertEquals(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER, ce.getKey());
//        }
    }

    @Test
    public void givenUpdateBranchFailure_OfficeInactive() throws Exception {
//        StaticHibernateUtil.startTransaction();
//        group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
//        officeBO = createOffice();
//        officeBO.update(officeBO.getOfficeName(), officeBO.getShortName(), OfficeStatus.INACTIVE, officeBO
//                .getOfficeLevel(), officeBO.getParentOffice(), null, null);
//        StaticHibernateUtil.commitTransaction();
//        StaticHibernateUtil.closeSession();
//        try {
//            group.transferToBranch(officeBO);
//            Assert.assertTrue(false);
//        } catch (CustomerException ce) {
//            Assert.assertTrue(true);
//            Assert.assertEquals(CustomerConstants.ERRORS_TRANSFER_IN_INACTIVE_OFFICE, ce.getKey());
//        }
    }

    @Test
    public void givenUpdateBranchFailure_DuplicateGroupName() throws Exception {
//        StaticHibernateUtil.startTransaction();
//        group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
//        officeBO = createOffice();
//        StaticHibernateUtil.commitTransaction();
//        StaticHibernateUtil.closeSession();
//
//        StaticHibernateUtil.startTransaction();
//        group1 = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE, officeBO.getOfficeId());
//        try {
//            group.transferToBranch(officeBO);
//            Assert.assertTrue(false);
//        } catch (CustomerException ce) {
//            Assert.assertTrue(true);
//            Assert.assertEquals(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, ce.getKey());
//        }
    }

    @Test
    public void givenSuccessfulTransferToBranch() throws Exception {
//        StaticHibernateUtil.startTransaction();
//        group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
//        client = createClient(group, CustomerStatus.CLIENT_ACTIVE);
//        client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
//        client2 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
//        officeBO = createOffice();
//        client2.changeStatus(CustomerStatus.CLIENT_CLOSED, CustomerStatusFlag.CLIENT_CLOSED_TRANSFERRED, "comment");
//        StaticHibernateUtil.commitTransaction();
//        StaticHibernateUtil.closeSession();
//
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        group.setUserContext(TestUtils.makeUser());
//        Assert.assertNull(client.getActiveCustomerMovement());
//
//        group.transferToBranch(officeBO);
//        StaticHibernateUtil.commitTransaction();
//        StaticHibernateUtil.closeSession();
//
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        client1 = TestObjectFactory.getClient(client1.getCustomerId());
//        client2 = TestObjectFactory.getClient(client2.getCustomerId());
//        officeBO = new OfficePersistence().getOffice(officeBO.getOfficeId());
//        Assert.assertNotNull(group.getActiveCustomerMovement());
//        Assert.assertNotNull(client.getActiveCustomerMovement());
//        Assert.assertNotNull(client1.getActiveCustomerMovement());
//        Assert.assertNotNull(client2.getActiveCustomerMovement());
//
//        Assert.assertEquals(officeBO.getOfficeId(), group.getOffice().getOfficeId());
//        Assert.assertEquals(officeBO.getOfficeId(), client.getOffice().getOfficeId());
//        Assert.assertEquals(officeBO.getOfficeId(), client1.getOffice().getOfficeId());
//        Assert.assertEquals(officeBO.getOfficeId(), client2.getOffice().getOfficeId());
//
//        Assert.assertEquals(CustomerStatus.GROUP_HOLD, group.getStatus());
//        Assert.assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
//        Assert.assertEquals(CustomerStatus.CLIENT_PARTIAL, client1.getStatus());
//        Assert.assertEquals(CustomerStatus.CLIENT_CLOSED, client2.getStatus());
//
//        Assert.assertNull(group.getPersonnel());
//        Assert.assertNull(client.getPersonnel());
//        Assert.assertNull(client1.getPersonnel());
//        Assert.assertNull(client2.getPersonnel());
    }

    @Test
    public void givenUpdateCenterFailure_CenterNULL() throws Exception {
//        createInitialObjects();
//        try {
//            group.transferToCenter(null);
//            Assert.assertTrue(false);
//        } catch (CustomerException ce) {
//            Assert.assertTrue(true);
//            Assert.assertEquals(CustomerConstants.INVALID_PARENT, ce.getKey());
//        }
    }

    @Test
    public void givenUpdateCenterFailure_TransferInSameCenter() throws Exception {
//        createInitialObjects();
//        try {
//            group.transferToCenter((CenterBO) group.getParentCustomer());
//            Assert.assertTrue(false);
//        } catch (CustomerException ce) {
//            Assert.assertTrue(true);
//            Assert.assertEquals(CustomerConstants.ERRORS_SAME_PARENT_TRANSFER, ce.getKey());
//        }
    }

    @Test
    public void givenUpdateCenterFailure_TransferInInactiveCenter() throws Exception {
//        createInitialObjects();
//        center1 = createCenter("newCenter");
////        center1.changeStatus(CustomerStatus.CENTER_INACTIVE, null, "changeStatus");
//        StaticHibernateUtil.commitTransaction();
//        try {
//            group.transferToCenter(center1);
//            Assert.fail();
//        } catch (CustomerException e) {
//            Assert.assertEquals(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE, e.getKey());
//        }
    }

    @Test
    public void givenUpdateCenterFailure_GroupHasActiveAccount() throws Exception {
//        createInitialObjects();
//        account1 = getSavingsAccount(group, "Savings Prod", "SAVP");
//        center1 = createCenter("newCenter");
//        StaticHibernateUtil.closeSession();
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        try {
//            group.transferToCenter(center1);
//            Assert.assertTrue(false);
//        } catch (CustomerException ce) {
//            Assert.assertTrue(true);
//            Assert.assertEquals(CustomerConstants.ERRORS_HAS_ACTIVE_ACCOUNT, ce.getKey());
//        }
//        StaticHibernateUtil.closeSession();
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        account1 = TestObjectFactory.getObject(AccountBO.class, account1.getAccountId());
    }

    @Test
    public void givenUpdateCenterFailure_GroupChildrenHasActiveAccount() throws Exception {
//        createInitialObjects();
//        account1 = getSavingsAccount(client, "Savings Prod", "SAVP");
//        center1 = createCenter("newCenter");
//        StaticHibernateUtil.closeSession();
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        try {
//            group.transferToCenter(center1);
//            Assert.assertTrue(false);
//        } catch (CustomerException ce) {
//            Assert.assertTrue(true);
//            Assert.assertEquals(CustomerConstants.ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT, ce.getKey());
//        }
//        StaticHibernateUtil.closeSession();
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        account1 = TestObjectFactory.getObject(AccountBO.class, account1.getAccountId());
    }

    @Test
    public void givenUpdateCenterFailure_MeetingFrequencyMismatch() throws Exception {
//        createInitialObjects();
//        center1 = createCenter("newCenter", createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, Short
//                .valueOf("1"), new Date()));
//        StaticHibernateUtil.closeSession();
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        try {
//            group.transferToCenter(center1);
//            Assert.assertTrue(false);
//        } catch (CustomerException ce) {
//            Assert.assertTrue(true);
//            Assert.assertEquals(CustomerConstants.ERRORS_MEETING_FREQUENCY_MISMATCH, ce.getKey());
//        }
//        StaticHibernateUtil.closeSession();
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    @Test
    public void givenUpdateCenterFailure_MeetingFrequencyMonthly() throws Exception {
//        center = createCenter("Centerold", createMonthlyMeetingOnDate(Short.valueOf("5"), Short.valueOf("1"),
//                new Date()));
//        group = createGroup("groupold", center);
//        client = createClient(group, CustomerStatus.CLIENT_ACTIVE);
//        center1 = createCenter("newCenter", createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, Short
//                .valueOf("1"), new Date()));
//        StaticHibernateUtil.closeSession();
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        group.setUserContext(TestObjectFactory.getContext());
//        group.transferToCenter(center1);
//        StaticHibernateUtil.commitTransaction();
//        StaticHibernateUtil.closeSession();
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        client = TestObjectFactory.getClient(client.getCustomerId());
//
//        Assert.assertNotNull(group.getCustomerMeeting().getUpdatedMeeting());
//        Assert.assertNotNull(client.getCustomerMeeting().getUpdatedMeeting());
//        Assert.assertEquals(WeekDay.MONDAY, group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
//                .getWeekDay());
//        Assert.assertEquals(WeekDay.MONDAY, client.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
//                .getWeekDay());
//
//        group.changeUpdatedMeeting();
//        client.changeUpdatedMeeting();
//        StaticHibernateUtil.commitTransaction();
//        StaticHibernateUtil.closeSession();
//
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//
//        Assert.assertNull(group.getCustomerMeeting().getUpdatedMeeting());
//        Assert.assertNull(client.getCustomerMeeting().getUpdatedMeeting());
//
//        Assert.assertEquals(WeekDay.MONDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
//        Assert.assertEquals(WeekDay.MONDAY, client.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
//
//        center = TestObjectFactory.getCenter(center.getCustomerId());
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    @Test
    public void givenSuccessfulTransferToCenterInSameBranch() throws Exception {
//        createObjectsForTranferToCenterInSameBranch();
//        String newCenterSearchId = center1.getSearchId();
//        StaticHibernateUtil.closeSession();
//
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        group.setUserContext(center.getUserContext());
//        group.transferToCenter(center1);
//        StaticHibernateUtil.commitTransaction();
//        StaticHibernateUtil.closeSession();
//
//        center = TestObjectFactory.getCenter(center.getCustomerId());
//        center1 = TestObjectFactory.getCenter(center1.getCustomerId());
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        group1 = TestObjectFactory.getGroup(group1.getCustomerId());
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        client1 = TestObjectFactory.getClient(client1.getCustomerId());
//        client2 = TestObjectFactory.getClient(client2.getCustomerId());
//
//        Assert.assertNotNull(group.getCustomerMeeting().getUpdatedMeeting());
//        Assert.assertNotNull(client.getCustomerMeeting().getUpdatedMeeting());
//        Assert.assertNotNull(client1.getCustomerMeeting().getUpdatedMeeting());
//        Assert.assertNotNull(client2.getCustomerMeeting().getUpdatedMeeting());
//
//        Assert.assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
//                .getWeekDay());
//        Assert.assertEquals(WeekDay.THURSDAY, client.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
//                .getWeekDay());
//        Assert.assertEquals(WeekDay.THURSDAY, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
//                .getWeekDay());
//        Assert.assertEquals(WeekDay.THURSDAY, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
//                .getWeekDay());
//
//        Assert.assertEquals(center1.getCustomerId(), group.getParentCustomer().getCustomerId());
//        Assert.assertEquals(0, center.getMaxChildCount().intValue());
//        Assert.assertEquals(2, center1.getMaxChildCount().intValue());
//        Assert.assertEquals(3, group.getMaxChildCount().intValue());
//
//        Assert.assertEquals(newCenterSearchId + ".2", group.getSearchId());
//        Assert.assertEquals(group.getSearchId() + ".1", client.getSearchId());
//        Assert.assertEquals(group.getSearchId() + ".2", client1.getSearchId());
//        Assert.assertEquals(group.getSearchId() + ".3", client2.getSearchId());
//
//        Assert.assertNull(group.getActiveCustomerMovement());
//        Assert.assertNull(client.getActiveCustomerMovement());
//        Assert.assertNull(client1.getActiveCustomerMovement());
//        Assert.assertNull(client2.getActiveCustomerMovement());
//
//        CustomerHierarchyEntity currentHierarchy = group.getActiveCustomerHierarchy();
//        Assert.assertEquals(center1.getCustomerId(), currentHierarchy.getParentCustomer().getCustomerId());
    }

    @Test
    public void givenSuccessfulTransferToCenterInDifferentBranch() throws Exception {
//        createObjectsForTranferToCenterInDifferentBranch();
//        String newCenterSearchId = center1.getSearchId();
//        StaticHibernateUtil.closeSession();
//
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        group.setUserContext(center.getUserContext());
//        group.transferToCenter(center1);
//        StaticHibernateUtil.commitTransaction();
//        StaticHibernateUtil.closeSession();
//
//        center = TestObjectFactory.getCenter(center.getCustomerId());
//        center1 = TestObjectFactory.getCenter(center1.getCustomerId());
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        group1 = TestObjectFactory.getGroup(group1.getCustomerId());
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        client1 = TestObjectFactory.getClient(client1.getCustomerId());
//        client2 = TestObjectFactory.getClient(client2.getCustomerId());
//        officeBO = new OfficePersistence().getOffice(officeBO.getOfficeId());
//
//        Assert.assertEquals(center1.getCustomerId(), group.getParentCustomer().getCustomerId());
//        Assert.assertEquals(0, center.getMaxChildCount().intValue());
//        Assert.assertEquals(2, center1.getMaxChildCount().intValue());
//        Assert.assertEquals(3, group.getMaxChildCount().intValue());
//
//        Assert.assertEquals(newCenterSearchId + ".2", group.getSearchId());
//        Assert.assertEquals(group.getSearchId() + ".1", client.getSearchId());
//        Assert.assertEquals(group.getSearchId() + ".2", client1.getSearchId());
//        Assert.assertEquals(group.getSearchId() + ".3", client2.getSearchId());
//
//        Assert.assertEquals(CustomerStatus.GROUP_HOLD.getValue(), group.getCustomerStatus().getId());
//        Assert.assertEquals(CustomerStatus.CLIENT_HOLD.getValue(), client.getCustomerStatus().getId());
//        Assert.assertEquals(CustomerStatus.CLIENT_PARTIAL.getValue(), client1.getCustomerStatus().getId());
//        Assert.assertEquals(CustomerStatus.CLIENT_CANCELLED.getValue(), client2.getCustomerStatus().getId());
//
//        CustomerHierarchyEntity currentHierarchy = group.getActiveCustomerHierarchy();
//        Assert.assertEquals(center1.getCustomerId(), currentHierarchy.getParentCustomer().getCustomerId());
//
//        Assert.assertNotNull(group.getActiveCustomerMovement());
//        Assert.assertNotNull(client.getActiveCustomerMovement());
//        Assert.assertNotNull(client1.getActiveCustomerMovement());
//        Assert.assertNotNull(client2.getActiveCustomerMovement());
//
//        CustomerMovementEntity customerMovement = group.getActiveCustomerMovement();
//        Assert.assertEquals(officeBO.getOfficeId(), customerMovement.getOffice().getOfficeId());
//
//        Assert.assertEquals(officeBO.getOfficeId(), center1.getOffice().getOfficeId());
//        Assert.assertEquals(officeBO.getOfficeId(), group.getOffice().getOfficeId());
//        Assert.assertEquals(officeBO.getOfficeId(), client.getOffice().getOfficeId());
//        Assert.assertEquals(officeBO.getOfficeId(), client1.getOffice().getOfficeId());
//        Assert.assertEquals(officeBO.getOfficeId(), client2.getOffice().getOfficeId());
    }

    @Test
    public void givenSuccessfulTransferToCenterInDifferentBranch_SecondTransfer() throws Exception {
//        createObjectsForTranferToCenterInDifferentBranch();
//        StaticHibernateUtil.closeSession();
//
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        group.setUserContext(TestObjectFactory.getContext());
//        group.transferToCenter(center1);
//        StaticHibernateUtil.commitTransaction();
//        StaticHibernateUtil.closeSession();
//
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        center = TestObjectFactory.getCenter(center.getCustomerId());
//
//        group.setUserContext(TestObjectFactory.getContext());
//        group.transferToCenter(center);
//        StaticHibernateUtil.commitTransaction();
//        StaticHibernateUtil.closeSession();
//
//        center = TestObjectFactory.getCenter(center.getCustomerId());
//        center1 = TestObjectFactory.getCenter(center1.getCustomerId());
//        group = TestObjectFactory.getGroup(group.getCustomerId());
//        group1 = TestObjectFactory.getGroup(group1.getCustomerId());
//        client = TestObjectFactory.getClient(client.getCustomerId());
//        client1 = TestObjectFactory.getClient(client1.getCustomerId());
//        client2 = TestObjectFactory.getClient(client2.getCustomerId());
//        officeBO = new OfficePersistence().getOffice(officeBO.getOfficeId());
//
//        Assert.assertEquals(center.getCustomerId(), group.getParentCustomer().getCustomerId());
//        Assert.assertEquals(1, center.getMaxChildCount().intValue());
//        Assert.assertEquals(1, center1.getMaxChildCount().intValue());
//        Assert.assertEquals(3, group.getMaxChildCount().intValue());
    }

//    private void createGroup(String name) {
//        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(name, CustomerStatus.GROUP_ACTIVE, center);
//    }
//
//    private GroupBO createGroupUnderBranch(String name, CustomerStatus customerStatus) {
//        meeting = getMeeting();
//        return TestObjectFactory.createGroupUnderBranch(name, customerStatus, officeId1, meeting, personnelId);
//    }
//
//    private GroupBO createGroupUnderBranch(String name, CustomerStatus customerStatus,
//            List<CustomFieldView> customFieldView) {
//        meeting = getMeeting();
//        return TestObjectFactory.createGroupUnderBranch(name, customerStatus, officeId1, meeting, personnelId,
//                customFieldView);
//    }
//
//    private void removeFees(List<FeeView> feesToRemove) {
//        for (FeeView fee : feesToRemove) {
//            TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
//        }
//    }
//
//    private List<CustomFieldView> getNewCustomFields() {
//        List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
//        fields.add(new CustomFieldView(Short.valueOf("4"), "value3", CustomFieldType.ALPHA_NUMERIC));
//        fields.add(new CustomFieldView(Short.valueOf("3"), "value4", CustomFieldType.NUMERIC));
//        return fields;
//    }

//    private void createObjectsForTranferToCenterInSameBranch() throws Exception {
//        createInitialObjects();
//        client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
//        client2 = createClient(group, CustomerStatus.CLIENT_CANCELLED);
//        center1 = createCenter("toTransfer", officeId3, WeekDay.THURSDAY);
//        group1 = createGroup("newGroup", center1);
//    }
//
//    private void createObjectsForTranferToCenterInDifferentBranch() throws Exception {
//        createInitialObjects();
//        client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
//        client2 = createClient(group, CustomerStatus.CLIENT_CANCELLED);
//        officeBO = createOffice();
//        center1 = createCenter("toTransfer", officeBO.getOfficeId(), WeekDay.FRIDAY);
//        group1 = createGroup("newGroup", center1);
//    }
//
//    private OfficeBO createOffice() throws Exception {
//        return TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory
//                .getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
//    }

//    private CenterBO createCenter(String name, MeetingBO meeting) {
//        return TestObjectFactory.createWeeklyFeeCenter(name, meeting, officeId3, personnelId);
//    }
//
//    private MeetingBO createMonthlyMeetingOnDate(Short dayNumber, Short recurAfer, Date startDate)
//            throws MeetingException {
//        return new MeetingBO(dayNumber, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
//    }
//
//    private MeetingBO createMonthlyMeetingOnWeekDay(WeekDay weekDay, RankType rank, Short recurAfer, Date startDate)
//            throws MeetingException {
//        return new MeetingBO(weekDay, rank, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
//    }
}