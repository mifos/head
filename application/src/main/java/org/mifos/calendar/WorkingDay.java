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

package org.mifos.calendar;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class WorkingDay {

    public static boolean isWorkingDay(final DateTime day, final List<Days> workingDays) {
        return workingDays.contains(Days.days(day.dayOfWeek().get()));
    }

    public static boolean isNotWorkingDay(final DateTime day, final List<Days> workingDays) {
        return !isWorkingDay(day, workingDays);
    }

    public static DateTime nextWorkingDay(final DateTime day, final List<Days> workingDays) {

        if (workingDays == null || workingDays.isEmpty()) {
            throw new IllegalStateException("workingDays cannot be null or empty");
        }

        DateTime nextWorkingDay = day;

        do {
            nextWorkingDay = nextWorkingDay.plusDays(1);
        }
        while (isNotWorkingDay(nextWorkingDay, workingDays));

        return nextWorkingDay;
    }

    public static DateTime nearestWorkingDayOnOrAfter(final DateTime day, final List<Days> workingDays) {

        if (workingDays == null || workingDays.isEmpty()) {
            throw new IllegalStateException("workingDays cannot be null or empty");
        }

        DateTime nextWorkingDay = day;
        while ( isNotWorkingDay(nextWorkingDay, workingDays)) {
            nextWorkingDay = nextWorkingDay.plusDays(1);
        }
        return nextWorkingDay;
    }

}
