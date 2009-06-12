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

package org.mifos.application.productsmix.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.application.productsmix.business.ProductMixBO;
import org.mifos.application.productsmix.business.service.ProductMixBusinessService;
import org.mifos.application.productsmix.struts.actionforms.ProductMixActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class ProductMixAction extends BaseAction {
    private MifosLogger prdLogger = MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

    @Override
    protected BusinessService getService() {
        return getPrdMixBusinessService();
    }

    private ProductMixBusinessService getPrdMixBusinessService() {
        return (ProductMixBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.PrdMix);

    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("productMixAction");
        security.allow("load", SecurityConstants.CAN_DEFINE_PRODUCT_MIX);
        security.allow("loadParent", SecurityConstants.CAN_DEFINE_PRODUCT_MIX);
        security.allow("loadProductInstance", SecurityConstants.CAN_DEFINE_PRODUCT_MIX);
        security.allow("loadDefaultAllowedProduct", SecurityConstants.CAN_DEFINE_PRODUCT_MIX);
        security.allow("get", SecurityConstants.CAN_DEFINE_PRODUCT_MIX);
        security.allow("preview", SecurityConstants.CAN_DEFINE_PRODUCT_MIX);
        security.allow("previous", SecurityConstants.CAN_DEFINE_PRODUCT_MIX);
        security.allow("editPreview_success", SecurityConstants.CAN_EDIT_PRODUCT_MIX);
        security.allow("create", SecurityConstants.CAN_DEFINE_PRODUCT_MIX);
        security.allow("manage", SecurityConstants.CAN_EDIT_PRODUCT_MIX);
        security.allow("update", SecurityConstants.CAN_EDIT_PRODUCT_MIX);
        security.allow("viewAllProductMix", SecurityConstants.CAN_DEFINE_PRODUCT_MIX);
        security.allow("editPreview", SecurityConstants.CAN_EDIT_PRODUCT_MIX);
        security.allow("cancelCreate", SecurityConstants.CAN_DEFINE_PRODUCT_MIX);
        security.allow("cancel", SecurityConstants.CAN_DEFINE_PRODUCT_MIX);
        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("validate", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);

        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        prdLogger.debug("start Load method of Product Mix Action");
        doCleanUp(request, form);
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, getProductTypes(userContext),
                request);
        prdLogger.debug("Load method of Product Mix Action called");
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadProductInstance(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;
        SessionUtils.removeAttribute(ProductDefinitionConstants.ALLOWEDPRODUCTLIST, request);
        SessionUtils.removeAttribute(ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST, request);
        SessionUtils.removeAttribute(ProductDefinitionConstants.PRODUCTINSTANCELIST, request);

        if (!StringUtils.isNullOrEmpty(prdMixActionForm.getProductType())
                && prdMixActionForm.getProductType().equals(ProductType.LOAN.getValue().toString())) {
            List<LoanOfferingBO> loanOffInstance = ((ProductMixBusinessService) getService())
                    .getLoanOfferingsNotMixed(getUserContext(request).getLocaleId());
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTINSTANCELIST, loanOffInstance,
                    request);

        } else if (!StringUtils.isNullOrEmpty(prdMixActionForm.getProductType())
                && prdMixActionForm.getProductType().equals(ProductType.SAVINGS.getValue().toString())) {
            List<SavingsOfferingBO> savingOffInstance = ((ProductMixBusinessService) getService())
                    .getSavingsOfferingsNotMixed(getUserContext(request).getLocaleId());
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTINSTANCELIST, savingOffInstance,
                    request);
        }

        return mapping.findForward(ActionForwards.loadParents_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadParent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;
        SessionUtils.removeAttribute(ProductDefinitionConstants.ALLOWEDPRODUCTLIST, request);
        SessionUtils.removeAttribute(ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST, request);
        SessionUtils.removeAttribute(ProductDefinitionConstants.PRODUCTINSTANCELIST, request);

        if (!StringUtils.isNullOrEmpty(prdMixActionForm.getProductType())
                && prdMixActionForm.getProductType().equals(ProductType.LOAN.getValue().toString())) {
            List<LoanOfferingBO> loanOffInstance = ((ProductMixBusinessService) getService())
                    .getAllLoanOfferings(getUserContext(request).getLocaleId());
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTINSTANCELIST, loanOffInstance,
                    request);

        } else if (!StringUtils.isNullOrEmpty(prdMixActionForm.getProductType())
                && prdMixActionForm.getProductType().equals(ProductType.SAVINGS.getValue().toString())) {
            List<SavingsOfferingBO> savingOffInstance = ((ProductMixBusinessService) getService())
                    .getAllSavingsProducts();
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTINSTANCELIST, savingOffInstance,
                    request);
        }

        return mapping.findForward(ActionForwards.loadParents_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        prdLogger.error("in validate");
        String method = (String) request.getAttribute(ProductDefinitionConstants.METHODCALLED);
        if (!StringUtils.isNullOrEmpty(method) && method.equalsIgnoreCase(Methods.preview.toString())) {
            prdLogger.error(" validate:::" + Methods.preview.toString());
            return mapping.findForward(Methods.preview.toString() + "_failure");
        }
        prdLogger.error("out validate");
        return mapping.findForward(ActionForwards.preview_failure.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, getPrdMixBusinessService().getPrdOfferingByID(
                Short.valueOf(prdMixActionForm.getProductInstance())), request);
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, getProductTypes(userContext),
                request);
        if (!StringUtils.isNullOrEmpty(prdMixActionForm.getProductType())
                && prdMixActionForm.getProductType().equals(ProductType.LOAN.getValue().toString())) {
            List<LoanOfferingBO> loanOffInstance = ((ProductMixBusinessService) getService())
                    .getAllLoanOfferings(getUserContext(request).getLocaleId());
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTINSTANCELIST, loanOffInstance,
                    request);

        } else if (!StringUtils.isNullOrEmpty(prdMixActionForm.getProductType())
                && prdMixActionForm.getProductType().equals(ProductType.SAVINGS.getValue().toString())) {
            List<SavingsOfferingBO> savingOffInstance = ((ProductMixBusinessService) getService())
                    .getAllSavingsProducts();
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTINSTANCELIST, savingOffInstance,
                    request);
        }

        if (!StringUtils.isNullOrEmpty(prdMixActionForm.getProductType())) {

            List<PrdOfferingBO> prdOfferingList = ((ProductMixBusinessService) getService())
                    .getAllowedPrdOfferingsByType(prdMixActionForm.getProductInstance(), prdMixActionForm
                            .getProductType());

            List<PrdOfferingBO> notAllowedPrdOfferingList = ((ProductMixBusinessService) getService())
                    .getNotAllowedPrdOfferingsByType(prdMixActionForm.getProductInstance());

            SessionUtils
                    .setCollectionAttribute(ProductDefinitionConstants.ALLOWEDPRODUCTLIST, prdOfferingList, request);
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST,
                    notAllowedPrdOfferingList, request);
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTOFFERINGLIST, prdOfferingList,
                    request);
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.OLDNOTALLOWEDPRODUCTLIST,
                    notAllowedPrdOfferingList, request);
        }

        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadDefaultAllowedProduct(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, getPrdMixBusinessService().getPrdOfferingByID(
                Short.valueOf(prdMixActionForm.getProductInstance())), request);

        if (!StringUtils.isNullOrEmpty(prdMixActionForm.getProductType())) {

            List<PrdOfferingBO> prdOfferingList = ((ProductMixBusinessService) getService())
                    .getAllowedPrdOfferingsForMixProduct(prdMixActionForm.getProductInstance(), prdMixActionForm
                            .getProductType());

            List<PrdOfferingBO> notAllowedPrdOfferingList = ((ProductMixBusinessService) getService())
                    .getNotAllowedPrdOfferingsForMixProduct(prdMixActionForm.getProductInstance(), prdMixActionForm
                            .getProductType());

            SessionUtils
                    .setCollectionAttribute(ProductDefinitionConstants.ALLOWEDPRODUCTLIST, prdOfferingList, request);
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST,
                    notAllowedPrdOfferingList, request);
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.OLDNOTALLOWEDPRODUCTLIST,
                    notAllowedPrdOfferingList, request);
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTOFFERINGLIST, prdOfferingList,
                    request);

        }
        return mapping.findForward(ActionForwards.loadParents_success.toString());
    }

    private void updateAllowedLists(HttpServletRequest request, ProductMixActionForm prdMixActionForm)
            throws PageExpiredException {

        boolean addFlag = false;
        List<PrdOfferingBO> selectList = new ArrayList<PrdOfferingBO>();
        if (prdMixActionForm.getProductMix() != null) {
            List<PrdOfferingBO> masterList = (List<PrdOfferingBO>) SessionUtils.getAttribute(
                    ProductDefinitionConstants.PRODUCTOFFERINGLIST, request);

            for (PrdOfferingBO product : masterList) {
                for (String productId : prdMixActionForm.getProductMix()) {
                    if (productId != null
                            && product.getPrdOfferingId().intValue() == Integer.valueOf(productId).intValue()) {
                        selectList.add(product);
                        addFlag = true;
                    }
                }
            }
        }
        if (addFlag) {
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.ALLOWEDPRODUCTLIST, selectList, request);
            List<PrdOfferingBO> notAllowedProductList = (List<PrdOfferingBO>) SessionUtils.getAttribute(
                    ProductDefinitionConstants.PRODUCTOFFERINGLIST, request);
            notAllowedProductList.removeAll(selectList);
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST,
                    notAllowedProductList, request);
        } else {
            SessionUtils.setAttribute(ProductDefinitionConstants.ALLOWEDPRODUCTLIST, null, request);
            List<PrdOfferingBO> notAllowedProductList = (List<PrdOfferingBO>) SessionUtils.getAttribute(
                    ProductDefinitionConstants.PRODUCTOFFERINGLIST, request);

            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST,
                    notAllowedProductList, request);
        }
    }

    private List<ProductTypeEntity> getProductTypes(UserContext userContext) throws Exception {

        List<ProductTypeEntity> productTypeList = getPrdMixBusinessService().getProductTypes();
        ProductTypeEntity savingsProductEntity = null;
        for (ProductTypeEntity productTypeEntity : productTypeList) {
            productTypeEntity.setUserContext(userContext);

            if (productTypeEntity.getProductTypeID().equals(ProductType.SAVINGS.getValue())) {
                savingsProductEntity = productTypeEntity;
            }
        }
        productTypeList.remove(savingsProductEntity);
        return productTypeList;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;

        if (!StringUtils.isNullOrEmpty(prdMixActionForm.getProductType())) {
            List<PrdOfferingBO> prdOfferingList = ((ProductMixBusinessService) getService())
                    .getAllPrdOfferingsByType(prdMixActionForm.getProductType());

            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTOFFERINGLIST, prdOfferingList,
                    request);

        }
        checkBeforeUpdate(form, request);
        updateAllowedLists(request, prdMixActionForm);
        return mapping.findForward(ActionForwards.editpreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;

        loadDefaultAllowedProduct(mapping, form, request, response);
        checkBeforeUpdate(form, request);
        if (!StringUtils.isNullOrEmpty(prdMixActionForm.getProductType())) {
            List<PrdOfferingBO> prdOfferingList = ((ProductMixBusinessService) getService())
                    .getAllPrdOfferingsByType(prdMixActionForm.getProductType());

            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTOFFERINGLIST, prdOfferingList,
                    request);

        }
        checkBeforeUpdate(form, request);
        updateAllowedLists(request, prdMixActionForm);
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    private void checkBeforeUpdate(ActionForm form, HttpServletRequest request) throws PageExpiredException,
            NumberFormatException, ServiceException {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;
        List<PrdOfferingBO> notAllowedProductList = (List<PrdOfferingBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST, request);
        PrdOfferingBO productOff = getPrdMixBusinessService().getPrdOfferingByID(
                Short.valueOf(prdMixActionForm.getProductInstance()));
        productOff.setUserContext(getUserContext(request));
        List<PrdOfferingBO> tabAllowed = new ArrayList<PrdOfferingBO>();
        boolean showMessage = false;
        List<PrdOfferingBO> oldNotAllowedProductList = (List<PrdOfferingBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.OLDNOTALLOWEDPRODUCTLIST, request);

        for (PrdOfferingBO prdOff : oldNotAllowedProductList) {
            for (PrdOfferingBO prdOff2 : notAllowedProductList) {
                if (prdOff.getPrdOfferingId() == prdOff2.getPrdOfferingId()) {
                    tabAllowed.add(prdOff);
                    showMessage = true;

                }

            }
        }

        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.TABALLOWED, tabAllowed, request);
        SessionUtils.setAttribute(ProductDefinitionConstants.SHOWMESSAGE, showMessage, request);

    }

    private void doCleanUp(HttpServletRequest request, ActionForm form) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;
        prdMixActionForm.doCleanUp();
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.removeAttribute(ProductDefinitionConstants.ALLOWEDPRODUCTLIST, request);
        SessionUtils.removeAttribute(ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST, request);
        SessionUtils.removeAttribute(ProductDefinitionConstants.PRODUCTOFFERINGLIST, request);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;
        prdMixActionForm.setProductMix(null);
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPreview_success(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;
        prdMixActionForm.setProductMix(null);
        return mapping.findForward(ActionForwards.edit_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;
        PrdOfferingBO prdOfferingBO = getPrdMixBusinessService().getPrdOfferingByID(
                Short.valueOf(prdMixActionForm.getPrdOfferingId()));
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, prdOfferingBO, request);
        if (!StringUtils.isNullOrEmpty(prdOfferingBO.getPrdType().getProductTypeID().toString())) {

            List<PrdOfferingBO> prdOfferingList = ((ProductMixBusinessService) getService())
                    .getAllowedPrdOfferingsByType(prdMixActionForm.getPrdOfferingId(), prdMixActionForm
                            .getProductType());
            List<PrdOfferingBO> notAllowedPrdOfferingList = ((ProductMixBusinessService) getService())
                    .getNotAllowedPrdOfferingsByType(prdMixActionForm.getPrdOfferingId());
            SessionUtils
                    .setCollectionAttribute(ProductDefinitionConstants.ALLOWEDPRODUCTLIST, prdOfferingList, request);
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST,
                    notAllowedPrdOfferingList, request);
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTOFFERINGLIST, prdOfferingList,
                    request);
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.OLDPRODUCTOFFERINGLIST,
                    notAllowedPrdOfferingList, request);
        }

        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelCreate_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelCreate_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward viewAllProductMix(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());
        List<PrdOfferingBO> listPrdOfferingBO = getPrdMixBusinessService().getPrdOfferingMix();
        if (listPrdOfferingBO != null) {
            for (PrdOfferingBO prdOfferingBO : listPrdOfferingBO) {
                prdOfferingBO.getPrdCategory().getPrdCategoryStatus().setLocaleId(userContext.getLocaleId());
                prdOfferingBO.getPrdCategory().getProductType().setUserContext(userContext);
            }
        }
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTCATEGORYLIST,
                getAllCategories(userContext), request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTMIXLIST, getPrdMixBusinessService()
                .getPrdOfferingMix(), request);

        return mapping.findForward(ActionForwards.viewAllProductMix_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;
        PrdOfferingBO prdOfferingBO = getPrdMixBusinessService().getPrdOfferingByID(
                Short.valueOf(prdMixActionForm.getProductInstance()));

        prdOfferingBO.updatePrdOfferingFlag();

        List<PrdOfferingBO> notAllowedProductList = (List<PrdOfferingBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST, request);
        PrdOfferingBO productOff = getPrdMixBusinessService().getPrdOfferingByID(
                Short.valueOf(prdMixActionForm.getProductInstance()));
        productOff.setUserContext(getUserContext(request));
        List<PrdOfferingBO> oldNotAllowedProductList = (List<PrdOfferingBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.OLDNOTALLOWEDPRODUCTLIST, request);
        if (null != oldNotAllowedProductList && oldNotAllowedProductList.size() != 0) {
            for (PrdOfferingBO oldnotallowedProduct : oldNotAllowedProductList) {

                ProductMixBO product = getPrdMixBusinessService().getPrdOfferingMixByPrdOfferingID(
                        productOff.getPrdOfferingId(), oldnotallowedProduct.getPrdOfferingId());
                if (null != product)
                    product.delete();
                ProductMixBO productmix = getPrdMixBusinessService().getPrdOfferingMixByPrdOfferingID(
                        oldnotallowedProduct.getPrdOfferingId(), productOff.getPrdOfferingId());
                if (null != productmix)
                    productmix.delete();
            }
        }

        if (null != notAllowedProductList) {
            for (PrdOfferingBO notallowedProduct : notAllowedProductList) {
                ProductMixBO product = new ProductMixBO(productOff, notallowedProduct);
                product.setUserContext(getUserContext(request));
                product.update();
            }
        }

        request.setAttribute(ProductDefinitionConstants.PRDOFFERINGBO, prdOfferingBO);

        prdOfferingBO = null;
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProductMixActionForm prdMixActionForm = (ProductMixActionForm) form;
        PrdOfferingBO prdOfferingBO = getPrdMixBusinessService().getPrdOfferingByID(
                Short.valueOf(prdMixActionForm.getProductInstance()));

        prdOfferingBO.updatePrdOfferingFlag();
        List<PrdOfferingBO> notAllowedProductList = (List<PrdOfferingBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.NOTALLOWEDPRODUCTLIST, request);
        PrdOfferingBO productOff = getPrdMixBusinessService().getPrdOfferingByID(
                Short.valueOf(prdMixActionForm.getProductInstance()));
        productOff.setUserContext(getUserContext(request));

        List<PrdOfferingBO> oldNotAllowedProductList = (List<PrdOfferingBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.OLDNOTALLOWEDPRODUCTLIST, request);
        if (null != oldNotAllowedProductList && oldNotAllowedProductList.size() != 0) {
            for (PrdOfferingBO oldnotallowedProduct : oldNotAllowedProductList) {

                ProductMixBO product = getPrdMixBusinessService().getPrdOfferingMixByPrdOfferingID(
                        productOff.getPrdOfferingId(), oldnotallowedProduct.getPrdOfferingId());
                // product.setUserContext(getUserContext(request));
                if (null != product)
                    product.delete();
                ProductMixBO productmix = getPrdMixBusinessService().getPrdOfferingMixByPrdOfferingID(
                        oldnotallowedProduct.getPrdOfferingId(), productOff.getPrdOfferingId());
                if (null != productmix)
                    productmix.delete();

            }
        }

        if (null != notAllowedProductList) {
            for (PrdOfferingBO notallowedProduct : notAllowedProductList) {
                ProductMixBO product = new ProductMixBO(productOff, notallowedProduct);
                product.setUserContext(getUserContext(request));
                product.update();

            }
        }

        prdOfferingBO = null;
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    private List<ProductCategoryBO> getAllCategories(UserContext userContext) throws Exception {
        List<ProductCategoryBO> productCategoryList = getCategoryBusinessService().getAllCategories();
        if (productCategoryList != null) {
            for (ProductCategoryBO productCategoryBO : productCategoryList) {
                productCategoryBO.getPrdCategoryStatus().setLocaleId(userContext.getLocaleId());
                productCategoryBO.getProductType().setUserContext(userContext);
            }
        }
        return productCategoryList;
    }

    private ProductCategoryBusinessService getCategoryBusinessService() {
        return new ProductCategoryBusinessService();
    }
}
