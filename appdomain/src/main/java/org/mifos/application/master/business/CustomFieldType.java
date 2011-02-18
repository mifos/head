/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.application.master.business;

import org.mifos.config.LocalizedTextLookup;

public enum CustomFieldType implements LocalizedTextLookup {

    NONE((short) 0), NUMERIC((short) 1), ALPHA_NUMERIC((short) 2), DATE((short) 3);

    private Short value;

    private CustomFieldType(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public static CustomFieldType fromInt(int value) {
        for (CustomFieldType candidate : values()) {
            if (candidate.getValue() == value) {
                return candidate;
            }
        }
        return CustomFieldType.NONE;
    }

    public String getPropertiesKey() {
        return "CustomFieldType." + toString();
    }

}
