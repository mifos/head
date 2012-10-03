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

package org.mifos.test.acceptance.framework.user;

import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;

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

    private void fillForm(CreateUserParameters parameters){
        typeTextIfNotEmpty("create_user.input.firstName", parameters.getFirstName());
        typeTextIfNotEmpty("create_user.input.lastName", parameters.getLastName());
        typeTextIfNotEmpty("create_user.input.secondLastName", parameters.getSecondLastName());
        typeTextIfNotEmpty("create_user.input.email", parameters.getEmail());
        selectIfNotEmpty("maritalStatus", parameters.getMaritalStatus());
        typeTextIfNotEmpty("dobDD", parameters.getDateOfBirthDD());
        typeTextIfNotEmpty("dobMM", parameters.getDateOfBirthMM());
        typeTextIfNotEmpty("dobYY", parameters.getDateOfBirthYYYY());
        selectValueIfNotZero("gender", parameters.getGender());
        selectValueIfNotZero("level", parameters.getUserLevel());
        if (parameters.getRole() != null && parameters.getRole().equals("Admin")) {
          selenium.click("MoveRight");
        }
        typeTextIfNotEmpty("create_user.input.userName", parameters.getUserName());
        typeTextIfNotEmpty("create_user.input.password", parameters.getPassword());
        typeTextIfNotEmpty("create_user.input.passwordRepeat", parameters.getPasswordRepeat());

        selenium.click("create_user.button.preview");
        waitForPageToLoad();
    }

    public CreateUserEnterDataPage submitAndReturnToThisPage(CreateUserParameters parameters) {
        fillForm(parameters);
        return new CreateUserEnterDataPage(selenium);
    }

    public CreateUserPreviewDataPage submitAndGotoCreateUserPreviewDataPage(CreateUserParameters parameters) {
        fillForm(parameters);
        waitForPageToLoad();
        return new CreateUserPreviewDataPage(selenium);
    }

    public QuestionResponsePage submitAndNavigateToQuestionResponsePage(CreateUserParameters parameters) {
        fillForm(parameters);
        waitForPageToLoad();
        return new QuestionResponsePage(selenium);
    }

    public void verifyPasswordChangeError(){
        String errorMsg = selenium.getText("create_user.error.message");
        Assert.assertTrue(errorMsg.contains("Please ensure that password and confirm password entries are made and they are identical."));
    }

    public void verifyDateError() {
        String errorMsg = selenium.getText("create_user.error.message");
        Assert.assertTrue(errorMsg.contains("Date of Birth is incorrect."));
    }
}
