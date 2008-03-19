package org.mifos.application.branchreport;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.branchreport.persistence.BranchReportPersistenceTest;
import org.mifos.application.reports.business.BranchReportParameterFormTest;
import org.mifos.application.reports.business.service.BranchReportConfigServiceTest;
import org.mifos.application.reports.business.service.BranchReportServiceTest;
import org.mifos.application.reports.business.service.ReportProductOfferingServiceTest;
import org.mifos.application.reports.business.validator.BranchReportParameterValidatorTest;
import org.mifos.application.reports.business.validator.ReportParameterValidatorFactoryTest;
import org.mifos.framework.components.batchjobs.helpers.BranchReportClientSummaryHelperTest;
import org.mifos.framework.components.batchjobs.helpers.BranchReportHelperTest;
import org.mifos.framework.components.batchjobs.helpers.BranchReportLoanArrearsAgingHelperTest;
import org.mifos.framework.components.batchjobs.helpers.BranchReportStaffSummaryHelperTest;
import org.mifos.framework.components.batchjobs.helpers.BranchReportStaffingLevelSummaryHelperTest;

public class BranchReportTestSuite extends TestSuite {
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(BranchReportHelperTest.class);
		suite.addTestSuite(BranchReportServiceTest.class);
		suite.addTestSuite(BranchReportPersistenceTest.class);
		suite.addTestSuite(BranchReportParameterFormTest.class);
		suite.addTestSuite(BranchReportParameterValidatorTest.class);
		suite.addTestSuite(BranchReportClientSummaryHelperTest.class);
		suite.addTestSuite(BranchReportLoanArrearsAgingHelperTest.class);
		suite.addTestSuite(BranchReportStaffSummaryHelperTest.class);
		suite.addTestSuite(BranchReportConfigServiceTest.class);
		suite.addTestSuite(ReportProductOfferingServiceTest.class);
		suite.addTestSuite(BranchReportStaffingLevelSummaryHelperTest.class);
		suite.addTestSuite(BranchReportStaffingLevelSummaryBOTest.class);
		suite.addTestSuite(ReportParameterValidatorFactoryTest.class);
		return suite;
	}
}
