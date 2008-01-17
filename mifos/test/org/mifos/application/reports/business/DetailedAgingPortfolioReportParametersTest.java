package org.mifos.application.reports.business;

import org.mifos.application.reports.business.validator.Errors;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import junit.framework.TestCase;

public class DetailedAgingPortfolioReportParametersTest extends TestCase {

	private Errors errorsMock;

	public void testValidatorNoErrorIfAllParamsValid() throws Exception {
		DetailedAgingPortfolioReportParameters reportParameters = new DetailedAgingPortfolioReportParameters(
				DetailedAgingPortfolioReportParameters.VALID_ID,
				DetailedAgingPortfolioReportParameters.VALID_ID,
				DetailedAgingPortfolioReportParameters.VALID_ID);
		replay(errorsMock);
		reportParameters.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsErrorIfBranchIdIsSelect() throws Exception {
		DetailedAgingPortfolioReportParameters reportParameters = new DetailedAgingPortfolioReportParameters(
				DetailedAgingPortfolioReportParameters.INVALID_ID,
				DetailedAgingPortfolioReportParameters.VALID_ID,
				DetailedAgingPortfolioReportParameters.VALID_ID);
		errorsMock.rejectValue(DetailedAgingPortfolioReportParameters.BRANCH_ID_PARAM, 
				DetailedAgingPortfolioReportParameters.BRANCH_ID_INVALID_MSG);
		replay(errorsMock);
		reportParameters.validate(errorsMock);
		verify(errorsMock);
	}
	
	public void testValidatorReturnsErrorIfLoanOfficerIsSelect() throws Exception {
		DetailedAgingPortfolioReportParameters reportParameters = new DetailedAgingPortfolioReportParameters(
				DetailedAgingPortfolioReportParameters.VALID_ID,
				DetailedAgingPortfolioReportParameters.INVALID_ID,
				DetailedAgingPortfolioReportParameters.VALID_ID);
		errorsMock.rejectValue(DetailedAgingPortfolioReportParameters.LOAN_OFFICER_ID_PARAM, 
				DetailedAgingPortfolioReportParameters.LOAN_OFFICER_ID_INVALID_MSG);
		replay(errorsMock);
		reportParameters.validate(errorsMock);
		verify(errorsMock);
	}
	
	public void testValidatorReturnsErrorIfLoanProductIsSelect() throws Exception {
		DetailedAgingPortfolioReportParameters reportParameters = new DetailedAgingPortfolioReportParameters(
				DetailedAgingPortfolioReportParameters.VALID_ID,
				DetailedAgingPortfolioReportParameters.VALID_ID,
				DetailedAgingPortfolioReportParameters.INVALID_ID);
		errorsMock.rejectValue(DetailedAgingPortfolioReportParameters.LOAN_PRODUCT_ID,
				DetailedAgingPortfolioReportParameters.LOAN_PRODUCT_ID_INVALID_MSG);
		replay(errorsMock);
		reportParameters.validate(errorsMock);
		verify(errorsMock);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		errorsMock = createMock(Errors.class);
	}
}