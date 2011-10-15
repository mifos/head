/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.application.collectionsheet.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BulkEntryPersistenceIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    private LegacyAccountDao legacyAccountDao;

    private CustomerBO center;

    private CustomerBO group;

    private CustomerBO client;

    private AccountBO account;

    @After
    public void tearDown() throws Exception {
        account = null;
        client = null;
        group = null;
        center = null;
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testGetAccount() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        account = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        StaticHibernateUtil.flushSession();
        account = legacyAccountDao.getAccount(account.getAccountId());
        Assert.assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(), "Loan");
    }

    @Test
    public void testSuccessfulApplyPayment() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        account = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        StaticHibernateUtil.flushSession();
        account = legacyAccountDao.getAccount(account.getAccountId());
        Assert.assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(), "Loan");

        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.add(account.getAccountActionDates().iterator().next());
        Date currentDate = startDate;
        PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(accntActionDates, TestUtils
                .createMoney("100.0"), null, account.getPersonnel(), "423423", Short.valueOf("1"), currentDate,
                currentDate);
        try {
            IntegrationTestObjectMother.applyAccountPayment(account, paymentData);
            Assert.assertEquals(((LoanBO) account).getLoanSummary().getFeesPaid(), TestUtils.createMoney("100.0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        account.getAccountPayments().clear();

    }

    @Test
    public void testSuccessfulLoanUpdate() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        account = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        StaticHibernateUtil.flushSession();
        account = legacyAccountDao.getAccount(account.getAccountId());
        Assert.assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(), "Loan");

        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.add(account.getAccountActionDates().iterator().next());
        Date currentDate = startDate;
        PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(accntActionDates, TestUtils
                .createMoney("100.0"), null, account.getPersonnel(), "423423", Short.valueOf("1"), currentDate,
                currentDate);

        IntegrationTestObjectMother.applyAccountPayment(account, paymentData);
        Assert.assertEquals(((LoanBO) account).getLoanSummary().getFeesPaid(), TestUtils.createMoney("100.0"));
    }

    @Test
    public void testFailureApplyPayment() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        account = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        StaticHibernateUtil.flushSession();
        account = legacyAccountDao.getAccount(account.getAccountId());
        Assert.assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(), "Loan");
        for (AccountActionDateEntity actionDate : account.getAccountActionDates()) {
            if (actionDate.getInstallmentId().equals(Short.valueOf("1"))) {
                actionDate.setPaymentStatus(PaymentStatus.PAID);
            }
        }
        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.addAll(account.getAccountActionDates());
        PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(accntActionDates, TestUtils
                .createMoney("3000.0"), null, account.getPersonnel(), "423423", Short.valueOf("1"), startDate,
                startDate);

        try {
            account.applyPayment(paymentData);
            Assert.fail("should throw exception");
        } catch (AccountException be) {
            Assert.assertNotNull(be);
            Assert.assertEquals(be.getKey(), "errors.makePayment");
            Assert.assertTrue(true);
        }

    }
}
