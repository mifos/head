package org.mifos.application.reports.business;

import org.mifos.application.reports.business.validator.Errors;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;

public interface ReportParameterForm {
	public void validate(Errors errors);

	public void removeRequestParameters(ModifiableParameterServletRequest modifiedRequest, Errors errors);
}
