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
 
package org.mifos.application.accounts.loan.persistence;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanBOIntegrationTest;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPersistenceIntegrationTest extends MifosIntegrationTest {

	public LoanPersistenceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final double DELTA = 0.00000001;

    LoanPersistence loanPersistence;

	CustomerBO center = null;

	CustomerBO group = null;

	MeetingBO meeting = null;

	AccountBO loanAccount = null;

	AccountBO loanAccountForDisbursement = null;

	LoanBO badAccount;

	LoanBO goodAccount;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		loanPersistence = new LoanPersistence();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenterForTestGetLoanAccounts("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		
		loanAccount = getLoanAccount(group, meeting);
	    badAccount = getBadAccount();
	    goodAccount = getGoodAccount();
	}

	@Override
	public void tearDown() throws Exception {

		TestObjectFactory.cleanUp(loanAccount);
		TestObjectFactory.cleanUp(badAccount);
		TestObjectFactory.cleanUp(goodAccount);
		TestObjectFactory.cleanUp(loanAccountForDisbursement);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		StaticHibernateUtil.closeSession();
	}
	


	public void testGetLoanAccountsInArrears() throws Exception {
		Calendar currentDate = new GregorianCalendar();
		Calendar twoDaysBack = new GregorianCalendar(currentDate
				.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
				currentDate.get(Calendar.DAY_OF_MONTH) - 2, 0, 0, 0);

		for (AccountActionDateEntity accountAction : loanAccount
				.getAccountActionDates()) {
			if (accountAction.getInstallmentId().equals(Short.valueOf("1")))
				LoanBOIntegrationTest.setActionDate(accountAction,new Date(twoDaysBack
						.getTimeInMillis()));
		}

		TestObjectFactory.updateObject(loanAccount);
		StaticHibernateUtil.closeSession();
		loanAccount = new AccountPersistence().getAccount(loanAccount
				.getAccountId());

		List<Integer> list = loanPersistence.getLoanAccountsInArrears(Short
				.valueOf("1"));
		assertEquals(1, list.size());

		list = loanPersistence.getLoanAccountsInArrears(Short.valueOf("2"));
		assertEquals(1, list.size());
		StaticHibernateUtil.closeSession();

		LoanBO testBO = TestObjectFactory.getObject(LoanBO.class, list
				.get(0));
		assertEquals(Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING),
				testBO.getAccountState().getId());
		AccountActionDateEntity actionDate = testBO.getAccountActionDate(Short
				.valueOf("1"));
		assertFalse(actionDate.isPaid());

		StaticHibernateUtil.closeSession();
		list = loanPersistence.getLoanAccountsInArrears(Short.valueOf("3"));
		assertEquals(0, list.size());

		StaticHibernateUtil.closeSession();
		loanAccount = TestObjectFactory.getObject(LoanBO.class,
				loanAccount.getAccountId());
	}

	public void testFindBySystemId() throws Exception {
		LoanPersistence loanPersistance = new LoanPersistence();
		LoanBO loanBO = loanPersistance.findBySystemId(loanAccount
				.getGlobalAccountNum());
		assertEquals(loanBO.getGlobalAccountNum(), loanAccount
				.getGlobalAccountNum());
		assertEquals(loanBO.getAccountId(), loanAccount.getAccountId());
	}
	
	public void testFindIndividualLoans() throws Exception {
		LoanPersistence loanPersistance = new LoanPersistence();
		List<LoanBO> listLoanBO = loanPersistance.findIndividualLoans(loanAccount
				.getAccountId().toString());
		
		assertEquals(0,listLoanBO.size());
	}
	public void testGetFeeAmountAtDisbursement() throws Exception {
		loanAccountForDisbursement = getLoanAccount("cdfg", group, meeting,
				AccountState.LOAN_APPROVED);
		assertEquals(30.0, loanPersistence.getFeeAmountAtDisbursement(loanAccountForDisbursement.getAccountId()), DELTA);
	}

	public void testGetLoanAccountsInArrearsInGoodStanding()
			throws PersistenceException {
		Short latenessDays = 1;
		Calendar actionDate = new GregorianCalendar();
		int year = actionDate.get(Calendar.YEAR);
		int month = actionDate.get(Calendar.MONTH);
		int day = actionDate.get(Calendar.DAY_OF_MONTH);
		actionDate = new GregorianCalendar(year, month, day - latenessDays);

		Date date = new Date(actionDate.getTimeInMillis());
		Calendar checkDate = new GregorianCalendar(year, month, day - 15);
		Date startDate = new Date(checkDate.getTimeInMillis());
		for (AccountActionDateEntity accountAction : loanAccount
				.getAccountActionDates()) {
			LoanBOIntegrationTest.setActionDate(accountAction,startDate);
		}
		TestObjectFactory.updateObject(loanAccount);
		loanAccount = new AccountPersistence().getAccount(loanAccount
				.getAccountId());
		List<Integer> list = loanPersistence
				.getLoanAccountsInArrearsInGoodStanding(latenessDays);
		assertNotNull(list);
		LoanBO testBO = TestObjectFactory.getObject(LoanBO.class, list
				.get(0));
		assertEquals(Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING),
				testBO.getAccountState().getId());

		// Get the first action date i.e for the first Installment
		Short firstInstallment = 1;
		AccountActionDateEntity actionDates = 
			testBO.getAccountActionDate(firstInstallment);

		assertTrue(date.after(actionDates.getActionDate()));
		assertFalse(actionDates.isPaid());
	}

	public void testGetAccount() throws Exception {
		LoanBO loanBO = loanPersistence.getAccount(loanAccount.getAccountId());
		assertEquals(loanBO.getAccountId(), loanAccount.getAccountId());
	}
	public void testGetLoanAccountsActiveInGoodBadStanding()
			throws PersistenceException {
		List<LoanBO> loanBO1 = loanPersistence.getLoanAccountsActiveInGoodBadStanding(loanAccount.getCustomer().getCustomerId());
		assertEquals(3, loanBO1.size());
	}

	public void testGetLastPaymentAction() throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		loanAccountForDisbursement = getLoanAccount(
				AccountState.LOAN_APPROVED, startDate, 1);
		disburseLoan(startDate);
		assertEquals("Last payment action should be 'PAYMENT'",
				AccountActionTypes.DISBURSAL.getValue(), loanPersistence
						.getLastPaymentAction(loanAccountForDisbursement
								.getAccountId()));
	}
	
	public void testGetLoanOffering() throws Exception {
		LoanOfferingBO loanOffering = TestObjectFactory.createCompleteLoanOfferingObject();
		LoanOfferingBO loanOfferingBO = loanPersistence.getLoanOffering(
				loanOffering.getPrdOfferingId(),
				TestObjectFactory.TEST_LOCALE);
		assertEquals(loanOfferingBO.getPrdOfferingId(),loanOffering.getPrdOfferingId());
		TestObjectFactory.removeObject(loanOfferingBO);
	}
	
	public void testGetLastLoanAmountForCustomer() throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		loanAccountForDisbursement = getLoanAccount(
				AccountState.LOAN_APPROVED, startDate, 1);
		disburseLoan(startDate);
		assertEquals(((LoanBO) loanAccountForDisbursement).getLoanAmount(),
				loanPersistence.getLastLoanAmountForCustomer(group
						.getCustomerId(), loanAccountForDisbursement.getAccountId()+1));
	}

	private void disburseLoan(Date startDate) throws Exception {
		((LoanBO) loanAccountForDisbursement).disburseLoan("1234", startDate,
				Short.valueOf("1"), loanAccountForDisbursement.getPersonnel(),
				startDate, Short.valueOf("1"));
		StaticHibernateUtil.commitTransaction();
	}

	private AccountBO getLoanAccount(AccountState state, Date startDate,
			int disbursalType) {
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loanvcfg", "bhgf", ApplicableTo.GROUPS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short)3, 
				InterestType.FLAT, meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, state, startDate, loanOffering,
				disbursalType);
	}

	private AccountBO getLoanAccount(String shortName, CustomerBO customer,
			MeetingBO meeting, AccountState state) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan123", shortName, ApplicableTo.GROUPS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short)3, 
				InterestType.FLAT, meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"42423142341", customer, state, startDate, loanOffering,
				1);

	}

	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loancfgb", "dhsq", ApplicableTo.GROUPS, 
				startDate, PrdStatus.LOAN_ACTIVE, 
				300.0, 1.2, (short)3, 
				InterestType.FLAT, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate, loanOffering);

	}
	
	private LoanBO getBadAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loanabcd", "abcd", ApplicableTo.CLIENTS, 
				startDate, PrdStatus.LOAN_ACTIVE, 
				300.0, 1.2, (short)3, 
				InterestType.FLAT,meeting);
		return TestObjectFactory.createLoanAccount("42423142323", group,
				AccountState.LOAN_ACTIVE_IN_BAD_STANDING, 
				startDate, loanOffering);
	}
	private LoanBO getGoodAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loanabf", "abf", ApplicableTo.CLIENTS, 
				startDate, PrdStatus.LOAN_ACTIVE, 
				300.0, 1.2, (short)3, 
				InterestType.FLAT, meeting);
		return TestObjectFactory.createLoanAccount("42423142342", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate, loanOffering);
	}

	public void testGetLoanAccountsInActiveBadStandingShouldReturnLoanBOInActiveBadByBranchId() throws Exception {
		short branchId = 3;
		List<LoanBO> loanList = loanPersistence.getLoanAccountsInActiveBadStanding(branchId,(short)-1,(short)-1);
		assertEquals(1,loanList.size());
	}
	
	
	public void testGetLoanAccountsInActiveBadStandingShouldReturnLoanBOInActiveBadByBranchIdAndLoanOfficerId() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		List<LoanBO> loanList = loanPersistence.getLoanAccountsInActiveBadStanding(branchId,loanOfficerId,(short)-1);
		assertEquals(1,loanList.size());
		
	}
	public void testGetLoanAccountsInActiveBadStandingShouldReturnLoanBOInActiveBadByBranchIdLoanOfficerIdAndLoanProductId() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		short loanProductId = badAccount.getLoanOffering().getPrdOfferingId() ;
		List<LoanBO> loanList = loanPersistence.getLoanAccountsInActiveBadStanding(branchId,loanOfficerId,loanProductId);
		assertEquals(1,loanList.size());
		
	}
	
	public void testGetTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStandingByBranchId() throws Exception {
		short branchId = 3;
		BigDecimal money = loanPersistence.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(branchId,(short)-1,(short)-1);
		assertEquals(0,new BigDecimal(600).compareTo(money));
	}
	public void testGetTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStandingByBranchIdAndLoanOfficerId() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		BigDecimal money = loanPersistence.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(branchId,loanOfficerId,(short)-1);
		assertEquals(0,new BigDecimal(600).compareTo(money));
	}
	public void testGetTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStandingByBranchIdLoanOfficerIdAndLoanProductId() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		short loanProductId = goodAccount.getLoanOffering().getPrdOfferingId();
		BigDecimal money = loanPersistence.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(branchId,loanOfficerId,loanProductId);
		assertEquals(0,new BigDecimal(300).compareTo(money));
	}
	public void testGetActiveLoansBothInGoodAndBadStandingByLoanOfficer() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		List<LoanBO> loanList = loanPersistence.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(branchId,loanOfficerId,(short)-1);
		assertEquals(3,loanList.size());
	}
	public void testGetActiveLoansBothInGoodAndBadStandingByLoanOfficerAndLoanProduct() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		short goodLoanProductId = goodAccount.getLoanOffering().getPrdOfferingId();
		List<LoanBO> goodLoanList = loanPersistence.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(branchId,loanOfficerId,goodLoanProductId);
		assertEquals(1,goodLoanList.size());
		
		short badLoanProductId = badAccount.getLoanOffering().getPrdOfferingId();
		List<LoanBO> badLoanList = loanPersistence.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(branchId,loanOfficerId,badLoanProductId);
		assertEquals(1,badLoanList.size());
	}

 	public void testGetAllLoanAccounts() throws Exception {		
 		List<LoanBO> loanAccounts = loanPersistence.getAllLoanAccounts();
 		assertEquals(3,loanAccounts.size());
 	}
}
