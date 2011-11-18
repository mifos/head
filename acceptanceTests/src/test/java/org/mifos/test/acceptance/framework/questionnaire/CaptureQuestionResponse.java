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
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;

import java.util.Map;

import org.testng.Assert;

@SuppressWarnings("PMD")
public class CaptureQuestionResponse extends MifosPage {

    public CaptureQuestionResponse(Selenium selenium) {
        super(selenium);
        verifyPage("captureQuestionResponse");
    }

    public void verifyPage() {
        super.verifyPage("captureQuestionResponse");
    }

    public void populateAnswers(QuestionResponseParameters responseParameters) {
        for (Map.Entry<String, String> entry : responseParameters.getTextResponses().entrySet()) {
            selenium.type(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : responseParameters.getSingleSelectResponses().entrySet()) {
            selenium.check("//input[@name='" + entry.getKey() + "' and @value='" + entry.getValue() + "']");
        }
    }

    public void navigateToNextPageSavingsAccountClosing() {
        selenium.click("captureQuestionResponses.button.continue");
        waitForPageToLoad();
    }

    public void verifyQuestionsExists(String[] questions) {
        for(String question: questions) {
            Assert.assertTrue(selenium.isTextPresent(question));
        }
    }
}
