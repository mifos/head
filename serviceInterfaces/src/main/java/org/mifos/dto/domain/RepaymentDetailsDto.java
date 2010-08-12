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

package org.mifos.dto.domain;

public class RepaymentDetailsDto {

    private final Integer frequencyType;
    private final Integer recurs;
    private final Integer calculationType;
    private final MinMaxDefaultDto<Integer> sameForAllLoanRange;
    private final Integer gracePeriodType;
    private final Integer gracePeriodDuration;

    public RepaymentDetailsDto(Integer frequencyType, Integer recurs, Integer calculationType,
            MinMaxDefaultDto<Integer> sameForAllLoanRange, Integer gracePeriodType, Integer gracePeriodDuration) {
        this.frequencyType = frequencyType;
        this.recurs = recurs;
        this.calculationType = calculationType;
        this.sameForAllLoanRange = sameForAllLoanRange;
        this.gracePeriodType = gracePeriodType;
        this.gracePeriodDuration = gracePeriodDuration;
    }

    public Integer getFrequencyType() {
        return this.frequencyType;
    }

    public Integer getRecurs() {
        return this.recurs;
    }

    public Integer getCalculationType() {
        return this.calculationType;
    }

    public MinMaxDefaultDto<Integer> getSameForAllLoanRange() {
        return this.sameForAllLoanRange;
    }

    public Integer getGracePeriodType() {
        return this.gracePeriodType;
    }

    public Integer getGracePeriodDuration() {
        return this.gracePeriodDuration;
    }
}