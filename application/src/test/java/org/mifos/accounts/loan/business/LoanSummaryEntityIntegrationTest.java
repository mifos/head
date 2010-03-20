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

package org.mifos.accounts.loan.business;

import java.util.Date;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanSummaryEntityIntegrationTest extends MifosIntegrationTestCase {

    public LoanSummaryEntityIntegrationTest() throws Exception {
        super();
    }

    protected AccountBO accountBO = null;
    protected CustomerBO center = null;
    protected CustomerBO group = null;

    public void testDecreaseBy() {
        accountBO = getLoanAccount();
        LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO).getLoanSummary();
        Money principal = TestUtils.createMoney(100);
        Money interest = TestUtils.createMoney(10);
        Money penalty = TestUtils.createMoney(20);
        Money fees = TestUtils.createMoney(30);
        Money originalPrincipal = loanSummaryEntity.getOriginalPrincipal();
        Money originalInterest = loanSummaryEntity.getOriginalInterest();
        Money originalFees = loanSummaryEntity.getOriginalFees();
        Money originalPenalty = loanSummaryEntity.getOriginalPenalty();
        loanSummaryEntity.decreaseBy(principal, interest, penalty, fees);
       Assert.assertEquals(loanSummaryEntity.getOriginalPrincipal().add(principal), originalPrincipal);
       Assert.assertEquals(loanSummaryEntity.getOriginalInterest().add(interest), originalInterest);
       Assert.assertEquals(loanSummaryEntity.getOriginalFees().add(fees), originalFees);
       Assert.assertEquals(loanSummaryEntity.getOriginalPenalty().add(penalty), originalPenalty);
    }

    public void testUpdatePaymentDetails() {
        accountBO = getLoanAccount();
        LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO).getLoanSummary();
        Money principal = TestUtils.createMoney(100);
        Money interest = TestUtils.createMoney(10);
        Money penalty = TestUtils.createMoney(20);
        Money fees = TestUtils.createMoney(30);
        loanSummaryEntity.updatePaymentDetails(principal, interest, penalty, fees);
       Assert.assertEquals(loanSummaryEntity.getPrincipalPaid(), principal);
       Assert.assertEquals(loanSummaryEntity.getInterestPaid(), interest);
       Assert.assertEquals(loanSummaryEntity.getFeesPaid(), fees);
       Assert.assertEquals(loanSummaryEntity.getPenaltyPaid(), penalty);
    }

    private AccountBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    @Override
    protected void tearDown() throws Exception {
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
        group = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, group.getCustomerId());
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
        TestObjectFactory.cleanUp(accountBO);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);

        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

}
