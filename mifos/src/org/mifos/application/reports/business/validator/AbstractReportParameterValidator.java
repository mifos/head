package org.mifos.application.reports.business.validator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.mifos.application.reports.business.ReportParameterForm;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;

public abstract class AbstractReportParameterValidator<T extends ReportParameterForm>
		implements ReportParameterValidator<T> {

	private List<String> applicableFilePaths;

	public AbstractReportParameterValidator(List<String> applicableFilePaths) {
		this.applicableFilePaths = (List<String>) CollectionUtils.collect(
				applicableFilePaths, new Transformer() {
					public Object transform(Object input) {
						return ((String) input).trim();
					}
				});
	}

	public void removeRequestParameters(
			ModifiableParameterServletRequest modifiedRequest, T form,
			Errors errors) {
		form.removeRequestParameters(modifiedRequest, errors);
	}

	public void validate(T form, Errors errors) {
		form.validate(errors);
	}

	public boolean isAFreshRequest(HttpServletRequest request) {
		return buildReportParameterForm(request).isFormEmpty();
	}

	public boolean isApplicableToReportFilePath(String reportFilePath) {
		return applicableFilePaths.contains(reportFilePath);
	}
}
