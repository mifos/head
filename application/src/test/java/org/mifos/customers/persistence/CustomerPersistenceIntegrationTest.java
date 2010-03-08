/*
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

package org.mifos.customers.persistence;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.customers.checklist.business.CheckListBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.checklist.util.helpers.CheckListConstants;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerBOTestUtils;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.business.CustomerPerformanceHistoryView;
import org.mifos.customers.business.CustomerSearch;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.CustomerView;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.AttendanceType;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.group.BasicGroupInfo;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.servicefacade.CollectionSheetCustomerDto;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.AccountingRulesConstants;
import org.mifos.config.ConfigurationManager;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public CustomerPersistenceIntegrationTest() throws Exception {
        super();
    }

    private MeetingBO meeting;

    private CustomerBO center;

    private ClientBO client;

    private CustomerBO group2;

    private CustomerBO group;

    private AccountBO account;

    private LoanBO groupAccount;

    private LoanBO clientAccount;

    private SavingsBO centerSavingsAccount;

    private SavingsBO groupSavingsAccount;

    private SavingsBO clientSavingsAccount;

    private SavingsOfferingBO savingsOffering;

    private final CustomerPersistence customerPersistence = new CustomerPersistence();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(centerSavingsAccount);
            TestObjectFactory.cleanUp(groupSavingsAccount);
            TestObjectFactory.cleanUp(clientSavingsAccount);
            TestObjectFactory.cleanUp(groupAccount);
            TestObjectFactory.cleanUp(clientAccount);
            TestObjectFactory.cleanUp(account);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group2);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
            StaticHibernateUtil.closeSession();
        } catch (Exception e) {
            // Throwing from tearDown will tend to mask the real failure.
            e.printStackTrace();
        }
        super.tearDown();
    }

    public void testGetTotalAmountForAllClientsOfGroupForSingleCurrency() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = createCenter("new_center");
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);

        AccountBO clientAccount1 = getLoanAccount(client, meeting, "fdbdhgsgh", "54hg", TestUtils.RUPEE);
        AccountBO clientAccount2 = getLoanAccount(client, meeting, "fasdfdsfasdf", "1qwe", TestUtils.RUPEE);
        Money amount = customerPersistence.getTotalAmountForAllClientsOfGroup(group.getOffice().getOfficeId(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, group.getSearchId() + ".%");
        Assert.assertEquals(new Money(TestUtils.RUPEE, "600"), amount);

        TestObjectFactory.cleanUp(clientAccount1);
        TestObjectFactory.cleanUp(clientAccount2);

    }

    /*
     * When trying to sum amounts across loans with different currencies, we should get an exception
     */
    public void testGetTotalAmountForAllClientsOfGroupForMultipleCurrencies() throws Exception {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES, TestUtils.EURO.getCurrencyCode());

        AccountBO clientAccount1;
        AccountBO clientAccount2;

        try {
            MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
            center = createCenter("new_center");
            group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
            client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);

            clientAccount1 = getLoanAccount(client, meeting, "fdbdhgsgh", "54hg", TestUtils.RUPEE);
            clientAccount2 = getLoanAccount(client, meeting, "fasdfdsfasdf", "1qwe", TestUtils.EURO);

            try {
                customerPersistence.getTotalAmountForAllClientsOfGroup(group.getOffice().getOfficeId(),
                        AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, group.getSearchId() + ".%");
                fail("didn't get the expected CurrencyMismatchException");
            } catch (CurrencyMismatchException e) {
                // if we got here then we got the exception we were expecting
                assertNotNull(e);
            } catch (Exception e) {
                fail("didn't get the expected CurrencyMismatchException");
            }
        } finally {
            configMgr.clearProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES);
        }

        TestObjectFactory.cleanUp(clientAccount1);
        TestObjectFactory.cleanUp(clientAccount2);

    }

    /*
     * When trying to sum amounts across loans with different currencies, we should get an exception
     */
    public void testGetTotalAmountForGroupForMultipleCurrencies() throws Exception {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES, TestUtils.EURO.getCurrencyCode());

        GroupBO group1;
        AccountBO account1;
        AccountBO account2;

        try {
            CustomerPersistence customerPersistence = new CustomerPersistence();
            meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                    CUSTOMER_MEETING));
            center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
            group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
            account1 = getLoanAccount(group1, meeting, "adsfdsfsd", "3saf", TestUtils.RUPEE);
            account2 = getLoanAccount(group1, meeting, "adspp", "kkaf", TestUtils.EURO);

            try {
                customerPersistence.getTotalAmountForGroup(group1.getCustomerId(),
                        AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);
                fail("didn't get the expected CurrencyMismatchException");
            } catch (CurrencyMismatchException e) {
                // if we got here then we got the exception we were expecting
                assertNotNull(e);
            } catch (Exception e) {
                fail("didn't get the expected CurrencyMismatchException");
            }
        } finally {
            configMgr.clearProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES);
        }

        TestObjectFactory.cleanUp(account1);
        TestObjectFactory.cleanUp(account2);
        TestObjectFactory.cleanUp(group1);
    }

    public void testGetTotalAmountForGroup() throws Exception {
        CustomerPersistence customerPersistence = new CustomerPersistence();
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        GroupBO group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        AccountBO account1 = getLoanAccount(group1, meeting, "adsfdsfsd", "3saf");
        AccountBO account2 = getLoanAccount(group1, meeting, "adspp", "kkaf");
        Money amount = customerPersistence.getTotalAmountForGroup(group1.getCustomerId(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);
       Assert.assertEquals(new Money(getCurrency(), "600"), amount);
        AccountBO account3 = getLoanAccountInActiveBadStanding(group1, meeting, "adsfdsfsd1", "4sa");
        AccountBO account4 = getLoanAccountInActiveBadStanding(group1, meeting, "adspp2", "kaf5");
        Money amount2 = customerPersistence.getTotalAmountForGroup(group1.getCustomerId(),
                AccountState.LOAN_ACTIVE_IN_BAD_STANDING);
       Assert.assertEquals(new Money(getCurrency(), "600"), amount2);

        TestObjectFactory.cleanUp(account1);
        TestObjectFactory.cleanUp(account2);
        TestObjectFactory.cleanUp(account3);
        TestObjectFactory.cleanUp(account4);
        TestObjectFactory.cleanUp(group1);
    }

    public void testGetTotalAmountForAllClientsOfGroup() throws Exception {

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = createCenter("new_center");
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);

        AccountBO clientAccount1 = getLoanAccount(client, meeting, "fdbdhgsgh", "54hg");
        AccountBO clientAccount2 = getLoanAccount(client, meeting, "fasdfdsfasdf", "1qwe");
        Money amount = customerPersistence.getTotalAmountForAllClientsOfGroup(group.getOffice().getOfficeId(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, group.getSearchId() + ".%");
       Assert.assertEquals(new Money(getCurrency(), "600"), amount);
        clientAccount1.changeStatus(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue(), null, "none");
        clientAccount2.changeStatus(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue(), null, "none");
        TestObjectFactory.updateObject(clientAccount1);
        TestObjectFactory.updateObject(clientAccount2);
        StaticHibernateUtil.commitTransaction();
        Money amount2 = customerPersistence.getTotalAmountForAllClientsOfGroup(group.getOffice().getOfficeId(),
                AccountState.LOAN_ACTIVE_IN_BAD_STANDING, group.getSearchId() + ".%");
       Assert.assertEquals(new Money(getCurrency(), "600"), amount2);

        TestObjectFactory.cleanUp(clientAccount1);
        TestObjectFactory.cleanUp(clientAccount2);

    }

    public void testGetAllBasicGroupInfo() throws Exception {
        CustomerPersistence customerPersistence = new CustomerPersistence();
        center = createCenter("new_center");
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        GroupBO newGroup = TestObjectFactory.createWeeklyFeeGroupUnderCenter("newGroup", CustomerStatus.GROUP_HOLD, center);
        GroupBO newGroup2 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("newGroup2", CustomerStatus.GROUP_CANCELLED,
                center);
        GroupBO newGroup3 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("newGroup3", CustomerStatus.GROUP_CLOSED, center);
        GroupBO newGroup4 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("newGroup4", CustomerStatus.GROUP_PARTIAL, center);
        GroupBO newGroup5 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("newGroup5", CustomerStatus.GROUP_PENDING, center);
        List<BasicGroupInfo> groupInfos = customerPersistence.getAllBasicGroupInfo();
        Assert.assertEquals(2, groupInfos.size());
        Assert.assertEquals(group.getDisplayName(), groupInfos.get(0).getGroupName());
        Assert.assertEquals(group.getSearchId(), groupInfos.get(0).getSearchId());
        Assert.assertEquals(group.getOffice().getOfficeId(), groupInfos.get(0).getBranchId());
        Assert.assertEquals(group.getCustomerId(), groupInfos.get(0).getGroupId());
        Assert.assertEquals(newGroup.getDisplayName(), groupInfos.get(1).getGroupName());
        Assert.assertEquals(newGroup.getSearchId(), groupInfos.get(1).getSearchId());
        Assert.assertEquals(newGroup.getOffice().getOfficeId(), groupInfos.get(1).getBranchId());
        Assert.assertEquals(newGroup.getCustomerId(), groupInfos.get(1).getGroupId());
        TestObjectFactory.cleanUp(newGroup);
        TestObjectFactory.cleanUp(newGroup2);
        TestObjectFactory.cleanUp(newGroup3);
        TestObjectFactory.cleanUp(newGroup4);
        TestObjectFactory.cleanUp(newGroup5);

    }

    public void testCustomersUnderLO() throws Exception {
        CustomerPersistence customerPersistence = new CustomerPersistence();
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active", meeting);
        List<CustomerView> customers = customerPersistence.getActiveParentList(Short.valueOf("1"), CustomerLevel.CENTER
                .getValue(), Short.valueOf("3"));
        Assert.assertEquals(1, customers.size());

    }

    public void testActiveCustomersUnderParent() throws Exception {
        CustomerPersistence customerPersistence = new CustomerPersistence();
        createCustomers(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        List<CustomerView> customers = customerPersistence.getChildrenForParent(center.getCustomerId(), center
                .getSearchId(), center.getOffice().getOfficeId());
        Assert.assertEquals(2, customers.size());
    }

    public void testOnHoldCustomersUnderParent() throws Exception {
        CustomerPersistence customerPersistence = new CustomerPersistence();
        createCustomers(CustomerStatus.GROUP_HOLD, CustomerStatus.CLIENT_HOLD);
        List<CustomerView> customers = customerPersistence.getChildrenForParent(center.getCustomerId(), center
                .getSearchId(), center.getOffice().getOfficeId());
        Assert.assertEquals(2, customers.size());
    }

    public void testGetLastMeetingDateForCustomer() throws Exception {
        CustomerPersistence customerPersistence = new CustomerPersistence();
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        account = getLoanAccount(group, meeting, "adsfdsfsd", "3saf");
        // Date actionDate = new Date(2006,03,13);
        Date meetingDate = customerPersistence.getLastMeetingDateForCustomer(center.getCustomerId());
        Assert.assertEquals(new Date(getMeetingDates(meeting).getTime()).toString(), meetingDate.toString());

    }

    public void testGetChildernOtherThanClosed() throws Exception {
        CustomerPersistence customerPersistence = new CustomerPersistence();
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_CLOSED, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_CANCELLED, group);
        ClientBO client4 = TestObjectFactory.createClient("client4", CustomerStatus.CLIENT_PENDING, group);

        List<CustomerBO> customerList = customerPersistence.getChildren(center.getSearchId(), center.getOffice()
                .getOfficeId(), CustomerLevel.CLIENT, ChildrenStateType.OTHER_THAN_CLOSED);
        Assert.assertEquals(new Integer("3").intValue(), customerList.size());

        for (CustomerBO customer : customerList) {
            if (customer.getCustomerId().intValue() == client3.getCustomerId().intValue()) {
                Assert.assertTrue(true);
            }
        }
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client4);
    }

    public void testGetChildernActiveAndHold() throws Exception {
        CustomerPersistence customerPersistence = new CustomerPersistence();
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_PARTIAL, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_PENDING, group);
        ClientBO client4 = TestObjectFactory.createClient("client4", CustomerStatus.CLIENT_HOLD, group);

        List<CustomerBO> customerList = customerPersistence.getChildren(center.getSearchId(), center.getOffice()
                .getOfficeId(), CustomerLevel.CLIENT, ChildrenStateType.ACTIVE_AND_ONHOLD);
        Assert.assertEquals(new Integer("2").intValue(), customerList.size());

        for (CustomerBO customer : customerList) {
            if (customer.getCustomerId().intValue() == client.getCustomerId().intValue()) {
                Assert.assertTrue(true);
            }
            if (customer.getCustomerId().intValue() == client4.getCustomerId().intValue()) {
                Assert.assertTrue(true);
            }
        }
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client4);
    }

    public void testGetChildernOtherThanClosedAndCancelled() throws Exception {
        CustomerPersistence customerPersistence = new CustomerPersistence();
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_CLOSED, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_CANCELLED, group);
        ClientBO client4 = TestObjectFactory.createClient("client4", CustomerStatus.CLIENT_PENDING, group);

        List<CustomerBO> customerList = customerPersistence.getChildren(center.getSearchId(), center.getOffice()
                .getOfficeId(), CustomerLevel.CLIENT, ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED);
        Assert.assertEquals(new Integer("2").intValue(), customerList.size());

        for (CustomerBO customer : customerList) {
            if (customer.getCustomerId().equals(client4.getCustomerId())) {
                Assert.assertTrue(true);
            }
        }
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client4);
    }

    public void testGetAllChildern() throws Exception {
        CustomerPersistence customerPersistence = new CustomerPersistence();
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_CLOSED, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_CANCELLED, group);
        ClientBO client4 = TestObjectFactory.createClient("client4", CustomerStatus.CLIENT_PENDING, group);

        List<CustomerBO> customerList = customerPersistence.getChildren(center.getSearchId(), center.getOffice()
                .getOfficeId(), CustomerLevel.CLIENT, ChildrenStateType.ALL);
        Assert.assertEquals(new Integer("4").intValue(), customerList.size());

        for (CustomerBO customer : customerList) {
            if (customer.getCustomerId().equals(client2.getCustomerId())) {
                Assert.assertTrue(true);
            }
        }
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client4);
    }

    public void testRetrieveSavingsAccountForCustomer() throws Exception {
        java.util.Date currentDate = new java.util.Date();

        CustomerPersistence customerPersistence = new CustomerPersistence();
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", "S", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        UserContext user = new UserContext();
        user.setId(PersonnelConstants.SYSTEM_USER);
        account = TestObjectFactory.createSavingsAccount("000100000000020", group, AccountState.SAVINGS_ACTIVE,
                currentDate, savingsOffering, user);
        StaticHibernateUtil.closeSession();
        List<SavingsBO> savingsList = customerPersistence.retrieveSavingsAccountForCustomer(group.getCustomerId());
        Assert.assertEquals(1, savingsList.size());
        account = savingsList.get(0);
        group = account.getCustomer();
        center = group.getParentCustomer();
    }

    public void testNumberOfMeetingsAttended() throws Exception {
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);

        client.handleAttendance(new Date(System.currentTimeMillis()), AttendanceType.ABSENT);

        client.handleAttendance(new Date(System.currentTimeMillis()), AttendanceType.PRESENT);

        Calendar currentDate = new GregorianCalendar();
        currentDate.roll(Calendar.DATE, 1);

        client.handleAttendance(new Date(currentDate.getTimeInMillis()), AttendanceType.LATE);

        StaticHibernateUtil.commitTransaction();

        CustomerPerformanceHistoryView customerPerformanceHistoryView = customerPersistence.numberOfMeetings(true,
                client.getCustomerId());
        Assert.assertEquals(2, customerPerformanceHistoryView.getMeetingsAttended().intValue());

        StaticHibernateUtil.closeSession();
    }

    public void testNumberOfMeetingsMissed() throws Exception {
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);

        client.handleAttendance(new Date(System.currentTimeMillis()), AttendanceType.PRESENT);
        client.handleAttendance(new Date(System.currentTimeMillis()), AttendanceType.ABSENT);

        Calendar currentDate = new GregorianCalendar();
        currentDate.roll(Calendar.DATE, 1);

        client.handleAttendance(new Date(currentDate.getTimeInMillis()), AttendanceType.APPROVED_LEAVE);

        StaticHibernateUtil.commitTransaction();

        CustomerPerformanceHistoryView customerPerformanceHistoryView = customerPersistence.numberOfMeetings(false,
                client.getCustomerId());
        Assert.assertEquals(2, customerPerformanceHistoryView.getMeetingsMissed().intValue());
        StaticHibernateUtil.closeSession();
    }

    public void testLastLoanAmount() throws PersistenceException, AccountException {
        Date startDate = new Date(System.currentTimeMillis());
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, center.getCustomerMeeting()
                .getMeeting());
        LoanBO loanBO = TestObjectFactory.createLoanAccount("42423142341", client,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        account = (AccountBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, loanBO.getAccountId());
        AccountStateEntity accountStateEntity = new AccountStateEntity(AccountState.LOAN_CLOSED_OBLIGATIONS_MET);
        account.setUserContext(TestObjectFactory.getContext());
        account.changeStatus(accountStateEntity.getId(), null, "");
        TestObjectFactory.updateObject(account);
        CustomerPersistence customerPersistence = new CustomerPersistence();
        CustomerPerformanceHistoryView customerPerformanceHistoryView = customerPersistence.getLastLoanAmount(client
                .getCustomerId());
        Assert.assertEquals("300.0", customerPerformanceHistoryView.getLastLoanAmount());
    }

    public void testFindBySystemId() throws Exception {
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        GroupBO groupBO = (GroupBO) customerPersistence.findBySystemId(group.getGlobalCustNum());
        Assert.assertEquals(groupBO.getDisplayName(), group.getDisplayName());
    }

    public void testGetBySystemId() throws Exception {
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        GroupBO groupBO = (GroupBO) customerPersistence.findBySystemId(group.getGlobalCustNum(), group
                .getCustomerLevel().getId());
        Assert.assertEquals(groupBO.getDisplayName(), group.getDisplayName());
    }

    public void testOptionalCustomerStates() throws Exception {
        Assert.assertEquals(Integer.valueOf(0).intValue(), customerPersistence.getCustomerStates(Short.valueOf("0"))
                .size());
    }

    public void testCustomerStatesInUse() throws Exception {
        Assert.assertEquals(Integer.valueOf(14).intValue(), customerPersistence.getCustomerStates(Short.valueOf("1"))
                .size());
    }

    public void testGetCustomersWithUpdatedMeetings() throws Exception {
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        CustomerBOTestUtils.setUpdatedFlag(group.getCustomerMeeting(), YesNoFlag.YES.getValue());
        TestObjectFactory.updateObject(group);
        List<Integer> customerIds = customerPersistence.getCustomersWithUpdatedMeetings();
        Assert.assertEquals(1, customerIds.size());

    }

    public void testRetrieveAllLoanAccountUnderCustomer() throws PersistenceException {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = createCenter("center");
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        CenterBO center1 = createCenter("center1");
        GroupBO group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_ACTIVE, group1);
        account = getLoanAccount(group, meeting, "cdfggdfs", "1qdd");
        AccountBO account1 = getLoanAccount(client, meeting, "fdbdhgsgh", "54hg");
        AccountBO account2 = getLoanAccount(client2, meeting, "fasdfdsfasdf", "1qwe");
        AccountBO account3 = getLoanAccount(client3, meeting, "fdsgdfgfd", "543g");
        AccountBO account4 = getLoanAccount(group1, meeting, "fasdf23", "3fds");
        CustomerBOTestUtils.setCustomerStatus(client2, new CustomerStatusEntity(CustomerStatus.CLIENT_CLOSED));

        TestObjectFactory.updateObject(client2);
        client2 = TestObjectFactory.getClient(client2.getCustomerId());
        CustomerBOTestUtils.setCustomerStatus(client3, new CustomerStatusEntity(CustomerStatus.CLIENT_CANCELLED));
        TestObjectFactory.updateObject(client3);
        client3 = TestObjectFactory.getClient(client3.getCustomerId());

        List<AccountBO> loansForCenter = customerPersistence.retrieveAccountsUnderCustomer(center.getSearchId(), Short
                .valueOf("3"), Short.valueOf("1"));
        Assert.assertEquals(3, loansForCenter.size());
        List<AccountBO> loansForGroup = customerPersistence.retrieveAccountsUnderCustomer(group.getSearchId(), Short
                .valueOf("3"), Short.valueOf("1"));
        Assert.assertEquals(3, loansForGroup.size());
        List<AccountBO> loansForClient = customerPersistence.retrieveAccountsUnderCustomer(client.getSearchId(), Short
                .valueOf("3"), Short.valueOf("1"));
        Assert.assertEquals(1, loansForClient.size());

        TestObjectFactory.cleanUp(account4);
        TestObjectFactory.cleanUp(account3);
        TestObjectFactory.cleanUp(account2);
        TestObjectFactory.cleanUp(account1);
        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(group1);
        TestObjectFactory.cleanUp(center1);
    }

    public void testRetrieveAllSavingsAccountUnderCustomer() throws Exception {
        center = createCenter("new_center");
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        CenterBO center1 = createCenter("new_center1");
        GroupBO group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_CLOSED, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_CANCELLED, group1);
        account = getSavingsAccount(center, "Savings Prd1", "Abc1");
        AccountBO account1 = getSavingsAccount(client, "Savings Prd2", "Abc2");
        AccountBO account2 = getSavingsAccount(client2, "Savings Prd3", "Abc3");
        AccountBO account3 = getSavingsAccount(client3, "Savings Prd4", "Abc4");
        AccountBO account4 = getSavingsAccount(group1, "Savings Prd5", "Abc5");
        AccountBO account5 = getSavingsAccount(group, "Savings Prd6", "Abc6");
        AccountBO account6 = getSavingsAccount(center1, "Savings Prd7", "Abc7");

        List<AccountBO> savingsForCenter = customerPersistence.retrieveAccountsUnderCustomer(center.getSearchId(),
                Short.valueOf("3"), Short.valueOf("2"));
        Assert.assertEquals(4, savingsForCenter.size());
        List<AccountBO> savingsForGroup = customerPersistence.retrieveAccountsUnderCustomer(group.getSearchId(), Short
                .valueOf("3"), Short.valueOf("2"));
        Assert.assertEquals(3, savingsForGroup.size());
        List<AccountBO> savingsForClient = customerPersistence.retrieveAccountsUnderCustomer(client.getSearchId(),
                Short.valueOf("3"), Short.valueOf("2"));
        Assert.assertEquals(1, savingsForClient.size());

        TestObjectFactory.cleanUp(account3);
        TestObjectFactory.cleanUp(account2);
        TestObjectFactory.cleanUp(account1);
        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(account4);
        TestObjectFactory.cleanUp(account5);
        TestObjectFactory.cleanUp(group1);
        TestObjectFactory.cleanUp(account6);
        TestObjectFactory.cleanUp(center1);
    }

    public void testGetAllChildrenForParent() throws NumberFormatException, PersistenceException {
        center = createCenter("Center");
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        CenterBO center1 = createCenter("center11");
        GroupBO group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_CLOSED, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_CANCELLED, group1);

        List<CustomerBO> customerList1 = customerPersistence.getAllChildrenForParent(center.getSearchId(), Short
                .valueOf("3"), CustomerLevel.CENTER.getValue());
        Assert.assertEquals(2, customerList1.size());
        List<CustomerBO> customerList2 = customerPersistence.getAllChildrenForParent(center.getSearchId(), Short
                .valueOf("3"), CustomerLevel.GROUP.getValue());
        Assert.assertEquals(1, customerList2.size());

        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(group1);
        TestObjectFactory.cleanUp(center1);
    }

    public void testGetChildrenForParent() throws NumberFormatException, SystemException, ApplicationException {
        center = createCenter("center");
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        CenterBO center1 = createCenter("center1");
        GroupBO group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_CLOSED, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_CANCELLED, group1);
        List<Integer> customerIds = customerPersistence.getChildrenForParent(center.getSearchId(), Short.valueOf("3"));
        Assert.assertEquals(3, customerIds.size());
        CustomerBO customer = TestObjectFactory.getCustomer(customerIds.get(0));
        Assert.assertEquals("Group", customer.getDisplayName());
        customer = TestObjectFactory.getCustomer(customerIds.get(1));
        Assert.assertEquals("client1", customer.getDisplayName());
        customer = TestObjectFactory.getCustomer(customerIds.get(2));
        Assert.assertEquals("client2", customer.getDisplayName());

        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(group1);
        TestObjectFactory.cleanUp(center1);
    }

    public void testGetCustomers() throws NumberFormatException, SystemException, ApplicationException {
        center = createCenter("center");
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        CenterBO center1 = createCenter("center11");
        GroupBO group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_CLOSED, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_CANCELLED, group1);
        List<Integer> customerIds = customerPersistence.getCustomers(CustomerLevel.CENTER.getValue());
        Assert.assertEquals(2, customerIds.size());

        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(group1);
        TestObjectFactory.cleanUp(center1);
    }

    public void testGetCustomerChecklist() throws NumberFormatException, SystemException, ApplicationException,
            Exception {

        center = createCenter("center");
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        CustomerCheckListBO checklistCenter = TestObjectFactory.createCustomerChecklist(center.getCustomerLevel()
                .getId(), center.getCustomerStatus().getId(), CheckListConstants.STATUS_ACTIVE);
        CustomerCheckListBO checklistClient = TestObjectFactory.createCustomerChecklist(client.getCustomerLevel()
                .getId(), client.getCustomerStatus().getId(), CheckListConstants.STATUS_INACTIVE);
        CustomerCheckListBO checklistGroup = TestObjectFactory.createCustomerChecklist(
                group.getCustomerLevel().getId(), group.getCustomerStatus().getId(), CheckListConstants.STATUS_ACTIVE);
        StaticHibernateUtil.closeSession();
        Assert.assertEquals(1, customerPersistence.getStatusChecklist(center.getCustomerStatus().getId(),
                center.getCustomerLevel().getId()).size());
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class,
                Integer.valueOf(client.getCustomerId()));
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, Integer.valueOf(group.getCustomerId()));
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class,
                Integer.valueOf(center.getCustomerId()));
        checklistCenter = (CustomerCheckListBO) StaticHibernateUtil.getSessionTL().get(CheckListBO.class,
                new Short(checklistCenter.getChecklistId()));
        checklistClient = (CustomerCheckListBO) StaticHibernateUtil.getSessionTL().get(CheckListBO.class,
                new Short(checklistClient.getChecklistId()));
        checklistGroup = (CustomerCheckListBO) StaticHibernateUtil.getSessionTL().get(CheckListBO.class,
                new Short(checklistGroup.getChecklistId()));
        TestObjectFactory.cleanUp(checklistCenter);
        TestObjectFactory.cleanUp(checklistClient);
        TestObjectFactory.cleanUp(checklistGroup);

    }

    public void testRetrieveAllCustomerStatusList() throws NumberFormatException, SystemException, ApplicationException {
        center = createCenter();
        Assert.assertEquals(2, customerPersistence.retrieveAllCustomerStatusList(center.getCustomerLevel().getId())
                .size());
    }

    public void testCustomerCountByOffice() throws Exception {
        int count = customerPersistence.getCustomerCountForOffice(CustomerLevel.CENTER, Short.valueOf("3"));
        Assert.assertEquals(0, count);
        center = createCenter();
        count = customerPersistence.getCustomerCountForOffice(CustomerLevel.CENTER, Short.valueOf("3"));
        Assert.assertEquals(1, count);
    }

    public void testGetAllCustomerNotes() throws Exception {
        center = createCenter();
        center.addCustomerNotes(TestObjectFactory.getCustomerNote("Test Note", center));
        TestObjectFactory.updateObject(center);
        Assert.assertEquals(1, customerPersistence.getAllCustomerNotes(center.getCustomerId()).getSize());
        for (CustomerNoteEntity note : center.getCustomerNotes()) {
            Assert.assertEquals("Test Note", note.getComment());
            Assert.assertEquals(center.getPersonnel().getPersonnelId(), note.getPersonnel().getPersonnelId());
        }
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class,
                Integer.valueOf(center.getCustomerId()));
    }

    public void testGetAllCustomerNotesWithZeroNotes() throws Exception {
        center = createCenter();
        Assert.assertEquals(0, customerPersistence.getAllCustomerNotes(center.getCustomerId()).getSize());
        Assert.assertEquals(0, center.getCustomerNotes().size());
    }

    public void testGetFormedByPersonnel() throws NumberFormatException, SystemException, ApplicationException {
        center = createCenter();
        Assert.assertEquals(1, customerPersistence.getFormedByPersonnel(ClientConstants.LOAN_OFFICER_LEVEL,
                center.getOffice().getOfficeId()).size());
    }

    public void testGetAllClosedAccounts() throws Exception {
        getCustomer();
        groupAccount.changeStatus(AccountState.LOAN_CANCELLED.getValue(), AccountStateFlag.LOAN_WITHDRAW.getValue(),
                "WITHDRAW LOAN ACCOUNT");
        clientAccount.changeStatus(AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue(), null, "WITHDRAW LOAN ACCOUNT");
        clientSavingsAccount.changeStatus(AccountState.SAVINGS_CANCELLED.getValue(), AccountStateFlag.SAVINGS_REJECTED
                .getValue(), "WITHDRAW LOAN ACCOUNT");
        TestObjectFactory.updateObject(groupAccount);
        TestObjectFactory.updateObject(clientAccount);
        TestObjectFactory.updateObject(clientSavingsAccount);
        StaticHibernateUtil.commitTransaction();
        Assert.assertEquals(1, customerPersistence.getAllClosedAccount(client.getCustomerId(),
                AccountTypes.LOAN_ACCOUNT.getValue()).size());
        Assert.assertEquals(1, customerPersistence.getAllClosedAccount(group.getCustomerId(),
                AccountTypes.LOAN_ACCOUNT.getValue()).size());
        Assert.assertEquals(1, customerPersistence.getAllClosedAccount(client.getCustomerId(),
                AccountTypes.SAVINGS_ACCOUNT.getValue()).size());
    }

    public void testGetAllClosedAccountsWhenNoAccountsClosed() throws Exception {
        getCustomer();
        Assert.assertEquals(0, customerPersistence.getAllClosedAccount(client.getCustomerId(),
                AccountTypes.LOAN_ACCOUNT.getValue()).size());
        Assert.assertEquals(0, customerPersistence.getAllClosedAccount(group.getCustomerId(),
                AccountTypes.LOAN_ACCOUNT.getValue()).size());
        Assert.assertEquals(0, customerPersistence.getAllClosedAccount(client.getCustomerId(),
                AccountTypes.SAVINGS_ACCOUNT.getValue()).size());
    }

    public void testGetLOForCustomer() throws PersistenceException {
        createCustomers(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        Short LO = customerPersistence.getLoanOfficerForCustomer(center.getCustomerId());
        Assert.assertEquals(center.getPersonnel().getPersonnelId(), LO);
    }

    public void testUpdateLOsForAllChildren() {
        createCustomers(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        Assert.assertEquals(center.getPersonnel().getPersonnelId(), group.getPersonnel().getPersonnelId());
        Assert.assertEquals(center.getPersonnel().getPersonnelId(), client.getPersonnel().getPersonnelId());
        StaticHibernateUtil.startTransaction();
        PersonnelBO newLO = TestObjectFactory.getPersonnel(Short.valueOf("2"));
        new CustomerPersistence().updateLOsForAllChildren(newLO.getPersonnelId(), center.getSearchId(), center
                .getOffice().getOfficeId());
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(newLO.getPersonnelId(), group.getPersonnel().getPersonnelId());
        Assert.assertEquals(newLO.getPersonnelId(), client.getPersonnel().getPersonnelId());
    }

    public void testUpdateLOsForAllChildrenAccounts() throws Exception {
        createCustomers(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        Assert.assertEquals(center.getPersonnel().getPersonnelId(), group.getPersonnel().getPersonnelId());
        Assert.assertEquals(center.getPersonnel().getPersonnelId(), client.getPersonnel().getPersonnelId());
        StaticHibernateUtil.startTransaction();
        PersonnelBO newLO = TestObjectFactory.getPersonnel(Short.valueOf("2"));
        new CustomerPersistence().updateLOsForAllChildrenAccounts(newLO.getPersonnelId(), center.getSearchId(), center
                .getOffice().getOfficeId());
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        for (AccountBO account : client.getAccounts()) {
            Assert.assertEquals(newLO.getPersonnelId(), account.getPersonnel().getPersonnelId());
        }
    }

    public void testCustomerDeleteMeeting() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        client = TestObjectFactory.createClient("myClient", meeting, CustomerStatus.CLIENT_PENDING);
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        customerPersistence.deleteCustomerMeeting(client);
        CustomerBOTestUtils.setCustomerMeeting(client, null);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertNull(client.getCustomerMeeting());
    }

    public void testDeleteMeeting() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        StaticHibernateUtil.closeSession();
        meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());

        customerPersistence.deleteMeeting(meeting);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
        Assert.assertNull(meeting);
    }

    public void testSearchWithOfficeId() throws Exception {
        createCustomers(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.commitTransaction();
        QueryResult queryResult = new CustomerPersistence().search("C", Short.valueOf("3"), Short.valueOf("1"), Short
                .valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(2, queryResult.getSize());
        Assert.assertEquals(2, queryResult.get(0, 10).size());
    }

    public void testSearchWithoutOfficeId() throws Exception {
        createCustomers(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.commitTransaction();
        QueryResult queryResult = new CustomerPersistence().search("C", Short.valueOf("0"), Short.valueOf("1"), Short
                .valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(2, queryResult.getSize());
        Assert.assertEquals(2, queryResult.get(0, 10).size());
    }

    public void testSearchWithGlobalNo() throws Exception {
        createCustomers(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.commitTransaction();
        QueryResult queryResult = new CustomerPersistence().search(group.getGlobalCustNum(), Short.valueOf("3"), Short
                .valueOf("1"), Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());

    }

    public void testSearchWithGovernmentId() throws Exception {
        createCustomersWithGovernmentId(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.commitTransaction();
        QueryResult queryResult = new CustomerPersistence().search("76346793216", Short.valueOf("3"), Short
                .valueOf("1"), Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());

    }

    @SuppressWarnings("unchecked")
    public void testSearchWithCancelLoanAccounts() throws Exception {
        groupAccount = getLoanAccount();
        groupAccount.changeStatus(AccountState.LOAN_CANCELLED.getValue(), AccountStateFlag.LOAN_WITHDRAW.getValue(),
                "WITHDRAW LOAN ACCOUNT");
        TestObjectFactory.updateObject(groupAccount);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        groupAccount = TestObjectFactory.getObject(LoanBO.class, groupAccount.getAccountId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        QueryResult queryResult = new CustomerPersistence().search(group.getGlobalCustNum(), Short.valueOf("3"), Short
                .valueOf("1"), Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        List results = queryResult.get(0, 10);
        Assert.assertEquals(1, results.size());
        CustomerSearch customerSearch = (CustomerSearch) results.get(0);
        Assert.assertEquals(0, customerSearch.getLoanGlobalAccountNum().size());
    }

    public void testSearchWithAccountGlobalNo() throws Exception {
        getCustomer();
        StaticHibernateUtil.commitTransaction();
        QueryResult queryResult = new CustomerPersistence().search(groupAccount.getGlobalAccountNum(), Short
                .valueOf("3"), Short.valueOf("1"), Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());

    }

    public void testSearchGropAndClient() throws Exception {
        createCustomers(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.commitTransaction();
        QueryResult queryResult = new CustomerPersistence().searchGroupClient("C", Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());

    }

    public void testSearchGropAndClientForLoNoResults() throws Exception {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting, Short.valueOf("3"), Short.valueOf("3"));
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, "1234", true,
                new java.util.Date(), null, null, null, Short.valueOf("3"), center);
        StaticHibernateUtil.commitTransaction();
        QueryResult queryResult = new CustomerPersistence().searchGroupClient("C", Short.valueOf("3"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(0, queryResult.getSize());
        Assert.assertEquals(0, queryResult.get(0, 10).size());

    }

    public void testSearchGropAndClientForLo() throws Exception {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting, Short.valueOf("3"), Short.valueOf("3"));
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, "1234", true,
                new java.util.Date(), null, null, null, Short.valueOf("3"), center);
        StaticHibernateUtil.commitTransaction();
        QueryResult queryResult = new CustomerPersistence().searchGroupClient("G", Short.valueOf("3"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());

    }

    public void testSearchCustForSavings() throws Exception {
        createCustomers(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.commitTransaction();
        QueryResult queryResult = new CustomerPersistence().searchCustForSavings("C", Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(2, queryResult.getSize());
        Assert.assertEquals(2, queryResult.get(0, 10).size());

    }

    public void testGetCustomerAccountsForFee() throws Exception {
        groupAccount = getLoanAccount();
        FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("ClientPeridoicFee", FeeCategory.CENTER, "5",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        AccountFeesEntity accountFee = new AccountFeesEntity(center.getCustomerAccount(), periodicFee,
                ((AmountFeeBO) periodicFee).getFeeAmount().getAmountDoubleValue());
        CustomerAccountBO customerAccount = center.getCustomerAccount();
        AccountTestUtils.addAccountFees(accountFee, customerAccount);
        TestObjectFactory.updateObject(customerAccount);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // check for the account fee
        List<AccountBO> accountList = new CustomerPersistence().getCustomerAccountsForFee(periodicFee.getFeeId());
        Assert.assertNotNull(accountList);
        Assert.assertEquals(1, accountList.size());
        Assert.assertTrue(accountList.get(0) instanceof CustomerAccountBO);
        // get all objects again
        groupAccount = TestObjectFactory.getObject(LoanBO.class, groupAccount.getAccountId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
    }

    public void testRetrieveCustomerAccountActionDetails() throws Exception {
        center = createCenter();
        Assert.assertNotNull(center.getCustomerAccount());
        List<AccountActionDateEntity> actionDates = new CustomerPersistence().retrieveCustomerAccountActionDetails(
                center.getCustomerAccount().getAccountId(), new java.sql.Date(System.currentTimeMillis()));
        Assert.assertEquals("The size of the due insallments is ", actionDates.size(), 1);
    }

    public void testGetActiveCentersUnderUser() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("center", meeting, Short.valueOf("1"), Short.valueOf("1"));
        PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
        List<CustomerBO> customers = new CustomerPersistence().getActiveCentersUnderUser(personnel);
        Assert.assertNotNull(customers);
        Assert.assertEquals(1, customers.size());
    }

    public void testgetGroupsUnderUser() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("center", meeting, Short.valueOf("1"), Short.valueOf("1"));
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        group2 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group33", CustomerStatus.GROUP_CANCELLED, center);
        PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
        List<CustomerBO> customers = new CustomerPersistence().getGroupsUnderUser(personnel);
        Assert.assertNotNull(customers);
        Assert.assertEquals(1, customers.size());
    }

    @SuppressWarnings("unchecked")
    public void testSearchForActiveInBadStandingLoanAccount() throws Exception {
        groupAccount = getLoanAccount();
        groupAccount.changeStatus(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue(), null, "Changing to badStanding");
        TestObjectFactory.updateObject(groupAccount);
        StaticHibernateUtil.closeSession();
        groupAccount = TestObjectFactory.getObject(LoanBO.class, groupAccount.getAccountId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        QueryResult queryResult = new CustomerPersistence().search(group.getGlobalCustNum(), Short.valueOf("3"), Short
                .valueOf("1"), Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        List results = queryResult.get(0, 10);
        Assert.assertEquals(1, results.size());
        CustomerSearch customerSearch = (CustomerSearch) results.get(0);
        Assert.assertEquals(1, customerSearch.getLoanGlobalAccountNum().size());
    }

    public void testGetCustomersByLevelId() throws Exception {
        createCustomers(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.commitTransaction();
        List<CustomerBO> client = new CustomerPersistence().getCustomersByLevelId(Short.parseShort("1"));
        Assert.assertNotNull(client);
        Assert.assertEquals(1, client.size());

        List<CustomerBO> group = new CustomerPersistence().getCustomersByLevelId(Short.parseShort("2"));
        Assert.assertNotNull(group);
        Assert.assertEquals(1, group.size());

        List<CustomerBO> center = new CustomerPersistence().getCustomersByLevelId(Short.parseShort("3"));
        Assert.assertNotNull(center);
        Assert.assertEquals(1, center.size());
    }

    public void testFindCustomerWithNoAssocationsLoadedReturnsActiveCenter() throws Exception {

        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Active Center", meeting);

        verifyCustomerLoaded(center.getCustomerId(), center.getDisplayName());

    }

    public void testFindCustomerWithNoAssocationsLoadedDoesntReturnInactiveCenter() throws Exception {

        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Inactive Center", meeting);
        center.changeStatus(CustomerStatus.CENTER_INACTIVE, CustomerStatusFlag.GROUP_CANCEL_BLACKLISTED, "Made Inactive");

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        verifyCustomerNotLoaded(center.getCustomerId(), center.getDisplayName());
    }


    public void testFindCustomerWithNoAssocationsLoadedReturnsActiveGroup() throws Exception {

        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Active Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Active Group", CustomerStatus.GROUP_ACTIVE,
                center);

        verifyCustomerLoaded(group.getCustomerId(), group.getDisplayName());
    }

    public void testFindCustomerWithNoAssocationsLoadedReturnsHoldGroup() throws Exception {

        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Active Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Hold Group", CustomerStatus.GROUP_HOLD,
                center);

        verifyCustomerLoaded(group.getCustomerId(), group.getDisplayName());
    }

    public void testFindCustomerWithNoAssocationsLoadedDoesntReturnClosedGroup() throws Exception {

        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Active Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Closed Group", CustomerStatus.GROUP_CLOSED,
                center);

        verifyCustomerNotLoaded(group.getCustomerId(), group.getDisplayName());
    }

    public void testFindCustomerWithNoAssocationsLoadedReturnsActiveClient() throws Exception {

        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Active Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Active Group", CustomerStatus.GROUP_ACTIVE,
                center);
        client = TestObjectFactory.createClient("Active Client", CustomerStatus.CLIENT_ACTIVE, group);

        verifyCustomerLoaded(client.getCustomerId(), client.getDisplayName());
    }

    public void testFindCustomerWithNoAssocationsLoadedReturnsHoldClient() throws Exception {

        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Active Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Active Group", CustomerStatus.GROUP_ACTIVE,
                center);
        client = TestObjectFactory.createClient("Hold Client", CustomerStatus.CLIENT_HOLD, group);

        verifyCustomerLoaded(client.getCustomerId(), client.getDisplayName());
    }

    public void testFindCustomerWithNoAssocationsLoadedDoesntReturnClosedClient() throws Exception {

        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Active Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Active Group", CustomerStatus.GROUP_ACTIVE,
                center);
        client = TestObjectFactory.createClient("Closed Client", CustomerStatus.CLIENT_CLOSED, group);

        verifyCustomerNotLoaded(client.getCustomerId(), client.getDisplayName());
    }


    private void verifyCustomerLoaded(Integer customerId, String customerName) {

        CollectionSheetCustomerDto collectionSheetCustomerDto = customerPersistence
        .findCustomerWithNoAssocationsLoaded(customerId);

        Assert.assertNotNull(customerName + " was not returned", collectionSheetCustomerDto);
        Assert.assertEquals(collectionSheetCustomerDto.getCustomerId(), customerId);
    }

    private void verifyCustomerNotLoaded(Integer customerId, String customerName) {

        CollectionSheetCustomerDto collectionSheetCustomerDto = customerPersistence
        .findCustomerWithNoAssocationsLoaded(customerId);

        Assert.assertNull(customerName + " was returned", collectionSheetCustomerDto);
    }


    private AccountBO getSavingsAccount(final CustomerBO customer, final String prdOfferingname, final String shortName)
            throws Exception {
        Date startDate = new Date(System.currentTimeMillis());
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct(prdOfferingname, shortName,
                startDate, RecommendedAmountUnit.COMPLETE_GROUP);
        return TestObjectFactory.createSavingsAccount("432434", customer, Short.valueOf("16"), startDate,
                savingsOffering);
    }

    private void getCustomer() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        LoanOfferingBO loanOffering1 = TestObjectFactory.createLoanOffering("Loanwer", "43fs", startDate, meeting);
        LoanOfferingBO loanOffering2 = TestObjectFactory.createLoanOffering("Loancd123", "vfr", startDate, meeting);
        groupAccount = TestObjectFactory.createLoanAccount("42423142341", group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering1);
        clientAccount = TestObjectFactory.createLoanAccount("3243", client, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering2);
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd12", "abc1", startDate,
                RecommendedAmountUnit.COMPLETE_GROUP, meetingIntCalc, meetingIntPost);
        SavingsOfferingBO savingsOffering1 = TestObjectFactory.createSavingsProduct("SavingPrd11", "abc2", startDate,
                RecommendedAmountUnit.COMPLETE_GROUP, meetingIntCalc, meetingIntPost);
        centerSavingsAccount = TestObjectFactory.createSavingsAccount("432434", center, Short.valueOf("16"), startDate,
                savingsOffering);
        clientSavingsAccount = TestObjectFactory.createSavingsAccount("432434", client, Short.valueOf("16"), startDate,
                savingsOffering1);
    }

    private void createCustomers(final CustomerStatus groupStatus, final CustomerStatus clientStatus) {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", groupStatus, center);
        client = TestObjectFactory.createClient("Client", clientStatus, group);
    }

    private void createCustomersWithGovernmentId(final CustomerStatus groupStatus, final CustomerStatus clientStatus) {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", groupStatus, center);
        client = TestObjectFactory.createClient("Client", clientStatus, group, TestObjectFactory.getFees(), "76346793216", new java.util.Date(1222333444000L));
    }

    private static java.util.Date getMeetingDates(final MeetingBO meeting) {
        List<java.util.Date> dates = new ArrayList<java.util.Date>();
        try {
            dates = meeting.getAllDates(new java.util.Date(System.currentTimeMillis()));
        } catch (MeetingException e) {
            e.printStackTrace();
        }
        return dates.get(dates.size() - 1);
    }

    private CenterBO createCenter() {
        return createCenter("Center_Active_test");
    }

    private CenterBO createCenter(final String name) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        return TestObjectFactory.createWeeklyFeeCenter(name, meeting);
    }

    private LoanBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    private AccountBO getLoanAccount(final CustomerBO group, final MeetingBO meeting, final String offeringName,
            final String shortName) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(offeringName, shortName, startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

    }

    private AccountBO getLoanAccount(final CustomerBO group, final MeetingBO meeting, final String offeringName,
            final String shortName, MifosCurrency currency) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(offeringName, shortName, startDate, meeting, currency);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

    }


    private AccountBO getLoanAccountInActiveBadStanding(final CustomerBO group, final MeetingBO meeting,
            final String offeringName, final String shortName) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(offeringName, shortName, startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423141111", group, AccountState.LOAN_ACTIVE_IN_BAD_STANDING,
                startDate, loanOffering);

    }
}
