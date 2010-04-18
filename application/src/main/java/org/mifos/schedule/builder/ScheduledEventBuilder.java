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

package org.mifos.schedule.builder;

import org.joda.time.DateTime;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.schedule.ScheduledEvent;

/**
 * Uses the Builder pattern to build a {@link ScheduledEvent}.
 *
 * <p>This example builds a weekly scheduled event occurring
 * every other Monday:</p>
 * <pre>
 * ScheduledEvent event = new ScheduledEventBuilder()
 *                            .weekly(WeekDay.MONDAY)
 *                            .every(2)
 *                            .build();
 * </pre>
 * <p>This example builds a monthly scheduled event occurring on the 5th of every month:</p>
 * <pre>
 * ScheduledEvent event = new ScheduledEventBuilder()
 *                            .monthlyOnDate(5)
 *                            .build();
 * </pre>
 * <p>To build a monthly event occurring on the second Tuesday of very 3rd month irrespective of holidays:</p>
 * <pre>
 * ScheduledEvent event = new ScheduledEventBuilder()
 *                           .monthlyOnWeekAndWeekday(RankType.THIRD, WeekDay.Tuesday)
 *                           .every(3)
 *                           .build();
 * </pre>
 * <p> An event occurring every otrher working day
 * ScheduledEvent event = new ScheduledEventBuilder()
 *                           .daily()
 *                           .every(2)
 *                           .build();
 * </pre>
 *
 */
public abstract class ScheduledEventBuilder {

    protected Short every = (short) 1;

    public ScheduledEvent build() {
        throw new IllegalArgumentException ("Schedule occurrence (weekly or monthly) has not been set.");
    }

    public ScheduledEventBuilder daily() {
        return new DailyScheduledEventBuilder();
    }

    public ScheduledEventBuilder weekly(WeekDay dayOfWeek) {
        assert dayOfWeek != null;
        return new WeeklyScheduleBuilder(dayOfWeek);
    }

    public ScheduledEventBuilder monthlyOnWeekAndWeekday(RankOfDay weekOfMonth, WeekDay dayOfWeek) {
        assert weekOfMonth != null;
        assert dayOfWeek != null;
        return new MonthlyOnWeekAndWeekDayScheduledEventBuilder (weekOfMonth, dayOfWeek);
    }

    public ScheduledEventBuilder monthlyOnDate (Integer dayOfMonth) {
        assert dayOfMonth != null;
        assert (dayOfMonth >= 1) && (dayOfMonth <= 31);
        return new MonthlyOnDateScheduledEventBuilder(dayOfMonth);
    }

    public ScheduledEventBuilder every(final Integer recurAfter) {
        assert recurAfter != null;
        assert recurAfter >= 1;
        this.every = recurAfter.shortValue();
        return this;
    }

    /*
     * Move these to factory that creates a schedule generator?
     *
    public ScheduleBuilder startingToday() {
        if (! (null == this.startDate) ) {
            throw new IllegalArgumentException("Starting date was already set");
        }
       this.startDate = new DateTime().toDate();
        return this;
    }

    public ScheduleBuilder starting(Date startDate) {
        if (! (null == this.startDate) ) {
            throw new IllegalArgumentException("Starting date was already set");
        }
        this.startDate = startDate;
        return this;
    }

    public ScheduleBuilder adjustingForHolidays() {
        this.adjustForHolidays = true;
        return this;
    }

    public ScheduleBuilder notAdjustingForHolidays() {
        this.adjustForHolidays = false;
        return this;
    }

    public ScheduleBuilder endingOn (Date day) {
        if (null == day) {
            throw new IllegalArgumentException("Ending date cannot be null.");
        }
        if ( !(null == startDate) && (day.compareTo(startDate) < 0) ) {
            throw new IllegalArgumentException("Ending date cannot be earlier than the schedule's starting date");
        }
        this.endDate = day;
        return this;
    }

    public ScheduleBuilder numberOfOccurrences (Integer count) {
        if (null == count) {
            throw new IllegalArgumentException("The number of scheduled dates cannot be null.");
        }
        if (count < 0) {
            throw new IllegalArgumentException("The number of scheduled dates cannot be negative.");
        }
        this.numberOfOccurrences = count;
        return this;
    }
    */

    protected static WeekDay dateTimeToWeekDay(final DateTime dateTime) {
        final int dayOfWeekUsingDateTimeApi = dateTime.getDayOfWeek();

        if (dayOfWeekUsingDateTimeApi == 7) {
            return WeekDay.SUNDAY;
        }

        return WeekDay.getWeekDay(dayOfWeekUsingDateTimeApi + 1);
    }

    protected void validateParameters() {
        if ( null == every ) {
            throw new IllegalArgumentException("Scheduled number of intervals to recur after has not been specified.");
        }
        /*
        if ( null == startDate ) {
            throw new IllegalArgumentException("Scheduled start date has not been specified.");
        }
        if ( (null == endDate) && (null == numberOfOccurrences) ) {
            throw new IllegalArgumentException("Must specify either number of occurrences or end date.");
        }
        if ( !(null == endDate) && !(null == numberOfOccurrences) ) {
            throw new IllegalArgumentException("Cannot specify both schedule number of occurrences and end date");
        }
        */
    }

}
