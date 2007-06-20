package org.mifos.application.configuration;


import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.configuration.struts.action.LookupOptionsActionTest;


public class LookupOptionsTestSuite extends TestSuite {
	
	public static void main (String[] args)
	{
		Test testSuite = suite();
		TestRunner.run (testSuite);
	}
	

	public static Test suite()
	{
		TestSuite testSuite = new LookupOptionsTestSuite();
		testSuite.addTestSuite(LookupOptionsActionTest.class);
		return testSuite;
		
	}
}

