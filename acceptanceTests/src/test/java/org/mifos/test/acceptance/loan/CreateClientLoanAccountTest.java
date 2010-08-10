/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.test.acceptance.loan;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationParameters;
import org.mifos.test.acceptance.framework.loan.EditPreviewLoanAccountPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"loan","acceptance","ui"})
public class CreateClientLoanAccountTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    private NavigationHelper navigationHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    private Random random;
    public static final String DATE = "Date";
    public static final String SINGLE_SELECT = "Single Select";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
        random = new Random();
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @Test(sequential = true, groups = {"smoke"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void newWeeklyClientLoanAccountWithQuestionGroups() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml.zip", dataSource, selenium);
        String questionGroupTitle = "QG1" + random.nextInt(100);
        String question1 = "DT_" + random.nextInt(100);
        String question2 = "SS_" + random.nextInt(100);
        String answer = "01/01/2010";
        String choiceAnswer = "Choice2";
        createQuestionGroupForCreateLoan(questionGroupTitle, question1, question2);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Veronica Abisya");
        searchParameters.setLoanProduct("Flat Interest Loan Product With Fee");
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1012.0");
        QuestionResponseParameters parameters = new QuestionResponseParameters();
        parameters.addTextAnswer("create_ClientPersonalInfo.input.customField", answer);
        parameters.addSingleSelectAnswer("name=questionGroups[0].sectionDetails[0].questions[1].value", choiceAnswer);

        LoanAccountPage loanAccountPage = createLoanAccount(searchParameters, submitAccountParameters, parameters);
        ViewQuestionResponseDetailPage questionResponseDetailPage = loanAccountPage.navigateToAdditionalInformationPage();
        questionResponseDetailPage.verifyPage();
        questionResponseDetailPage.verifyQuestionPresent(question1, answer);
        questionResponseDetailPage.verifyQuestionPresent(question2, choiceAnswer);
        questionResponseDetailPage.navigateToDetailsPage();
        loanAccountPage.verifyPage();
    }

    private void createQuestionGroupForCreateLoan(String questionGroupTitle, String question1, String question2) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage().verifyPage();
        createQuestionPage.addQuestion(getCreateQuestionParams(question1, DATE, null));
        createQuestionPage.addQuestion(getCreateQuestionParams(question2, SINGLE_SELECT, asList("Choice1", "Choice2")));
        adminPage = createQuestionPage.submitQuestions();

        CreateQuestionGroupPage createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage().verifyPage();
        CreateQuestionGroupParameters parameters = getCreateQuestionGroupParameters(questionGroupTitle, question1, question2);
        createQuestionGroupPage.addSection(parameters);
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(asList(question1));
        createQuestionGroupPage.submit(parameters);
    }

    private CreateQuestionParameters getCreateQuestionParams(String title, String type, List<String> choices) {
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        parameters.setTitle(title);
        parameters.setType(type);
        parameters.setChoicesFromStrings(choices);
        return parameters;
    }

    private CreateQuestionGroupParameters getCreateQuestionGroupParameters(String questionGroupTitle, String question1, String question2) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(questionGroupTitle);
        parameters.setAppliesTo("Create Loan");
        parameters.setAnswerEditable(true);
        parameters.setSectionName("Default Section");
        parameters.setQuestions(asList(question1, question2));
        return parameters;
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void newWeeklyClientLoanAccountWithModifyErrors() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Veronica Abisya");
        searchParameters.setLoanProduct("Flat Interest Loan Product With Fee");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1012.0");
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml.zip", dataSource, selenium);

        String loanId = createLoanAndCheckAmount(searchParameters, submitAccountParameters, null);
        submitAccountParameters.setAmount("10666.0");
        EditLoanAccountInformationParameters editAccountParameters = new EditLoanAccountInformationParameters();
        editAccountParameters.setGracePeriod("15");
        EditPreviewLoanAccountPage editPreviewLoanAccountPage = tryToEditLoan(loanId, submitAccountParameters, editAccountParameters);
        editPreviewLoanAccountPage.verifyErrorInForm("Please specify valid Amount. Amount should be a value between 1 and 10,000, inclusive");
        editPreviewLoanAccountPage.verifyErrorInForm("Please specify valid Grace period for repayments. Grace period for repayments should be a value less than 12");
    }

    @Test(sequential = true, groups = {"loan","acceptance","ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newWeeklyClientLoanAccountWithDateTypeCustomField() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Veronica Abisya");
        searchParameters.setLoanProduct("Flat Interest Loan Product With Fee");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1012.0");
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_014_dbunit.xml.zip", dataSource, selenium);

        String loanId = createLoanAndCheckAmount(searchParameters, submitAccountParameters, null);

        submitAccountParameters.setAmount("1666.0");
        EditLoanAccountInformationParameters editAccountParameters = new EditLoanAccountInformationParameters();
        editAccountParameters.setGracePeriod("5");
        EditPreviewLoanAccountPage editPreviewLoanAccountPage = tryToEditLoan(loanId, submitAccountParameters, editAccountParameters);
        LoanAccountPage loanAccountPage = editPreviewLoanAccountPage.submitAndNavigateToLoanAccountPage();
        loanAccountPage.verifyPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void newMonthlyClientLoanAccountWithMeetingOnSpecificDayOfMonth() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct("MonthlyClientFlatLoan1stOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1234.0");
        submitAccountParameters.setGracePeriodTypeNone(true);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml.zip", dataSource, selenium);

        createLoanAndCheckAmount(searchParameters, submitAccountParameters, null);
    }

    @Test(sequential = true, groups = {"loan","acceptance","ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void newMonthlyClientLoanAccountWithMeetingOnSameWeekAndWeekdayOfMonth() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mia Monthly3rdFriday");
        searchParameters.setLoanProduct("MonthlyClientFlatLoanThirdFridayOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("2765.0");
        submitAccountParameters.setGracePeriodTypeNone(true);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml.zip", dataSource, selenium);

        createLoanAndCheckAmount(searchParameters, submitAccountParameters, null);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @Test(sequential = true, groups = {"loan","acceptance","ui"})
    public void newMonthlyClientLoanAccountWithZeroInterestRate() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Tesa Mendez");
        searchParameters.setLoanProduct("MIFOS-2636-GKEmergencyLoanWithZeroInterest");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1000");
        submitAccountParameters.setGracePeriodTypeNone(true);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_010_dbunit.xml.zip", dataSource, selenium);

        createLoanAndCheckAmount(searchParameters, submitAccountParameters, null);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void tryClientLoanAccountWithAdditionalFees() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Tesa Mendez");
        searchParameters.setLoanProduct("MIFOS-2636-GKEmergencyLoanWithZeroInterest");

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_010_dbunit.xml.zip", dataSource, selenium);

        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

        loanAccountEntryPage.selectAdditionalFees();
        loanAccountEntryPage.clickContinue();

        HomePage homePage = loanAccountEntryPage.navigateToHomePage();
        homePage.verifyPage();
        loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPageWithoutLogout(homePage, searchParameters);
        loanAccountEntryPage.verifyAdditionalFeesAreEmpty();
    }

    private String createLoanAndCheckAmount(CreateLoanAccountSearchParameters searchParameters,
                                            CreateLoanAccountSubmitParameters submitAccountParameters,
                                            QuestionResponseParameters questionResponseParameters) {
        LoanAccountPage loanAccountPage = createLoanAccount(searchParameters, submitAccountParameters, questionResponseParameters);
        loanAccountPage.verifyLoanAmount(submitAccountParameters.getAmount());
        return loanAccountPage.getAccountId();
    }

    private LoanAccountPage createLoanAccount(CreateLoanAccountSearchParameters searchParameters,
                                              CreateLoanAccountSubmitParameters submitAccountParameters,
                                              QuestionResponseParameters questionResponseParameters) {
        LoanAccountPage loanAccountPage;
        if (questionResponseParameters == null) {
            loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        } else {
            loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters, questionResponseParameters);
        }
        loanAccountPage.verifyPage();
        return loanAccountPage;
    }

    private EditPreviewLoanAccountPage tryToEditLoan(String loanId, CreateLoanAccountSubmitParameters submitAccountParameters, EditLoanAccountInformationParameters editAccountParameters) {
        return loanTestHelper.changeLoanAccountInformation(loanId, submitAccountParameters, editAccountParameters);
    }
}
