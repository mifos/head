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

package org.mifos.test.acceptance.framework.group;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;

public class CreateGroupEntryPage extends MifosPage {

    public void verifyPage() {
        this.verifyPage("LoanCreationDetail");
    }

    public CreateGroupEntryPage(Selenium selenium) {
        super(selenium);
    }

    public QuestionResponsePage submitNewGroupAndNavigateToQuestionResponsePage(CreateGroupSubmitParameters formParameters) {
        enterGroupData(formParameters);
        selenium.click("creategroup.button.preview");
        waitForPageToLoad();
        return new QuestionResponsePage(selenium);
    }

    public CreateGroupConfirmationPage submitNewGroupForApproval(CreateGroupSubmitParameters formParameters) {
        enterGroupData(formParameters);
        selenium.click("creategroup.button.preview");
        waitForPageToLoad();
        //selenium.isVisible("previewgroup.button.submitForApproval");
        selenium.click("previewgroup.button.submitForApproval");
        waitForPageToLoad();
        return new CreateGroupConfirmationPage(selenium);
    }

    public QuestionResponsePage submitAndNavigateToQuestionResponsePage(CreateGroupSubmitParameters formParameters) {
        enterGroupData(formParameters);
        selenium.click("creategroup.button.preview");
        waitForPageToLoad();
        return new QuestionResponsePage(selenium);
    }
    public CreateGroupConfirmationPage submitNewGroupForApprove(CreateGroupSubmitParameters formParameters) {
        enterGroupData(formParameters);
        selenium.click("creategroup.button.preview");
        waitForPageToLoad();
        selenium.isVisible("previewgroup.button.approve");
        selenium.click("previewgroup.button.approve");
        waitForPageToLoad();
        return new CreateGroupConfirmationPage(selenium);
    }

    public CreateGroupConfirmationPage submitNewGroupForPartialApplication(CreateGroupSubmitParameters formParameters) {
        enterGroupData(formParameters);
        selenium.click("creategroup.button.preview");
        waitForPageToLoad();
        selenium.isVisible("previewgroup.button.saveForLater");
        selenium.click("previewgroup.button.saveForLater");
        waitForPageToLoad();
        return new CreateGroupConfirmationPage(selenium);
    }

    private void enterGroupData(CreateGroupSubmitParameters formParameters) {
        selenium.type("creategroup.input.displayName", formParameters.getGroupName());
        typeTextIfNotEmpty("formedByPersonnel", formParameters.getRecruitedBy());
        typeTextIfNotEmpty("trainedDateDD", formParameters.getTrainedDateDay());
        typeTextIfNotEmpty("trainedDateMM", formParameters.getTrainedDateMonth());
        typeTextIfNotEmpty("trainedDateYY", formParameters.getTrainedDateYear());
        typeTextIfNotEmpty("creategroup.input.externalId", formParameters.getExternalId());
        typeTextIfNotEmpty("creategroup.input.address1", formParameters.getAddressOne());
        typeTextIfNotEmpty("creategroup.input.address2", formParameters.getAddressTwo());
        typeTextIfNotEmpty("creategroup.input.address3", formParameters.getAddressThree());
        typeTextIfNotEmpty("creategroup.input.city", formParameters.getCity());
        typeTextIfNotEmpty("creategroup.input.state", formParameters.getState());
        typeTextIfNotEmpty("creategroup.input.country", formParameters.getCountry());
        typeTextIfNotEmpty("creategroup.input.postalCode", formParameters.getPostalCode());
        typeTextIfNotEmpty("creategroup.input.telephone", formParameters.getTelephone());
    }

    @SuppressWarnings("PMD.TooManyFields")
    // lots of fields ok for form input case
    public static class CreateGroupSubmitParameters {

        private String groupName;
        private String recruitedBy;
        private String trainedDateDay;
        private String trainedDateMonth;
        private String trainedDateYear;
        private String externalId;
        private String addressOne;
        private String addressTwo;
        private String addressThree;
        private String city;
        private String state;
        private String country;
        private String postalCode;
        private String telephone;

        public String getGroupName() {
            return this.groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getRecruitedBy() {
            return this.recruitedBy;
        }

        public void setRecruitedBy(String recruitedBy) {
            this.recruitedBy = recruitedBy;
        }

        public String getTrainedDateDay() {
            return this.trainedDateDay;
        }

        public void setTrainedDateDay(String trainedDateDay) {
            this.trainedDateDay = trainedDateDay;
        }

        public String getTrainedDateMonth() {
            return this.trainedDateMonth;
        }

        public void setTrainedDateMonth(String trainedDateMonth) {
            this.trainedDateMonth = trainedDateMonth;
        }

        public String getTrainedDateYear() {
            return this.trainedDateYear;
        }

        public void setTrainedDateYear(String trainedDateYear) {
            this.trainedDateYear = trainedDateYear;
        }

        public String getExternalId() {
            return this.externalId;
        }

        public void setExternalId(String externalId) {
            this.externalId = externalId;
        }

        public String getAddressOne() {
            return this.addressOne;
        }

        public void setAddressOne(String addressOne) {
            this.addressOne = addressOne;
        }

        public String getAddressTwo() {
            return this.addressTwo;
        }

        public void setAddressTwo(String addressTwo) {
            this.addressTwo = addressTwo;
        }

        public String getAddressThree() {
            return this.addressThree;
        }

        public void setAddressThree(String addressThree) {
            this.addressThree = addressThree;
        }

        public String getCity() {
            return this.city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return this.state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return this.country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPostalCode() {
            return this.postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getTelephone() {
            return this.telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

    }

}
