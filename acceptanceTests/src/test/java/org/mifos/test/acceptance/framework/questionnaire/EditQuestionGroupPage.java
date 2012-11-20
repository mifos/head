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

import java.util.List;

public class EditQuestionGroupPage extends CreateQuestionGroupRootPage {

    public EditQuestionGroupPage(Selenium selenium) {
        super(selenium);
        verifyPage("editQuestionGroup");
    }

    public QuestionGroupDetailPage submit() {
        selenium.click("id=_eventId_defineQuestionGroup");
        waitForPageToLoad();
        return new QuestionGroupDetailPage(selenium);
    }

    public QuestionGroupDetailPage activate() {
        selenium.click("id=active0");
        return submit();
    }

    public QuestionGroupDetailPage deactivate() {
        selenium.click("id=active1");
        return submit();
    }

    /* TODO: if needed add more types to add. For now it is only Free Text questions */
    public void addNewQuestions(List<CreateQuestionParameters> questions) {
        selenium.click("addQuestionFlag1");
        for(CreateQuestionParameters question : questions) {
            selenium.type("currentQuestion.text", question.getText());
            selenium.select("currentQuestion.type", question.getType());
            selenium.click("_eventId_addQuestion");
            waitForPageToLoad();
        }
    }

    private void setAppliesTo(String[] eventList){
        for (String event : eventList) {
            selenium.addSelection("eventSourceIds", "label=" + event);
        }
    }

    public QuestionGroupDetailPage changeAppliesTo(String[] eventList){
        setAppliesTo(eventList);
        return submit();
    }

    public QuestionGroupDetailPage editQuestionGroup(boolean active, String title,
        String appliesTo, List<String> questionsId) {

        if(active) {
            selenium.click("id=active0");
        }
        else {
            selenium.click("id=active1");
        }
        selenium.getEval("window.document.getElementsByName('title')[0].value='"+title+"'");
        selenium.select("eventSourceIds", appliesTo);

        for(String questionId: questionsId) {
            selenium.check("selectedQuestionIds value=" + questionId);
        }

        selenium.click("_eventId_defineQuestionGroup");
        waitForPageToLoad();

        return new QuestionGroupDetailPage(selenium);
    }

    public QuestionGroupDetailPage editQuestionGroup(String appliesTo) {
        selenium.select("eventSourceIds", appliesTo);
        selenium.click("_eventId_defineQuestionGroup");
        waitForPageToLoad();

        return new QuestionGroupDetailPage(selenium);
    }
    public EditQuestionGroupPage moveSectionUp(String nameSection) {
        selenium.click("moveSectionUp_"+nameSection.replace(" ", ""));
        waitForPageToLoad();

        return new EditQuestionGroupPage(selenium);
    }

    public EditQuestionGroupPage moveQuestionUp(int questionId) {
        selenium.click("moveQuestionUp_"+questionId);
        waitForPageToLoad();

        return new EditQuestionGroupPage(selenium);
    }

    public EditQuestionGroupPage moveQuestionDown(int questionId) {
        selenium.click("moveQuestionDown_"+questionId);
        waitForPageToLoad();

        return new EditQuestionGroupPage(selenium);
    }

    public boolean isApplayForAllLoansCheckboxChecked(){
        return selenium.isChecked("applyToAllLoanProducts");
    }

    public void setApplayForAllLoansCheckbox(boolean val){
        if (val){
            selenium.check("applyToAllLoanProducts");
        }
        else{
            selenium.uncheck("applyToAllLoanProducts");
        }
    }
}
