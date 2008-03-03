package org.mifos.application.accounts.financial.util.helpers;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.GLCategoryType;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.framework.util.helpers.TestCaseInitializer;

public class ChartOfAccountsCacheTest {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ChartOfAccountsCacheTest.class);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		// initialize Spring, Hibernate, etc.
		new TestCaseInitializer();
	}

	private static final Short GL_CODE_ENTITY_ID = 197;
	private static final String GL_CODE = "17777";
	private static final int ACCOUNT_ID = 197;
	private static final String ACCOUNT_NAME = "test account";
	private static final String INCOME_GL_ACCOUNT_CODE = "30000";

	@Test
	public void testAddAndGet() throws FinancialException {
		GLCodeEntity glCodeEntity = new GLCodeEntity(GL_CODE_ENTITY_ID, GL_CODE);
		COABO newAccount = new COABO(ACCOUNT_ID, ACCOUNT_NAME, glCodeEntity);
		ChartOfAccountsCache.add(newAccount);

		COABO cachedAccount = ChartOfAccountsCache.get(GL_CODE);
		assertEquals(GL_CODE, cachedAccount.getGlCode());
		assertEquals(ACCOUNT_NAME, cachedAccount.getAccountName());
		assertEquals(newAccount, cachedAccount);
	}

	@Test
	public void testTopLevelAccountCached() throws Exception {
		AccountPersistence ap = new AccountPersistence();
		COABO income = ap.getCategory(GLCategoryType.INCOME);
		COABO cachedIncome = ChartOfAccountsCache.get(INCOME_GL_ACCOUNT_CODE);
		assertEquals(income, cachedIncome);
	}

}
