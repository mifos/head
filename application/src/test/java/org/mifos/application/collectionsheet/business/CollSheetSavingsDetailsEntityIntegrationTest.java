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

import junit.framework.Assert;

import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingBOTestUtils;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CollSheetSavingsDetailsEntityIntegrationTest extends MifosIntegrationTestCase {
    public CollSheetSavingsDetailsEntityIntegrationTest() throws Exception {
        super();
    }

    private CustomerBO group;

    private CustomerBO center;

    private CustomerBO client1;

    private CustomerBO client2;

    private SavingsBO savings;

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(savings);
        TestObjectFactory.cleanUp(client1);
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testForMandatoryAccountWithNoPreviousPayments() throws Exception {

        savings = createSavingsAccount(SavingsType.MANDATORY);
        CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
        // obtaining the last installment
        // Scenario: Mandatory savings Account: None of the previous
        // installments have been paid and hence with a deposit
        // of 200, total overdue amount is 400 and due amount for next meeting
        // date is 200
        collSheetSavingsDetail.addAccountDetails(savings.getAccountActionDate((short) 3));
       Assert.assertEquals(TestUtils.createMoney(400.00), collSheetSavingsDetail.getAmntOverDue());
       Assert.assertEquals(TestUtils.createMoney(200.00), collSheetSavingsDetail.getRecommendedAmntDue());

        SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        SavingBOTestUtils.setDepositPaid(accountActionDate, new Money(getCurrency(), "100.00"));
        collSheetSavingsDetail.addAccountDetails(savings.getAccountActionDate((short) 3));
       Assert.assertEquals(TestUtils.createMoney(300.00), collSheetSavingsDetail.getAmntOverDue());
       Assert.assertEquals(TestUtils.createMoney(200.00), collSheetSavingsDetail.getRecommendedAmntDue());
        accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        SavingBOTestUtils.setDepositPaid(accountActionDate, new Money(getCurrency(), "200.00"));
        accountActionDate.setPaymentStatus(PaymentStatus.PAID);
        collSheetSavingsDetail.addAccountDetails(savings.getAccountActionDate((short) 3));
       Assert.assertEquals(TestUtils.createMoney(200.00), collSheetSavingsDetail.getAmntOverDue());
       Assert.assertEquals(TestUtils.createMoney(200.00), collSheetSavingsDetail.getRecommendedAmntDue());

    }

    public void testForMandatoryAccountWithPartialPayment() throws Exception {

        savings = createSavingsAccount(SavingsType.MANDATORY);
        CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
        // obtaining the last installment
        // Scenario: Mandatory savings Account: For the first installment a
        // partial payment of Rs 100 has been done and hence with a deposit
        // of 200, total overdue amount is Rs 300 and due amount for next
        // meeting date is Rs 200
        SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        SavingBOTestUtils.setDepositPaid(accountActionDate, new Money(getCurrency(), "100.00"));
        collSheetSavingsDetail.addAccountDetails(savings.getAccountActionDate((short) 3));
       Assert.assertEquals(TestUtils.createMoney(300.00), collSheetSavingsDetail.getAmntOverDue());
       Assert.assertEquals(TestUtils.createMoney(200.00), collSheetSavingsDetail.getRecommendedAmntDue());
    }

    public void testForMandatoryAccountWithFullPayment() throws Exception {

        savings = createSavingsAccount(SavingsType.MANDATORY);
        CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
        // obtaining the last installment
        // Scenario: Mandatory savings Account: For the first installment a full
        // payment of Rs 200 has been done and hence with a deposit
        // of 200, total overdue amount is Rs 200 and due amount for next
        // meeting date is Rs 200
        SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        SavingBOTestUtils.setDepositPaid(accountActionDate, new Money(getCurrency(), "200.00"));
        accountActionDate.setPaymentStatus(PaymentStatus.PAID);
        collSheetSavingsDetail.addAccountDetails(savings.getAccountActionDate((short) 3));
       Assert.assertEquals(TestUtils.createMoney(200.00), collSheetSavingsDetail.getAmntOverDue());
       Assert.assertEquals(TestUtils.createMoney(200.00), collSheetSavingsDetail.getRecommendedAmntDue());
    }

    public void testForVoluntaryAccountWithPartialPayment() throws Exception {

        savings = createSavingsAccount(SavingsType.VOLUNTARY);
        CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
        // obtaining the last installment
        // Scenario: Voluntary savings Account: For the first installment a full
        // payment of Rs 200 has been done and hence with a deposit
        // of 200, total overdue amount is Rs 0 and due amount for next meeting
        // date is Rs 200
        SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        SavingBOTestUtils.setDepositPaid(accountActionDate, new Money(getCurrency(), "100.00"));
        collSheetSavingsDetail.addAccountDetails(savings.getAccountActionDate((short) 3));
       Assert.assertEquals(TestUtils.createMoney(), collSheetSavingsDetail.getAmntOverDue());
       Assert.assertEquals(TestUtils.createMoney(200.00), collSheetSavingsDetail.getRecommendedAmntDue());
    }

    public void testForVoluntaryAccountWithFullPayment() throws Exception {

        savings = createSavingsAccount(SavingsType.VOLUNTARY);
        CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
        // obtaining the last installment
        // Scenario: Voluntary savings Account: For the first installment a full
        // payment of Rs 200 has been done and hence with a deposit
        // of 200, total overdue amount is Rs 0 and due amount for next meeting
        // date is Rs 200
        SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        SavingBOTestUtils.setDepositPaid(accountActionDate, new Money(getCurrency(), "200.00"));
        accountActionDate.setPaymentStatus(PaymentStatus.PAID);
        collSheetSavingsDetail.addAccountDetails(savings.getAccountActionDate((short) 3));
       Assert.assertEquals(TestUtils.createMoney(), collSheetSavingsDetail.getAmntOverDue());
       Assert.assertEquals(TestUtils.createMoney(200.00), collSheetSavingsDetail.getRecommendedAmntDue());
    }

    public void testTotalSavingsAmountDueForVoluntaryAccount() throws Exception {

        savings = createSavingsAccount(SavingsType.VOLUNTARY);
        CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
        SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        SavingBOTestUtils.setDepositPaid(accountActionDate, new Money(getCurrency(), "200.00"));
        accountActionDate.setPaymentStatus(PaymentStatus.PAID);
        collSheetSavingsDetail.addAccountDetails(savings.getAccountActionDate((short) 3));
       Assert.assertEquals(TestUtils.createMoney(200.00), collSheetSavingsDetail.getTotalSavingsAmntDue());
    }

    public void testTotalSavingsAmountDueForMandatoryAccount() throws Exception {

        savings = createSavingsAccount(SavingsType.MANDATORY);
        CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
        SavingsScheduleEntity accountActionDate = (SavingsScheduleEntity) savings.getAccountActionDate((short) 1);
        SavingBOTestUtils.setDepositPaid(accountActionDate, new Money(getCurrency(), "200.00"));
        accountActionDate.setPaymentStatus(PaymentStatus.PAID);
        collSheetSavingsDetail.addAccountDetails(savings.getAccountActionDate((short) 3));
       Assert.assertEquals(TestUtils.createMoney(400.00), collSheetSavingsDetail.getTotalSavingsAmntDue());
    }

    private SavingsBO createSavingsAccount(SavingsType savingsType) throws Exception {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", ApplicableTo.GROUPS,
                startDate, PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 200.0, 200.0,
                savingsType, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        return TestObjectFactory.createSavingsAccount("43245434", client1, (short) 16, startDate, savingsOffering);

    }
}
