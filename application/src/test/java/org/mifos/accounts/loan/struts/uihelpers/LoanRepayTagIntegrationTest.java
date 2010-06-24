/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.loan.struts.uihelpers;

import static junitx.framework.StringAssert.assertContains;

import java.util.Date;

import org.mifos.accounts.loan.business.LoanActivityEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class LoanRepayTagIntegrationTest extends MifosIntegrationTestCase {
    public LoanRepayTagIntegrationTest() throws Exception {
        super();
    }

    CustomerBO center;
    CustomerBO group;
    CustomerBO client;
    LoanBO accountBO;
    UserContext userContext;

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(accountBO);
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        userContext = TestObjectFactory.getContext();
        super.setUp();
    }

    public void testX() {

    }

    public void ignore_testCreateInstallmentRow() {

        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        StaticHibernateUtil.flushAndCloseSession();
        accountBO = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        LoanRepaymentTag loanRepaymentTag = new LoanRepaymentTag();
        loanRepaymentTag.locale = userContext.getPreferredLocale();
        assertContains("100.0", loanRepaymentTag.createInstallmentRow(
                (LoanScheduleEntity) accountBO.getAccountActionDate(Short.valueOf("1")), false).toString());
    }

    public void ignore_testcreateRunningBalanceRow() {
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        StaticHibernateUtil.flushAndCloseSession();

        accountBO = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());

        LoanRepaymentTag loanRepaymentTag = new LoanRepaymentTag();
        loanRepaymentTag.locale = userContext.getPreferredLocale();
        assertContains("90.0", loanRepaymentTag.createRunningBalanceRow(
                (LoanScheduleEntity) accountBO.getAccountActionDate(Short.valueOf("1")), new Money(getCurrency(), "50"),
                new Money(getCurrency(), "20"), new Money(getCurrency(), "20")).toString());
    }

    private LoanBO getLoanAccount(AccountState state, Date startDate, int disbursalType) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        accountBO = TestObjectFactory.createLoanAccountWithDisbursement("99999999999", group, state, startDate,
                loanOffering, disbursalType);
        LoanActivityEntity loanActivity = new LoanActivityEntity(accountBO, TestObjectFactory.getPersonnel(userContext
                .getId()), "testing", new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(getCurrency(), "100"),
                new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), startDate);
        accountBO.addLoanActivity(loanActivity);
        TestObjectFactory.updateObject(accountBO);
        return accountBO;
    }
}
