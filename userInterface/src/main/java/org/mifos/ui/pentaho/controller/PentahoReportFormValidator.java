/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
package org.mifos.ui.pentaho.controller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifos.reports.pentaho.params.PentahoDateParameter;
import org.mifos.reports.pentaho.params.PentahoInputParameter;
import org.mifos.reports.pentaho.params.PentahoMultiSelectParameter;
import org.mifos.reports.pentaho.params.PentahoSingleSelectParameter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@SuppressWarnings("PMD")
public class PentahoReportFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PentahoReportFormBean.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PentahoReportFormBean formBean = (PentahoReportFormBean) target;

        validateDateParams(formBean, errors);
        validateInputParams(formBean, errors);
        validateSingleSelectParams(formBean, errors);
        validateMultiSelectParams(formBean, errors);
    }

    private void validateDateParams(PentahoReportFormBean formBean, Errors errors) {
        int i = 0;
        String[] args = new String[1];
        for (PentahoDateParameter dateParam : formBean.getReportDateParams()) {
            if(!dateParam.getDateDD().isEmpty()){
             try {
                    Integer day = Integer.parseInt(dateParam.getDateDD().split("/")[0]);
                    Integer month = Integer.parseInt(dateParam.getDateDD().split("/")[1]);
                    Integer year = Integer.parseInt(dateParam.getDateDD().split("/")[2]);
                    new LocalDate(year, month, day);
                } catch (RuntimeException ex) {
                    args[0] = dateParam.getParamName();
                    errors.rejectValue("reportDateParams[" + i++ + "].dateDD", "reports.invalidDate", args,
                            dateParam.getParamName() + ": Invalid date");
                }
            } else if (dateParam.isMandatory()) {
                rejectValueAsMandatory("reportDateParams[" + i++ + "].dateDD", dateParam.getParamName(), errors);
            }
        }
    }

    private void validateInputParams(PentahoReportFormBean formBean, Errors errors) {
        int i = 0;
        for (PentahoInputParameter inputParam : formBean.getReportInputParams()) {
            if (inputParam.isMandatory() && StringUtils.isBlank(inputParam.getValue())) {
                rejectValueAsMandatory("reportInputParams[" + i++ + "].value", inputParam.getParamName(), errors);
            }
        }
    }

    private void validateSingleSelectParams(PentahoReportFormBean formBean, Errors errors) {
        int i = 0;
        for (PentahoSingleSelectParameter singleSelectParam : formBean.getReportSingleSelectParams()) {
            if (singleSelectParam.isMandatory() && StringUtils.isBlank(singleSelectParam.getSelectedValue())) {
                rejectValueAsMandatory("reportSingleSelectParams[" + i++ + "].selectedValue",
                        singleSelectParam.getParamName(), errors);
            }
        }
    }

    private void validateMultiSelectParams(PentahoReportFormBean formBean, Errors errors) {
        int i = 0;
        for (PentahoMultiSelectParameter multiSelectParam : formBean.getReportMultiSelectParams()) {
            if (multiSelectParam.isMandatory() && multiSelectParam.getSelectedValues().isEmpty()) {
                rejectValueAsMandatory("reportMultiSelectParams[" + i++ + "].selectedValues",
                        multiSelectParam.getParamName(), errors);
            }
        }
    }

    private void rejectValueAsMandatory(String fieldName, String paramName, Errors errors) {
        String errCode = "reports.mandatory";
        String[] args = new String[] { paramName };
        String defaultMsg = paramName + ": field is mandatory";

        errors.rejectValue(fieldName, errCode, args, defaultMsg);
    }
}
