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

package org.mifos.customers.client.struts.action;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.business.SpouseFatherLookupEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.questionnaire.struts.DefaultQuestionnaireServiceFacadeLocator;
import org.mifos.application.questionnaire.struts.QuestionnaireAction;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.questionnaire.struts.QuestionnaireServiceFacadeLocator;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.config.util.helpers.HiddenMandatoryFieldNamesConstants;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.struts.actionforms.ClientCustActionForm;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.struts.action.CustAction;
import org.mifos.customers.struts.uihelpers.CustomerUIHelperFn;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.ClientCreationDetail;
import org.mifos.dto.domain.ClientFamilyDetailsDto;
import org.mifos.dto.domain.ClientFamilyInfoUpdate;
import org.mifos.dto.domain.ClientMfiInfoUpdate;
import org.mifos.dto.domain.ClientPersonalInfoUpdate;
import org.mifos.dto.domain.ClientRulesDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.ProcessRulesDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.screen.ClientFamilyDetailDto;
import org.mifos.dto.screen.ClientFamilyInfoDto;
import org.mifos.dto.screen.ClientFormCreationDto;
import org.mifos.dto.screen.ClientInformationDto;
import org.mifos.dto.screen.ClientMfiInfoDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPersonalDetailDto;
import org.mifos.dto.screen.ClientPersonalInfoDto;
import org.mifos.dto.screen.ClientPhotoDto;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.mifos.dto.screen.UploadedFileDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.image.domain.ClientPhoto;
import org.mifos.framework.image.service.ClientPhotoService;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.core.MifosRuntimeException;

public class ClientCustAction extends CustAction implements QuestionnaireAction {

    private QuestionnaireServiceFacadeLocator questionnaireServiceFacadeLocator = new DefaultQuestionnaireServiceFacadeLocator();

    private final QuestionnaireFlowAdapter createClientQuestionnaire = new QuestionnaireFlowAdapter("Create", "Client",
            ActionForwards.next_success, "clientCustAction.do?method=cancel", questionnaireServiceFacadeLocator);

    @TransactionDemarcate(saveToken = true)
    public ActionForward chooseOffice(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                      @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setGroupFlag(ClientConstants.NO);

        OnlyBranchOfficeHierarchyDto officeHierarchy = customerServiceFacade.retrieveBranchOnlyOfficeHierarchy();

        SessionUtils.setAttribute(OnlyBranchOfficeHierarchyDto.IDENTIFIER, officeHierarchy, request);

        return mapping.findForward(ActionForwards.chooseOffice_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        UserContext userContext = getUserContext(request);

        actionForm.clearMostButNotAllFieldsOnActionForm();
        SessionUtils.removeAttribute(CustomerConstants.CUSTOMER_MEETING, request);

        Short officeId = actionForm.getOfficeIdValue();
        String officeName = actionForm.getOfficeName();
        Short groupFlag = actionForm.getGroupFlagValue();
        String parentGroupId = actionForm.getParentGroupId();

        ClientFormCreationDto clientFormCreationDto = this.clientServiceFacade.retrieveClientFormCreationData(groupFlag, officeId, parentGroupId);

        if (clientFormCreationDto.getFormedByPersonnelId() != null) {
            actionForm.setFormedByPersonnel(clientFormCreationDto.getFormedByPersonnelId().toString());
            
            MeetingBO groupMeeting = customerDao.findCustomerById(Integer.valueOf(parentGroupId)).getCustomerMeetingValue();
            clientFormCreationDto.getParentCustomerMeeting().setMeetingSchedule(CustomerUIHelperFn.getMeetingSchedule(groupMeeting, userContext));
            SessionUtils.setAttribute("meeting", clientFormCreationDto.getParentCustomerMeeting(), request);
        }
        actionForm.setCenterDisplayName(clientFormCreationDto.getCenterDisplayName());
        actionForm.setGroupDisplayName(clientFormCreationDto.getGroupDisplayName());
        actionForm.setOfficeId(clientFormCreationDto.getOfficeId().toString());
        actionForm.setOfficeName(officeName);
        if (clientFormCreationDto.getFormedByPersonnelId() != null) {
            actionForm.setLoanOfficerId(clientFormCreationDto.getFormedByPersonnelId().toString());
        }
        actionForm.setLoanOfficerName(clientFormCreationDto.getFormedByPersonnelName());
        actionForm.setCustomFields(new ArrayList<CustomFieldDto>());

        List<ApplicableAccountFeeDto> defaultFees = clientFormCreationDto.getDefaultFees();
        actionForm.setDefaultFees(defaultFees);

        List<ApplicableAccountFeeDto> additionalFees = clientFormCreationDto.getAdditionalFees();
        SessionUtils.setCollectionAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, additionalFees, request);

        List<SpouseFatherLookupEntity> spouseFather = legacyMasterDao.findMasterDataEntitiesWithLocale(SpouseFatherLookupEntity.class);
        SessionUtils.setCollectionAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, spouseFather, request);

