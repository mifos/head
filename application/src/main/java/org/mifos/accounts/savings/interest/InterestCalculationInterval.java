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

import org.joda.time.Interval;
import org.joda.time.LocalDate;

public class InterestCalculationInterval {

    private final Interval interval;

    public InterestCalculationInterval(LocalDate startDate, LocalDate endDate) {
        this.interval = new Interval(startDate.toDateTimeAtStartOfDay(), endDate.toDateTimeAtStartOfDay());
    }

    public InterestCalculationInterval(Interval interval) {
        this.interval = interval;
    }

    public LocalDate getStartDate() {
        return interval.getStart().toLocalDate();
    }

    public LocalDate getEndDate() {
        return interval.getEnd().toLocalDate();
    }

    public Interval getInterval() {
        return interval;
    }

    public boolean dateFallsWithin(LocalDate date) {
        return interval.contains(date.toDateTimeAtStartOfDay()) || interval.getEnd().isEqual(date.toDateTimeAtStartOfDay());
    }

    @Override
    public String toString() {
        return new StringBuilder().append('[').append(interval.getStart().toLocalDate()).append(" - ")
                .append(interval.getEnd().toLocalDate()).append(']').toString();
    }
}