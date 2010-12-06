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
import org.junit.internal.matchers.TypeSafeMatcher;
import org.mifos.accounts.loan.schedule.domain.Installment;

import static org.junit.Assert.assertEquals;

public class InstallmentMatcher extends TypeSafeMatcher<Installment> {
    private Installment installment;
    private static final double DELTA = .0001;

    public InstallmentMatcher(Installment installment) {
        this.installment = installment;
    }

    @Override
    public boolean matchesSafely(Installment installment) {
        assertEquals(this.installment.getPrincipal().doubleValue(), installment.getPrincipal().doubleValue(), DELTA);
        assertEquals(this.installment.getPrincipalPaid().doubleValue(), installment.getPrincipalPaid().doubleValue(), DELTA);
        assertEquals(this.installment.getPrincipalDue().doubleValue(), installment.getPrincipalDue().doubleValue(), DELTA);

        assertEquals(this.installment.getInterest().doubleValue(), installment.getInterest().doubleValue(), DELTA);
        assertEquals(this.installment.getInterestPaid().doubleValue(), installment.getInterestPaid().doubleValue(), DELTA);
        assertEquals(this.installment.getInterestDue().doubleValue(), installment.getInterestDue().doubleValue(), DELTA);

        assertEquals(this.installment.getExtraInterest().doubleValue(), installment.getExtraInterest().doubleValue(), DELTA);
        assertEquals(this.installment.getExtraInterestPaid().doubleValue(), installment.getExtraInterestPaid().doubleValue(), DELTA);
        assertEquals(this.installment.getExtraInterestDue().doubleValue(), installment.getExtraInterestDue().doubleValue(), DELTA);

        assertEquals(this.installment.getFees().doubleValue(), installment.getFees().doubleValue(), DELTA);
        assertEquals(this.installment.getFeesPaid().doubleValue(), installment.getFeesPaid().doubleValue(), DELTA);
        assertEquals(this.installment.getFeesDue().doubleValue(), installment.getFeesDue().doubleValue(), DELTA);

        assertEquals(this.installment.getMiscFees().doubleValue(), installment.getMiscFees().doubleValue(), DELTA);
        assertEquals(this.installment.getMiscFeesPaid().doubleValue(), installment.getMiscFeesPaid().doubleValue(), DELTA);
        assertEquals(this.installment.getMiscFeesDue().doubleValue(), installment.getMiscFeesDue().doubleValue(), DELTA);

        assertEquals(this.installment.getPenalty().doubleValue(), installment.getPenalty().doubleValue(), DELTA);
        assertEquals(this.installment.getPenaltyPaid().doubleValue(), installment.getPenaltyPaid().doubleValue(), DELTA);
        assertEquals(this.installment.getPenaltyDue().doubleValue(), installment.getPenaltyDue().doubleValue(), DELTA);

        assertEquals(this.installment.getMiscPenalty().doubleValue(), installment.getMiscPenalty().doubleValue(), DELTA);
        assertEquals(this.installment.getMiscPenaltyPaid().doubleValue(), installment.getMiscPenaltyPaid().doubleValue(), DELTA);
        assertEquals(this.installment.getMiscPenaltyDue().doubleValue(), installment.getMiscPenaltyDue().doubleValue(), DELTA);

        return this.installment.getDueDate().equals(installment.getDueDate());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Installment did not match");
    }
}
