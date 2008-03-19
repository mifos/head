package org.mifos.application.reports.business.validator;

import org.mifos.application.reports.business.ReportParameterForm;
import org.mifos.framework.MifosTestCase;

public class ReportParameterValidatorFactoryTest extends MifosTestCase {

	private static final String DETAILED_AGING_PORTFOLIO_REPORT_FILENAME = "report/DetailedAgingPortfolioAtRisk.rptdesign";
	private static final String COLLECTION_SHEET_REPORT_FILENAME = "report/CollectionSheetReport.rptdesign";
	private static final String ACTIVE_LOANS_BY_LOAN_OFFICER_REPORT_FILENAME = "report/ActiveLoansByLoanOfficer.rptdesign";
	private static final String BRANCH_REPORT_FILENAME = "report/BranchReport.rptdesign";
	private static final String BRANCH_CASH_CONFIRMATION_REPORT_FILENAME = "report/BranchCashConfirmationReport.rptdesign";

	public void testReturnsNullIfValidatorNotFound() throws Exception {
		ReportParameterValidator<ReportParameterForm> validator = new ReportParameterValidatorFactory()
				.getValidator("not-existing-reportname");
		assertNull(validator);
	}

	public void testReturnsCollectionSheetReportValidator()
			throws Exception {
		retrieveAndAssertValidatorType(COLLECTION_SHEET_REPORT_FILENAME,
				CollectionSheetReportParamValidator.class);
	}

	public void testReturnsDetailedAgingPortfolioValidator()
			throws Exception {
		retrieveAndAssertValidatorType(
				DETAILED_AGING_PORTFOLIO_REPORT_FILENAME,
				DetailedAgingPortfolioReportParamValidator.class);
	}

	public void testReturnsActiveLoansByLoanOfficerValidator()
			throws Exception {
		retrieveAndAssertValidatorType(
				ACTIVE_LOANS_BY_LOAN_OFFICER_REPORT_FILENAME,
				DetailedAgingPortfolioReportParamValidator.class);
	}

	public void testReturnsBranchReportValidator() throws Exception {
		retrieveAndAssertValidatorType(BRANCH_REPORT_FILENAME,
				BranchReportParamValidator.class);
	}

	public void testReturnsBranchCashConfirmationReportValidator() throws Exception {
		retrieveAndAssertValidatorType(BRANCH_CASH_CONFIRMATION_REPORT_FILENAME,
				BranchCashConfirmationReportParamValidator.class);
	}

	private void retrieveAndAssertValidatorType(String reportFilename,
			Class validatorClass) {
		ReportParameterValidator<ReportParameterForm> validator = new ReportParameterValidatorFactory()
				.getValidator(reportFilename);
		assertNotNull("Validator not found for " + reportFilename, validator);
		assertEquals(validatorClass, validator.getClass());
	}
}