        SessionUtils.setCollectionAttribute(ClientConstants.SALUTATION_ENTITY, clientFormCreationDto.getClientDropdowns().getSalutations(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, clientFormCreationDto.getClientDropdowns().getGenders(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.MARITAL_STATUS_ENTITY, clientFormCreationDto.getClientDropdowns().getMaritalStatuses(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.CITIZENSHIP_ENTITY, clientFormCreationDto.getClientDropdowns().getCitizenship(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.ETHNICITY_ENTITY, clientFormCreationDto.getClientDropdowns().getEthnicity(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, clientFormCreationDto.getClientDropdowns().getEducationLevels(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, clientFormCreationDto.getClientDropdowns().getBusinessActivity(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.POVERTY_STATUS, clientFormCreationDto.getClientDropdowns().getPoverty(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.HANDICAPPED_ENTITY, clientFormCreationDto.getClientDropdowns().getHandicapped(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, new ArrayList<CustomFieldDto>(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, clientFormCreationDto.getPersonnelList(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST, clientFormCreationDto.getFormedByPersonnelList(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.SAVINGS_OFFERING_LIST, clientFormCreationDto.getSavingsOfferings(), request);
        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, ClientRules.getCenterHierarchyExists(), request);
        SessionUtils.setAttribute(ClientConstants.MAXIMUM_NUMBER_OF_FAMILY_MEMBERS, ClientRules.getMaximumNumberOfFamilyMembers(), request);

        boolean isFamilyDetailsRequired = ClientRules.isFamilyDetailsRequired();
        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_REQUIRED, isFamilyDetailsRequired, request);
        if (isFamilyDetailsRequired) {
            boolean isFamilyDetailsMandatory = isFamilyDetailsMandatory();
            if (!isFamilyDetailsMandatory) {
                actionForm.removeFamilyMember(0);
            }
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_MANDATORY, isFamilyDetailsMandatory, request);
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_HIDDEN, false, request);
        } else {
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_MANDATORY, isSpouseFatherInformationMandatory(), request);
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_HIDDEN, isSpouseFatherInformationHidden(), request);
        }
      
        return mapping.findForward(ActionForwards.load_success.toString());
    }
    
    @TransactionDemarcate(joinToken = true)
    public ActionForward addFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        FormFile file = actionForm.getSelectedFile();
        String fileName = actionForm.getSelectedFile().getFileName();
        String fileContentType = actionForm.getSelectedFile().getContentType();
        Integer fileSize = actionForm.getSelectedFile().getFileSize();
        String fileDescription = actionForm.getSelectedFileDescription();
        if (file != null) {
            actionForm.getFiles().add(file);
            actionForm.getFilesMetadata().add(
                    new UploadedFileDto(fileName, fileContentType, fileSize, fileDescription));
        }
        return mapping.findForward(ActionForwards.load_success.toString());
    }
    
    @TransactionDemarcate(joinToken = true)
    public ActionForward deleteFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        String fileName = request.getParameter("fileName");
        if (fileName != null) {
            int index = 0;
            for(FormFile formFile : actionForm.getFiles()) {
                if (formFile.getFileName().equals(fileName)) {
                    index = actionForm.getFiles().indexOf(formFile);
                    break;
                }
            }
            if (index >= 0) {
                actionForm.getFiles().remove(index);
                actionForm.getFilesMetadata().remove(index);
            }
        }
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ClientCustActionForm actionForm = (ClientCustActionForm) form;

        ClientFamilyDetailsDto clientFamilyDetails = this.clientServiceFacade.retrieveClientFamilyDetails();

        if (clientFamilyDetails.isFamilyDetailsRequired()) {

            SessionUtils.setCollectionAttribute(ClientConstants.LIVING_STATUS_ENTITY, clientFamilyDetails.getLivingStatus(), request);
            SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, clientFamilyDetails.getGenders(), request);

            actionForm.setFamilyDetailBean(clientFamilyDetails.getFamilyDetails());

            return mapping.findForward(ActionForwards.next_success_family.toString());
        }

        return createClientQuestionnaire.fetchAppliedQuestions(mapping, actionForm, request, ActionForwards.next_success);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward familyInfoNext(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                        @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.setFamilyDateOfBirth();
        actionForm.constructFamilyDetails();
        return createClientQuestionnaire.fetchAppliedQuestions(mapping, actionForm, request, ActionForwards.next_success);
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
        ClientNameDetailDto clientNameDetail = actionForm.getClientName();
        clientNameDetail.setNames(ClientRules.getNameSequence());
        String clientName = clientNameDetail.getDisplayName();
        String givenDateOfBirth = actionForm.getDateOfBirth();

        ClientNameDetailDto spouseName = actionForm.getSpouseName();
        spouseName.setNames(ClientRules.getNameSequence());

        DateTime dateOfBirth = new DateTime(DateUtils.getDateAsSentFromBrowser(givenDateOfBirth));
        ProcessRulesDto processRules = this.clientServiceFacade.previewClient(governmentId, dateOfBirth, clientName, actionForm.isDefaultFeeRemoved(), actionForm.getOfficeIdValue(), actionForm.getLoanOfficerIdValue());

        String pendingApprovalState = processRules.isClientPendingApprovalStateEnabled() ? CustomerConstants.YES : CustomerConstants.NO;
        SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, pendingApprovalState, request);

        Short officeId = actionForm.getOfficeIdValue();
        Short groupFlag = actionForm.getGroupFlagValue();
        String parentGroupId = actionForm.getParentGroupId();
        ClientFormCreationDto clientFormCreationDto = this.clientServiceFacade.retrieveClientFormCreationData(groupFlag, officeId, parentGroupId);
        
        if (clientFormCreationDto.getFormedByPersonnelId() != null) {
            UserContext userContext = getUserContext(request);
            MeetingBO groupMeeting = customerDao.findCustomerById(Integer.valueOf(parentGroupId)).getCustomerMeetingValue();
            clientFormCreationDto.getParentCustomerMeeting().setMeetingSchedule(CustomerUIHelperFn.getMeetingSchedule(groupMeeting, userContext));     
            SessionUtils.setAttribute("meeting", clientFormCreationDto.getParentCustomerMeeting(), request);
        }
        addWarningMessages(request, processRules,calculateAge(DateUtils.getDateAsSentFromBrowser(givenDateOfBirth)));
        actionForm.setEditFamily("edit");
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(givenDateOfBirth)));
        actionForm.setClientName(clientNameDetail);
        actionForm.setSpouseName(spouseName);
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    private void addWarningMessages(HttpServletRequest request, ProcessRulesDto processRules, int age)
            throws PageExpiredException {
        if (processRules.isGovernmentIdValidationFailing()) {
            SessionUtils.addWarningMessage(request, CustomerConstants.CLIENT_WITH_SAME_GOVT_ID_EXIST_IN_CLOSED);
        }

        if (processRules.isDuplicateNameOnBlackListedClient()) {
            SessionUtils.addWarningMessage(request, CustomerConstants.CLIENT_WITH_SAME_NAME_DOB_EXIST_IN_BLACKLISTED);
        }

        if (processRules.isDuplicateNameOnClosedClient()) {
            SessionUtils.addWarningMessage(request, CustomerConstants.CLIENT_WITH_SAME_NAME_DOB_EXIST_IN_CLOSED);
        }
        if (processRules.isGovermentIdValidationUnclosedFailing()) {
            SessionUtils.addWarningMessage(request, CustomerConstants.CLIENT_WITH_SAME_GOVT_ID_EXIST_IN_UNCLOSED);
        }
        if (processRules.isduplicateNameOnClient()) {
            SessionUtils.addWarningMessage(request, CustomerConstants.CLIENT_WITH_SAME_NAME_EXIST);
        }
        if(ClientRules.isAgeCheckEnabled()&&ClientRules.isAgeCheckWarningInsteadOfErrorEnabled()) {
        	if(age > ClientRules.getMaximumAgeForNewClient() || age< ClientRules.getMinimumAgeForNewClient()){
        		SessionUtils.addWarningMessage(request, new ActionMessage(CustomerConstants.CLIENT_AGE_OUT_OF_BOUNDS
        																 ,ClientRules.getMinimumAgeForNewClient()
        																 ,ClientRules.getMaximumAgeForNewClient()));        		
        	}
        }
    }

    public ActionForward cancel(ActionMapping mapping, ActionForm form,
                                @SuppressWarnings("unused") HttpServletRequest request,
                                @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        String forward = null;
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        String fromPage = actionForm.getInput();
        if (ClientConstants.INPUT_PERSONAL_INFO.equals(fromPage) || ClientConstants.INPUT_MFI_INFO.equals(fromPage)
                || CenterConstants.INPUT_CREATE.equals(fromPage)) {
            actionForm.setEditFamily("notEdit");
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

        ClientBO clientBO = getClientFromSession(request);
        ClientPhotoService cps = ApplicationContextProvider.getBean(ClientPhotoService.class);
        ClientPhoto cp = cps.read(clientBO.getCustomerId().longValue());
        InputStream in = null;
        if(cp != null) {
            in = new ByteArrayInputStream(cps.getData(cp));
            response.setContentType(cp.getImageInfo().getContentType());
        } else {
            in = ClientPhotoService.class.getResourceAsStream("/org/mifos/image/nopicture.png");
            response.setContentType("image/png");
        }
        in.mark(0);
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

        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
        List<SavingsDetailDto> allowedSavingProducts = getSavingsOfferingsFromSession(request);

        if (ClientRules.isFamilyDetailsRequired()) {
            actionForm.setFamilyDateOfBirth();
            actionForm.constructFamilyDetails();
        }

        List<Short> selectedSavingProducts = actionForm.getSelectedOfferings();
        String clientName = actionForm.getClientName().getDisplayName();
        Short clientStatus = actionForm.getStatusValue().getValue();
        java.sql.Date mfiJoiningDate = DateUtils.getDateAsSentFromBrowser(actionForm.getMfiJoiningDate());
        String externalId = actionForm.getExternalId();
        AddressDto address = null;
        if (actionForm.getAddress() != null) {
            address = Address.toDto(actionForm.getAddress());
        }
        Short formedBy = actionForm.getFormedByPersonnelValue();
        java.sql.Date dateOfBirth = DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth());
        String governmentId = actionForm.getGovernmentId();
        boolean trained = isTrained(actionForm.getTrainedValue());
        java.sql.Date trainedDate = DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate());
        Short groupFlagValue = actionForm.getGroupFlagValue();
        ClientNameDetailDto clientNameDetailDto = actionForm.getClientName();
        ClientPersonalDetailDto clientPersonalDetailDto = actionForm.getClientDetailView();
        ClientNameDetailDto spouseFatherName = actionForm.getSpouseName();
        InputStream picture = actionForm.getCustomerPicture();
        String parentGroupId = actionForm.getParentGroupId();
        List<ClientNameDetailDto> familyNames = actionForm.getFamilyNames();
        List<ClientFamilyDetailDto> familyDetails = actionForm.getFamilyDetails();
        Short loanOfficerId = actionForm.getLoanOfficerIdValue();
        Short officeId = actionForm.getOfficeIdValue();

        // only applies when status is active
        LocalDate activationDateAsToday = new LocalDate();

        ClientCreationDetail clientCreationDetail = new ClientCreationDetail(selectedSavingProducts, clientName, clientStatus, mfiJoiningDate, externalId,
                address, formedBy, dateOfBirth, governmentId, trained, trainedDate, groupFlagValue, clientNameDetailDto, clientPersonalDetailDto, spouseFatherName,
                picture, actionForm.getFeesToApply(), parentGroupId, familyNames, familyDetails, loanOfficerId, officeId, activationDateAsToday);

        MeetingDto meetingDto = null;
        if (meeting != null) {
            meetingDto = meeting.toDto();
        }

        CustomerDetailsDto clientDetails = this.clientServiceFacade.createNewClient(clientCreationDetail, meetingDto,
                allowedSavingProducts);
        
        List<FormFile> formFiles = actionForm.getFiles();
        List<UploadedFileDto> filesMetadata = actionForm.getFilesMetadata();
        
        for(int i=0; i<formFiles.size(); i++)
        {
            if (formFiles.get(i).getFileSize() != 0) {
                InputStream inputStream = formFiles.get(i).getInputStream();
                UploadedFileDto fileMetadata = filesMetadata.get(i);
                clientServiceFacade.uploadFile(clientDetails.getId(), inputStream, fileMetadata);
            }
        }

        actionForm.setCustomerId(clientDetails.getId().toString());
        actionForm.setGlobalCustNum(clientDetails.getGlobalCustNum());
        actionForm.setEditFamily("notEdit");
        createClientQuestionnaire.saveResponses(request, actionForm, clientDetails.getId());
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    private boolean isTrained(Short trainedValue) {
        return Short.valueOf("1").equals(trainedValue);
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

        String clientSystemId = ((ClientCustActionForm) form).getGlobalCustNum();
        ClientInformationDto clientInformationDto;
        try {
            clientInformationDto = clientServiceFacade.getClientInformationDto(clientSystemId);
        }
        catch (MifosRuntimeException e) {
            if (e.getCause() instanceof ApplicationException) {
                throw (ApplicationException) e.getCause();
            }
            throw e;
        }
        SessionUtils.removeThenSetAttribute("clientInformationDto", clientInformationDto, request);

        // John W - for breadcrumb or another other action downstream that exists business_key set (until refactored)
        ClientBO clientBO = (ClientBO) this.customerDao.findCustomerById(clientInformationDto.getClientDisplay().getCustomerId());
        SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, clientBO, request);
        SessionUtils.setAttribute(ClientConstants.IS_PHOTO_FIELD_HIDDEN, FieldConfig.getInstance().isFieldHidden("Client.Photo"), request);
        setCurrentPageUrl(request, clientBO);
        setQuestionGroupInstances(request, clientBO);

        return mapping.findForward(ActionForwards.get_success.toString());
    }

