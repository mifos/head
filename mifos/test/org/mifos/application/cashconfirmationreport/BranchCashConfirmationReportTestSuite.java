/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
