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
import org.mifos.test.acceptance.framework.MifosPage;

public class CreateQuestionGroupPage extends MifosPage {
    public static final String PAGE_ID = "createQuestionGroup";

    public CreateQuestionGroupPage(Selenium selenium) {
        super(selenium);
    }

    public CreateQuestionGroupPage verifyPage() {
        verifyPage(PAGE_ID);
        return this;
    }

    public void cancel() {
        selenium.click("id=_eventId_cancel");
        waitForPageToLoad();
    }

    public void submit(CreateQuestionGroupParameters createQuestionGroupParameters) {
        selenium.type("title", createQuestionGroupParameters.getTitle());
        selenium.select("eventSourceId", createQuestionGroupParameters.getAppliesTo());
        selenium.click("id=_eventId_defineQuestionGroup");
        waitForPageToLoad();
    }

    public void addSection(CreateQuestionGroupParameters createQuestionGroupParameters) {
        selenium.type("sectionName", createQuestionGroupParameters.getSectionName());
        selenium.click("id=_eventId_addSection");
        waitForPageToLoad();
    }
}
