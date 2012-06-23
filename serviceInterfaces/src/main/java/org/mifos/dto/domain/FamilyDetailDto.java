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

public class FamilyDetailDto {

    private Short relationship = 0;
    private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private String dateOfBirthDD;
    private String dateOfBirthMM;
    private String dateOfBirthYY;
    private Short gender = 0;
    private Short livingStatus = 0;

    public Short getRelationship() {
        return this.relationship;
    }

    public void setRelationship(Short relationship) {
        this.relationship = relationship;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getSecondLastName() {
    	return this.secondLastName;
    }
    
    public void setSecondLastName(String secondLastName) {
    	this.secondLastName = secondLastName;
    }
	
    public String getDateOfBirthDD() {
        return this.dateOfBirthDD;
    }

    public void setDateOfBirthDD(String dateOfBirthDD) {
        this.dateOfBirthDD = dateOfBirthDD;
    }

    public String getDateOfBirthMM() {
        return this.dateOfBirthMM;
    }

    public void setDateOfBirthMM(String dateOfBirthMM) {
        this.dateOfBirthMM = dateOfBirthMM;
    }

    public String getDateOfBirthYY() {
        return this.dateOfBirthYY;
    }

    public void setDateOfBirthYY(String dateOfBirthYY) {
        this.dateOfBirthYY = dateOfBirthYY;
    }

    public Short getGender() {
        return this.gender;
    }

    public void setGender(Short gender) {
        this.gender = gender;
    }

    public Short getLivingStatus() {
        return this.livingStatus;
    }

    public void setLivingStatus(Short livingStatus) {
        this.livingStatus = livingStatus;
    }
}