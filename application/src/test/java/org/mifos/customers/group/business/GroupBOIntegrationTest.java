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

package org.mifos.customers.group.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.savings.business.SavingBOTestUtils;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.persistence.GroupPersistence;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupBOIntegrationTest extends MifosIntegrationTestCase {

    public GroupBOIntegrationTest() throws Exception {
        super();
    }

    private AccountBO account1 = null;
    private AccountBO account2 = null;
    private CenterBO center;
    private CenterBO center1 = null;
    private GroupBO group;
    private GroupBO group1;
    private ClientBO client;
    private ClientBO client1 = null;
    private ClientBO client2 = null;
    private MeetingBO meeting;
    private OfficeBO officeBO;
    private final SavingsTestHelper helper = new SavingsTestHelper();
    private SavingsOfferingBO savingsOffering;
    private final Short officeId3 = 3;
    private final Short officeId1 = 1;
    private OfficeBO officeBo1;

    private final Short personnelId = 3;
    private PersonnelBO personnelBo;

    private PersonnelPersistence personnelPersistence = new PersonnelPersistence();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        personnelBo = personnelPersistence.getPersonnel(personnelId);
        officeBo1 = new OfficePersistence().getOffice(officeId1);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(account2);
            TestObjectFactory.cleanUp(account1);
            TestObjectFactory.cleanUp(client1);
            TestObjectFactory.cleanUp(client2);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(group1);
            TestObjectFactory.cleanUp(center);
            TestObjectFactory.cleanUp(center1);
            TestObjectFactory.cleanUp(officeBO);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testSuccessfulCreate_Group_UnderCenter() throws Exception {
        createCenter();
        String name = "GroupTest";
        Date trainedDate = getDate("11/12/2005");
        String externalId = "1234";
        StaticHibernateUtil.closeSession();
        Assert.assertEquals(0, center.getMaxChildCount().intValue());

        group = new GroupBO(TestUtils.makeUser(), name, CustomerStatus.GROUP_ACTIVE, externalId, true, trainedDate,
                getAddress(), getCustomFields(), getFees(), personnelBo, center);
        new GroupPersistence().saveGroup(group);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        center = TestObjectFactory.getCenter(center.getCustomerId());

        Assert.assertEquals(name, group.getDisplayName());
        Assert.assertEquals(externalId, group.getExternalId());
        Assert.assertTrue(group.isTrained());
        Assert.assertEquals(trainedDate, DateUtils.getDateWithoutTimeStamp(group.getTrainedDate().getTime()));
        Assert.assertEquals(CustomerStatus.GROUP_ACTIVE, group.getStatus());
        Address address = group.getCustomerAddressDetail().getAddress();
        Assert.assertEquals("Aditi", address.getLine1());
        Assert.assertEquals("Bangalore", address.getCity());
        Assert.assertEquals(getCustomFields().size(), group.getCustomFields().size());
        Assert.assertEquals(1, center.getMaxChildCount().intValue());
        Assert.assertEquals(center.getPersonnel().getPersonnelId(), group.getPersonnel().getPersonnelId());
        Assert.assertEquals("1.1.1", group.getSearchId());
        Assert.assertEquals(group.getCustomerId(), group.getGroupPerformanceHistory().getGroup().getCustomerId());
        client = TestObjectFactory
                .createClient("new client", CustomerStatus.CLIENT_ACTIVE, group, new java.util.Date());
        Assert.assertEquals(1, group.getGroupPerformanceHistory().getActiveClientCount().intValue());
    }

    public void testSuccessfulCreate_Group_UnderBranch() throws Exception {
        String name = "GroupTest";
        String externalId = "1234";
        group = new GroupBO(TestUtils.makeUser(), name, CustomerStatus.GROUP_ACTIVE, externalId, false, null,
                getAddress(), getCustomFields(), getFees(), personnelBo, officeBo1, getMeeting(), personnelBo);
        new GroupPersistence().saveGroup(group);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());

        Assert.assertEquals(name, group.getDisplayName());
        Assert.assertEquals(externalId, group.getExternalId());
        Assert.assertFalse(group.isTrained());
        Assert.assertEquals(CustomerStatus.GROUP_ACTIVE, group.getStatus());
        Address address = group.getCustomerAddressDetail().getAddress();
        Assert.assertEquals("Aditi", address.getLine1());
        Assert.assertEquals("Bangalore", address.getCity());
        Assert.assertEquals(getCustomFields().size(), group.getCustomFields().size());

        Assert.assertEquals(personnelId, group.getCustomerFormedByPersonnel().getPersonnelId());
        Assert.assertEquals(personnelId, group.getPersonnel().getPersonnelId());
        Assert.assertEquals(officeId1, group.getOffice().getOfficeId());
        Assert.assertNotNull(group.getCustomerMeeting().getMeeting());
        Assert.assertEquals("1.1", group.getSearchId());
    }

    public void testGetTotalOutStandingLoanAmount() throws Exception {
        createInitialObject();
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Assert.assertEquals(new Money(getCurrency(), "600.0"), group.getGroupPerformanceHistory()
                .getTotalOutStandingLoanAmount());
        Assert.assertEquals(new Money(getCurrency(), "600.0"), group.getGroupPerformanceHistory()
                .getTotalOutStandingLoanAmount());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        account1 = TestObjectFactory.getObject(AccountBO.class, account1.getAccountId());
        account2 = TestObjectFactory.getObject(AccountBO.class, account2.getAccountId());
    }

    public void testGetAverageLoanAmount() throws Exception {
        createInitialObject();
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Assert.assertEquals(new Money(getCurrency(), "300.0"), group.getGroupPerformanceHistory()
                .getAvgLoanAmountForMember());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        account1 = TestObjectFactory.getObject(AccountBO.class, account1.getAccountId());
        account2 = TestObjectFactory.getObject(AccountBO.class, account2.getAccountId());
    }

    public void testGetTotalSavingsBalance() throws Exception {
        createInitialObjects();
        SavingsBO savings1 = getSavingsAccount(group, "fsaf6", "ads6");
        SavingBOTestUtils.setBalance(savings1, new Money(getCurrency(), "1000"));

        savings1.update();
        SavingsBO savings2 = getSavingsAccount(client, "fsaf5", "ads5");
        SavingBOTestUtils.setBalance(savings2, new Money(getCurrency(), "2000"));
        savings1.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings1 = TestObjectFactory.getObject(SavingsBO.class, savings1.getAccountId());
        savings2 = TestObjectFactory.getObject(SavingsBO.class, savings2.getAccountId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Assert.assertEquals(new Money(getCurrency(), "1000.0"), savings1.getSavingsBalance());
        Assert.assertEquals(new Money(getCurrency(), "2000.0"), savings2.getSavingsBalance());
        Assert.assertEquals(new Money(getCurrency(), "2000.0"), client.getSavingsBalance(getCurrency()));
        Assert.assertEquals(new Money(getCurrency(), "3000.0"), group.getGroupPerformanceHistory()
                .getTotalSavingsAmount());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        savings1 = TestObjectFactory.getObject(SavingsBO.class, savings1.getAccountId());
        savings2 = TestObjectFactory.getObject(SavingsBO.class, savings2.getAccountId());
        TestObjectFactory.cleanUp(savings1);
        TestObjectFactory.cleanUp(savings2);
    }

    public void testGetActiveOnHoldChildrenOfGroup() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        client1 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_HOLD, group);
        client2 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_CANCELLED, group);
        Assert.assertEquals(Integer.valueOf("2"), group.getGroupPerformanceHistory().getActiveClientCount());
    }

    public void testUpdateMeeting_SavedToUpdateLater() throws Exception {
        String oldMeetingPlace = "Delhi";
        MeetingBO weeklyMeeting = new MeetingBO(WeekDay.FRIDAY, Short.valueOf("1"), new java.util.Date(),
                MeetingType.CUSTOMER_MEETING, oldMeetingPlace);
        group = TestObjectFactory.createGroupUnderBranch("group1", CustomerStatus.GROUP_ACTIVE, officeId3,
                weeklyMeeting, personnelId);

        client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
        client2 = createClient(group, CustomerStatus.CLIENT_ACTIVE);

        StaticHibernateUtil.closeSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        MeetingBO groupMeeting = group.getCustomerMeeting().getMeeting();

        String meetingPlace = "Bangalore";
        MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, groupMeeting.getMeetingDetails().getRecurAfter(),
                groupMeeting.getStartDate(), MeetingType.CUSTOMER_MEETING, meetingPlace);

        group.updateMeeting(newMeeting);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        client1 = TestObjectFactory.getClient(client1.getCustomerId());
        client2 = TestObjectFactory.getClient(client2.getCustomerId());

        Assert.assertEquals(WeekDay.FRIDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
        Assert.assertEquals(WeekDay.FRIDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
        Assert.assertEquals(WeekDay.FRIDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());

        Assert.assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
                .getWeekDay());
        Assert.assertEquals(WeekDay.THURSDAY, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
                .getWeekDay());
        Assert.assertEquals(WeekDay.THURSDAY, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
                .getWeekDay());

        Assert.assertEquals(oldMeetingPlace, group.getCustomerMeeting().getMeeting().getMeetingPlace());
        Assert.assertEquals(oldMeetingPlace, client1.getCustomerMeeting().getMeeting().getMeetingPlace());
        Assert.assertEquals(oldMeetingPlace, client2.getCustomerMeeting().getMeeting().getMeetingPlace());

        Assert.assertEquals(meetingPlace, group.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
        Assert.assertEquals(meetingPlace, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
        Assert.assertEquals(meetingPlace, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());

        Assert.assertEquals(YesNoFlag.YES.getValue(), group.getCustomerMeeting().getUpdatedFlag());
        Assert.assertEquals(YesNoFlag.YES.getValue(), client1.getCustomerMeeting().getUpdatedFlag());
        Assert.assertEquals(YesNoFlag.YES.getValue(), client2.getCustomerMeeting().getUpdatedFlag());

        Integer groupUpdateMeetingId = group.getCustomerMeeting().getUpdatedMeeting().getMeetingId();
        Assert.assertEquals(groupUpdateMeetingId, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingId());
        Assert.assertEquals(groupUpdateMeetingId, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingId());
    }

    public void testUpdateMeeting() throws Exception {
        StaticHibernateUtil.startTransaction();
        group = createGroupUnderBranch(CustomerStatus.GROUP_PENDING);
        client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
        client2 = createClient(group, CustomerStatus.CLIENT_PENDING);

        MeetingBO groupMeeting = group.getCustomerMeeting().getMeeting();
        String oldMeetingPlace = "Delhi";
        String meetingPlace = "Bangalore";
        MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, groupMeeting.getMeetingDetails().getRecurAfter(),
                groupMeeting.getStartDate(), MeetingType.CUSTOMER_MEETING, meetingPlace);
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        group.updateMeeting(newMeeting);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        client1 = TestObjectFactory.getClient(client1.getCustomerId());
        client2 = TestObjectFactory.getClient(client2.getCustomerId());

        Assert.assertEquals(WeekDay.MONDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
        Assert.assertEquals(WeekDay.MONDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
        Assert.assertEquals(WeekDay.MONDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());

        Assert.assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
                .getWeekDay());
        Assert.assertEquals(WeekDay.THURSDAY, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
                .getWeekDay());
        Assert.assertEquals(WeekDay.THURSDAY, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
                .getWeekDay());

        Assert.assertEquals(oldMeetingPlace, group.getCustomerMeeting().getMeeting().getMeetingPlace());
        Assert.assertEquals(oldMeetingPlace, client1.getCustomerMeeting().getMeeting().getMeetingPlace());
        Assert.assertEquals(oldMeetingPlace, client2.getCustomerMeeting().getMeeting().getMeetingPlace());

        Assert.assertEquals(meetingPlace, group.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
        Assert.assertEquals(meetingPlace, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
        Assert.assertEquals(meetingPlace, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
    }

    public void testCreateMeeting() throws Exception {
        group = createGroupUnderBranchWithoutMeeting("MyGroup");
        client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
        client2 = createClient(group, CustomerStatus.CLIENT_PENDING);
        StaticHibernateUtil.closeSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        group.setUserContext(TestObjectFactory.getContext());
        String meetingPlace = "newPlace";
        Short recurAfter = Short.valueOf("4");
        MeetingBO newMeeting = new MeetingBO(WeekDay.FRIDAY, recurAfter, new Date(), MeetingType.CUSTOMER_MEETING,
                meetingPlace);
        group.updateMeeting(newMeeting);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        client1 = TestObjectFactory.getClient(client1.getCustomerId());
        client2 = TestObjectFactory.getClient(client2.getCustomerId());

        Assert.assertEquals(WeekDay.FRIDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
        Assert.assertEquals(WeekDay.FRIDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
        Assert.assertEquals(WeekDay.FRIDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());

        Assert.assertEquals(meetingPlace, group.getCustomerMeeting().getMeeting().getMeetingPlace());
        Assert.assertEquals(meetingPlace, client1.getCustomerMeeting().getMeeting().getMeetingPlace());
        Assert.assertEquals(meetingPlace, client2.getCustomerMeeting().getMeeting().getMeetingPlace());

        Assert.assertEquals(recurAfter, group.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
        Assert.assertEquals(recurAfter, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
        Assert.assertEquals(recurAfter, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
    }

    public void testFailureCreate_Group_UnderCenter() throws Exception {
        createCenter();
        String name = "GroupTest";
        Date trainedDate = getDate("11/12/2005");
        String externalId = "1234";
        StaticHibernateUtil.closeSession();

        try {
            group = new GroupBO(TestUtils.makeUser(), name, CustomerStatus.GROUP_ACTIVE, externalId, true, trainedDate,
                    getAddress(), null, null, personnelBo, center);
            TestObjectFactory.simulateInvalidConnection();
            new GroupPersistence().saveGroup(group);
            Assert.fail();
        } catch (CustomerException ce) {
            Assert.assertEquals("Customer.CreateFailed", ce.getKey());
        } finally {
            group = null;
            StaticHibernateUtil.closeSession();
        }
    }


    public void testGeneratePortfolioAtRisk() throws Exception {
        createInitialObject();
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        for (AccountBO account : group.getAccounts()) {
            if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
                changeFirstInstallmentDate(account, 31);
            }
        }
        for (AccountBO account : client.getAccounts()) {
            if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
                changeFirstInstallmentDate(account, 31);
            }
        }
        group.getGroupPerformanceHistory().generatePortfolioAtRisk();
        Assert.assertEquals(new Money(getCurrency(), "1.0"), group.getGroupPerformanceHistory().getPortfolioAtRisk());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        account1 = TestObjectFactory.getObject(AccountBO.class, account1.getAccountId());
        account2 = TestObjectFactory.getObject(AccountBO.class, account2.getAccountId());
    }

    public void testChangeUpdatedMeeting() throws Exception {
        String oldMeetingPlace = "Delhi";
        MeetingBO weeklyMeeting = new MeetingBO(WeekDay.FRIDAY, Short.valueOf("1"), new java.util.Date(),
                MeetingType.CUSTOMER_MEETING, oldMeetingPlace);
        group = TestObjectFactory.createGroupUnderBranch("group1", CustomerStatus.GROUP_ACTIVE, officeId3,
                weeklyMeeting, personnelId);

        client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
        client2 = createClient(group, CustomerStatus.CLIENT_ACTIVE);

        StaticHibernateUtil.closeSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        MeetingBO groupMeeting = group.getCustomerMeeting().getMeeting();

        String meetingPlace = "Bangalore";
        MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, groupMeeting.getMeetingDetails().getRecurAfter(),
                groupMeeting.getStartDate(), MeetingType.CUSTOMER_MEETING, meetingPlace);

        group.updateMeeting(newMeeting);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        client1 = TestObjectFactory.getClient(client1.getCustomerId());
        client2 = TestObjectFactory.getClient(client2.getCustomerId());

        Assert.assertEquals(WeekDay.FRIDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
        Assert.assertEquals(WeekDay.FRIDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
        Assert.assertEquals(WeekDay.FRIDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());

        Assert.assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
                .getWeekDay());
        Assert.assertEquals(WeekDay.THURSDAY, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
                .getWeekDay());
        Assert.assertEquals(WeekDay.THURSDAY, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails()
                .getWeekDay());

        Integer updatedMeetingId = group.getCustomerMeeting().getUpdatedMeeting().getMeetingId();

        client1.changeUpdatedMeeting();
        group.changeUpdatedMeeting();

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        client1 = TestObjectFactory.getClient(client1.getCustomerId());
        client2 = TestObjectFactory.getClient(client2.getCustomerId());

        Assert.assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
        Assert.assertEquals(WeekDay.THURSDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails()
                .getWeekDay());
        Assert.assertEquals(WeekDay.THURSDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails()
                .getWeekDay());

        Assert.assertNull(group.getCustomerMeeting().getUpdatedMeeting());
        Assert.assertNull(client1.getCustomerMeeting().getUpdatedMeeting());
        Assert.assertNull(client2.getCustomerMeeting().getUpdatedMeeting());

        MeetingBO meeting = new MeetingPersistence().getMeeting(updatedMeetingId);
        Assert.assertNull(meeting);
    }

    // FIXME - keithw - change of status is pushed out of domain model
    public void ignore_testChangeStatus_UpdatePendingClientToPartial_OnGroupCancelled() throws Exception {
        group = TestObjectFactory.createGroupUnderBranch("MyGroup", CustomerStatus.GROUP_PENDING, Short.valueOf("3"),
                meeting, personnelId, null);
        client1 = createClient(group, CustomerStatus.CLIENT_PENDING);
        client2 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        group.setUserContext(TestObjectFactory.getContext());

     // FIXME - keithw - use builder for creation of client for tests in given state.
//        group.changeStatus(CustomerStatus.GROUP_CANCELLED, null, "Group Cancelled");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        client1 = TestObjectFactory.getClient(client1.getCustomerId());
        client2 = TestObjectFactory.getClient(client2.getCustomerId());

        Assert.assertEquals(CustomerStatus.GROUP_CANCELLED, group.getStatus());
        Assert.assertEquals(CustomerStatus.CLIENT_PARTIAL, client1.getStatus());
        Assert.assertEquals(CustomerStatus.CLIENT_PARTIAL, client2.getStatus());
    }

    private GroupBO createGroupUnderBranchWithoutMeeting(String name) {
        return TestObjectFactory.createGroupUnderBranch(name, CustomerStatus.GROUP_PENDING, officeId1, null,
                personnelId);
    }

    private void createCenter() {
        meeting = getMeeting();
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
    }

    private CenterBO createCenter(String name) throws Exception {
        return createCenter(name, officeId3, WeekDay.MONDAY);
    }

    private CenterBO createCenter(String name, Short officeId, WeekDay weekDay) throws Exception {
        meeting = new MeetingBO(weekDay, Short.valueOf("1"), new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        return TestObjectFactory.createWeeklyFeeCenter(name, meeting, officeId, personnelId);
    }

    private GroupBO createGroupUnderBranch(CustomerStatus groupStatus) throws Exception {
        return createGroupUnderBranch(groupStatus, officeId3);
    }

    private GroupBO createGroupUnderBranch(CustomerStatus groupStatus, Short officeId) throws Exception {
        meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        return TestObjectFactory.createGroupUnderBranchWithMakeUser("group1", groupStatus, officeId, meeting,
                personnelId);
    }

    private MeetingBO getMeeting() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return meeting;
    }

    private List<CustomFieldView> getCustomFields() {
        List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
        fields.add(new CustomFieldView(Short.valueOf("4"), "value1", CustomFieldType.ALPHA_NUMERIC));
        fields.add(new CustomFieldView(Short.valueOf("3"), "value2", CustomFieldType.NUMERIC));
        return fields;
    }

    private Address getAddress() {
        Address address = new Address();
        address.setLine1("Aditi");
        address.setCity("Bangalore");
        return address;
    }

    private List<FeeView> getFees() {
        List<FeeView> fees = new ArrayList<FeeView>();
        AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory.createPeriodicAmountFee("PeriodicAmountFee",
                FeeCategory.GROUP, "200", RecurrenceType.WEEKLY, Short.valueOf("2"));
        AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory.createOneTimeAmountFee("OneTimeAmountFee",
                FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
        fees.add(new FeeView(TestObjectFactory.getContext(), fee1));
        fees.add(new FeeView(TestObjectFactory.getContext(), fee2));
        StaticHibernateUtil.commitTransaction();
        return fees;
    }

    private ClientBO createClient(GroupBO group, CustomerStatus clientStatus) {
        return TestObjectFactory.createClient("client1", clientStatus, group, new Date());
    }

    private GroupBO createGroup(String name, CenterBO center) {
        return TestObjectFactory.createWeeklyFeeGroupUnderCenter(name, CustomerStatus.GROUP_ACTIVE, center);
    }

    private void createInitialObjects() throws Exception {
        center = createCenter("Center");
        group = createGroup("Group", center);
        client = createClient(group, CustomerStatus.CLIENT_ACTIVE);
    }

    private SavingsBO getSavingsAccount(CustomerBO customerBO, String offeringName, String shortName) throws Exception {
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", customerBO,
                AccountStates.SAVINGS_ACC_APPROVED, new Date(System.currentTimeMillis()), savingsOffering);
    }

    private void createInitialObject() {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loandsdasd", "fsad", startDate, meeting);
        account1 = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        loanOffering = TestObjectFactory.createLoanOffering("Loandfas", "dsvd", ApplicableTo.CLIENTS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, meeting);
        account2 = TestObjectFactory.createLoanAccount("42427777341", client,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);
    }

    private void changeFirstInstallmentDate(AccountBO accountBO, int numberOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - numberOfDays);
        for (AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()) {
            LoanBOTestUtils.setActionDate(accountActionDateEntity, new java.sql.Date(currentDateCalendar
                    .getTimeInMillis()));
            break;
        }
    }
}
