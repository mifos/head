/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.framework.formulaic;

import java.math.BigDecimal;

public class NumberValidator extends IsInstanceValidator {

    public NumberValidator() {
        super(String.class);
    }

    public NumberValidator(BigDecimal min, BigDecimal max) {
        this();
        this.min = min;
        this.max = max;
    }

    private BigDecimal min;

    private BigDecimal max;

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    @Override
    public BigDecimal validate(Object input) throws ValidationError {
        String inputString = (String) super.validate(input);
        try {
            BigDecimal value = new BigDecimal(inputString);
            if (max != null && max.compareTo(value) < 0) {
                throw makeError(input, ErrorType.NUMBER_OUT_OF_RANGE);
            }
            if (min != null && min.compareTo(value) > 0) {
                throw makeError(input, ErrorType.NUMBER_OUT_OF_RANGE);
            }
            return value;
        } catch (NumberFormatException e) {
            throw makeError(input, ErrorType.INVALID_NUMBER);
        }
    }

}
