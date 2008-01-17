package org.mifos.application.reports.business.validator;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.CollectionSheetReportParameters;
import org.mifos.application.reports.business.ReportParameterForm;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;


public class CollectionSheetReportParamValidator implements
		ReportParameterValidator<CollectionSheetReportParameters> {
	public static final ReportParameterValidator INSTANCE = new CollectionSheetReportParamValidator();

	public void validate(CollectionSheetReportParameters target, Errors errors) {
		target.validate(errors);
	}

	public CollectionSheetReportParameters buildReportParameterForm(
			HttpServletRequest request) {
		return CollectionSheetReportParameters.build(request);
	}

	public void removeRequestParameters(ModifiableParameterServletRequest modifiedRequest, ReportParameterForm form, Errors errors) {
		form.removeRequestParameters(modifiedRequest, errors);
	}
}
