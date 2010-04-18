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

package org.mifos.application.meeting;

import java.util.Date;

import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.util.helpers.DateUtils;

public class MeetingTemplateImpl implements MeetingTemplate {
    private RecurrenceType recurrenceType;
    private Short dateNumber;
    private WeekDay weekDay;
    private RankOfDay rankType;
    private Short recurAfter;
    private Date startDate;
    private MeetingType meetingType;
    private String meetingPlace;

    private MeetingTemplateImpl(Date startDate, WeekDay weekDay) {
        this.recurrenceType = RecurrenceType.WEEKLY;
        this.weekDay = weekDay;
        this.meetingType = MeetingType.CUSTOMER_MEETING;
        this.startDate = startDate;
        this.recurAfter = 1;
        this.meetingPlace = "SomeTestMeetingPlaceLocation";
    }

    private MeetingTemplateImpl(Date startDate) {
        this.recurrenceType = RecurrenceType.WEEKLY;
        this.weekDay = DateUtils.getWeekDayForDate(startDate);
        this.meetingType = MeetingType.CUSTOMER_MEETING;
        this.startDate = startDate;
        this.recurAfter = 1;
        this.meetingPlace = "SomeTestMeetingPlaceLocation";
    }

    public RecurrenceType getReccurenceType() {
        return this.recurrenceType;
    }

    public Short getDateNumber() {
        return this.dateNumber;
    }

    public WeekDay getWeekDay() {
        return this.weekDay;
    }

    public RankOfDay getRankType() {
        return this.rankType;
    }

    public Short getRecurAfter() {
        return this.recurAfter;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public MeetingType getMeetingType() {
        return this.meetingType;
    }

    public String getMeetingPlace() {
        return this.meetingPlace;
    }

    public static MeetingTemplateImpl createWeeklyMeetingTemplate() {
        return createWeeklyMeetingTemplateOnMondaysStartingFrom(new Date());
    }

    public static MeetingTemplateImpl createWeeklyMeetingTemplateOnMondaysStartingFrom(Date startDate) {
        return new MeetingTemplateImpl(startDate, WeekDay.MONDAY);
    }

    public static MeetingTemplateImpl createWeeklyMeetingTemplateStartingFrom(Date startDate) {
        return new MeetingTemplateImpl(startDate);
    }

}
