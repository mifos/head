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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.service.HolidayBusinessService;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.meeting.util.helpers.WeekDay;

/**
 *
 */
class WeeklySchedule extends Schedule {
    
    private HolidayBusinessService holidayBusinessService;
 
    private final WeekDay dayOfWeek;
   
    public HolidayBusinessService getHolidayBusinessService() {
        return this.holidayBusinessService;
    }

    public void setHolidayBusinessService(HolidayBusinessService holidayBusinessService) {
        this.holidayBusinessService = holidayBusinessService;
    }
    
    public WeeklySchedule (Date startDate, Date endDate, Short recurAfter, WeekDay dayOfWeek, Integer numberOfOccurrences,
                           Boolean adjustForHolidays) {
        super(startDate, endDate, recurAfter, numberOfOccurrences, adjustForHolidays);
        this.dayOfWeek = dayOfWeek;
    }
    
    public List<Date> getAllDates() {
        List<Date> dates;
        if (adjustForHolidays) {
            dates = getAllDatesAdjustedForHolidays();
        } else {
            dates = getAllUnadjustedDates();
        }
        return dates;
    }
    
    private List<Date> getAllUnadjustedDates () {
        List<Date> scheduledDates = new ArrayList<Date>();
        Date date = startDate;

        for (int dateCount = 0; dateCount < numberOfOccurrences; dateCount++) {
            scheduledDates.add(date);
            date = getNextDate(date);
        }
        return scheduledDates;
    }
    
    private List<Date> getAllDatesAdjustedForHolidays () {
        if (numberOfOccurrences == 0) {
            return new ArrayList<Date>();
        }
       
        return adjustDatesForHolidays(getAllUnadjustedDates ());
    }
    /**
     * Return the list of dates adjusted according to holiday repayment rules for any holidays that
     * the dates fall in.
     */
    private List<Date> adjustDatesForHolidays (List<Date> dates) {
        List<Date> adjustedDates = new ArrayList<Date>();
        if (dates.size() > 0) {
            Date firstDate = advanceToWorkingDay(dates.get(0));
            if (isInMoratorium(firstDate)) {
                adjustedDates = adjustDatesForHolidays (shiftAllDatesOnce (dates));
            } else {
                HolidayBO holiday = holidayBusinessService.findNonPushOutHolidayContaining(firstDate);
                if (holiday == null) {
                    adjustedDates = adjustDatesForHolidays(butFirstDate (dates));
                    adjustedDates.add(0, firstDate);
                } else {
                    adjustedDates = adjustDatesWhenFirstDateInNonMoratoriumHoliday
                                            (firstDate, butFirstDate(dates), holiday);
                }
            }
        }
        return adjustedDates;
    }
    
    private List<Date> adjustDatesWhenFirstDateInNonMoratoriumHoliday
                            (Date firstDate, List<Date> rest, HolidayBO holiday) {
        
        assert firstDate != null;
        assert rest != null;
        assert holiday !=null;
        assert !isInMoratorium(firstDate);
        assert holiday.encloses(firstDate);
        
        List<Date> adjustedDates;
        if (holiday.getRepaymentRuleType().equals(RepaymentRuleTypes.SAME_DAY)) {
            // Same as if no holiday
            adjustedDates = adjustDatesForHolidays(rest);
            adjustedDates.add(0, firstDate);                    
        }
        else {
            Date shiftedFirstDate = firstDate;
            do {
                shiftedFirstDate = shiftDateOnceUsingRepaymentRule 
                                      (shiftedFirstDate, holiday.getRepaymentRuleType());
            } while (holiday.encloses(shiftedFirstDate) && !isInMoratorium(shiftedFirstDate));
            adjustedDates = adjustDatesForHolidays(makeList(shiftedFirstDate, rest));
        }
        return adjustedDates;
    }
    /**
     * Return a copy of the list with the first date removed. Are there utility functions to do these
     * utility list functions?
     */
    private List<Date> butFirstDate (List<Date> dates) {
        List<Date> butFirst = new ArrayList<Date>();
        for (int i = 1; i < dates.size(); i++) {
            butFirst.add(dates.get(i));
        }
        return butFirst;
    }
    
    private List<Date> makeList (Date first, List<Date> rest) {
        List<Date> list = rest;
        list.add(0, first);
        return list;
    }
    
