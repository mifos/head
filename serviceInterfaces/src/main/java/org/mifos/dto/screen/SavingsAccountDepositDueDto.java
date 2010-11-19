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

package org.mifos.dto.screen;

import java.util.List;

import org.mifos.dto.domain.DueOnDateDto;

public class SavingsAccountDepositDueDto {

    private final DueOnDateDto nextDueDetail;
    private final List<DueOnDateDto> previousDueDetails;
    private final Short stateId;
    private final String stateName;

    public SavingsAccountDepositDueDto(DueOnDateDto nextDueDetail, List<DueOnDateDto> previousDueDetails, Short stateId, String stateName) {
        this.nextDueDetail = nextDueDetail;
        this.previousDueDetails = previousDueDetails;
        this.stateId = stateId;
        this.stateName = stateName;
    }

    public DueOnDateDto getNextDueDetail() {
        return this.nextDueDetail;
    }

    public List<DueOnDateDto> getPreviousDueDetails() {
        return this.previousDueDetails;
    }

    public Short getStateId() {
        return this.stateId;
    }

    public String getStateName() {
        return this.stateName;
    }
}