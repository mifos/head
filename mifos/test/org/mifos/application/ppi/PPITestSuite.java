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