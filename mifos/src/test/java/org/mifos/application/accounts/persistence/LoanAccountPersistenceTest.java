package org.mifos.application.accounts.persistence;

import java.util.List;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class LoanAccountPersistenceTest extends MifosIntegrationTest {

	public LoanAccountPersistenceTest() throws SystemException, ApplicationException {
        super();
    }

    public void testSelectCoSigningClients() throws Exception {
		List<CustomerBO> coSigningClients = new AccountPersistence().getCoSigningClientsForGlim(1);
		assertNotNull(coSigningClients);
		assertEquals(0, coSigningClients.size());
	}
}
