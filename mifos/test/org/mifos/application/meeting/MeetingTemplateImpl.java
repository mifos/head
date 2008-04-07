package org.mifos.application.meeting;

import java.util.Date;

import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;

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
public class MeetingTemplateImpl implements MeetingTemplate {
    private RecurrenceType recurrenceType;
    private Short dateNumber;
    private WeekDay weekDay;
    private RankType rankType;
    private Short recurAfter;
    private Date startDate;
    private MeetingType meetingType;
    private String meetingPlace;

    private MeetingTemplateImpl(Date startDate) {
        this.recurrenceType = RecurrenceType.WEEKLY;
        this.weekDay = WeekDay.MONDAY;
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

    public RankType getRankType() {
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
        return createWeeklyMeetingTemplateStartingFrom(new Date());
    }

    public static MeetingTemplateImpl createWeeklyMeetingTemplateStartingFrom(Date startDate) {
    	return new MeetingTemplateImpl(startDate);
    }
}
