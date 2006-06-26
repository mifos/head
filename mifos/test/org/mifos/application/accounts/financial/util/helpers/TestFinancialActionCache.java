package org.mifos.application.accounts.financial.util.helpers;

import org.hibernate.Session;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;

import junit.framework.TestCase;

public class TestFinancialActionCache extends TestCase {
	

	public void testFinancialActionCache() throws FinancialException
	{
		
		FinancialActionCache.addToCache(createFinancialAction());
		
		FinancialActionBO principalAction = FinancialActionCache.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);
		assertEquals(principalAction.getId().shortValue(),1);
		
	}
	
	public void testFinancialActionCacheException() 
	{
	  try
	  {
			FinancialActionCache.addToCache(createFinancialAction());
			
			FinancialActionCache.getFinancialAction(new Short("55"));
			assertFalse(true);
	  }
	  catch(FinancialException fin)
	  {
		  assertTrue(true);
	  }
		
	}

	
	private FinancialActionBO createFinancialAction()
	{
		return (FinancialActionBO)HibernateUtil.getSessionTL().get(FinancialActionBO.class,FinancialActionConstants.PRINCIPALPOSTING);

	}

}
