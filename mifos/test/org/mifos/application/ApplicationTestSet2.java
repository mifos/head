/**

 * ApplicationTestSuite.java    version: xxx



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.application;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.accounts.financial.FinancialTestSuite;
import org.mifos.application.accounts.loan.LoanTestSuite;
import org.mifos.application.bulkentry.BulkEntryTestSuite;
import org.mifos.application.configuration.ApplicationConfigurationTestSuite;
import org.mifos.application.master.MasterTestSuite;
import org.mifos.application.meeting.MeetingTestSuite;
import org.mifos.application.productdefinition.ProductDefinitionTestSuite;
import org.mifos.application.rolesandpermission.RolesAndPermissionTestSuite;
import org.mifos.framework.components.batchjobs.BatchJobTestSuite;
import org.mifos.framework.components.configuration.ConfigurationTestSuite;
import org.mifos.framework.components.fieldConfiguration.FieldConfigurationTestSuite;
import org.mifos.framework.util.helpers.StringToMoneyConverterTest;

public class ApplicationTestSet2 extends TestSuite {

	public ApplicationTestSet2() throws Exception {
	}

	public static void main(String[] args) throws Exception {
		Test testSuite = suite();
		TestRunner.run(testSuite);
	}

	public static Test suite() throws Exception {
		TestSuite suite = new ApplicationTestSet2();

		suite.addTest(ApplicationConfigurationTestSuite.suite());

		suite.addTest(BulkEntryTestSuite.suite());
		suite.addTest(MasterTestSuite.suite());

		suite.addTest(ConfigurationTestSuite.suite());
		suite.addTest(LoanTestSuite.suite());
		suite.addTest(FieldConfigurationTestSuite.suite());  //dependency on ConfigurationTestSuite
		suite.addTest(ProductDefinitionTestSuite.suite()); //dependency on ConfigurationTestSuite
		
		suite.addTest(FinancialTestSuite.suite());
		suite.addTestSuite(StringToMoneyConverterTest.class);
		suite.addTest(BatchJobTestSuite.suite());
		
		suite.addTest(RolesAndPermissionTestSuite.suite());
		suite.addTest(MeetingTestSuite.suite());
		
		return suite;
	}
}

