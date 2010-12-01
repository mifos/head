package org.mifos.customers.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerBusinessServiceTest {
    final CustomerPersistence customerPersistence = mock(CustomerPersistence.class);
    CustomerBusinessService service = new CustomerBusinessService(customerPersistence);

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
    public void testInvalidConnectionGetActiveCentersUnderUser() throws PersistenceException {
        try {
            PersonnelBO personnelBO = mock(PersonnelBO.class);
            when(customerPersistence.getActiveCentersUnderUser(personnelBO)).thenThrow(new PersistenceException("some exception"));
            service.getActiveCentersUnderUser(personnelBO);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    public void testInvalidConnectionGetGroupsUnderUser() throws PersistenceException {
        try {
            PersonnelBO personnelBO = mock(PersonnelBO.class);
            when(customerPersistence.getGroupsUnderUser(personnelBO)).thenThrow(new PersistenceException("some exception"));
            service.getGroupsUnderUser(personnelBO);
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

    @Test
    public void testInvalidConnectionSearchCustForSavings() throws PersistenceException {
        try {
            String searchWord = "c";
            Short userId = Short.valueOf("1");
            when(customerPersistence.searchCustForSavings(searchWord, userId)).thenThrow(new PersistenceException("some exception"));
            service.searchCustForSavings(searchWord, userId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    public void testInvalidConnectionSearchGroupClient() throws ConfigurationException, PersistenceException {
        try {
            String searchWord = "c";
            Short userId = Short.valueOf("1");
            when(customerPersistence.searchGroupClient(searchWord, userId)).thenThrow(new PersistenceException("some exception"));
            service.searchGroupClient(searchWord, userId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }
}
