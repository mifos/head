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

package org.mifos.domain.builders;

import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.dto.domain.MeetingUpdateRequest;

public class MeetingUpdateRequestBuilder {

    private Integer customerId = Integer.valueOf(1);
    private Integer customerVersionNo = Integer.valueOf(1);
    private Short recurrenceType = RecurrenceType.WEEKLY.getValue();
    private String meetingPlace = "builderSomewhere";
    private Short recursEvery = Short.valueOf("1");
    private Short dayOfWeek = WeekDay.MONDAY.getValue();
    private Short dayOfMonth;
    private Short dayOfWeekForWeekOfMonth;
    private Short weekOfMonth;

    public MeetingUpdateRequest build() {
        return new MeetingUpdateRequest(customerId, customerVersionNo, recurrenceType, meetingPlace, recursEvery, dayOfWeek, dayOfMonth, dayOfWeekForWeekOfMonth, weekOfMonth);
    }

    public MeetingUpdateRequestBuilder withCustomerId(Integer withCustomerId) {
        this.customerId = withCustomerId;
        return this;
    }

    public MeetingUpdateRequestBuilder with(WeekDay withDayOfWeek) {
        this.dayOfWeek = withDayOfWeek.getValue();
        return this;
    }

    public MeetingUpdateRequestBuilder withMeetingPlace(String withMeetingPlace) {
        this.meetingPlace = withMeetingPlace;
        return this;
    }

    public MeetingUpdateRequestBuilder withVersionNum(Integer versionNo) {
        this.customerVersionNo = versionNo;
        return this;
    }

    public MeetingUpdateRequestBuilder withRecurrence(RecurrenceType withRercurrenceType) {
        this.recurrenceType = withRercurrenceType.getValue();
        return this;
    }
}