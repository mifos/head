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

package org.mifos.application.servicefacade;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.config.ClientRules;
import org.mifos.config.Localization;
import org.mifos.config.SitePreferenceType;
import org.mifos.config.persistence.ApplicationConfigurationDao;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelCustomFieldEntity;
import org.mifos.customers.personnel.business.PersonnelDetailsEntity;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;
import org.mifos.customers.personnel.business.PersonnelNotesEntity;
import org.mifos.customers.personnel.business.PersonnelRoleEntity;
import org.mifos.customers.personnel.business.PersonnelStatusEntity;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CenterDescriptionDto;
import org.mifos.dto.domain.ClientDescriptionDto;
import org.mifos.dto.domain.CreateOrUpdatePersonnelInformation;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerHierarchyDto;
import org.mifos.dto.domain.GroupDescriptionDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.dto.domain.UserSearchDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.DefinePersonnelDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.PersonnelDetailsDto;
import org.mifos.dto.screen.PersonnelInformationDto;
import org.mifos.dto.screen.PersonnelNoteDto;
import org.mifos.dto.screen.SystemUserSearchResultsDto;
import org.mifos.dto.screen.UserSettingsDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.MifosUser;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class PersonnelServiceFacadeWebTier implements PersonnelServiceFacade {

    private final OfficeDao officeDao;
    private final CustomerDao customerDao;
    private final PersonnelDao personnelDao;
    private final LegacyRolesPermissionsDao rolesPermissionsPersistence;
    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    @Autowired
    private LegacyMasterDao legacyMasterDao;

    @Autowired
    private LegacyPersonnelDao legacyPersonnelDao;

    @Autowired
    public PersonnelServiceFacadeWebTier(OfficeDao officeDao, CustomerDao customerDao, PersonnelDao personnelDao, ApplicationConfigurationDao applicationConfigurationDao, LegacyRolesPermissionsDao rolesPermissionsPersistence) {
        super();
        this.officeDao = officeDao;
        this.customerDao = customerDao;
        this.personnelDao = personnelDao;
        this.rolesPermissionsPersistence = rolesPermissionsPersistence;
    }

    @Override
    public SystemUserSearchResultsDto searchUser(UserSearchDto searchDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return this.personnelDao.search(searchDto, user);
    }

    @Override
    public DefinePersonnelDto retrieveInfoForNewUserDefinition(Short officeId) {
        String officeName = "";
        if (officeId != null) {
            OfficeBO office = officeDao.findOfficeById(officeId);
            officeName = office.getOfficeName();
        }

        List<ValueListElement> titles = customerDao.retrieveTitles();
        List<ListElement> titleList = new ArrayList<ListElement>();
        for (ValueListElement element : titles) {
            ListElement listElement = new ListElement(element.getId(), element.getName());
            titleList.add(listElement);
        }

        List<PersonnelLevelEntity> personnelLevels = customerDao.retrievePersonnelLevels();
        List<ListElement> personnelLevelList = new ArrayList<ListElement>();
        for (PersonnelLevelEntity level : personnelLevels) {
            String name = level.getLookUpValue().getLookUpName();
            String localisedName = ApplicationContextProvider.getBean(MessageLookup.class).lookup(name);
            ListElement listElement = new ListElement(new Integer(level.getId()), localisedName);
            personnelLevelList.add(listElement);
        }

        List<ValueListElement> genders = customerDao.retrieveGenders();
        List<ListElement> genderList = new ArrayList<ListElement>();
        for (ValueListElement element : genders) {
            ListElement listElement = new ListElement(element.getId(), element.getName());
            genderList.add(listElement);
        }

        List<ValueListElement> maritalStatuses = customerDao.retrieveMaritalStatuses();
        List<ListElement> maritalStatusList = new ArrayList<ListElement>();
        for (ValueListElement element : maritalStatuses) {
            ListElement listElement = new ListElement(element.getId(), element.getName());
            maritalStatusList.add(listElement);
        }

        List<RoleBO> roles = new ArrayList<RoleBO>();
        try {
            roles = rolesPermissionsPersistence.getRoles();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }

        List<ListElement> roleList = new ArrayList<ListElement>();
        for (RoleBO element : roles) {
            ListElement listElement = new ListElement(new Integer(element.getId()), element.getName());
            roleList.add(listElement);
        }

        List<ListElement> languageList = Localization.getInstance().getLocaleList();

        DefinePersonnelDto defineUserDto = new DefinePersonnelDto(officeName, titleList, personnelLevelList,
                genderList, maritalStatusList, languageList, roleList);
        return defineUserDto;
    }

    @Override
    public PersonnelInformationDto getPersonnelInformationDto(final Long userId, final String globalNumber) {

        PersonnelBO personnel = null;
        if (userId != null) {
            personnel = personnelDao.findPersonnelById(userId.shortValue());
        } else {
            personnel = personnelDao.findByGlobalPersonnelNum(globalNumber);
        }
        if (personnel == null) {
            throw new MifosRuntimeException("personnel not found for id" + userId);
        }

        String displayName = personnel.getDisplayName();
        PersonnelStatusEntity personnelStatus = personnel.getStatus();
        String statusName = ApplicationContextProvider.getBean(MessageLookup.class).lookup(personnelStatus.getLookUpValue());
        ListElement status = new ListElement(new Integer(personnelStatus.getId()), statusName);
        boolean locked = personnel.isLocked();
        PersonnelDetailsEntity personnelDetailsEntity = personnel.getPersonnelDetails();
        Address address = personnelDetailsEntity.getAddress();
        AddressDto addressDto = new AddressDto(address.getLine1(), address.getLine2(), address.getLine3(), address
                .getCity(), address.getState(), address.getCountry(), address.getZip(), address.getPhoneNumber());

        Name name = personnelDetailsEntity.getName();
        PersonnelDetailsDto personnelDetails = new PersonnelDetailsDto(personnelDetailsEntity.getGovernmentIdNumber(),
                new DateTime(personnelDetailsEntity.getDob()).toDateMidnight().toDateTime(), personnelDetailsEntity.getMaritalStatus(),
                personnelDetailsEntity.getGender(), new DateTime(personnelDetailsEntity.getDateOfJoiningMFI()).toDateMidnight().toDateTime(),
                new DateTime(personnelDetailsEntity.getDateOfJoiningBranch()).toDateMidnight().toDateTime(), new DateTime(personnelDetailsEntity
                        .getDateOfLeavingBranch()).toDateMidnight().toDateTime(), addressDto, name.getFirstName(), name.getMiddleName(), name
                        .getSecondLastName(), name.getLastName());

        String emailId = personnel.getEmailId();

        Short preferredLocale = personnel.getPreferredLocale();
        String languageName = Localization.getInstance().getDisplayName(preferredLocale);

        if (preferredLocale != null) {
            languageName = Localization.getInstance().getDisplayName(preferredLocale);
        }
        PersonnelLevelEntity level = personnel.getLevel();
        OfficeBO office = personnel.getOffice();
        Integer title = personnel.getTitle();
        Set<PersonnelRoleEntity> personnelRoleEntities = personnel.getPersonnelRoles();
        Set<ListElement> personnelRoles = new LinkedHashSet<ListElement>();
        for (PersonnelRoleEntity entity : personnelRoleEntities) {
            ListElement element = new ListElement(entity.getRole().getId().intValue(), entity.getRole().getName());
            personnelRoles.add(element);
        }

        Short personnelId = personnel.getPersonnelId();
        String userName = personnel.getUserName();
        Set<PersonnelCustomFieldEntity> personnelCustomFields = personnel.getCustomFields();
        Set<CustomFieldDto> customFields = new LinkedHashSet<CustomFieldDto>();

        for (PersonnelCustomFieldEntity fieldDef : personnelCustomFields) {
            customFields.add(new CustomFieldDto(fieldDef.getFieldId(), fieldDef.getFieldValue()));
        }

        Set<PersonnelNotesEntity> personnelNotesEntity = personnel.getPersonnelNotes();
        Set<PersonnelNoteDto> personnelNotes = new LinkedHashSet<PersonnelNoteDto>();
        for (PersonnelNotesEntity entity : personnelNotesEntity) {
            personnelNotes.add(new PersonnelNoteDto(new DateTime(entity.getCommentDate()), entity.getComment(), entity
                    .getPersonnelName()));
        }

        return new PersonnelInformationDto(personnel.getPersonnelId().intValue(), personnel.getGlobalPersonnelNum(), displayName, status, locked,
                personnelDetails, emailId, languageName, preferredLocale.intValue(), level.getId(), office.getOfficeId().intValue(), office
                        .getOfficeName(), title, personnelRoles, personnelId, userName, customFields, personnelNotes);
    }

    @Override
    public UserDetailDto createPersonnelInformation(CreateOrUpdatePersonnelInformation personnel) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {

            PersonnelBusinessService personnelBusinessService = new PersonnelBusinessService();
            List<RoleBO> roles = new ArrayList<RoleBO>();

            for (ListElement element : personnel.getRoles()) {
                RoleBO role = personnelBusinessService.getRoleById(new Short(element.getId().shortValue()));
                roles.add(role);
            }

            AddressDto addressDto = personnel.getAddress();
            Address address = new Address(addressDto.getLine1(), addressDto.getLine2(), addressDto.getLine3(),
                    addressDto.getCity(), addressDto.getState(), addressDto.getCountry(), addressDto.getZip(),
                    addressDto.getPhoneNumber());

            OfficeBO office = officeDao.findOfficeById(personnel.getOfficeId());

            Name name = new Name(personnel.getFirstName(), personnel.getMiddleName(), personnel.getSecondLastName(), personnel.getLastName());
            verifyFields(personnel.getUserName(), personnel.getGovernmentIdNumber(), personnel.getDob().toDate(), name.getDisplayName());

            PersonnelBO newPersonnel = new PersonnelBO(PersonnelLevel.fromInt(personnel.getPersonnelLevelId()
                    .intValue()), office, personnel.getTitle(), personnel.getPreferredLocale(),
                    personnel.getPassword(), personnel.getUserName(), personnel.getEmailId(), roles, personnel
                            .getCustomFields(), name, personnel.getGovernmentIdNumber(),
                    personnel.getDob().toDate(), personnel.getMaritalStatus(), personnel.getGender(), personnel
                            .getDateOfJoiningMFI().toDate(), personnel.getDateOfJoiningBranch().toDate(), address,
                    Integer.valueOf(user.getUserId()).shortValue());

            transactionHelper.startTransaction();
            this.personnelDao.save(newPersonnel);
            transactionHelper.flushSession();
            newPersonnel.generateGlobalPersonnelNum();
            this.personnelDao.save(newPersonnel);
            transactionHelper.commitTransaction();

            return newPersonnel.toDto();

        } catch (PersistenceException e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (ValidationException e) {
            transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (ServiceException e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    private void verifyFields(final String userName, final String governmentIdNumber, final java.util.Date dob, final String displayName)
            throws ValidationException, PersistenceException {
        if (StringUtils.isBlank(userName)) {
            throw new ValidationException(PersonnelConstants.ERRORMANDATORY);
        }
        if (legacyPersonnelDao.isUserExist(userName)) {
            throw new ValidationException(PersonnelConstants.DUPLICATE_USER, new Object[] { userName });

        }
        if (StringUtils.isNotBlank(governmentIdNumber)) {
            if (legacyPersonnelDao.isUserExistWithGovernmentId(governmentIdNumber)) {
                throw new ValidationException(PersonnelConstants.DUPLICATE_GOVT_ID, new Object[] { governmentIdNumber });
            }
        } else {
            if (legacyPersonnelDao.isUserExist(displayName, dob)) {
                throw new ValidationException(PersonnelConstants.DUPLICATE_USER_NAME_OR_DOB,
                        new Object[] { displayName });
            }
        }
    }


    @Override
    public UserDetailDto updatePersonnel(CreateOrUpdatePersonnelInformation personnel) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        PersonnelBO userForUpdate = this.personnelDao.findPersonnelById(personnel.getId().shortValue());
        userForUpdate.updateDetails(userContext);

        AddressDto addressDto = personnel.getAddress();

        Address address = new Address(addressDto.getLine1(), addressDto.getLine2(), addressDto.getLine3(), addressDto
                .getCity(), addressDto.getState(), addressDto.getCountry(), addressDto.getZip(), addressDto
                .getPhoneNumber());

        PersonnelStatus status = PersonnelStatus.getPersonnelStatus(personnel.getPersonnelStatusId());
        PersonnelLevel userHierarchyLevel = PersonnelLevel.fromInt(personnel.getPersonnelLevelId().intValue());
        OfficeBO newOffice = this.officeDao.findOfficeById(personnel.getOfficeId());

        validateForUpdate(userForUpdate, status, newOffice, userHierarchyLevel);

        List<RoleBO> selectedRoles = new ArrayList<RoleBO>();
        try {
            List<RoleBO> allRoles = new PersonnelBusinessService().getRoles();
            for (RoleBO role : allRoles) {
                if (isRoleSelected(role, personnel.getRoles())) {
                    selectedRoles.add(role);
                }
            }

            PersonnelStatusEntity personnelStatus = legacyMasterDao
                    .getPersistentObject(PersonnelStatusEntity.class, status.getValue());

            PersonnelLevelEntity personnelLevel = legacyMasterDao.getPersistentObject(
                    PersonnelLevelEntity.class, userHierarchyLevel.getValue());

            Short preferredLocaleId = personnel.getPreferredLocale();


            transactionHelper.startTransaction();
            transactionHelper.beginAuditLoggingFor(userForUpdate);

            userForUpdate.updateUserDetails(personnel.getFirstName(), personnel.getMiddleName(), personnel
                    .getSecondLastName(), personnel.getLastName(), personnel.getEmailId(), personnel.getGender(),
                    personnel.getMaritalStatus(), preferredLocaleId, personnelStatus, address, personnel.getTitle(),
                    personnelLevel, selectedRoles, personnel.getPassword(), newOffice);
            userForUpdate.getPersonnelDetails().setDob(personnel.getDob().toDate());

            this.personnelDao.save(userForUpdate);
            transactionHelper.commitTransaction();

            return userForUpdate.toDto();
        } catch (PersistenceException e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    private void validateForUpdate(PersonnelBO oldUserDetails, final PersonnelStatus newStatus, final OfficeBO newOffice, final PersonnelLevel newLevel) {

        if (oldUserDetails.isLevelDifferent(newLevel)) {
            validateUserHierarchyChange(oldUserDetails, newLevel, newOffice);
        }

        if (oldUserDetails.isOfficeDifferent(newOffice)) {
            validateOfficeTransfer(oldUserDetails, newOffice, newLevel);
        }

        if (oldUserDetails.isLevelDifferent(newLevel)) {
            validateStatusChange(oldUserDetails, newStatus, newLevel, newOffice);
        }
    }

    private void validateStatusChange(PersonnelBO oldUserDetails, final PersonnelStatus newStatus, final PersonnelLevel newLevel, final OfficeBO newOffice) {

        try {
            if (oldUserDetails.isActive() && newStatus.equals(PersonnelStatus.INACTIVE) && newLevel.equals(PersonnelLevel.LOAN_OFFICER)) {
                if (legacyPersonnelDao.getActiveChildrenForLoanOfficer(oldUserDetails.getPersonnelId(), oldUserDetails.getOffice().getOfficeId())) {
                    throw new BusinessRuleException(PersonnelConstants.STATUS_CHANGE_EXCEPTION);
                }
            } else if (oldUserDetails.isInActive() && newStatus.equals(PersonnelStatus.ACTIVE) && !newOffice.isActive()) {
                Object values[] = new Object[1];
                values[0] = newOffice.getOfficeId();
                throw new BusinessRuleException(PersonnelConstants.INACTIVE_BRANCH, values);
            }
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void validateOfficeTransfer(PersonnelBO oldUserDetails, final OfficeBO newOffice, final PersonnelLevel newLevel) {
        try {
            if (newLevel.equals(PersonnelLevel.LOAN_OFFICER)) {

                if (newOffice.isNotBranch()) {
                    Object values[] = new Object[1];
                    values[0] = oldUserDetails.getGlobalPersonnelNum();
                    throw new BusinessRuleException(PersonnelConstants.LO_ONLY_IN_BRANCHES, values);
                }
            }

            if (legacyPersonnelDao.getActiveChildrenForLoanOfficer(oldUserDetails.getPersonnelId(), oldUserDetails.getOffice().getOfficeId())) {
                Object values[] = new Object[1];
                values[0] = oldUserDetails.getGlobalPersonnelNum();
                throw new BusinessRuleException(PersonnelConstants.TRANSFER_NOT_POSSIBLE_EXCEPTION, values);
            }
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void validateUserHierarchyChange(PersonnelBO oldUserDetails, final PersonnelLevel newLevel, final OfficeBO newOffice) {
        try {
            if (oldUserDetails.isLoanOfficer() && newLevel.equals(PersonnelLevel.NON_LOAN_OFFICER)) {

                if (legacyPersonnelDao.getAllChildrenForLoanOfficer(oldUserDetails.getPersonnelId(), oldUserDetails.getOffice().getOfficeId())) {
                    throw new BusinessRuleException(PersonnelConstants.HIERARCHY_CHANGE_EXCEPTION);
                }
            } else if (oldUserDetails.isNonLoanOfficer()
                    && newLevel.equals(PersonnelLevel.LOAN_OFFICER)
                    && newOffice.isNotBranch()) {

                Object values[] = new Object[1];
                values[0] = oldUserDetails.getGlobalPersonnelNum();
                throw new BusinessRuleException(PersonnelConstants.LO_ONLY_IN_BRANCHES, values);
            }
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }

    }

    private boolean isRoleSelected(RoleBO role, List<ListElement> roles) {
        boolean found = false;

        for (ListElement item : roles) {
            if (item.getId().shortValue() == role.getId().shortValue()) {
                found = true;
            }
        }

        return found;
    }

    @Override
    public UserSettingsDto retrieveUserSettings() {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        PersonnelBO personnel = this.personnelDao.findPersonnelById(userContext.getId());

        String gender = getNameForBusinessActivityEntity(personnel.getPersonnelDetails().getGender());
        String martialStatus = getNameForBusinessActivityEntity(personnel.getPersonnelDetails().getMaritalStatus());
        String language = Localization.getInstance().getDisplayName(personnel.getPreferredLocale());
        String sitePreference = SitePreferenceType.getSitePreference(personnel.getSitePreference()).name();

        List<ValueListElement> genders = this.customerDao.retrieveGenders();
        List<ValueListElement> martialStatuses = this.customerDao.retrieveMaritalStatuses();
        List<ValueListElement> languages = Localization.getInstance().getLocaleForUI();
        List<ValueListElement> sitePreferenceTypes = new ArrayList<ValueListElement>();

        for ( short i = 0; i < SitePreferenceType.values().length; i++ ){
            SitePreferenceType sitePreferenceType = SitePreferenceType.values()[i];
            ValueListElement valueListElement = new BusinessActivityEntity(sitePreferenceType.getValue().intValue(),
                    sitePreferenceType.name(), sitePreferenceType.name());
            sitePreferenceTypes.add(valueListElement);
        }

        int age = DateUtils.DateDiffInYears(((Date) personnel.getPersonnelDetails().getDob()));
        if (age < 0) {
            age = 0;
        }

        return new UserSettingsDto(gender, martialStatus, language, age, sitePreference, genders, martialStatuses, languages, sitePreferenceTypes);
    }

    private String getNameForBusinessActivityEntity(Integer entityId) {
        try {
            String value = "";
            if (entityId != null) {
                 value = legacyMasterDao.getMessageForLookupEntity(entityId);
            }
            return value;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public UserSettingsDto retrieveUserSettings(Integer genderId, Integer maritalStatusId, Integer localeId, Short sitePreferenceId) {

        String gender = getNameForBusinessActivityEntity(genderId);
        String martialStatus = getNameForBusinessActivityEntity(maritalStatusId);
        String language = (localeId != null) ? Localization.getInstance().getDisplayName(localeId.shortValue()) : "";
        String sitePreference = SitePreferenceType.getSitePreference(sitePreferenceId).name();

        int age = 0;
        List<ValueListElement> empty = new ArrayList<ValueListElement>();
        return new UserSettingsDto(gender, martialStatus, language, age, sitePreference, empty, empty, empty, empty);
    }

    @Override
    public UserDetailDto retrieveUserInformation(Short personnelId) {

        PersonnelBO personnel = this.personnelDao.findPersonnelById(personnelId);
        return personnel.toDto();
    }

    @Override
    public void updateUserSettings(Short personnelId, String emailId, Name name, Integer maritalStatusValue, Integer genderValue,
            AddressDto address, Short preferredLocale, Short sitePreference) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        PersonnelBO personnel = this.personnelDao.findPersonnelById(personnelId);

        try {
            personnel.updateDetails(userContext);
            this.transactionHelper.startTransaction();
            this.transactionHelper.beginAuditLoggingFor(personnel);

            Address theAddress = null;
            if (address != null) {
                theAddress = new Address(address.getLine1(), address.getLine2(), address.getLine3(), address.getCity(),
                        address.getState(), address.getCountry(), address.getZip(), address.getPhoneNumber());
            }

            personnel.update(emailId, name, maritalStatusValue, genderValue, theAddress, preferredLocale, sitePreference);

            if (preferredLocale != null && preferredLocale != 0) {
                user.setPreferredLocaleId(preferredLocale);
            }

            this.transactionHelper.commitTransaction();
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public void unLockUserAccount(String globalAccountNum) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        PersonnelBO personnel = this.personnelDao.findByGlobalPersonnelNum(globalAccountNum);
        personnel.updateDetails(userContext);
        try {
            this.transactionHelper.startTransaction();
            personnel.unlockPersonnel();
            this.personnelDao.save(personnel);
            this.transactionHelper.commitTransaction();
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public Locale getUserPreferredLocale() {
        return getUserPreferredLocale(null);
    }

    @Override
    public Locale getUserPreferredLocale(@SuppressWarnings("unused") HttpServletRequest request) {
        if(SecurityContextHolder.getContext() != null) {
            if(SecurityContextHolder.getContext().getAuthentication() != null) {
                if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
                    MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    return Localization.getInstance().getLocaleById(user.getPreferredLocaleId());
                }
            } else {
                if (request != null) {
                    try {
                        UserContext userContext = (UserContext) request.getSession().getAttribute(LoginConstants.USERCONTEXT);
                        return Localization.getInstance().getLocaleById(userContext.getLocaleId());
                    } catch (Exception e) { /* do nothing and return system default locale */
                    }
                }
            }
        }
        return Localization.getInstance().getConfiguredLocale();
    }

    @Override
    public Short changeUserLocale(Short id, HttpServletRequest request) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (id != null) {
            Short newLocaleId = id;
            if(Localization.getInstance().getLocaleById(newLocaleId) != null) {
                user.setPreferredLocaleId(newLocaleId);
                try {
                    this.transactionHelper.startTransaction();
                    PersonnelBO p = this.personnelDao.findPersonnelById((short) user.getUserId());
                    p.setPreferredLocale(newLocaleId);
                    this.personnelDao.update(p);
                    this.transactionHelper.commitTransaction();
                    UserContext userContext = (UserContext) request.getSession().getAttribute(LoginConstants.USERCONTEXT);
                    userContext.setLocaleId(newLocaleId);
                } catch (Exception e) {
                    this.transactionHelper.rollbackTransaction();
                    throw new MifosRuntimeException(e);
                }
                ApplicationContextProvider.getBean(MessageLookup.class).updateLabelCache();
            }
        }
        return user.getPreferredLocaleId();
    }

    @Override
    public List<ValueListElement> getDisplayLocaleList() {
        return Localization.getInstance().getLocaleForUI();
    }

    @Override
    public CustomerHierarchyDto getLoanOfficerCustomersHierarchyForDay(Short loanOfficerId, DateTime day) {
        PersonnelBO personnel = personnelDao.findPersonnelById(loanOfficerId);
        CustomerHierarchyDto hierarchy = new CustomerHierarchyDto();
        // Yesterday as current date because upcoming meetings should include current day
        DateTime currentDate = new DateTime().minusDays(1);

        try {
            if (ClientRules.getCenterHierarchyExists()) {
                for (CustomerDetailDto center : this.customerDao.findActiveCentersUnderUser(personnel)){
                    CustomerBO centerBO = this.customerDao.findCustomerById(center.getCustomerId());
                    DateTime nextMeeting = new DateTime(centerBO.getCustomerMeetingValue().getNextScheduleDateAfterRecurrenceWithoutAdjustment(currentDate.toDate()));
                    if ( Days.daysBetween(day, nextMeeting).getDays() == 0 ){
                        CenterDescriptionDto centerDescription = new CenterDescriptionDto();
                        centerDescription.setId(center.getCustomerId());
                        centerDescription.setDisplayName(center.getDisplayName());
                        centerDescription.setGlobalCustNum(center.getGlobalCustNum());
                        centerDescription.setSearchId(center.getSearchId());
                        hierarchy.getCenters().add(centerDescription);
                    }
                }
            }

            allGroups:
            for (CustomerDetailDto group : this.customerDao.findGroupsUnderUser(personnel)){
                CustomerBO groupBO = this.customerDao.findCustomerById(group.getCustomerId());
                DateTime nextMeeting = new DateTime(groupBO.getCustomerMeetingValue().getNextScheduleDateAfterRecurrenceWithoutAdjustment(currentDate.toDate()));
                if ( Days.daysBetween(day, nextMeeting).getDays() == 0 ){
                    GroupDescriptionDto groupDescription = new GroupDescriptionDto();
                    groupDescription.setId(group.getCustomerId());
                    groupDescription.setDisplayName(group.getDisplayName());
                    groupDescription.setGlobalCustNum(group.getGlobalCustNum());
                    groupDescription.setSearchId(group.getSearchId());

                    for (ClientBO client : this.customerDao.findActiveClientsUnderParent(group.getSearchId(), personnel.getOffice().getOfficeId())) {
                        ClientDescriptionDto clientDescription = new ClientDescriptionDto();
                        clientDescription.setId(client.getCustomerId());
                        clientDescription.setDisplayName(client.getDisplayName());
                        clientDescription.setGlobalCustNum(client.getGlobalCustNum());
                        clientDescription.setSearchId(client.getSearchId());
                        groupDescription.getClients().add(clientDescription);
                    }

                    for (CenterDescriptionDto center : hierarchy.getCenters()) {
                        if (group.getSearchId().startsWith(center.getSearchId())) {
                            center.getGroups().add(groupDescription);
                            continue allGroups;
                        }
                    }
                    hierarchy.getGroups().add(groupDescription);
                }
            }
        } catch (MeetingException e) {
            e.printStackTrace();
        }

        return hierarchy;
    }

    @Override
    public List<PersonnelDto> retrieveActiveLoanOfficersUnderOffice(Short officeId) {
        List<PersonnelBO> personnelList;
        try {
            personnelList = legacyPersonnelDao.getActiveLoanOfficersUnderOffice(officeId);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }

        List<PersonnelDto> personnelDtoList = new ArrayList<PersonnelDto>();
        for (PersonnelBO personnelBO : personnelList){
            personnelDtoList.add(new PersonnelDto(personnelBO.getPersonnelId(), personnelBO.getDisplayName()));
        }

        return personnelDtoList;
    }

    @Override
    public SitePreferenceType retrieveSitePreference(Integer userId) {
        PersonnelBO personnelBO = personnelDao.findPersonnelById(userId.shortValue());
        return SitePreferenceType.getSitePreference(personnelBO.getSitePreference());
    }

}