package org.mifos.application.customer.group.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.business.CustomerMovementEntity;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.business.TestCustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
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
	
	public static void setLastGroupLoanAmount(
			GroupPerformanceHistoryEntity groupPerformanceHistoryEntity,
			Money disburseAmount) {
		groupPerformanceHistoryEntity.setLastGroupLoanAmount(disburseAmount);
	}
	
	public void testChangeUpdatedMeeting()throws Exception{
		String oldMeetingPlace = "Delhi";
		MeetingBO weeklyMeeting = new MeetingBO(WeekDay.FRIDAY, Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMER_MEETING, oldMeetingPlace);
		group = TestObjectFactory.createGroupUnderBranch("group1", CustomerStatus.GROUP_ACTIVE,
				officeId, weeklyMeeting, personnel);
		
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_ACTIVE);
		
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		MeetingBO groupMeeting = group.getCustomerMeeting().getMeeting();
		
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, groupMeeting.getMeetingDetails().getRecurAfter(), groupMeeting.getStartDate(), MeetingType.CUSTOMER_MEETING, meetingPlace);
		
		group.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());
		
		assertEquals(WeekDay.FRIDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.FRIDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.FRIDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		
		assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		
		Integer updatedMeetingId = group.getCustomerMeeting().getUpdatedMeeting().getMeetingId();
		
		client1.changeUpdatedMeeting();
		group.changeUpdatedMeeting();		
		
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());
		
		assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		
		assertNull(group.getCustomerMeeting().getUpdatedMeeting());
		assertNull(client1.getCustomerMeeting().getUpdatedMeeting());
		assertNull(client2.getCustomerMeeting().getUpdatedMeeting());
		 
		MeetingBO meeting = new MeetingPersistence().getMeeting(updatedMeetingId);
		assertNull(meeting);
	}
	
	public void testChangeStatus_UpdatePendingClientToPartial_OnGroupCancelled() throws Exception {
		group = TestObjectFactory.createGroupUnderBranch("MyGroup", CustomerStatus.GROUP_PENDING,
				Short.valueOf("3"), meeting, personnel,null);
		client1 = createClient(group, CustomerStatus.CLIENT_PENDING);
		client2 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		HibernateUtil.closeSession();
		
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group.setUserContext(TestObjectFactory.getContext());
		group.changeStatus(CustomerStatus.GROUP_CANCELLED, null, "Group Cancelled");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());
		
		assertEquals(CustomerStatus.GROUP_CANCELLED,group.getStatus());
		assertEquals(CustomerStatus.CLIENT_PARTIAL,client1.getStatus());
		assertEquals(CustomerStatus.CLIENT_PARTIAL,client2.getStatus());
	}
	
	public void testSuccessfulUpdate_Group_UnderBranchForLoggig() throws Exception {
			String name = "Group_underBranch";
			group = createGroupUnderBranch(name, CustomerStatus.GROUP_ACTIVE,getCustomFields());
			group = TestObjectFactory.getObject(GroupBO.class, group
					.getCustomerId());
			client = TestObjectFactory.createClient("Client",
					CustomerStatus.CLIENT_ACTIVE, group);
			client1 = TestObjectFactory.createClient("Client1",
					CustomerStatus.CLIENT_ACTIVE, group);
			HibernateUtil.getSessionTL();
			HibernateUtil.getInterceptor().createInitialValueMap(group);
			
			List<CustomerPositionView> customerPositionList= new ArrayList<CustomerPositionView>();
			TestCustomerBO.setDisplayName(group,"changed group name");
			group.update(TestUtils.makeUser(), group.getDisplayName(), personnel,
					"ABCD", Short.valueOf("1"), new Date(), TestObjectFactory
							.getAddressHelper(), getNewCustomFields(),
							customerPositionList);
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			group = TestObjectFactory.getObject(GroupBO.class, group
					.getCustomerId());
			
			List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(
					EntityType.GROUP,group.getCustomerId());
			assertEquals(1,auditLogList.size());
			assertEquals(EntityType.GROUP.getValue(),auditLogList.get(0).getEntityType());
			assertEquals(8,auditLogList.get(0).getAuditLogRecords().size());
			for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
				if(auditLogRecord.getFieldName().equalsIgnoreCase("City/District")){
					assertEquals("-",auditLogRecord.getOldValue());
					assertEquals("city",auditLogRecord.getNewValue());
				}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Trained")){
					assertEquals("0",auditLogRecord.getOldValue());
					assertEquals("1",auditLogRecord.getNewValue());
				}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Name")){
					assertEquals("Group_underBranch",auditLogRecord.getOldValue());
					assertEquals("changed group name",auditLogRecord.getNewValue());
				}
			}
			TestObjectFactory.cleanUpChangeLog();
			
	}
	
	public void testSuccessfulTransferToCenterInSameBranchForLogging() throws Exception {
		createObjectsForTranferToCenterInSameBranch();
		HibernateUtil.closeSession();

		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group.setUserContext(center.getUserContext());
		HibernateUtil.getSessionTL();
		HibernateUtil.getInterceptor().createInitialValueMap(group);
		group.transferToCenter(center1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		center1 = TestObjectFactory.getObject(CenterBO.class,
				center1.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class, group1
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class,
				client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class,
				client2.getCustomerId());
		
		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(
				EntityType.GROUP,group.getCustomerId());
		assertEquals(1,auditLogList.size());
		assertEquals(EntityType.GROUP.getValue(),auditLogList.get(0).getEntityType());
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Kendra Name")){
				assertEquals("Center",auditLogRecord.getOldValue());
				assertEquals("toTransfer",auditLogRecord.getNewValue());
			}
			else {
				// TODO: Kendra versus Center?
				//fail();
			}
		}
		TestObjectFactory.cleanUpChangeLog();
	}

	public void testCreateWithoutName() throws Exception {
		try {
			group = new GroupBO(TestUtils.makeUser(), "",
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
			group = new GroupBO(TestUtils.makeUser(),
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
			group = new GroupBO(TestUtils.makeUser(),
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
			group = new GroupBO(TestUtils.makeUser(),
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
			group = new GroupBO(TestUtils.makeUser(),
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
			group = new GroupBO(TestUtils.makeUser(),
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
			group = new GroupBO(TestUtils.makeUser(),
					"GroupName", CustomerStatus.GROUP_PARTIAL, null, false,
					null, null, null, null, null, center);
			fail();
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_FORMED_BY, ce.getKey());
		}
	}

	public void testCreateWithoutTrainedDate_WhenTrained() throws Exception {
		try {
			createCenter();
			meeting = getMeeting();
			group = new GroupBO(TestUtils.makeUser(),
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
			group1 = new GroupBO(TestUtils.makeUser(), name,
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

		group = new GroupBO(TestUtils.makeUser(), name,
				CustomerStatus.GROUP_ACTIVE, externalId, true, trainedDate,
				getAddress(), getCustomFields(), getFees(), personnel, center);
		group.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class, center
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
				CustomerStatus.CLIENT_ACTIVE, group,
				new java.util.Date());
		assertEquals(1, group.getPerformanceHistory().getActiveClientCount()
				.intValue());
	}

	public void testSuccessfulCreate_Group_UnderBranch() throws Exception {
		String name = "GroupTest";
		String externalId = "1234";
		group = new GroupBO(TestUtils.makeUser(), name,
				CustomerStatus.GROUP_ACTIVE, externalId, false, null,
				getAddress(), getCustomFields(), getFees(), personnel, office,
				getMeeting(), personnel);
		group.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group = TestObjectFactory.getObject(GroupBO.class, group
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
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(name, group.getDisplayName());
		group.update(TestUtils.makeUser(), newName, personnel,
				" ", Short.valueOf("1"), new Date(), TestObjectFactory
						.getAddressHelper(), getCustomFields(),
				new ArrayList<CustomerPositionView>());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(newName, group.getDisplayName());
		assertTrue(group.isTrained());

	}

	public void testSuccessfulUpdate_Group_UnderCenter() throws Exception {
		String name = "Group_underBranch";
		String newName = "Group_NameChanged";
		createCenter();
		createGroup(name);
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(name, group.getDisplayName());
		group.update(TestUtils.makeUser(), newName, personnel,
				" ", Short.valueOf("1"), new Date(), TestObjectFactory
						.getAddressHelper(), getCustomFields(),
				new ArrayList<CustomerPositionView>());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(newName, group.getDisplayName());
		assertTrue(group.isTrained());

	}

	public void testFailureUpdate_ActiveGroup_WithoutLoanOfficer()
			throws Exception {
		String name = "Group_underBranch";
		String newName = "Group_NameChanged";
		group = createGroupUnderBranch(name, CustomerStatus.GROUP_ACTIVE);
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(name, group.getDisplayName());
		try {
			group.update(TestUtils.makeUser(), newName, null,
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
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(name, group.getDisplayName());
		try {
			group.update(TestUtils.makeUser(), newName, null,
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
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group1 = createGroupUnderBranch(newName, CustomerStatus.GROUP_ACTIVE);
		group1 = TestObjectFactory.getObject(GroupBO.class, group1
				.getCustomerId());
		assertEquals(name, group.getDisplayName());
		assertEquals(newName, group1.getDisplayName());
		try {
			group1.update(TestUtils.makeUser(), name, personnel,
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
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		for (AccountBO account : group.getAccounts()) {
			if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
				changeFirstInstallmentDate(account, 31);
			}
		}
		for (AccountBO account : client.getAccounts()) {
			if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
				changeFirstInstallmentDate(account, 31);
			}
		}
		group.getPerformanceHistory().generatePortfolioAtRisk();
		assertEquals(new Money("1.0"), group
				.getPerformanceHistory().getPortfolioAtRisk());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		account1 = TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
		account2 = TestObjectFactory.getObject(AccountBO.class,
				account2.getAccountId());
	}

	public void testGetTotalOutStandingLoanAmount() throws Exception {
		createInitialObject();
		TestObjectFactory.flushandCloseSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(new Money("600.0"), group.getPerformanceHistory().getTotalOutStandingLoanAmount());
		assertEquals(new Money("600.0"), group
				.getPerformanceHistory().getTotalOutStandingLoanAmount());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		account1 = TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
		account2 = TestObjectFactory.getObject(AccountBO.class,
				account2.getAccountId());
	}

	public void testGetAverageLoanAmount() throws Exception {
		createInitialObject();
		TestObjectFactory.flushandCloseSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(new Money("300.0"), group.getPerformanceHistory().getAvgLoanAmountForMember());
		assertEquals(new Money("300.0"), group
				.getPerformanceHistory().getAvgLoanAmountForMember());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		account1 = TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
		account2 = TestObjectFactory.getObject(AccountBO.class,
				account2.getAccountId());
	}

	public void testGetTotalSavingsBalance() throws Exception {
		createInitialObjects();
		SavingsBO savings1 = getSavingsAccount(group, "fsaf6", "ads6");
		TestSavingsBO.setBalance(savings1,new Money("1000"));
		
		savings1.update();
		SavingsBO savings2 = getSavingsAccount(client, "fsaf5", "ads5");
		TestSavingsBO.setBalance(savings2,new Money("2000"));
		savings1.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings1 = TestObjectFactory.getObject(SavingsBO.class,
				savings1.getAccountId());
		savings2 = TestObjectFactory.getObject(SavingsBO.class,
				savings2.getAccountId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		assertEquals(new Money("1000.0"), savings1.getSavingsBalance());
		assertEquals(new Money("2000.0"), savings2.getSavingsBalance());
		assertEquals(new Money("2000.0"), 
				client.getSavingsBalance());
		assertEquals(new Money("3000.0"), 
				group.getPerformanceHistory().getTotalSavingsAmount());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		savings1 = TestObjectFactory.getObject(SavingsBO.class,
				savings1.getAccountId());
		savings2 = TestObjectFactory.getObject(SavingsBO.class,
				savings2.getAccountId());
		TestObjectFactory.cleanUp(savings1);
		TestObjectFactory.cleanUp(savings2);
	}

	public void testGetActiveOnHoldChildrenOfGroup() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		client1 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_HOLD, group);
		client2 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_CANCELLED, group);
		assertEquals(Integer.valueOf("2"), 
				group.getPerformanceHistory().getActiveClientCount());
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

	public void testUpdateBranchFailure_OfficeInactive() throws Exception {
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
		officeBO = createOffice();
		officeBO.update(officeBO.getOfficeName(), officeBO.getShortName(), OfficeStatus.INACTIVE, officeBO.getOfficeLevel(), officeBO.getParentOffice(), null, null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		try {
			group.transferToBranch(officeBO);
			assertTrue(false);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_TRANSFER_IN_INACTIVE_OFFICE, ce
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
		client2.changeStatus(CustomerStatus.CLIENT_CLOSED, 
				CustomerStatusFlag.CLIENT_CLOSED_TRANSFERRED, 
				"comment");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group.setUserContext(TestUtils.makeUser());
		assertNull(client.getActiveCustomerMovement());

		group.transferToBranch(officeBO);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class,
				client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class,
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

	public void testUpdateCenterFailure_TransferInInactiveCenter() throws Exception {
		createInitialObjects();
		center1 = createCenter("newCenter");
		center1.changeStatus(CustomerStatus.CENTER_INACTIVE, null, "changeStatus");
		HibernateUtil.commitTransaction();
		try {
			group.transferToCenter(center1);
			fail();
		} catch (CustomerException e) {
			assertEquals(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE, e
					.getKey());
		}
	}
	
	public void testUpdateCenterFailure_GroupHasActiveAccount()
			throws Exception {
		createInitialObjects();
		account1 = getSavingsAccount(group, "Savings Prod", "SAVP");
		center1 = createCenter("newCenter");
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
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
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		account1 = TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
	}

	public void testUpdateCenterFailure_GroupChildrenHasActiveAccount()
			throws Exception {
		createInitialObjects();
		account1 = getSavingsAccount(client, "Savings Prod", "SAVP");
		center1 = createCenter("newCenter");
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
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
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		account1 = TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
	}

	public void testUpdateCenterFailure_MeetingFrequencyMismatch()throws Exception {
		createInitialObjects();
		center1 = createCenter("newCenter", createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, Short.valueOf("1"), new Date()));
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		try {
			group.transferToCenter(center1);
			assertTrue(false);
		} catch (CustomerException ce) {
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_MEETING_FREQUENCY_MISMATCH,
					ce.getKey());
		}
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
	}
	
   public void testUpdateCenterFailure_MeetingFrequencyMonthly()throws Exception {
		center = createCenter("Centerold", createMonthlyMeetingOnDate(Short.valueOf("5"), Short.valueOf("1"), new Date()));
		group = createGroup("groupold", center);
		client = createClient(group, CustomerStatus.CLIENT_ACTIVE);
		center1 = createCenter("newCenter", createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, Short.valueOf("1"), new Date()));
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		group.setUserContext(TestObjectFactory.getContext());
		group.transferToCenter(center1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		
		assertNotNull(group.getCustomerMeeting().getUpdatedMeeting());
		assertNotNull(client.getCustomerMeeting().getUpdatedMeeting());
		assertEquals(WeekDay.MONDAY,group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY,client.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
	
		group.changeUpdatedMeeting();
		client.changeUpdatedMeeting();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		
		assertNull(group.getCustomerMeeting().getUpdatedMeeting());
		assertNull(client.getCustomerMeeting().getUpdatedMeeting());
		
		assertEquals(WeekDay.MONDAY,group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY,client.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
	}
	
	public void testSuccessfulTransferToCenterInSameBranch() throws Exception {
		createObjectsForTranferToCenterInSameBranch();
		String newCenterSearchId = center1.getSearchId();
		HibernateUtil.closeSession();

		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group.setUserContext(center.getUserContext());
		group.transferToCenter(center1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		center1 = TestObjectFactory.getObject(CenterBO.class,
				center1.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class, group1
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class,
				client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class,
				client2.getCustomerId());
		
		assertNotNull(group.getCustomerMeeting().getUpdatedMeeting());
		assertNotNull(client.getCustomerMeeting().getUpdatedMeeting());
		assertNotNull(client1.getCustomerMeeting().getUpdatedMeeting());
		assertNotNull(client2.getCustomerMeeting().getUpdatedMeeting());

		assertEquals(WeekDay.THURSDAY,group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY,client.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY,client1.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY,client2.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());		
		
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

		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group.setUserContext(center.getUserContext());
		group.transferToCenter(center1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		center1 = TestObjectFactory.getObject(CenterBO.class,
				center1.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class, group1
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class,
				client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class,
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

	public void testSuccessfulTransferToCenterInDifferentBranch_SecondTransfer() throws Exception {
		createObjectsForTranferToCenterInDifferentBranch();
		HibernateUtil.closeSession();
		
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group.setUserContext(TestObjectFactory.getContext());
		group.transferToCenter(center1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		
		group.setUserContext(TestObjectFactory.getContext());
		group.transferToCenter(center);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		center1 = TestObjectFactory.getObject(CenterBO.class,
				center1.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class, group1
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class,
				client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class,
				client2.getCustomerId());
		officeBO = new OfficePersistence().getOffice(officeBO.getOfficeId());
		
		assertEquals(center.getCustomerId(), group.getParentCustomer()
				.getCustomerId());
		assertEquals(1, center.getMaxChildCount().intValue());
		assertEquals(1, center1.getMaxChildCount().intValue());
		assertEquals(3, group.getMaxChildCount().intValue());	
	}
	
	public void testUpdateMeeting_SavedToUpdateLater()throws Exception{
		String oldMeetingPlace = "Delhi";
		MeetingBO weeklyMeeting = new MeetingBO(WeekDay.FRIDAY, Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMER_MEETING, oldMeetingPlace);
		group = TestObjectFactory.createGroupUnderBranch("group1", CustomerStatus.GROUP_ACTIVE,
				officeId, weeklyMeeting, personnel);
		
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_ACTIVE);
		
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		MeetingBO groupMeeting = group.getCustomerMeeting().getMeeting();
		
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, groupMeeting.getMeetingDetails().getRecurAfter(), groupMeeting.getStartDate(), MeetingType.CUSTOMER_MEETING, meetingPlace);
		
		group.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());
		
		assertEquals(WeekDay.FRIDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.FRIDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.FRIDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		
		assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		
		assertEquals(oldMeetingPlace, group.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(oldMeetingPlace, client1.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(oldMeetingPlace, client2.getCustomerMeeting().getMeeting().getMeetingPlace());
		
		assertEquals(meetingPlace, group.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
		
		assertEquals(YesNoFlag.YES.getValue(), group.getCustomerMeeting().getUpdatedFlag());
		assertEquals(YesNoFlag.YES.getValue(), client1.getCustomerMeeting().getUpdatedFlag());
		assertEquals(YesNoFlag.YES.getValue(), client2.getCustomerMeeting().getUpdatedFlag());
		
		Integer groupUpdateMeetingId = group.getCustomerMeeting().getUpdatedMeeting().getMeetingId();
		assertEquals(groupUpdateMeetingId, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingId());
		assertEquals(groupUpdateMeetingId, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingId());
	}
	
	public void testUpdateMeeting()throws Exception{
		group = createGroupUnderBranch(CustomerStatus.GROUP_PENDING);
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_PENDING);
		
		MeetingBO groupMeeting = group.getCustomerMeeting().getMeeting();
		String oldMeetingPlace = "Delhi";
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, groupMeeting.getMeetingDetails().getRecurAfter(), groupMeeting.getStartDate(), MeetingType.CUSTOMER_MEETING, meetingPlace);
		HibernateUtil.closeSession();
		
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());
		
		assertEquals(WeekDay.MONDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY, client1.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY, client2.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		
		assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		
		assertEquals(oldMeetingPlace, group.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(oldMeetingPlace, client1.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(oldMeetingPlace, client2.getCustomerMeeting().getMeeting().getMeetingPlace());
		
		assertEquals(meetingPlace, group.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client1.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client2.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
	}
	
	public void testCreateMeeting()throws Exception{
		group = createGroupUnderBranchWithoutMeeting("MyGroup");
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_PENDING);
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group.setUserContext(TestObjectFactory.getContext());
		String meetingPlace = "newPlace";
		Short recurAfter = Short.valueOf("4");
		MeetingBO newMeeting = new MeetingBO(WeekDay.FRIDAY, recurAfter, new Date(), MeetingType.CUSTOMER_MEETING, meetingPlace);
		group.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client1 = TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());
		
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
	
	public void testFailureCreate_Group_UnderCenter() throws Exception {
		createCenter();
		String name = "GroupTest";
		Date trainedDate = getDate("11/12/2005");
		String externalId = "1234";
		HibernateUtil.closeSession();
		
		try {
			group = new GroupBO(TestUtils.makeUser(), name,
					CustomerStatus.GROUP_ACTIVE, externalId, true, trainedDate,
					getAddress(), null, null, personnel, center);
			TestObjectFactory.simulateInvalidConnection();
			group.save();
			fail();
		} catch (CustomerException ce) {
			assertEquals("Customer.CreateFailed", ce.getKey());
		}finally {
			group=null;
			HibernateUtil.closeSession();
		}
	}

		

	private GroupBO createGroupUnderBranchWithoutMeeting(String name) {
		return TestObjectFactory.createGroupUnderBranch(name, CustomerStatus.GROUP_PENDING,
				office, null, personnel);
	}
	
	private void createCenter() {
		meeting = getMeeting();
		center = TestObjectFactory.createCenter("Center",
				meeting);
	}
	
	private CenterBO createCenter(String name) throws Exception{
		return createCenter(name, officeId, WeekDay.MONDAY);
	}

	private CenterBO createCenter(String name, Short officeId, WeekDay weekDay) throws Exception{
		meeting = new MeetingBO(weekDay, Short.valueOf("1"), new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
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
	
	private GroupBO createGroupUnderBranch(String name,
			CustomerStatus customerStatus,List<CustomFieldView> customFieldView) {
		meeting = getMeeting();
		return TestObjectFactory.createGroupUnderBranch(name, customerStatus,
				office, meeting, personnel,customFieldView);
	}
	
	private GroupBO createGroupUnderBranch(CustomerStatus groupStatus)throws Exception {
		return createGroupUnderBranch(groupStatus, officeId);
	}

	private GroupBO createGroupUnderBranch(CustomerStatus groupStatus,
			Short officeId) throws Exception{
		meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		return TestObjectFactory.createGroupUnderBranchWithMakeUser("group1", groupStatus,
				officeId, meeting, personnel);
	}

	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
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
				CustomFieldType.ALPHA_NUMERIC));
		fields.add(new CustomFieldView(Short.valueOf("3"), "value2",
				CustomFieldType.NUMERIC));
		return fields;
	}
	
	private List<CustomFieldView> getNewCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(Short.valueOf("4"), "value3",
				CustomFieldType.ALPHA_NUMERIC));
		fields.add(new CustomFieldView(Short.valueOf("3"), "value4",
				CustomFieldType.NUMERIC));
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
		fees.add(new FeeView(TestObjectFactory.getContext(),fee1));
		fees.add(new FeeView(TestObjectFactory.getContext(),fee2));
		HibernateUtil.commitTransaction();
		return fees;
	}

	private void createObjectsForTranferToCenterInSameBranch() throws Exception {
		createInitialObjects();
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_CANCELLED);
		center1 = createCenter("toTransfer", officeId, WeekDay.THURSDAY);
		group1 = createGroup("newGroup", center1);
	}

	private void createObjectsForTranferToCenterInDifferentBranch()
			throws Exception {
		createInitialObjects();
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_CANCELLED);
		officeBO = createOffice();
		center1 = createCenter("toTransfer", officeBO.getOfficeId(), WeekDay.FRIDAY);
		group1 = createGroup("newGroup", center1);
	}

	private ClientBO createClient(GroupBO group, CustomerStatus clientStatus) {
		return TestObjectFactory.createClient("client1", clientStatus,
				group, new Date());
	}

	private GroupBO createGroup(String name, CenterBO center) {
		return TestObjectFactory.createGroupUnderCenter(name, CustomerStatus.GROUP_ACTIVE, center);
	}

	private OfficeBO createOffice() throws Exception {
		return TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE,
				TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE),
				"customer_office", "cust");
	}

	private void createInitialObjects() throws Exception{
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
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loandsdasd", "fsad", startDate, meeting);
		account1 = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
		loanOffering = TestObjectFactory.createLoanOffering("Loandfas", "dsvd",
				ApplicableTo.CLIENTS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, 
				InterestType.FLAT, true, true,
				meeting);
		account2 = TestObjectFactory.createLoanAccount("42427777341", client,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
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
			TestLoanBO.setActionDate(accountActionDateEntity,new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
			break;
		}
	}
	
	private CenterBO createCenter(String name, MeetingBO meeting){
		return TestObjectFactory.createCenter(name, meeting, officeId,
				personnel);
	}
	
	private MeetingBO createMonthlyMeetingOnDate(Short dayNumber, Short recurAfer, Date startDate) throws MeetingException{
		return new MeetingBO(dayNumber, recurAfer, startDate, MeetingType.CUSTOMER_MEETING,"MeetingPlace");	
	}
	
	private MeetingBO createMonthlyMeetingOnWeekDay(WeekDay weekDay, RankType rank, Short recurAfer, Date startDate) throws MeetingException{
		return new MeetingBO(weekDay, rank, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
	}
}