    /**
     * Shift the entire schedule by one recurrence period
     */
    private List<Date> shiftAllDatesOnce (List<Date> dates) {
        List<Date> pushedOutDates = new ArrayList<Date>();
        for (Date date : dates) {
            pushedOutDates.add(getNextDate(date));
        }
        return pushedOutDates;
    }

    private Date shiftDateOnceUsingRepaymentRule(Date date, RepaymentRuleTypes repaymentRule) {
        Date shiftedDate = date;
        switch (repaymentRule) {
        case NEXT_WORKING_DAY:
            shiftedDate = holidayBusinessService.getNextWorkingDay(date);
            break;
        case NEXT_MEETING_OR_REPAYMENT:
            shiftedDate = getNextScheduleDateAfterRecurrenceWithoutAdjustment(date);
            break;
        case REPAYMENT_MORATORIUM:
            shiftedDate = getNextScheduleDateAfterRecurrenceWithoutAdjustment(date);
            break;
        case SAME_DAY:
            //return the unadjusted date
        default:
            break;
        }
        return shiftedDate;
    }
    


    protected Date getNextDateForWeek(Date startDate) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        calendar.add(Calendar.WEEK_OF_MONTH, recurAfter);
        return calendar.getTime();
    }


    private Date getNextScheduleDateAfterRecurrenceWithoutAdjustment(Date afterDate) {
        Date from = getFirstDate(startDate);
        Date currentScheduleDate = getNextDate(from);
        // Date currentScheduleDate=getNextDate(getStartDate());
        while (currentScheduleDate.compareTo(afterDate) <= 0) {
            currentScheduleDate = getNextDate(currentScheduleDate);
        }
        return currentScheduleDate;
    }

    /**
     * Set the day of week according to given start day to the require weekday,
     * i.e. so it matches the schedule's day of the week.
     * 
     * <p>Sample scenarios</p>
     * <ul>
     * <li> Start date is Monday 9 June 2008 and week day is
     *      Tuesday. Roll forward the date to Tuesday 10 June 2008</li>
     * <li> Start date is Sunday 8 June 2008 and week day is Saturday. Roll
     *      forward the date to Saturday 14 June 2008.</li>
     * <li> Start date is Tuesday 10 June 2008 and week day is Monday. Roll forward the date
     *      to Monday 16 June 2008.</li>
     * <li> Start date is Sunday 8 June 2008 and week day is Sunday. 
     *      Keep the date as Sunday 8 June 2008.</li>
     * <li> Start date is Saturday 7 June 2008 and week day is Sunday. 
     *      Roll forward the date to Sunday 8 June 2008 </li>
     * </ul>
     * 
     * TODO Move this method to a generic calendaring class.
     * 
     */
    protected Date getFirstDate(Date startDate) {
        final GregorianCalendar firstDateForWeek = new GregorianCalendar();
        firstDateForWeek.setTime(startDate);
        int startDateWeekDay = firstDateForWeek.get(Calendar.DAY_OF_WEEK);
        int meetingWeekDay = dayOfWeek.getValue();

        // Calculate amount of days that need adding to roll forward to the
        // meeting day
        int amountOfDaysToAdd = (meetingWeekDay - startDateWeekDay);
        if (amountOfDaysToAdd < 0) {
            // amountOfDaysToAdd can result in a negative (e.g.
            // Calendar.SATURDAY (7) is greater than Calendar.SUNDAY (1),
            // if so then will add 7 to roll forward a week
            amountOfDaysToAdd += 7;
        }
        firstDateForWeek.add(Calendar.DAY_OF_WEEK, amountOfDaysToAdd);
        return firstDateForWeek.getTime();
    }
    
    private boolean isInMoratorium(Date date) {
        return !(holidayBusinessService
                    .getAllPushOutHolidaysContaining(date)
                       .isEmpty());
    }
    
    private Date advanceToWorkingDay (Date day) {
        Date closestWorkingDay = day;
        if (!holidayBusinessService.isWorkingDay(day)) {
            closestWorkingDay = holidayBusinessService.getNextWorkingDay(day);
        }
        return closestWorkingDay;

    }
    
    @Override
    protected Date getNextDate(Date startDate) {
        return null;
    }
 
}
