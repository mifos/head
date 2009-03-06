package org.mifos.application.accounts.financial.util.helpers;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.hibernate.Query;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class TestFinancialActionConstants extends MifosIntegrationTest {

	public TestFinancialActionConstants() throws SystemException, ApplicationException {
        super();
    }

    /*
	 * Verify that the number of elements in the enum 
	 * {@link FinancialActionConstants} matches 
	 * the number of elements in the corresponding table
	 * "financial_actions".
	 */
	@Test
	public void testGetFinancialAction() {
		Query queryFinancialAction = HibernateUtil.getSessionTL()
			.getNamedQuery(FinancialQueryConstants.GET_ALL_FINANCIAL_ACTION);
		// NOTE: the following 2 database entries are not currently represented
		// in the enum.  Should they be added to the enum or removed from the db?
		//   FIN_ACTION_ID = 15 (Interest_Posting)
		//   FIN_ACTION_ID = 17 (Customer_Adjustment)
		// The (-2) adjustment to the assert compensates for this difference

		assertEquals(FinancialActionConstants.values().length, queryFinancialAction.list().size() - 2);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestFinancialActionConstants.class);
	}
	
}
