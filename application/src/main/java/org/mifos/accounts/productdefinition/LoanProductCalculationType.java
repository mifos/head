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

package org.mifos.accounts.productdefinition;

public enum LoanProductCalculationType {
    UNKNOWN(0), SAME_FOR_ALL_LOANS(1), BY_LAST_LOAN(2), BY_LOAN_CYCLE(3);

    private final int value;

    private LoanProductCalculationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static LoanProductCalculationType fromInt(int value) {

        LoanProductCalculationType type = UNKNOWN;

        switch (value) {
        case 0:
            type = UNKNOWN;
            break;
        case 1:
            type = SAME_FOR_ALL_LOANS;
            break;
        case 2:
            type = BY_LAST_LOAN;
            break;
        case 3:
            type = BY_LOAN_CYCLE;
            break;
        default:
            type = UNKNOWN;
            break;
        }

        return type;
    }
}
