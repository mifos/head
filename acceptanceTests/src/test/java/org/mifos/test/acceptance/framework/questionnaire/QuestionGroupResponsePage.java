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

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;

public class QuestionGroupResponsePage extends MifosPage {
    private static final String ANSWER_JS = "window.$(\"#displayResponseForm label:contains(\\\"%s\\\")\").parent().text().trim()";

    public QuestionGroupResponsePage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        super.verifyPage("display_question_group_reponse");
    }

    public String getAnswerHtml(String question) {
        return getEval(String.format(ANSWER_JS, question));
    }

    public ClientViewDetailsPage navigateToViewClientDetailsPage() {
        selenium.click("id=_eventId_cancel");
        waitForPageToLoad();
        return new ClientViewDetailsPage(selenium);
    }

    public QuestionnairePage navigateToEditResponses() {
        selenium.click("link=Edit");
        waitForPageToLoad();
        return new QuestionnairePage(selenium);
    }
}
