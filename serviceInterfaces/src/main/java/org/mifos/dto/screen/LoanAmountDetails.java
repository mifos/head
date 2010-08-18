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

public class LoanAmountDetails {

    private final Integer calculationType;
    private final MinMaxDefaultDto<Double> sameForAllLoanRange;
    private final List<LowerUpperMinMaxDefaultDto<Double>> byLastLoanAmountList;
    private final List<MinMaxDefaultDto<Double>> byLoanCycleList;

    public LoanAmountDetails(Integer calculationType, MinMaxDefaultDto<Double> sameForAllLoanRange, List<LowerUpperMinMaxDefaultDto<Double>> byLastLoanAmountList, List<MinMaxDefaultDto<Double>> byLoanCycleList) {
        this.calculationType = calculationType;
        this.sameForAllLoanRange = sameForAllLoanRange;
        this.byLastLoanAmountList = byLastLoanAmountList;
        this.byLoanCycleList = byLoanCycleList;
    }

    public Integer getCalculationType() {
        return this.calculationType;
    }

    public MinMaxDefaultDto<Double> getSameForAllLoanRange() {
        return this.sameForAllLoanRange;
    }

    public List<LowerUpperMinMaxDefaultDto<Double>> getByLastLoanAmountList() {
        return this.byLastLoanAmountList;
    }

    public List<MinMaxDefaultDto<Double>> getByLoanCycleList() {
        return this.byLoanCycleList;
    }
}