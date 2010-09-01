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

package org.mifos.dto.screen;

import org.joda.time.DateTime;

public class LoanProductDetails {

    private final String name;
    private final String shortName;
    private final String description;
    private final Integer category;
    private final DateTime startDate;
    private final DateTime endDate;
    private final Integer applicableFor;
    private final boolean includeInLoanCycleCounter;
    private final boolean waiverInterest;
    private final Integer currencyId;

    @SuppressWarnings("PMD")
    public LoanProductDetails(String name, String shortName, String description, Integer category, DateTime startDate,
            DateTime endDate, Integer applicableFor, boolean includeInLoanCycleCounter, boolean waiverInterest,
            Integer currencyId) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.applicableFor = applicableFor;
        this.includeInLoanCycleCounter = includeInLoanCycleCounter;
        this.waiverInterest = waiverInterest;
        this.currencyId = currencyId;
    }

    public String getName() {
        return this.name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getCategory() {
        return this.category;
    }

    public DateTime getStartDate() {
        return this.startDate;
    }

    public DateTime getEndDate() {
        return this.endDate;
    }

    public Integer getApplicableFor() {
        return this.applicableFor;
    }

    public boolean isIncludeInLoanCycleCounter() {
        return this.includeInLoanCycleCounter;
    }

    public Integer getCurrencyId() {
        return this.currencyId;
    }

    public boolean shouldWaiverInterest() {
        return this.waiverInterest;
    }
}