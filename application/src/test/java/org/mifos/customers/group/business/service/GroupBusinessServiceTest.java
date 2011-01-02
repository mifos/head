package org.mifos.customers.group.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.persistence.GroupPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.annotation.ExpectedException;

@RunWith(MockitoJUnitRunner.class)
public class GroupBusinessServiceTest {
    final GroupPersistence groupPersistence = mock(GroupPersistence.class);

    GroupBusinessService service = new GroupBusinessService() {
        @Override
        protected GroupPersistence getGroupPersistence() {
            return groupPersistence;
        }
    };

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetGroup() throws PersistenceException {
        try {
            Integer customerId = new Integer(1);
            when(groupPersistence.getGroupByCustomerId(customerId)).thenThrow(new PersistenceException("some exception"));
            service.getGroup(customerId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInFindBySystemId() throws PersistenceException {
        try {
            String globalCustNo = "globalCustNo";
            when(groupPersistence.findBySystemId(globalCustNo)).thenThrow(new PersistenceException("some exception"));
            service.findBySystemId(globalCustNo);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInSearch() throws PersistenceException {
        try {
            String searchWord = "globalCustNo";
            Short userId = new Short("1");
            when(groupPersistence.search(searchWord, userId)).thenThrow(new PersistenceException("some exception"));
            service.search(searchWord, userId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }


}
