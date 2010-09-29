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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.framework.util.helpers.Money;

public class AverageBalanceInterestCalculator extends AbstractInterestCalculator {

    private static int internalPrecision = 5;

    private static RoundingMode internalRoundingMode = RoundingMode.HALF_UP;

    private static MathContext mc = new MathContext(internalPrecision, internalRoundingMode);

    @Override
    public Money getPrincipal(List<EndOfDayBalance> balanceRecords, final LocalDate calculationStartDate, final LocalDate calculationEndDate) {

        validateData(balanceRecords,"balanceRecords list");
        validateData(calculationStartDate,"calculationStartDate");
        validateData(calculationEndDate,"calculationEndDate");

        LocalDate startDate = calculationStartDate.minusDays(1);
        LocalDate endDate = calculationEndDate;
        Money totalBalance = null;
        int duration = 0;
        Money principal = null;

        if (!balanceRecords.isEmpty()) {
            if (balanceRecords.get(0).getDate().isBefore(startDate) || balanceRecords.get(balanceRecords.size()- 1).getDate().isAfter(endDate)) {
                throw new IllegalArgumentException("Tinvalid list of EndOfDayDetails");
            }
            startDate = balanceRecords.get(0).getDate();
        }

        duration = Days.daysBetween(startDate, endDate).getDays();
        totalBalance = calculateTotalBalance(balanceRecords, startDate, endDate);

        if (duration != 0 && totalBalance != null) {
            principal = totalBalance.divide(duration);
        }

        return principal;
    }

    private Money calculateTotalBalance(List<EndOfDayBalance> balanceRecords, LocalDate startDate, LocalDate endDate) {
        LocalDate prevDate = startDate;
        Money totalBalance = null;
        for (int count = 0; count < balanceRecords.size(); count++) {
            LocalDate nextDate = null;
            if (count < balanceRecords.size() - 1) {
                nextDate = balanceRecords.get(count + 1).getDate();
            } else {
                nextDate = endDate;
            }

            int subDuration = Days.daysBetween(prevDate, nextDate).getDays();

            if (totalBalance == null) {
                totalBalance = balanceRecords.get(count).getBalance().multiply(subDuration);
            } else {
                totalBalance = totalBalance.add(balanceRecords.get(count).getBalance().multiply(subDuration));
            }
            prevDate = nextDate;
        }
        return totalBalance;
    }

    private BigDecimal getPrincipal(EndOfDayDetail[] deposits) {
        LocalDate startDate = new LocalDate(2010, 9, 1).minusDays(1);
        LocalDate endDate = new LocalDate(2010, 9, 30);
        int duration = 0;
        BigDecimal totalBalance = BigDecimal.ZERO;

        if (deposits != null && deposits.length > 0) {
            if (deposits[0].getDate().isBefore(startDate) || deposits[deposits.length - 1].getDate().isAfter(endDate)) {
                throw new IllegalArgumentException("invalid list of EndOfDayDetails");
            }
            startDate = deposits[0].getDate();
        }

        duration = Days.daysBetween(startDate, endDate).getDays();

        LocalDate prevDate = startDate;

        BigDecimal runningBalance = BigDecimal.ZERO;

        for (int count = 0; count < deposits.length; count++) {

            LocalDate nextDate = null;

            if (count < deposits.length - 1) {
                nextDate = deposits[count + 1].getDate();
            } else {
                nextDate = endDate;
            }

            int subDuration = Days.daysBetween(prevDate, nextDate).getDays();

            runningBalance = runningBalance.add(deposits[count].getAmount());

            totalBalance = totalBalance.add(runningBalance.multiply(new BigDecimal(subDuration)));

            prevDate = nextDate;
        }

        if (duration != 0 && totalBalance.compareTo(BigDecimal.ZERO) != 0) {
            return totalBalance.divide(BigDecimal.valueOf(duration), mc);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calcInterest(InterestCalculationRange interestCalculationRange, EndOfDayDetail... depositDetail) {

        BigDecimal principal = getPrincipal(depositDetail);

        // BigDecimal principal = new BigDecimal(totalBalance.floatValue() / (deposits.length));

        double intRate = 10;

        int duration = 365; // one year

        int accountingNumberOfInterestDaysInYear = 365; // one year

        intRate = (intRate / accountingNumberOfInterestDaysInYear) * duration;

        BigDecimal interestAmount = principal.multiply(new BigDecimal(1 + intRate / 100)).subtract(principal);
        interestAmount = interestAmount.round(new MathContext(internalPrecision, internalRoundingMode));
        return interestAmount;
    }

}
