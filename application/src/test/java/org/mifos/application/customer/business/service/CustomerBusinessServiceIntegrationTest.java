/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.customer.business.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountStateMachines;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanBOTestUtils;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsBOIntegrationTest;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.util.helpers.CheckListConstants;
import org.mifos.application.customer.business.CustomerAccountBOIntegrationTest;
import org.mifos.application.customer.business.CustomerActivityEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerBOIntegrationTest;
import org.mifos.application.customer.business.CustomerFeeScheduleEntity;
import org.mifos.application.customer.business.CustomerNoteEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.business.CenterPerformanceHistory;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerRecentActivityView;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficecFixture;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerBusinessServiceIntegrationTest extends MifosIntegrationTestCase {
    public CustomerBusinessServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final Integer TWO = Integer.valueOf(2);

    private static final Integer THREE = Integer.valueOf(3);

    private static final Integer ONE = Integer.valueOf(1);

    private CustomerBO center;

    private GroupBO group;

    private CustomerBO client;

    private AccountBO account;

    private LoanBO groupAccount;

    private LoanBO clientAccount;

    private SavingsBO clientSavingsAccount;

    private MeetingBO meeting;

    private final SavingsTestHelper helper = new SavingsTestHelper();

    private SavingsOfferingBO savingsOffering;

    private SavingsBO savingsBO;

    private final MifosCurrency currency = Configuration.getInstance().getSystemConfig().getCurrency();

    private LoanOfferingBO loanOffering = null;

    private CustomerBusinessService service;

    private CustomerPersistence customerPersistenceMock;

    private CustomerBusinessService customerBusinessServiceWithMock;

    private static final OfficeBO OFFICE = OfficecFixture.createOffice(Short.valueOf("1"));

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = (CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(
                BusinessServiceName.Customer);
        customerPersistenceMock = createMock(CustomerPersistence.class);
        customerBusinessServiceWithMock = new CustomerBusinessService(customerPersistenceMock);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(clientSavingsAccount);
            TestObjectFactory.cleanUp(groupAccount);
            TestObjectFactory.cleanUp(clientAccount);
            TestObjectFactory.cleanUp(account);
            TestObjectFactory.cleanUp(savingsBO);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
            StaticHibernateUtil.closeSession();
        } catch (Exception e) {
            // throwing here tends to mask other failures
            e.printStackTrace();
        }
        super.tearDown();
    }


    public void testSearchGropAndClient() throws Exception {
        createInitialCustomers();
        QueryResult queryResult = new CustomerBusinessService().searchGroupClient("cl", Short.valueOf("1"));
        assertNotNull(queryResult);
        assertEquals(1, queryResult.getSize());
        assertEquals(1, queryResult.get(0, 10).size());

    }

    public void testFailureSearchGropAndClient() throws Exception {
        createInitialCustomers();
        TestObjectFactory.simulateInvalidConnection();
        try {
            new CustomerBusinessService().searchGroupClient("cl", Short.valueOf("1"));
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testSearchCustForSavings() throws Exception {
        createInitialCustomers();
        QueryResult queryResult = new CustomerBusinessService().searchCustForSavings("c", Short.valueOf("1"));
        assertNotNull(queryResult);
        assertEquals(2, queryResult.getSize());
        assertEquals(2, queryResult.get(0, 10).size());

    }

    public void testFailureSearchCustForSavings() throws Exception {
        createInitialCustomers();
        TestObjectFactory.simulateInvalidConnection();
        try {
            new CustomerBusinessService().searchCustForSavings("c", Short.valueOf("1"));
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testFailureFetchLoanCycleCounter() throws Exception {
        account = getLoanAccount();
        loanOffering.setLoanCounter(true);
        TestObjectFactory.updateObject(loanOffering);
        TestObjectFactory.flushandCloseSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            ((CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer))
            .fetchLoanCycleCounter(group);
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testGetAllActivityView() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center", meeting);
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        List<CustomerRecentActivityView> customerActivityViewList = service.getAllActivityView(center
                .getGlobalCustNum());
        assertEquals(0, customerActivityViewList.size());
        center.getCustomerAccount().setUserContext(TestUtils.makeUser());
        center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        Set<CustomerActivityEntity> customerActivityDetails = center.getCustomerAccount().getCustomerActivitDetails();
        assertEquals(1, customerActivityDetails.size());
        for (CustomerActivityEntity customerActivityEntity : customerActivityDetails) {
            assertEquals(new Money("100"), customerActivityEntity.getAmount());
        }
        List<CustomerRecentActivityView> customerActivityView = service.getAllActivityView(center.getGlobalCustNum());
        assertEquals(1, customerActivityView.size());
        for (CustomerRecentActivityView view : customerActivityView) {
            assertEquals(new Money("100").toString(), view.getAmount());
            assertEquals("Amnt waived", view.getDescription());
            assertEquals(TestObjectFactory.getContext().getName(), view.getPostedBy());
        }
    }

    public void testFailureGetRecentActivityView() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center", meeting);
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getAllActivityView(center.getGlobalCustNum());
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testGetRecentActivityView() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center", meeting);
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        List<CustomerRecentActivityView> customerActivityViewList = service.getAllActivityView(center
                .getGlobalCustNum());
        assertEquals(0, customerActivityViewList.size());
        UserContext uc = TestUtils.makeUser();
        center.getCustomerAccount().setUserContext(uc);
        center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        for (AccountActionDateEntity accountAction : center.getCustomerAccount().getAccountActionDates()) {
            CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) accountAction;
            if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                Set<AccountFeesActionDetailEntity> accountFeesActionDetails = accountActionDateEntity
                .getAccountFeesActionDetails();
                for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {
                    CustomerAccountBOIntegrationTest.setFeeAmount(
                            (CustomerFeeScheduleEntity) accountFeesActionDetailEntity, new Money("100"));
                }
            }
        }
        TestObjectFactory.updateObject(center);
        center.getCustomerAccount().setUserContext(uc);
        center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        for (AccountActionDateEntity accountAction : center.getCustomerAccount().getAccountActionDates()) {
            CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) accountAction;
            if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                Set<AccountFeesActionDetailEntity> accountFeesActionDetails = accountActionDateEntity
                .getAccountFeesActionDetails();
                for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {
                    CustomerAccountBOIntegrationTest.setFeeAmount(
                            (CustomerFeeScheduleEntity) accountFeesActionDetailEntity, new Money("100"));
                }
            }
        }
        TestObjectFactory.updateObject(center);
        center.getCustomerAccount().setUserContext(uc);
        center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        for (AccountActionDateEntity accountAction : center.getCustomerAccount().getAccountActionDates()) {
            CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) accountAction;
            if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                Set<AccountFeesActionDetailEntity> accountFeesActionDetails = accountActionDateEntity
                .getAccountFeesActionDetails();
                for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {
                    CustomerAccountBOIntegrationTest.setFeeAmount(
                            (CustomerFeeScheduleEntity) accountFeesActionDetailEntity, new Money("20"));
                }
            }
        }
        TestObjectFactory.updateObject(center);
        center.getCustomerAccount().setUserContext(uc);
        center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        Set<CustomerActivityEntity> customerActivityDetails = center.getCustomerAccount().getCustomerActivitDetails();
        assertEquals(3, customerActivityDetails.size());
        for (CustomerActivityEntity customerActivityEntity : customerActivityDetails) {
            assertEquals(new Money("100"), customerActivityEntity.getAmount());
        }

        List<CustomerRecentActivityView> customerActivityView = service.getRecentActivityView(center.getCustomerId());
        assertEquals(3, customerActivityView.size());
        for (CustomerRecentActivityView view : customerActivityView) {
            assertEquals(new Money("100").toString(), view.getAmount());
            assertEquals(TestObjectFactory.getContext().getName(), view.getPostedBy());
        }
    }

    public void testFindBySystemId() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        savingsBO = getSavingsAccount(group, "fsaf5", "ads5");
        StaticHibernateUtil.closeSession();
        group = (GroupBO) service.findBySystemId(group.getGlobalCustNum());
        assertEquals("Group_Active_test", group.getDisplayName());
        assertEquals(2, group.getAccounts().size());
        assertEquals(0, group.getOpenLoanAccounts().size());
        assertEquals(1, group.getOpenSavingAccounts().size());
        assertEquals(CustomerStatus.GROUP_ACTIVE, group.getStatus());
        StaticHibernateUtil.closeSession();
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    public void testgetBySystemId() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        savingsBO = getSavingsAccount(group, "fsaf5", "ads5");
        StaticHibernateUtil.closeSession();
        group = (GroupBO) service.findBySystemId(group.getGlobalCustNum(), group.getCustomerLevel().getId());
        assertEquals("Group_Active_test", group.getDisplayName());
        assertEquals(2, group.getAccounts().size());
        assertEquals(0, group.getOpenLoanAccounts().size());
        assertEquals(1, group.getOpenSavingAccounts().size());
        assertEquals(CustomerStatus.GROUP_ACTIVE.getValue(), group.getCustomerStatus().getId());
        StaticHibernateUtil.closeSession();
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    public void testSuccessfulGet() throws Exception {
        center = createCenter("MyCenter");
        savingsBO = getSavingsAccount(center, "fsaf5", "ads5");
        StaticHibernateUtil.closeSession();
        center = service.getCustomer(center.getCustomerId());
        assertNotNull(center);
        assertEquals("MyCenter", center.getDisplayName());
        assertEquals(2, center.getAccounts().size());
        assertEquals(0, center.getOpenLoanAccounts().size());
        assertEquals(1, center.getOpenSavingAccounts().size());
        assertEquals(CustomerStatus.CENTER_ACTIVE.getValue(), center.getCustomerStatus().getId());
        StaticHibernateUtil.closeSession();
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
    }

    public void testFailureGet() throws Exception {
        center = createCenter("MyCenter");
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getCustomer(center.getCustomerId());
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testFailureGetBySystemId() throws Exception {
        center = createCenter("MyCenter");
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.findBySystemId(center.getGlobalCustNum());
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testGetCenterPerformanceHistory() throws Exception {
        Money totalLoan = new Money();
        Money totalSavings = new Money();
        Money totalPortfolioAtRisk = new Money();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        CenterBO center1 = TestObjectFactory.createCenter("Center_Active_test1", meeting);
        GroupBO group1 = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_ACTIVE, group1);
        account = getSavingsAccountWithBalance(center, meeting, "savings prd123", "xyz1");
        AccountBO account1 = getSavingsAccountWithBalance(client, meeting, "savings prd1231", "xyz2");
        AccountBO account2 = getSavingsAccountWithBalance(client2, meeting, "savings prd1232", "xyz3");
        AccountBO account3 = getSavingsAccountWithBalance(client3, meeting, "savings prd1233", "xyz4");
        AccountBO account4 = getSavingsAccountWithBalance(group1, meeting, "savings prd1234", "xyz5");
        AccountBO account5 = getSavingsAccountWithBalance(group, meeting, "savings prd1235", "xyz6");
        AccountBO account6 = getSavingsAccountWithBalance(center1, meeting, "savings prd1236", "xyz7");

        AccountBO account7 = getLoanAccount(client, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, meeting, "fdbdhgsgh",
        "54hg");
        changeFirstInstallmentDateToPastDate(account7);
        AccountBO account8 = getLoanAccount(client2, AccountState.LOAN_ACTIVE_IN_BAD_STANDING, meeting, "dsafasdf",
        "32fs");
        changeFirstInstallmentDateToPastDate(account8);
        AccountBO account9 = getLoanAccount(client, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, meeting, "afvasfgfdg",
        "a12w");
        changeFirstInstallmentDateToPastDate(account9);
        AccountBO account10 = getLoanAccount(group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, meeting, "afadsff",
        "23e");
        CustomerBOIntegrationTest.setCustomerStatus(client2, new CustomerStatusEntity(CustomerStatus.CLIENT_CLOSED));

        TestObjectFactory.updateObject(client2);
        client2 = TestObjectFactory.getClient(client2.getCustomerId());
        CustomerBOIntegrationTest.setCustomerStatus(client3, new CustomerStatusEntity(CustomerStatus.CLIENT_CANCELLED));
        TestObjectFactory.updateObject(client3);
        client3 = TestObjectFactory.getClient(client3.getCustomerId());

        CenterPerformanceHistory centerPerformanceHistory = service.getCenterPerformanceHistory(center.getSearchId(),
                Short.valueOf("3"));
        totalLoan = centerPerformanceHistory.getTotalOutstandingPortfolio();
        totalSavings = centerPerformanceHistory.getTotalSavings();
        totalPortfolioAtRisk = centerPerformanceHistory.getPortfolioAtRisk();
        assertEquals(1, centerPerformanceHistory.getNumberOfGroups().intValue());
        assertEquals(1, centerPerformanceHistory.getNumberOfClients().intValue());

        account1 = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(account1
                .getAccountId())));
        account2 = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(account2
                .getAccountId())));
        account3 = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(account3
                .getAccountId())));
        account4 = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(account4
                .getAccountId())));
        account5 = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(account5
                .getAccountId())));
        account6 = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(account6
                .getAccountId())));
        account7 = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(account7
                .getAccountId())));
        account8 = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(account8
                .getAccountId())));
        account9 = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(account9
                .getAccountId())));
        account10 = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(account10
                .getAccountId())));
        account = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(account
                .getAccountId())));
        client = (ClientBO) (StaticHibernateUtil.getSessionTL().get(ClientBO.class, Integer.valueOf(client
                .getCustomerId())));
        group = (GroupBO) (StaticHibernateUtil.getSessionTL()
                .get(GroupBO.class, Integer.valueOf(group.getCustomerId())));
        center = (CenterBO) (StaticHibernateUtil.getSessionTL().get(CenterBO.class, Integer.valueOf(center
                .getCustomerId())));
        client2 = (ClientBO) (StaticHibernateUtil.getSessionTL().get(ClientBO.class, Integer.valueOf(client2
                .getCustomerId())));
        client3 = (ClientBO) (StaticHibernateUtil.getSessionTL().get(ClientBO.class, Integer.valueOf(client3
                .getCustomerId())));
        group1 = (GroupBO) (StaticHibernateUtil.getSessionTL().get(GroupBO.class, Integer.valueOf(group1
                .getCustomerId())));
        center1 = (CenterBO) (StaticHibernateUtil.getSessionTL().get(CenterBO.class, Integer.valueOf(center1
                .getCustomerId())));
        TestObjectFactory.cleanUp(account3);
        TestObjectFactory.cleanUp(account2);
        TestObjectFactory.cleanUp(account1);
        TestObjectFactory.cleanUp(account8);
        TestObjectFactory.cleanUp(account9);
        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(account4);
        TestObjectFactory.cleanUp(account5);
        TestObjectFactory.cleanUp(account10);
        TestObjectFactory.cleanUp(group1);
        TestObjectFactory.cleanUp(account6);
        TestObjectFactory.cleanUp(center1);
        TestObjectFactory.cleanUp(account7);

        // temporarily(?) moved so assertion failure doesn't preclude cleanup
        assertEquals(new Money("1200.0"), totalLoan);
        assertEquals(new Money("400.0"), totalSavings);
        assertEquals(new Money("0.25"), totalPortfolioAtRisk);
    }


    public void testGetCustomerChecklist() throws Exception {

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        CustomerCheckListBO checklistCenter = TestObjectFactory.createCustomerChecklist(center.getCustomerLevel()
                .getId(), center.getCustomerStatus().getId(), CheckListConstants.STATUS_ACTIVE);
        CustomerCheckListBO checklistClient = TestObjectFactory.createCustomerChecklist(client.getCustomerLevel()
                .getId(), client.getCustomerStatus().getId(), CheckListConstants.STATUS_INACTIVE);
        CustomerCheckListBO checklistGroup = TestObjectFactory.createCustomerChecklist(
                group.getCustomerLevel().getId(), group.getCustomerStatus().getId(), CheckListConstants.STATUS_ACTIVE);
        StaticHibernateUtil.closeSession();
        assertEquals(1, service.getStatusChecklist(center.getCustomerStatus().getId(),
                center.getCustomerLevel().getId()).size());
        client = (ClientBO) (StaticHibernateUtil.getSessionTL().get(ClientBO.class, Integer.valueOf(client
                .getCustomerId())));
        group = (GroupBO) (StaticHibernateUtil.getSessionTL()
                .get(GroupBO.class, Integer.valueOf(group.getCustomerId())));
        center = (CenterBO) (StaticHibernateUtil.getSessionTL().get(CenterBO.class, Integer.valueOf(center
                .getCustomerId())));
        checklistCenter = (CustomerCheckListBO) (StaticHibernateUtil.getSessionTL().get(CheckListBO.class, new Short(
                checklistCenter.getChecklistId())));
        checklistClient = (CustomerCheckListBO) (StaticHibernateUtil.getSessionTL().get(CheckListBO.class, new Short(
                checklistClient.getChecklistId())));
        checklistGroup = (CustomerCheckListBO) (StaticHibernateUtil.getSessionTL().get(CheckListBO.class, new Short(
                checklistGroup.getChecklistId())));
        TestObjectFactory.cleanUp(checklistCenter);
        TestObjectFactory.cleanUp(checklistClient);
        TestObjectFactory.cleanUp(checklistGroup);

    }

    public void testFailureGetCustomerChecklist() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        StaticHibernateUtil.closeSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getStatusChecklist(center.getCustomerStatus().getId(), center.getCustomerLevel().getId());
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testRetrieveAllCustomerStatusList() throws NumberFormatException, SystemException, ApplicationException {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        assertEquals(2, service.retrieveAllCustomerStatusList(center.getCustomerLevel().getId()).size());
    }

    public void testFailureRetrieveAllCustomerStatusList() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getStatusChecklist(center.getCustomerStatus().getId(), center.getCustomerLevel().getId());
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testGetFormedByPersonnel() throws NumberFormatException, SystemException, ApplicationException {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        assertEquals(1, service.getFormedByPersonnel(ClientConstants.LOAN_OFFICER_LEVEL,
                center.getOffice().getOfficeId()).size());
    }

    public void testFailureGetFormedByPersonnel() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getFormedByPersonnel(ClientConstants.LOAN_OFFICER_LEVEL, center.getOffice().getOfficeId());
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testGetAllCustomerNotes() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        center.addCustomerNotes(TestObjectFactory.getCustomerNote("Test Note", center));
        TestObjectFactory.updateObject(center);
        assertEquals(1, service.getAllCustomerNotes(center.getCustomerId()).getSize());
        for (CustomerNoteEntity note : center.getCustomerNotes()) {
            assertEquals("Test Note", note.getComment());
            assertEquals(center.getPersonnel().getPersonnelId(), note.getPersonnel().getPersonnelId());
        }
        center = (CenterBO) (StaticHibernateUtil.getSessionTL().get(CenterBO.class, Integer.valueOf(center
                .getCustomerId())));
    }

    public void testGetAllCustomerNotesWithZeroNotes() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        assertEquals(0, service.getAllCustomerNotes(center.getCustomerId()).getSize());
        assertEquals(0, center.getCustomerNotes().size());
    }

    public void testGetStatusName() throws Exception {
        createInitialCustomers();
        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, center.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.CENTER);
        String statusNameForCenter = service.getStatusName(TestObjectFactory.TEST_LOCALE, center.getStatus(),
                CustomerLevel.CENTER);
        assertEquals("Active", statusNameForCenter);

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, group.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.GROUP);
        String statusNameForGroup = service.getStatusName(TestObjectFactory.TEST_LOCALE, group.getStatus(),
                CustomerLevel.GROUP);
        assertEquals("Active", statusNameForGroup);

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, client.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.CLIENT);
        String statusNameForClient = service.getStatusName(TestObjectFactory.TEST_LOCALE, client.getStatus(),
                CustomerLevel.CLIENT);
        assertNotNull("Active", statusNameForClient);
    }

    public void testGetFlagName() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client", CustomerStatus.CLIENT_CLOSED, group);

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, client.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.CLIENT);
        String flagNameForClient = service.getFlagName(TestObjectFactory.TEST_LOCALE,
                CustomerStatusFlag.CLIENT_CLOSED_DUPLICATE, CustomerLevel.CLIENT);
        assertNotNull("Duplicate", flagNameForClient);

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, group.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.GROUP);
        String flagNameForGroup = service.getFlagName(TestObjectFactory.TEST_LOCALE,
                CustomerStatusFlag.GROUP_CLOSED_DUPLICATE, CustomerLevel.GROUP);
        assertNotNull("Duplicate", flagNameForGroup);
    }

    public void testGetStatusList() throws Exception {
        createInitialCustomers();
        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, center.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.CENTER);
        List<CustomerStatusEntity> statusListForCenter = service.getStatusList(center.getCustomerStatus(),
                CustomerLevel.CENTER, TestObjectFactory.TEST_LOCALE);
        assertEquals(1, statusListForCenter.size());

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, group.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.GROUP);
        List<CustomerStatusEntity> statusListForGroup = service.getStatusList(group.getCustomerStatus(),
                CustomerLevel.GROUP, TestObjectFactory.TEST_LOCALE);
        assertEquals(2, statusListForGroup.size());

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, client.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.CLIENT);
        List<CustomerStatusEntity> statusListForClient = service.getStatusList(client.getCustomerStatus(),
                CustomerLevel.CLIENT, TestObjectFactory.TEST_LOCALE);
        assertEquals(2, statusListForClient.size());
    }

    public void testSearch() throws Exception {

        center = createCenter("MyCenter");
        QueryResult queryResult = service
        .search("MyCenter", Short.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"));
        assertNotNull(queryResult);
        assertEquals(1, queryResult.getSize());
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
        assertEquals(1, service.getAllClosedAccount(client.getCustomerId(), AccountTypes.LOAN_ACCOUNT.getValue())
                .size());
        assertEquals(1, service.getAllClosedAccount(group.getCustomerId(), AccountTypes.LOAN_ACCOUNT.getValue()).size());
        assertEquals(1, service.getAllClosedAccount(client.getCustomerId(), AccountTypes.SAVINGS_ACCOUNT.getValue())
                .size());
    }

    public void testFailureGetAllClosedAccounts() throws Exception {
        getCustomer();
        groupAccount.changeStatus(AccountState.LOAN_CANCELLED.getValue(), AccountStateFlag.LOAN_WITHDRAW.getValue(),
        "WITHDRAW LOAN ACCOUNT");
        clientAccount.changeStatus(AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue(), null, "WITHDRAW LOAN ACCOUNT");
        clientSavingsAccount.changeStatus(AccountState.SAVINGS_CANCELLED.getValue(), AccountStateFlag.SAVINGS_REJECTED
                .getValue(), "WITHDRAW LOAN ACCOUNT");
        TestObjectFactory.updateObject(groupAccount);
        TestObjectFactory.updateObject(clientAccount);
        TestObjectFactory.updateObject(clientSavingsAccount);
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getAllClosedAccount(client.getCustomerId(), AccountTypes.LOAN_ACCOUNT.getValue());
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
        StaticHibernateUtil.closeSession();

    }

    public void testGetAllClosedAccountsWhenNoAccountsClosed() throws Exception {
        getCustomer();
        assertEquals(0, service.getAllClosedAccount(client.getCustomerId(), AccountTypes.LOAN_ACCOUNT.getValue())
                .size());
        assertEquals(0, service.getAllClosedAccount(group.getCustomerId(), AccountTypes.LOAN_ACCOUNT.getValue()).size());
        assertEquals(0, service.getAllClosedAccount(client.getCustomerId(), AccountTypes.SAVINGS_ACCOUNT.getValue())
                .size());
    }

    public void testGetActiveCentersUnderUser() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("center", meeting, Short.valueOf("1"), Short.valueOf("1"));
        PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
        List<CustomerBO> customers = service.getActiveCentersUnderUser(personnel);
        assertNotNull(customers);
        assertEquals(1, customers.size());
    }

    public void testFailureGetActiveCentersUnderUser() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("center", meeting, Short.valueOf("1"), Short.valueOf("1"));
        PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getActiveCentersUnderUser(personnel);
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
        StaticHibernateUtil.closeSession();

    }

    public void testgetGroupsUnderUser() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("center", meeting, Short.valueOf("1"), Short.valueOf("1"));
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
        List<CustomerBO> customers = service.getGroupsUnderUser(personnel);
        assertNotNull(customers);
        assertEquals(1, customers.size());
    }

    public void testFailuregetGroupsUnderUser() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("center", meeting, Short.valueOf("1"), Short.valueOf("1"));
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getGroupsUnderUser(personnel);
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
        StaticHibernateUtil.closeSession();

    }

    public void testGetCustomersByLevelId() throws Exception {
        createInitialCustomers();

        List<CustomerBO> client = service.getCustomersByLevelId(Short.parseShort("1"));
        assertNotNull(client);
        assertEquals(1, client.size());

        List<CustomerBO> group = service.getCustomersByLevelId(Short.parseShort("2"));
        assertNotNull(group);
        assertEquals(1, group.size());

        List<CustomerBO> center = service.getCustomersByLevelId(Short.parseShort("3"));
        assertNotNull(center);
        assertEquals(1, client.size());

    }

    private AccountBO getLoanAccount(CustomerBO customer, AccountState accountState, MeetingBO meeting,
            String offeringName, String shortName) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(offeringName, shortName, startDate, meeting);
        return TestObjectFactory.createBasicLoanAccount(customer, accountState, startDate, loanOffering);

    }

    private AccountBO getSavingsAccountWithBalance(CustomerBO customer, MeetingBO meeting, String productName,
            String shortName) throws Exception {
        Date startDate = new Date(System.currentTimeMillis());
        Date currentDate = new Date(System.currentTimeMillis());
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct(productName, shortName, currentDate);
        SavingsBO savingsBO = TestObjectFactory.createSavingsAccount("432434", customer, Short.valueOf("16"),
                startDate, savingsOffering);
        StaticHibernateUtil.closeSession();
        savingsBO = (SavingsBO) (new AccountPersistence().getAccount(savingsBO.getAccountId()));
        SavingsBOIntegrationTest.setBalance(savingsBO, new Money());
        Money enteredAmount = new Money(currency, "100.0");
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, savingsBO.getPersonnel(), Short
                .valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setCustomer(customer);
        paymentData.setRecieptDate(new Date(System.currentTimeMillis()));
        paymentData.setRecieptNum("34244");
        AccountActionDateEntity accountActionDate = savingsBO.getAccountActionDate(Short.valueOf("1"));

        SavingsPaymentData savingsPaymentData = new SavingsPaymentData(accountActionDate);
        paymentData.addAccountPaymentData(savingsPaymentData);
        savingsBO.applyPaymentWithPersist(paymentData);

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        SavingsBO savingsNew = (SavingsBO) (StaticHibernateUtil.getSessionTL().get(SavingsBO.class, Integer
                .valueOf(savingsBO.getAccountId())));
        return savingsNew;

    }

    private void changeFirstInstallmentDateToPastDate(AccountBO accountBO) throws Exception {
        accountBO = (AccountBO) (StaticHibernateUtil.getSessionTL().get(AccountBO.class, Integer.valueOf(accountBO
                .getAccountId())));
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - 40);
        for (AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()) {
            LoanBOTestUtils.setActionDate(accountActionDateEntity, new java.sql.Date(currentDateCalendar
                    .getTimeInMillis()));
            break;
        }
        TestObjectFactory.updateObject(accountBO);
    }

    private void createInitialCustomers() throws Exception {
        center = createCenter("Center_Active_test");
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private CenterBO createCenter(String name) throws Exception {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createCenter(name, meeting);
    }

    private LoanBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    private SavingsBO getSavingsAccount(CustomerBO customerBO, String offeringName, String shortName) throws Exception {
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", customerBO,
                AccountStates.SAVINGS_ACC_APPROVED, new Date(System.currentTimeMillis()), savingsOffering);
    }

    private void getCustomer() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());

        createInitialCustomers();
        LoanOfferingBO loanOffering1 = TestObjectFactory.createLoanOffering("Loanwer", "43fs", startDate, meeting);
        LoanOfferingBO loanOffering2 = TestObjectFactory.createLoanOffering("Loancd123", "vfr", startDate, meeting);
        groupAccount = TestObjectFactory.createLoanAccount("42423142341", group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering1);
        clientAccount = TestObjectFactory.createLoanAccount("3243", client, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering2);
        clientSavingsAccount = getSavingsAccount(client, "SavingPrd11", "abc2");
    }

    public void testDropOutRate() throws Exception {
        expect(customerPersistenceMock.getDropOutClientsCountForOffice(OFFICE)).andReturn(ONE);
        expect(customerPersistenceMock.getActiveOrHoldClientCountForOffice(OFFICE)).andReturn(THREE);
        replay(customerPersistenceMock);
        BigDecimal dropOutRate = customerBusinessServiceWithMock.getClientDropOutRateForOffice(OFFICE);
        verify(customerPersistenceMock);
        assertEquals(25d, dropOutRate.doubleValue(), 0.001);
    }

    public void testVeryPoorClientDropoutRate() throws Exception {
        expect(customerPersistenceMock.getVeryPoorDropOutClientsCountForOffice(OFFICE)).andReturn(ONE);
        expect(customerPersistenceMock.getVeryPoorActiveOrHoldClientCountForOffice(OFFICE)).andReturn(THREE);
        replay(customerPersistenceMock);
        BigDecimal veryPoorClientDropoutRateForOffice = customerBusinessServiceWithMock
        .getVeryPoorClientDropoutRateForOffice(OFFICE);
        assertEquals(25d, veryPoorClientDropoutRateForOffice.doubleValue(), 0.001);
        verify(customerPersistenceMock);
    }
}
