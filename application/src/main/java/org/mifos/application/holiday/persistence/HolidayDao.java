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

package org.mifos.application.holiday.persistence;

import java.util.List;

import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.calendar.CalendarEvent;
import org.mifos.framework.exceptions.PersistenceException;

public interface HolidayDao {

    List<Holiday> findAllHolidaysThisYearAndNext();

    List<Holiday> findAllHolidaysThisYearAndNext(short officeId);

    void save(Holiday holiday) throws PersistenceException;

    List<HolidayBO> findAllHolidays();

    List<String> applicableOffices(Integer id);

    CalendarEvent findCalendarEventsForThisYearAndNext(short officeId);


}
