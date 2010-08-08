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

package org.mifos.customers.personnel.business.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelCustomFieldEntity;
import org.mifos.customers.personnel.business.PersonnelDetailsEntity;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;
import org.mifos.customers.personnel.business.PersonnelNotesEntity;
import org.mifos.customers.personnel.business.PersonnelRoleEntity;
import org.mifos.customers.personnel.business.PersonnelStatusEntity;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.PersonnelDetailsDto;
import org.mifos.dto.screen.PersonnelNoteDto;
import org.mifos.framework.business.util.Address;

/**
 *
 */
public class PersonnelDetailsServiceFacadeWebTier implements PersonnelDetailsServiceFacade {

    private final PersonnelDao personnelDao;

    public PersonnelDetailsServiceFacadeWebTier(PersonnelDao personnelDao) {
        this.personnelDao = personnelDao;
    }

    @Override
    public PersonnelInformationDto getPersonnelInformationDto(String globalCustNum) {

        PersonnelBO personnel = personnelDao.findByGlobalPersonnelNum(globalCustNum);
        if (personnel == null) {
            throw new MifosRuntimeException("personnel not found for globalCustNum" + globalCustNum);
        }

        String displayName = personnel.getDisplayName();
        PersonnelStatusEntity personnelStatus = personnel.getStatus();
        ListElement status = new ListElement(new Integer(personnelStatus.getId()), personnelStatus.getName());
        boolean locked =  personnel.isLocked();
        PersonnelDetailsEntity personnelDetailsEntity = personnel.getPersonnelDetails();
        Address address = personnelDetailsEntity.getAddress();
        AddressDto addressDto = new AddressDto(address.getLine1(), address.getLine2(), address.getLine3(), address.getCity(), address.getState(),
                                    address.getCountry(), address.getZip(), address.getPhoneNumber());
        PersonnelDetailsDto personnelDetails = new PersonnelDetailsDto(personnelDetailsEntity.getGovernmentIdNumber(), new DateTime(personnelDetailsEntity.getDob()),
                                                personnelDetailsEntity.getMaritalStatus(), personnelDetailsEntity.getGender(),
                                                new DateTime(personnelDetailsEntity.getDateOfJoiningMFI()), new DateTime(personnelDetailsEntity.getDateOfJoiningBranch()),
                                                new DateTime(personnelDetailsEntity.getDateOfLeavingBranch()), addressDto);
        String emailId = personnel.getEmailId();
        SupportedLocalesEntity preferredLocale = personnel.getPreferredLocale();
        PersonnelLevelEntity level = personnel.getLevel();
        OfficeBO office = personnel.getOffice();
        Integer title = personnel.getTitle();
        Set<PersonnelRoleEntity> personnelRoleEntities = personnel.getPersonnelRoles();
        Set<ListElement> personnelRoles = new LinkedHashSet<ListElement>();
        for(PersonnelRoleEntity entity: personnelRoleEntities) {
            ListElement element = new ListElement(entity.getPersonnelRoleId(), entity.getRole().getName());
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
//        personnel.getStatus().setLocaleId(userLocaleId);
        return new PersonnelInformationDto(displayName, status, locked,
                                           personnelDetails, emailId, preferredLocale.getLanguageName(),
                                           level.getId(), office.getOfficeName(), title, personnelRoles,
                                           personnelId, userName, customFields,
                                           personnelNotes);
    }
}