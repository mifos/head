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

package org.mifos.dto.domain;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("PMD")
public class SavingsPerformanceHistoryDto implements Serializable {

    private final Date openedDate;
    private final String totalDeposits;
    private final String totalWithdrawals;
    private final String totalInterestEarned;
    private final String missedDeposits;

    public SavingsPerformanceHistoryDto(Date openedDate, String totalDeposits, String totalWithdrawals, String totalInterestEarned, String missedDeposits) {
        this.openedDate = openedDate;
        this.totalDeposits = totalDeposits;
        this.totalWithdrawals = totalWithdrawals;
        this.totalInterestEarned = totalInterestEarned;
        this.missedDeposits = missedDeposits;
    }

    public Date getOpenedDate() {
        return openedDate;
    }

    public String getTotalDeposits() {
        return totalDeposits;
    }

    public String getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public String getTotalInterestEarned() {
        return totalInterestEarned;
    }

    public String getMissedDeposits() {
        return missedDeposits;
    }
}