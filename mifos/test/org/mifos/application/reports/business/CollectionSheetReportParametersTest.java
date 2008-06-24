package org.mifos.application.reports.business;

import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.application.reports.ui.SelectionItem.NA_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_LOAN_OFFICER_SELECTION_ITEM;

import java.text.ParseException;

import org.mifos.application.reports.util.helpers.ReportValidationConstants;

public class CollectionSheetReportParametersTest extends AbstractReportParametersTest {

	private static final String VALID_REPORT_DATE = "07/03/2007 12:00:00 AM";	
	
	public void testValidatorReturnsErrorIfBranchIdIsSelect() throws Exception {
		reportParams = new CollectionSheetReportParameterForm(String
				.valueOf(SELECT_BRANCH_OFFICE_SELECTION_ITEM
						.getId()), AbstractReportParametersTest.VALID_ID, AbstractReportParametersTest.VALID_ID, VALID_REPORT_DATE);
		errorsMock.rejectValue(ReportValidationConstants.BRANCH_ID_PARAM, ReportValidationConstants.BRANCH_ID_INVALID_MSG);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsLoanOfficerInvalidIfLoanOfficerIsSelect()
			throws Exception {
		reportParams = new CollectionSheetReportParameterForm(AbstractReportParametersTest.VALID_ID, String
				.valueOf(SELECT_LOAN_OFFICER_SELECTION_ITEM
						.getId()), AbstractReportParametersTest.VALID_ID, VALID_REPORT_DATE);
		errorsMock.rejectValue(ReportValidationConstants.LOAN_OFFICER_ID_PARAM, ReportValidationConstants.LOAN_OFFICER_ID_INVALID_MSG);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsLoanOfficerInvalidIfLoanOfficerIsNA()
			throws Exception {
		reportParams = new CollectionSheetReportParameterForm(AbstractReportParametersTest.VALID_ID, String
				.valueOf(NA_LOAN_OFFICER_SELECTION_ITEM
						.getId()), AbstractReportParametersTest.VALID_ID, VALID_REPORT_DATE);
		errorsMock.rejectValue(ReportValidationConstants.LOAN_OFFICER_ID_PARAM, ReportValidationConstants.LOAN_OFFICER_ID_INVALID_MSG);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsCenterInvalidIfCenterIsSelect()
			throws Exception {
		reportParams = new CollectionSheetReportParameterForm(AbstractReportParametersTest.VALID_ID, AbstractReportParametersTest.VALID_ID,
				String.valueOf(SELECT_CENTER_SELECTION_ITEM
						.getId()), VALID_REPORT_DATE);
		errorsMock.rejectValue(ReportValidationConstants.CENTER_ID_PARAM, ReportValidationConstants.CENTER_ID_INVALID_MSG);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsCenterInvalidIfCenterIsNA()
			throws Exception {
		reportParams = new CollectionSheetReportParameterForm(AbstractReportParametersTest.VALID_ID, AbstractReportParametersTest.VALID_ID,
				String.valueOf(NA_CENTER_SELECTION_ITEM
						.getId()), VALID_REPORT_DATE);
		errorsMock.rejectValue(ReportValidationConstants.CENTER_ID_PARAM, ReportValidationConstants.CENTER_ID_INVALID_MSG);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsMeetingDateInvalidIfMeetingDateIsSelect()
			throws Exception {
		reportParams = new CollectionSheetReportParameterForm(AbstractReportParametersTest.VALID_ID, AbstractReportParametersTest.VALID_ID,
				AbstractReportParametersTest.VALID_ID, VALID_REPORT_DATE);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testReportDateFormat() throws Exception {
		try {
			AbstractReportParameterForm.REPORT_DATE_PARAM_FORMAT.parse("01/01/1970 05:30:00 AM");
		}
		catch (ParseException e) {			
			fail("Should be parsing given date");
		}		
	}
}
