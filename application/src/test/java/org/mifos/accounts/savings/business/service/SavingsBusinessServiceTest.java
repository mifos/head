package org.mifos.accounts.savings.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.springframework.test.annotation.ExpectedException;

public class SavingsBusinessServiceTest {
    final SavingsPersistence savingsPersistence = mock(SavingsPersistence.class);
    final SavingsDao savingsDao = mock(SavingsDao.class);
    SavingsBusinessService service = new SavingsBusinessService() {
        @Override
        protected SavingsPersistence getSavingsPersistence() {
            return savingsPersistence;
        }
    };

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
