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
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.user.UserViewDetailsPage;

import java.util.List;
import java.util.Map;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;

import org.testng.Assert;

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

    public void verifyRadioGroup(String locator, String value, boolean checked){
        Assert.assertEquals(selenium.isChecked(locator + " value="+ value),checked);
    }

    private String getQuestionLocator(String question) {
        return getEval(String.format(SELECT_QUESTION_JS, question));
    }

    public void setResponse(String question, String answer) {
        Number questionCount = selenium.getXpathCount("//label/following-sibling::textarea");
        if ((Integer) questionCount == 1) {
            selenium.type("//label/following-sibling::textarea", answer);
        } else {
            selenium.type("id=" + getQuestionLocator(question), answer);
        }
    }

    public QuestionnairePage setResponses(Map<String, String> responses) {
        for(String question: responses.keySet()) {
            setResponse(question, responses.get(question));
        }
        return this;
    }

    public void checkResponse(String question, String answer) {
        selenium.check("name=" + getQuestionLocator(question) + " value=" + answer);
    }

    public QuestionnairePage checkResponses(Map<String, List<String>> responses) {
        for(String question: responses.keySet()) {
            for(String answer : responses.get(question)) {
            checkResponse(question, answer);
            }
        }
        return this;
    }

    private void clickSubmit() {
        selenium.click("id=_eventId_saveQuestionnaire");
        waitForPageToLoad();
    }

    public MifosPage submit() {
        clickSubmit();
        return selenium.isElementPresent("id=allErrors") ? new QuestionnairePage(selenium) : new ClientViewDetailsPage(selenium);
    }

    public SavingsAccountDetailPage submitAndNavigateToSavingsAccountDetailPage() {
        clickSubmit();
        return new SavingsAccountDetailPage(selenium);
    }

    public MifosPage submitAndNavigateToPersonnalDetailsPage(){
        clickSubmit();
        return selenium.isElementPresent("id=allErrors") ? new QuestionnairePage(selenium) : new UserViewDetailsPage(selenium);
    }

    public MifosPage submitAndNavigateToCenterViewDetailsPage() {
        clickSubmit();
        return selenium.isElementPresent("id=allErrors") ? new QuestionnairePage(selenium) : new CenterViewDetailsPage(selenium);
    }

    public MifosPage submitAndNavigateToGroupViewDetailsPage() {
        clickSubmit();
        return selenium.isElementPresent("id=allErrors") ? new QuestionnairePage(selenium) : new GroupViewDetailsPage(selenium);
    }

    public MifosPage submitAndNavigateToLoanViewDetailsPage() {
        clickSubmit();
        return selenium.isElementPresent("id=allErrors") ? new QuestionnairePage(selenium) : new LoanAccountPage(selenium);
    }

    public MifosPage submitAndNavigateToClientViewDetailsPage() {
        clickSubmit();
        return selenium.isElementPresent("id=allErrors") ? new QuestionnairePage(selenium) : new ClientViewDetailsPage(selenium);
    }

    public void setResponsesForMultiSelect(String question, String... choices) {
        for (String choice : choices) {
            selenium.check("//label[contains(text(),'" + question + "')]/following-sibling::fieldset//label[contains(text(),'" + choice + "')]/preceding-sibling::input");
        }
    }
//    public void setResponsesForMultiSelect(String question, int totalChoices, String... choices) {
//        String questionId = getEval(String.format(SELECT_QUESTION_JS, question));
//        String choiceIdFormat = questionId + "s%d";
//        List<String> choiceList = Arrays.asList(choices);
//        for (int i=0; i<totalChoices; i++) {
//            String choiceId = String.format(choiceIdFormat, i);
//            String choice = getEval("window.document.getElementById(\"" + choiceId + "\").value");
//            if (choiceList.contains(choice)) {
//                selenium.check("id=" + choiceId);
//            }
//        }
//    }

    public boolean isErrorPresent(String errorMsg) {
        return isTextPresentInPage(errorMsg);
    }

    public void verifyErrorsOnPage(String[] errors) {
        for(String error : errors) {
            Assert.assertTrue(isErrorPresent(error));
        }
    }

    public QuestionnairePage verifyEmptyTextQuestionResponses(Map<String, String> questions) {
        for(String question: questions.keySet()) {
            Assert.assertEquals(selenium.getValue(getQuestionLocator(question)), "");
        }
        return this;
    }

    public QuestionnairePage verifyEmptyCheckQuestionResponses(Map<String, List<String>> questions) {
        for(String question: questions.keySet()) {
            for(String answer : questions.get(question)) {
                Assert.assertFalse(selenium.isChecked("name=" + getQuestionLocator(question) + " value=" + answer));
            }
        }
        return this;
    }

    private void clickCancel() {
        selenium.click("id=_eventId_cancel");
        waitForPageToLoad();
    }

    public ClientViewDetailsPage cancel() {
        clickCancel();
        return new ClientViewDetailsPage(selenium);
    }

    public CenterViewDetailsPage cancelAndNavigateToCenterViewDetailsPage() {
        clickCancel();
        return new CenterViewDetailsPage(selenium);
    }

    public GroupViewDetailsPage cancelAndNavigateToGroupViewDetailsPage() {
        clickCancel();
        return new GroupViewDetailsPage(selenium);
    }

    public LoanAccountPage cancelAndNavigateToLoanViewDetailsPage() {
        clickCancel();
        return new LoanAccountPage(selenium);
    }

    public SavingsAccountDetailPage cancelAndNavigateToSavingsAccountDetailPage() {
        clickCancel();
        return new SavingsAccountDetailPage(selenium);
    }

    public void verifyTextPresent(String expectedText, String errorMessage) {
        Assert.assertTrue(isTextPresentInPage(expectedText), errorMessage);
    }


    public void typeText(String locator, String value) {
        selenium.type(locator,value);
    }
}
