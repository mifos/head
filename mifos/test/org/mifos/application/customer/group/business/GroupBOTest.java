package org.mifos.application.customer.group.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.business.CustomerMovementEntity;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupBOTest extends MifosTestCase {

	private AccountBO account1 = null;

	private AccountBO account2 = null;

	private CenterBO center;

	private CenterBO center1 = null;

	private GroupBO group;

	private GroupBO group1;

	private ClientBO client;

	private ClientBO client1 = null;

	private ClientBO client2 = null;

	private MeetingBO meeting;

	private OfficeBO officeBO;

	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsOfferingBO savingsOffering;

	private Short officeId = 3;

	private Short office = 1;

	private Short personnel = 3;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(account2);
		TestObjectFactory.cleanUp(account1);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(center1);
		TestObjectFactory.cleanUp(officeBO);
		HibernateUtil.closeSession();
	}

	public void testCreateWithoutName() throws Exception {
		try {
			group = new GroupBO(TestObjectFactory.getUserContext(), "",
					CustomerStatus.GROUP_PARTIAL, null, false, null, null,
					null, null, personnel, office, meeting, personnel);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_NAME, ce.getKey());
		}
	}

	public void testCreateWithoutStatus() throws Exception {
		try {
			group = new GroupBO(TestObjectFactory.getUserContext(),
					"GroupName", null, null, false, null, null, null, null,
					personnel, office, meeting, personnel);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_STATUS, ce.getKey());
		}
	}

	public void testCreateWithoutOffice_WithoutCenterHierarchy()
			throws Exception {
		try {
			group = new GroupBO(TestObjectFactory.getUserContext(),
					"GroupName", CustomerStatus.GROUP_PARTIAL, null, false,
					null, null, null, null, personnel, null, meeting, personnel);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_OFFICE, ce.getKey());
		}
	}

	public void testCreateWithoutLO_InActiveState_WithoutCenterHierarchy()
			throws Exception {
		try {
			group = new GroupBO(TestObjectFactory.getUserContext(),
					"GroupName", CustomerStatus.GROUP_ACTIVE, null, false,
					null, null, null, null, personnel, office, meeting, null);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_LOAN_OFFICER, ce.getKey());
		}
	}

	public void testCreateWithoutMeeting_InActiveState_WithoutCenterHierarchy()
			throws Exception {
		try {
			group = new GroupBO(TestObjectFactory.getUserContext(),
					"GroupName", CustomerStatus.GROUP_ACTIVE, null, false,
					null, null, null, null, personnel, office, null, personnel);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_MEETING, ce.getKey());
		}
	}

	public void testCreateWithoutParent_WhenCenter_HierarchyExists()
			throws Exception {
		try {
			meeting = getMeeting();
			group = new GroupBO(TestObjectFactory.getUserContext(),
					"GroupName", CustomerStatus.GROUP_PARTIAL, null, false,
					null, null, null, null, personnel, null);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_PARENT, ce.getKey());
		}
	}

	public void testCreateWithoutFormedBy() throws Exception {
		try {
			createCenter();
			meeting = getMeeting();
			group = new GroupBO(TestObjectFactory.getUserContext(),
					"GroupName", CustomerStatus.GROUP_PARTIAL, null, false,
					null, null, null, null, null, center);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_FORMED_BY, ce.getKey());
		}
	}

	public void testCreateWithoutTrainedDate_WhenTrained() throws Exception {
		try {
			createCenter();
			meeting = getMeeting();
			group = new GroupBO(TestObjectFactory.getUserContext(),
					"GroupName", CustomerStatus.GROUP_PARTIAL, null, true,
					null, null, null, null, personnel, center);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_TRAINED_OR_TRAINEDDATE, ce
					.getKey());
		}
		TestObjectFactory.removeObject(meeting);
	}

	public void testFailureCreate_DuplicateName() throws Exception {
		String name = "GroupTest";
		createCenter();
		createGroup(name);
		HibernateUtil.closeSession();

		List<FeeView> fees = getFees();
		try {
			group1 = new GroupBO(TestObjectFactory.getUserContext(), name,
					CustomerStatus.GROUP_ACTIVE, null, false, null, null, null,
					fees, personnel, center);
			assertFalse(true);
		} catch (CustomerException e) {
			assertTrue(true);
			assertNull(group1);
			assertEquals(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, e
					.getKey());
		}
		removeFees(fees);
	}

	public void testSuccessfulCreate_Group_UnderCenter() throws Exception {
		createCenter();
		String name = "GroupTest";
		Date trainedDate = getDate("11/12/2005");
		String externalId = "1234";
		HibernateUtil.closeSession();
		assertEquals(0, center.getMaxChildCount().intValue());

		group = new GroupBO(TestObjectFactory.getUserContext(), name,
				CustomerStatus.GROUP_ACTIVE, externalId, true, trainedDate,
				getAddress(), getCustomFields(), getFees(), personnel, center);
		group.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());

		assertEquals(name, group.getDisplayName());
		assertEquals(externalId, group.getExternalId());
		assertTrue(group.isTrained());
		assertEquals(trainedDate, DateUtils.getDateWithoutTimeStamp(group
				.getTrainedDate().getTime()));
		assertEquals(CustomerStatus.GROUP_ACTIVE, group.getStatus());
		Address address = group.getCustomerAddressDetail().getAddress();
		assertEquals("Aditi", address.getLine1());
		assertEquals("Bangalore", address.getCity());
		assertEquals(getCustomFields().size(), group.getCustomFields().size());
		assertEquals(1, center.getMaxChildCount().intValue());
		assertEquals(center.getPersonnel().getPersonnelId(), group
				.getPersonnel().getPersonnelId());
		assertEquals("1.1.1", group.getSearchId());
		assertEquals(group.getCustomerId(), group.getPerformanceHistory()
				.getGroup().getCustomerId());
		client = TestObjectFactory.createClient("new client",
				CustomerStatus.CLIENT_ACTIVE.getValue(), group,
				new java.util.Date());
		assertEquals(1, group.getPerformanceHistory().getActiveClientCount()
				.intValue());
	}

	public void testSuccessfulCreate_Group_UnderBranch() throws Exception {
		String name = "GroupTest";
		String externalId = "1234";
		group = new GroupBO(TestObjectFactory.getUserContext(), name,
				CustomerStatus.GROUP_ACTIVE, externalId, false, null,
				getAddress(), getCustomFields(), getFees(), personnel, office,
				getMeeting(), personnel);
		group.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());

		assertEquals(name, group.getDisplayName());
		assertEquals(externalId, group.getExternalId());
		assertFalse(group.isTrained());
		assertEquals(CustomerStatus.GROUP_ACTIVE, group.getStatus());
		Address address = group.getCustomerAddressDetail().getAddress();
		assertEquals("Aditi", address.getLine1());
		assertEquals("Bangalore", address.getCity());
		assertEquals(getCustomFields().size(), group.getCustomFields().size());

		assertEquals(personnel, group.getCustomerFormedByPersonnel()
				.getPersonnelId());
		assertEquals(personnel, group.getPersonnel().getPersonnelId());
		assertEquals(office, group.getOffice().getOfficeId());
		assertNotNull(group.getCustomerMeeting().getMeeting());
		assertEquals("1.1", group.getSearchId());
	}

	public void testSuccessfulUpdate_Group_UnderBranch() throws Exception {
		String name = "Group_underBranch";
		String newName = "Group_NameChanged";
		group = createGroupUnderBranch(name, CustomerStatus.GROUP_ACTIVE);
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(name, group.getDisplayName());
		group.update(TestObjectFactory.getUserContext(), newName, personnel,
				" ", Short.valueOf("1"), new Date(), TestObjectFactory
						.getAddressHelper(), getCustomFields(),
				new ArrayList<CustomerPositionView>());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(newName, group.getDisplayName());
		assertTrue(group.isTrained());

	}

	public void testSuccessfulUpdate_Group_UnderCenter() throws Exception {
		String name = "Group_underBranch";
		String newName = "Group_NameChanged";
		createCenter();
		createGroup(name);
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(name, group.getDisplayName());
		group.update(TestObjectFactory.getUserContext(), newName, personnel,
				" ", Short.valueOf("1"), new Date(), TestObjectFactory
						.getAddressHelper(), getCustomFields(),
				new ArrayList<CustomerPositionView>());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(newName, group.getDisplayName());
		assertTrue(group.isTrained());

	}

	public void testFailureUpdate_ActiveGroup_WithoutLoanOfficer()
			throws Exception {
		String name = "Group_underBranch";
		String newName = "Group_NameChanged";
		group = createGroupUnderBranch(name, CustomerStatus.GROUP_ACTIVE);
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(name, group.getDisplayName());
		try {
			group.update(TestObjectFactory.getUserContext(), newName, null,
					" ", Short.valueOf("1"), new Date(), TestObjectFactory
							.getAddressHelper(), getCustomFields(),
					new ArrayList<CustomerPositionView>());
			assertFalse(true);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.INVALID_LOAN_OFFICER, ce.getKey());
		}

	}

	public void testFailureUpdate_OnHoldGroup_WithoutLoanOfficer()
			throws Exception {
		String name = "Group_underBranch";
		String newName = "Group_NameChanged";
		group = createGroupUnderBranch(name, CustomerStatus.GROUP_HOLD);
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(name, group.getDisplayName());
		try {
			group.update(TestObjectFactory.getUserContext(), newName, null,
					" ", Short.valueOf("1"), new Date(), TestObjectFactory
							.getAddressHelper(), getCustomFields(),
					new ArrayList<CustomerPositionView>());
			assertFalse(true);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.INVALID_LOAN_OFFICER, ce.getKey());
		}

	}

	public void testFailureUpdate_Group_WithDuplicateName() throws Exception {
		String name = "Group_underBranch";
		String newName = "Group_NameChanged";
		group = createGroupUnderBranch(name, CustomerStatus.GROUP_ACTIVE);
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group1 = createGroupUnderBranch(newName, CustomerStatus.GROUP_ACTIVE);
		group1 = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group1
				.getCustomerId());
		assertEquals(name, group.getDisplayName());
		assertEquals(newName, group1.getDisplayName());
		try {
			group1.update(TestObjectFactory.getUserContext(), name, personnel,
					" ", Short.valueOf("1"), new Date(), TestObjectFactory
							.getAddressHelper(), getCustomFields(),
					new ArrayList<CustomerPositionView>());
			assertFalse(true);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, ce
					.getKey());
		}

	}

	public void testGeneratePortfolioAtRisk() throws Exception {
		createInitialObject();
		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		for (AccountBO account : group.getAccounts()) {
			if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.LOANACCOUNT.getValue())) {
				changeFirstInstallmentDate(account, 31);
			}
		}
		for (AccountBO account : client.getAccounts()) {
			if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.LOANACCOUNT.getValue())) {
				changeFirstInstallmentDate(account, 31);
			}
		}
		group.getPerformanceHistory().generatePortfolioAtRisk();
		assertEquals(new Money("1.0"), ((GroupBO) group)
				.getPerformanceHistory().getPortfolioAtRisk());
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		account1 = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
		account2 = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				account2.getAccountId());
	}

	public void testGetTotalOutStandingLoanAmount() throws Exception {
		createInitialObject();
		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(new Money("600.0"), ((GroupBO) group).getPerformanceHistory().getTotalOutStandingLoanAmount());
		assertEquals(new Money("600.0"), ((GroupBO) group)
				.getPerformanceHistory().getTotalOutStandingLoanAmount());
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		account1 = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
		account2 = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				account2.getAccountId());
	}

	public void testGetAverageLoanAmount() throws Exception {
		createInitialObject();
		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(new Money("300.0"), ((GroupBO) group).getPerformanceHistory().getAvgLoanAmountForMember());
		assertEquals(new Money("300.0"), ((GroupBO) group)
				.getPerformanceHistory().getAvgLoanAmountForMember());
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		account1 = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
		account2 = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				account2.getAccountId());
	}

	public void testGetTotalSavingsBalance() throws Exception {
		createInitialObjects();
		SavingsBO savings1 = getSavingsAccount(group, "fsaf6", "ads6");
		savings1.setSavingsBalance(new Money("1000"));
		savings1.update();
		SavingsBO savings2 = getSavingsAccount(client, "fsaf5", "ads5");
		savings2.setSavingsBalance(new Money("2000"));
		savings1.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings1 = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,
				savings1.getAccountId());
		savings2 = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,
				savings2.getAccountId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(new Money("1000.0"), savings1.getSavingsBalance());
		assertEquals(new Money("2000.0"), savings2.getSavingsBalance());
		assertEquals(new Money("2000.0"), ((ClientBO) client)
				.getSavingsBalance());
		assertEquals(new Money("3000.0"), ((GroupBO) group).getPerformanceHistory().getTotalSavingsAmount());
		TestObjectFactory.flushandCloseSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		savings1 = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,
				savings1.getAccountId());
		savings2 = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,
				savings2.getAccountId());
		TestObjectFactory.cleanUp(savings1);
		TestObjectFactory.cleanUp(savings2);
	}

	public void testGetActiveOnHoldChildrenOfGroup() throws Exception {
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
		client1 = TestObjectFactory.createClient("client2",
				ClientConstants.STATUS_HOLD, "1.4.1.2", group, new Date(System
						.currentTimeMillis()));
		client2 = TestObjectFactory.createClient("client3",
				ClientConstants.STATUS_CANCELLED, "1.4.1.3", group, new Date(
						System.currentTimeMillis()));
		assertEquals(Integer.valueOf("2"), ((GroupBO) group).getPerformanceHistory().getActiveClientCount());
	}

	public void testUpdateBranchFailure_OfficeNULL() throws Exception {
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
		try {
			group.transferToBranch(null);
			assertTrue(false);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.INVALID_OFFICE, ce.getKey());
		}
	}

	public void testUpdateBranchFailure_TransferInSameOffice() throws Exception {
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
		try {
			group.transferToBranch(group.getOffice());
			assertTrue(false);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER, ce
					.getKey());
		}
	}

	public void testUpdateBranchFailure_DuplicateGroupName() throws Exception {
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
		officeBO = createOffice();
		group1 = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE, officeBO
				.getOfficeId());
		try {
			group.transferToBranch(officeBO);
			assertTrue(false);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, ce
					.getKey());
		}
	}

	public void testSuccessfulTransferToBranch() throws Exception {
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
		client = createClient(group, CustomerStatus.CLIENT_ACTIVE);
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		officeBO = createOffice();
		client2.changeStatus(CustomerStatus.CLIENT_CLOSED.getValue(), Short
				.valueOf("6"), "comment");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group.setUserContext(TestObjectFactory.getUserContext());
		assertNull(client.getActiveCustomerMovement());

		group.transferToBranch(officeBO);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		client1 = (ClientBO) TestObjectFactory.getObject(ClientBO.class,
				client1.getCustomerId());
		client2 = (ClientBO) TestObjectFactory.getObject(ClientBO.class,
				client2.getCustomerId());
		officeBO = new OfficePersistence().getOffice(officeBO.getOfficeId());
		assertNotNull(group.getActiveCustomerMovement());
		assertNotNull(client.getActiveCustomerMovement());
		assertNotNull(client1.getActiveCustomerMovement());
		assertNotNull(client2.getActiveCustomerMovement());

		assertEquals(officeBO.getOfficeId(), group.getOffice().getOfficeId());
		assertEquals(officeBO.getOfficeId(), client.getOffice().getOfficeId());
		assertEquals(officeBO.getOfficeId(), client1.getOffice().getOfficeId());
		assertEquals(officeBO.getOfficeId(), client2.getOffice().getOfficeId());

		assertEquals(CustomerStatus.GROUP_HOLD, group.getStatus());
		assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
		assertEquals(CustomerStatus.CLIENT_PARTIAL, client1.getStatus());
		assertEquals(CustomerStatus.CLIENT_CLOSED, client2.getStatus());

		assertNull(group.getPersonnel());
		assertNull(client.getPersonnel());
		assertNull(client1.getPersonnel());
		assertNull(client2.getPersonnel());
	}

	public void testUpdateCenterFailure_CenterNULL() throws Exception {
		createInitialObjects();
		try {
			group.transferToCenter(null);
			assertTrue(false);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.INVALID_PARENT, ce.getKey());
		}
	}

	public void testUpdateCenterFailure_TransferInSameCenter() throws Exception {
		createInitialObjects();
		try {
			group.transferToCenter((CenterBO) group.getParentCustomer());
			assertTrue(false);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_SAME_PARENT_TRANSFER, ce
					.getKey());
		}
	}

	public void testUpdateCenterFailure_GroupHasActiveAccount()
			throws Exception {
		createInitialObjects();
		account1 = getSavingsAccount(group, "Savings Prod", "SAVP");
		center1 = createCenter("newCenter");
		HibernateUtil.closeSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		try {
			group.transferToCenter(center1);
			assertTrue(false);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_HAS_ACTIVE_ACCOUNT, ce
					.getKey());
		}
		HibernateUtil.closeSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		account1 = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
	}

	public void testUpdateCenterFailure_GroupChildrenHasActiveAccount()
			throws Exception {
		createInitialObjects();
		account1 = getSavingsAccount(client, "Savings Prod", "SAVP");
		center1 = createCenter("newCenter");
		HibernateUtil.closeSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		try {
			group.transferToCenter(center1);
			assertTrue(false);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT,
					ce.getKey());
		}
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		account1 = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
	}

	public void testSuccessfulTransferToCenterInSameBranch() throws Exception {
		createObjectsForTranferToCenterInSameBranch();
		String newCenterSearchId = center1.getSearchId();
		HibernateUtil.closeSession();

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group.setUserContext(center.getUserContext());
		group.transferToCenter(center1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		center1 = (CenterBO) TestObjectFactory.getObject(CenterBO.class,
				center1.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group1 = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group1
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		client1 = (ClientBO) TestObjectFactory.getObject(ClientBO.class,
				client1.getCustomerId());
		client2 = (ClientBO) TestObjectFactory.getObject(ClientBO.class,
				client2.getCustomerId());

		assertEquals(center1.getCustomerId(), group.getParentCustomer()
				.getCustomerId());
		assertEquals(0, center.getMaxChildCount().intValue());
		assertEquals(2, center1.getMaxChildCount().intValue());
		assertEquals(3, group.getMaxChildCount().intValue());

		assertEquals(newCenterSearchId + ".2", group.getSearchId());
		assertEquals(group.getSearchId() + ".1", client.getSearchId());
		assertEquals(group.getSearchId() + ".2", client1.getSearchId());
		assertEquals(group.getSearchId() + ".3", client2.getSearchId());

		assertNull(group.getActiveCustomerMovement());
		assertNull(client.getActiveCustomerMovement());
		assertNull(client1.getActiveCustomerMovement());
		assertNull(client2.getActiveCustomerMovement());

		CustomerHierarchyEntity currentHierarchy = group
				.getActiveCustomerHierarchy();
		assertEquals(center1.getCustomerId(), currentHierarchy
				.getParentCustomer().getCustomerId());
	}

	public void testSuccessfulTransferToCenterInDifferentBranch()
			throws Exception {
		createObjectsForTranferToCenterInDifferentBranch();
		String newCenterSearchId = center1.getSearchId();
		HibernateUtil.closeSession();

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group.setUserContext(center.getUserContext());
		group.transferToCenter(center1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		center1 = (CenterBO) TestObjectFactory.getObject(CenterBO.class,
				center1.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group1 = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group1
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		client1 = (ClientBO) TestObjectFactory.getObject(ClientBO.class,
				client1.getCustomerId());
		client2 = (ClientBO) TestObjectFactory.getObject(ClientBO.class,
				client2.getCustomerId());
		officeBO = new OfficePersistence().getOffice(officeBO.getOfficeId());

		assertEquals(center1.getCustomerId(), group.getParentCustomer()
				.getCustomerId());
		assertEquals(0, center.getMaxChildCount().intValue());
		assertEquals(2, center1.getMaxChildCount().intValue());
		assertEquals(3, group.getMaxChildCount().intValue());

		assertEquals(newCenterSearchId + ".2", group.getSearchId());
		assertEquals(group.getSearchId() + ".1", client.getSearchId());
		assertEquals(group.getSearchId() + ".2", client1.getSearchId());
		assertEquals(group.getSearchId() + ".3", client2.getSearchId());

		assertEquals(CustomerStatus.GROUP_HOLD.getValue(), group
				.getCustomerStatus().getId());
		assertEquals(CustomerStatus.CLIENT_HOLD.getValue(), client
				.getCustomerStatus().getId());
		assertEquals(CustomerStatus.CLIENT_PARTIAL.getValue(), client1
				.getCustomerStatus().getId());
		assertEquals(CustomerStatus.CLIENT_CANCELLED.getValue(), client2
				.getCustomerStatus().getId());

		CustomerHierarchyEntity currentHierarchy = group
				.getActiveCustomerHierarchy();
		assertEquals(center1.getCustomerId(), currentHierarchy
				.getParentCustomer().getCustomerId());

		assertNotNull(group.getActiveCustomerMovement());
		assertNotNull(client.getActiveCustomerMovement());
		assertNotNull(client1.getActiveCustomerMovement());
		assertNotNull(client2.getActiveCustomerMovement());

		CustomerMovementEntity customerMovement = group
				.getActiveCustomerMovement();
		assertEquals(officeBO.getOfficeId(), customerMovement.getOffice()
				.getOfficeId());

		assertEquals(officeBO.getOfficeId(), center1.getOffice().getOfficeId());
		assertEquals(officeBO.getOfficeId(), group.getOffice().getOfficeId());
		assertEquals(officeBO.getOfficeId(), client.getOffice().getOfficeId());
		assertEquals(officeBO.getOfficeId(), client1.getOffice().getOfficeId());
		assertEquals(officeBO.getOfficeId(), client2.getOffice().getOfficeId());
	}

	public void testUpdateMeeting()throws Exception{
		group = createGroupUnderBranch(CustomerStatus.GROUP_PENDING);
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_PENDING);
		
		MeetingBO groupMeeting = group.getCustomerMeeting().getMeeting();
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, groupMeeting.getMeetingDetails().getRecurAfter(), groupMeeting.getStartDate(), MeetingType.CUSTOMERMEETING, meetingPlace);
		group.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client1 = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());
		
		assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		
		assertEquals(meetingPlace, group.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client1.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client2.getCustomerMeeting().getMeeting().getMeetingPlace());
	}
	
	public void testCreateMeeting()throws Exception{
		group = createGroupUnderBranchWithoutMeeting("MyGroup");
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_PENDING);
		HibernateUtil.closeSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group.setUserContext(TestObjectFactory.getContext());
		String meetingPlace = "newPlace";
		Short recurAfter = Short.valueOf("4");
		MeetingBO newMeeting = new MeetingBO(WeekDay.FRIDAY, recurAfter, new Date(), MeetingType.CUSTOMERMEETING, meetingPlace);
		group.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client1 = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());
		
		assertEquals(WeekDay.FRIDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.FRIDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.FRIDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		
		assertEquals(meetingPlace, group.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client1.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client2.getCustomerMeeting().getMeeting().getMeetingPlace());
		
		assertEquals(recurAfter, group.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
		assertEquals(recurAfter, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
		assertEquals(recurAfter, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
	}
	
	private GroupBO createGroupUnderBranchWithoutMeeting(String name) {
		return TestObjectFactory.createGroupUnderBranch(name, CustomerStatus.GROUP_PENDING,
				office, null, personnel);
	}
	
	private void createCenter() {
		meeting = getMeeting();
		center = TestObjectFactory.createCenter("Center",
				CustomerStatus.CENTER_ACTIVE.getValue(), "1.1", meeting,
				new Date(System.currentTimeMillis()));
	}
	
	private CenterBO createCenter(String name) {
		return createCenter(name, officeId);
	}

	private CenterBO createCenter(String name, Short officeId) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createCenter(name, meeting, officeId,
				personnel);
	}

	private void createGroup(String name) {
		group = TestObjectFactory.createGroupUnderCenter(name,
				CustomerStatus.GROUP_ACTIVE, center);
	}

	private GroupBO createGroupUnderBranch(String name,
			CustomerStatus customerStatus) {
		meeting = getMeeting();
		return TestObjectFactory.createGroupUnderBranch(name, customerStatus,
				office, meeting, personnel);
	}
	
	private GroupBO createGroupUnderBranch(CustomerStatus groupStatus) {
		return createGroupUnderBranch(groupStatus, officeId);
	}

	private GroupBO createGroupUnderBranch(CustomerStatus groupStatus,
			Short officeId) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createGroupUnderBranch("group1", groupStatus,
				officeId, meeting, personnel);
	}

	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
	//	meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}

	private void removeFees(List<FeeView> feesToRemove) {
		for (FeeView fee : feesToRemove) {
			TestObjectFactory.cleanUp(new FeePersistence().getFee(fee
					.getFeeIdValue()));
		}
	}

	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(Short.valueOf("4"), "value1",
				CustomFieldType.ALPHA_NUMERIC.getValue()));
		return fields;
	}

	private Address getAddress() {
		Address address = new Address();
		address.setLine1("Aditi");
		address.setCity("Bangalore");
		return address;
	}

	private List<FeeView> getFees() {
		List<FeeView> fees = new ArrayList<FeeView>();
		AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory
				.createPeriodicAmountFee("PeriodicAmountFee",
						FeeCategory.GROUP, "200", RecurrenceType.WEEKLY,
						Short.valueOf("2"));
		AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory
				.createOneTimeAmountFee("OneTimeAmountFee",
						FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
		fees.add(new FeeView(fee1));
		fees.add(new FeeView(fee2));
		HibernateUtil.commitTransaction();
		return fees;
	}

	private void createObjectsForTranferToCenterInSameBranch() throws Exception {
		createInitialObjects();
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_CANCELLED);
		center1 = createCenter("toTransfer");
		group1 = createGroup("newGroup", center1);
	}

	private void createObjectsForTranferToCenterInDifferentBranch()
			throws Exception {
		createInitialObjects();
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_CANCELLED);
		officeBO = createOffice();
		center1 = createCenter("toTransfer", officeBO.getOfficeId());
		group1 = createGroup("newGroup", center1);
	}

	private ClientBO createClient(GroupBO group, CustomerStatus clientStatus) {
		return TestObjectFactory.createClient("client1", clientStatus
				.getValue(), group, new Date());
	}

	private GroupBO createGroup(String name, CenterBO center) {
		return TestObjectFactory.createGroup(name, GroupConstants.ACTIVE,
				"1.4.1", center, new Date(System.currentTimeMillis()));
	}

	private OfficeBO createOffice() throws Exception {
		return TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE,
				TestObjectFactory.getOffice(Short.valueOf("1")),
				"customer_office", "cust");
	}

	private void createInitialObjects() {
		center = createCenter("Center");
		group = createGroup("Group", center);
		client = createClient(group, CustomerStatus.CLIENT_ACTIVE);
	}

	private SavingsBO getSavingsAccount(CustomerBO customerBO,
			String offeringName, String shortName) throws Exception {
		savingsOffering = helper.createSavingsOffering(offeringName, shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017",
				customerBO, AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}

	private void createInitialObject() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				center.getSearchId() + ".1", center, new Date(System
						.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				ClientConstants.STATUS_ACTIVE, group.getSearchId() + ".1",
				group, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loandsdasd", "fsad", Short.valueOf("2"), new Date(System
						.currentTimeMillis()), Short.valueOf("1"), 300.0, 1.2,
				Short.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				meeting);
		account1 = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		loanOffering = TestObjectFactory.createLoanOffering("Loandfas", "dsvd",
				Short.valueOf("1"), new Date(System.currentTimeMillis()), Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		account2 = TestObjectFactory.createLoanAccount("42427777341", client,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
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
}
