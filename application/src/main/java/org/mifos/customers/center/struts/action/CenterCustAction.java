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

package org.mifos.customers.center.struts.action;

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
import org.joda.time.LocalDate;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.questionnaire.struts.DefaultQuestionnaireServiceFacadeLocator;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.questionnaire.struts.QuestionnaireServiceFacadeLocator;
import org.mifos.application.servicefacade.CustomerSearch;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.calendar.CalendarUtils;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.struts.actionforms.CenterCustActionForm;
import org.mifos.customers.struts.action.CustAction;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.CenterCreationDetail;
import org.mifos.dto.domain.CenterDto;
import org.mifos.dto.domain.CenterInformationDto;
import org.mifos.dto.domain.CenterUpdate;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.screen.CenterFormCreationDto;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;

public class CenterCustAction extends CustAction {

    private QuestionnaireServiceFacadeLocator questionnaireServiceFacadeLocator = new DefaultQuestionnaireServiceFacadeLocator();

    private QuestionnaireFlowAdapter createCenterQuestionnaire = new QuestionnaireFlowAdapter("Create", "Center",
            ActionForwards.preview_success, "clientsAndAccounts.ftl", questionnaireServiceFacadeLocator
        );

    @TransactionDemarcate(saveToken = true)
    public ActionForward chooseOffice(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        OnlyBranchOfficeHierarchyDto officeHierarchy = customerServiceFacade.retrieveBranchOnlyOfficeHierarchy();

        SessionUtils.setAttribute(OnlyBranchOfficeHierarchyDto.IDENTIFIER, officeHierarchy, request);
        SessionUtils.setAttribute(CustomerConstants.URL_MAP, null, request.getSession(false));

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

        CenterFormCreationDto centerFormCreation = this.centerServiceFacade.retrieveCenterFormCreationData(centerCreationDto);

//        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, new ArrayList<Serializable>(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, centerFormCreation.getActiveLoanOfficersForBranch(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, centerFormCreation.getAdditionalFees(), request);
//        actionForm.setCustomFields(centerFormCreation.getCustomFieldViews());
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
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        // NOTE - pulls information from session scope variables and from actionform in session scope
        CenterCustActionForm actionForm = (CenterCustActionForm) form;
        return createCenterQuestionnaire.fetchAppliedQuestions(
                mapping, actionForm, request, ActionForwards.preview_success);
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

        LocalDate mfiJoiningDate = new LocalDate(CalendarUtils.getDateFromString(actionForm.getMfiJoiningDate(), getUserContext(request).getPreferredLocale()));

        Address address = actionForm.getAddress();
        AddressDto addressDto = Address.toDto(address);

        MeetingDto meetingDto = meeting.toDto();

        List<CreateAccountFeeDto> accountFeesToBeApplied = new ArrayList<CreateAccountFeeDto>();
        List<ApplicableAccountFeeDto> feesToBeApplied = actionForm.getFeesToApply();
        for (ApplicableAccountFeeDto feeDto : feesToBeApplied) {
            accountFeesToBeApplied.add(new CreateAccountFeeDto(feeDto.getFeeId(), feeDto.getAmount()));
        }

        try {
            CenterCreationDetail centerCreationDetail = new CenterCreationDetail(mfiJoiningDate, actionForm.getDisplayName(), actionForm.getExternalId(), addressDto, actionForm.getLoanOfficerIdValue(), actionForm.getOfficeIdValue(), accountFeesToBeApplied);
            CustomerDetailsDto centerDetails = this.centerServiceFacade.createNewCenter(centerCreationDetail, meetingDto);
            createCenterQuestionnaire.saveResponses(request, actionForm, centerDetails.getId());

            actionForm.setCustomerId(centerDetails.getId().toString());
            actionForm.setGlobalCustNum(centerDetails.getGlobalCustNum());
        } catch (BusinessRuleException e) {
            throw new ApplicationException(e.getMessageKey(), e.getMessageValues());
        }

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

        center = this.customerDao.findCenterBySystemId(center.getGlobalCustNum());

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, null, request);

        CenterDto centerDto = this.centerServiceFacade.retrieveCenterDetailsForUpdate(centerId);

