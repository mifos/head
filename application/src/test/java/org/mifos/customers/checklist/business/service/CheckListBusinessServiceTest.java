package org.mifos.customers.checklist.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.checklist.persistence.CheckListPersistence;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.annotation.ExpectedException;

@RunWith(MockitoJUnitRunner.class)

public class CheckListBusinessServiceTest {
    final CheckListPersistence checkListPersistence = mock(CheckListPersistence.class);
    Short id = new Short("1");

    CheckListBusinessService service = new CheckListBusinessService() {
        @Override
        protected CheckListPersistence getCheckListPersistence() {
            return checkListPersistence;
        }
    };

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionRetrieveAllAccountCheckLists() throws PersistenceException {
        try {
            when(checkListPersistence.retreiveAllAccountCheckLists()).thenThrow(new PersistenceException("some exception"));
            service.retreiveAllAccountCheckLists();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionRetrieveAllCustomerCheckLists() throws PersistenceException {
        try {
            when(checkListPersistence.retreiveAllCustomerCheckLists()).thenThrow(new PersistenceException("some exception"));
            service.retreiveAllCustomerCheckLists();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }
}