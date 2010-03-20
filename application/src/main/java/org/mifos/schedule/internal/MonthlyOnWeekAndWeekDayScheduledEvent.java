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

import org.joda.time.DateTime;
import org.mifos.calendar.CalendarUtils;
import org.mifos.schedule.ScheduledEvent;

public class MonthlyOnWeekAndWeekDayScheduledEvent implements ScheduledEvent {

    private final int every;
    private final int dayOfWeek;
    private final int weekOfMonth;

    public MonthlyOnWeekAndWeekDayScheduledEvent(final int every, final int weekOfMonth, final int dayOfWeek) {
        this.every = every;
        this.weekOfMonth = weekOfMonth;
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public DateTime nextEventDateAfter(final DateTime startDate) {

        return CalendarUtils.getNextDayForMonthUsingWeekRankAndWeekday(startDate, weekOfMonth, this.dayOfWeek, every);

    }

    @Override
    public DateTime nearestMatchingDateBeginningAt(final DateTime startDate) {

        return CalendarUtils.getFirstDayForMonthUsingWeekRankAndWeekday(startDate, this.weekOfMonth, this.dayOfWeek);
    }

}
