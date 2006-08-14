package org.mifos.application.customer.business;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanPerformanceHistoryEntity;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.exception.StateChangeException;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerBO extends MifosTestCase {
	private AccountBO accountBO;
	private CenterBO center;
	private GroupBO group;
	private ClientBO client;
	private CustomerPersistence customerPersistence;
	private MeetingBO meeting;
	private SavingsTestHelper helper = new SavingsTestHelper();
	private SavingsOfferingBO savingsOffering;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		customerPersistence = new CustomerPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
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
	
	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer, Short
				.valueOf("5"), startDate, loanOffering);

	}
	
	public void testGroupPerfObject() throws PersistenceException {
		createInitialObjects();
		GroupPerformanceHistoryEntity groupPerformanceHistory = new GroupPerformanceHistoryEntity();
		groupPerformanceHistory.setClientCount(Integer.valueOf("1"));
		groupPerformanceHistory.setLastGroupLoanAmount(new Money("100"));
		groupPerformanceHistory.setPortfolioAtRisk(new Money("100"));
		groupPerformanceHistory.setGroup(group);
		group.setPerformanceHistory(groupPerformanceHistory);
		TestObjectFactory.updateObject(group);
		group = (GroupBO)customerPersistence.getBySystemId(group.getGlobalCustNum(),group.getCustomerLevel().getId());
		assertEquals(group.getCustomerId(),group.getPerformanceHistory().getGroup().getCustomerId());
		assertEquals(Integer.valueOf("1"),group.getPerformanceHistory().getClientCount());
		assertEquals(new Money("100"),group.getPerformanceHistory().getLastGroupLoanAmount());
	}
	
	public void testClientPerfObject() throws PersistenceException {
		createInitialObjects();
		ClientPerformanceHistoryEntity clientPerformanceHistory = ((ClientBO)client).getPerformanceHistory();
		clientPerformanceHistory.setLoanCycleNumber(Integer.valueOf("1"));
		clientPerformanceHistory.setNoOfActiveLoans(Integer.valueOf("1"));
		clientPerformanceHistory.setLastLoanAmount(new Money("100"));
		TestObjectFactory.updateObject(client);
		client = (ClientBO)customerPersistence.getBySystemId(client.getGlobalCustNum(),client.getCustomerLevel().getId());
		assertEquals(client.getCustomerId(),client.getPerformanceHistory().getClient().getCustomerId());
		assertEquals(Integer.valueOf("1"),client.getPerformanceHistory().getLoanCycleNumber());
		assertEquals(new Money("100"),client.getPerformanceHistory().getLastLoanAmount());
		assertEquals(new Money("0"),client.getPerformanceHistory().getDelinquentPortfolioAmount());
	}
	
	public void testLoanPerfObject() throws PersistenceException {
		Date currentDate = new Date(System.currentTimeMillis());
		createInitialObjects();
		accountBO = getLoanAccount(client,meeting);
		LoanBO loanBO = (LoanBO)accountBO;
		LoanPerformanceHistoryEntity loanPerformanceHistory = loanBO.getPerformanceHistory();
		loanPerformanceHistory.setNoOfPayments(Integer.valueOf("3"));
		loanPerformanceHistory.setLoanMaturityDate(currentDate);
		TestObjectFactory.updateObject(loanBO);
		loanBO = (LoanBO) new AccountPersistanceService().getAccount(loanBO.getAccountId());
		assertEquals(Integer.valueOf("3"),loanBO.getPerformanceHistory().getNoOfPayments());
		assertEquals(currentDate,loanBO.getPerformanceHistory().getLoanMaturityDate());
	}

	

	public void testGetBalanceForAccountsAtRisk() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group,meeting);
		TestObjectFactory.flushandCloseSession();
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		assertEquals(new Money(),group.getBalanceForAccountsAtRisk());
		changeFirstInstallmentDate(accountBO,31);
		assertEquals(new Money("300"),group.getBalanceForAccountsAtRisk());
		TestObjectFactory.flushandCloseSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
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
	
	private void setActionDateToPastDate() {
		Calendar calendar = new GregorianCalendar();
        calendar.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
        calendar.add(calendar.WEEK_OF_MONTH,-1);
        java.sql.Date lastWeekDate = new java.sql.Date(calendar.getTimeInMillis());        
        
        Calendar date = new GregorianCalendar();
        date.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
        date.add(date.WEEK_OF_MONTH,-2);
        java.sql.Date twoWeeksBeforeDate = new java.sql.Date(date.getTimeInMillis());
        
        
		for(AccountActionDateEntity installment : accountBO.getAccountActionDates()){
			if(installment.getInstallmentId().intValue()==1){
				installment.setActionDate(lastWeekDate);
			}
			else if(installment.getInstallmentId().intValue()==2){
				installment.setActionDate(twoWeeksBeforeDate);
			}
		}
	}
	
	public void testGetDelinquentPortfolioAmount() {
		createInitialObjects();
		accountBO = getLoanAccount(client,meeting);
		TestObjectFactory.flushandCloseSession();
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		setActionDateToPastDate();
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		client=(ClientBO)TestObjectFactory.getObject(CustomerBO.class,client.getCustomerId());
		assertEquals(new Money("0.7"),client.getDelinquentPortfolioAmount());
		TestObjectFactory.flushandCloseSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
	}
	
	public void testGetOutstandingLoanAmount() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group,meeting);
		TestObjectFactory.flushandCloseSession();
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		assertEquals(new Money("300.0"),group.getOutstandingLoanAmount());
		TestObjectFactory.flushandCloseSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
	}
	
	public void testGetActiveLoanCounts() throws PersistenceException {
		createInitialObjects();
		accountBO = getLoanAccount(group,meeting);
		TestObjectFactory.flushandCloseSession();
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		assertEquals(1,group.getActiveLoanCounts().intValue());
		TestObjectFactory.flushandCloseSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
	}
	
	private SavingsBO getSavingsAccount() {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		return TestObjectFactory.createSavingsAccount("000100000000017",
				client, AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}
	
	public void testgetSavingsBalance() throws Exception {
		SavingsBO savings = getSavingsAccount();
		savings.setSavingsBalance(new Money("1000"));
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings=(SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savings.getAccountId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		assertEquals(new Money("1000.0"), savings.getSavingsBalance());
		assertEquals(new Money("1000.0"), client.getSavingsBalance());
		TestObjectFactory.flushandCloseSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group=(GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		client=(ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		savings=(SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savings.getAccountId());
		TestObjectFactory.cleanUp(savings);
	}
	
	public void testValidateStatusWithActiveGroups() throws Exception {
		createInitialObjects();
		Short newStatusId= CustomerStatus.CENTER_INACTIVE.getValue();
		try{
			center.changeStatus(newStatusId,null,"Test");
			//assertFalse(true);
		}
		catch(StateChangeException sce){
			assertTrue(true);
			assertEquals(sce.getKey(),CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION);
		}
		//TestObjectFactory.cleanUp(savings);
	}
}
