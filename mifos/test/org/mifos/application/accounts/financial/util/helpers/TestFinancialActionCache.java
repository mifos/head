package org.mifos.application.accounts.financial.util.helpers;

import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestFinancialActionCache extends MifosTestCase {
	

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
