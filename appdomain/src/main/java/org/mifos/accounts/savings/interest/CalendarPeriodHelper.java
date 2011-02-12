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

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;

public class CalendarPeriodHelper {

    /**
     * determines all possible periods from the start of the fiscal year of the accounts first activity date.
     */
    public List<CalendarPeriod> determineAllPossiblePeriods(LocalDate firstActivityDate,
            InterestScheduledEvent interestCalculationEvent, LocalDate endDateOfLastPeriod) {

        List<CalendarPeriod> validIntervals = new ArrayList<CalendarPeriod>();

        // fiscal start of year is jan-01 so begin at jan-01 of first deposit.
        LocalDate startOfFiscalYearOfFirstDeposit = new LocalDate(new DateTime().withYearOfEra(
                firstActivityDate.getYear()).withMonthOfYear(1).withDayOfMonth(1));

        List<LocalDate> allMatchingDates = interestCalculationEvent.findAllMatchingDatesFromBaseDateUpToAndIncludingNearestMatchingEndDate(startOfFiscalYearOfFirstDeposit, endDateOfLastPeriod);
        for (LocalDate matchingDate : allMatchingDates) {
            LocalDate firstDayofInterval = interestCalculationEvent.findFirstDateOfPeriodForMatchingDate(matchingDate);

            CalendarPeriod interval = new CalendarPeriod(firstDayofInterval, matchingDate);
            if (interval.contains(firstActivityDate)) {
                if (matchingDate.isAfter(endDateOfLastPeriod)) {
                    interval = new CalendarPeriod(firstActivityDate, endDateOfLastPeriod);
                } else {
                    interval = new CalendarPeriod(firstActivityDate, matchingDate);
                }
                validIntervals.add(interval);
            } else if (matchingDate.isAfter(firstActivityDate) && matchingDate.isBefore(endDateOfLastPeriod)
                    || matchingDate.isEqual(endDateOfLastPeriod)) {
                validIntervals.add(interval);
            } else if (matchingDate.isAfter(endDateOfLastPeriod) && (firstDayofInterval.isBefore(endDateOfLastPeriod) || firstDayofInterval.isEqual(endDateOfLastPeriod))) {
                interval = new CalendarPeriod(firstDayofInterval, endDateOfLastPeriod);
                validIntervals.add(interval);
            }
        }

        return validIntervals;
    }
}