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

package org.mifos.application.customer.client.struts.action;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Hibernate;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerCustomFieldEntity;
import org.mifos.application.customer.business.CustomerFlagDetailEntity;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailEntity;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailEntity;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.service.ClientBusinessService;
import org.mifos.application.customer.client.persistence.ClientPersistence;
import org.mifos.application.customer.client.struts.actionforms.ClientCustActionForm;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.struts.action.CustAction;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.SpouseFatherLookupEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.ValidationConstants;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class ClientCustAction extends CustAction {

    @Override
    protected BusinessService getService() {
        return getClientBusinessService();
    }

    private ClientBusinessService getClientBusinessService() {
        return (ClientBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Client);
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("clientCustAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("chooseOffice", SecurityConstants.VIEW);
        security.allow("next", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previewPersonalInfo", SecurityConstants.VIEW);
        security.allow("retrievePictureOnPreview", SecurityConstants.VIEW);
        security.allow("prevPersonalInfo", SecurityConstants.VIEW);
        security.allow("prevMFIInfo", SecurityConstants.VIEW);
        security.allow("prevMeeting", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("loadMeeting", SecurityConstants.MEETING_CREATE_CLIENT_MEETING);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("editPersonalInfo", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("previewEditPersonalInfo", SecurityConstants.VIEW);
        security.allow("prevEditPersonalInfo", SecurityConstants.VIEW);
        security.allow("updatePersonalInfo", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("editMfiInfo", SecurityConstants.CIENT_EDIT_MFI_INFORMATION);
        security.allow("previewEditMfiInfo", SecurityConstants.VIEW);
        security.allow("prevEditMfiInfo", SecurityConstants.VIEW);
        security.allow("updateMfiInfo", SecurityConstants.CIENT_EDIT_MFI_INFORMATION);
        security.allow("retrievePicture", SecurityConstants.VIEW);
        security.allow("showPicture", SecurityConstants.VIEW);
        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward chooseOffice(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setGroupFlag(ClientConstants.NO);
        return mapping.findForward(ActionForwards.chooseOffice_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        doCleanUp(actionForm, request);
        SessionUtils.removeAttribute(CustomerConstants.CUSTOMER_MEETING, request);
        if (actionForm.getGroupFlagValue().equals(YesNoFlag.YES.getValue())) {
            CustomerBO customer = getCustomerBusinessService().getCustomer(
                    Integer.valueOf(actionForm.getParentGroupId()));
            // make sure that lazily loaded child objects get read in
            // so that they are there to be used when the object is detached
            // from the Hibernate session
            Hibernate.initialize(customer.getPersonnel());
            Hibernate.initialize(customer.getOffice());
            actionForm.setParentGroup(customer);

            if (actionForm.getParentGroup().getCustomerMeeting() != null) {
                actionForm.getParentGroup().getCustomerMeeting().getMeeting().isMonthly();
                actionForm.getParentGroup().getCustomerMeeting().getMeeting().isWeekly();
            }
            actionForm.setOfficeId(actionForm.getParentGroup().getOffice().getOfficeId().toString());
            if (actionForm.getParentGroup().getPersonnel() != null)
                actionForm.setFormedByPersonnel(actionForm.getParentGroup().getPersonnel().getPersonnelId().toString());
        }
        loadCreateMasterData(actionForm, request);
        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, ClientRules.getCenterHierarchyExists(),
                request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.next_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        if (actionForm.getParentGroupId() != null) {
            CustomerBO parent = getCustomerBusinessService()
                    .getCustomer(Integer.valueOf(actionForm.getParentGroupId()));
            if (null != parent && null != parent.getParentCustomer()) {
                parent.getParentCustomer().getDisplayName();
            }
            Hibernate.initialize(parent.getPersonnel());
            Hibernate.initialize(parent.getOffice());
            actionForm.setParentGroup(parent);
        }
        if (ProcessFlowRules.isClientPendingApprovalStateEnabled() == true) {
            SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, CustomerConstants.YES, request);

        } else
            SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, CustomerConstants.NO, request);
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth())));
        checkForGovtIdAndDisplayNameDobDuplicacy(request, actionForm);
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    private void checkForGovtIdAndDisplayNameDobDuplicacy(HttpServletRequest request, ClientCustActionForm actionForm)
            throws PersistenceException, PageExpiredException, InvalidDateException {
        ClientPersistence clientPersistence = new ClientPersistence();
        String governmentId = actionForm.getGovernmentId();
        /*
         * If govt id is not null or empty, check if client with same govt id is
         * present in closed state and display warning otherwise if govt id is
         * null or empty, and display name + dob combination is present in
         * closed state display warning
         */
        if (!StringUtils.isNullOrEmpty(governmentId)
                && clientPersistence.checkForDuplicacyOnGovtIdForClosedClients(governmentId)) {
            SessionUtils.addWarningMessage(request, CustomerConstants.CLIENT_WITH_SAME_GOVT_ID_EXIST_IN_CLOSED);
        } else if (StringUtils.isNullOrEmpty(governmentId)
                && clientPersistence.checkForDuplicacyForClosedClientsOnNameAndDob(actionForm.getClientName()
                        .getDisplayName(), DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth()))) {
            SessionUtils.addWarningMessage(request, CustomerConstants.CLIENT_WITH_SAME_GOVT_ID_EXIST_IN_CLOSED);
        }
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String forward = null;
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        String fromPage = actionForm.getInput();
        if (ClientConstants.INPUT_PERSONAL_INFO.equals(fromPage) || ClientConstants.INPUT_MFI_INFO.equals(fromPage)
                || CenterConstants.INPUT_CREATE.equals(fromPage))
            forward = ActionForwards.cancelCreate_success.toString();
        else if (ClientConstants.INPUT_EDIT_PERSONAL_INFO.equals(fromPage)
                || ClientConstants.INPUT_EDIT_MFI_INFO.equals(fromPage))
            forward = ActionForwards.cancelEdit_success.toString();
        return mapping.findForward(forward);
    }

    private void loadCreateMasterData(ClientCustActionForm actionForm, HttpServletRequest request)
            throws ApplicationException, SystemException {
        Short officeId = null;
        loadMasterDataEntities(actionForm, request);
        loadCreateCustomFields(actionForm, EntityType.CLIENT, request);
        if (actionForm.getGroupFlagValue().equals(YesNoFlag.NO.getValue())) {
            loadLoanOfficers(actionForm.getOfficeIdValue(), request);
            officeId = actionForm.getOfficeIdValue();
            loadFees(actionForm, request, FeeCategory.CLIENT, null);
        } else {
            officeId = actionForm.getParentGroup().getOffice().getOfficeId();
            if (actionForm.getParentGroup().getCustomerMeeting() != null)
                loadFees(actionForm, request, FeeCategory.CLIENT, actionForm.getParentGroup().getCustomerMeeting()
                        .getMeeting());
            else
                loadFees(actionForm, request, FeeCategory.CLIENT, null);
        }
        loadFormedByPersonnel(officeId, request);
        SessionUtils.setCollectionAttribute(ClientConstants.SAVINGS_OFFERING_LIST, getClientBusinessService()
                .retrieveOfferingsApplicableToClient(), request);
    }

    private void loadMasterDataEntities(ClientCustActionForm actionForm, HttpServletRequest request)
            throws ApplicationException, SystemException {
        SessionUtils.setCollectionAttribute(ClientConstants.SALUTATION_ENTITY, getCustomerBusinessService()
                .retrieveMasterEntities(MasterConstants.SALUTATION, getUserContext(request).getLocaleId()), request);
        SessionUtils
                .setCollectionAttribute(ClientConstants.MARITAL_STATUS_ENTITY, getCustomerBusinessService()
                        .retrieveMasterEntities(MasterConstants.MARITAL_STATUS, getUserContext(request).getLocaleId()),
                        request);
        SessionUtils.setCollectionAttribute(ClientConstants.CITIZENSHIP_ENTITY, getCustomerBusinessService()
                .retrieveMasterEntities(MasterConstants.CITIZENSHIP, getUserContext(request).getLocaleId()), request);
        SessionUtils.setCollectionAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, getCustomerBusinessService()
                .retrieveMasterEntities(MasterConstants.BUSINESS_ACTIVITIES, getUserContext(request).getLocaleId()),
                request);
        SessionUtils.setCollectionAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, getCustomerBusinessService()
                .retrieveMasterEntities(MasterConstants.EDUCATION_LEVEL, getUserContext(request).getLocaleId()),
                request);
        SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, getCustomerBusinessService()
                .retrieveMasterEntities(MasterConstants.GENDER, getUserContext(request).getLocaleId()), request);
        SessionUtils.setCollectionAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, getMasterEntities(
                SpouseFatherLookupEntity.class, getUserContext(request).getLocaleId()), request);
        SessionUtils.setCollectionAttribute(ClientConstants.HANDICAPPED_ENTITY, getCustomerBusinessService()
                .retrieveMasterEntities(MasterConstants.HANDICAPPED, getUserContext(request).getLocaleId()), request);
        SessionUtils.setCollectionAttribute(ClientConstants.ETHINICITY_ENTITY, getCustomerBusinessService()
                .retrieveMasterEntities(MasterConstants.ETHINICITY, getUserContext(request).getLocaleId()), request);
        SessionUtils
                .setCollectionAttribute(ClientConstants.POVERTY_STATUS, getCustomerBusinessService()
                        .retrieveMasterEntities(MasterConstants.POVERTY_STATUS, getUserContext(request).getLocaleId()),
                        request);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward retrievePictureOnPreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        InputStream in = actionForm.getPicture().getInputStream();
        in.mark(0);
        response.setContentType("image/jpeg");
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        byte[] by = new byte[1024 * 4]; // 4K buffer buf, 0, buf.length
        int index = in.read(by, 0, 1024 * 4);
        while (index != -1) {
            out.write(by, 0, index);
            index = in.read(by, 0, 1024 * 4);
        }
        out.flush();
        out.close();
        in.reset();
        String forward = ClientConstants.CUSTOMER_PICTURE_PAGE;
        return mapping.findForward(forward);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward retrievePicture(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ClientBO clientBO = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        InputStream in = clientBO.getCustomerPicture().getPicture().getBinaryStream();

        in.mark(0);
        response.setContentType("image/jpeg");
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        byte[] by = new byte[1024 * 4]; // 4K buffer buf, 0, buf.length
        int index = in.read(by, 0, 1024 * 4);
        while (index != -1) {
            out.write(by, 0, index);
            index = in.read(by, 0, 1024 * 4);
        }
        out.flush();
        out.close();
        in.reset();
        String forward = ClientConstants.CUSTOMER_PICTURE_PAGE;
        return mapping.findForward(forward);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth())));
        return mapping.findForward(ActionForwards.previewPersonalInfo_success.toString());

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        return mapping.findForward(ActionForwards.prevPersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevMFIInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        return mapping.findForward(ActionForwards.prevMFIInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevMeeting(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        return mapping.findForward(ActionForwards.next_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadMeeting(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.loadMeeting_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ClientBO client = null;
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
        List<CustomFieldView> customFields = actionForm.getCustomFields();
        UserContext userContext = getUserContext(request);
        convertCustomFieldDateToUniformPattern(customFields, userContext.getPreferredLocale());
        Short personnelId = null;
        Short officeId = null;
        CustomerPersistence customerPersistence = new CustomerPersistence();
        if (actionForm.getGroupFlagValue().equals(YesNoFlag.YES.getValue())) {
            if (actionForm.getParentGroup().getPersonnel() != null) {
                personnelId = actionForm.getParentGroup().getPersonnel().getPersonnelId();
            }
            officeId = actionForm.getParentGroup().getOffice().getOfficeId();
        } else {
            personnelId = actionForm.getLoanOfficerIdValue();
            officeId = actionForm.getOfficeIdValue();
        }
        if (personnelId != null)
            checkPermissionForCreate(actionForm.getStatusValue().getValue(), getUserContext(request), null, officeId,
                    personnelId);
        else
            checkPermissionForCreate(actionForm.getStatusValue().getValue(), getUserContext(request), null, officeId,
                    getUserContext(request).getId());
        List<SavingsOfferingBO> selectedOfferings = getSelectedOfferings(actionForm, request);
        if (actionForm.getGroupFlagValue().equals(YesNoFlag.NO.getValue())) {
            client = new ClientBO(userContext, actionForm.getClientName().getDisplayName(),
                    actionForm.getStatusValue(), actionForm.getExternalId(), DateUtils
                            .getDateAsSentFromBrowser(actionForm.getMfiJoiningDate()), actionForm.getAddress(),
                    customFields, actionForm.getFeesToApply(), selectedOfferings, new PersonnelPersistence()
                            .getPersonnel(actionForm.getFormedByPersonnelValue()), new OfficePersistence()
                            .getOffice(actionForm.getOfficeIdValue()), meeting, new PersonnelPersistence()
                            .getPersonnel(actionForm.getLoanOfficerIdValue()), DateUtils
                            .getDateAsSentFromBrowser(actionForm.getDateOfBirth()), actionForm.getGovernmentId(),
                    actionForm.getTrainedValue(), DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()),
                    actionForm.getGroupFlagValue(), actionForm.getClientName(), actionForm.getSpouseName(), actionForm
                            .getClientDetailView(), actionForm.getCustomerPicture());
        } else {
            CustomerBO parentCustomer = getCustomerBusinessService().getCustomer(
                    actionForm.getParentGroup().getCustomerId());
            parentCustomer.setVersionNo(actionForm.getParentGroup().getVersionNo());
            client = new ClientBO(userContext, actionForm.getClientName().getDisplayName(),
                    actionForm.getStatusValue(), actionForm.getExternalId(), DateUtils
                            .getDateAsSentFromBrowser(actionForm.getMfiJoiningDate()), actionForm.getAddress(),
                    customFields, actionForm.getFeesToApply(), selectedOfferings, new PersonnelPersistence()
                            .getPersonnel(actionForm.getFormedByPersonnelValue()), parentCustomer.getOffice(),
                    parentCustomer, DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth()), actionForm
                            .getGovernmentId(), actionForm.getTrainedValue(), DateUtils
                            .getDateAsSentFromBrowser(actionForm.getTrainedDate()), actionForm.getGroupFlagValue(),
                    actionForm.getClientName(), actionForm.getSpouseName(), actionForm.getClientDetailView(),
                    actionForm.getCustomerPicture());
        }
        new CustomerPersistence().saveCustomer(client);
        actionForm.setCustomerId(client.getCustomerId().toString());
        actionForm.setGlobalCustNum(client.getGlobalCustNum());
        client = null;
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    private List<SavingsOfferingBO> getSelectedOfferings(ClientCustActionForm actionForm, HttpServletRequest request)
            throws Exception {
        List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>(ClientConstants.MAX_OFFERINGS_SIZE);
        List<SavingsOfferingBO> offeringsList = (List<SavingsOfferingBO>) SessionUtils.getAttribute(
                ClientConstants.SAVINGS_OFFERING_LIST, request);
        for (Short offeringId : actionForm.getSelectedOfferings()) {
            if (offeringId != null) {
                for (SavingsOfferingBO savingsOffering : offeringsList)
                    if (offeringId.equals(savingsOffering.getPrdOfferingId()))
                        selectedOfferings.add(savingsOffering);
            }
        }
        return selectedOfferings;
    }

    private void doCleanUp(ClientCustActionForm actionForm, HttpServletRequest request) {
        clearActionForm(actionForm);
    }

    private void clearActionForm(ClientCustActionForm actionForm) {
        actionForm.setDefaultFees(new ArrayList<FeeView>());
        actionForm.setAdditionalFees(new ArrayList<FeeView>());
        actionForm.setCustomFields(new ArrayList<CustomFieldView>());
        actionForm.setAddress(new Address());
        actionForm.setDisplayName(null);
        actionForm.setDateOfBirthDD(null);
        actionForm.setDateOfBirthMM(null);
        actionForm.setDateOfBirthYY(null);
        actionForm.setGovernmentId(null);
        actionForm.setMfiJoiningDate(null);
        actionForm.setGlobalCustNum(null);
        actionForm.setCustomerId(null);
        actionForm.setExternalId(null);
        actionForm.setLoanOfficerId(null);
        actionForm.setFormedByPersonnel(null);
        actionForm.setTrained(null);
        actionForm.setTrainedDate(null);
        actionForm.setParentGroup(null);
        actionForm.setClientName(new ClientNameDetailView());
        actionForm.setSpouseName(new ClientNameDetailView());
        actionForm.setClientDetailView(new ClientDetailView());
        actionForm.setNextOrPreview("next");
        for (int i = 0; i < actionForm.getSelectedOfferings().size(); i++)
            actionForm.getSelectedOfferings().set(i, null);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;

        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        Integer loanIndividualMonitoringIsEnabled = configurationPersistence.getConfigurationKeyValueInteger(
                "loanIndividualMonitoringIsEnabled").getValue();
        if (null != loanIndividualMonitoringIsEnabled && loanIndividualMonitoringIsEnabled.intValue() != 0)
            SessionUtils.setAttribute(LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED,
                    loanIndividualMonitoringIsEnabled.intValue(), request);

        ClientBO clientBO = (ClientBO) getCustomerBusinessService().findBySystemId(actionForm.getGlobalCustNum(),
                CustomerLevel.CLIENT.getValue());
        clientBO.setUserContext(getUserContext(request));
        clientBO.getCustomerStatus().setLocaleId(getUserContext(request).getLocaleId());
        for (CustomerFlagDetailEntity custFlag : clientBO.getCustomerFlags())
            custFlag.getStatusFlag().setLocaleId(getUserContext(request).getLocaleId());
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, clientBO, request);
        request.getSession().setAttribute(Constants.BUSINESS_KEY, clientBO);
        request.removeAttribute(ClientConstants.AGE);
        loadMasterDataForDetailsPage(request, clientBO);
        setSpouseOrFatherName(request, clientBO);
        setPicture(actionForm, clientBO, request);

        SurveysPersistence surveysPersistence = new SurveysPersistence();
        List<SurveyInstance> surveys = surveysPersistence.retrieveInstancesByCustomer(clientBO);
        boolean activeSurveys = surveysPersistence.retrieveSurveysByTypeAndState(SurveyType.CLIENT, SurveyState.ACTIVE)
                .size() > 0;
        request.setAttribute(CustomerConstants.SURVEY_KEY, surveys);
        request.setAttribute(CustomerConstants.SURVEY_COUNT, activeSurveys);
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    private void setPicture(ClientCustActionForm actionForm, ClientBO clientBO, HttpServletRequest request)
            throws Exception {
        if (clientBO.getCustomerPicture() != null && clientBO.getCustomerPicture().getPicture().length() != 0) {
            SessionUtils.setAttribute("noPictureOnGet", "No", request);
        } else {
            SessionUtils.setAttribute("noPictureOnGet", "Yes", request);
        }

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward showPicture(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String forward = ClientConstants.CUSTOMER_PICTURE_PAGE;
        return mapping.findForward(forward);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        clearActionForm(actionForm);
        ClientBO client = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        ClientBO clientBO = (ClientBO) getCustomerBusinessService().findBySystemId(client.getGlobalCustNum(),
                CustomerLevel.CLIENT.getValue());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, clientBO, request);
        client = null;
        loadUpdateMasterData(actionForm, request);
        setValuesInActionForm(actionForm, request);
        return mapping.findForward(ActionForwards.editPersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewEditPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth())));
        checkForGovtIdAndDisplayNameDobDuplicacy(request, actionForm);
        return mapping.findForward(ActionForwards.previewEditPersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevEditPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth())));
        return mapping.findForward(ActionForwards.prevEditPersonalInfo_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updatePersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws ApplicationException, InvalidDateException {

        ClientBO clientInSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        ClientBO client = getClientBusinessService().getClient(clientInSession.getCustomerId());
        checkVersionMismatch(clientInSession.getVersionNo(), client.getVersionNo());
        client.setVersionNo(clientInSession.getVersionNo());
        clientInSession = null;
        client.setUserContext(getUserContext(request));
        setInitialObjectForAuditLogging(client);

        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        client.updateAddress(actionForm.getAddress());
        convertCustomFieldDateToUniformPattern(actionForm.getCustomFields(), getUserContext(request)
                .getPreferredLocale());
        for (CustomFieldView fieldView : actionForm.getCustomFields())
            for (CustomerCustomFieldEntity fieldEntity : client.getCustomFields())
                if (fieldView.getFieldId().equals(fieldEntity.getFieldId()))
                    fieldEntity.setFieldValue(fieldView.getFieldValue());
        client.getClientName().updateNameDetails(actionForm.getClientName());
        client.getSpouseName().updateNameDetails(actionForm.getSpouseName());
        client.setFirstName(actionForm.getClientName().getFirstName());
        client.setLastName(actionForm.getClientName().getLastName());
        client.setSecondLastName(actionForm.getClientName().getSecondLastName());
        if (actionForm.getPicture() != null && !StringUtils.isNullOrEmpty(actionForm.getPicture().getFileName())) {
            client.updatePicture(actionForm.getCustomerPicture());
        }
        client.setUserContext(getUserContext(request));
        client.updateClientDetails(actionForm.getClientDetailView());
        client.updatePersonalInfo(actionForm.getClientName().getDisplayName(), actionForm.getGovernmentId(), DateUtils
                .getDateAsSentFromBrowser(actionForm.getDateOfBirth()));

        return mapping.findForward(ActionForwards.updatePersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editMfiInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, ClientRules.getCenterHierarchyExists(),
                request);
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        clearActionForm(actionForm);
        ClientBO client = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        ClientBO clientBO = (ClientBO) getCustomerBusinessService().findBySystemId(client.getGlobalCustNum(),
                CustomerLevel.CLIENT.getValue());
        client = null;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, clientBO, request);
        if (!clientBO.isClientUnderGroup())
            loadUpdateMfiMasterData(clientBO.getOffice().getOfficeId(), request);
        setValuesForMfiEditInActionForm(actionForm, request);
        return mapping.findForward(ActionForwards.editMfiInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewEditMfiInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        return mapping.findForward(ActionForwards.previewEditMfiInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevEditMfiInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        return mapping.findForward(ActionForwards.prevEditMfiInfo_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updateMfiInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ClientBO clientInSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        ClientBO client = getClientBusinessService().getClient(clientInSession.getCustomerId());
        checkVersionMismatch(clientInSession.getVersionNo(), client.getVersionNo());
        client.setVersionNo(clientInSession.getVersionNo());
        clientInSession = null;
        client.setUserContext(getUserContext(request));
        setInitialObjectForAuditLogging(client);
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        client.setExternalId(actionForm.getExternalId());
        if (actionForm.getTrainedValue() != null && actionForm.getTrainedValue().equals(YesNoFlag.YES.getValue()))
            client.setTrained(true);
        else
            client.setTrained(false);

        client.setTrainedDate(DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()));
        PersonnelBO personnel = null;

        if (actionForm.getGroupFlagValue().equals(YesNoFlag.NO.getValue())) {
            if (actionForm.getLoanOfficerIdValue() != null) {
                personnel = getPersonnelBusinessService().getPersonnel(actionForm.getLoanOfficerIdValue());
            }
        } else if (actionForm.getGroupFlagValue().equals(YesNoFlag.YES.getValue())) {
            personnel = client.getPersonnel();
        }

        client.updateMfiInfo(personnel);
        client.setUserContext(getUserContext(request));
        return mapping.findForward(ActionForwards.updateMfiInfo_success.toString());
    }

    private void setValuesInActionForm(ClientCustActionForm actionForm, HttpServletRequest request) throws Exception {
        ClientBO client = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        if (client.getPersonnel() != null) {
            actionForm.setLoanOfficerId(client.getPersonnel().getPersonnelId().toString());
        }
        actionForm.setCustomerId(client.getCustomerId().toString());
        actionForm.setGlobalCustNum(client.getGlobalCustNum());
        actionForm.setExternalId(client.getExternalId());
        actionForm.setAddress(client.getAddress());
        actionForm.setCustomFields(createCustomFieldViews(client.getCustomFields(), request));
        for (ClientNameDetailView nameView : createNameViews(client.getNameDetailSet())) {
            if (nameView.getNameType().equals(ClientConstants.CLIENT_NAME_TYPE)) {
                actionForm.setClientName(nameView);
            } else {
                actionForm.setSpouseName(nameView);
            }
        }
        actionForm.setClientDetailView(createClientDetailView(client.getCustomerDetail()));
        actionForm.setGovernmentId(client.getGovernmentId());
        actionForm.setDateOfBirth(DateUtils.makeDateAsSentFromBrowser(client.getDateOfBirth()));

    }

    private void setValuesForMfiEditInActionForm(ClientCustActionForm actionForm, HttpServletRequest request)
            throws Exception {
        ClientBO client = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        if (client.getPersonnel() != null) {
            actionForm.setLoanOfficerId(client.getPersonnel().getPersonnelId().toString());
            client.getPersonnel().getDisplayName();
        }
        actionForm.setCustomerId(client.getCustomerId().toString());
        actionForm.setGlobalCustNum(client.getGlobalCustNum());
        actionForm.setExternalId(client.getExternalId());
        if (client.isClientUnderGroup()) {
            actionForm.setGroupFlag(ClientConstants.YES);
            actionForm.setParentGroup(client.getParentCustomer());
        } else
            actionForm.setGroupFlag(ClientConstants.NO);
        if (client.isTrained())
            actionForm.setTrained(ClientConstants.YES);
        else
            actionForm.setTrained(ClientConstants.NO);
        if (client.getTrainedDate() != null)
            actionForm.setTrainedDate(DateUtils.makeDateAsSentFromBrowser(client.getTrainedDate()));
    }

    private void loadUpdateMfiMasterData(Short officeId, HttpServletRequest request) throws Exception {
        loadLoanOfficers(officeId, request);
    }

    private ClientDetailView createClientDetailView(ClientDetailEntity customerDetail) {
        return new ClientDetailView(customerDetail.getEthinicity(), customerDetail.getCitizenship(), customerDetail
                .getHandicapped(), customerDetail.getBusinessActivities(), customerDetail.getMaritalStatus(),
                customerDetail.getEducationLevel(), customerDetail.getNumChildren(), customerDetail.getGender(),
                customerDetail.getPovertyStatus());
    }

    private List<ClientNameDetailView> createNameViews(Set<ClientNameDetailEntity> nameDetailSet) {
        List<ClientNameDetailView> clientNameDetailViews = new ArrayList<ClientNameDetailView>();
        for (ClientNameDetailEntity clientNameDetail : nameDetailSet) {
            clientNameDetailViews.add(createNameViewObject(clientNameDetail));
        }
        return clientNameDetailViews;
    }

    private ClientNameDetailView createNameViewObject(ClientNameDetailEntity clientNameDetail) {
        ClientNameDetailView nameView = new ClientNameDetailView(clientNameDetail.getNameType(), clientNameDetail
                .getSalutation(), new StringBuilder(clientNameDetail.getDisplayName()), clientNameDetail.getName()
                .getFirstName(), clientNameDetail.getName().getMiddleName(), clientNameDetail.getName().getLastName(),
                clientNameDetail.getName().getSecondLastName());
        return nameView;
    }

    private void loadUpdateMasterData(ClientCustActionForm actionForm, HttpServletRequest request)
            throws ApplicationException, SystemException {
        loadMasterDataEntities(actionForm, request);
        loadCustomFieldDefinitions(EntityType.CLIENT, request);
    }

    private void loadMasterDataForDetailsPage(HttpServletRequest request, ClientBO clientBO) throws Exception {
        Short localeId = getUserContext(request).getLocaleId();
        loadCustomFieldDefinitions(EntityType.CLIENT, request);
        SessionUtils.setAttribute(ClientConstants.AGE, calculateAge(new java.sql.Date((clientBO.getDateOfBirth())
                .getTime())), request);
        SessionUtils.setCollectionAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, getMasterEntities(
                SpouseFatherLookupEntity.class, localeId), request);
        SessionUtils.setAttribute(CustomerConstants.CUSTOMERPERFORMANCE, getCustomerBusinessService().numberOfMeetings(
                true, clientBO.getCustomerId()), request);
        SessionUtils.setAttribute(CustomerConstants.CUSTOMERPERFORMANCEHISTORY, getCustomerBusinessService()
                .numberOfMeetings(false, clientBO.getCustomerId()), request);
        SessionUtils.setCollectionAttribute(ClientConstants.LOANCYCLECOUNTER, getCustomerBusinessService()
                .fetchLoanCycleCounter(clientBO), request);
        List<LoanBO> loanAccounts = clientBO.getOpenLoanAccounts();
        List<SavingsBO> savingsAccounts = clientBO.getOpenSavingAccounts();
        setLocaleIdToLoanStatus(loanAccounts, localeId);
        setLocaleIdToSavingsStatus(savingsAccounts, localeId);
        SessionUtils.setCollectionAttribute(ClientConstants.CUSTOMERLOANACCOUNTSINUSE, loanAccounts, request);
        SessionUtils.setCollectionAttribute(ClientConstants.CUSTOMERSAVINGSACCOUNTSINUSE, savingsAccounts, request);
        SessionUtils.setAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY_NAME, getNameForBusinessActivityEntity(
                clientBO.getCustomerDetail().getBusinessActivities(), localeId), request);
        SessionUtils.setAttribute(ClientConstants.HANDICAPPED_ENTITY_NAME, getNameForBusinessActivityEntity(clientBO
                .getCustomerDetail().getHandicapped(), localeId), request);
        SessionUtils.setAttribute(ClientConstants.MARITAL_STATUS_ENTITY_NAME, getNameForBusinessActivityEntity(clientBO
                .getCustomerDetail().getMaritalStatus(), localeId), request);
        SessionUtils.setAttribute(ClientConstants.CITIZENSHIP_ENTITY_NAME, getNameForBusinessActivityEntity(clientBO
                .getCustomerDetail().getCitizenship(), localeId), request);
        SessionUtils.setAttribute(ClientConstants.ETHINICITY_ENTITY_NAME, getNameForBusinessActivityEntity(clientBO
                .getCustomerDetail().getEthinicity(), localeId), request);
        SessionUtils.setAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY_NAME, getNameForBusinessActivityEntity(
                clientBO.getCustomerDetail().getEducationLevel(), localeId), request);
        SessionUtils.setCollectionAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, getMasterEntities(
                SpouseFatherLookupEntity.class, localeId), request);
        SessionUtils
                .setCollectionAttribute(ClientConstants.POVERTY_STATUS, getCustomerBusinessService()
                        .retrieveMasterEntities(MasterConstants.POVERTY_STATUS, getUserContext(request).getLocaleId()),
                        request);

    }

    private void setLocaleIdToLoanStatus(List<LoanBO> accountList, Short localeId) {
        for (LoanBO accountBO : accountList)
            setLocaleForAccount(accountBO, localeId);
    }

    private void setLocaleIdToSavingsStatus(List<SavingsBO> accountList, Short localeId) {
        for (SavingsBO accountBO : accountList)
            setLocaleForAccount(accountBO, localeId);
    }

    private void setLocaleForAccount(AccountBO account, Short localeId) {
        account.getAccountState().setLocaleId(localeId);
    }

    private String getNameForBusinessActivityEntity(Integer entityId, Short localeId) throws PersistenceException,
            ServiceException {
        if (entityId != null)
            return ((MasterDataService) ServiceFactory.getInstance().getBusinessService(
                    BusinessServiceName.MasterDataService)).retrieveMasterEntities(entityId, localeId);
        return "";
    }

    private void setSpouseOrFatherName(HttpServletRequest request, ClientBO clientBO) throws Exception {
        for (ClientNameDetailEntity clientNameDetailEntity : clientBO.getNameDetailSet()) {
            if (clientNameDetailEntity.getNameType().shortValue() != ClientConstants.CLIENT_NAME_TYPE) {
                SessionUtils.setAttribute(ClientConstants.SPOUSE_FATHER_NAME_VALUE, clientNameDetailEntity
                        .getDisplayName(), request);
                SessionUtils.setAttribute(ClientConstants.SPOUSE_FATHER_VALUE, findMasterEntity(request,
                        ClientConstants.SPOUSE_FATHER_ENTITY, clientNameDetailEntity.getNameType()).getName(), request);
                break;
            }
        }
    }

    private MasterDataEntity findMasterEntity(HttpServletRequest request, String collectionName, Short value)
            throws Exception {
        List<MasterDataEntity> entities = (List<MasterDataEntity>) SessionUtils.getAttribute(collectionName, request);
        for (MasterDataEntity entity : entities)
            if (entity.getId().equals(value))
                return entity;
        return null;
    }

    private PersonnelBusinessService getPersonnelBusinessService() throws ServiceException {
        return (PersonnelBusinessService) ServiceFactory.getInstance()
                .getBusinessService(BusinessServiceName.Personnel);
    }

    public int calculateAge(Date date) {
        int age = DateUtils.DateDiffInYears(date);
        return age;
    }
}
