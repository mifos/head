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

package org.mifos.dto.screen;

import static org.apache.commons.lang.StringUtils.isNotBlank;

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

    public LoanAccountMeetingDto(String recurrenceId, String weekDay, String recurWeek,
                                 String monthType, String monthDay, String dayRecurMonth,
                                 String monthWeek, String recurMonth, String monthRank) {
        this.monthType = monthType;
        if (isNotBlank(recurrenceId)) {
            this.recurrenceId = Short.valueOf(recurrenceId);
        }
        if (isNotBlank(weekDay)) {
            this.weekDay = Short.valueOf(weekDay);
        }
        if (isNotBlank(recurWeek)) {
            this.everyWeek = Short.valueOf(recurWeek);
        }
        if (isNotBlank(monthDay)) {
            this.dayOfMonth = Short.valueOf(monthDay);
        }
        if (isNotBlank(dayRecurMonth)) {
            this.dayRecurMonth = Short.valueOf(dayRecurMonth);
        }
        if (isNotBlank(monthWeek)) {
            this.weekOfMonth = Short.valueOf(monthWeek);
        }
        if (isNotBlank(recurMonth)) {
            this.everyMonth = Short.valueOf(recurMonth);
        }
        if (isNotBlank(monthRank)) {
            this.monthRank = Short.valueOf(monthRank);
        }
    }

    private LoanAccountMeetingDto() {
    }

    public Short getRecurrenceId() {
        return this.recurrenceId;
    }

    public Short getWeekDay() {
        return this.weekDay;
    }


    public Short getEveryWeek() {
        return this.everyWeek;
    }


    public String getMonthType() {
        return this.monthType;
    }

    public Short getDayOfMonth() {
        return this.dayOfMonth;
    }


    public Short getDayRecurMonth() {
        return this.dayRecurMonth;
    }

    public Short getWeekOfMonth() {
        return this.weekOfMonth;
    }

    public Short getEveryMonth() {
        return this.everyMonth;
    }

    public Short getMonthRank() {
        return this.monthRank;
    }

    @SuppressWarnings("PMD")
    public static class Builder {
        private final LoanAccountMeetingDto instance;

        public Builder() {
            instance = new LoanAccountMeetingDto();
        }

        public Builder weekly() {
            instance.recurrenceId = 1;
            return this;
        }

        public Builder on(int day) {
            instance.weekDay = (short)day;
            return this;
        }

        public Builder recurring(int everyWeek) {
            instance.everyWeek = (short)everyWeek;
            return this;
        }

        public LoanAccountMeetingDto build() {
            return instance;
        }
    }
}