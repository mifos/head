package org.mifos.application.accounts.financial.business;

import java.util.Set;

import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.CategoryConstants;
import org.mifos.application.accounts.financial.util.helpers.ChartOfAccountsCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.TestConstants;

public class TestCOABO extends MifosTestCase {
	public void testGetCOAHead() throws FinancialException {
		FinancialInitializer.initialize();
		COABO coaInterestLoans = ChartOfAccountsCache
				.get(CategoryConstants.INTERESTONLOANS);
		COABO coaHead = coaInterestLoans.getCOAHead();
		assertEquals(coaHead.getCategoryId().shortValue(),
				CategoryConstants.INCOME);
	}

	public void testGetCurrentSubCategory() throws FinancialException {
		FinancialInitializer.initialize();
		COABO coaDirectIncome = ChartOfAccountsCache.get(CategoryConstants.DIRECTINCOME);
		Set<COABO> currentSubCategory = coaDirectIncome.getCurrentSubCategory();
		assertEquals(currentSubCategory.size(),
				TestConstants.FINANCIAL_DIRECTINCOME_SIZE);
	}

    public void testEquals() throws Exception {
        COABO chart53 = new COABO(53, "Example 1");
        COABO chart54 = new COABO(54, "Example 1");
        COABO chart53b = new COABO(53, "ChangedName");
        TestUtils.assertAllEqual(new Object[] {chart53, chart53b});
        TestUtils.assertIsNotEqual(chart53, chart54);
    }

}
