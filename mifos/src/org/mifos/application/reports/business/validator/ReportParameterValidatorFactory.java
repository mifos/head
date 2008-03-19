package org.mifos.application.reports.business.validator;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.mifos.application.reports.business.ReportParameterForm;
import org.mifos.framework.util.helpers.FilePaths;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ReportParameterValidatorFactory {
	private static Collection validators;

	public  ReportParameterValidator<ReportParameterForm> getValidator(
			final String reportFilePath) {
		initValidators();
		return (ReportParameterValidator<ReportParameterForm>)CollectionUtils.find(validators, new Predicate() {
			public boolean evaluate(Object validator) {
				return ((ReportParameterValidator<ReportParameterForm>) validator)
				.isApplicableToReportFilePath(reportFilePath);
			}
		});
	}
	
	private void initValidators() {
		if (validators == null) {
			validators = new ClassPathXmlApplicationContext(
					FilePaths.REPORT_PARAMETER_VALIDATOR_CONFIG)
								.getBeansOfType(ReportParameterValidator.class).values();
		}
	}	
}
