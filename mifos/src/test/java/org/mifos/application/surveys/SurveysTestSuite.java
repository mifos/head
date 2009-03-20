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
 
package org.mifos.application.surveys;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.surveys.business.TestSurvey;
import org.mifos.application.surveys.persistence.SurveysPersistenceTest;
import org.mifos.application.surveys.struts.action.TestQuestionsAction;
import org.mifos.application.surveys.struts.action.TestSurveyInstanceAction;
import org.mifos.application.surveys.struts.action.TestSurveysAction;
import org.mifos.application.surveys.struts.tag.QuestionTagTest;

public class SurveysTestSuite extends TestSuite {
	
	public static Test suite() throws Exception {
		TestSuite testSuite = new SurveysTestSuite();
		testSuite.addTestSuite(TestSurvey.class);
		testSuite.addTestSuite(TestSurveysAction.class);
		testSuite.addTestSuite(TestQuestionsAction.class);
		testSuite.addTestSuite(TestSurveyInstanceAction.class);
		testSuite.addTestSuite(QuestionTagTest.class);
		testSuite.addTest(SurveysPersistenceTest.suite());
		return testSuite;
	}
}