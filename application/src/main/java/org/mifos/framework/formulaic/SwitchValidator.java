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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

public class SwitchValidator extends BaseValidator {

    public static final String MISSING_EXPECTED_VALUE_ERROR = "errors.formulaic.SwitchValidator.missingexpectedvalue";

    private Map<Object, Schema> cases = new HashMap<Object, Schema>();
    private String switchField;
    private Schema defaultCase;

    public void addCase(Object value, Schema schema) {
        cases.put(value, schema);
    }

    public SwitchValidator(String switchField) {
        this(switchField, null);
    }

    public SwitchValidator(String switchField, Schema defaultCase) {
        this.defaultCase = defaultCase;
        this.switchField = switchField;
    }

    public void setDefaultCase(Schema defaultCase) {
        this.defaultCase = defaultCase;
    }

    public Map<String, Object> validate(ServletRequest request) throws ValidationError {
        return validate(Schema.convertRequest(request));
    }

    @Override
    public Map<String, Object> validate(Object objectData) throws ValidationError {
        Map<String, String> data;

        try {
            data = (Map<String, String>) objectData;
        }

        catch (ClassCastException e) {
            throw makeError(objectData, ErrorType.WRONG_VALUE);
        }

        if (data.containsKey(switchField) && cases.containsKey(data.get(switchField))) {
            Schema correctSchema = cases.get(data.get(switchField));
            return correctSchema.validate(data);
        } else {
            if (defaultCase != null) {
                return defaultCase.validate(data);
            }
            // if a switch field value matching the available choices is not
            // given,
            // and there's no default
            else {
                throw makeError(objectData, ErrorType.MISSING_EXPECTED_VALUE); // the
                                                                               // default,
                                                                               // most
                                                                               // generic
                                                                               // validation
                                                                               // error
            }
        }
    }

}
