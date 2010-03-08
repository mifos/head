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
 * If input is null, this validator returns null.  Otherwise, returns
 * the output of passing the input through the contained validator.
 * Useful for indicating that field is optional, but if it is given it must
 * meet certain constraints
 */
public class NotNullEmptyValidator extends IsInstanceValidator {

    private Validator other;
    private String fieldName;

    public NotNullEmptyValidator() {
        super(String.class);
    }

    public NotNullEmptyValidator(String fieldName) {
        super(String.class);
        this.fieldName = fieldName;
    }

    public NotNullEmptyValidator(Validator other) {
        super(String.class);
        this.other = other;
    }

    public NotNullEmptyValidator(String fieldName, Validator other) {
        super(String.class);
        this.other = other;
        this.fieldName = fieldName;
    }

    @Override
    public String validate(Object value) throws ValidationError {
        super.validate(value);
        if (other != null)
            other.validate(value);
        if (((String) value).trim().equals("")) {
            if (fieldName == null)
                throw makeError(value, ErrorType.MISSING);
            else
                throw makeError(value, ErrorType.MISSING_FIELD, fieldName);
        }
        return (String) value;
    }
}
