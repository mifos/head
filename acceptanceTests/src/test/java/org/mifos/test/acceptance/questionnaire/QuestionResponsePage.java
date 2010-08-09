/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.test.acceptance.questionnaire;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.group.CreateGroupConfirmationPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class QuestionResponsePage extends MifosPage {
    public QuestionResponsePage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        super.verifyPage("captureQuestionResponse");
    }

    public void verifyNumericBoundsValidation(String questionInputId, String answer, String questionTitle) {
        populateTextAnswer(questionInputId, answer);
        selenium.click("captureQuestionResponses.button.continue");
        waitForPageToLoad();
        assertTrue(selenium.isTextPresent("Please specify " + questionTitle));
    }

    public void populateTextAnswer(String questionInputId, String answer) {
        selenium.type(questionInputId, answer);
    }

    public void populateSmartSelect(String smartSelectId, Map<String, String> tags) {
        for (String tag : tags.keySet()) {
            selenium.type(smartSelectId, tag);
            selenium.keyUp(smartSelectId, " ");
            selenium.check("css=input[value=" + tags.get(tag) + "]");
        }
    }

    public CreateGroupConfirmationPage submitNewGroupForApproval() {
        selenium.click("captureQuestionResponses.button.continue");
        waitForPageToLoad();
        selenium.click("editQuestionResponses_button");
        waitForPageToLoad();
        super.verifyPage("captureQuestionResponse");
        selenium.click("captureQuestionResponses.button.continue");
        waitForPageToLoad();
        selenium.isVisible("previewgroup.button.submitForApproval");
        selenium.click("previewgroup.button.submitForApproval");
        waitForPageToLoad();
        return new CreateGroupConfirmationPage(selenium);
    }

    public GroupViewDetailsPage navigateToCreateGroupDetailsPage(String status) {
        CreateGroupConfirmationPage confirmationPage = submitNewGroupForApproval();
        confirmationPage.verifyPage();
        GroupViewDetailsPage groupDetailsPage = confirmationPage.navigateToGroupDetailsPage();
        groupDetailsPage.verifyPage();
        groupDetailsPage.verifyStatus(status);
        return groupDetailsPage;
    }
}
