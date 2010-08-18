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

import org.mifos.dto.domain.LowerUpperMinMaxDefaultDto;
import org.mifos.dto.domain.MinMaxDefaultDto;

public class LoanAmountDetailsDto {

    private final Integer calculationType;
    private final MinMaxDefaultDto sameForAllLoanRange;
    private final List<LowerUpperMinMaxDefaultDto> byLastLoanAmountList;
    private final List<MinMaxDefaultDto> byLoanCycleList;

    public LoanAmountDetailsDto(Integer calculationType, MinMaxDefaultDto sameForAllLoanRange, List<LowerUpperMinMaxDefaultDto> byLastLoanAmountList, List<MinMaxDefaultDto> byLoanCycleList) {
        this.calculationType = calculationType;
        this.sameForAllLoanRange = sameForAllLoanRange;
        this.byLastLoanAmountList = byLastLoanAmountList;
        this.byLoanCycleList = byLoanCycleList;
    }

    public Integer getCalculationType() {
        return this.calculationType;
    }

    public MinMaxDefaultDto getSameForAllLoanRange() {
        return this.sameForAllLoanRange;
    }

    public List<LowerUpperMinMaxDefaultDto> getByLastLoanAmountList() {
        return this.byLastLoanAmountList;
    }

    public List<MinMaxDefaultDto> getByLoanCycleList() {
        return this.byLoanCycleList;
    }
}