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

package org.mifos.schedule;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.schedule.internal.DailyScheduledEvent;
import org.mifos.schedule.internal.MonthlyOnDateScheduledEvent;
import org.mifos.schedule.internal.MonthlyOnWeekAndWeekDayScheduledEvent;
import org.mifos.schedule.internal.WeeklyScheduledEvent;

public class ScheduledEventFactory {

    public static ScheduledEvent createScheduledEvent(final RecurrenceType period, final int every,
            final int dayOfWeek, final int dayOfMonth, final int weekOfMonth) {

        ScheduledEvent recurringEvent;
        switch (period) {
        case WEEKLY:
            recurringEvent = new WeeklyScheduledEvent(every, dayOfWeek);
            break;
        case MONTHLY:
            if (weekOfMonth != 0 && dayOfWeek != 0) {
                recurringEvent = new MonthlyOnWeekAndWeekDayScheduledEvent(every, weekOfMonth, dayOfWeek);
            }
            else if (dayOfMonth != 0) {
                recurringEvent = new MonthlyOnDateScheduledEvent(every, dayOfMonth);
            } else {
                throw new IllegalStateException("not enough information to create a monthly scheduled event");
            }
            break;
        case DAILY:
            recurringEvent = new DailyScheduledEvent(every);
            break;
        default:
            throw new IllegalStateException("unknown recurring period type");
        }

        return recurringEvent;
    }

    public static ScheduledEvent createScheduledEventFrom(final MeetingBO meeting) {

        RecurrenceType period = meeting.getRecurrenceType();
        int every = meeting.getRecurAfter();
        int dayOfWeek = 0;
        if (meeting.getMeetingDetails().getWeekDay() != null) {
            dayOfWeek = WeekDay.getJodaDayOfWeekThatMatchesMifosWeekDay(meeting.getMeetingDetails().getWeekDay().getValue());
        }

        int dayOfMonth = 0;
        if (meeting.getMeetingDetails().getDayNumber() != null) {
            dayOfMonth = meeting.getMeetingDetails().getDayNumber();
        }
        int weekOfMonth = 0;
        if (meeting.getMeetingDetails().getWeekRank() != null) {
            weekOfMonth = meeting.getMeetingDetails().getWeekRank().getValue();
        }

        return createScheduledEvent(period, every, dayOfWeek, dayOfMonth, weekOfMonth);
    }

    /**
     * create scheduled event based on meeting details but override recurrence details of meeting with data passed in.
     */
    public static ScheduledEvent createScheduledEventFrom(MeetingBO customerMeeting, MeetingBO overridingDetails) {

        RecurrenceType period = customerMeeting.getRecurrenceType();
        int every = overridingDetails.getRecurAfter().intValue();

        int dayOfWeek = 0;
        if (customerMeeting.getMeetingDetails().getWeekDay() != null) {
            dayOfWeek = WeekDay.getJodaDayOfWeekThatMatchesMifosWeekDay(customerMeeting.getMeetingDetails().getWeekDay().getValue());
        }

        int dayOfMonth = 0;
        if (customerMeeting.getMeetingDetails().getDayNumber() != null) {
            dayOfMonth = customerMeeting.getMeetingDetails().getDayNumber();
        }
        int weekOfMonth = 0;
        if (customerMeeting.getMeetingDetails().getWeekRank() != null) {
            weekOfMonth = customerMeeting.getMeetingDetails().getWeekRank().getValue();
        }

        return createScheduledEvent(period, every, dayOfWeek, dayOfMonth, weekOfMonth);
    }
}