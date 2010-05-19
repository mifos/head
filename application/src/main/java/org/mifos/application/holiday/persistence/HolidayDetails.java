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
package org.mifos.application.holiday.persistence;

import java.util.Date;

import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.util.helpers.DateUtils;

public class HolidayDetails {
    private String name;
    private Date fromDate;
    private Date thruDate;
    private RepaymentRuleTypes repaymentRuleType;
    private boolean disableValidation;
    private YesNoFlag holidayChangesAppliedFlag;

    public HolidayDetails(String name, Date fromDate, Date thruDate, RepaymentRuleTypes repaymentRuleType) {
        this.name = name;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
        this.repaymentRuleType = repaymentRuleType;
        this.holidayChangesAppliedFlag = YesNoFlag.NO;
    }

    public HolidayDetails(String name, Date fromDate, Date thruDate, RepaymentRuleTypes repaymentRuleType,
            YesNoFlag holidayChangesAppliedFlag) {
        this.name = name;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
        this.repaymentRuleType = repaymentRuleType;
        this.holidayChangesAppliedFlag = holidayChangesAppliedFlag;
    }

    public YesNoFlag getHolidayChangesAppliedFlag() {
        return this.holidayChangesAppliedFlag;
    }

    public String getName() {
        return name;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getThruDate() {
        if (thruDate == null) {
            return fromDate;
        }
        return thruDate;
    }

    public RepaymentRuleTypes getRepaymentRuleType() {
        return repaymentRuleType;
    }

    public String getRepaymentRuleName() {
        return repaymentRuleType.getName();
    }
    // TODO:this is only to support integration testing. Remove this
    public void disableValidation(boolean disableValidation) {
        this.disableValidation = disableValidation;
    }

    public void validate() throws ValidationException {
        if (!disableValidation) {
            Date fromDate = DateUtils.getDateWithoutTimeStamp(getFromDate());
            Date thruDate = DateUtils.getDateWithoutTimeStamp(getThruDate());
            // Today is not the system date. Today is obtained from DateTimeService which adds an offset to system date.
            // This offset allows test data to be have dates in the past.
            Date today = DateUtils.getCurrentDateWithoutTimeStamp();
            if (fromDate.compareTo(today) <= 0) {
                throw new ValidationException(
                        "Holiday cannot start from today or a date in the past. Start date specified is " + fromDate);
            }
            if (thruDate.compareTo(fromDate) < 0) {
                throw new ValidationException("Holiday cannot end before start date. Start date specified is "
                        + fromDate + " and end date specified is " + thruDate);
            }
        }
    }
}
