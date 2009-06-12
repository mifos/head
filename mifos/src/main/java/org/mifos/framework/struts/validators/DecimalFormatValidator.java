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

package org.mifos.framework.struts.validators;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.util.ValidatorUtils;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.Resources;
import org.mifos.framework.util.helpers.DecimalFieldHelper;
import org.mifos.framework.util.LocalizationConverter;

/**
 * This is a custom validator class used to validate the format of the decimal
 * field.
 */
public class DecimalFormatValidator {

    public DecimalFormatValidator() {
        super();
    }

    /**
     * This class validates a decimal field of the action form.The format
     * against which it would validate will be obtained from the var elements
     * defined in validation.xml. The name of the var element which is supposed
     * to hold the format should be 'format', and it should be specfied as (x,y)
     * where x-y is the total number of digits before decimal
     * 
     * @param bean
     * @param va
     * @param field
     * @param errors
     * @param request
     * @return
     */
    public static boolean validateDecimalFormat(Object bean, ValidatorAction va, Field field, ActionMessages errors,
            Validator validator, HttpServletRequest request) {

        Double validatableField = null;
        boolean returnable = true;
        String format = null;
        // get the field to be validated.
        String fieldToBeValidated = ValidatorUtils.getValueAsString(bean, field.getProperty());
        // it could be null if the property is not being passed from the UI
        if (null != fieldToBeValidated && fieldToBeValidated != "") {
            try {

                validatableField = LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(
                        fieldToBeValidated);
                validatableField = Math.abs(validatableField);
                // get the format against which it has to be validated
                format = field.getVarValue("format");
                // validate using a helper
                returnable = DecimalFieldHelper.validate(validatableField, format);

                if (!returnable) {
                    errors.add(field.getKey(), Resources.getActionMessage(validator, request, va, field));
                }
            } catch (NumberFormatException nfe) {

                nfe.printStackTrace();
            }
        }
        return returnable;
    }

}
