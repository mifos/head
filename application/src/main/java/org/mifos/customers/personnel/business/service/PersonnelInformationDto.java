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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelCustomFieldEntity;
import org.mifos.customers.personnel.business.PersonnelDetailsEntity;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;
import org.mifos.customers.personnel.business.PersonnelNotesEntity;
import org.mifos.customers.personnel.business.PersonnelRoleEntity;
import org.mifos.customers.personnel.business.PersonnelStatusEntity;
import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.util.helpers.DateUtils;

/**
 * Data transfer object to hold data for display on the viewCenterDetails.jsp page
 */
public class PersonnelInformationDto implements DataTransferObject {

    private final String displayName;
    private final PersonnelStatusEntity status;
    private final Boolean locked;
    private final PersonnelDetailsEntity personnelDetails;
    private final String emailId;
    private final SupportedLocalesEntity preferredLocale;
    private final PersonnelLevelEntity level;
    private final OfficeBO office;
    private final Integer title;
    private final Set<PersonnelRoleEntity> personnelRoles;
    private final Short personnelId;
    private final String userName;
    private final Set<PersonnelCustomFieldEntity> customFields;
    private final Set<PersonnelNotesEntity> personnelNotes;


    public PersonnelInformationDto(String displayName, PersonnelStatusEntity status, Boolean locked,
                                   PersonnelDetailsEntity personnelDetails, String emailId, SupportedLocalesEntity preferredLocale,
                                   PersonnelLevelEntity level, OfficeBO office, Integer title, Set<PersonnelRoleEntity> personnelRoles,
                                   Short personnelId, String userName, Set<PersonnelCustomFieldEntity> customFields,
                                   Set<PersonnelNotesEntity> personnelNotes) {
        super();
        this.displayName = displayName;
        this.status = status;
        this.locked = locked;
        this.personnelDetails = personnelDetails;
        this.emailId = emailId;
        this.preferredLocale = preferredLocale;
        this.level = level;
        this.office = office;
        this.title = title;
        this.personnelRoles = personnelRoles;
        this.personnelId = personnelId;
        this.userName = userName;
        this.customFields = customFields;
        this.personnelNotes = personnelNotes;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public PersonnelStatusEntity getStatus() {
        return this.status;
    }

    public Boolean getLocked() {
        return this.locked;
    }

    public PersonnelDetailsEntity getPersonnelDetails() {
        return this.personnelDetails;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public SupportedLocalesEntity getPreferredLocale() {
        return this.preferredLocale;
    }

    public PersonnelLevelEntity getLevel() {
        return this.level;
    }

    public OfficeBO getOffice() {
        return this.office;
    }

    public Integer getTitle() {
        return this.title;
    }

    public Set<PersonnelRoleEntity> getPersonnelRoles() {
        return this.personnelRoles;
    }

    public Short getPersonnelId() {
        return this.personnelId;
    }

    public String getUserName() {
        return this.userName;
    }

    public Set<PersonnelCustomFieldEntity> getCustomFields() {
        return this.customFields;
    }

    public Set<PersonnelNotesEntity> getPersonnelNotes() {
        return this.personnelNotes;
    }

    public String getAge() {
        if (this.personnelDetails != null && this.personnelDetails.getDob() != null
                && !this.personnelDetails.getDob().equals("")) {
            return String.valueOf(DateUtils
                    .DateDiffInYears(new java.sql.Date(this.personnelDetails.getDob().getTime())));
        }

        return "";
    }

    public List<PersonnelNotesEntity> getRecentPersonnelNotes() {
        List<PersonnelNotesEntity> notes = new ArrayList<PersonnelNotesEntity>();
        int count = 0;
        for (PersonnelNotesEntity personnelNotes : getPersonnelNotes()) {
            if (count > 2) {
                break;
            }
            notes.add(personnelNotes);
            count++;
        }
        return notes;
    }
}