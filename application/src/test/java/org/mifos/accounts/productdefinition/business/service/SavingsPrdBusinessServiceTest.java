package org.mifos.accounts.productdefinition.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mifos.accounts.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.springframework.test.annotation.ExpectedException;

public class SavingsPrdBusinessServiceTest {
    final PrdOfferingPersistence prdOfferingPersistence = mock(PrdOfferingPersistence.class);
    final SavingsProductDao savingsProductDao = mock(SavingsProductDao.class);

    SavingsPrdBusinessService service = new SavingsPrdBusinessService() {
        @Override
        protected PrdOfferingPersistence getPrdOfferingPersistence() {
            return prdOfferingPersistence;
        }

        @Override
        protected SavingsProductDao getSavingsProductDao() {
            return savingsProductDao;
        }
    };

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForGetActiveSavingsProductCategories() throws PersistenceException {
        try {
            when(prdOfferingPersistence.getApplicableProductCategories(ProductType.SAVINGS, PrdCategoryStatus.ACTIVE)).
                    thenThrow(new PersistenceException("some exception"));
            service.getActiveSavingsProductCategories();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForGetApplicablePrdStatus() throws PersistenceException {
        try {
            Short localeId = new Short("123");
            when(prdOfferingPersistence.getApplicablePrdStatus(ProductType.SAVINGS, localeId)).
                    thenThrow(new PersistenceException("some exception"));
            service.getApplicablePrdStatus(localeId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

}
