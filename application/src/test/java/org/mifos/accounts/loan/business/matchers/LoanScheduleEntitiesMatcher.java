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
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.Transformer;

import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertThat;

public class LoanScheduleEntitiesMatcher extends TypeSafeMatcher<Collection<LoanScheduleEntity>> {
    private Map<Short, LoanScheduleEntity> loanScheduleEntityMap;

    public LoanScheduleEntitiesMatcher(Collection<LoanScheduleEntity> loanScheduleEntityMap) {
        this.loanScheduleEntityMap = CollectionUtils.asValueMap(loanScheduleEntityMap, new Transformer<LoanScheduleEntity, Short>() {
            @Override
            public Short transform(LoanScheduleEntity input) {
                return input.getInstallmentId();
            }
        });
    }

    @Override
    public boolean matchesSafely(Collection<LoanScheduleEntity> loanScheduleEntities) {
        if (this.loanScheduleEntityMap.size() == loanScheduleEntities.size()) {
            for (LoanScheduleEntity loanScheduleEntity : loanScheduleEntities) {
                assertThat(loanScheduleEntity,
                        new LoanScheduleEntityMatcher(loanScheduleEntityMap.get(loanScheduleEntity.getInstallmentId())));
            }
            return true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("LoanScheduleEntities did not match");
    }

}
