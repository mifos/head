/*
 * Copyright Grameen Foundation USA
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

import java.util.Collection;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.accounts.loan.business.OriginalLoanScheduleEntity;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.Transformer;

public class OriginalLoanScheduleEntitiesMatcher extends TypeSafeMatcher<Collection<OriginalLoanScheduleEntity>> {
    private Map<Short, OriginalLoanScheduleEntity> loanScheduleEntityMap;

    public OriginalLoanScheduleEntitiesMatcher(Collection<OriginalLoanScheduleEntity> loanScheduleEntityMap) {
        this.loanScheduleEntityMap = CollectionUtils.asValueMap(loanScheduleEntityMap, new Transformer<OriginalLoanScheduleEntity, Short>() {
            @Override
            public Short transform(OriginalLoanScheduleEntity input) {
                return input.getInstallmentId();
            }
        });
    }

    @Override
    public boolean matchesSafely(Collection<OriginalLoanScheduleEntity> loanScheduleEntities) {
        if (this.loanScheduleEntityMap.size() == loanScheduleEntities.size()) {
            for (OriginalLoanScheduleEntity loanScheduleEntity : loanScheduleEntities) {
                if(!loanScheduleEntityMap.containsKey(loanScheduleEntity.getInstallmentId())){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("OriginalLoanScheduleEntities did not match");
    }

}
