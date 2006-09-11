package org.mifos.application.bulkentry.business.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.business.CustomerAccountView;
import org.mifos.application.accounts.business.LoanAccountView;
import org.mifos.application.accounts.business.LoanAccountsProductView;
import org.mifos.application.accounts.business.SavingsAccountView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.bulkentry.business.BulkEntryInstallmentView;
import org.mifos.application.bulkentry.exceptions.BulkEntryAccountUpdateException;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
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
		bulkEntryBusinessService = (BulkEntryBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.BulkEntryService);
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

	public void testSuccessfulSaveAttendance() throws NumberFormatException,
			BulkEntryAccountUpdateException {
		createInitialObjects();

		bulkEntryBusinessService.saveAttendance(client.getCustomerId(),
				currentDate, (short) 1);

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertEquals("The size of attendance ", ((ClientBO) client)
				.getClientAttendances().size(), 1);
	}

	public void testSaveAttendanceForNoMeetingDate() {
		createInitialObjects();
		try {
			bulkEntryBusinessService.saveAttendance(client.getCustomerId(),
					null, (short) 1);
			HibernateUtil.commitTransaction();
			assertTrue("The attendance has been update for meeting date null",
					false);
		} catch (Exception e) {
			assertTrue(
					"The attendance has not been update for meeting date null",
					true);
		}

		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
	}

	public void testSuccessfulSaveLoanAccount() throws Exception {
		try {
			createLoanAccount();

			bulkEntryBusinessService.saveLoanAccount(getAccountView(account),
					(short) 1, "324423", (short) 1, null, new java.sql.Date(
							System.currentTimeMillis()));

			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		account = (LoanBO) accountPersistence.getAccount(account
				.getAccountId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
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
			assertTrue("A paid installment can be paid again", false);
		} catch (BulkEntryAccountUpdateException be) {
			HibernateUtil.getTransaction().rollback();
			account.getAccountPayments().clear();
			assertNotNull(be);
			assertEquals(be.getKey(), "errors.update");
			assertTrue("A paid installment cannot be paid again", true);
		}
	}

	public void testRetrieveSavingsAccountInformationForCustomer() throws Exception{
		createCenter();
		centerSavingsAccount = TestObjectFactory.createSavingsAccount("432434",
				center, Short.valueOf("16"), new Date(System
						.currentTimeMillis()),
				createSavingsOffering("SavingPrd1wa","qads"));

		List<SavingsAccountView> savingsAccounts = bulkEntryBusinessService
				.retrieveSavingsAccountInformationForCustomer(center
						.getCustomerId());
		assertEquals("The number of savings account", 1, savingsAccounts.size());

	}

	public void testRetrieveSavingsAccountTransactionDetail() throws Exception{
		createInitialObjects();
		centerSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43244334", center, Short.valueOf("16"), currentDate,
				createSavingsOffering("Center12","q1se"));
		groupSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43234434", group, Short.valueOf("16"), currentDate,
				createSavingsOffering("Group23","cvxs"));
		clientSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43245434", client, Short.valueOf("16"), currentDate,
				createSavingsOffering("Clienta1","qase"));

		List<AccountActionDateEntity> centerActions = bulkEntryBusinessService
				.retrieveSavingsAccountTransactionDetail(centerSavingsAccount
						.getAccountId(), center.getCustomerId(),
						new java.sql.Date(System.currentTimeMillis()), true);
		assertEquals("The number of installments due for account", 1,
				centerActions.size());

	}

	public void testSuccessfulSavingsAccountDeposit() throws Exception {
		createInitialObjects();
		clientSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43245434", client, Short.valueOf("16"), new Date(System
						.currentTimeMillis()), createSavingsOffering("Client21","ased"));
		HibernateUtil.closeSession();

		bulkEntryBusinessService.saveSavingsDepositAccount(
				getSavingsAccountView(clientSavingsAccount, "100", "0"), Short
						.valueOf("1"), "3424", (short) 1, new java.sql.Date(
						System.currentTimeMillis()), new java.sql.Date(System
						.currentTimeMillis()), false, client.getCustomerId());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		clientSavingsAccount = (SavingsBO) accountPersistence
				.getAccount(clientSavingsAccount.getAccountId());
		assertEquals("The balance for account", clientSavingsAccount
				.getSavingsBalance().getAmountDoubleValue(), 100.0);
		assertEquals(1, clientSavingsAccount.getSavingsActivityDetails().size());
	}

	public void testSuccessfulSavingsAccountWithdrawal()
			throws Exception{
		createSavingsAccountWithBal("100","Dfre1qw","xzsc");
		bulkEntryBusinessService.saveSavingsWithdrawalAccount(
				getSavingsAccountView(clientSavingsAccount, "0", "100"), Short
						.valueOf("1"), "3424", (short) 1, new java.sql.Date(
						System.currentTimeMillis()), new java.sql.Date(System
						.currentTimeMillis()), client.getCustomerId());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		clientSavingsAccount = (SavingsBO) accountPersistence
				.getAccount(clientSavingsAccount.getAccountId());
		assertEquals("The balance for account", clientSavingsAccount
				.getSavingsBalance().getAmountDoubleValue(), 0.0);
		assertEquals(2, clientSavingsAccount.getSavingsActivityDetails().size());
	}

	public void testGetFeeAmountAtDisbursement() throws Exception {
		createInitialObjects();
		account = createLoanAccountForDisb(group, center.getCustomerMeeting()
				.getMeeting(), Short.valueOf("3"));

		assertEquals(30.0, bulkEntryBusinessService.getFeeAmountAtDisbursement(
				account.getAccountId(), new java.sql.Date(System
						.currentTimeMillis())));

	}

	public void testRetrieveCustomerAccountActionDetails() throws Exception {
		createCenter();

		assertNotNull(center.getCustomerAccount());
		List<AccountActionDateEntity> actionDates = bulkEntryBusinessService
				.retrieveCustomerAccountActionDetails(center
						.getCustomerAccount().getAccountId(),
						new java.sql.Date(System.currentTimeMillis()));
		assertEquals("The size of the due insallments is ", actionDates.size(),
				1);

		HibernateUtil.closeSession();
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
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
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		customerAccount = center.getCustomerAccount();
		assertEquals("The size of the payments done is", customerAccount
				.getAccountPayments().size(), 1);
		assertEquals("The size of the due insallments after payment is",
				TestObjectFactory.getDueActionDatesForAccount(
						customerAccount.getAccountId(), currentDate).size(), 0);
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
			assertTrue("A paid installment can be paid again", false);
		} catch (BulkEntryAccountUpdateException e) {
			assertTrue("A paid installment cannot be paid again", true);
		}
		HibernateUtil.closeSession();
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
	}

	private void createCenter() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, currentDate);
	}

	private void createLoanAccount() {
		createInitialObjects();

		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), currentDate, (short) 1, 300.0, 1.2,
				Short.valueOf("3"), (short) 1, Short.valueOf("1"), (short) 1,
				(short) 1, (short) 1, center.getCustomerMeeting().getMeeting());
		account = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), currentDate, loanOffering);
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
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", Short.valueOf("3"),
				"1.1.1.1", group, currentDate);
		HibernateUtil.closeSession();
	}

	private LoanBO createLoanAccountForDisb(CustomerBO customer,
			MeetingBO meeting, Short accountSate) {
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), currentDate, (short) 1, 300.0, 1.2,
				Short.valueOf("3"), (short) 1, Short.valueOf("1"), (short) 1,
				(short) 1, (short) 1, meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"42423142341", customer, accountSate, currentDate,
				loanOffering, 1);

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

	private SavingsOfferingBO createSavingsOffering(String offeringName,String shortName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(offeringName, shortName,Short
				.valueOf("2"), currentDate, Short.valueOf("2"), 300.0,
				(short) 1, 1.2, 200.0, 200.0, Short.valueOf("2"), (short) 1,
				meetingIntCalc, meetingIntPost);
	}

	private void createSavingsAccountWithBal(String amount,String OfferingName,String shortName)
			throws NumberFormatException, BulkEntryAccountUpdateException {
		createInitialObjects();
		clientSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43245434", client, Short.valueOf("16"), currentDate,
				createSavingsOffering(OfferingName,shortName));
		HibernateUtil.closeSession();

		bulkEntryBusinessService.saveSavingsDepositAccount(
				getSavingsAccountView(clientSavingsAccount, amount, "0"), Short
						.valueOf("1"), "3424", (short) 1, currentDate,
				currentDate, false, client.getCustomerId());
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
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		return customerAccountView;
	}

	private void makePaymentForallInstallments()throws Exception {
		for (AccountActionDateEntity actionDate : account
				.getAccountActionDates()) {
			actionDate.setPaymentStatus(PaymentStatus.PAID.getValue());
		}

		TestObjectFactory.updateObject(account);
	}
}
