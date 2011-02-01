package org.mifos.accounts.productdefinition.business;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Test;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.springframework.test.annotation.ExpectedException;

public class SavingsOfferingBOTest {

    @Test
    @ExpectedException(value = ProductDefinitionException.class)
    public void testInvalidConnectionThrowsExceptionInSave() throws PersistenceException {
        final SavingsPrdPersistence savingsPrdPersistence = mock(SavingsPrdPersistence.class);
        SavingsOfferingBO savingsOfferingBO = new SavingsOfferingBO(){
            @Override
            protected SavingsPrdPersistence getSavingsPrdPersistence() {
                return savingsPrdPersistence;
            }
        };
        try {
            when(savingsPrdPersistence.createOrUpdate(savingsOfferingBO)).thenThrow(new PersistenceException("some exception"));
            savingsOfferingBO.save();
            Assert.fail("should fail because of invalid session");
        } catch (ProductDefinitionException e) {
        }
    }
}
