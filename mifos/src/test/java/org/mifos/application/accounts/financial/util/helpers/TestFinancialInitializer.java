package org.mifos.application.accounts.financial.util.helpers;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.GLCategoryType;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.util.helpers.TestCaseInitializer;

public class TestFinancialInitializer {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestFinancialInitializer.class);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		new TestCaseInitializer();
	}

	@Test
	public void testAssetsCategoryIsCached() throws Exception {
		String assetsGlCode = "10000";
		COABO account1 = ChartOfAccountsCache.get(assetsGlCode);
		assertEquals(GLCategoryType.ASSET, account1.getCategoryType());
	}

	@Test
	public void testFinancialActionInitializer() throws FinancialException {
		FinancialInitializer.initalizeFinancialAction();
		FinancialActionBO financialActionPrincipal = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);

		assertEquals(financialActionPrincipal.getId().shortValue(),
				FinancialActionConstants.PRINCIPALPOSTING.value);
	}

	@Test(expected=FinancialException.class)
	public void testCOACacherException() throws Exception {
		ChartOfAccountsCache.get("-1");
	}

}
