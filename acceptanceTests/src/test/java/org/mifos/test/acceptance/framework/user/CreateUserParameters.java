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

@SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
public class CreateUserParameters {
    // sex
    public static final int MALE = 49;
    public static final int FEMALE = 50;
    
    // title
    public static final int MR = 47;
    public static final int MRS = 48;
    public static final int MS = 228;
    
    // poverty level
    public static final int VERY_POOR = 41;
    public static final int POOR = 42;
    public static final int NOT_POOR = 43;
    
    // locale
    public static final int ENGLISH = 189;
    public static final int ICELANDIC = 599;
    public static final int SPANISH = 600;
    public static final int FRENCH = 601;
    public static final int CHINESE = 663;
    public static final int SWAHILI = 664;
    
    // user level
    public static final int LOAN_OFFICER = 1;
    public static final int NON_LOAN_OFFICER = 2;
    
    String firstName;
    String lastName;
    String email;
    String maritalStatus;
    String dateOfBirthDD;
    String dateOfBirthMM;
    String dateOfBirthYYYY;
    int gender;
    int preferredLanguage;
    int userLevel;
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
    
    public int getGender() {
        return this.gender;
    }
    
    public void setGender(int gender) {
        this.gender = gender;
    }
    
    public int getPreferredLanguage() {
        return this.preferredLanguage;
    }
    
    public void setPreferredLanguage(int preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
    
    public int getUserLevel() {
        return this.userLevel;
    }
    
    public void setUserLevel(int userLevel) {
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