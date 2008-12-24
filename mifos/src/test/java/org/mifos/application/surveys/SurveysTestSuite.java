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
		testSuite.addTestSuite(SurveysPersistenceTest.class);
		return testSuite;
	}
}