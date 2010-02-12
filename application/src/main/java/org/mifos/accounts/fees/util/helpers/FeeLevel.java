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

package org.mifos.accounts.fees.util.helpers;

public enum FeeLevel {
    CLIENTLEVEL((short) 1), GROUPLEVEL((short) 2), CENTERLEVEL((short) 3);

    Short value;

    FeeLevel(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public static FeeLevel getFeeLevel(Short value) {
        for (FeeLevel feeLevel : FeeLevel.values())
            if (feeLevel.getValue().equals(value))
                return feeLevel;
        return null;
    }
}
