package org.mifos.application.reports.business.validator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.reports.business.CollectionSheetReportParameterForm;
import org.mifos.application.reports.business.SqlCollectionSheetReportParameterForm;

public class SqlCollectionSheetReportParamValidator extends
		AbstractReportParameterValidator<SqlCollectionSheetReportParameterForm> {

	public SqlCollectionSheetReportParamValidator(
			List<String> applicableReportFilePaths) {
		super(applicableReportFilePaths);
	}

	public SqlCollectionSheetReportParameterForm buildReportParameterForm(
			HttpServletRequest request) {
		return SqlCollectionSheetReportParameterForm.build(request);
	}

}
