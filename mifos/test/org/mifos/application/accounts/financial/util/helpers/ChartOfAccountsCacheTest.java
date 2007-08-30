package org.mifos.application.accounts.financial.util.helpers;

import junit.framework.TestCase;

import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;

public class ChartOfAccountsCacheTest extends TestCase {

	private static final Short EXCEED_SIZE=20000;
	private static final short TEST_ID = 10000;
	private static final String TEST_NAME = "test account";
	
	public void testAddAndGet() throws FinancialException {
		ChartOfAccountsCache.add(new COABO(TEST_ID,TEST_NAME));

		COABO coaAssets = ChartOfAccountsCache.get(TEST_ID);
		assertEquals(TEST_ID, coaAssets.getCategoryId().shortValue());
		assertEquals(TEST_NAME, coaAssets.getCategoryName());
	}

	public void testExceedSize() {
		try {
			ChartOfAccountsCache.get(EXCEED_SIZE);
			fail();
		} catch (FinancialException expected) {
		}

	}

}
