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

package org.mifos.dto.domain;

import java.util.Date;

import org.joda.time.LocalDate;

public class HolidayDetails {

    private final String name;
    private final LocalDate fromDate;
    private LocalDate thruDate;
    private final Short repaymentRuleType;
    private final boolean holidayChangesApplied;
    private String repaymentRuleName;

    public HolidayDetails(String name, Date fromDate, Date thruDate, Short repaymentRuleType) {
        this.name = name;
        this.fromDate = new LocalDate(fromDate);
        if (thruDate != null) {
            this.thruDate = new LocalDate(thruDate);
        }
        this.repaymentRuleType = repaymentRuleType;
        this.holidayChangesApplied = false;
    }

    public HolidayDetails(String name, Date fromDate, Date thruDate, Short repaymentRuleType, boolean holidayChangesApplied) {
        this.name = name;
        this.fromDate = new LocalDate(fromDate);
        if (thruDate != null) {
            this.thruDate = new LocalDate(thruDate);
        }
        this.repaymentRuleType = repaymentRuleType;
        this.holidayChangesApplied = holidayChangesApplied;
    }

    public boolean isHolidayChangesApplied() {
        return this.holidayChangesApplied;
    }

    public String getName() {
        return name;
    }

    public LocalDate getFromDate() {
        return new LocalDate(fromDate);
    }

    public LocalDate getThruDate() {
        LocalDate returnedThruDate = null;
        if (thruDate == null) {
            returnedThruDate = new LocalDate(fromDate);
        } else {
            returnedThruDate = new LocalDate(thruDate);
        }
        return returnedThruDate;
    }

    public Short getRepaymentRuleType() {
        return repaymentRuleType;
    }

    public String getRepaymentRuleName() {
        return this.repaymentRuleName;
    }

    public void setRepaymentRuleName(String repaymentRuleName) {
        this.repaymentRuleName = repaymentRuleName;
    }
}
