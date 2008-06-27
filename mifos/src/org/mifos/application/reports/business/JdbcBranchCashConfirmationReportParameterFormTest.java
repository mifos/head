package org.mifos.application.reports.business;

import static org.junit.Assert.*;

import java.util.Locale;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.junit.Test;
import org.mifos.application.reports.business.validator.Errors;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;

public class JdbcBranchCashConfirmationReportParameterFormTest {

	@Test
	public void shouldAcceptValidBranchIdAndDate() {
		JdbcBranchCashConfirmationReportParameterForm form = new JdbcBranchCashConfirmationReportParameterForm(
				"1", "26/06/2008");
		Errors errorsMock = createMock(Errors.class);
		replay(errorsMock);
		form.validate(errorsMock);
		verify(errorsMock);

	}

	@Test
	public void shouldReportErrorIfBranchIdAndDateAreInvalid() throws Exception {

		JdbcBranchCashConfirmationReportParameterForm form = new JdbcBranchCashConfirmationReportParameterForm(
				"-2", "262008");
		Errors errors = new Errors(Locale.ENGLISH);
		String branchInvalidErrorCode = getErrorCode(errors,
				ReportValidationConstants.BRANCH_ID_PARAM);
		String dateInvalidErrorCode = getErrorCode(errors,
				ReportValidationConstants.RUN_DATE_PARAM_FOR_CASH_CONF_REPORT);
		
		form.validate(errors);
		
		assertTrue(errors.hasErrors());
		assertEquals(ReportValidationConstants.BRANCH_ID_INVALID_MSG,
				branchInvalidErrorCode);
		assertEquals(ReportValidationConstants.RUN_DATE_INVALID_MSG,
				dateInvalidErrorCode);

	}


	private String getErrorCode(Errors errors, String fieldName) {
		return errors.getFieldError(fieldName).getErrorCode();
	}

}
