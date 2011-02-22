/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.test.acceptance.framework.questionnaire;

import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import com.thoughtworks.selenium.Selenium;

import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.office.OfficeViewDetailsPage;

import static org.junit.Assert.assertTrue;

public class ViewQuestionResponseDetailPage extends MifosPage {
    public ViewQuestionResponseDetailPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        super.verifyPage("display_question_group_responses");
    }

    public void verifyQuestionPresent(String question, String... answers) {
        assertTrue(selenium.isTextPresent(question));
        for (String answer : answers) {
            assertTrue(selenium.isTextPresent(answer));
        }
    }

    public void verifyQuestionsDoesnotappear(String[] questions) {
        for (String question : questions) {
            Assert.assertFalse(selenium.isTextPresent(question));
        }
    }

    public void verifyEditButtonDisabled(String number) {
        Assert.assertFalse(selenium.isTextPresent("xpath=//a[@questiongroupinstancedetailindex='"+number+"']"));
    }

    public QuestionnairePage navigateToEditSection(String number) {
        selenium.click("xpath=//a[@questiongroupinstancedetailindex='"+number+"']");
        waitForPageToLoad();
        return new QuestionnairePage(selenium);
    }

    public OfficeViewDetailsPage navigateToDetailsPage() {
        selenium.click("_eventId_cancel");
        waitForPageToLoad();

        return new OfficeViewDetailsPage(selenium);
    }

    public ClientViewDetailsPage navigateToClientViewDetailsPage() {
        selenium.click("_eventId_cancel");
        waitForPageToLoad();

        return new ClientViewDetailsPage(selenium);
    }
}
