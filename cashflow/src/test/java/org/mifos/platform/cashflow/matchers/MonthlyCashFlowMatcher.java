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
package org.mifos.platform.cashflow.matchers;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.platform.cashflow.domain.MonthlyCashFlow;

public class MonthlyCashFlowMatcher extends TypeSafeMatcher<MonthlyCashFlow> {
    private MonthlyCashFlow monthlyCashFlow;

    public MonthlyCashFlowMatcher(MonthlyCashFlow monthlyCashFlow) {
        this.monthlyCashFlow = monthlyCashFlow;
    }

    @Override
    public boolean matchesSafely(MonthlyCashFlow monthlyCashFlow) {
        return (sameMonthYear(monthlyCashFlow)
                && monthlyCashFlow.getRevenue().equals(this.monthlyCashFlow.getRevenue()))
                && (monthlyCashFlow.getExpenses().equals(this.monthlyCashFlow.getExpenses()))
                && (StringUtils.equals(monthlyCashFlow.getNotes(), this.monthlyCashFlow.getNotes())
                && sameId(monthlyCashFlow));
    }

    private boolean sameId(MonthlyCashFlow monthlyCashFlow) {
        return ((null == monthlyCashFlow.getId() && null == this.monthlyCashFlow.getId()) ||
                monthlyCashFlow.getId().equals(this.monthlyCashFlow.getId()));
    }

    private boolean sameMonthYear(MonthlyCashFlow monthlyCashFlow) {
        return (monthlyCashFlow.getMonthYear().getMonthOfYear() == this.monthlyCashFlow.getMonthYear().getMonthOfYear())
                && (monthlyCashFlow.getMonthYear().getYear() == this.monthlyCashFlow.getMonthYear().getYear());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Monthly cash flow entity did not match");
    }
}
