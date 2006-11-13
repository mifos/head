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
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
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
				.getMeetingHelper(1, 1, 4, 2));
		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, startDate);
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

	public void testexecute() throws Exception {
		int statusChangeHistorySize = loanAccount
				.getAccountStatusChangeHistory().size();
		loanArrearTask.run();
		Query query = HibernateUtil.getSessionTL().createQuery(
				"from org.mifos.framework.components.cronjobs.business.Task");
		List<Task> tasks = query.list();
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		for (Task task : tasks) {
			assertEquals(TaskStatus.COMPLETE.getValue().shortValue(), task
					.getStatus());
			assertEquals(SchedulerConstants.FINISHEDSUCCESSFULLY, task
					.getDescription());
			TestObjectFactory.removeObject(task);
		}
		loanAccount = new AccountPersistence().getAccount(loanAccount
				.getAccountId());
		assertEquals(Short.valueOf(AccountStates.LOANACC_BADSTANDING),
				loanAccount.getAccountState().getId());
		assertEquals(statusChangeHistorySize + 1, loanAccount
				.getAccountStatusChangeHistory().size());
	}

	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting)
			throws AccountException {
		Date currentdate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), currentdate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
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
					actionDate.getActionDate(), 15));
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
