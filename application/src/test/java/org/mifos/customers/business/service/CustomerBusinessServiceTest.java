package org.mifos.customers.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerBusinessServiceTest {

    private CustomerPersistence customerPersistence = mock(CustomerPersistence.class);
    private CustomerBusinessService service = new CustomerBusinessService(customerPersistence);

    @Test
    public void testInvalidConnectionGetCustomer() throws PersistenceException {
        Integer customerId = new Integer(1);
        try {
            when(customerPersistence.getCustomer(customerId)).thenThrow(new PersistenceException("some exception"));
            service.getCustomer(customerId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    public void testInvalidConnectionFindBySystemId() throws PersistenceException {
        try {
            String globalNum = "globalNum";
            when(customerPersistence.findBySystemId(globalNum)).thenThrow(new PersistenceException("some exception"));
            service.findBySystemId(globalNum);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }
}
