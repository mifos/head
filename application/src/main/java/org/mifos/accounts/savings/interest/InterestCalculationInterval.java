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

import org.joda.time.LocalDate;

public class InterestCalculationInterval {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public InterestCalculationInterval(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * checks that <code>date</code> occurs within the {@link InterestCalculationInterval} with startDate and endDate inclusive.
     */
    public boolean contains(LocalDate date) {
        return (date.isEqual(this.startDate) || date.isAfter(this.startDate)) && (date.isEqual(this.endDate) || date.isBefore(endDate));
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    @Override
    public String toString() {
        return new StringBuilder().append('[').append(this.startDate).append(" - ")
                .append(this.endDate).append(']').toString();
    }
}