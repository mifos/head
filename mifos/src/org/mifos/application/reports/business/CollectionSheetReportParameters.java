package org.mifos.application.reports.business;

import static org.mifos.application.reports.ui.SelectionItem.NA_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.util.helpers.CollectionSheetReportConstants.BRANCH_ID_PARAM;
import static org.mifos.application.reports.util.helpers.CollectionSheetReportConstants.CENTER_ID_INVALID_MSG;
import static org.mifos.application.reports.util.helpers.CollectionSheetReportConstants.CENTER_ID_PARAM;
import static org.mifos.application.reports.util.helpers.CollectionSheetReportConstants.LOAN_OFFICER_ID_INVALID_MSG;
import static org.mifos.application.reports.util.helpers.CollectionSheetReportConstants.LOAN_OFFICER_ID_PARAM;
import static org.mifos.application.reports.util.helpers.CollectionSheetReportConstants.MEETING_DATE_INVALID_MSG;
import static org.mifos.application.reports.util.helpers.CollectionSheetReportConstants.MEETING_DATE_PARAM;
import static org.mifos.application.reports.util.helpers.CollectionSheetReportConstants.REPORT_PARAMS_ARRAY;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.validator.Errors;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;
import org.mifos.framework.util.helpers.NumberUtils;

public class CollectionSheetReportParameters implements ReportParameterForm {

	public static final SimpleDateFormat REPORT_DATE_PARAM_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy hh:mm:ss a");
	private final String branchId;
	private final String loanOfficerId;
	private final String centerId;
	private final String meetingDate;

	public CollectionSheetReportParameters(String branchId,
			String loanOfficerId, String centerId, String meetingDate) {
		this.branchId = branchId;
		this.loanOfficerId = loanOfficerId;
		this.centerId = centerId;
		this.meetingDate = meetingDate;
	}

	public void validate(Errors errors) {
		addErrorIfInvalid(errors, branchId,
				SELECT_BRANCH_OFFICE_SELECTION_ITEM
						.getId(), BRANCH_ID_PARAM, "branchId.invalid");
		addErrorIfInvalid(errors, loanOfficerId,
				SELECT_LOAN_OFFICER_SELECTION_ITEM
						.getId(), LOAN_OFFICER_ID_PARAM,
				LOAN_OFFICER_ID_INVALID_MSG);
		addErrorIfInvalid(errors, loanOfficerId,
				NA_LOAN_OFFICER_SELECTION_ITEM
						.getId(), LOAN_OFFICER_ID_PARAM,
				LOAN_OFFICER_ID_INVALID_MSG);
		addErrorIfInvalid(errors, centerId,
				SELECT_CENTER_SELECTION_ITEM.getId(),
				CENTER_ID_PARAM, CENTER_ID_INVALID_MSG);
		addErrorIfInvalid(errors, centerId,
				NA_CENTER_SELECTION_ITEM.getId(),
				CENTER_ID_PARAM, CENTER_ID_INVALID_MSG);
		addErrorIfInvalid(errors, meetingDate,
				DateSelectionItem.NA_MEETING_DATE.getDate(),
				MEETING_DATE_PARAM, MEETING_DATE_INVALID_MSG);
	}

	private void addErrorIfInvalid(Errors errors, String parameter,
			Date notAvailableDate, String fieldName, String errorCode) {
		Date date = null;
		try {
			date = REPORT_DATE_PARAM_FORMAT.parse(parameter);
		}
		catch (ParseException e) {
		}
		if (notAvailableDate.equals(date)) {
			errors.rejectValue(fieldName, errorCode);
		}
	}

	private void addErrorIfInvalid(Errors errors, String parameter,
			Integer selectValue, String fieldName, String errorCode) {
		if (!NumberUtils.isDigits(parameter)
				|| selectValue.equals(Integer.valueOf(parameter))) {
			errors.rejectValue(fieldName, errorCode);
		}
	}

	private void addErrorIfInvalid(Errors errors, String parameter,
			Short selectValue, String fieldName, String errorCode) {
		if (!NumberUtils.isDigits(parameter)
				|| selectValue.equals(Short.valueOf(parameter))) {
			errors.rejectValue(fieldName, errorCode);
		}
	}

	public static CollectionSheetReportParameters build(
			HttpServletRequest request) {
		return new CollectionSheetReportParameters(extractBranchId(request),
				extractLoanOfficerId(request), extractCenterId(request),
				extractMeetingDate(request));
	}

	private static String getParameter(HttpServletRequest request,
			String paramName) {
		String parameter = request.getParameter(paramName);
		return parameter != null ? parameter : request.getAttribute(parameter)
				.toString();
	}

	public void removeRequestParameters(
			ModifiableParameterServletRequest modifiedRequest, Errors errors) {
		for (String reportParam : REPORT_PARAMS_ARRAY) {
			if (errors.getFieldError(reportParam) != null)
				modifiedRequest.removeParameter(reportParam);
		}
	}

	private static String extractMeetingDate(HttpServletRequest request) {
		return getParameter(request, MEETING_DATE_PARAM);
	}

	private static String extractCenterId(HttpServletRequest request) {
		return getParameter(request, CENTER_ID_PARAM);
	}

	private static String extractLoanOfficerId(HttpServletRequest request) {
		return getParameter(request, LOAN_OFFICER_ID_PARAM);
	}

	private static String extractBranchId(HttpServletRequest request) {
		return getParameter(request, BRANCH_ID_PARAM);
	}
}
