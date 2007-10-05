package org.mifos.config;


import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.mifos.config.TestLocalization;



public class ConfigTestSuite extends TestSuite {

	public ConfigTestSuite() throws Exception {
	}

	public static void main(String[] args) throws Exception {
		Test testSuite = suite();
		TestRunner.run(testSuite);
	}

	public static Test suite() throws Exception 
	{
		TestSuite suite = new ConfigTestSuite();
		suite.addTest(TestAccountingRules.suite());
		suite.addTestSuite(TestLocalization.class);
		
		return suite;
	}
}
