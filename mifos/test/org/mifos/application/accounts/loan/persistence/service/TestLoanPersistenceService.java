package org.mifos.application.accounts.loan.persistence.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.LoanAccountView;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.persistance.service.LoanPersistenceService;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanPersistenceService extends TestCase {

	LoanPersistenceService loanPersistenceService;

	CustomerBO center = null;

	CustomerBO group = null;
	
	MeetingBO meeting = null;

	AccountBO loanAccount = null;
	
	public void setUp() throws Exception {
		super.setUp();
		loanPersistenceService = new LoanPersistenceService();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, startDate);
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, startDate);
		loanAccount = getLoanAccount(group, meeting);
		
	}
	
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loanAccount);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}

	public void testFindBySystemId()throws Exception{			
		LoanPersistenceService loanPersistenceService=new LoanPersistenceService();
		LoanBO loanBO=loanPersistenceService.findBySystemId(loanAccount.getGlobalAccountNum());
		assertEquals(loanBO.getGlobalAccountNum(),loanAccount.getGlobalAccountNum());
		assertEquals(loanBO.getAccountId(),loanAccount.getAccountId());		
	}
	
	public void testGetLoanAccountsForCustomer() {

		List<LoanAccountView> loanAccounts = loanPersistenceService
				.getLoanAccountsForCustomer(group.getCustomerId(),new Date(System.currentTimeMillis()));
		assertEquals(1, loanAccounts.size());

	}

	public void testGetLoanAccountTransactionDetail() {

		Date transactionDate = new Date(System.currentTimeMillis());
		List<AccountActionDateEntity> details = loanPersistenceService
				.getTransactionDetailForLoanAccount(loanAccount.getAccountId(),
						transactionDate);
		assertEquals(1, details.size());
	}

	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				Short.valueOf("5"), startDate, loanOffering);

	}
	public void testGetLoanAccountsInArrears()
	{
		Short latenessDays = 1;
		Calendar actionDate = new GregorianCalendar();
		int year = actionDate.get(Calendar.YEAR);
		int month = actionDate.get(Calendar.MONTH);
		int day = actionDate.get(Calendar.DAY_OF_MONTH);
		actionDate = new GregorianCalendar(year, month, day-latenessDays);
				
		Date date = new Date(actionDate.getTimeInMillis());
		List<LoanBO> list;
		try {
			
			Calendar checkDate = new GregorianCalendar(year, month, day-15);
			Date startDate = new Date(checkDate.getTimeInMillis());
			for(AccountActionDateEntity accountAction : loanAccount.getAccountActionDates()) {
				accountAction.setActionDate(startDate);
				}
			new AccountPersistanceService().updateAccount(loanAccount);
			loanAccount = new AccountPersistanceService().getAccount(loanAccount.getAccountId());
			list = loanPersistenceService.getLoanAccountsInArrears(latenessDays);
			assertNotNull(list);
			LoanBO testBO = (LoanBO) list.get(0);
			assertEquals("42423142341",testBO.getGlobalAccountNum());
			assertEquals(Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING),testBO.getAccountState().getId());
			
			// Get the first action date i.e for the first Installment
			AccountActionDateEntity actionDates = testBO.getAccountActionDate(Short.valueOf("1"));
			
			// assert that the date comes after the action date
			assertTrue(date.after(actionDates.getActionDate()));
			
			//assert that the payment status in 0 - unpaid
			assertEquals(AccountConstants.PAYMENT_UNPAID,actionDates.getPaymentStatus());
			
			
			
			} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
	
	}

}
