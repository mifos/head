package org.mifos.application.accounts.financial.util.helpers;

import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestFinancialInitializer extends MifosTestCase {

	public void testCOAInitializer() throws Exception {
		FinancialInitializer.initializeCOA(HibernateUtil.getSessionTL());
		COABO coaAssets = ChartOfAccountsCache.get(CategoryConstants.ASSETS);
		assertEquals(coaAssets.getCategoryId().shortValue(),
				CategoryConstants.ASSETS);
	}

	public void testFinancialActionInitializer() throws FinancialException {
		FinancialInitializer.initalizeFinancialAction(HibernateUtil
				.getSessionTL());
		FinancialActionBO financialActionPrincipal = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);

		assertEquals(financialActionPrincipal.getId().shortValue(),
				FinancialActionConstants.PRINCIPALPOSTING);
	}

	public void testFinancialActionInitializerException() throws Exception {
		try {
			FinancialInitializer.initalizeFinancialAction(HibernateUtil
					.getSessionTL());
			FinancialActionCache.getFinancialAction(Short.valueOf("-1"));
			assertTrue(false);
		} catch (FinancialException e) {
			assertTrue(true);
		}
	}

	public void testCOAInitializerException() throws Exception {
		try {
			FinancialInitializer.initializeCOA(HibernateUtil.getSessionTL());
			ChartOfAccountsCache.get(Short.valueOf("-1"));
			assertTrue(false);
		} catch (FinancialException e) {
			assertTrue(true);
		}
	}

	public void testInit() throws FinancialException {
		FinancialInitializer.initialize();
		COABO coaAssets = ChartOfAccountsCache.get(CategoryConstants.ASSETS);
		assertEquals(coaAssets.getCategoryId().shortValue(),
				CategoryConstants.ASSETS);
		FinancialActionBO financialActionPrincipal = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);
		assertEquals(financialActionPrincipal.getId().shortValue(),
				FinancialActionConstants.PRINCIPALPOSTING);
	}

	public void testCOAInitializerForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			FinancialInitializer.initializeCOA(HibernateUtil.getSessionTL());
			fail();
		} catch (FinancialException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}

	}

}
