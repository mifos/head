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

package org.mifos.customers.personnel.struts.action;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.questionnaire.struts.DefaultQuestionnaireServiceFacadeLocator;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.Localization;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelDetailsEntity;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;
import org.mifos.customers.personnel.business.PersonnelRoleEntity;
import org.mifos.customers.personnel.business.PersonnelStatusEntity;
import org.mifos.customers.personnel.exceptions.PersonnelException;
import org.mifos.customers.personnel.struts.actionforms.PersonActionForm;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CreateOrUpdatePersonnelInformation;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.DefinePersonnelDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.PersonnelInformationDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;

public class PersonAction extends SearchAction {

    private QuestionnaireFlowAdapter createGroupQuestionnaire = new QuestionnaireFlowAdapter("Create", "Personnel",
            ActionForwards.preview_success, "custSearchAction.do?method=loadMainSearch", new DefaultQuestionnaireServiceFacadeLocator());

    @SuppressWarnings("unused")
    @TransactionDemarcate(saveToken = true)
    public ActionForward chooseOffice(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SessionUtils.setAttribute(CustomerConstants.URL_MAP, null, request.getSession(false));
        return mapping.findForward(ActionForwards.chooseOffice_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        PersonActionForm personActionForm = (PersonActionForm) form;
        Short officeId = getShortValue(personActionForm.getOfficeId());

        OfficeBO office = this.officeDao.findOfficeById(officeId);
        SessionUtils.setAttribute(PersonnelConstants.OFFICE, office, request);
        personActionForm.clear();

        //Shahid - keeping the previous session objects for the sake of existing tests, once fully converted to spring
        //then we can get rid of the session objects made redundant by the dto
        DefinePersonnelDto definePersonnelDto = this.personnelServiceFacade.retrieveInfoForNewUserDefinition(officeId);
        SessionUtils.setAttribute("definePersonnelDto", definePersonnelDto, request);

        List<ValueListElement> titles = this.customerDao.retrieveTitles();
        List<ValueListElement> genders = this.customerDao.retrieveGenders();
        List<ValueListElement> maritalStatuses = this.customerDao.retrieveMaritalStatuses();
        List<ValueListElement> languages = Localization.getInstance().getLocaleForUI();

        List<RoleBO> roles = legacyRolesPermissionsDao.getRoles();
        List<PersonnelLevelEntity> personnelLevels = this.customerDao.retrievePersonnelLevels();

        SessionUtils.setCollectionAttribute(PersonnelConstants.TITLE_LIST, titles, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, personnelLevels, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.GENDER_LIST, genders, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.MARITAL_STATUS_LIST, maritalStatuses, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.LANGUAGE_LIST, languages, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.ROLES_LIST, roles, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.ROLEMASTERLIST, roles, request);

        List<CustomFieldDefinitionEntity> customFieldDefs1 = new ArrayList<CustomFieldDefinitionEntity>();
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, customFieldDefs1, request);

        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        personActionForm.setCustomFields(customFields);

        if (office.getOfficeLevel() != OfficeLevel.BRANCHOFFICE) {
            for (MasterDataEntity level : personnelLevels) {
                if (level.getId().equals(PersonnelLevel.LOAN_OFFICER.getValue())) {
                    personnelLevels.remove(level);
                    break;
                }
            }
        }
        personActionForm.setCustomFields(new ArrayList<CustomFieldDto>());
        personActionForm.setDateOfJoiningMFI(DateUtils.makeDateAsSentFromBrowser());

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @SuppressWarnings("unchecked")
    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        PersonActionForm personActionForm = (PersonActionForm) form;
        UserContext userContext = getUserContext(request);
        personActionForm.setDateOfJoiningBranch(DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        PersonActionForm personActionForm1 = (PersonActionForm) form;
        boolean addFlag = false;
        List<RoleBO> selectList = new ArrayList<RoleBO>();
        if (personActionForm1.getPersonnelRoles() != null) {
            List<RoleBO> masterList = (List<RoleBO>) SessionUtils.getAttribute(PersonnelConstants.ROLEMASTERLIST, request);
            for (RoleBO role : masterList) {
                for (String roleId : personActionForm1.getPersonnelRoles()) {
                    if (roleId != null && role.getId().intValue() == Integer.valueOf(roleId).intValue()) {
                        selectList.add(role);
                        addFlag = true;
                    }
                }
            }
        }
        if (addFlag) {
            SessionUtils.setCollectionAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST, selectList, request);
        } else {
            SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST, null, request);
        }

