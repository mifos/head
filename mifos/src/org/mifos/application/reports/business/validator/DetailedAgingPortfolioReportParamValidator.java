package org.mifos.application.reports.business.validator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.DetailedAgingPortfolioReportParameters;

public class DetailedAgingPortfolioReportParamValidator
		extends
		AbstractReportParameterValidator<DetailedAgingPortfolioReportParameters> {

	public DetailedAgingPortfolioReportParamValidator(
			List<String> applicableReportFilePaths) {
		super(applicableReportFilePaths);
	}

	public DetailedAgingPortfolioReportParameters buildReportParameterForm(
			HttpServletRequest request) {
		return DetailedAgingPortfolioReportParameters.build(request);
	}
}
