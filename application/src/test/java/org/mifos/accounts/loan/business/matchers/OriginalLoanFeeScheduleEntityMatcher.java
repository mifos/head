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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.accounts.loan.business.OriginalLoanFeeScheduleEntity;

public class OriginalLoanFeeScheduleEntityMatcher extends TypeSafeMatcher<OriginalLoanFeeScheduleEntity> {
    private OriginalLoanFeeScheduleEntity feeScheduleEntity;

    public OriginalLoanFeeScheduleEntityMatcher(OriginalLoanFeeScheduleEntity feeScheduleEntity) {
        this.feeScheduleEntity = feeScheduleEntity;
    }


    @Override
    public boolean matchesSafely(OriginalLoanFeeScheduleEntity originalLoanFeeScheduleEntity) {
        return feeScheduleEntity.getInstallmentId().equals(originalLoanFeeScheduleEntity.getInstallmentId());
    }

    @Override
    public void describeTo(Description description) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
