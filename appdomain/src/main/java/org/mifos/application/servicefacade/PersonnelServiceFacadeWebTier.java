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

package org.mifos.application.servicefacade;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.config.Localization;
import org.mifos.config.persistence.ApplicationConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
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
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CreateOrUpdatePersonnelInformation;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.OfficeDto;
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
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;

public class PersonnelServiceFacadeWebTier implements PersonnelServiceFacade {

    private final OfficeDao officeDao;
    private final CustomerDao customerDao;
    private final PersonnelDao personnelDao;
    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    public PersonnelServiceFacadeWebTier(OfficeDao officeDao, CustomerDao customerDao, PersonnelDao personnelDao) {
        super();
        this.officeDao = officeDao;
        this.customerDao = customerDao;
        this.personnelDao = personnelDao;
    }

    @Override
    public SystemUserSearchResultsDto searchUser(UserSearchDto searchDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return this.personnelDao.search(searchDto, user);
    }

    @Override
    public DefinePersonnelDto retrieveInfoForNewUserDefinition(Short officeId, Locale preferredLocale) {
        OfficeDto officeDto;
        String officeName;
        if (officeId != null) {
            officeDto = officeDao.findOfficeDtoById(officeId);
            officeName = officeDto.getLookupNameKey();
        } else {
            officeDto = null;
            officeName = null;
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
            // if (officeDto.getLevelId().equals(OfficeLevel.BRANCHOFFICE.getValue()) &&
            // !level.getId().equals(PersonnelLevel.LOAN_OFFICER.getValue())) {
            String name = level.getLookUpValue().getLookUpName();
            String localisedName = MessageLookup.getInstance().lookup(name);
            ListElement listElement = new ListElement(new Integer(level.getId()), localisedName);
            personnelLevelList.add(listElement);
            // }
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

        List<ValueListElement> languages = customerDao.retrieveLanguages();
        List<ListElement> languageList = new ArrayList<ListElement>();
        for (ValueListElement element : languages) {
            ListElement listElement = new ListElement(element.getId(), element.getName());
            languageList.add(listElement);
        }

        List<RoleBO> roles;
        try {
            roles = new PersonnelBusinessService().getRoles();
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }

        List<ListElement> roleList = new ArrayList<ListElement>();
        for (RoleBO element : roles) {
            ListElement listElement = new ListElement(new Integer(element.getId()), element.getName());
            roleList.add(listElement);
        }

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
        String statusName = MessageLookup.getInstance().lookup(personnelStatus.getLookUpValue());
        ListElement status = new ListElement(new Integer(personnelStatus.getId()), statusName);
        boolean locked = personnel.isLocked();
        PersonnelDetailsEntity personnelDetailsEntity = personnel.getPersonnelDetails();
        Address address = personnelDetailsEntity.getAddress();
        AddressDto addressDto = new AddressDto(address.getLine1(), address.getLine2(), address.getLine3(), address
                .getCity(), address.getState(), address.getCountry(), address.getZip(), address.getPhoneNumber());

        Name name = personnelDetailsEntity.getName();
        PersonnelDetailsDto personnelDetails = new PersonnelDetailsDto(personnelDetailsEntity.getGovernmentIdNumber(),
                new DateTime(personnelDetailsEntity.getDob()), personnelDetailsEntity.getMaritalStatus(),
                personnelDetailsEntity.getGender(), new DateTime(personnelDetailsEntity.getDateOfJoiningMFI()),
                new DateTime(personnelDetailsEntity.getDateOfJoiningBranch()), new DateTime(personnelDetailsEntity
                        .getDateOfLeavingBranch()), addressDto, name.getFirstName(), name.getMiddleName(), name
                        .getSecondLastName(), name.getLastName());

        String emailId = personnel.getEmailId();

        SupportedLocalesEntity preferredLocale = personnel.getPreferredLocale();
        String languageName = Localization.getInstance().getLanguageName();
        Integer languageLookUpId = Integer.valueOf(601); // french

        if (preferredLocale == null) {
            // strange - locale for user doesn't exist in supported locales..
//            defaultLocale = Localization.getInstance().getMainLocale();
        } else {
            languageName = preferredLocale.getLanguageName();
            languageLookUpId = preferredLocale.getLanguage().getLookUpValue().getLookUpId();
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
                personnelDetails, emailId, languageName, languageLookUpId, level.getId(), office.getOfficeId().intValue(), office
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

        PersonnelPersistence persistence = new PersonnelPersistence();
        if (StringUtils.isBlank(userName)) {
            throw new ValidationException(PersonnelConstants.ERRORMANDATORY);
        }
        if (persistence.isUserExist(userName)) {
            throw new ValidationException(PersonnelConstants.DUPLICATE_USER, new Object[] { userName });

        }
        if (StringUtils.isNotBlank(governmentIdNumber)) {
            if (persistence.isUserExistWithGovernmentId(governmentIdNumber)) {
                throw new ValidationException(PersonnelConstants.DUPLICATE_GOVT_ID, new Object[] { governmentIdNumber });
            }
        } else {
            if (persistence.isUserExist(displayName, dob)) {
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

            PersonnelStatusEntity personnelStatus = (PersonnelStatusEntity) new MasterPersistence()
                    .getPersistentObject(PersonnelStatusEntity.class, status.getValue());

            PersonnelLevelEntity personnelLevel = (PersonnelLevelEntity) new MasterPersistence().getPersistentObject(
                    PersonnelLevelEntity.class, userHierarchyLevel.getValue());

            Short preferredLocaleId = Localization.getInstance().getLocaleId();
            List<SupportedLocalesEntity> allLocales = new ApplicationConfigurationPersistence().getSupportedLocale();
            for (SupportedLocalesEntity locale : allLocales) {
                if (personnel.getPreferredLocale() != null
                        && locale.getLanguage().getLookUpValue().getLookUpId() == personnel.getPreferredLocale()
                                .intValue()) {
                    preferredLocaleId = locale.getLocaleId();
                }
            }

            transactionHelper.startTransaction();
            transactionHelper.beginAuditLoggingFor(userForUpdate);

            userForUpdate.updateUserDetails(personnel.getFirstName(), personnel.getMiddleName(), personnel
                    .getSecondLastName(), personnel.getLastName(), personnel.getEmailId(), personnel.getGender(),
                    personnel.getMaritalStatus(), preferredLocaleId, personnelStatus, address, personnel.getTitle(),
                    personnelLevel, selectedRoles, personnel.getPassword());

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
                if (new PersonnelPersistence().getActiveChildrenForLoanOfficer(oldUserDetails.getPersonnelId(), oldUserDetails.getOffice().getOfficeId())) {
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

            if (new PersonnelPersistence().getActiveChildrenForLoanOfficer(oldUserDetails.getPersonnelId(), oldUserDetails.getOffice().getOfficeId())) {
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

                if (new PersonnelPersistence().getAllChildrenForLoanOfficer(oldUserDetails.getPersonnelId(), oldUserDetails.getOffice().getOfficeId())) {
                    throw new BusinessRuleException(PersonnelConstants.HIERARCHY_CHANGE_EXCEPTION);
                }
            } else if (oldUserDetails.isNonLoanOfficer()
                    && newLevel.equals(PersonnelLevel.LOAN_OFFICER)
                    && !newOffice.isNotBranch()) {

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
        String language = getNameForBusinessActivityEntity(personnel.getPreferredLocale().getLanguage().getLookUpValue().getLookUpId());

        List<ValueListElement> genders = this.customerDao.retrieveGenders();
        List<ValueListElement> martialStatuses = this.customerDao.retrieveMaritalStatuses();
        List<ValueListElement> languages = this.customerDao.retrieveLanguages();

        int age = DateUtils.DateDiffInYears(((Date) personnel.getPersonnelDetails().getDob()));
        if (age < 0) {
            age = 0;
        }

        return new UserSettingsDto(gender, martialStatus, language, age, genders, martialStatuses, languages);
    }

    private String getNameForBusinessActivityEntity(Integer entityId) {
        try {
            String value = "";
            if (entityId != null) {
                 value = new MasterPersistence().retrieveMasterEntities(entityId);
            }
            return value;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public UserSettingsDto retrieveUserSettings(Integer genderId, Integer maritalStatusId, Integer languageId) {

        String gender = getNameForBusinessActivityEntity(genderId);
        String martialStatus = getNameForBusinessActivityEntity(maritalStatusId);
        String language = getNameForBusinessActivityEntity(languageId);

        int age = 0;
        List<ValueListElement> empty = new ArrayList<ValueListElement>();
        return new UserSettingsDto(gender, martialStatus, language, age, empty, empty, empty);
    }

    @Override
    public UserDetailDto retrieveUserInformation(Short personnelId) {

        PersonnelBO personnel = this.personnelDao.findPersonnelById(personnelId);
        return personnel.toDto();
    }

    @Override
    public void updateUserSettings(Short personnelId, String emailId, Name name, Integer maritalStatusValue, Integer genderValue,
            AddressDto address, Short preferredLocale) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        PersonnelBO personnel = this.personnelDao.findPersonnelById(personnelId);

        try {
            personnel.updateDetails(userContext);
            this.transactionHelper.startTransaction();
            this.transactionHelper.beginAuditLoggingFor(personnel);

            Short localeId = Localization.getInstance().getLocaleId();
            if (preferredLocale != null) {
                for (SupportedLocalesEntity locale : new ApplicationConfigurationPersistence().getSupportedLocale()) {
                    if (locale.getLanguage().getLookUpValue().getLookUpId() == preferredLocale.intValue()) {
                        localeId = locale.getLocaleId();
                    }
                }
            }

            Address theAddress = null;
            if (address != null) {
                theAddress = new Address(address.getLine1(), address.getLine2(), address.getLine3(), address.getCity(),
                        address.getState(), address.getCountry(), address.getZip(), address.getPhoneNumber());
            }

            personnel.update(emailId, name, maritalStatusValue, genderValue, theAddress, localeId);
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
}