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
 
package org.mifos.application.fees;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.fees.business.FeeBOTest;
import org.mifos.application.fees.business.service.TestFeeBusinessService;
import org.mifos.application.fees.persistence.FeePersistenceTest;
import org.mifos.application.fees.struts.action.FeeActionTest;

public class FeeTestSuite extends TestSuite {

	public FeeTestSuite() {
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
		FeeTestSuite testSuite = new FeeTestSuite();
		testSuite.addTestSuite(FeeActionTest.class);
		testSuite.addTestSuite(FeeBOTest.class);
		testSuite.addTestSuite(FeePersistenceTest.class);
		testSuite.addTestSuite(TestFeeBusinessService.class);
		return testSuite;
	}
}
