package org.mifos.application.reports.business.validator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.JdbcBranchCashConfirmationReportParameterForm;

public class JdbcBranchCashConfirmationReportParamValidator extends
		AbstractReportParameterValidator<JdbcBranchCashConfirmationReportParameterForm> {
	
	
	public JdbcBranchCashConfirmationReportParamValidator(
			List<String> applicableReportFilePaths) {
		super(applicableReportFilePaths);
	}

	public JdbcBranchCashConfirmationReportParameterForm buildReportParameterForm(
			HttpServletRequest request) {
		return JdbcBranchCashConfirmationReportParameterForm.build(request);
	}

}
