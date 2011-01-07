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

package org.mifos.application.util.helpers;

public enum TrxnTypes {

    loan_disbursement(Short.valueOf("1")), loan_repayment(Short.valueOf("2")), savings_deposit(Short.valueOf("3")), savings_withdrawal(
            Short.valueOf("4")), fee(Short.valueOf("5"));

    Short value;

    TrxnTypes(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }
}
