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
package org.mifos.platform.cashflow.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.mifos.platform.cashflow.domain.CashFlow;

import static org.hamcrest.CoreMatchers.is;

public class CashFlowMatcher extends TypeSafeMatcher<CashFlow> {
    private CashFlow cashFlow;

    public CashFlowMatcher(CashFlow cashFlow) {
        this.cashFlow = cashFlow;
    }

    @Override
    public boolean matchesSafely(CashFlow cashFlowEntity) {
        Assert.assertEquals(this.cashFlow.getId(), cashFlowEntity.getId());
        Assert.assertThat(this.cashFlow.getMonthlyCashFlows(), new MonthlyCashFlowsMatcher(cashFlowEntity.getMonthlyCashFlows()));
        Assert.assertThat(this.cashFlow.getTotalCapital().doubleValue(), is(cashFlowEntity.getTotalCapital().doubleValue()));
        Assert.assertThat(this.cashFlow.getTotalLiability().doubleValue(), is(cashFlowEntity.getTotalLiability().doubleValue()));
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Cash flow entities did not match");
    }
}
