package org.mifos.application.customer.center;

import java.util.Date;

import org.mifos.application.customer.CustomerTemplateImpl;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;

/**
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * <p/>
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license
 * and how it is applied.
 */
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
