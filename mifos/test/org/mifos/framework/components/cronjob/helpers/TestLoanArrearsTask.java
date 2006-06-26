package org.mifos.framework.components.cronjob.helpers;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.components.cronjobs.helpers.LoanArrearsTask;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.TestObjectFactory;

import junit.framework.TestCase;

public class TestLoanArrearsTask extends TestCase{
	
	private LoanArrearsTask loanArrearTask;
	
	CustomerBO center = null;

	CustomerBO group = null;
	
	MeetingBO meeting = null;
	
	AccountBO loanAccount = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		loanArrearTask = new LoanArrearsTask();
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
		loanArrearTask = null;
		HibernateUtil.closeSession();
		}
	
	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Calendar actionDate = new GregorianCalendar();
		int year = actionDate.get(Calendar.YEAR);
		int month = actionDate.get(Calendar.MONTH);
		int day = actionDate.get(Calendar.DAY_OF_MONTH);
		actionDate = new GregorianCalendar(year, month, day-15);
		Date startDate = new Date(actionDate.getTimeInMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				Short.valueOf("5"), startDate, loanOffering);

	}
	
	public void testexecute() throws Exception{
		
		
		int statusChangeHistorySize=loanAccount.getAccountStatusChangeHistory().size();
		
		loanArrearTask.run();
		
		loanAccount = new AccountPersistanceService().getAccount(loanAccount.getAccountId());
		
		assertEquals(Short.valueOf(AccountStates.LOANACC_BADSTANDING),loanAccount.getAccountState().getId());
		assertEquals(statusChangeHistorySize+1,loanAccount.accountStatusChangeHistory.size());
		
		
		}



}
