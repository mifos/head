/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.clientportfolio.newloan.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.schedule.ScheduledEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoanDisbursementCoupledToCustomerMeetingScheduleStrategyImplTest {

    // class under test
    private LoanDisbursementStrategy loanDisbursementStrategy;
    
    @Mock private ScheduledEvent scheduledEvent;
    
    @Before
    public void setup() {
        LocalDate nextValidCustomerMeetingDate = new LocalDate();
        loanDisbursementStrategy = new LoanDisbursementCoupledToCustomerMeetingScheduleStrategyImpl(scheduledEvent, nextValidCustomerMeetingDate);
    }
    
    @Test
    public void shouldFindNextMatchingDateInclusiveOfDateProvided() {
        
        // setup
        LocalDate fromAndInclusiveOf = new LocalDate();
        DateTime expectedDateTimeToStartSearchFrom = fromAndInclusiveOf.minusDays(1).toDateMidnight().toDateTime();
        DateTime nextMatchingDate = new DateTime();
        
        // stubbing
        when(scheduledEvent.nextEventDateAfter(expectedDateTimeToStartSearchFrom)).thenReturn(nextMatchingDate);
        
        // exercise test
        LocalDate result = loanDisbursementStrategy.findClosestMatchingDateFromAndInclusiveOf(fromAndInclusiveOf);
        
        // verification
        assertThat(result, is(nextMatchingDate.toLocalDate()));
    }
    
    @Test
    public void shouldDetermineDateAsValidForScheduleWhenReturnsSameDate() {
        
        // setup
        LocalDate disbursementDate = new LocalDate();
        DateTime disbursementDateAsDateTime = disbursementDate.toDateMidnight().toDateTime();
        
        // stubbing
        when(scheduledEvent.nearestMatchingDateBeginningAt(disbursementDateAsDateTime)).thenReturn(disbursementDateAsDateTime);
        
        // exercise test
        boolean result = loanDisbursementStrategy.isDisbursementDateValid(disbursementDate);
        
        // verification
        assertTrue("given nearest matching valid date is same as date provided, then dibursement date should be valid.", result);
    }
}