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

package org.mifos.domain.builders;

import org.joda.time.Days;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;

public class ScheduledEventBuilder {

    private RecurrenceType period;
    private int every;
    private int dayOfWeek;
    private int dayOfMonth;
    private int weekOfMonth;

    public ScheduledEvent build() {
        return ScheduledEventFactory.createScheduledEvent(period, every, dayOfWeek, dayOfMonth, weekOfMonth);
    }

    public ScheduledEventBuilder weeks() {
        this.period = RecurrenceType.WEEKLY;
        return this;
    }

    public ScheduledEventBuilder months() {
        this.period = RecurrenceType.MONTHLY;
        return this;
    }

    public ScheduledEventBuilder every(final int withEvery) {
        this.every = withEvery;
        return this;
    }

    public ScheduledEventBuilder onWeekOfMonth(final int withWeekOfMonth) {
        this.weekOfMonth = withWeekOfMonth;
        return this;
    }

    public ScheduledEventBuilder on(final Days withDayOfWeek) {
        this.dayOfWeek = withDayOfWeek.getDays();
        return this;
    }

    public ScheduledEventBuilder on(final int withDayOfWeek) {
        this.dayOfWeek = withDayOfWeek;
        return this;
    }

    public ScheduledEventBuilder onDayOfMonth(final int withDayOfMonth) {
        this.dayOfMonth = withDayOfMonth;
        return this;
    }
}
