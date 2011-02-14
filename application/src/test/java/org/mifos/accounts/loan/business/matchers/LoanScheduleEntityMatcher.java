/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static junit.framework.Assert.assertEquals;

import org.apache.commons.lang.ObjectUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.accounts.loan.business.LoanScheduleEntity;

public class LoanScheduleEntityMatcher extends TypeSafeMatcher<LoanScheduleEntity> {
    private LoanScheduleEntity loanScheduleEntity;
    public static final double DELTA = .0001;

    public LoanScheduleEntityMatcher(LoanScheduleEntity loanScheduleEntity) {
        this.loanScheduleEntity = loanScheduleEntity;
    }

    @Override
    public boolean matchesSafely(LoanScheduleEntity loanScheduleEntity) {
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getPrincipalAsDouble(), loanScheduleEntity.getPrincipalAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getPrincipalPaidAsDouble(), loanScheduleEntity.getPrincipalPaidAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getPrincipalDueAsDouble(), loanScheduleEntity.getPrincipalDueAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getInterestAsDouble(), loanScheduleEntity.getInterestAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getInterestPaidAsDouble(), loanScheduleEntity.getInterestPaidAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getInterestDueAsDouble(), loanScheduleEntity.getInterestDueAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getTotalFeesAsDouble(), loanScheduleEntity.getTotalFeesAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getTotalFeesPaidAsDouble(), loanScheduleEntity.getTotalFeesPaidAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getTotalFeesDueAsDouble(), loanScheduleEntity.getTotalFeesDueAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getMiscFeeAsDouble(), loanScheduleEntity.getMiscFeeAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getMiscFeePaidAsDouble(), loanScheduleEntity.getMiscFeePaidAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getMiscFeesDueAsDouble(), loanScheduleEntity.getMiscFeesDueAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getPenaltyAsDouble(), loanScheduleEntity.getPenaltyAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getPenaltyPaidAsDouble(), loanScheduleEntity.getPenaltyPaidAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getPenaltyDueAsDouble(), loanScheduleEntity.getPenaltyDueAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getMiscPenaltyAsDouble(), loanScheduleEntity.getMiscPenaltyAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getMiscPenaltyPaidAsDouble(), loanScheduleEntity.getMiscPenaltyPaidAsDouble(), DELTA);
        assertEquals(loanScheduleEntity.getInstallmentId() + " is failing", this.loanScheduleEntity.getMiscPenaltyDueAsDouble(), loanScheduleEntity.getMiscPenaltyDueAsDouble(), DELTA);
        return ObjectUtils.equals(this.loanScheduleEntity.getPaymentDate(), loanScheduleEntity.getPaymentDate()) &&
                this.loanScheduleEntity.getPaymentStatus().equals(loanScheduleEntity.getPaymentStatus());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("LoanScheduleEntity did not match");
    }

}
