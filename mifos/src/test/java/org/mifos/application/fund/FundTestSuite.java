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
 
package org.mifos.application.fund;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.fund.business.FundBOIntegrationTest;
import org.mifos.application.fund.business.service.FundBusinessServiceIntegrationTest;
import org.mifos.application.fund.persistence.FundPersistenceIntegrationTest;
import org.mifos.application.fund.struts.action.FundActionTest;

public class FundTestSuite extends TestSuite{
	public FundTestSuite() {
		super();
	}

	public static void main(String[] args) {
		try {
			Test testSuite = suite();
			TestRunner.run(testSuite);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Test suite() throws Exception {
		FundTestSuite testSuite = new FundTestSuite();
		testSuite.addTestSuite(FundBOIntegrationTest.class);
		testSuite.addTestSuite(FundActionTest.class);
		testSuite.addTestSuite(FundBusinessServiceIntegrationTest.class);
		testSuite.addTestSuite(FundPersistenceIntegrationTest.class);
		return testSuite;
	}
}
