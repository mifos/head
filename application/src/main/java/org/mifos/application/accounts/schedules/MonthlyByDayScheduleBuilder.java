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

/**
 *
 */
public class MonthlyByDayScheduleBuilder extends ScheduleBuilder {
    
    private Integer dayOfMonth;
    
    public Schedule build() {
        return null;
    }
    
    public MonthlyByDayScheduleBuilder dayOfMonth( Integer day) {
        this.dayOfMonth = day;
        return this;
    }

    protected void setDayOfWeek() {
        //do nothing
    }
    @Override
    protected void validateParameters() {
        super.validateParameters();
        if ( null == dayOfMonth ) {
            throw new IllegalArgumentException("Scheduled day of month has not been specfied");
        }
        if ( (dayOfMonth < 1) || (dayOfMonth > 31) ) {
            throw new IllegalArgumentException("Scheduled day of month must be between 1 and 31");
        }
    } 
}
