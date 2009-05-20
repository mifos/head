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
 
package org.mifos.application.accounts.savings;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.accounts.savings.business.SavingsBOIntegrationTest;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessServiceIntegrationTest;
import org.mifos.application.accounts.savings.persistence.SavingsPersistenceIntegrationTest;
import org.mifos.application.accounts.savings.struts.action.SavingsActionTest;
import org.mifos.application.accounts.savings.struts.action.SavingsApplyAdjustmentActionTest;
import org.mifos.application.accounts.savings.struts.action.SavingsClosureActionTest;
import org.mifos.application.accounts.savings.struts.action.SavingsDepositWithdrawalActionTest;
import org.mifos.application.accounts.savings.struts.tag.SavingsOverDueDepositsTagIntegrationTest;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelperIntegrationTest;

public class SavingsTestSuite extends TestSuite {

	public SavingsTestSuite() throws Exception {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new SavingsTestSuite();
		testSuite.addTestSuite(SavingsPersistenceIntegrationTest.class);
		testSuite.addTestSuite(SavingsBusinessServiceIntegrationTest.class);
		testSuite.addTestSuite(SavingsActionTest.class);
		testSuite.addTestSuite(SavingsBOIntegrationTest.class);
		testSuite.addTestSuite(SavingsClosureActionTest.class);
		testSuite.addTestSuite(SavingsHelperIntegrationTest.class);
		testSuite.addTestSuite(SavingsApplyAdjustmentActionTest.class);
		testSuite.addTestSuite(SavingsDepositWithdrawalActionTest.class);
		testSuite.addTestSuite(SavingsOverDueDepositsTagIntegrationTest.class);
		return testSuite;
	}
	
}