        return createGroupQuestionnaire.fetchAppliedQuestions(mapping, personActionForm, request, ActionForwards.preview_success);
    }

    @SuppressWarnings("unused")
    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PersonActionForm personActionForm = (PersonActionForm) form;
        personActionForm.setPersonnelRoles(null);
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        PersonActionForm personActionForm = (PersonActionForm) form;
        CreateOrUpdatePersonnelInformation perosonnelInfo = translateFormToCreatePersonnelInformationDto(request, personActionForm);

        try {
            UserDetailDto userDetails = this.personnelServiceFacade.createPersonnelInformation(perosonnelInfo);

            String globalPersonnelNum = userDetails.getSystemId();
            Name name = new Name(personActionForm.getFirstName(), personActionForm.getMiddleName(), personActionForm.getSecondLastName(), personActionForm.getLastName());

            request.setAttribute("displayName", name.getDisplayName());
            request.setAttribute("globalPersonnelNum", globalPersonnelNum);
            createGroupQuestionnaire.saveResponses(request, personActionForm, userDetails.getId());
            return mapping.findForward(ActionForwards.create_success.toString());
        } catch (BusinessRuleException e) {
            throw new PersonnelException(e.getMessageKey(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private CreateOrUpdatePersonnelInformation translateFormToCreatePersonnelInformationDto(HttpServletRequest request,
            PersonActionForm personActionForm) throws PageExpiredException, InvalidDateException {
        UserContext userContext = getUserContext(request);
        PersonnelLevel level = PersonnelLevel.fromInt(getShortValue(personActionForm.getLevel()));

        PersonnelStatus personnelStatus = PersonnelStatus.ACTIVE;
        if (StringUtils.isNotBlank(personActionForm.getStatus())) {
            personnelStatus = PersonnelStatus.getPersonnelStatus(getShortValue(personActionForm.getStatus()));
        }

        OfficeBO office = (OfficeBO) SessionUtils.getAttribute(PersonnelConstants.OFFICE, request);
        if (office == null) {
            Short officeId = getShortValue(personActionForm.getOfficeId());
            office = this.officeDao.findOfficeById(officeId);
        }

        Integer title = getIntegerValue(personActionForm.getTitle());

        Short preferredLocale = Localization.getInstance().getConfiguredLocaleId();
        preferredLocale = getPerefferedLocale(personActionForm, userContext);

        Date dob = null;
        if (personActionForm.getDob() != null && !personActionForm.getDob().equals("")) {
            dob = DateUtils.getDate(personActionForm.getDob());
        }
        Date dateOfJoiningMFI = null;

        if (personActionForm.getDateOfJoiningMFI() != null && !personActionForm.getDateOfJoiningMFI().equals("")) {
            dateOfJoiningMFI = DateUtils.getDateAsSentFromBrowser(personActionForm.getDateOfJoiningMFI());
        }

        List<RoleBO> roles = new ArrayList<RoleBO>();
        boolean addFlag = false;
        List<RoleBO> selectList = new ArrayList<RoleBO>();
        List<RoleBO> masterList = (List<RoleBO>) SessionUtils.getAttribute(PersonnelConstants.ROLEMASTERLIST, request);
        if (personActionForm.getPersonnelRoles() != null) {
            for (RoleBO role : masterList) {
                for (String roleId : personActionForm.getPersonnelRoles()) {
                    if (roleId != null && role.getId().intValue() == Integer.valueOf(roleId).intValue()) {
                        selectList.add(role);
                        addFlag = true;
                    }
                }
            }
        }
        if (addFlag) {
            roles = selectList;
        }

        List<ListElement> roleList = new ArrayList<ListElement>();
        for (RoleBO element : roles) {
            ListElement listElement = new ListElement(new Integer(element.getId()), element.getName());
            roleList.add(listElement);
        }

        Address address = personActionForm.getAddress();
        AddressDto addressDto = new AddressDto(address.getLine1(), address.getLine2(), address.getLine3(), address.getCity(), address.getState(),
                address.getCountry(), address.getZip(), address.getPhoneNumber());

        Long id = null;
        if (StringUtils.isNotBlank(personActionForm.getPersonnelId())) {
            id = Long.valueOf(personActionForm.getPersonnelId());
        }

        CreateOrUpdatePersonnelInformation perosonnelInfo = new CreateOrUpdatePersonnelInformation(id, level.getValue(), office.getOfficeId(), title,
                preferredLocale, personActionForm.getUserPassword(), personActionForm.getLoginName(), personActionForm.getEmailId(), roleList,
                personActionForm.getCustomFields(), personActionForm.getFirstName(), personActionForm.getMiddleName(), personActionForm.getLastName(),
                personActionForm.getSecondLastName(), personActionForm.getGovernmentIdNumber(), new DateTime(dob),
                getIntegerValue(personActionForm.getMaritalStatus()), getIntegerValue(personActionForm.getGender()), new DateTime(dateOfJoiningMFI),
                new DateTimeService().getCurrentDateTime(), addressDto, personnelStatus.getValue());

        return perosonnelInfo;
    }

    private Short getPerefferedLocale(PersonActionForm personActionForm, UserContext userContext) {
        if (personActionForm.getPreferredLocale() != null) {
            return personActionForm.getPreferredLocale();
        }

        return userContext.getLocaleId();
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        PersonActionForm actionform = (PersonActionForm) form;
        actionform.clear();
        PersonnelBO personnelInSession = (PersonnelBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        PersonnelBO personnel = this.personnelDao.findPersonnelById(personnelInSession.getPersonnelId());

        List<ValueListElement> titles = this.customerDao.retrieveTitles();
        List<ValueListElement> genders = this.customerDao.retrieveGenders();
        List<ValueListElement> maritalStatuses = this.customerDao.retrieveMaritalStatuses();
        List<ValueListElement> languages = Localization.getInstance().getLocaleForUI();

        List<RoleBO> roles = legacyRolesPermissionsDao.getRoles();
        List<PersonnelLevelEntity> personnelLevels = this.customerDao.retrievePersonnelLevels();
        for (PersonnelLevelEntity personnelLevelEntity : personnelLevels) {
            String messageTextLookup = ApplicationContextProvider.getBean(MessageLookup.class).lookup(personnelLevelEntity.getLookUpValue().getPropertiesKey());
            personnelLevelEntity.setName(messageTextLookup);
        }

        SessionUtils.setCollectionAttribute(PersonnelConstants.TITLE_LIST, titles, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, personnelLevels, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.GENDER_LIST, genders, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.MARITAL_STATUS_LIST, maritalStatuses, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.LANGUAGE_LIST, languages, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.ROLES_LIST, roles, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.ROLEMASTERLIST, roles, request);

        List<CustomFieldDefinitionEntity> customFieldDefs = new ArrayList<CustomFieldDefinitionEntity>();
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, customFieldDefs, request);

        UserContext userContext = getUserContext(request);
        List<PersonnelStatusEntity> statuses = legacyMasterDao.findMasterDataEntitiesWithLocale(PersonnelStatusEntity.class);
        for (PersonnelStatusEntity personnelStatusEntity : statuses) {
            String messageTextLookup = ApplicationContextProvider.getBean(MessageLookup.class).lookup(personnelStatusEntity.getLookUpValue().getPropertiesKey());
            personnelStatusEntity.setName(messageTextLookup);
        }

        SessionUtils.setCollectionAttribute(PersonnelConstants.STATUS_LIST, statuses, request);

        OfficeBO loggedInOffice = this.officeDao.findOfficeById(userContext.getBranchId());
        List<OfficeDetailsDto> officeHierarchy = new OfficePersistence().getChildOffices(loggedInOffice.getSearchId());
        SessionUtils.setCollectionAttribute(PersonnelConstants.OFFICE_LIST, officeHierarchy, request);

        actionform.setPersonnelId(personnel.getPersonnelId().toString());
        if (personnel.getOffice() != null) {
            actionform.setOfficeId(personnel.getOffice().getOfficeId().toString());
        }
        if (personnel.getTitle() != null) {
            actionform.setTitle(personnel.getTitle().toString());
        }
        if (personnel.getLevel() != null) {
            actionform.setLevel(personnel.getLevelEnum().getValue().toString());
        }
        if (personnel.getStatus() != null) {
            actionform.setStatus(personnel.getStatus().getId().toString());
        }
        actionform.setLoginName(personnel.getUserName());
        actionform.setGlobalPersonnelNum(personnel.getGlobalPersonnelNum());
        actionform.setCustomFields(new ArrayList<CustomFieldDto>());

        if (personnel.getPersonnelDetails() != null) {
            PersonnelDetailsEntity personnelDetails = personnel.getPersonnelDetails();
            actionform.setFirstName(personnelDetails.getName().getFirstName());
            actionform.setMiddleName(personnelDetails.getName().getMiddleName());
            actionform.setSecondLastName(personnelDetails.getName().getSecondLastName());
            actionform.setLastName(personnelDetails.getName().getLastName());
            if (personnelDetails.getGender() != null) {
                actionform.setGender(personnelDetails.getGender().toString());
            }
            if (personnelDetails.getMaritalStatus() != null) {
                actionform.setMaritalStatus(personnelDetails.getMaritalStatus().toString());
            }
            actionform.setAddress(personnelDetails.getAddress());
            if (personnelDetails.getDateOfJoiningMFI() != null) {
                actionform.setDateOfJoiningMFI(DateUtils.makeDateAsSentFromBrowser(personnelDetails
                        .getDateOfJoiningMFI()));
            }
            if (personnelDetails.getDob() != null) {
                actionform.setDob(DateUtils.makeDateAsSentFromBrowser(personnelDetails.getDob()));
            }

        }
        actionform.setEmailId(personnel.getEmailId());
        if (personnel.getPreferredLocale() != null) {
            actionform.setPreferredLocale(personnel.getPreferredLocale());
        }
        List<RoleBO> selectList = new ArrayList<RoleBO>();
        for (PersonnelRoleEntity personnelRole : personnel.getPersonnelRoles()) {
            selectList.add(personnelRole.getRole());
        }
        SessionUtils.setCollectionAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST, selectList, request);

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnel, request);

        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @SuppressWarnings("unchecked")
    @TransactionDemarcate(joinToken = true)
    public ActionForward previewManage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        PersonActionForm actionForm = (PersonActionForm) form;
        boolean addFlag = false;
        List<RoleBO> selectList = new ArrayList<RoleBO>();
        if (actionForm.getPersonnelRoles() != null) {
            List<RoleBO> masterList = (List<RoleBO>) SessionUtils.getAttribute(PersonnelConstants.ROLEMASTERLIST, request);
            for (RoleBO role : masterList) {
                for (String roleId : actionForm.getPersonnelRoles()) {
                    if (roleId != null && role.getId().intValue() == Integer.valueOf(roleId).intValue()) {
                        selectList.add(role);
                        addFlag = true;
                    }
                }
            }
        }
        if (addFlag) {
            SessionUtils.setCollectionAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST, selectList, request);
        } else {
            SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST, null, request);
        }
        return mapping.findForward(ActionForwards.previewManage_success.toString());
    }

    @SuppressWarnings("unused")
    @TransactionDemarcate(joinToken = true)
    public ActionForward previousManage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PersonActionForm personActionForm = (PersonActionForm) form;
        personActionForm.setPersonnelRoles(null);
        return mapping.findForward(ActionForwards.previousManage_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        PersonActionForm actionForm = (PersonActionForm) form;

        try {
            CreateOrUpdatePersonnelInformation perosonnelInfo = translateFormToCreatePersonnelInformationDto(request, actionForm);
            UserDetailDto userDetails = this.personnelServiceFacade.updatePersonnel(perosonnelInfo);

            String globalPersonnelNum = userDetails.getSystemId();
            Name name = new Name(actionForm.getFirstName(), actionForm.getMiddleName(), actionForm.getSecondLastName(), actionForm.getLastName());
            request.setAttribute("displayName", name.getDisplayName());
            request.setAttribute("globalPersonnelNum", globalPersonnelNum);

            return mapping.findForward(ActionForwards.update_success.toString());
        } catch (BusinessRuleException e) {
            throw new PersonnelException(e.getMessageKey(), e.getMessageValues());
        }
    }

    @SuppressWarnings("unused")
    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    @SuppressWarnings("unused")
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String forward = null;
        PersonActionForm actionForm = (PersonActionForm) form;
        String fromPage = actionForm.getInput();
        if (fromPage.equals(PersonnelConstants.MANAGE_USER) || fromPage.equals(PersonnelConstants.UNLOCK_USER)) {
            forward = ActionForwards.cancelEdit_success.toString();
        } else {
            forward = ActionForwards.cancel_success.toString();
        }
        return mapping.findForward(forward.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        PersonActionForm personActionForm = (PersonActionForm) form;
        String globalId = request.getParameter("globalPersonnelNum");
        if (globalId == null) {
            globalId = personActionForm.getGlobalPersonnelNum();
        }

        PersonnelInformationDto personnelInformationDto = this.personnelServiceFacade.getPersonnelInformationDto(null, globalId);

        SessionUtils.removeThenSetAttribute("personnelInformationDto", personnelInformationDto, request);

        // John W - for other actions downstream (like edit) business_key set (until all actions refactored)
        PersonnelBO personnelBO = this.personnelDao.findPersonnelById(personnelInformationDto.getPersonnelId());
        SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, personnelBO, request);

        String url = String.format("PersonAction.do?globalPersonnelNum=%s", personnelBO.getGlobalPersonnelNum());
        String encodedUrl = URLEncoder.encode(url, "UTF-8");
        SessionUtils.removeThenSetAttribute("currentPageUrl", encodedUrl, request);


        List<ValueListElement> titles = this.customerDao.retrieveTitles();
        List<ValueListElement> genders = this.customerDao.retrieveGenders();
        List<ValueListElement> maritalStatuses = this.customerDao.retrieveMaritalStatuses();
        List<ValueListElement> languages = Localization.getInstance().getLocaleForUI();

        List<RoleBO> roles = legacyRolesPermissionsDao.getRoles();
        List<PersonnelLevelEntity> personnelLevels = this.customerDao.retrievePersonnelLevels();
        for (PersonnelLevelEntity personnelLevelEntity : personnelLevels) {
            String messageTextLookup = ApplicationContextProvider.getBean(MessageLookup.class).lookup(personnelLevelEntity.getLookUpValue().getPropertiesKey());
            personnelLevelEntity.setName(messageTextLookup);
        }

        SessionUtils.setCollectionAttribute(PersonnelConstants.TITLE_LIST, titles, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, personnelLevels, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.GENDER_LIST, genders, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.MARITAL_STATUS_LIST, maritalStatuses, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.LANGUAGE_LIST, languages, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.ROLES_LIST, roles, request);
        SessionUtils.setCollectionAttribute(PersonnelConstants.ROLEMASTERLIST, roles, request);

        List<CustomFieldDefinitionEntity> customFieldDefs1 = new ArrayList<CustomFieldDefinitionEntity>();
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, customFieldDefs1, request);

        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        personActionForm.setCustomFields(customFields);
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @SuppressWarnings("unused")
    @TransactionDemarcate(joinToken = true)
    public ActionForward loadUnLockUser(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SessionUtils.setAttribute(PersonnelConstants.LOGIN_ATTEMPTS_COUNT, LoginConstants.MAXTRIES, request);
        return mapping.findForward(ActionForwards.loadUnLockUser_success.toString());
    }

    @SuppressWarnings("unused")
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward unLockUserAccount(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PersonnelBO personnel = (PersonnelBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        UserContext userContext = getUserContext(request);

        this.personnelServiceFacade.unLockUserAccount(personnel.getGlobalPersonnelNum());

        return mapping.findForward(ActionForwards.unLockUserAccount_success.toString());
    }

    @Override
    @TransactionDemarcate(saveToken = true)
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ActionForward actionForward = null;
        UserContext userContext = getUserContext(request);
        PersonnelBO personnel = this.personnelDao.findPersonnelById(userContext.getId());
        String searchString = ((PersonActionForm) form).getSearchString();
        addSeachValues(searchString, personnel.getOffice().getOfficeId().toString(), personnel.getOffice()
                .getOfficeName(), request);
        searchString = org.mifos.framework.util.helpers.SearchUtils.normalizeSearchString(searchString);
        actionForward = super.search(mapping, form, request, response);
        SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, legacyPersonnelDao.search(searchString,
                userContext.getId()), request);
        return actionForward;
    }

    @SuppressWarnings("unused")
    @TransactionDemarcate(saveToken = true)
    public ActionForward loadSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SessionUtils.setRemovableAttribute("TableCache", null, TableTagConstants.PATH, request.getSession());
        SessionUtils.removeAttribute(Constants.SEARCH_RESULTS, request);
        return mapping.findForward(ActionForwards.search_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward captureQuestionResponses(
            final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "captureQuestionResponses");
        ActionErrors errors = createGroupQuestionnaire.validateResponses(request, (PersonActionForm) form);
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
        return createGroupQuestionnaire.editResponses(mapping, request, (PersonActionForm) form);
    }
}