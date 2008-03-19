package org.mifos.application.reports.business.validator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.BranchReportParameterForm;
import org.mifos.application.reports.business.service.BranchCashConfirmationReportService;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;

public class BranchCashConfirmationReportParamValidator extends
		AbstractReportParameterValidator<BranchReportParameterForm> {

	private final BranchCashConfirmationReportService service;

	public BranchCashConfirmationReportParamValidator(
			List<String> applicableFilePaths,
			BranchCashConfirmationReportService service) {
		super(applicableFilePaths);
		this.service = service;
	}

	public BranchReportParameterForm buildReportParameterForm(
			HttpServletRequest request) {
		return BranchReportParameterForm.build(request);
	}

	@Override
	public void validate(BranchReportParameterForm form, Errors errors) {
		super.validate(form, errors);
		if (errors.hasErrors())
			return;
		if (!service.isReportDataPresentForRundateAndBranchId(form
				.getBranchId(), form.getRunDate())) {
			errors.rejectValue(ReportValidationConstants.RUN_DATE_PARAM,
					ReportValidationConstants.BRANCH_REPORT_NO_DATA_FOUND_MSG);
		}
	}
}
