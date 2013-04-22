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

import org.joda.time.DateTime;
import org.mifos.dto.domain.AddressDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SE_NO_SERIALVERSIONID", justification = "should disable at filter level and also for pmd - not important for us")
public class PersonnelDetailsDto implements Serializable {

    private final String governmentIdNumber;
    private final DateTime dob;
    private final Integer maritalStatus;
    private final Integer gender;
    private final DateTime dateOfJoiningMFI;
    private final DateTime passwordExpirationDate;
    private final DateTime dateOfJoiningBranch;
    private final DateTime dateOfLeavingBranch;
    private final AddressDto address;
    private final String firstName;
    private final String middleName;
    private final String secondLastName;
    private final String lastName;

    public PersonnelDetailsDto(String governmentIdNumber, DateTime dob, Integer maritalStatus, Integer gender,
            DateTime dateOfJoiningMFI, DateTime dateOfJoiningBranch, DateTime dateOfLeavingBranch, AddressDto address,
            String firstName, String middleName, String secondLastName, String lastName, DateTime passwordExpirationDate) {
        this.governmentIdNumber = governmentIdNumber;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.gender = gender;
        this.dateOfJoiningMFI = dateOfJoiningMFI;
        this.dateOfJoiningBranch = dateOfJoiningBranch;
        this.dateOfLeavingBranch = dateOfLeavingBranch;
        this.address = address;
        this.firstName = firstName;
        this.middleName = middleName;
        this.secondLastName = secondLastName;
        this.lastName = lastName;
        this.passwordExpirationDate = passwordExpirationDate;
    }

    public DateTime getPasswordExpirationDate() {
		return passwordExpirationDate;
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

    public DateTime getDateOfLeavingBranch() {
        return this.dateOfLeavingBranch;
    }

    public AddressDto getAddress() {
        return this.address;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public String getSecondLastName() {
        return this.secondLastName;
    }

    public String getLastName() {
        return this.lastName;
    }
}