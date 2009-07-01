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

/*
 * This validator converts strings into integers
 */
public class IntValidator extends IsInstanceValidator {

    public static final String PARSE_ERROR = "errors.formulaic.IntValidator.parse";

    public IntValidator() {
        super(String.class);
    }

    @Override
    public Integer validate(Object input) throws ValidationError {
        input = super.validate(input);
        try {
            String inputString = (String) input;
            int result = Integer.parseInt(inputString);
            return result;
        }

        catch (NumberFormatException e) {
            throw makeError(input, ErrorType.INVALID_INT);
        }
    }

}
