package org.mifos.application.customer.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanPerformanceHistoryEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.personnel.util.helpers.PersonnelStatus;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerBO extends MifosTestCase {
	private AccountBO accountBO;

	private CenterBO center;

	private GroupBO group;

	private ClientBO client;

	private CustomerPersistence customerPersistence;

	private MeetingBO meeting;

	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsOfferingBO savingsOffering;

	PersonnelBO loanOfficer;
	
	private OfficeBO createdBranchOffice;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		customerPersistence = new CustomerPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(loanOfficer);
		TestObjectFactory.cleanUp(createdBranchOffice);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGroupPerfObject() throws PersistenceException {
		createInitialObjects();
		GroupPerformanceHistoryEntity groupPerformanceHistory = group
				.getPerformanceHistory();
		groupPerformanceHistory.setLastGroupLoanAmount(new Money("100"));
		TestObjectFactory.updateObject(group);
		HibernateUtil.closeSession();
		group = (GroupBO) customerPersistence.getBySystemId(group
				.getGlobalCustNum(), group.getCustomerLevel().getId());
		assertEquals(group.getCustomerId(), group.getPerformanceHistory()
				.getGroup().getCustomerId());
		assertEquals(new Money("100"), group.getPerformanceHistory()
				.getLastGroupLoanAmount());
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
	}

	public void testClientPerfObject() throws PersistenceException {
		createInitialObjects();
		ClientPerformanceHistoryEntity clientPerformanceHistory = ((ClientBO) client)
				.getPerformanceHistory();
		clientPerformanceHistory.setLoanCycleNumber(Integer.valueOf("1"));
		clientPerformanceHistory.setNoOfActiveLoans(Integer.valueOf("1"));
		clientPerformanceHistory.setLastLoanAmount(new Money("100"));
		TestObjectFactory.updateObject(client);
		client = (ClientBO) customerPersistence.getBySystemId(client
				.getGlobalCustNum(), client.getCustomerLevel().getId());
		assertEquals(client.getCustomerId(), client.getPerformanceHistory()
				.getClient().getCustomerId());
		assertEquals(Integer.valueOf("1"), client.getPerformanceHistory()
				.getLoanCycleNumber());
		assertEquals(new Money("100"), client.getPerformanceHistory()
				.getLastLoanAmount());
		assertEquals(new Money("0"), client.getPerformanceHistory()
				.getDelinquentPortfolioAmount());
	}

	public void testLoanPerfObject() throws PersistenceException {
		Date currentDate = new Date(System.currentTimeMillis());
		createInitialObjects();
		accountBO = getLoanAccount(client, meeting);
		LoanBO loanBO = (LoanBO) accountBO;
		LoanPerformanceHistoryEntity loanPerformanceHistory = loanBO
				.getPerformanceHistory();
		loanPerformanceHistory.setNoOfPayments(Integer.valueOf("3"));
		loanPerformanceHistory.setLoanMaturityDate(currentDate);
		TestObjectFactory.updateObject(loanBO);
		loanBO = (LoanBO) new AccountPersistence().getAccount(loanBO
				.getAccountId());
		assertEquals(Integer.valueOf("3"), loanBO.getPerformanceHistory()
				.getNoOfPayments());
		assertEquals(currentDate, loanBO.getPerformanceHistory()
				.getLoanMaturityDate());
	}

	public void testGetBalanceForAccountsAtRisk() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group, meeting);
		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertEquals(new Money(), group.getBalanceForAccountsAtRisk());
		changeFirstInstallmentDate(accountBO, 31);
		assertEquals(new Money("300"), group.getBalanceForAccountsAtRisk());
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testGetDelinquentPortfolioAmount() {
		createInitialObjects();
		accountBO = getLoanAccount(client, meeting);
		TestObjectFactory.flushandCloseSession();
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		setActionDateToPastDate();
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		client = (ClientBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertEquals(new Money("0.7"), client.getDelinquentPortfolioAmount());
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testGetOutstandingLoanAmount() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group, meeting);
		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(new Money("300.0"), group.getOutstandingLoanAmount());
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testGetActiveLoanCounts() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group, meeting);
		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(1, group.getActiveLoanCounts().intValue());
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testGetLoanAccountInUse() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group, meeting);
		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		List<LoanBO> loans = group.getOpenLoanAccounts();
		assertEquals(1, loans.size());
		assertEquals(accountBO.getAccountId(), loans.get(0).getAccountId());
		TestObjectFactory.flushandCloseSession();

		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testGetSavingsAccountInUse() throws Exception {
		accountBO = getSavingsAccount("fsaf6", "ads6");
		TestObjectFactory.flushandCloseSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		List<SavingsBO> savings = client.getOpenSavingAccounts();
		assertEquals(1, savings.size());
		assertEquals(accountBO.getAccountId(), savings.get(0).getAccountId());
		TestObjectFactory.flushandCloseSession();

		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testHasAnyLoanAccountInUse() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group, meeting);
		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertTrue(group.isAnyLoanAccountOpen());
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testHasAnySavingsAccountInUse() throws Exception {
		accountBO = getSavingsAccount("fsaf5", "ads5");
		TestObjectFactory.flushandCloseSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertTrue(client.isAnySavingsAccountOpen());
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testgetSavingsBalance() throws Exception {
		SavingsBO savings = getSavingsAccount("fsaf4", "ads4");
		savings.setSavingsBalance(new Money("1000"));
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,
				savings.getAccountId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals(new Money("1000.0"), savings.getSavingsBalance());
		assertEquals(new Money("1000.0"), client.getSavingsBalance());
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		savings = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,
				savings.getAccountId());
		TestObjectFactory.cleanUp(savings);
	}

	public void testValidateStatusWithActiveGroups() throws CustomerException,
			AccountException {
		createInitialObjects();
		Short newStatusId = CustomerStatus.CENTER_INACTIVE.getValue();
		try {
			center.changeStatus(newStatusId, null, "Test");
			assertFalse(true);
		} catch (CustomerException sce) {
			assertTrue(true);
			assertEquals(sce.getKey(),
					CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION);
			assertEquals(CustomerStatus.CENTER_ACTIVE.getValue(),center.getCustomerStatus().getId());
		}
	}

	public void testValidateStatusForClientWithPartialGroups() throws Exception {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.4", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group",
				CustomerStatus.GROUP_PARTIAL.getValue(), "1.4.1", center,
				new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_PARTIAL.getValue(), "1.4.1.1", group,
				new Date(System.currentTimeMillis()));
		Short newStatusId = CustomerStatus.CLIENT_ACTIVE.getValue();
		try {
			client.changeStatus(newStatusId, null, "Test");
			assertFalse(true);
		} catch (CustomerException sce) {
			assertTrue(true);
			assertEquals(sce.getKey(),ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION);
			assertEquals(CustomerStatus.CLIENT_PARTIAL.getValue(),client.getCustomerStatus().getId());
		}
	}

	public void testValidateStatusForClientWithActiveAccounts()
			throws Exception {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.4", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group",
				CustomerStatus.GROUP_ACTIVE.getValue(), "1.4.1", center,
				new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE.getValue(), "1.4.1.1", group,
				new Date(System.currentTimeMillis()));
		accountBO = getLoanAccount(client, meeting);
		HibernateUtil.closeSession();
		client = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		Short newStatusId = CustomerStatus.CLIENT_CLOSED.getValue();
		try {
			client.changeStatus(newStatusId, null, "Test");
			assertFalse(true);
		} catch (CustomerException sce) {
			assertTrue(true);
			assertEquals(sce.getKey(),
					CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
			assertEquals(CustomerStatus.CLIENT_ACTIVE.getValue(),client.getCustomerStatus().getId());
		}
		HibernateUtil.closeSession();
		client = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		accountBO = (LoanBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				accountBO.getAccountId());
	}
	
	public void testValidateStatusChangeForCustomerWithInactiveLoanofficerAssigned() throws Exception {
		createPersonnel(PersonnelLevel.LOAN_OFFICER);
		createCenter(getBranchOffice().getOfficeId(), loanOfficer.getPersonnelId());
		center.changeStatus(CustomerStatus.CENTER_INACTIVE.getValue(),null,"comment");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanOfficer=(PersonnelBO)HibernateUtil.getSessionTL().get(PersonnelBO.class,loanOfficer.getPersonnelId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,center.getCustomerId());
		updatePersonnel(PersonnelLevel.LOAN_OFFICER,PersonnelStatus.INACTIVE, getBranchOffice());
		try {
			center.changeStatus(CustomerStatus.CENTER_ACTIVE.getValue(),null,"comment");
			assertFalse(true);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(ce.getKey(), CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION);
			assertEquals(CustomerStatus.CENTER_INACTIVE.getValue(),center.getCustomerStatus().getId());
		}
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,center.getCustomerId());
		loanOfficer=(PersonnelBO)HibernateUtil.getSessionTL().get(PersonnelBO.class,loanOfficer.getPersonnelId());
	}
	
	public void testValidateStatusChangeForCustomerWithLoanofficerAssignedToDifferentBranch() throws Exception {
		OfficeBO office = TestObjectFactory.getOffice(Short.valueOf("1"));
		createdBranchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
		HibernateUtil.closeSession();
		createdBranchOffice = (OfficeBO) HibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		createPersonnel(PersonnelLevel.LOAN_OFFICER);
		createCenter(getBranchOffice().getOfficeId(), loanOfficer.getPersonnelId());
		center.changeStatus(CustomerStatus.CENTER_INACTIVE.getValue(),null,"comment");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanOfficer=(PersonnelBO)HibernateUtil.getSessionTL().get(PersonnelBO.class,loanOfficer.getPersonnelId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,center.getCustomerId());
		updatePersonnel(PersonnelLevel.LOAN_OFFICER,PersonnelStatus.ACTIVE, createdBranchOffice);
		try {
			center.changeStatus(CustomerStatus.CENTER_ACTIVE.getValue(),null,"comment");
			assertFalse(true);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(ce.getKey(), CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION);
			assertEquals(CustomerStatus.CENTER_INACTIVE.getValue(),center.getCustomerStatus().getId());
		}
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,center.getCustomerId());
		loanOfficer=(PersonnelBO)HibernateUtil.getSessionTL().get(PersonnelBO.class,loanOfficer.getPersonnelId());
	}
	
	public void testValidateStatusForClientSavingsAccountInactive()
			throws Exception {
		accountBO = getSavingsAccount("fsaf6", "ads6");
		accountBO.changeStatus(AccountState.SAVINGS_ACC_INACTIVE.getValue(),
				null, "changed status");
		accountBO.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		Short newStatusId = CustomerStatus.CLIENT_CLOSED.getValue();
		try {
			client.changeStatus(newStatusId, null, "Test");
			assertFalse(true);
		} catch (CustomerException sce) {
			assertTrue(true);
			assertEquals(sce.getKey(),
					CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
			assertEquals(CustomerStatus.CLIENT_ACTIVE.getValue(),client.getCustomerStatus().getId());
		}
		HibernateUtil.closeSession();
		client = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		accountBO = (SavingsBO) HibernateUtil.getSessionTL().get(
				SavingsBO.class, accountBO.getAccountId());
	}

	private void changeFirstInstallmentDate(AccountBO accountBO,
			int numberOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day
				- numberOfDays);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			accountActionDateEntity.setActionDate(new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
			break;
		}
	}

	private void setActionDateToPastDate() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
		calendar.add(calendar.WEEK_OF_MONTH, -1);
		java.sql.Date lastWeekDate = new java.sql.Date(calendar
				.getTimeInMillis());

		Calendar date = new GregorianCalendar();
		date.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
		date.add(date.WEEK_OF_MONTH, -2);
		java.sql.Date twoWeeksBeforeDate = new java.sql.Date(date
				.getTimeInMillis());

		for (AccountActionDateEntity installment : accountBO
				.getAccountActionDates()) {
			if (installment.getInstallmentId().intValue() == 1) {
				installment.setActionDate(lastWeekDate);
			} else if (installment.getInstallmentId().intValue() == 2) {
				installment.setActionDate(twoWeeksBeforeDate);
			}
		}
	}

	private SavingsBO getSavingsAccount(String offeringName, String shortName) throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering(offeringName, shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017",
				client, AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}

	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", CustomerStatus.CENTER_ACTIVE.getValue(),
				"1.4", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE,
				"1.4.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				ClientConstants.STATUS_ACTIVE, "1.4.1.1", group, new Date(
						System.currentTimeMillis()));
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
	
	private void createPersonnel(PersonnelLevel personnelLevel) throws Exception{
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456",
				Short.valueOf("1")));
		 Address address = new Address("abcd","abcd","abcd","abcd","abcd","abcd","abcd","abcd");
		 Name name = new Name("XYZ", null, null, "Last Name");
		 java.util.Date date =new java.util.Date();
		 loanOfficer = new PersonnelBO(personnelLevel,
				 getBranchOffice(), Integer.valueOf("1"), Short.valueOf("1"),
					"ABCD", "XYZ", "xyz@yahoo.com", null,
					customFieldView, name, "111111", date, Integer
							.valueOf("1"), Integer.valueOf("1"), date, date, address, Short.valueOf("1"));
		loanOfficer.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanOfficer=(PersonnelBO)HibernateUtil.getSessionTL().get(PersonnelBO.class,loanOfficer.getPersonnelId());
		
	}
	
	private void updatePersonnel(PersonnelLevel personnelLevel, PersonnelStatus newStatus , OfficeBO office) throws Exception{
		Address address = new Address("abcd","abcd","abcd","abcd","abcd","abcd","abcd","abcd");
		 Name name = new Name("XYZ", null, null, "Last Name");
		loanOfficer.update(newStatus,
				personnelLevel, office, Integer
						.valueOf("1"), Short.valueOf("1"), "ABCD",
				"rajendersaini@yahoo.com", null, null, name,
				Integer.valueOf("1"), Integer.valueOf("1"), address,
				Short.valueOf("1"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanOfficer=(PersonnelBO)HibernateUtil.getSessionTL().get(PersonnelBO.class,loanOfficer.getPersonnelId());
		
	}
	
	private void createCenter(Short officeId, Short personnelId) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));

		center = TestObjectFactory.createCenter("Center", Short.valueOf("14"),
				"1.4", meeting, officeId, personnelId, new Date(System
						.currentTimeMillis()));
	}
	
	public OfficeBO getBranchOffice(){
		return TestObjectFactory.getOffice(Short.valueOf("3"));
		
	}
}
