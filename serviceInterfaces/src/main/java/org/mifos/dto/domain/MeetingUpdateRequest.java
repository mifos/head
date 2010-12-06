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

package org.mifos.dto.domain;

public class MeetingUpdateRequest {

    private final Integer customerId;
    private final Integer customerVersionNo;
    private final Short recurrenceType;
    private final String meetingPlace;
    private final Short recursEvery;
    private final Short weekDay;
    private final Short dayOfMonth;
    private final Short monthWeek;
    private final Short rankOfDay;

    public MeetingUpdateRequest(Integer customerId, Integer customerVersionNo, Short recurrenceType,
            String meetingPlace, Short recursEvery, Short weekDay, Short dayOfMonth, Short monthWeek, Short rankOfDay) {
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

    public Short getRecurrenceType() {
        return this.recurrenceType;
    }

    public String getMeetingPlace() {
        return this.meetingPlace;
    }

    public Short getRecursEvery() {
        return this.recursEvery;
    }

    public Short getWeekDay() {
        return this.weekDay;
    }

    public Short getDayOfMonth() {
        return this.dayOfMonth;
    }

    public Short getMonthWeek() {
        return this.monthWeek;
    }

    public Short getRankOfDay() {
        return this.rankOfDay;
    }
}