package org.mifos.application.accounts.financial.util.helpers;

import junit.framework.TestCase;

import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.COACache;
import org.mifos.application.accounts.financial.util.helpers.CategoryConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;

public class TestFinancialInitializer extends TestCase {
	



	public void testCOAInitializer() throws Exception {
		FinancialInitializer.initializeCOA(HibernateUtil.getSessionTL());
		COABO coaAssets = COACache.getCOA(CategoryConstants.ASSETS);
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

	public void testFinancialActionInitializerException() throws Exception

	{
		try { 
		FinancialInitializer.initalizeFinancialAction(HibernateUtil
				.getSessionTL());
		FinancialActionCache.getFinancialAction(Short.valueOf("-1"));
		assertTrue(false);
		}
		catch( FinancialException  e){
			
			assertTrue(true);
			
		}
	}

	public void testCOAInitializerException() throws Exception

	{
		
		try { 

		FinancialInitializer.initializeCOA(HibernateUtil.getSessionTL());

		COACache.getCOA(Short.valueOf("-1"));
		
		assertTrue(false);
		}
		catch( FinancialException  e){
			
			assertTrue(true);
			
		}


	}

	public void testInit() throws FinancialException

	{

		FinancialInitializer.initialize();

		COABO coaAssets = COACache.getCOA(CategoryConstants.ASSETS);

		assertEquals(coaAssets.getCategoryId().shortValue(),
				CategoryConstants.ASSETS);

		FinancialActionBO financialActionPrincipal = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);

		assertEquals(financialActionPrincipal.getId().shortValue(),
				FinancialActionConstants.PRINCIPALPOSTING);

	}

}
