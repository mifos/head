package org.mifos.application.accounts.loan.persistence;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.framework.MifosTestCase;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.LoanAccountView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanPersistence extends MifosTestCase {
		
	LoanPersistance loanPersistence;

	CustomerBO center = null;

	CustomerBO group = null;
	
	MeetingBO meeting = null;
	
	AccountBO loanAccount = null;
	
	AccountBO loanAccountForDisbursement = null;
	

	public void setUp() throws Exception {
		super.setUp();
		loanPersistence = new LoanPersistance();
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
		TestObjectFactory.cleanUp(loanAccountForDisbursement);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}

	public void testFindBySystemId()throws Exception{			
		LoanPersistance loanPersistance=new LoanPersistance();
		LoanBO loanBO=loanPersistance.findBySystemId(loanAccount.getGlobalAccountNum());
		assertEquals(loanBO.getGlobalAccountNum(),loanAccount.getGlobalAccountNum());
		assertEquals(loanBO.getAccountId(),loanAccount.getAccountId());		
	}
	
	
	public void testGetLoanAccountsForCustomer() {

		List<LoanAccountView> loanAccounts = loanPersistence
				.getLoanAccountsForCustomer(group.getCustomerId(),new Date(System.currentTimeMillis()));
		assertEquals(1, loanAccounts.size());

	}
	

	public void testGetLoanAccountTransactionDetail() {

		Date transactionDate = new Date(System.currentTimeMillis());
		List<AccountActionDateEntity> details = loanPersistence
				.getLoanAccountTransactionDetail(loanAccount.getAccountId(),
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
	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting,Short accountSate) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement("42423142341", customer,
				accountSate, startDate, loanOffering,1);

	}
	
	public void testGetFeeAmountAtDisbursement() throws InterruptedException{
		loanAccountForDisbursement=getLoanAccount(group,meeting,Short.valueOf("3"));
		assertEquals(30.0,loanPersistence.getFeeAmountAtDisbursement(loanAccountForDisbursement.getAccountId(),new Date(System.currentTimeMillis())));
	}
	
	/**
	 * Test case to check whether the Loan Accounts that are in arrears are being returned properly.
	 * Requires a Loan Account that has atleat one due date of payment past the current system date - lateness days
	 * @param latenessDays
	 * @throws PersistenceException 
	 */
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
			
			list = loanPersistence.getLoanAccountsInArrears(latenessDays);
			assertNotNull(list);
			LoanBO testBO = (LoanBO) list.get(0);
			
			assertEquals(Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING),testBO.getAccountState().getId());
			
			// Get the first action date i.e for the first Installment
			AccountActionDateEntity actionDates = testBO.getAccountActionDate(Short.valueOf("1"));
			
			// assert that the date comes after the action date
			assertTrue(date.after(actionDates.getActionDate()));
			
			//assert that the payment status in 0 - unpaid
			assertEquals(PaymentStatus.UNPAID.getValue(),actionDates.getPaymentStatus());
			
			
			
			} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
	}
	
	public void testGetAccount() throws Exception{		
		LoanBO loanBO=loanPersistence.getAccount(loanAccount.getAccountId());
		assertEquals(loanBO.getAccountId(),loanAccount.getAccountId());				
	}
	
}
