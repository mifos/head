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

package org.mifos.accounts.savings.interest;

import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;

public class AverageBalanceCalculationStrategy implements PrincipalCalculationStrategy {

    @Override
    public Money calculatePrincipal(InterestCalculationPeriodDetail interestCalculationPeriodDetail) {

        CalendarPeriod interestCalculationInterval = interestCalculationPeriodDetail.getInterval();

        LocalDate startDate = interestCalculationInterval.getStartDate();
        LocalDate endDate = interestCalculationInterval.getEndDate();

        List<EndOfDayDetail> endOfDayDetails = interestCalculationPeriodDetail.getDailyDetails();

        if (endOfDayDetails.isEmpty()) {
            return interestCalculationPeriodDetail.getBalanceBeforeInterval();
        }

        int duration = interestCalculationPeriodDetail.getDuration();
        LocalDate prevDate = startDate;
        LocalDate nextDate = endOfDayDetails.get(0).getDate();
        Money runningBalance = interestCalculationPeriodDetail.getBalanceBeforeInterval();

        //Calculation of effect of previous balance till the first activity in the calculation interval
        int subDuration = Days.daysBetween(prevDate, nextDate).getDays();

        // if not a period which has first activity rule, then number of days is inclusive e.g duration for 1st to 10th is 10 days (not 9)
        if (interestCalculationPeriodDetail.isFirstActivityBeforeInterval()) {
            subDuration++;
        }

        Money totalBalance = runningBalance.multiply(subDuration);

        prevDate = nextDate;

        for (int count = 0; count < endOfDayDetails.size(); count++) {

            if (count < endOfDayDetails.size() - 1) {
                nextDate = endOfDayDetails.get(count + 1).getDate();
            } else {
                nextDate = endDate;
            }

            subDuration = Days.daysBetween(prevDate, nextDate).getDays();

            if(count==0 && !interestCalculationPeriodDetail.isFirstActivityBeforeInterval()) {
                subDuration -= 1;
                duration -= 1;
            }

            runningBalance = runningBalance.add(endOfDayDetails.get(count).getResultantAmountForDay());

            totalBalance = totalBalance.add(runningBalance.multiply(subDuration));

            prevDate = nextDate;
        }

        if (duration != 0) {
            totalBalance = totalBalance.divide(duration);
        }

        return MoneyUtils.currencyRound(totalBalance);
    }
}