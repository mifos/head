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

@SuppressWarnings("PMD")
public class LowerUpperMinMaxDefaultDto {

    private final Number lower;
    private final Number upper;
    private final Number min;
    private final Number max;
    private final Number theDefault;

    public static LowerUpperMinMaxDefaultDto create(Number lower, Number upper, Number min, Number max, Number theDefault) {
        return new LowerUpperMinMaxDefaultDto(lower, upper, min, max, theDefault);
    }

    private LowerUpperMinMaxDefaultDto(Number lower, Number upper, Number min, Number max, Number theDefault) {
        this.lower = lower;
        this.upper = upper;
        this.min = min;
        this.max = max;
        this.theDefault = theDefault;
    }

    public Number getLower() {
        return this.lower;
    }

    public Number getUpper() {
        return this.upper;
    }

    public Number getMin() {
        return this.min;
    }

    public Number getMax() {
        return this.max;
    }

    public Number getTheDefault() {
        return this.theDefault;
    }
}