package org.mifos.application.reports.business;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.validator.Errors;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;

public class SqlCollectionSheetReportParameterForm extends
		CollectionSheetReportParameterForm {

	public SqlCollectionSheetReportParameterForm(String branchId,
			String loanOfficerId, String centerId, String meetingDate) {
		super(branchId, loanOfficerId, centerId, meetingDate);
	}
	
	@Override
	public void validate(Errors errors) {
		validateCascadingParameters(errors);
		addErrorIfInvalidRunDate(errors, getMeetingDate(), ReportValidationConstants.MEETING_DATE_PARAM, ReportValidationConstants.RUN_DATE_INVALID_MSG);
	}
	
	public static SqlCollectionSheetReportParameterForm build(
			HttpServletRequest request) {
		return new SqlCollectionSheetReportParameterForm(extractBranchId(request),
				CollectionSheetReportParameterForm.extractLoanOfficerId(request), CollectionSheetReportParameterForm.extractCenterId(request),
				CollectionSheetReportParameterForm.extractMeetingDate(request));
	}	
}
