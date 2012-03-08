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

public class FeeParameters {

    private final Map<Short, String> categories;
    private final Map<Short, String> timesOfCharging;
    private final Map<Short, String> timesOfChargingCustomers;
    private final Map<Short, String> formulas;
    private final Map<Short, String> frequencies;
    private final Map<Short, GLCodeDto> glCodes;

    public FeeParameters(Map<Short, String> categories, Map<Short, String> timesOfCharging,
            Map<Short, String> timesOfChargingCustomers, Map<Short, String> formulas, Map<Short, String> frequencies,
            Map<Short, GLCodeDto> glCodes) {
        this.categories = categories;
        this.timesOfCharging = timesOfCharging;
        this.timesOfChargingCustomers = timesOfChargingCustomers;
        this.formulas = formulas;
        this.frequencies = frequencies;
        this.glCodes = glCodes;
    }

    public Map<Short, String> getCategories() {
        return this.categories;
    }

    public Map<Short, String> getTimesOfCharging() {
        return this.timesOfCharging;
    }

    public Map<Short, String> getTimesOfChargingCustomers() {
        return this.timesOfChargingCustomers;
    }

    public Map<Short, String> getFormulas() {
        return this.formulas;
    }

    public Map<Short, String> getFrequencies() {
        return this.frequencies;
    }

    public Map<Short, GLCodeDto> getGlCodes() {
        return this.glCodes;
    }
}