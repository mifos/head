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

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanBOTestUtils;
import org.mifos.application.accounts.loan.business.LoanFeeScheduleEntity;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CollSheetCustBOIntegrationTest extends MifosIntegrationTestCase {

    public CollSheetCustBOIntegrationTest() throws Exception {
        super();
    }

    private AccountBO accountBO;

    private CustomerBO center;

    private CustomerBO group;

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(accountBO);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StaticHibernateUtil.getSessionTL();
    }

    public void testPopulateCustomerDetails() {
        CollSheetCustBO collSheetCustBO = new CollSheetCustBO(getCurrency());
        MeetingBO meeting = TestObjectFactory.getTypicalMeeting();
        TestObjectFactory.createMeeting(meeting);
        CustomerBO centerBO = TestObjectFactory.createWeeklyFeeCenter("ash", meeting);
        CustomerBO groupBO = TestObjectFactory.createClient("ashGrp", CustomerStatus.CLIENT_PARTIAL, centerBO);
        collSheetCustBO.populateCustomerDetails(groupBO);
       Assert.assertEquals("1.1.1", collSheetCustBO.getSearchId());
       Assert.assertEquals(centerBO.getCustomerId(), collSheetCustBO.getParentCustomerId());

        TestObjectFactory.cleanUp(groupBO);
        TestObjectFactory.cleanUp(centerBO);
    }

    public void testPopulateAccountDetails() {
        CollSheetCustBO collSheetCustBO = new CollSheetCustBO(getCurrency());
        LoanBO loan = (LoanBO) createLoanAccount();

        LoanScheduleEntity accountActionDate = new LoanScheduleEntity(loan, group, (short) 1, new java.sql.Date(System
                .currentTimeMillis()), PaymentStatus.UNPAID, new Money(getCurrency()), new Money(getCurrency()));
        LoanFeeScheduleEntity accntFeesActionDetailEntity = new LoanFeeScheduleEntity(accountActionDate, null, null,
                TestObjectFactory.getMoneyForMFICurrency(5));
        LoanBOTestUtils
                .setFeeAmountPaid(accntFeesActionDetailEntity, TestObjectFactory.getMoneyForMFICurrency(3));
        accountActionDate.addAccountFeesAction(accntFeesActionDetailEntity);

        LoanBOTestUtils.modifyData(accountActionDate, TestObjectFactory.getMoneyForMFICurrency(10),
                TestObjectFactory.getMoneyForMFICurrency(5), TestObjectFactory.getMoneyForMFICurrency(3),
                TestObjectFactory.getMoneyForMFICurrency(0), TestObjectFactory.getMoneyForMFICurrency(5),
                TestObjectFactory.getMoneyForMFICurrency(5), accountActionDate.getPrincipal(), accountActionDate
                        .getPrincipalPaid(), accountActionDate.getInterest(), accountActionDate.getInterestPaid());

        collSheetCustBO.populateAccountDetails(accountActionDate);

       Assert.assertEquals(TestObjectFactory.getMoneyForMFICurrency(8), collSheetCustBO.getCustAccntPenalty());
       Assert.assertEquals(TestObjectFactory.getMoneyForMFICurrency(2), collSheetCustBO.getCustAccntFee());
    }

    public void testAddCollectionSheetLoanDetail() {
        CollSheetLnDetailsEntity collectionSheetLoanDetail = new CollSheetLnDetailsEntity();
        collectionSheetLoanDetail.setAccountId(Integer.valueOf("1"));
        collectionSheetLoanDetail.setPrincipalDue(TestObjectFactory.getMoneyForMFICurrency(20));
        collectionSheetLoanDetail.setInterestDue(TestObjectFactory.getMoneyForMFICurrency(10));
        collectionSheetLoanDetail.setInterestOverDue(TestObjectFactory.getMoneyForMFICurrency(5));
        collectionSheetLoanDetail.setAmntToBeDisbursed(TestObjectFactory.getMoneyForMFICurrency(15));
        collectionSheetLoanDetail.setFeesDue(TestObjectFactory.getMoneyForMFICurrency(0));
        collectionSheetLoanDetail.setPenaltyDue(TestObjectFactory.getMoneyForMFICurrency(0));

        CollSheetCustBO collSheetCustBO = new CollSheetCustBO(getCurrency());
        collSheetCustBO.addCollectionSheetLoanDetail(collectionSheetLoanDetail);

       Assert.assertEquals(TestObjectFactory.getMoneyForMFICurrency(35), collSheetCustBO.getCollectiveLoanAmntDue());
       Assert.assertEquals(TestObjectFactory.getMoneyForMFICurrency(15), collSheetCustBO.getCollectiveLoanDisbursal());
       Assert.assertEquals(1, collSheetCustBO.getCollectionSheetLoanDetails().size());
    }

    public void testGetLoanDetailsForAccountId() {
        CollSheetLnDetailsEntity collectionSheetLoanDetail = new CollSheetLnDetailsEntity();
        collectionSheetLoanDetail.setAccountId(Integer.valueOf("1"));

        CollSheetCustBO collSheetCustBO = new CollSheetCustBO(getCurrency());
        collSheetCustBO.addCollectionSheetLoanDetail(collectionSheetLoanDetail);
       Assert.assertEquals(1, collSheetCustBO.getLoanDetailsForAccntId(1).getAccountId().intValue());
    }

    public void testGetLoanDetailsForNullAccountId() {
        CollSheetCustBO collSheetCustBO = new CollSheetCustBO(getCurrency());
        Assert.assertNull(collSheetCustBO.getLoanDetailsForAccntId(Integer.valueOf("1")));
    }

    public void testAddCollectiveTotalsForChild() {
        CollSheetCustBO collSheetCustBO = new CollSheetCustBO(getCurrency());
        collSheetCustBO.setCollectiveAccntCharges(TestObjectFactory.getMoneyForMFICurrency(5));
        collSheetCustBO.setCollectiveLoanAmntDue(TestObjectFactory.getMoneyForMFICurrency(5));
        collSheetCustBO.setCollectiveLoanDisbursal(TestObjectFactory.getMoneyForMFICurrency(5));
        collSheetCustBO.setCollectiveNetCashIn(TestObjectFactory.getMoneyForMFICurrency(5));
        collSheetCustBO.setCollectiveSavingsAmntDue(TestObjectFactory.getMoneyForMFICurrency(5));
        collSheetCustBO.setCollectiveTotalCollection(TestObjectFactory.getMoneyForMFICurrency(5));

        CollSheetCustBO collSheetCustObj = new CollSheetCustBO(getCurrency());
        collSheetCustObj.setCollectiveAccntCharges(TestObjectFactory.getMoneyForMFICurrency(5));
        collSheetCustObj.setCollectiveLoanAmntDue(TestObjectFactory.getMoneyForMFICurrency(5));
        collSheetCustObj.setCollectiveLoanDisbursal(TestObjectFactory.getMoneyForMFICurrency(5));
        collSheetCustObj.setCollectiveNetCashIn(TestObjectFactory.getMoneyForMFICurrency(5));
        collSheetCustObj.setCollectiveSavingsAmntDue(TestObjectFactory.getMoneyForMFICurrency(5));
        collSheetCustObj.setCollectiveTotalCollection(TestObjectFactory.getMoneyForMFICurrency(5));

        collSheetCustObj.addCollectiveTotalsForChild(collSheetCustBO);
       Assert.assertEquals(TestObjectFactory.getMoneyForMFICurrency(10), collSheetCustObj.getCollectiveAccntCharges());
       Assert.assertEquals(TestObjectFactory.getMoneyForMFICurrency(10), collSheetCustObj.getCollectiveAccntCharges());
       Assert.assertEquals(TestObjectFactory.getMoneyForMFICurrency(10), collSheetCustObj.getCollectiveAccntCharges());
    }

    private AccountBO createLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center1", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);
        return accountBO;
    }
}
