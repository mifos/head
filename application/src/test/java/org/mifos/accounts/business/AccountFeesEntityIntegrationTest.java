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

package org.mifos.accounts.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Date;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AccountFeesEntityIntegrationTest extends MifosIntegrationTestCase {

    public AccountFeesEntityIntegrationTest() throws Exception {
        super();
    }

    private AccountBO accountBO = null;
    private CustomerBO center = null;
    private CustomerBO group = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(accountBO);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testChangeFeesStatus() {
        accountBO = getLoanAccount();
        Set<AccountFeesEntity> accountFeesEntitySet = accountBO.getAccountFees();
        for (AccountFeesEntity accountFeesEntity : accountFeesEntitySet) {
            accountFeesEntity.changeFeesStatus(FeeStatus.INACTIVE, new Date(System.currentTimeMillis()));
           Assert.assertEquals(accountFeesEntity.getFeeStatus(), FeeStatus.INACTIVE.getValue());
        }
    }

    private AccountBO getLoanAccount() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loan", ApplicableTo.GROUPS, new Date(System
                .currentTimeMillis()), PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                new Date(System.currentTimeMillis()), loanOffering);
    }
}