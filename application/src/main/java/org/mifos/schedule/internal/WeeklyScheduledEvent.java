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

public class WeeklyScheduledEvent extends AbstractScheduledEvent {

    private final int every;
    private final int dayOfWeek;

    public WeeklyScheduledEvent(final int every, final int dayOfWeek) {
        this.every = every;
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public DateTime nextEventDateAfter(final DateTime startingDate) {
        return startingDate.plusWeeks(every).withDayOfWeek(dayOfWeek);
    }

    @Override
    public DateTime nearestMatchingDateBeginningAt(final DateTime startDate) {

        return CalendarUtils.getFirstDateForWeek(startDate, this.dayOfWeek);
    }

    @Override
    public int getEvery() {
        return every;
    }
}
