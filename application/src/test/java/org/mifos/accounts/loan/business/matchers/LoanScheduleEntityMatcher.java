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
package org.mifos.accounts.loan.business.matchers;

import org.apache.commons.lang.ObjectUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.accounts.loan.business.LoanScheduleEntity;

import static org.junit.Assert.assertEquals;

public class LoanScheduleEntityMatcher extends TypeSafeMatcher<LoanScheduleEntity> {
    private static final double DELTA = .0001;
    private LoanScheduleEntity loanScheduleEntity;

    public LoanScheduleEntityMatcher(LoanScheduleEntity loanScheduleEntity) {
        this.loanScheduleEntity = loanScheduleEntity;
    }

    @Override
    public boolean matchesSafely(LoanScheduleEntity loanScheduleEntity) {
        assertEquals(this.loanScheduleEntity.getPrincipalAsDouble(), loanScheduleEntity.getPrincipalAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getPrincipalPaidAsDouble(), loanScheduleEntity.getPrincipalPaidAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getPrincipalDueAsDouble(), loanScheduleEntity.getPrincipalDueAsDouble(), DELTA);

        assertEquals(this.loanScheduleEntity.getInterestAsDouble(), loanScheduleEntity.getInterestAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getInterestPaidAsDouble(), loanScheduleEntity.getInterestPaidAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getInterestDueAsDouble(), loanScheduleEntity.getInterestDueAsDouble(), DELTA);

        assertEquals(this.loanScheduleEntity.getTotalFeesAsDouble(), loanScheduleEntity.getTotalFeesAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getTotalFeesPaidAsDouble(), loanScheduleEntity.getTotalFeesPaidAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getTotalFeesDueAsDouble(), loanScheduleEntity.getTotalFeesDueAsDouble(), DELTA);

        assertEquals(this.loanScheduleEntity.getMiscFeeAsDouble(), loanScheduleEntity.getMiscFeeAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getMiscFeePaidAsDouble(), loanScheduleEntity.getMiscFeePaidAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getMiscFeesDueAsDouble(), loanScheduleEntity.getMiscFeesDueAsDouble(), DELTA);

        assertEquals(this.loanScheduleEntity.getPenaltyAsDouble(), loanScheduleEntity.getPenaltyAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getPenaltyPaidAsDouble(), loanScheduleEntity.getPenaltyPaidAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getPenaltyDueAsDouble(), loanScheduleEntity.getPenaltyDueAsDouble(), DELTA);

        assertEquals(this.loanScheduleEntity.getMiscPenaltyAsDouble(), loanScheduleEntity.getMiscPenaltyAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getMiscPenaltyPaidAsDouble(), loanScheduleEntity.getMiscPenaltyPaidAsDouble(), DELTA);
        assertEquals(this.loanScheduleEntity.getMiscPenaltyDueAsDouble(), loanScheduleEntity.getMiscPenaltyDueAsDouble(), DELTA);

        return
                ObjectUtils.equals(this.loanScheduleEntity.getPaymentDate(), loanScheduleEntity.getPaymentDate())
                && this.loanScheduleEntity.getPaymentStatus().equals(loanScheduleEntity.getPaymentStatus());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("LoanScheduleEntity did not match");
    }
}
