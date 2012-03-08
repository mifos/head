/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.util.Map;

import org.mifos.dto.domain.GLCodeDto;

public class PenaltyParametersDto {
    private final Map<String, String> categoryType;
    private final Map<String, String> statusType;
    private final Map<String, String> periodType;
    private final Map<String, String> formulaType;
    private final Map<String, String> frequencyType;
    private final Map<String, GLCodeDto> glCodes;
    
    public PenaltyParametersDto(Map<String, String> categoryType, Map<String, String> statusType,
            Map<String, String> periodType, Map<String, String> formulaType, Map<String, String> frequencyType,
            Map<String, GLCodeDto> glCodes) {
        this.categoryType = categoryType;
        this.statusType = statusType;
        this.periodType = periodType;
        this.formulaType = formulaType;
        this.frequencyType = frequencyType;
        this.glCodes = glCodes;
    }

    public Map<String, String> getCategoryType() {
        return categoryType;
    }

    public Map<String, String> getStatusType() {
        return statusType;
    }

    public Map<String, String> getPeriodType() {
        return periodType;
    }

    public Map<String, String> getFormulaType() {
        return formulaType;
    }

    public Map<String, String> getFrequencyType() {
        return frequencyType;
    }

    public Map<String, GLCodeDto> getGlCodes() {
        return glCodes;
    }
    
}
