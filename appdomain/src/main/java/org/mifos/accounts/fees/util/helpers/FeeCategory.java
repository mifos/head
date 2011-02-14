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

package org.mifos.accounts.fees.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum FeeCategory {
    ALLCUSTOMERS((short) 1), CLIENT((short) 2), GROUP((short) 3), CENTER((short) 4), LOAN((short) 5);

    Short value;

    FeeCategory(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public static FeeCategory getFeeCategory(Short value) throws PropertyNotFoundException {
        for (FeeCategory feeCategory : FeeCategory.values()) {
            if (feeCategory.getValue().equals(value)) {
                return feeCategory;
            }
        }
        throw new PropertyNotFoundException("FeeCategory");
    }
}
