package org.mifos.application.customer.persistence;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.TestAccountFeesEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerNoteEntity;
import org.mifos.application.customer.business.CustomerPerformanceHistoryView;
import org.mifos.application.customer.business.CustomerSearch;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.business.TestCustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerPersistence extends MifosTestCase {

	private MeetingBO meeting;

	private CustomerBO center;

	private CustomerBO client;
	
	private CustomerBO group2;
	private CustomerBO group;

	private AccountBO account;

	private LoanBO groupAccount;

	private LoanBO clientAccount;

	private SavingsBO centerSavingsAccount;

	private SavingsBO groupSavingsAccount;

	private SavingsBO clientSavingsAccount;

	private SavingsOfferingBO savingsOffering;

	private CustomerPersistence customerPersistence = new CustomerPersistence();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
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
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testCustomersUnderLO() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active", meeting);
		List<CustomerView> customers = customerPersistence.getActiveParentList(
				Short.valueOf("1"), CustomerConstants.CENTER_LEVEL_ID, Short
						.valueOf("3"));
		assertEquals(1, customers.size());

	}

	public void testActiveCustomersUnderParent() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		createCustomers(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
		List<CustomerView> customers = customerPersistence
				.getChildrenForParent(center.getCustomerId(), center
						.getSearchId(), center.getOffice().getOfficeId());
		assertEquals(2, customers.size());
	}

	public void testOnHoldCustomersUnderParent() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		createCustomers(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_HOLD, CustomerStatus.CLIENT_HOLD);
		List<CustomerView> customers = customerPersistence
				.getChildrenForParent(center.getCustomerId(), center
						.getSearchId(), center.getOffice().getOfficeId());
		assertEquals(2, customers.size());
	}

	public void testGetLastMeetingDateForCustomer() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		account = getLoanAccount(group, meeting, "adsfdsfsd", "3saf");
		// Date actionDate = new Date(2006,03,13);
		Date meetingDate = customerPersistence
				.getLastMeetingDateForCustomer(center.getCustomerId());
		assertEquals(new Date(getMeetingDates(meeting).getTime()).toString(),
				meetingDate.toString());

	}

	public void testGetProducts() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		account = getLoanAccount(group, meeting, "Loan342423", "1wed");
		Date meetingDate = customerPersistence
				.getLastMeetingDateForCustomer(center.getCustomerId());
		List<PrdOfferingBO> productList = customerPersistence.getLoanProducts(
				meetingDate, "1.1", center.getPersonnel().getPersonnelId());
		assertEquals(1, productList.size());
	}

	private AccountBO getLoanAccount(CustomerBO group, MeetingBO meeting,
			String offeringName, String shortName) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				offeringName, shortName, Short.valueOf("2"), startDate, Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), startDate, loanOffering);

	}

	public void testGetSavingsProducts() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();

		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));

		Date startDate = new Date(System.currentTimeMillis());
		center = createCenter();
		SavingsOfferingBO savingsOffering = TestObjectFactory
				.createSavingsOffering("SavingPrd1", Short.valueOf("2"),
						new Date(System.currentTimeMillis()), Short
								.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		account = TestObjectFactory.createSavingsAccount("432434", center,
				Short.valueOf("16"), startDate, savingsOffering);
		List<PrdOfferingBO> productList = customerPersistence
				.getSavingsProducts(startDate, center.getSearchId(), center
						.getPersonnel().getPersonnelId());
		assertEquals(1, productList.size());
	}

	public void testGetChildernOtherThanClosed() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_CLOSED, group);
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_CANCELLED, group);
		ClientBO client4 = TestObjectFactory.createClient("client4",
				CustomerStatus.CLIENT_PENDING, group);

		List<CustomerBO> customerList = customerPersistence.getChildren(center
				.getSearchId(), center.getOffice().getOfficeId(),
				CustomerLevel.CLIENT, ChildrenStateType.OTHER_THAN_CLOSED);
		assertEquals(new Integer("3").intValue(), customerList.size());

		for (CustomerBO customer : customerList) {
			if (customer.getCustomerId().intValue() == client3.getCustomerId()
					.intValue())
				assertTrue(true);
		}
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client4);
	}

	public void testGetChildernActiveAndHold() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_PARTIAL, group);
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_PENDING, group);
		ClientBO client4 = TestObjectFactory.createClient("client4",
				CustomerStatus.CLIENT_HOLD, group);

		List<CustomerBO> customerList = customerPersistence.getChildren(center
				.getSearchId(), center.getOffice().getOfficeId(),
				CustomerLevel.CLIENT, ChildrenStateType.ACTIVE_AND_ONHOLD);
		assertEquals(new Integer("2").intValue(), customerList.size());

		for (CustomerBO customer : customerList) {
			if (customer.getCustomerId().intValue() == client.getCustomerId()
					.intValue())
				assertTrue(true);
			if (customer.getCustomerId().intValue() == client4.getCustomerId()
					.intValue())
				assertTrue(true);
		}
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client4);
	}

	public void testGetChildernOtherThanClosedAndCancelled() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_CLOSED, group);
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_CANCELLED, group);
		ClientBO client4 = TestObjectFactory.createClient("client4",
				CustomerStatus.CLIENT_PENDING, group);

		List<CustomerBO> customerList = customerPersistence.getChildren(center
				.getSearchId(), center.getOffice().getOfficeId(),
				CustomerLevel.CLIENT,
				ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED);
		assertEquals(new Integer("2").intValue(), customerList.size());

		for (CustomerBO customer : customerList) {
			if (customer.getCustomerId().equals(client4.getCustomerId()))
				assertTrue(true);
		}
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client4);
	}

	public void testGetAllChildern() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_CLOSED, group);
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_CANCELLED, group);
		ClientBO client4 = TestObjectFactory.createClient("client4",
				CustomerStatus.CLIENT_PENDING, group);

		List<CustomerBO> customerList = customerPersistence.getChildren(center
				.getSearchId(), center.getOffice().getOfficeId(),
				CustomerLevel.CLIENT, ChildrenStateType.ALL);
		assertEquals(new Integer("4").intValue(), customerList.size());

		for (CustomerBO customer : customerList) {
			if (customer.getCustomerId().equals(client2.getCustomerId()))
				assertTrue(true);
		}
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client4);
	}

	public void testRetrieveSavingsAccountForCustomer() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		savingsOffering = TestObjectFactory.createSavingsOffering("SavingPrd1",
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0,
				200.0, Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
		UserContext uc = new UserContext();
		uc.setId(Short.valueOf("1"));
		account = TestObjectFactory.createSavingsAccount("000100000000020",
				group, AccountStates.SAVINGS_ACC_APPROVED,
				new java.util.Date(), savingsOffering, uc);
		HibernateUtil.closeSession();
		List<SavingsBO> savingsList = customerPersistence
				.retrieveSavingsAccountForCustomer(group.getCustomerId());
		assertNotNull(savingsList);
		assertEquals(Integer.valueOf("1").intValue(), savingsList.size());
		account = savingsList.get(0);
		group = account.getCustomer();
		center = group.getParentCustomer();
	}

	public void testNumberOfMeetingsAttended() throws Exception {

		BulkEntryBusinessService bulkEntryBusinessService = new BulkEntryBusinessService();

		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE,
				group);
		HibernateUtil.closeSession();
		List<ClientBO> clients = new ArrayList<ClientBO>();
		bulkEntryBusinessService.setClientAttendance(client.getCustomerId(),
				new Date(System.currentTimeMillis()), Short.valueOf("2"),clients);
		HibernateUtil.closeSession();
		bulkEntryBusinessService.saveClientAttendance(clients.get(0));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		clients = new ArrayList<ClientBO>();
		bulkEntryBusinessService.setClientAttendance(client.getCustomerId(),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),clients);
		HibernateUtil.closeSession();
		bulkEntryBusinessService.saveClientAttendance(clients.get(0));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		Calendar currentDate = new GregorianCalendar();
		currentDate.roll(Calendar.DATE, 1);

		clients = new ArrayList<ClientBO>();
		bulkEntryBusinessService.setClientAttendance(client.getCustomerId(),
				new Date(currentDate.getTimeInMillis()), Short.valueOf("4"),clients);
		HibernateUtil.closeSession();
		bulkEntryBusinessService.saveClientAttendance(clients.get(0));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		CustomerPersistence customerPersistence = new CustomerPersistence();
		CustomerPerformanceHistoryView customerPerformanceHistoryView = customerPersistence
				.numberOfMeetings(true, client.getCustomerId());
		assertEquals(2, customerPerformanceHistoryView.getMeetingsAttended()
				.intValue());
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
	}

	public void testNumberOfMeetingsMissed() throws Exception {
		BulkEntryBusinessService bulkEntryBusinessService = new BulkEntryBusinessService();

		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE,
				group);
		HibernateUtil.closeSession();
		List<ClientBO> clients = new ArrayList<ClientBO>();
		bulkEntryBusinessService.setClientAttendance(client.getCustomerId(),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),clients);
		HibernateUtil.closeSession();
		bulkEntryBusinessService.saveClientAttendance(clients.get(0));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		clients = new ArrayList<ClientBO>();
		bulkEntryBusinessService.setClientAttendance(client.getCustomerId(),
				new Date(System.currentTimeMillis()), Short.valueOf("2"),clients);
		HibernateUtil.closeSession();
		bulkEntryBusinessService.saveClientAttendance(clients.get(0));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		Calendar currentDate = new GregorianCalendar();
		currentDate.roll(Calendar.DATE, 1);
		clients = new ArrayList<ClientBO>();
		bulkEntryBusinessService.setClientAttendance(client.getCustomerId(),
				new Date(currentDate.getTimeInMillis()), Short.valueOf("3"),clients);
		HibernateUtil.closeSession();
		bulkEntryBusinessService.saveClientAttendance(clients.get(0));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		CustomerPersistence customerPersistence = new CustomerPersistence();
		CustomerPerformanceHistoryView customerPerformanceHistoryView = customerPersistence
				.numberOfMeetings(false, client.getCustomerId());
		assertEquals(2, customerPerformanceHistoryView.getMeetingsMissed()
				.intValue());
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
	}

	public void testLastLoanAmount() throws PersistenceException, AccountException {
		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client", 
				CustomerStatus.CLIENT_ACTIVE,
				group);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		LoanBO loanBO = TestObjectFactory.createLoanAccount("42423142341",
				client, Short.valueOf("5"),
				new Date(System.currentTimeMillis()), loanOffering);
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		account = (AccountBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				loanBO.getAccountId());
		AccountStateEntity accountStateEntity = new AccountStateEntity(
				AccountState.LOANACC_OBLIGATIONSMET);
		account.setUserContext(TestObjectFactory.getContext());
		account.changeStatus(accountStateEntity.getId(),
				null, "");
		TestObjectFactory.updateObject(account);
		CustomerPersistence customerPersistence = new CustomerPersistence();
		CustomerPerformanceHistoryView customerPerformanceHistoryView = customerPersistence
				.getLastLoanAmount(client.getCustomerId());
		assertEquals("300.0", customerPerformanceHistoryView
				.getLastLoanAmount());
	}

	public void testFindBySystemId() throws PersistenceException,
			ServiceException {
		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
		GroupBO groupBO = (GroupBO) customerPersistence.findBySystemId(group
				.getGlobalCustNum());
		assertEquals(groupBO.getDisplayName(), group.getDisplayName());
	}

	public void testGetBySystemId() throws PersistenceException,
			ServiceException {
		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
		GroupBO groupBO = (GroupBO) customerPersistence.findBySystemId(group
				.getGlobalCustNum(), group.getCustomerLevel().getId());
		assertEquals(groupBO.getDisplayName(), group.getDisplayName());
	}

	public void testOptionalCustomerStates() throws Exception {
		assertEquals(Integer.valueOf(0).intValue(), customerPersistence
				.getCustomerStates(Short.valueOf("0")).size());
	}

	public void testCustomerStatesInUse() throws Exception {
		assertEquals(Integer.valueOf(14).intValue(), customerPersistence
				.getCustomerStates(Short.valueOf("1")).size());
	}

	public void testGetCustomersWithUpdatedMeetings() throws Exception {
		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
		TestCustomerBO.setUpdatedFlag(group.getCustomerMeeting(),YesNoFlag.YES.getValue());
		TestObjectFactory.updateObject(group);
		List<Integer> customerIds = customerPersistence
				.getCustomersWithUpdatedMeetings();
		assertEquals(1, customerIds.size());

	}

	private AccountBO getSavingsAccount(CustomerBO customer, MeetingBO meeting,
			String prdOfferingname, String shortName) throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		SavingsOfferingBO savingsOffering = TestObjectFactory
				.createSavingsOffering(prdOfferingname, shortName, Short
						.valueOf("2"), new Date(System.currentTimeMillis()),
						Short.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		return TestObjectFactory.createSavingsAccount("432434", customer, Short
				.valueOf("16"), startDate, savingsOffering);

	}

	public void testRetrieveAllLoanAccountUnderCustomer()
			throws PersistenceException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = createCenter("center");
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		CenterBO center1 = createCenter("center1");
		GroupBO group1 = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_ACTIVE, group1);
		account = getLoanAccount(group, meeting, "cdfggdfs", "1qdd");
		AccountBO account1 = getLoanAccount(client, meeting, "fdbdhgsgh",
				"54hg");
		AccountBO account2 = getLoanAccount(client2, meeting, "fasdfdsfasdf",
				"1qwe");
		AccountBO account3 = getLoanAccount(client3, meeting, "fdsgdfgfd",
				"543g");
		AccountBO account4 = getLoanAccount(group1, meeting, "fasdf23", "3fds");
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

		List<AccountBO> loansForCenter = customerPersistence
				.retrieveAccountsUnderCustomer(center.getSearchId(), Short
						.valueOf("3"), Short.valueOf("1"));
		assertEquals(3, loansForCenter.size());
		List<AccountBO> loansForGroup = customerPersistence
				.retrieveAccountsUnderCustomer(group.getSearchId(), Short
						.valueOf("3"), Short.valueOf("1"));
		assertEquals(3, loansForGroup.size());
		List<AccountBO> loansForClient = customerPersistence
				.retrieveAccountsUnderCustomer(client.getSearchId(), Short
						.valueOf("3"), Short.valueOf("1"));
		assertEquals(1, loansForClient.size());

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
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = createCenter("new_center");
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		CenterBO center1 = createCenter("new_center1");
		GroupBO group1 = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_CLOSED, group);
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_CANCELLED, group1);
		account = getSavingsAccount(center, meeting, "Savings Prd1", "Abc1");
		AccountBO account1 = getSavingsAccount(client, meeting, "Savings Prd2",
				"Abc2");
		AccountBO account2 = getSavingsAccount(client2, meeting,
				"Savings Prd3", "Abc3");
		AccountBO account3 = getSavingsAccount(client3, meeting,
				"Savings Prd4", "Abc4");
		AccountBO account4 = getSavingsAccount(group1, meeting, "Savings Prd5",
				"Abc5");
		AccountBO account5 = getSavingsAccount(group, meeting, "Savings Prd6",
				"Abc6");
		AccountBO account6 = getSavingsAccount(center1, meeting,
				"Savings Prd7", "Abc7");

		List<AccountBO> savingsForCenter = customerPersistence
				.retrieveAccountsUnderCustomer(center.getSearchId(), Short
						.valueOf("3"), Short.valueOf("2"));
		assertEquals(4, savingsForCenter.size());
		List<AccountBO> savingsForGroup = customerPersistence
				.retrieveAccountsUnderCustomer(group.getSearchId(), Short
						.valueOf("3"), Short.valueOf("2"));
		assertEquals(3, savingsForGroup.size());
		List<AccountBO> savingsForClient = customerPersistence
				.retrieveAccountsUnderCustomer(client.getSearchId(), Short
						.valueOf("3"), Short.valueOf("2"));
		assertEquals(1, savingsForClient.size());

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

	public void testGetAllChildrenForParent() throws NumberFormatException,
			PersistenceException {
		center = createCenter("Center");
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		CenterBO center1 = createCenter("center11");
		GroupBO group1 = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_CLOSED, group);
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_CANCELLED, group1);

		List<CustomerBO> customerList1 = customerPersistence
				.getAllChildrenForParent(center.getSearchId(), Short
						.valueOf("3"), CustomerConstants.CENTER_LEVEL_ID);
		assertEquals(2, customerList1.size());
		List<CustomerBO> customerList2 = customerPersistence
				.getAllChildrenForParent(center.getSearchId(), Short
						.valueOf("3"), CustomerConstants.GROUP_LEVEL_ID);
		assertEquals(1, customerList2.size());

		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center1);
	}

	public void testGetChildrenForParent() throws NumberFormatException,
			SystemException, ApplicationException {
		center = createCenter("center");
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		CenterBO center1 = createCenter("center1");
		GroupBO group1 = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_CLOSED, group);
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_CANCELLED, group1);
		List<Integer> customerIds = customerPersistence.getChildrenForParent(
				center.getSearchId(), Short.valueOf("3"));
		assertEquals(3, customerIds.size());
		CustomerBO customer = (CustomerBO) TestObjectFactory.getObject(
				CustomerBO.class, customerIds.get(0));
		assertEquals("Group", customer.getDisplayName());
		customer = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				customerIds.get(1));
		assertEquals("client1", customer.getDisplayName());
		customer = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				customerIds.get(2));
		assertEquals("client2", customer.getDisplayName());

		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center1);
	}

	public void testGetCustomers() throws NumberFormatException,
			SystemException, ApplicationException {
		center = createCenter("center");
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		CenterBO center1 = createCenter("center11");
		GroupBO group1 = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_CLOSED, group);
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_CANCELLED, group1);
		List<Integer> customerIds = customerPersistence.getCustomers(Short
				.valueOf("3"));
		assertEquals(2, customerIds.size());

		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center1);
	}

	public void testGetCustomerChecklist() throws NumberFormatException,
			SystemException, ApplicationException, Exception {

		center = createCenter("center");
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
		assertEquals(1, customerPersistence.getStatusChecklist(
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
		center = createCenter();
		assertEquals(2, customerPersistence.retrieveAllCustomerStatusList(
				center.getCustomerLevel().getId()).size());
	}

	public void testCustomerCountByOffice() throws Exception {
		int count = customerPersistence.getCustomerCountForOffice(
				CustomerLevel.CENTER, Short.valueOf("3"));
		assertEquals(0, count);
		center = createCenter();
		count = customerPersistence.getCustomerCountForOffice(
				CustomerLevel.CENTER, Short.valueOf("3"));
		assertEquals(1, count);
	}

	public void testGetAllCustomerNotes() throws Exception {
		center = createCenter();
		center.addCustomerNotes(TestObjectFactory.getCustomerNote("Test Note",
				center));
		TestObjectFactory.updateObject(center);
		assertEquals(1, customerPersistence.getAllCustomerNotes(
				center.getCustomerId()).getSize());
		for (CustomerNoteEntity note : center.getCustomerNotes()) {
			assertEquals("Test Note", note.getComment());
			assertEquals(center.getPersonnel().getPersonnelId(), note
					.getPersonnel().getPersonnelId());
		}
		center = (CenterBO) (HibernateUtil.getSessionTL().get(CenterBO.class,
				new Integer(center.getCustomerId())));
	}

	public void testGetAllCustomerNotesWithZeroNotes() throws Exception {
		center = createCenter();
		assertEquals(0, customerPersistence.getAllCustomerNotes(
				center.getCustomerId()).getSize());
		assertEquals(0, center.getCustomerNotes().size());
	}

	public void testGetFormedByPersonnel() throws NumberFormatException,
			SystemException, ApplicationException {
		center = createCenter();
		assertEquals(1, customerPersistence.getFormedByPersonnel(
				ClientConstants.LOAN_OFFICER_LEVEL,
				center.getOffice().getOfficeId()).size());
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
		HibernateUtil.commitTransaction();
		assertEquals(1, customerPersistence.getAllClosedAccount(
				client.getCustomerId(), AccountTypes.LOANACCOUNT.getValue())
				.size());
		assertEquals(1, customerPersistence.getAllClosedAccount(
				group.getCustomerId(), AccountTypes.LOANACCOUNT.getValue())
				.size());
		assertEquals(1, customerPersistence.getAllClosedAccount(
				client.getCustomerId(), AccountTypes.SAVINGSACCOUNT.getValue())
				.size());
	}

	public void testGetAllClosedAccountsWhenNoAccountsClosed() throws Exception {
		getCustomer();
		assertEquals(0, customerPersistence.getAllClosedAccount(
				client.getCustomerId(), AccountTypes.LOANACCOUNT.getValue())
				.size());
		assertEquals(0, customerPersistence.getAllClosedAccount(
				group.getCustomerId(), AccountTypes.LOANACCOUNT.getValue())
				.size());
		assertEquals(0, customerPersistence.getAllClosedAccount(
				client.getCustomerId(), AccountTypes.SAVINGSACCOUNT.getValue())
				.size());
	}

	public void testGetLOForCustomer() throws PersistenceException {
		createCustomers(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
		Short LO = customerPersistence.getLoanOfficerForCustomer(center
				.getCustomerId());
		assertEquals(center.getPersonnel().getPersonnelId(), LO);
	}

	public void testUpdateLOsForAllChildren() {
		createCustomers(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
		assertEquals(center.getPersonnel().getPersonnelId(), group
				.getPersonnel().getPersonnelId());
		assertEquals(center.getPersonnel().getPersonnelId(), client
				.getPersonnel().getPersonnelId());
		PersonnelBO newLO = TestObjectFactory.getPersonnel(Short.valueOf("2"));
		new CustomerPersistence().updateLOsForAllChildren(newLO
				.getPersonnelId(), center.getSearchId(), center.getOffice()
				.getOfficeId());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals(newLO.getPersonnelId(), group.getPersonnel()
				.getPersonnelId());
		assertEquals(newLO.getPersonnelId(), client.getPersonnel()
				.getPersonnelId());
	}

	public void testCustomerDeleteMeeting() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		client = TestObjectFactory.createClient("myClient", meeting,
				CustomerStatus.CLIENT_PENDING);
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		customerPersistence.deleteCustomerMeeting(client);
		TestCustomerBO.setCustomerMeeting(client,null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertNull(client.getCustomerMeeting());
	}

	public void testDeleteMeeting() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		
		customerPersistence.deleteMeeting(meeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertNull(meeting);
	}
	
	public void testSearchWithOfficeId() throws Exception {
		createCustomers(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
		HibernateUtil.commitTransaction();
		QueryResult queryResult = new CustomerPersistence().search("C", Short
				.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(2, queryResult.getSize());
		assertEquals(2, queryResult.get(0, 10).size());
	}

	public void testSearchWithoutOfficeId() throws Exception {
		createCustomers(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
		HibernateUtil.commitTransaction();
		QueryResult queryResult = new CustomerPersistence().search("C", Short
				.valueOf("0"), Short.valueOf("1"), Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(2, queryResult.getSize());
		assertEquals(2, queryResult.get(0, 10).size());
	}

	public void testSearchWithGlobalNo() throws Exception {
		createCustomers(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
		HibernateUtil.commitTransaction();
		QueryResult queryResult = new CustomerPersistence().search(group
				.getGlobalCustNum(), Short.valueOf("3"), Short.valueOf("1"),
				Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1, queryResult.getSize());
		assertEquals(1, queryResult.get(0, 10).size());

	}

	public void testSearchWithCancelLoanAccounts()throws Exception{
		groupAccount = getLoanAccount();
		groupAccount.changeStatus(AccountState.LOANACC_CANCEL.getValue(),
				AccountStateFlag.LOAN_WITHDRAW.getValue(),
				"WITHDRAW LOAN ACCOUNT");
		TestObjectFactory.updateObject(groupAccount);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		groupAccount =(LoanBO) TestObjectFactory.getObject(LoanBO.class,groupAccount.getAccountId());
		center = (CustomerBO)TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		group = (CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		QueryResult queryResult = new CustomerPersistence().search(group
				.getGlobalCustNum(), Short.valueOf("3"), Short.valueOf("1"),
				Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1, queryResult.getSize());
		List results = queryResult.get(0, 10);
		assertEquals(1, results.size());
		CustomerSearch customerSearch =(CustomerSearch) results.get(0);
		assertEquals(0,customerSearch.getLoanGlobalAccountNum().size());
	}
	public void testSearchWithAccountGlobalNo() throws Exception {
		getCustomer();
		HibernateUtil.commitTransaction();
		QueryResult queryResult = new CustomerPersistence().search(groupAccount
				.getGlobalAccountNum(), Short.valueOf("3"), Short.valueOf("1"),
				Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1, queryResult.getSize());
		assertEquals(1, queryResult.get(0, 10).size());

	}

	
	public void testSearchGropAndClient() throws Exception {
		createCustomers(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
		HibernateUtil.commitTransaction();
		QueryResult queryResult = new CustomerPersistence().searchGroupClient("C",  Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1, queryResult.getSize());
		assertEquals(1, queryResult.get(0, 10).size());

	}
	public void testSearchGropAndClientForLoNoResults() throws Exception {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting, Short.valueOf("3"), Short.valueOf("3"));	
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE,
				"1234",true,new java.util.Date(),null,null,null,Short.valueOf("3"), center);
		HibernateUtil.commitTransaction();
		QueryResult queryResult = new CustomerPersistence().searchGroupClient("C",  Short.valueOf("3"));
		assertNotNull(queryResult);
		assertEquals(0, queryResult.getSize());
		assertEquals(0, queryResult.get(0, 10).size());

	}
	public void testSearchGropAndClientForLo() throws Exception {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting, Short.valueOf("3"), Short.valueOf("3"));	
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE,
				"1234",true,new java.util.Date(),null,null,null,Short.valueOf("3"), center);
		HibernateUtil.commitTransaction();
		QueryResult queryResult = new CustomerPersistence().searchGroupClient("G",  Short.valueOf("3"));
		assertNotNull(queryResult);
		assertEquals(1, queryResult.getSize());
		assertEquals(1, queryResult.get(0, 10).size());

	}
	public void testSearchCustForSavings() throws Exception {
		createCustomers(CustomerStatus.CENTER_ACTIVE,
				CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
		HibernateUtil.commitTransaction();
		QueryResult queryResult = new CustomerPersistence()
				.searchCustForSavings("C", Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(2, queryResult.getSize());
		assertEquals(2, queryResult.get(0, 10).size());

	}

	public void testGetCustomerAccountsForFee() throws Exception {
		groupAccount = getLoanAccount();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"ClientPeridoicFee", FeeCategory.CENTER, "5",
				RecurrenceType.WEEKLY, Short.valueOf("1"));
		AccountFeesEntity accountFee = new AccountFeesEntity(center
				.getCustomerAccount(), periodicFee, ((AmountFeeBO) periodicFee)
				.getFeeAmount().getAmountDoubleValue());
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		TestAccountFeesEntity.addAccountFees(accountFee, customerAccount);
		TestObjectFactory.updateObject(customerAccount);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		// check for the account fee
		List accountList = new CustomerPersistence()
				.getCustomerAccountsForFee(periodicFee.getFeeId());
		assertNotNull(accountList);
		assertEquals(1, accountList.size());
		// get all objects again
		groupAccount = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				groupAccount.getAccountId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
	}

	public void testRetrieveCustomerAccountActionDetails() throws Exception {
		center = createCenter();
		assertNotNull(center.getCustomerAccount());
		List<AccountActionDateEntity> actionDates = new CustomerPersistence()
				.retrieveCustomerAccountActionDetails(center
						.getCustomerAccount().getAccountId(),
						new java.sql.Date(System.currentTimeMillis()));
		assertEquals("The size of the due insallments is ", actionDates.size(),
				1);
	}
	
	public  void testGetActiveCentersUnderUser() throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("center",meeting,Short.valueOf("1"),Short.valueOf("1"));
		PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
		List<CustomerBO>  customers = new CustomerPersistence().getActiveCentersUnderUser(personnel);
		assertNotNull(customers);
		assertEquals(1,customers.size());
	}
	public  void testgetGroupsUnderUser() throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("center",meeting,Short.valueOf("1"),Short.valueOf("1"));
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		group2 = TestObjectFactory.createGroupUnderCenter("Group33", CustomerStatus.GROUP_CANCELLED, center);
		PersonnelBO personnel = TestObjectFactory.getPersonnel(Short.valueOf("1"));
		List<CustomerBO>  customers = new CustomerPersistence().getGroupsUnderUser(personnel);
		assertNotNull(customers);
		assertEquals(1,customers.size());
	}
	private void getCustomer() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client", 
				CustomerStatus.CLIENT_ACTIVE,
				group);
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
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		SavingsOfferingBO savingsOffering = TestObjectFactory
				.createSavingsOffering("SavingPrd12", "abc1", Short
						.valueOf("2"), new Date(System.currentTimeMillis()),
						Short.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		SavingsOfferingBO savingsOffering1 = TestObjectFactory
				.createSavingsOffering("SavingPrd11", "abc2", Short
						.valueOf("2"), new Date(System.currentTimeMillis()),
						Short.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		centerSavingsAccount = TestObjectFactory.createSavingsAccount("432434",
				center, Short.valueOf("16"), new Date(System
						.currentTimeMillis()), savingsOffering);
		clientSavingsAccount = TestObjectFactory.createSavingsAccount("432434",
				client, Short.valueOf("16"), new Date(System
						.currentTimeMillis()), savingsOffering1);
	}

	private void createCustomers(CustomerStatus centerStatus,
			CustomerStatus groupStatus, CustomerStatus clientStatus) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", groupStatus, center);
		client = TestObjectFactory.createClient("Client", clientStatus,
				group);
	}
	
	private static java.util.Date getMeetingDates(MeetingBO meeting) {
		List<java.util.Date> dates = new ArrayList<java.util.Date>();
		try {
			dates = meeting.getAllDates(new java.util.Date(System
					.currentTimeMillis()));
		} catch (MeetingException e) {
			e.printStackTrace();
		}
		return dates.get(dates.size() - 1);
	}

	private CenterBO createCenter() {
		return createCenter("Center_Active_test");
	}

	private CenterBO createCenter(String name) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createCenter(name, meeting);
	}

	private LoanBO getLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}
}
