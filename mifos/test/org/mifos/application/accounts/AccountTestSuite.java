/**
 * 
 */
package org.mifos.application.accounts;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.accounts.business.AccountActionEntityTest;
import org.mifos.application.accounts.business.AddAccountStateFlagTest;
import org.mifos.application.accounts.business.TestAccountActionDateEntity;
import org.mifos.application.accounts.business.TestAccountActionEntity;
import org.mifos.application.accounts.business.TestAccountBO;
import org.mifos.application.accounts.business.TestAccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.TestAccountFeesEntity;
import org.mifos.application.accounts.business.TestAccountPaymentEntity;
import org.mifos.application.accounts.business.TestAccountStateEntity;
import org.mifos.application.accounts.business.TestAccountStateMachine;
import org.mifos.application.accounts.business.TestLoanTrxnDetailEntity;
import org.mifos.application.accounts.business.service.TestAccountService;
import org.mifos.application.accounts.financial.business.service.TestFinancialBusinessService;
import org.mifos.application.accounts.offsetting.TestOffsetAccountBO;
import org.mifos.application.accounts.persistence.TestAccountPersistence;
import org.mifos.application.accounts.struts.action.TestAccountAction;
import org.mifos.application.accounts.struts.action.TestApplyAdjustmentAction;
import org.mifos.application.accounts.struts.action.TestApplyChargeAction;
import org.mifos.application.accounts.struts.action.TestApplyPaymentAction;
import org.mifos.application.accounts.struts.action.TestEditStatusAction;
import org.mifos.application.accounts.struts.action.TestNotesAction;
import org.mifos.application.accounts.util.helper.TestAccountState;
import org.mifos.application.customer.business.TestCustomerAccountBO;

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
		testSuite.addTestSuite(TestAccountPersistence.class);
		testSuite.addTestSuite(TestAccountBO.class);
		testSuite.addTestSuite(TestAccountFeesEntity.class);
		testSuite.addTestSuite(TestAccountActionDateEntity.class);
		testSuite.addTestSuite(TestAccountService.class);
		testSuite.addTestSuite(TestAccountAction.class);
		testSuite.addTestSuite(TestApplyAdjustmentAction.class);
		testSuite.addTestSuite(TestAccountStateEntity.class);
		testSuite.addTestSuite(TestAccountActionEntity.class);
		testSuite.addTestSuite(AccountActionEntityTest.class);
		testSuite.addTestSuite(TestLoanTrxnDetailEntity.class);
		testSuite.addTestSuite(TestAccountFeesActionDetailEntity.class);
		testSuite.addTestSuite(TestCustomerAccountBO.class);
		testSuite.addTestSuite(TestAccountPaymentEntity.class);
		testSuite.addTestSuite(TestFinancialBusinessService.class);
		testSuite.addTestSuite(TestApplyPaymentAction.class);
		testSuite.addTestSuite(TestNotesAction.class);
		testSuite.addTestSuite(TestAccountStateMachine.class);
		testSuite.addTestSuite(TestEditStatusAction.class);
		testSuite.addTestSuite(TestApplyChargeAction.class);		
		testSuite.addTestSuite(TestOffsetAccountBO.class);	
		testSuite.addTest(AddAccountStateFlagTest.suite());
		
		testSuite.addTestSuite(TestAccountState.class);
		return testSuite;
		
	}
	
	
}
