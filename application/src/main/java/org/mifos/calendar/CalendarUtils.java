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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.helpers.DateUtils;

/**
 * Utility class contain side-effect free functions for processing date and calendar functionality.
 *
 * TODO - refactor all methods to use joda-time rather than java.util.Date and Calendar.
 */
public class CalendarUtils {

    /**
     * Set the day of week according to given start day to the require weekday, i.e. so it matches the meeting week day.
     *
     * e.g. - If start date is Monday 9 June 2008 and meeting week day is Tuesday, then roll forward the date to Tuesday
     * 10 June 2008 - or if start date is Sunday 8 June 2008 and meeting week day is Saturday, then roll forward the
     * date to Saturday 14 June 2008 - or if start date is Tuesday 10 2008 June and meeting week day is Monday, then
     * roll forward the date to Monday 16 June 2008 - or if start date is Sunday 8 June 2008 and meeting week day is
     * Sunday, then keep the date as Sunday 8 June 2008 - or if start date is Saturday 7 June 2008 and meeting week day
     * is Sunday, then roll forward the date to Sunday 9 June 2008.
     */
    public static DateTime getFirstDateForWeek(final DateTime startDate, final int dayOfWeek) {

        /*
         * In Joda time MONDAY=1 and SUNDAY=7, so shift these to SUNDAY=1, SATURDAY=7 to match this class
         */
        int calendarDayOfWeek = (dayOfWeek % 7) + 1;

        final GregorianCalendar firstDateForWeek = new GregorianCalendar();
        firstDateForWeek.setTime(startDate.toDate());

        int startDateWeekDay = firstDateForWeek.get(Calendar.DAY_OF_WEEK);
        int amountOfDaysToAdd = calendarDayOfWeek - startDateWeekDay;
        if (amountOfDaysToAdd < 0) {
            amountOfDaysToAdd += 7;
        }
        firstDateForWeek.add(Calendar.DAY_OF_WEEK, amountOfDaysToAdd);
        return new DateTime(firstDateForWeek.getTime());
    }

    /**
     * for monthly on date return the next date falling on the same day. If date has passed, pass in the date of next
     * month, adjust to day number if day number exceed total number of days in month
     */
    public static DateTime getFirstDateForMonthOnDate(final DateTime startDate, final int dayOfMonth) {

        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate.toDate());

        int dt = gc.get(GregorianCalendar.DATE);
        // if date passed in, is after the date on which schedule has to
        // lie, move to next month
        if (dt > dayOfMonth) {
            gc.add(GregorianCalendar.MONTH, 1);
        }
        // set the date on which schedule has to lie
        int M1 = gc.get(GregorianCalendar.MONTH);
        gc.set(GregorianCalendar.DATE, dayOfMonth);
        int M2 = gc.get(GregorianCalendar.MONTH);

        int daynum = dayOfMonth;
        while (M1 != M2) {
            gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
            gc.set(GregorianCalendar.DATE, daynum - 1);
            M2 = gc.get(GregorianCalendar.MONTH);
            daynum--;
        }

