package org.mifos.application.acceptedpaymenttype;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistenceTest;
import org.mifos.application.acceptedpaymenttype.struts.action.AcceptedPaymentTypeActionTest;



public class ApplicationAcceptedPaymentTypeTestSuite extends TestSuite {
	
	public static void main (String[] args)
	{
		Test testSuite = suite();
		TestRunner.run (testSuite);
	}
	

	public static Test suite()
	{
		TestSuite testSuite = new ApplicationAcceptedPaymentTypeTestSuite();
		testSuite.addTestSuite(AcceptedPaymentTypeActionTest.class);
		testSuite.addTestSuite(AcceptedPaymentTypePersistenceTest.class);
		
		return testSuite;
		
	}

}