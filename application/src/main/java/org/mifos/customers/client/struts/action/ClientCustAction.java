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
import org.joda.time.DateTime;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.SpouseFatherLookupEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ClientDetailDto;
import org.mifos.application.servicefacade.ClientFamilyDetailsDto;
import org.mifos.application.servicefacade.ClientFormCreationDto;
import org.mifos.application.servicefacade.ClientPersonalInfoDto;
import org.mifos.application.servicefacade.ClientRulesDto;
import org.mifos.application.servicefacade.CustomerServiceFacade;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.servicefacade.OnlyBranchOfficeHierarchyDto;
import org.mifos.application.servicefacade.ProcessRulesDto;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
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
import org.mifos.customers.client.business.service.ClientBusinessService;
import org.mifos.customers.client.business.service.ClientDetailsServiceFacade;
import org.mifos.customers.client.business.service.ClientInformationDto;
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
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
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

        // note doesn't complete reset all form values - some are retained
        clearActionForm(actionForm);
        SessionUtils.removeAttribute(CustomerConstants.CUSTOMER_MEETING, request);

        Short officeId = actionForm.getOfficeIdValue();
        Short groupFlag = actionForm.getGroupFlagValue();
        String parentGroupId = actionForm.getParentGroupId();

        UserContext userContext = getUserContext(request);
        ClientFormCreationDto clientFormCreationDto = this.customerServiceFacade.retrieveClientFormCreationData(userContext, groupFlag, officeId, parentGroupId);

        if (clientFormCreationDto.getFormedByPersonnelId() != null) {
            actionForm.setFormedByPersonnel(clientFormCreationDto.getFormedByPersonnelId().toString());
        }
        actionForm.setCenterDisplayName(clientFormCreationDto.getCenterDisplayName());
        actionForm.setOfficeId(clientFormCreationDto.getOfficeId().toString());
        actionForm.setCustomFields(clientFormCreationDto.getCustomFieldViews());
        actionForm.setDefaultFees(clientFormCreationDto.getApplicableFees().getDefaultFees());

        SessionUtils.setCollectionAttribute(ClientConstants.SALUTATION_ENTITY, clientFormCreationDto.getClientDropdowns().getSalutations(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, clientFormCreationDto.getClientDropdowns().getGenders(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.MARITAL_STATUS_ENTITY, clientFormCreationDto.getClientDropdowns().getMaritalStatuses(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.CITIZENSHIP_ENTITY, clientFormCreationDto.getClientDropdowns().getCitizenship(),request);
        SessionUtils.setCollectionAttribute(ClientConstants.ETHINICITY_ENTITY, clientFormCreationDto.getClientDropdowns().getEthinicity(),request);
        SessionUtils.setCollectionAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, clientFormCreationDto.getClientDropdowns().getEducationLevels(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, clientFormCreationDto.getClientDropdowns().getBusinessActivity(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.POVERTY_STATUS, clientFormCreationDto.getClientDropdowns().getPoverty(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.HANDICAPPED_ENTITY, clientFormCreationDto.getClientDropdowns().getHandicapped(),request);
        SessionUtils.setCollectionAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, clientFormCreationDto.getClientDropdowns().getSpouseFather(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, clientFormCreationDto.getCustomFieldViews(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, clientFormCreationDto.getPersonnelList(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, clientFormCreationDto.getApplicableFees().getAdditionalFees(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST, clientFormCreationDto.getFormedByPersonnelList(), request);

        List<SavingsDetailDto> savingsOfferings = this.customerDao.retrieveSavingOfferingsApplicableToClient();
        SessionUtils.setCollectionAttribute(ClientConstants.SAVINGS_OFFERING_LIST, savingsOfferings, request);
        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, ClientRules.getCenterHierarchyExists(), request);
        SessionUtils.setAttribute(ClientConstants.MAXIMUM_NUMBER_OF_FAMILY_MEMBERS, ClientRules.getMaximumNumberOfFamilyMembers(), request);
        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_REQUIRED, ClientRules.isFamilyDetailsRequired(),request);

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ClientCustActionForm actionForm = (ClientCustActionForm) form;

        ClientFamilyDetailsDto clientFamilyDetails = this.customerServiceFacade.retrieveClientFamilyDetails();

        if (clientFamilyDetails.isFamilyDetailsRequired()) {

            SessionUtils.setCollectionAttribute(ClientConstants.LIVING_STATUS_ENTITY, clientFamilyDetails.getLivingStatus() , request);
            SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, clientFamilyDetails.getGenders(), request);

            actionForm.setFamilyDetailBean(clientFamilyDetails.getFamilyDetails());

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

        String governmentId = actionForm.getGovernmentId();
        String clientName = actionForm.getClientName().getDisplayName();
        DateTime dateOfBirth = new DateTime(DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth()));

        ProcessRulesDto processRules = this.customerServiceFacade.previewClient(governmentId, dateOfBirth, clientName);

        if (processRules.isClientPendingApprovalStateEnabled()) {
            SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, CustomerConstants.YES, request);
        } else {
            SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, CustomerConstants.NO, request);
        }

        actionForm.setEditFamily("edit");
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth())));

        if (processRules.isGovernmentIdValidationFailing()) {
            SessionUtils.addWarningMessage(request, CustomerConstants.CLIENT_WITH_SAME_GOVT_ID_EXIST_IN_CLOSED);
        }

        return mapping.findForward(ActionForwards.preview_success.toString());
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

        // TODO - refactor create
        ClientBO client = null;
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        UserContext userContext = getUserContext(request);

        MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
        List<CustomFieldView> customFields = actionForm.getCustomFields();
        CustomFieldView.convertCustomFieldDateToUniformPattern(customFields, userContext.getPreferredLocale());

        Short personnelId = null;
        Short officeId = null;

        if (actionForm.getGroupFlagValue().equals(YesNoFlag.YES.getValue())) {

            Integer parentGroupId = Integer.parseInt(actionForm.getParentGroupId());
            CustomerBO group = this.customerDao.findCustomerById(parentGroupId);

            if (group.getPersonnel() != null) {
                personnelId = group.getPersonnel().getPersonnelId();
            }

            if (group.getParentCustomer() != null) {
                actionForm.setGroupDisplayName(group.getDisplayName());
                if (group.getParentCustomer() != null) {
                    actionForm.setCenterDisplayName(group.getParentCustomer().getDisplayName());
                }
            }

            officeId = group.getOffice().getOfficeId();
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

        List<SavingsDetailDto> selectedOfferings1 = new ArrayList<SavingsDetailDto>(ClientConstants.MAX_OFFERINGS_SIZE);

        List<SavingsDetailDto> offeringsList = getSavingsOfferingsFromSession(request);
        for (Short offeringId : actionForm.getSelectedOfferings()) {
            if (offeringId != null) {
                for (SavingsDetailDto savingsOffering : offeringsList) {
                    if (offeringId.equals(savingsOffering.getPrdOfferingId())) {
                        selectedOfferings1.add(savingsOffering);
                    }
                }
            }
        }

        // FIXME - keithw - translate from savingsDetailsDto to savingsOfferings
//        List<SavingsOfferingBO> selectedOfferings = selectedOfferings1;
        List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();

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
            Integer parentGroupId = Integer.parseInt(actionForm.getParentGroupId());
            CustomerBO group = this.customerDao.findCustomerById(parentGroupId);

            if (group.getParentCustomer() != null) {
                actionForm.setGroupDisplayName(group.getDisplayName());
                if (group.getParentCustomer() != null) {
                    actionForm.setCenterDisplayName(group.getParentCustomer().getDisplayName());
                }
            }

            if (ClientRules.isFamilyDetailsRequired()) {
                actionForm.setFamilyDateOfBirth();
                actionForm.constructFamilyDetails();

                client = new ClientBO(userContext, actionForm.getClientName().getDisplayName(), actionForm
                        .getStatusValue(), actionForm.getExternalId(), DateUtils.getDateAsSentFromBrowser(actionForm
                        .getMfiJoiningDate()), actionForm.getAddress(), customFields, actionForm.getFeesToApply(),
                        selectedOfferings, new PersonnelPersistence().getPersonnel(actionForm
                                .getFormedByPersonnelValue()), group.getOffice(), group, DateUtils
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
                                .getFormedByPersonnelValue()), group.getOffice(), group, DateUtils
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

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        // John W - UserContext object passed because some status' need to be looked up for internationalisation based
        // on UserContext info
        ClientInformationDto clientInformationDto = clientDetailsServiceFacade.getClientInformationDto(
                ((ClientCustActionForm) form).getGlobalCustNum(), getUserContext(request));
        SessionUtils.removeThenSetAttribute("clientInformationDto", clientInformationDto, request);

        // John W - for breadcrumb or another other action downstream that exists business_key set (until refactored)
        ClientBO clientBO = (ClientBO) this.customerDao.findCustomerById(clientInformationDto.getClientDisplay().getCustomerId());
        SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, clientBO, request);

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
        UserContext userContext = getUserContext(request);
        ClientBO clientFromSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        final String clientSystemId = clientFromSession.getGlobalCustNum();

        ClientPersonalInfoDto personalInfo = this.customerServiceFacade.retrieveClientPersonalInfoForUpdate(clientSystemId, userContext);

        SessionUtils.setCollectionAttribute(ClientConstants.SALUTATION_ENTITY, personalInfo.getClientDropdowns().getSalutations(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, personalInfo.getClientDropdowns().getGenders(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.MARITAL_STATUS_ENTITY, personalInfo.getClientDropdowns().getMaritalStatuses(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.CITIZENSHIP_ENTITY, personalInfo.getClientDropdowns().getCitizenship(),request);
        SessionUtils.setCollectionAttribute(ClientConstants.ETHINICITY_ENTITY, personalInfo.getClientDropdowns().getEthinicity(),request);
        SessionUtils.setCollectionAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, personalInfo.getClientDropdowns().getEducationLevels(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, personalInfo.getClientDropdowns().getBusinessActivity(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.POVERTY_STATUS, personalInfo.getClientDropdowns().getPoverty(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.HANDICAPPED_ENTITY, personalInfo.getClientDropdowns().getHandicapped(),request);
        SessionUtils.setCollectionAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, personalInfo.getClientDropdowns().getSpouseFather(), request);

        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_REQUIRED, personalInfo.getClientRules().isFamilyDetailsRequired(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, personalInfo.getCustomFieldViews(), request);

        // customer specific
        actionForm.setCustomerId(personalInfo.getCustomerDetail().getCustomerId().toString());
        actionForm.setLoanOfficerId(personalInfo.getCustomerDetail().getLoanOfficerIdAsString());
        actionForm.setGlobalCustNum(personalInfo.getCustomerDetail().getGlobalCustNum());
        actionForm.setExternalId(personalInfo.getCustomerDetail().getExternalId());
        actionForm.setAddress(personalInfo.getCustomerDetail().getAddress());

        // client specific
        actionForm.setGovernmentId(personalInfo.getClientDetail().getGovernmentId());
        actionForm.setDateOfBirth(personalInfo.getClientDetail().getDateOfBirth());
        actionForm.setClientDetailView(personalInfo.getClientDetail().getCustomerDetail());
        actionForm.setClientName(personalInfo.getClientDetail().getClientName());
        actionForm.setSpouseName(personalInfo.getClientDetail().getSpouseName());
        actionForm.setCustomFields(personalInfo.getCustomFieldViews());

        ClientBO client = this.customerDao.findClientBySystemId(clientSystemId);
        SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, client, request);

        return mapping.findForward(ActionForwards.editPersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewEditPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;

        String governmentId = actionForm.getGovernmentId();
        String dateOfBirth = actionForm.getDateOfBirth();
        String clientName = actionForm.getClientName().getDisplayName();
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(dateOfBirth)));

        ClientDetailDto clientDetailDto = new ClientDetailDto(governmentId, dateOfBirth, clientName);
        ClientRulesDto clientRules = this.customerServiceFacade.retrieveClientDetailsForPreviewingEditOfPersonalInfo(clientDetailDto);

        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_REQUIRED, clientRules.isFamilyDetailsRequired(), request);
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
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        ClientBO clientInSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        UserContext userContext = getUserContext(request);

        ClientBO client = (ClientBO) this.customerDao.findCustomerById(clientInSession.getCustomerId());

        checkVersionMismatch(clientInSession.getVersionNo(), client.getVersionNo());
        client.setVersionNo(clientInSession.getVersionNo());
        clientInSession = null;

        client.setUserContext(userContext);
        setInitialObjectForAuditLogging(client);

        client.updateAddress(actionForm.getAddress());
        convertCustomFieldDateToUniformPattern(actionForm.getCustomFields(), userContext.getPreferredLocale());
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
        ClientBO clientFromSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        ClientBO clientBO = this.customerDao.findClientBySystemId(clientFromSession.getGlobalCustNum());
        clientFromSession = null;

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, clientBO, request);


        SessionUtils.setCollectionAttribute(ClientConstants.LIVING_STATUS_ENTITY, getCustomerBusinessService()
                .retrieveMasterEntities(MasterConstants.LIVING_STATUS, getUserContext(request).getLocaleId()), request);
        SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, getCustomerBusinessService()
                .retrieveMasterEntities(MasterConstants.GENDER, getUserContext(request).getLocaleId()), request);
        SessionUtils.setCollectionAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, getMasterEntities(
                SpouseFatherLookupEntity.class, getUserContext(request).getLocaleId()), request);

        ClientBO client1 = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        if (client1.getPersonnel() != null) {
            actionForm.setLoanOfficerId(client1.getPersonnel().getPersonnelId().toString());
        }

        actionForm.setCustomerId(client1.getCustomerId().toString());
        actionForm.setGlobalCustNum(client1.getGlobalCustNum());
        actionForm.setExternalId(client1.getExternalId());
        actionForm.setAddress(client1.getAddress());

        UserContext userContext = getUserContext(request);
        List<CustomFieldDefinitionEntity> fieldDefinitions = customerDao.retrieveCustomFieldEntitiesForClient();
        List<CustomFieldView> customFieldViews = CustomerCustomFieldEntity.toDto(client1.getCustomFields(),
                fieldDefinitions, userContext);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, customFieldViews, request);

        actionForm.setCustomFields(customFieldViews);

        for (ClientNameDetailView nameView : createNameViews(client1.getNameDetailSet())) {
            if (nameView.getNameType().equals(ClientConstants.CLIENT_NAME_TYPE)) {
                actionForm.setClientName(nameView);
            } else if (!ClientRules.isFamilyDetailsRequired()) {
                actionForm.setSpouseName(nameView);
            }
        }
        ClientDetailEntity customerDetail = client1.getCustomerDetail();
        actionForm.setClientDetailView(new ClientDetailView(customerDetail.getEthinicity(), customerDetail
                .getCitizenship(), customerDetail.getHandicapped(), customerDetail.getBusinessActivities(),
                customerDetail.getMaritalStatus(), customerDetail.getEducationLevel(), customerDetail.getNumChildren(),
                customerDetail.getGender(), customerDetail.getPovertyStatus()));
        actionForm.setGovernmentId(client1.getGovernmentId());
        actionForm.setDateOfBirth(DateUtils.makeDateAsSentFromBrowser(client1.getDateOfBirth()));
        ClientBO client2 = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        actionForm.initializeFamilyMember();

        int familyMemberCount = 0;
        for (ClientNameDetailEntity clientNameDetailEntity : client2.getNameDetailSet()) {
            if (clientNameDetailEntity.getNameType().shortValue() != ClientConstants.CLIENT_NAME_TYPE) {
                ClientNameDetailView nameView1 = createNameViewObject(clientNameDetailEntity);
                actionForm.addFamilyMember();
                actionForm.setFamilyPrimaryKey(familyMemberCount, clientNameDetailEntity.getCustomerNameId());
                actionForm.setFamilyFirstName(familyMemberCount, nameView1.getFirstName());
                actionForm.setFamilyMiddleName(familyMemberCount, nameView1.getMiddleName());
                actionForm.setFamilyLastName(familyMemberCount, nameView1.getLastName());
                actionForm.setFamilyRelationship(familyMemberCount, nameView1.getNameType());
                for (ClientFamilyDetailEntity clientFamilyDetailEntity : client2.getFamilyDetailSet()) {
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

        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, ClientRules.getCenterHierarchyExists(), request);

        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        clearActionForm(actionForm);
        ClientBO clientFromSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        String clientSystemId = clientFromSession.getGlobalCustNum();

        ClientBO client = this.customerDao.findClientBySystemId(clientSystemId);

        if (client.getParentCustomer() != null) {
            actionForm.setGroupDisplayName(client.getParentCustomer().getDisplayName());
            if (client.getParentCustomer().getParentCustomer() != null) {
                actionForm.setCenterDisplayName(client.getParentCustomer().getParentCustomer().getDisplayName());
            }
        }

        clientFromSession = null;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
        if (!client.isClientUnderGroup()) {
            PersonnelBusinessService personnelService = new PersonnelBusinessService();

            UserContext userContext = getUserContext(request);
            List<PersonnelView> personnelList = personnelService.getActiveLoanOfficersInBranch(client.getOffice().getOfficeId(), userContext
                    .getId(), userContext.getLevelId());
            SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, personnelList, request);
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
            actionForm.setParentGroupId(client1.getParentCustomer().getCustomerId().toString());
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

    @SuppressWarnings("unchecked")
    private List<SavingsDetailDto> getSavingsOfferingsFromSession(HttpServletRequest request)
            throws PageExpiredException {
        return (List<SavingsDetailDto>) SessionUtils.getAttribute(ClientConstants.SAVINGS_OFFERING_LIST, request);
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
        actionForm.setClientName(new ClientNameDetailView());
        actionForm.setSpouseName(new ClientNameDetailView());
        actionForm.setClientDetailView(new ClientDetailView());
        actionForm.setNextOrPreview("next");
        for (int i = 0; i < actionForm.getSelectedOfferings().size(); i++) {
            actionForm.getSelectedOfferings().set(i, null);
        }
    }
}