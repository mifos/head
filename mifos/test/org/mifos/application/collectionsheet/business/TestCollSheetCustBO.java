/**
 
 * TestCollSheetCustBO.java    version: 1.0
 
 
 
 * Copyright (c) 2005-2006 Grameen Foundation USA
 
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 
 * All rights reserved.
 
 
 
 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 
 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 
 
 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 
 
 * and how it is applied. 
 
 *
 
 */
package org.mifos.application.collectionsheet.business;

import java.util.Date;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanFeeScheduleEntity;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.loan.business.TestLoanScheduleEntity;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCollSheetCustBO extends MifosTestCase {

	private AccountBO accountBO;

	private CustomerBO center;

	private CustomerBO group;

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		HibernateUtil.getSessionTL();
	}

	public void testPopulateCustomerDetails() {
		CollSheetCustBO collSheetCustBO = new CollSheetCustBO();
		MeetingBO meeting = TestObjectFactory.getTypicalMeeting();
		TestObjectFactory.createMeeting(meeting);
		CustomerBO centerBO = TestObjectFactory.createCenter("ash", meeting);
		CustomerBO groupBO = TestObjectFactory.createClient("ashGrp", 
				CustomerStatus.CLIENT_PARTIAL, centerBO);
		collSheetCustBO.populateCustomerDetails(groupBO);
		assertEquals("1.1.1", collSheetCustBO.getSearchId());
		assertEquals(
			centerBO.getCustomerId(),
			collSheetCustBO.getParentCustomerId());

		TestObjectFactory.cleanUp(groupBO);
		TestObjectFactory.cleanUp(centerBO);
	}

	public void testPopulateAccountDetails() {
		CollSheetCustBO collSheetCustBO = new CollSheetCustBO();
		LoanBO loan = (LoanBO) createLoanAccount();

		LoanScheduleEntity accountActionDate = new LoanScheduleEntity(loan,
				group, (short) 1,
				new java.sql.Date(System.currentTimeMillis()),
				PaymentStatus.UNPAID, new Money(), new Money());
		LoanFeeScheduleEntity accntFeesActionDetailEntity = new LoanFeeScheduleEntity(
				accountActionDate, null, null, TestObjectFactory
						.getMoneyForMFICurrency(5));
		TestLoanBO.setFeeAmountPaid(accntFeesActionDetailEntity,TestObjectFactory
				.getMoneyForMFICurrency(3));
		accountActionDate.addAccountFeesAction(accntFeesActionDetailEntity);
		

		TestLoanScheduleEntity.modifyData(accountActionDate,
				TestObjectFactory.getMoneyForMFICurrency(10),
				TestObjectFactory.getMoneyForMFICurrency(5),
				TestObjectFactory.getMoneyForMFICurrency(3),
				TestObjectFactory.getMoneyForMFICurrency(0),
				TestObjectFactory.getMoneyForMFICurrency(5),
				TestObjectFactory.getMoneyForMFICurrency(5),
				accountActionDate.getPrincipal(),
				accountActionDate.getPrincipalPaid(),
				accountActionDate.getInterest(),
				accountActionDate.getInterestPaid());

		collSheetCustBO.populateAccountDetails(accountActionDate);

		assertEquals(TestObjectFactory.getMoneyForMFICurrency(8),
				collSheetCustBO.getCustAccntPenalty());
		assertEquals(
				TestObjectFactory.getMoneyForMFICurrency(2),
				collSheetCustBO.getCustAccntFee());
	}

	public void testAddCollectionSheetLoanDetail() {
		CollSheetLnDetailsEntity collectionSheetLoanDetail = new CollSheetLnDetailsEntity();
		collectionSheetLoanDetail.setAccountId(Integer.valueOf("1"));
		collectionSheetLoanDetail.setPrincipalDue(TestObjectFactory
				.getMoneyForMFICurrency(20));
		collectionSheetLoanDetail.setInterestDue(TestObjectFactory
				.getMoneyForMFICurrency(10));
		collectionSheetLoanDetail.setInterestOverDue(TestObjectFactory
				.getMoneyForMFICurrency(5));
		collectionSheetLoanDetail.setAmntToBeDisbursed(TestObjectFactory
				.getMoneyForMFICurrency(15));
		collectionSheetLoanDetail.setFeesDue(TestObjectFactory
				.getMoneyForMFICurrency(0));
		collectionSheetLoanDetail.setPenaltyDue(TestObjectFactory
				.getMoneyForMFICurrency(0));

		CollSheetCustBO collSheetCustBO = new CollSheetCustBO();
		collSheetCustBO.addCollectionSheetLoanDetail(collectionSheetLoanDetail);

		assertEquals(
				TestObjectFactory.getMoneyForMFICurrency(35),
				collSheetCustBO.getCollectiveLoanAmntDue());
		assertEquals(
				TestObjectFactory.getMoneyForMFICurrency(15),
				collSheetCustBO.getCollectiveLoanDisbursal());
		assertEquals(
				1,
				collSheetCustBO.getCollectionSheetLoanDetails().size());
	}

	public void testGetLoanDetailsForAccountId() {
		CollSheetLnDetailsEntity collectionSheetLoanDetail = new CollSheetLnDetailsEntity();
		collectionSheetLoanDetail.setAccountId(Integer.valueOf("1"));

		CollSheetCustBO collSheetCustBO = new CollSheetCustBO();
		collSheetCustBO.addCollectionSheetLoanDetail(collectionSheetLoanDetail);
		assertEquals(1,
			collSheetCustBO.getLoanDetailsForAccntId(1).getAccountId()
				.intValue());
	}

	public void testGetLoanDetailsForNullAccountId() {
		CollSheetCustBO collSheetCustBO = new CollSheetCustBO();
		assertNull(collSheetCustBO.getLoanDetailsForAccntId(Integer
				.valueOf("1")));
	}

	public void testAddCollectiveTotalsForChild() {
		CollSheetCustBO collSheetCustBO = new CollSheetCustBO();
		collSheetCustBO.setCollectiveAccntCharges(TestObjectFactory
				.getMoneyForMFICurrency(5));
		collSheetCustBO.setCollectiveLoanAmntDue(TestObjectFactory
				.getMoneyForMFICurrency(5));
		collSheetCustBO.setCollectiveLoanDisbursal(TestObjectFactory
				.getMoneyForMFICurrency(5));
		collSheetCustBO.setCollectiveNetCashIn(TestObjectFactory
				.getMoneyForMFICurrency(5));
		collSheetCustBO.setCollectiveSavingsAmntDue(TestObjectFactory
				.getMoneyForMFICurrency(5));
		collSheetCustBO.setCollectiveTotalCollection(TestObjectFactory
				.getMoneyForMFICurrency(5));

		CollSheetCustBO collSheetCustObj = new CollSheetCustBO();
		collSheetCustObj.setCollectiveAccntCharges(TestObjectFactory
				.getMoneyForMFICurrency(5));
		collSheetCustObj.setCollectiveLoanAmntDue(TestObjectFactory
				.getMoneyForMFICurrency(5));
		collSheetCustObj.setCollectiveLoanDisbursal(TestObjectFactory
				.getMoneyForMFICurrency(5));
		collSheetCustObj.setCollectiveNetCashIn(TestObjectFactory
				.getMoneyForMFICurrency(5));
		collSheetCustObj.setCollectiveSavingsAmntDue(TestObjectFactory
				.getMoneyForMFICurrency(5));
		collSheetCustObj.setCollectiveTotalCollection(TestObjectFactory
				.getMoneyForMFICurrency(5));

		collSheetCustObj.addCollectiveTotalsForChild(collSheetCustBO);
		assertEquals(TestObjectFactory.getMoneyForMFICurrency(10),
				collSheetCustObj.getCollectiveAccntCharges());
		assertEquals(TestObjectFactory.getMoneyForMFICurrency(10),
				collSheetCustObj.getCollectiveAccntCharges());
		assertEquals(TestObjectFactory.getMoneyForMFICurrency(10),
				collSheetCustObj.getCollectiveAccntCharges());
	}

	private AccountBO createLoanAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center1", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate,
				loanOffering);
		return accountBO;
	}

}
