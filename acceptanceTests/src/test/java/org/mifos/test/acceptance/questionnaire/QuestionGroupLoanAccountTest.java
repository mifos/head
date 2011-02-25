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
//import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
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
@Test(groups = {"client", "acceptance", "ui"})
public class QuestionGroupLoanAccountTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private QuestionGroupTestHelper questionGroupTestHelper;
    private LoanTestHelper loanTestHelper;
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,2,24,15,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @AfterMethod
    public void logOut() {
        loanTestHelper = new LoanTestHelper(selenium);
      //  (new MifosPage(selenium)).logout();
    }

    /**
     * Capturing responses during the Loan disburse
     * http://mifosforge.jira.com/browse/MIFOSTEST-668
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyCapturingResponsesDuringLoanDisburse() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);

        CreateLoanAccountSearchParameters createLoanAccountSearchParameters = new CreateLoanAccountSearchParameters();
        createLoanAccountSearchParameters.setLoanProduct("WeeklyFlatLoanWithOneTimeFees");
        createLoanAccountSearchParameters.setSearchString("Stu1232993852651 Client1232993852651");
        DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
        disburseLoanParameters.setPaymentType(DisburseLoanParameters.CASH);
        disburseLoanParameters.setDisbursalDateDD("24");
        disburseLoanParameters.setDisbursalDateMM("02");
        disburseLoanParameters.setDisbursalDateYYYY("2011");
        String loan1ID = null;
        String loan2ID = "000100000000004";
        QuestionResponseParameters questionResponseParameters = new QuestionResponseParameters();
        questionResponseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[0].valuesAsArray", "third");
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[1].value", "20");
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[2].value", "10");
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[3].value", "answerGaga");
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[4].value", "30");
        questionResponseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[1].questions[0].value", "bad");
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[0].value", "04/02/2011");
        questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[0].questions[1].valuesAsArray", "green");
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[1].questions[1].value", "07/02/2011");
        questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[4].value", "2");
        CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
        createQuestionParameters.setType(createQuestionParameters.TYPE_FREE_TEXT);
        createQuestionParameters.setText("newQuestion232");
        List<CreateQuestionParameters> newQuestionList = new ArrayList<CreateQuestionParameters>();
        newQuestionList.add(createQuestionParameters);
        String[] questionsExist = {"newQuestion232", "MultiSelect", "NumberQuestion2", "question 2"};
        String[] questionsInactive = {"SingleSelect", "TextQuestion", "MultiSelectQuestion", "SingleSelectQuestion", "DateQuestion"};
        Map<String, String> questionsAndAnswers = new HashMap<String, String>();
        questionsAndAnswers.put("MultiSelect", "third");
        questionsAndAnswers.put("NumberQuestion2", "10");
        questionsAndAnswers.put("question 2", "35");

        LoanAccountPage loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(createLoanAccountSearchParameters);
        loan1ID = loanAccountPage.getAccountId();

        QuestionResponsePage questionResponsePage = questionGroupTestHelper.navigateToQuestionResponsePageDuringLoanDisbursal(loan1ID, disburseLoanParameters);
        questionResponsePage.populateAnswers(questionResponseParameters);
        questionResponsePage = questionResponsePage
            .continueAndNavigateToDisburseLoanConfirmationPage()
            .navigateToEditAdditionalInformation();
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[3].value", "answer1111");
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[4].value", "35");
        questionResponsePage.populateAnswers(questionResponseParameters);
        questionResponsePage
            .continueAndNavigateToDisburseLoanConfirmationPage()
            .submitAndNavigateToLoanAccountPage();

        questionGroupTestHelper.markQuestionAsInactive("question 1");
        questionGroupTestHelper.markQuestionAsInactive("Number");
        questionGroupTestHelper.markQuestionAsInactive("SingleSelect");
        questionGroupTestHelper.markQuestionAsInactive("Text");
        questionGroupTestHelper.markQuestionAsInactive("TextQuestion");
        questionGroupTestHelper.markQuestionGroupAsInactive("DisburseLoanQG2");
        questionGroupTestHelper.addNewQuestionsToQuestionGroup("DisburseLoanQG", newQuestionList);

        questionResponsePage = questionGroupTestHelper.navigateToQuestionResponsePageDuringLoanDisbursal(loan2ID, disburseLoanParameters);
        questionResponsePage.verifyQuestionsExists(questionsExist);
        questionResponsePage.verifyQuestionsDoesnotappear(questionsInactive);

//        verifyQuestionResponsesExistInDatabase(loan1ID, "Disburse Loan", questionsAndAnswers);
    }

    public void verifyQuestionResponsesExistInDatabase(String loanID, String event, Map<String, String> questions) throws SQLException {
        for(String question : questions.keySet()) {
            Assert.assertTrue(applicationDatabaseOperation.deosQuestionResponseForLoanExist(loanID, event, question, questions.get(question)));
        }
    }
}
