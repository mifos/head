package org.mifos.accounts.productdefinition.business.service;

import junit.framework.Assert;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.accounts.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.accounts.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;
import org.springframework.test.annotation.ExpectedException;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoanPrdBusinessServiceTest {
    final PrdOfferingPersistence prdOfferingPersistence = mock(PrdOfferingPersistence.class);
    final LoanPrdPersistence loanPrdPersistence = mock(LoanPrdPersistence.class);
    final MasterPersistence masterPersistence = mock(MasterPersistence.class);

    LoanPrdBusinessService loanPrdBusinessService = new LoanPrdBusinessService() {
        @Override
        protected PrdOfferingPersistence getPrdOfferingPersistence() {
            return prdOfferingPersistence;
        }

        @Override
        protected LoanPrdPersistence getLoanPrdPersistence() {
            return loanPrdPersistence;
        }

        @Override
        protected MasterPersistence getMasterPersistence() {
            return masterPersistence;
        }
    };
    private Short localeId = new Short("1");

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionThrowsExceptionInGetApplicableProductCategories() throws PersistenceException {

        try {
            when(prdOfferingPersistence.getApplicableProductCategories(ProductType.LOAN, PrdCategoryStatus.ACTIVE)).
                    thenThrow(new PersistenceException("some exception"));
            loanPrdBusinessService.getActiveLoanProductCategories();
            Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionThrowsExceptionInGetLoanApplicableCustomerTypes() throws PersistenceException {

        try {
            when(masterPersistence.retrieveMasterEntities(PrdApplicableMasterEntity.class, localeId)).thenThrow(new PersistenceException("some exception"));
            loanPrdBusinessService.getLoanApplicableCustomerTypes(localeId);
            Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionThrowsExceptionInGetApplicablePrdStatus() throws PersistenceException {

        try {
            when(prdOfferingPersistence.getApplicablePrdStatus(ProductType.LOAN, localeId)).
                    thenThrow(new PersistenceException("some exception"));
            loanPrdBusinessService.getApplicablePrdStatus(localeId);
            Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionThrowsExceptionInGetAllLoanOfferings() throws PersistenceException {

        try {
            when(loanPrdPersistence.getAllLoanOfferings(localeId)).thenThrow(new PersistenceException("some exception"));
            loanPrdBusinessService.getAllLoanOfferings(localeId);
            Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionThrowsExceptionInGetLoanOffering() throws PersistenceException {

        try {
            when(loanPrdPersistence.getLoanOffering(new Short("112"))).thenThrow(new PersistenceException("some exception"));
            loanPrdBusinessService.getLoanOffering(new Short("112"));
            Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionThrowsExceptionInGetLoanOfferingWithLocaleId() throws PersistenceException {

        try {
            when(loanPrdPersistence.getLoanOffering(new Short("112"), localeId)).thenThrow(new PersistenceException("some exception"));
            loanPrdBusinessService.getLoanOffering(new Short("112"), localeId);
            Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

}
