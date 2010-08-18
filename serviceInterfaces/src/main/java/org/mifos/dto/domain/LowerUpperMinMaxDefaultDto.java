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

public class LowerUpperMinMaxDefaultDto<T> {

    private final T lower;
    private final T upper;
    private final T min;
    private final T max;
    private final T theDefault;

    public LowerUpperMinMaxDefaultDto(T lower, T upper, T min, T max, T theDefault) {
        this.lower = lower;
        this.upper = upper;
        this.min = min;
        this.max = max;
        this.theDefault = theDefault;
    }

    public T getLower() {
        return this.lower;
    }

    public T getUpper() {
        return this.upper;
    }

    public T getMin() {
        return this.min;
    }

    public T getMax() {
        return this.max;
    }

    public T getTheDefault() {
        return this.theDefault;
    }
}