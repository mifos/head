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

package org.mifos.accounts.savings.interest.schedule.internal;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;

/**
 * I represent an {@link InterestScheduledEvent} that occurs every number of months on the last day of that month.
 */
public class MonthlyOnLastDayOfMonthInterestScheduledEvent implements InterestScheduledEvent {

    private final int every;

    public MonthlyOnLastDayOfMonthInterestScheduledEvent(int every) {
        this.every = every;
    }

    @Override
    public LocalDate nextMatchingDateFromAlreadyMatchingDate(LocalDate validMatchingDate) {
        return lastDayOfMonthFor(validMatchingDate.plusMonths(every));
    }

    private LocalDate lastDayOfMonthFor(LocalDate date) {
        return new LocalDate(date.toDateTimeAtStartOfDay().dayOfMonth().withMaximumValue());
    }

    @Override
    public List<LocalDate> findAllMatchingDatesFromBaseDateToCutOffDate(LocalDate baseDate, LocalDate cutOffDate) {
        List<LocalDate> allMatchingDates = new ArrayList<LocalDate>();

        LocalDate previousMatchingDate = baseDate.minusDays(1);
        LocalDate matchingDate = nextMatchingDateFromAlreadyMatchingDate(previousMatchingDate);
        while (matchingDate.isEqual(cutOffDate) || matchingDate.isBefore(cutOffDate)) {
            allMatchingDates.add(matchingDate);
            matchingDate = nextMatchingDateFromAlreadyMatchingDate(matchingDate);
        }

        return allMatchingDates;
    }

    @Override
    public LocalDate findFirstDateOfPeriodForMatchingDate(LocalDate matchingDate) {
        LocalDate previousMatchingDate = lastDayOfMonthFor(matchingDate.minusMonths(every));
        return previousMatchingDate.plusDays(1);
    }
}