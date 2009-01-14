package org.mifos.application.accounts.loan;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.accounts.loan.business.LoanCalculationTest;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.loan.business.TestLoanBOForReversal;
import org.mifos.application.accounts.loan.business.TestLoanScheduleEntity;
import org.mifos.application.accounts.loan.business.TestLoanBORedoDisbursal;
import org.mifos.application.accounts.loan.business.service.TestLoanBusinessService;
import org.mifos.application.accounts.loan.persistence.TestLoanPersistence;
import org.mifos.application.accounts.loan.struts.action.LoanAccountActionTest;
import org.mifos.application.accounts.loan.struts.action.MultipleLoanAccountsCreationActionTest;
import org.mifos.application.accounts.loan.struts.action.ReverseLoanDisbursalActionTest;
import org.mifos.application.accounts.loan.struts.action.TestAccountStatusAction;
import org.mifos.application.accounts.loan.struts.action.TestLoanAccountAction;
import org.mifos.application.accounts.loan.struts.action.TestLoanAccountActionIndividualLoans;
import org.mifos.application.accounts.loan.struts.action.TestLoanActivityAction;
import org.mifos.application.accounts.loan.struts.action.TestRepayLoanAction;
import org.mifos.application.accounts.loan.struts.actionforms.LoanAccountActionFormTest;
import org.mifos.application.accounts.loan.struts.uihelpers.LoanUIHelperFnTest;
import org.mifos.application.accounts.loan.struts.uihelpers.TestLoanActivityTag;
import org.mifos.application.accounts.loan.struts.uihelpers.TestLoanRepayTag;

public class LoanTestSuite extends TestSuite {
	public static void main(String[] args) {
		Test testSuite = suite();
		TestRunner.run(testSuite);
	}

	public static Test suite() {
		TestSuite testSuite = new LoanTestSuite();
        testSuite.addTest(LoanAccountActionTest.testSuite());
		testSuite.addTest(LoanCalculationTest.testSuite());
		testSuite.addTestSuite(TestLoanPersistence.class);
		testSuite.addTestSuite(TestLoanBO.class);
		testSuite.addTestSuite(TestRepayLoanAction.class);
		testSuite.addTestSuite(TestLoanBusinessService.class);
		testSuite.addTestSuite(TestLoanActivityAction.class);
		testSuite.addTestSuite(TestLoanAccountAction.class);
		testSuite.addTestSuite(TestAccountStatusAction.class);
		testSuite.addTestSuite(TestLoanScheduleEntity.class);
		testSuite.addTestSuite(LoanUIHelperFnTest.class);
		testSuite.addTestSuite(TestLoanActivityTag.class);
		testSuite.addTestSuite(TestLoanRepayTag.class);
		testSuite.addTestSuite(MultipleLoanAccountsCreationActionTest.class);
		testSuite.addTestSuite(TestLoanBOForReversal.class);
        testSuite.addTest(TestLoanBORedoDisbursal.testSuite());
        testSuite.addTestSuite(ReverseLoanDisbursalActionTest.class);
        testSuite.addTestSuite(TestLoanAccountActionIndividualLoans.class);
        testSuite.addTestSuite(LoanAccountActionFormTest.class);
		return testSuite;
	}
}
