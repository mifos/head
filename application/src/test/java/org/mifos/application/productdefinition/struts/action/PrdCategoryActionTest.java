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

package org.mifos.application.productdefinition.struts.action;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.productdefinition.business.PrdCategoryStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.persistence.ProductCategoryPersistence;
import org.mifos.application.productdefinition.struts.actionforms.PrdCategoryActionForm;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class PrdCategoryActionTest extends MifosMockStrutsTestCase {

    public PrdCategoryActionTest() throws SystemException, ApplicationException {
        super();
    }

    private UserContext userContext;

    private ProductCategoryPersistence productCategoryPersistence;

    private String flowKey;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        productCategoryPersistence = new ProductCategoryPersistence();

        flowKey = createFlow(request, PrdCategoryAction.class);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
    }

    @Override
    protected void tearDown() throws Exception {
        productCategoryPersistence = null;
        userContext = null;
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoad() throws PersistenceException, PageExpiredException {
        setRequestPathInfo("/productCategoryAction.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("load_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        assertEquals(2, ((List<ProductTypeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.PRODUCTTYPELIST, request)).size());
    }

    public void testCreatePreview() throws PersistenceException, PageExpiredException {
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, getProductTypes(userContext),
                request);
        setRequestPathInfo("/productCategoryAction.do");
        addRequestParameter("method", "createPreview");
        addRequestParameter("productType", "1");
        addRequestParameter("productCategoryName", "product category");
        addRequestParameter("productCategoryDesc", "created a category");
        addRequestParameter("productCategoryStatus", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        performNoErrors();
        verifyForward("preview_success");
        List<ProductTypeEntity> productTypes = (List<ProductTypeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.PRODUCTTYPELIST, request);
        assertEquals(2, productTypes.size());
    }

    public void testCreatePrevious() throws PersistenceException, PageExpiredException {
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, getProductTypes(userContext),
                request);
        setRequestPathInfo("/productCategoryAction.do");
        addRequestParameter("method", "createPrevious");
        addRequestParameter("productType", "1");
        addRequestParameter("productCategoryName", "product category");
        addRequestParameter("productCategoryDesc", "created a category");
        addRequestParameter("productCategoryStatus", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("previous_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        assertEquals(2, ((List<ProductTypeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.PRODUCTTYPELIST, request)).size());
    }

    public void testCreate() throws PersistenceException, PageExpiredException {
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, getProductTypes(userContext),
                request);
        setRequestPathInfo("/productCategoryAction.do");
        addRequestParameter("method", "create");
        addRequestParameter("productType", "1");
        addRequestParameter("productCategoryName", "product category");
        addRequestParameter("productCategoryDesc", "created a category");
        addRequestParameter("productCategoryStatus", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        performNoErrors();
        verifyForward("create_success");
        ProductCategoryBO productCategoryBO = getProductCategory().get(2);
        assertEquals("product category", productCategoryBO.getProductCategoryName());
        assertEquals("created a category", productCategoryBO.getProductCategoryDesc());
        deleteProductCategory(productCategoryBO);
    }

    public void testGet() throws Exception {
        ProductCategoryBO productCategoryBO = new ProductCategoryBO(userContext, getProductTypes(userContext).get(0),
                "product category", "created a category");
        productCategoryBO.save();
        setRequestPathInfo("/productCategoryAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalPrdCategoryNum", productCategoryBO.getGlobalPrdCategoryNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("get_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        assertEquals(2, ((List<ProductTypeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.PRODUCTTYPELIST, request)).size());
        productCategoryBO = getProductCategory().get(2);
        deleteProductCategory(productCategoryBO);
    }

    public void testManage() throws Exception {
        ProductCategoryBO productCategoryBO = new ProductCategoryBO(userContext, getProductTypes(userContext).get(0),
                "product category", "created a category");
        productCategoryBO.save();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, productCategoryBO, request);
        setRequestPathInfo("/productCategoryAction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("manage_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        assertEquals(2, ((List<PrdCategoryStatusEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.PRDCATEGORYSTATUSLIST, request)).size());
        productCategoryBO = getProductCategory().get(2);
        assertEquals("product category", ((PrdCategoryActionForm) getActionForm()).getProductCategoryName());
        assertEquals("created a category", ((PrdCategoryActionForm) getActionForm()).getProductCategoryDesc());
        deleteProductCategory(productCategoryBO);
    }

    public void testManagePreview() throws Exception {
        ProductCategoryBO productCategoryBO = new ProductCategoryBO(userContext, getProductTypes(userContext).get(0),
                "product category", "created a category");
        productCategoryBO.save();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, productCategoryBO, request);
        setRequestPathInfo("/productCategoryAction.do");
        addRequestParameter("method", "managePreview");
        addRequestParameter("productCategoryName", "product category 1");
        addRequestParameter("productCategoryDesc", "created a category 1");
        addRequestParameter("productType", productCategoryBO.getProductType().getProductTypeID().toString());
        addRequestParameter("productCategoryStatus", productCategoryBO.getPrdCategoryStatus().getId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("editpreview_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        productCategoryBO = getProductCategory().get(2);
        assertEquals("product category 1", ((PrdCategoryActionForm) getActionForm()).getProductCategoryName());
        assertEquals("created a category 1", ((PrdCategoryActionForm) getActionForm()).getProductCategoryDesc());
        deleteProductCategory(productCategoryBO);
    }

    public void testManagePrevious() throws Exception {
        ProductCategoryBO productCategoryBO = new ProductCategoryBO(userContext, getProductTypes(userContext).get(0),
                "product category", "created a category");
        productCategoryBO.save();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, productCategoryBO, request);
        setRequestPathInfo("/productCategoryAction.do");
        addRequestParameter("method", "managePrevious");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("editprevious_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        productCategoryBO = getProductCategory().get(2);
        deleteProductCategory(productCategoryBO);
    }

    public void testUpdate() throws Exception {
        ProductCategoryBO productCategoryBO = new ProductCategoryBO(userContext, getProductTypes(userContext).get(0),
                "product category", "created a category");
        productCategoryBO.save();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, productCategoryBO, request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRDCATEGORYSTATUSLIST,
                getProductCategoryStatusList(userContext), request);
        setRequestPathInfo("/productCategoryAction.do");
        addRequestParameter("method", "update");
        addRequestParameter("productCategoryName", "product category 1");
        addRequestParameter("productCategoryDesc", "created a category 1");
        addRequestParameter("productType", "1");
        addRequestParameter("productCategoryStatus", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("update_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        productCategoryBO = getProductCategory().get(2);
        assertEquals("product category 1", productCategoryBO.getProductCategoryName());
        assertEquals("created a category 1", productCategoryBO.getProductCategoryDesc());
        deleteProductCategory(productCategoryBO);
    }

    public void testGetAllCategories() throws Exception {
        ProductCategoryBO productCategoryBO = new ProductCategoryBO(userContext, getProductTypes(userContext).get(0),
                "product category", "created a category");
        productCategoryBO.save();
        setRequestPathInfo("/productCategoryAction.do");
        addRequestParameter("method", "getAllCategories");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("search_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        productCategoryBO = getProductCategory().get(2);
        assertEquals(3, ((List<ProductCategoryBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.PRODUCTCATEGORYLIST, request)).size());
        deleteProductCategory(productCategoryBO);
    }

    public void testPrdDefException() throws Exception {
        ProductCategoryBO productCategoryBO = new ProductCategoryBO(userContext, getProductTypes(userContext).get(0),
                "product category", "created a category");
        TestObjectFactory.simulateInvalidConnection();
        try {
            productCategoryBO.save();
            fail();
        } catch (ProductDefinitionException pde) {
        }
    }

    private List<ProductTypeEntity> getProductTypes(UserContext userContext) throws PersistenceException {
        List<ProductTypeEntity> productCategoryList = productCategoryPersistence.getProductTypes();
        for (ProductTypeEntity productTypeEntity : productCategoryList)
            productTypeEntity.setUserContext(userContext);
        return productCategoryList;
    }

    private List<PrdCategoryStatusEntity> getProductCategoryStatusList(UserContext userContext)
            throws PersistenceException {
        List<PrdCategoryStatusEntity> productCategoryStatusList = productCategoryPersistence
                .getProductCategoryStatusList();
        for (PrdCategoryStatusEntity prdCategoryStatusEntity : productCategoryStatusList)
            prdCategoryStatusEntity.setLocaleId(userContext.getLocaleId());
        return productCategoryStatusList;
    }

    private List<ProductCategoryBO> getProductCategory() {
        return StaticHibernateUtil
                .getSessionTL()
                .createQuery(
                        "from org.mifos.application.productdefinition.business.ProductCategoryBO pcb order by pcb.productCategoryID")
                .list();
    }

    private void deleteProductCategory(ProductCategoryBO productCategoryBO) {
        Session session = StaticHibernateUtil.getSessionTL();
        Transaction transaction = StaticHibernateUtil.startTransaction();
        session.delete(productCategoryBO);
        transaction.commit();
    }

}
