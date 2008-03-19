package org.mifos.application.reports.business.validator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.CollectionSheetReportParameterForm;


public class CollectionSheetReportParamValidator extends
		AbstractReportParameterValidator<CollectionSheetReportParameterForm> {

	public CollectionSheetReportParamValidator(
			List<String> applicableReportFilePaths) {
		super(applicableReportFilePaths);
	}

	public CollectionSheetReportParameterForm buildReportParameterForm(
			HttpServletRequest request) {
		return CollectionSheetReportParameterForm.build(request);
	}

}
