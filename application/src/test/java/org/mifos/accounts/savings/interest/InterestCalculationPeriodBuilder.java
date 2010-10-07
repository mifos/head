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

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class InterestCalculationPeriodBuilder {

    private InterestCalculationInterval interval;
    private List<EndOfDayDetail> dailyDetails = new ArrayList<EndOfDayDetail>();
    private MifosCurrency currency = TestUtils.RUPEE;
    private Money balanceBeforeInterval = TestUtils.createMoney("10");
    private Money minBalanceRequired  = TestUtils.createMoney("25");
    private Double interestRate = Double.valueOf("10");
    private Boolean isFirstActivityBeforeInterval = Boolean.FALSE;
    private LocalDate intervalStartDate = new LocalDate(2010, 1, 1);
    private LocalDate intervalEndDate = new LocalDate(2010, 12, 31);

    public InterestCalculationPeriodDetail build() {
        interval = new InterestCalculationInterval(intervalStartDate, intervalEndDate);
        return new InterestCalculationPeriodDetail(interval, dailyDetails, minBalanceRequired, balanceBeforeInterval, currency, interestRate, isFirstActivityBeforeInterval);
    }

    public InterestCalculationPeriodBuilder from(LocalDate intervalStartDate) {
        this.intervalStartDate = intervalStartDate;
        return this;
    }

    public InterestCalculationPeriodBuilder to(LocalDate intervalEndDate) {
        this.intervalEndDate = intervalEndDate;
        return this;
    }

    public InterestCalculationPeriodBuilder withStartingBalance(String startingBalance) {
        this.balanceBeforeInterval = TestUtils.createMoney(startingBalance);
        return this;
    }

    public InterestCalculationPeriodBuilder withMinRequiredBalance(String minimumRequiredBalance) {
        this.minBalanceRequired = TestUtils.createMoney(minimumRequiredBalance);
        return this;
    }

    public InterestCalculationPeriodBuilder containing(EndOfDayDetail... endOfDayDetails) {
        for (EndOfDayDetail dailyDetail : endOfDayDetails) {
            this.dailyDetails.add(dailyDetail);
        }
        return this;
    }
}