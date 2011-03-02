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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import com.thoughtworks.selenium.Selenium;

import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.office.OfficeViewDetailsPage;

import static org.junit.Assert.assertTrue;

public class ViewQuestionResponseDetailPage extends MifosPage {

    private Map<String, List<String>> questionMap = null;

    public ViewQuestionResponseDetailPage(Selenium selenium) {
        super(selenium);
        verifyPage("display_question_group_responses", "display_question_group_reponse");
    }

    private Map<String, List<String>> loadQuestionsAndAnswers() {
        Map<String, List<String>> questions = new HashMap<String, List<String>>();
        for(int i = 0; true; i++) {
            if(!selenium.isElementPresent(getQuestionLocator(i, 0))) {
                break;
            }
            for(int j = 0; true; j++) {
                if(!selenium.isElementPresent(getQuestionLocator(i, j))) {
                    break;
                }
                questions.put(selenium.getText(getQuestionLocator(i, j)), getQuestionAnswers(i, j));
            }
        }
        return questions;
    }

    private List<String> getQuestionAnswers(int section, int question) {
        List<String> answers = new ArrayList<String>();
        if(selenium.isElementPresent(getSingleAnswerLocator(section, question))) {
            answers.add(selenium.getText(getSingleAnswerLocator(section, question)));
        }
        else {
            for(int i = 0; true; i++) {
                if(!selenium.isElementPresent(getMultiAnswersLocator(section, question, i))) {
                    break;
                }
                answers.add(selenium.getText(getMultiAnswersLocator(section, question, i)));
            }
        }
        return answers;
    }

    private String getQuestionLocator(int section, int question) {
        return "displayQuestionGroupReponse.text.section["+section+"].question["+question+"].questionName";
    }

    private String getSingleAnswerLocator(int section, int question) {
        return "displayQuestionGroupReponse.text.section["+section+"].question["+question+"].questionAnswer";
    }

    private String getMultiAnswersLocator(int section, int question, int answer) {
        return "displayQuestionGroupReponse.text.section["+section+"].question["+question+"].questionAnswer["+answer+"]";
    }

    public void verifyPage() {
        super.verifyPage("display_question_group_responses", "display_question_group_reponse");
    }

    public void verifyQuestionPresent(String question, String... answers) {
        assertTrue(selenium.isTextPresent(question));
        for (String answer : answers) {
            assertTrue(selenium.isTextPresent(answer));
        }
    }

    public void verifyQuestionsAndAnswers(AttachQuestionGroupParameters attachParams) {
        if(this.questionMap == null) {
            this.questionMap = loadQuestionsAndAnswers();
        }
        Map<String, String> textAnswers = attachParams.getTextResponses();
        Map<String, List<String>> checkAnswers = attachParams.getCheckResponses();
        for(String question : textAnswers.keySet()) {
            verifyAnswerForQuestion(question, textAnswers.get(question));
        }
        for(String question : checkAnswers.keySet()) {
            for(String answer : checkAnswers.get(question)) {
                verifyAnswerForQuestion(question, answer);
            }
        }
    }

    public void verifyAnswerForQuestion(String question, String answer) {
        List<String> answers = questionMap.get(question);
        Assert.assertNotNull(answers);
        boolean exist = false;
        String answersFound = "";
        for(String listAnswer : answers) {
            answersFound = answersFound.concat(" "+listAnswer);
            if(listAnswer.equals(answer)) {
                exist = true;
                break;
            }
        }
        Assert.assertTrue("Failed to find response for question: '"+question+"' answer '"+answer+"' \n FOUND: "+answersFound, exist);
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
