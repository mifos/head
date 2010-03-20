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

package org.mifos.application.meeting.util.helpers;

import org.joda.time.DateTimeConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.config.LocalizedTextLookup;

public enum WeekDay implements LocalizedTextLookup {
    SUNDAY((short) 1), MONDAY((short) 2), TUESDAY((short) 3), WEDNESDAY((short) 4), THURSDAY((short) 5), FRIDAY(
            (short) 6), SATURDAY((short) 7);

    Short value;
    String name;

    WeekDay(final Short value) {
        this.value = value;
        this.name = null;

    }

    public Short getValue() {
        return value;
    }

    /*
     * In Joda time MONDAY=1 and SUNDAY=7, so shift these
     * to SUNDAY=1, SATURDAY=7 to match this class
     */
    public static WeekDay getJodaWeekDay(final int value) {
        return getWeekDay((value%7)+1);
    }

    /*
     * Shift Sunday=1 to joda time equivalent Sunday=7
     * Shift Monday=2 to joda time equivalent Monday=1
     * Shift Tuesday=3 to joda time equivalent Tuesday=2
     * etc.
     */
    public static int getJodaDayOfWeekThatMatchesMifosWeekDay(final int mifosWeekDayValue) {

        if (WeekDay.SUNDAY.getValue().equals((short)mifosWeekDayValue)) {
            return DateTimeConstants.SUNDAY;
        }

        if (WeekDay.SATURDAY.getValue().equals((short)mifosWeekDayValue)) {
            return DateTimeConstants.SATURDAY;
        }

        return (mifosWeekDayValue%7)-1;
    }

    public static WeekDay getWeekDay(final int value) {
        for (WeekDay weekday : WeekDay.values()) {
            if (weekday.value == value) {
                return weekday;
            }
        }
        throw new RuntimeException("no week day " + value);
    }

    public WeekDay next() {
        if (this == SATURDAY) {
            return SUNDAY;
        }
        return getWeekDay(value + 1);
    }

    /*
     * TODO: we should be passing in a Locale or UserContext here
     */
    public String getName() {
        if (name == null) {
            name = MessageLookup.getInstance().lookup(this);
        }
        return name;
    }

    public String getPropertiesKey() {
        return "WeekDay." + toString();
    }
}