    private void setQuestionGroupInstances(HttpServletRequest request, ClientBO clientBO) throws PageExpiredException {
        QuestionnaireServiceFacade questionnaireServiceFacade = questionnaireServiceFacadeLocator.getService(request);
        boolean containsQGForCloseClient = false;
        if (questionnaireServiceFacade != null) {
            setQuestionGroupInstances(questionnaireServiceFacade, request, clientBO.getCustomerId());
            if (clientBO.getCustomerStatus().getId().equals(CustomerStatus.CLIENT_CLOSED.getValue())) {
                containsQGForCloseClient = questionnaireServiceFacade.getQuestionGroupInstances(clientBO.getCustomerId(), "Close", "Client").size() > 0;
            }
        }
        SessionUtils.removeThenSetAttribute("containsQGForCloseClient", containsQGForCloseClient, request);
    }

    // Intentionally made public to aid testing !
    public void setQuestionGroupInstances(QuestionnaireServiceFacade questionnaireServiceFacade, HttpServletRequest request, Integer customerId) throws PageExpiredException {
        List<QuestionGroupInstanceDetail> instanceDetails = questionnaireServiceFacade.getQuestionGroupInstances(customerId, "View", "Client");
        SessionUtils.setCollectionAttribute("questionGroupInstances", instanceDetails, request);
    }

