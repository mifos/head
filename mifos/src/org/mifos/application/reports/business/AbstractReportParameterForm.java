package org.mifos.application.reports.business;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.validator.Errors;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;
import org.mifos.framework.util.helpers.NumberUtils;
import org.mifos.framework.util.helpers.ServletUtils;


public abstract class AbstractReportParameterForm implements
		ReportParameterForm {

	public static final SimpleDateFormat REPORT_DATE_PARAM_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy hh:mm:ss a");

	public abstract String[] getAllFormParameterNames();
	
	protected void addErrorIfInvalid(Errors errors, String input,
			Short invalidValue, String fieldName, String errorCode) {
		if (!NumberUtils.isDigits(input)
				|| invalidValue.equals(Short.valueOf(input))) {
			errors.rejectValue(fieldName, errorCode);
		}
	}

	protected void addErrorIfInvalid(Errors errors, String meetingDate,
			Date notAvailableDate, String fieldName, String errorCode) {
		Date date = parseDate(meetingDate, REPORT_DATE_PARAM_FORMAT);
		if (notAvailableDate.equals(date)) {
			errors.rejectValue(fieldName, errorCode);
		}
	}

	protected Date parseDate(String dateParam, DateFormat dateFormat) {
		Date date = null;
		try {
			date = dateFormat.parse(dateParam);
		}
		catch (ParseException e) {
		}
		return date;
	}

	protected void removeRequestParams(
			ModifiableParameterServletRequest modifiedRequest, Errors errors) {
		for (String reportParam : getAllFormParameterNames()) {
			if (errors.getFieldError(reportParam) != null)
				modifiedRequest.removeParameter(reportParam);
		}
	}

	protected static String extractBranchId(HttpServletRequest request) {
		return ServletUtils.getParameter(request, ReportValidationConstants.BRANCH_ID_PARAM);
	}
}