        return new DateTime(gc.getTime());
    }


    public static DateTime getFirstDayForMonthUsingWeekRankAndWeekday(final DateTime startDate, final int calendarWeekOfMonth,
            final int dayOfWeek) {

        /*
         * In Joda time MONDAY=1 and SUNDAY=7, so shift these to SUNDAY=1, SATURDAY=7 to match this class
         */
        int calendarDayOfWeek = (dayOfWeek % 7) + 1;

        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate.toDate());

        DateTime scheduleDate = null;
        // if current weekday is after the weekday on which schedule has to
        // lie, move to next week
        if (gc.get(Calendar.DAY_OF_WEEK) > calendarDayOfWeek) {
            gc.add(Calendar.WEEK_OF_MONTH, 1);
        }
        // set the weekday on which schedule has to lie
        gc.set(Calendar.DAY_OF_WEEK, calendarDayOfWeek);
        // if week rank is First, Second, Third or Fourth, Set the
        // respective week.
        // if current week rank is after the weekrank on which schedule has
        // to lie, move to next month
        if (!RankType.fromInt(calendarWeekOfMonth).equals(RankType.LAST)) {
            if (gc.get(Calendar.DAY_OF_WEEK_IN_MONTH) > calendarWeekOfMonth) {
                gc.add(GregorianCalendar.MONTH, 1);
                gc.set(GregorianCalendar.DATE, 1);
            }
            // set the weekrank on which schedule has to lie
            gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, calendarWeekOfMonth);
            scheduleDate = new DateTime(gc.getTime().getTime());
        }
        else {// scheduleData.getWeekRank()=Last
            int M1 = gc.get(GregorianCalendar.MONTH);
            // assumption: there are 5 weekdays in the month
            gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 5);
            int M2 = gc.get(GregorianCalendar.MONTH);
            // if assumption fails, it means there exists 4 weekdays in a
            // month, return last weekday date
            // if M1==M2, means there exists 5 weekdays otherwise 4 weekdays
            // in a month
            if (M1 != M2) {
                gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
                gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 4);
            }
            scheduleDate = new DateTime(gc.getTime().getTime());
        }

        return scheduleDate;
    }

    public static DateTime getNextDateForDay(final DateTime startDate, final int every) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate.toDate());

        gc.add(Calendar.DAY_OF_WEEK, every);
        return new DateTime(gc.getTime().getTime());
    }

    public static DateTime getNextDateForMonthOnDate(final DateTime startDate, final int dayOfMonth, final int every) {

        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate.toDate());

        gc.add(GregorianCalendar.MONTH, every);
        int M1 = gc.get(GregorianCalendar.MONTH);
        gc.set(GregorianCalendar.DATE, dayOfMonth);
        int M2 = gc.get(GregorianCalendar.MONTH);

        int daynum = dayOfMonth;
        while (M1 != M2) {
            gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
            gc.set(GregorianCalendar.DATE, daynum - 1);
            M2 = gc.get(GregorianCalendar.MONTH);
            daynum--;
        }
        return new DateTime(gc.getTime().getTime());
    }

    public static DateTime getNextDayForMonthUsingWeekRankAndWeekday(final DateTime startDate, final int weekOfMonth,
            final int dayOfWeek, final int every) {

        /*
         * In Joda time MONDAY=1 and SUNDAY=7, so shift these to SUNDAY=1, SATURDAY=7 to match this class
         */
        int calendarDayOfWeek = (dayOfWeek % 7) + 1;

        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(startDate.toDate());

        DateTime scheduleDate;

        if (!RankType.fromInt(weekOfMonth).equals(RankType.LAST)) {
            // apply month recurrence
            gc.add(GregorianCalendar.MONTH, every);
            gc.set(Calendar.DAY_OF_WEEK, calendarDayOfWeek);
            gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, weekOfMonth);
            scheduleDate = new DateTime(gc.getTime().getTime());
        }
        else {// weekCount=-1
            gc.set(GregorianCalendar.DATE, 15);
            gc.add(GregorianCalendar.MONTH, every);
            gc.set(Calendar.DAY_OF_WEEK, calendarDayOfWeek);
            int M1 = gc.get(GregorianCalendar.MONTH);
            // assumption: there are 5 weekdays in the month
            gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 5);
            int M2 = gc.get(GregorianCalendar.MONTH);
            // if assumption fails, it means there exists 4 weekdays in a
            // month, return last weekday date
            // if M1==M2, means there exists 5 weekdays otherwise 4 weekdays
            // in a month
            if (M1 != M2) {
                gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
                gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 4);
            }
            scheduleDate = new DateTime(gc.getTime().getTime());
        }

        return scheduleDate;
    }

    public static DateTime nearestDayOfWeekTo(final int dayOfWeek, final DateTime date) {

        DateTime withDayOfWeek = date.withDayOfWeek(dayOfWeek);

        if (date.getYear() == withDayOfWeek.getYear()) {

            if (date.getDayOfYear() > withDayOfWeek.getDayOfYear()) {
                return withDayOfWeek.plusWeeks(1);
            }

            return withDayOfWeek;
        }

        // back a year
        if (date.getYear() > withDayOfWeek.getYear()) {
            return withDayOfWeek.plusWeeks(1);
        }

        return withDayOfWeek;
    }

    public static List<DateTime> convertListOfDatesToDateTimes(final List<Date> meetingDates) {

        List<DateTime> convertedDates = new ArrayList<DateTime>();

        for (Date meeting : meetingDates) {
            convertedDates.add(new DateTime(meeting));
        }

        return convertedDates;
    }

    public static DateTime getDateFromString(String strDate, Locale locale) throws InvalidDateException {
        if (StringUtils.isBlank(strDate)) {
            throw new IllegalArgumentException("strDate cannot be null or empty");
        }
        return new DateTime(DateUtils.getLocaleDate(locale, strDate).getTime());
    }
}
