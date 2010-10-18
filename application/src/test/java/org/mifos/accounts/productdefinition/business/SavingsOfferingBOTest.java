package org.mifos.accounts.productdefinition.business;

import junit.framework.Assert;
import org.junit.Test;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.accounts.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.springframework.test.annotation.ExpectedException;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
