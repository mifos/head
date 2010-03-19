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

package org.mifos.customers.center.struts.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.CenterCreation;
import org.mifos.application.servicefacade.CenterDetailsDto;
import org.mifos.application.servicefacade.CenterDto;
import org.mifos.application.servicefacade.CenterFormCreationDto;
import org.mifos.application.servicefacade.CenterUpdate;
import org.mifos.application.servicefacade.CustomerSearch;
import org.mifos.application.servicefacade.CustomerServiceFacade;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.servicefacade.OnlyBranchOfficeHierarchyDto;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.business.CustomerPositionView;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.business.service.CenterDetailsServiceFacade;
import org.mifos.customers.center.business.service.CenterInformationDto;
import org.mifos.customers.center.struts.actionforms.CenterCustActionForm;
import org.mifos.customers.struts.action.CustAction;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class CenterCustAction extends CustAction {

    private final CustomerServiceFacade customerServiceFacade = DependencyInjectedServiceLocator
            .locateCustomerServiceFacade();
    private final CenterDetailsServiceFacade centerDetailsServiceFacade = DependencyInjectedServiceLocator.locateCenterDetailsServiceFacade();

    @Override
    protected BusinessService getService() {
        return new DummyBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(@SuppressWarnings("unused") String method) {
        return true;
    }

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
        clearActionForm(actionForm);
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

        CenterDetailsDto centerDetails = this.customerServiceFacade.createNewCenter(actionForm, meeting, userContext);

        actionForm.setCustomerId(centerDetails.getId().toString());
        actionForm.setGlobalCustNum(centerDetails.getGlobalCustNum());

        return mapping.findForward(ActionForwards.create_success.toString());
    }

    // NOTE edit center details
    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        CenterCustActionForm actionForm = (CenterCustActionForm) form;

        clearActionForm(actionForm);
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

        CenterUpdate centerUpdate = new CenterUpdate(centerFromSession.getCustomerId(), centerFromSession
                .getVersionNo(), actionForm.getLoanOfficerIdValue(), actionForm.getExternalId(), actionForm
                .getMfiJoiningDate(), actionForm.getAddress(), actionForm.getCustomFields(), actionForm
                .getCustomerPositions());

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

        long startTime = System.currentTimeMillis();
        // John W - UserContext object passed because some status' need to be looked up for internationalisation based
        // on UserContext info
        CenterInformationDto centerInformationDto = this.centerDetailsServiceFacade.getCenterInformationDto(
                ((CenterCustActionForm) form).getGlobalCustNum(), CustomerLevel.CENTER.getValue(), getUserContext(request));
        SessionUtils.removeAttribute("centerInformationDto", request);
        SessionUtils.setAttribute("centerInformationDto", centerInformationDto, request);

        // John W - 'BusinessKey' attribute linked to CenterBo is still used by other actions (e.g. meeting related)
        // further on and also breadcrumb.
        CenterBO center = (CenterBO) new CustomerBusinessService().getCustomer(centerInformationDto.getCenterDisplay()
                .getCustomerId());
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, center, request);
        System.out.println("get Center Transaction Took: " + (System.currentTimeMillis() - startTime));
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

    private void clearActionForm(CenterCustActionForm actionForm) {
        actionForm.setDefaultFees(new ArrayList<FeeView>());
        actionForm.setAdditionalFees(new ArrayList<FeeView>());
        actionForm.setCustomerPositions(new ArrayList<CustomerPositionView>());
        actionForm.setCustomFields(new ArrayList<CustomFieldView>());
        actionForm.setAddress(new Address());
        actionForm.setDisplayName(null);
        actionForm.setMfiJoiningDate(null);
        actionForm.setGlobalCustNum(null);
        actionForm.setCustomerId(null);
        actionForm.setExternalId(null);
        actionForm.setLoanOfficerId(null);
    }
    
    private class DummyBusinessService implements BusinessService {

        @Override
        public BusinessObject getBusinessObject(@SuppressWarnings("unused") final UserContext userContext) {
            return null;
        }

    }
}