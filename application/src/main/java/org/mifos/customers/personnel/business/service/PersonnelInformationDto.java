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

import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.PersonnelDetailsDto;
import org.mifos.dto.screen.PersonnelNoteDto;
import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.util.helpers.DateUtils;

/**
 * Data transfer object to hold data for display on the viewCenterDetails.jsp page
 */
public class PersonnelInformationDto implements DataTransferObject {

    private final String displayName;
    private final ListElement status;
    private final Boolean locked;
    private final PersonnelDetailsDto personnelDetails;
    private final String emailId;
    private final String preferredLocaleLanguageName;
    private final Short levelId;
    private final String officeName;
    private final Integer title;
    private final Set<ListElement> personnelRoles;
    private final Short personnelId;
    private final String userName;
    private final Set<CustomFieldDto> customFields;
    private final Set<PersonnelNoteDto> personnelNotes;


    public PersonnelInformationDto(String displayName, ListElement status, Boolean locked,
                                   PersonnelDetailsDto personnelDetails, String emailId, String preferredLocaleLanguageName,
                                   Short levelId, String officeName, Integer title, Set<ListElement> personnelRoles,
                                   Short personnelId, String userName, Set<CustomFieldDto> customFields,
                                   Set<PersonnelNoteDto> personnelNotes) {
        super();
        this.displayName = displayName;
        this.status = status;
        this.locked = locked;
        this.personnelDetails = personnelDetails;
        this.emailId = emailId;
        this.preferredLocaleLanguageName = preferredLocaleLanguageName;
        this.levelId = levelId;
        this.officeName = officeName;
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

    public ListElement getStatus() {
        return this.status;
    }

    public Boolean getLocked() {
        return this.locked;
    }

    public PersonnelDetailsDto getPersonnelDetails() {
        return this.personnelDetails;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public String getPreferredLocaleLanguage() {
        return this.preferredLocaleLanguageName;
    }

    public Short getLevelId() {
        return this.levelId;
    }

    public String getOfficeName() {
        return this.officeName;
    }

    public Integer getTitle() {
        return this.title;
    }

    public Set<ListElement> getPersonnelRoles() {
        return this.personnelRoles;
    }

    public Short getPersonnelId() {
        return this.personnelId;
    }

    public String getUserName() {
        return this.userName;
    }

    public Set<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

    public Set<PersonnelNoteDto> getPersonnelNotes() {
        return this.personnelNotes;
    }

    public String getAge() {
        if (this.personnelDetails != null && this.personnelDetails.getDob() != null
                && !this.personnelDetails.getDob().equals("")) {
            return String.valueOf(DateUtils
                    .DateDiffInYears(new java.sql.Date(this.personnelDetails.getDob().toDate().getTime())));
        }

        return "";
    }

    public List<PersonnelNoteDto> getRecentPersonnelNotes() {
        List<PersonnelNoteDto> notes = new ArrayList<PersonnelNoteDto>();
        int count = 0;
        for (PersonnelNoteDto personnelNotes : getPersonnelNotes()) {
            if (count > 2) {
                break;
            }
            notes.add(personnelNotes);
            count++;
        }
        return notes;
    }
}