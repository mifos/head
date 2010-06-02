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

package org.mifos.application.servicefacade;

import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;

public class MeetingUpdateRequest {

    private final Integer customerId;
    private final Integer customerVersionNo;
    private final RecurrenceType recurrenceType;
    private final String meetingPlace;
    private final Short recursEvery;
    private final WeekDay weekDay;
    private final Short dayOfMonth;
    private final WeekDay monthWeek;
    private final RankOfDay rankOfDay;

    public MeetingUpdateRequest(Integer customerId, Integer customerVersionNo, RecurrenceType recurrenceType,
            String meetingPlace, Short recursEvery, WeekDay weekDay, Short dayOfMonth, WeekDay monthWeek, RankOfDay rankOfDay) {
        this.customerId = customerId;
        this.customerVersionNo = customerVersionNo;
        this.recurrenceType = recurrenceType;
        this.meetingPlace = meetingPlace;
        this.weekDay = weekDay;
        this.recursEvery = recursEvery;
        this.dayOfMonth = dayOfMonth;
        this.monthWeek = monthWeek;
        this.rankOfDay = rankOfDay;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public Integer getCustomerVersionNo() {
        return this.customerVersionNo;
    }

    public RecurrenceType getRecurrenceType() {
        return this.recurrenceType;
    }

    public String getMeetingPlace() {
        return this.meetingPlace;
    }

    public Short getRecursEvery() {
        return this.recursEvery;
    }

    public WeekDay getWeekDay() {
        return this.weekDay;
    }

    public Short getDayOfMonth() {
        return this.dayOfMonth;
    }

    public WeekDay getMonthWeek() {
        return this.monthWeek;
    }

    public RankOfDay getRankOfDay() {
        return this.rankOfDay;
    }
}