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
 
package org.mifos.application.ppi;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.ppi.business.PpiLikelihoodTest;
import org.mifos.application.ppi.business.PpiSurveyInstanceTest;
import org.mifos.application.ppi.business.TestPPIChoice;
import org.mifos.application.ppi.business.TestPPISurvey;
import org.mifos.application.ppi.helpers.TestXmlPPIParser;
import org.mifos.application.ppi.struts.action.TestPPIAction;

public class PPITestSuite extends TestSuite {
	
	public static Test suite() throws Exception {
		TestSuite testSuite = new PPITestSuite();
		testSuite.addTest(TestXmlPPIParser.suite());
		testSuite.addTest(TestPPIChoice.suite());
		testSuite.addTest(TestPPISurvey.suite());
		testSuite.addTestSuite(TestPPIAction.class);
		testSuite.addTest(PpiLikelihoodTest.suite());
		testSuite.addTest(PpiSurveyInstanceTest.suite());
		return testSuite;
	}
}