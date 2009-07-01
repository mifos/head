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

package org.mifos.application.reports.business.validator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.BranchReportParameterForm;
import org.mifos.application.reports.business.service.BranchCashConfirmationReportService;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;

public class BranchCashConfirmationReportParamValidator extends
        AbstractReportParameterValidator<BranchReportParameterForm> {

    private final BranchCashConfirmationReportService service;

    public BranchCashConfirmationReportParamValidator(List<String> applicableFilePaths,
            BranchCashConfirmationReportService service) {
        super(applicableFilePaths);
        this.service = service;
    }

    public BranchReportParameterForm buildReportParameterForm(HttpServletRequest request) {
        return BranchReportParameterForm.build(request);
    }

    @Override
    public void validate(BranchReportParameterForm form, Errors errors) {
        super.validate(form, errors);
        if (errors.hasErrors())
            return;
        if (!service.isReportDataPresentForRundateAndBranchId(form.getBranchId(), form.getRunDate())) {
            errors.rejectValue(ReportValidationConstants.RUN_DATE_PARAM,
                    ReportValidationConstants.BRANCH_REPORT_NO_DATA_FOUND_MSG);
        }
    }

}
