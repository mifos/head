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

package org.mifos.test.acceptance.framework.loan;

public class CreateLoanAccountSubmitParameters {
    private String amount;
    private String lsimFrequencyWeeks;
    private String lsimFrequencyMonths;
    private String lsimWeekFrequency;
    private String lsimWeekDay;
    private String lsimDayOfMonth;
    private String lsimMonthTypeDayOfMonth;
    private String lsimMonthTypeNthWeekdayOfMonth;
    private String lsimMonthRank;
    private boolean gracePeriodTypeNone;


    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
    public String getLsimFrequencyWeeks() {
        return this.lsimFrequencyWeeks;
    }

    public void setLsimFrequencyWeeks(String lsimFrequencyWeeks) {
        this.lsimFrequencyWeeks = lsimFrequencyWeeks;
    }

    public String getLsimFrequencyMonths() {
        return this.lsimFrequencyMonths;
    }

    public void setLsimFrequencyMonths(String lsimFrequencyMonths) {
        this.lsimFrequencyMonths = lsimFrequencyMonths;
    }

    public String getLsimWeekFrequency() {
        return this.lsimWeekFrequency;
    }

    public void setLsimWeekFrequency(String lsimWeekFrequency) {
        this.lsimWeekFrequency = lsimWeekFrequency;
    }

    public String getLsimWeekDay() {
        return this.lsimWeekDay;
    }

    public void setLsimWeekDay(String lsimWeekDay) {
        this.lsimWeekDay = lsimWeekDay;
    }

    public String getLsimDayOfMonth() {
        return this.lsimDayOfMonth;
    }

    public void setLsimDayOfMonth(String lsimDayOfMonth) {
        this.lsimDayOfMonth = lsimDayOfMonth;
    }

    public String getLsimMonthTypeDayOfMonth() {
        return this.lsimMonthTypeDayOfMonth;
    }

    public void setLsimMonthTypeDayOfMonth(String lsimMonthTypeDayOfMonth) {
        this.lsimMonthTypeDayOfMonth = lsimMonthTypeDayOfMonth;
    }

    public String getLsimMonthTypeNthWeekdayOfMonth() {
        return this.lsimMonthTypeNthWeekdayOfMonth;
    }

    public void setLsimMonthTypeNthWeekdayOfMonth(String lsimMonthTypeNthWeekdayOfMonth) {
        this.lsimMonthTypeNthWeekdayOfMonth = lsimMonthTypeNthWeekdayOfMonth;
    }

    public String getLsimMonthRank() {
        return this.lsimMonthRank;
    }

    public void setLsimMonthRank(String lsimMonthRank) {
        this.lsimMonthRank = lsimMonthRank;
    }

    public boolean isGracePeriodTypeNone() {
        return gracePeriodTypeNone;
    }

    public void setGracePeriodTypeNone(boolean gracePeriodTypeNone) {
        this.gracePeriodTypeNone = gracePeriodTypeNone;
    }
}
