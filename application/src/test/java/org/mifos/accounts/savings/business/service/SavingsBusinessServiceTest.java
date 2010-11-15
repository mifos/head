package org.mifos.accounts.savings.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.springframework.test.annotation.ExpectedException;

public class SavingsBusinessServiceTest {
    final SavingsPersistence savingsPersistence = mock(SavingsPersistence.class);
    SavingsBusinessService service = new SavingsBusinessService() {
        @Override
        protected SavingsPersistence getSavingsPersistence() {
            return savingsPersistence;
        }
    };

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForFindById() throws PersistenceException {
        try {
            Integer accountId = new Integer(1);
            when(savingsPersistence.findById(accountId)).
                    thenThrow(new PersistenceException("some exception"));
            service.findById(accountId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForFindBySystemId() throws PersistenceException {
        try {
            String accountNumber = "globalAcctNo";
            when(savingsPersistence.findBySystemId(accountNumber)).
                    thenThrow(new PersistenceException("some exception"));
            service.findBySystemId(accountNumber);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForGetAllClosedAccounts() throws PersistenceException {
        try {
            Integer customerId = new Integer(1);
            when(savingsPersistence.getAllClosedAccount(customerId)).
                    thenThrow(new PersistenceException("some exception"));
            service.getAllClosedAccounts(customerId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }
    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForRetrieveCustomFieldsDefinition() throws PersistenceException {
        try {
            when(savingsPersistence.retrieveCustomFieldsDefinition(SavingsConstants.SAVINGS_CUSTOM_FIELD_ENTITY_TYPE)).
                    thenThrow(new PersistenceException("some exception"));
            service.retrieveCustomFieldsDefinition();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }
}
