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

package org.mifos.application.accounts.loan.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanBOForReversal extends MifosTestCase {

	private LoanBO loan = null;

	private CenterBO center = null;

	protected GroupBO group = null;

	private ClientBO client = null;

	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
        initializeStatisticsService();
    }

	@Override
	protected void tearDown() throws Exception {
		if (loan != null)
			loan = (LoanBO) HibernateUtil.getSessionTL().get(LoanBO.class,
					loan.getAccountId());
		if (client != null)
			client = (ClientBO) HibernateUtil.getSessionTL().get(
					ClientBO.class, client.getCustomerId());
		if (group != null)
			group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
					group.getCustomerId());
		if (center != null)
			center = (CenterBO) HibernateUtil.getSessionTL().get(
					CenterBO.class, center.getCustomerId());
		TestObjectFactory.cleanUp(loan);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);

		HibernateUtil.closeSession();
		super.tearDown();
	}

	private void createInitialCustomers() {
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
        long numberOfTransactions =
            getStatisticsService().getSuccessfulTransactionCount() - transactionCount;
        // TODO: numberOfTransactions needs to be 0
        assertTrue("numberOfTransactions=" + numberOfTransactions + " should be less than: " + 5,
                numberOfTransactions <= 5);
    }

	private void createLoanAccount() {
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        Date startDate = new Date(System.currentTimeMillis());
		createInitialCustomers();
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, center.getCustomerMeeting().getMeeting());
		loan = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_APPROVED, startDate, loanOffering);
        long numberOfTransactions =
            getStatisticsService().getSuccessfulTransactionCount() - transactionCount;
        // TODO: numberOfTransactions needs to be 0, but is 8
        assertTrue("numberOfTransactions=" + numberOfTransactions + " should be less than: " + 8,
                numberOfTransactions <= 8);
    }

	private void disburseLoan() throws AccountException {
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        loan.setUserContext(userContext);
		loan.disburseLoan("4534", new Date(), Short.valueOf("1"), group
				.getPersonnel(), new Date(), Short.valueOf("1"));
        long numberOfTransactions =
            getStatisticsService().getSuccessfulTransactionCount() - transactionCount;
        assertTrue("numberOfTransactions=" + numberOfTransactions + " should be: " + 0,
                numberOfTransactions == 0);
        HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}

	private LoanBO retrieveLoanAccount() {
		return (LoanBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				loan.getAccountId());
	}

	private void applyPaymentForLoan() throws AccountException {
		loan = retrieveLoanAccount();
		loan.setUserContext(userContext);
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		Date currentDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accntActionDates,
				TestObjectFactory.getMoneyForMFICurrency(200), null, loan
						.getPersonnel(), "receiptNum", Short.valueOf("1"),
				currentDate, currentDate);
		loan.applyPaymentWithPersist(paymentData);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}

	private void reverseLoan() throws AccountException {
		loan = retrieveLoanAccount();
		loan.setUserContext(userContext);
		loan.reverseLoanDisbursal(group.getPersonnel(), "Loan Disbursal", new CustomerPersistence());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}

	private void adjustLastPayment() throws AccountException {
		loan = retrieveLoanAccount();
		loan.setUserContext(userContext);
		loan.adjustPmnt("loan account has been adjusted by test code");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}

	/*
	 * TODO: fn_calc_test_fix
	 * remove this empty test after adding the tests below back in.
	 */
	public void testNothing() {
		
	}
	/*
	 * TODO: fn_calc_test_fix
	 *

	public void testLoanDisbursalReversal() throws AccountException {
		createLoanAccount();
		disburseLoan();
		applyPaymentForLoan();
		loan = retrieveLoanAccount();
		int noOfPayments = loan.getAccountPayments().size();
		int noOfTransactions = 0;
		int noOfFinancialTransactions = 0;
		int noOfActivities = loan.getLoanActivityDetails().size();
		int noOfNotes = loan.getAccountNotes().size();
		for (AccountPaymentEntity accountPayment : loan.getAccountPayments()) {
			noOfTransactions += accountPayment.getAccountTrxns().size();
			for (AccountTrxnEntity accountTrxn : accountPayment
					.getAccountTrxns()) {
				noOfFinancialTransactions += accountTrxn
						.getFinancialTransactions().size();
			}
		}

		reverseLoan();
		loan = retrieveLoanAccount();
		assertEquals(noOfPayments, loan.getAccountPayments().size());
		int noOfTransactionsAfterReversal = 0;
		int noOfFinancialTransactionsAfterReversal = 0;
		for (AccountPaymentEntity accountPayment : loan.getAccountPayments()) {
			assertEquals(new Money(), accountPayment.getAmount());
			noOfTransactionsAfterReversal += accountPayment.getAccountTrxns()
					.size();
			for (AccountTrxnEntity accountTrxn : accountPayment
					.getAccountTrxns()) {
				noOfFinancialTransactionsAfterReversal += accountTrxn
						.getFinancialTransactions().size();
			}
		}
		assertEquals(2 * noOfFinancialTransactions,
				noOfFinancialTransactionsAfterReversal);
		assertEquals(2 * noOfTransactions, noOfTransactionsAfterReversal);
		assertEquals(2 * noOfActivities, loan.getLoanActivityDetails().size());
		assertEquals(AccountState.LOAN_CANCELLED, loan.getState());
		assertEquals(1, loan.getAccountFlags().size());
		for (AccountFlagMapping accountFlagMapping : loan.getAccountFlags()) {
			assertEquals(AccountStateFlag.LOAN_REVERSAL.getValue(),
					accountFlagMapping.getFlag().getId());
		}
		assertEquals(noOfNotes + 1, loan.getAccountNotes().size());
		assertEquals(loan.getLoanAmount(), loan.getLoanSummary()
				.getPrincipalDue());
		assertEquals(new Money(), loan.getLoanSummary().getTotalAmntPaid());
		HibernateUtil.closeSession();
	}
*/
	
	/*
	 * TODO: fn_calc_test_fix
	 *
	
	public void testLoanDisbursalReversalWithAdjustment()
			throws AccountException {
		createLoanAccount();
		disburseLoan();
		applyPaymentForLoan();
		loan = retrieveLoanAccount();
		int noOfPayments = loan.getAccountPayments().size();
		int noOfTransactions = 0;
		int noOfFinancialTransactions = 0;
		int noOfActivities = loan.getLoanActivityDetails().size();
		int noOfNotes = loan.getAccountNotes().size();
		for (AccountPaymentEntity accountPayment : loan.getAccountPayments()) {
			noOfTransactions += accountPayment.getAccountTrxns().size();
			for (AccountTrxnEntity accountTrxn : accountPayment
					.getAccountTrxns()) {
				noOfFinancialTransactions += accountTrxn
						.getFinancialTransactions().size();
			}
		}
		adjustLastPayment();
		applyPaymentForLoan();
		reverseLoan();
		loan = retrieveLoanAccount();
		assertEquals(noOfPayments + 1, loan.getAccountPayments().size());
		int noOfTransactionsAfterReversal = 0;
		int noOfFinancialTransactionsAfterReversal = 0;
		for (AccountPaymentEntity accountPayment : loan.getAccountPayments()) {
			assertEquals(new Money(), accountPayment.getAmount());
			noOfTransactionsAfterReversal += accountPayment.getAccountTrxns()
					.size();
			for (AccountTrxnEntity accountTrxn : accountPayment
					.getAccountTrxns()) {
				noOfFinancialTransactionsAfterReversal += accountTrxn
						.getFinancialTransactions().size();
			}
		}
		assertEquals((3 * noOfFinancialTransactions) - 2,
				noOfFinancialTransactionsAfterReversal);
		assertEquals((3 * noOfTransactions) - 1, noOfTransactionsAfterReversal);
		assertEquals((3 * noOfActivities) - 1, loan.getLoanActivityDetails()
				.size());
		assertEquals(AccountState.LOAN_CANCELLED, loan.getState());
		assertEquals(1, loan.getAccountFlags().size());
		for (AccountFlagMapping accountFlagMapping : loan.getAccountFlags()) {
			assertEquals(AccountStateFlag.LOAN_REVERSAL.getValue(),
					accountFlagMapping.getFlag().getId());
		}
		assertEquals(noOfNotes + 1, loan.getAccountNotes().size());
		assertEquals(loan.getLoanAmount(), loan.getLoanSummary()
				.getPrincipalDue());
		assertEquals(new Money(), loan.getLoanSummary().getTotalAmntPaid());
		HibernateUtil.closeSession();
	}
*/
	
}
