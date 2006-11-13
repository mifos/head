/**

 * TestCollSheetSavingsDetailsEntity.java version: 1.0



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

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCollSheetSavingsDetailsEntity extends MifosTestCase {
	private CustomerBO group;

	private CustomerBO center;

	private CustomerBO client1;

	private CustomerBO client2;

	private SavingsBO savings;

	private SavingsOfferingBO savingsOffering;

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testForMandatoryAccountWithNoPreviousPayments()
			throws Exception {

		savings = createSavingsAccount(SavingsConstants.SAVINGS_MANDATORY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		// obtaining the last installment
		// Scenario: Mandatory savings Account: None of the previous
		// installments have been paid and hence with a deposit
		// of 200, total overdue amount is 400 and due amount for next meeting
		// date is 200
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(400.00, collSheetSavingsDetail.getAmntOverDue()
				.getAmountDoubleValue());
		assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue()
				.getAmountDoubleValue());

		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		TestSavingsBO.setDepositPaid(accountActionDate,new Money("100.00"));
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(300.00, collSheetSavingsDetail.getAmntOverDue()
				.getAmountDoubleValue());
		assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue()
				.getAmountDoubleValue());
		accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		TestSavingsBO.setDepositPaid(accountActionDate,new Money("200.00"));
		TestSavingsBO.setPaymentStatus(accountActionDate,PaymentStatus.PAID.getValue());
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(200.00, collSheetSavingsDetail.getAmntOverDue()
				.getAmountDoubleValue());
		assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue()
				.getAmountDoubleValue());

	}

	public void testForMandatoryAccountWithPartialPayment()
			throws Exception {

		savings = createSavingsAccount(SavingsConstants.SAVINGS_MANDATORY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		// obtaining the last installment
		// Scenario: Mandatory savings Account: For the first installment a
		// partial payment of Rs 100 has been done and hence with a deposit
		// of 200, total overdue amount is Rs 300 and due amount for next
		// meeting date is Rs 200
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		TestSavingsBO.setDepositPaid(accountActionDate,new Money("100.00"));
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(300.00, collSheetSavingsDetail.getAmntOverDue()
				.getAmountDoubleValue());
		assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue()
				.getAmountDoubleValue());
	}

	public void testForMandatoryAccountWithFullPayment()
			throws Exception {

		savings = createSavingsAccount(SavingsConstants.SAVINGS_MANDATORY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		// obtaining the last installment
		// Scenario: Mandatory savings Account: For the first installment a full
		// payment of Rs 200 has been done and hence with a deposit
		// of 200, total overdue amount is Rs 200 and due amount for next
		// meeting date is Rs 200
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		TestSavingsBO.setDepositPaid(accountActionDate,new Money("200.00"));
		TestSavingsBO.setPaymentStatus(accountActionDate,PaymentStatus.PAID.getValue());
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(200.00, collSheetSavingsDetail.getAmntOverDue()
				.getAmountDoubleValue());
		assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue()
				.getAmountDoubleValue());
	}

	public void testForVoluntaryAccountWithPartialPayment()
			throws Exception {

		savings = createSavingsAccount(SavingsConstants.SAVINGS_VOLUNTARY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		// obtaining the last installment
		// Scenario: Voluntary savings Account: For the first installment a full
		// payment of Rs 200 has been done and hence with a deposit
		// of 200, total overdue amount is Rs 0 and due amount for next meeting
		// date is Rs 200
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		TestSavingsBO.setDepositPaid(accountActionDate,new Money("100.00"));
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(0.00, collSheetSavingsDetail.getAmntOverDue()
				.getAmountDoubleValue());
		assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue()
				.getAmountDoubleValue());
	}

	public void testForVoluntaryAccountWithFullPayment()
			throws Exception {

		savings = createSavingsAccount(SavingsConstants.SAVINGS_VOLUNTARY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		// obtaining the last installment
		// Scenario: Voluntary savings Account: For the first installment a full
		// payment of Rs 200 has been done and hence with a deposit
		// of 200, total overdue amount is Rs 0 and due amount for next meeting
		// date is Rs 200
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		TestSavingsBO.setDepositPaid(accountActionDate,new Money("200.00"));
		TestSavingsBO.setPaymentStatus(accountActionDate,PaymentStatus.PAID.getValue());
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(0.00, collSheetSavingsDetail.getAmntOverDue()
				.getAmountDoubleValue());
		assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue()
				.getAmountDoubleValue());
	}

	public void testTotalSavingsAmountDueForVoluntaryAccount()
			throws Exception {

		savings = createSavingsAccount(SavingsConstants.SAVINGS_VOLUNTARY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		TestSavingsBO.setDepositPaid(accountActionDate,new Money("200.00"));
		TestSavingsBO.setPaymentStatus(accountActionDate,PaymentStatus.PAID.getValue());
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(200.00, collSheetSavingsDetail.getTotalSavingsAmntDue()
				.getAmountDoubleValue());
	}

	public void testTotalSavingsAmountDueForMandatoryAccount()
			throws Exception {

		savings = createSavingsAccount(SavingsConstants.SAVINGS_MANDATORY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		TestSavingsBO.setDepositPaid(accountActionDate,new Money("200.00"));
		TestSavingsBO.setPaymentStatus(accountActionDate,PaymentStatus.PAID.getValue());
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(400.00, collSheetSavingsDetail.getTotalSavingsAmntDue()
				.getAmountDoubleValue());
	}

	private SavingsBO createSavingsAccount(short savingsType) throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		SavingsOfferingBO savingsOffering = TestObjectFactory
				.createSavingsOffering("SavingPrd1", (short) 2, new Date(System
						.currentTimeMillis()), (short) 2, 300.0, (short) 1,
						1.2, 200.0, 200.0, savingsType, (short) 1,
						meetingIntCalc, meetingIntPost);
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE,
				group);
		return TestObjectFactory.createSavingsAccount("43245434", client1,
				(short) 16, new Date(System.currentTimeMillis()),
				savingsOffering);

	}
}
