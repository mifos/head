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

import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;

public class AverageBalanceCaluclationStrategy implements PrincipalCalculationStrategy {

    @Override
    public Money calculatePrincipal(InterestCalculationPeriodDetail interestCalculationPeriodDetail) {

        InterestCalculationInterval interestCalculationInterval = interestCalculationPeriodDetail.getInterval();

        LocalDate startDate = interestCalculationInterval.getStartDate();
        LocalDate endDate = interestCalculationInterval.getEndDate();

        List<EndOfDayDetail> endOfDayDetails = interestCalculationPeriodDetail.getDailyDetails();

        MifosCurrency currencyInUse = Money.getDefaultCurrency();
        if (!endOfDayDetails.isEmpty()) {
            startDate = endOfDayDetails.get(0).getDate();
            currencyInUse = endOfDayDetails.get(0).getResultantAmountForDay().getCurrency();
        }

        int duration = Days.daysBetween(startDate, endDate).getDays();
        LocalDate prevDate = startDate;

        Money totalBalance = Money.zero(currencyInUse);
        Money runningBalance = interestCalculationPeriodDetail.getBalanceBeforeInterval();

        for (int count = 0; count < endOfDayDetails.size(); count++) {

            LocalDate nextDate = null;

            if (count < endOfDayDetails.size() - 1) {
                nextDate = endOfDayDetails.get(count + 1).getDate();
            } else {
                nextDate = endDate;
            }

            int subDuration = Days.daysBetween(prevDate, nextDate).getDays();

            if (runningBalance.equals(Money.zero(currencyInUse))) {
                runningBalance = endOfDayDetails.get(count).getResultantAmountForDay();
            } else {
                runningBalance = runningBalance.add(endOfDayDetails.get(count).getResultantAmountForDay());
            }

            if (totalBalance.equals(Money.zero(currencyInUse))) {
                totalBalance = runningBalance.multiply(subDuration);
            } else {
                totalBalance = totalBalance.add(runningBalance.multiply(subDuration));
            }
            prevDate = nextDate;
        }

        if (duration != 0 && totalBalance.isNonZero()) {
            totalBalance = totalBalance.divide(duration);
        }

        return totalBalance;
    }
}