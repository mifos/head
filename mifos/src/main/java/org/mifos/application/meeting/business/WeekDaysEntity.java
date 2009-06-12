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

package org.mifos.application.meeting.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.config.FiscalCalendarRules;

/**
 * This class encapsulate the weekDay
 */
public class WeekDaysEntity extends MasterDataEntity {

    private Short workDay;

    private Short startOfWeek;

    public WeekDaysEntity(WeekDay weekDay) {
        super(weekDay.getValue());
        this.workDay = null;
    }

    public WeekDaysEntity() {
    }

    public boolean isWorkingDay() {
        return FiscalCalendarRules.isWorkingDay(getId());
    }

    public boolean isStartOfFiscalWeek() {
        return FiscalCalendarRules.isStartOfFiscalWeek(getId());
    }

    public Short getStartOfWeek() {
        return startOfWeek;
    }

    public void setStartOfWeek(Short startOfWeek) {
        this.startOfWeek = startOfWeek;
    }

    public Short getWorkDay() {
        return workDay;
    }

    public void setWorkDay(Short workDay) {
        this.workDay = workDay;
    }

    public void save() throws RuntimeException {
        try {
            new MasterPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    public void update(Short startOfWeek, Short workDay) throws RuntimeException {
        this.startOfWeek = startOfWeek;
        this.workDay = workDay;
        // this block should not be here
        try {
            new MasterPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
        // end of block
    }

}
