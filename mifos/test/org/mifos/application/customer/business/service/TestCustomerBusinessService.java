package org.mifos.application.customer.business.service;

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
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
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
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.customer.business.CustomerActivityEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFeeScheduleEntity;
import org.mifos.application.customer.business.CustomerNoteEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.TestCustomerAccountBO;
import org.mifos.application.customer.business.TestCustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.business.CenterPerformanceHistory;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerRecentActivityView;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.customer.util.helpers.LoanCycleCounter;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerBusinessService extends MifosTestCase {
	private CustomerBO center;

	private GroupBO group;

	private CustomerBO client;

	private AccountBO account;

	private LoanBO groupAccount;

	private LoanBO clientAccount;

	private SavingsBO clientSavingsAccount;

	private MeetingBO meeting;

	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsOfferingBO savingsOffering;

	private SavingsBO savingsBO;

	private MifosCurrency currency = Configuration.getInstance()
			.getSystemConfig().getCurrency();

	private LoanOfferingBO loanOffering = null;

	private CustomerBusinessService service;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		service = (CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer);
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
			HibernateUtil.closeSession();
		}
		catch (Exception e) {
			// throwing here tends to mask other failures
			e.printStackTrace();
		}
		super.tearDown();
	}

	public void testSearchGropAndClient() throws Exception {
		createInitialCustomers();
		QueryResult queryResult = new CustomerBusinessService()
				.searchGroupClient("cl", Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1, queryResult.getSize());
		assertEquals(1, queryResult.get(0, 10).size());

	}

	public void testFailureSearchGropAndClient() throws Exception {
		createInitialCustomers();
		TestObjectFactory.simulateInvalidConnection();
		try {
			new CustomerBusinessService().searchGroupClient("cl", Short
					.valueOf("1"));
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testSearchCustForSavings() throws Exception {
		createInitialCustomers();
		QueryResult queryResult = new CustomerBusinessService()
				.searchCustForSavings("c", Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(2, queryResult.getSize());
		assertEquals(2, queryResult.get(0, 10).size());

	}

	public void testFailureSearchCustForSavings() throws Exception {
		createInitialCustomers();
		TestObjectFactory.simulateInvalidConnection();
		try {
			new CustomerBusinessService().searchCustForSavings("c", Short
					.valueOf("1"));
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testFetchLoanCycleCounter() throws Exception {
		account = getLoanAccount();
		loanOffering.setLoanCounter(true);
		TestObjectFactory.updateObject(loanOffering);
		TestObjectFactory.flushandCloseSession();
		List<LoanCycleCounter> loanCycleCounters = ((CustomerBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Customer))
				.fetchLoanCycleCounter(group.getCustomerId());
		for (LoanCycleCounter loanCycleCounter : loanCycleCounters) {
			assertEquals(loanCycleCounter.getOfferingName(), "Loan");
			assertEquals(loanCycleCounter.getCounter(), 1);
			break;
		}
	}

	public void testFailureFetchLoanCycleCounter() throws Exception {
		account = getLoanAccount();
		loanOffering.setLoanCounter(true);
		TestObjectFactory.updateObject(loanOffering);
		TestObjectFactory.flushandCloseSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			((CustomerBusinessService) ServiceFactory.getInstance()
					.getBusinessService(BusinessServiceName.Customer))
					.fetchLoanCycleCounter(group.getCustomerId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testGetAllActivityView() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		List<CustomerRecentActivityView> customerActivityViewList = service
				.getAllActivityView(center.getGlobalCustNum());
		assertEquals(0, customerActivityViewList.size());
		UserContext uc = TestObjectFactory.getUserContext();
		center.getCustomerAccount().setUserContext(uc);
		center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		Set<CustomerActivityEntity> customerActivityDetails = center
				.getCustomerAccount().getCustomerActivitDetails();
		assertEquals(1, customerActivityDetails.size());
		for (CustomerActivityEntity customerActivityEntity : customerActivityDetails) {
			assertEquals(new Money("100"), customerActivityEntity.getAmount());
		}
		List<CustomerRecentActivityView> customerActivityView = service
				.getAllActivityView(center.getGlobalCustNum());
		assertEquals(1, customerActivityView.size());
		for (CustomerRecentActivityView view : customerActivityView) {
			assertEquals(new Money("100").toString(), view.getAmount());
			assertEquals("Amnt waived", view.getDescription());
			assertEquals(TestObjectFactory.getContext().getName(), view
					.getPostedBy());
		}
	}

	public void testFailureGetRecentActivityView() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getAllActivityView(center.getGlobalCustNum());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}

	public void testGetRecentActivityView() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		List<CustomerRecentActivityView> customerActivityViewList = service
				.getAllActivityView(center.getGlobalCustNum());
		assertEquals(0, customerActivityViewList.size());
		UserContext uc = TestObjectFactory.getUserContext();
		center.getCustomerAccount().setUserContext(uc);
		center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
		TestObjectFactory.flushandCloseSession();

		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		for (AccountActionDateEntity accountAction : center
				.getCustomerAccount().getAccountActionDates()) {
			CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) accountAction;
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				Set<AccountFeesActionDetailEntity> accountFeesActionDetails = accountActionDateEntity
						.getAccountFeesActionDetails();
				for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {
					TestCustomerAccountBO
							.setFeeAmount(
									(CustomerFeeScheduleEntity) accountFeesActionDetailEntity,
									new Money("100"));
				}
			}
		}
		TestObjectFactory.updateObject(center);
		center.getCustomerAccount().setUserContext(uc);
		center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
		TestObjectFactory.flushandCloseSession();

		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		for (AccountActionDateEntity accountAction : center
				.getCustomerAccount().getAccountActionDates()) {
			CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) accountAction;
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				Set<AccountFeesActionDetailEntity> accountFeesActionDetails = accountActionDateEntity
						.getAccountFeesActionDetails();
				for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {
					TestCustomerAccountBO
							.setFeeAmount(
									(CustomerFeeScheduleEntity) accountFeesActionDetailEntity,
									new Money("100"));
				}
			}
		}
		TestObjectFactory.updateObject(center);
		center.getCustomerAccount().setUserContext(uc);
		center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
		TestObjectFactory.flushandCloseSession();

		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		for (AccountActionDateEntity accountAction : center
				.getCustomerAccount().getAccountActionDates()) {
			CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) accountAction;
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("3"))) {
				Set<AccountFeesActionDetailEntity> accountFeesActionDetails = accountActionDateEntity
						.getAccountFeesActionDetails();
				for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {
					TestCustomerAccountBO
					.setFeeAmount(
							(CustomerFeeScheduleEntity) accountFeesActionDetailEntity,new Money("20"));
				}
			}
		}
		TestObjectFactory.updateObject(center);
		center.getCustomerAccount().setUserContext(uc);
		center.getCustomerAccount().waiveAmountDue(WaiveEnum.ALL);
		TestObjectFactory.flushandCloseSession();

		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		Set<CustomerActivityEntity> customerActivityDetails = center
				.getCustomerAccount().getCustomerActivitDetails();
		assertEquals(3, customerActivityDetails.size());
		for (CustomerActivityEntity customerActivityEntity : customerActivityDetails) {
			assertEquals(new Money("100"), customerActivityEntity.getAmount());
		}

		List<CustomerRecentActivityView> customerActivityView = service
				.getRecentActivityView(center.getCustomerId());
		assertEquals(3, customerActivityView.size());
		for (CustomerRecentActivityView view : customerActivityView) {
			assertEquals(new Money("100").toString(), view.getAmount());
			assertEquals(TestObjectFactory.getContext().getName(), view
					.getPostedBy());
		}
	}

	public void testFindBySystemId() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
		savingsBO = getSavingsAccount(group, "fsaf5", "ads5");
		HibernateUtil.closeSession();
		group = (GroupBO) service.findBySystemId(group.getGlobalCustNum());
		assertEquals("Group_Active_test", group.getDisplayName());
		assertEquals(2, group.getAccounts().size());
		assertEquals(0, group.getOpenLoanAccounts().size());
		assertEquals(1, group.getOpenSavingAccounts().size());
		assertEquals(CustomerStatus.GROUP_ACTIVE.getValue(), group
				.getCustomerStatus().getId());
		HibernateUtil.closeSession();
		savingsBO = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,
				savingsBO.getAccountId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
	}

	public void testgetBySystemId() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
		savingsBO = getSavingsAccount(group, "fsaf5", "ads5");
		HibernateUtil.closeSession();
		group = (GroupBO) service.findBySystemId(group.getGlobalCustNum(),
				group.getCustomerLevel().getId());
		assertEquals("Group_Active_test", group.getDisplayName());
		assertEquals(2, group.getAccounts().size());
		assertEquals(0, group.getOpenLoanAccounts().size());
		assertEquals(1, group.getOpenSavingAccounts().size());
		assertEquals(CustomerStatus.GROUP_ACTIVE.getValue(), group
				.getCustomerStatus().getId());
		HibernateUtil.closeSession();
		savingsBO = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,
				savingsBO.getAccountId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
	}

	public void testSuccessfulGet() throws Exception {
		center = createCenter("MyCenter");
		savingsBO = getSavingsAccount(center, "fsaf5", "ads5");
		HibernateUtil.closeSession();
		center = service.getCustomer(center.getCustomerId());
		assertNotNull(center);
		assertEquals("MyCenter", center.getDisplayName());
		assertEquals(2, center.getAccounts().size());
		assertEquals(0, center.getOpenLoanAccounts().size());
		assertEquals(1, center.getOpenSavingAccounts().size());
		assertEquals(CustomerStatus.CENTER_ACTIVE.getValue(), center
				.getCustomerStatus().getId());
		HibernateUtil.closeSession();
		savingsBO = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,
				savingsBO.getAccountId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
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
		HibernateUtil.closeSession();
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
		HibernateUtil.closeSession();
	}

	public void testGetCenterPerformanceHistory() throws Exception {
		Money totalLoan = new Money();
		Money totalSavings = new Money();
		Money totalPortfolioAtRisk = new Money();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		CenterBO center1 = TestObjectFactory.createCenter(
				"Center_Active_test1", meeting);
		GroupBO group1 = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_ACTIVE, group1);
		account = getSavingsAccountWithBalance(center, meeting,
				"savings prd123", "xyz1");
		AccountBO account1 = getSavingsAccountWithBalance(client, meeting,
				"savings prd1231", "xyz2");
		AccountBO account2 = getSavingsAccountWithBalance(client2, meeting,
				"savings prd1232", "xyz3");
		AccountBO account3 = getSavingsAccountWithBalance(client3, meeting,
				"savings prd1233", "xyz4");
		AccountBO account4 = getSavingsAccountWithBalance(group1, meeting,
				"savings prd1234", "xyz5");
		AccountBO account5 = getSavingsAccountWithBalance(group, meeting,
				"savings prd1235", "xyz6");
		AccountBO account6 = getSavingsAccountWithBalance(center1, meeting,
				"savings prd1236", "xyz7");

		AccountBO account7 = getLoanAccount(client, meeting, "fdbdhgsgh",
				"54hg");
		changeFirstInstallmentDateToPastDate(account7);
		AccountBO account8 = getLoanAccount(client2, meeting, "dsafasdf",
				"32fs");
		changeFirstInstallmentDateToPastDate(account8);
		AccountBO account9 = getLoanAccount(client, meeting, "afvasfgfdg",
				"a12w");
		changeFirstInstallmentDateToPastDate(account9);
		AccountBO account10 = getLoanAccount(group, meeting, "afadsff", "23e");
		TestCustomerBO.setCustomerStatus(client2,new CustomerStatusEntity(
				CustomerStatus.CLIENT_CLOSED));
		
		TestObjectFactory.updateObject(client2);
		client2 = (ClientBO) TestObjectFactory.getObject(ClientBO.class,
				client2.getCustomerId());
		TestCustomerBO.setCustomerStatus(client3,new CustomerStatusEntity(
				CustomerStatus.CLIENT_CANCELLED));
		TestObjectFactory.updateObject(client3);
		client3 = (ClientBO) TestObjectFactory.getObject(ClientBO.class,
				client3.getCustomerId());

		CenterPerformanceHistory centerPerformanceHistory = service
				.getCenterPerformanceHistory(center.getSearchId(), Short
						.valueOf("3"));
		totalLoan = centerPerformanceHistory.getTotalOutstandingPortfolio();
		totalSavings = centerPerformanceHistory.getTotalSavings();
		totalPortfolioAtRisk = centerPerformanceHistory.getPortfolioAtRisk();
		assertEquals(1, centerPerformanceHistory.getNumberOfGroups().intValue());
		assertEquals(1, centerPerformanceHistory.getNumberOfClients()
				.intValue());
		assertEquals(new Money("2400.0"), totalLoan);
		assertEquals(new Money("400.0"), totalSavings);
		assertEquals(new Money("0.5"), totalPortfolioAtRisk);

		account1 = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(account1.getAccountId())));
		account2 = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(account2.getAccountId())));
		account3 = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(account3.getAccountId())));
		account4 = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(account4.getAccountId())));
		account5 = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(account5.getAccountId())));
		account6 = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(account6.getAccountId())));
		account7 = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(account7.getAccountId())));
		account8 = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(account8.getAccountId())));
		account9 = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(account9.getAccountId())));
		account10 = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(account10.getAccountId())));
		account = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(account.getAccountId())));
		client = (ClientBO) (HibernateUtil.getSessionTL().get(ClientBO.class,
				new Integer(client.getCustomerId())));
		group = (GroupBO) (HibernateUtil.getSessionTL().get(GroupBO.class,
				new Integer(group.getCustomerId())));
		center = (CenterBO) (HibernateUtil.getSessionTL().get(CenterBO.class,
				new Integer(center.getCustomerId())));
		client2 = (ClientBO) (HibernateUtil.getSessionTL().get(ClientBO.class,
				new Integer(client2.getCustomerId())));
		client3 = (ClientBO) (HibernateUtil.getSessionTL().get(ClientBO.class,
				new Integer(client3.getCustomerId())));
		group1 = (GroupBO) (HibernateUtil.getSessionTL().get(GroupBO.class,
				new Integer(group1.getCustomerId())));
		center1 = (CenterBO) (HibernateUtil.getSessionTL().get(CenterBO.class,
				new Integer(center1.getCustomerId())));
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
	}

	public void testGetCustomerChecklist() throws Exception {

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		CustomerCheckListBO checklistCenter = TestObjectFactory
				.createCustomerChecklist(center.getCustomerLevel().getId(),
						center.getCustomerStatus().getId(),
						CheckListConstants.STATUS_ACTIVE);
		CustomerCheckListBO checklistClient = TestObjectFactory
				.createCustomerChecklist(client.getCustomerLevel().getId(),
						client.getCustomerStatus().getId(),
						CheckListConstants.STATUS_INACTIVE);
		CustomerCheckListBO checklistGroup = TestObjectFactory
				.createCustomerChecklist(group.getCustomerLevel().getId(),
						group.getCustomerStatus().getId(),
						CheckListConstants.STATUS_ACTIVE);
		HibernateUtil.closeSession();
		assertEquals(1, service.getStatusChecklist(
				center.getCustomerStatus().getId(),
				center.getCustomerLevel().getId()).size());
		client = (ClientBO) (HibernateUtil.getSessionTL().get(ClientBO.class,
				new Integer(client.getCustomerId())));
		group = (GroupBO) (HibernateUtil.getSessionTL().get(GroupBO.class,
				new Integer(group.getCustomerId())));
		center = (CenterBO) (HibernateUtil.getSessionTL().get(CenterBO.class,
				new Integer(center.getCustomerId())));
		checklistCenter = (CustomerCheckListBO) (HibernateUtil.getSessionTL()
				.get(CheckListBO.class, new Short(checklistCenter
						.getChecklistId())));
		checklistClient = (CustomerCheckListBO) (HibernateUtil.getSessionTL()
				.get(CheckListBO.class, new Short(checklistClient
						.getChecklistId())));
		checklistGroup = (CustomerCheckListBO) (HibernateUtil.getSessionTL()
				.get(CheckListBO.class, new Short(checklistGroup
						.getChecklistId())));
		TestObjectFactory.cleanUp(checklistCenter);
		TestObjectFactory.cleanUp(checklistClient);
		TestObjectFactory.cleanUp(checklistGroup);

	}

	public void testFailureGetCustomerChecklist() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getStatusChecklist(center.getCustomerStatus().getId(),
					center.getCustomerLevel().getId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}

	public void testRetrieveAllCustomerStatusList()
			throws NumberFormatException, SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		assertEquals(2, service.retrieveAllCustomerStatusList(
				center.getCustomerLevel().getId()).size());
	}

	public void testFailureRetrieveAllCustomerStatusList() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getStatusChecklist(center.getCustomerStatus().getId(),
					center.getCustomerLevel().getId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}

	public void testGetFormedByPersonnel() throws NumberFormatException,
			SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		assertEquals(1, service.getFormedByPersonnel(
				ClientConstants.LOAN_OFFICER_LEVEL,
				center.getOffice().getOfficeId()).size());
	}

	public void testFailureGetFormedByPersonnel() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getFormedByPersonnel(
					ClientConstants.LOAN_OFFICER_LEVEL,
					center.getOffice().getOfficeId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}

	public void testGetAllCustomerNotes() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		center.addCustomerNotes(TestObjectFactory.getCustomerNote("Test Note",
				center));
		TestObjectFactory.updateObject(center);
		assertEquals(1, service.getAllCustomerNotes(center.getCustomerId())
				.getSize());
		for (CustomerNoteEntity note : center.getCustomerNotes()) {
			assertEquals("Test Note", note.getComment());
			assertEquals(center.getPersonnel().getPersonnelId(), note
					.getPersonnel().getPersonnelId());
		}
		center = (CenterBO) (HibernateUtil.getSessionTL().get(CenterBO.class,
				new Integer(center.getCustomerId())));
	}

	public void testGetAllCustomerNotesWithZeroNotes() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		assertEquals(0, service.getAllCustomerNotes(center.getCustomerId())
				.getSize());
		assertEquals(0, center.getCustomerNotes().size());
	}

	public void testGetStatusName() throws Exception {
		createInitialCustomers();
		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				center.getOffice().getOfficeId(), AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.CENTER);
		String statusNameForCenter = service.getStatusName(TestObjectFactory
				.getUserContext().getLocaleId(), center.getStatus(),
				CustomerLevel.CENTER);
		assertEquals("Active", statusNameForCenter);

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				group.getOffice().getOfficeId(), AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.GROUP);
		String statusNameForGroup = service.getStatusName(TestObjectFactory
				.getUserContext().getLocaleId(), group.getStatus(),
				CustomerLevel.GROUP);
		assertEquals("Active", statusNameForGroup);

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				client.getOffice().getOfficeId(), AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.CLIENT);
		String statusNameForClient = service.getStatusName(TestObjectFactory
				.getUserContext().getLocaleId(), client.getStatus(),
				CustomerLevel.CLIENT);
		assertNotNull("Active", statusNameForClient);
	}

	public void testGetFlagName() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test",
				meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("client",
				CustomerStatus.CLIENT_CLOSED, group);

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				client.getOffice().getOfficeId(), AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.CLIENT);
		String flagNameForClient = service.getFlagName(TestObjectFactory
				.getUserContext().getLocaleId(),
				CustomerStatusFlag.CLIENT_CLOSED_DUPLICATE,
				CustomerLevel.CLIENT);
		assertNotNull("Duplicate", flagNameForClient);

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				group.getOffice().getOfficeId(), AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.GROUP);
		String flagNameForGroup = service.getFlagName(TestObjectFactory
				.getUserContext().getLocaleId(),
				CustomerStatusFlag.GROUP_CLOSED_DUPLICATE, CustomerLevel.GROUP);
		assertNotNull("Duplicate", flagNameForGroup);
	}

	public void testGetStatusList() throws Exception {
		createInitialCustomers();
		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				center.getOffice().getOfficeId(), AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.CENTER);
		List<CustomerStatusEntity> statusListForCenter = service.getStatusList(
				center.getCustomerStatus(), CustomerLevel.CENTER,
				TestObjectFactory.getUserContext().getLocaleId());
		assertEquals(1, statusListForCenter.size());

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				group.getOffice().getOfficeId(), AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.GROUP);
		List<CustomerStatusEntity> statusListForGroup = service.getStatusList(
				group.getCustomerStatus(), CustomerLevel.GROUP,
				TestObjectFactory.getUserContext().getLocaleId());
		assertEquals(2, statusListForGroup.size());

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				client.getOffice().getOfficeId(), AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.CLIENT);
		List<CustomerStatusEntity> statusListForClient = service.getStatusList(
				client.getCustomerStatus(), CustomerLevel.CLIENT,
				TestObjectFactory.getUserContext().getLocaleId());
		assertEquals(2, statusListForClient.size());
	}

	public void testSearch() throws Exception {

		center = createCenter("MyCenter");
		QueryResult queryResult = service.search("MyCenter",
				Short.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1, queryResult.getSize());
	}

	public void testGetAllClosedAccounts() throws Exception {
		getCustomer();
		groupAccount.changeStatus(AccountState.LOANACC_CANCEL.getValue(),
				AccountStateFlag.LOAN_WITHDRAW.getValue(),
				"WITHDRAW LOAN ACCOUNT");
		clientAccount.changeStatus(AccountState.LOANACC_WRITTENOFF.getValue(),
				null, "WITHDRAW LOAN ACCOUNT");
		clientSavingsAccount.changeStatus(AccountState.SAVINGS_ACC_CANCEL
				.getValue(), AccountStateFlag.SAVINGS_REJECTED.getValue(),
				"WITHDRAW LOAN ACCOUNT");
		TestObjectFactory.updateObject(groupAccount);
		TestObjectFactory.updateObject(clientAccount);
		TestObjectFactory.updateObject(clientSavingsAccount);
		assertEquals(1, service.getAllClosedAccount(client.getCustomerId(),
				AccountTypes.LOANACCOUNT.getValue()).size());
		assertEquals(1, service.getAllClosedAccount(group.getCustomerId(),
				AccountTypes.LOANACCOUNT.getValue()).size());
		assertEquals(1, service.getAllClosedAccount(client.getCustomerId(),
				AccountTypes.SAVINGSACCOUNT.getValue()).size());
	}
	
	public void testFailureGetAllClosedAccounts() throws Exception {
		getCustomer();
		groupAccount.changeStatus(AccountState.LOANACC_CANCEL.getValue(),
				AccountStateFlag.LOAN_WITHDRAW.getValue(),
				"WITHDRAW LOAN ACCOUNT");
		clientAccount.changeStatus(AccountState.LOANACC_WRITTENOFF.getValue(),
				null, "WITHDRAW LOAN ACCOUNT");
		clientSavingsAccount.changeStatus(AccountState.SAVINGS_ACC_CANCEL
				.getValue(), AccountStateFlag.SAVINGS_REJECTED.getValue(),
				"WITHDRAW LOAN ACCOUNT");
		TestObjectFactory.updateObject(groupAccount);
		TestObjectFactory.updateObject(clientAccount);
		TestObjectFactory.updateObject(clientSavingsAccount);
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getAllClosedAccount(client.getCustomerId(),
					AccountTypes.LOANACCOUNT.getValue());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();

	}

	public void testGetAllClosedAccountsWhenNoAccountsClosed() throws Exception {
		getCustomer();
		assertEquals(0, service.getAllClosedAccount(client.getCustomerId(),
				AccountTypes.LOANACCOUNT.getValue()).size());
		assertEquals(0, service.getAllClosedAccount(group.getCustomerId(),
				AccountTypes.LOANACCOUNT.getValue()).size());
		assertEquals(0, service.getAllClosedAccount(client.getCustomerId(),
				AccountTypes.SAVINGSACCOUNT.getValue()).size());
	}

	public void testGetActiveCentersUnderUser() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("center", meeting, Short
				.valueOf("1"), Short.valueOf("1"));
		PersonnelBO personnel = TestObjectFactory.getPersonnel(Short
				.valueOf("1"));
		List<CustomerBO> customers = service
				.getActiveCentersUnderUser(personnel);
		assertNotNull(customers);
		assertEquals(1, customers.size());
	}
	
	public void testFailureGetActiveCentersUnderUser() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("center", meeting, Short
				.valueOf("1"), Short.valueOf("1"));
		PersonnelBO personnel = TestObjectFactory.getPersonnel(Short
				.valueOf("1"));
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getActiveCentersUnderUser(personnel);
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();

	}

	public void testgetGroupsUnderUser() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("center", meeting, Short
				.valueOf("1"), Short.valueOf("1"));
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		PersonnelBO personnel = TestObjectFactory.getPersonnel(Short
				.valueOf("1"));
		List<CustomerBO> customers = service.getGroupsUnderUser(personnel);
		assertNotNull(customers);
		assertEquals(1, customers.size());
	}
	
	public void testFailuregetGroupsUnderUser() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("center", meeting, Short
				.valueOf("1"), Short.valueOf("1"));
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		PersonnelBO personnel = TestObjectFactory.getPersonnel(Short
				.valueOf("1"));
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getGroupsUnderUser(personnel);
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();

	}

	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting,
			String offeringName, String shortName) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				offeringName, shortName, Short.valueOf("2"), startDate, Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				Short.valueOf("5"), startDate, loanOffering);

	}

	private AccountBO getSavingsAccountWithBalance(CustomerBO customer,
			MeetingBO meeting, String prdofferingName, String shortName)
			throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		SavingsOfferingBO savingsOffering = TestObjectFactory
				.createSavingsOffering(prdofferingName, shortName, Short
						.valueOf("2"), new Date(System.currentTimeMillis()),
						Short.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		SavingsBO savingsBO = TestObjectFactory.createSavingsAccount("432434",
				customer, Short.valueOf("16"), startDate, savingsOffering);
		HibernateUtil.closeSession();
		savingsBO = (SavingsBO) (new AccountPersistence().getAccount(savingsBO
				.getAccountId()));
		TestSavingsBO.setBalance(savingsBO,new Money());
		Money enteredAmount = new Money(currency, "100.0");
		PaymentData paymentData = new PaymentData(enteredAmount, savingsBO
				.getPersonnel(), Short.valueOf("1"), new Date(System
				.currentTimeMillis()));
		paymentData.setCustomer(customer);
		paymentData.setRecieptDate(new Date(System.currentTimeMillis()));
		paymentData.setRecieptNum("34244");
		AccountActionDateEntity accountActionDate = savingsBO
				.getAccountActionDate(Short.valueOf("1"));

		SavingsPaymentData savingsPaymentData = new SavingsPaymentData(
				accountActionDate);
		paymentData.addAccountPaymentData(savingsPaymentData);
		savingsBO.applyPayment(paymentData);

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		SavingsBO savingsNew = (SavingsBO) (HibernateUtil.getSessionTL().get(
				SavingsBO.class, new Integer(savingsBO.getAccountId())));
		return savingsNew;

	}

	private void changeFirstInstallmentDateToPastDate(AccountBO accountBO)
			throws Exception {
		accountBO = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(accountBO.getAccountId())));
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - 40);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			TestLoanBO.setActionDate(accountActionDateEntity,new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
			break;
		}
		TestObjectFactory.updateObject(accountBO);
	}

	private void createInitialCustomers() throws Exception {
		center = createCenter("Center_Active_test");
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	private CenterBO createCenter(String name) throws Exception {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createCenter(name,
				meeting);
	}

	private LoanBO getLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		loanOffering = TestObjectFactory.createLoanOffering("Loan", Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
				.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"), Short
				.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}

	private SavingsBO getSavingsAccount(CustomerBO customerBO,
			String offeringName, String shortName) throws Exception {
		savingsOffering = helper.createSavingsOffering(offeringName, shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017",
				customerBO, AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}

	private void getCustomer() throws Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering1 = TestObjectFactory.createLoanOffering(
				"Loanwer", "43fs", Short.valueOf("2"), new Date(System
						.currentTimeMillis()), Short.valueOf("1"), 300.0, 1.2,
				Short.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				meeting);
		LoanOfferingBO loanOffering2 = TestObjectFactory.createLoanOffering(
				"Loancd123", "vfr", Short.valueOf("2"), new Date(System
						.currentTimeMillis()), Short.valueOf("1"), 300.0, 1.2,
				Short.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				meeting);
		groupAccount = TestObjectFactory.createLoanAccount("42423142341",
				group, Short.valueOf("5"),
				new Date(System.currentTimeMillis()), loanOffering1);
		clientAccount = TestObjectFactory.createLoanAccount("3243", client,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering2);
		clientSavingsAccount = getSavingsAccount(client, "SavingPrd11", "abc2");
	}
}
