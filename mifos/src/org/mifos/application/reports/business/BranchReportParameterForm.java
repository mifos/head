package org.mifos.application.reports.business;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.util.helpers.ReportUtils.reportDatePattern;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.BRANCH_ID_INVALID_MSG;
import static org.mifos.application.reports.util.helpers.ReportValidationConstants.BRANCH_ID_PARAM;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.DateValidator;
import org.mifos.application.reports.business.validator.Errors;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;
import org.mifos.framework.util.helpers.ServletUtils;

public class BranchReportParameterForm extends AbstractReportParameterForm {

	private final String branchId;
	private final String runDate;

	public BranchReportParameterForm(String branchId, String runDate) {
		this.branchId = branchId;
		this.runDate = runDate;
	}

	public void removeRequestParameters(ModifiableParameterServletRequest modifiedRequest, Errors errors) {
		removeRequestParams(modifiedRequest, errors);
	}

	public void validate(Errors errors) {
		addErrorIfInvalid(errors, branchId, SELECT_BRANCH_OFFICE_SELECTION_ITEM
				.getId(), BRANCH_ID_PARAM, BRANCH_ID_INVALID_MSG);
		addErrorIfInvalidRunDate(errors, runDate, ReportValidationConstants.RUN_DATE_PARAM, ReportValidationConstants.RUN_DATE_INVALID_MSG);
	}
	

	private void addErrorIfInvalidRunDate(Errors errors, String runDate, String fieldName, String errorCode) {
		if(!DateValidator.getInstance().isValid(runDate, reportDatePattern(), false))
			errors.rejectValue(fieldName, errorCode);
	}

	public static BranchReportParameterForm build(HttpServletRequest request) {
	    return new BranchReportParameterForm(extractBranchId(request), extractRunDate(request));	
	}
	
	private static String extractRunDate(HttpServletRequest request) {
		return ServletUtils.getParameter(request, ReportValidationConstants.RUN_DATE_PARAM);
	}

	public boolean isFormEmpty() {
		return branchId == null && runDate == null;
	}

	@Override
	public String[] getAllFormParameterNames() {
		return ReportValidationConstants.BRANCH_REPORT_PARAMS_ARRAY;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getRunDate() {
		return runDate;
	}
}
