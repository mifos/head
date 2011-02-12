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

package org.mifos.dto.screen;

import java.util.List;

public class AccountingDetailsDto {

    private final List<Integer> applicableFunds;
    private final Integer interestGlCodeId;
    private final Integer principalClCodeId;

    public AccountingDetailsDto(List<Integer> applicableFunds, Integer interestGlCodeId, Integer principalClCodeId) {
        this.applicableFunds = applicableFunds;
        this.interestGlCodeId = interestGlCodeId;
        this.principalClCodeId = principalClCodeId;
    }

    public List<Integer> getApplicableFunds() {
        return this.applicableFunds;
    }

    public Integer getInterestGlCodeId() {
        return this.interestGlCodeId;
    }

    public Integer getPrincipalClCodeId() {
        return this.principalClCodeId;
    }
}