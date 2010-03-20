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

package org.mifos.reports.business;

import javax.servlet.http.HttpServletRequest;

import org.mifos.reports.business.validator.Errors;
import org.mifos.reports.util.helpers.ReportValidationConstants;

public class SqlCollectionSheetReportParameterForm extends CollectionSheetReportParameterForm {

    public SqlCollectionSheetReportParameterForm(String branchId, String loanOfficerId, String centerId,
            String meetingDate) {
        super(branchId, loanOfficerId, centerId, meetingDate);
    }

    @Override
    public void validate(Errors errors) {
        validateCascadingParameters(errors);
        addErrorIfInvalidRunDate(errors, getMeetingDate(), ReportValidationConstants.MEETING_DATE_PARAM,
                ReportValidationConstants.RUN_DATE_INVALID_MSG);
    }

    public static SqlCollectionSheetReportParameterForm build(HttpServletRequest request) {
        return new SqlCollectionSheetReportParameterForm(extractBranchId(request), CollectionSheetReportParameterForm
                .extractLoanOfficerId(request), CollectionSheetReportParameterForm.extractCenterId(request),
                CollectionSheetReportParameterForm.extractMeetingDate(request));
    }
}
