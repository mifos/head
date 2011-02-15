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
import org.mifos.platform.cashflow.ui.model.MonthlyCashFlowForm;

public class MonthlyCashFlowFormMatcher extends TypeSafeMatcher<MonthlyCashFlowForm> {
    private MonthlyCashFlowForm monthlyCashFlowForm;
    private MonthlyCashFlowForm actual;

    public MonthlyCashFlowFormMatcher(MonthlyCashFlowForm monthlyCashFlowForm) {
        this.monthlyCashFlowForm = monthlyCashFlowForm;
    }

    @Override
    public boolean matchesSafely(MonthlyCashFlowForm monthlyCashFlowForm) {
        actual = monthlyCashFlowForm;
        return StringUtils.equals(monthlyCashFlowForm.getMonth(), this.monthlyCashFlowForm.getMonth())
                && StringUtils.equals(monthlyCashFlowForm.getNotes(), this.monthlyCashFlowForm.getNotes())
                && (monthlyCashFlowForm.getYear() == this.monthlyCashFlowForm.getYear())
                && sameRevenue(monthlyCashFlowForm)
                && sameExpense(monthlyCashFlowForm);
    }

    private boolean sameExpense(MonthlyCashFlowForm monthlyCashFlowForm) {
        return (monthlyCashFlowForm.getExpense() == null && this.monthlyCashFlowForm.getExpense() == null)
                || monthlyCashFlowForm.getExpense().equals(this.monthlyCashFlowForm.getExpense());
    }

    private boolean sameRevenue(MonthlyCashFlowForm monthlyCashFlowForm) {
        return (monthlyCashFlowForm.getRevenue() == null && this.monthlyCashFlowForm.getRevenue() == null)
                || monthlyCashFlowForm.getRevenue().equals(this.monthlyCashFlowForm.getRevenue());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(
                "Monthly cash flow form did not match. Actual value is \n" + actual + "\n" +
                        "Expected value is \n" + monthlyCashFlowForm
        );
    }
}
