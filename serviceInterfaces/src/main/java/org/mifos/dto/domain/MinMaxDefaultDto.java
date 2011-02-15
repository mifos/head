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

package org.mifos.dto.domain;

@SuppressWarnings("PMD")
public class MinMaxDefaultDto {

    private final Number min;
    private final Number max;
    private final Number theDefault;

    public static MinMaxDefaultDto create(Number min, Number max, Number theDefault) {
        return new MinMaxDefaultDto(min, max, theDefault);
    }

    private MinMaxDefaultDto(Number min, Number max, Number theDefault) {
        this.min = min;
        this.max = max;
        this.theDefault = theDefault;
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