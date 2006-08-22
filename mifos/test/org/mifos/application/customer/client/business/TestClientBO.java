package org.mifos.application.customer.client.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerMovementEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestClientBO extends MifosTestCase {

	private CustomerBO center;

	private CustomerBO group;

	private ClientBO client;
	
	private MeetingBO meeting;
	
	private Short officeId = 1;
	
	private Short personnel = 3;

	private OfficeBO office;
	private CustomerPersistence customerPersistence = new CustomerPersistence();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(office);
		super.tearDown();
	}

	public void testAddClientAttendance() {
		createInitialObjects();
		java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
		client.addClientAttendance(getClientAttendance(meetingDate));
		customerPersistence.createOrUpdate(client);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
	}

	public void testGetClientAttendanceForMeeting() {
		createInitialObjects();
		java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
		client.addClientAttendance(getClientAttendance(meetingDate));
		customerPersistence.createOrUpdate(client);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendance(), Short.valueOf("1"));
	}

	public void testHandleAttendance() throws NumberFormatException,
			ServiceException {
		createInitialObjects();
		java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
		client.handleAttendance(meetingDate, Short.valueOf("1"));
		HibernateUtil.commitTransaction();
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendance(), Short.valueOf("1"));
		client.handleAttendance(meetingDate, Short.valueOf("2"));
		HibernateUtil.commitTransaction();
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendance(), Short.valueOf("2"));
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
	}

	public void testHandleAttendanceForDifferentDates()
			throws NumberFormatException, ServiceException {
		createInitialObjects();
		java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
		client.handleAttendance(meetingDate, Short.valueOf("1"));
		HibernateUtil.commitTransaction();
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendance(), Short.valueOf("1"));
		HibernateUtil.closeSession();
		Date offSetDate = getDateOffset(1);
		client.handleAttendance(offSetDate, Short.valueOf("2"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 2);
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendance(), Short.valueOf("1"));
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(offSetDate)
						.getAttendance(), Short.valueOf("2"));

	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", Short.valueOf("3"),
				"1.1.1.1", group, new Date(System.currentTimeMillis()));
		HibernateUtil.closeSession();
	}
	
	private void createParentObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("7"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		HibernateUtil.closeSession();
	}

	private ClientAttendanceBO getClientAttendance(java.util.Date meetingDate) {
		ClientAttendanceBO clientAttendance = new ClientAttendanceBO();
		clientAttendance.setAttendance(Short.valueOf("1"));
		clientAttendance.setMeetingDate(meetingDate);
		return clientAttendance;
	}

	private Date getDateOffset(int numberOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month,
				(day - numberOfDays));
		return new Date(currentDateCalendar.getTimeInMillis());
	}

	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}
	
	public void testCreateClientWithoutName() throws Exception {
		try {
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder(""),"","","","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
			client = new ClientBO(TestObjectFactory.getUserContext(), "", CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, personnel, officeId, null, null,
					null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_NAME);
		}
	}
	
	public void testCreateClientWithoutOffice() throws Exception {
		try {
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder("firstlast"),"first","","last","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
			client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, personnel, null, null, null,
					null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_OFFICE);
		}
	}
	
	public void testSuccessfulCreateWithoutFeeAndCustomField() throws Exception {
		String name = "Client 1";
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder(name),"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, personnel, officeId, null, null,
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(name, client.getDisplayName());
		assertEquals(officeId, client.getOffice().getOfficeId());
	}
	
	public void testSuccessfulCreateWithParentGroup() throws Exception {
		String name = "Client 1";
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder(name),"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
		createParentObjects(); 
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, personnel, group.getOffice().getOfficeId(), group, null,
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(name, client.getDisplayName());
		assertEquals(client.getOffice().getOfficeId(), group.getOffice().getOfficeId());
	}
	
	public void testFailureCreatePendingClientWithParentGroupInLowerStatus() throws Exception {
		try{
			String name = "Client 1";
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder(name),"Client","","1","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
			createParentObjects(); 
			client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("2")), null, null, null, null, null, personnel, group.getOffice().getOfficeId(), group, null,
					null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(ce.getKey(), ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION);
		}
	}
	
	public void testFailureCreateActiveClientWithParentGroupInLowerStatus() throws Exception {
		try{
			String name = "Client 1";
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder(name),"Client","","1","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
			createParentObjects(); 
			client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("3")), null, null, null, null, null, personnel, group.getOffice().getOfficeId(), group, null,
					null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(ce.getKey(), ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION);
		}
	}
	
	public void testFailureCreateActiveClientWithoutLO() throws Exception {
		List<FeeView> fees = getFees();
		try {
			
			meeting = getMeeting();
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder("firstlast"),"first","","last","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
			client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("3")), null, null, null, null, fees, personnel, officeId, meeting,null, null,
					null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
			
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_LOAN_OFFICER);
		}
		TestObjectFactory.removeObject(meeting);
		removeFees(fees);
	}
	
	public void testFailureCreateActiveClientWithoutMeeting() throws Exception {
		try {
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder("firstlast"),"first","","last","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
			client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("3")), null, null, null, null, null, personnel, officeId, null,personnel, null,
					null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
			
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_MEETING);
		}
			
	}
	
	public void testFailureCreateClientWithDuplicateNameAndDOB() throws Exception {
		ClientBO client1 = null;
		String name = "Client 1";
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder(name),"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, personnel, officeId, null,personnel, new java.util.Date(),
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		try {
			client1 = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, personnel, officeId, null,personnel, new java.util.Date(),
					null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
			
		} catch (CustomerException ce) {
			assertNull(client1);
			assertEquals(ce.getKey(), CustomerConstants.CUSTOMER_DUPLICATE_CUSTOMERNAME_EXCEPTION);
		}
			
	}
	
	public void testFailureCreateClientWithDuplicateGovtId() throws Exception {
		ClientBO client1 = null;
		String name = "Client 1";
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder(name),"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, personnel, officeId, null,personnel, new java.util.Date(),
				"1",null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		try {
			client1 = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, personnel, officeId, null,personnel, new java.util.Date(),
					"1",null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
			
		} catch (CustomerException ce) {
			assertNull(client1);
			assertEquals(ce.getKey(), CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION);
		}
			
	}
	
	public void testSuccessfulCreateClientInBranch() throws Exception {		
		String name = "Client 1";
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder(name),"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, getCustomFields(), null, personnel, officeId, meeting,personnel, null,
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(name, client.getDisplayName());
		assertEquals(officeId, client.getOffice().getOfficeId());			
	}
	
	public void testUpdateBranchFailure_OfficeNULL()throws Exception{
		createInitialObjects();
		try{
			client.updateBranch(null);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.INVALID_OFFICE,ce.getKey());
		}
	}
	
	public void testUpdateBranchFailure_TransferInSameOffice()throws Exception{
		createInitialObjects();
		try{
			client.updateBranch(client.getOffice());
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER,ce.getKey());
		}
	}
	
	public void testUpdateBranchFirstTime()throws Exception{
		createObjectsForClientTransfer();
		assertNull(client.getActiveCustomerMovement());
		
		client.updateBranch(office);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertNotNull(client.getActiveCustomerMovement());
		assertEquals(office.getOfficeId(), client.getOffice().getOfficeId());
		assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
		office = client.getOffice();
	}
	
	public void testUpdateBranchSecondTime()throws Exception{
		createObjectsForClientTransfer();
		assertNull(client.getActiveCustomerMovement());
		OfficeBO oldOffice = client.getOffice();
		
		client.updateBranch(office);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client.setUserContext(TestObjectFactory.getUserContext());
		CustomerMovementEntity currentMovement = client.getActiveCustomerMovement();
		assertNotNull(currentMovement);
		assertEquals(office.getOfficeId(), currentMovement.getOffice().getOfficeId());
		assertEquals(office.getOfficeId(), client.getOffice().getOfficeId());
		
		client.updateBranch(oldOffice);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		currentMovement = client.getActiveCustomerMovement();
		assertNotNull(currentMovement);
		assertEquals(oldOffice.getOfficeId(), currentMovement.getOffice().getOfficeId());
		assertEquals(oldOffice.getOfficeId(), client.getOffice().getOfficeId());
		
		office = new OfficePersistence().getOffice(office.getOfficeId());
	}
	
	public void testGetClientAndSpouseName()throws Exception{
		createObjectsForClient("Client 1");
		assertEquals(client.getClientName().getName().getFirstName() , "Client 1");
		assertEquals(client.getSpouseName().getName().getFirstName() , "Client 1");
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		office = new OfficePersistence().getOffice(office.getOfficeId());
	}
	
	public void testUpdateClientDetails()throws Exception{
		createObjectsForClient("Client 1");
		ClientDetailView clientDetailView = new ClientDetailView(2,2,2,2,2,2,Short.valueOf("1"),Short.valueOf("1"));
		client.updateClientDetails(clientDetailView);
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		office = new OfficePersistence().getOffice(office.getOfficeId());
	}
	
	private void createObjectsForClientTransfer()throws Exception{
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory.getOffice(Short.valueOf("1")), "customer_office", "cust");
		client = TestObjectFactory.createClient("client_to_transfer",getMeeting(),CustomerStatus.CLIENT_ACTIVE.getValue(), new java.util.Date());
		HibernateUtil.closeSession();
	}
	
	private void createObjectsForClient(String name)throws Exception{
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory.getOffice(Short.valueOf("1")), "customer_office", "cust");
		client = TestObjectFactory.createClient(name,getMeeting(),CustomerStatus.CLIENT_ACTIVE.getValue(), new java.util.Date());
		HibernateUtil.closeSession();
	}
	
	private List<FeeView> getFees() {
		List<FeeView> fees = new ArrayList<FeeView>();
		AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory
				.createPeriodicAmountFee("PeriodicAmountFee",
						FeeCategory.CENTER, "200", MeetingFrequency.WEEKLY,
						Short.valueOf("2"));
		AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory
				.createOneTimeAmountFee("OneTimeAmountFee",
						FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
		fees.add(new FeeView(fee1));
		fees.add(new FeeView(fee2));
		HibernateUtil.commitTransaction();
		return fees;
	}
	
	private void removeFees(List<FeeView> feesToRemove){
		for(FeeView fee :feesToRemove){
			TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
		}
	}
	
	private List<CustomFieldView> getCustomFields() {
			List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
			fields.add(new CustomFieldView(Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC.getValue()));
			fields.add(new CustomFieldView(Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC.getValue()));
			return fields;
	}
	
}
