package org.mifos.application.surveys;

import org.mifos.application.surveys.business.TestSurvey;

import junit.framework.TestSuite;
import junit.framework.Test;

public class SurveysTestSuite extends TestSuite {
	
	public static Test suite() throws Exception {
		TestSuite testSuite = new SurveysTestSuite();
		testSuite.addTestSuite(TestSurvey.class);
		return testSuite;
	}

}
