package org.mifos.application.reports.business.validator;

import static org.easymock.classextension.EasyMock.*;

import java.util.ArrayList;

import org.mifos.application.reports.business.BranchReportParameterForm;
import org.mifos.application.reports.business.service.BranchReportService;
import org.mifos.application.reports.business.service.IBranchReportService;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;

import junit.framework.TestCase;

public class BranchReportParameterValidatorTest extends TestCase {
	private static final String BRANCH_ID = "2";
	private static final String VALID_RUN_DATE = "01/01/2008";
	private static final String INVALID_BRANCH_ID = "-2";
	private IBranchReportService branchReportServiceMock;
	private BranchReportParamValidator validator;
	private Errors errors;
	private BranchReportParameterForm validForm;
	private BranchReportParameterForm invalidForm;

	public void testValidatorCallsServiceIfDataPresent() throws Exception {
		expect(
				branchReportServiceMock
						.isReportDataPresentForRundateAndBranchId(BRANCH_ID,
								VALID_RUN_DATE)).andReturn(Boolean.TRUE);
		replay(branchReportServiceMock);

		Errors errors = new Errors(null);
		validator.validate(new BranchReportParameterForm(BRANCH_ID,
				VALID_RUN_DATE), errors);
		verify(branchReportServiceMock);
		assertFalse(errors.hasErrors());
	}

	public void testValidatorAddsErrorIfServiceSaysNoDataFound()
			throws Exception {
		expect(
				branchReportServiceMock
						.isReportDataPresentForRundateAndBranchId(BRANCH_ID,
								VALID_RUN_DATE)).andReturn(Boolean.FALSE);
		replay(branchReportServiceMock);
		validator.validate(validForm, errors);
		verify(branchReportServiceMock);
		assertTrue(errors.hasErrors());
		ErrorEntry fieldError = errors
				.getFieldError(ReportValidationConstants.RUN_DATE_PARAM);
		assertNotNull(fieldError);
		assertEquals(ReportValidationConstants.BRANCH_REPORT_NO_DATA_FOUND_MSG,
				fieldError.getErrorCode());
	}

	public void testValidatorDoesNotCheckDataPresenceIfErrorsExists()
			throws Exception {
		replay(branchReportServiceMock);
		validator.validate(invalidForm, errors);
		verify(branchReportServiceMock);
	}

	@Override
	protected void setUp() throws Exception {
		branchReportServiceMock = createMock(BranchReportService.class);
		validator = new BranchReportParamValidator(new ArrayList<String>(), branchReportServiceMock);
		errors = new Errors(null);
		validForm = new BranchReportParameterForm(BRANCH_ID, VALID_RUN_DATE);
		invalidForm = new BranchReportParameterForm(INVALID_BRANCH_ID, VALID_RUN_DATE);
	}
}
