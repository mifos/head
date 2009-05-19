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
 
package org.mifos.application.accounts;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.accounts.business.AddAccountStateFlagTest;
import org.mifos.application.accounts.business.AccountActionDateEntityIntegrationTest;
import org.mifos.application.accounts.business.AccountActionEntityIntegrationTest;
import org.mifos.application.accounts.business.AccountBOIntegrationTest;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntityIntegrationTest;
import org.mifos.application.accounts.business.AccountFeesEntityIntegrationTest;
import org.mifos.application.accounts.business.AccountPaymentEntityIntegrationTest;
import org.mifos.application.accounts.business.AccountStateEntityIntegrationTestTest;
import org.mifos.application.accounts.business.AccountStateMachineIntegrationTest;
import org.mifos.application.accounts.business.LoanTrxnDetailEntityIntegrationTest;
import org.mifos.application.accounts.business.service.TestAccountService;
import org.mifos.application.accounts.financial.business.service.FinancialBusinessServiceIntegrationTest;
import org.mifos.application.accounts.offsetting.TestOffsetAccountBO;
import org.mifos.application.accounts.persistence.AccountPersistenceTest;
import org.mifos.application.accounts.struts.action.TestAccountAction;
import org.mifos.application.accounts.struts.action.TestApplyAdjustmentAction;
import org.mifos.application.accounts.struts.action.TestApplyChargeAction;
import org.mifos.application.accounts.struts.action.TestApplyPaymentAction;
import org.mifos.application.accounts.struts.action.TestEditStatusAction;
import org.mifos.application.accounts.struts.action.TestNotesAction;
import org.mifos.application.accounts.util.helper.TestAccountState;
import org.mifos.application.customer.business.TestCustomerAccountBO;
import org.mifos.application.accounts.business.AddAccountActionTest;

public class AccountTestSuite extends TestSuite {
	
	AccountTestSuite() throws Exception{
		super();
	}
	
	public static void main(String[] args) throws Exception {
		Test testSuite = suite();
		TestRunner.run(testSuite);
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new AccountTestSuite();
		testSuite.addTestSuite(AccountPersistenceTest.class);
		testSuite.addTestSuite(AccountBOIntegrationTest.class);
		testSuite.addTestSuite(AccountFeesEntityIntegrationTest.class);
		testSuite.addTestSuite(AccountActionDateEntityIntegrationTest.class);
		testSuite.addTestSuite(TestAccountService.class);
		testSuite.addTestSuite(TestAccountAction.class);
		testSuite.addTestSuite(TestApplyAdjustmentAction.class);
		testSuite.addTestSuite(AccountStateEntityIntegrationTestTest.class);
		testSuite.addTestSuite(AccountActionEntityIntegrationTest.class);
		testSuite.addTestSuite(LoanTrxnDetailEntityIntegrationTest.class);
		testSuite.addTestSuite(AccountFeesActionDetailEntityIntegrationTest.class);
		testSuite.addTestSuite(TestCustomerAccountBO.class);
		testSuite.addTestSuite(AccountPaymentEntityIntegrationTest.class);
		testSuite.addTestSuite(FinancialBusinessServiceIntegrationTest.class);
		testSuite.addTestSuite(TestApplyPaymentAction.class);
		testSuite.addTestSuite(TestNotesAction.class);
		testSuite.addTestSuite(AccountStateMachineIntegrationTest.class);
		testSuite.addTestSuite(TestEditStatusAction.class);
		testSuite.addTestSuite(TestApplyChargeAction.class);		
		testSuite.addTestSuite(TestOffsetAccountBO.class);	
		testSuite.addTest(AddAccountStateFlagTest.testSuite());
		testSuite.addTestSuite(TestAccountState.class);
		testSuite.addTest(AddAccountActionTest.suite());
		return testSuite;
		
	}
	
	
}
