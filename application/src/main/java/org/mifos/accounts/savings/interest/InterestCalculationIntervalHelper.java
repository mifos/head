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
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;

public class InterestCalculationIntervalHelper {

    public List<InterestCalculationInterval> determineAllPossibleInterestCalculationPeriods(LocalDate firstDepositDate,
            InterestScheduledEvent interestCalculationEvent, LocalDate cutOffDateForValidCalculationPeriods) {

        List<InterestCalculationInterval> validInterestCalculationIntervals = new ArrayList<InterestCalculationInterval>();

        // fiscal start of year is jan-01 so begin at jan-01 of first deposit.
        LocalDate startOfFiscalYearOfFirstDeposit = new LocalDate(new DateTime().withYearOfEra(
                firstDepositDate.getYear()).withMonthOfYear(1).withDayOfMonth(1));

        List<LocalDate> allMatchingDates = interestCalculationEvent.findAllMatchingDatesFromBaseDateToCutOffDate(
                startOfFiscalYearOfFirstDeposit, cutOffDateForValidCalculationPeriods);
        for (LocalDate matchingDate : allMatchingDates) {
            LocalDate firstDayofInterval = interestCalculationEvent.findFirstDateOfPeriodForMatchingDate(matchingDate);

            InterestCalculationInterval interval = new InterestCalculationInterval(firstDayofInterval, matchingDate);
            if (interval.dateFallsWithin(firstDepositDate)) {
                interval = new InterestCalculationInterval(firstDepositDate, matchingDate);
                validInterestCalculationIntervals.add(interval);
            } else if (matchingDate.isAfter(firstDepositDate)
                    && matchingDate.isBefore(cutOffDateForValidCalculationPeriods)
                    || matchingDate.isEqual(cutOffDateForValidCalculationPeriods)) {
                validInterestCalculationIntervals.add(interval);
            }
        }

        return validInterestCalculationIntervals;
    }

    public List<InterestCalculationInterval> determineInterestCalculationPeriods(Interval postingInterval,
            LocalDate firstDepositDate, InterestScheduledEvent interestCalculationEvent) {
        List<InterestCalculationInterval> validInterestCalculationIntervals = new ArrayList<InterestCalculationInterval>();

        List<LocalDate> allMatchingDates = interestCalculationEvent.findAllMatchingDatesFromBaseDateToCutOffDate(
                postingInterval.getStart().toLocalDate(), postingInterval.getEnd().toLocalDate());
        for (LocalDate matchingDate : allMatchingDates) {
            LocalDate firstDayofInterval = interestCalculationEvent.findFirstDateOfPeriodForMatchingDate(matchingDate);

            InterestCalculationInterval interval = new InterestCalculationInterval(firstDayofInterval, matchingDate);
            if (interval.dateFallsWithin(firstDepositDate)) {
                interval = new InterestCalculationInterval(firstDepositDate, matchingDate);
                validInterestCalculationIntervals.add(interval);
            }
        }

        return validInterestCalculationIntervals;
    }

    public List<InterestCalculationInterval> determineAllPossibleInterestPostingPeriods(
            InterestScheduledEvent postingEvent, LocalDate firstAccountActivityDate, LocalDate currentPostingDate) {
        List<InterestCalculationInterval> validInterestPostingIntervals = new ArrayList<InterestCalculationInterval>();

        // fiscal start of year is jan-01 so begin at jan-01 of first deposit.
        LocalDate startOfFiscalYearOfFirstDeposit = new LocalDate(new DateTime().withYearOfEra(
                firstAccountActivityDate.getYear()).withMonthOfYear(1).withDayOfMonth(1));

        List<LocalDate> allMatchingDates = postingEvent.findAllMatchingDatesFromBaseDateToCutOffDate(
                startOfFiscalYearOfFirstDeposit, currentPostingDate);
        for (LocalDate matchingDate : allMatchingDates) {
            LocalDate firstDayofInterval = postingEvent.findFirstDateOfPeriodForMatchingDate(matchingDate);

            InterestCalculationInterval interval = new InterestCalculationInterval(firstDayofInterval, matchingDate);
            if (matchingDate.isAfter(firstAccountActivityDate) && matchingDate.isBefore(currentPostingDate)
                    || matchingDate.isEqual(currentPostingDate)) {
                validInterestPostingIntervals.add(interval);
            }
        }

        return validInterestPostingIntervals;
    }
}