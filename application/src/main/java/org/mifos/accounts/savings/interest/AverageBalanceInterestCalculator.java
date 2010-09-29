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

import org.mifos.framework.util.helpers.Money;

public class AverageBalanceInterestCalculator implements InterestCalculator {

    // TODO - unit test me on my own
    private PrincipalInterestCalculationPolicy interestCaluclationPolicy = new PrincipalInterestCalculationPolicy();

    private PrincipalCalculationStrategy principalCalculationStrategy = new AverageBalancePrincipalCaluclationStrategy();

    // TODO - can unit test me on my own through mocking
    @Override
    public Money calcInterest(InterestCalculationRange interestCalculationRange, EndOfDayDetail... depositDetail) {

        Money principal = principalCalculationStrategy.calculatePrincipal(interestCalculationRange, depositDetail);

        return interestCaluclationPolicy.calculateInterest(principal);
    }

//    @Override
//    public Money getPrincipal(List<EndOfDayBalance> balanceRecords, final LocalDate calculationStartDate,
//            final LocalDate calculationEndDate) {
//
////        validateData(balanceRecords, "balanceRecords list");
////        validateData(calculationStartDate, "calculationStartDate");
////        validateData(calculationEndDate, "calculationEndDate");
//
//        LocalDate startDate = calculationStartDate.minusDays(1);
//        LocalDate endDate = calculationEndDate;
//        Money totalBalance = null;
//        int duration = 0;
//        Money principal = null;
//
//        if (!balanceRecords.isEmpty()) {
//            if (balanceRecords.get(0).getDate().isBefore(startDate)
//                    || balanceRecords.get(balanceRecords.size() - 1).getDate().isAfter(endDate)) {
//                throw new IllegalArgumentException("Tinvalid list of EndOfDayDetails");
//            }
//            startDate = balanceRecords.get(0).getDate();
//        }
//
//        duration = Days.daysBetween(startDate, endDate).getDays();
//        totalBalance = calculateTotalBalance(balanceRecords, startDate, endDate);
//
//        if (duration != 0 && totalBalance != null) {
//            principal = totalBalance.divide(duration);
//        }
//
//        return principal;
//    }
//
//    private Money calculateTotalBalance(List<EndOfDayBalance> balanceRecords, LocalDate startDate, LocalDate endDate) {
//        LocalDate prevDate = startDate;
//        Money totalBalance = null;
//        for (int count = 0; count < balanceRecords.size(); count++) {
//            LocalDate nextDate = null;
//            if (count < balanceRecords.size() - 1) {
//                nextDate = balanceRecords.get(count + 1).getDate();
//            } else {
//                nextDate = endDate;
//            }
//
//            int subDuration = Days.daysBetween(prevDate, nextDate).getDays();
//
//            if (totalBalance == null) {
//                totalBalance = balanceRecords.get(count).getBalance().multiply(subDuration);
//            } else {
//                totalBalance = totalBalance.add(balanceRecords.get(count).getBalance().multiply(subDuration));
//            }
//            prevDate = nextDate;
//        }
//        return totalBalance;
//    }
}