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

package org.mifos.schedule.builder;

import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.internal.MonthlyOnWeekAndWeekDayScheduledEvent;

/**
 * 
 *
 */
public class MonthlyOnWeekAndWeekDayScheduledEventBuilder extends ScheduledEventBuilder {
    
    private RankType weekOfMonth;
    private WeekDay dayOfWeek;
    
    @Override
    public ScheduledEvent build() {
        return new MonthlyOnWeekAndWeekDayScheduledEvent(every, weekOfMonth.getValue(), dayOfWeek.getValue());
    }
    
    @Override
    public ScheduledEventBuilder monthlyOnDate (Integer dayOfMonth) {
        assert false : "Type of monthly schedule has already been set.";
        return null;
    }
    
    MonthlyOnWeekAndWeekDayScheduledEventBuilder ( RankType weekOfMonth, WeekDay dayOfWeek) {
        assert weekOfMonth != null;
        assert dayOfWeek != null;

        this.weekOfMonth = weekOfMonth;
        this.dayOfWeek = dayOfWeek;
    }
}
