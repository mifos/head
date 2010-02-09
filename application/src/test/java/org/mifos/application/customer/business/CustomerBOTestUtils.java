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

package org.mifos.application.customer.business;

import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.util.helpers.YesNoFlag;

public class CustomerBOTestUtils {

    public static void setCustomerStatus(final CustomerBO customer, final CustomerStatusEntity customerStatusEntity) {
        customer.setCustomerStatus(customerStatusEntity);
    }

    public static void setCustomerMeeting(final CustomerBO customer, final CustomerMeetingEntity customerMeeting) {
        customer.setCustomerMeeting(customerMeeting);
    }

    public static void setPersonnel(final CustomerBO customer, final PersonnelBO personnel) {
        customer.setPersonnel(personnel);
    }

    public static void setDisplayName(final CustomerBO customer, final String displayName) {
        customer.setDisplayName(displayName);
    }

    public static void setUpdatedFlag(final CustomerMeetingEntity customerMeetingEntity, final Short updatedFlag) {
        customerMeetingEntity.setUpdatedFlag(updatedFlag);
    }

    public static void setUpdatedMeeting(final CustomerMeetingEntity customerMeetingEntity,
            final MeetingBO updatedMeeting) {
        customerMeetingEntity.setUpdatedFlag(YesNoFlag.YES.getValue());
        customerMeetingEntity.setUpdatedMeeting(updatedMeeting);
    }

    public static void addFeesTrxnDetail(final CustomerTrxnDetailEntity accountTrxnEntity, final FeesTrxnDetailEntity feeTrxn) {
        accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
    }

}
