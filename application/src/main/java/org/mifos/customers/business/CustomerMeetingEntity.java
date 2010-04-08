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

package org.mifos.customers.business;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingTypeEntity;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.AbstractEntity;

/**
 * This class encapsulate the customer meeting
 */
public class CustomerMeetingEntity extends AbstractEntity {

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private final Integer custMeetingId;
    private MeetingBO meeting;
    private CustomerBO customer;
    private Short updatedFlag;
    private MeetingBO updatedMeeting;

    /**
     * default constructor for hibernate usage
     */
    protected CustomerMeetingEntity() {
        this.custMeetingId = null;
        this.customer = null;
    }

    /**
     * minimal constructor for builder
     */
    public CustomerMeetingEntity(final MeetingBO meeting, final Short updatedFlag) {
        this.custMeetingId = null;
        this.meeting = meeting;
        this.updatedFlag = updatedFlag;
    }

    public CustomerMeetingEntity(final CustomerBO customer, final MeetingBO meeting) {
        meeting.setMeetingType(new MeetingTypeEntity(MeetingType.CUSTOMER_MEETING));
        this.customer = customer;
        this.meeting = meeting;
        this.custMeetingId = null;
        this.updatedFlag = YesNoFlag.NO.getValue();
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public MeetingBO getMeeting() {
        return meeting;
    }

    void setMeeting(final MeetingBO meeting) {
        this.meeting = meeting;
    }

    public Short getUpdatedFlag() {
        return updatedFlag;
    }

    void setUpdatedFlag(final Short updatedFlag) {
        this.updatedFlag = updatedFlag;
    }

    public MeetingBO getUpdatedMeeting() {
        return updatedMeeting;
    }

    void setUpdatedMeeting(final MeetingBO updatedMeeting) {
        this.updatedMeeting = updatedMeeting;
    }

    public void setCustomer(final CustomerBO customer) {
        this.customer = customer;
    }
}
