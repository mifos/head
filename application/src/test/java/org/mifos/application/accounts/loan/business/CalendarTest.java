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

package org.mifos.application.accounts.loan.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.framework.util.helpers.DateUtils;

@Ignore
public class CalendarTest {

    private Date createPreviousDate(int numberOfDays) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -numberOfDays);
        Date pastDate = DateUtils.getDateWithoutTimeStamp(calendar.getTime());
        return pastDate;
    }

    Date getFirstDateForWeek(GregorianCalendar gc, Date startDate, int meetingDayOfWeek) {
        gc.setTime(startDate);

        // Jump to next week if the required weekday has passed for current week
        if (gc.get(Calendar.DAY_OF_WEEK) > meetingDayOfWeek) {
            gc.add(Calendar.WEEK_OF_MONTH, 1);
        }

        // Set the day of week as the require weekday
        gc.set(Calendar.DAY_OF_WEEK, meetingDayOfWeek);
        return gc.getTime();
    }

    @Test
    public void testDayOfWeek() {
        Date twoWeeksAgo = createPreviousDate(14);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(twoWeeksAgo);

        Assert.assertEquals(cal.get(Calendar.DAY_OF_WEEK), 1);
        System.out.println("two weeks ago: " + cal.getTime());

        Date nextMondayAfterTwoWeeksAgo = createPreviousDate(13);
        System.out.println("Expected: " + nextMondayAfterTwoWeeksAgo);
        Date computed = getFirstDateForWeek(cal, twoWeeksAgo, 2);
        System.out.println("actual: " + cal.getTime());
        Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(nextMondayAfterTwoWeeksAgo), DateUtils
                .getDateWithoutTimeStamp(computed));
    }

    @Test
    public void testDayOfWeek2() {
        Date twoWeeksAgo = createPreviousDate(14);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setFirstDayOfWeek(2);
        cal.setTime(twoWeeksAgo);

        Assert.assertEquals(cal.get(Calendar.DAY_OF_WEEK), 1);
        System.out.println("two weeks ago: " + cal.getTime());

        Date nextMondayAfterTwoWeeksAgo = createPreviousDate(13);
        System.out.println("Expected: " + nextMondayAfterTwoWeeksAgo);
        Date computed = getFirstDateForWeek(cal, twoWeeksAgo, 2);
        System.out.println("actual: " + cal.getTime());
        Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(nextMondayAfterTwoWeeksAgo), DateUtils
                .getDateWithoutTimeStamp(computed));

    }

    /*
     * @Test public void testDayOfWeek3() { //Wednesday, June 4, 2008
     * GregorianCalendar aWednesdayCal = new GregorianCalendar(2008, 5, 4); Date
     * aWednesday = aWednesdayCal.getTime();
     * 
     * 
     * Date computed = getFirstDateForWeek(new GregorianCalendar, aWednesday,
     * 2); System.out.println ("actual: " + cal.getTime());
     * Assert.assertEquals(DateUtils
     * .getDateWithoutTimeStamp(nextMondayAfterTwoWeeksAgo),
     * DateUtils.getDateWithoutTimeStamp(computed)); }
     */
}
