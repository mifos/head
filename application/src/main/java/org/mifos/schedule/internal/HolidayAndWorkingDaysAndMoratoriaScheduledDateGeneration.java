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

package org.mifos.schedule.internal;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.calendar.ListOfDatesAdjustmentStrategy;
import org.mifos.calendar.MoratoriumStrategy;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;

/**
 *
 *
 */
public class HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration implements ScheduledDateGeneration {

    private final List<Days> workingDays;
    private final List<Holiday> upcomingHolidays;

    public HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration
                    (final List<Days> workingDays, final List<Holiday> upcomingHolidays) {
        this.workingDays = workingDays;
        this.upcomingHolidays = upcomingHolidays;
    }

    public List<DateTime> generateScheduledDates
                                (int occurences, DateTime lastScheduledDate, ScheduledEvent scheduledEvent) {

        HolidayAndWorkingDaysScheduledDateGeneration generatorForHolidaysAndWorkingDays
            = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays, new ArrayList<Holiday>());

        //Generate dates adjusted only for working days
        List<DateTime> unAdjustedDates
                = generatorForHolidaysAndWorkingDays.generateScheduledDates
                                                            (occurences, lastScheduledDate, scheduledEvent);

        return adjustDatesForHolidays (unAdjustedDates, upcomingHolidays, scheduledEvent);
    }

    @Override
    public List<DateTime> generateScheduledDatesThrough(DateTime lastScheduledDate, DateTime throughDate,
            ScheduledEvent scheduledEvent) {

        DateTime lastGeneratedDate = null;
        List<DateTime> generatedDates = new ArrayList<DateTime>();

        lastGeneratedDate = lastScheduledDate;
        do {
            generatedDates.addAll(this.generateScheduledDates(10, lastGeneratedDate, scheduledEvent));

            lastGeneratedDate = generatedDates.get(generatedDates.size()-1);
            if (lastGeneratedDate.isBefore(throughDate)) {
                // roll forward date to prevent adding same date to list incorrectly
                // can't use set to keep unique as we use same dates to show pushed out periods etc.
                lastGeneratedDate = lastGeneratedDate.plusDays(1);
            }
        } while (lastGeneratedDate.isBefore(throughDate));

        return removeDatesAfterThroughDate(generatedDates, throughDate);
    }

    private List<DateTime> adjustDatesForHolidays
                    (final List<DateTime> dates, List<Holiday> upcomingHolidays, final ScheduledEvent scheduledEvent) {

        ListOfDatesAdjustmentStrategy adjustmentStrategy = new MoratoriumStrategy(upcomingHolidays, workingDays, scheduledEvent);
        return adjustmentStrategy.adjust(dates);
    }

    private List<DateTime> removeDatesAfterThroughDate (List<DateTime> dates, DateTime throughDate) {
        if (dates.isEmpty() || !dates.get(dates.size()-1).isAfter(throughDate)) {
            return dates;
        }
        dates.remove(dates.get(dates.size()-1));
        return removeDatesAfterThroughDate(dates, throughDate);
    }
}
