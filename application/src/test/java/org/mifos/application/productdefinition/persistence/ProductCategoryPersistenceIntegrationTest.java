/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.productdefinition.persistence;

import java.util.List;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;

public class ProductCategoryPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public ProductCategoryPersistenceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private ProductCategoryPersistence productCategoryPersistence;

    private UserContext userContext = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        productCategoryPersistence = new ProductCategoryPersistence();
    }

    @Override
    protected void tearDown() throws Exception {
        productCategoryPersistence = null;
        userContext = null;
        super.tearDown();
    }

    public void testGetMaxPrdCategoryId() throws Exception {
       Assert.assertEquals(Short.valueOf("2"), productCategoryPersistence.getMaxPrdCategoryId());
    }

    public void testGetProductCategory() throws Exception {
       Assert.assertEquals(Integer.valueOf("0"), productCategoryPersistence.getProductCategory("product"));
    }


    public void testGetProductTypes() throws Exception {
        List<ProductTypeEntity> productTypeList = productCategoryPersistence.getProductTypes();
       Assert.assertEquals(2, productTypeList.size());
        for (ProductTypeEntity productTypeEntity : productTypeList) {
            productTypeEntity.setUserContext(TestUtils.makeUser());
            if (productTypeEntity.getType() == ProductType.LOAN) {
               Assert.assertEquals("Loan", productTypeEntity.getName());
            } else {
                //Assert.assertEquals("Margin Money",productTypeEntity.getName());
               Assert.assertEquals("Savings", productTypeEntity.getName());
            }
        }
    }

    public void testGetProductTypesByType() throws Exception {
        ProductTypeEntity productTypeEntity = productCategoryPersistence.getProductTypes(ProductType.LOAN.getValue());
        Assert.assertNotNull(productTypeEntity);
        if (productTypeEntity.getType() == ProductType.LOAN) {
           Assert.assertEquals("Loan", productTypeEntity.getName());
        } else {
           Assert.assertEquals("", productTypeEntity.getName());
        }
    }

    public void testGetPrdCategory() throws Exception {
       Assert.assertEquals(Integer.valueOf("0"), productCategoryPersistence.getProductCategory("product category",
                getProductCategory().get(0).getProductCategoryID()));
        ProductCategoryBO productCategoryBO = createProductCategory();
       Assert.assertEquals(Integer.valueOf("0"), productCategoryPersistence.getProductCategory("product category",
                getProductCategory().get(2).getProductCategoryID()));
       Assert.assertEquals(Integer.valueOf("1"), productCategoryPersistence.getProductCategory("product category",
                getProductCategory().get(1).getProductCategoryID()));
        deleteProductCategory(productCategoryBO);
    }

    public void testFindByGlobalNum() throws Exception {
        ProductCategoryBO productCategoryBO = createProductCategory();
        Assert.assertNotNull(productCategoryPersistence.findByGlobalNum(productCategoryBO.getGlobalPrdCategoryNum()));
        deleteProductCategory(productCategoryBO);
    }

    public void testGetProductCategoryStatusList() throws Exception {
       Assert.assertEquals(2, productCategoryPersistence.getProductCategoryStatusList().size());
    }

    public void testGetAllCategories() throws Exception {
       Assert.assertEquals(2, productCategoryPersistence.getAllCategories().size());
        ProductCategoryBO productCategoryBO = createProductCategory();
       Assert.assertEquals(3, productCategoryPersistence.getAllCategories().size());
        deleteProductCategory(productCategoryBO);
    }

    private List<ProductCategoryBO> getProductCategory() {
        return StaticHibernateUtil.getSessionTL().createQuery(
                "from " + ProductCategoryBO.class.getName() + " pcb order by pcb.productCategoryID").list();
    }

    private void deleteProductCategory(ProductCategoryBO productCategoryBO) {
        Session session = StaticHibernateUtil.getSessionTL();
        Transaction transaction = StaticHibernateUtil.startTransaction();
        session.delete(productCategoryBO);
        transaction.commit();
    }

    private ProductCategoryBO createProductCategory() throws Exception {
        userContext = TestUtils.makeUser();
        ProductCategoryBO productCategoryBO = new ProductCategoryBO(userContext, productCategoryPersistence
                .getProductTypes().get(0), "product category", "created a category");
        productCategoryBO.save();
        StaticHibernateUtil.commitTransaction();
        return getProductCategory().get(2);
    }

}
