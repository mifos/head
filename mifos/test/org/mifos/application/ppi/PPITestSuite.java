package org.mifos.application.ppi;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.ppi.business.PpiLikelihoodChartTest;
import org.mifos.application.ppi.business.PpiLikelihoodTest;
import org.mifos.application.ppi.business.TestPPIChoice;
import org.mifos.application.ppi.business.TestPPISurvey;
import org.mifos.application.ppi.helpers.TestXmlPPIParser;
import org.mifos.application.ppi.struts.action.TestPPIAction;

public class PPITestSuite extends TestSuite {
	
	public static Test suite() throws Exception {
		TestSuite testSuite = new PPITestSuite();
		testSuite.addTestSuite(TestXmlPPIParser.class);
		testSuite.addTest(TestPPIChoice.suite());
		testSuite.addTest(TestPPISurvey.suite());
		testSuite.addTestSuite(TestPPIAction.class);
		testSuite.addTest(PpiLikelihoodChartTest.suite());
		testSuite.addTest(PpiLikelihoodTest.suite());
		return testSuite;
	}
}