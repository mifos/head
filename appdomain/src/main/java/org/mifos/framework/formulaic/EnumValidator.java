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

/*
 * This validator transforms a string into its matching enum, given an Enum
 * class to look up values in.  The validator assumes that individual enums are
 * given uppercased names, and matches the given strings in a case-insensitive manner,
 * keeping with current mifos style.
 */
public class EnumValidator extends IsInstanceValidator {

    public static final String INVALID_ENUM_ERROR = "invalidenum";

    private Class enumType;
    private String fieldName;

    public EnumValidator(Class enumType) {
        super(String.class);
        assert enumType != null;
        assert enumType.isEnum();
        this.enumType = enumType;
    }

    public EnumValidator(Class enumType, String fieldName) {
        super(String.class);
        assert enumType != null;
        assert enumType.isEnum();
        this.enumType = enumType;
        this.fieldName = fieldName;
    }

    @Override
    public Enum validate(Object input) throws ValidationError {
        input = super.validate(input);

        try {
            String inputString = ((String) input).toUpperCase();
            return Enum.valueOf(enumType, inputString);
        } catch (IllegalArgumentException e) {
            if (fieldName == null) {
                throw makeError(input, ErrorType.INVALID_ENUM);
            } else {
                throw makeError(input, ErrorType.INVALID_ENUM, fieldName);
            }
        }

    }

}
