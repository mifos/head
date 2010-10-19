package org.mifos.customers.client.business.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.client.persistence.ClientPersistence;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.annotation.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientBusinessServiceTest {
    final ClientPersistence clientPersistence = mock(ClientPersistence.class);

    ClientBusinessService service = new ClientBusinessService() {
        @Override
        protected ClientPersistence getClientPersistence() {
            return clientPersistence;
        }
    };

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetClient() throws PersistenceException {
        try {
            Integer customerId = new Integer(1);
            when(clientPersistence.getClient(customerId)).thenThrow(new PersistenceException("some exception"));
            service.getClient(customerId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetActiveClientsUnderParent() throws PersistenceException {
        try {
            Short officeId = new Short("1");
            String searchId = "searchId";
            when(clientPersistence.getActiveClientsUnderParent(searchId, officeId)).
                    thenThrow(new PersistenceException("some exception"));
            service.getActiveClientsUnderParent(searchId, officeId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

}
