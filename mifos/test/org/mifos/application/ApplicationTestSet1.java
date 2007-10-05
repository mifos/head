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

import org.mifos.application.accounts.savings.SavingsTestSuite;
import org.mifos.application.collectionsheet.CollectionSheetTestSuite;
import org.mifos.application.fees.FeeTestSuite;
import org.mifos.application.office.OfficeTestSuite;
import org.mifos.application.productsmix.ProductMixTestSuite;
import org.mifos.application.reports.ReportsTestSuite;
import org.mifos.config.ConfigTestSuite;
import org.mifos.config.TestConfigurationManager;
import org.mifos.framework.components.ComponentsTestSuite;
import org.mifos.framework.security.SecurityTestSuite;


public class ApplicationTestSet1 extends TestSuite {

	public ApplicationTestSet1() throws Exception {
	}

	public static void main(String[] args) throws Exception {
		Test testSuite = suite();
		TestRunner.run(testSuite);
	}

	public static Test suite() throws Exception {
		TestSuite suite = new ApplicationTestSet1();
		suite.addTest(ConfigTestSuite.suite());
		// Put fast tests at the top for quick feedback if they fail
		suite.addTest(TestConfigurationManager.suite());
		
		suite.addTest(ProductMixTestSuite.suite());
		suite.addTest(FastTests.suite());
		suite.addTest(SecurityTestSuite.suite());
		suite.addTest(CollectionSheetTestSuite.suite());

		suite.addTest(SavingsTestSuite.suite());
		suite.addTest(ReportsTestSuite.suite());
		suite.addTest(FeeTestSuite.suite());
		suite.addTest(OfficeTestSuite.suite());

		suite.addTest(ComponentsTestSuite.suite());
		
		
		return suite;
	}
}

