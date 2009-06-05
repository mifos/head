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

import org.mifos.application.branchreport.persistence.BranchReportPersistenceIntegrationTest;
import org.mifos.application.reports.business.BranchReportParameterFormIntegrationTest;
import org.mifos.application.reports.business.service.BranchReportConfigServiceIntegrationTest;
import org.mifos.application.reports.business.service.BranchReportServiceIntegrationTest;
import org.mifos.application.reports.business.service.ReportProductOfferingServiceIntegrationTest;
import org.mifos.application.reports.business.validator.BranchReportParameterValidatorTest;
import org.mifos.application.reports.business.validator.ReportParameterValidatorFactoryIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.BranchReportClientSummaryHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.BranchReportHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.BranchReportLoanArrearsAgingHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.BranchReportStaffSummaryHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.BranchReportStaffingLevelSummaryHelperIntegrationTest;

public class BranchReportTestSuite extends TestSuite {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(BranchReportHelperIntegrationTest.class);
        suite.addTestSuite(BranchReportServiceIntegrationTest.class);
        suite.addTestSuite(BranchReportPersistenceIntegrationTest.class);
        suite.addTestSuite(BranchReportParameterFormIntegrationTest.class);
        suite.addTestSuite(BranchReportParameterValidatorTest.class);
        suite.addTestSuite(BranchReportClientSummaryHelperIntegrationTest.class);
        suite.addTestSuite(BranchReportLoanArrearsAgingHelperIntegrationTest.class);
        suite.addTestSuite(BranchReportStaffSummaryHelperIntegrationTest.class);
        suite.addTestSuite(BranchReportConfigServiceIntegrationTest.class);
        suite.addTestSuite(ReportProductOfferingServiceIntegrationTest.class);
        suite.addTestSuite(BranchReportStaffingLevelSummaryHelperIntegrationTest.class);
        suite.addTestSuite(BranchReportStaffingLevelSummaryBOTest.class);
        suite.addTestSuite(ReportParameterValidatorFactoryIntegrationTest.class);
        suite.addTest(LoanArrearsAgingPeriodTest.suite());
        return suite;
    }
}
