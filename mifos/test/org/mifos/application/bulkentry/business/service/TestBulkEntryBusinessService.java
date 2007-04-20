package org.mifos.application.bulkentry.business.service;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitx.framework.ObjectAssert;
import junitx.framework.StringAssert;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.bulkentry.business.BulkEntryInstallmentView;
import org.mifos.application.bulkentry.util.helpers.BulkEntrySavingsCache;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestBulkEntryBusinessService extends MifosTestCase {
	private BulkEntryBusinessService bulkEntryBusinessService;

	private CustomerBO center;

	private CustomerBO group;

	private CustomerBO client;

	private LoanBO account;

	private SavingsBO centerSavingsAccount;

	private SavingsBO groupSavingsAccount;

	private SavingsBO clientSavingsAccount;

	private AccountPersistence accountPersistence;

	Date currentDate;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bulkEntryBusinessService = new BulkEntryBusinessService();
		accountPersistence = new AccountPersistence();
		currentDate = new Date(System.currentTimeMillis());
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(clientSavingsAccount);
		TestObjectFactory.cleanUp(groupSavingsAccount);
		TestObjectFactory.cleanUp(centerSavingsAccount);
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testSuccessfulSaveAttendance() throws Exception {
		createInitialObjects();
		List<ClientBO> clients = new ArrayList<ClientBO>();
		bulkEntryBusinessService.setClientAttendance(client.getCustomerId(),
				currentDate, (short) 1,clients);
		HibernateUtil.closeSession();
		bulkEntryBusinessService.saveClientAttendance(clients.get(0));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		client = TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertEquals("The size of attendance ", ((ClientBO) client)
				.getClientAttendances().size(), 1);
	}

	public void testSaveAttendanceForNoMeetingDate() throws Exception {
		createInitialObjects();
		List<ClientBO> clients = new ArrayList<ClientBO>();
		try {
			bulkEntryBusinessService.setClientAttendance(client.getCustomerId(),
					null, (short) 1,clients);
			HibernateUtil.closeSession();
			bulkEntryBusinessService.saveClientAttendance(clients.get(0));
			HibernateUtil.commitTransaction();
			fail("The attendance has been update for meeting date null");
		} catch (ServiceException e) {
			// What should we report to the user? errors.updatefailed?
			// The exception here is a null in a NOT NULL column
			ObjectAssert.assertInstanceOf(
				PersistenceException.class, e.getCause());
		}

		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
	}

	public void testSuccessfulSaveLoanAccount() throws Exception {
		createLoanAccount();

		bulkEntryBusinessService.saveLoanAccount(getAccountView(account),
				(short) 1, "324423", (short) 1, null, new java.sql.Date(System
						.currentTimeMillis()));

		HibernateUtil.commitTransaction();

		account = (LoanBO) accountPersistence
				.getAccount(account.getAccountId());
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		assertEquals(account.getLoanOffering().getPrdOfferingName(), "Loan");
		assertEquals(account.getLoanSummary().getFeesPaid()
				.getAmountDoubleValue(), Double.valueOf("100.0"));
	}

	public void testLoanAccountInstallmentPayableOnlyOnce() throws Exception {
		createLoanAccount();
		makePaymentForallInstallments();

		try {
			bulkEntryBusinessService.saveLoanAccount(getAccountView(account),
					(short) 1, "324423", (short) 1, new java.sql.Date(System
							.currentTimeMillis()), new java.sql.Date(System
							.currentTimeMillis()));
			fail("A paid installment can be paid again");
		} catch (ServiceException be) {
			HibernateUtil.rollbackTransaction();
			account.getAccountPayments().clear();
			assertUpdateError(be);
		}
	}

	public void testSuccessfulSavingsAccountDeposit() throws Exception {
		createInitialObjects();
		clientSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43245434", client, Short.valueOf("16"), new Date(System
						.currentTimeMillis()), createSavingsOffering(
						"Client21", "ased"));
		HibernateUtil.closeSession();
		
		Map<Integer,BulkEntrySavingsCache> savingsAccounts = new HashMap<Integer,BulkEntrySavingsCache>();
		bulkEntryBusinessService.setSavingsDepositAccountDetails(
				getSavingsAccountView(clientSavingsAccount, "100", "0"), Short
						.valueOf("1"), "3424", (short) 1, new java.sql.Date(
						System.currentTimeMillis()), new java.sql.Date(System
						.currentTimeMillis()), false, client.getCustomerId(),
				savingsAccounts);
		HibernateUtil.closeSession();
		bulkEntryBusinessService.saveSavingsAccount(savingsAccounts.get(
				clientSavingsAccount.getAccountId()).getAccount());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		clientSavingsAccount = (SavingsBO) accountPersistence
				.getAccount(clientSavingsAccount.getAccountId());
		assertEquals("The balance for account", clientSavingsAccount
				.getSavingsBalance().getAmountDoubleValue(), 100.0);
		assertEquals(1, clientSavingsAccount.getSavingsActivityDetails().size());
	}

	public void testSuccessfulSavingsAccountWithdrawal() throws Exception {
		createSavingsAccountWithBal("100", "Dfre1qw", "xzsc");
		HibernateUtil.closeSession();
		Map<Integer,BulkEntrySavingsCache> savingsAccounts = new HashMap<Integer,BulkEntrySavingsCache>();
		bulkEntryBusinessService.setSavingsWithdrawalAccountDetails(
				getSavingsAccountView(clientSavingsAccount, "0", "100"), Short
						.valueOf("1"), "3424", (short) 1, new java.sql.Date(
						System.currentTimeMillis()), new java.sql.Date(System
						.currentTimeMillis()), client.getCustomerId(),savingsAccounts);
		HibernateUtil.closeSession();
		bulkEntryBusinessService.saveSavingsAccount(savingsAccounts.get(
				clientSavingsAccount.getAccountId()).getAccount());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		clientSavingsAccount = (SavingsBO) accountPersistence
				.getAccount(clientSavingsAccount.getAccountId());
		assertEquals("The balance for account", clientSavingsAccount
				.getSavingsBalance().getAmountDoubleValue(), 0.0);
		assertEquals(2, clientSavingsAccount.getSavingsActivityDetails().size());
	}

	public void testSuccesulSaveCustomerAccount() throws Exception {
		createCenter();

		CustomerAccountBO customerAccount = center.getCustomerAccount();
		assertNotNull(customerAccount);

		bulkEntryBusinessService.saveCustomerAccountCollections(
				TestObjectFactory.getCustomerAccountView(center), center
						.getPersonnel().getPersonnelId(), "65463", Short
						.valueOf("1"), null, currentDate);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		customerAccount = center.getCustomerAccount();
		assertEquals("The size of the payments done is", 
				1, customerAccount.getAccountPayments().size());
		assertEquals("The size of the due insallments after payment is",
				0,
				TestObjectFactory.getDueActionDatesForAccount(
						customerAccount.getAccountId(), currentDate).size());
	}

	public void testCustomerAccountInstallmentPayableOnlyOnce()
			throws Exception {
		CustomerAccountView customerAccountView = createCustomerAccountWithNoDue();

		try {
			bulkEntryBusinessService.saveCustomerAccountCollections(
					customerAccountView,
					center.getPersonnel().getPersonnelId(), "65463", Short
							.valueOf("1"), null, new Date(System
							.currentTimeMillis()));
			fail("A paid installment can be paid again");
		} catch (ServiceException e) {
			assertUpdateError(e);
		}
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
	}

	private void assertUpdateError(ServiceException e) {
		assertEquals("errors.update", e.getKey());
		assertEquals(1, e.getValues().length);
		String accountNumber = (String) e.getValues()[0];
		assertEquals(15, accountNumber.length());
		StringAssert.assertStartsWith("000", accountNumber);
	}

	public void testGetLastMeetingDateForCustomer() throws ServiceException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center_Active", meeting);
		java.util.Date lastMeetingDate = bulkEntryBusinessService
				.getLastMeetingDateForCustomer(center.getCustomerId());
		assertEquals(DateUtils.toDatabaseFormat(DateUtils.getCurrentDateWithoutTimeStamp()).toString(),
				lastMeetingDate.toString());
	}

	public void testGetLastMeetingDateForCustomerForInvalidConnection() 
	throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center_Active", meeting);
		TestObjectFactory.simulateInvalidConnection();
		try {
			bulkEntryBusinessService.getLastMeetingDateForCustomer(center
					.getCustomerId());
			fail();
		} catch (ServiceException e) {
			// What should we report to the user here?
			ObjectAssert.assertInstanceOf(
					PersistenceException.class, e.getCause());
		} finally {
			HibernateUtil.closeSession();
		}

	}

	public void testSaveCustomerAccountForInvalidConnection() throws Exception {
		createCenter();

		CustomerAccountBO customerAccount = center.getCustomerAccount();
		assertNotNull(customerAccount);
		CustomerAccountView customerAccountView = TestObjectFactory
				.getCustomerAccountView(center);
		TestObjectFactory.simulateInvalidConnection();
		try {
			bulkEntryBusinessService.saveCustomerAccountCollections(
					customerAccountView,
					center.getPersonnel().getPersonnelId(), "65463", Short
							.valueOf("1"), null, currentDate);
			fail();
		} catch (ServiceException e) {
			// What should we report to the user here?
			ObjectAssert.assertInstanceOf(
					PersistenceException.class, e.getCause());
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testSaveAttendanceForInvalidConnection() throws Exception {
		createInitialObjects();
		TestObjectFactory.simulateInvalidConnection();
		try {
			bulkEntryBusinessService.setClientAttendance(client.getCustomerId(),
					currentDate, (short) 1,new ArrayList<ClientBO>());
			fail();
		} catch (ServiceException e) {
			// errors.update doesn't seem right,
			// rather than errors.updatefailed or something like that.
			assertEquals("errors.update", e.getKey());
			assertEquals(1, e.getValues().length);
			String accountNumber = (String) e.getValues()[0];
			assertEquals("" + client.getCustomerId(), accountNumber);
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testDisburseLoanWithFeeAtDisbursement() throws Exception {
		Date startDate = new Date(System.currentTimeMillis());

		account = getLoanAccount(Short.valueOf("3"), startDate, 1);
		HibernateUtil.closeSession();
		LoanAccountsProductView loanAccountsProductView = getAccountView(account);
		loanAccountsProductView.setDisBursementAmountEntered(account.getLoanAmount().toString());
		bulkEntryBusinessService.saveLoanAccount(loanAccountsProductView,
				(short) 1, "324423", (short) 1, null, startDate);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		account = TestObjectFactory.getObject(LoanBO.class, account
				.getAccountId());
		assertEquals(AccountState.LOANACC_ACTIVEINGOODSTANDING.getValue(),
				account.getAccountState().getId());
	}

	private void createCenter() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
	}

	private void createLoanAccount() {
		createInitialObjects();

		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				currentDate,
				center.getCustomerMeeting().getMeeting());
		account = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, 
				currentDate, loanOffering);
		HibernateUtil.closeSession();

	}

	private LoanAccountsProductView getAccountView(LoanBO account) {
		LoanAccountView accountView = TestObjectFactory
				.getLoanAccountView(account);
		List<BulkEntryInstallmentView> actionDates = new ArrayList<BulkEntryInstallmentView>();
		actionDates.add(TestObjectFactory.getBulkEntryAccountActionView(account
				.getAccountActionDates().iterator().next()));
		accountView.addTrxnDetails(actionDates);

		LoanAccountsProductView loanAccountsProductView = new LoanAccountsProductView(
				accountView.getPrdOfferingId(), accountView
						.getPrdOfferingShortName());
		loanAccountsProductView.setEnteredAmount("100.0");
		loanAccountsProductView.addLoanAccountView(accountView);
		return loanAccountsProductView;
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient(
				"Client", CustomerStatus.CLIENT_ACTIVE,
				group);
		HibernateUtil.closeSession();
	}

	private SavingsAccountView getSavingsAccountView(SavingsBO account,
			String depAmount, String withAmount) {
		SavingsAccountView accountView = new SavingsAccountView(account
				.getAccountId(), account.getAccountType().getAccountTypeId(),
				account.getSavingsOffering());
		accountView.setDepositAmountEntered(depAmount);
		accountView.setWithDrawalAmountEntered(withAmount);
		accountView.addAccountTrxnDetail(TestObjectFactory
				.getBulkEntryAccountActionView(account.getAccountActionDates()
						.iterator().next()));
		return accountView;
	}

	private SavingsOfferingBO createSavingsOffering(String offeringName,
			String shortName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		return TestObjectFactory.createSavingsOffering(offeringName, shortName, 
				ApplicableTo.GROUPS, currentDate, 
				PrdStatus.SAVINGS_ACTIVE, 300.0, 
				RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 
				200.0, 200.0, 
				SavingsType.VOLUNTARY, 
				InterestCalcType.MINIMUM_BALANCE, 
				meetingIntCalc, meetingIntPost);
	}

	private void createSavingsAccountWithBal(String amount,
			String OfferingName, String shortName) throws Exception {
		createInitialObjects();
		clientSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43245434", client, Short.valueOf("16"), currentDate,
				createSavingsOffering(OfferingName, shortName));
		HibernateUtil.closeSession();

		Map<Integer,BulkEntrySavingsCache> savingsAccounts = new HashMap<Integer,BulkEntrySavingsCache>();
		bulkEntryBusinessService.setSavingsDepositAccountDetails(
				getSavingsAccountView(clientSavingsAccount, amount, "0"), Short
						.valueOf("1"), "3424", (short) 1, currentDate,
				currentDate, false,client.getCustomerId(),savingsAccounts);
		HibernateUtil.closeSession();
		bulkEntryBusinessService.saveSavingsAccount(savingsAccounts.get(
				clientSavingsAccount.getAccountId()).getAccount());
		HibernateUtil.commitTransaction();
	}

	private CustomerAccountView createCustomerAccountWithNoDue()
			throws Exception {
		createCenter();

		CustomerAccountView customerAccountView = TestObjectFactory
				.getCustomerAccountView(center);
		bulkEntryBusinessService.saveCustomerAccountCollections(
				customerAccountView, center.getPersonnel().getPersonnelId(),
				"65463", (short) 1, null, currentDate);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		return customerAccountView;
	}

	private void makePaymentForallInstallments() throws Exception {
		for (AccountActionDateEntity actionDate : account
				.getAccountActionDates()) {
			actionDate.setPaymentStatus(PaymentStatus.PAID);
		}

		TestObjectFactory.updateObject(account);
	}

	private LoanBO getLoanAccount(Short accountSate, Date startDate,
			int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, accountSate, startDate, loanOffering,
				disbursalType);

	}

}
