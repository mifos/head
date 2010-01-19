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

package org.mifos.application.accounts.loan.struts.uihelpers;

import static junitx.framework.StringAssert.assertContains;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanActivityTagIntegrationTest extends MifosIntegrationTestCase {

    public LoanActivityTagIntegrationTest() throws Exception {
        super();
    }

    CustomerBO center;
    CustomerBO group;
    CustomerBO client;
    AccountBO accountBO;
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

    public void testBuildLeftHeaderRows() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        List<LoanActivityView> activityViews = new LoanBusinessService().getAllActivityView(accountBO
                .getGlobalAccountNum());
        assertContains("100", new LoanActivityTag().buildLeftHeaderRows(activityViews.get(0)).toString());

    }

    public void testBuildRightHeaderRows() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        List<LoanActivityView> activityViews = new LoanBusinessService().getAllActivityView(accountBO
                .getGlobalAccountNum());
        assertContains("100", new LoanActivityTag().buildRightHeaderRows(activityViews.get(0)).toString());

    }

    private AccountBO getLoanAccount(final AccountState state, final Date startDate, final int disbursalType) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        accountBO = TestObjectFactory.createLoanAccountWithDisbursement("99999999999", group, state, startDate,
                loanOffering, disbursalType);
        LoanActivityEntity loanActivity = new LoanActivityEntity(accountBO, TestObjectFactory.getPersonnel(userContext
                .getId()), "testing", new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(
                getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(
                getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), startDate);
        ((LoanBO) accountBO).addLoanActivity(loanActivity);
        TestObjectFactory.updateObject(accountBO);
        return accountBO;
    }
}
