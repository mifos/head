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
 
package org.mifos.application.collectionsheet.business;

import java.util.Date;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.accounts.savings.business.SavingsBOIntegrationTest;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CollSheetSavingsDetailsEntityIntegrationTest extends MifosIntegrationTest {
	public CollSheetSavingsDetailsEntityIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final double DELTA = 0.00000001;

    private CustomerBO group;

	private CustomerBO center;

	private CustomerBO client1;

	private CustomerBO client2;

	private SavingsBO savings;

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testForMandatoryAccountWithNoPreviousPayments()
			throws Exception {

		savings = createSavingsAccount(SavingsType.MANDATORY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		// obtaining the last installment
		// Scenario: Mandatory savings Account: None of the previous
		// installments have been paid and hence with a deposit
		// of 200, total overdue amount is 400 and due amount for next meeting
		// date is 200
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(400.00, collSheetSavingsDetail.getAmntOverDue().getAmountDoubleValue(), DELTA);
		assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue().getAmountDoubleValue(), DELTA);

		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		SavingsBOIntegrationTest.setDepositPaid(accountActionDate,new Money("100.00"));
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(300.00, collSheetSavingsDetail.getAmntOverDue().getAmountDoubleValue(), DELTA);
        assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue().getAmountDoubleValue(), DELTA);
		accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		SavingsBOIntegrationTest.setDepositPaid(accountActionDate,new Money("200.00"));
		accountActionDate.setPaymentStatus(PaymentStatus.PAID);
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(200.00, collSheetSavingsDetail.getAmntOverDue().getAmountDoubleValue(), DELTA);
        assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue().getAmountDoubleValue(), DELTA);

	}

	public void testForMandatoryAccountWithPartialPayment()
			throws Exception {

		savings = createSavingsAccount(SavingsType.MANDATORY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		// obtaining the last installment
		// Scenario: Mandatory savings Account: For the first installment a
		// partial payment of Rs 100 has been done and hence with a deposit
		// of 200, total overdue amount is Rs 300 and due amount for next
		// meeting date is Rs 200
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		SavingsBOIntegrationTest.setDepositPaid(accountActionDate,new Money("100.00"));
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(300.00, collSheetSavingsDetail.getAmntOverDue().getAmountDoubleValue(), DELTA);
        assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue().getAmountDoubleValue(), DELTA);
	}

	public void testForMandatoryAccountWithFullPayment()
			throws Exception {

		savings = createSavingsAccount(SavingsType.MANDATORY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		// obtaining the last installment
		// Scenario: Mandatory savings Account: For the first installment a full
		// payment of Rs 200 has been done and hence with a deposit
		// of 200, total overdue amount is Rs 200 and due amount for next
		// meeting date is Rs 200
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		SavingsBOIntegrationTest.setDepositPaid(accountActionDate,new Money("200.00"));
		accountActionDate.setPaymentStatus(PaymentStatus.PAID);
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(200.00, collSheetSavingsDetail.getAmntOverDue().getAmountDoubleValue(), DELTA);
        assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue().getAmountDoubleValue(), DELTA);
	}

	public void testForVoluntaryAccountWithPartialPayment()
			throws Exception {

		savings = createSavingsAccount(SavingsType.VOLUNTARY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		// obtaining the last installment
		// Scenario: Voluntary savings Account: For the first installment a full
		// payment of Rs 200 has been done and hence with a deposit
		// of 200, total overdue amount is Rs 0 and due amount for next meeting
		// date is Rs 200
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		SavingsBOIntegrationTest.setDepositPaid(accountActionDate,new Money("100.00"));
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(0.00, collSheetSavingsDetail.getAmntOverDue().getAmountDoubleValue(), DELTA);
        assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue().getAmountDoubleValue(), DELTA);
	}

	public void testForVoluntaryAccountWithFullPayment()
			throws Exception {

		savings = createSavingsAccount(SavingsType.VOLUNTARY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		// obtaining the last installment
		// Scenario: Voluntary savings Account: For the first installment a full
		// payment of Rs 200 has been done and hence with a deposit
		// of 200, total overdue amount is Rs 0 and due amount for next meeting
		// date is Rs 200
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		SavingsBOIntegrationTest.setDepositPaid(accountActionDate,new Money("200.00"));
		accountActionDate.setPaymentStatus(PaymentStatus.PAID);
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(0.00, collSheetSavingsDetail.getAmntOverDue().getAmountDoubleValue(), DELTA);
        assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue().getAmountDoubleValue(), DELTA);
	}

	public void testTotalSavingsAmountDueForVoluntaryAccount()
			throws Exception {

		savings = createSavingsAccount(SavingsType.VOLUNTARY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		SavingsBOIntegrationTest.setDepositPaid(accountActionDate,new Money("200.00"));
		accountActionDate.setPaymentStatus(PaymentStatus.PAID);
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(200.00, collSheetSavingsDetail.getTotalSavingsAmntDue().getAmountDoubleValue(), DELTA);
	}

	public void testTotalSavingsAmountDueForMandatoryAccount()
			throws Exception {

		savings = createSavingsAccount(SavingsType.MANDATORY);
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
		SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings
				.getAccountActionDate((short) 1);
		SavingsBOIntegrationTest.setDepositPaid(accountActionDate,new Money("200.00"));
		accountActionDate.setPaymentStatus(PaymentStatus.PAID);
		collSheetSavingsDetail.addAccountDetails(savings
				.getAccountActionDate((short) 3));
		assertEquals(400.00, collSheetSavingsDetail.getTotalSavingsAmntDue().getAmountDoubleValue(), DELTA);
	}

	private SavingsBO createSavingsAccount(SavingsType savingsType) 
	throws Exception {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		SavingsOfferingBO savingsOffering = 
			TestObjectFactory.createSavingsProduct("SavingPrd1", 
				ApplicableTo.GROUPS, 
				startDate, PrdStatus.SAVINGS_ACTIVE, 
				300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 
				1.2, 200.0, 200.0, 
				savingsType, InterestCalcType.MINIMUM_BALANCE, 
				meetingIntCalc, meetingIntPost);
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE,
				group);
		return TestObjectFactory.createSavingsAccount("43245434", client1,
				(short) 16, startDate,
				savingsOffering);

	}
}
