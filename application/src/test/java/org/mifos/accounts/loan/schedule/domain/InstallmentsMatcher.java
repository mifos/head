/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.accounts.loan.schedule.domain;

import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.internal.matchers.TypeSafeMatcher;

import java.util.Map;

public class InstallmentsMatcher extends TypeSafeMatcher<Map<Integer, Installment>> {
    private Map<Integer, Installment> installments;

    public InstallmentsMatcher(Map<Integer, Installment> installments) {
        this.installments = installments;
    }

    @Override
    public boolean matchesSafely(Map<Integer, Installment> installments) {
        if (this.installments.keySet().size() == installments.keySet().size()) {
            for (Integer installmentId : installments.keySet()) {
                Assert.assertThat(installments.get(installmentId), new InstallmentMatcher(this.installments.get(installmentId)));
            }
            return true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Installments did not match");
    }
}
