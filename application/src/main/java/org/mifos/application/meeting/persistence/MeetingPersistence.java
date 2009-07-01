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

package org.mifos.application.meeting.persistence;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.application.meeting.util.helpers.WeekDay;

public class MeetingPersistence extends Persistence {

    List<WeekDaysEntity> getWorkingDays(Short localeId) throws PersistenceException {
        List<WeekDaysEntity> workingDays = new ArrayList<WeekDaysEntity>();
        List<MasterDataEntity> weekDaysList = new MasterPersistence().retrieveMasterEntities(WeekDaysEntity.class,
                localeId);
        for (MasterDataEntity weekDay : weekDaysList) {
            if (((WeekDaysEntity) weekDay).isWorkingDay())
                workingDays.add((WeekDaysEntity) weekDay);
        }
        return workingDays;
    }

    public List<WeekDay> getWorkingDays() {
        return FiscalCalendarRules.getWorkingDays();
    }

    public MeetingBO getMeeting(Integer meetingId) throws PersistenceException {
        return (MeetingBO) getPersistentObject(MeetingBO.class, meetingId);
    }
}
