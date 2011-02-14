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

package org.mifos.accounts.productdefinition.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;

import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanOfferingBOFixture {

    public static LoanOfferingBO createLoanOfferingBO(String prdOfferingName, String shortName) {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(WEEKLY, EVERY_WEEK,
                LOAN_INSTALLMENT, MONDAY));
        return TestObjectFactory.createLoanOffering(prdOfferingName, shortName, ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, frequency);
    }
}