    private void setCurrentPageUrl(HttpServletRequest request, ClientBO clientBO) throws PageExpiredException, UnsupportedEncodingException {
        SessionUtils.removeThenSetAttribute("currentPageUrl", constructCurrentPageUrl(request, clientBO), request);
    }

    private String constructCurrentPageUrl(HttpServletRequest request, CustomerBO clientBO) throws UnsupportedEncodingException {
        String officerId = request.getParameter("recordOfficeId");
        String loanOfficerId = request.getParameter("recordLoanOfficerId");
        String url = String.format("clientCustAction.do?globalCustNum=%s&recordOfficeId=%s&recordLoanOfficerId=%s",
                clientBO.getGlobalCustNum(), officerId, loanOfficerId);
        return url;
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
        actionForm.clearMostButNotAllFieldsOnActionForm();
        ClientBO clientFromSession = getClientFromSession(request);
        final String clientSystemId = clientFromSession.getGlobalCustNum();
        ClientBO client = this.customerDao.findClientBySystemId(clientSystemId);
        short loanOfficerId = client.getCreatedBy();
        String clientStatus = client.getCustomerStatus().getName();
        ClientPersonalInfoDto personalInfo = this.clientServiceFacade.retrieveClientPersonalInfoForUpdate(clientSystemId, clientStatus, loanOfficerId);
        
        SessionUtils.setCollectionAttribute(ClientConstants.SALUTATION_ENTITY, personalInfo.getClientDropdowns().getSalutations(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, personalInfo.getClientDropdowns().getGenders(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.MARITAL_STATUS_ENTITY, personalInfo.getClientDropdowns().getMaritalStatuses(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.CITIZENSHIP_ENTITY, personalInfo.getClientDropdowns().getCitizenship(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.ETHNICITY_ENTITY, personalInfo.getClientDropdowns().getEthnicity(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, personalInfo.getClientDropdowns().getEducationLevels(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, personalInfo.getClientDropdowns().getBusinessActivity(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.POVERTY_STATUS, personalInfo.getClientDropdowns().getPoverty(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.HANDICAPPED_ENTITY, personalInfo.getClientDropdowns().getHandicapped(), request);

        UserContext userContext = getUserContext(request);
        List<SpouseFatherLookupEntity> spouseFather = legacyMasterDao.findMasterDataEntitiesWithLocale(SpouseFatherLookupEntity.class);
        SessionUtils.setCollectionAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, spouseFather, request);

        SessionUtils.setAttribute("CanEditPhoneNumber", ActivityMapper.getInstance().isEditPhoneNumberPermitted(userContext, userContext.getBranchId()), request);

        boolean isFamilyDetailsRequired = personalInfo.getClientRules().isFamilyDetailsRequired();
        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_REQUIRED, isFamilyDetailsRequired, request);
        if (isFamilyDetailsRequired) {
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_MANDATORY, isFamilyDetailsMandatory(), request);
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_HIDDEN, false, request);
        } else {
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_MANDATORY, isSpouseFatherInformationMandatory(), request);
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_HIDDEN, isSpouseFatherInformationHidden(), request);
        }
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, new ArrayList<CustomFieldDto>(), request);

        // customer specific
        actionForm.setCustomerId(personalInfo.getCustomerDetail().getCustomerId().toString());
        actionForm.setLoanOfficerId(personalInfo.getCustomerDetail().getLoanOfficerIdAsString());
        actionForm.setGlobalCustNum(personalInfo.getCustomerDetail().getGlobalCustNum());
        actionForm.setExternalId(personalInfo.getCustomerDetail().getExternalId());

        actionForm.setAddress(Address.toAddress(client.getAddress()));

        // client specific
        actionForm.setGovernmentId(personalInfo.getClientDetail().getGovernmentId());
        actionForm.setDateOfBirth(personalInfo.getClientDetail().getDateOfBirth());
        actionForm.setClientDetailView(personalInfo.getClientDetail().getCustomerDetail());

        ClientNameDetailDto clientName = personalInfo.getClientDetail().getClientName();
        clientName.setNames(ClientRules.getNameSequence());
        actionForm.setClientName(clientName);
        String photoDelete = request.getParameter("photoDelete");
        if(photoDelete != null && photoDelete.equals("true")) {
            ApplicationContextProvider.getBean(ClientPhotoService.class).delete(client.getCustomerId().longValue());
        }

        boolean isPhotoFieldHidden = FieldConfig.getInstance().isFieldHidden("Client.Photo");
        SessionUtils.setAttribute(ClientConstants.IS_PHOTO_FIELD_HIDDEN, isPhotoFieldHidden, request);
        if (!isPhotoFieldHidden) {
            ClientPhotoDto clientPhotoDto = this.clientServiceFacade.getClientPhoto(client.getCustomerId().longValue());
            if (clientPhotoDto != null) {
                FormFile formFile = new PictureFormFile(clientPhotoDto.getContentType(), clientPhotoDto.getOut(),
                        client.getCustomerId().toString(), clientPhotoDto.getContentLength().intValue());
                actionForm.setPicture(formFile);
            } else {
                actionForm.setPicture(null);
            }
        } else {
            actionForm.setPicture(null);
        }
        ClientNameDetailDto spouseName = personalInfo.getClientDetail().getSpouseName();
        if (spouseName != null) {
            spouseName.setNames(ClientRules.getNameSequence());
            actionForm.setSpouseName(spouseName);
        }
        actionForm.setSpouseName(spouseName);
        actionForm.setCustomFields(new ArrayList<CustomFieldDto>());

        SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, client, request);

        return mapping.findForward(ActionForwards.editPersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewEditPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;

        String dateOfBirth = actionForm.getDateOfBirth();
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(dateOfBirth)));

