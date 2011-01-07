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

import org.joda.time.Days;
import org.mifos.framework.util.helpers.Money;

/**
 * I represent a valid Interest Calculation Period.
 *
 * In mifos, savings interest calculation is to occur every x days/months.
 */
public class InterestCalculationPeriodDetail {

    private final CalendarPeriod interval;
    private final List<EndOfDayDetail> dailyDetails;
    private final Money balanceBeforeInterval;
    private final boolean isFirstActivityBeforeInterval;

    /**
     * I am responsible for ensuring a {@link InterestCalculationPeriodDetail} is populated with correct
     * {@link EndOfDayDetail}'s applicable to given period and with the running balance of the account before this
     * period.
     */
    public static InterestCalculationPeriodDetail populatePeriodDetailBasedOnInterestCalculationInterval(
            CalendarPeriod calculationPeriod, List<EndOfDayDetail> allEndOfDayDetailsForAccount, Money balanceBeforeInterval) {

        Money balance = balanceBeforeInterval;
        boolean isFirstActivityBeforeInterval = true;

        if(!allEndOfDayDetailsForAccount.isEmpty()) {
            if(!allEndOfDayDetailsForAccount.get(0).getDate().isBefore(calculationPeriod.getStartDate())) {
                isFirstActivityBeforeInterval = false;
            }
        }

        List<EndOfDayDetail> applicableDailyDetailsForPeriod = new ArrayList<EndOfDayDetail>();

        for (EndOfDayDetail endOfDayDetail : allEndOfDayDetailsForAccount) {
            if (calculationPeriod.contains(endOfDayDetail.getDate())) {
                applicableDailyDetailsForPeriod.add(endOfDayDetail);
            }
        }

        return new InterestCalculationPeriodDetail(calculationPeriod, applicableDailyDetailsForPeriod, balance, isFirstActivityBeforeInterval);
    }

    public InterestCalculationPeriodDetail(CalendarPeriod interval, List<EndOfDayDetail> dailyDetails, Money balanceBeforeInterval, boolean isFirstActivityBeforeInterval) {
        this.dailyDetails = dailyDetails;
        this.balanceBeforeInterval = balanceBeforeInterval;
        this.interval = interval;
        this.isFirstActivityBeforeInterval = isFirstActivityBeforeInterval;
    }

    public CalendarPeriod getInterval() {
        return this.interval;
    }

    public List<EndOfDayDetail> getDailyDetails() {
        return this.dailyDetails;
    }

    public int getDuration() {
        int duration = Days.daysBetween(interval.getStartDate(), interval.getEndDate()).getDays();
        if (isFirstActivityBeforeInterval) {
            duration += 1;
        }
        return duration;
    }

    public Money getBalanceBeforeInterval() {
        return balanceBeforeInterval;
    }

    public boolean isFirstActivityBeforeInterval() {
        return isFirstActivityBeforeInterval;
    }

    public Money zeroAmount() {
        return Money.zero(this.balanceBeforeInterval.getCurrency());
    }

    public Money sumOfPrincipal() {
        Money principalForPeriod = zeroAmount();

        for (EndOfDayDetail daysDetail : this.dailyDetails) {
            principalForPeriod = principalForPeriod.add(daysDetail.getResultantAmountForDay());
        }

        return principalForPeriod;
    }

    public Money sumOfInterest() {
        Money interestForPeriod = zeroAmount();

        for (EndOfDayDetail daysDetail : this.dailyDetails) {
            interestForPeriod = interestForPeriod.add(daysDetail.getInterest());
        }

        return interestForPeriod;
    }
}