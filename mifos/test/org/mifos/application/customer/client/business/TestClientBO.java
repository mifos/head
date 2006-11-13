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
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
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
		super.tearDown();
	}
	
	public static void setDateOfBirth(ClientBO client,Date dateofBirth) {
		client.setDateOfBirth(dateofBirth);
	}

	public static void setNoOfActiveLoans(ClientPerformanceHistoryEntity clientPerformanceHistoryEntity,Integer noOfActiveLoans) {
		clientPerformanceHistoryEntity.setNoOfActiveLoans(noOfActiveLoans);
	}
 
	public void testUpdateWeeklyMeeting_SavedToUpdateLater()throws Exception{
		String oldMeetingPlace = "Delhi";
		MeetingBO weeklyMeeting = new MeetingBO(WeekDay.FRIDAY, Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMERMEETING, oldMeetingPlace);
		client = TestObjectFactory.createClient("clientname",weeklyMeeting,CustomerStatus.CLIENT_ACTIVE.getValue(), new java.util.Date());
		MeetingBO clientMeeting = client.getCustomerMeeting().getMeeting();
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, clientMeeting.getMeetingDetails().getRecurAfter(), clientMeeting.getStartDate(), MeetingType.CUSTOMERMEETING, meetingPlace);
		client.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		
		assertEquals(WeekDay.FRIDAY, client.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(oldMeetingPlace, client.getCustomerMeeting().getMeeting().getMeetingPlace());
		
		assertEquals(YesNoFlag.YES.getValue(), client.getCustomerMeeting().getUpdatedFlag());
		assertEquals(WeekDay.THURSDAY, client.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(meetingPlace, client.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
	}	
	
	public void testGenerateScheduleForClient_CenterSavingsAccount_OnChangeStatus()throws Exception{
		SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsOffering("Offering1", "s1", SavingsType.MANDATORY, PrdApplicableMaster.CENTERS);
		createParentObjects(CustomerStatus.GROUP_ACTIVE);
		accountBO = TestObjectFactory.createSavingsAccount("globalNum", center, AccountState.SAVINGS_ACC_APPROVED.getValue(), new java.util.Date(), savingsOffering, TestObjectFactory.getContext());
		client = createClient(CustomerStatus.CLIENT_PENDING);
		HibernateUtil.closeSession();
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertEquals(0,accountBO.getAccountActionDates().size());
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client.setUserContext(TestObjectFactory.getContext());
		client.changeStatus(CustomerStatus.CLIENT_ACTIVE.getValue(), null, "clientActive");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertNotNull(accountBO.getAccountActionDates());

		assertEquals(10,accountBO.getAccountActionDates().size());
		assertEquals(1,accountBO.getAccountCustomFields().size());
		for(AccountActionDateEntity actionDate: accountBO.getAccountActionDates()){
			assertEquals(client.getCustomerId(),actionDate.getCustomer().getCustomerId());
			assertTrue(true);
		}
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());		
	}
	
	public void testGenerateScheduleForClient_GroupSavingsAccount_OnChangeStatus()throws Exception{
		SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsOffering("Offering1", "s1", SavingsType.MANDATORY, PrdApplicableMaster.GROUPS);
		createParentObjects(CustomerStatus.GROUP_ACTIVE);
		accountBO = TestObjectFactory.createSavingsAccount("globalNum", group, AccountState.SAVINGS_ACC_APPROVED.getValue(), new java.util.Date(), savingsOffering, TestObjectFactory.getContext());
		client = createClient(CustomerStatus.CLIENT_PENDING);
		HibernateUtil.closeSession();
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertEquals(0,accountBO.getAccountActionDates().size());
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client.setUserContext(TestObjectFactory.getContext());
		client.changeStatus(CustomerStatus.CLIENT_ACTIVE.getValue(), null, "clientActive");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertNotNull(accountBO.getAccountActionDates());
		assertEquals(1,accountBO.getAccountCustomFields().size());
		assertEquals(10,accountBO.getAccountActionDates().size());
		for(AccountActionDateEntity actionDate: accountBO.getAccountActionDates()){
			assertEquals(client.getCustomerId(),actionDate.getCustomer().getCustomerId());
			assertTrue(true);
		}
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());		
	}
	
	public void testGenerateScheduleForClient_OnClientCreate()throws Exception{
		SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsOffering("Offering1", "s1", SavingsType.MANDATORY, PrdApplicableMaster.GROUPS);
		createParentObjects(CustomerStatus.GROUP_ACTIVE);
		accountBO = TestObjectFactory.createSavingsAccount("globalNum", center, AccountState.SAVINGS_ACC_APPROVED.getValue(), new java.util.Date(), savingsOffering, TestObjectFactory.getContext());
		assertEquals(0,accountBO.getAccountActionDates().size());
		client = createClient(CustomerStatus.CLIENT_ACTIVE);
		HibernateUtil.closeSession();

		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertEquals(1,accountBO.getAccountCustomFields().size());
		assertEquals(10,accountBO.getAccountActionDates().size());
		for(AccountActionDateEntity actionDate: accountBO.getAccountActionDates()){
			assertEquals(client.getCustomerId(),actionDate.getCustomer().getCustomerId());
			assertTrue(true);
		}
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());		
	}
	
	public void testFailure_InitialSavingsOfferingAtCreate()throws Exception{
		savingsOffering1 = TestObjectFactory.createSavingsOffering("Offering1", "s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		String name = "client1";
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
		List<SavingsOfferingBO> offerings = new ArrayList<SavingsOfferingBO>();
		offerings.add(savingsOffering1);
		offerings.add(savingsOffering1);
		try{
			client = new ClientBO(TestObjectFactory.getContext(), clientNameDetailView.getDisplayName(), CustomerStatus.CLIENT_PARTIAL, null, null, null, null, null, offerings, personnel, officeId, null, null,
				null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		}catch(CustomerException ce){
			assertEquals(ClientConstants.ERRORS_DUPLICATE_OFFERING_SELECTED, ce.getKey());
			assertTrue(true);
		}		
		savingsOffering1 = (SavingsOfferingBO)TestObjectFactory.getObject(SavingsOfferingBO.class, savingsOffering1.getPrdOfferingId());
	}		
	
	public void testInitialSavingsOfferingAtCreate()throws Exception{
		savingsOffering1 = TestObjectFactory.createSavingsOffering("Offering1", "s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		savingsOffering2 = TestObjectFactory.createSavingsOffering("Offering2", "s2", SavingsType.VOLUNTARY, PrdApplicableMaster.CLIENTS);
		String name = "client1";
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
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
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
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
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
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
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
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

	
	
	public void testCreateClientWithoutName() throws Exception {
		try {
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"","","","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			client = new ClientBO(TestObjectFactory.getUserContext(), "", CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, null, personnel, officeId, null, null,
					null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_NAME);
		}
	}
	
	public void testCreateClientWithoutOffice() throws Exception {
		try {
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"first","","last","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, null, personnel, null, null, null,
					null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_OFFICE);
		}
	}
	
	public void testSuccessfulCreateWithoutFeeAndCustomField() throws Exception {
		String name = "Client 1";
		Short povertyStatus = Short.valueOf("41");
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),povertyStatus);
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, null, personnel, officeId, null, null,
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(name, client.getDisplayName());
		assertEquals(povertyStatus, client.getCustomerDetail().getPovertyStatus());
		assertEquals(officeId, client.getOffice().getOfficeId());
	}
	
	public void testSuccessfulCreateInActiveState_WithAssociatedSavingsOffering() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsOffering("offering1","s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		HibernateUtil.closeSession();
		List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();
		selectedOfferings.add(savingsOffering1);
		
		String name = "Client 1";
		Short povertyStatus = Short.valueOf("41");
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),povertyStatus);
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.CLIENT_ACTIVE, null, null, null, null, null, selectedOfferings, personnel, Short.valueOf("3"), getMeeting(), personnel,
				null, null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
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
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		savingsOffering1 = null;		
	}
	
	public void testSavingsAccountOnChangeStatusToActive() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsOffering("offering1","s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		savingsOffering2 = TestObjectFactory.createSavingsOffering("offering2","s2", SavingsType.VOLUNTARY, PrdApplicableMaster.CLIENTS);
		HibernateUtil.closeSession();
		List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();
		selectedOfferings.add(savingsOffering1);
		selectedOfferings.add(savingsOffering2);
		
		String name = "Client 1";
		Short povertyStatus = Short.valueOf("41");
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),povertyStatus);
		client = new ClientBO(TestObjectFactory.getContext(), clientNameDetailView.getDisplayName(), CustomerStatus.CLIENT_PENDING, null, null, null, null, null, selectedOfferings, personnel, Short.valueOf("3"), getMeeting(), personnel,
				null,null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(2, client.getOfferingsAssociatedInCreate().size());
		assertEquals(1, client.getAccounts().size());
		
		client.setUserContext(TestObjectFactory.getContext());
		client.changeStatus(CustomerStatus.CLIENT_ACTIVE.getValue(),null, "Client Made Active");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
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
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
		createParentObjects(CustomerStatus.GROUP_PARTIAL); 
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, null, personnel, group.getOffice().getOfficeId(), group, null,
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
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"Client","","1","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			createParentObjects(CustomerStatus.GROUP_PARTIAL); 
			client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("2")), null, null, null, null, null, null, personnel, group.getOffice().getOfficeId(), group, null,
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
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"Client","","1","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			createParentObjects(CustomerStatus.GROUP_PARTIAL); 
			client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("3")), null, null, null, null, null, null, personnel, group.getOffice().getOfficeId(), group, null,
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
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"first","","last","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("3")), null, null, null, null, fees, null, personnel, officeId, meeting,null, null,
					null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
			
		} catch (CustomerException ce) {
			assertNull(client);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_LOAN_OFFICER);
		}
		removeFees(fees);
	}
	
	public void testFailureCreateActiveClientWithoutMeeting() throws Exception {
		try {
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"first","","last","");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("3")), null, null, null, null, null,  null, personnel, officeId, null,personnel, null,
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
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, null, personnel, officeId, null,personnel, new java.util.Date(),
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		try {
			client1 = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, null, personnel, officeId, null,personnel, new java.util.Date(),
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
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, null, personnel, officeId, null,personnel, new java.util.Date(),
				"1",null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		try {
			client1 = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, null, null, null, personnel, officeId, null,personnel, new java.util.Date(),
					"1",null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			assertFalse("Client Created", true);
			
		} catch (CustomerException ce) {
			assertNull(client1);
			assertEquals(ce.getKey(), CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION);
		}
			
	}
	
	public void testSuccessfulCreateClientInBranch() throws Exception {		
		String firstName = "Client";
		String lastName = "Last";
		String displayName = "Client Last";
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("3"),1,firstName,"",lastName,"");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, null, getCustomFields(), null, null, personnel, officeId, meeting,personnel, null,
				null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(displayName, client.getDisplayName());
		assertEquals(firstName, client.getFirstName());
		assertEquals(lastName, client.getLastName());
		assertEquals(officeId, client.getOffice().getOfficeId());			
	}
	
	public void testUpdateGroupFailure_GroupNULL()throws Exception{
		createInitialObjects();
		try{
			client.transferToGroup(null);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.INVALID_PARENT,ce.getKey());
		}
	}
	
	public void testUpdateGroupFailure_TransferInSameGroup()throws Exception{
		createInitialObjects();
		try{
			client.transferToGroup((GroupBO)client.getParentCustomer());
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_SAME_PARENT_TRANSFER,ce.getKey());
		}
	}
	
	public void testUpdateGroupFailure_GroupCancelled()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
		group1.changeStatus(CustomerStatus.GROUP_CANCELLED.getValue(),Short.valueOf("11"), "Status Changed");
		HibernateUtil.commitTransaction();
		try{
			client.transferToGroup(group1);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE,ce.getKey());
		}
	}
	
	public void testUpdateGroupFailure_GroupClosed()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
		group1.changeStatus(CustomerStatus.GROUP_CLOSED.getValue(),Short.valueOf("16"), "Status Changed");
		HibernateUtil.commitTransaction();
		try{
			client.transferToGroup(group1);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE,ce.getKey());
		}
	}
	
	public void testUpdateGroupFailure_GroupStatusLower()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_PARTIAL);		
		try{
			client.transferToGroup(group1);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(ClientConstants.ERRORS_LOWER_GROUP_STATUS,ce.getKey());
		}
	}
	
	public void testUpdateGroupFailure_GroupStatusLower_Client_OnHold()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_PARTIAL);	
		client.changeStatus(CustomerStatus.CLIENT_HOLD.getValue(), null, "client on hold");
		try{
			client.transferToGroup(group1);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(ClientConstants.ERRORS_LOWER_GROUP_STATUS,ce.getKey());
		}
	}
	
	public void testUpdateGroupFailure_ClientHasActiveAccounts()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
		accountBO = createSavingsAccount(client,"fsaf6","ads6");
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client.setUserContext(TestObjectFactory.getUserContext());
		try{
			client.transferToGroup(group1);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);			
			assertEquals(ClientConstants.ERRORS_ACTIVE_ACCOUNTS_PRESENT,ce.getKey());
		}
		HibernateUtil.closeSession();
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
	}
	
	public void testUpdateGroupFailure_MeetingFrequencyMismatch()throws Exception{
		createInitialObjects();
		MeetingBO meeting = new MeetingBO(Short.valueOf("2"), Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMERMEETING, "Bangalore");
		center1 = TestObjectFactory.createCenter("Center1", CustomerStatus.CENTER_ACTIVE.getValue(),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group1 = TestObjectFactory.createGroupUnderCenter("Group2", CustomerStatus.GROUP_ACTIVE, center1);
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client.setUserContext(TestObjectFactory.getUserContext());
		try{
			client.transferToGroup(group1);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);	
			assertEquals(CustomerConstants.ERRORS_MEETING_FREQUENCY_MISMATCH,ce.getKey());
		}
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
	}
	
	public void testSuccessfulTransferToGroupInSameBranch()throws Exception{
		createObjectsForTranferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
		PositionEntity position  = (PositionEntity) new MasterPersistence().retrieveMasterEntities(PositionEntity.class, Short.valueOf("1")).get(0);
		group.addCustomerPosition(new CustomerPositionEntity(position, client, group));
		center.addCustomerPosition(new CustomerPositionEntity(position, client, group));
		client.transferToGroup(group1);

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group1 = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group1.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
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
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group1 = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group1.getCustomerId());
		
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
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group1 = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group1.getCustomerId());
		
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
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group1 = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group1.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
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

		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group1 = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group1.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		center1 = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center1.getCustomerId());
		office = new OfficePersistence().getOffice(office.getOfficeId());
	}
	
	public void testUpdateBranchFailure_OfficeNULL()throws Exception{
		createInitialObjects();
		try{
			client.transferToBranch(null);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.INVALID_OFFICE,ce.getKey());
		}
	}
	
	public void testUpdateBranchFailure_TransferInSameOffice()throws Exception{
		createInitialObjects();
		try{
			client.transferToBranch(client.getOffice());
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER,ce.getKey());
		}
	}
	
	public void testUpdateBranchFailure_OfficeInactive()throws Exception{
		createObjectsForClientTransfer();
		office.update(office.getOfficeName(), office.getShortName(), OfficeStatus.INACTIVE, office.getOfficeLevel(), office.getParentOffice(), null, null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		try{
			client.transferToBranch(office);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_TRANSFER_IN_INACTIVE_OFFICE,ce.getKey());
		}
	}
	
	public void testUpdateBranchFirstTime()throws Exception{
		createObjectsForClientTransfer();
		assertNull(client.getActiveCustomerMovement());
		
		client.transferToBranch(office);
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
		
		client.transferToBranch(office);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client.setUserContext(TestObjectFactory.getUserContext());
		CustomerMovementEntity currentMovement = client.getActiveCustomerMovement();
		assertNotNull(currentMovement);
		assertEquals(office.getOfficeId(), currentMovement.getOffice().getOfficeId());
		assertEquals(office.getOfficeId(), client.getOffice().getOfficeId());
		
		client.transferToBranch(oldOffice);
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
		createObjectsForClient("Client 1",CustomerStatus.CLIENT_ACTIVE);
		assertEquals(client.getClientName().getName().getFirstName() , "Client 1");
		assertEquals(client.getSpouseName().getName().getFirstName() , "Client 1");
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
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
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		office = new OfficePersistence().getOffice(office.getOfficeId());
	}
	
	public void testUpdateFailureIfLoanOffcierNotThereInActiveState()throws Exception{
		createObjectsForClient("Client 1",CustomerStatus.CLIENT_ACTIVE);
		try{
			client.updateMfiInfo(null);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.INVALID_LOAN_OFFICER,ce.getKey());
		}
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
	}
	
	public void testUpdateFailureIfLoanOffcierNotThereInHoldState()throws Exception{
		
			createObjectsForClient("Client 1",CustomerStatus.CLIENT_HOLD );
			try{
			client.updateMfiInfo(null);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.INVALID_LOAN_OFFICER,ce.getKey());
		}
		catch(Exception ce){
			ce.printStackTrace();
		}
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
	}
	
	public void testUpdateWeeklyMeeting()throws Exception{
		client = TestObjectFactory.createClient("clientname",getMeeting(),CustomerStatus.CLIENT_PENDING.getValue(), new java.util.Date());
		MeetingBO clientMeeting = client.getCustomerMeeting().getMeeting();
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, clientMeeting.getMeetingDetails().getRecurAfter(), clientMeeting.getStartDate(), MeetingType.CUSTOMERMEETING, meetingPlace);
		client.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());

		assertEquals(WeekDay.THURSDAY, client.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(meetingPlace, client.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
	}
	
	public void testUpdateMonthlyMeeting()throws Exception{
		String meetingPlace = "Bangalore";
		MeetingBO monthlyMeeting = new MeetingBO(WeekDay.MONDAY, RankType.FIRST, Short.valueOf("2"), new java.util.Date(), MeetingType.CUSTOMERMEETING, "delhi");
		client = TestObjectFactory.createClient("clientname",monthlyMeeting,CustomerStatus.CLIENT_PENDING.getValue(), new java.util.Date());
		MeetingBO clientMeeting = client.getCustomerMeeting().getMeeting();
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, RankType.FIRST, clientMeeting.getMeetingDetails().getRecurAfter(), clientMeeting.getStartDate(), MeetingType.CUSTOMERMEETING, meetingPlace);
		client.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(WeekDay.THURSDAY, client.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
		assertEquals(meetingPlace, client.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
	}
	
	public void testUpdateMonthlyMeetingOnDate()throws Exception{
		MeetingBO monthlyMeetingOnDate = new MeetingBO(Short.valueOf("5"), Short.valueOf("2"), new java.util.Date(), MeetingType.CUSTOMERMEETING, "delhi");
		client = TestObjectFactory.createClient("clientname",monthlyMeetingOnDate,CustomerStatus.CLIENT_PENDING.getValue(), new java.util.Date());
		MeetingBO clientMeeting = client.getCustomerMeeting().getMeeting();
		String meetingPlace = "Bangalore";
		MeetingBO newMeeting = new MeetingBO(WeekDay.THURSDAY, clientMeeting.getMeetingDetails().getRecurAfter(), clientMeeting.getStartDate(), MeetingType.CUSTOMERMEETING, meetingPlace);
		client.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		assertEquals(meetingPlace, client.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
	}
	public void testCreateMeeting()throws Exception{
		client = TestObjectFactory.createClient("clientname",null,CustomerStatus.CLIENT_PENDING.getValue(), new java.util.Date());
		String meetingPlace = "newPlace";
		Short recurAfter = Short.valueOf("4");
		MeetingBO newMeeting = new MeetingBO(WeekDay.FRIDAY, recurAfter, new java.util.Date(), MeetingType.CUSTOMERMEETING, meetingPlace);
		client.updateMeeting(newMeeting);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		
		assertEquals(WeekDay.FRIDAY, client.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
		assertEquals(meetingPlace, client.getCustomerMeeting().getMeeting().getMeetingPlace());
		assertEquals(recurAfter, client.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter());
	}
	
	private void createObjectsForClientTransfer()throws Exception{
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory.getOffice(Short.valueOf("1")), "customer_office", "cust");
		client = TestObjectFactory.createClient("client_to_transfer",getMeeting(),CustomerStatus.CLIENT_ACTIVE.getValue(), new java.util.Date());
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
		client = TestObjectFactory.createClient("new client" ,CustomerStatus.CLIENT_PARTIAL.getValue(), group, new java.util.Date());
		HibernateUtil.closeSession();
	}
	
	private void createObjectsForTranferToGroup_WithoutMeeting()throws Exception{
		Short officeId = new Short("3");
		Short personnel = new Short("1");
		group = TestObjectFactory.createGroupUnderBranch("Group", CustomerStatus.GROUP_PENDING,
				officeId, getMeeting(), personnel);
		group1 = TestObjectFactory.createGroupUnderBranch("Group2", CustomerStatus.GROUP_PENDING, officeId,null, personnel);
		client = TestObjectFactory.createClient("new client" ,CustomerStatus.CLIENT_PARTIAL.getValue(), group, new java.util.Date());
		HibernateUtil.closeSession();
	}
	
	private void createObjectsForTranferToGroup_SameBranch(CustomerStatus groupStatus)throws Exception{
		createInitialObjects();
		MeetingBO meeting = new MeetingBO(WeekDay.THURSDAY, Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMERMEETING, "Bangalore");
		center1 = TestObjectFactory.createCenter("Center1", CustomerStatus.CENTER_ACTIVE.getValue(),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group1 = TestObjectFactory.createGroupUnderCenter("Group2", groupStatus, center1);
		HibernateUtil.closeSession();
	}
	
	private void createObjectsForTranferToGroup_DifferentBranch()throws Exception{
		createInitialObjects();
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory.getOffice(Short.valueOf("1")), "customer_office", "cust");
		HibernateUtil.closeSession();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center1 = TestObjectFactory.createCenter("Center1", meeting, office.getOfficeId(), Short.valueOf("1"));
		group1 = TestObjectFactory.createGroupUnderCenter("Group2", CustomerStatus.GROUP_ACTIVE, center1);
		HibernateUtil.closeSession();
	}
	
	private void createObjectsForClient(String name, CustomerStatus status)throws Exception{
		office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory.getOffice(Short.valueOf("1")), "customer_office", "cust");
		client = TestObjectFactory.createClient(name,getMeeting(),status.getValue(), new java.util.Date());
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
				Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC.getValue())
			);
			fields.add(new CustomFieldView(
				Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC.getValue())
			);
			return fields;
	}
	
	private void createInitialObjects() throws Exception{
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMERMEETING, "Delhi");
		center = TestObjectFactory.createCenter("Center", CustomerStatus.CENTER_ACTIVE.getValue(),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = createClient(CustomerStatus.CLIENT_ACTIVE);
		HibernateUtil.closeSession();
	}
	
	private ClientBO createClient(CustomerStatus clientStatus){
		return TestObjectFactory.createClient("Client", clientStatus.getValue(),
				"1.1.1.1", group, new Date(System.currentTimeMillis()));
	}
	
	private void createParentObjects(CustomerStatus groupStatus) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", CustomerStatus.CENTER_ACTIVE.getValue(),
				"1.1", meeting, new Date(System.currentTimeMillis()));
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
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMERMEETING, "Delhi");
		return meeting;
	}
}
