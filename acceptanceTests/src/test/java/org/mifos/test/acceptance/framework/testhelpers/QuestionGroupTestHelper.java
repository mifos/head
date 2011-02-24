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

package org.mifos.test.acceptance.framework.testhelpers;

import java.util.List;
import java.util.Map;

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.questionnaire.AttachQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.EditQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewAllQuestionGroupsPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionDetailPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewAllQuestionsPage;
import org.mifos.test.acceptance.framework.questionnaire.EditQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionGroupDetailPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;

import com.thoughtworks.selenium.Selenium;

import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.AttachSurveyPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.office.CreateOfficePreviewDataPage;
import org.mifos.test.acceptance.framework.office.OfficeViewDetailsPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionGroupResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.testng.Assert;

public class QuestionGroupTestHelper {

    private final NavigationHelper navigationHelper;
    private final Selenium selenium;

    public QuestionGroupTestHelper(Selenium selenium) {
        super();
        this.navigationHelper = new NavigationHelper(selenium);
        this.selenium = selenium;
    }

    public void createQuestionGroup(CreateQuestionGroupParameters createQuestionGroupParameters) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionGroupPage createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage();
        for (String section : createQuestionGroupParameters.getExistingQuestions().keySet()) {
            createQuestionGroupPage.addExistingQuestion(section, createQuestionGroupParameters.getExistingQuestions().get(section));
        }
        for (String section : createQuestionGroupParameters.getNewQuestions().keySet()){
            createQuestionGroupPage.setSection(section);
            for(CreateQuestionParameters createQuestionParameters : createQuestionGroupParameters.getNewQuestions().get(section)){
                createQuestionGroupPage.addNewQuestion(createQuestionParameters);
            }
        }
        createQuestionGroupPage.submit(createQuestionGroupParameters);
    }

    public void validatePageBlankMandatoryField() {
        CreateQuestionGroupPage createQuestionGroupPage = navigateToCreateQuestionGroupPage();
        createQuestionGroupPage.submit();
        String error = "Please specify Question Group title";
        createQuestionGroupPage.verifyTextPresent(error, "No text <"+ error +"> present on the page");
        error = "Please add at least one section";
        createQuestionGroupPage.verifyTextPresent(error, "No text <"+ error +"> present on the page");
        error = "Please choose a valid 'Applies To' value";
        createQuestionGroupPage.verifyTextPresent(error, "No text <"+ error +"> present on the page");
        createQuestionGroupPage.cancel();
        Assert.assertEquals(selenium.getAttribute("page.id@title"), AdminPage.PAGE_ID);
    }

    public void validateQuestionGroupTitle(List<String> titles) {
        navigationHelper.navigateToAdminPage().navigateToCreateQuestionGroupPage();
        for(String title : titles) {
            selenium.type("title", title);
            Assert.assertEquals(selenium.getValue("title"), title);
        }
    }

    public ClientViewDetailsPage attachQuestionGroup(String clientName, String questionGroupTitle, List<String> sections, Map<String,String> answers) {
        AttachSurveyPage attachSurveyPage = navigationHelper.navigateToClientViewDetailsPage(clientName).navigateToAttachSurveyPage();
        QuestionnairePage questionnairePage = attachSurveyPage.selectSurvey(questionGroupTitle);
        for(String section: sections) {
            questionnairePage.verifyTextPresent(section, clientName);
        }
        for(String question: answers.keySet()) {
            questionnairePage.setResponse(question, answers.get(question));
        }
        return (ClientViewDetailsPage)questionnairePage.submit();
    }

    public CenterViewDetailsPage attachQuestionGroupToCenter(AttachQuestionGroupParameters attachParams) {
        return (CenterViewDetailsPage) navigationHelper
            .navigateToCenterViewDetailsPage(attachParams.getTarget())
            .navigateToAttachSurveyPage()
            .verifyNoneSelected()
            .selectSurvey(attachParams.getQuestionGroupName())
            .verifyEmptyTextQuestionResponses(attachParams.getTextResponses())
            .verifyEmptyCheckQuestionResponses(attachParams.getCheckResponses())
            .setResponses(attachParams.getTextResponses())
            .checkResponses(attachParams.getCheckResponses())
            .submitAndNavigateToCenterViewDetailsPage();
    }

    public CenterViewDetailsPage editQuestionGroupResponsesInCenter(AttachQuestionGroupParameters attachParams) {
        CenterViewDetailsPage centerViewDetailsPage = (CenterViewDetailsPage) navigationHelper
            .navigateToCenterViewDetailsPage(attachParams.getTarget())
            .navigateToViewQuestionResponseDetailPage(attachParams.getQuestionGroupName())
            .navigateToEditSection("0")
            .setResponses(attachParams.getTextResponses())
            .checkResponses(attachParams.getCheckResponses())
            .submitAndNavigateToCenterViewDetailsPage();
        ViewQuestionResponseDetailPage viewQuestionResponseDetailPage = centerViewDetailsPage.navigateToViewQuestionResponseDetailPage(attachParams.getQuestionGroupName());
        viewQuestionResponseDetailPage.verifyQuestionsAndAnswers(attachParams);
        viewQuestionResponseDetailPage.navigateBack();
        return new CenterViewDetailsPage(selenium);
    }

    public CenterViewDetailsPage verifyErrorsWhileAttachingQuestionGroupToCenter(AttachQuestionGroupParameters attachParams) {
        QuestionnairePage questionnairePage = (QuestionnairePage) navigationHelper
            .navigateToCenterViewDetailsPage(attachParams.getTarget())
            .navigateToAttachSurveyPage()
            .selectSurvey(attachParams.getQuestionGroupName())
            .setResponses(attachParams.getTextResponses())
            .checkResponses(attachParams.getCheckResponses())
            .submitAndNavigateToCenterViewDetailsPage();
        questionnairePage.verifyErrorsOnPage(attachParams.getErrors());
        return questionnairePage.cancelAndNavigateToCenterViewDetailsPage();
    }

    public void createQuestions(List<CreateQuestionParameters> questions){
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage();
        for (CreateQuestionParameters question : questions) {
            createQuestionPage.addQuestion(question);
        }
        adminPage = createQuestionPage.submitQuestions();
    }

    public void addQuestionsToQuestionGroup(String questionGroup, Map<String, List<String>> existingQuestions){
        EditQuestionGroupPage editQuestionGroupPage = naviagateToEditQuestionGroup(questionGroup);
        for (String section : existingQuestions.keySet()) {
            editQuestionGroupPage.addExistingQuestion(section, existingQuestions.get(section));
        }
        editQuestionGroupPage.submit();
    }

    public void addQuestionsToQuestionGroup(int questionGroupId, Map<String, List<String>> existingQuestions){
        EditQuestionGroupPage editQuestionGroupPage = naviagateToEditQuestionGroup(questionGroupId);
        for (String section : existingQuestions.keySet()) {
            editQuestionGroupPage.addExistingQuestion(section, existingQuestions.get(section));
        }
        editQuestionGroupPage.submit();
    }

    public EditQuestionGroupPage naviagateToEditQuestionGroup(String questionGroup){
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewAllQuestionGroupsPage allQuestionGroupsPage = adminPage.navigateToViewAllQuestionGroups();
        QuestionGroupDetailPage questionGroupDetailPage = allQuestionGroupsPage.navigateToQuestionGroupDetailPage(questionGroup);
        return questionGroupDetailPage.navigateToEditPage();
    }

    public EditQuestionGroupPage naviagateToEditQuestionGroup(int questionGroupId){
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewAllQuestionGroupsPage allQuestionGroupsPage = adminPage.navigateToViewAllQuestionGroups();
        QuestionGroupDetailPage questionGroupDetailPage = allQuestionGroupsPage.navigateToQuestionGroupDetailPage(questionGroupId);
        return questionGroupDetailPage.navigateToEditPage();
    }

    private EditQuestionPage naviagateToEditQuestion(String question){
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewAllQuestionsPage viewAllQuestionsPage = adminPage.navigateToViewAllQuestions();
        QuestionDetailPage questionDetailPage = viewAllQuestionsPage.navigateToQuestionDetail(question);
        return questionDetailPage.navigateToEditQuestionPage();
    }

    public ViewAllQuestionGroupsPage navigateToViewQuestionGroups(List<String> questions) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewAllQuestionGroupsPage viewAllQuestionGroupsPage = adminPage.navigateToViewAllQuestionGroups();
        viewAllQuestionGroupsPage.verifyQuestionGroup(questions);
        return viewAllQuestionGroupsPage;
    }

    public CreateQuestionPage navigateToCreateQuestionPage() {
        return navigationHelper
                .navigateToAdminPage()
                .navigateToCreateQuestionPage();
    }

    public CreateQuestionGroupPage navigateToCreateQuestionGroupPage() {
        return navigationHelper
                .navigateToAdminPage()
                .navigateToCreateQuestionGroupPage();
    }

    public void markQuestionAsInactive(String question) {
        EditQuestionPage editQuestionPage = naviagateToEditQuestion(question);
        editQuestionPage.deactivate();
    }

    public void markQuestionAsActive(String question) {
        EditQuestionPage editQuestionPage = naviagateToEditQuestion(question);
        editQuestionPage.activate();
    }

    public void markQuestionGroupAsActive(String questionGroup) {
        EditQuestionGroupPage editQuestionGroupPage = naviagateToEditQuestionGroup(questionGroup);
        editQuestionGroupPage.activate();
    }

    public void markQuestionGroupAsInactive(String questionGroup) {
        EditQuestionGroupPage editQuestionGroupPage = naviagateToEditQuestionGroup(questionGroup);
        editQuestionGroupPage.deactivate();
    }

    public void addNewQuestionsToQuestionGroup(String questionGroupName, List<CreateQuestionParameters> questions) {
        EditQuestionGroupPage editQuestionGroupPage = navigationHelper
            .navigateToAdminPage()
            .navigateToViewAllQuestionGroups()
            .navigateToQuestionGroupDetailPage(questionGroupName)
            .navigateToEditPage();
        editQuestionGroupPage.addNewQuestions(questions);
        editQuestionGroupPage.submit();
    }

    public ViewAllQuestionsPage markQuestionsAsInactive(List<String> questions) {
        ViewAllQuestionsPage viewAllQuestionsPage = navigationHelper
            .navigateToAdminPage()
            .navigateToViewAllQuestions();
        for(String question : questions) {
            EditQuestionPage editQuestionPage = viewAllQuestionsPage
                .navigateToQuestionDetail(question)
                .navigateToEditQuestionPage();
            editQuestionPage.deactivate().navigateToViewAllQuestionsPage();
        }
        return viewAllQuestionsPage;
    }

    public OfficeViewDetailsPage createOfficeWithQuestionGroup(QuestionResponsePage questionResponsePage,
            QuestionResponseParameters responseParameters, QuestionResponseParameters responseParameters2) {
        questionResponsePage.populateAnswers(responseParameters);
        CreateOfficePreviewDataPage createOfficePreviewDataPage = questionResponsePage.navigateToNextPageAndReturnPage();
        QuestionResponsePage questionResponsePage2 = createOfficePreviewDataPage.editAdditionalInformation();
        questionResponsePage2.populateAnswers(responseParameters2);
        createOfficePreviewDataPage = questionResponsePage2.navigateToNextPageAndReturnPage();
        return createOfficePreviewDataPage.submit().navigateToOfficeViewDetailsPage();
    }

    public void editResponses(ClientViewDetailsPage clientViewDetailsPage, int id, Map<String,String> answers) {
        QuestionGroupResponsePage questionGroupResponsePage = clientViewDetailsPage.navigateToQuestionGroupResponsePage(id);
        QuestionnairePage questionnairePage = questionGroupResponsePage.navigateToEditResponses();
        for(String question: answers.keySet()) {
            questionnairePage.setResponse(question, answers.get(question));
        }
        ClientViewDetailsPage clientViewDetailsPage2 = (ClientViewDetailsPage)questionnairePage.submit();
        Assert.assertEquals(clientViewDetailsPage2.getQuestionGroupInstances().get(2).getName(),"TestQuestionGroup");
    }
}