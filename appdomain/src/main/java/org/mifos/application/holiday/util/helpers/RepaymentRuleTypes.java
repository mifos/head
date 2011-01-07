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

import org.mifos.application.master.MessageLookup;

public enum RepaymentRuleTypes {
    SAME_DAY((short) 1, "RepaymentRule-SameDay"), //
    NEXT_MEETING_OR_REPAYMENT((short) 2, "RepaymentRule-NextMeetingRepayment"), //
    NEXT_WORKING_DAY((short) 3, "RepaymentRule-NextWorkingDay"), //
    REPAYMENT_MORATORIUM((short) 4, "RepaymentRule-RepaymentMoratorium");

    private Short value;
    private String propertyKey;

    RepaymentRuleTypes(final Short value, final String key) {
        this.value = value;
        this.propertyKey = key;
    }

    public Short getValue() {
        return value;
    }

    public String getPropertiesKey() {
        return propertyKey;
    }

    public static RepaymentRuleTypes fromShort(final Short id) {
        for (RepaymentRuleTypes adjustmentRule : values()) {
            if (adjustmentRule.getValue().equals(id)) {
                return adjustmentRule;
            }
        }

        throw new IllegalArgumentException("No " + RepaymentRuleTypes.class.getSimpleName() + " defined for id=" + id);
    }

    public static RepaymentRuleTypes fromInt(Integer id) {
        return fromShort(id.shortValue());
    }

    /**
     * This method will always read from Message resource bundle as
     * see the class doc of {@link MessageLookup}
     * @return
     */
    public String getName() {
        return MessageLookup.getInstance().lookup(getPropertiesKey());
    }
}
