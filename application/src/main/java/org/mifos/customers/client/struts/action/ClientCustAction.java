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

package org.mifos.customers.client.struts.action;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Hibernate;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.SpouseFatherLookupEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ClientFormCreationDto;
import org.mifos.application.servicefacade.CustomerServiceFacade;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.servicefacade.OnlyBranchOfficeHierarchyDto;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientDetailEntity;
import org.mifos.customers.client.business.ClientDetailView;
import org.mifos.customers.client.business.ClientFamilyDetailEntity;
import org.mifos.customers.client.business.ClientFamilyDetailView;
import org.mifos.customers.client.business.ClientNameDetailEntity;
import org.mifos.customers.client.business.ClientNameDetailView;
import org.mifos.customers.client.business.FamilyDetailDTO;
import org.mifos.customers.client.business.service.ClientBusinessService;
import org.mifos.customers.client.business.service.ClientDetailsServiceFacade;
import org.mifos.customers.client.business.service.ClientInformationDto;
import org.mifos.customers.client.persistence.ClientPersistence;
import org.mifos.customers.client.struts.actionforms.ClientCustActionForm;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.struts.action.CustAction;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class ClientCustAction extends CustAction {

    private final CustomerServiceFacade customerServiceFacade = DependencyInjectedServiceLocator
            .locateCustomerServiceFacade();
    private final ClientDetailsServiceFacade clientDetailsServiceFacade = DependencyInjectedServiceLocator
            .locateClientDetailsServiceFacade();
    private final CustomerDao customerDao = DependencyInjectedServiceLocator.locateCustomerDao();

    @Override
    protected BusinessService getService() {
        return getClientBusinessService();
    }

    private ClientBusinessService getClientBusinessService() {
        return new ClientBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(@SuppressWarnings("unused") String method) {
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
        security.allow("editFamilyInfo", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("editAddFamilyRow", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("editDeleteFamilyRow", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("previewEditFamilyInfo", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
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
        security.allow("familyInfoNext", SecurityConstants.VIEW);
        security.allow("prevFamilyInfo", SecurityConstants.VIEW);
        security.allow("prevFamilyInfoNext", SecurityConstants.VIEW);
        security.allow("addFamilyRow", SecurityConstants.VIEW);
        security.allow("deleteFamilyRow", SecurityConstants.VIEW);
        security.allow("updateFamilyInfo", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("editPreviewEditFamilyInfo", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward chooseOffice(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setGroupFlag(ClientConstants.NO);

        UserContext userContext = getUserContext(request);

        OnlyBranchOfficeHierarchyDto officeHierarchy = customerServiceFacade
                .retrieveBranchOnlyOfficeHierarchy(userContext);

        SessionUtils.setAttribute(OnlyBranchOfficeHierarchyDto.IDENTIFIER, officeHierarchy, request);

        return mapping.findForward(ActionForwards.chooseOffice_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        clearActionForm(actionForm);
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
            if (actionForm.getParentGroup().getPersonnel() != null) {
                actionForm.setFormedByPersonnel(actionForm.getParentGroup().getPersonnel().getPersonnelId().toString());
            }
        }

        Short officeId = null;
        UserContext userContext = getUserContext(request);
        ClientFormCreationDto clientFormCreationDto = this.customerServiceFacade
                .retrieveClientFormCreationData(userContext);

        actionForm.setCustomFields(clientFormCreationDto.getCustomFieldViews());

        if (actionForm.getGroupFlagValue().equals(YesNoFlag.NO.getValue())) {
            loadLoanOfficers(actionForm.getOfficeIdValue(), request);
            officeId = actionForm.getOfficeIdValue();
            loadFees(actionForm, request, FeeCategory.CLIENT, null);
        } else {
            officeId = actionForm.getParentGroup().getOffice().getOfficeId();
            if (actionForm.getParentGroup().getCustomerMeeting() != null) {
                loadFees(actionForm, request, FeeCategory.CLIENT, actionForm.getParentGroup().getCustomerMeeting()
                        .getMeeting());
            } else {
                loadFees(actionForm, request, FeeCategory.CLIENT, null);
            }
        }

        List<PersonnelView> formedByPersonnel = this.customerDao.findLoanOfficerThatFormedOffice(officeId);

        SessionUtils.setCollectionAttribute(ClientConstants.SALUTATION_ENTITY, clientFormCreationDto.getSalutations(),
                request);
        SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, clientFormCreationDto.getGenders(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.MARITAL_STATUS_ENTITY, clientFormCreationDto
                .getMaritalStatuses(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.CITIZENSHIP_ENTITY, clientFormCreationDto.getCitizenship(),
                request);
        SessionUtils.setCollectionAttribute(ClientConstants.ETHINICITY_ENTITY, clientFormCreationDto.getEthinicity(),
                request);
        SessionUtils.setCollectionAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, clientFormCreationDto
                .getEducationLevels(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, clientFormCreationDto
                .getBusinessActivity(), request);
        SessionUtils
                .setCollectionAttribute(ClientConstants.POVERTY_STATUS, clientFormCreationDto.getPoverty(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.HANDICAPPED_ENTITY, clientFormCreationDto.getHandicapped(),
                request);
        SessionUtils.setCollectionAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, clientFormCreationDto
                .getSpouseFather(), request);

        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, clientFormCreationDto
                .getCustomFieldViews(), request);

        SessionUtils.setCollectionAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST, formedByPersonnel, request);
        SessionUtils.setCollectionAttribute(ClientConstants.SAVINGS_OFFERING_LIST, getClientBusinessService()
                .retrieveOfferingsApplicableToClient(), request);
        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, ClientRules.getCenterHierarchyExists(),
                request);
        SessionUtils.setAttribute(ClientConstants.MAXIMUM_NUMBER_OF_FAMILY_MEMBERS, ClientRules
                .getMaximumNumberOfFamilyMembers(), request);
        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_REQUIRED, ClientRules.isFamilyDetailsRequired(),
                request);

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        if (ClientRules.isFamilyDetailsRequired()) {
            SessionUtils.setCollectionAttribute(ClientConstants.LIVING_STATUS_ENTITY, getCustomerBusinessService()
                    .retrieveMasterEntities(MasterConstants.LIVING_STATUS, getUserContext(request).getLocaleId()),
                    request);
            SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, getCustomerBusinessService()
                    .retrieveMasterEntities(MasterConstants.GENDER, getUserContext(request).getLocaleId()), request);
            ClientCustActionForm actionForm = (ClientCustActionForm) form;
            ArrayList<FamilyDetailDTO> familyDetailBean = new ArrayList<FamilyDetailDTO>();
            familyDetailBean.add(new FamilyDetailDTO());
            actionForm.setFamilyDetailBean(familyDetailBean);
            return mapping.findForward(ActionForwards.next_success_family.toString());
        }

        return mapping.findForward(ActionForwards.next_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward familyInfoNext(ActionMapping mapping, ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setFamilyDateOfBirth();
        actionForm.constructFamilyDetails();
        return mapping.findForward(ActionForwards.next_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward addFamilyRow(ActionMapping mapping, ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        if (actionForm.getFamilyNames().size() < ClientRules.getMaximumNumberOfFamilyMembers()) {
            actionForm.addFamilyMember();
        }
        return mapping.findForward(ActionForwards.addFamilyRow_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward deleteFamilyRow(ActionMapping mapping, ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        if (Integer.parseInt(actionForm.getDeleteThisRow()) < actionForm.getFamilyFirstName().size()) {
            actionForm.removeFamilyMember(Integer.parseInt(actionForm.getDeleteThisRow()));
        }
        return mapping.findForward(ActionForwards.addFamilyRow_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editAddFamilyRow(ActionMapping mapping, ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        if (actionForm.getFamilyNames().size() < ClientRules.getMaximumNumberOfFamilyMembers()) {
            actionForm.addFamilyMember();
        }
        return mapping.findForward(ActionForwards.editAddFamilyRow_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editDeleteFamilyRow(ActionMapping mapping, ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        if (Integer.parseInt(actionForm.getDeleteThisRow()) < actionForm.getFamilyFirstName().size()) {
            actionForm.removeFamilyMember(Integer.parseInt(actionForm.getDeleteThisRow()));
        }
        return mapping.findForward(ActionForwards.editAddFamilyRow_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

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

        } else {
            SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, CustomerConstants.NO, request);
        }

        actionForm.setEditFamily("edit");
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth())));
        checkForGovtIdAndDisplayNameDobDuplicacy(request, actionForm);
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    private void checkForGovtIdAndDisplayNameDobDuplicacy(HttpServletRequest request, ClientCustActionForm actionForm)
            throws PersistenceException, PageExpiredException, InvalidDateException {

        ClientPersistence clientPersistence = new ClientPersistence();
        String governmentId = actionForm.getGovernmentId();

        /*
         * If govt id is not null or empty, check if client with same govt id is present in closed state and display
         * warning otherwise if govt id is null or empty, and display name + dob combination is present in closed state
         * display warning
         */
        if (StringUtils.isNotBlank(governmentId)
                && clientPersistence.checkForDuplicacyOnGovtIdForClosedClients(governmentId)) {
            SessionUtils.addWarningMessage(request, CustomerConstants.CLIENT_WITH_SAME_GOVT_ID_EXIST_IN_CLOSED);
        } else if (StringUtils.isBlank(governmentId)
                && clientPersistence.checkForDuplicacyForClosedClientsOnNameAndDob(actionForm.getClientName()
                        .getDisplayName(), DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth()))) {
            SessionUtils.addWarningMessage(request, CustomerConstants.CLIENT_WITH_SAME_GOVT_ID_EXIST_IN_CLOSED);
        }
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        String forward = null;
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        String fromPage = actionForm.getInput();
        if (ClientConstants.INPUT_PERSONAL_INFO.equals(fromPage) || ClientConstants.INPUT_MFI_INFO.equals(fromPage)
                || CenterConstants.INPUT_CREATE.equals(fromPage)) {
            forward = ActionForwards.cancelCreate_success.toString();
        } else if (ClientConstants.INPUT_EDIT_PERSONAL_INFO.equals(fromPage)
                || ClientConstants.INPUT_EDIT_MFI_INFO.equals(fromPage)
                || ClientConstants.INPUT_EDIT_FAMILY_INFO.equals(fromPage)) {
            forward = ActionForwards.cancelEdit_success.toString();
        }
        return mapping.findForward(forward);
    }

    public ActionForward editPreviewEditFamilyInfo(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.editPreviewEditFamilyInfo_success.toString());
    }

    private void loadMasterDataEntities(@SuppressWarnings("unused") ClientCustActionForm actionForm,
            HttpServletRequest request) throws ApplicationException, SystemException {

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
    public ActionForward retrievePictureOnPreview(ActionMapping mapping, ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request, HttpServletResponse response) throws Exception {

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
    public ActionForward retrievePicture(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

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
    public ActionForward previewPersonalInfo(ActionMapping mapping, ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth())));
        return mapping.findForward(ActionForwards.previewPersonalInfo_success.toString());

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevFamilyInfo(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        return mapping.findForward(ActionForwards.prevFamilyInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevFamilyInfoNext(ActionMapping mapping, ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setFamilyDateOfBirth();
        actionForm.constructFamilyDetails();
        return mapping.findForward(ActionForwards.prevFamilyInfoNext_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevPersonalInfo(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        return mapping.findForward(ActionForwards.prevPersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevMFIInfo(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        return mapping.findForward(ActionForwards.prevMFIInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevMeeting(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        return mapping.findForward(ActionForwards.next_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadMeeting(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.loadMeeting_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientBO client = null;
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
        List<CustomFieldView> customFields = actionForm.getCustomFields();
        UserContext userContext = getUserContext(request);
        convertCustomFieldDateToUniformPattern(customFields, userContext.getPreferredLocale());
        Short personnelId = null;
        Short officeId = null;

        if (actionForm.getGroupFlagValue().equals(YesNoFlag.YES.getValue())) {
            if (actionForm.getParentGroup().getPersonnel() != null) {
                personnelId = actionForm.getParentGroup().getPersonnel().getPersonnelId();
            }
            officeId = actionForm.getParentGroup().getOffice().getOfficeId();
        } else {
            personnelId = actionForm.getLoanOfficerIdValue();
            officeId = actionForm.getOfficeIdValue();
        }
        if (personnelId != null) {
            checkPermissionForCreate(actionForm.getStatusValue().getValue(), getUserContext(request), null, officeId,
                    personnelId);
        } else {
            checkPermissionForCreate(actionForm.getStatusValue().getValue(), getUserContext(request), null, officeId,
                    getUserContext(request).getId());
        }
        List<SavingsOfferingBO> selectedOfferings1 = new ArrayList<SavingsOfferingBO>(
                ClientConstants.MAX_OFFERINGS_SIZE);
        List<SavingsOfferingBO> offeringsList = (List<SavingsOfferingBO>) SessionUtils.getAttribute(
                ClientConstants.SAVINGS_OFFERING_LIST, request);
        for (Short offeringId : actionForm.getSelectedOfferings()) {
            if (offeringId != null) {
                for (SavingsOfferingBO savingsOffering : offeringsList) {
                    if (offeringId.equals(savingsOffering.getPrdOfferingId())) {
                        selectedOfferings1.add(savingsOffering);
                    }
                }
            }
        }

        List<SavingsOfferingBO> selectedOfferings = selectedOfferings1;
        if (actionForm.getGroupFlagValue().equals(YesNoFlag.NO.getValue())) {
            if (ClientRules.isFamilyDetailsRequired()) {
                actionForm.setFamilyDateOfBirth();
                actionForm.constructFamilyDetails();
                client = new ClientBO(userContext, actionForm.getClientName().getDisplayName(), actionForm
                        .getStatusValue(), actionForm.getExternalId(), DateUtils.getDateAsSentFromBrowser(actionForm
                        .getMfiJoiningDate()), actionForm.getAddress(), customFields, actionForm.getFeesToApply(),
                        selectedOfferings, new PersonnelPersistence().getPersonnel(actionForm
                                .getFormedByPersonnelValue()), new OfficePersistence().getOffice(actionForm
                                .getOfficeIdValue()), meeting, new PersonnelPersistence().getPersonnel(actionForm
                                .getLoanOfficerIdValue()), DateUtils.getDateAsSentFromBrowser(actionForm
                                .getDateOfBirth()), actionForm.getGovernmentId(), actionForm.getTrainedValue(),
                        DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()),
                        actionForm.getGroupFlagValue(), actionForm.getClientName(), null, actionForm
                                .getClientDetailView(), actionForm.getCustomerPicture());
                client.setFamilyAndNameDetailSets(actionForm.getFamilyNames(), actionForm.getFamilyDetails());

            } else {
                client = new ClientBO(userContext, actionForm.getClientName().getDisplayName(), actionForm
                        .getStatusValue(), actionForm.getExternalId(), DateUtils.getDateAsSentFromBrowser(actionForm
                        .getMfiJoiningDate()), actionForm.getAddress(), customFields, actionForm.getFeesToApply(),
                        selectedOfferings, new PersonnelPersistence().getPersonnel(actionForm
                                .getFormedByPersonnelValue()), new OfficePersistence().getOffice(actionForm
                                .getOfficeIdValue()), meeting, new PersonnelPersistence().getPersonnel(actionForm
                                .getLoanOfficerIdValue()), DateUtils.getDateAsSentFromBrowser(actionForm
                                .getDateOfBirth()), actionForm.getGovernmentId(), actionForm.getTrainedValue(),
                        DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()),
                        actionForm.getGroupFlagValue(), actionForm.getClientName(), actionForm.getSpouseName(),
                        actionForm.getClientDetailView(), actionForm.getCustomerPicture());
            }

        } else {
            CustomerBO parentCustomer = getCustomerBusinessService().getCustomer(
                    actionForm.getParentGroup().getCustomerId());
            parentCustomer.setVersionNo(actionForm.getParentGroup().getVersionNo());

            if (ClientRules.isFamilyDetailsRequired()) {
                actionForm.setFamilyDateOfBirth();
                actionForm.constructFamilyDetails();
                client = new ClientBO(userContext, actionForm.getClientName().getDisplayName(), actionForm
                        .getStatusValue(), actionForm.getExternalId(), DateUtils.getDateAsSentFromBrowser(actionForm
                        .getMfiJoiningDate()), actionForm.getAddress(), customFields, actionForm.getFeesToApply(),
                        selectedOfferings, new PersonnelPersistence().getPersonnel(actionForm
                                .getFormedByPersonnelValue()), parentCustomer.getOffice(), parentCustomer, DateUtils
                                .getDateAsSentFromBrowser(actionForm.getDateOfBirth()), actionForm.getGovernmentId(),
                        actionForm.getTrainedValue(), DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()),
                        actionForm.getGroupFlagValue(), actionForm.getClientName(), null, actionForm
                                .getClientDetailView(), actionForm.getCustomerPicture());
                client.setFamilyAndNameDetailSets(actionForm.getFamilyNames(), actionForm.getFamilyDetails());
            } else {
                client = new ClientBO(userContext, actionForm.getClientName().getDisplayName(), actionForm
                        .getStatusValue(), actionForm.getExternalId(), DateUtils.getDateAsSentFromBrowser(actionForm
                        .getMfiJoiningDate()), actionForm.getAddress(), customFields, actionForm.getFeesToApply(),
                        selectedOfferings, new PersonnelPersistence().getPersonnel(actionForm
                                .getFormedByPersonnelValue()), parentCustomer.getOffice(), parentCustomer, DateUtils
                                .getDateAsSentFromBrowser(actionForm.getDateOfBirth()), actionForm.getGovernmentId(),
                        actionForm.getTrainedValue(), DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()),
                        actionForm.getGroupFlagValue(), actionForm.getClientName(), actionForm.getSpouseName(),
                        actionForm.getClientDetailView(), actionForm.getCustomerPicture());
            }

        }
        new CustomerPersistence().saveCustomer(client);
        actionForm.setCustomerId(client.getCustomerId().toString());
        actionForm.setGlobalCustNum(client.getGlobalCustNum());
        client = null;
        actionForm.setEditFamily("notEdit");
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    private void clearActionForm(ClientCustActionForm actionForm) {
        actionForm.setDefaultFees(new ArrayList<FeeView>());
        actionForm.setAdditionalFees(new ArrayList<FeeView>());
        actionForm.setCustomFields(new ArrayList<CustomFieldView>());
        actionForm.setFamilyNames(new ArrayList<ClientNameDetailView>());
        actionForm.setFamilyDetails(new ArrayList<ClientFamilyDetailView>());
        actionForm.setFamilyRelationship(new ArrayList<Short>());
        actionForm.setFamilyFirstName(new ArrayList<String>());
        actionForm.setFamilyMiddleName(new ArrayList<String>());
        actionForm.setFamilyLastName(new ArrayList<String>());
        actionForm.setFamilyDateOfBirthDD(new ArrayList<String>());
        actionForm.setFamilyDateOfBirthMM(new ArrayList<String>());
        actionForm.setFamilyDateOfBirthYY(new ArrayList<String>());
        actionForm.setFamilyGender(new ArrayList<Short>());
        actionForm.setFamilyLivingStatus(new ArrayList<Short>());
        actionForm.initializeFamilyMember();
        actionForm.addFamilyMember();
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
        for (int i = 0; i < actionForm.getSelectedOfferings().size(); i++) {
            actionForm.getSelectedOfferings().set(i, null);
        }
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        long startTime = System.currentTimeMillis();

        // John W - UserContext object passed because some status' need to be looked up for internationalisation based
        // on UserContext info
        ClientInformationDto clientInformationDto = clientDetailsServiceFacade.getClientInformationDto(((ClientCustActionForm) form)
                .getGlobalCustNum(), getUserContext(request));
        SessionUtils.removeThenSetAttribute("clientInformationDto", clientInformationDto, request);

        // John W - for breadcrumb or another other action downstream that exists business_key set (until refactored)
        ClientBO clientBO = (ClientBO) getCustomerBusinessService().getCustomer(
                clientInformationDto.getClientDisplay().getCustomerId());
        SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, clientBO, request);

        System.out.println("get Client Transaction Took: " + (System.currentTimeMillis() - startTime));
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward showPicture(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        String forward = ClientConstants.CUSTOMER_PICTURE_PAGE;
        return mapping.findForward(forward);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        clearActionForm(actionForm);
        ClientBO client = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        ClientBO clientBO = (ClientBO) getCustomerBusinessService().findBySystemId(client.getGlobalCustNum(),
                CustomerLevel.CLIENT.getValue());

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, clientBO, request);
        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_REQUIRED, ClientRules.isFamilyDetailsRequired(),
                request);
        client = null;

        loadMasterDataEntities(actionForm, request);
        loadCustomFieldDefinitions(EntityType.CLIENT, request);
        setValuesInActionForm(actionForm, request);
        return mapping.findForward(ActionForwards.editPersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewEditPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth())));
        checkForGovtIdAndDisplayNameDobDuplicacy(request, actionForm);
        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_REQUIRED, ClientRules.isFamilyDetailsRequired(),
                request);
        return mapping.findForward(ActionForwards.previewEditPersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevEditPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_REQUIRED, ClientRules.isFamilyDetailsRequired(),
                request);
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth())));
        return mapping.findForward(ActionForwards.prevEditPersonalInfo_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updatePersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws ApplicationException, InvalidDateException {

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
        for (CustomFieldView fieldView : actionForm.getCustomFields()) {
            for (CustomerCustomFieldEntity fieldEntity : client.getCustomFields()) {
                if (fieldView.getFieldId().equals(fieldEntity.getFieldId())) {
                    fieldEntity.setFieldValue(fieldView.getFieldValue());
                }
            }
        }

        client.getClientName().updateNameDetails(actionForm.getClientName());
        if (!ClientRules.isFamilyDetailsRequired()) {
            client.getSpouseName().updateNameDetails(actionForm.getSpouseName());
        }

        client.setFirstName(actionForm.getClientName().getFirstName());
        client.setLastName(actionForm.getClientName().getLastName());
        client.setSecondLastName(actionForm.getClientName().getSecondLastName());
        if (actionForm.getPicture() != null && StringUtils.isNotBlank(actionForm.getPicture().getFileName())) {
            client.updatePicture(actionForm.getCustomerPicture());
        }
        client.setUserContext(getUserContext(request));
        client.updateClientDetails(actionForm.getClientDetailView());
        client.updatePersonalInfo(actionForm.getClientName().getDisplayName(), actionForm.getGovernmentId(), DateUtils
                .getDateAsSentFromBrowser(actionForm.getDateOfBirth()));

        return mapping.findForward(ActionForwards.updatePersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editFamilyInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        clearActionForm(actionForm);
        ClientBO client = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        ClientBO clientBO = (ClientBO) getCustomerBusinessService().findBySystemId(client.getGlobalCustNum(),
                CustomerLevel.CLIENT.getValue());
        client = null;

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, clientBO, request);
        SessionUtils.setCollectionAttribute(ClientConstants.LIVING_STATUS_ENTITY, getCustomerBusinessService()
                .retrieveMasterEntities(MasterConstants.LIVING_STATUS, getUserContext(request).getLocaleId()), request);
        SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, getCustomerBusinessService()
                .retrieveMasterEntities(MasterConstants.GENDER, getUserContext(request).getLocaleId()), request);
        SessionUtils.setCollectionAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, getMasterEntities(
                SpouseFatherLookupEntity.class, getUserContext(request).getLocaleId()), request);
        setValuesInActionForm(actionForm, request);
        setValuesForFamilyEditInActionForm(actionForm, request);
        return mapping.findForward(ActionForwards.editFamilyInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewEditFamilyInfo(ActionMapping mapping, ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setFamilyDateOfBirth();
        actionForm.constructFamilyDetails();
        return mapping.findForward(ActionForwards.previewEditFamilyInfo_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updateFamilyInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ClientBO clientInSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        ClientBO client = getClientBusinessService().getClient(clientInSession.getCustomerId());
        checkVersionMismatch(clientInSession.getVersionNo(), client.getVersionNo());
        client.setVersionNo(clientInSession.getVersionNo());
        clientInSession = null;
        client.setUserContext(getUserContext(request));
        setInitialObjectForAuditLogging(client);
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setFamilyDateOfBirth();
        actionForm.constructFamilyDetails();
        // client.updateFamilyInfo(actionForm.getRelativePrimaryKey(),actionForm.getFamilyNames(),actionForm.getFamilyDetails());
        client.updateFamilyInfo(actionForm.getFamilyPrimaryKey(), actionForm.getFamilyNames(), actionForm
                .getFamilyDetails());
        client.setUserContext(getUserContext(request));

        return mapping.findForward(ActionForwards.updateFamilyInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editMfiInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, ClientRules.getCenterHierarchyExists(),
                request);

        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        clearActionForm(actionForm);
        ClientBO client = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        ClientBO clientBO = (ClientBO) getCustomerBusinessService().findBySystemId(client.getGlobalCustNum(),
                CustomerLevel.CLIENT.getValue());
        client = null;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, clientBO, request);
        if (!clientBO.isClientUnderGroup()) {
            loadLoanOfficers(clientBO.getOffice().getOfficeId(), request);
        }

        ClientBO client1 = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        if (client1.getPersonnel() != null) {
            actionForm.setLoanOfficerId(client1.getPersonnel().getPersonnelId().toString());
            client1.getPersonnel().getDisplayName();
        }
        actionForm.setCustomerId(client1.getCustomerId().toString());
        actionForm.setGlobalCustNum(client1.getGlobalCustNum());
        actionForm.setExternalId(client1.getExternalId());
        if (client1.isClientUnderGroup()) {
            actionForm.setGroupFlag(ClientConstants.YES);
            actionForm.setParentGroup(client1.getParentCustomer());
        } else {
            actionForm.setGroupFlag(ClientConstants.NO);
        }

        if (client1.isTrained()) {
            actionForm.setTrained(ClientConstants.YES);
        } else {
            actionForm.setTrained(ClientConstants.NO);
        }

        if (client1.getTrainedDate() != null) {
            actionForm.setTrainedDate(DateUtils.makeDateAsSentFromBrowser(client1.getTrainedDate()));
        }

        return mapping.findForward(ActionForwards.editMfiInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewEditMfiInfo(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        return mapping.findForward(ActionForwards.previewEditMfiInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevEditMfiInfo(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        return mapping.findForward(ActionForwards.prevEditMfiInfo_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updateMfiInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ClientBO clientInSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        ClientBO client = getClientBusinessService().getClient(clientInSession.getCustomerId());
        checkVersionMismatch(clientInSession.getVersionNo(), client.getVersionNo());
        client.setVersionNo(clientInSession.getVersionNo());
        clientInSession = null;
        client.setUserContext(getUserContext(request));
        setInitialObjectForAuditLogging(client);
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        client.setExternalId(actionForm.getExternalId());

        if (actionForm.getTrainedValue() != null && actionForm.getTrainedValue().equals(YesNoFlag.YES.getValue())) {
            client.setTrained(true);
        } else {
            client.setTrained(false);
        }

        client.setTrainedDate(DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()));
        PersonnelBO personnel = null;

        if (actionForm.getGroupFlagValue().equals(YesNoFlag.NO.getValue())) {
            if (actionForm.getLoanOfficerIdValue() != null) {
                personnel = new PersonnelBusinessService().getPersonnel(actionForm.getLoanOfficerIdValue());
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

        UserContext userContext = getUserContext(request);
        List<CustomFieldDefinitionEntity> fieldDefinitions = customerDao.retrieveCustomFieldEntitiesForClient();
        List<CustomFieldView> customFieldViews = CustomerCustomFieldEntity.toDto(client.getCustomFields(),
                fieldDefinitions, userContext);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, customFieldViews, request);

        actionForm.setCustomFields(customFieldViews);

        for (ClientNameDetailView nameView : createNameViews(client.getNameDetailSet())) {
            if (nameView.getNameType().equals(ClientConstants.CLIENT_NAME_TYPE)) {
                actionForm.setClientName(nameView);
            } else if (!ClientRules.isFamilyDetailsRequired()) {
                actionForm.setSpouseName(nameView);
            }
        }
        ClientDetailEntity customerDetail = client.getCustomerDetail();
        actionForm.setClientDetailView(new ClientDetailView(customerDetail.getEthinicity(), customerDetail
                .getCitizenship(), customerDetail.getHandicapped(), customerDetail.getBusinessActivities(),
                customerDetail.getMaritalStatus(), customerDetail.getEducationLevel(), customerDetail.getNumChildren(),
                customerDetail.getGender(), customerDetail.getPovertyStatus()));
        actionForm.setGovernmentId(client.getGovernmentId());
        actionForm.setDateOfBirth(DateUtils.makeDateAsSentFromBrowser(client.getDateOfBirth()));
    }

    /*
     * This is for populating each list for family in the action form , Equivalent to writing data into each column of
     * the jsp
     */
    private void setValuesForFamilyEditInActionForm(ClientCustActionForm actionForm, HttpServletRequest request)
            throws Exception {

        ClientBO client = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        actionForm.initializeFamilyMember();

        int familyMemberCount = 0;
        for (ClientNameDetailEntity clientNameDetailEntity : client.getNameDetailSet()) {
            if (clientNameDetailEntity.getNameType().shortValue() != ClientConstants.CLIENT_NAME_TYPE) {
                ClientNameDetailView nameView = createNameViewObject(clientNameDetailEntity);
                actionForm.addFamilyMember();
                actionForm.setFamilyPrimaryKey(familyMemberCount, clientNameDetailEntity.getCustomerNameId());
                actionForm.setFamilyFirstName(familyMemberCount, nameView.getFirstName());
                actionForm.setFamilyMiddleName(familyMemberCount, nameView.getMiddleName());
                actionForm.setFamilyLastName(familyMemberCount, nameView.getLastName());
                actionForm.setFamilyRelationship(familyMemberCount, nameView.getNameType());
                for (ClientFamilyDetailEntity clientFamilyDetailEntity : client.getFamilyDetailSet()) {
                    if (clientNameDetailEntity.getCustomerNameId() == clientFamilyDetailEntity.getClientName()
                            .getCustomerNameId()) {
                        Calendar cal = Calendar.getInstance();
                        // actionForm.setFamilyRelationship(i,clientFamilyDetailEntity.getRelationship());
                        if (clientFamilyDetailEntity.getDateOfBirth() != null) {
                            String date1 = DateUtils.makeDateAsSentFromBrowser(clientFamilyDetailEntity
                                    .getDateOfBirth());
                            java.util.Date date = DateUtils.getDate(date1);
                            cal.setTime(date);
                            actionForm.setFamilyDateOfBirthDD(familyMemberCount, String.valueOf(cal
                                    .get(Calendar.DAY_OF_MONTH)));
                            actionForm.setFamilyDateOfBirthMM(familyMemberCount, String
                                    .valueOf(cal.get(Calendar.MONTH) + 1));
                            actionForm
                                    .setFamilyDateOfBirthYY(familyMemberCount, String.valueOf(cal.get(Calendar.YEAR)));
                        }

                        actionForm.setFamilyGender(familyMemberCount, clientFamilyDetailEntity.getGender());
                        actionForm.setFamilyLivingStatus(familyMemberCount, clientFamilyDetailEntity.getLivingStatus());
                    }
                }
                familyMemberCount++;
            }
        }
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

    private int calculateAge(Date date) {
        int age = DateUtils.DateDiffInYears(date);
        return age;
    }
}