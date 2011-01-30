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
package org.mifos.test.acceptance.questionnaire;

import com.thoughtworks.selenium.Selenium;

public class CreateQuestionGroupPage extends CreateQuestionGroupRootPage {

    public CreateQuestionGroupPage(Selenium selenium) {
        super(selenium);
        verifyPage("createQuestionGroup");
    }

    public void submit(CreateQuestionGroupParameters createQuestionGroupParameters) {
        if (createQuestionGroupParameters.getTitle() != null) {
            selenium.type("name=title", createQuestionGroupParameters.getTitle());
        }
        if (createQuestionGroupParameters.isAnswerEditable()) {
           selenium.click("id=editable");
        }
        selectAppliesTo(createQuestionGroupParameters.getAppliesTo());
        selenium.click("id=_eventId_defineQuestionGroup");
        waitForPageToLoad();
    }

    private void selectAppliesTo(String appliesTo) {
        if (appliesTo == null || "--select one--".equals(appliesTo)) {
            return;
        }

        selenium.addSelection("id=eventSourceIds", "label=" + appliesTo);
    }

    public void addEmptySection(String sectionName) {
        selenium.check("id=addQuestionFlag0");
        if (!selenium.isVisible("id=selectQuestionsDiv")) {
            selenium.fireEvent("name=addQuestionFlag", "change");
        }
        selenium.type("id=sectionName", sectionName);
        selenium.click("id=_eventId_addSection");
        waitForPageToLoad();
    }
}
