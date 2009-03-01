package org.mifos.application.accounts.financial.business;

import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.ChartOfAccountsCache;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestConstants;

public class TestCOABO extends MifosTestCase {
	public TestCOABO() throws SystemException, ApplicationException {
        super();
    }

    public void testGetCOAHead() throws FinancialException {
		String interestOnLoansGlCode = "31101";
		COABO coaInterestLoans = ChartOfAccountsCache.get(interestOnLoansGlCode);
		COABO coaHead = coaInterestLoans.getCOAHead();
		assertEquals(GLCategoryType.INCOME, coaHead.getCategoryType());
	}

	public void testGetCurrentSubCategory() throws FinancialException {
		String directIncomeGlCode = "31000";
		COABO coaDirectIncome = ChartOfAccountsCache.get(directIncomeGlCode);
		Set<COABO> currentSubCategory = coaDirectIncome.getCurrentSubCategory();
		assertEquals(TestConstants.FINANCIAL_DIRECTINCOME_SIZE,
				currentSubCategory.size());
		
		GLCodeEntity g = (GLCodeEntity) HibernateUtil.getSessionTL().load(GLCodeEntity.class, new Short("1"));
		System.out.println(g.getGlcode());
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
    	
		String cashBankBalanceGlCode = "11000";
		COABO coa = ChartOfAccountsCache.get(cashBankBalanceGlCode);
		List<COABO> subCategories = coa.getSubCategoryCOABOs();
		for (int index=0; index < subCategories.size(); ++index) {
			COABO subcat1 = subCategories.get(index);
			assertEquals(SUB_CATEGORY_NAMES[index], subcat1.getAccountName());
			assertEquals(SUB_CATEGORY_GLCODES[index], subcat1.getAssociatedGlcode().getGlcode());
		}    	
    }

}
