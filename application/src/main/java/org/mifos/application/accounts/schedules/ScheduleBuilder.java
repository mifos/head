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
 * Uses the Builder pattern to build a {@link Schedule}. This example builds a schedule occurring
 * every other Monday starting on 2010-01-25 and continuing for 13 occurrences, without adjusting
 * for holidays or repayment moratoria:
 * <pre>
 * Schedule schedule = new ScheduleBuilder()
 *                        .weekly()
 *                        .starting(DateUtils.getDate(2010,00,25)
 *                        .every(2)
 *                        .numberOfOccurrences(13)
 *                        .notAdjustingForHolidays()
 *                        .build();
 * </pre>
 * <h3>Defaults</h3>
 * <ul>
 *   <li>Whether to adjust for holidays defaults to true. To override, call <code>notAdjustedForHolidays()</code></li>
 *   <li>For weekly schedules or monthly schedules meeting in a given week, the scheduled day of the week
 *       defaults to that of the starting date. After either <code>weekly()</code> or 
 *       <code>monthly</code> + <code>weekOfMonth()</code> have been specified, this can be overridden by
 *       calling onDayOfWeek().</li>
 * </ul>
 * 
 * <p>The following parameters must be specified unless described as optional:</p>
 * <dl>
 *   <dt>For all schedules:
 *     <dd>
 *       <ul>
 *         <li>Exactly one of <code>weekly()</code> or <code>monthly()</code> (must be called first) </li>
 *         <li><code>every(Int)</code></li>
 *         <li><code>startingOn(Date)</code></li>
 *         <li>Either <code>numberOfOccurrences(Short)</code> or <code>endingOn(Date) but not both</code></li>
 *         <li><code>adjustedForHolidays()</code> or <code>notAdjustedForHolidays()</code> (optional. Default is to
 *             adjust for holidays)
 *       </ul>
 *     </dd>
 *   </dt>
 *   <dt> For weekly schedules:
 *     <dd>
 *       <ul>
 *         <li><code>onDayOfWeek(WeekDay)</code> (optional -- defaults to starting date's day of week)</li>
 *       </ul>
 *     </dd>
 *   </dt>
 *   <dt> For monthly schedules:
 *     <dd>
 *       <ul>
 *         <li>Exactly one of <code>onDayOfMonth(Int)</code> or <code>onWeekOfMonth(Int)</code> (must be called second)</li>
 *         <li>if <code>onWeekOfMonth()</code>, then <code>onDayOfWeek()</code> (optional -- defaults to the
 *             starting date's day of week)</li>
 *       </ul>
 *     </dd>
 *   </dt>
 *   <dt> For daily schedules:
 *     <dd> No additional information need be specified.</dd>
 *   </dt>
 * </dl>
 *     
 * 
 * <p>Constraints:</p>
 * <ul>
 *   <li>Specifying inconsistent parameters or specifying parameters not in the order
 *       specified above raises an <code>IllegalArgumentException</code>.</li>
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
    
    protected abstract void onDayOfWeek();
    
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
