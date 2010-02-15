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

package org.mifos.application.accounts.schedules;

import java.util.Date;

import org.joda.time.DateTime;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.WeekDay;

/**
 * Uses the Builder pattern to build a {@link Schedule}.
 * 
 * <p>Constraints:</p>
 * <ul>
 *   <li>One of methods daily(), weekly(), monthly() must be called first.</li>
 *   <li>Specifying inconsistent parameters raises IllegalArgumentException.</li>
 * </ul>
 *
 */
public abstract class ScheduleBuilder {
    
    protected Short recurAfter;
    protected Date startDate;
    protected Boolean adjustForHolidays = true;
    protected Integer numberOfOccurrences;
    protected Date endDate;
    
    public abstract Schedule build();
    
    protected abstract void setDayOfWeek();
    
    public ScheduleBuilder daily() {
        return new DailyScheduleBuilder();
    }

    public ScheduleBuilder weekly() {
        return new WeeklyScheduleBuilder();
    }
    
    public MonthlyByWeekScheduleBuilder monthly(RankType weekOfMonth) {
        return new MonthlyByWeekScheduleBuilder().weekOfMonth(weekOfMonth);
    }
    
    public MonthlyByDayScheduleBuilder monthly(Integer dayOfMonth) {
        return new MonthlyByDayScheduleBuilder().dayOfMonth(dayOfMonth);
    }

    public ScheduleBuilder every(final Integer recurAfter) {
        this.recurAfter = recurAfter.shortValue();
        return this;
    }

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
        setDayOfWeek();
        return this;
    }
    
    protected static WeekDay dateTimeToWeekDay(final DateTime dateTime) {
        final int dayOfWeekUsingDateTimeApi = dateTime.getDayOfWeek();

        if (dayOfWeekUsingDateTimeApi == 7) {
            return WeekDay.SUNDAY;
        }

        return WeekDay.getWeekDay(dayOfWeekUsingDateTimeApi + 1);
    }
    
    protected void validateParameters() {
        if ( null == recurAfter ) {
            throw new IllegalArgumentException("Scheduled number of intervals to recur after has not been specified."); 
        }
        if ( null == startDate ) {
            throw new IllegalArgumentException("Scheduled start date has not been specified."); 
        }
        if ( (null == endDate) && (null == numberOfOccurrences) ) {
            throw new IllegalArgumentException("Must specify either number of occurrences or end date.");
        }
        if ( !(null == endDate) && !(null == numberOfOccurrences) ) {
            throw new IllegalArgumentException("Cannot specify both schedule number of occurrences and end date");
        }
    }

}
