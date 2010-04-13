/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.checklist.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.checklist.business.CheckListBO;
import org.mifos.customers.checklist.business.CheckListDetailEntity;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.checklist.business.service.CheckListBusinessService;
import org.mifos.customers.checklist.struts.actionforms.ChkListActionForm;
import org.mifos.customers.checklist.util.helpers.CheckListConstants;
import org.mifos.customers.checklist.util.helpers.CheckListMasterView;
import org.mifos.customers.checklist.util.helpers.CheckListStatesView;
import org.mifos.customers.checklist.util.helpers.CheckListType;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;

public class ChkListAction extends BaseAction {

    @Override
    protected BusinessService getService() throws ServiceException {
        return new CheckListBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("chkListAction");
        security.allow("load", SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
        security.allow("getStates", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
        security.allow("create", SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
        security.allow("cancelCreate", SecurityConstants.VIEW);
        security.allow("cancelManage", SecurityConstants.VIEW);

        security.allow("manage", SecurityConstants.CHECKLIST_EDIT_CHECKLIST);
        security.allow("getEditStates", SecurityConstants.VIEW);
        security.allow("managePreview", SecurityConstants.VIEW);
        security.allow("managePrevious", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.CHECKLIST_EDIT_CHECKLIST);

        security.allow("loadAllChecklist", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<CustomerStatusEntity> statesData = null;
        List<String> details = null;
        request.getSession().setAttribute("ChkListActionForm", null);
        List<CheckListMasterView> masterData = ((CheckListBusinessService) getService())
                .getCheckListMasterData(getUserContext(request));
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);
        SessionUtils.setCollectionAttribute(CheckListConstants.STATES, statesData, request);
        SessionUtils.setCollectionAttribute(CheckListConstants.CHECKLIST_MASTERDATA, masterData, request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getStates(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        List<String> details = chkListActionForm.getValidCheckListDetails();
        List<CheckListStatesView> states = getStates(chkListActionForm, request);
        SessionUtils.setCollectionAttribute(CheckListConstants.STATES, states, request);
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        List<String> details = chkListActionForm.getValidCheckListDetails();
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);
        isValidCheckListState(getShortValue(chkListActionForm.getMasterTypeId()), getShortValue(chkListActionForm
                .getStateId()), chkListActionForm.getIsCustomer());

        return mapping.findForward(ActionForwards.preview_success.toString());

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        List<String> details = chkListActionForm.getValidCheckListDetails();
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request.getSession());
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        if (chkListActionForm.getIsCustomer()) {
            CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(CustomerLevel
                    .getLevel(getShortValue(chkListActionForm.getMasterTypeId())));
            CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(getShortValue(chkListActionForm
                    .getStateId()));
            CustomerCheckListBO customerCheckListBO = new CustomerCheckListBO(customerLevelEntity,
                    customerStatusEntity, chkListActionForm.getChecklistName(), CheckListConstants.STATUS_ACTIVE,
                    chkListActionForm.getValidCheckListDetails(), getUserContext(request).getLocaleId(),
                    getUserContext(request).getId());
            customerCheckListBO.save();
        } else {
            ProductTypeEntity productTypeEntity = null;
            for (ProductTypeEntity prdTypeEntity : new ProductCategoryBusinessService().getProductTypes()) {
                if (chkListActionForm.getMasterTypeId().equals(getStringValue(prdTypeEntity.getProductTypeID()))) {
                    productTypeEntity = prdTypeEntity;
                    break;
                }
            }
            AccountStateEntity accountStateEntity = new AccountStateEntity(AccountState
                    .fromShort(getShortValue(chkListActionForm.getStateId())));
            AccountCheckListBO accountCheckListBO = new AccountCheckListBO(productTypeEntity, accountStateEntity,
                    chkListActionForm.getChecklistName(), CheckListConstants.STATUS_ACTIVE, chkListActionForm
                            .getValidCheckListDetails(), getUserContext(request).getLocaleId(), getUserContext(request)
                            .getId());
            accountCheckListBO.save();
        }
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelCreate_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward loadAllChecklist(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        request.getSession().setAttribute("ChkListActionForm", null);
        List<CustomerCheckListBO> customerCheckLists = ((CheckListBusinessService) getService())
                .retreiveAllCustomerCheckLists();
        List<AccountCheckListBO> accountCheckLists = ((CheckListBusinessService) getService())
                .retreiveAllAccountCheckLists();
        Short localeId = getUserContext(request).getLocaleId();
        SessionUtils.setCollectionAttribute(CheckListConstants.CENTER_CHECKLIST, getCustomerCheckLists(
                customerCheckLists, CustomerLevel.CENTER, localeId), request);
        SessionUtils.setCollectionAttribute(CheckListConstants.GROUP_CHECKLIST, getCustomerCheckLists(
                customerCheckLists, CustomerLevel.GROUP, localeId), request);
        SessionUtils.setCollectionAttribute(CheckListConstants.CLIENT_CHECKLIST, getCustomerCheckLists(
                customerCheckLists, CustomerLevel.CLIENT, localeId), request);

        SessionUtils.setCollectionAttribute(CheckListConstants.LOAN_CHECKLIST, getAccountCheckLists(accountCheckLists,
                ProductType.LOAN, localeId), request);
        SessionUtils.setCollectionAttribute(CheckListConstants.SAVINGS_CHECKLIST, getAccountCheckLists(
                accountCheckLists, ProductType.SAVINGS, localeId), request);
        return mapping.findForward(ActionForwards.loadAllChecklist_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        Short localeId = getUserContext(request).getLocaleId();
        CheckListBO checkList = ((CheckListBusinessService) getService()).getCheckList(getShortValue(chkListActionForm
                .getCheckListId()));
        if (checkList.getCheckListType().equals(CheckListType.CUSTOMER_CHECKLIST)) {
            CustomerCheckListBO customerCheckList = (CustomerCheckListBO) checkList;
            customerCheckList.getCustomerStatus().setLocaleId(localeId);
            customerCheckList.getCustomerLevel().setLocaleId(localeId);
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerCheckList, request);
            SessionUtils.setAttribute(CheckListConstants.TYPE, CheckListType.CUSTOMER_CHECKLIST.getValue(), request);
        } else {
            AccountCheckListBO accountCheckList = (AccountCheckListBO) checkList;
            accountCheckList.getAccountStateEntity().setLocaleId(localeId);
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountCheckList, request);
            SessionUtils.setAttribute(CheckListConstants.TYPE, CheckListType.ACCOUNT_CHECKLIST.getValue(), request);
        }
        SessionUtils.setAttribute(CheckListConstants.CREATED_BY_NAME, new PersonnelBusinessService().getPersonnel(
                checkList.getCreatedBy()).getDisplayName(), request);

        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        CheckListBO checkList = ((CheckListBusinessService) getService()).getCheckList(getShortValue(chkListActionForm
                .getCheckListId()));
        if (checkList.getCheckListType().equals(CheckListType.CUSTOMER_CHECKLIST)) {
            setValuesInForm(chkListActionForm, checkList, request);
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, checkList, request);
            SessionUtils.setAttribute(CheckListConstants.TYPE, CheckListType.CUSTOMER_CHECKLIST.getValue(), request);
        } else {
            setValuesInForm(chkListActionForm, checkList, request);
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, checkList, request);
            SessionUtils.setAttribute(CheckListConstants.TYPE, CheckListType.ACCOUNT_CHECKLIST.getValue(), request);
        }

        List<CheckListMasterView> masterData = ((CheckListBusinessService) getService())
                .getCheckListMasterData(getUserContext(request));
        SessionUtils.setCollectionAttribute(CheckListConstants.CHECKLIST_MASTERDATA, masterData, request);
        List<CheckListStatesView> states = getStates(chkListActionForm, request);
        SessionUtils.setCollectionAttribute(CheckListConstants.STATES, states, request);
        List<String> details = chkListActionForm.getValidCheckListDetails();
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);
        SessionUtils.setAttribute(CheckListConstants.OLDCHECKLISTNAME, checkList.getChecklistName(), request);
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getEditStates(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;

        List<String> details = chkListActionForm.getValidCheckListDetails();
        List<CheckListStatesView> states = getStates(chkListActionForm, request);
        SessionUtils.setCollectionAttribute(CheckListConstants.STATES, states, request);
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward managePreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        List<String> details = chkListActionForm.getValidCheckListDetails();
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);
        return mapping.findForward(ActionForwards.managepreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward managePrevious(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.manageprevious_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        if (chkListActionForm.getIsCustomer()) {
            CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(CustomerLevel
                    .getLevel(getShortValue(chkListActionForm.getMasterTypeId())));
            CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(getShortValue(chkListActionForm
                    .getStateId()));
            CustomerCheckListBO customerCheckList = (CustomerCheckListBO) SessionUtils.getAttribute(
                    Constants.BUSINESS_KEY, request);
            customerCheckList.update(customerLevelEntity, customerStatusEntity, chkListActionForm.getChecklistName(),
                    getShortValue(chkListActionForm.getChecklistStatus()),
                    chkListActionForm.getValidCheckListDetails(), getUserContext(request).getLocaleId(),
                    getUserContext(request).getId());
        } else {
            ProductTypeEntity productTypeEntity = null;
            for (ProductTypeEntity prdTypeEntity : new ProductCategoryBusinessService().getProductTypes()) {
                if (chkListActionForm.getMasterTypeId().equals(getStringValue(prdTypeEntity.getProductTypeID()))) {
                    productTypeEntity = prdTypeEntity;
                    break;
                }
            }
            AccountStateEntity accountStateEntity = new AccountStateEntity(AccountState
                    .fromShort(getShortValue(chkListActionForm.getStateId())));
            AccountCheckListBO accountCheckList = (AccountCheckListBO) SessionUtils.getAttribute(
                    Constants.BUSINESS_KEY, request);

            accountCheckList.update(productTypeEntity, accountStateEntity, chkListActionForm.getChecklistName(),
                    getShortValue(chkListActionForm.getChecklistStatus()),
                    chkListActionForm.getValidCheckListDetails(), getUserContext(request).getLocaleId(),
                    getUserContext(request).getId());
        }
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelManage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelEdit_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        String method = (String) request.getAttribute("methodCalled");

        if (method.equalsIgnoreCase(Methods.preview.toString())) {
            return mapping.findForward(ActionForwards.load_success.toString());
        }
        if (method.equalsIgnoreCase(Methods.managePreview.toString())) {
            return mapping.findForward(ActionForwards.manage_success.toString());
        }
        if (method.equalsIgnoreCase(Methods.create.toString())) {
            return mapping.findForward(ActionForwards.preview_success.toString());
        }
        if (method.equalsIgnoreCase(Methods.update.toString())) {
            return mapping.findForward(ActionForwards.managepreview_success.toString());
        }
        return null;
    }

    private List<CustomerCheckListBO> getCustomerCheckLists(List<CustomerCheckListBO> checkLists, CustomerLevel level,
            Short localeId) {
        List<CustomerCheckListBO> customerCheckLists = new ArrayList<CustomerCheckListBO>();
        if (checkLists != null && checkLists.size() > 0) {
            for (CustomerCheckListBO checkList : checkLists) {
                if (checkList.getCustomerLevel().getId().equals(level.getValue())) {
                    checkList.getCustomerStatus().setLocaleId(localeId);
                    customerCheckLists.add(checkList);
                }
            }
        }
        return customerCheckLists;
    }

    private List<AccountCheckListBO> getAccountCheckLists(List<AccountCheckListBO> checkLists, ProductType productType,
            Short localeId) {
        List<AccountCheckListBO> accountCheckLists = new ArrayList<AccountCheckListBO>();
        if (checkLists != null && checkLists.size() > 0) {
            for (AccountCheckListBO checkList : checkLists) {
                if (checkList.getProductTypeEntity().getProductTypeID().equals(productType.getValue())) {
                    checkList.getAccountStateEntity().setLocaleId(localeId);
                    accountCheckLists.add(checkList);
                }
            }
        }
        return accountCheckLists;
    }

    private void isValidCheckListState(Short levelId, Short customerState, boolean isCustomer) throws Exception {
        ((CheckListBusinessService) getService()).isValidCheckListState(levelId, customerState, isCustomer);
    }

    private void setValuesInForm(ChkListActionForm form, CheckListBO checkList, HttpServletRequest request) {
        form.setCheckListId(getStringValue(checkList.getChecklistId()));
        form.setChecklistName(checkList.getChecklistName());
        if (checkList.getCheckListType().equals(CheckListType.CUSTOMER_CHECKLIST)) {
            form.setMasterTypeId(getStringValue(((CustomerCheckListBO) checkList).getCustomerLevel().getId()));

            if (form.getMasterTypeId().equals(getStringValue(CustomerLevel.CENTER.getValue()))) {
                form.setType("0");
                form.setMasterTypeName(ConfigurationConstants.CENTER);
            } else if (form.getMasterTypeId().equals(getStringValue(CustomerLevel.GROUP.getValue()))) {
                form.setType("1");
                form.setMasterTypeName(ConfigurationConstants.GROUP);
            } else if (form.getMasterTypeId().equals(getStringValue(CustomerLevel.CLIENT.getValue()))) {
                form.setType("2");
                form.setMasterTypeName(ConfigurationConstants.CLIENT);
            }
            form.setStateName(((CustomerCheckListBO) checkList).getCustomerStatus().getName());
            form.setStateId(getStringValue(((CustomerCheckListBO) checkList).getCustomerStatus().getId()));
            form.setIsCustomer(true);
        } else {

            form.setMasterTypeId(getStringValue(((AccountCheckListBO) checkList).getProductTypeEntity()
                    .getProductTypeID()));
            if (form.getMasterTypeId().equals(getStringValue(ProductType.LOAN.getValue()))) {
                form.setType("3");
            } else {
                form.setType("4");
            }
            ((AccountCheckListBO) checkList).getAccountStateEntity().setLocaleId(getUserContext(request).getLocaleId());
            form.setMasterTypeName(((AccountCheckListBO) checkList).getProductTypeEntity().getName());
            form.setStateName(((AccountCheckListBO) checkList).getAccountStateEntity().getName());
            form.setStateId(getStringValue(((AccountCheckListBO) checkList).getAccountStateEntity().getId()));
            form.setIsCustomer(false);
        }
        if (checkList.getChecklistStatus().equals(CheckListConstants.STATUS_ACTIVE)) {
            form.setChecklistStatus(getStringValue(CheckListConstants.STATUS_ACTIVE));
        } else {
            form.setChecklistStatus(getStringValue(CheckListConstants.STATUS_INACTIVE));
        }
        List<String> details = new ArrayList<String>();
        for (CheckListDetailEntity checkListDetailEntity : checkList.getChecklistDetails()) {
            details.add(checkListDetailEntity.getDetailText());
        }
        form.setDetailsList(details);
    }

    private List<CheckListStatesView> getStates(ChkListActionForm chkListActionForm, HttpServletRequest request)
            throws Exception {
        List<CheckListStatesView> states = new ArrayList<CheckListStatesView>();
        if (chkListActionForm.getIsCustomer()) {
            states = ((CheckListBusinessService) getService()).getCustomerStates(getShortValue(chkListActionForm
                    .getMasterTypeId()), getUserContext(request).getLocaleId());
        } else {
            states = ((CheckListBusinessService) getService()).getAccountStates(getShortValue(chkListActionForm
                    .getMasterTypeId()), getUserContext(request).getLocaleId());
        }
        return states;
    }

}
