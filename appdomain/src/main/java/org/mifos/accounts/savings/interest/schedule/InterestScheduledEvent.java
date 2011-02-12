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

package org.mifos.accounts.savings.interest.schedule;

import java.util.List;

import org.joda.time.LocalDate;

/**
 * When calculating interest for savings we care about certain scheduling certain events.
 * These include interest calculation and posting which occur on day and month time periods.
 *
 * The dates for these events are typically calculated from a base date like the start of the fiscal year.
 */
public interface InterestScheduledEvent {

    LocalDate nextMatchingDateFromAlreadyMatchingDate(LocalDate validMatchingDate);

    List<LocalDate> findAllMatchingDatesFromBaseDateUpToAndIncludingNearestMatchingEndDate(LocalDate baseDate, LocalDate cutOffDate);

    LocalDate findFirstDateOfPeriodForMatchingDate(LocalDate matchingDate);

    boolean isAMatchingDate(LocalDate baseDate, LocalDate date);

    LocalDate nextMatchingDateAfter(LocalDate baseDate, LocalDate after);

}
