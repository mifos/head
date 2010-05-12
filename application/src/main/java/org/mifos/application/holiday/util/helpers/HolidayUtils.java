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

import static org.mifos.application.holiday.util.helpers.RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT;
import static org.mifos.application.holiday.util.helpers.RepaymentRuleTypes.NEXT_WORKING_DAY;
import static org.mifos.framework.util.helpers.DateUtils.getCalendar;
import static org.mifos.framework.util.helpers.DateUtils.getDateWithoutTimeStamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.service.HolidayBusinessService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.DateTimeService;

/**
 * Helper methods related to holidays logic
 */
public class HolidayUtils {

    public static boolean isWorkingDay(final Calendar day) throws RuntimeException {
        return new FiscalCalendarRules().isWorkingDay(day);
    }

    /**
     * Find the holiday containing the given day.
     *
     * @param day
     *            the day to test
     * @return the holiday containing the day, if any, otherwise null
     */
    @Deprecated
    public static HolidayBO inHoliday(Calendar day) {
        HolidayBO holiday;
        try {
            holiday = inHoliday(day, getAllHolidaysInCurrentAndNextYears(day.get(Calendar.YEAR)));
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
        return holiday;
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

    @Deprecated
    public static Calendar adjustDate(final Calendar day, final MeetingBO meeting) throws MeetingException {
        Calendar adjustedDate = isWorkingDay(day) ? day : getNextWorkingDay(day);
        HolidayBO holiday;
        try {
            holiday = inHoliday(adjustedDate, getAllHolidaysInCurrentAndNextYears(adjustedDate.get(Calendar.YEAR)));
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
        if (holiday == null) {
            return adjustedDate;
        }
        return adjustDateUsingRepaymentRule(holiday.getRepaymentRuleType(), adjustedDate, meeting);
    }

    public static Calendar getNextWorkingDay(final Calendar day) {
        do {
            day.add(Calendar.DATE, 1);
        } while (!isWorkingDay(day));
        return day;
    }

    private static Calendar adjustDateUsingRepaymentRule(final RepaymentRuleTypes repaymentRule, final Calendar adjustedDate,
            final MeetingBO meeting) throws MeetingException {
        if (NEXT_WORKING_DAY.equals(repaymentRule)) {
            adjustedDate.add(Calendar.DATE, 1);
            return adjustDate(adjustedDate, meeting);
        } else if (NEXT_MEETING_OR_REPAYMENT.equals(repaymentRule)) {
            Date nextDate = meeting.getNextScheduleDateAfterRecurrenceWithoutAdjustment(adjustedDate.getTime());
            return adjustDate(getCalendar(nextDate), meeting);
        }
        return adjustedDate;
    }

    private static List<HolidayBO> getAllHolidaysInCurrentAndNextYears(final int year) throws ServiceException {
        List<HolidayBO> holidays = new ArrayList<HolidayBO>();
        HolidayBusinessService service = new HolidayBusinessService();
        holidays.addAll(service.getHolidays(year));
        holidays.addAll(service.getHolidays(year + 1));
        return holidays;
    }

}
