package org.mifos.customers.checklist.business.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.checklist.persistence.CheckListPersistence;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.annotation.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void testInvalidConnectionInGetAccountStates() throws PersistenceException {
        try {
            when(checkListPersistence.retrieveAllAccountStateList(id, id)).thenThrow(new PersistenceException("some exception"));
            service.getAccountStates(id, id);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetCheckList() throws PersistenceException {
        try {
            when(checkListPersistence.getCheckList(id)).thenThrow(new PersistenceException("some exception"));
            service.getCheckList(id);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetCustomerStates() throws PersistenceException {
        try {
            when(checkListPersistence.retrieveAllCustomerStatusList(id, id)).thenThrow(new PersistenceException("some exception"));
            service.getCustomerStates(id, id);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInIsValidCheckListState() throws PersistenceException {
        try {
            when(checkListPersistence.isValidCheckListState(id, id, true)).thenThrow(new PersistenceException("some exception"));
            service.isValidCheckListState(id, id, true);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

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

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetCheckListMasterData() throws PersistenceException {
        try {
            UserContext context = mock(UserContext.class);
            when(context.getLocaleId()).thenReturn(id);
            when(checkListPersistence.getCheckListMasterData(id)).thenThrow(new PersistenceException("some exception"));
            service.getCheckListMasterData(context);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

}
