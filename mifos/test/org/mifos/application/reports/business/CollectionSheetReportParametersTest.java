package org.mifos.application.reports.business;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.application.reports.ui.SelectionItem.NA_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.NA_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_CENTER_SELECTION_ITEM;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_LOAN_OFFICER_SELECTION_ITEM;

import java.text.ParseException;

import org.mifos.application.reports.business.validator.Errors;
import org.mifos.framework.MifosTestCase;


public class CollectionSheetReportParametersTest extends MifosTestCase {

	private static final String VALID_DATE = "07/03/2007 12:00:00 AM";
	private static final String VALID_ID = "0";
	private CollectionSheetReportParameters reportParams;
	private Errors errorsMock;

	public void testValidatorReturnsErrorIfBranchIdIsSelect() throws Exception {
		reportParams = new CollectionSheetReportParameters(String
				.valueOf(SELECT_BRANCH_OFFICE_SELECTION_ITEM
						.getId()), VALID_ID, VALID_ID, VALID_DATE);
		errorsMock.rejectValue("branchId", "branchId.invalid");
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsLoanOfficerInvalidIfLoanOfficerIsSelect()
			throws Exception {
		reportParams = new CollectionSheetReportParameters(VALID_ID, String
				.valueOf(SELECT_LOAN_OFFICER_SELECTION_ITEM
						.getId()), VALID_ID, VALID_DATE);
		errorsMock.rejectValue("loanOfficerId", "loanOfficerId.invalid");
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsLoanOfficerInvalidIfLoanOfficerIsNA()
			throws Exception {
		reportParams = new CollectionSheetReportParameters(VALID_ID, String
				.valueOf(NA_LOAN_OFFICER_SELECTION_ITEM
						.getId()), VALID_ID, VALID_DATE);
		errorsMock.rejectValue("loanOfficerId", "loanOfficerId.invalid");
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsCenterInvalidIfCenterIsSelect()
			throws Exception {
		reportParams = new CollectionSheetReportParameters(VALID_ID, VALID_ID,
				String.valueOf(SELECT_CENTER_SELECTION_ITEM
						.getId()), VALID_DATE);
		errorsMock.rejectValue("centerId", "centerId.invalid");
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsCenterInvalidIfCenterIsNA()
			throws Exception {
		reportParams = new CollectionSheetReportParameters(VALID_ID, VALID_ID,
				String.valueOf(NA_CENTER_SELECTION_ITEM
						.getId()), VALID_DATE);
		errorsMock.rejectValue("centerId", "centerId.invalid");
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsMeetingDateInvalidIfMeetingDateIsSelect()
			throws Exception {
		reportParams = new CollectionSheetReportParameters(VALID_ID, VALID_ID,
				VALID_ID, VALID_DATE);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testReportDateFormat() throws Exception {
		try {
			CollectionSheetReportParameters.REPORT_DATE_PARAM_FORMAT
					.parse("01/01/1970 05:30:00 AM");
		}
		catch (ParseException e) {			
			fail("Should be parsing given date");
		}		
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		errorsMock = createMock(Errors.class);
	}
}
