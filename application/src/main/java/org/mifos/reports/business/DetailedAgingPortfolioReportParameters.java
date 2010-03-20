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

package org.mifos.reports.business;

import javax.servlet.http.HttpServletRequest;

import org.mifos.framework.servlet.ModifiableParameterServletRequest;
import org.mifos.framework.util.helpers.NumberUtils;
import org.mifos.framework.util.helpers.ServletUtils;
import org.mifos.reports.business.validator.Errors;
import org.mifos.reports.util.helpers.ReportValidationConstants;

public class DetailedAgingPortfolioReportParameters extends AbstractReportParameterForm {

    private final String branchId;
    private final String loanOfficerId;
    private final String productId;

    public static final String VALID_ID = "1";

    public static final String INVALID_ID = "-2";

    DetailedAgingPortfolioReportParameters(String branchId, String loanOfficerId, String productId) {
        this.branchId = branchId;
        this.loanOfficerId = loanOfficerId;
        this.productId = productId;
    }

    public void validate(Errors errors) {
        addErrorIfInvalid(errors, branchId, ReportValidationConstants.BRANCH_ID_PARAM,
                ReportValidationConstants.BRANCH_ID_INVALID_MSG);
        addErrorIfInvalid(errors, loanOfficerId, ReportValidationConstants.LOAN_OFFICER_ID_PARAM,
                ReportValidationConstants.LOAN_OFFICER_ID_INVALID_MSG);
        addErrorIfInvalid(errors, productId, ReportValidationConstants.LOAN_PRODUCT_ID_PARAM,
                ReportValidationConstants.LOAN_PRODUCT_ID_INVALID_MSG);
    }

    private void addErrorIfInvalid(Errors errors, String parameter, String fieldName, String errorCode) {
        if (!NumberUtils.isDigits(parameter) || Integer.valueOf(INVALID_ID).equals(Integer.valueOf(parameter))) {
            errors.rejectValue(fieldName, errorCode);
        }
    }

    public static DetailedAgingPortfolioReportParameters build(HttpServletRequest request) {
        return new DetailedAgingPortfolioReportParameters(ServletUtils.getParameter(request,
                ReportValidationConstants.BRANCH_ID_PARAM), ServletUtils.getParameter(request,
                ReportValidationConstants.LOAN_OFFICER_ID_PARAM), ServletUtils.getParameter(request,
                ReportValidationConstants.LOAN_PRODUCT_ID_PARAM));
    }

    public void removeRequestParameters(ModifiableParameterServletRequest modifiedRequest, Errors errors) {
        removeRequestParams(modifiedRequest, errors);
    }

    public boolean isFormEmpty() {
        return branchId == null && loanOfficerId == null && productId == null;
    }

    @Override
    public String[] getAllFormParameterNames() {
        return ReportValidationConstants.DETAILED_AGING_REPORT_PARAMS_ARRAY;
    }
}
