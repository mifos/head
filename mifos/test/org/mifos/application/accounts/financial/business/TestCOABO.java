package org.mifos.application.accounts.financial.business;

import java.util.List;
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
        COABO subclass = new COABO(53, "Example 1") { };

        TestUtils.verifyBasicEqualsContract(
        	new COABO[] { chart53, chart53b, subclass }, 
        	new COABO[] { chart54 });
    }
    
    public void testGetSubCategoryCOABOs() throws FinancialException {
    	String[] SUB_CATEGORY_NAMES = {"Petty Cash Accounts", "Bank Balances"};
    	String[] SUB_CATEGORY_GLCODES = {"11100", "11200"};
    	
		FinancialInitializer.initialize();
		COABO coa = ChartOfAccountsCache.get(CategoryConstants.CASHBANKBALANCE);
		List<COABO> subCategories = coa.getSubCategoryCOABOs();
		for (int index=0; index < subCategories.size(); ++index) {
			COABO subcat1 = subCategories.get(index);
			assertEquals(SUB_CATEGORY_NAMES[index], subcat1.getCategoryName());
			assertEquals(SUB_CATEGORY_GLCODES[index], subcat1.getAssociatedGlcode().getGlcode());
		}    	
    }

}
