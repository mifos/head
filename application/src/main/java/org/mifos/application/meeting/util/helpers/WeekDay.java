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

package org.mifos.application.meeting.util.helpers;

import org.mifos.application.master.MessageLookup;
import org.mifos.config.LocalizedTextLookup;

public enum WeekDay implements LocalizedTextLookup {
    SUNDAY((short) 1), MONDAY((short) 2), TUESDAY((short) 3), WEDNESDAY((short) 4), THURSDAY((short) 5), FRIDAY(
            (short) 6), SATURDAY((short) 7);

    Short value;
    String name;

    WeekDay(Short value) {
        this.value = value;
        this.name = null;

    }

    public Short getValue() {
        return value;
    }

    public static WeekDay getWeekDay(int value) {
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
        if (name == null)
            name = MessageLookup.getInstance().lookup(this);
        return name;
    }

    public String getPropertiesKey() {
        return "WeekDay." + toString();
    }
}
