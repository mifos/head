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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.master.business.ValueListElement;
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
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CreateOrUpdatePersonnelInformation;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.dto.domain.UserSearchDto;
import org.mifos.dto.screen.DefinePersonnelDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.PersonnelDetailsDto;
import org.mifos.dto.screen.PersonnelInformationDto;
import org.mifos.dto.screen.PersonnelNoteDto;
import org.mifos.dto.screen.SystemUserSearchResultsDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.MifosUser;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;

public class PersonnelServiceFacadeWebTier implements PersonnelServiceFacade {

    private final OfficeDao officeDao;
    private final CustomerDao customerDao;
    private final PersonnelDao personnelDao;

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
    public void searchUser(String searchString, Short userId, HttpServletRequest request) {
        PersonnelBusinessService personnelBusinessService = new PersonnelBusinessService();
        try {
            PersonnelBO personnel = personnelBusinessService.getPersonnel(userId);

            addSearchValues(searchString, personnel.getOffice().getOfficeId().toString(), personnel.getOffice()
                    .getOfficeName(), request);
            searchString = org.mifos.framework.util.helpers.SearchUtils.normalizeSearchString(searchString);

            SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, new PersonnelPersistence().search(searchString, userId), request);
        } catch (PageExpiredException e) {
            throw new MifosRuntimeException(e);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void addSearchValues(String searchString, String officeId, String officeName, HttpServletRequest request)
    throws PageExpiredException {
        SessionUtils.setAttribute(Constants.SEARCH_STRING, searchString, request);
        SessionUtils.setAttribute(Constants.BRANCH_ID, officeId, request);
        SessionUtils.setAttribute(Constants.OFFICE_NAME, officeName, request);
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
        for (ValueListElement element: titles) {
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
        for (ValueListElement element: genders) {
            ListElement listElement = new ListElement(element.getId(), element.getName());
            genderList.add(listElement);
        }

        List<ValueListElement> maritalStatuses = customerDao.retrieveMaritalStatuses();
        List<ListElement> maritalStatusList = new ArrayList<ListElement>();
        for (ValueListElement element: maritalStatuses) {
            ListElement listElement = new ListElement(element.getId(), element.getName());
            maritalStatusList.add(listElement);
        }

        List<ValueListElement> languages = customerDao.retrieveLanguages();
        List<ListElement> languageList = new ArrayList<ListElement>();
        for (ValueListElement element: languages) {
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
        for (RoleBO element: roles) {
            ListElement listElement = new ListElement(new Integer(element.getId()), element.getName());
            roleList.add(listElement);
        }

        List<CustomFieldDto> customFields = customerDao.retrieveCustomFieldsForPersonnel(preferredLocale);
        DefinePersonnelDto defineUserDto = new DefinePersonnelDto(officeName, titleList, personnelLevelList, genderList, maritalStatusList, languageList, roleList,
                                        customFields);
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
        ListElement status = new ListElement(new Integer(personnelStatus.getId()), personnelStatus.getName());
        boolean locked =  personnel.isLocked();
        PersonnelDetailsEntity personnelDetailsEntity = personnel.getPersonnelDetails();
        Address address = personnelDetailsEntity.getAddress();
        AddressDto addressDto = new AddressDto(address.getLine1(), address.getLine2(), address.getLine3(), address.getCity(), address.getState(),
                                    address.getCountry(), address.getZip(), address.getPhoneNumber());

        Name name = personnelDetailsEntity.getName();
        PersonnelDetailsDto personnelDetails = new PersonnelDetailsDto(personnelDetailsEntity.getGovernmentIdNumber(), new DateTime(personnelDetailsEntity.getDob()),
                                                personnelDetailsEntity.getMaritalStatus(), personnelDetailsEntity.getGender(),
                                                new DateTime(personnelDetailsEntity.getDateOfJoiningMFI()), new DateTime(personnelDetailsEntity.getDateOfJoiningBranch()),
                                                new DateTime(personnelDetailsEntity.getDateOfLeavingBranch()), addressDto, name.getFirstName(), name.getMiddleName(), name.getSecondLastName(), name.getLastName());
        String emailId = personnel.getEmailId();
        SupportedLocalesEntity preferredLocale = personnel.getPreferredLocale();
        PersonnelLevelEntity level = personnel.getLevel();
        OfficeBO office = personnel.getOffice();
        Integer title = personnel.getTitle();
        Set<PersonnelRoleEntity> personnelRoleEntities = personnel.getPersonnelRoles();
        Set<ListElement> personnelRoles = new LinkedHashSet<ListElement>();
        for(PersonnelRoleEntity entity: personnelRoleEntities) {
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
        for (PersonnelNotesEntity entity: personnelNotesEntity) {
            personnelNotes.add(new PersonnelNoteDto(new DateTime(entity.getCommentDate()), entity.getComment(), entity.getPersonnelName()));
        }
        return new PersonnelInformationDto(personnel.getPersonnelId().intValue(), displayName, status, locked,
                                           personnelDetails, emailId, preferredLocale.getLanguageName(), preferredLocale.getLanguage().getLookUpValue().getLookUpId(),
                                           level.getId(), office.getOfficeId().intValue(), office.getOfficeName(), title, personnelRoles,
                                           personnelId, userName, customFields,
                                           personnelNotes);
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

            // FIXME - extract out validation for duplicate user name from model pojo
            PersonnelBO newPersonnel = new PersonnelBO(PersonnelLevel.fromInt(personnel.getPersonnelLevelId()
                    .intValue()), office, personnel.getTitle(), personnel.getPreferredLocale(),
                    personnel.getPassword(), personnel.getUserName(), personnel.getEmailId(), roles, personnel
                            .getCustomFields(), new Name(personnel.getFirstName(), personnel.getMiddleName(), personnel
                            .getSecondLastName(), personnel.getLastName()), personnel.getGovernmentIdNumber(),
                    personnel.getDob().toDate(), personnel.getMaritalStatus(), personnel.getGender(), personnel
                            .getDateOfJoiningMFI().toDate(), personnel.getDateOfJoiningBranch().toDate(), address,
                    Integer.valueOf(user.getUserId()).shortValue());

            StaticHibernateUtil.startTransaction();
            this.personnelDao.save(newPersonnel);
            StaticHibernateUtil.flushSession();
            newPersonnel.generateGlobalPersonnelNum();
            this.personnelDao.save(newPersonnel);
            StaticHibernateUtil.commitTransaction();

            return newPersonnel.toDto();

        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (ValidationException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (ServiceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }
}
