package org.mifos.application.customer.group.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupBOTest extends MifosTestCase{

private CenterBO center;
	
	private GroupBO group;
	
	private GroupBO group1;
	
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
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}

	public void testCreateWithoutName() throws Exception {
		try {
			group = new GroupBO(TestObjectFactory.getUserContext(), "", CustomerStatus.GROUP_PARTIAL, null, false, null,
					null, null, null, personnel,  officeId, meeting, personnel);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_NAME, ce.getKey());
		}
	}
	
	public void testCreateWithoutStatus() throws Exception {
		try {
			group = new GroupBO(TestObjectFactory.getUserContext(), "GroupName", null, null, false, null,
					null, null, null, personnel,  officeId, meeting, personnel);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_STATUS, ce.getKey());
		}
	}	
	
	public void testCreateWithoutOffice_WithoutCenterHierarchy() throws Exception {
		try {
			group = new GroupBO(TestObjectFactory.getUserContext(), "GroupName", CustomerStatus.GROUP_PARTIAL, null, false, null,
					null, null, null, personnel, null, meeting, personnel);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_OFFICE, ce.getKey());
		}
	}	
	
	public void testCreateWithoutLO_InActiveState_WithoutCenterHierarchy() throws Exception {
		try {
			group = new GroupBO(TestObjectFactory.getUserContext(), "GroupName", CustomerStatus.GROUP_ACTIVE, null, false, null,
					null, null, null, personnel, officeId, meeting, null);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_LOAN_OFFICER, ce.getKey());
		}
	}	
	
	public void testCreateWithoutMeeting_InActiveState_WithoutCenterHierarchy() throws Exception {
		try {
			group = new GroupBO(TestObjectFactory.getUserContext(), "GroupName", CustomerStatus.GROUP_ACTIVE, null, false, null,
					null, null, null, personnel, officeId, null, personnel);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_MEETING, ce.getKey());
		}
	}	
	
	public void testCreateWithoutParent_WhenCenter_HierarchyExists() throws Exception {
		try {
			meeting = getMeeting();
			group = new GroupBO(TestObjectFactory.getUserContext(), "GroupName", CustomerStatus.GROUP_PARTIAL, null, false, null,
					null, null, null, personnel,  null);
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
			group = new GroupBO(TestObjectFactory.getUserContext(), "GroupName", CustomerStatus.GROUP_PARTIAL, null, false, null,
					null, null, null, null,  center);
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
			group = new GroupBO(TestObjectFactory.getUserContext(), "GroupName", CustomerStatus.GROUP_PARTIAL, null, true, null,
					null, null, null, personnel, center);
			assertFalse("Group Created", true);
		} catch (CustomerException ce) {
			assertNull(group);
			assertEquals(CustomerConstants.INVALID_TRAINED_OR_TRAINEDDATE, ce.getKey());
		}
		TestObjectFactory.removeObject(meeting);
	}	
	
	public void testFailureCreate_DuplicateName() throws Exception {
		String name = "GroupTest";
		createCenter();
		createGroup(name);
		HibernateUtil.closeSession();

		List<FeeView> fees = getFees();
		try{
			group1 = new GroupBO(TestObjectFactory.getUserContext(),name, CustomerStatus.GROUP_ACTIVE, null, false, null,
					null, null, fees, personnel, center);
			assertFalse(true);
		}catch(CustomerException e){
			assertTrue(true);
			assertNull(group1);
			assertEquals(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, e.getKey());
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
		
		group = new GroupBO(TestObjectFactory.getUserContext(),name, CustomerStatus.GROUP_ACTIVE, externalId, true, trainedDate,
				getAddress(), getCustomFields(), getFees(), personnel, center);
		group.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		
		assertEquals(name, group.getDisplayName());
		assertEquals(externalId,group.getExternalId());
		assertTrue(group.isTrained());
		assertEquals(trainedDate, DateUtils.getDateWithoutTimeStamp(group.getTrainedDate().getTime()));
		assertEquals(CustomerStatus.GROUP_ACTIVE, group.getStatus());
		Address address = group.getCustomerAddressDetail().getAddress();
		assertEquals("Aditi", address.getLine1());
		assertEquals("Bangalore", address.getCity());
		assertEquals(getCustomFields().size(), group.getCustomFields().size());		
		assertEquals(1, center.getMaxChildCount().intValue());
		assertEquals(center.getPersonnel().getPersonnelId(), group.getPersonnel().getPersonnelId());		
		assertEquals("1.1.1", group.getSearchId());
	}
	
	public void testSuccessfulCreate_Group_UnderBranch() throws Exception {	
		String name = "GroupTest";		
		String externalId = "1234";
		group = new GroupBO(TestObjectFactory.getUserContext(),name, CustomerStatus.GROUP_ACTIVE, externalId, false, null,
				getAddress(), getCustomFields(), getFees(), personnel, officeId, getMeeting(), personnel);
		group.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		
		assertEquals(name, group.getDisplayName());
		assertEquals(externalId,group.getExternalId());
		assertFalse(group.isTrained());
		assertEquals(CustomerStatus.GROUP_ACTIVE, group.getStatus());
		Address address = group.getCustomerAddressDetail().getAddress();
		assertEquals("Aditi", address.getLine1());
		assertEquals("Bangalore", address.getCity());
		assertEquals(getCustomFields().size(), group.getCustomFields().size());
		
		assertEquals(personnel, group.getCustomerFormedByPersonnel().getPersonnelId());
		assertEquals(personnel, group.getPersonnel().getPersonnelId());
		assertEquals(officeId, group.getOffice().getOfficeId());
		assertNotNull(group.getCustomerMeeting().getMeeting());
		assertEquals("1.1", group.getSearchId());
	}
	
	private void createCenter(){
		meeting = getMeeting();
		center = TestObjectFactory.createCenter("Center", CustomerStatus.CENTER_ACTIVE.getValue(), "1.1", meeting, new Date(System
			.currentTimeMillis()));
	}
	
	private void createGroup(String name){		
		group = TestObjectFactory.createGroupUnderCenter(name, CustomerStatus.GROUP_ACTIVE, center);
	}
	
	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}
	
	private void removeFees(List<FeeView> feesToRemove){
		for(FeeView fee :feesToRemove){
			TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
		}
	}
	
	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(
			Short.valueOf("4"), "value1", CustomFieldType.ALPHA_NUMERIC.getValue()));		
		return fields;
	}
	
	private Address getAddress(){
		Address address = new Address();
		address.setLine1("Aditi");
		address.setCity("Bangalore");
		return address;
	}
	
	private List<FeeView> getFees() {
		List<FeeView> fees = new ArrayList<FeeView>();
		AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory
				.createPeriodicAmountFee("PeriodicAmountFee",
						FeeCategory.GROUP, "200", MeetingFrequency.WEEKLY,
						Short.valueOf("2"));
		AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory
				.createOneTimeAmountFee("OneTimeAmountFee",
						FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
		fees.add(new FeeView(fee1));
		fees.add(new FeeView(fee2));
		HibernateUtil.commitTransaction();
		return fees;
	}
}
