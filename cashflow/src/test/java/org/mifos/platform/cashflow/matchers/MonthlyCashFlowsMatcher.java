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
package org.mifos.platform.cashflow.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.mifos.platform.cashflow.domain.MonthlyCashFlow;

import java.util.List;

public class MonthlyCashFlowsMatcher extends TypeSafeMatcher<List<MonthlyCashFlow>> {
    private List<MonthlyCashFlow> monthlyCashFlows;

    public MonthlyCashFlowsMatcher(List<MonthlyCashFlow> monthlyCashFlows) {
        this.monthlyCashFlows = monthlyCashFlows;
    }

    @Override
    public boolean matchesSafely(List<MonthlyCashFlow> monthlyCashFlows) {
        if (monthlyCashFlows.size() == this.monthlyCashFlows.size()) {
            for (MonthlyCashFlow monthlyCashFlowEntity : this.monthlyCashFlows) {
                Assert.assertThat(monthlyCashFlows, Matchers.hasItem(new MonthlyCashFlowMatcher(monthlyCashFlowEntity)));
            }
            return true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Cash flow entities did not match");
    }
}
