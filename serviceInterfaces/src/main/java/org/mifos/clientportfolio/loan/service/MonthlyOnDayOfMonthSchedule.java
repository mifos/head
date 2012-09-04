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

package org.mifos.clientportfolio.loan.service;

public class MonthlyOnDayOfMonthSchedule implements RecurringSchedule {

    private final Integer recursEvery;
    private final Integer dayOfMonth;

    public MonthlyOnDayOfMonthSchedule(Integer recursEvery, Integer dayOfMonth) {
        this.recursEvery = recursEvery;
        this.dayOfMonth = dayOfMonth;
    }

    @Override
    public boolean isWeekly() {
        return false;
    }

    @Override
    public boolean isMonthly() {
        return true;
    }

    @Override
    public boolean isMonthlyOnDayOfMonth() {
        return true;
    }

    @Override
    public boolean isMonthlyOnWeekAndDayOfMonth() {
        return false;
    }

    @Override
    public Integer getEvery() {
        return this.recursEvery;
    }

    @Override
    public Integer getDay() {
        return this.dayOfMonth;
    }

    @Override
    public Integer getWeek() {
        return Integer.valueOf(-1);
    }

	@Override
	public boolean isDaily() {
		return false;
	}
}