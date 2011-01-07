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

package org.mifos.framework.formulaic;

public class MaxLengthValidator extends IsInstanceValidator {

    private int max;
    public static final String TOO_LONG_ERROR = "errors.formulaic.MaxLengthValidator.toolong";

    public MaxLengthValidator(int max) {
        super(String.class);
        assert max > 0;
        this.max = max;
    }

    @Override
    public Object validate(Object input) throws ValidationError {
        String inputString = (String) super.validate(input);
        if (inputString.length() > max) {
            throw makeError(input, ErrorType.STRING_TOO_LONG);
        }
        return inputString;
    }

}
