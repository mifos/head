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
			Integer invalidValue, String fieldName, String errorCode) {
		if (!NumberUtils.isDigits(input)
				|| invalidValue.equals(Integer.valueOf(input))) {
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
