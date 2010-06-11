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

import java.util.Set;
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
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class PersonnelDetailsServiceFacadeWebTier implements PersonnelDetailsServiceFacade {

    private final PersonnelDao personnelDao;

    public PersonnelDetailsServiceFacadeWebTier(PersonnelDao personnelDao) {
        this.personnelDao = personnelDao;
    }

    @Override
    public PersonnelInformationDto getPersonnelInformationDto(String globalCustNum, UserContext userContext)
            throws ServiceException {

        PersonnelBO personnel = personnelDao.getPersonnelByGlobalPersonnelNum(globalCustNum);
        if (personnel == null) {
            throw new MifosRuntimeException("personnel not found for globalCustNum" + globalCustNum);
        }

        String displayName = personnel.getDisplayName();
        PersonnelStatusEntity status = personnel.getStatus();
        boolean locked =  personnel.isLocked();
        PersonnelDetailsEntity personnelDetails = personnel.getPersonnelDetails();
        String emailId = personnel.getEmailId();
        SupportedLocalesEntity preferredLocale = personnel.getPreferredLocale();
        PersonnelLevelEntity level = personnel.getLevel();
        OfficeBO office = personnel.getOffice();
        Integer title = personnel.getTitle();
        Set<PersonnelRoleEntity> personnelRoles = personnel.getPersonnelRoles();
        Short personnelId = personnel.getPersonnelId();
        String userName = personnel.getUserName();
        Set<PersonnelCustomFieldEntity> customFields = personnel.getCustomFields();
        Set<PersonnelNotesEntity> personnelNotes = personnel.getPersonnelNotes();
        personnel.getStatus().setLocaleId(userContext.getLocaleId());

        return new PersonnelInformationDto(displayName, status, locked,
                                           personnelDetails, emailId, preferredLocale,
                                           level, office, title, personnelRoles,
                                           personnelId, userName, customFields,
                                           personnelNotes);
    }
}