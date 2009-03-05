package org.mifos.application.accounts.savings;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.savings.business.service.TestSavingsBusinessService;
import org.mifos.application.accounts.savings.persistence.TestSavingsPersistence;
import org.mifos.application.accounts.savings.struts.action.TestSavingsAction;
import org.mifos.application.accounts.savings.struts.action.TestSavingsApplyAdjustmentAction;
import org.mifos.application.accounts.savings.struts.action.TestSavingsClosureAction;
import org.mifos.application.accounts.savings.struts.action.TestSavingsDepositWithdrawalAction;
import org.mifos.application.accounts.savings.struts.tag.TestSavingsOverDueDepositsTag;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelperIntegrationTest;

public class SavingsTestSuite extends TestSuite {

	public SavingsTestSuite() throws Exception {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new SavingsTestSuite();
		testSuite.addTestSuite(TestSavingsPersistence.class);
		testSuite.addTestSuite(TestSavingsBusinessService.class);
		testSuite.addTestSuite(TestSavingsAction.class);
		testSuite.addTestSuite(TestSavingsBO.class);
		testSuite.addTestSuite(TestSavingsClosureAction.class);
		testSuite.addTestSuite(SavingsHelperIntegrationTest.class);
		testSuite.addTestSuite(TestSavingsApplyAdjustmentAction.class);
		testSuite.addTestSuite(TestSavingsDepositWithdrawalAction.class);
		testSuite.addTestSuite(TestSavingsOverDueDepositsTag.class);
		return testSuite;
	}
	
}
