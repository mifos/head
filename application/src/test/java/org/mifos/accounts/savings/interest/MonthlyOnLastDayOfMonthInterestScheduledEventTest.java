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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.internal.MonthlyOnLastDayOfMonthInterestScheduledEvent;

public class MonthlyOnLastDayOfMonthInterestScheduledEventTest {

    private InterestScheduledEvent monthlyEvent;

    private LocalDate startOfFiscalYear = new LocalDate(new DateTime().withDate(2010, 1, 1));
    private LocalDate cutOffDate = new LocalDate(new DateTime().withDate(2010, 12, 31));

    private LocalDate jan1st = new LocalDate(new DateTime().withDate(2010, 1, 1));
    private LocalDate jun1st = new LocalDate(new DateTime().withDate(2010, 6, 1));

    private LocalDate jan31st = new LocalDate(new DateTime().withDate(2010, 1, 31));
    private LocalDate feb28th = new LocalDate(new DateTime().withDate(2010, 2, 28));
    private LocalDate mar31st = new LocalDate(new DateTime().withDate(2010, 3, 31));
    private LocalDate apr30 = new LocalDate(new DateTime().withDate(2010, 4, 30));
    private LocalDate may31st = new LocalDate(new DateTime().withDate(2010, 5, 31));
    private LocalDate jun30th = new LocalDate(new DateTime().withDate(2010, 6, 30));
    private LocalDate jul31st = new LocalDate(new DateTime().withDate(2010, 7, 31));
    private LocalDate aug31st = new LocalDate(new DateTime().withDate(2010, 8, 31));
    private LocalDate sep30th = new LocalDate(new DateTime().withDate(2010, 9, 30));
    private LocalDate oct31st = new LocalDate(new DateTime().withDate(2010, 10, 31));
    private LocalDate nov30th = new LocalDate(new DateTime().withDate(2010, 11, 30));
    private LocalDate dec31st = new LocalDate(new DateTime().withDate(2010, 12, 31));

    @Test
    public void shouldReturnDateWithLastDayOfMonthOneMonthFromJan() {

        // setup
        int every = 1;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        LocalDate nextValidMatchingDate = monthlyEvent.nextMatchingDateFromAlreadyMatchingDate(jan31st);

        assertThat(nextValidMatchingDate, is(feb28th));
    }

    @Test
    public void shouldReturnDateWithLastDayOfMonthTwoMonthFromJan() {

        // setup
        int every = 2;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        LocalDate nextValidMatchingDate = monthlyEvent.nextMatchingDateFromAlreadyMatchingDate(jan31st);

        assertThat(nextValidMatchingDate, is(mar31st));
    }

    @Test
    public void shouldReturnAllMatchingMonthlyDatesStartingFromFiscalStartDateUpToCutOffDate() {

        // setup
        int every = 1;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        List<LocalDate> nextValidMatchingDate = monthlyEvent.findAllMatchingDatesFromBaseDateUpToAndIncludingNearestMatchingEndDate(startOfFiscalYear, cutOffDate);

        assertThat(nextValidMatchingDate, hasItem(jan31st));
        assertThat(nextValidMatchingDate, hasItem(feb28th));
        assertThat(nextValidMatchingDate, hasItem(mar31st));
        assertThat(nextValidMatchingDate, hasItem(apr30));
        assertThat(nextValidMatchingDate, hasItem(may31st));
        assertThat(nextValidMatchingDate, hasItem(jun30th));
        assertThat(nextValidMatchingDate, hasItem(jul31st));
        assertThat(nextValidMatchingDate, hasItem(aug31st));
        assertThat(nextValidMatchingDate, hasItem(sep30th));
        assertThat(nextValidMatchingDate, hasItem(oct31st));
        assertThat(nextValidMatchingDate, hasItem(nov30th));
        assertThat(nextValidMatchingDate, hasItem(dec31st));
    }

    @Test
    public void shouldReturnAllMatchingQuarterlyDatesStartingFromFiscalStartDateUpToCutOffDate() {

        // setup
        int every = 3;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        List<LocalDate> nextValidMatchingDate = monthlyEvent.findAllMatchingDatesFromBaseDateUpToAndIncludingNearestMatchingEndDate(startOfFiscalYear, cutOffDate);

        assertThat(nextValidMatchingDate, hasItem(mar31st));
        assertThat(nextValidMatchingDate, hasItem(jun30th));
        assertThat(nextValidMatchingDate, hasItem(sep30th));
        assertThat(nextValidMatchingDate, hasItem(dec31st));
    }

    @Test
    public void shouldReturnFirstDateOfPeriodOfMatchingMonthlyDate() {

        // setup
        int every = 1;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        LocalDate nextValidMatchingDate = monthlyEvent.findFirstDateOfPeriodForMatchingDate(jun30th);

        assertThat(nextValidMatchingDate, is(jun1st));
    }

    @Test
    public void shouldReturnFirstDateOfPeriodOfMatchingBiMonthlyDate() {

        // setup
        int every = 2;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        LocalDate nextValidMatchingDate = monthlyEvent.findFirstDateOfPeriodForMatchingDate(feb28th);

        assertThat(nextValidMatchingDate, is(jan1st));
    }

    @Test
    public void shouldReturnFirstDateOfPeriodOfMatchingTriMonthlyDate() {

        // setup
        int every = 3;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        LocalDate nextValidMatchingDate = monthlyEvent.findFirstDateOfPeriodForMatchingDate(mar31st);

        assertThat(nextValidMatchingDate, is(jan1st));
    }

    @Test
    public void shouldReturnFalseForInvalidMatch() {

        // setup
        int every = 2;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        boolean isMatch = monthlyEvent.isAMatchingDate(jan1st, jan31st);

        assertFalse(isMatch);
    }

    @Test
    public void shouldReturnTrueForMatchingDateInSameMonth() {

        // setup
        int every = 1;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        boolean isMatch = monthlyEvent.isAMatchingDate(jan1st, jan31st);

        assertTrue(isMatch);
    }

    @Test
    public void shouldReturnTrueForAnyMatchingDate() {

        // setup
        int every = 2;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        boolean isMatch = monthlyEvent.isAMatchingDate(jan1st, apr30);

        assertTrue(isMatch);
    }

    @Test
    public void shouldFindNearestMatchingDateAfterALegalEndOfMonthDate() {

        // setup
        int every = 1;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        LocalDate nextMatchingDate = monthlyEvent.nextMatchingDateAfter(jan1st, jan31st);

        assertThat(nextMatchingDate, is(feb28th));
    }

    @Test
    public void shouldFindNearestMatchingDateAfterAnyGivenDate() {

        // setup
        int every = 1;
        monthlyEvent = new MonthlyOnLastDayOfMonthInterestScheduledEvent(every);

        // exercise test
        LocalDate nextMatchingDate = monthlyEvent.nextMatchingDateAfter(jan1st, jun1st);

        assertThat(nextMatchingDate, is(jun30th));
    }

}