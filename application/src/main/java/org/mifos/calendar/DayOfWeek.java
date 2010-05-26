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

import org.joda.time.DateTime;
import org.joda.time.Days;

public class DayOfWeek {

    public static int monday() {
        return Days.ONE.getDays();
    }

    public static int tuesday() {
        return Days.TWO.getDays();
    }

    public static int wednesday() {
        return Days.THREE.getDays();
    }

    public static int thursday() {
        return Days.FOUR.getDays();
    }

    public static int friday() {
        return Days.FIVE.getDays();
    }

    public static int saturday() {
        return Days.SIX.getDays();
    }

    public static int sunday() {
        return Days.SEVEN.getDays();
    }

    public static DateTime oneWeekFrom(final DateTime lastScheduledDate) {
        return lastScheduledDate.plusWeeks(1);
    }

    public static DateTime mondayMidnight() {
        return new DateTime().withDayOfWeek(monday()).toDateMidnight().toDateTime();
    }

    public static DateTime tuesdayMidnight() {
        return new DateTime().toDateMidnight().withDayOfWeek(tuesday()).toDateTime();
    }

    public static DateTime wednesdayMidnight() {
        return new DateTime().toDateMidnight().withDayOfWeek(wednesday()).toDateTime();
    }

    public static DateTime thursdayMidnight() {
        return new DateTime().toDateMidnight().withDayOfWeek(thursday()).toDateTime();
    }

    public static DateTime fridayMidnight() {
        return new DateTime().toDateMidnight().withDayOfWeek(friday()).toDateTime();
    }

    public static DateTime saturdayMidnight() {
        return new DateTime().toDateMidnight().withDayOfWeek(saturday()).toDateTime();
    }

    public static DateTime sundayMidnight() {
        return new DateTime().toDateMidnight().withDayOfWeek(sunday()).toDateTime();
    }

    public static Days mondayAsDay() {
        return Days.days(monday());
    }

    public static Days tuesdayAsDay() {
        return Days.days(tuesday());
    }

    public static Days wednesdayAsDay() {
        return Days.days(wednesday());
    }

    public static Days thursdayAsDay() {
        return Days.days(thursday());
    }

    public static Days fridayAsDay() {
        return Days.days(friday());
    }

    public static Days saturdayAsDay() {
        return Days.days(saturday());
    }

    public static Days sundayAsDay() {
        return Days.days(sunday());
    }
}
