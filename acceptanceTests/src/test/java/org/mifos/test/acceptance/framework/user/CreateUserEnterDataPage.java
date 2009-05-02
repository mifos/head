/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.test.acceptance.framework.user;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class CreateUserEnterDataPage extends MifosPage {

	public CreateUserEnterDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CreateUserEnterDataPage(Selenium selenium) {
		super(selenium);
	}
	
    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
    public static class SubmitFormParameters {
        String firstName;
        String lastName;
        String email;
        String maritalStatus;
        String dateOfBirthDD;
        String dateOfBirthMM;
        String dateOfBirthYYYY;
        String gender;
        String preferredLanguage;
        String userLevel;
        String role;
        String userName;
        String password;
        String passwordRepeat;
        
        public String getFirstName() {
            return this.firstName;
        }
        
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return this.lastName;
        }
        
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        public String getEmail() {
            return this.email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getMaritalStatus() {
            return this.maritalStatus;
        }
        
        public void setMaritalStatus(String maritalStatus) {
            this.maritalStatus = maritalStatus;
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
        
        public String getDateOfBirthYYYY() {
            return this.dateOfBirthYYYY;
        }
        
        public void setDateOfBirthYYYY(String dateOfBirthYYYY) {
            this.dateOfBirthYYYY = dateOfBirthYYYY;
        }
        
        public String getGender() {
            return this.gender;
        }
        
        public void setGender(String gender) {
            this.gender = gender;
        }
        
        public String getPreferredLanguage() {
            return this.preferredLanguage;
        }
        
        public void setPreferredLanguage(String preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
        }
        
        public String getUserLevel() {
            return this.userLevel;
        }
        
        public void setUserLevel(String userLevel) {
            this.userLevel = userLevel;
        }
        
        public String getRole() {
            return this.role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }  
        
        public String getUserName() {
            return this.userName;
        }
        
        public void setUserName(String userName) {
            this.userName = userName;
        }
        
        public String getPassword() {
            return this.password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getPasswordRepeat() {
            return this.passwordRepeat;
        }
        
        public void setPasswordRepeat(String passwordRepeat) {
            this.passwordRepeat = passwordRepeat;
        }

    }
 
    public CreateUserPreviewDataPage submitAndGotoCreateUserPreviewDataPage(SubmitFormParameters parameters) {
        typeTextIfNotEmpty("create_user.input.firstName", parameters.getFirstName());
        typeTextIfNotEmpty("create_user.input.lastName", parameters.getLastName());
        typeTextIfNotEmpty("create_user.input.email", parameters.getEmail());
        selectIfNotEmpty("maritalStatus", parameters.getMaritalStatus());
        typeTextIfNotEmpty("dobDD", parameters.getDateOfBirthDD());
        typeTextIfNotEmpty("dobMM", parameters.getDateOfBirthMM());
        typeTextIfNotEmpty("dobYY", parameters.getDateOfBirthYYYY());        
        selectIfNotEmpty("gender", parameters.getGender());
        selectIfNotEmpty("preferredLocale", parameters.getPreferredLanguage());
        selectIfNotEmpty("level", parameters.getUserLevel());
        if (parameters.getRole() != null && parameters.getRole().equals("Admin")) {
          selenium.click("MoveRight");
        }
        typeTextIfNotEmpty("create_user.input.userName", parameters.getUserName());
        typeTextIfNotEmpty("create_user.input.password", parameters.getPassword());
        typeTextIfNotEmpty("create_user.input.passwordRepeat", parameters.getPasswordRepeat());
        
        selenium.click("create_user.button.preview");
        waitForPageToLoad();
        return new CreateUserPreviewDataPage(selenium);
    }    
}
