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

package org.mifos.application.reports.business;

import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.BRANCH_ID_INVALID_MSG;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.BRANCH_ID_PARAM;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.validator.Errors;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;
import org.mifos.framework.util.helpers.ServletUtils;

public class JdbcBranchCashConfirmationReportParameterForm extends AbstractReportParameterForm {

    private String branchId;
    private String runDate;

    public JdbcBranchCashConfirmationReportParameterForm(String branchId, String runDate) {
        this.branchId = branchId;
        this.runDate = runDate;
    }

    @Override
    public String[] getAllFormParameterNames() {
        return ReportValidationConstants.JDBC_CASH_CONFIRMATION_REPORT_PARAMS_ARRAY;
    }

    public boolean isFormEmpty() {
        return branchId == null && runDate == null;
    }

    public void removeRequestParameters(ModifiableParameterServletRequest modifiedRequest, Errors errors) {
        removeRequestParams(modifiedRequest, errors);
    }

    public void validate(Errors errors) {
        addErrorIfInvalid(errors, branchId, SELECT_BRANCH_OFFICE_SELECTION_ITEM.getId(), BRANCH_ID_PARAM,
                BRANCH_ID_INVALID_MSG);
        addErrorIfInvalidRunDate(errors, runDate, ReportValidationConstants.RUN_DATE_PARAM_FOR_CASH_CONF_REPORT,
                ReportValidationConstants.RUN_DATE_INVALID_MSG);
    }

    public static JdbcBranchCashConfirmationReportParameterForm build(HttpServletRequest request) {
        return new JdbcBranchCashConfirmationReportParameterForm(extractBranchId(request), extractRunDate(request));
    }

    private static String extractRunDate(HttpServletRequest request) {
        return ServletUtils.getParameter(request, ReportValidationConstants.RUN_DATE_PARAM_FOR_CASH_CONF_REPORT);
    }

    public String getBranchId() {
        return branchId;
    }

    public String getRunDate() {
        return runDate;
    }

}
