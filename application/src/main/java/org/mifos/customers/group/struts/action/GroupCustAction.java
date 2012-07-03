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

package org.mifos.customers.group.struts.action;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.questionnaire.struts.DefaultQuestionnaireServiceFacadeLocator;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.questionnaire.struts.QuestionnaireServiceFacadeLocator;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.ClientRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.struts.actionforms.GroupCustActionForm;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.customers.struts.action.CustAction;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CenterDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.GroupCreation;
import org.mifos.dto.domain.GroupCreationDetail;
import org.mifos.dto.domain.GroupFormCreationDto;
import org.mifos.dto.domain.GroupUpdate;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.screen.CenterHierarchySearchDto;
import org.mifos.dto.screen.GroupInformationDto;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SearchUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupCustAction extends CustAction {

    private static final Logger logger = LoggerFactory.getLogger(GroupCustAction.class);

    private QuestionnaireServiceFacadeLocator questionnaireServiceFacadeLocator = new DefaultQuestionnaireServiceFacadeLocator();

    private QuestionnaireFlowAdapter createGroupQuestionnaire = new QuestionnaireFlowAdapter("Create", "Group",
            ActionForwards.preview_success, "custSearchAction.do?method=loadMainSearch", questionnaireServiceFacadeLocator);

    @TransactionDemarcate(saveToken = true)
    public ActionForward hierarchyCheck(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ActionForwards actionForward = null;

        CenterHierarchySearchDto centerHierarchySearchDto = this.groupServiceFacade.isCenterHierarchyConfigured();

        if (centerHierarchySearchDto.isCenterHierarchyExists()) {
            SessionUtils.setAttribute(GroupConstants.CENTER_SEARCH_INPUT, centerHierarchySearchDto.getSearchInputs(), request.getSession());
            actionForward = ActionForwards.loadCenterSearch;
        } else {
            actionForward = ActionForwards.loadCreateGroup;
        }

        SessionUtils.setAttribute(CustomerConstants.URL_MAP, null, request.getSession(false));
        SessionUtils.setAttribute(GroupConstants.PREVIEW_CREATE_NEW_GROUP_FAILURE, false, request);

        return mapping.findForward(actionForward.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward chooseOffice(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        OnlyBranchOfficeHierarchyDto officeHierarchy = customerServiceFacade.retrieveBranchOnlyOfficeHierarchy();

        SessionUtils.setAttribute(OnlyBranchOfficeHierarchyDto.IDENTIFIER, officeHierarchy, request);

        return mapping.findForward(ActionForwards.chooseOffice_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        GroupCustActionForm actionForm = (GroupCustActionForm) form;
        actionForm.cleanForm();
        SessionUtils.removeAttribute(CustomerConstants.CUSTOMER_MEETING, request.getSession());

        GroupCreation groupCreation = null;
        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
        if (isCenterHierarchyExists) {
            String centerSystemId = actionForm.getCenterSystemId();
            CenterBO center = this.customerDao.findCenterBySystemId(centerSystemId);
            groupCreation = new GroupCreation(actionForm.getOfficeIdValue(), centerSystemId);

            // inherit these settings from center/parent if center hierarchy is configured
            actionForm.setParentCustomer(center);
            actionForm.setOfficeId(center.getOfficeId().toString());
            actionForm.setFormedByPersonnel(center.getLoanOfficerId().toString());
        } else {
            groupCreation = new GroupCreation(actionForm.getOfficeIdValue(), "");
        }

        GroupFormCreationDto groupFormCreationDto = this.groupServiceFacade.retrieveGroupFormCreationData(groupCreation);

        actionForm.setCustomFields(new ArrayList<CustomFieldDto>());
        actionForm.setDefaultFees(groupFormCreationDto.getDefaultFees());

        SessionUtils.setCollectionAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, groupFormCreationDto.getAdditionalFees(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, groupFormCreationDto.getPersonnelList(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, new ArrayList<CustomFieldDto>(), request);
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
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        GroupCustActionForm actionForm = (GroupCustActionForm) form;
        boolean isPendingApprovalDefined = ProcessFlowRules.isGroupPendingApprovalStateEnabled();
        SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, isPendingApprovalDefined, request);
        return createGroupQuestionnaire.fetchAppliedQuestions(
                mapping, actionForm, request, ActionForwards.preview_success);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewOnly(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
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
        MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);

        UserContext userContext = getUserContext(request);

        String groupName = actionForm.getDisplayName();
        String externalId = actionForm.getExternalId();
        boolean trained = actionForm.isCustomerTrained();
        DateTime trainedOn = new DateTime(actionForm.getTrainedDateValue(userContext.getPreferredLocale()));
        AddressDto addressDto = null;
        if (actionForm.getAddress() != null) {
            addressDto = Address.toDto(actionForm.getAddress());
        }
        Short customerStatusId = actionForm.getStatusValue().getValue();

        String centerSystemId = "";
        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
        if (isCenterHierarchyExists) {
            centerSystemId = actionForm.getParentCustomer().getGlobalCustNum();
        }
        Short officeId = actionForm.getOfficeIdValue();

        MeetingDto meetingDto = null;
        if (meeting!= null) {
            meetingDto = meeting.toDto();
        }

        DateTime mfiJoiningDate = new DateTime().toDateMidnight().toDateTime();
        DateTime activationDate = new DateTime().toDateMidnight().toDateTime();
        try {
            GroupCreationDetail groupCreationDetail = new GroupCreationDetail(groupName, externalId,
                    addressDto, actionForm.getFormedByPersonnelValue(), actionForm.getFeesToApply(),
                    customerStatusId, trained, trainedOn, centerSystemId, officeId, mfiJoiningDate, activationDate);

            CustomerDetailsDto centerDetails = this.groupServiceFacade.createNewGroup(groupCreationDetail, meetingDto);
            createGroupQuestionnaire.saveResponses(request, actionForm, centerDetails.getId());
            actionForm.setCustomerId(centerDetails.getId().toString());
            actionForm.setGlobalCustNum(centerDetails.getGlobalCustNum());
        } catch (BusinessRuleException e) {
            throw new ApplicationException(e.getMessageKey(), e);
        }

        SessionUtils.setAttribute(GroupConstants.IS_GROUP_LOAN_ALLOWED, ClientRules.getGroupCanApplyLoans(), request);

        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In GroupCustAction get method ");

        // John W - UserContext object passed because some status' need to be looked up for internationalisation based
        // on UserContext info

        String groupSystemId = ((GroupCustActionForm) form).getGlobalCustNum();
        GroupInformationDto groupInformationDto;
        try {
            groupInformationDto = this.groupServiceFacade.getGroupInformationDto(groupSystemId);
        }
        catch (MifosRuntimeException e) {
            if (e.getCause() instanceof ApplicationException) {
                throw (ApplicationException) e.getCause();
            }
            throw e;
        }
        SessionUtils.removeThenSetAttribute("groupInformationDto", groupInformationDto, request);

        // John W - - not sure whether to leave these rules as is or do something else like bake the logic into the main
        // dto and out of the jsp
        SessionUtils.setAttribute(GroupConstants.IS_GROUP_LOAN_ALLOWED, ClientRules.getGroupCanApplyLoans(), request);
        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, ClientRules.getCenterHierarchyExists(),
                request);

        // John W - 'BusinessKey' attribute linked to GroupBo is still used by other actions (e.g. meeting related)
        // further on and also breadcrumb.
        GroupBO groupBO = (GroupBO) this.customerDao.findCustomerById(groupInformationDto.getGroupDisplay().getCustomerId());
        SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, groupBO, request);
        setCurrentPageUrl(request, groupBO);
        setQuestionGroupInstances(request, groupBO);

        logger.debug("Exiting GroupCustAction get method ");
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    private void setQuestionGroupInstances(HttpServletRequest request, GroupBO groupBO) throws PageExpiredException {
        QuestionnaireServiceFacade questionnaireServiceFacade = questionnaireServiceFacadeLocator.getService(request);
        if (questionnaireServiceFacade != null) {
            setQuestionGroupInstances(questionnaireServiceFacade, request, groupBO.getCustomerId());
        }
    }

    // Intentionally made public to aid testing !
    public void setQuestionGroupInstances(QuestionnaireServiceFacade questionnaireServiceFacade, HttpServletRequest request, Integer customerId) throws PageExpiredException {
        List<QuestionGroupInstanceDetail> instanceDetails = questionnaireServiceFacade.getQuestionGroupInstances(customerId, "View", "Group");
        SessionUtils.setCollectionAttribute("questionGroupInstances", instanceDetails, request);
    }

    private void setCurrentPageUrl(HttpServletRequest request, GroupBO groupBO) throws PageExpiredException, UnsupportedEncodingException {
        SessionUtils.removeThenSetAttribute("currentPageUrl", constructCurrentPageUrl(request, groupBO), request);
    }

    private String constructCurrentPageUrl(HttpServletRequest request, GroupBO groupBO) throws UnsupportedEncodingException {
        String officerId = request.getParameter("recordOfficeId");
        String loanOfficerId = request.getParameter("recordLoanOfficerId");
        String url = String.format("groupCustAction.do?globalCustNum=%s&recordOfficeId=%s&recordLoanOfficerId=%s",
                groupBO.getGlobalCustNum(), officerId, loanOfficerId);
        return url;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        GroupCustActionForm actionForm = (GroupCustActionForm) form;
        actionForm.cleanForm();

        // FIXME - store group identifier (id, globalCustNum) instead of entire business object
        GroupBO group = (GroupBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        group = this.customerDao.findGroupBySystemId(group.getGlobalCustNum());
        logger.debug("Entering GroupCustAction manage method and customer id: " + group.getGlobalCustNum());

        CenterDto groupDto = this.groupServiceFacade.retrieveGroupDetailsForUpdate(group.getGlobalCustNum());

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);

        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, groupDto.getActiveLoanOfficersForBranch(), request);
        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, groupDto.isCenterHierarchyExists(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, new ArrayList<CustomFieldDto>(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.POSITIONS, groupDto.getCustomerPositionViews(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CLIENT_LIST, groupDto.getClientList(), request);

        actionForm.setLoanOfficerId(String.valueOf(group.getLoanOfficerId()));
        actionForm.setDisplayName(group.getDisplayName());
        actionForm.setCustomerId(group.getCustomerId().toString());
        actionForm.setGlobalCustNum(group.getGlobalCustNum());
        actionForm.setExternalId(group.getExternalId());
        actionForm.setAddress(group.getAddress());
        actionForm.setCustomerPositions(groupDto.getCustomerPositionViews());
        actionForm.setCustomFields(new ArrayList<CustomFieldDto>());

        if (group.isTrained()) {
            actionForm.setTrained(GroupConstants.TRAINED);
        } else {
            actionForm.setTrained(GroupConstants.NOT_TRAINED);
        }
        if (group.getTrainedDate() != null) {
            actionForm.setTrainedDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(), group
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

        boolean trained = false;
        if (actionForm.getTrainedValue() != null && actionForm.getTrainedValue().equals(Short.valueOf("1"))) {
            trained = true;
        }

        AddressDto address = null;
        if (actionForm.getAddress() != null) {
            address = Address.toDto(actionForm.getAddress());
        }

        GroupUpdate groupUpdate = new GroupUpdate(group.getCustomerId(), group.getGlobalCustNum(), group.getVersionNo(), actionForm.getDisplayName(),
                actionForm.getLoanOfficerIdValue(), actionForm.getExternalId(), trained,
                actionForm.getTrainedDate(), address, actionForm.getCustomFields(), actionForm.getCustomerPositions());

        try {
            this.groupServiceFacade.updateGroup(groupUpdate);
        } catch (BusinessRuleException e) {
            throw new ApplicationException(e.getMessageKey(), e);
        }

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


        SessionUtils.setAttribute(CustomerConstants.URL_MAP, null, request.getSession(false));

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
        if(Methods.previewOnly.toString().equals(method)) {
            SessionUtils.setAttribute(GroupConstants.PREVIEW_CREATE_NEW_GROUP_FAILURE, true, request);
        }
        return mapping.findForward(method + "_failure");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward captureQuestionResponses(
            final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "captureQuestionResponses");
        ActionErrors errors = createGroupQuestionnaire.validateResponses(request, (GroupCustActionForm) form);
        if (errors != null && !errors.isEmpty()) {
            addErrors(request, errors);
            return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
        }
        return createGroupQuestionnaire.rejoinFlow(mapping);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editQuestionResponses(
            final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "editQuestionResponses");
        return createGroupQuestionnaire.editResponses(mapping, request, (GroupCustActionForm) form);
    }
}
