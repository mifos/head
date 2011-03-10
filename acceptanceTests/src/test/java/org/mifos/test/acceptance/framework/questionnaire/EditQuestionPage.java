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
package org.mifos.test.acceptance.framework.questionnaire;

import com.thoughtworks.selenium.Selenium;
import java.util.List;
import org.testng.Assert;

public class EditQuestionPage extends CreateQuestionRootPage {

    public EditQuestionPage(Selenium selenium) {
        super(selenium);
        verifyPage("editQuestion");
    }

    private void submit() {
        selenium.click("id=_eventId_update");
        waitForPageToLoad();
    }

    public QuestionDetailPage changeName(String newName) {
        setQuestionName(newName);
        submit();
        return new QuestionDetailPage(selenium);
    }

    public QuestionDetailPage activate() {
        selenium.click("id=currentQuestion.active0");
        submit();
        return new QuestionDetailPage(selenium);
    }

    public QuestionDetailPage deactivate() {
        selenium.click("id=currentQuestion.active1");
        submit();
        return new QuestionDetailPage(selenium);
    }

    public QuestionDetailPage update(CreateQuestionParameters createQuestionParameters) {
        enterDetails(createQuestionParameters);
        submit();
        return new QuestionDetailPage(selenium);
    }

    public EditQuestionPage tryUpdate(CreateQuestionParameters createQuestionParameters) {
        enterDetails(createQuestionParameters);
        submit();
        return new EditQuestionPage(selenium);
    }

    public void verifyTextPresent(String expectedText, String errorMessage) {
        Assert.assertTrue(isTextPresentInPage(expectedText), errorMessage);
    }

    public QuestionDetailPage cancelEdit() {
        selenium.click("id=_eventId_cancel");
        waitForPageToLoad();
        return new QuestionDetailPage(selenium);
    }

    public void setQuestionName(String name) {
        selenium.typeKeys("currentQuestion.text", name);
    }

    public void verifyQuestionName(String name) {
        Assert.assertEquals(selenium.getValue("currentQuestion.text"), name);
    }

    public void setNumberQuestion(String min, String max) {
        selenium.typeKeys("currentQuestion.numericMin", min);
        selenium.typeKeys("currentQuestion.numericMax", max);
    }

    public void verifyNumberQuestion(String min, String max) {
        Assert.assertEquals(selenium.getText("currentQuestion.numericMin"), min);
        Assert.assertEquals(selenium.getText("currentQuestion.numericMax"), max);
    }

    public void addSmartAnswerChoices(List<String> answerChoices) {
        for(String answerChoice : answerChoices) {
            selenium.typeKeys("currentQuestion.currentSmartChoice", answerChoice);
            selenium.keyUp("id=currentQuestion.currentSmartChoice", " ");
            selenium.click("_eventId_addSmartChoice");
            waitForPageToLoad();
        }
    }

    public void addAnswerChoices(List<String> answerChoices) {
        for(String answerChoice : answerChoices) {
            selenium.typeKeys("currentQuestion.currentChoice", answerChoice);
            selenium.keyUp("id=currentQuestion.currentChoice"," ");
            selenium.click("_eventId_addChoice");
            waitForPageToLoad();
        }
    }

    public void removeAnswerChoice(String index) {
        selenium.click("xpath=//a[@choiceindex='"+index+"']");
        waitForPageToLoad();
    }
}
