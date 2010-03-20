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

public enum AccountActionTypes {
    LOAN_REPAYMENT(1), LOAN_PENALTY(2), LOAN_PENALTY_MISC(3), FEE_REPAYMENT(4), FEE_REPAYMENT_MISC(5), SAVINGS_DEPOSIT(
            6), SAVINGS_WITHDRAWAL(7), PAYMENT(8), LOAN_ADJUSTMENT(9), DISBURSAL(10), SAVINGS_INTEREST_POSTING(11), CUSTOMER_ACCOUNT_REPAYMENT(
            12), CUSTOMER_ADJUSTMENT(13), SAVINGS_ADJUSTMENT(14), WRITEOFF(15), WAIVEOFFDUE(16), WAIVEOFFOVERDUE(17), LOAN_REVERSAL(
            18), LOAN_DISBURSAL_AMOUNT_REVERSAL(19), LOAN_RESCHEDULED(20);

    private Short value;

    private AccountActionTypes(int value) {
        this.value = (short) value;
    }

    public Short getValue() {
        return value;
    }

    public static AccountActionTypes fromInt(int id) {
        for (AccountActionTypes candidate : AccountActionTypes.values()) {
            if (candidate.getValue() == id) {
                return candidate;
            }
        }
        throw new RuntimeException("no account action " + id);
    }

    public String getPropertiesKey() {
        return "AccountActionTypes." + toString();
    }

}
