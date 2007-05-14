package org.mifos.application.customer.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupBOTest;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.personnel.util.helpers.PersonnelStatus;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
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

	public static void setCustomerStatus(CustomerBO customer,
			CustomerStatusEntity customerStatusEntity) {
		customer.setCustomerStatus(customerStatusEntity);
	}

	public static void setCustomerMeeting(CustomerBO customer,
			CustomerMeetingEntity customerMeeting) {
		customer.setCustomerMeeting(customerMeeting);
	}

	public static void setPersonnel(CustomerBO customer, PersonnelBO personnel) {
		customer.setPersonnel(personnel);
	}

	public static void setDisplayName(CustomerBO customer, String displayName) {
		customer.setDisplayName(displayName);
	}

	public static void setUpdatedFlag(
			CustomerMeetingEntity customerMeetingEntity, Short updatedFlag) {
		customerMeetingEntity.setUpdatedFlag(updatedFlag);
	}

	public void testClientHasAPerClientMandatorySavingsAcccount() 
	throws Exception {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		//accountBO = getLoanAccount(client, meeting);
		HibernateUtil.closeSession();
		/*ClientBO client1 = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());*/
		boolean hasMandatory =
			client.clientHasAPerClientMandatorySavingsAcccount();
		assertEquals(false, hasMandatory);
		TestObjectFactory.cleanUpChangeLog();
	}
	
	public void testRemoveGroupMemberShip() throws Exception {
		createInitialObjects();
		client.setUserContext(TestObjectFactory.getUserContext());
		HibernateUtil.getInterceptor().createInitialValueMap(client);
		createPersonnel(PersonnelLevel.LOAN_OFFICER);
		client.removeGroupMemberShip(loanOfficer, "comment");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanOfficer = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, loanOfficer.getPersonnelId());
		assertEquals(loanOfficer.getPersonnelId(), client.getPersonnel().getPersonnelId());
			group = TestObjectFactory.getObject(GroupBO.class, group
		 .getCustomerId());

		TestObjectFactory.cleanUpChangeLog();
	}
	
	
	
	public void testHasAnActiveLoanCounts() throws Exception {
		createInitialObjects();
		client.setUserContext(TestObjectFactory.getUserContext());
		HibernateUtil.getInterceptor().createInitialValueMap(client);
		boolean res=client.hasAnActiveLoanCounts();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		assertEquals(res,false );
		TestObjectFactory.cleanUpChangeLog();
	}
	public void testCheckIfClientIsATitleHolder() throws Exception {
		createInitialObjects();
		client.setUserContext(TestObjectFactory.getUserContext());
		HibernateUtil.getInterceptor().createInitialValueMap(client);
	
		try{
			client.checkIfClientIsATitleHolder();
		
		}catch (CustomerException expected) {
			assertEquals(CustomerConstants.CLIENT_IS_A_TITLE_HOLDER_EXCEPTION,
					expected.getKey());
		}
		
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	
		TestObjectFactory.cleanUpChangeLog();
	}
	
	//
	
	public void testStatusChangeForCenterForLogging() throws Exception {
		OfficeBO office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		createdBranchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
		HibernateUtil.closeSession();
		createdBranchOffice = (OfficeBO) HibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		createPersonnel(PersonnelLevel.LOAN_OFFICER);

		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());

		center = TestObjectFactory.createCenter("Center", meeting, 
			getBranchOffice().getOfficeId(), loanOfficer.getPersonnelId());
		center.setUserContext(TestUtils.makeUserWithLocales());
		HibernateUtil.getInterceptor().createInitialValueMap(center);
		center.changeStatus(CustomerStatus.CENTER_INACTIVE, null,
				"comment");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		loanOfficer = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, loanOfficer.getPersonnelId());

		List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(
				EntityType.CENTER, center.getCustomerId());
		assertEquals(1, auditLogList.size());
		assertEquals(EntityType.CENTER, 
				auditLogList.get(0).getEntityTypeAsEnum());
		Set<AuditLogRecord> records = auditLogList.get(0).getAuditLogRecords();
		assertEquals(1, records.size());
		AuditLogRecord record = records.iterator().next();
		assertEquals("Status", record.getFieldName());
		assertEquals("Active", record.getOldValue());
		assertEquals("Inactive", record.getNewValue());
		TestObjectFactory.cleanUpChangeLog();
	}

	public void testStatusChangeForGroupForLogging() throws Exception {
		createGroup();

		// This gives us "9" instead of "Active" on the assert
//		group.setUserContext(TestUtils.makeUser());
		group.setUserContext(TestObjectFactory.getUserContext());

		HibernateUtil.getInterceptor().createInitialValueMap(group);
		group.changeStatus(CustomerStatus.GROUP_CANCELLED, 
				CustomerStatusFlag.GROUP_CANCEL_DUPLICATE, 
				"comment");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(
				EntityType.GROUP, group.getCustomerId());
		assertEquals(1, auditLogList.size());
		assertEquals(EntityType.GROUP.getValue(), auditLogList.get(0)
				.getEntityType());
		assertEquals(2, auditLogList.get(0).getAuditLogRecords().size());
		for (AuditLogRecord auditLogRecord : auditLogList.get(0)
				.getAuditLogRecords()) {
			if (auditLogRecord.getFieldName().equalsIgnoreCase("Status")) {
				assertEquals("Active", auditLogRecord.getOldValue());
				assertEquals("Cancelled", auditLogRecord.getNewValue());
			} else if (auditLogRecord.getFieldName().equalsIgnoreCase(
					"Status Change Explanation")) {
				assertEquals("-", auditLogRecord.getOldValue());
				assertEquals("Duplicate", auditLogRecord.getNewValue());
			}
			else {
				fail("unexpected record " + auditLogRecord.getFieldName());
			}
		}
		TestObjectFactory.cleanUpChangeLog();
	}

	public void testGroupPerfObject() throws PersistenceException {
		createInitialObjects();
		GroupPerformanceHistoryEntity groupPerformanceHistory = group
				.getPerformanceHistory();
		GroupBOTest.setLastGroupLoanAmount(groupPerformanceHistory, new Money(
				"100"));
		TestObjectFactory.updateObject(group);
		HibernateUtil.closeSession();
		group = (GroupBO) customerPersistence.findBySystemId(group
				.getGlobalCustNum(), group.getCustomerLevel().getId());
		assertEquals(group.getCustomerId(), group.getPerformanceHistory()
				.getGroup().getCustomerId());
		assertEquals(new Money("100"), group.getPerformanceHistory()
				.getLastGroupLoanAmount());
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
	}

	public void testGroupPerformanceObject() throws Exception {
		GroupPerformanceHistoryEntity groupPerformanceHistory = new GroupPerformanceHistoryEntity(
				Integer.valueOf("1"), new Money("23"), new Money("24"),
				new Money("26"), new Money("25"), new Money("27"));
		assertEquals(new Money("23"), groupPerformanceHistory
				.getLastGroupLoanAmount());
		assertEquals(new Money("27"), groupPerformanceHistory
				.getPortfolioAtRisk());
		assertEquals(1, groupPerformanceHistory.getClientCount().intValue());

	}

	public void testClientPerfObject() throws PersistenceException {
		createInitialObjects();
		ClientPerformanceHistoryEntity clientPerformanceHistory = client
				.getPerformanceHistory();
		clientPerformanceHistory.setNoOfActiveLoans(Integer.valueOf("1"));
		clientPerformanceHistory.setLastLoanAmount(new Money("100"));
		TestObjectFactory.updateObject(client);
		client = (ClientBO) customerPersistence.findBySystemId(client
				.getGlobalCustNum(), client.getCustomerLevel().getId());
		assertEquals(client.getCustomerId(), client.getPerformanceHistory()
				.getClient().getCustomerId());
		assertEquals(new Money("100"), client.getPerformanceHistory()
				.getLastLoanAmount());
		assertEquals(new Money("0"), client.getPerformanceHistory()
				.getDelinquentPortfolioAmount());
	}

	public void testGetBalanceForAccountsAtRisk() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group, meeting);
		TestObjectFactory.flushandCloseSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertEquals(new Money(), group.getBalanceForAccountsAtRisk());
		changeFirstInstallmentDate(accountBO, 31);
		assertEquals(new Money("300"), group.getBalanceForAccountsAtRisk());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testGetDelinquentPortfolioAmount() {
		createInitialObjects();
		accountBO = getLoanAccount(client, meeting);
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		setActionDateToPastDate();
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		client = TestObjectFactory.getObject(ClientBO.class,
				client.getCustomerId());
		assertEquals(new Money("0.7"), client.getDelinquentPortfolioAmount());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testGetOutstandingLoanAmount() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group, meeting);
		TestObjectFactory.flushandCloseSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(new Money("300.0"), group.getOutstandingLoanAmount());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testGetActiveLoanCounts() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group, meeting);
		TestObjectFactory.flushandCloseSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(1, group.getActiveLoanCounts().intValue());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testGetLoanAccountInUse() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group, meeting);
		TestObjectFactory.flushandCloseSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		List<LoanBO> loans = group.getOpenLoanAccounts();
		assertEquals(1, loans.size());
		assertEquals(accountBO.getAccountId(), loans.get(0).getAccountId());
		TestObjectFactory.flushandCloseSession();

		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testGetSavingsAccountInUse() throws Exception {
		accountBO = getSavingsAccount("fsaf6", "ads6");
		TestObjectFactory.flushandCloseSession();
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		List<SavingsBO> savings = client.getOpenSavingAccounts();
		assertEquals(1, savings.size());
		assertEquals(accountBO.getAccountId(), savings.get(0).getAccountId());
		TestObjectFactory.flushandCloseSession();

		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testHasAnyLoanAccountInUse() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group, meeting);
		TestObjectFactory.flushandCloseSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertTrue(group.isAnyLoanAccountOpen());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testHasAnySavingsAccountInUse() throws Exception {
		accountBO = getSavingsAccount("fsaf5", "ads5");
		TestObjectFactory.flushandCloseSession();
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertTrue(client.isAnySavingsAccountOpen());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testgetSavingsBalance() throws Exception {
		SavingsBO savings = getSavingsAccount("fsaf4", "ads4");
		TestSavingsBO.setBalance(savings, new Money("1000"));
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = TestObjectFactory.getObject(SavingsBO.class,
				savings.getAccountId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals(new Money("1000.0"), savings.getSavingsBalance());
		assertEquals(new Money("1000.0"), client.getSavingsBalance());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		savings = TestObjectFactory.getObject(SavingsBO.class,
				savings.getAccountId());
		TestObjectFactory.cleanUp(savings);
	}

	public void testValidateStatusWithActiveGroups() throws CustomerException,
			AccountException {
		createInitialObjects();
		try {
			center.changeStatus(CustomerStatus.CENTER_INACTIVE, null, "Test");
			fail();
		} catch (CustomerException expected) {
			assertEquals(
				CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION,
				expected.getKey()
			);
			assertEquals(CustomerStatus.CENTER_ACTIVE, center.getStatus());
		}
	}

	public void testValidateStatusForClientWithCancelledGroups()
			throws Exception {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_CANCELLED, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_PARTIAL, group);
		try {
			client.changeStatus(CustomerStatus.CLIENT_ACTIVE, null, "Test");
			fail();
		} catch (CustomerException expected) {
			assertEquals(
				ClientConstants.ERRORS_GROUP_CANCELLED,
				expected.getKey()
			);
			assertEquals(CustomerStatus.CLIENT_PARTIAL, client.getStatus());
		}
	}

	public void testValidateStatusForClientWithPartialGroups() throws Exception {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_PARTIAL, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_PARTIAL, group);
		try {
			client.changeStatus(CustomerStatus.CLIENT_ACTIVE, null, "Test");
			fail();
		} catch (CustomerException sce) {
			assertEquals(
				ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION,
				sce.getKey());
			assertEquals(CustomerStatus.CLIENT_PARTIAL, client.getStatus());
		}
	}

	public void testValidateStatusForClientWithActiveAccounts()
			throws Exception {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		accountBO = getLoanAccount(client, meeting);
		HibernateUtil.closeSession();
		client = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		try {
			client.changeStatus(CustomerStatus.CLIENT_CLOSED, null, "Test");
			fail();
		} catch (CustomerException expected) {
			assertEquals(
				CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION,
				expected.getKey()
			);
			assertEquals(CustomerStatus.CLIENT_ACTIVE, client.getStatus());
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

	public void testValidateStatusChangeForCustomerWithInactiveLoanofficerAssigned()
			throws Exception {
		createPersonnel(PersonnelLevel.LOAN_OFFICER);
		createCenter(getBranchOffice().getOfficeId(), loanOfficer
				.getPersonnelId());
		center.changeStatus(CustomerStatus.CENTER_INACTIVE, null,
				"comment");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanOfficer = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, loanOfficer.getPersonnelId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		updatePersonnel(PersonnelLevel.LOAN_OFFICER, PersonnelStatus.INACTIVE,
				getBranchOffice());
		try {
			center.changeStatus(CustomerStatus.CENTER_ACTIVE, null,
					"comment");
			fail();
		} catch (CustomerException expected) {
			assertEquals(
				CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION,
				expected.getKey()
			);
			assertEquals(CustomerStatus.CENTER_INACTIVE, center.getStatus());
		}
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		loanOfficer = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, loanOfficer.getPersonnelId());
	}

	public void testValidateStatusChangeForCustomerWithLoanofficerAssignedToDifferentBranch()
			throws Exception {
		OfficeBO office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		createdBranchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
		HibernateUtil.closeSession();
		createdBranchOffice = (OfficeBO) HibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		createPersonnel(PersonnelLevel.LOAN_OFFICER);
		createCenter(getBranchOffice().getOfficeId(), loanOfficer
				.getPersonnelId());
		center.changeStatus(CustomerStatus.CENTER_INACTIVE, null,
				"comment");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanOfficer = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, loanOfficer.getPersonnelId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		updatePersonnel(PersonnelLevel.LOAN_OFFICER, PersonnelStatus.ACTIVE,
				createdBranchOffice);
		try {
			center.changeStatus(CustomerStatus.CENTER_ACTIVE, null,
					"comment");
			assertFalse(true);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(ce.getKey(),
					CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION);
			assertEquals(CustomerStatus.CENTER_INACTIVE.getValue(), center
					.getCustomerStatus().getId());
		}
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		loanOfficer = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, loanOfficer.getPersonnelId());
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
		try {
			client.changeStatus(CustomerStatus.CLIENT_CLOSED, null, "Test");
			fail();
		} catch (CustomerException expected) {
			assertEquals(
				CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION,
				expected.getKey()
			);
			assertEquals(CustomerStatus.CLIENT_ACTIVE, client.getStatus());
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

	public void testApplicablePrdforCustomLevel() throws Exception {
		createInitialObjects();
		assertEquals(Short.valueOf("1"), client.getCustomerLevel()
				.getProductApplicableType());
		assertEquals(Short.valueOf("3"), center.getCustomerLevel()
				.getProductApplicableType());
	}

	public void testCustomerPerformanceView() throws Exception {
		CustomerPerformanceHistoryView customerPerformanceView = new CustomerPerformanceHistoryView(
				Integer.valueOf("1"), Integer.valueOf("1"), "10");

		assertEquals(1, customerPerformanceView
				.getMeetingsAttended().intValue());
		assertEquals(1, customerPerformanceView
				.getMeetingsMissed().intValue());
		assertEquals("10", customerPerformanceView
				.getLastLoanAmount());

	}
	
	public void testCustomerPositionView() throws Exception {
		CustomerPositionView customerPositionView = new CustomerPositionView(
				Integer.valueOf("1"), Short.valueOf("2"));

		assertEquals(1, customerPositionView
				.getCustomerId().intValue());
		assertEquals(2, customerPositionView
				.getPositionId().shortValue());
		

	}
	
	public void testCustomerStatusFlagEntity() throws Exception {
		CustomerStatusFlagEntity customerStatusFlag = (CustomerStatusFlagEntity)TestObjectFactory.getObject(CustomerStatusFlagEntity.class,Short.valueOf("1"));
		assertEquals("Withdraw", customerStatusFlag.getFlagDescription());
		customerStatusFlag.setFlagDescription("Other");
		assertEquals("Other", customerStatusFlag.getFlagDescription());

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
			TestLoanBO.setActionDate(accountActionDateEntity,
					new java.sql.Date(currentDateCalendar.getTimeInMillis()));
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
				TestLoanBO.setActionDate(installment, lastWeekDate);
			} else if (installment.getInstallmentId().intValue() == 2) {
				TestLoanBO.setActionDate(installment, twoWeeksBeforeDate);
			}
		}
	}

	private SavingsBO getSavingsAccount(String offeringName, String shortName)
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering(offeringName, shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017",
				client, AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}

	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center",
				meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, 
				startDate, loanOffering);

	}

	private void createPersonnel(PersonnelLevel personnelLevel)
			throws Exception {
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456",
				CustomFieldType.NUMERIC));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Name name = new Name("XYZ", null, null, "Last Name");
		java.util.Date date = new java.util.Date();
		loanOfficer = new PersonnelBO(personnelLevel, getBranchOffice(),
				Integer.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
				"xyz@yahoo.com", null, customFieldView, name, "111111", date,
				Integer.valueOf("1"), Integer.valueOf("1"), date, date,
				address, Short.valueOf("1"));
		loanOfficer.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanOfficer = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, loanOfficer.getPersonnelId());

	}

	private void updatePersonnel(PersonnelLevel personnelLevel,
			PersonnelStatus newStatus, OfficeBO office) throws Exception {
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Name name = new Name("XYZ", null, null, "Last Name");
		loanOfficer.update(newStatus, personnelLevel, office, Integer
				.valueOf("1"), Short.valueOf("1"), "ABCD",
				"rajendersaini@yahoo.com", null, null, name, Integer
						.valueOf("1"), Integer.valueOf("1"), address, Short
						.valueOf("1"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanOfficer = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, loanOfficer.getPersonnelId());

	}

	private void createCenter(Short officeId, Short personnelId) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());

		center = TestObjectFactory.createCenter("Center", meeting, officeId, personnelId);
	}

	private void createGroup() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center",
				meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
	}

	public OfficeBO getBranchOffice() {
		return TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);

	}
}
