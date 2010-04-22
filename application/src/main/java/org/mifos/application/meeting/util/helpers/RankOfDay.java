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

import java.util.List;

import org.mifos.application.master.MessageLookup;

import edu.emory.mathcs.backport.java.util.Arrays;

public enum RankOfDay {

    FIRST ((short) 1),
    SECOND((short) 2),
    THIRD ((short) 3),
    FOURTH((short) 4),
    LAST  ((short) 5);

    Short value;

    RankOfDay(final Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public static RankOfDay getRankOfDay(final Integer value) {
        for (RankOfDay rank : RankOfDay.values()) {
            if (rank.getValue().equals(value.shortValue())) {
                return rank;
            }
        }
        return null;
    }

    public static RankOfDay getRankOfDay(final Short value) {
        return getRankOfDay(value.intValue());
    }

    public static List<RankOfDay> getRankOfDayList() {
        return Arrays.asList(values());
    }

    /**
     * This method will always read from Message resource bundle as
     * see the class doc of {@link MessageLookup}
     * @return
     */
    public String getName() {
        return MessageLookup.getInstance().lookup(getPropertiesKey());
    }

    public String getPropertiesKey() {
        return RankOfDay.class.getSimpleName()+ "." + toString();
    }
}
