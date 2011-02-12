/*
 * Copyright Grameen Foundation USA
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

import java.util.Date;

import org.joda.time.LocalDate;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.util.helpers.Money;

public class SavingsProductHistoricalInterestDetail {

    private final Integer id;
    private final Double interestRate;
    private final Money minAmntForInt;
    private final Date periodStartDate;
    private final Date periodEndDate;
    private final SavingsOfferingBO savingsProduct;

    /*
     * hibernate constructor
     */
    protected SavingsProductHistoricalInterestDetail() {
        this.id = null;
        this.interestRate = Double.valueOf("0");
        this.minAmntForInt = null;
        this.periodStartDate = null;
        this.periodEndDate = null;
        this.savingsProduct = null;
    }

    public SavingsProductHistoricalInterestDetail(CalendarPeriod calculationPeriod, Double interestRate, Money minAmntForInt, SavingsOfferingBO savingsProduct) {
        this.id = null;
        this.interestRate = interestRate;
        this.minAmntForInt = minAmntForInt;
        this.savingsProduct = savingsProduct;
        this.periodStartDate = calculationPeriod.getStartDate().toDateMidnight().toDate();
        this.periodEndDate = calculationPeriod.getEndDate().toDateMidnight().toDate();
    }

    public Double getInterestRate() {
        return this.interestRate;
    }

    public Money getMinAmntForInt() {
        return this.minAmntForInt;
    }

    public LocalDate getStartDate() {
        return new LocalDate(this.periodStartDate);
    }

    public LocalDate getEndDate() {
        return new LocalDate(this.periodEndDate);
    }
}