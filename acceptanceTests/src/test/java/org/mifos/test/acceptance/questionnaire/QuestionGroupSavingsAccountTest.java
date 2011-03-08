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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.questionnaire.AttachQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"client", "acceptance", "ui", "no_db_unit"})
public class QuestionGroupSavingsAccountTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    private QuestionGroupTestHelper questionGroupTestHelper;
    private SavingsAccountHelper savingsAccountHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        savingsAccountHelper = new SavingsAccountHelper(selenium);
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
    //disabled due to MIFOS-4814

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    public void verifyCapturingResponsesDuringSavingsCreation() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 28, 15, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString("Stu1232993852651 Client1232993852651");
        searchParameters.setSavingsProduct("MySavingsProduct1233265923516");

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("248.0");

        QuestionResponseParameters questionResponseParameters = new QuestionResponseParameters();
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "04/02/2011");
        questionResponseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[1].valuesAsArray", "green");
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[2].value", "123");
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[3].value", "23");
        questionResponseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[1].questions[0].value", "yes");
        questionResponseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[1].questions[1].valuesAsArray", "Yes");

        questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[0].questions[0].value", "1");
        //questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[0].questions[1].valuesAsArray", "january");
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[2].value", "234");
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[3].value", "textquestion");
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[1].questions[0].value", "04/02/2011");
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[1].questions[1].value", "234");
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[1].questions[2].value", "234");

        QuestionResponseParameters questionResponseParameters2 = new QuestionResponseParameters();
        questionResponseParameters2.addTextAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "05/02/2011");
        questionResponseParameters2.addTextAnswer("questionGroups[1].sectionDetails[0].questions[3].value", "textQuestion");

        SavingsAccountDetailPage savingsAccountDetailPage = new SavingsAccountDetailPage(selenium);
        savingsAccountHelper.fillQuestionGroupsDurringCreationSavingsAccount(searchParameters, submitAccountParameters, questionResponseParameters);


        savingsAccountDetailPage = savingsAccountHelper.editAdditionalInformationDurringCreationSavingsAccount(questionResponseParameters2);
        String savingsId = savingsAccountDetailPage.getAccountId();

        questionGroupTestHelper.markQuestionGroupAsInactive("DateQuestion");
        questionGroupTestHelper.markQuestionGroupAsInactive("question 2");
        questionGroupTestHelper.markQuestionAsInactive("SingleSelectQuestion");
        questionGroupTestHelper.markQuestionAsInactive("SmartSelect");
        questionGroupTestHelper.markQuestionAsInactive("Text");
        questionGroupTestHelper.markQuestionAsInactive("TextQuestion");
        questionGroupTestHelper.markQuestionAsInactive("Date");
        questionGroupTestHelper.markQuestionGroupAsInactive("Number");
        questionGroupTestHelper.markQuestionGroupAsInactive("question 1");

        CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
        createQuestionParameters.setType(createQuestionParameters.TYPE_FREE_TEXT);
        createQuestionParameters.setText("newQuestion232");
        List<CreateQuestionParameters> newQuestionList = new ArrayList<CreateQuestionParameters>();
        newQuestionList.add(createQuestionParameters);
        questionGroupTestHelper.addNewQuestionsToQuestionGroup("CreateGroupQG", newQuestionList);

        Map<String, String> questionsAndAnswers = new HashMap<String, String>();
        questionsAndAnswers.put("MultiSelect", "third");
        questionsAndAnswers.put("NumberQuestion2", "10");
        questionsAndAnswers.put("question 2", "35");
        String[] questionsExist = {"newQuestion232", "MultiSelectQuestion", "NumberQuestion2", "question 4", "question 5"};
        String[] questionsInactive = {"DateQuestion", "question 2", "MultiSelectQuestion", "SingleSelectQuestion", "DateQuestion", "question 2"};


        QuestionResponsePage questionResponsePage = new QuestionResponsePage(selenium);
        questionResponsePage = savingsAccountHelper.navigateToQuestionResponseDuringCreateSavings(searchParameters, submitAccountParameters);
        questionResponsePage.verifyQuestionsExists(questionsExist);
        questionResponsePage.verifyQuestionsDoesnotappear(questionsInactive);

        verifyQuestionResponsesExistInDatabase(savingsId, "Disburse Loan", questionsAndAnswers);

    }

    private void verifyQuestionResponsesExistInDatabase(String savingsID, String event, Map<String, String> questions) throws SQLException {
        for (String question : questions.keySet()) {
            Assert.assertTrue(applicationDatabaseOperation.deosQuestionResponseForSavingsExist(savingsID, event, question, questions.get(question)));
        }
    }
}
