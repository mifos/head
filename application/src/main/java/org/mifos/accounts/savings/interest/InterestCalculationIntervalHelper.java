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
import org.mifos.schedule.internal.MonthlyAtEndOfMonthScheduledEvent;

public class InterestCalculationIntervalHelper {

    public List<InterestCalculationInterval> determineAllPossibleInterestCalculationPeriods(LocalDate firstDepositDate, ScheduledEvent interestCalculationEvent, LocalDate cutOffDateForValidCalculationPeriods) {

        List<InterestCalculationInterval> validInterestCalculationIntervals = new ArrayList<InterestCalculationInterval>();

        // fiscal start of year is jan-01 so begin at jan-01 of first deposit.
        LocalDate startOfFiscalYearOfFirstDeposit = new LocalDate(new DateTime().withYearOfEra(firstDepositDate.getYear()).withMonthOfYear(1).withDayOfMonth(1));

        LocalDate startFrom = startOfFiscalYearOfFirstDeposit;
        while (startFrom.isBefore(cutOffDateForValidCalculationPeriods)) {
            LocalDate endDate = new LocalDate(interestCalculationEvent.nextEventDateAfter(startFrom.toDateTimeAtCurrentTime()));

            LocalDate firstDayofInterval = startFrom;
            if (interestCalculationEvent instanceof MonthlyAtEndOfMonthScheduledEvent) {
                firstDayofInterval = startFrom;
            } else {
                firstDayofInterval = startFrom.plusDays(1);
            }

            InterestCalculationInterval interval = new InterestCalculationInterval(firstDayofInterval, endDate);
            if (interval.dateFallsWithin(firstDepositDate)) {
                interval = new InterestCalculationInterval(firstDepositDate, endDate);
                validInterestCalculationIntervals.add(interval);
            } else if (endDate.isAfter(firstDepositDate) && endDate.isBefore(cutOffDateForValidCalculationPeriods) || endDate.isEqual(cutOffDateForValidCalculationPeriods)) {
                validInterestCalculationIntervals.add(interval);
            }

            if (interestCalculationEvent instanceof MonthlyAtEndOfMonthScheduledEvent) {
                startFrom = endDate.plusDays(1);
            } else {
                startFrom = endDate;
            }
        }

        return validInterestCalculationIntervals;
    }
}