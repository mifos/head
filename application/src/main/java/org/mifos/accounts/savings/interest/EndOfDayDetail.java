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

package org.mifos.accounts.savings.interest;

import org.joda.time.LocalDate;
import org.mifos.framework.util.helpers.Money;

public class EndOfDayDetail {

    private final LocalDate date;
    private final Money deposits;
    private final Money withdrawals;
    private final Money interest;

    public EndOfDayDetail(LocalDate date, Money deposits, Money withdrawals, Money interest) {
        this.date = date;
        this.deposits = deposits;
        this.withdrawals = withdrawals;
        this.interest = interest;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Money getBalanceForDay() {
        return this.deposits.subtract(this.withdrawals).add(this.interest);
    }
}
