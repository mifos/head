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

package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.joda.time.DateMidnight;
import org.mifos.dto.domain.CustomFieldDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "SE_BAD_FIELD"}, justification="should disable at filter level and also for pmd - not important for us")
public class PersonnelInformationDto implements Serializable {

    private final Integer id;
    private final String globalPersonnelNum;
    private final String displayName;
    private final ListElement status;
    private final Boolean locked;
    private final PersonnelDetailsDto personnelDetails;
    private final String emailId;
    private final Integer preferredLanguageId;
    private final String preferredLocaleLanguageName;
    private final Short levelId;
    private final Integer officeId;
    private final String officeName;
    private final Integer title;
    private final Set<ListElement> personnelRoles;
    private final Short personnelId;
    private final String userName;
    private final Set<CustomFieldDto> customFields;
    private final Set<PersonnelNoteDto> personnelNotes;
    private final Date passwordExpirationDate;

    public PersonnelInformationDto(Integer id, String globalPersonnelNum, String displayName, ListElement status, Boolean locked,
                                   PersonnelDetailsDto personnelDetails, String emailId, String preferredLocaleLanguageName,
                                   Integer preferredLanguageId, Short levelId, Integer officeId, String officeName, Integer title, Set<ListElement> personnelRoles,
                                   Short personnelId, String userName, Set<CustomFieldDto> customFields,
                                   Set<PersonnelNoteDto> personnelNotes, Date passwordExpirationDate) {
        this.id = id;
        this.globalPersonnelNum = globalPersonnelNum;
        this.displayName = displayName;
        this.status = status;
        this.locked = locked;
        this.personnelDetails = personnelDetails;
        this.emailId = emailId;
        this.preferredLocaleLanguageName = preferredLocaleLanguageName;
        this.preferredLanguageId = preferredLanguageId;
        this.levelId = levelId;
        this.officeId = officeId;
        this.officeName = officeName;
        this.title = title;
        this.personnelRoles = personnelRoles;
        this.personnelId = personnelId;
        this.userName = userName;
        this.customFields = customFields;
        this.personnelNotes = personnelNotes;
        this.passwordExpirationDate = passwordExpirationDate == null ? null : new Date(passwordExpirationDate.getTime());	
    }
    
    public Date getPasswordExpirationDate() {
		return passwordExpirationDate == null ? null : new Date(passwordExpirationDate.getTime());
	}

	public String getGlobalPersonnelNum() {
        return globalPersonnelNum;
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

    public String getPreferredLocaleLanguageName() {
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
                && !this.personnelDetails.getDob().toString().equals("")) {
            return String.valueOf(dateDiffInYears(new java.sql.Date(this.personnelDetails.getDob().toDate().getTime())));
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

    private int dateDiffInYears(java.sql.Date fromDate) {
        Calendar fromDateCal = new GregorianCalendar();

        fromDateCal.setTime(fromDate);

        // Create a calendar object with today's date
        Calendar today = new DateMidnight().toGregorianCalendar();
        // Get age based on year
        int age = today.get(Calendar.YEAR) - fromDateCal.get(Calendar.YEAR);
        int monthDiff = (today.get(Calendar.MONTH) + 1) - (fromDateCal.get(Calendar.MONTH) + 1);
        int dayDiff = today.get(Calendar.DAY_OF_MONTH) - fromDateCal.get(Calendar.DAY_OF_MONTH);
        // If this year's birthday has not happened yet, subtract one from age
        if (monthDiff < 0) {
            age--;
        } else if (monthDiff == 0) {
            if (dayDiff < 0) {
                age--;
            }
        }
        return age;
    }

    public Integer getId() {
        return this.id;
    }

    public Integer getOfficeId() {
        return this.officeId;
    }

    public Integer getPreferredLanguageId() {
        return this.preferredLanguageId;
    }
}