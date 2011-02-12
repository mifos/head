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

package org.mifos.ui.core.controller;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ByLastLoanAmountBean {

    @Min(value=0)
    @NotNull
    private Double lower;

    @Min(value=0)
    @NotNull
    private Double upper;

    @Min(value=0)
    @NotNull
    private Double min;

    @Min(value=0)
    @NotNull
    private Double max;

    @Min(value=0)
    @NotNull
    private Double theDefault;

    @SuppressWarnings("PMD") // needed for binding for some reason
    public ByLastLoanAmountBean() {
        // default constructor for binding
    }

    public Double getLower() {
        return this.lower;
    }

    public void setLower(Double lower) {
        this.lower = lower;
    }

    public Double getUpper() {
        return this.upper;
    }

    public void setUpper(Double upper) {
        this.upper = upper;
    }

    public boolean minIsLessThanMax() {
        return this.min.doubleValue() < this.max.doubleValue();
    }

    public boolean defaultIsBetweenMinAndMax() {
        return minIsLessThanMax() && this.theDefault.doubleValue() <= this.max.doubleValue();
    }

    public Double getMin() {
        return this.min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return this.max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getTheDefault() {
        return this.theDefault;
    }

    public void setTheDefault(Double theDefault) {
        this.theDefault = theDefault;
    }

    public boolean isRangeValid() {
        return this.upper.doubleValue() > this.lower.doubleValue();
    }
}
