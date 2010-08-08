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

package org.mifos.ui.core.controller;

import org.joda.time.DateTime;
import org.mifos.dto.domain.OfficeHoliday;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class HolidayFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return HolidayFormBean.class.isAssignableFrom(clazz) || OfficeHoliday.class.isAssignableFrom(clazz);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "REC_CATCH_EXCEPTION"}, justification = "Using catch all to detect invalid dates.")
    @Override
    public void validate(Object target, Errors errors) {

        if (target instanceof OfficeHoliday) {
            return;
        }

        HolidayFormBean formBean = (HolidayFormBean) target;
        rejectIfEmptyOrWhitespace(errors, formBean.getName(), "error.holiday.mandatory_field", "Please specify Holiday Name.");

        try {
            new DateTime().withDate(Integer.parseInt(formBean.getFromYear()), formBean.getFromMonth(), formBean.getFromDay()).toDate();
        } catch (Exception e) {
            errors.reject("holiday.fromDate.invalid", "Please specify From Date.");
        }

        try {
            if (formBean.getToDay() != null) {
                new DateTime().withDate(Integer.parseInt(formBean.getToYear()), formBean.getToMonth(), formBean.getToDay()).toDate();
            }
        } catch (Exception e) {
            errors.reject("holiday.thruDate.invalid", "Please specify thru Date.");
        }

        if (formBean.getRepaymentRuleId() == null || Integer.parseInt(formBean.getRepaymentRuleId()) < 0) {
            errors.reject("holiday.repaymentrule.required", "Please specify Repayment Rule.");
        }

        if (formBean.getSelectedOfficeIds().trim().isEmpty()) {
            errors.reject("holiday.appliesto.required", "Please specify Applies To.");
        }
    }

    private void rejectIfEmptyOrWhitespace(Errors errors, String value, String errorCode, String defaultMessage) {

        if (value == null ||!StringUtils.hasText(value)) {
            errors.reject(errorCode, defaultMessage);
        }
    }

}
