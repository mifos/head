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

package org.mifos.application.holiday.util.helpers;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.util.DateTimeService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mifos.framework.util.helpers.DateUtils.getDateWithoutTimeStamp;

/**
 * Use {@link org.mifos.application.holiday.persistence.HolidayServiceFacadeWebTier} instead of HolidayUtils.
 * HolidayUtils contains static dependencies & static methods -> very bad for effective unit testing &
 * dependency management.
 */
@Deprecated
public class HolidayUtils {
    private static final FiscalCalendarRules fiscalCalendarRules = new FiscalCalendarRules();

    public static boolean isWorkingDay(final Calendar day) throws RuntimeException {
        return fiscalCalendarRules.isWorkingDay(day);
    }

    static HolidayBO inHoliday(final Calendar pday, final List<HolidayBO> holidays) {
        Calendar day = new DateTimeService().getCurrentDateTime().toGregorianCalendar();
        day.setTimeInMillis(0);
        day.set(pday.get(Calendar.YEAR), pday.get(Calendar.MONTH), pday.get(Calendar.DAY_OF_MONTH));
        Date givenDate = getDateWithoutTimeStamp(day.getTimeInMillis());
        for (HolidayBO holiday : holidays) {
            if (holiday.encloses(givenDate)) {
                return holiday;
            }
        }
        return null;
    }

    public static Calendar getNextWorkingDay(final Calendar day) {
        while (!isWorkingDay(day)) {
            day.add(Calendar.DATE, 1);
        }
        return day;
    }

    public static Date getNextWorkingDay(Date date) {
        return getNextWorkingDay(getCalendarDate(date)).getTime();
    }

    private static Calendar getCalendarDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
