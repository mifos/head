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
import org.mifos.test.acceptance.framework.account.AccountStatus;
import org.mifos.test.acceptance.framework.account.EditAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
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
@Test(singleThreaded = true, groups = {"client", "acceptance", "ui", "no_db_unit"})
public class QuestionGroupLoanAccountTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    private QuestionGroupTestHelper questionGroupTestHelper;
    private LoanTestHelper loanTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 24, 15, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);


    }

    @AfterMethod
    public void logOut() {

        (new MifosPage(selenium)).logout();
    }

    /**
     * Capturing responses during the Loan disburse
     * http://mifosforge.jira.com/browse/MIFOSTEST-668
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyCapturingResponsesDuringLoanDisburse() throws Exception {

        questionGroupTestHelper.markQuestionGroupAsActive("QGForDisburseLoan1");
        questionGroupTestHelper.markQuestionGroupAsActive("QGForDisburseLoan2");
        try {
            CreateLoanAccountSearchParameters createLoanAccountSearchParameters = new CreateLoanAccountSearchParameters();
            createLoanAccountSearchParameters.setLoanProduct("WeeklyClientFlatLoanWithNoFee");
            createLoanAccountSearchParameters.setSearchString("ClientWithLoan 20110221");
            DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
            disburseLoanParameters.setPaymentType(DisburseLoanParameters.CASH);
            disburseLoanParameters.setDisbursalDateDD("24");
            disburseLoanParameters.setDisbursalDateMM("02");
            disburseLoanParameters.setDisbursalDateYYYY("2011");
            QuestionResponseParameters questionResponseParameters = new QuestionResponseParameters();

            questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "04/02/2011");
            questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[1].value", "free text");
            questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[1].questions[0].value", "free text1");

            questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[0].value", "07/02/2011");
            questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[1].value", "20");
            questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[0].valuesAsArray", "three");
            questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[1].questions[1].value", "free text2");

            LoanAccountPage loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(createLoanAccountSearchParameters);
            String loan1ID = loanAccountPage.getAccountId();
            loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(createLoanAccountSearchParameters);
            String loan2ID = loanAccountPage.getAccountId();

            QuestionResponsePage questionResponsePage = questionGroupTestHelper.navigateToQuestionResponsePageDuringLoanDisbursal(loan1ID, disburseLoanParameters);
            questionResponsePage.populateAnswers(questionResponseParameters);
            questionResponsePage = questionResponsePage
                    .continueAndNavigateToDisburseLoanConfirmationPage()
                    .navigateToEditAdditionalInformation();
            questionResponseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[1].questions[1].value", "blue");
            questionResponsePage.populateAnswers(questionResponseParameters);
            questionResponsePage
                    .continueAndNavigateToDisburseLoanConfirmationPage()
                    .submitAndNavigateToLoanAccountPage();

            CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
            createQuestionParameters.setType(createQuestionParameters.TYPE_FREE_TEXT);
            createQuestionParameters.setText("createdByverifyCapturingResponsesDuringLoanDisburse");
            List<CreateQuestionParameters> newQuestionList = new ArrayList<CreateQuestionParameters>();
            newQuestionList.add(createQuestionParameters);
            String[] questionsExist = {"Date", "FreeText", "SingleSelect", "createdByverifyCapturingResponsesDuringLoanDisburse"};
            String[] questionsInactive = {"ToBeDisabled"};
            Map<String, String> questionsAndAnswers = new HashMap<String, String>();

            questionsAndAnswers.put("MultiSelect", "three");
            questionsAndAnswers.put("Number", "20");
            questionsAndAnswers.put("Date", "04/02/2011");

            questionGroupTestHelper.markQuestionAsInactive("ToBeDisabled");
            questionGroupTestHelper.markQuestionGroupAsInactive("QGForDisburseLoan2");
            questionGroupTestHelper.addNewQuestionsToQuestionGroup("QGForDisburseLoan1", newQuestionList);

            questionResponsePage = questionGroupTestHelper.navigateToQuestionResponsePageDuringLoanDisbursal(loan2ID, disburseLoanParameters);

            questionResponsePage.verifyQuestionsExists(questionsExist);
            questionResponsePage.verifyQuestionsDoesnotappear(questionsInactive);
            verifyQuestionResponsesExistInDatabase(loan1ID, "Disburse Loan", questionsAndAnswers);
        } finally {
            questionGroupTestHelper.markQuestionAsActive("ToBeDisabled");
            questionGroupTestHelper.markQuestionGroupAsInactive("QGForDisburseLoan1");
            questionGroupTestHelper.markQuestionGroupAsInactive("QGForDisburseLoan2");
        }
    }

    /**
     * Capturing responses during the creation of Loan account
     * http://mifosforge.jira.com/browse/MIFOSTEST-683
     *
     * @throws Exception
     */
    @Test(enabled = false)
    // TODO js - temporarily disabled broken test
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyResponsesDuringCreationOfLoanAccount() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);

        CreateQuestionGroupParameters createQuestionGroupParameters = getCreateQuestionGroupParameters1("createCenter", "Create Loan");
        createQuestionGroupParameters.setApplyToAllLoanProducts(true);
        CreateQuestionGroupParameters createQuestionGroupParameters2 = getCreateQuestionGroupParameters2("crcenter2", "Create Loan");
        createQuestionGroupParameters2.setApplyToAllLoanProducts(true);

        CreateLoanAccountSearchParameters createLoanAccountSearchParameters = new CreateLoanAccountSearchParameters();
        createLoanAccountSearchParameters.setLoanProduct("WeeklyFlatLoanWithOneTimeFees");
        createLoanAccountSearchParameters.setSearchString("Stu1232993852651 Client1232993852651");
        QuestionResponseParameters questionResponseParameters = new QuestionResponseParameters();
        questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "222");
        questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[0].questions[0].value", "no");
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[1].questions[0].value", "18/02/2011");
        questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[1].valuesAsArray", "second");
        CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
        createQuestionParameters.setType(createQuestionParameters.TYPE_FREE_TEXT);
        createQuestionParameters.setText("newQuestion232");
        List<CreateQuestionParameters> newQuestionList = new ArrayList<CreateQuestionParameters>();
        newQuestionList.add(createQuestionParameters);
        String[] questionsExist = {"newQuestion232", "DateQuestion"};
        String[] questionsInactive = {"question 2", "MultiSelect", "question 4"};

        questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);
        questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters2);

        QuestionResponsePage questionResponsePage = questionGroupTestHelper.navigateToQuestionResponsePageDuringLoanCreation(createLoanAccountSearchParameters);
        questionResponsePage.populateAnswers(questionResponseParameters);
        questionResponsePage = questionResponsePage
                .continueAndNavigateToCreateLoanAccountReviewInstallmentPage()
                .clickPreviewAndGoToReviewLoanAccountPage()
                .navigateToQuestionResponsePage();
        questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[1].questions[0].value", "11/02/2011");
        questionResponsePage.populateAnswers(questionResponseParameters);
        LoanAccountPage loanAccountPage = questionResponsePage
                .continueAndNavigateToCreateLoanAccountReviewInstallmentPage()
                .clickPreviewAndGoToReviewLoanAccountPage()
                .submit()
                .navigateToLoanAccountDetailsPage();
        String loanID = loanAccountPage.getAccountId();

        questionGroupTestHelper.markQuestionAsInactive("question 4");
        questionGroupTestHelper.markQuestionAsInactive("MultiSelect");
        questionGroupTestHelper.markQuestionGroupAsInactive("crcenter2");
        questionGroupTestHelper.addNewQuestionsToQuestionGroup("createCenter", newQuestionList);

        questionResponsePage = questionGroupTestHelper.navigateToQuestionResponsePageDuringLoanCreation(createLoanAccountSearchParameters);
        questionResponsePage.verifyQuestionsExists(questionsExist);
        questionResponsePage.verifyQuestionsDoesnotappear(questionsInactive);

        ViewQuestionResponseDetailPage viewQuestionResponseDetailPage = questionGroupTestHelper.navigateToLoanViewQuestionResponseDetailPage(loanID);
        viewQuestionResponseDetailPage.verifyQuestionPresent("newQuestion232", "");
        viewQuestionResponseDetailPage.verifyQuestionPresent("DateQuestion", "11/02/2011");
    }

    /**
     * Capturing responses while approving Loan account
     * http://mifosforge.jira.com/browse/MIFOSTEST-684
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyResponsesDuringLoanAccountApproval() throws Exception {
        questionGroupTestHelper.markQuestionGroupAsActive("QGForApproveLoan1");
        questionGroupTestHelper.markQuestionGroupAsActive("QGForApproveLoan2");
        try {

            CreateLoanAccountSearchParameters createLoanAccountSearchParameters = new CreateLoanAccountSearchParameters();
            createLoanAccountSearchParameters.setLoanProduct("WeeklyClientFlatLoanWithNoFee");
            createLoanAccountSearchParameters.setSearchString("ClientWithLoan 20110221");

            EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
            editAccountStatusParameters.setAccountStatus(AccountStatus.LOAN_APPROVED);
            editAccountStatusParameters.setNote("note note");

            QuestionResponseParameters questionResponseParameters = new QuestionResponseParameters();
            questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "04/02/2011");
            questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[1].value", "free text");
            questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[1].questions[0].value", "free text1");

            questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[0].value", "07/02/2011");
            questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[1].value", "20");
            questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[0].valuesAsArray", "three");
            questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[1].questions[1].value", "free text2");

            CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
            createQuestionParameters.setType(createQuestionParameters.TYPE_FREE_TEXT);
            createQuestionParameters.setText("questionByVerifyResponsesDuringLoanAccountApproval");
            List<CreateQuestionParameters> newQuestionList = new ArrayList<CreateQuestionParameters>();
            newQuestionList.add(createQuestionParameters);
            String[] questionsExist = {"Date", "FreeText", "SingleSelect", "questionByVerifyResponsesDuringLoanAccountApproval"};
            String[] questionsInactive = {"ToBeDisabled"};
            Map<String, String> questionsAndAnswers = new HashMap<String, String>();

            questionsAndAnswers.put("MultiSelect", "three");
            questionsAndAnswers.put("Number", "20");
            questionsAndAnswers.put("Date", "04/02/2011");

            LoanAccountPage loanAccountPage = loanTestHelper.createDefaultLoanAccount(createLoanAccountSearchParameters);
            String loanID1 = loanAccountPage.getAccountId();
            loanAccountPage = loanTestHelper.createDefaultLoanAccount(createLoanAccountSearchParameters);
            String loanID2 = loanAccountPage.getAccountId();

            QuestionResponsePage questionResponsePage = questionGroupTestHelper.navigateToQuestionResponsePageDuringLoanApproval(loanID1, editAccountStatusParameters);
            questionResponsePage.populateAnswers(questionResponseParameters);
            questionResponsePage = questionResponsePage
                    .continueAndNavigateToEditAccountStatusConfirmationPage()
                    .navigateToEditStatus()
                    .submitAndNavigateToQuestionResponsePage(editAccountStatusParameters);
            questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[1].questions[1].value", "blue");
            questionResponsePage.populateAnswers(questionResponseParameters);
            questionResponsePage
                    .continueAndNavigateToEditAccountStatusConfirmationPage()
                    .submitAndNavigateToLoanAccountPage();

            questionGroupTestHelper.markQuestionAsInactive("ToBeDisabled");
            questionGroupTestHelper.markQuestionGroupAsInactive("QGForApproveLoan2");
            questionGroupTestHelper.addNewQuestionsToQuestionGroup("QGForApproveLoan1", newQuestionList);

            questionResponsePage = questionGroupTestHelper.navigateToQuestionResponsePageDuringLoanApproval(loanID2, editAccountStatusParameters);
            questionResponsePage.verifyQuestionsExists(questionsExist);
            questionResponsePage.verifyQuestionsDoesnotappear(questionsInactive);

            verifyQuestionResponsesExistInDatabase(loanID1, "Approve Loan", questionsAndAnswers);
        } finally {
            questionGroupTestHelper.markQuestionAsActive("ToBeDisabled");
            questionGroupTestHelper.markQuestionGroupAsInactive("QGForApproveLoan1");
            questionGroupTestHelper.markQuestionGroupAsInactive("QGForApproveLoan2");
        }
    }

    private void verifyQuestionResponsesExistInDatabase(String loanID, String event, Map<String, String> questions) throws SQLException {
        for (String question : questions.keySet()) {
            Assert.assertTrue(applicationDatabaseOperation.deosQuestionResponseForLoanExist(loanID, event, question, questions.get(question)), "Can't find question '" + question + "' and answer '" + questions.get(question) + "' in database");
        }
    }

    private CreateQuestionGroupParameters getCreateQuestionGroupParameters1(String name, String applied) {
        List<String> group1sec1 = new ArrayList<String>();
        group1sec1.add("DateQuestion");
        group1sec1.add("MultiSelect");
        List<String> group1sec2 = new ArrayList<String>();
        group1sec2.add("question 4");
        Map<String, List<String>> existingQuestions1 = new HashMap<String, List<String>>();
        existingQuestions1.put("sec 1", group1sec1);
        existingQuestions1.put("sec 2", group1sec2);
        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.setTitle(name);
        createQuestionGroupParameters.setAnswerEditable(true);
        createQuestionGroupParameters.setExistingQuestions(existingQuestions1);
        createQuestionGroupParameters.setAppliesTo(applied);
        return createQuestionGroupParameters;
    }

    private CreateQuestionGroupParameters getCreateQuestionGroupParameters2(String name, String applied) {
        Map<String, List<String>> existingQuestions2 = new HashMap<String, List<String>>();
        List<String> group2sec1 = new ArrayList<String>();
        group2sec1.add("question 2");
        existingQuestions2.put("section 1", group2sec1);
        CreateQuestionGroupParameters createQuestionGroupParameters2 = new CreateQuestionGroupParameters();
        createQuestionGroupParameters2.setTitle(name);
        createQuestionGroupParameters2.setAnswerEditable(true);
        createQuestionGroupParameters2.setExistingQuestions(existingQuestions2);
        createQuestionGroupParameters2.setAppliesTo(applied);
        return createQuestionGroupParameters2;
    }
}
