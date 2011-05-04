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

import org.joda.time.LocalDate;
import org.mifos.schedule.ScheduledEvent;

public class LoanDisbursementCoupledToCustomerMeetingScheduleStrategyImpl implements LoanDisbursementStrategy {

    private final ScheduledEvent scheduledEvent;
    private final LocalDate nextValidCustomerMeetingDate;

    public LoanDisbursementCoupledToCustomerMeetingScheduleStrategyImpl(ScheduledEvent scheduledEvent, LocalDate nextValidCustomerMeetingDate) {
        this.scheduledEvent = scheduledEvent;
        this.nextValidCustomerMeetingDate = nextValidCustomerMeetingDate;
    }

    @Override
    public LocalDate findClosestMatchingDateFromAndInclusiveOf(LocalDate fromAndInclusiveOf) {
        
        LocalDate closestMatch = nextValidCustomerMeetingDate;
        if (closestMatch.isBefore(fromAndInclusiveOf)) {
            closestMatch = new LocalDate(scheduledEvent.nextEventDateAfter(closestMatch.toDateMidnight().toDateTime()));
            while (closestMatch.isBefore(fromAndInclusiveOf)) {
                closestMatch = new LocalDate(scheduledEvent.nextEventDateAfter(closestMatch.toDateMidnight().toDateTime()));
            }
        }
        return closestMatch;
    }

    @Override
    public boolean isDisbursementDateValid(LocalDate disbursementDate) {
        
        boolean result = false;
        
        if (disbursementDate.isEqual(this.nextValidCustomerMeetingDate)) {
            result = true;
        } else if (disbursementDate.isAfter(this.nextValidCustomerMeetingDate)) {
            LocalDate matchingDate = this.nextValidCustomerMeetingDate;
            matchingDate = new LocalDate(scheduledEvent.nextEventDateAfter(matchingDate.toDateMidnight().toDateTime()));
            while(matchingDate.isBefore(disbursementDate)) {
                matchingDate = new LocalDate(scheduledEvent.nextEventDateAfter(matchingDate.toDateMidnight().toDateTime()));
            }
            
            if (matchingDate.isEqual(disbursementDate)) {
                result = true;
            }
        }
        
        return result;
    }
}