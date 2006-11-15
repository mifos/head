package org.mifos.application.accounts.financial.business;

import java.util.Set;

import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.ChartOfAccountsCache;
import org.mifos.application.accounts.financial.util.helpers.CategoryConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.framework.MifosTestCase;
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
}
