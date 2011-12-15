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

package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

import org.mifos.test.acceptance.framework.admin.DefineLookupOptionParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;

public class CreateClientEnterPersonalDataPage extends MifosPage {

    public CreateClientEnterPersonalDataPage() {
        super();
    }

    /**
     * @param selenium
     */
    public CreateClientEnterPersonalDataPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("create_ClientPersonalInfo");
    }

    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
    public static class SubmitFormParameters {
        // gender
        public static final int MALE = 49;
        public static final int FEMALE = 50;

        // salutation
        public static final int MR = 47;
        public static final int MRS = 48;
        public static final int MS = 228;

        // poverty level
        public static final int VERY_POOR = 41;
        public static final int POOR = 42;
        public static final int NOT_POOR = 43;

        // father/spouse relationship
        public static final int SPOUSE = 1;
        public static final int FATHER = 2;

        int salutation;
        String firstName;
        String middleName;
        String lastName;
        String secondLastName;
        String dateOfBirthDD;
        String dateOfBirthMM;
        String dateOfBirthYYYY;
        int povertyStatus;
        int gender;
        String numberOfChildren;
        int maritalStatus;
        int citizenship;
        int ethnicity;
        int handicapped2;
        int educationLevel;
        String handicapped;
        String spouseFirstName;
        String spouseLastName;
        String govId;
        String address1;
        String phone;
        
        private int spouseNameType;

        public int getSalutation() {
            return this.salutation;
        }

        public void setSalutation(int salutation) {
            this.salutation = salutation;
        }

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

        public int getPovertyStatus() {
            return this.povertyStatus;
        }

        public void setPovertyStatus(int povertyStatus) {
            this.povertyStatus = povertyStatus;
        }

        public int getGender() {
            return this.gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }
        
        public int getEthnicity() {
            return this.ethnicity;
        }
        
        public void setEthnicity(int ethnicity) {
            this.ethnicity = ethnicity;
        }
        
        public int getCitizenship() {
            return this.citizenship;
        }
        
        public void setCitizenship(int citizenship) {
            this.citizenship = citizenship;
        }
        
        public int getEducationLevel() {
            return this.educationLevel;
        }
        
        public void setEducationLevel(int educationLevel) {
            this.educationLevel = educationLevel;
        }
        
        public int getMaritalStatus() {
            return this.maritalStatus;
        }
        
        public void setMaritalStatus(int maritalStatus) {
            this.maritalStatus = maritalStatus;
        }
        
        public String getNumberOfChildren() {
            return this.numberOfChildren;
        }
        
        public void setNumberOfChildren(String numberOfChildren) {
            this.numberOfChildren = numberOfChildren;
        }

        public String getHandicapped() {
            return this.handicapped;
        }

        public void setHandicapped(String handicapped) {
            this.handicapped = handicapped;
        }
        
        public int getHandicappedDropdown() {
            return this.handicapped2;
        }
        
        public void setHandicappedDropdown(int handicapped2) {
            this.handicapped2 = handicapped2;
        }
        
        public int getSpouseNameType() {
            return this.spouseNameType;
        }

        public void setSpouseNameType(int spouseNameType) {
            this.spouseNameType = spouseNameType;
        }

        public String getSpouseFirstName() {
            return this.spouseFirstName;
        }

        public void setSpouseFirstName(String spouseFirstName) {
            this.spouseFirstName = spouseFirstName;
        }

        public String getSpouseLastName() {
            return this.spouseLastName;
        }

        public void setSpouseLastName(String spouseLastName) {
            this.spouseLastName = spouseLastName;
        }

        public String getMiddleName() {
            return this.middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }
        
        public String getGovernmentID() {
            return this.govId;
        }
        
        public void setGovernmentID(String govId) {
            this.govId = govId;
        }
        
        public String getSecondLastName() {
            return this.secondLastName;
        }

        public void setSecondLastName(String secondLastName) {
            this.secondLastName = secondLastName;
        }
        
        public String getAddress1() {
            return this.address1;
        }
        
        public void setAddress1(String address1) {
            this.address1 = address1;
        }
        
        public String getPhone() {
            return this.phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public CreateClientEnterPersonalDataPage create(SubmitFormParameters parameters) {
        selectValueIfNotZero("clientName.salutation", parameters.getSalutation());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.firstName", parameters.getFirstName());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.middleName", parameters.getMiddleName());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.lastName", parameters.getLastName());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.secondLastName", parameters.getSecondLastName());

        typeTextIfNotEmpty("dateOfBirthDD", parameters.getDateOfBirthDD());
        typeTextIfNotEmpty("dateOfBirthMM", parameters.getDateOfBirthMM());
        typeTextIfNotEmpty("dateOfBirthYY", parameters.getDateOfBirthYYYY());

        selectValueIfNotZero("clientDetailView.gender", parameters.getGender());
        selectValueIfNotZero("clientDetailView.povertyStatus", parameters.getPovertyStatus());
        selectIfNotEmpty("clientDetailView.handicapped", parameters.getHandicapped());

        selectValueIfNotZero("spouseName.nameType", parameters.getSpouseNameType());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.spouseFirstName", parameters.getSpouseFirstName());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.spouseLastName", parameters.getSpouseLastName());

        return this;

    }
    public CreateClientEnterPersonalDataPage createWithoutSpouse(SubmitFormParameters parameters) {
         selectValueIfNotZero("clientName.salutation", parameters.getSalutation());
         typeTextIfNotEmpty("create_ClientPersonalInfo.input.firstName", parameters.getFirstName());
         typeTextIfNotEmpty("create_ClientPersonalInfo.input.lastName", parameters.getLastName());
         typeTextIfNotEmpty("dateOfBirthDD", parameters.getDateOfBirthDD());
         typeTextIfNotEmpty("dateOfBirthMM", parameters.getDateOfBirthMM());
         typeTextIfNotEmpty("dateOfBirthYY", parameters.getDateOfBirthYYYY());
         selectValueIfNotZero("clientDetailView.gender", parameters.getGender());
         selectValueIfNotZero("clientDetailView.povertyStatus", parameters.getPovertyStatus());
         selectIfNotEmpty("clientDetailView.handicapped", parameters.getHandicapped());
         return this;
    }

    public CreateClientEnterPersonalDataPage createWithMandatoryFields(SubmitFormParameters parameters) {
        selectValueIfNotZero("clientName.salutation", parameters.getSalutation());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.firstName", parameters.getFirstName());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.middleName", parameters.getMiddleName());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.lastName", parameters.getLastName());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.secondLastName", parameters.getSecondLastName());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.governmentId", parameters.getGovernmentID());
        selectValueIfNotZero("clientDetailView.maritalStatus", parameters.getMaritalStatus());
        typeTextIfNotEmpty("clientDetailView.numChildren", parameters.getNumberOfChildren());
        selectValueIfNotZero("clientDetailView.citizenship", parameters.getCitizenship());
        selectValueIfNotZero("clientDetailView.ethnicity", parameters.getEthnicity());
        selectValueIfNotZero("clientDetailView.educationLevel", parameters.getEducationLevel());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.spouseSecondLastName", parameters.getSpouseLastName());
        typeTextIfNotEmpty("dateOfBirthDD", parameters.getDateOfBirthDD());
        typeTextIfNotEmpty("dateOfBirthMM", parameters.getDateOfBirthMM());
        typeTextIfNotEmpty("dateOfBirthYY", parameters.getDateOfBirthYYYY());
        selectValueIfNotZero("clientDetailView.gender", parameters.getGender());
        selectValueIfNotZero("spouseName.nameType", parameters.getSpouseNameType());
        selectValueIfNotZero("clientDetailView.povertyStatus", parameters.getPovertyStatus());
        selectValueIfNotZero("clientDetailView.handicapped", parameters.getHandicappedDropdown());
        //selectIfNotEmpty("clientDetailView.handicapped", parameters.getHandicapped());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.address1", parameters.getAddress1());
        typeTextIfNotEmpty("create_ClientPersonalInfo.input.telephone", parameters.getPhone());
        return this;
   }
    
    public CreateClientEnterMfiDataPage submitAndGotoCreateClientEnterMfiDataPage() {
        selenium.click("create_ClientPersonalInfo.button.continue");
        waitForPageToLoad();
        return new CreateClientEnterMfiDataPage(selenium);
    }

    public QuestionResponsePage submitAndGotoCaptureQuestionResponsePage() {
        selenium.click("create_ClientPersonalInfo.button.continue");
        waitForPageToLoad();
        return new QuestionResponsePage(selenium);
    }

    public CreateClientEnterFamilyDetailsPage submitAndGotoCreateClientEnterFamilyDetailsPage() {
        selenium.click("create_ClientPersonalInfo.button.continue");
        waitForPageToLoad();
        return new CreateClientEnterFamilyDetailsPage(selenium);
    }

    /* It to should fail to load next page due to an induced error*/
    public CreateClientEnterPersonalDataPage dontLoadNext() {
        selenium.click("create_ClientPersonalInfo.button.continue");
        waitForPageToLoad();
        return new CreateClientEnterPersonalDataPage(selenium);
    }

    public void verifyLookupOption(DefineLookupOptionParameters lookupOptionParams) {
        switch (lookupOptionParams.getType()) {
        case DefineLookupOptionParameters.TYPE_SALUTATION:
            verifySelectHasOption("clientName.salutation", lookupOptionParams.getName());
            break;
        case DefineLookupOptionParameters.TYPE_MARITAL_STATUS:
            verifySelectHasOption("clientDetailView.maritalStatus", lookupOptionParams.getName());
            break;
        case DefineLookupOptionParameters.TYPE_CITIZENSHIP:
            verifySelectHasOption("clientDetailView.citizenship", lookupOptionParams.getName());
            break;
        case DefineLookupOptionParameters.TYPE_ETHNICITY:
            verifySelectHasOption("clientDetailView.ethnicity", lookupOptionParams.getName());
            break;
        case DefineLookupOptionParameters.TYPE_EDUCATION_LEVEL:
            verifySelectHasOption("clientDetailView.educationLevel", lookupOptionParams.getName());
            break;
        case DefineLookupOptionParameters.TYPE_HANDICAPPED:
            verifySelectHasOption("clientDetailView.handicapped", lookupOptionParams.getName());
            break;
        default:
            break;
        }
    }

    public String[] getMandatoryBlankFieldsNames() {
        dontLoadNext();
        String xpath = "//span[@id=\"create_ClientPersonalInfo.error.message\"]/li";
        int errorCount = selenium.getXpathCount(xpath).intValue();
        String[] errors = new String[errorCount];

        for (int i = 1; i <= errorCount; ++i) {
            String error = selenium.getText(String.format("%s[%d]", xpath, i));
            error = error.replace("Please specify ", "");

            if (error.startsWith("value for ")) {
                error = error.replace("value for ", "");
            }

            errors[i - 1] = error.replace(".", "");
        }

        return errors;
    }


}
