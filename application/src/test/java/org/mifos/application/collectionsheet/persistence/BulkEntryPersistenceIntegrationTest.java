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

package org.mifos.application.collectionsheet.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class BulkEntryPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public BulkEntryPersistenceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private AccountPersistence accountPersistence;

    private CustomerBO center;

    private CustomerBO group;

    private CustomerBO client;

    private AccountBO account;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        accountPersistence = new AccountPersistence();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(account);
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetAccount() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        account = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        StaticHibernateUtil.closeSession();
        account = accountPersistence.getAccount(account.getAccountId());
       Assert.assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(), "Loan");
    }

    public void testSuccessfulApplyPayment() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        account = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        StaticHibernateUtil.closeSession();
        account = accountPersistence.getAccount(account.getAccountId());
       Assert.assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(), "Loan");

        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.add(account.getAccountActionDates().iterator().next());
        Date currentDate = startDate;
        PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(accntActionDates, new Money(
                TestObjectFactory.getMFICurrency(), "100.0"), null, account.getPersonnel(), "423423", Short
                .valueOf("1"), currentDate, currentDate);
        try {
            account.applyPaymentWithPersist(paymentData);
           Assert.assertEquals(((LoanBO) account).getLoanSummary().getFeesPaid().getAmountDoubleValue(), Double
                    .valueOf("100.0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        account.getAccountPayments().clear();

    }

    public void testSuccessfulLoanUpdate() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        account = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        StaticHibernateUtil.closeSession();
        account = accountPersistence.getAccount(account.getAccountId());
       Assert.assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(), "Loan");

        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.add(account.getAccountActionDates().iterator().next());
        Date currentDate = startDate;
        PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(accntActionDates, new Money(
                TestObjectFactory.getMFICurrency(), "100.0"), null, account.getPersonnel(), "423423", Short
                .valueOf("1"), currentDate, currentDate);

        account.applyPaymentWithPersist(paymentData);
        StaticHibernateUtil.commitTransaction();
       Assert.assertEquals(((LoanBO) account).getLoanSummary().getFeesPaid().getAmountDoubleValue(), Double.valueOf("100.0"));
    }

    public void testFailureApplyPayment() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        account = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        StaticHibernateUtil.closeSession();
        account = accountPersistence.getAccount(account.getAccountId());
       Assert.assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(), "Loan");
        for (AccountActionDateEntity actionDate : account.getAccountActionDates()) {
            if (actionDate.getInstallmentId().equals(Short.valueOf("1"))) {
                actionDate.setPaymentStatus(PaymentStatus.PAID);
            }
        }
        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.addAll(account.getAccountActionDates());
        PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(accntActionDates, new Money(
                TestObjectFactory.getMFICurrency(), "3000.0"), null, account.getPersonnel(), "423423", Short
                .valueOf("1"), startDate, startDate);

        try {
            account.applyPaymentWithPersist(paymentData);
           Assert.assertTrue(false);
        } catch (AccountException be) {
            Assert.assertNotNull(be);
           Assert.assertEquals(be.getKey(), "errors.makePayment");
           Assert.assertTrue(true);
        }

    }
}
