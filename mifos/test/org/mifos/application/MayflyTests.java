package org.mifos.application;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.struts.action.TestApplyAdjustmentAction;
import org.mifos.application.fees.persistence.FeePersistenceTest;
import org.mifos.application.fees.struts.action.FeeActionTest;
import org.mifos.application.holiday.util.helpers.TestHolidayUtils;
import org.mifos.framework.persistence.DatabaseVersionPersistenceTest;
import org.mifos.framework.persistence.LatestTest;
import org.mifos.framework.util.helpers.DatabaseSetup;

/**
 * Tests that are known to pass with Mayfly, if you enable Mayfly in
 * {@link DatabaseSetup}.
 * 
 * Does not include tests which are hardcoded to always use Mayfly,
 * like {@link LatestTest} or {@link DatabaseVersionPersistenceTest}.
 */
public class MayflyTests extends TestSuite {
	
	public static Test suite() throws Exception {
		TestSuite suite = new MayflyTests();
		suite.addTestSuite(FeePersistenceTest.class);
		//suite.addTestSuite(CenterBOTest.class);
		
		// Hung up on SELECT DISTINCT vs ORDER BY
		// Also has other failures - apparently unrelated
		//CustomerPersistenceTest
		
		/* Failing in getMaxOfficeId.
		   Perhaps Integer vs. Long as return from getObject (but that's
		   unconfirmed)? */
		//suite.addTestSuite(TestOfficePersistence.class);

		suite.addTestSuite(TestSavingsBO.class);
		suite.addTestSuite(TestApplyAdjustmentAction.class);
		suite.addTestSuite(FeeActionTest.class);
		suite.addTestSuite(TestHolidayUtils.class);
		
		suite.addTestSuite(MayflyMiscTest.class);
		return suite;
	}

}
