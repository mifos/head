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

package org.mifos.ui.core.controller;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("PMD")
public class HolidayFormBean {

    private String name;
    private Integer fromDay;
    private Integer fromMonth;
    private String fromYear;
    private Integer toDay;
    private Integer toMonth;
    private String toYear;
    private String repaymentRuleId;
    private String selectedOfficeIds;

    private Map<String, String> repaymentRuleOptions = new LinkedHashMap<String, String>();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFromDay() {
        return this.fromDay;
    }

    public void setFromDay(Integer fromDay) {
        this.fromDay = fromDay;
    }

    public Integer getFromMonth() {
        return this.fromMonth;
    }

    public void setFromMonth(Integer fromMonth) {
        this.fromMonth = fromMonth;
    }

    public String getFromYear() {
        return this.fromYear;
    }

    public void setFromYear(String fromYear) {
        this.fromYear = fromYear;
    }

    public Integer getToDay() {
        return this.toDay;
    }

    public void setToDay(Integer toDay) {
        this.toDay = toDay;
    }

    public Integer getToMonth() {
        return this.toMonth;
    }

    public void setToMonth(Integer toMonth) {
        this.toMonth = toMonth;
    }

    public String getToYear() {
        return this.toYear;
    }

    public void setToYear(String toYear) {
        this.toYear = toYear;
    }

    public boolean allToDateFieldsFilled() {
        if (getToDay() == null) {
            return false;
        }
        if (getToMonth() == null) {
            return false;
        }
        if (getToYear() == null || getToYear().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean anyToDateFieldFilled() {
        if (getToDay() != null) {
            return true;
        }
        if (getToMonth() != null) {
            return true;
        }
        if (getToYear() != null && !getToYear().isEmpty()) {
            return true;
        }
        return false;
    }

    public String getRepaymentRuleId() {
        return this.repaymentRuleId;
    }

    public void setRepaymentRuleId(String repaymentRuleId) {
        this.repaymentRuleId = repaymentRuleId;
    }

    public String getSelectedOfficeIds() {
        return this.selectedOfficeIds;
    }

    public void setSelectedOfficeIds(String selectedOfficeIds) {
        this.selectedOfficeIds = selectedOfficeIds;
    }

    public Map<String, String> getRepaymentRuleOptions() {
        this.repaymentRuleOptions = new LinkedHashMap<String, String>();
        this.repaymentRuleOptions.put("1", "sameDay");
        this.repaymentRuleOptions.put("2", "nextMeeting/Repayment");
        this.repaymentRuleOptions.put("3", "nextWorkingDay");
        this.repaymentRuleOptions.put("4", "repaymentMoratorium");
        return this.repaymentRuleOptions;
    }

    public void setRepaymentRuleOptions(Map<String, String> repaymentRuleOptions) {
        this.repaymentRuleOptions = repaymentRuleOptions;
    }
}