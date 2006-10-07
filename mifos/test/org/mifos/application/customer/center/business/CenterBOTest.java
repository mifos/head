package org.mifos.application.customer.center.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
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
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
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
	
	public void testSuccessfulUpdateForLogging() throws Exception {
		createCustomers();
		Date mfiJoiningDate = getDate("11/12/2005");
		String city ="Bangalore";
		String addressLine1="Aditi";
		String externalId = "1234";
		Address address = new Address();
		address.setCity(city);
		address.setLine1(addressLine1);
		center.setUserContext(TestObjectFactory.getUserContext());
		HibernateUtil.getInterceptor().createInitialValueMap(center);
		center.update(TestObjectFactory.getUserContext(), personnel, externalId, mfiJoiningDate, address, null, null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		
		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(EntityType.CENTER.getValue(),center.getCustomerId());
		assertEquals(EntityType.CENTER.getValue(),auditLogList.get(0).getEntityType());
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Address1")){
				auditLogRecord.getOldValue().equalsIgnoreCase("-");
				auditLogRecord.getNewValue().equalsIgnoreCase("Aditi");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("City/District")){
				auditLogRecord.getOldValue().equalsIgnoreCase("-");
				auditLogRecord.getNewValue().equalsIgnoreCase("Bangalore");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Loan Officer Assigned")){
				auditLogRecord.getOldValue().equalsIgnoreCase("mifos");
				auditLogRecord.getNewValue().equalsIgnoreCase("loan officer");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("MFI Joining Date")){
				auditLogRecord.getOldValue().equalsIgnoreCase("-");
				auditLogRecord.getNewValue().equalsIgnoreCase("11/12/2005");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("External Id")){
				auditLogRecord.getOldValue().equalsIgnoreCase("-");
				auditLogRecord.getNewValue().equalsIgnoreCase("1234");
			}
		}
		
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		TestObjectFactory.cleanUpChangeLog();
	
	}

	public void testCreateWithoutName() throws Exception {
		try {
			meeting = getMeeting();
			center = new CenterBO(TestObjectFactory.getUserContext(), "", null,
					null, null, null,null,  officeId, meeting, personnel);
			assertFalse("Center Created", true);
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_NAME);
		}
		TestObjectFactory.removeObject(meeting);
	}


	public void testCreateWithoutLO() throws Exception {
		try {
			meeting = getMeeting();
			center = new CenterBO(TestObjectFactory.getUserContext(), "Center",
					null, null, null,null, null, officeId, meeting, null);
			assertFalse("Center Created", true);
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_LOAN_OFFICER);
		}
		TestObjectFactory.removeObject(meeting);
	}


	public void testCreateWithoutMeeting() throws Exception {
		try {
			center = new CenterBO(TestObjectFactory.getUserContext(), "Center",
					null, null, null,null, null, officeId, meeting, personnel);
			assertFalse("Center Created", true);
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_MEETING);
		}
	}

	public void testCreateWithoutOffice() throws Exception {
		try {
			meeting = getMeeting();
			center = new CenterBO(TestObjectFactory.getUserContext(), "Center",
					null, null, null, null, null, null, meeting, personnel);
			assertFalse("Center Created", true);
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_OFFICE);
		}
		TestObjectFactory.removeObject(meeting);
	}

	public void testSuccessfulCreateWithoutFeeAndCustomField() throws Exception {
		String name = "Center1";
		meeting = getMeeting();
		center = new CenterBO(TestObjectFactory.getUserContext(), name, null,
				null, null, null, null,  officeId, meeting, personnel);
		center.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		assertEquals(name, center.getDisplayName());
		assertEquals(officeId, center.getOffice().getOfficeId());
	}

	public void testSuccessfulCreateWithoutFee() throws Exception {
		String name = "Center1";
		meeting = getMeeting();
		center = new CenterBO(TestObjectFactory.getUserContext(), name, null,
				getCustomFields(), null,  null, null, officeId, meeting,
				personnel);
		center.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		assertEquals(name, center.getDisplayName());
		assertEquals(officeId, center.getOffice().getOfficeId());
		assertEquals(2, center.getCustomFields().size());
	}

	public void testFailureCreate_DuplicateName() throws Exception {
		String name = "Center1";
		center = TestObjectFactory.createCenter(name, CustomerStatus.CENTER_ACTIVE.getValue(), "", getMeeting(), new Date());
		HibernateUtil.closeSession();
		
		String externalId = "12345";
		Date mfiJoiningDate = getDate("11/12/2005");
		meeting = getMeeting();
		List<FeeView> fees = getFees();
		try{
			center = new CenterBO(TestObjectFactory.getUserContext(), name, null,
					null, fees, externalId,mfiJoiningDate ,  officeId, meeting,
					personnel);
		}catch(CustomerException e){
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, e.getKey());
		}			
		removeFees(fees);
	}
	
	public void testSuccessfulCreate() throws Exception {
		String name = "Center1";
		String externalId = "12345";
		Date mfiJoiningDate = getDate("11/12/2005");
		meeting = getMeeting();
		List<FeeView> fees = getFees();
		center = new CenterBO(TestObjectFactory.getUserContext(), name, null,
				getCustomFields(), fees, externalId,mfiJoiningDate ,  officeId, meeting,
				personnel);
		center.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		assertEquals(name, center.getDisplayName());
		assertEquals(externalId, center.getExternalId());
		assertEquals(mfiJoiningDate, DateUtils.getDateWithoutTimeStamp(center.getMfiJoiningDate().getTime()));
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
		try{
			center.update(TestObjectFactory.getUserContext(), null, "1234", null, null, null, null);
		}catch(CustomerException ce){
			assertEquals(ce.getKey(), CustomerConstants.INVALID_LOAN_OFFICER);
		}
	}
	
	public void testSuccessfulUpdate() throws Exception {
		createCustomers();		
		Date mfiJoiningDate = getDate("11/12/2005");
		String city ="Bangalore";
		String addressLine1="Aditi";
		String externalId = "1234";
		Address address = new Address();
		address.setCity(city);
		address.setLine1(addressLine1);
		
		center.update(TestObjectFactory.getUserContext(), personnel, externalId, mfiJoiningDate, address, null, null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		
		assertEquals(city, center.getCustomerAddressDetail().getAddress().getCity());
		assertEquals(addressLine1, center.getCustomerAddressDetail().getAddress().getLine1());
		assertEquals(externalId, center.getExternalId());
		assertEquals(mfiJoiningDate, DateUtils.getDateWithoutTimeStamp(center.getMfiJoiningDate().getTime()));
		assertEquals(personnel, center.getPersonnel().getPersonnelId());
	}
	
	public void testSuccessfulUpdateWithoutLO_in_InActiveState() throws Exception {
		createCustomers();
		client.changeStatus(CustomerStatus.CLIENT_CANCELLED.getValue(),Short.valueOf("1"),"client cancelled");
		client.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group.changeStatus(CustomerStatus.GROUP_CANCELLED.getValue(),Short.valueOf("11"),"group cancelled");
		group.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		center.changeStatus(CustomerStatus.CENTER_INACTIVE.getValue(), null, "Center_Inactive");
		center.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		
		
		center.update(TestObjectFactory.getUserContext(), null, center.getExternalId(), center.getMfiJoiningDate(), center.getAddress(), null, null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		
		assertNull(center.getPersonnel());
		assertNull(group.getPersonnel());
		assertNull(client.getPersonnel());
	}
	
	public void testUpdateLOsForAllChildren() throws Exception{
		createCustomers();
		assertEquals(center.getPersonnel().getPersonnelId(), group.getPersonnel().getPersonnelId());
		assertEquals(center.getPersonnel().getPersonnelId(), client.getPersonnel().getPersonnelId());
		PersonnelBO newLO = TestObjectFactory.getPersonnel(Short.valueOf("2"));
		HibernateUtil.closeSession();
		center.update(TestObjectFactory.getUserContext(),newLO.getPersonnelId(),center.getExternalId(), center.getMfiJoiningDate(), center.getAddress(), null,null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(newLO.getPersonnelId(), center.getPersonnel().getPersonnelId());
		assertEquals(newLO.getPersonnelId(), group.getPersonnel().getPersonnelId());
		assertEquals(newLO.getPersonnelId(), client.getPersonnel().getPersonnelId());
	}
	
	public void testUpdateMeeting()throws Exception{
		createCustomers();
		MeetingBO centerMeeting = center.getCustomerMeeting().getMeeting();
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, centerMeeting.getMeetingDetails().getRecurAfter(), centerMeeting.getStartDate(), MeetingType.CUSTOMERMEETING, meetingPlace);
		center.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(WeekDay.THURSDAY, center.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(WeekDay.THURSDAY, client.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(meetingPlace, center.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(meetingPlace, group.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(meetingPlace, client.getCustomerMeeting().getMeeting().getMeetingPlace());
	}
	
	private void createCustomers() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", CustomerStatus.CENTER_ACTIVE.getValue(), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", CustomerStatus.GROUP_ACTIVE.getValue(), center.getSearchId() + ".1", center,
				new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE.getValue(), group.getSearchId() + ".1", group,
				new Date(System.currentTimeMillis()));
	}
	
	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return meeting;
	}

	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(
			Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC.getValue()));
		fields.add(new CustomFieldView(
			Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC.getValue()));
		return fields;
	}

	private List<FeeView> getFees() {
		List<FeeView> fees = new ArrayList<FeeView>();
		AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory
				.createPeriodicAmountFee("PeriodicAmountFee",
						FeeCategory.CENTER, "200", RecurrenceType.WEEKLY,
						Short.valueOf("2"));
		AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory
				.createOneTimeAmountFee("OneTimeAmountFee",
						FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
		fees.add(new FeeView(TestObjectFactory.getContext(),fee1));
		fees.add(new FeeView(TestObjectFactory.getContext(),fee2));
		HibernateUtil.commitTransaction();
		return fees;
	}
	
	private void removeFees(List<FeeView> feesToRemove){
		for(FeeView fee :feesToRemove){
			TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
		}
	}	
	
}
