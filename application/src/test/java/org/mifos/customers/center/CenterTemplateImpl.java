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

package org.mifos.customers.center;

import java.util.Date;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.CustomerTemplateImpl;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;

public class CenterTemplateImpl extends CustomerTemplateImpl implements CenterTemplate {
    private CustomerLevel customerLevel;
    private Date mfiJoiningDate;
    private Short officeId;
    private MeetingBO meeting;

    public CenterTemplateImpl(MeetingBO meeting, Short officeId) {
        super("TestCenter", CustomerStatus.CENTER_ACTIVE);
        this.meeting = meeting;
        this.officeId = officeId;
    }

    public CustomerLevel getCustomerLevel() {
        return this.customerLevel;
    }

    public Date getMfiJoiningDate() {
        return this.mfiJoiningDate;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public MeetingBO getMeeting() {
        return this.meeting;
    }
}
