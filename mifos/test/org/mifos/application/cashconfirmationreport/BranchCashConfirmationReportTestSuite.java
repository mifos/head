package org.mifos.application.cashconfirmationreport;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.reports.business.service.BranchCashConfirmationConfigServiceTest;
import org.mifos.application.reports.business.service.BranchCashConfirmationReportServiceTest;
//import org.mifos.framework.components.batchjobs.helpers.BranchCashConfirmationReportHelperTest;
import org.mifos.report.branchcashconfirmation.persistence.BranchCashConfirmationReportPersistenceTest;

public class BranchCashConfirmationReportTestSuite extends TestSuite {
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite();
//		suite.addTestSuite(BranchCashConfirmationReportHelperTest.class);
		suite.addTestSuite(BranchCashConfirmationConfigServiceTest.class);
		suite.addTestSuite(BranchCashConfirmationReportPersistenceTest.class);
		suite.addTestSuite(BranchCashConfirmationReportServiceTest.class);
		return suite;
	}
}
