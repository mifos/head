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

package org.mifos.reports.business.validator;

import java.util.ArrayList;
import java.util.List;

public class Errors {
    List<ErrorEntry> errors;

    public Errors() {
        errors = new ArrayList<ErrorEntry>();
    }

    public void rejectValue(String fieldName, String errorCode) {
        errors.add(new ErrorEntry(fieldName, errorCode));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<ErrorEntry> getErrors() {
        return errors;
    }

    public ErrorEntry getFieldError(String fieldName) {
        for (ErrorEntry errorEntry : errors) {
            if (errorEntry.fieldName.equals(fieldName)) {
                return errorEntry;
            }
        }
        return null;
    }
}
