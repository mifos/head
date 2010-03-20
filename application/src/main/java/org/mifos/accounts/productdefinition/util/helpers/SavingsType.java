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

package org.mifos.accounts.productdefinition.util.helpers;

public enum SavingsType {
    MANDATORY((short) 1), VOLUNTARY((short) 2);

    private Short value;

    private SavingsType(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public static SavingsType fromInt(int value) {
        for (SavingsType savingsType : SavingsType.values()) {
            if (savingsType.getValue() == value) {
                return savingsType;
            }
        }
        throw new RuntimeException("no savings type " + value);
    }
}
