package org.mifos.accounts.productdefinition.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mifos.accounts.productdefinition.persistence.ProductCategoryPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.springframework.test.annotation.ExpectedException;

public class ProductCategoryBusinessServiceTest {
    final ProductCategoryPersistence productCategoryPersistence = mock(ProductCategoryPersistence.class);

    ProductCategoryBusinessService service = new ProductCategoryBusinessService() {
        @Override
        protected ProductCategoryPersistence getProductCategoryPersistence() {
            return productCategoryPersistence;
        }
    };

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForFindByGlobalNum() throws PersistenceException {

        String globalNum = "globalNum";
        try {
            when(productCategoryPersistence.findByGlobalNum(globalNum)).thenThrow(new PersistenceException("some exception"));
            service.findByGlobalNum(globalNum);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionGetAllCategories() throws PersistenceException {
        try {
            when(productCategoryPersistence.getAllCategories()).thenThrow(new PersistenceException("some exception"));
            service.getAllCategories();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionGetProductCategoryStatusList() throws PersistenceException {
        try {
            when(productCategoryPersistence.getProductCategoryStatusList()).thenThrow(new PersistenceException("some exception"));
            service.getProductCategoryStatusList();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionGetProductTypes() throws PersistenceException {
        try {
            when(productCategoryPersistence.getProductTypes()).thenThrow(new PersistenceException("some exception"));
            service.getProductTypes();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

}
