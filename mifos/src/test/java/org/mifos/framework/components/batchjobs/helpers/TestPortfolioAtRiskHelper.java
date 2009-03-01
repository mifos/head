package org.mifos.framework.components.batchjobs.helpers;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.hibernate.Session;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.components.batchjobs.persistence.TaskPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPortfolioAtRiskHelper extends MifosTestCase {

	public TestPortfolioAtRiskHelper() throws SystemException, ApplicationException {
        super();
    }

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
		try {
			TestObjectFactory.cleanUp(account2);
			TestObjectFactory.cleanUp(account1);
			TestObjectFactory.cleanUp(client);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(center);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	// PortfolioAtRisk needs this LoanArrearsTask to run successfully first
	private Task insertLoanArrearsTask() throws Exception
	{
		Task task = new Task();
		task.setCreatedBy((short) 1);
		task.setCreatedDate(new Date(System.currentTimeMillis()));
		task.setDescription(SchedulerConstants.FINISHED_SUCCESSFULLY);
		task.setStartTime(new Timestamp(System.currentTimeMillis()));
		task.setEndTime(new Timestamp(System.currentTimeMillis()));
		task.setStatus(TaskStatus.COMPLETE.getValue());
		task.setTask("LoanArrearsTask");
		
		TaskPersistence p = new TaskPersistence();
		p.saveAndCommitTask(task);
		return task;
		
	}
	

	public void testExecute() throws Exception {
		Task task = insertLoanArrearsTask();
		createInitialObject();

		group = TestObjectFactory.getCustomer(group.getCustomerId());
		client = TestObjectFactory.getCustomer(client.getCustomerId());
		for (AccountBO account : group.getAccounts()) {
			if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
				changeFirstInstallmentDate(account, 7);
				((LoanBO)account).handleArrears();
			}
		}
		for (AccountBO account : client.getAccounts()) {
			if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
				changeFirstInstallmentDate(account, 7);
				((LoanBO)account).handleArrears();
			}
		}
		TestObjectFactory.updateObject(client);
		TestObjectFactory.updateObject(group);
		TestObjectFactory.flushandCloseSession();

		PortfolioAtRiskTask portfolioAtRiskTask = new PortfolioAtRiskTask();
		PortfolioAtRiskHelper portfolioAtRiskHelper = (PortfolioAtRiskHelper) portfolioAtRiskTask
				.getTaskHelper();
		portfolioAtRiskHelper.execute(System.currentTimeMillis());
		//Session session = HibernateUtil.getSessionTL();
		//session.delete(task);
		TestObjectFactory.removeObject(task);
		
		HibernateUtil.closeSession();
		center = TestObjectFactory.getCustomer(center.getCustomerId());
		group = TestObjectFactory.getCustomer(group.getCustomerId());
		client = TestObjectFactory.getCustomer(client.getCustomerId());
		account1 = TestObjectFactory.getObject(AccountBO.class,
				account1.getAccountId());
		account2 = TestObjectFactory.getObject(AccountBO.class,
				account2.getAccountId());
	}

	private void createInitialObject() {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "LOAN", startDate, meeting);
		account1 = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
		loanOffering = TestObjectFactory.createLoanOffering("Loan123", "LOAP",
				ApplicableTo.CLIENTS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, 
				InterestType.FLAT, meeting);
		account2 = TestObjectFactory.createLoanAccount("42427777341", client,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
	}

	private void changeFirstInstallmentDate(AccountBO accountBO,
			int numberOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day
				- numberOfDays);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			TestLoanBO.setActionDate(accountActionDateEntity,new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
			break;
		}
	}
}
