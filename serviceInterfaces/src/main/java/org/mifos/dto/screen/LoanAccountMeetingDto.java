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

public class LoanAccountMeetingDto {

    private Short recurrenceId;
    private Short weekDay;
    private Short everyWeek;

    private String monthType;
    private Short dayOfMonth;
    private Short dayRecurMonth;

    private Short weekOfMonth;
    private Short everyMonth;
    private Short monthRank;

    public Short getRecurrenceId() {
        return this.recurrenceId;
    }

    public void setRecurrenceId(Short recurrenceId) {
        this.recurrenceId = recurrenceId;
    }

    public Short getWeekDay() {
        return this.weekDay;
    }

    public void setWeekDay(Short weekDay) {
        this.weekDay = weekDay;
    }

    public Short getEveryWeek() {
        return this.everyWeek;
    }

    public void setEveryWeek(Short everyWeek) {
        this.everyWeek = everyWeek;
    }

    public String getMonthType() {
        return this.monthType;
    }

    public void setMonthType(String monthType) {
        this.monthType = monthType;
    }

    public Short getDayOfMonth() {
        return this.dayOfMonth;
    }

    public void setDayOfMonth(Short dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public Short getDayRecurMonth() {
        return this.dayRecurMonth;
    }

    public void setDayRecurMonth(Short dayRecurMonth) {
        this.dayRecurMonth = dayRecurMonth;
    }

    public Short getWeekOfMonth() {
        return this.weekOfMonth;
    }

    public void setWeekOfMonth(Short weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }

    public Short getEveryMonth() {
        return this.everyMonth;
    }

    public void setEveryMonth(Short everyMonth) {
        this.everyMonth = everyMonth;
    }

    public Short getMonthRank() {
        return this.monthRank;
    }

    public void setMonthRank(Short monthRank) {
        this.monthRank = monthRank;
    }
}