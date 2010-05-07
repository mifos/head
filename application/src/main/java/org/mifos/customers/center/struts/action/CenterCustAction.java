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

package org.mifos.customers.center.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.CenterCreation;
import org.mifos.application.servicefacade.CenterDto;
import org.mifos.application.servicefacade.CenterFormCreationDto;
import org.mifos.application.servicefacade.CenterUpdate;
import org.mifos.application.servicefacade.CustomerDetailsDto;
import org.mifos.application.servicefacade.CustomerSearch;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.servicefacade.OnlyBranchOfficeHierarchyDto;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.business.CustomerPositionDto;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.business.service.CenterDetailsServiceFacade;
import org.mifos.customers.center.business.service.CenterInformationDto;
import org.mifos.customers.center.struts.actionforms.CenterCustActionForm;
import org.mifos.customers.struts.action.CustAction;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class CenterCustAction extends CustAction {

    private final CenterDetailsServiceFacade centerDetailsServiceFacade = DependencyInjectedServiceLocator.locateCenterDetailsServiceFacade();

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("centerCustAction");
        security.allow("chooseOffice", SecurityConstants.CENTER_CREATE_NEW_CENTER);
        security.allow("load", SecurityConstants.CENTER_CREATE_NEW_CENTER);
        security.allow("loadMeeting", SecurityConstants.MEETING_CREATE_CENTER_MEETING);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.CENTER_CREATE_NEW_CENTER);
        security.allow("manage", SecurityConstants.CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS);
        security.allow("editPrevious", SecurityConstants.VIEW);
        security.allow("editPreview", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS);

        security.allow("get", SecurityConstants.VIEW);
        security.allow("loadSearch", SecurityConstants.VIEW);
        security.allow("search", SecurityConstants.VIEW);
        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);

        security.allow("loadTransferSearch", SecurityConstants.VIEW);
        security.allow("searchTransfer", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward chooseOffice(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        UserContext userContext = getUserContext(request);

        OnlyBranchOfficeHierarchyDto officeHierarchy = customerServiceFacade
                .retrieveBranchOnlyOfficeHierarchy(userContext);

        SessionUtils.setAttribute(OnlyBranchOfficeHierarchyDto.IDENTIFIER, officeHierarchy, request);

        return mapping.findForward(ActionForwards.chooseOffice_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        CenterCustActionForm actionForm = (CenterCustActionForm) form;
        actionForm.clearActionFormFields();
        SessionUtils.removeAttribute(CustomerConstants.CUSTOMER_MEETING, request);

        UserContext userContext = getUserContext(request);
        CenterCreation centerCreationDto = new CenterCreation(actionForm.getOfficeIdValue(), userContext.getId(),
                userContext.getLevelId(), userContext.getPreferredLocale());

        CenterFormCreationDto centerFormCreation = this.customerServiceFacade.retrieveCenterFormCreationData(
                centerCreationDto, userContext);

        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, centerFormCreation
                .getCustomFieldViews(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, centerFormCreation
                .getActiveLoanOfficersForBranch(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, centerFormCreation
                .getAdditionalFees(), request);
        actionForm.setCustomFields(centerFormCreation.getCustomFieldViews());
        actionForm.setDefaultFees(centerFormCreation.getDefaultFees());

        DateTime today = new DateTime().toDateMidnight().toDateTime();
        actionForm.setMfiJoiningDate(today.getDayOfMonth(), today.getMonthOfYear(), today.getYear());

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadMeeting(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        // NOTE - forwards to MeetingAction.load and MeetingAction.create to save meeting schedule
        return mapping.findForward(ActionForwards.loadMeeting_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        // NOTE - pulls information from session scope variables and from actionform in session scope
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        CenterCustActionForm actionForm = (CenterCustActionForm) form;
        MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
        UserContext userContext = getUserContext(request);

        CustomerDetailsDto centerDetails = this.customerServiceFacade.createNewCenter(actionForm, meeting, userContext);

        actionForm.setCustomerId(centerDetails.getId().toString());
        actionForm.setGlobalCustNum(centerDetails.getGlobalCustNum());

        return mapping.findForward(ActionForwards.create_success.toString());
    }

    // NOTE edit center details
    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        CenterCustActionForm actionForm = (CenterCustActionForm) form;

        actionForm.clearActionFormFields();
        CenterBO center = (CenterBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        final Integer centerId = center.getCustomerId();
        UserContext userContext = getUserContext(request);

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, null, request);

        CenterDto centerDto = this.customerServiceFacade.retrieveCenterDetailsForUpdate(centerId, userContext);

        actionForm.setLoanOfficerId(centerDto.getLoanOfficerIdAsString());
        actionForm.setCustomerId(centerDto.getCustomerIdAsString());
        actionForm.setGlobalCustNum(centerDto.getGlobalCustNum());
        actionForm.setExternalId(centerDto.getExternalId());
        actionForm.setMfiJoiningDate(centerDto.getMfiJoiningDateAsString());
        actionForm.setMfiJoiningDate(centerDto.getMfiJoiningDate().getDayOfMonth(), centerDto.getMfiJoiningDate()
                .getMonthOfYear(), centerDto.getMfiJoiningDate().getYear());
        actionForm.setAddress(centerDto.getAddress());
        actionForm.setCustomerPositions(centerDto.getCustomerPositionViews());
        actionForm.setCustomFields(centerDto.getCustomFieldViews());

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, centerDto.getCenter(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, centerDto
                .getActiveLoanOfficersForBranch(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, centerDto.getCustomFieldViews(),
                request);
        SessionUtils.setCollectionAttribute(CustomerConstants.POSITIONS, centerDto.getCustomerPositionViews(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CLIENT_LIST, centerDto.getClientList(), request);

        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    // NOTE - manage->preview
    @TransactionDemarcate(joinToken = true)
    public ActionForward editPreview(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.editpreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPrevious(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.editprevious_success.toString());
    }

    // NOTE - manage->preview->update
    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        CenterBO centerFromSession = (CenterBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        CenterCustActionForm actionForm = (CenterCustActionForm) form;
        UserContext userContext = getUserContext(request);


        List<CustomerPositionDto> populatedCustomerPositions = new ArrayList<CustomerPositionDto>();
        for (CustomerPositionDto position : actionForm.getCustomerPositions()) {
            if (position.getCustomerId() != null) {
                populatedCustomerPositions.add(position);
            }
        }

        CenterUpdate centerUpdate = new CenterUpdate(centerFromSession.getCustomerId(), centerFromSession
                .getVersionNo(), actionForm.getLoanOfficerIdValue(), actionForm.getExternalId(), actionForm
                .getMfiJoiningDate(), actionForm.getAddress(), actionForm.getCustomFields(), populatedCustomerPositions);

        this.customerServiceFacade.updateCenter(userContext, centerUpdate);

        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) {
        CenterCustActionForm actionForm = (CenterCustActionForm) form;
        ActionForwards forward = null;

        if (actionForm.getInput().equals(Methods.create.toString())) {
            forward = ActionForwards.cancel_success;
        } else if (actionForm.getInput().equals(Methods.manage.toString())) {
            forward = ActionForwards.editcancel_success;
        }

        return mapping.findForward(forward.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        // John W - UserContext passed because some status' need to be looked up for internationalisation
        CenterInformationDto centerInformationDto = this.centerDetailsServiceFacade.getCenterInformationDto(
                ((CenterCustActionForm) form).getGlobalCustNum(), getUserContext(request));
        SessionUtils.removeThenSetAttribute("centerInformationDto", centerInformationDto, request);

        // John W - 'BusinessKey' attribute used by breadcrumb but is not in associated jsp
        CenterBO center = (CenterBO) this.customerDao.findCustomerById(centerInformationDto.getCenterDisplay().getCustomerId());
        SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, center, request);

        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(conditionToken = true)
    public ActionForward loadSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        cleanSearchResults(request, (CenterCustActionForm) form);
        return mapping.findForward(ActionForwards.loadSearch_success.toString());
    }

    @TransactionDemarcate(conditionToken = true)
    public ActionForward loadTransferSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        cleanSearchResults(request, (CenterCustActionForm) form);
        return mapping.findForward(ActionForwards.loadTransferSearch_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward searchTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        cleanUpSearch(request);
        CenterCustActionForm actionForm = (CenterCustActionForm) form;
        String searchString = actionForm.getSearchString();
        UserContext userContext = getUserContext(request);

        CustomerSearch searchResult = this.customerServiceFacade.search(searchString, userContext);

        addSeachValues(searchString, searchResult.getOfficeId(), searchResult.getOfficeName(), request);
        SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, searchResult.getSearchResult(), request);
        return mapping.findForward(ActionForwards.transferSearch_success.toString());
    }

    /**
     * invoked when searching for centers from group creation screen
     */
    @Override
    @TransactionDemarcate(joinToken = true)
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ActionForward actionForward = super.search(mapping, form, request, response);
        CenterCustActionForm actionForm = (CenterCustActionForm) form;
        String searchString = actionForm.getSearchString();
        UserContext userContext = getUserContext(request);

        CustomerSearch searchResult = this.customerServiceFacade.search(searchString, userContext);

        addSeachValues(searchString, searchResult.getOfficeId(), searchResult.getOfficeName(), request);
        SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, searchResult.getSearchResult(), request);
        return actionForward;
    }

    private void cleanSearchResults(HttpServletRequest request, CenterCustActionForm actionForm)
            throws PageExpiredException {
        actionForm.setSearchString(null);
        cleanUpSearch(request);
    }
}