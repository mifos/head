package org.mifos.application.customer.group;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestGroupBO extends MifosTestCase {
	
	protected AccountBO account1 = null;
	
	protected AccountBO account2 = null;

	protected CenterBO center = null;

	protected GroupBO group = null;
	protected GroupBO group1 = null;
	protected ClientBO client = null;
	protected ClientBO client1 = null;
	protected ClientBO client2 = null;

	private SavingsTestHelper helper = new SavingsTestHelper();
	
	private SavingsOfferingBO savingsOffering;
	
	private MeetingBO meeting;

	private Short officeId = 3;
	
	private Short personnelId = 3;
	
	private OfficeBO office;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}


	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(account2);
		TestObjectFactory.cleanUp(account1);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(office);
		HibernateUtil.closeSession();
		super.tearDown();
	}


	public void testGeneratePortfolioAtRisk() throws SchedulerException, HibernateException, ServiceException, PersistenceException{
		createInitialObject();
		TestObjectFactory.flushandCloseSession();
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = new GroupPerformanceHistoryEntity(
				0, new Money(), new Money(), ((LoanBO)account1).getLoanSummary().getOriginalPrincipal().add(((LoanBO)account2).getLoanSummary().getOriginalPrincipal()),
				new Money(), new Money());
		((GroupBO)group).setPerformanceHistory(groupPerformanceHistoryEntity);
		TestObjectFactory.updateObject(group);
		TestObjectFactory.flushandCloseSession();
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		for(AccountBO account : group.getAccounts()){
			if(account.getAccountType().getAccountTypeId().equals(AccountTypes.LOANACCOUNT.getValue())){
				changeFirstInstallmentDate(account,31);
			}
		}
		for(AccountBO account : client.getAccounts()){
			if(account.getAccountType().getAccountTypeId().equals(AccountTypes.LOANACCOUNT.getValue())){
				changeFirstInstallmentDate(account,31);
			}
		}
		group.generatePortfolioAtRisk();
		assertEquals(new Money("1.0"),((GroupBO)group).getPerformanceHistory().getPortfolioAtRisk());
		TestObjectFactory.flushandCloseSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		account1=(AccountBO)TestObjectFactory.getObject(AccountBO.class,account1.getAccountId());
		account2=(AccountBO)TestObjectFactory.getObject(AccountBO.class,account2.getAccountId());
	}
	
	public void testGetTotalOutStandingLoanAmount() throws SchedulerException, HibernateException, ServiceException, PersistenceException{
		createInitialObject();
		GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = new GroupPerformanceHistoryEntity(
				0, new Money(), new Money(), ((LoanBO)account1).getLoanSummary().getOriginalPrincipal().add(((LoanBO)account2).getLoanSummary().getOriginalPrincipal()),
				new Money(), new Money());
		((GroupBO)group).setPerformanceHistory(groupPerformanceHistoryEntity);
		TestObjectFactory.updateObject(group);
		TestObjectFactory.flushandCloseSession();
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		assertEquals(new Money("600.0"),((GroupBO)group).getTotalOutStandingLoanAmount());
		assertEquals(new Money("600.0"),((GroupBO)group).getPerformanceHistory().getTotalOutStandingLoanAmount());
		TestObjectFactory.flushandCloseSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		account1=(AccountBO)TestObjectFactory.getObject(AccountBO.class,account1.getAccountId());
		account2=(AccountBO)TestObjectFactory.getObject(AccountBO.class,account2.getAccountId());
	}
	
	public void testGetAverageLoanAmount() throws SchedulerException, HibernateException, ServiceException, PersistenceException{
		createInitialObject();
		GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = new GroupPerformanceHistoryEntity(
				0, new Money(), new Money(), ((LoanBO)account1).getLoanSummary().getOriginalPrincipal().add(((LoanBO)account2).getLoanSummary().getOriginalPrincipal()),
				new Money(), new Money());
		((GroupBO)group).setPerformanceHistory(groupPerformanceHistoryEntity);
		TestObjectFactory.updateObject(group);
		TestObjectFactory.flushandCloseSession();
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		assertEquals(new Money("300.0"),((GroupBO)group).getAverageLoanAmount());
		assertEquals(new Money("300.0"),((GroupBO)group).getPerformanceHistory().getAvgLoanAmountForMember());
		TestObjectFactory.flushandCloseSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		account1=(AccountBO)TestObjectFactory.getObject(AccountBO.class,account1.getAccountId());
		account2=(AccountBO)TestObjectFactory.getObject(AccountBO.class,account2.getAccountId());
	}


	private void createInitialObject() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				center.getSearchId()+".1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",ClientConstants.STATUS_ACTIVE,group.getSearchId()+".1",group,new Date(System
				.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		account1= TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("1"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		account2= TestObjectFactory.createLoanAccount("42427777341", client, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}
	
	private void changeFirstInstallmentDate(AccountBO accountBO,int numberOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - numberOfDays);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			accountActionDateEntity.setActionDate(new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
			break;
		}
	}
	
	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",ClientConstants.STATUS_ACTIVE,"1.4.1.1",group,new Date(System
				.currentTimeMillis()));
	}
	
	private SavingsBO getSavingsAccount(CustomerBO customerBO) {
		savingsOffering = helper.createSavingsOffering();
		return TestObjectFactory.createSavingsAccount("000100000000017",
				customerBO, AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}
	
	public void testGetTotalSavingsBalance() throws Exception {
		createInitialObjects();
		SavingsBO savings1 = getSavingsAccount(group);
		savings1.setSavingsBalance(new Money("1000"));
		savings1.update();
		SavingsBO savings2 = getSavingsAccount(client);
		savings2.setSavingsBalance(new Money("2000"));
		savings1.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings1=(SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savings1.getAccountId());
		savings2=(SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savings2.getAccountId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		assertEquals(new Money("1000.0"), savings1.getSavingsBalance());
		assertEquals(new Money("2000.0"), savings2.getSavingsBalance());
		assertEquals(new Money("2000.0"), ((ClientBO)client).getSavingsBalance());
		assertEquals(new Money("3000.0"), ((GroupBO)group).getTotalSavingsBalance());
		TestObjectFactory.flushandCloseSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		savings1=(SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savings1.getAccountId());
		savings2=(SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savings2.getAccountId());
		TestObjectFactory.cleanUp(savings1);
		TestObjectFactory.cleanUp(savings2);
	}
	public void testGetActiveOnHoldChildrenOfGroup() throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("client1",ClientConstants.STATUS_ACTIVE,"1.4.1.1",group,new Date(System
				.currentTimeMillis()));
		client1 = TestObjectFactory.createClient("client2",ClientConstants.STATUS_HOLD,"1.4.1.2",group,new Date(System
				.currentTimeMillis()));
		client2 = TestObjectFactory.createClient("client3",ClientConstants.STATUS_CANCELLED,"1.4.1.3",group,new Date(System
				.currentTimeMillis()));
		assertEquals(Integer.valueOf("2") , ((GroupBO)group).getActiveOnHoldChildrenOfGroup());
	}
	
	public void testUpdateBranchFailure_OfficeNULL()throws Exception{
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
		try{
			group.transferToBranch(null);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.INVALID_OFFICE,ce.getKey());
		}
	}
	
	public void testUpdateBranchFailure_TransferInSameOffice()throws Exception{
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
		try{
			group.transferToBranch(group.getOffice());
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER,ce.getKey());
		}
	}
	
	public void testUpdateBranchFailure_DuplicateGroupName()throws Exception{
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
		office = createOffice();
		group1 = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE, office.getOfficeId());
		try{
			group.transferToBranch(office);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER,ce.getKey());
		}
	}
	
	public void testUpdateBranchFailure_GroupHasActiveAccount()throws Exception{
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
		account1 = getSavingsAccount(group);
		office = createOffice();
		HibernateUtil.closeSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		try{
			group.transferToBranch(office);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_HAS_ACTIVE_ACCOUNT,ce.getKey());
		}
		HibernateUtil.closeSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		account1 = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
		office = new OfficePersistence().getOffice(office.getOfficeId());
	}
	
	public void testUpdateBranchFailure_GroupChildrenHasActiveAccount()throws Exception{
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
		client = createClient(group, CustomerStatus.CLIENT_ACTIVE);
		account1 = getSavingsAccount(client);
		office = createOffice();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		
		try{
			group.transferToBranch(office);
			assertTrue(false);
		}catch(CustomerException ce){
			assertTrue(true);
			assertEquals(CustomerConstants.ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT,ce.getKey());
		}
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		account1 = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
		office = new OfficePersistence().getOffice(office.getOfficeId());
	}
	
	public void testSuccessfulTransferToBranch()throws Exception{
		group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE);
		client = createClient(group, CustomerStatus.CLIENT_ACTIVE);
		client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		client2 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
		office = createOffice();
		client2.changeStatus(CustomerStatus.CLIENT_CLOSED.getValue(), Short.valueOf("6"),"comment");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		group.setUserContext(TestObjectFactory.getUserContext());
		assertNull(client.getActiveCustomerMovement());
		
		group.transferToBranch(office);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
		client1 = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client1.getCustomerId());
		client2 = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client2.getCustomerId());
		office = new OfficePersistence().getOffice(office.getOfficeId());
		assertNotNull(group.getActiveCustomerMovement());
		assertNotNull(client.getActiveCustomerMovement());
		assertNotNull(client1.getActiveCustomerMovement());
		assertNotNull(client2.getActiveCustomerMovement());
		
		assertEquals(office.getOfficeId(), group.getOffice().getOfficeId());
		assertEquals(office.getOfficeId(), client.getOffice().getOfficeId());
		assertEquals(office.getOfficeId(), client1.getOffice().getOfficeId());
		assertEquals(office.getOfficeId(), client2.getOffice().getOfficeId());
		
		assertEquals(CustomerStatus.GROUP_HOLD, group.getStatus());
		assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
		assertEquals(CustomerStatus.CLIENT_PARTIAL, client1.getStatus());
		assertEquals(CustomerStatus.CLIENT_CLOSED, client2.getStatus());
		
		assertNull(group.getPersonnel());
		assertNull(client.getPersonnel());
		assertNull(client1.getPersonnel());
		assertNull(client2.getPersonnel());	
	}
	
	private GroupBO createGroupUnderBranch(CustomerStatus groupStatus){
		return createGroupUnderBranch(groupStatus, officeId);
	}
	
	private GroupBO createGroupUnderBranch(CustomerStatus groupStatus, Short officeId){
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createGroupUnderBranch("group1",groupStatus, officeId, meeting, personnelId);
	}
	
	private ClientBO createClient(GroupBO group, CustomerStatus clientStatus){
		return TestObjectFactory.createClient("client1", clientStatus.getValue(), group, new Date());
	}
	
	private OfficeBO createOffice()throws Exception{
		return TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory.getOffice(Short.valueOf("1")), "customer_office", "cust");
	}
}
