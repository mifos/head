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

package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class CreateClientEnterFamilyDetailsPage extends MifosPage {

    public CreateClientEnterFamilyDetailsPage() {
        super();
    }

    /**
     * @param selenium
     */
    public CreateClientEnterFamilyDetailsPage(Selenium selenium) {
        super(selenium);
    }

    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
    public static class SubmitFormParameters {


        Integer relationship;
        Integer livingStatus;
        String  firstName;
        String  lastName;
        String  middleName;
        String  dateOfBirthDD;
        String  dateOfBirthMM;
        String  dateOfBirthYY;
        Integer gender;

        // gender
        public static final int MALE = 49;
        public static final int FEMALE = 50;

        //Living Status
        public static final int TOGETHER= 622;
        public static final int NOT_TOGETHER= 623;

        //relationship
        public static final int SPOUSE = 1;
        public static final int FATHER = 2;
        public static final int OTHER_RELATIVE= 4;

        public Integer getGender() {
            return this.gender;
        }
        public void setGender(Integer gender) {
            this.gender = gender;
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

        public String getLastName() {
            return this.lastName;
        }
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        public String getDateOfBirthYY() {
            return this.dateOfBirthYY;
        }
        public void setDateOfBirthYY(String dateOfBirthYY) {
            this.dateOfBirthYY = dateOfBirthYY;
        }


        public Integer getRelationship() {
            return this.relationship;
        }
        public void setRelationship(Integer relationship) {
            this.relationship = relationship;
        }
        public Integer getLivingStatus() {
            return this.livingStatus;
        }
        public void setLivingStatus(Integer livingStatus) {
            this.livingStatus = livingStatus;
        }
        public String getFirstName() {
            return this.firstName;
        }



    }

    public CreateClientEnterFamilyDetailsPage createMember(SubmitFormParameters parameters) {
        selectValueIfNotZero("familyRelationship[0]", parameters.getRelationship());
        typeTextIfNotEmpty("familyFirstName[0]", parameters.getFirstName());
        typeTextIfNotEmpty("familyLastName[0]", parameters.getLastName());
        typeTextIfNotEmpty("familyDateOfBirthDD[0]", parameters.getDateOfBirthDD());
        typeTextIfNotEmpty("familyDateOfBirthMM[0]", parameters.getDateOfBirthMM());
        typeTextIfNotEmpty("familyDateOfBirthYY[0]", parameters.getDateOfBirthYY());
        selectValueIfNotZero("familyGender[0]", parameters.getGender());
        selectValueIfNotZero("familyLivingStatus[0]", parameters.getLivingStatus());
        return this;

    }

    public CreateClientEnterMfiDataPage submitAndGotoCreateClientEnterMfiDataPage() {
        selenium.click("create_ClientFamilyInfo.button.continue");
        waitForPageToLoad();
        return new CreateClientEnterMfiDataPage(selenium);
    }

    public CreateClientEnterFamilyDetailsPage addRow() {
        selenium.click("create_ClientFamilyInfo.button.addRow");
        waitForPageToLoad();
        return new CreateClientEnterFamilyDetailsPage(selenium);
    }

    public CreateClientEnterFamilyDetailsPage deleteRow() {
        selenium.click("create_ClientFamilyInfo.button.deleteRow");
        waitForPageToLoad();
        return new CreateClientEnterFamilyDetailsPage(selenium);
    }
}
