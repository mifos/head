package org.mifos.application.customer.client.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.business.CustomerMovementEntity;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.persistence.ClientPersistence;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestClientBO extends MifosTestCase {
	private AccountBO accountBO;
	private CustomerBO center;
	private CenterBO center1;
	private CustomerBO group;
	private GroupBO group1;
	private SavingsOfferingBO savingsOffering1;
	private SavingsOfferingBO savingsOffering2;
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
		try {
			TestObjectFactory.cleanUp(accountBO);
			TestObjectFactory.cleanUp(client);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(group1);
			TestObjectFactory.cleanUp(center);
			TestObjectFactory.cleanUp(center1);
			TestObjectFactory.cleanUp(office);
			TestObjectFactory.removeObject(savingsOffering1);
			TestObjectFactory.removeObject(savingsOffering2);
			HibernateUtil.closeSession();
		}
		catch (Exception e) {
			// throwing here may obscure previous failures
			e.printStackTrace();
		}
		super.tearDown();
	}
	
	public static void setDateOfBirth(ClientBO client,Date dateofBirth) {
		client.setDateOfBirth(dateofBirth);
	}

	public static void setNoOfActiveLoans(ClientPerformanceHistoryEntity clientPerformanceHistoryEntity,Integer noOfActiveLoans) {
		clientPerformanceHistoryEntity.setNoOfActiveLoans(noOfActiveLoans);
	}
	
	public void testRemoveClientFromGroup() throws Exception {
		createInitialObjects();
		client.updateClientFlag();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
	
	}

	public void testUpdateWeeklyMeeting_SavedToUpdateLater()throws Exception{
		String oldMeetingPlace = "Delhi";
		MeetingBO weeklyMeeting = new MeetingBO(WeekDay.FRIDAY, Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMER_MEETING, oldMeetingPlace);
		client = TestObjectFactory.createClient("clientname",weeklyMeeting,
			CustomerStatus.CLIENT_ACTIVE);
		MeetingBO clientMeeting = client.getCustomerMeeting().getMeeting();
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, clientMeeting.getMeetingDetails().getRecurAfter(), clientMeeting.getStartDate(), MeetingType.CUSTOMER_MEETING, meetingPlace);
		client.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		
		assertEquals(WeekDay.FRIDAY, client.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(oldMeetingPlace, client.getCustomerMeeting().getMeeting().getMeetingPlace());
		
		assertEquals(YesNoFlag.YES.getValue(), client.getCustomerMeeting().getUpdatedFlag());
		assertEquals(WeekDay.THURSDAY, client.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(meetingPlace, client.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
	}	
	
	public void testGenerateScheduleForClient_CenterSavingsAccount_OnChangeStatus()throws Exception{
		SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY, ApplicableTo.CENTERS, new Date(System.currentTimeMillis()));
		createParentObjects(CustomerStatus.GROUP_ACTIVE);
		accountBO = TestObjectFactory.createSavingsAccount("globalNum", 
				center, AccountState.SAVINGS_ACC_APPROVED, 
				new java.util.Date(), savingsOffering, 
				TestObjectFactory.getContext());
		client = createClient(CustomerStatus.CLIENT_PENDING);
		HibernateUtil.closeSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertEquals(0,accountBO.getAccountActionDates().size());
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client.setUserContext(TestObjectFactory.getContext());
		client.changeStatus(CustomerStatus.CLIENT_ACTIVE, null, "clientActive");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertNotNull(accountBO.getAccountActionDates());

		assertEquals(10,accountBO.getAccountActionDates().size());
		assertEquals(1,accountBO.getAccountCustomFields().size());
		for(AccountActionDateEntity actionDate: accountBO.getAccountActionDates()){
			assertEquals(client.getCustomerId(),actionDate.getCustomer().getCustomerId());
			assertTrue(true);
		}
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());		
	}
	
	public void testGenerateScheduleForClient_GroupSavingsAccount_OnChangeStatus()throws Exception{
		SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY, ApplicableTo.GROUPS, new Date(System.currentTimeMillis()));
		createParentObjects(CustomerStatus.GROUP_ACTIVE);
		accountBO = TestObjectFactory.createSavingsAccount(
				"globalNum", group, AccountState.SAVINGS_ACC_APPROVED, 
				new java.util.Date(), savingsOffering, 
				TestObjectFactory.getContext());
		client = createClient(CustomerStatus.CLIENT_PENDING);
		HibernateUtil.closeSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertEquals(0,accountBO.getAccountActionDates().size());
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client.setUserContext(TestObjectFactory.getContext());
		client.changeStatus(CustomerStatus.CLIENT_ACTIVE, null, "clientActive");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertNotNull(accountBO.getAccountActionDates());
		assertEquals(1,accountBO.getAccountCustomFields().size());
		assertEquals(10,accountBO.getAccountActionDates().size());
		for(AccountActionDateEntity actionDate: accountBO.getAccountActionDates()){
			assertEquals(client.getCustomerId(),actionDate.getCustomer().getCustomerId());
			assertTrue(true);
		}
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());		
	}
	
	public void testGenerateScheduleForClient_OnClientCreate()throws Exception{
		SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY, ApplicableTo.GROUPS, new Date(System.currentTimeMillis()));
		createParentObjects(CustomerStatus.GROUP_ACTIVE);
		accountBO = TestObjectFactory.createSavingsAccount("globalNum", 
				center, AccountState.SAVINGS_ACC_APPROVED, 
				new java.util.Date(), savingsOffering, 
				TestObjectFactory.getContext());
		assertEquals(0,accountBO.getAccountActionDates().size());
		client = createClient(CustomerStatus.CLIENT_ACTIVE);
		HibernateUtil.closeSession();

		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertEquals(1,accountBO.getAccountCustomFields().size());
		assertEquals(10,accountBO.getAccountActionDates().size());
		for(AccountActionDateEntity actionDate: accountBO.getAccountActionDates()){
			assertEquals(client.getCustomerId(),actionDate.getCustomer().getCustomerId());
			assertTrue(true);
		}
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());		
	}
	
	public void testFailure_InitialSavingsOfferingAtCreate()throws Exception{
		savingsOffering1 = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY, ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
		ClientNameDetailView clientView = 
			new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"Client","","1","");
		ClientNameDetailView spouseView = 
			new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
		List<SavingsOfferingBO> offerings = new ArrayList<SavingsOfferingBO>();
		offerings.add(savingsOffering1);
		offerings.add(savingsOffering1);
		try{
			client = new ClientBO(TestObjectFactory.getContext(), clientView.getDisplayName(), CustomerStatus.CLIENT_PARTIAL, null, null, null, null, null, offerings, personnel, officeId, null, null,
				null,null,null,YesNoFlag.NO.getValue(),clientView,spouseView,clientDetailView,null);
		}catch(CustomerException ce){
			assertEquals(ClientConstants.ERRORS_DUPLICATE_OFFERING_SELECTED, ce.getKey());
			assertTrue(true);
		}		
		savingsOffering1 = (SavingsOfferingBO)TestObjectFactory.getObject(SavingsOfferingBO.class, savingsOffering1.getPrdOfferingId());
	}		
	
	public void testInitialSavingsOfferingAtCreate()throws Exception{
		savingsOffering1 = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY, ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
		savingsOffering2 = TestObjectFactory.createSavingsProduct("Offering2", "s2", SavingsType.VOLUNTARY, ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
		List<SavingsOfferingBO> offerings = new ArrayList<SavingsOfferingBO>();
		offerings.add(savingsOffering1);
		offerings.add(savingsOffering2);
		client = new ClientBO(TestObjectFactory.getContext(), clientNameDetailView.getDisplayName(), CustomerStatus.CLIENT_PARTIAL, null, null, null, null, null, offerings, personnel, officeId, null, null,
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		client = new ClientPersistence().getClient(client.getCustomerId());
		assertEquals(offerings.size(), client.getOfferingsAssociatedInCreate().size());
		for(ClientInitialSavingsOfferingEntity clientOffering: client.getOfferingsAssociatedInCreate()){
			if(clientOffering.getSavingsOffering().getPrdOfferingId().equals(savingsOffering1.getPrdOfferingId()))
				assertTrue(true);
			if(clientOffering.getSavingsOffering().getPrdOfferingId().equals(savingsOffering2.getPrdOfferingId()))
				assertTrue(true);
		}
		savingsOffering1 = (SavingsOfferingBO)TestObjectFactory.getObject(SavingsOfferingBO.class, savingsOffering1.getPrdOfferingId());
		savingsOffering2 = (SavingsOfferingBO)TestObjectFactory.getObject(SavingsOfferingBO.class, savingsOffering2.getPrdOfferingId());
	}		
	
	public void testAddClientAttendance() throws Exception {
		createInitialObjects();
		java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
		client.addClientAttendance(getClientAttendance(meetingDate));
		customerPersistence.createOrUpdate(client);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
	}

	public void testGetClientAttendanceForMeeting() throws Exception {
		createInitialObjects();
		java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
		client.addClientAttendance(getClientAttendance(meetingDate));
		customerPersistence.createOrUpdate(client);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendance(), Short.valueOf("1"));
	}

	public void testHandleAttendance() throws Exception{
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
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
	}

	public void testHandleAttendanceForDifferentDates()	throws Exception {
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
		client = TestObjectFactory.getObject(ClientBO.class, client
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

	
	
	public void testCreateClientWithoutName() throws Exception {
		try {
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(
				NameType.CLIENT, TestObjectFactory.SAMPLE_SALUTATION,"","","","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(
				NameType.SPOUSE, TestObjectFactory.SAMPLE_SALUTATION,
				"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			client = new ClientBO(TestUtils.makeUser(), "", 
					CustomerStatus.fromInt(new Short("1")), null, null, null, 
					null, null, null, personnel, officeId, null, null,
					null,null,null,YesNoFlag.YES.getValue(),
					clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			fail("Client Created");
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(CustomerConstants.INVALID_NAME, ce.getKey());
		}
	}
	
	public void testCreateClientWithoutOffice() throws Exception {
		try {
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"first","","last","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			client = new ClientBO(TestUtils.makeUser(), 
					clientNameDetailView.getDisplayName(), 
					CustomerStatus.fromInt(new Short("1")), 
					null, null, null, null, null, null, personnel, null, null, null,
					null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			fail("Client Created");
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_OFFICE);
		}
	}
	
	public void testSuccessfulCreateWithoutFeeAndCustomField() throws Exception {
		String name = "Client 1";
		Short povertyStatus = Short.valueOf("41");
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),povertyStatus);
		client = new ClientBO(TestUtils.makeUser(), 
				clientNameDetailView.getDisplayName(), 
				CustomerStatus.fromInt(new Short("1")), null, null, null, 
				null, null, null, personnel, officeId, null, null,
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,
				spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(name, client.getDisplayName());
		assertEquals(povertyStatus, client.getCustomerDetail().getPovertyStatus());
		assertEquals(officeId, client.getOffice().getOfficeId());
	}
	
	public void testSuccessfulCreateInActiveState_WithAssociatedSavingsOffering() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsProduct("offering1","s1", SavingsType.MANDATORY, ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
		HibernateUtil.closeSession();
		List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();
		selectedOfferings.add(savingsOffering1);
		
		String name = "Client 1";
		Short povertyStatus = Short.valueOf("41");
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),povertyStatus);
		client = new ClientBO(TestUtils.makeUser(), clientNameDetailView.getDisplayName(), CustomerStatus.CLIENT_ACTIVE, null, null, null, null, null, selectedOfferings, personnel, Short.valueOf("3"), getMeeting(), personnel,
				null, null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(name, client.getDisplayName());
		assertEquals(1, client.getOfferingsAssociatedInCreate().size());
		assertEquals(2, client.getAccounts().size());
		for(AccountBO account: client.getAccounts()){
			if(account instanceof SavingsBO){
				assertEquals(savingsOffering1.getPrdOfferingId(), ((SavingsBO)account).getSavingsOffering().getPrdOfferingId());
				assertNotNull(account.getGlobalAccountNum());
				assertTrue(true);
			}
		}
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		savingsOffering1 = null;		
	}
	
	public void testSavingsAccountOnChangeStatusToActive() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsProduct("offering1","s1", SavingsType.MANDATORY, ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
		savingsOffering2 = TestObjectFactory.createSavingsProduct("offering2","s2", SavingsType.VOLUNTARY, ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
		HibernateUtil.closeSession();
		List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();
		selectedOfferings.add(savingsOffering1);
		selectedOfferings.add(savingsOffering2);
		
		Short povertyStatus = Short.valueOf("41");
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),povertyStatus);
		client = new ClientBO(TestObjectFactory.getContext(), clientNameDetailView.getDisplayName(), CustomerStatus.CLIENT_PENDING, null, null, null, null, null, selectedOfferings, personnel, Short.valueOf("3"), getMeeting(), personnel,
				null,null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(2, client.getOfferingsAssociatedInCreate().size());
		assertEquals(1, client.getAccounts().size());
		
		client.setUserContext(TestObjectFactory.getContext());
		client.changeStatus(CustomerStatus.CLIENT_ACTIVE, 
				null, "Client Made Active");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(3, client.getAccounts().size());
		
		for(AccountBO account: client.getAccounts()){
			if(account instanceof SavingsBO){
				assertNotNull(account.getGlobalAccountNum());
				assertTrue(true);
			}
		}
		savingsOffering1 = null;
		savingsOffering2 = null;
	}
	
	public void testSuccessfulCreateWithParentGroup() throws Exception {
		String name = "Client 1";
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
		createParentObjects(CustomerStatus.GROUP_PARTIAL); 
		client = new ClientBO(TestUtils.makeUser(), clientNameDetailView.getDisplayName(), CustomerStatus.fromInt(new Short("1")), null, null, null, null, null, null, personnel, group.getOffice().getOfficeId(), group, null,
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(name, client.getDisplayName());
		assertEquals(client.getOffice().getOfficeId(), group.getOffice().getOfficeId());
	}
	
	public void testFailureCreatePendingClientWithParentGroupInLowerStatus() throws Exception {
		try{
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"Client","","1","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			createParentObjects(CustomerStatus.GROUP_PARTIAL);
			client = new ClientBO(TestUtils.makeUserWithLocales(), 
				clientNameDetailView.getDisplayName(), 
				CustomerStatus.CLIENT_PENDING, null, null, null, null, null, 
				null, personnel, group.getOffice().getOfficeId(), group, null,
				null,null,null,YesNoFlag.YES.getValue(),
				clientNameDetailView,spouseNameDetailView,
				clientDetailView,null);
			fail();
		} catch (CustomerException e) {
			assertNull(client);
			assertEquals(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION, 
				e.getKey());
		}
	}
	
	public void testFailureCreateActiveClientWithParentGroupInLowerStatus() throws Exception {
		try{
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"Client","","1","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			createParentObjects(CustomerStatus.GROUP_PARTIAL); 
			client = new ClientBO(TestUtils.makeUserWithLocales(), 
					clientNameDetailView.getDisplayName(), 
					CustomerStatus.CLIENT_ACTIVE, null, null, null, null, 
					null, null, personnel, group.getOffice().getOfficeId(), 
					group, null,
					null,null,null,YesNoFlag.YES.getValue(),
					clientNameDetailView,spouseNameDetailView,
					clientDetailView,null);
			fail();
		} catch (CustomerException e) {
			assertNull(client);
			assertEquals(
				ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION,
				e.getKey());
		}
	}
	
	public void testFailureCreateActiveClientWithoutLO() throws Exception {
		List<FeeView> fees = getFees();
		try {			
			meeting = getMeeting();
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"first","","last","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			client = new ClientBO(TestUtils.makeUser(), clientNameDetailView.getDisplayName(), CustomerStatus.fromInt(new Short("3")), null, null, null, null, fees, null, personnel, officeId, meeting,null, null,
					null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			fail();
		} catch (CustomerException e) {
			assertNull(client);
			assertEquals(CustomerConstants.INVALID_LOAN_OFFICER, e.getKey());
		}
		removeFees(fees);
	}
	
	public void testFailureCreateActiveClientWithoutMeeting() throws Exception {
		try {
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"first","","last","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			client = new ClientBO(TestUtils.makeUser(), clientNameDetailView.getDisplayName(), CustomerStatus.fromInt(new Short("3")), null, null, null, null, null,  null, personnel, officeId, null,personnel, null,
					null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			fail();
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(CustomerConstants.INVALID_MEETING, ce.getKey());
		}
			
	}
	
	public void testFailureCreateClientWithDuplicateNameAndDOB() throws Exception {
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
		client = new ClientBO(TestUtils.makeUser(), clientNameDetailView.getDisplayName(), CustomerStatus.fromInt(new Short("1")), null, null, null, null, null, null, personnel, officeId, null,personnel, new java.util.Date(),
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		try {
			new ClientBO(TestUtils.makeUser(), clientNameDetailView.getDisplayName(), CustomerStatus.fromInt(new Short("1")), null, null, null, null, null, null, personnel, officeId, null,personnel, new java.util.Date(),
					null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			fail();
		} catch (CustomerException e) {
			assertEquals(
				CustomerConstants.CUSTOMER_DUPLICATE_CUSTOMERNAME_EXCEPTION,
				e.getKey());
		}
			
	}
	
	public void testFailureCreateClientWithDuplicateGovtId() throws Exception {
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
		client = new ClientBO(TestUtils.makeUser(), clientNameDetailView.getDisplayName(), CustomerStatus.fromInt(new Short("1")), null, null, null, null, null, null, personnel, officeId, null,personnel, new java.util.Date(),
				"1",null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(
			ClientBO.class, client.getCustomerId());
		try {
			client = new ClientBO(TestUtils.makeUserWithLocales(), 
				clientNameDetailView.getDisplayName(), 
				CustomerStatus.CLIENT_PARTIAL, null, null, null, null, 
				null, null, personnel, officeId, null,personnel, 
				new java.util.Date(),
				"1",null,null,YesNoFlag.NO.getValue(),
				clientNameDetailView,spouseNameDetailView,
				clientDetailView,null);
			fail();
		} catch (CustomerException e) {
			assertEquals(CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION, 
				e.getKey());
		}
			
	}
	
	public void testSuccessfulCreateClientInBranch() throws Exception {		
		String firstName = "Client";
		String lastName = "Last";
		String displayName = "Client Last";
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT,TestObjectFactory.SAMPLE_SALUTATION,firstName,"",lastName,"");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,TestObjectFactory.SAMPLE_SALUTATION,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
		client = new ClientBO(TestUtils.makeUser(), clientNameDetailView.getDisplayName(), CustomerStatus.fromInt(new Short("1")), null, null, null, getCustomFields(), null, null, personnel, officeId, meeting,personnel, null,
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(displayName, client.getDisplayName());
		assertEquals(firstName, client.getFirstName());
		assertEquals(lastName, client.getLastName());
		assertEquals(officeId, client.getOffice().getOfficeId());			
	}
	
	public void testUpdateGroupFailure_GroupNULL()throws Exception{
		createInitialObjects();
		try{
			client.transferToGroup(null);
			fail();
		}catch(CustomerException ce){
			assertEquals(CustomerConstants.INVALID_PARENT,ce.getKey());
		}
	}
	
	public void testUpdateGroupFailure_TransferInSameGroup()throws Exception{
		createInitialObjects();
		try{
			client.transferToGroup((GroupBO)client.getParentCustomer());
			fail();
		}catch(CustomerException e){
			assertEquals(CustomerConstants.ERRORS_SAME_PARENT_TRANSFER,
				e.getKey());
		}
	}
	
	public void testUpdateGroupFailure_GroupCancelled()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
		group1.changeStatus(CustomerStatus.GROUP_CANCELLED,
				CustomerStatusFlag.GROUP_CANCEL_WITHDRAW, 
				"Status Changed");
		HibernateUtil.commitTransaction();
		try{
			client.transferToGroup(group1);
			fail();
		}catch(CustomerException e){
			assertEquals(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE,
				e.getKey());
		}
	}
	
	public void testUpdateGroupFailure_GroupClosed()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
		group1.changeStatus(CustomerStatus.GROUP_CLOSED,
				CustomerStatusFlag.GROUP_CLOSED_TRANSFERRED, 
				"Status Changed");
		HibernateUtil.commitTransaction();
		try {
			client.transferToGroup(group1);
			fail();
		}
		catch(CustomerException expected){
			assertEquals(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE,
				expected.getKey());
		}
	}
	
	public void testUpdateGroupFailure_GroupStatusLower()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_PARTIAL);		
		try {
			client.transferToGroup(group1);
			fail();
		}
		catch (CustomerException expected){
			assertEquals(ClientConstants.ERRORS_LOWER_GROUP_STATUS,
				expected.getKey());
		}
	}
	
	public void testUpdateGroupFailure_GroupStatusLower_Client_OnHold()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_PARTIAL);	
		client.changeStatus(CustomerStatus.CLIENT_HOLD, null, "client on hold");
		try{
			client.transferToGroup(group1);
			fail();
		}catch(CustomerException e){
			assertEquals(ClientConstants.ERRORS_LOWER_GROUP_STATUS,e.getKey());
		}
	}
	
	public void testUpdateGroupFailure_ClientHasActiveAccounts()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
		accountBO = createSavingsAccount(client,"fsaf6","ads6");
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client.setUserContext(TestUtils.makeUserWithLocales());
		try{
			client.transferToGroup(group1);
			fail();
		}catch(CustomerException ce){
			assertEquals(ClientConstants.ERRORS_ACTIVE_ACCOUNTS_PRESENT,ce.getKey());
		}
		HibernateUtil.closeSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
	}
	
	public void testUpdateGroupFailure_MeetingFrequencyMismatch()throws Exception{
		createInitialObjects();
		MeetingBO meeting = new MeetingBO(Short.valueOf("2"), Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMER_MEETING, "Bangalore");
		center1 = TestObjectFactory.createCenter("Center1", meeting);
		group1 = TestObjectFactory.createGroupUnderCenter("Group2", CustomerStatus.GROUP_ACTIVE, center1);
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client.setUserContext(TestUtils.makeUser());
		try{
			client.transferToGroup(group1);
			fail();
		}catch(CustomerException e){
			assertEquals(CustomerConstants.ERRORS_MEETING_FREQUENCY_MISMATCH,
				e.getKey());
		}
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
	}
	
	public void testSuccessfulTransferToGroupInSameBranch()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
		PositionEntity position  = (PositionEntity) new MasterPersistence().retrieveMasterEntities(PositionEntity.class, Short.valueOf("1")).get(0);
		group.addCustomerPosition(new CustomerPositionEntity(position, client, group));
		center.addCustomerPosition(new CustomerPositionEntity(position, client, group));
		client.transferToGroup(group1);

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class, group1.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		assertEquals(group1.getCustomerMeeting().getMeeting().getMeetingId(), client.getCustomerMeeting().getUpdatedMeeting().getMeetingId());
		assertEquals(group1.getCustomerId(),client.getParentCustomer().getCustomerId());
		assertEquals(0, group.getMaxChildCount().intValue());
		assertEquals(1, group1.getMaxChildCount().intValue());
		assertEquals(center1.getSearchId()+".1.1", client.getSearchId());
		CustomerHierarchyEntity currentHierarchy = client.getActiveCustomerHierarchy();
		assertEquals(group1.getCustomerId(),currentHierarchy.getParentCustomer().getCustomerId());
	}
	
	public void testSuccessfulTransferToGroup_WithMeeting()throws Exception{
		createObjectsForTranferToGroup_WithMeeting();
		
		client.transferToGroup(group1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		assertEquals(WeekDay.MONDAY, client.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertNull(client.getCustomerMeeting().getUpdatedMeeting());
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class, group1.getCustomerId());
		
		assertEquals(group1.getCustomerId(),client.getParentCustomer().getCustomerId());
		assertEquals(0, group.getMaxChildCount().intValue());
		assertEquals(1, group1.getMaxChildCount().intValue());

		CustomerHierarchyEntity currentHierarchy = client.getActiveCustomerHierarchy();
		assertEquals(group1.getCustomerId(),currentHierarchy.getParentCustomer().getCustomerId());
		
	}
	
	public void testSuccessfulTransferToGroup_WithOutMeeting()throws Exception{
		createObjectsForTranferToGroup_WithoutMeeting();
		assertNotNull(client.getCustomerMeeting());
		
		client.transferToGroup(group1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class, group1.getCustomerId());
		
		assertNull(client.getCustomerMeeting());
		assertEquals(group1.getCustomerId(),client.getParentCustomer().getCustomerId());		
	}
	
	public void testSuccessfulTransferToGroupInDifferentBranch()throws Exception{
		createObjectsForTranferToGroup_DifferentBranch();
		PositionEntity position  = (PositionEntity) new MasterPersistence().retrieveMasterEntities(PositionEntity.class, Short.valueOf("1")).get(0);
		group.addCustomerPosition(new CustomerPositionEntity(position, client, group));
		center.addCustomerPosition(new CustomerPositionEntity(position, client, group));
		
		client.transferToGroup(group1);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class, group1.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		assertEquals(office.getOfficeId(),client.getOffice().getOfficeId());
		assertEquals(group1.getCustomerId(),client.getParentCustomer().getCustomerId());
		assertEquals(0, group.getMaxChildCount().intValue());
		assertEquals(1, group1.getMaxChildCount().intValue());
		assertEquals(center1.getSearchId()+".1.1", client.getSearchId());
		CustomerHierarchyEntity currentHierarchy = client.getActiveCustomerHierarchy();
		assertEquals(group1.getCustomerId(),currentHierarchy.getParentCustomer().getCustomerId());
		CustomerMovementEntity customerMovementEntity = client.getActiveCustomerMovement();
		assertEquals(office.getOfficeId(),customerMovementEntity.getOffice().getOfficeId());		
		
		client.setUserContext(TestObjectFactory.getContext());
		client.transferToGroup((GroupBO)group);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class, group1.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		center1 = TestObjectFactory.getObject(CenterBO.class, center1.getCustomerId());
		office = new OfficePersistence().getOffice(office.getOfficeId());
	}
	
	public void testUpdateBranchFailure_OfficeNULL()throws Exception{
		createInitialObjects();
		try{
			client.transferToBranch(null);
			fail();
		}catch(CustomerException e){
			assertEquals(CustomerConstants.INVALID_OFFICE, e.getKey());
		}
	}
	
	public void testUpdateBranchFailure_TransferInSameOffice()throws Exception{
		createInitialObjects();
		try{
			client.transferToBranch(client.getOffice());
			fail();
		}
		catch (CustomerException e) {
			assertEquals(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER,
				e.getKey());
		}
	}
	
	public void testUpdateBranchFailure_OfficeInactive()throws Exception{
		createObjectsForClientTransfer();
		office.update(office.getOfficeName(), office.getShortName(), OfficeStatus.INACTIVE, office.getOfficeLevel(), office.getParentOffice(), null, null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		try{
			client.transferToBranch(office);
			fail();
		}catch(CustomerException e){
			assertEquals(CustomerConstants.ERRORS_TRANSFER_IN_INACTIVE_OFFICE,
				e.getKey());
		}
	}
	
	public void testUpdateBranchFirstTime()throws Exception{
		createObjectsForClientTransfer();
		assertNull(client.getActiveCustomerMovement());
		
		client.transferToBranch(office);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertNotNull(client.getActiveCustomerMovement());
		assertEquals(office.getOfficeId(), client.getOffice().getOfficeId());
		assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
		office = client.getOffice();
	}
	
	public void testUpdateBranchSecondTime()throws Exception{
		createObjectsForClientTransfer();
		assertNull(client.getActiveCustomerMovement());
		OfficeBO oldOffice = client.getOffice();
		
		client.transferToBranch(office);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client.setUserContext(TestUtils.makeUser());
		CustomerMovementEntity currentMovement = client.getActiveCustomerMovement();
		assertNotNull(currentMovement);
		assertEquals(office.getOfficeId(), currentMovement.getOffice().getOfficeId());
		assertEquals(office.getOfficeId(), client.getOffice().getOfficeId());
		
		client.transferToBranch(oldOffice);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		currentMovement = client.getActiveCustomerMovement();
		assertNotNull(currentMovement);
		assertEquals(oldOffice.getOfficeId(), currentMovement.getOffice().getOfficeId());
		assertEquals(oldOffice.getOfficeId(), client.getOffice().getOfficeId());
		
		office = new OfficePersistence().getOffice(office.getOfficeId());
	}
	
	public void testGetClientAndSpouseName()throws Exception{
		createObjectsForClient("Client 1",CustomerStatus.CLIENT_ACTIVE);
		assertEquals(client.getClientName().getName().getFirstName() , "Client 1");
		assertEquals(client.getSpouseName().getName().getFirstName() , "Client 1");
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		office = new OfficePersistence().getOffice(office.getOfficeId());
	}
	
	public void testUpdateClientDetails()throws Exception{
		createObjectsForClient("Client 1",CustomerStatus.CLIENT_ACTIVE);
		Short povertyStatus = Short.valueOf("41");
		assertEquals(1, client.getCustomerDetail().getEthinicity().intValue());
		assertEquals(1, client.getCustomerDetail().getCitizenship().intValue());
		assertEquals(1, client.getCustomerDetail().getHandicapped().intValue());
		ClientDetailView clientDetailView = new ClientDetailView(2,2,2,2,2,2,Short.valueOf("1"),Short.valueOf("1"),povertyStatus);
		client.updateClientDetails(clientDetailView);
		assertEquals(2, client.getCustomerDetail().getEthinicity().intValue());
		assertEquals(2, client.getCustomerDetail().getCitizenship().intValue());
		assertEquals(2, client.getCustomerDetail().getHandicapped().intValue());
		assertEquals(povertyStatus, client.getCustomerDetail().getPovertyStatus());
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		office = new OfficePersistence().getOffice(office.getOfficeId());
	}
	
	public void testUpdateFailureIfLoanOffcierNotThereInActiveState()throws Exception{
		createObjectsForClient("Client 1",CustomerStatus.CLIENT_ACTIVE);
		try{
			client.updateMfiInfo(null);
			fail();
		}catch(CustomerException e){
			assertEquals(CustomerConstants.INVALID_LOAN_OFFICER,e.getKey());
		}
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
	}
	
	public void testUpdateFailureIfLoanOffcierNotThereInHoldState()
	throws Exception {
		createObjectsForClient("Client 1", CustomerStatus.CLIENT_HOLD);
		try{
			client.updateMfiInfo(null);
			fail();
		}
		catch(CustomerException e) {
			assertEquals(CustomerConstants.INVALID_LOAN_OFFICER, e.getKey());
		}
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
	}
	
	public void testUpdateWeeklyMeeting()throws Exception{
		client = TestObjectFactory.createClient("clientname", getMeeting(),
			CustomerStatus.CLIENT_PENDING);
		MeetingBO clientMeeting = client.getCustomerMeeting().getMeeting();
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, 
			clientMeeting.getMeetingDetails().getRecurAfter(), 
			clientMeeting.getStartDate(), MeetingType.CUSTOMER_MEETING, 
			meetingPlace);
		client.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());

		assertEquals(WeekDay.THURSDAY, client.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(meetingPlace, client.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
	}
	
	public void testUpdateMonthlyMeeting()throws Exception{
		String meetingPlace = "Bangalore";
		MeetingBO monthlyMeeting = new MeetingBO(WeekDay.MONDAY, RankType.FIRST, Short.valueOf("2"), new java.util.Date(), MeetingType.CUSTOMER_MEETING, "delhi");
		client = TestObjectFactory.createClient("clientname",monthlyMeeting,
			CustomerStatus.CLIENT_PENDING);
		MeetingBO clientMeeting = client.getCustomerMeeting().getMeeting();
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, RankType.FIRST, clientMeeting.getMeetingDetails().getRecurAfter(), clientMeeting.getStartDate(), MeetingType.CUSTOMER_MEETING, meetingPlace);
		client.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(WeekDay.THURSDAY, client.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(meetingPlace, client.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
	}
	
	public void testUpdateMonthlyMeetingOnDate()throws Exception{
		MeetingBO monthlyMeetingOnDate = new MeetingBO(Short.valueOf("5"), 
			Short.valueOf("2"), new java.util.Date(), 
			MeetingType.CUSTOMER_MEETING, "delhi");
		client = TestObjectFactory.createClient("clientname",
			monthlyMeetingOnDate, CustomerStatus.CLIENT_PENDING);
		MeetingBO clientMeeting = client.getCustomerMeeting().getMeeting();
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, clientMeeting.getMeetingDetails().getRecurAfter(), clientMeeting.getStartDate(), MeetingType.CUSTOMER_MEETING, meetingPlace);
		client.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(meetingPlace, client.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
	}

	public void testCreateMeeting()throws Exception{
		client = TestObjectFactory.createClient("clientname",
			null, CustomerStatus.CLIENT_PENDING);
		String meetingPlace = "newPlace";
		Short recurAfter = Short.valueOf("4");
		MeetingBO newMeeting = new MeetingBO(WeekDay.FRIDAY, recurAfter, 
			new java.util.Date(), MeetingType.CUSTOMER_MEETING, meetingPlace);
		client.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		
		assertEquals(WeekDay.FRIDAY, client.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(meetingPlace, client.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(recurAfter, client.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
	}

	private void createObjectsForClientTransfer()throws Exception{
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, 
			TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE), 
			"customer_office", "cust");
		client = TestObjectFactory.createClient("client_to_transfer",
				getMeeting(), CustomerStatus.CLIENT_ACTIVE);
		HibernateUtil.closeSession();
	}
	
	private SavingsBO createSavingsAccount(CustomerBO customer,String offeringName,String shortName) throws Exception {
		SavingsOfferingBO savingsOffering = new SavingsTestHelper().createSavingsOffering(offeringName,shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017",
				customer, AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}
	
	private void createObjectsForTranferToGroup_WithMeeting()throws Exception{
		Short officeId = new Short("3");
		Short personnel = new Short("1");
		group = TestObjectFactory.createGroupUnderBranch("Group", CustomerStatus.GROUP_PENDING, officeId, null, personnel);
		group1 = TestObjectFactory.createGroupUnderBranch("Group2", CustomerStatus.GROUP_PENDING,officeId, getMeeting(), personnel);
		client = TestObjectFactory.createClient("new client" ,CustomerStatus.CLIENT_PARTIAL, group, new java.util.Date());
		HibernateUtil.closeSession();
	}
	
	private void createObjectsForTranferToGroup_WithoutMeeting()throws Exception{
		Short officeId = new Short("3");
		Short personnel = new Short("1");
		group = TestObjectFactory.createGroupUnderBranch("Group", CustomerStatus.GROUP_PENDING,
				officeId, getMeeting(), personnel);
		group1 = TestObjectFactory.createGroupUnderBranch("Group2", 
				CustomerStatus.GROUP_PENDING, officeId,null, personnel);
		client = TestObjectFactory.createClient("new client",
				CustomerStatus.CLIENT_PARTIAL, group, new java.util.Date());
		HibernateUtil.closeSession();
	}
	
	private void createObjectsForTranferToGroup_SameBranch(
			CustomerStatus groupStatus)
	throws Exception {
		createInitialObjects();
		MeetingBO meeting = new MeetingBO(WeekDay.THURSDAY, 
				Short.valueOf("1"), new java.util.Date(), 
				MeetingType.CUSTOMER_MEETING, "Bangalore");
		center1 = TestObjectFactory.createCenter("Center1", meeting);
		group1 = TestObjectFactory.createGroupUnderCenter("Group2", 
				groupStatus, center1);
		HibernateUtil.closeSession();
	}
	
	private void createObjectsForTranferToGroup_DifferentBranch()throws Exception{
		createInitialObjects();
		office = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, 
				TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE), 
				"customer_office", "cust");
		HibernateUtil.closeSession();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center1 = TestObjectFactory.createCenter("Center1", meeting, 
				office.getOfficeId(), PersonnelConstants.SYSTEM_USER);
		group1 = TestObjectFactory.createGroupUnderCenter("Group2", 
				CustomerStatus.GROUP_ACTIVE, center1);
		HibernateUtil.closeSession();
	}
	
	private void createObjectsForClient(String name, CustomerStatus status)throws Exception{
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
		client = TestObjectFactory.createClient(name, getMeeting(), status);
		HibernateUtil.closeSession();
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
	
	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(
			Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC)
		);
		fields.add(new CustomFieldView(
			Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC)
		);
		return fields;
	}
	
	private void createInitialObjects() throws Exception{
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, 
			Short.valueOf("1"), new java.util.Date(), 
			MeetingType.CUSTOMER_MEETING, "Delhi");
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = createClient(CustomerStatus.CLIENT_ACTIVE);
		HibernateUtil.closeSession();
	}
	
	private ClientBO createClient(CustomerStatus clientStatus){
		return TestObjectFactory.createClient("Client", clientStatus,
				group);
	}
	
	private void createParentObjects(CustomerStatus groupStatus) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", groupStatus, center);
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

	private MeetingBO getMeeting() throws Exception{
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		return meeting;
	}
}
