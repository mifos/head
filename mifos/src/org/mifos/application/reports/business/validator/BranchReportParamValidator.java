package org.mifos.application.reports.business.validator;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.mifos.application.reports.business.BranchReportParameterForm;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;

public class BranchReportParamValidator extends
		AbstractReportParameterValidator<BranchReportParameterForm> {
	private final IBranchReportService branchReportService;

	public BranchReportParamValidator(List<String> applicableReportFilePaths,
			IBranchReportService branchReportService) {
		super(applicableReportFilePaths);
		this.branchReportService = branchReportService;
	}

	@Override
	public void validate(BranchReportParameterForm form, Errors errors) {
		super.validate(form, errors);
		if (errors.hasErrors())
			return;
		if (!branchReportService.isReportDataPresentForRundateAndBranchId(form
				.getBranchId(), form.getRunDate())) {
			errors.rejectValue(ReportValidationConstants.RUN_DATE_PARAM,
					ReportValidationConstants.BRANCH_REPORT_NO_DATA_FOUND_MSG);
		}
	}

	public BranchReportParameterForm buildReportParameterForm(
			HttpServletRequest request) {
		return BranchReportParameterForm.build(request);
	}
}
