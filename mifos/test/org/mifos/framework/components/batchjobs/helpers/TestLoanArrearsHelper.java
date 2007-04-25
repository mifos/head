package org.mifos.framework.components.batchjobs.helpers;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.batchjobs.helpers.LoanArrearsHelper;
import org.mifos.framework.components.batchjobs.helpers.LoanArrearsTask;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanArrearsHelper extends MifosTestCase {

	private LoanArrearsHelper loanArrearHelper;

	CustomerBO center = null;

	CustomerBO group = null;

	MeetingBO meeting = null;

	AccountBO loanAccount = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		LoanArrearsTask arrearsTask = new LoanArrearsTask();
		loanArrearHelper = (LoanArrearsHelper)arrearsTask.getTaskHelper();
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
		loanArrearHelper = null;
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testExecute() throws Exception {
		int statusChangeHistorySize = loanAccount
				.getAccountStatusChangeHistory().size();
		loanArrearHelper.execute(System.currentTimeMillis());
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
				currentdate, meeting);
		loanAccount = TestObjectFactory.createLoanAccount("42423142341",
				customer, AccountState.LOANACC_ACTIVEINGOODSTANDING, 
				currentdate, loanOffering);
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
