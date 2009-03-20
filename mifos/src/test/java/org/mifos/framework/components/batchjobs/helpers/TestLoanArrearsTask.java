/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.framework.components.batchjobs.helpers;

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
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanArrearsTask extends MifosIntegrationTest {

	public TestLoanArrearsTask() throws SystemException, ApplicationException {
        super();
    }

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
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testExecute() throws Exception {
		int statusChangeHistorySize = loanAccount
				.getAccountStatusChangeHistory().size();
		loanArrearTask.run();
		Query query = StaticHibernateUtil.getSessionTL().createQuery(
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
		assertEquals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING,
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
				customer, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				currentdate, loanOffering);
		setDisbursementDateAsOldDate(loanAccount);
		loanAccount.update();
		StaticHibernateUtil.commitTransaction();
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
