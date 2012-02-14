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

package org.mifos.accounts.productdefinition.util.helpers;

import org.mifos.core.MifosRuntimeException;

public enum InterestType {

    FLAT((short) 1), DECLINING((short) 2), COMPOUND((short) 3), DECLINING_EPI((short) 4)/* Equal Principal Installments*/
    ,DECLINING_PB((short) 5);


    private Short value;

    private InterestType(Short value) {
        this.value = value;
    }

    public static boolean isDecliningInterestType(int interestTypeNum) {
        return fromInt(interestTypeNum).equals(DECLINING);
    }

    public static boolean isDecliningInterestPrincipalBalanceType(int interestTypeNum) {
        return fromInt(interestTypeNum).equals(DECLINING_PB);
    }

    public static boolean isFlatInterestType(int interestTypeNum) {
        return fromInt(interestTypeNum).equals(FLAT);
    }

    public Short getValue() {
        return value;
    }

    public static InterestType fromInt(int value) {
        for (InterestType candidate : InterestType.values()) {
            if (candidate.getValue() == value) {
                return candidate;
            }
        }
        throw new MifosRuntimeException("interest type " + value + " not recognized");
    }

    public String getValueAsString() {
        return value.toString();
    }
}
