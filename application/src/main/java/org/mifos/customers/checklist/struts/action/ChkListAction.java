/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.checklist.business.CheckListBO;
import org.mifos.customers.checklist.business.CheckListDetailEntity;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.checklist.persistence.CheckListPersistence;
import org.mifos.customers.checklist.struts.actionforms.ChkListActionForm;
import org.mifos.customers.checklist.util.helpers.CheckListConstants;
import org.mifos.customers.checklist.util.helpers.CheckListType;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.dto.domain.CheckListMasterDto;
import org.mifos.dto.screen.AccountCheckBoxItemDto;
import org.mifos.dto.screen.CheckListStatesView;
import org.mifos.dto.screen.CustomerCheckBoxItemDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class ChkListAction extends BaseAction {

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        List<CustomerStatusEntity> statesData = null;
        List<String> details = null;
        request.getSession().setAttribute("ChkListActionForm", null);

        List<CheckListMasterDto> masterData = this.checkListServiceFacade.retrieveChecklistMasterData();

        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);
        SessionUtils.setCollectionAttribute(CheckListConstants.STATES, statesData, request);
        SessionUtils.setCollectionAttribute(CheckListConstants.CHECKLIST_MASTERDATA, masterData, request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getStates(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        List<String> details = chkListActionForm.getValidCheckListDetails();
        List<CheckListStatesView> states = retrieveStates(chkListActionForm.getIsCustomer(), chkListActionForm.getMasterTypeId());

        SessionUtils.setCollectionAttribute(CheckListConstants.STATES, states, request);
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        List<String> details = chkListActionForm.getValidCheckListDetails();
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);

        Short masterTypeId = getShortValue(chkListActionForm.getMasterTypeId());
        Short stateId = getShortValue(chkListActionForm.getStateId());
        boolean isCustomer = chkListActionForm.getIsCustomer();

        this.checkListServiceFacade.validateIsValidCheckListState(masterTypeId, stateId, isCustomer);

        return mapping.findForward(ActionForwards.preview_success.toString());

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        List<String> details = chkListActionForm.getValidCheckListDetails();
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request.getSession());
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ChkListActionForm chkListActionForm = (ChkListActionForm) form;

        Short stateId = getShortValue(chkListActionForm.getStateId());
        String checklistName = chkListActionForm.getChecklistName();
        List<String> checklistDetails = chkListActionForm.getValidCheckListDetails();
        if (chkListActionForm.getIsCustomer()) {

            Short levelId = getShortValue(chkListActionForm.getMasterTypeId());
            this.checkListServiceFacade.createCustomerChecklist(levelId, stateId, checklistName, checklistDetails);
        } else {
            Short productId = getShortValue(chkListActionForm.getMasterTypeId());
            this.checkListServiceFacade.createAccountChecklist(productId, stateId, checklistName, checklistDetails);
        }
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelCreate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelCreate_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward loadAllChecklist(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        request.getSession().setAttribute("ChkListActionForm", null);

        List<CustomerCheckBoxItemDto> customerCheckLists = checkListServiceFacade.retreiveAllCustomerCheckLists();
        List<AccountCheckBoxItemDto> accountCheckLists = checkListServiceFacade.retreiveAllAccountCheckLists();

        SessionUtils.setCollectionAttribute(CheckListConstants.CENTER_CHECKLIST, filterCustomerCheckListsByLevel(customerCheckLists, CustomerLevel.CENTER), request);
        SessionUtils.setCollectionAttribute(CheckListConstants.GROUP_CHECKLIST, filterCustomerCheckListsByLevel(customerCheckLists, CustomerLevel.GROUP), request);
        SessionUtils.setCollectionAttribute(CheckListConstants.CLIENT_CHECKLIST, filterCustomerCheckListsByLevel(customerCheckLists, CustomerLevel.CLIENT), request);

        SessionUtils.setCollectionAttribute(CheckListConstants.LOAN_CHECKLIST, filterAccountCheckListsByProductType(accountCheckLists,ProductType.LOAN), request);
        SessionUtils.setCollectionAttribute(CheckListConstants.SAVINGS_CHECKLIST, filterAccountCheckListsByProductType( accountCheckLists, ProductType.SAVINGS), request);
        return mapping.findForward(ActionForwards.loadAllChecklist_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        Short localeId = getUserContext(request).getLocaleId();

        Short checklistId = getShortValue(chkListActionForm.getCheckListId());
        CheckListBO checkList = new CheckListPersistence().getCheckList(checklistId);
        if (checkList.getCheckListType().equals(CheckListType.CUSTOMER_CHECKLIST)) {
            CustomerCheckListBO customerCheckList = (CustomerCheckListBO) checkList;
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerCheckList, request);
            SessionUtils.setAttribute(CheckListConstants.TYPE, CheckListType.CUSTOMER_CHECKLIST.getValue(), request);
        } else {
            AccountCheckListBO accountCheckList = (AccountCheckListBO) checkList;
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountCheckList, request);
            SessionUtils.setAttribute(CheckListConstants.TYPE, CheckListType.ACCOUNT_CHECKLIST.getValue(), request);
        }
        SessionUtils.setAttribute(CheckListConstants.CREATED_BY_NAME, new PersonnelBusinessService().getPersonnel(
                checkList.getCreatedBy()).getDisplayName(), request);

        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;

        Short checklistId = getShortValue(chkListActionForm.getCheckListId());
        CheckListBO checkList = new CheckListPersistence().getCheckList(checklistId);

        if (checkList.getCheckListType().equals(CheckListType.CUSTOMER_CHECKLIST)) {
            chkListActionForm.setCheckListId(getStringValue(checkList.getChecklistId()));
            chkListActionForm.setChecklistName(checkList.getChecklistName());
            if (checkList.getCheckListType().equals(CheckListType.CUSTOMER_CHECKLIST)) {
                chkListActionForm.setMasterTypeId(getStringValue(((CustomerCheckListBO) checkList).getCustomerLevel().getId()));

                if (chkListActionForm.getMasterTypeId().equals(getStringValue(CustomerLevel.CENTER.getValue()))) {
                    chkListActionForm.setType("0");
                    chkListActionForm.setMasterTypeName(ConfigurationConstants.CENTER);
                } else if (chkListActionForm.getMasterTypeId().equals(getStringValue(CustomerLevel.GROUP.getValue()))) {
                    chkListActionForm.setType("1");
                    chkListActionForm.setMasterTypeName(ConfigurationConstants.GROUP);
                } else if (chkListActionForm.getMasterTypeId().equals(getStringValue(CustomerLevel.CLIENT.getValue()))) {
                    chkListActionForm.setType("2");
                    chkListActionForm.setMasterTypeName(ConfigurationConstants.CLIENT);
                }
                chkListActionForm.setStateName(((CustomerCheckListBO) checkList).getCustomerStatus().getName());
                chkListActionForm.setStateId(getStringValue(((CustomerCheckListBO) checkList).getCustomerStatus().getId()));
                chkListActionForm.setIsCustomer(true);
            } else {

                chkListActionForm.setMasterTypeId(getStringValue(((AccountCheckListBO) checkList).getProductTypeEntity()
                        .getProductTypeID()));
                if (chkListActionForm.getMasterTypeId().equals(getStringValue(ProductType.LOAN.getValue()))) {
                    chkListActionForm.setType("3");
                } else {
                    chkListActionForm.setType("4");
                }
                chkListActionForm.setMasterTypeName(((AccountCheckListBO) checkList).getProductTypeEntity().getName());
                chkListActionForm.setStateName(((AccountCheckListBO) checkList).getAccountStateEntity().getName());
                chkListActionForm.setStateId(getStringValue(((AccountCheckListBO) checkList).getAccountStateEntity().getId()));
                chkListActionForm.setIsCustomer(false);
            }
            if (checkList.getChecklistStatus().equals(CheckListConstants.STATUS_ACTIVE)) {
                chkListActionForm.setChecklistStatus(getStringValue(CheckListConstants.STATUS_ACTIVE));
            } else {
                chkListActionForm.setChecklistStatus(getStringValue(CheckListConstants.STATUS_INACTIVE));
            }
            List<String> details1 = new ArrayList<String>();
            for (CheckListDetailEntity checkListDetailEntity : checkList.getChecklistDetails()) {
                details1.add(checkListDetailEntity.getDetailText());
            }
            chkListActionForm.setDetailsList(details1);
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, checkList, request);
            SessionUtils.setAttribute(CheckListConstants.TYPE, CheckListType.CUSTOMER_CHECKLIST.getValue(), request);
        } else {
            chkListActionForm.setCheckListId(getStringValue(checkList.getChecklistId()));
            chkListActionForm.setChecklistName(checkList.getChecklistName());
            if (checkList.getCheckListType().equals(CheckListType.CUSTOMER_CHECKLIST)) {
                chkListActionForm.setMasterTypeId(getStringValue(((CustomerCheckListBO) checkList).getCustomerLevel().getId()));

                if (chkListActionForm.getMasterTypeId().equals(getStringValue(CustomerLevel.CENTER.getValue()))) {
                    chkListActionForm.setType("0");
                    chkListActionForm.setMasterTypeName(ConfigurationConstants.CENTER);
                } else if (chkListActionForm.getMasterTypeId().equals(getStringValue(CustomerLevel.GROUP.getValue()))) {
                    chkListActionForm.setType("1");
                    chkListActionForm.setMasterTypeName(ConfigurationConstants.GROUP);
                } else if (chkListActionForm.getMasterTypeId().equals(getStringValue(CustomerLevel.CLIENT.getValue()))) {
                    chkListActionForm.setType("2");
                    chkListActionForm.setMasterTypeName(ConfigurationConstants.CLIENT);
                }
                chkListActionForm.setStateName(((CustomerCheckListBO) checkList).getCustomerStatus().getName());
                chkListActionForm.setStateId(getStringValue(((CustomerCheckListBO) checkList).getCustomerStatus().getId()));
                chkListActionForm.setIsCustomer(true);
            } else {

                chkListActionForm.setMasterTypeId(getStringValue(((AccountCheckListBO) checkList).getProductTypeEntity()
                        .getProductTypeID()));
                if (chkListActionForm.getMasterTypeId().equals(getStringValue(ProductType.LOAN.getValue()))) {
                    chkListActionForm.setType("3");
                } else {
                    chkListActionForm.setType("4");
                }
                chkListActionForm.setMasterTypeName(((AccountCheckListBO) checkList).getProductTypeEntity().getName());
                chkListActionForm.setStateName(((AccountCheckListBO) checkList).getAccountStateEntity().getName());
                chkListActionForm.setStateId(getStringValue(((AccountCheckListBO) checkList).getAccountStateEntity().getId()));
                chkListActionForm.setIsCustomer(false);
            }
            if (checkList.getChecklistStatus().equals(CheckListConstants.STATUS_ACTIVE)) {
                chkListActionForm.setChecklistStatus(getStringValue(CheckListConstants.STATUS_ACTIVE));
            } else {
                chkListActionForm.setChecklistStatus(getStringValue(CheckListConstants.STATUS_INACTIVE));
            }
            List<String> details1 = new ArrayList<String>();
            for (CheckListDetailEntity checkListDetailEntity : checkList.getChecklistDetails()) {
                details1.add(checkListDetailEntity.getDetailText());
            }
            chkListActionForm.setDetailsList(details1);
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, checkList, request);
            SessionUtils.setAttribute(CheckListConstants.TYPE, CheckListType.ACCOUNT_CHECKLIST.getValue(), request);
        }

        List<CheckListMasterDto> masterData = this.checkListServiceFacade.retrieveChecklistMasterData();
        SessionUtils.setCollectionAttribute(CheckListConstants.CHECKLIST_MASTERDATA, masterData, request);

        List<CheckListStatesView> states = retrieveStates(chkListActionForm.getIsCustomer(), chkListActionForm.getMasterTypeId());

        SessionUtils.setCollectionAttribute(CheckListConstants.STATES, states, request);
        List<String> details = chkListActionForm.getValidCheckListDetails();
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);
        SessionUtils.setAttribute(CheckListConstants.OLDCHECKLISTNAME, checkList.getChecklistName(), request);
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getEditStates(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;

        List<String> details = chkListActionForm.getValidCheckListDetails();
        List<CheckListStatesView> states = retrieveStates(chkListActionForm.getIsCustomer(), chkListActionForm.getMasterTypeId());

        SessionUtils.setCollectionAttribute(CheckListConstants.STATES, states, request);
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward managePreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ChkListActionForm chkListActionForm = (ChkListActionForm) form;
        List<String> details = chkListActionForm.getValidCheckListDetails();
        SessionUtils.setCollectionAttribute(CheckListConstants.DETAILS, details, request);
        return mapping.findForward(ActionForwards.managepreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward managePrevious(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.manageprevious_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ChkListActionForm chkListActionForm = (ChkListActionForm) form;

        Short stateId = getShortValue(chkListActionForm.getStateId());
        String checklistName = chkListActionForm.getChecklistName();
        Short checklistStatus = getShortValue(chkListActionForm.getChecklistStatus());
        List<String> details = chkListActionForm.getValidCheckListDetails();

        if (chkListActionForm.getIsCustomer()) {

            CustomerCheckListBO customerCheckList = (CustomerCheckListBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

            Short levelId = getShortValue(chkListActionForm.getMasterTypeId());
            this.checkListServiceFacade.updateCustomerChecklist(customerCheckList.getChecklistId(), levelId, stateId, checklistStatus, checklistName, details);
        } else {
            AccountCheckListBO accountCheckList = (AccountCheckListBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
            Short productId = getShortValue(chkListActionForm.getMasterTypeId());
            this.checkListServiceFacade.updateAccountChecklist(accountCheckList.getChecklistId(), productId, stateId, checklistStatus, checklistName, details);
        }
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelManage(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancelEdit_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
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

    private List<CustomerCheckBoxItemDto> filterCustomerCheckListsByLevel(List<CustomerCheckBoxItemDto> checkLists, CustomerLevel level) {
        List<CustomerCheckBoxItemDto> customerCheckLists = new ArrayList<CustomerCheckBoxItemDto>();
        if (checkLists != null && checkLists.size() > 0) {
            for (CustomerCheckBoxItemDto checkList : checkLists) {
                if (checkList.getCustomerLevelId().equals(level.getValue())) {
                    customerCheckLists.add(checkList);
                }
            }
        }
        return customerCheckLists;
    }

    private List<AccountCheckBoxItemDto> filterAccountCheckListsByProductType(List<AccountCheckBoxItemDto> checkLists, ProductType productType) {
        List<AccountCheckBoxItemDto> accountCheckLists = new ArrayList<AccountCheckBoxItemDto>();
        if (checkLists != null && checkLists.size() > 0) {
            for (AccountCheckBoxItemDto checkList : checkLists) {
                if (checkList.getProductTypeId().equals(productType.getValue())) {
                    accountCheckLists.add(checkList);
                }
            }
        }
        return accountCheckLists;
    }

    private List<CheckListStatesView> retrieveStates(boolean isCustomer, String masterTypeId) {

        List<CheckListStatesView> states = new ArrayList<CheckListStatesView>();

        if (isCustomer) {
            Short levelId = getShortValue(masterTypeId);
            states = this.checkListServiceFacade.retrieveAllCustomerStates(levelId);
            List<CustomerCheckBoxItemDto> customerCheckLists = filterCustomerCheckListsByLevel(
                    checkListServiceFacade.retreiveAllCustomerCheckLists(), CustomerLevel.getLevel(levelId));

            for (int i = states.size() - 1; i >= 0; i--) {
                Short state = states.get(i).getStateId();

                for (CustomerCheckBoxItemDto customer : customerCheckLists) {
                    if (state.equals(customer.getCustomerStatusId())) {
                        states.remove(i);
                    }
                }
            }
        } else {
            Short prdTypeId = getShortValue(masterTypeId);
            states = this.checkListServiceFacade.retrieveAllAccountStates(prdTypeId);
            List<AccountCheckBoxItemDto> accountCheckLists = filterAccountCheckListsByProductType(
                    checkListServiceFacade.retreiveAllAccountCheckLists(), ProductType.fromInt((int) prdTypeId));

            for (int i = states.size() - 1; i >= 0; i--) {
                Short state = states.get(i).getStateId();

                for (AccountCheckBoxItemDto account : accountCheckLists) {
                    if (state.equals(account.getAccountStateId())) {
                        states.remove(i);
                    }
                }
            }
        }

        return states;
    }
}