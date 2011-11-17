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

package org.mifos.test.acceptance.questionnaire;

import static java.util.Arrays.asList;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.account.EditAccountStatusParameters;
import org.mifos.test.acceptance.framework.account.AccountStatus;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.questionnaire.AttachQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"client", "acceptance", "ui", "no_db_unit"})
public class QuestionGroupSavingsAccountTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    private QuestionGroupTestHelper questionGroupTestHelper;
    private SavingsAccountHelper savingsAccountHelper;
    private ClientTestHelper clientTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        savingsAccountHelper = new SavingsAccountHelper(selenium);
        clientTestHelper = new ClientTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    /**
     * Attaching a Question Group to a Savings Account and capturing responses
     * http://mifosforge.jira.com/browse/MIFOSTEST-659
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAttachingQuestionGroupToSavingsAccount() throws Exception {
        //Given
        questionGroupTestHelper.markQuestionGroupAsActive("QGForViewSavings");
        try {
            AttachQuestionGroupParameters attachParams = new AttachQuestionGroupParameters();
            attachParams.setTarget("000100000000059");
            attachParams.setQuestionGroupName("QGForViewSavings");
            attachParams.addTextResponse("DateQuestion", "09/02/2011");
            attachParams.addTextResponse("Number", "10");
            attachParams.addTextResponse("NumberBetween5And10", "6");

            AttachQuestionGroupParameters attachErrorParams = new AttachQuestionGroupParameters();
            attachErrorParams.setTarget("000100000000059");
            attachErrorParams.setQuestionGroupName("QGForViewSavings");
            attachErrorParams.addTextResponse("Number", "qwerty");
            attachErrorParams.addTextResponse("NumberBetween5And10", "qwerty");

            attachErrorParams.addError("Please specify DateQuestion");
            attachErrorParams.addError("Please specify a number for Number");
            attachErrorParams.addError("Please specify a number between 5 and 10 for NumberBetween5And10");

            //When
            questionGroupTestHelper.verifyErrorsWhileAttachingQuestionGroupToSavingsAccount(attachErrorParams);
            questionGroupTestHelper.attachQuestionGroupToSavingsAccount(attachParams);
            attachParams.addTextResponse("Number", "15");
            attachParams.addTextResponse("NumberBetween5And10", "10");
            //Then
            questionGroupTestHelper.editQuestionGroupResponsesInSavingsAccount(attachParams);
        } finally {
            questionGroupTestHelper.markQuestionGroupAsInactive("QGForViewSavings");
        }
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-669
    /*
     * need to fix up commonality between questionaire pages for loan and savings.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void verifyCapturingResponsesDuringSavingsCreation() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 28, 15, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        setQuestionGroup();
        createClient("669");

        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString("Joe669 Doe669");
        searchParameters.setSavingsProduct("MonthlyClientSavingsAccount");

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("248.0");

        QuestionResponseParameters questionResponseParameters = new QuestionResponseParameters();

        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[0].value", "textquestion");
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[1].value", "100");
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[2].value", "Text");
        questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[0].questions[3].value", "blue");

        questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[0].values", "two");
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[1].questions[1].value", "6");

        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "04/02/2011");
        questionResponseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[1].values", "one");
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[2].value", "123");
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[3].value", "7");

        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[1].questions[0].value", "Text");
        questionResponseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[1].questions[1].value", "red");

        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[2].questions[0].value", "Text");

        QuestionResponseParameters questionResponseParameters2 = new QuestionResponseParameters();
        questionResponseParameters2.addTextAnswer("questionGroups[1].sectionDetails[0].questions[0].value", "textQuestion");
        questionResponseParameters2.addTextAnswer("questionGroups[0].sectionDetails[0].questions[3].value", "9");

        savingsAccountHelper.fillQuestionGroupsDurringCreationSavingsAccount(searchParameters, submitAccountParameters, questionResponseParameters);


        SavingsAccountDetailPage savingsAccountDetailPage = savingsAccountHelper.editAdditionalInformationDurringCreationSavingsAccount(questionResponseParameters2);
        String savingsId = savingsAccountDetailPage.getAccountId();

        questionGroupTestHelper.markQuestionGroupAsInactive("QGForCreateSavingsAccount");
        questionGroupTestHelper.markQuestionAsInactive("SingleSelect");
        questionGroupTestHelper.markQuestionAsInactive("NumberBetween5And10");
        questionGroupTestHelper.markQuestionAsInactive("Question1");
        questionGroupTestHelper.markQuestionAsInactive("question 1");

        CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        createQuestionParameters.setText("newQuestion232");
        List<CreateQuestionParameters> newQuestionList = new ArrayList<CreateQuestionParameters>();
        newQuestionList.add(createQuestionParameters);
        questionGroupTestHelper.addNewQuestionsToQuestionGroup("QGForCreateSavingsAccount2", newQuestionList);

        Map<String, String> questionsAndAnswers = new HashMap<String, String>();
        questionsAndAnswers.put("MultiSelect", "two");
        questionsAndAnswers.put("Number", "100");
        questionsAndAnswers.put("FreeText", "textquestion");
        String[] questionsExist = {"newQuestion232", "MultiSelect", "Number", "FreeText"};
        String[] questionsInactive = {"SingleSelect", "NumberBetween5And10", "Question1", "question 1", "Date"};


        QuestionnairePage questionnairePage = savingsAccountHelper.navigateToQuestionResponseDuringCreateSavings(searchParameters, submitAccountParameters);
        questionnairePage.verifyQuestionsExists(questionsExist);
        questionnairePage.verifyQuestionsDoesnotappear(questionsInactive);

        verifyQuestionResponsesExistInDatabase(savingsId, "Create Savings", questionsAndAnswers);

        questionGroupTestHelper.markQuestionGroupAsInactive("QGForCreateSavingsAccount2");
        questionGroupTestHelper.markQuestionAsActive("SingleSelect");
        questionGroupTestHelper.markQuestionAsActive("NumberBetween5And10");
        questionGroupTestHelper.markQuestionAsActive("Question1");
        questionGroupTestHelper.markQuestionAsActive("question 1");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void verifyCapturingResponsesDuringSavingsClosing() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 28, 15, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        questionGroupTestHelper.markQuestionGroupAsActive("QGForCloseSavings");
        try {
            createClient("670");

            CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
            searchParameters.setSearchString("Joe670 Doe670");
            searchParameters.setSavingsProduct("MonthlyClientSavingsAccount");

            CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
            submitAccountParameters.setAmount("248.0");

            QuestionResponseParameters questionResponseParameters = new QuestionResponseParameters();

            questionResponseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[0].valuesAsArray", "three");
            questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[1].value", "20");

            String savingsId = savingsAccountHelper.createSavingsAccount(searchParameters, submitAccountParameters).getAccountId();

            EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
            editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
            editAccountStatusParameters.setNote("test");
            savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

            String[] questionsExist = {"MultiSelect", "Number"};
            savingsAccountHelper.fillQuestionGroupsDuringClosingSavingsAccount(savingsId, questionResponseParameters, questionsExist);

            Map<String, String> questionsAndAnswers = new HashMap<String, String>();
            questionsAndAnswers.put("MultiSelect", "three");
            questionsAndAnswers.put("Number", "20");

            verifyQuestionResponsesExistInDatabase(savingsId, "Close Savings", questionsAndAnswers);

        } finally {
            questionGroupTestHelper.markQuestionGroupAsInactive("QGForCloseSavings");
        }
    }

    private void createClient(String postfix) {
        String groupName = "group1";
        CreateClientEnterPersonalDataPage.SubmitFormParameters clientParams = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        clientParams.setSalutation(CreateClientEnterPersonalDataPage.SubmitFormParameters.MRS);
        clientParams.setFirstName("Joe" + postfix);
        clientParams.setLastName("Doe" + postfix);
        clientParams.setDateOfBirthDD("17");
        clientParams.setDateOfBirthMM("11");
        clientParams.setDateOfBirthYYYY("1977");
        clientParams.setGender(CreateClientEnterPersonalDataPage.SubmitFormParameters.MALE);
        clientParams.setPovertyStatus(CreateClientEnterPersonalDataPage.SubmitFormParameters.NOT_POOR);
        clientParams.setSpouseNameType(CreateClientEnterPersonalDataPage.SubmitFormParameters.FATHER);
        clientParams.setSpouseFirstName("fatherName");
        clientParams.setSpouseLastName("fatherLastName");
        clientTestHelper.createNewClient(groupName, clientParams);
        clientTestHelper.activateClient("Joe" + postfix + " Doe" + postfix);
    }

    private void verifyQuestionResponsesExistInDatabase(String savingsID, String event, Map<String, String> questions) throws SQLException {
        for (String question : questions.keySet()) {
            Assert.assertTrue(applicationDatabaseOperation.deosQuestionResponseForSavingsExist(savingsID, event, question, questions.get(question)));
        }
    }

    private void setQuestionGroup() {
        String questionGroupName = "QGForCreateSavingsAccount";
        questionGroupTestHelper.markQuestionGroupAsActive(questionGroupName);
        CreateQuestionGroupParameters createQuestionGroupParameters = questionGroupTestHelper.getCreateQuestionGroupParameters(questionGroupName+"2",
                asList("Number", "SingleSelect", "FreeText" , "question 1"), "Create Savings", "Sec 1");
        createQuestionGroupParameters.addExistingQuestion("Sec 2", "NumberBetween5And10");
        createQuestionGroupParameters.addExistingQuestion("Sec 2", "MultiSelect");

        questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);
        Map<String, List<String>> sectionQuestions = new HashMap<String, List<String>>();

        List<String> questions = new ArrayList<String>();

        questions.add("Date");
        questions.add("MultiSelect");
        questions.add("Number");
        questions.add("NumberBetween5And10");
        sectionQuestions.put("Sec 1", questions);

        questions = new ArrayList<String>();
        questions.add("SingleSelect");
        questions.add("FreeText");

        sectionQuestions.put("Sec 2", questions);

        questionGroupTestHelper.addQuestionsToQuestionGroup(questionGroupName, sectionQuestions);
    }
}
