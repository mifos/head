package org.mifos.accounts.fees.business;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Test;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.springframework.test.annotation.ExpectedException;

public class FeeBOTest {

    @Test
    @ExpectedException(value = FeeException.class)
    public void testInvalidConnectionThrowsExceptionInConstructor() throws PersistenceException{
        final OfficePersistence officePersistence = mock(OfficePersistence.class);
        try {
            when(officePersistence.getHeadOffice()).thenThrow(new PersistenceException("some exception"));
            new RateFeeBO(null, "Customer Fee", new CategoryTypeEntity(FeeCategory.CENTER), new FeeFrequencyTypeEntity(
                        FeeFrequencyType.ONETIME), null, 2.0, null, false, new FeePaymentEntity(FeePayment.UPFRONT)){
              @Override
                public OfficePersistence getOfficePersistence() {
                    return officePersistence;
                }
            };
            Assert.fail("should fail because of invalid session");
        } catch (FeeException e) {
        }
    }
}
