package org.mifos.application.accounts.financial.util.helpers;

import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestFinancialActionCache extends MifosIntegrationTest {
	

	public TestFinancialActionCache() throws SystemException, ApplicationException {
        super();
    }


    public void testFinancialActionCache() throws FinancialException
	{
		
		FinancialActionCache.addToCache(createFinancialAction());
		
		FinancialActionBO principalAction = FinancialActionCache.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);
		assertEquals(principalAction.getId().shortValue(),1);
		
	}
	
	
	private FinancialActionBO createFinancialAction()
	{
		return (FinancialActionBO)HibernateUtil.getSessionTL().get(FinancialActionBO.class,FinancialActionConstants.PRINCIPALPOSTING.value);

	}

}
