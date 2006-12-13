package org.mifos.application;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.struts.action.TestApplyAdjustmentAction;
import org.mifos.application.fees.persistence.FeePersistenceTest;
import org.mifos.application.fees.struts.action.FeeActionTest;
import org.mifos.framework.util.helpers.DatabaseSetup;

/**
 * Tests that are known to pass with Mayfly.
 * At least for now, you still need to enable Mayfly in
 * {@link DatabaseSetup}.
 */
public class MayflyTests extends TestSuite {
	
	public static Test suite() throws Exception {
		TestSuite suite = new MayflyTests();
		suite.addTestSuite(FeePersistenceTest.class);
		//suite.addTestSuite(CenterBOTest.class);
		
		// Hung up on SELECT DISTINCT vs ORDER BY
		// Also has other failures - apparently unrelated
		//TestCustomerPersistence

		suite.addTestSuite(TestSavingsBO.class);
		suite.addTestSuite(TestApplyAdjustmentAction.class);
		suite.addTestSuite(FeeActionTest.class);
		
		suite.addTestSuite(MayflyMiscTest.class);
		return suite;
	}

}
