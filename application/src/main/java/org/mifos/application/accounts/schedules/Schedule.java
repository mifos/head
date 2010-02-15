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

import java.util.Date;
import java.util.List;

/**
 * Encapsulates a regularly scheduled event. Events having regular schedules include
 * <ul>
 *   <li>Customer meetings
 *   <li>Loan repayments
 *   <li>fee repayments
 *   <li>savings interest calculations
 *   <li>savings interest postings
 *   <li>mandatory savings deposits
 * </ul>
 */
public abstract class Schedule {
    
    /**
     * Turn new scheduling code on or off
     */
    public static boolean isNewSchedulingEnabled = false;
    
    private Integer recurAfter;
    private Date startDate;
    
    private List<Date> scheduledDates;
    
    public List<Date> getScheduledDates() {
        return this.scheduledDates;
    }

    protected void setScheduledDates(List<Date> scheduledDates) {
        this.scheduledDates = scheduledDates;
    }

}
