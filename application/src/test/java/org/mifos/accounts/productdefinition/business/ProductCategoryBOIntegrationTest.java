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

package org.mifos.accounts.productdefinition.business;

import java.util.List;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.accounts.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ProductCategoryBOIntegrationTest extends MifosIntegrationTestCase {

    public ProductCategoryBOIntegrationTest() throws Exception {
        super();
    }

    public void testCreateProductCategory() throws SystemException, ApplicationException {
        UserContext userContext = TestUtils.makeUser();
        List<ProductTypeEntity> productTypeList = new ProductCategoryBusinessService().getProductTypes();
       Assert.assertEquals(2, productTypeList.size());
        TestObjectFactory.updateObject(new ProductCategoryBO(userContext, productTypeList.get(0), "product category",
                null));
        ProductCategoryBO productCategoryBO = getProductCategory().get(2);
       Assert.assertEquals("1-003", productCategoryBO.getGlobalPrdCategoryNum());
       Assert.assertEquals("product category", productCategoryBO.getProductCategoryName());
        Assert.assertNull(productCategoryBO.getProductCategoryDesc());
        Assert.assertNotNull(productCategoryBO.getProductType());
        try {
            TestObjectFactory.updateObject(new ProductCategoryBO(userContext, productTypeList.get(0),
                    "product category", null));
            Assert.fail();
        } catch (ProductDefinitionException expected) {
           Assert.assertEquals(ProductDefinitionConstants.DUPLICATE_CATEGORY_NAME, expected.getKey());
        }
        deleteProductCategory(productCategoryBO);
    }

    public void testUpdateProductCategory() throws SystemException, ApplicationException {
        UserContext userContext = TestUtils.makeUser();
        List<ProductTypeEntity> productTypeList = new ProductCategoryBusinessService().getProductTypes();
       Assert.assertEquals(2, productTypeList.size());
        TestObjectFactory.updateObject(new ProductCategoryBO(userContext, productTypeList.get(0), "product category",
                null));
        ProductCategoryBO productCategoryBO = getProductCategory().get(2);
        PrdCategoryStatusEntity prdCategoryStatusEntity = (PrdCategoryStatusEntity) TestObjectFactory.getObject(
                PrdCategoryStatusEntity.class, PrdCategoryStatus.INACTIVE.getValue());
        productCategoryBO.updateProductCategory("Category", "Name changed", prdCategoryStatusEntity);
        TestObjectFactory.updateObject(productCategoryBO);
        productCategoryBO = getProductCategory().get(2);
       Assert.assertEquals("1-003", productCategoryBO.getGlobalPrdCategoryNum());
       Assert.assertEquals("Category", productCategoryBO.getProductCategoryName());
        Assert.assertNotNull(productCategoryBO.getProductCategoryDesc());
        Assert.assertNotNull(productCategoryBO.getProductType());
        Assert.assertNotNull("Name changed", productCategoryBO.getProductCategoryDesc());
        productCategoryBO.updateProductCategory("Category", "Name changed", prdCategoryStatusEntity);
        // TODO: assert that it got updated.
        deleteProductCategory(productCategoryBO);
    }

    private List<ProductCategoryBO> getProductCategory() {
        return StaticHibernateUtil.getSessionTL().createQuery(
                "from org.mifos.accounts.productdefinition.business.ProductCategoryBO pcb "
                        + "order by pcb.productCategoryID").list();
    }

    private void deleteProductCategory(ProductCategoryBO productCategoryBO) {
        Session session = StaticHibernateUtil.getSessionTL();
        Transaction transaction = StaticHibernateUtil.startTransaction();
        session.delete(productCategoryBO);
        transaction.commit();
    }

}
