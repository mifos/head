package org.mifos.framework.components.cronjob.helpers;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.mifos.framework.MifosTestCase;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.components.cronjobs.helpers.LoanArrearsHelper;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanArrearsHelper extends MifosTestCase{
	
	private LoanArrearsHelper loanArrearHelper;
	
	CustomerBO center = null;

	CustomerBO group = null;
	
	MeetingBO meeting = null;
	
	AccountBO loanAccount = null;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		loanArrearHelper = new LoanArrearsHelper();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, startDate);
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, startDate);
		
		loanAccount = getLoanAccount(group, meeting);
		}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		
		TestObjectFactory.cleanUp(loanAccount);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		loanArrearHelper = null;
		HibernateUtil.closeSession();
		}
	
	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) throws AccountException {
		Date currentdate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), currentdate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		loanAccount = TestObjectFactory.createLoanAccount("42423142341", customer,
				Short.valueOf("5"), currentdate, loanOffering);
		setDisbursementDateAsOldDate(loanAccount);
		loanAccount.update();
		HibernateUtil.commitTransaction();
		
		return loanAccount;

	}
	
	public void testexecute() throws Exception{
		
		
		int statusChangeHistorySize=loanAccount.getAccountStatusChangeHistory().size();
		
		loanArrearHelper.execute(System.currentTimeMillis());
		
		loanAccount = new AccountPersistanceService().getAccount(loanAccount.getAccountId());
		
		assertEquals(Short.valueOf(AccountStates.LOANACC_BADSTANDING),loanAccount.getAccountState().getId());
		assertEquals(statusChangeHistorySize+1,loanAccount.getAccountStatusChangeHistory().size());
				
		}

	private void setDisbursementDateAsOldDate(AccountBO account) {
		Date startDate = offSetCurrentDate(15);
		LoanBO loan = (LoanBO) account;
		loan.setDisbursementDate(startDate);
		for(AccountActionDateEntity actionDate : loan.getAccountActionDates())
			actionDate.setActionDate(offSetGivenDate(actionDate.getActionDate(),15));
	}
	
	private java.sql.Date offSetGivenDate(Date date,int numberOfDays) {
		Calendar dateCalendar = new GregorianCalendar();
		dateCalendar.setTimeInMillis(date.getTime());
		int year = dateCalendar.get(Calendar.YEAR);
		int month = dateCalendar.get(Calendar.MONTH);
		int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
		dateCalendar = new GregorianCalendar(year, month, day- numberOfDays);
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
