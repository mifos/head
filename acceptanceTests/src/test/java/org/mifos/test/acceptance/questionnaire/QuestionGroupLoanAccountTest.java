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
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.account.AccountStatus;
import org.mifos.test.acceptance.framework.account.EditAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountPreviewPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"client", "acceptance", "ui", "no_db_unit", "smoke"})
public class QuestionGroupLoanAccountTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    private QuestionGroupTestHelper questionGroupTestHelper;
    private LoanTestHelper loanTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun=true)
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
    /*
     * FIXME - keithw - disabled 
     */
    @Test(enabled=false)
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
            questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[0].values2", "three");
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
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyResponsesDuringCreationOfLoanAccount() throws Exception {
        CreateLoanAccountSearchParameters createLoanAccountSearchParameters = new CreateLoanAccountSearchParameters();
        createLoanAccountSearchParameters.setLoanProduct("WeeklyClientFlatLoanWithNoFee");
        createLoanAccountSearchParameters.setSearchString("ClientWithLoan 20110221");
        
        CreateLoanAccountSubmitParameters formParameters = new CreateLoanAccountSubmitParameters();
        formParameters.setAdditionalFee1("oneTimeFee");
        formParameters.setAdditionalFee2("loanWeeklyFee");
        
        verifyQGNotDisplayed(createLoanAccountSearchParameters);
        questionGroupTestHelper.markQuestionGroupAsActive("QGForCreateLoan1");
        questionGroupTestHelper.markQuestionGroupAsActive("QGForCreateLoan2");
        try {
            QuestionResponseParameters questionResponseParameters = new QuestionResponseParameters();
            questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "04/02/2011");
            questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[1].value", "free text");
            questionResponseParameters.addTextAnswer("questionGroups[0].sectionDetails[1].questions[0].value", "free text1");

            questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[0].value", "07/02/2011");
            questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[0].questions[1].value", "20");
            questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[0].values", "three");
            questionResponseParameters.addTextAnswer("questionGroups[1].sectionDetails[1].questions[1].value", "free text2");

            CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
            createQuestionParameters.setType(createQuestionParameters.TYPE_FREE_TEXT);
            createQuestionParameters.setText("questionByVerifyResponsesDuringCreationOfLoanAccount");
            List<CreateQuestionParameters> newQuestionList = new ArrayList<CreateQuestionParameters>();
            newQuestionList.add(createQuestionParameters);

            String[] questionsExist = {"Date", "FreeText", "SingleSelect", "questionByVerifyResponsesDuringCreationOfLoanAccount"};
            String[] questionsInactive = {"ToBeDisabled"};

            Map<String, String> questionsAndAnswers = new HashMap<String, String>();
            questionsAndAnswers.put("MultiSelect", "three");
            questionsAndAnswers.put("Number", "20");
            questionsAndAnswers.put("Date", "04/02/2011");


            QuestionResponsePage questionResponsePage = questionGroupTestHelper.navigateToQuestionResponsePageDuringLoanCreation(createLoanAccountSearchParameters, formParameters);
            questionResponsePage.populateAnswers(questionResponseParameters);
            CreateLoanAccountPreviewPage createLoanAccountPreviewPage = questionResponsePage
                    .continueAndNavigateToCreateLoanAccountReviewInstallmentPage()
                    .clickPreviewAndGoToReviewLoanAccountPage();
            
            verifyCreateLoanAccountPreviewPage();
            
            questionResponsePage = createLoanAccountPreviewPage.navigateToQuestionResponsePage();
            
            questionResponseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[1].questions[1].value", "blue");
            
            questionResponsePage.populateAnswers(questionResponseParameters);
            LoanAccountPage loanAccountPage = questionResponsePage
                    .continueAndNavigateToCreateLoanAccountReviewInstallmentPage()
                    .clickPreviewAndGoToReviewLoanAccountPage()
                    .submit()
                    .navigateToLoanAccountDetailsPage();
            String loanID = loanAccountPage.getAccountId();
            
            verifyFees();
            
            ViewQuestionResponseDetailPage viewQuestionResponseDetailPage = questionGroupTestHelper.navigateToLoanViewQuestionResponseDetailPage(loanID);
            
            viewQuestionResponseDetailPage.verifyQuestionPresent("Date", "04/02/2011");
            viewQuestionResponseDetailPage.verifyQuestionPresent("ToBeDisabled", "free text");
            viewQuestionResponseDetailPage.verifyQuestionPresent("FreeText", "free text1");
            viewQuestionResponseDetailPage.verifyQuestionPresent("SingleSelect", "blue");
            viewQuestionResponseDetailPage.verifyQuestionPresent("DateQuestion", "07/02/2011");
            viewQuestionResponseDetailPage.verifyQuestionPresent("Number", "20");
            viewQuestionResponseDetailPage.verifyQuestionPresent("MultiSelect", "three");
            viewQuestionResponseDetailPage.verifyQuestionPresent("Text", "free text2");

            questionGroupTestHelper.markQuestionAsInactive("ToBeDisabled");
            questionGroupTestHelper.markQuestionGroupAsInactive("QGForCreateLoan2");
            questionGroupTestHelper.addNewQuestionsToQuestionGroup("QGForCreateLoan1", newQuestionList);

            questionResponsePage = questionGroupTestHelper.navigateToQuestionResponsePageDuringLoanCreation(createLoanAccountSearchParameters, null);
            questionResponsePage.verifyQuestionsExists(questionsExist);
            questionResponsePage.verifyQuestionsDoesnotappear(questionsInactive);

            viewQuestionResponseDetailPage = questionGroupTestHelper.navigateToLoanViewQuestionResponseDetailPage(loanID);
            viewQuestionResponseDetailPage.verifyQuestionPresent("MultiSelect", "three");
            viewQuestionResponseDetailPage.verifyQuestionPresent("Number", "20");
            viewQuestionResponseDetailPage.verifyQuestionPresent("Date", "04/02/2011");

            verifyQuestionResponsesExistInDatabase(loanID, "Create Loan", questionsAndAnswers);
        } finally {
            questionGroupTestHelper.markQuestionAsActive("ToBeDisabled");
            questionGroupTestHelper.markQuestionGroupAsInactive("QGForCreateLoan1");
            questionGroupTestHelper.markQuestionGroupAsInactive("QGForCreateLoan2");
        }
    }

    private void verifyQGNotDisplayed(CreateLoanAccountSearchParameters createLoanAccountSearchParameters) {
        loanTestHelper.navigateToCreateLoanAccountEntryPage(createLoanAccountSearchParameters)
            .clickContinue()
            .verifyPage();
    }
            
    private void verifyFees() {
        Assert.assertEquals(selenium.getText("xpath=//table[@id='loanSummaryTable']//tr[4]//td[2]"), "1010.0");
        Assert.assertEquals(selenium.getText("xpath=//table[@id='loanSummaryTable']//tr[4]//td[4]"), "1010.0");
    }
    
    private void verifyCreateLoanAccountPreviewPage() {
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary'][2]/div[@class='row'][1]/div[@class='value']"), "10,000 (Allowed amount: 1,000 - 10,000)");
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary'][2]/div[@class='row'][4]/div[@class='value']"), "25-Feb-2011");
        
        Assert.assertEquals(selenium.getText("xpath=//table[@id='installments.table']//tr[1]//td[2]"), "04-Mar-2011");
        Assert.assertEquals(selenium.getText("xpath=//table[@id='installments.table']//tr[1]//td[5]"), "110");
        Assert.assertEquals(selenium.getText("xpath=//table[@id='installments.table']//tr[1]//td[6]"), "1,156");
        
        Assert.assertEquals(selenium.getText("xpath=//table[@id='installments.table']//tr[4]//td[2]"), "25-Mar-2011");
        Assert.assertEquals(selenium.getText("xpath=//table[@id='installments.table']//tr[4]//td[5]"), "100");
        Assert.assertEquals(selenium.getText("xpath=//table[@id='installments.table']//tr[4]//td[6]"), "1,146");
        
        Assert.assertEquals(selenium.getText("xpath=//div[@class='summary']/div[@class='row']/div[@class='value']"), "04/02/2011");
        Assert.assertEquals(selenium.getText("xpath=//div[@class='summary']/div[@class='row'][2]/div[@class='value']"), "free text");
        Assert.assertEquals(selenium.getText("xpath=//div[@class='summary']/div[@class='row'][3]/div[@class='value']"), "free text1");
        Assert.assertEquals(selenium.getText("xpath=//div[@class='summary']/div[@class='row'][6]/div[@class='value']"), "07/02/2011");
        Assert.assertEquals(selenium.getText("xpath=//div[@class='summary']/div[@class='row'][7]/div[@class='value']"), "20");
        Assert.assertEquals(selenium.getText("xpath=//div[@class='summary']/div[@class='row'][8]/div[@class='value']"), "three");
        Assert.assertEquals(selenium.getText("xpath=//div[@class='summary']/div[@class='row'][9]/div[@class='value']"), "free text2");
    }
    
    /**
     * Capturing responses while approving Loan account
     * http://mifosforge.jira.com/browse/MIFOSTEST-684
     *
     * @throws Exception
     */
    /*
     * FIXME - keithw - disabled 
     */
    @Test(enabled=false)
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
            questionResponseParameters.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[0].values2", "three");
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
}