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
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class InterestCalculationPeriodResultBuilder {

    private InterestCalculationInterval interval;
    private LocalDate intervalStartDate = new LocalDate(2010, 1, 1);
    private LocalDate intervalEndDate = new LocalDate(2010, 12, 31);
    private Money averagePrincipal = TestUtils.createMoney("0");
    private Money totalPrincipal = TestUtils.createMoney("0");
    private Money calculatedInterest = TestUtils.createMoney("0");
    private Money previousTotalInterestForPeriod = TestUtils.createMoney("0");

    public InterestCalculationPeriodResult build() {
        interval = new InterestCalculationInterval(intervalStartDate, intervalEndDate);
        return new InterestCalculationPeriodResult(interval, calculatedInterest, averagePrincipal, totalPrincipal, previousTotalInterestForPeriod);
    }

    public InterestCalculationPeriodResultBuilder from(LocalDate intervalStartDate) {
        this.intervalStartDate = intervalStartDate;
        return this;
    }

    public InterestCalculationPeriodResultBuilder to(LocalDate intervalEndDate) {
        this.intervalEndDate = intervalEndDate;
        return this;
    }

    public InterestCalculationPeriodResultBuilder withTotalPrincipal(String withTotalPrincipal) {
        this.totalPrincipal = TestUtils.createMoney(withTotalPrincipal);
        return this;
    }

    public InterestCalculationPeriodResultBuilder withPreviousInterest(String withPreviousInterest) {
        this.previousTotalInterestForPeriod = TestUtils.createMoney(withPreviousInterest);
        return this;
    }

    public InterestCalculationPeriodResultBuilder withCalculatedInterest(String withCalculatedInterest) {
        this.calculatedInterest = TestUtils.createMoney(withCalculatedInterest);
        return this;
    }
}