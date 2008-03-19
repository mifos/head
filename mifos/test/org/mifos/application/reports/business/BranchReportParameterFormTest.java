package org.mifos.application.reports.business;

import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;

import org.mifos.application.reports.util.helpers.ReportValidationConstants;

public class BranchReportParameterFormTest extends AbstractReportParametersTest {

	private static final String VALID_BRANCH_REPORT_DATE = "21/01/2007";
	private static final String INVALID_BRANCH_REPORT_DATE = "01/21/2007";

	public void testValidatorReturnsErrorIfBranchIdIsSelect() throws Exception {
		reportParams = new BranchReportParameterForm(String
				.valueOf(SELECT_BRANCH_OFFICE_SELECTION_ITEM.getId()),
				VALID_BRANCH_REPORT_DATE);
		errorsMock.rejectValue(ReportValidationConstants.BRANCH_ID_PARAM,
				ReportValidationConstants.BRANCH_ID_INVALID_MSG);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsErrorIfReportDateIsEmpty() throws Exception {
		reportParams = new BranchReportParameterForm(VALID_ID, "");
		errorsMock.rejectValue(ReportValidationConstants.RUN_DATE_PARAM,
				ReportValidationConstants.RUN_DATE_INVALID_MSG);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}
	
	public void testReportDateFormatIsInvalid() throws Exception {
		reportParams = new BranchReportParameterForm(VALID_ID, INVALID_BRANCH_REPORT_DATE);
		errorsMock.rejectValue(ReportValidationConstants.RUN_DATE_PARAM,
				ReportValidationConstants.RUN_DATE_INVALID_MSG);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);		
	}
	
	public void testReportDateFormatIsValid() throws Exception {
		reportParams = new BranchReportParameterForm(VALID_ID, VALID_BRANCH_REPORT_DATE);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

}