        actionForm.setLoanOfficerId(centerDto.getLoanOfficerIdAsString());
        actionForm.setCustomerId(centerDto.getCustomerIdAsString());
        actionForm.setGlobalCustNum(centerDto.getGlobalCustNum());
        actionForm.setExternalId(centerDto.getExternalId());
        actionForm.setMfiJoiningDate(centerDto.getMfiJoiningDateAsString());
        actionForm.setMfiJoiningDate(centerDto.getMfiJoiningDate().getDayOfMonth(), centerDto.getMfiJoiningDate()
                .getMonthOfYear(), centerDto.getMfiJoiningDate().getYear());
        actionForm.setAddress(center.getAddress());
        actionForm.setCustomerPositions(centerDto.getCustomerPositionViews());
        actionForm.setCustomFields(new ArrayList<CustomFieldDto>());

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, center, request);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, centerDto.getActiveLoanOfficersForBranch(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, new ArrayList<CustomFieldDto>(), request);
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

        AddressDto dto = null;
        if (actionForm.getAddress() != null) {
            dto = Address.toDto(actionForm.getAddress());
        }

        CenterUpdate centerUpdate = new CenterUpdate(centerFromSession.getCustomerId(),
                actionForm.getDisplayName(), centerFromSession.getVersionNo(),
                actionForm.getLoanOfficerIdValue(), actionForm.getExternalId(), actionForm
                .getMfiJoiningDate(), dto, actionForm.getCustomFields(), actionForm.getCustomerPositions());

        try {
            this.centerServiceFacade.updateCenter(centerUpdate);
        } catch (BusinessRuleException e) {
            throw new ApplicationException(e.getMessageKey(), e);
        }

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

        CenterInformationDto centerInformationDto;
        try {
            centerInformationDto = this.centerServiceFacade.getCenterInformationDto(((CenterCustActionForm) form).getGlobalCustNum());
        }
        catch (MifosRuntimeException e) {
            if (e.getCause() instanceof ApplicationException) {
                throw (ApplicationException) e.getCause();
            }
            throw e;
        }
        SessionUtils.removeThenSetAttribute("centerInformationDto", centerInformationDto, request);

        // John W - 'BusinessKey' attribute used by breadcrumb but is not in associated jsp
        CenterBO centerBO = (CenterBO) this.customerDao.findCustomerById(centerInformationDto.getCenterDisplay().getCustomerId());
        SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, centerBO, request);
        setCurrentPageUrl(request, centerBO);
        setQuestionGroupInstances(request, centerBO);

        return mapping.findForward(ActionForwards.get_success.toString());
    }

    private void setQuestionGroupInstances(HttpServletRequest request, CenterBO centerBO) throws PageExpiredException {
        QuestionnaireServiceFacade questionnaireServiceFacade = questionnaireServiceFacadeLocator.getService(request);
        if (questionnaireServiceFacade != null) {
            setQuestionGroupInstances(questionnaireServiceFacade, request, centerBO.getCustomerId());
        }
    }

    // Intentionally made public to aid testing !
    public void setQuestionGroupInstances(QuestionnaireServiceFacade questionnaireServiceFacade, HttpServletRequest request, Integer customerId) throws PageExpiredException {
        List<QuestionGroupInstanceDetail> instanceDetails = questionnaireServiceFacade.getQuestionGroupInstances(customerId, "View", "Center");
        SessionUtils.setCollectionAttribute("questionGroupInstances", instanceDetails, request);
    }

    private void setCurrentPageUrl(HttpServletRequest request, CenterBO centerBO) throws PageExpiredException, UnsupportedEncodingException {
        SessionUtils.removeThenSetAttribute("currentPageUrl", constructCurrentPageUrl(request, centerBO), request);
    }

    private String constructCurrentPageUrl(HttpServletRequest request, CenterBO centerBO) throws UnsupportedEncodingException {
        String officerId = request.getParameter("recordOfficeId");
        String loanOfficerId = request.getParameter("recordLoanOfficerId");
        String url = String.format("centerCustAction.do?globalCustNum=%s&recordOfficeId=%s&recordLoanOfficerId=%s",
                centerBO.getGlobalCustNum(), officerId, loanOfficerId);
        return URLEncoder.encode(url, "UTF-8");
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

        CustomerSearch searchResult = this.customerServiceFacade.search(searchString);

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

        CustomerSearch searchResult = this.customerServiceFacade.search(searchString);

        addSeachValues(searchString, searchResult.getOfficeId(), searchResult.getOfficeName(), request);
        SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, searchResult.getSearchResult(), request);
        return actionForward;
    }

    private void cleanSearchResults(HttpServletRequest request, CenterCustActionForm actionForm)
            throws PageExpiredException {
        actionForm.setSearchString(null);
        cleanUpSearch(request);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward captureQuestionResponses(
            final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "captureQuestionResponses");
        ActionErrors errors = createCenterQuestionnaire.validateResponses(request, (CenterCustActionForm) form);
        if (errors != null && !errors.isEmpty()) {
            addErrors(request, errors);
            return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
        }
        return createCenterQuestionnaire.rejoinFlow(mapping);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editQuestionResponses(
            final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "editQuestionResponses");
        return createCenterQuestionnaire.editResponses(mapping, request, (CenterCustActionForm) form);
    }
}