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

package org.mifos.customers.group.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.CenterDetailsDto;
import org.mifos.application.servicefacade.CenterDto;
import org.mifos.application.servicefacade.CenterHierarchySearchDto;
import org.mifos.application.servicefacade.CustomerServiceFacade;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.servicefacade.GroupCreation;
import org.mifos.application.servicefacade.GroupFormCreationDto;
import org.mifos.application.servicefacade.GroupUpdate;
import org.mifos.application.servicefacade.OnlyBranchOfficeHierarchyDto;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.ClientRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.business.service.GroupDetailsServiceFacade;
import org.mifos.customers.group.business.service.GroupInformationDto;
import org.mifos.customers.group.struts.actionforms.GroupCustActionForm;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.struts.action.CustAction;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SearchUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class GroupCustAction extends CustAction {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER);
    private final GroupDetailsServiceFacade groupDetailsServiceFacade = DependencyInjectedServiceLocator.locateGroupDetailsServiceFacade();
    private final CustomerServiceFacade customerServiceFacade = DependencyInjectedServiceLocator.locateCustomerServiceFacade();
    private final CustomerDao customerDao = DependencyInjectedServiceLocator.locateCustomerDao();

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("groupCustAction");
        security.allow("hierarchyCheck", SecurityConstants.VIEW);
        security.allow("chooseOffice", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.VIEW);
        security.allow("loadMeeting", SecurityConstants.MEETING_CREATE_GROUP_MEETING);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);

        security.allow("getDetails", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("manage", SecurityConstants.GROUP_EDIT_GROUP);
        security.allow("previewManage", SecurityConstants.VIEW);
        security.allow("previousManage", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.GROUP_EDIT_GROUP);
        security.allow("loadSearch", SecurityConstants.VIEW);
        security.allow("search", SecurityConstants.VIEW);

        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward hierarchyCheck(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ActionForwards actionForward = null;
        UserContext userContext = getUserContext(request);
        Short loggedInUserBranch = userContext.getBranchId();

        CenterHierarchySearchDto centerHierarchySearchDto = this.customerServiceFacade.isCenterHierarchyConfigured(loggedInUserBranch);

        if (centerHierarchySearchDto.isCenterHierarchyExists()) {
            SessionUtils.setAttribute(GroupConstants.CENTER_SEARCH_INPUT, centerHierarchySearchDto.getSearchInputs(), request.getSession());
            actionForward = ActionForwards.loadCenterSearch;
        } else {
            actionForward = ActionForwards.loadCreateGroup;
        }

        return mapping.findForward(actionForward.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward chooseOffice(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        UserContext userContext = getUserContext(request);

        OnlyBranchOfficeHierarchyDto officeHierarchy = customerServiceFacade
                .retrieveBranchOnlyOfficeHierarchy(userContext);

        SessionUtils.setAttribute(OnlyBranchOfficeHierarchyDto.IDENTIFIER, officeHierarchy, request);

        return mapping.findForward(ActionForwards.chooseOffice_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        GroupCustActionForm actionForm = (GroupCustActionForm) form;
        actionForm.cleanForm();
        SessionUtils.removeAttribute(CustomerConstants.CUSTOMER_MEETING, request.getSession());

        UserContext userContext = getUserContext(request);
        GroupCreation groupCreation = new GroupCreation(actionForm.getOfficeIdValue(), actionForm.getCenterSystemId(), userContext);

        GroupFormCreationDto groupFormCreationDto = this.customerServiceFacade.retrieveGroupFormCreationData(groupCreation);

        // inherit these settings from center/parent if center hierarchy is configured
        actionForm.setParentCustomer(groupFormCreationDto.getParentCustomer());
//        actionForm.getParentCustomer().getCustomerMeeting().getMeeting().isMonthly();
//        actionForm.getParentCustomer().getCustomerMeeting().getMeeting().isWeekly();
        actionForm.setOfficeId(groupFormCreationDto.getParentOfficeId());
        actionForm.setFormedByPersonnel(groupFormCreationDto.getParentPersonnelId());

        actionForm.setCustomFields(groupFormCreationDto.getCustomFieldViews());
        actionForm.setDefaultFees(groupFormCreationDto.getDefaultFees());

        SessionUtils.setCollectionAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, groupFormCreationDto.getAdditionalFees(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, groupFormCreationDto.getPersonnelList(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, groupFormCreationDto.getCustomFieldViews(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST, groupFormCreationDto.getFormedByPersonnel(), request);
        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, groupFormCreationDto.isCenterHierarchyExists(), request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadMeeting(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.loadMeeting_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        boolean isPendingApprovalDefined = ProcessFlowRules.isGroupPendingApprovalStateEnabled();
        SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, isPendingApprovalDefined, request);
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        GroupCustActionForm actionForm = (GroupCustActionForm) form;
        UserContext userContext = getUserContext(request);
        MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);

        CenterDetailsDto centerDetails = this.customerServiceFacade.createNewGroup(actionForm, meeting, userContext);

        actionForm.setCustomerId(centerDetails.getId().toString());
        actionForm.setGlobalCustNum(centerDetails.getGlobalCustNum());
        SessionUtils.setAttribute(GroupConstants.IS_GROUP_LOAN_ALLOWED, ClientRules.getGroupCanApplyLoans(), request);
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In GroupCustAction get method ");

        // John W - UserContext object passed because some status' need to be looked up for internationalisation based
        // on UserContext info
        GroupInformationDto groupInformationDto = groupDetailsServiceFacade.getGroupInformationDto(
                ((GroupCustActionForm) form).getGlobalCustNum(), getUserContext(request));
        SessionUtils.removeThenSetAttribute("groupInformationDto", groupInformationDto, request);

        // John W - - not sure whether to leave these rules as is or do something else like bake the logic into the main
        // dto and out of the jsp
        SessionUtils.setAttribute(GroupConstants.IS_GROUP_LOAN_ALLOWED, ClientRules.getGroupCanApplyLoans(), request);
        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, ClientRules.getCenterHierarchyExists(),
                request);

        // John W - 'BusinessKey' attribute linked to GroupBo is still used by other actions (e.g. meeting related)
        // further on and also breadcrumb.
        GroupBO group = (GroupBO) this.customerDao.findCustomerById(groupInformationDto.getGroupDisplay().getCustomerId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);

        logger.debug("Exiting GroupCustAction get method ");
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        GroupCustActionForm actionForm = (GroupCustActionForm) form;
        actionForm.cleanForm();
        UserContext userContext = getUserContext(request);

        // FIXME - store group identifier (id, globalCustNum) instead of entire business object
        GroupBO group = (GroupBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        logger.debug("Entering GroupCustAction manage method and customer id: " + group.getGlobalCustNum());

        CenterDto groupDto = this.customerServiceFacade.retrieveGroupDetailsForUpdate(group.getGlobalCustNum(), userContext);

        CustomerBO group1 = groupDto.getCenter();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, group1, request);

        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, groupDto.getActiveLoanOfficersForBranch(), request);
        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, groupDto.isCenterHierarchyExists(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, groupDto.getCustomFieldViews(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.POSITIONS, groupDto.getCustomerPositionViews(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CLIENT_LIST, groupDto.getClientList(), request);

        actionForm.setLoanOfficerId(group1.getPersonnel().getPersonnelId().toString());
        actionForm.setDisplayName(group1.getDisplayName());
        actionForm.setCustomerId(group1.getCustomerId().toString());
        actionForm.setGlobalCustNum(group1.getGlobalCustNum());
        actionForm.setExternalId(group1.getExternalId());
        actionForm.setAddress(group1.getAddress());
        actionForm.setCustomerPositions(groupDto.getCustomerPositionViews());
        actionForm.setCustomFields(groupDto.getCustomFieldViews());

        if (group1.isTrained()) {
            actionForm.setTrained(GroupConstants.TRAINED);
        } else {
            actionForm.setTrained(GroupConstants.NOT_TRAINED);
        }
        if (group1.getTrainedDate() != null) {
            actionForm.setTrainedDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(), group1
                    .getTrainedDate().toString()));
        }

        logger.debug("Exiting GroupCustAction manage method ");
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewManage(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previewManage_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previousManage(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previousManage_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        GroupBO group = (GroupBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        GroupCustActionForm actionForm = (GroupCustActionForm) form;
        UserContext userContext = getUserContext(request);

        boolean trained = false;
        if (actionForm.getTrainedValue() != null && actionForm.getTrainedValue().equals(Short.valueOf("1"))) {
            trained = true;
        }

        GroupUpdate groupUpdate = new GroupUpdate(group.getCustomerId(), group.getGlobalCustNum(), group.getVersionNo(), actionForm.getDisplayName(),
                actionForm.getLoanOfficerIdValue(), actionForm.getExternalId(), trained,
                actionForm.getTrainedDate(), actionForm.getAddress(), actionForm.getCustomFields(), actionForm.getCustomerPositions());

        this.customerServiceFacade.updateGroup(userContext, groupUpdate);

        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ActionForwards forward = null;
        GroupCustActionForm actionForm = (GroupCustActionForm) form;
        String fromPage = actionForm.getInput();
        if (fromPage.equals(GroupConstants.MANAGE_GROUP) || fromPage.equals(GroupConstants.PREVIEW_MANAGE_GROUP)) {
            forward = ActionForwards.cancelEdit_success;
        } else if (fromPage.equals(GroupConstants.CREATE_GROUP)) {
            forward = ActionForwards.cancelCreate_success;
        }
        return mapping.findForward(forward.toString());
    }

    @TransactionDemarcate(conditionToken = true)
    public ActionForward loadSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        GroupCustActionForm actionForm = (GroupCustActionForm) form;
        actionForm.setSearchString(null);
        cleanUpSearch(request);


        boolean groupHierarchyRequired = this.customerServiceFacade.isGroupHierarchyRequired();

        if (groupHierarchyRequired) {
            SessionUtils.setAttribute(CustomerConstants.GROUP_HIERARCHY_REQUIRED, CustomerConstants.NO, request);
        } else {
            SessionUtils.setAttribute(CustomerConstants.GROUP_HIERARCHY_REQUIRED, CustomerConstants.YES, request);
        }

        if (actionForm.getInput() != null && actionForm.getInput().equals(GroupConstants.GROUP_SEARCH_CLIENT_TRANSFER)) {
            return mapping.findForward(ActionForwards.loadTransferSearch_success.toString());
        }

        return mapping.findForward(ActionForwards.loadSearch_success.toString());
    }

    @Override
    @TransactionDemarcate(joinToken = true)
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        GroupCustActionForm actionForm = (GroupCustActionForm) form;
        UserContext userContext = getUserContext(request);
        ActionForward actionForward = super.search(mapping, form, request, response);
        String searchString = actionForm.getSearchString();
        if (searchString == null) {
            if (actionForm.getInput() != null && actionForm.getInput().equals(GroupConstants.GROUP_SEARCH_CLIENT_TRANSFER)) {
                request.setAttribute(Constants.INPUT, CenterConstants.INPUT_SEARCH_TRANSFERGROUP);
            } else {
                request.setAttribute(Constants.INPUT, null);
            }
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        addSeachValues(searchString, userContext.getBranchId().toString(), new OfficeBusinessService().getOffice(
                userContext.getBranchId()).getOfficeName(), request);

        final String normalizedSearchString = SearchUtils.normalizeSearchString(searchString);
        if (normalizedSearchString.equals("")) {
            if (actionForm.getInput() != null && actionForm.getInput().equals(GroupConstants.GROUP_SEARCH_CLIENT_TRANSFER)) {
                request.setAttribute(Constants.INPUT, CenterConstants.INPUT_SEARCH_TRANSFERGROUP);
            } else {
                request.setAttribute(Constants.INPUT, null);
            }
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        boolean searchForAddingClientsToGroup = (actionForm.getInput() != null && actionForm.getInput().equals(GroupConstants.GROUP_SEARCH_ADD_CLIENTS_TO_GROUPS));

        GroupSearchResultsDto searchResult = this.customerServiceFacade.searchGroups(searchForAddingClientsToGroup, normalizedSearchString, userContext.getId());

        SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, searchResult.getSearchResults(), request);

        if (actionForm.getInput() != null && actionForm.getInput().equals(GroupConstants.GROUP_SEARCH_CLIENT_TRANSFER)) {
            return mapping.findForward(ActionForwards.transferSearch_success.toString());
        } else if (searchForAddingClientsToGroup) {
            SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, searchResult.getSearchForAddingClientToGroupResults(), request);
            return mapping.findForward(ActionForwards.addGroupSearch_success.toString());
        } else {
            return actionForward;
        }
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");

        return mapping.findForward(method + "_failure");

    }
}
