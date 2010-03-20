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
import org.mifos.calendar.BasicHolidayStrategy;
import org.mifos.calendar.BasicWorkingDayStrategy;
import org.mifos.calendar.DateAdjustmentStrategy;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;

public class HolidayAndWorkingDaysScheduledDateGeneration implements ScheduledDateGeneration {

    private final List<Days> workingDays;
    private final List<Holiday> upcomingHolidays;

    public HolidayAndWorkingDaysScheduledDateGeneration(final List<Days> workingDays, final List<Holiday> upcomingHolidays) {
        this.workingDays = workingDays;
        this.upcomingHolidays = upcomingHolidays;
    }

    @Override
    public List<DateTime> generateScheduledDates(final int occurences, final DateTime lastScheduledDate,
            final ScheduledEvent scheduledEvent) {

        DateTime matchingDayOfWeekDate = scheduledEvent.nearestMatchingDateBeginningAt(lastScheduledDate);

        List<DateTime> scheduledDates = new ArrayList<DateTime>();

        DateTime latestGeneratedDate = matchingDayOfWeekDate;
        for (int i = 0; i < occurences; i++) {

            DateAdjustmentStrategy workingDay = new BasicWorkingDayStrategy(workingDays);
            DateTime ajustedForWorkingDay = workingDay.adjust(latestGeneratedDate);

            DateAdjustmentStrategy holidayAjustment = new BasicHolidayStrategy(upcomingHolidays, workingDays,
                    scheduledEvent);
            DateTime ajustedForHolidays = holidayAjustment.adjust(ajustedForWorkingDay);

            scheduledDates.add(ajustedForHolidays);

            latestGeneratedDate = scheduledEvent.nextEventDateAfter(ajustedForWorkingDay);
        }

        return scheduledDates;
    }
}
