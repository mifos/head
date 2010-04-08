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
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.schedule.ScheduledEvent;

public class HolidayAdjustmentRuleFactory {

    public static DateAdjustmentStrategy createStrategy(final DateTime scheduledDay, final List<Days> workingDays, final ScheduledEvent scheduledEvent,
            final RepaymentRuleTypes holidayAdjustmentRule) {

        DateAdjustmentStrategy holidayAdjustmentStrategy;
        switch (holidayAdjustmentRule) {
        case NEXT_WORKING_DAY:
            holidayAdjustmentStrategy = new NextWorkingDayStrategy(workingDays);
            break;
        case NEXT_MEETING_OR_REPAYMENT:
            holidayAdjustmentStrategy = new NextScheduledEventStrategy(scheduledEvent);
            break;
        case SAME_DAY:
            holidayAdjustmentStrategy = new SameDayStrategy(scheduledDay);
            break;
        case REPAYMENT_MORATORIUM:
            holidayAdjustmentStrategy = new NextScheduledEventStrategy(scheduledEvent);
            break;
        default:
            throw new IllegalStateException("unknown holiday ajustment rule type.");
        }

        return holidayAdjustmentStrategy;
    }

}
