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

package org.mifos.application.holiday.util.helpers;

public enum RepaymentRuleTypes {
    SAME_DAY((short) 1), NEXT_MEETING_OR_REPAYMENT((short) 2), NEXT_WORKING_DAY((short) 3), REPAYMENT_MORATORIUM((short) 4);

    Short value;

    RepaymentRuleTypes(final Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public static RepaymentRuleTypes fromShort(final Short id) {
        
        for (RepaymentRuleTypes adjustmentRule : values()) {
            if (adjustmentRule.getValue().equals(id)) {
                return adjustmentRule;
            }
        }
        
        return null;
    }
    
    public static RepaymentRuleTypes fromOrd(int i) {
        if (i < 0 || i >= RepaymentRuleTypes.values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal value for RepaymentRuleTypes");
        }
        return RepaymentRuleTypes.values()[i];
    }

}
