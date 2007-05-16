package org.mifos.application.customer.center.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.center.util.helpers.CenterSearchResults;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CenterBOTest extends MifosTestCase {

	private CenterBO center;

	private GroupBO group;

	private ClientBO client;

	private Short officeId = 1;

	private Short personnel = 3;

	private MeetingBO meeting;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}

	public static void setPerformanceHistoryDetails(
			CenterPerformanceHistory centerPerformanceHistory,
			Integer numberOfGroups, Integer numberOfClients,
			Money totalOutstandingPortfolio, Money totalSavings,
			Money portfolioAtRisk) {
		centerPerformanceHistory.setPerformanceHistoryDetails(numberOfGroups,
				numberOfClients, totalOutstandingPortfolio, totalSavings,
				portfolioAtRisk);
	}

	public void testSuccessfulUpdateForLogging() throws Exception {
		createCustomers();
		Date mfiJoiningDate = getDate("11/12/2005");
		String externalId = "1234";
		Address address = new Address();
		address.setCity("Bangalore");
		address.setLine1("Aditi");

		center.setUserContext(TestUtils.makeUserWithLocales());
		HibernateUtil.getInterceptor().createInitialValueMap(center);
		center.update(TestObjectFactory.getUserContext(), personnel,
				externalId, mfiJoiningDate, address, null, null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());

		List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(
				EntityType.CENTER, center.getCustomerId());
		assertEquals(1, auditLogList.size());
		AuditLog auditLog = auditLogList.get(0);

		assertEquals(EntityType.CENTER.getValue(), auditLog.getEntityType());
		Set<AuditLogRecord> records = auditLog.getAuditLogRecords();
		assertEquals(5, records.size());
		for (AuditLogRecord auditLogRecord : records) {
			String fieldName = auditLogRecord.getFieldName();
			if (fieldName.equalsIgnoreCase("Address1")) {
				assertEquals("-", auditLogRecord.getOldValue());
				assertEquals("Aditi", auditLogRecord.getNewValue());
			} else if (fieldName.equalsIgnoreCase(
					"City/District")) {
				assertEquals("-", auditLogRecord.getOldValue());
				assertEquals("Bangalore", auditLogRecord.getNewValue());
			} else if (fieldName.equalsIgnoreCase(
					"Loan Officer Assigned")) {
				assertEquals("mifos", auditLogRecord.getOldValue());
				assertEquals("loan officer", auditLogRecord.getNewValue());
			} else if (fieldName.equalsIgnoreCase(
					"MFI Joining Date")) {
				assertEquals("-", auditLogRecord.getOldValue());
				assertEquals("11/12/2005", auditLogRecord.getNewValue());
			} else if (fieldName.equalsIgnoreCase(
					"External Id")) {
				assertEquals("-", auditLogRecord.getOldValue());
				assertEquals("1234", auditLogRecord.getNewValue());
			}
			else {
				fail("unrecognized field name " + fieldName);
			}
		}

		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		TestObjectFactory.cleanUpChangeLog();
	}

	public void testCreateWithoutName() throws Exception {
		try {
			meeting = getMeeting();
			center = new CenterBO(TestUtils.makeUser(), "", null,
					null, null, null, null, officeId, meeting, personnel);
			fail();
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(CustomerConstants.INVALID_NAME, ce.getKey());
		}
		TestObjectFactory.removeObject(meeting);
	}

	public void testCreateWithoutLO() throws Exception {
		try {
			meeting = getMeeting();
			center = new CenterBO(TestUtils.makeUser(), "Center",
					null, null, null, null, null, officeId, meeting, null);
			fail();
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(CustomerConstants.INVALID_LOAN_OFFICER, ce.getKey());
		}
		TestObjectFactory.removeObject(meeting);
	}

	public void testCreateWithoutMeeting() throws Exception {
		try {
			center = new CenterBO(TestUtils.makeUser(), "Center",
					null, null, null, null, null, officeId, meeting, personnel);
			fail();
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(CustomerConstants.INVALID_MEETING, ce.getKey());
		}
	}

	public void testCreateWithoutOffice() throws Exception {
		try {
			meeting = getMeeting();
			center = new CenterBO(TestUtils.makeUser(), "Center",
					null, null, null, null, null, null, meeting, personnel);
			fail();
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(CustomerConstants.INVALID_OFFICE, ce.getKey());
		}
		TestObjectFactory.removeObject(meeting);
	}

	public void testSuccessfulCreateWithoutFeeAndCustomField() throws Exception {
		String name = "Center1";
		meeting = getMeeting();
		center = new CenterBO(TestUtils.makeUser(), name, null,
				null, null, null, null, officeId, meeting, personnel);
		center.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		assertEquals(name, center.getDisplayName());
		assertEquals(officeId, center.getOffice().getOfficeId());
	}

	public void testSuccessfulCreateWithoutFee() throws Exception {
		String name = "Center1";
		meeting = getMeeting();
		center = new CenterBO(TestUtils.makeUser(), name, null,
				getCustomFields(), null, null, null, officeId, meeting,
				personnel);
		center.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		assertEquals(name, center.getDisplayName());
		assertEquals(officeId, center.getOffice().getOfficeId());
		assertEquals(2, center.getCustomFields().size());
	}

	public void testFailureCreate_DuplicateName() throws Exception {
		String name = "Center1";
		center = TestObjectFactory.createCenter(name,
				getMeeting());
		HibernateUtil.closeSession();

		String externalId = "12345";
		Date mfiJoiningDate = getDate("11/12/2005");
		meeting = getMeeting();
		List<FeeView> fees = getFees();
		try {
			center = new CenterBO(TestUtils.makeUser(), name,
					null, null, fees, externalId, mfiJoiningDate, officeId,
					meeting, personnel);
			fail();
		} catch (CustomerException e) {
			assertEquals(
				CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, e.getKey());
		}
		removeFees(fees);
	}

	public void testFailureDuplicateName() throws Exception {
		String name = "Center1";
		center = TestObjectFactory.createCenter(name,
				getMeeting());
		HibernateUtil.closeSession();

		String externalId = "12345";
		Date mfiJoiningDate = getDate("11/12/2005");
		meeting = getMeeting();
		UserContext userContext = TestUtils.makeUser();
		TestObjectFactory.simulateInvalidConnection();
		try {
			center = new CenterBO(userContext, name, null, null, null,
					externalId, mfiJoiningDate, officeId, meeting, personnel);
			fail();
		} catch (CustomerException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testSuccessfulCreate() throws Exception {
		String name = "Center1";
		String externalId = "12345";
		Date mfiJoiningDate = getDate("11/12/2005");
		meeting = getMeeting();
		List<FeeView> fees = getFees();
		center = new CenterBO(TestUtils.makeUser(), name, null,
				getCustomFields(), fees, externalId, mfiJoiningDate, officeId,
				meeting, personnel);
		center.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		assertEquals(name, center.getDisplayName());
		assertEquals(externalId, center.getExternalId());
		assertEquals(mfiJoiningDate, DateUtils.getDateWithoutTimeStamp(center
				.getMfiJoiningDate().getTime()));
		assertEquals(officeId, center.getOffice().getOfficeId());
		assertEquals(2, center.getCustomFields().size());
		assertEquals(AccountState.CUSTOMERACCOUNT_ACTIVE.getValue(), center
				.getCustomerAccount().getAccountState().getId());
		// check if values in account fees are entered.
		assertNotNull(center.getCustomerAccount().getAccountFees(
				fees.get(0).getFeeIdValue()));
		assertNotNull(center.getCustomerAccount().getAccountFees(
				fees.get(1).getFeeIdValue()));

	}

	public void testUpdateFailure() throws Exception {
		createCustomers();
		try {
			center.update(TestUtils.makeUser(), null, "1234",
					null, null, null, null);
			fail();
		} catch (CustomerException ce) {
			assertEquals(CustomerConstants.INVALID_LOAN_OFFICER, ce.getKey());
		}
	}

	public void testSuccessfulUpdate() throws Exception {
		createCustomers();
		Date mfiJoiningDate = getDate("11/12/2005");
		String city = "Bangalore";
		String addressLine1 = "Aditi";
		String externalId = "1234";
		Address address = new Address();
		address.setCity(city);
		address.setLine1(addressLine1);

		center.update(TestUtils.makeUser(), personnel,
				externalId, mfiJoiningDate, address, null, null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());

		assertEquals(city, center.getCustomerAddressDetail().getAddress()
				.getCity());
		assertEquals(addressLine1, center.getCustomerAddressDetail()
				.getAddress().getLine1());
		assertEquals(externalId, center.getExternalId());
		assertEquals(mfiJoiningDate, DateUtils.getDateWithoutTimeStamp(center
				.getMfiJoiningDate().getTime()));
		assertEquals(personnel, center.getPersonnel().getPersonnelId());
	}

	public void testSuccessfulUpdateWithoutLO_in_InActiveState()
			throws Exception {
		createCustomers();
		client.changeStatus(CustomerStatus.CLIENT_CANCELLED, 
				CustomerStatusFlag.CLIENT_CANCEL_WITHDRAW, 
				"client cancelled");
		client.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group.changeStatus(CustomerStatus.GROUP_CANCELLED, 
				CustomerStatusFlag.GROUP_CANCEL_WITHDRAW, 
				"group cancelled");
		group.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		center.changeStatus(CustomerStatus.CENTER_INACTIVE, null,
				"Center_Inactive");
		center.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());

		center.update(TestUtils.makeUser(), null, center
				.getExternalId(), center.getMfiJoiningDate(), center
				.getAddress(), null, null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());

		assertNull(center.getPersonnel());
		assertNull(group.getPersonnel());
		assertNull(client.getPersonnel());
	}

	public void testUpdateLOsForAllChildren() throws Exception {
		createCustomers();
		assertEquals(center.getPersonnel().getPersonnelId(), group
				.getPersonnel().getPersonnelId());
		assertEquals(center.getPersonnel().getPersonnelId(), client
				.getPersonnel().getPersonnelId());
		PersonnelBO newLO = TestObjectFactory.getPersonnel(Short.valueOf("2"));
		HibernateUtil.closeSession();
		center.update(TestUtils.makeUser(), newLO
				.getPersonnelId(), center.getExternalId(), center
				.getMfiJoiningDate(), center.getAddress(), null, null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals(newLO.getPersonnelId(), center.getPersonnel()
				.getPersonnelId());
		assertEquals(newLO.getPersonnelId(), group.getPersonnel()
				.getPersonnelId());
		assertEquals(newLO.getPersonnelId(), client.getPersonnel()
				.getPersonnelId());
	}

	public void testUpdateMeeting_SaveToUpdateLater() throws Exception {
		createCustomers();
		String oldMeetingPlace = "Delhi";
		MeetingBO centerMeeting = center.getCustomerMeeting().getMeeting();
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.MONDAY, centerMeeting
				.getMeetingDetails().getRecurAfter(), centerMeeting
				.getStartDate(), MeetingType.CUSTOMER_MEETING, meetingPlace);
		HibernateUtil.closeSession();

		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		center.setUserContext(TestObjectFactory.getContext());
		center.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());

		assertEquals(WeekDay.THURSDAY, center.getCustomerMeeting().getMeeting()
				.getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getMeeting()
				.getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client.getCustomerMeeting().getMeeting()
				.getMeetingDetails().getWeekDay());

		assertEquals(WeekDay.MONDAY, center.getCustomerMeeting()
				.getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY, group.getCustomerMeeting()
				.getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY, client.getCustomerMeeting()
				.getUpdatedMeeting().getMeetingDetails().getWeekDay());

		assertEquals(oldMeetingPlace, center.getCustomerMeeting().getMeeting()
				.getMeetingPlace());
		assertEquals(oldMeetingPlace, group.getCustomerMeeting().getMeeting()
				.getMeetingPlace());
		assertEquals(oldMeetingPlace, client.getCustomerMeeting().getMeeting()
				.getMeetingPlace());

		assertEquals(meetingPlace, center.getCustomerMeeting()
				.getUpdatedMeeting().getMeetingPlace());
		assertEquals(meetingPlace, group.getCustomerMeeting()
				.getUpdatedMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client.getCustomerMeeting()
				.getUpdatedMeeting().getMeetingPlace());
	}

	public void testUpdateMeeting_updateWithSaveMeeting() throws Exception {
		testUpdateMeeting_SaveToUpdateLater();
		Integer updatedMeeting = center.getCustomerMeeting().getUpdatedMeeting()
				.getMeetingId();
		center.changeUpdatedMeeting();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());

		assertEquals(WeekDay.MONDAY, center.getCustomerMeeting().getMeeting()
				.getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY, group.getCustomerMeeting().getMeeting()
				.getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.MONDAY, client.getCustomerMeeting().getMeeting()
				.getMeetingDetails().getWeekDay());

		assertNull(center.getCustomerMeeting().getUpdatedMeeting());
		assertNull(group.getCustomerMeeting().getUpdatedMeeting());
		assertNull(client.getCustomerMeeting().getUpdatedMeeting());

		MeetingBO meeting = new MeetingPersistence().getMeeting(updatedMeeting);
		assertNull(meeting);
	}

	public void testCenterSearchResultsView(){
		
		CenterSearchResults searchResults = new CenterSearchResults();
		searchResults.setCenterName("Center");
		searchResults.setCenterSystemId("1234");
		searchResults.setParentOfficeId(Short.valueOf("1"));
		searchResults.setParentOfficeName("BO");
		assertEquals("Center",searchResults.getCenterName());
		assertEquals("1234",searchResults.getCenterSystemId());
		assertEquals(Short.valueOf("1").shortValue(),searchResults.getParentOfficeId());
		assertEquals("BO",searchResults.getParentOfficeName());
		
	}
	
	public void testSearchIdOnlyUniquePerOffice() throws Exception {
		Date startDate = new Date();

		// In real life, would be another branch rather than an area
		Short branch1 = TestObjectFactory.SAMPLE_AREA_OFFICE;
		
		MeetingBO meeting = new MeetingBO(WeekDay.THURSDAY, (short)1,
			startDate, MeetingType.CUSTOMER_MEETING, "Delhi");

		center = new CenterBO(TestUtils.makeUser(), 
			"center1", null, null, null, null, startDate, 
			branch1, meeting,
			PersonnelConstants.SYSTEM_USER);
		HibernateUtil.getSessionTL().save(center);

		CenterBO center2 = new CenterBO(TestUtils.makeUser(), 
				"center2", null, null, null, null, startDate, 
				TestObjectFactory.SAMPLE_BRANCH_OFFICE, meeting,
				PersonnelConstants.SYSTEM_USER);
		
		CenterBO sameBranch = new CenterBO(TestUtils.makeUser(), 
				"sameBranch", null, null, null, null, startDate, 
				branch1, meeting,
				PersonnelConstants.SYSTEM_USER);
			HibernateUtil.getSessionTL().save(center);
		
		assertEquals("1.1", center.getSearchId());
		assertEquals("1.1", center2.getSearchId());
		assertEquals("1.2", sameBranch.getSearchId());
	}

	private void createCustomers() throws Exception {
		meeting = new MeetingBO(WeekDay.THURSDAY, Short.valueOf("1"),
				new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		center = TestObjectFactory.createCenter("Center",
				meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		return meeting;
	}

	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(Short.valueOf("5"), "value1",
				CustomFieldType.ALPHA_NUMERIC));
		fields.add(new CustomFieldView(Short.valueOf("6"), "value2",
				CustomFieldType.ALPHA_NUMERIC));
		return fields;
	}

	private List<FeeView> getFees() {
		List<FeeView> fees = new ArrayList<FeeView>();
		AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory
				.createPeriodicAmountFee("PeriodicAmountFee",
						FeeCategory.CENTER, "200", RecurrenceType.WEEKLY, Short
								.valueOf("2"));
		AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory
				.createOneTimeAmountFee("OneTimeAmountFee",
						FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
		fees.add(new FeeView(TestObjectFactory.getContext(), fee1));
		fees.add(new FeeView(TestObjectFactory.getContext(), fee2));
		HibernateUtil.commitTransaction();
		return fees;
	}

	private void removeFees(List<FeeView> feesToRemove) {
		for (FeeView fee : feesToRemove) {
			TestObjectFactory.cleanUp(new FeePersistence().getFee(fee
					.getFeeIdValue()));
		}
	}

}
