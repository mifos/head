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

package org.mifos.application.accounts.offsetting;

import java.util.Date;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.TestObjectFactory;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

public class OffsetAccountBOIntegrationTest extends MifosIntegrationTestCase {

    public OffsetAccountBOIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    protected LoanBO accountBO = null;
    protected CustomerBO center = null;
    protected CustomerBO group = null;
    protected AccountPersistence accountPersistence;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        accountBO = createLoanAccount();
        accountPersistence = new AccountPersistence();

        /**
         * if (super.accountBO == null) { //Calling super setup on the
         * TestAccount //with a currently existing TestAccount causes //a
         * "duplicate AccountBO object insertion error". //First cleaning up if
         * a TestAccount exists then get //get a new TestAccount. super.setUp();
         * }
         */
    }

    public void testLoanAccountBOOfsetAllowablePersistence() {
        assertTrue(accountBO.getOffsettingAllowable().intValue() == 1);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(accountBO);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
            accountPersistence = null;
        } catch (Exception e) {
            // throwing here tends to mask failures
            e.printStackTrace();
        }
        super.tearDown();
    }

    private LoanBO createLoanAccount() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("OffsetCenter", meeting);
        group = TestObjectFactory.createGroupUnderCenter("OffsetGroup1", CustomerStatus.GROUP_ACTIVE, center);

        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("OffsetLoan1", ApplicableTo.GROUPS,
                new Date(System.currentTimeMillis()), PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT,
                meeting);

        return TestObjectFactory.createLoanAccount("42423142342", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                new Date(System.currentTimeMillis()), loanOffering);
    }
}