        ClientRulesDto clientRules = this.clientServiceFacade.retrieveClientDetailsForPreviewingEditOfPersonalInfo();

        boolean isFamilyDetailsRequired = clientRules.isFamilyDetailsRequired();
        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_REQUIRED, isFamilyDetailsRequired, request);
        if (isFamilyDetailsRequired) {
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_MANDATORY, isFamilyDetailsMandatory(), request);
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_HIDDEN, false, request);
        } else {
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_MANDATORY, isSpouseFatherInformationMandatory(), request);
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_HIDDEN, isSpouseFatherInformationHidden(), request);
        }

        ClientNameDetailDto clientName = actionForm.getClientName();
        clientName.setNames(ClientRules.getNameSequence());
        actionForm.setClientName(clientName);

        ClientNameDetailDto spouseName = actionForm.getSpouseName();
        if (spouseName != null) {
            spouseName.setNames(ClientRules.getNameSequence());
            actionForm.setSpouseName(spouseName);
        }

        return mapping.findForward(ActionForwards.previewEditPersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward prevEditPersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                              @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        boolean isFamilyDetailsRequired = ClientRules.isFamilyDetailsRequired();
        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_REQUIRED, isFamilyDetailsRequired, request);
        if (isFamilyDetailsRequired) {
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_MANDATORY, isFamilyDetailsMandatory(), request);
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_HIDDEN, false, request);
        } else {
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_MANDATORY, isSpouseFatherInformationMandatory(), request);
            SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_HIDDEN, isSpouseFatherInformationHidden(), request);
        }
        actionForm.setAge(calculateAge(DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth())));
        return mapping.findForward(ActionForwards.prevEditPersonalInfo_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updatePersonalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        ClientBO clientInSession = getClientFromSession(request);
        Integer oldClientVersionNumber = clientInSession.getVersionNo();
        Integer customerId = clientInSession.getCustomerId();
        String clientStatus = clientInSession.getCustomerStatus().getName();
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        short loanOfficerId = clientInSession.getCreatedBy();
        final String clientSystemId = clientInSession.getGlobalCustNum();

        ClientPersonalInfoDto clientPersonalInfo = this.clientServiceFacade.retrieveClientPersonalInfoForUpdate(clientSystemId, clientStatus, loanOfficerId);
        
        AddressDto address = null;
        if (actionForm.getAddress() != null) {
            address = Address.toDto(actionForm.getAddress());
        }

        if(clientPersonalInfo.getCustomerDetail()!= null)
        {
            if(clientPersonalInfo.getCustomerDetail().getAddress()!=null)
            {
                if(clientPersonalInfo.getCustomerDetail().getAddress().getPhoneNumber() != null && (!clientPersonalInfo.getCustomerDetail().getAddress().getPhoneNumber().equals(address.getPhoneNumber())))
                {
                    UserContext userContext = getUserContext(request);
                    if(!ActivityMapper.getInstance().isEditPhoneNumberPermitted(userContext, userContext.getBranchId()))
                    {
                        throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
                    }
                }
                else if(clientPersonalInfo.getCustomerDetail().getAddress().getPhoneNumber() == null && address.getPhoneNumber()!= null && !address.getPhoneNumber().equals(""))
                {
                    UserContext userContext = getUserContext(request);
                    if(!ActivityMapper.getInstance().isEditPhoneNumberPermitted(userContext, userContext.getBranchId()))
                    {
                        throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
                    }
                }
            }
            else if(address.getPhoneNumber()!= null && !address.getPhoneNumber().equals(""))
            {
                UserContext userContext = getUserContext(request);
                if(!ActivityMapper.getInstance().isEditPhoneNumberPermitted(userContext, userContext.getBranchId()))
                {
                    throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
                }
            }
        }
        else if(address.getPhoneNumber()!= null && !address.getPhoneNumber().equals(""))
        {
            UserContext userContext = getUserContext(request);
            if(!ActivityMapper.getInstance().isEditPhoneNumberPermitted(userContext, userContext.getBranchId()))
            {
                throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
            }
        }

        ClientNameDetailDto spouseFather = null;
        if (!ClientRules.isFamilyDetailsRequired()) {
            spouseFather = actionForm.getSpouseName();
        }

        InputStream picture = null;
        if (actionForm.getPicture() != null && StringUtils.isNotBlank(actionForm.getPicture().getFileName())) {
            picture = actionForm.getCustomerPicture();
        }

        ClientNameDetailDto clientNameDetails = actionForm.getClientName();
        ClientPersonalDetailDto clientDetail = actionForm.getClientDetailView();

        String governmentId = actionForm.getGovernmentId();
        String clientDisplayName = actionForm.getClientName().getDisplayName();

        String dateOfBirth = actionForm.getDateOfBirth();

        ClientPersonalInfoUpdate personalInfo = new ClientPersonalInfoUpdate(customerId, oldClientVersionNumber,
                customFields, address, clientDetail, clientNameDetails, spouseFather, picture, governmentId,
                clientDisplayName, dateOfBirth);

        this.clientServiceFacade.updateClientPersonalInfo(personalInfo, clientStatus, loanOfficerId);
        
        return mapping.findForward(ActionForwards.updatePersonalInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editFamilyInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                        @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.clearMostButNotAllFieldsOnActionForm();
        ClientBO clientFromSession = getClientFromSession(request);

        ClientFamilyInfoDto clientFamilyInfo = this.clientServiceFacade.retrieveFamilyInfoForEdit(clientFromSession.getGlobalCustNum());

        SessionUtils.setCollectionAttribute(ClientConstants.LIVING_STATUS_ENTITY, clientFamilyInfo.getClientDropdowns().getLivingStatus(), request);
        SessionUtils.setCollectionAttribute(ClientConstants.GENDER_ENTITY, clientFamilyInfo.getClientDropdowns().getGenders(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, new ArrayList<CustomFieldDto>(), request);

        UserContext userContext = getUserContext(request);
        List<SpouseFatherLookupEntity> spouseFather = legacyMasterDao.findMasterDataEntitiesWithLocale(SpouseFatherLookupEntity.class);
        SessionUtils.setCollectionAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, spouseFather, request);

        SessionUtils.setAttribute(ClientConstants.ARE_FAMILY_DETAILS_MANDATORY, isFamilyDetailsMandatory(), request);

        // customer specific
        actionForm.setCustomerId(clientFamilyInfo.getCustomerDetail().getCustomerId().toString());
        actionForm.setLoanOfficerId(clientFamilyInfo.getCustomerDetail().getLoanOfficerIdAsString());
        actionForm.setGlobalCustNum(clientFamilyInfo.getCustomerDetail().getGlobalCustNum());
        actionForm.setExternalId(clientFamilyInfo.getCustomerDetail().getExternalId());

        // client specific
        actionForm.setGovernmentId(clientFamilyInfo.getClientDetail().getGovernmentId());
        actionForm.setDateOfBirth(clientFamilyInfo.getClientDetail().getDateOfBirth());
        actionForm.initializeFamilyMember();

        actionForm.setClientDetailView(clientFamilyInfo.getClientDetail().getCustomerDetail());
        actionForm.setClientName(clientFamilyInfo.getClientDetail().getClientName());
        actionForm.setSpouseName(clientFamilyInfo.getClientDetail().getSpouseName());
        actionForm.setCustomFields(new ArrayList<CustomFieldDto>());

        // client family specific
        int familyMemberCount = 0;
        for (ClientNameDetailDto familyMember : clientFamilyInfo.getFamilyMembers()) {

            actionForm.addFamilyMember();
            actionForm.setFamilyPrimaryKey(familyMemberCount, familyMember.getCustomerNameId());
            actionForm.setFamilyFirstName(familyMemberCount, familyMember.getFirstName());
            actionForm.setFamilyMiddleName(familyMemberCount, familyMember.getMiddleName());
            actionForm.setFamilyLastName(familyMemberCount, familyMember.getLastName());
            actionForm.setFamilySecondLastName(familyMemberCount, familyMember.getSecondLastName());
            actionForm.setFamilyRelationship(familyMemberCount, familyMember.getNameType());

            Map<Integer, List<ClientFamilyDetailDto>> clientFamilyDetailsMap = clientFamilyInfo.getClientFamilyDetails();

            Integer key = Integer.valueOf(familyMemberCount);
            List<ClientFamilyDetailDto> clientFamilyDetails = clientFamilyDetailsMap.get(key);

            if (clientFamilyDetails != null) {
                for (ClientFamilyDetailDto clientFamilyDetailDto : clientFamilyDetails) {
                    Calendar cal = Calendar.getInstance();

                    if (clientFamilyDetailDto.getDateOfBirth() != null) {
                        String date1 = DateUtils.makeDateAsSentFromBrowser(clientFamilyDetailDto.getDateOfBirth());
                        java.util.Date date = DateUtils.getDate(date1);
                        cal.setTime(date);
                        actionForm.setFamilyDateOfBirthDD(familyMemberCount, String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                        actionForm.setFamilyDateOfBirthMM(familyMemberCount, String.valueOf(cal.get(Calendar.MONTH) + 1));
                        actionForm.setFamilyDateOfBirthYY(familyMemberCount, String.valueOf(cal.get(Calendar.YEAR)));
                    }

                    actionForm.setFamilyGender(familyMemberCount, clientFamilyDetailDto.getGender());
                    actionForm.setFamilyLivingStatus(familyMemberCount, clientFamilyDetailDto.getLivingStatus());
                }

            }

            familyMemberCount++;
        }

        ClientBO client = this.customerDao.findClientBySystemId(clientFromSession.getGlobalCustNum());
        actionForm.setAddress(Address.toAddress(client.getAddress()));
        SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, client, request);

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

        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        ClientBO clientInSession = getClientFromSession(request);

        Integer customerId = clientInSession.getCustomerId();

        ClientFamilyInfoUpdate clientFamilyInfoUpdate = new ClientFamilyInfoUpdate(customerId, clientInSession.getVersionNo(),
                actionForm.getFamilyPrimaryKey(), actionForm.getFamilyNames(), actionForm.getFamilyDetails());
        this.clientServiceFacade.updateFamilyInfo(clientFamilyInfoUpdate);

        actionForm.setFamilyDateOfBirth();
        actionForm.constructFamilyDetails();

        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        return mapping.findForward(ActionForwards.updateFamilyInfo_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editMfiInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        actionForm.clearMostButNotAllFieldsOnActionForm();
        ClientBO clientFromSession = getClientFromSession(request);

        String clientSystemId = clientFromSession.getGlobalCustNum();

        ClientMfiInfoDto mfiInfoDto = this.clientServiceFacade.retrieveMfiInfoForEdit(clientSystemId);

        SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST, ClientRules.getCenterHierarchyExists(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, mfiInfoDto.getLoanOfficersList(), request);

        actionForm.setGroupDisplayName(mfiInfoDto.getGroupDisplayName());
        actionForm.setCenterDisplayName(mfiInfoDto.getCenterDisplayName());

        actionForm.setLoanOfficerId(mfiInfoDto.getCustomerDetail().getLoanOfficerIdAsString());
        actionForm.setCustomerId(mfiInfoDto.getCustomerDetail().getCustomerId().toString());
        actionForm.setGlobalCustNum(mfiInfoDto.getCustomerDetail().getGlobalCustNum());
        actionForm.setExternalId(mfiInfoDto.getCustomerDetail().getExternalId());

        actionForm.setGroupFlag(mfiInfoDto.getClientDetail().getGroupFlagAsString());
        actionForm.setParentGroupId(mfiInfoDto.getClientDetail().getParentGroupId().toString());
        actionForm.setTrained(mfiInfoDto.getClientDetail().getTrainedAsString());
        actionForm.setTrainedDate(mfiInfoDto.getClientDetail().getTrainedDate());

        actionForm.setDateOfBirth(clientFromSession.getDateOfBirth());

        ClientBO client = this.customerDao.findClientBySystemId(clientSystemId);
        SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, client, request);

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

        ClientCustActionForm actionForm = (ClientCustActionForm) form;
        ClientBO clientInSession = getClientFromSession(request);

        Integer clientId = clientInSession.getCustomerId();
        Integer oldVersionNumber = clientInSession.getVersionNo();

        boolean trained = false;
        if (trainedValue(actionForm) != null && trainedValue(actionForm).equals(YesNoFlag.YES.getValue())) {
            trained = true;
        }

        DateTime trainedDate = null;
        try {
            java.sql.Date inputDate = trainedDate(actionForm);
            if (inputDate != null) {
                trainedDate = new DateTime(trainedDate(actionForm));
            }
        } catch (InvalidDateException e) {
            throw new CustomerException(ClientConstants.TRAINED_DATE_MANDATORY);
        }

        Short personnelId = Short.valueOf("-1");
        if (groupFlagValue(actionForm).equals(YesNoFlag.NO.getValue())) {
            if (actionForm.getLoanOfficerIdValue() != null) {
                personnelId = actionForm.getLoanOfficerIdValue();
            }
        } else if (groupFlagValue(actionForm).equals(YesNoFlag.YES.getValue())) {
            // TODO for an urgent fix this reads client to get personnelId.
            // Client is read again in updateClientMfiInfo. Refactor to only read in
            // updateClientMfiInfo.
            ClientBO client = (ClientBO) this.customerDao.findCustomerById(clientId);
            personnelId = client.getPersonnel().getPersonnelId();
        }

        ClientMfiInfoUpdate clientMfiInfoUpdate = new ClientMfiInfoUpdate(clientId, oldVersionNumber, personnelId,
                externalId(actionForm), trained, trainedDate);

        this.clientServiceFacade.updateClientMfiInfo(clientMfiInfoUpdate);

        return mapping.findForward(ActionForwards.updateMfiInfo_success.toString());
    }

    private Date trainedDate(ClientCustActionForm actionForm) throws InvalidDateException {
        return DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate());
    }

    private Short trainedValue(ClientCustActionForm actionForm) {
        return actionForm.getTrainedValue();
    }

    private String externalId(ClientCustActionForm actionForm) {
        return actionForm.getExternalId();
    }

    private Short groupFlagValue(ClientCustActionForm actionForm) {
        return actionForm.getGroupFlagValue();
    }

    private int calculateAge(Date date) {
        return DateUtils.DateDiffInYears(date);
    }

    private ClientBO getClientFromSession(HttpServletRequest request) throws PageExpiredException {
        return (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
    }

    @SuppressWarnings("unchecked")
    private List<SavingsDetailDto> getSavingsOfferingsFromSession(HttpServletRequest request)
            throws PageExpiredException {
        return (List<SavingsDetailDto>) SessionUtils.getAttribute(ClientConstants.SAVINGS_OFFERING_LIST, request);
    }

    private boolean isSpouseFatherInformationMandatory() {
        return FieldConfig.getInstance().isFieldManadatory("Client." + HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_INFORMATION);
    }

    private boolean isSpouseFatherInformationHidden() {
        return FieldConfig.getInstance().isFieldHidden("Client." + HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_INFORMATION);
    }

    private boolean isFamilyDetailsMandatory() {
        return FieldConfig.getInstance().isFieldManadatory("Client." + HiddenMandatoryFieldNamesConstants.FAMILY_DETAILS);
    }

    @Override
    public ActionForward captureQuestionResponses(ActionMapping mapping, ActionForm form, HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "captureQuestionResponses");
        ActionErrors errors = createClientQuestionnaire.validateResponses(request, (ClientCustActionForm) form);
        if (errors != null && !errors.isEmpty()) {
            addErrors(request, errors);
            return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
        }
        return createClientQuestionnaire.rejoinFlow(mapping);
    }

    @Override
    public ActionForward editQuestionResponses(ActionMapping mapping, ActionForm form, HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "editQuestionResponses");
        return createClientQuestionnaire.editResponses(mapping, request, (ClientCustActionForm) form);
    }
}
class PictureFormFile implements FormFile {

    private String contentType;
    private byte[] data;
    private String name;
    private int size;


    public PictureFormFile(String contentType, byte[] data, String name, int size) {
        super();
        this.contentType = contentType;
        this.data = data;
        this.name = name;
        this.size = size;
    }

    @Override
    public void destroy() {
        contentType = null;
        data = null;
        name = null;
        size = 0;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public byte[] getFileData() throws FileNotFoundException, IOException {
        return data;
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public int getFileSize() {
        return size;
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException, IOException {
        return new ByteArrayInputStream(data);
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;

    }

    @Override
    public void setFileName(String name) {
        this.name = name;
    }

    @Override
    public void setFileSize(int size) {
        this.size = size;
    }
}