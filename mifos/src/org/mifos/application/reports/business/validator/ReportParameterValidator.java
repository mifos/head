package org.mifos.application.reports.business.validator;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.ReportParameterForm;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;

public interface ReportParameterValidator<T extends ReportParameterForm> {
	public void validate(T target, Errors errors);
	public T buildReportParameterForm(HttpServletRequest request);
	public void removeRequestParameters(ModifiableParameterServletRequest modifiedRequest, ReportParameterForm form, Errors errors);
}
