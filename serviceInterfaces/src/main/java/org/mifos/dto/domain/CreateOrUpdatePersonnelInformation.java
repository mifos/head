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

package org.mifos.dto.domain;

import java.util.List;

import org.joda.time.DateTime;
import org.mifos.dto.screen.ListElement;

@SuppressWarnings("PMD")
public class CreateOrUpdatePersonnelInformation {

    private final Short personnelLevelId;
    private final Short officeId;
    private final Integer title;
    private final Short preferredLocale;
    private final String password;
    private final String userName;
    private final String emailId;
    private final List<ListElement> roles;
    private final List<CustomFieldDto> customFields;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String secondLastName;
    private final String governmentIdNumber;
    private final DateTime dob;
    private final Integer maritalStatus;
    private final Integer gender;
    private final DateTime dateOfJoiningMFI;
    private final DateTime dateOfJoiningBranch;
    private final AddressDto address;
    private final DateTime passwordExpirationDate;

    private final Short personnelStatusId;
    private final Long id;

    public CreateOrUpdatePersonnelInformation(Long id, Short personnelLevelId, Short officeId, Integer title,
            Short preferredLocale, String password, String userName, String emailId, List<ListElement> roles,
            List<CustomFieldDto> customFields, String firstName, String middleName, String lastName,
            String secondLastName, String governmentIdNumber, DateTime dob, Integer maritalStatus, Integer gender,
            DateTime dateOfJoiningMFI, DateTime dateOfJoiningBranch, AddressDto address, Short personnelStatusId,
            DateTime passwordExpirationDate) {
        this.id = id;
        this.personnelLevelId = personnelLevelId;
        this.officeId = officeId;
        this.title = title;
        this.preferredLocale = preferredLocale;
        this.password = password;
        this.userName = userName;
        this.emailId = emailId;
        this.roles = roles;
        this.customFields = customFields;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.secondLastName = secondLastName;
        this.governmentIdNumber = governmentIdNumber;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.gender = gender;
        this.dateOfJoiningMFI = dateOfJoiningMFI;
        this.dateOfJoiningBranch = dateOfJoiningBranch;
        this.address = address;
        this.personnelStatusId = personnelStatusId;
        this.passwordExpirationDate = passwordExpirationDate;
    }
    
    public DateTime getPasswordExpirationDate() {
		return passwordExpirationDate;
	}

	public Short getPersonnelLevelId() {
        return this.personnelLevelId;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public Integer getTitle() {
        return this.title;
    }

    public Short getPreferredLocale() {
        return this.preferredLocale;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public List<ListElement> getRoles() {
        return this.roles;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getSecondLastName() {
        return this.secondLastName;
    }

    public String getGovernmentIdNumber() {
        return this.governmentIdNumber;
    }

    public DateTime getDob() {
        return this.dob;
    }

    public Integer getMaritalStatus() {
        return this.maritalStatus;
    }

    public Integer getGender() {
        return this.gender;
    }

    public DateTime getDateOfJoiningMFI() {
        return this.dateOfJoiningMFI;
    }

    public DateTime getDateOfJoiningBranch() {
        return this.dateOfJoiningBranch;
    }

    public AddressDto getAddress() {
        return this.address;
    }

    public Short getPersonnelStatusId() {
        return this.personnelStatusId;
    }

    public Long getId() {
        return this.id;
    }
}