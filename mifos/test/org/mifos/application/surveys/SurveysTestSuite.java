package org.mifos.application.surveys;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.surveys.business.TestSurvey;

public class SurveysTestSuite extends TestSuite {
	
	public static Test suite() throws Exception {
		TestSuite testSuite = new SurveysTestSuite();
		testSuite.addTestSuite(TestSurvey.class);
		return testSuite;
	}

}
