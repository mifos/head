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

package org.mifos.accounts.util.helpers;

public enum AccountStateFlag {
    LOAN_WITHDRAW(1), LOAN_REJECTED(2), LOAN_OTHER(3), SAVINGS_WITHDRAW(4), SAVINGS_REJECTED(5), SAVINGS_BLACKLISTED(6), LOAN_REVERSAL(
            7);

    Short value;

    private AccountStateFlag(int value) {
        this.value = (short) value;
    }

    public Short getValue() {
        return value;
    }

    public static AccountStateFlag getStatusFlag(Short value) {
        for (AccountStateFlag statusFlag : AccountStateFlag.values())
            if (statusFlag.getValue().equals(value))
                return statusFlag;
        return null;
    }
}
