/*
 * Copyright Grameen Foundation USA
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

import com.thoughtworks.selenium.Selenium;

import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("PMD")
public class QuestionnairePage extends MifosPage {
    private static final String SELECT_QUESTION_JS = "window.$(\"#questionnaire label:contains(\\\"%s\\\")\").attr(\"for\")";

    public QuestionnairePage(Selenium selenium) {
        super(selenium);
        verifyPage("questionnaire");
    }

    public void verifyPage() {
        super.verifyPage("questionnaire");
    }

    public void verifyField(String locator, String text) {
        Assert.assertEquals(selenium.getText(locator), text);
    }

    public void setResponse(String question, String answer) {
        selenium.type("id=" + selenium.getEval(String.format(SELECT_QUESTION_JS, question)), answer);
    }

    public void checkResponse(String question, String answer) {
        selenium.check("name=" + selenium.getEval(String.format(SELECT_QUESTION_JS, question)) + " value=" + answer);
    }

    public MifosPage submit() {
        selenium.click("id=_eventId_saveQuestionnaire");
        waitForPageToLoad();
        return selenium.isElementPresent("id=allErrors") ? new QuestionnairePage(selenium) : new ClientViewDetailsPage(selenium);
    }

    public void setResponsesForMultiSelect(String question, int totalChoices, String... choices) {
        String questionId = selenium.getEval(String.format(SELECT_QUESTION_JS, question));
        String choiceIdFormat = questionId + "s%d";
        List<String> choiceList = Arrays.asList(choices);
        for (int i=0; i<totalChoices; i++) {
            String choiceId = String.format(choiceIdFormat, i);
            String choice = selenium.getEval("window.document.getElementById(\"" + choiceId + "\").value");
            if (choiceList.contains(choice)) {
                selenium.check("id=" + choiceId);
            }
        }
    }

    public boolean isErrorPresent(String errorMsg) {
        return selenium.isTextPresent(errorMsg);
    }

    public ClientViewDetailsPage cancel() {
        selenium.click("id=_eventId_cancel");
        waitForPageToLoad();
        return new ClientViewDetailsPage(selenium);
    }
}
