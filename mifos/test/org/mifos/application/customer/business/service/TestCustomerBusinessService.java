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
import org.mifos.application.accounts.business.CustomerActivityEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.business.CenterPerformanceHistory;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerRecentActivityView;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.customer.util.helpers.LoanCycleCounter;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.util.valueobjects.YesNoMaster;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerBusinessService extends MifosTestCase {
	private CustomerBO center;

	private CustomerBO group;

	private CustomerBO client;

	private AccountBO account;

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
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	protected LoanBO getLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		loanOffering = TestObjectFactory.createLoanOffering("Loan", Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
				.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"), Short
				.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}

	public void testFetchLoanCycleCounter() throws Exception {
		account = getLoanAccount();
		YesNoMaster yesNo = new YesNoMaster();
		yesNo.setYesNoMasterID(Short.valueOf("1"));
		loanOffering.setLoanCounter(yesNo);
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

	public void testGetAllActivityView() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
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
	}

	public void testGetRecentActivityView() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
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
					accountFeesActionDetailEntity
							.setFeeAmount(new Money("100"));
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
					accountFeesActionDetailEntity
							.setFeeAmount(new Money("100"));
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
					accountFeesActionDetailEntity.setFeeAmount(new Money("20"));
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
	}

	public void testFindBySystemId() throws PersistenceException,
			ServiceException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		GroupBO groupBO = (GroupBO) service.findBySystemId(group
				.getGlobalCustNum());
		assertEquals("Group_Active_test", groupBO.getDisplayName());
	}

	public void testgetBySystemId() throws PersistenceException,
			ServiceException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		GroupBO groupBO = (GroupBO) service.getBySystemId(group
				.getGlobalCustNum(), group.getCustomerLevel().getId());
		assertEquals("Group_Active_test", groupBO.getDisplayName());
	}

	public void testGetCenterPerformanceHistory() throws Exception {
		Money totalLoan = new Money();
		Money totalSavings = new Money();
		Money totalPortfolioAtRisk = new Money();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE,
				center.getSearchId() + ".1", center, new Date(System
						.currentTimeMillis()));
		CenterBO center1 = TestObjectFactory.createCenter(
				"Center_Active_test1", Short.valueOf("13"), "1.5", meeting,
				new Date(System.currentTimeMillis()));
		GroupBO group1 = TestObjectFactory.createGroup("Group1",
				GroupConstants.ACTIVE, center1.getSearchId() + ".1", center1,
				new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("client1",
				ClientConstants.STATUS_ACTIVE, group.getSearchId() + ".1",
				group, new Date(System.currentTimeMillis()));
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_ACTIVE.getValue(), group.getSearchId()
						+ ".2", group, new Date(System.currentTimeMillis()));
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_ACTIVE.getValue(), group1.getSearchId()
						+ ".1", group1, new Date(System.currentTimeMillis()));
		account = getSavingsAccountWithBalance(center, meeting,"savings prd123","xyz1");
		AccountBO account1 = getSavingsAccountWithBalance(client, meeting,"savings prd1231","xyz2");
		AccountBO account2 = getSavingsAccountWithBalance(client2, meeting,"savings prd1232","xyz3");
		AccountBO account3 = getSavingsAccountWithBalance(client3, meeting,"savings prd1233","xyz4");
		AccountBO account4 = getSavingsAccountWithBalance(group1, meeting,"savings prd1234","xyz5");
		AccountBO account5 = getSavingsAccountWithBalance(group, meeting,"savings prd1235","xyz6");
		AccountBO account6 = getSavingsAccountWithBalance(center1, meeting,"savings prd1236","xyz7");

		AccountBO account7 = getLoanAccount(client, meeting);
		changeFirstInstallmentDateToPastDate(account7);
		AccountBO account8 = getLoanAccount(client2, meeting);
		changeFirstInstallmentDateToPastDate(account8);
		AccountBO account9 = getLoanAccount(client, meeting);
		changeFirstInstallmentDateToPastDate(account9);
		AccountBO account10 = getLoanAccount(group, meeting);

		client2.setCustomerStatus(new CustomerStatusEntity(
				CustomerStatus.CLIENT_CLOSED));
		TestObjectFactory.updateObject(client2);
		client2 = (ClientBO) TestObjectFactory.getObject(ClientBO.class,
				client2.getCustomerId());
		client3.setCustomerStatus(new CustomerStatusEntity(
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

	public void testGetCustomerChecklist() throws NumberFormatException,
			SystemException, ApplicationException {

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE,
				"1.4.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("client1",
				ClientConstants.STATUS_ACTIVE, "1.4.1.1", group, new Date(
						System.currentTimeMillis()));
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

	public void testRetrieveAllCustomerStatusList()
			throws NumberFormatException, SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		assertEquals(2, service.retrieveAllCustomerStatusList(
				center.getCustomerLevel().getId()).size());
	}

	public void testGetFormedByPersonnel() throws NumberFormatException,
			SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		assertEquals(1, service.getFormedByPersonnel(
				ClientConstants.LOAN_OFFICER_LEVEL,
				center.getOffice().getOfficeId()).size());
	}

	public void testGetAllCustomerNotes() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		center.addCustomerNotes(TestObjectFactory.getCustomerNote("Test Note",
				center));
		TestObjectFactory.updateObject(center);
		assertEquals(1, service.getAllCustomerNotes(center.getCustomerId())
				.getSize());
		center = (CenterBO) (HibernateUtil.getSessionTL().get(CenterBO.class,
				new Integer(center.getCustomerId())));
	}

	public void testGetStatusName() throws StatesInitializationException,
			InvalidUserException, SystemException, ApplicationException {
		createInitialCustomers();
		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				center.getOffice().getOfficeId(),
				AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.CENTER);
		String statusNameForCenter = service.getStatusName(TestObjectFactory
				.getUserContext().getLocaleId(), center.getStatus(), CustomerLevel.CENTER);
		assertEquals("Active", statusNameForCenter);
		
		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				group.getOffice().getOfficeId(),
				AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.GROUP);
		String statusNameForGroup = service.getStatusName(TestObjectFactory
				.getUserContext().getLocaleId(), group.getStatus(), CustomerLevel.GROUP);
		assertEquals("Active", statusNameForGroup);

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				client.getOffice().getOfficeId(),
				AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.CLIENT);
		String statusNameForClient = service.getStatusName(TestObjectFactory
				.getUserContext().getLocaleId(), client.getStatus(), CustomerLevel.CLIENT);
		assertNotNull("Active", statusNameForClient);
	}

	public void testGetFlagName() throws StatesInitializationException,
			InvalidUserException, SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test",
				CustomerStatus.CENTER_ACTIVE.getValue(), "1.4", meeting,
				new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group",
				CustomerStatus.GROUP_ACTIVE.getValue(), "1.4.1", center,
				new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("client",
				CustomerStatus.CLIENT_CLOSED.getValue(), "1.4.1.1", group,
				new Date(System.currentTimeMillis()));

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				client.getOffice().getOfficeId(),
				AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.CLIENT);
		String flagNameForClient = service.getFlagName(TestObjectFactory
				.getUserContext().getLocaleId(), CustomerStatusFlag.CLIENT_CLOSED_DUPLICATE,
				CustomerLevel.CLIENT);
		assertNotNull("Duplicate", flagNameForClient);
		
		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				group.getOffice().getOfficeId(),
				AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.GROUP);
		String flagNameForGroup = service.getFlagName(TestObjectFactory
				.getUserContext().getLocaleId(), CustomerStatusFlag.GROUP_CLOSED_DUPLICATE,
				CustomerLevel.GROUP);
		assertNotNull("Duplicate", flagNameForGroup);
	}

	public void testGetStatusList() throws StatesInitializationException,
			InvalidUserException, SystemException, ApplicationException {
		createInitialCustomers();
		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				center.getOffice().getOfficeId(),
				AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.CENTER);
		List<CustomerStatusEntity> statusListForCenter = service.getStatusList(
				center.getCustomerStatus(), CustomerLevel.CENTER,
				TestObjectFactory.getUserContext().getLocaleId());
		assertEquals(1, statusListForCenter.size());
		
		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				group.getOffice().getOfficeId(),
				AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.GROUP);
		List<CustomerStatusEntity> statusListForGroup = service.getStatusList(
				group.getCustomerStatus(), CustomerLevel.GROUP,
				TestObjectFactory.getUserContext().getLocaleId());
		assertEquals(2, statusListForGroup.size());

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				client.getOffice().getOfficeId(),
				AccountTypes.CUSTOMERACCOUNT,
				CustomerLevel.CLIENT);
		List<CustomerStatusEntity> statusListForClient = service.getStatusList(
				client.getCustomerStatus(), CustomerLevel.CLIENT,
				TestObjectFactory.getUserContext().getLocaleId());
		assertEquals(2, statusListForClient.size());
	}

	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				Short.valueOf("5"), startDate, loanOffering);

	}

	private AccountBO getSavingsAccountWithBalance(CustomerBO customer,
			MeetingBO meeting,String prdofferingName,String shortName) throws Exception{
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		SavingsOfferingBO savingsOffering = TestObjectFactory
				.createSavingsOffering(prdofferingName,shortName, Short.valueOf("2"),
						new Date(System.currentTimeMillis()), Short
								.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		SavingsBO savingsBO = TestObjectFactory.createSavingsAccount("432434",
				customer, Short.valueOf("16"), startDate, savingsOffering);
		HibernateUtil.closeSession();
		savingsBO = (SavingsBO) (new AccountPersistence()
				.getAccount(savingsBO.getAccountId()));
		savingsBO.setSavingsBalance(new Money());
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
		return savingsBO;

	}

	private void changeFirstInstallmentDateToPastDate(AccountBO accountBO)
			throws Exception{
		AccountPersistence accountPersistence = new AccountPersistence();
		accountBO = (AccountBO) (HibernateUtil.getSessionTL().get(
				AccountBO.class, new Integer(accountBO.getAccountId())));
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - 40);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			accountActionDateEntity.setActionDate(new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
			break;
		}
		TestObjectFactory.updateObject(accountBO);
		HibernateUtil.getTransaction().commit();
	}

	private void createInitialCustomers() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test",
				CustomerStatus.CENTER_ACTIVE.getValue(), "1.4", meeting,
				new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group",
				CustomerStatus.GROUP_ACTIVE.getValue(), "1.4.1", center,
				new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("client",
				CustomerStatus.CLIENT_ACTIVE.getValue(), "1.4.1.1", group,
				new Date(System.currentTimeMillis()));
	}
	
}
