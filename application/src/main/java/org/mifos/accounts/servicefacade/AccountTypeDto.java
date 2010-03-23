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

package org.mifos.accounts.servicefacade;

public enum AccountTypeDto {
    LOAN_ACCOUNT(1), SAVINGS_ACCOUNT(2), CUSTOMER_ACCOUNT(3), INDIVIDUAL_LOAN_ACCOUNT(4);

    Short value;

    private AccountTypeDto(int value) {
        this.value = (short) value;
    }

    public Short getValue() {
        return value;
    }

    public static AccountTypeDto getAccountType(Short value) {
        for (AccountTypeDto accountTypes : AccountTypeDto.values()) {
            if (accountTypes.getValue().equals(value)) {
                return accountTypes;
            }
        }
        throw new RuntimeException("no account type " + value);
    }
}
