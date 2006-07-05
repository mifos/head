package org.mifos.framework;

import org.mifos.framework.business.handlers.BusinessProcessorTest;
import org.mifos.framework.dao.DAOTest;
import org.mifos.framework.struts.action.MifosBaseActionTest;

import junit.framework.*;
import junit.textui.TestRunner;


public class FrameworkTestSuite extends MifosTestCase
{
	public static void main (String[] args)
	{
		Test testSuite = suite();
		
		TestRunner.run (testSuite);
		
		

	}
	

	public static Test suite()
	{
		TestSuite testSuite = new TestSuite();
		testSuite.addTestSuite(MifosBaseActionTest.class);
		testSuite.addTestSuite(BusinessProcessorTest.class);
		testSuite.addTestSuite(DAOTest.class);
			
		return testSuite;
		
	}

}
