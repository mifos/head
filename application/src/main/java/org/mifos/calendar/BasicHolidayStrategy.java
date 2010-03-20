/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.calendar;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.schedule.ScheduledEvent;

public class BasicHolidayStrategy implements DateAdjustmentStrategy {

    /**
     * should be ordered by date ascending to avoid problems with overlapping holidays
     */
    private final List<Holiday> upcomingHolidays;
    private final List<Days> workingDays;
    private final ScheduledEvent scheduledEvent;

    public BasicHolidayStrategy(final List<Holiday> upcomingHolidays, final List<Days> workingDays,
            final ScheduledEvent scheduledEvent) {
        this.upcomingHolidays = upcomingHolidays;
        this.workingDays = workingDays;
        this.scheduledEvent = scheduledEvent;
    }

    @Override
    public DateTime adjust(final DateTime startingFrom) {

        DateTime adjustedDate = startingFrom;

        for (Holiday holiday : this.upcomingHolidays) {
            if (holiday.encloses(adjustedDate.toDate())) {
                adjustedDate = holiday.adjust(adjustedDate, this.workingDays, this.scheduledEvent);
            }
        }

        return adjustedDate;
    }

}
