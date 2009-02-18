/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
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
		suite.addTest(LoanArrearsAgingPeriodTest.suite());
		return suite;
	}
}
