package org.mifos.application.accounts.persistence;

import java.util.List;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.framework.MifosTestCase;

public class LoanAccountPersistenceTest extends MifosTestCase {

	public void testSelectCoSigningClients() throws Exception {
		List<CustomerBO> coSigningClients = new AccountPersistence().getCoSigningClientsForGlim(1);
		assertNotNull(coSigningClients);
		assertEquals(0, coSigningClients.size());
	}
}
