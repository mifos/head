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
import org.mifos.application.accounts.business.service.AccountServiceIntegrationTest;
import org.mifos.application.accounts.financial.business.service.FinancialBusinessServiceIntegrationTest;
import org.mifos.application.accounts.offsetting.OffsetAccountBOIntegrationTest;
import org.mifos.application.accounts.persistence.AccountPersistenceIntegrationTest;
import org.mifos.application.accounts.struts.action.AccountActionTest;
import org.mifos.application.accounts.struts.action.ApplyAdjustmentActionTest;
import org.mifos.application.accounts.struts.action.ApplyChargeActionTest;
import org.mifos.application.accounts.struts.action.ApplyPaymentActionTest;
import org.mifos.application.accounts.struts.action.EditStatusActionTest;
import org.mifos.application.accounts.struts.action.NotesActionTest;
import org.mifos.application.accounts.util.helper.AccountStateIntegrationTest;
import org.mifos.application.customer.business.CustomerAccountBOIntegrationTest;
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
		testSuite.addTestSuite(AccountPersistenceIntegrationTest.class);
		testSuite.addTestSuite(AccountBOIntegrationTest.class);
		testSuite.addTestSuite(AccountFeesEntityIntegrationTest.class);
		testSuite.addTestSuite(AccountActionDateEntityIntegrationTest.class);
		testSuite.addTestSuite(AccountServiceIntegrationTest.class);
		testSuite.addTestSuite(AccountActionTest.class);
		testSuite.addTestSuite(ApplyAdjustmentActionTest.class);
		testSuite.addTestSuite(AccountStateEntityIntegrationTestTest.class);
		testSuite.addTestSuite(AccountActionEntityIntegrationTest.class);
		testSuite.addTestSuite(LoanTrxnDetailEntityIntegrationTest.class);
		testSuite.addTestSuite(AccountFeesActionDetailEntityIntegrationTest.class);
		testSuite.addTestSuite(CustomerAccountBOIntegrationTest.class);
		testSuite.addTestSuite(AccountPaymentEntityIntegrationTest.class);
		testSuite.addTestSuite(FinancialBusinessServiceIntegrationTest.class);
		testSuite.addTestSuite(ApplyPaymentActionTest.class);
		testSuite.addTestSuite(NotesActionTest.class);
		testSuite.addTestSuite(AccountStateMachineIntegrationTest.class);
		testSuite.addTestSuite(EditStatusActionTest.class);
		testSuite.addTestSuite(ApplyChargeActionTest.class);		
		testSuite.addTestSuite(OffsetAccountBOIntegrationTest.class);	
		testSuite.addTest(AddAccountStateFlagTest.testSuite());
		testSuite.addTestSuite(AccountStateIntegrationTest.class);
		testSuite.addTest(AddAccountActionTest.suite());
		return testSuite;
		
	}
	
	
}
