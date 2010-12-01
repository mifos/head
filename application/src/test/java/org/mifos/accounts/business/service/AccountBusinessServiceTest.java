package org.mifos.accounts.business.service;

import junit.framework.Assert;
import org.junit.Test;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.springframework.test.annotation.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountBusinessServiceTest {
    AccountBusinessService accountBusinessService = new AccountBusinessService();

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionThrowsExceptionInRetrieveCustomFields() throws PersistenceException{
        final AccountPersistence accountPersistence = mock(AccountPersistence.class);
        AccountBO accountBO = new AccountBO() {
            @Override
            public AccountPersistence getAccountPersistence() {
                return accountPersistence;
            }
        };
        try {
            when(accountPersistence.retrieveCustomFieldsDefinition(EntityType.CENTER.getValue())).thenThrow(new PersistenceException("some exception"));
            accountBusinessService.retrieveCustomFieldsDefinition(EntityType.CENTER);
            Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }
}
