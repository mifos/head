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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.AccountingRulesConstants;
import org.mifos.config.ConfigurationManager;
import org.mifos.customers.business.CustomerAccountBOTestUtils;
import org.mifos.customers.business.CustomerActivityEntity;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerFeeScheduleEntity;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.checklist.business.CheckListBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.checklist.util.helpers.CheckListConstants;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficecFixture;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerRecentActivityView;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CustomerBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    static {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(FilePaths.SPRING_CONFIG_CORE);
        MessageSource springMessageSource = applicationContext.getBean(MessageSource.class);
        MessageLookup.getInstance().setMessageSource(springMessageSource);
    }

    public CustomerBusinessServiceIntegrationTest() throws Exception {
        super();
    }

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
    private LoanOfferingBO loanOffering = null;
    private CustomerBusinessService service;
    private CustomerPersistence customerPersistenceMock;
    private CustomerBusinessService customerBusinessServiceWithMock;
    private static final OfficeBO OFFICE = OfficecFixture.createOffice(Short.valueOf("1"));

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        service = new CustomerBusinessService();
        customerPersistenceMock = createMock(CustomerPersistence.class);
        customerBusinessServiceWithMock = new CustomerBusinessService(customerPersistenceMock);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            // if there is an additional currency code defined, then clear it
            ConfigurationManager.getInstance().clearProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES);
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



    public void testSearchGroupAndClient() throws Exception {
        createInitialCustomers();
        QueryResult queryResult = new CustomerBusinessService().searchGroupClient("cl", Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());

    }

    public void testFailureSearchGropAndClient() throws Exception {
        createInitialCustomers();
        TestObjectFactory.simulateInvalidConnection();
        try {
            new CustomerBusinessService().searchGroupClient("cl", Short.valueOf("1"));
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testSearchCustForSavings() throws Exception {
        createInitialCustomers();
        QueryResult queryResult = new CustomerBusinessService().searchCustForSavings("c", Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(2, queryResult.getSize());
        Assert.assertEquals(2, queryResult.get(0, 10).size());

    }

    public void testFailureSearchCustForSavings() throws Exception {
        createInitialCustomers();
        TestObjectFactory.simulateInvalidConnection();
        try {
            new CustomerBusinessService().searchCustForSavings("c", Short.valueOf("1"));
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
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
            new CustomerBusinessService().fetchLoanCycleCounter(group.getCustomerId(), group.getCustomerLevel().getId());
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testGetAllActivityView() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        List<CustomerRecentActivityView> customerActivityViewList = service.getAllActivityView(center
                .getGlobalCustNum());
        Assert.assertEquals(0, customerActivityViewList.size());
        center.getCustomerAccount().setUserContext(TestUtils.makeUser());
        center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        List<CustomerActivityEntity> customerActivityDetails = center.getCustomerAccount().getCustomerActivitDetails();
        Assert.assertEquals(1, customerActivityDetails.size());
        for (CustomerActivityEntity customerActivityEntity : customerActivityDetails) {
            Assert.assertEquals(new Money(getCurrency(), "100"), customerActivityEntity.getAmount());
        }
        List<CustomerRecentActivityView> customerActivityView = service.getAllActivityView(center.getGlobalCustNum());
        Assert.assertEquals(1, customerActivityView.size());
        for (CustomerRecentActivityView view : customerActivityView) {
            Assert.assertEquals(new Money(getCurrency(), "100").toString(), view.getAmount());
            Assert.assertEquals("Amnt waived", view.getDescription());
            Assert.assertEquals(TestObjectFactory.getContext().getName(), view.getPostedBy());
        }
    }

    public void testFailureGetRecentActivityView() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getAllActivityView(center.getGlobalCustNum());
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testGetRecentActivityView() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        List<CustomerRecentActivityView> customerActivityViewList = service.getAllActivityView(center
                .getGlobalCustNum());
        Assert.assertEquals(0, customerActivityViewList.size());
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
                    CustomerAccountBOTestUtils.setFeeAmount((CustomerFeeScheduleEntity) accountFeesActionDetailEntity,
                            new Money(getCurrency(), "100"));
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
                    CustomerAccountBOTestUtils.setFeeAmount((CustomerFeeScheduleEntity) accountFeesActionDetailEntity,
                            new Money(getCurrency(), "100"));
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
                    CustomerAccountBOTestUtils.setFeeAmount((CustomerFeeScheduleEntity) accountFeesActionDetailEntity,
                            new Money(getCurrency(), "20"));
                }
            }
        }
        TestObjectFactory.updateObject(center);
        center.getCustomerAccount().setUserContext(uc);
        center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        List<CustomerActivityEntity> customerActivityDetails = center.getCustomerAccount().getCustomerActivitDetails();
        Assert.assertEquals(3, customerActivityDetails.size());
        for (CustomerActivityEntity customerActivityEntity : customerActivityDetails) {
            Assert.assertEquals(new Money(getCurrency(), "100"), customerActivityEntity.getAmount());
        }

        List<CustomerRecentActivityView> customerActivityView = service.getRecentActivityView(center.getCustomerId());
        Assert.assertEquals(3, customerActivityView.size());
        for (CustomerRecentActivityView view : customerActivityView) {
            Assert.assertEquals(new Money(getCurrency(), "100").toString(), view.getAmount());
            Assert.assertEquals(TestObjectFactory.getContext().getName(), view.getPostedBy());
        }
    }

    public void testFindBySystemId() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        savingsBO = getSavingsAccount(group, "fsaf5", "ads5");
        StaticHibernateUtil.closeSession();
        group = (GroupBO) service.findBySystemId(group.getGlobalCustNum());
        Assert.assertEquals("Group_Active_test", group.getDisplayName());
        Assert.assertEquals(2, group.getAccounts().size());
        Assert.assertEquals(0, group.getOpenLoanAccounts().size());
        Assert.assertEquals(1, group.getOpenSavingAccounts().size());
        Assert.assertEquals(CustomerStatus.GROUP_ACTIVE, group.getStatus());
        StaticHibernateUtil.closeSession();
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    public void testgetBySystemId() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        savingsBO = getSavingsAccount(group, "fsaf5", "ads5");
        StaticHibernateUtil.closeSession();
        group = (GroupBO) service.findBySystemId(group.getGlobalCustNum(), group.getCustomerLevel().getId());
        Assert.assertEquals("Group_Active_test", group.getDisplayName());
        Assert.assertEquals(2, group.getAccounts().size());
        Assert.assertEquals(0, group.getOpenLoanAccounts().size());
        Assert.assertEquals(1, group.getOpenSavingAccounts().size());
        Assert.assertEquals(CustomerStatus.GROUP_ACTIVE.getValue(), group.getCustomerStatus().getId());
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
        Assert.assertNotNull(center);
        Assert.assertEquals("MyCenter", center.getDisplayName());
        Assert.assertEquals(2, center.getAccounts().size());
        Assert.assertEquals(0, center.getOpenLoanAccounts().size());
        Assert.assertEquals(1, center.getOpenSavingAccounts().size());
        Assert.assertEquals(CustomerStatus.CENTER_ACTIVE.getValue(), center.getCustomerStatus().getId());
        StaticHibernateUtil.closeSession();
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
    }

    public void testFailureGet() throws Exception {
        center = createCenter("MyCenter");
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getCustomer(center.getCustomerId());
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testFailureGetBySystemId() throws Exception {
        center = createCenter("MyCenter");
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.findBySystemId(center.getGlobalCustNum());
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testGetCustomerChecklist() throws Exception {

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        CustomerCheckListBO checklistCenter = TestObjectFactory.createCustomerChecklist(center.getCustomerLevel()
                .getId(), center.getCustomerStatus().getId(), CheckListConstants.STATUS_ACTIVE);
        CustomerCheckListBO checklistClient = TestObjectFactory.createCustomerChecklist(client.getCustomerLevel()
                .getId(), client.getCustomerStatus().getId(), CheckListConstants.STATUS_INACTIVE);
        CustomerCheckListBO checklistGroup = TestObjectFactory.createCustomerChecklist(
                group.getCustomerLevel().getId(), group.getCustomerStatus().getId(), CheckListConstants.STATUS_ACTIVE);
        StaticHibernateUtil.closeSession();
        Assert.assertEquals(1, service.getStatusChecklist(center.getCustomerStatus().getId(),
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
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        StaticHibernateUtil.closeSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getStatusChecklist(center.getCustomerStatus().getId(), center.getCustomerLevel().getId());
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testRetrieveAllCustomerStatusList() throws NumberFormatException, SystemException, ApplicationException {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        Assert.assertEquals(2, service.retrieveAllCustomerStatusList(center.getCustomerLevel().getId()).size());
    }

    public void testFailureRetrieveAllCustomerStatusList() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getStatusChecklist(center.getCustomerStatus().getId(), center.getCustomerLevel().getId());
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testGetAllCustomerNotes() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        center.addCustomerNotes(TestObjectFactory.getCustomerNote("Test Note", center));
        TestObjectFactory.updateObject(center);
        Assert.assertEquals(1, service.getAllCustomerNotes(center.getCustomerId()).getSize());
        for (CustomerNoteEntity note : center.getCustomerNotes()) {
            Assert.assertEquals("Test Note", note.getComment());
            Assert.assertEquals(center.getPersonnel().getPersonnelId(), note.getPersonnel().getPersonnelId());
        }
        center = (CenterBO) (StaticHibernateUtil.getSessionTL().get(CenterBO.class, Integer.valueOf(center
                .getCustomerId())));
    }

    public void testGetAllCustomerNotesWithZeroNotes() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        Assert.assertEquals(0, service.getAllCustomerNotes(center.getCustomerId()).getSize());
        Assert.assertEquals(0, center.getCustomerNotes().size());
    }

    public void testGetStatusName() throws Exception {
        createInitialCustomers();
        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, center.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.CENTER);
        String statusNameForCenter = service.getStatusName(TestObjectFactory.TEST_LOCALE, center.getStatus(),
                CustomerLevel.CENTER);
        Assert.assertEquals("Active", statusNameForCenter);

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, group.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.GROUP);
        String statusNameForGroup = service.getStatusName(TestObjectFactory.TEST_LOCALE, group.getStatus(),
                CustomerLevel.GROUP);
        Assert.assertEquals("Active", statusNameForGroup);

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, client.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.CLIENT);
        String statusNameForClient = service.getStatusName(TestObjectFactory.TEST_LOCALE, client.getStatus(),
                CustomerLevel.CLIENT);
        Assert.assertNotNull("Active", statusNameForClient);
    }

    public void testGetFlagName() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client", CustomerStatus.CLIENT_CLOSED, group);

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, client.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.CLIENT);
        String flagNameForClient = service.getFlagName(TestObjectFactory.TEST_LOCALE,
                CustomerStatusFlag.CLIENT_CLOSED_DUPLICATE, CustomerLevel.CLIENT);
        Assert.assertNotNull("Duplicate", flagNameForClient);

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, group.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.GROUP);
        String flagNameForGroup = service.getFlagName(TestObjectFactory.TEST_LOCALE,
                CustomerStatusFlag.GROUP_CLOSED_DUPLICATE, CustomerLevel.GROUP);
        Assert.assertNotNull("Duplicate", flagNameForGroup);
    }

    public void testGetStatusList() throws Exception {
        createInitialCustomers();
        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, center.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.CENTER);
        List<CustomerStatusEntity> statusListForCenter = service.getStatusList(center.getCustomerStatus(),
                CustomerLevel.CENTER, TestObjectFactory.TEST_LOCALE);
        Assert.assertEquals(1, statusListForCenter.size());

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, group.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.GROUP);
        List<CustomerStatusEntity> statusListForGroup = service.getStatusList(group.getCustomerStatus(),
                CustomerLevel.GROUP, TestObjectFactory.TEST_LOCALE);
        Assert.assertEquals(2, statusListForGroup.size());

        AccountStateMachines.getInstance().initialize(TestObjectFactory.TEST_LOCALE, client.getOffice().getOfficeId(),
                AccountTypes.CUSTOMER_ACCOUNT, CustomerLevel.CLIENT);
        List<CustomerStatusEntity> statusListForClient = service.getStatusList(client.getCustomerStatus(),
                CustomerLevel.CLIENT, TestObjectFactory.TEST_LOCALE);
        Assert.assertEquals(2, statusListForClient.size());
    }

    public void testSearch() throws Exception {

        center = createCenter("MyCenter");
        QueryResult queryResult = service
                .search("MyCenter", Short.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
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
        Assert.assertEquals(1, service
                .getAllClosedAccount(client.getCustomerId(), AccountTypes.LOAN_ACCOUNT.getValue()).size());
        Assert.assertEquals(1, service.getAllClosedAccount(group.getCustomerId(), AccountTypes.LOAN_ACCOUNT.getValue())
                .size());
        Assert.assertEquals(1, service.getAllClosedAccount(client.getCustomerId(),
                AccountTypes.SAVINGS_ACCOUNT.getValue()).size());
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
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();

    }

    public void testGetAllClosedAccountsWhenNoAccountsClosed() throws Exception {
        getCustomer();
        Assert.assertEquals(0, service
                .getAllClosedAccount(client.getCustomerId(), AccountTypes.LOAN_ACCOUNT.getValue()).size());
        Assert.assertEquals(0, service.getAllClosedAccount(group.getCustomerId(), AccountTypes.LOAN_ACCOUNT.getValue())
                .size());
        Assert.assertEquals(0, service.getAllClosedAccount(client.getCustomerId(),
                AccountTypes.SAVINGS_ACCOUNT.getValue()).size());
    }

    public void testGetActiveCentersUnderUser() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("center", meeting, Short.valueOf("1"), Short.valueOf("1"));
        PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
        List<CustomerBO> customers = service.getActiveCentersUnderUser(personnel);
        Assert.assertNotNull(customers);
        Assert.assertEquals(1, customers.size());
    }

    public void testFailureGetActiveCentersUnderUser() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("center", meeting, Short.valueOf("1"), Short.valueOf("1"));
        PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getActiveCentersUnderUser(personnel);
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();

    }

    public void testgetGroupsUnderUser() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("center", meeting, Short.valueOf("1"), Short.valueOf("1"));
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
        List<CustomerBO> customers = service.getGroupsUnderUser(personnel);
        Assert.assertNotNull(customers);
        Assert.assertEquals(1, customers.size());
    }

    public void testFailuregetGroupsUnderUser() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("center", meeting, Short.valueOf("1"), Short.valueOf("1"));
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getGroupsUnderUser(personnel);
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();

    }

    public void testGetCustomersByLevelId() throws Exception {
        createInitialCustomers();

        List<CustomerBO> client = service.getCustomersByLevelId(Short.parseShort("1"));
        Assert.assertNotNull(client);
        Assert.assertEquals(1, client.size());

        List<CustomerBO> group = service.getCustomersByLevelId(Short.parseShort("2"));
        Assert.assertNotNull(group);
        Assert.assertEquals(1, group.size());

        List<CustomerBO> center = service.getCustomersByLevelId(Short.parseShort("3"));
        Assert.assertNotNull(center);
        Assert.assertEquals(1, client.size());

    }

    private void createInitialCustomers() throws Exception {
        center = createCenter("Center_Active_test");
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private CenterBO createCenter(String name) throws Exception {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createWeeklyFeeCenter(name, meeting);
    }

    private LoanBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
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
        Assert.assertEquals(25d, dropOutRate.doubleValue(), 0.001);
    }

    public void testVeryPoorClientDropoutRate() throws Exception {
        expect(customerPersistenceMock.getVeryPoorDropOutClientsCountForOffice(OFFICE)).andReturn(ONE);
        expect(customerPersistenceMock.getVeryPoorActiveOrHoldClientCountForOffice(OFFICE)).andReturn(THREE);
        replay(customerPersistenceMock);
        BigDecimal veryPoorClientDropoutRateForOffice = customerBusinessServiceWithMock
                .getVeryPoorClientDropoutRateForOffice(OFFICE);
        Assert.assertEquals(25d, veryPoorClientDropoutRateForOffice.doubleValue(), 0.001);
        verify(customerPersistenceMock);
    }
}
