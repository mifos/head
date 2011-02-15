/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.platform.questionnaire.exceptions;

import org.mifos.platform.validations.ValidationException;

public class BadNumericResponseException extends ValidationException {
    private static final long serialVersionUID = 8068243180234201365L;

    private final Integer allowedMinValue;
    private final Integer allowedMaxValue;

    public BadNumericResponseException(String questionTitle, Integer allowedMinValue, Integer allowedMaxValue) {
        super("questionnaire.error.numeric.question.has.bad.answer", questionTitle);
        this.allowedMinValue = allowedMinValue;
        this.allowedMaxValue = allowedMaxValue;
    }

    public Integer getAllowedMinValue() {
        return allowedMinValue;
    }

    public Integer getAllowedMaxValue() {
        return allowedMaxValue;
    }

    public boolean areMinMaxBoundsPresent() {
        return getAllowedMinValue() != null && getAllowedMaxValue() != null;
    }

    public boolean isMinBoundPresent() {
        return getAllowedMinValue() != null;
    }

    public boolean isMaxBoundPresent() {
        return getAllowedMaxValue() != null;
    }
}
