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

package org.mifos.application.accounts.schedules;

import org.joda.time.DateTime;
import org.mifos.application.meeting.util.helpers.WeekDay;

/**
 *
 */
public class WeeklyScheduleBuilder extends ScheduleBuilder {
    
    private WeekDay dayOfWeek;
    
    @Override
    public Schedule build() {
        validateParameters();
        return new WeeklySchedule(startDate, recurAfter, dayOfWeek, numberOfOccurrences);
    }
    
    @Override
    protected final void setDayOfWeek() {
        dayOfWeek = dateTimeToWeekDay(new DateTime(startDate));
    }

    @Override
    protected void validateParameters() {
        super.validateParameters();
        if ( null == dayOfWeek ) {
            throw new IllegalArgumentException("Scheduled day of week has not been specfied");
        }
    }

}
