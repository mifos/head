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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.internal.DailyInterestScheduledEvent;

public class DailyInterestScheduledEventTest {

    private InterestScheduledEvent dailyEvent;

    private LocalDate jan1st = new LocalDate(new DateTime().withDate(2010, 1, 1));
    private LocalDate jan2nd = new LocalDate(new DateTime().withDate(2010, 1, 2));
    private LocalDate jan3rd = new LocalDate(new DateTime().withDate(2010, 1, 3));
    private LocalDate jan4th = new LocalDate(new DateTime().withDate(2010, 1, 4));
    private LocalDate dec31st = new LocalDate(new DateTime().withDate(2010, 12, 31));
    private LocalDate startOfFiscalYear = new LocalDate(new DateTime().withDate(2010, 1, 1));
    private LocalDate cutOffDate = new LocalDate(new DateTime().withDate(2010, 12, 31));

    @Test
    public void shouldReturnDateOneDayAheadOfJan1st() {

        // setup
        int every = 1;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        LocalDate nextValidMatchingDate = dailyEvent.nextMatchingDateFromAlreadyMatchingDate(jan1st);

        assertThat(nextValidMatchingDate, is(jan2nd));
    }

    @Test
    public void shouldReturnDateTwoDaysAheadOfJan1st() {

        // setup
        int every = 2;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        LocalDate nextValidMatchingDate = dailyEvent.nextMatchingDateFromAlreadyMatchingDate(jan1st);

        assertThat(nextValidMatchingDate, is(jan3rd));
    }

    @Test
    public void shouldReturnAllMatchingDailyDatesStartingFromFiscalStartDateUpToCutOffDate() {

        // setup
        int every = 1;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        List<LocalDate> nextValidMatchingDate = dailyEvent.findAllMatchingDatesFromBaseDateToCutOffDate(startOfFiscalYear, cutOffDate);

        assertThat(nextValidMatchingDate, hasItem(jan1st));
        assertThat(nextValidMatchingDate, hasItem(jan2nd));
        assertThat(nextValidMatchingDate, hasItem(jan3rd));
        assertThat(nextValidMatchingDate, hasItem(dec31st));
    }

    @Test
    public void shouldReturnAllMatchingBiDailyDatesStartingFromFiscalStartDateUpToCutOffDate() {

        // setup
        int every = 2;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        List<LocalDate> nextValidMatchingDate = dailyEvent.findAllMatchingDatesFromBaseDateToCutOffDate(startOfFiscalYear, cutOffDate);

        assertThat(nextValidMatchingDate, hasItem(jan2nd));
        assertThat(nextValidMatchingDate, hasItem(jan4th));
        assertThat(nextValidMatchingDate, not(hasItem(jan1st)));
        assertThat(nextValidMatchingDate, not(hasItem(jan3rd)));
    }

    @Test
    public void shouldReturnFirstDateOfPeriodOfMatchingDailyDate() {

        // setup
        int every = 1;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        LocalDate nextValidMatchingDate = dailyEvent.findFirstDateOfPeriodForMatchingDate(jan1st);

        assertThat(nextValidMatchingDate, is(jan1st));
    }

    @Test
    public void shouldReturnFirstDateOfPeriodOfMatchingBiDailyDate() {

        // setup
        int every = 2;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        LocalDate nextValidMatchingDate = dailyEvent.findFirstDateOfPeriodForMatchingDate(jan2nd);

        assertThat(nextValidMatchingDate, is(jan1st));
    }

    @Test
    public void shouldReturnFirstDateOfPeriodOfMatchingTriDailyDate() {

        // setup
        int every = 3;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        LocalDate nextValidMatchingDate = dailyEvent.findFirstDateOfPeriodForMatchingDate(jan3rd);

        assertThat(nextValidMatchingDate, is(jan1st));
    }

    @Test
    public void shouldReturnFalseForInvalidMatch() {

        // setup
        int every = 2;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        boolean isMatch = dailyEvent.isAMatchingDate(jan1st, jan1st);

        assertFalse(isMatch);
    }

    @Test
    public void shouldReturnTrueForMatchingDate() {

        // setup
        int every = 1;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        boolean isMatch = dailyEvent.isAMatchingDate(jan1st, jan2nd);

        assertTrue(isMatch);
    }

    @Test
    public void shouldReturnTrueForAnyMatchingDate() {

        // setup
        int every = 2;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        boolean isMatch = dailyEvent.isAMatchingDate(jan1st, jan2nd);

        assertTrue(isMatch);
    }

    @Test
    public void shouldFindNearestMatchingDateAfterALegalDailyDate() {

        // setup
        int every = 1;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        LocalDate nextMatchingDate = dailyEvent.nextMatchingDateAfter(jan1st, jan2nd);

        assertThat(nextMatchingDate, is(jan3rd));
    }

    @Test
    public void shouldFindNearestMatchingDateAfterAnyGivenDate() {

        // setup
        int every = 1;
        dailyEvent = new DailyInterestScheduledEvent(every);

        // exercise test
        LocalDate nextMatchingDate = dailyEvent.nextMatchingDateAfter(jan1st, jan3rd);

        assertThat(nextMatchingDate, is(jan4th));
    }
}