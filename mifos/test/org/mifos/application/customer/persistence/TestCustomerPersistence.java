package org.mifos.application.customer.persistence;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.application.bulkentry.exceptions.BulkEntryAccountUpdateException;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerPerformanceHistoryView;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.scheduler.Constants;
import org.mifos.framework.components.scheduler.ScheduleDataIntf;
import org.mifos.framework.components.scheduler.ScheduleInputsIntf;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerFactory;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerPersistence extends MifosTestCase {

	private MeetingBO meeting;

	private CustomerBO center;

	private CustomerBO client;

	private CustomerBO group;

	private AccountBO account;
	
	private SavingsOfferingBO savingsOffering;
	
	private CustomerPersistence customerPersistence = new CustomerPersistence();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() {
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);

		HibernateUtil.closeSession();

	}

	public void testCustomersUnderLO() {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		List<CustomerView> customers = customerPersistence.getActiveParentList(
				Short.valueOf("1"), CustomerConstants.CENTER_LEVEL_ID, Short
						.valueOf("3"));
		assertEquals(1, customers.size());

	}

	public void testActiveCustomersUnderParent() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		client = getCustomer("13", "9", "3");
		group = client.getParentCustomer();
		center = client.getParentCustomer().getParentCustomer();
		List<CustomerView> customers = customerPersistence
				.getChildrenForParent(center.getCustomerId(), "1.1", center
						.getOffice().getOfficeId());
		assertEquals(2, customers.size());
	}

	public void testOnHoldCustomersUnderParent() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		client = getCustomer("13", "10", "4");
		group = client.getParentCustomer();
		center = client.getParentCustomer().getParentCustomer();
		List<CustomerView> customers = customerPersistence
				.getChildrenForParent(center.getCustomerId(), "1.1", center
						.getOffice().getOfficeId());
		assertEquals(2, customers.size());
	}

	public void testGetLastMeetingDateForCustomer() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, startDate);
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, startDate);
		account = getLoanAccount(group, meeting);
		// Date actionDate = new Date(2006,03,13);
		Date meetingDate = customerPersistence
				.getLastMeetingDateForCustomer(center.getCustomerId());
		assertEquals(new Date(getMeetingDates(meeting).getTime()).toString(),
				meetingDate.toString());

	}

	public void testGetProducts() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, startDate);
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, startDate);
		account = getLoanAccount(group, meeting);
		Date meetingDate = customerPersistence
				.getLastMeetingDateForCustomer(center.getCustomerId());
		List<PrdOfferingBO> productList = customerPersistence.getLoanProducts(
				meetingDate, "1.1", center.getPersonnel().getPersonnelId());
		assertEquals(1, productList.size());
	}

	private AccountBO getLoanAccount(CustomerBO group, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), startDate, loanOffering);

	}

	private CustomerBO getCustomer(String centerStatus, String groupStatus,
			String clientStatus) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short
				.valueOf(centerStatus), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short
				.valueOf(groupStatus), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", Short
				.valueOf(clientStatus), "1.1.1.1", group, new Date(System
				.currentTimeMillis()));
		return client;
	}

	private static java.util.Date getMeetingDates(MeetingBO meeting) {
		List<java.util.Date> dates = new ArrayList<java.util.Date>();
		ScheduleDataIntf scheduleData;
		SchedulerIntf scheduler;
		ScheduleInputsIntf scheduleInputs;
		scheduler = SchedulerFactory.getScheduler();
		try {
			scheduleData = SchedulerFactory.getScheduleData(Constants.WEEK);
			scheduleInputs = SchedulerFactory.getScheduleInputs();
			scheduleInputs
					.setStartDate(meeting.getMeetingStartDate().getTime());
			scheduleData.setRecurAfter(1);

			scheduleData.setWeekDay(meeting.getMeetingDetails()
					.getMeetingRecurrence().getWeekDay().getWeekDayId());
			scheduleInputs.setScheduleData(scheduleData);
			scheduler.setScheduleInputs(scheduleInputs);
			dates = scheduler.getAllDates(new java.util.Date(System
					.currentTimeMillis()));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return dates.get(dates.size() - 1);
	}

	public void testGetSavingsProducts() throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));

		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, startDate);
		SavingsOfferingBO savingsOffering = TestObjectFactory
				.createSavingsOffering("SavingPrd1", Short.valueOf("2"),
						new Date(System.currentTimeMillis()), Short
								.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		account = TestObjectFactory.createSavingsAccount("432434", center,
				Short.valueOf("16"), startDate, savingsOffering);
		List<PrdOfferingBO> productList = customerPersistence
				.getSavingsProducts(startDate, "1.1", center.getPersonnel()
						.getPersonnelId());
		assertEquals(1, productList.size());
	}

	public void testGetChildernForParent()throws Exception {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group1", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("client1",ClientConstants.STATUS_ACTIVE,"1.4.1.1",group,new Date(System
				.currentTimeMillis()));
		ClientBO client2 = TestObjectFactory.createClient("client2",ClientConstants.STATUS_CLOSED,"1.4.1.2",group,new Date(System
				.currentTimeMillis()));
		ClientBO client3 = TestObjectFactory.createClient("client3",ClientConstants.STATUS_CANCELLED,"1.4.1.3",group,new Date(System
				.currentTimeMillis()));
		ClientBO client4 = TestObjectFactory.createClient("client4",ClientConstants.STATUS_PENDING,"1.4.1.4",group,new Date(System
				.currentTimeMillis()));

		List<CustomerBO> customerList = customerPersistence.getChildrenForParent(center.getSearchId(),center.getOffice().getOfficeId(),CustomerConstants.CLIENT_LEVEL_ID);
		assertEquals(new Integer("3").intValue(),customerList.size());
		
		for(CustomerBO customer:customerList){
			if(customer.getCustomerId().intValue()==client3.getCustomerId().intValue())
				assertTrue(true);
		}
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client4);
	}
	
	public void testRetrieveSavingsAccountForCustomer()throws Exception{
		CustomerPersistence customerPersistence = new CustomerPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group1", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		savingsOffering = TestObjectFactory.createSavingsOffering("SavingPrd1",Short.valueOf("2"),new Date(System.currentTimeMillis()),
				Short.valueOf("2"),300.0,Short.valueOf("1"),1.2,200.0,200.0,Short.valueOf("2"),Short.valueOf("1"),meetingIntCalc,meetingIntPost);
		UserContext uc = new UserContext();
		uc.setId(Short.valueOf("1"));
		account = TestObjectFactory.createSavingsAccount("000100000000020",group, AccountStates.SAVINGS_ACC_APPROVED ,new java.util.Date(),savingsOffering,uc);
		HibernateUtil.closeSession();
		List<SavingsBO> savingsList=customerPersistence.retrieveSavingsAccountForCustomer(group.getCustomerId());
		assertNotNull(savingsList);
		assertEquals(Integer.valueOf("1").intValue(),savingsList.size());
		account = savingsList.get(0);
		group = account.getCustomer();
		center = group.getParentCustomer();
	}
	public void testNumberOfMeetingsAttended() throws HibernateProcessException, NumberFormatException, BulkEntryAccountUpdateException{
		
		BulkEntryBusinessService bulkEntryBusinessService = new BulkEntryBusinessService();

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1,1,4,2));
		center=TestObjectFactory.createCenter("Center",Short.valueOf("13"),"1.1",meeting,new Date(System.currentTimeMillis()));
		group=TestObjectFactory.createGroup("Group",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",Short.valueOf("3"),"1.1.1.1",group,new Date(System.currentTimeMillis()));
		
		bulkEntryBusinessService.saveAttendance(client.getCustomerId(),new Date(System.currentTimeMillis()),Short.valueOf("2"));
		HibernateUtil.commitTransaction();
		
		bulkEntryBusinessService.saveAttendance(client.getCustomerId(),new Date(System.currentTimeMillis()),Short.valueOf("1"));
		HibernateUtil.commitTransaction();
		
		Calendar currentDate = new GregorianCalendar();
		currentDate.roll(Calendar.DATE,1);
		
		bulkEntryBusinessService.saveAttendance(client.getCustomerId(),new Date(currentDate.getTimeInMillis()),Short.valueOf("4"));
		HibernateUtil.commitTransaction();

		CustomerPersistence customerPersistence = new CustomerPersistence();
		CustomerPerformanceHistoryView customerPerformanceHistoryView = customerPersistence.numberOfMeetings(true,client.getCustomerId());		
		assertEquals(2,customerPerformanceHistoryView.getMeetingsAttended().intValue());
		HibernateUtil.closeSession();
		client = (CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client.getCustomerId());
	}
	
	public void testNumberOfMeetingsMissed() throws HibernateProcessException, NumberFormatException, BulkEntryAccountUpdateException{
		BulkEntryBusinessService bulkEntryBusinessService = new BulkEntryBusinessService();
		
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1,1,4,2));
		center=TestObjectFactory.createCenter("Center",Short.valueOf("13"),"1.1",meeting,new Date(System.currentTimeMillis()));
		group=TestObjectFactory.createGroup("Group",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",Short.valueOf("3"),"1.1.1.1",group,new Date(System.currentTimeMillis()));
		
		bulkEntryBusinessService.saveAttendance(client.getCustomerId(),new Date(System.currentTimeMillis()),Short.valueOf("1"));
		HibernateUtil.commitTransaction();
		
		bulkEntryBusinessService.saveAttendance(client.getCustomerId(),new Date(System.currentTimeMillis()),Short.valueOf("2"));
		HibernateUtil.commitTransaction();
		
		Calendar currentDate = new GregorianCalendar();
		currentDate.roll(Calendar.DATE,1);
		bulkEntryBusinessService.saveAttendance(client.getCustomerId(),new Date(currentDate.getTimeInMillis()),Short.valueOf("3"));
		HibernateUtil.commitTransaction();

		CustomerPersistence customerPersistence = new CustomerPersistence();
		CustomerPerformanceHistoryView customerPerformanceHistoryView = customerPersistence.numberOfMeetings(false,client.getCustomerId());		
		assertEquals(2,customerPerformanceHistoryView.getMeetingsMissed().intValue());	
		HibernateUtil.closeSession();
		client = (CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client.getCustomerId());
	}

	
	public void testLastLoanAmount() throws PersistenceException{		
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1,1,4,2));
		center=TestObjectFactory.createCenter("Center",Short.valueOf("13"),"1.1",meeting,new Date(System.currentTimeMillis()));
		group=TestObjectFactory.createGroup("Group",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",Short.valueOf("3"),"1.1.1.1",group,new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loan", Short.valueOf("2"),new Date(System.currentTimeMillis()), Short.valueOf("1"),300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),Short.valueOf("1"), meeting);
		LoanBO loanBO = TestObjectFactory.createLoanAccount("42423142341", client, Short.valueOf("5"), new Date(System.currentTimeMillis()),loanOffering);
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		account=(AccountBO)HibernateUtil.getSessionTL().get(LoanBO.class,loanBO.getAccountId());		
		AccountStateEntity accountStateEntity = new AccountStateEntity(Short.valueOf("6"));		
		AccountStatusChangeHistoryEntity accountStatusChangeHistoryEntity = new AccountStatusChangeHistoryEntity(account.getAccountState(),accountStateEntity,center.getPersonnel());
		account.addAccountStatusChangeHistory(accountStatusChangeHistoryEntity);
		account.setAccountState(accountStateEntity);				
		TestObjectFactory.updateObject(account);			
		CustomerPersistence customerPersistence = new CustomerPersistence();				
		CustomerPerformanceHistoryView customerPerformanceHistoryView = customerPersistence.getLastLoanAmount(client.getCustomerId());		
		assertEquals("300.0",customerPerformanceHistoryView.getLastLoanAmount());
	}	
	
	public void testFindBySystemId() throws PersistenceException, ServiceException {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.1", meeting,new Date(System.currentTimeMillis()));
		group=TestObjectFactory.createGroup("Group_Active_test",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
		GroupBO groupBO = (GroupBO)customerPersistence.findBySystemId(group.getGlobalCustNum());
		assertEquals(groupBO.getDisplayName(),group.getDisplayName());
	}
	public void testGetBySystemId() throws PersistenceException, ServiceException {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.1", meeting,new Date(System.currentTimeMillis()));
		group=TestObjectFactory.createGroup("Group_Active_test",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
		GroupBO groupBO = (GroupBO)customerPersistence.getBySystemId(group.getGlobalCustNum(),group.getCustomerLevel().getId());
		assertEquals(groupBO.getDisplayName(),group.getDisplayName());
	}	
	public void testOptionalCustomerStates() throws Exception{
		assertEquals(Integer.valueOf(0).intValue(),customerPersistence.getCustomerStates(Short.valueOf("0")).size());
	}
	public void testCustomerStatesInUse() throws Exception{
		assertEquals(Integer.valueOf(14).intValue(),customerPersistence.getCustomerStates(Short.valueOf("1")).size());
	}
	
	public void testGetCustomersWithUpdatedMeetings() throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group1", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		group.getCustomerMeeting().setUpdatedFlag(YesNoFlag.YES.getValue());
		TestObjectFactory.updateObject(group);
		List<Integer> customerIds = customerPersistence.getCustomersWithUpdatedMeetings();
		assertEquals(1,customerIds.size());
		
	}
	
	private AccountBO getSavingsAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		SavingsOfferingBO savingsOffering = TestObjectFactory
		.createSavingsOffering("SavingPrd1", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short
						.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
				200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
				meetingIntCalc, meetingIntPost);
		return TestObjectFactory.createSavingsAccount("432434", customer,
				Short.valueOf("16"), startDate, savingsOffering);

	}
	
	public void testRetrieveAllLoanAccountUnderCustomer() throws PersistenceException {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		CenterBO center1 = TestObjectFactory.createCenter("Center_Active_test1", Short
				.valueOf("13"), "1.5", meeting, new Date(System
				.currentTimeMillis()));
		GroupBO group1 = TestObjectFactory.createGroup("Group1", GroupConstants.ACTIVE, "1.5.1", center1, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("client1",ClientConstants.STATUS_ACTIVE,"1.4.1.1",group,new Date(System
				.currentTimeMillis()));
		ClientBO client2 = TestObjectFactory.createClient("client2",ClientConstants.STATUS_CLOSED,"1.4.1.2",group,new Date(System
				.currentTimeMillis()));
		ClientBO client3 = TestObjectFactory.createClient("client3",ClientConstants.STATUS_CANCELLED,"1.5.1",group1,new Date(System
				.currentTimeMillis()));
		account = getLoanAccount(group, meeting);
		AccountBO account1 = getLoanAccount(client, meeting);
		AccountBO account2 = getLoanAccount(client2, meeting);
		AccountBO account3 = getLoanAccount(client3, meeting);
		AccountBO account4 = getLoanAccount(group1, meeting);
		
		List<AccountBO> loansForCenter = customerPersistence.retrieveAccountsUnderCustomer("1.4",Short.valueOf("3"),Short.valueOf("1"));
		assertEquals(3,loansForCenter.size());
		List<AccountBO> loansForGroup = customerPersistence.retrieveAccountsUnderCustomer("1.4.1",Short.valueOf("3"),Short.valueOf("1"));
		assertEquals(3,loansForGroup.size());
		List<AccountBO> loansForClient = customerPersistence.retrieveAccountsUnderCustomer("1.4.1.1",Short.valueOf("3"),Short.valueOf("1"));
		assertEquals(1,loansForClient.size());

		TestObjectFactory.cleanUp(account4);
		TestObjectFactory.cleanUp(account3);
		TestObjectFactory.cleanUp(account2);
		TestObjectFactory.cleanUp(account1);
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center1);
	}
	
	public void testRetrieveAllSavingsAccountUnderCustomer() throws PersistenceException {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		CenterBO center1 = TestObjectFactory.createCenter("Center_Active_test1", Short
				.valueOf("13"), "1.5", meeting, new Date(System
				.currentTimeMillis()));
		GroupBO group1 = TestObjectFactory.createGroup("Group1", GroupConstants.ACTIVE, "1.5.1", center1, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("client1",ClientConstants.STATUS_ACTIVE,"1.4.1.1",group,new Date(System
				.currentTimeMillis()));
		ClientBO client2 = TestObjectFactory.createClient("client2",ClientConstants.STATUS_CLOSED,"1.4.1.2",group,new Date(System
				.currentTimeMillis()));
		ClientBO client3 = TestObjectFactory.createClient("client3",ClientConstants.STATUS_CANCELLED,"1.5.1",group1,new Date(System
				.currentTimeMillis()));
		account = getSavingsAccount(center, meeting);
		AccountBO account1 = getSavingsAccount(client, meeting);
		AccountBO account2 = getSavingsAccount(client2, meeting);
		AccountBO account3 = getSavingsAccount(client3, meeting);
		AccountBO account4 = getSavingsAccount(group1, meeting);
		AccountBO account5 = getSavingsAccount(group, meeting);
		AccountBO account6 = getSavingsAccount(center1, meeting);
		
		List<AccountBO> savingsForCenter = customerPersistence.retrieveAccountsUnderCustomer("1.4",Short.valueOf("3"),Short.valueOf("2"));
		assertEquals(4,savingsForCenter.size());
		List<AccountBO> savingsForGroup = customerPersistence.retrieveAccountsUnderCustomer("1.4.1",Short.valueOf("3"),Short.valueOf("2"));
		assertEquals(3,savingsForGroup.size());
		List<AccountBO> savingsForClient = customerPersistence.retrieveAccountsUnderCustomer("1.4.1.1",Short.valueOf("3"),Short.valueOf("2"));
		assertEquals(1,savingsForClient.size());
		
		
		TestObjectFactory.cleanUp(account3);
		TestObjectFactory.cleanUp(account2);
		TestObjectFactory.cleanUp(account1);
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(account4);
		TestObjectFactory.cleanUp(account5);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(account6);
		TestObjectFactory.cleanUp(center1);
	}
	
	public void testGetAllChildrenForParent() throws NumberFormatException, PersistenceException {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		CenterBO center1 = TestObjectFactory.createCenter("Center_Active_test1", Short
				.valueOf("13"), "1.5", meeting, new Date(System
				.currentTimeMillis()));
		GroupBO group1 = TestObjectFactory.createGroup("Group1", GroupConstants.ACTIVE, "1.5.1", center1, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("client1",ClientConstants.STATUS_ACTIVE,"1.4.1.1",group,new Date(System
				.currentTimeMillis()));
		ClientBO client2 = TestObjectFactory.createClient("client2",ClientConstants.STATUS_CLOSED,"1.4.1.2",group,new Date(System
				.currentTimeMillis()));
		ClientBO client3 = TestObjectFactory.createClient("client3",ClientConstants.STATUS_CANCELLED,"1.5.1",group1,new Date(System
				.currentTimeMillis()));
		
		List<CustomerBO> customerList1 = customerPersistence.getAllChildrenForParent("1.4",Short.valueOf("3"),CustomerConstants.CENTER_LEVEL_ID);
		assertEquals(2,customerList1.size());
		List<CustomerBO> customerList2 = customerPersistence.getAllChildrenForParent("1.4",Short.valueOf("3"),CustomerConstants.GROUP_LEVEL_ID);
		assertEquals(1,customerList2.size());
		
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center1);
	}
	
	public void testGetChildrenForParent() throws NumberFormatException, SystemException, ApplicationException {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		CenterBO center1 = TestObjectFactory.createCenter("Center_Active_test1", Short
				.valueOf("13"), "1.5", meeting, new Date(System
				.currentTimeMillis()));
		GroupBO group1 = TestObjectFactory.createGroup("Group1", GroupConstants.ACTIVE, "1.5.1", center1, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("client1",ClientConstants.STATUS_ACTIVE,"1.4.1.1",group,new Date(System
				.currentTimeMillis()));
		ClientBO client2 = TestObjectFactory.createClient("client2",ClientConstants.STATUS_CLOSED,"1.4.1.2",group,new Date(System
				.currentTimeMillis()));
		ClientBO client3 = TestObjectFactory.createClient("client3",ClientConstants.STATUS_CANCELLED,"1.5.1",group1,new Date(System
				.currentTimeMillis()));
		List<Integer> customerIds = customerPersistence.getChildrenForParent("1.4",Short.valueOf("3"));
		assertEquals(3,customerIds.size());
		CustomerBO customer = (CustomerBO)TestObjectFactory.getObject(CustomerBO.class,customerIds.get(0));
		assertEquals("Group",customer.getDisplayName());
		customer = (CustomerBO)TestObjectFactory.getObject(CustomerBO.class,customerIds.get(1));
		assertEquals("client1",customer.getDisplayName());
		customer = (CustomerBO)TestObjectFactory.getObject(CustomerBO.class,customerIds.get(2));
		assertEquals("client2",customer.getDisplayName());
		
		
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center1);
	}
	
	public void testGetCustomers() throws NumberFormatException, SystemException, ApplicationException {
		CustomerPersistence customerPersistence = new CustomerPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		CenterBO center1 = TestObjectFactory.createCenter("Center_Active_test1", Short
				.valueOf("13"), "1.5", meeting, new Date(System
				.currentTimeMillis()));
		GroupBO group1 = TestObjectFactory.createGroup("Group1", GroupConstants.ACTIVE, "1.5.1", center1, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("client1",ClientConstants.STATUS_ACTIVE,"1.4.1.1",group,new Date(System
				.currentTimeMillis()));
		ClientBO client2 = TestObjectFactory.createClient("client2",ClientConstants.STATUS_CLOSED,"1.4.1.2",group,new Date(System
				.currentTimeMillis()));
		ClientBO client3 = TestObjectFactory.createClient("client3",ClientConstants.STATUS_CANCELLED,"1.5.1",group1,new Date(System
				.currentTimeMillis()));
		List<Integer> customerIds = customerPersistence.getCustomers(Short.valueOf("3"));
		assertEquals(2,customerIds.size());
	
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center1);
	}

}
