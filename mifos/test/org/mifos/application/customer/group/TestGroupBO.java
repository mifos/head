package org.mifos.application.customer.group;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
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

	protected CustomerBO center = null;

	protected CustomerBO group = null;
	
	protected CustomerBO client = null;

	

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}


	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(account2);
		TestObjectFactory.cleanUp(account1);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}


	public void testGeneratePortfolioAtRisk() throws SchedulerException, HibernateException, ServiceException, PersistenceException{
		createInitialObject();
		TestObjectFactory.flushandCloseSession();
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		client=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client.getCustomerId());
		GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = new GroupPerformanceHistoryEntity(
				0, new Money(), new Money(), ((LoanBO)account1).getLoanSummary().getOriginalPrincipal().add(((LoanBO)account2).getLoanSummary().getOriginalPrincipal()),
				new Money(), new Money());
		((GroupBO)group).setPerformanceHistory(groupPerformanceHistoryEntity);
		TestObjectFactory.updateObject(group);
		TestObjectFactory.flushandCloseSession();
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		client=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client.getCustomerId());
		for(AccountBO account : group.getAccounts()){
			if(account.getAccountType().getAccountTypeId().equals(AccountConstants.LOAN_TYPE)){
				changeFirstInstallmentDate(account,31);
			}
		}
		for(AccountBO account : client.getAccounts()){
			if(account.getAccountType().getAccountTypeId().equals(AccountConstants.LOAN_TYPE)){
				changeFirstInstallmentDate(account,31);
			}
		}
		group.generatePortfolioAtRisk();
		assertEquals(new Money("1.0"),((GroupBO)group).getPerformanceHistory().getPortfolioAtRisk());
		TestObjectFactory.flushandCloseSession();
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		client=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client.getCustomerId());
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
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		assertEquals(new Money("600.0"),((GroupBO)group).getTotalOutStandingLoanAmount());
		assertEquals(new Money("600.0"),((GroupBO)group).getPerformanceHistory().getTotalOutStandingLoanAmount());
		TestObjectFactory.flushandCloseSession();
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		client=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client.getCustomerId());
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
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		assertEquals(new Money("1.0"),((GroupBO)group).getAverageLoanAmount());
		assertEquals(new Money("1.0"),((GroupBO)group).getPerformanceHistory().getAvgLoanAmountForMember());
		TestObjectFactory.flushandCloseSession();
		center=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		group=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		client=(CustomerBO)TestObjectFactory.getObject(CustomerBO.class,client.getCustomerId());
		account1=(AccountBO)TestObjectFactory.getObject(AccountBO.class,account1.getAccountId());
		account2=(AccountBO)TestObjectFactory.getObject(AccountBO.class,account2.getAccountId());
	}


	private void createInitialObject() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",ClientConstants.STATUS_ACTIVE,"1.1.1.1",group,new Date(System
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
}
