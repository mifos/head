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

import java.util.Date;
import java.util.Map;

import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.helpers.DateUtils;

public class DateComponentValidator extends IsInstanceValidator {

    public DateComponentValidator() {
        super(Map.class);
    }

    @Override
    public Date validate(Object input) throws ValidationError {
        input = super.validate(input);
        Map inputMap = (Map) input;
        if (inputMap.size() == 0) {
            throw makeError(input, ErrorType.MISSING);
        }
        try {
            String dayValue = (String) inputMap.get("DD");
            String monthValue = (String) inputMap.get("MM");
            String yearValue = (String) inputMap.get("YY");
            return DateUtils.parseBrowserDateFields(yearValue, monthValue, dayValue);
        } catch (InvalidDateException e) {
            throw makeError(input, ErrorType.DATE_FORMAT);
        }
    }

}
