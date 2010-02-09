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

package org.mifos.accounts.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanSummaryEntity;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanTrxnDetailEntityIntegrationTest extends MifosIntegrationTestCase {

    public LoanTrxnDetailEntityIntegrationTest() throws Exception {
        super();
    }

    private CustomerBO center;

    private CustomerBO group;

    private CustomerBO client;

    private AccountBO account;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
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

    public void testSuccessSetRunningBalance() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        Date sampleDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(sampleDate, meeting);
        account = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                sampleDate, loanOffering);
        StaticHibernateUtil.closeSession();
        account = new AccountPersistence().getAccount(account.getAccountId());
       Assert.assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(), "Loan");

        List<AccountActionDateEntity> accountActionsToBeUpdated = new ArrayList<AccountActionDateEntity>();
        accountActionsToBeUpdated.add(account.getAccountActionDates().iterator().next());
        PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(accountActionsToBeUpdated, new Money(
                TestObjectFactory.getMFICurrency(), "700.0"), null, account.getPersonnel(), "423423", Short
                .valueOf("1"), sampleDate, sampleDate);
        account.applyPaymentWithPersist(paymentData);
        StaticHibernateUtil.commitTransaction();

       Assert.assertEquals(1, account.getAccountPayments().size());
        AccountPaymentEntity payment = account.getAccountPayments().iterator().next();
       Assert.assertEquals(4, payment.getAccountTrxns().size());
        // Should we assert something about each of those transactions?

        LoanSummaryEntity loanSummaryEntity = ((LoanBO) account).getLoanSummary();
       Assert.assertEquals(loanSummaryEntity.getOriginalPrincipal().subtract(loanSummaryEntity.getPrincipalPaid()),
                ((LoanBO) account).getLoanActivityDetails().iterator().next().getPrincipalOutstanding());

    }

}
