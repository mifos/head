/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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

import static org.mifos.application.reports.ui.SelectionItem.NA_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.BRANCH_ID_INVALID_MSG;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.BRANCH_ID_PARAM;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.CENTER_ID_INVALID_MSG;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.CENTER_ID_PARAM;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.LOAN_OFFICER_ID_INVALID_MSG;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.LOAN_OFFICER_ID_PARAM;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.MEETING_DATE_INVALID_MSG;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.MEETING_DATE_PARAM;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.validator.Errors;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;
import org.mifos.framework.util.helpers.ServletUtils;

public class CollectionSheetReportParameterForm extends
		AbstractReportParameterForm {

	private final String branchId;
	private final String loanOfficerId;
	private final String centerId;
	private final String meetingDate;

	public CollectionSheetReportParameterForm(String branchId,
			String loanOfficerId, String centerId, String meetingDate) {
		this.branchId = branchId;
		this.loanOfficerId = loanOfficerId;
		this.centerId = centerId;
		this.meetingDate = meetingDate;
	}

	public void validate(Errors errors) {
		addErrorIfInvalid(errors, branchId, SELECT_BRANCH_OFFICE_SELECTION_ITEM
				.getId(), BRANCH_ID_PARAM, BRANCH_ID_INVALID_MSG);
		addErrorIfInvalid(errors, loanOfficerId,
				SELECT_LOAN_OFFICER_SELECTION_ITEM.getId(),
				LOAN_OFFICER_ID_PARAM, LOAN_OFFICER_ID_INVALID_MSG);
		addErrorIfInvalid(errors, loanOfficerId, NA_LOAN_OFFICER_SELECTION_ITEM
				.getId(), LOAN_OFFICER_ID_PARAM, LOAN_OFFICER_ID_INVALID_MSG);
		addErrorIfInvalid(errors, centerId, SELECT_CENTER_SELECTION_ITEM
				.getId(), CENTER_ID_PARAM, CENTER_ID_INVALID_MSG);
		addErrorIfInvalid(errors, centerId, NA_CENTER_SELECTION_ITEM.getId(),
				CENTER_ID_PARAM, CENTER_ID_INVALID_MSG);
		addErrorIfInvalid(errors, meetingDate,
				DateSelectionItem.NA_MEETING_DATE.getDate(),
				MEETING_DATE_PARAM, MEETING_DATE_INVALID_MSG);
	}

	public static CollectionSheetReportParameterForm build(
			HttpServletRequest request) {
		return new CollectionSheetReportParameterForm(extractBranchId(request),
				extractLoanOfficerId(request), extractCenterId(request),
				extractMeetingDate(request));
	}

	public void removeRequestParameters(
			ModifiableParameterServletRequest modifiedRequest, Errors errors) {
		removeRequestParams(modifiedRequest, errors);
	}

	private static String extractMeetingDate(HttpServletRequest request) {
		return ServletUtils.getParameter(request, MEETING_DATE_PARAM);
	}

	private static String extractCenterId(HttpServletRequest request) {
		return ServletUtils.getParameter(request, CENTER_ID_PARAM);
	}

	private static String extractLoanOfficerId(HttpServletRequest request) {
		return ServletUtils.getParameter(request, LOAN_OFFICER_ID_PARAM);
	}

	public boolean isFormEmpty() {
		return branchId == null && loanOfficerId == null && centerId == null
				&& meetingDate == null;
	}

	@Override
	public String[] getAllFormParameterNames() {
		return ReportValidationConstants.COLLECTION_SHEET_REPORT_PARAMS_ARRAY;
	}
}
