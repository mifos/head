package org.mifos.framework.components.cronjob.helpers;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Query;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.SchedulerConstants;
import org.mifos.framework.components.cronjobs.business.Task;
import org.mifos.framework.components.cronjobs.helpers.LoanArrearsTask;
import org.mifos.framework.components.cronjobs.helpers.TaskStatus;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanArrearsTask extends MifosTestCase {

	private LoanArrearsTask loanArrearTask;

	CustomerBO center = null;

	CustomerBO group = null;

	MeetingBO meeting = null;

	AccountBO loanAccount = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		loanArrearTask = new LoanArrearsTask();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		loanAccount = getLoanAccount(group, meeting);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loanAccount);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		loanArrearTask = null;
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testExecute() throws Exception {
		int statusChangeHistorySize = loanAccount
				.getAccountStatusChangeHistory().size();
		loanArrearTask.run();
		Query query = HibernateUtil.getSessionTL().createQuery(
				"from " + Task.class.getName());
		List<Task> tasks = query.list();
		assertEquals(1, tasks.size());
		
		Task task = tasks.get(0);
		assertEquals(TaskStatus.COMPLETE, task.getStatusEnum());
		assertEquals(SchedulerConstants.FINISHED_SUCCESSFULLY, task
				.getDescription());
		TestObjectFactory.removeObject(task);

		loanAccount = new AccountPersistence().getAccount(loanAccount
				.getAccountId());
		assertEquals(AccountState.LOANACC_BADSTANDING,
				loanAccount.getState());
		assertEquals(statusChangeHistorySize + 1, loanAccount
				.getAccountStatusChangeHistory().size());
	}

	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting)
			throws AccountException {
		Date currentdate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "L", PrdApplicableMaster.GROUPS, currentdate, 
				PrdStatus.LOANACTIVE.getValue(),
				300.0, 1.2, 
				(short)3, 
				InterestType.FLAT, 
				true, true, meeting,
				GraceType.GRACEONALLREPAYMENTS);
		loanAccount = TestObjectFactory.createLoanAccount("42423142341",
				customer, Short.valueOf("5"), currentdate, loanOffering);
		setDisbursementDateAsOldDate(loanAccount);
		loanAccount.update();
		HibernateUtil.commitTransaction();
		return loanAccount;
	}

	private void setDisbursementDateAsOldDate(AccountBO account) {
		Date startDate = offSetCurrentDate(15);
		LoanBO loan = (LoanBO) account;
		TestLoanBO.modifyDisbursmentDate(loan,startDate);
		for (AccountActionDateEntity actionDate : loan.getAccountActionDates())
			TestLoanBO.setActionDate(actionDate,offSetGivenDate(
					actionDate.getActionDate(), 18));
	}

	private java.sql.Date offSetGivenDate(Date date, int numberOfDays) {
		Calendar dateCalendar = new GregorianCalendar();
		dateCalendar.setTimeInMillis(date.getTime());
		int year = dateCalendar.get(Calendar.YEAR);
		int month = dateCalendar.get(Calendar.MONTH);
		int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
		dateCalendar = new GregorianCalendar(year, month, day - numberOfDays);
		return new java.sql.Date(dateCalendar.getTimeInMillis());
	}

	private java.sql.Date offSetCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}
}
