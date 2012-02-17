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

package org.mifos.test.acceptance.framework.admin;

public class PenaltyFormParameters {
    public static final String LIST_SELECT = "--Select--";
    public static final String APPLIES_LOANS = "Loans";
    public static final String APPLIES_SAVINGS = "Savings";

    public static final String PERIOD_INSTALLMENTS = "Number Of Installments";
    public static final String PERIOD_DAYS = "Number Of Days";
    public static final String PERIOD_NONE = "None";

    public static final String FORMULA_OUTSTANDING_PRINCIPAL = "Outstanding Principal Amount";
    public static final String FORMULA_OUTSTANDING_LOAN = "Outstanding Loan Amount";
    public static final String FORMULA_OVERDUE_AMOUNT = "Overdue Amount Due";
    public static final String FORMULA_OVERDUE_PRINCIPAL = "Overdue Principal";

    public static final String FREQUENCY_NONE = "None";
    public static final String FREQUENCY_DAILY = "Daily";
    public static final String FREQUENCY_WEEKLY = "Weekly";
    public static final String FREQUENCY_MONTHLY = "Monthly";

    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "InActive";

    private String name = "";
    private String applies = LIST_SELECT;
    private String period = LIST_SELECT;
    private String duration = "";
    private String min = "";
    private String max = "";
    private String amount = "";
    private String rate = "";
    private String formula = LIST_SELECT;
    private String frequency = FREQUENCY_NONE;
    private String glCode = LIST_SELECT;
    private String status = STATUS_ACTIVE;
    
    public void setToDefault() {
        name = "";
        applies = LIST_SELECT;
        period = LIST_SELECT;
        duration = "";
        min = "";
        max = "";
        amount = "";
        rate = "";
        formula = LIST_SELECT;
        frequency = FREQUENCY_NONE;
        glCode = LIST_SELECT;
        status = STATUS_ACTIVE;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getApplies() {
        return applies;
    }

    public void setApplies(final String applies) {
        this.applies = applies;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(final String period) {
        this.period = period;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }

    public String getMin() {
        return min;
    }

    public void setMin(final String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(final String max) {
        this.max = max;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(final String rate) {
        this.rate = rate;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(final String formula) {
        this.formula = formula;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(final String frequency) {
        this.frequency = frequency;
    }

    public String getGlCode() {
        return glCode;
    }

    public void setGlCode(final String glCode) {
        this.glCode = glCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

}