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

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.schedule.ScheduledEvent;

public class InterestCalculationIntervalHelper {

    public List<InterestCalculationInterval> determineAllPossibleInterestCalculationPeriods(LocalDate firstDepositDate, ScheduledEvent interestCalculationEvent, LocalDate cutOffDateForRanges) {

        List<InterestCalculationInterval> validInterestCalculationIntervals = new ArrayList<InterestCalculationInterval>();

        // fiscal start of year is jan-01 so begin at jan-01 of first deposit.
        LocalDate startOfFiscalYearOfFirstDeposit = new LocalDate(new DateTime().withYearOfEra(firstDepositDate.getYear()).withMonthOfYear(1).withDayOfMonth(1));

        LocalDate startDate = firstDepositDate;
        LocalDate startFrom = startOfFiscalYearOfFirstDeposit;
        while (startFrom.isBefore(cutOffDateForRanges)) {
            LocalDate checkItOut = new LocalDate(interestCalculationEvent.nextEventDateAfter(startFrom.toDateTimeAtCurrentTime()));
            LocalDate endDate = calculateNextInterestCalculationDateStartingFrom(startFrom, interestCalculationEvent);
            if (endDate.isAfter(startDate)) {
                validInterestCalculationIntervals.add(new InterestCalculationInterval(startDate, endDate));
                startDate = endDate.plusDays(1);
            }
            startFrom = endDate.plusDays(1);
        }

        return validInterestCalculationIntervals;
    }

    private LocalDate calculateNextInterestCalculationDateStartingFrom(LocalDate startingFrom, ScheduledEvent interestCalculationEvent) {
        return startingFrom.withDayOfMonth(1).plusMonths(interestCalculationEvent.getEvery()).minusDays(1);
    }
}