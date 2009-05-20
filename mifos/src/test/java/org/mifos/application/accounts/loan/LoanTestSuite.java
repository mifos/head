/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.accounts.loan;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.accounts.loan.business.LoanBOForReversalIntegrationTest;
import org.mifos.application.accounts.loan.business.LoanBOIntegrationTest;
import org.mifos.application.accounts.loan.business.LoanBORedoDisbursalTest;
import org.mifos.application.accounts.loan.business.LoanCalculationTest;
import org.mifos.application.accounts.loan.business.LoanScheduleEntityIntegrationTest;
import org.mifos.application.accounts.loan.business.service.LoanBusinessServiceIntegrationTest;
import org.mifos.application.accounts.loan.persistence.LoanPersistenceIntegrationTest;
import org.mifos.application.accounts.loan.struts.action.AccountStatusActionTest;
import org.mifos.application.accounts.loan.struts.action.LoanAccountActionIndividualLoansIntegrationTest;
import org.mifos.application.accounts.loan.struts.action.LoanAccountActionTest;
import org.mifos.application.accounts.loan.struts.action.LoanActivityActionTest;
import org.mifos.application.accounts.loan.struts.action.MultipleLoanAccountsCreationActionTest;
import org.mifos.application.accounts.loan.struts.action.RepayLoanActionTest;
import org.mifos.application.accounts.loan.struts.action.ReverseLoanDisbursalActionTest;
import org.mifos.application.accounts.loan.struts.action.LoanAccountActionEasyMockTest;
import org.mifos.application.accounts.loan.struts.actionforms.LoanAccountActionFormIntegrationTest;
import org.mifos.application.accounts.loan.struts.uihelpers.LoanUIHelperFnTest;
import org.mifos.application.accounts.loan.struts.uihelpers.LoanActivityTagIntegrationTest;
import org.mifos.application.accounts.loan.struts.uihelpers.LoanRepayTagIntegrationTest;

public class LoanTestSuite extends TestSuite {
	public static void main(String[] args) {
		Test testSuite = suite();
		TestRunner.run(testSuite);
	}

	public static Test suite() {
		TestSuite testSuite = new LoanTestSuite();
        testSuite.addTest(LoanAccountActionTest.testSuite());
		testSuite.addTest(LoanCalculationTest.testSuite());
		testSuite.addTestSuite(LoanPersistenceIntegrationTest.class);
		testSuite.addTestSuite(LoanBOIntegrationTest.class);
		testSuite.addTestSuite(RepayLoanActionTest.class);
		testSuite.addTestSuite(LoanBusinessServiceIntegrationTest.class);
		testSuite.addTestSuite(LoanActivityActionTest.class);
		testSuite.addTestSuite(LoanAccountActionEasyMockTest.class);
		testSuite.addTestSuite(AccountStatusActionTest.class);
		testSuite.addTestSuite(LoanScheduleEntityIntegrationTest.class);
		testSuite.addTestSuite(LoanUIHelperFnTest.class);
		testSuite.addTestSuite(LoanActivityTagIntegrationTest.class);
		testSuite.addTestSuite(LoanRepayTagIntegrationTest.class);
		testSuite.addTestSuite(MultipleLoanAccountsCreationActionTest.class);
		testSuite.addTestSuite(LoanBOForReversalIntegrationTest.class);
        testSuite.addTest(LoanBORedoDisbursalTest.testSuite());
        testSuite.addTestSuite(ReverseLoanDisbursalActionTest.class);
        testSuite.addTestSuite(LoanAccountActionIndividualLoansIntegrationTest.class);
        testSuite.addTestSuite(LoanAccountActionFormIntegrationTest.class);
		return testSuite;
	}
}
