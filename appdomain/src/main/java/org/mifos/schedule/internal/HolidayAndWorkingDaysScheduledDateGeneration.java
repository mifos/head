/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.schedule.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.calendar.BasicHolidayStrategy;
import org.mifos.calendar.BasicWorkingDayStrategy;
import org.mifos.calendar.DateAdjustmentStrategy;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;

public class HolidayAndWorkingDaysScheduledDateGeneration implements ScheduledDateGeneration {

    //TODO KRP: implement this
    @Override
    public List<DateTime> generateScheduledDatesThrough(DateTime lastScheduledDate, DateTime throughDate,
            ScheduledEvent scheduledEvent, boolean isCustomerSchedule) {
        return null;
    }

    private final List<Days> workingDays;
    private final List<Holiday> upcomingHolidays;

    public HolidayAndWorkingDaysScheduledDateGeneration(final List<Days> workingDays, final List<Holiday> upcomingHolidays) {
        this.workingDays = workingDays;
        this.upcomingHolidays = upcomingHolidays;
    }

    /**
     * @param lastScheduledDate starting point for schedule generation
     */
    @Override
    public List<DateTime> generateScheduledDates(final int occurences, final DateTime lastScheduledDate,
            final ScheduledEvent scheduledEvent, boolean isCustomerSchedule) {

        DateTime matchingDayOfWeekDate = lastScheduledDate;
        boolean isDailyMeeting = scheduledEvent instanceof DailyScheduledEvent;
        
        if (!isDailyMeeting) {
            matchingDayOfWeekDate = scheduledEvent.nearestMatchingDateBeginningAt(lastScheduledDate);
            if (isCustomerSchedule) {
                matchingDayOfWeekDate = scheduledEvent.nearestMatchNotTakingIntoAccountScheduleFrequency(lastScheduledDate);    
            }
        }

        List<DateTime> scheduledDates = new ArrayList<DateTime>();

        // Prepare a list of dates scheduled without adjusting working days/holidays.
        // It is used for computing next dates by 'ScheduledEvent' to omit problems
        // when adjusting a date changes a month (MIFOS-3584).
        List<DateTime> scheduledWithoutAdjustments = new ArrayList<DateTime>();
        DateTime withoutAdjustment = new DateTime(matchingDayOfWeekDate);
        scheduledWithoutAdjustments.add(withoutAdjustment);
        for (int i = 0; i < occurences; i++) {
            withoutAdjustment = scheduledEvent.nextEventDateAfter(withoutAdjustment);
            scheduledWithoutAdjustments.add(withoutAdjustment);
        }
        
        HashSet<DateTime> generatedDates = new HashSet<DateTime>();
        DateTime latestGeneratedDate = scheduledWithoutAdjustments.get(0);
        for (int i = 0; i < occurences; i++) {
            
            DateAdjustmentStrategy workingDay = new BasicWorkingDayStrategy(workingDays);
            DateTime adjustedForWorkingDay = workingDay.adjust(latestGeneratedDate);

            while (isDailyMeeting && generatedDates.contains(adjustedForWorkingDay)) {
                adjustedForWorkingDay = workingDay.adjust(adjustedForWorkingDay.plusDays(1));
            }
            
            DateAdjustmentStrategy holidayAdjustment = new BasicHolidayStrategy(upcomingHolidays, workingDays,
                    scheduledEvent);
            DateTime adjustedForHolidays = holidayAdjustment.adjust(adjustedForWorkingDay);
            
            generatedDates.add(adjustedForHolidays);
            scheduledDates.add(adjustedForHolidays);
            latestGeneratedDate = scheduledEvent.nextEventDateAfter(scheduledWithoutAdjustments.get(i));
        }

        return scheduledDates;
    }
}