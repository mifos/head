package org.mifos.application.reports.business.validator;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.DetailedAgingPortfolioReportParameters;
import org.mifos.application.reports.business.ReportParameterForm;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;

public class DetailedAgingPortfolioReportParamValidator  implements
		ReportParameterValidator<DetailedAgingPortfolioReportParameters> {

	public static final ReportParameterValidator INSTANCE = new DetailedAgingPortfolioReportParamValidator();
	
	public DetailedAgingPortfolioReportParameters buildReportParameterForm(HttpServletRequest request) {
		return DetailedAgingPortfolioReportParameters.build(request);
	}

	public void removeRequestParameters(ModifiableParameterServletRequest modifiedRequest, ReportParameterForm form, Errors errors) {
		form.removeRequestParameters(modifiedRequest, errors);
	}

	public void validate(DetailedAgingPortfolioReportParameters target, Errors errors) {
		target.validate(errors);
	}
}
