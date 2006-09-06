package org.mifos.application.accounts.loan;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.loan.business.service.TestLoanBusinessService;
import org.mifos.application.accounts.loan.persistence.TestLoanPersistence;
import org.mifos.application.accounts.loan.struts.TestRepayLoanAction;
import org.mifos.application.accounts.loan.struts.action.TestLoanAccountAction;
import org.mifos.application.accounts.loan.struts.action.TestLoanActivityAction;
import org.mifos.framework.MifosTestSuite;

public class LoanTestSuite extends MifosTestSuite {
	public static void main (String[] args)
	{
		Test testSuite = suite();
	
		TestRunner.run (testSuite);
	}
	

	public static Test suite()
	{
		TestSuite testSuite = new LoanTestSuite();
		testSuite.addTestSuite(TestLoanPersistence.class);
		testSuite.addTestSuite(TestLoanBO.class);
		testSuite.addTestSuite(TestRepayLoanAction.class);
		testSuite.addTestSuite(TestLoanBusinessService.class);
		testSuite.addTestSuite(TestLoanActivityAction.class);
		testSuite.addTestSuite(TestLoanAccountAction.class);
		return testSuite;
		
	}
}
