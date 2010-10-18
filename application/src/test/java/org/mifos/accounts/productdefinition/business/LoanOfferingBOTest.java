package org.mifos.accounts.productdefinition.business;

import junit.framework.Assert;
import org.junit.Test;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.springframework.test.annotation.ExpectedException;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoanOfferingBOTest {

 @Test
    @ExpectedException(value = ProductDefinitionException.class)
    public void testInvalidConnectionThrowsExceptionInUpdate() throws PersistenceException{
        final LoanPrdPersistence loanPrdPersistence = mock(LoanPrdPersistence.class);
        LoanOfferingBO loanOfferingBO = new LoanOfferingBO(){
            @Override
            protected LoanPrdPersistence getLoanPrdPersistence() {
                return loanPrdPersistence;
            }
        };
        try {
            when(loanPrdPersistence.createOrUpdate(loanOfferingBO)).thenThrow(new PersistenceException("some exception"));
            loanOfferingBO.update((short) 1, "Loan Product", "LOAN", null, null, null, null,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, null, (short) 0, null, new Money(new MifosCurrency(new Short("1"),"",new BigDecimal(123), ""), "1000"),
                    new Money(new MifosCurrency(new Short("1"),"",new BigDecimal(123), ""), "2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY, true);
            Assert.fail("should fail because of invalid session");
        } catch (ProductDefinitionException e) {
        }
    }
}
