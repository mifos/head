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

package org.mifos.test.acceptance.loan;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountsSuccessPage;
import org.mifos.test.acceptance.framework.loan.CreateMultipleLoanAccountSelectParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class CreateClientLoanAccountTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    private LoanProductTestHelper loanProductTestHelper;
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
    private QuestionGroupHelper questionGroupHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
        questionGroupHelper = new QuestionGroupHelper(navigationHelper);
        random = new Random();
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "smoke"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newWeeklyClientLoanAccountWithQuestionGroups() throws Exception {
        String questionGroupTitle = "QG1" + random.nextInt(100);
        String question1 = "DT_" + random.nextInt(100);
        String question2 = "SS_" + random.nextInt(100);
        String answer = "01/01/2010";
        String choiceAnswer = "Choice2";
        questionGroupHelper.createQuestionGroup(questionGroupTitle, question1, question2, "Create Loan");
        loanProductTestHelper.editLoanProduct("Flat Interest Loan Product With Fee", questionGroupTitle);
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("client1 lastname");
        searchParameters.setLoanProduct("Flat Interest Loan Product With Fee");
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1012.0");
        QuestionResponseParameters parameters = new QuestionResponseParameters();
        parameters.addTextAnswer("create_ClientPersonalInfo.input.customField", answer);
        parameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[1].value", choiceAnswer);

        LoanAccountPage loanAccountPage = createLoanAccount(searchParameters, submitAccountParameters, parameters);
        ViewQuestionResponseDetailPage questionResponseDetailPage = loanAccountPage.navigateToAdditionalInformationPage();
        questionResponseDetailPage.verifyPage();
        questionResponseDetailPage.verifyQuestionPresent(question1, answer);
        questionResponseDetailPage.verifyQuestionPresent(question2, choiceAnswer);
        questionResponseDetailPage.navigateToDetailsPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // add grace period of 10 to "Flat Interest Loan Product With Fee"
    public void newWeeklyClientLoanAccountWithModifyErrors() throws Exception {
        setAppDate(new DateTime(2011, 3, 7, 15, 0, 0, 0));
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("client1 lastname");
        searchParameters.setLoanProduct("Flat Interest Loan Product With Fee");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1012.0");

        String loanId = createLoanAndCheckAmount(searchParameters, submitAccountParameters, null);
        submitAccountParameters.setAmount("10666.0");
        EditLoanAccountInformationParameters editAccountParameters = new EditLoanAccountInformationParameters();
        editAccountParameters.setGracePeriod("15");
        EditLoanAccountInformationPage editPreviewLoanAccountPage = loanTestHelper.changeLoanAccountInformationWithErrors(loanId, submitAccountParameters, editAccountParameters);
        editPreviewLoanAccountPage.verifyErrorInForm("Please specify valid Amount. Amount should be a value between 1 and 10,000, inclusive");
        editPreviewLoanAccountPage.verifyErrorInForm("Please specify valid Grace period for repayments. Grace period for repayments should be a value less than 12");
    }

    @Test(singleThreaded = true, groups = {"loan", "acceptance", "ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newWeeklyClientLoanAccountWithDateTypeCustomField() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("client1 lastname");
        searchParameters.setLoanProduct("Flat Interest Loan Product With Fee");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1012.0");

        String loanId = createLoanAndCheckAmount(searchParameters, submitAccountParameters, null);

        submitAccountParameters.setAmount("1666.0");
        EditLoanAccountInformationParameters editAccountParameters = new EditLoanAccountInformationParameters();
        editAccountParameters.setGracePeriod("5");
        loanTestHelper.changeLoanAccountInformation(loanId, submitAccountParameters, editAccountParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-308
    public void newMonthlyClientLoanAccountWithMeetingOnSpecificDayOfMonth() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct("MonthlyClientFlatLoan1stOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1234.0");
        submitAccountParameters.setGracePeriodTypeNone(true);
        //Then
        String loanId = createLoanAndCheckAmount(searchParameters, submitAccountParameters, null);
        getLoanStatusActive(loanId);
    }

    @Test(singleThreaded = true, groups = {"loan", "acceptance", "ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-308
    // (1,4,'test' to (1,4,'test','2011-02-01'
    public void newMonthlyClientLoanAccountWithMeetingOnSameWeekAndWeekdayOfMonth() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mia Monthly3rdFriday");
        searchParameters.setLoanProduct("MonthlyClientFlatLoanThirdFridayOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("2765.0");
        submitAccountParameters.setGracePeriodTypeNone(true);

        //Then
        String loanId = createLoanAndCheckAmount(searchParameters, submitAccountParameters, null);
        getLoanStatusActive(loanId);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(singleThreaded = true, groups = {"loan", "acceptance", "ui"})
    public void newMonthlyClientLoanAccountWithZeroInterestRate() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct("EmergencyLoanWithZeroInterest");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1000.0");
        submitAccountParameters.setGracePeriodTypeNone(true);

        createLoanAndCheckAmount(searchParameters, submitAccountParameters, null);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    //IGNORED BY VIVEK SINGH
    public void tryClientLoanAccountWithAdditionalFees() throws Exception {
        setDateAsToday();
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct("EmergencyLoanWithZeroInterest");


        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

        loanAccountEntryPage.selectAdditionalFees();

        // there should be an error
        loanAccountEntryPage.submit();
        loanAccountEntryPage.verifyError("Multiple instances of the same one-time fee are not allowed");

        // after unselect everything should pass
        loanAccountEntryPage.unselectAdditionalFee();
        loanAccountEntryPage.clickContinue();

        HomePage homePage = loanAccountEntryPage.navigateToHomePage();
        homePage.verifyPage();
        loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPageWithoutLogout(searchParameters);
        loanAccountEntryPage.verifyAdditionalFeesAreEmpty();
    }

    /**
     * Verify a user is prevented to create loan accounts of loan products restricted by the mix.
     * http://mifosforge.jira.com/browse/MIFOSTEST-94
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createLoanAccountsWithRestrictedProductsMix() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 1, 24, 15, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        CreateLoanAccountSearchParameters searchParams1 = new CreateLoanAccountSearchParameters();
        searchParams1.setSearchString("MyGroup1233266255641");
        searchParams1.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");
        CreateLoanAccountSearchParameters searchParams2 = new CreateLoanAccountSearchParameters();
        searchParams2.setSearchString("MyGroup1233266255641");
        searchParams2.setLoanProduct("WeeklyGroupDeclineLoanWithPeriodicFee");
        DisburseLoanParameters disburseParams = new DisburseLoanParameters();
        disburseParams.setPaymentType(DisburseLoanParameters.CASH);
        disburseParams.setDisbursalDateDD("24");
        disburseParams.setDisbursalDateMM("01");
        disburseParams.setDisbursalDateYYYY("2011");
        String error = "The loan could not be disbursed as " + searchParams1.getLoanProduct() + " and " + searchParams2.getLoanProduct() + " are not allowed to co-exist";

        LoanAccountPage loanAccountPage = loanTestHelper.createTwoLoanAccountsWithMixedRestricedPoducts(searchParams1, searchParams2, disburseParams);

        loanAccountPage.verifyError(error);

    }

    /**
     * Verify a user is prevented from creating a second loan account
     * (for two or more clients using the bulk loan creation pipeline)
     * with a loan product restricted with the first loan.
     * http://mifosforge.jira.com/browse/MIFOSTEST-95
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createMultipleLoanAccountsWithRestrictedProductsMix() throws Exception {
        setDateAsToday();

        CreateMultipleLoanAccountSelectParameters multipleAccParameters1 = new CreateMultipleLoanAccountSelectParameters();
        multipleAccParameters1.setBranch("MyOffice1233265929385");
        multipleAccParameters1.setLoanOfficer("Joe1233265931256 Guy1233265931256");
        multipleAccParameters1.setCenter("MyCenter1233265933427");
        multipleAccParameters1.setLoanProduct("WeeklyFlatLoanWithOneTimeFees");
        CreateMultipleLoanAccountSelectParameters multipleAccParameters2 = new CreateMultipleLoanAccountSelectParameters();
        multipleAccParameters2.setBranch("MyOffice1233265929385");
        multipleAccParameters2.setLoanOfficer("Joe1233265931256 Guy1233265931256");
        multipleAccParameters2.setCenter("MyCenter1233265933427");
        multipleAccParameters2.setLoanProduct("WeeklyClientDeclinetLoanWithPeriodicFee");
        DisburseLoanParameters disburseParams = new DisburseLoanParameters();
        disburseParams.setPaymentType(DisburseLoanParameters.CASH);
        disburseParams.setDisbursalDateDD("24");
        disburseParams.setDisbursalDateMM("01");
        disburseParams.setDisbursalDateYYYY("2011");
        String error = "The loan could not be disbursed as " + multipleAccParameters1.getLoanProduct() + " and " + multipleAccParameters2.getLoanProduct() + " are not allowed to co-exist";
        String[] clients = new String[3];
        clients[0] = "Stu1233265941610 Client1233265941610";
        clients[1] = "Stu1233265958456 Client1233265958456";
        clients[2] = "Stu1233265968663 Client1233265968663";

        CreateLoanAccountsSuccessPage createLoanAccountsSuccessPage = loanTestHelper.createMultipleLoanAccountsWithMixedRestricedPoducts(multipleAccParameters1, multipleAccParameters2, disburseParams, clients);
        List<String> accountNumbers = createLoanAccountsSuccessPage.verifyAndGetLoanAccountNumbers(clients.length);
        LoanAccountPage loanAccountPage = createLoanAccountsSuccessPage.selectLoansAndNavigateToLoanAccountPage(0);

        for (int i = 0; i < accountNumbers.size(); i++) {
            if (i > 0) {
                loanAccountPage = loanAccountPage.navigateToClientsAndAccountsUsingHeaderTab()
                        .searchForClient(accountNumbers.get(i))
                        .navigateToLoanAccountSearchResult("Account # " + accountNumbers.get(i));
            }
            loanAccountPage.changeAccountStatusToAccepted();
            loanAccountPage.tryNavigatingToDisburseLoanWithError();
            loanAccountPage.verifyError(error);
        }
    }

    private void setDateAsToday() throws UnsupportedEncodingException {
        setAppDate(new DateTime(System.currentTimeMillis()));
    }

    private void setAppDate(DateTime dateTime) throws UnsupportedEncodingException {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        dateTimeUpdaterRemoteTestingService.setDateTime(dateTime);
    }

    /**
     * Verify loan product created with default loan amount and number of installments
     * are "same for all loans" can be used to create new loan accounts with the correct default amounts.
     * http://mifosforge.jira.com/browse/MIFOSTEST-97
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //CreateClient: weekly, Stu12332659912419 Client12332659912419, monday, activate
    public void verifyAccountFromProductInstallmentsSame() throws Exception {
        setAppDate(new DateTime(2011, 1, 24, 15, 0, 0, 0));

        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        productParams.setOfferingName("ProdTest97");
        productParams.setOfferingShortName("T97");
        productParams.setMinLoanAmount("1000");
        productParams.setMaxLoanAmount("10000");
        productParams.setDefaultLoanAmount("5000");
        productParams.setMinInstallemnts("10");
        productParams.setMaxInstallments("100");
        productParams.setDefInstallments("50");
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu12332659912419 Client12332659912419");
        searchParams.setLoanProduct("ProdTest97");
        DisburseLoanParameters disburseParams = new DisburseLoanParameters();
        disburseParams.setPaymentType(DisburseLoanParameters.CASH);
        disburseParams.setDisbursalDateDD("24");
        disburseParams.setDisbursalDateMM("01");
        disburseParams.setDisbursalDateYYYY("2011");
        ChargeParameters chargeParameters = new ChargeParameters();
        chargeParameters.setType(ChargeParameters.MISC_FEES);
        chargeParameters.setAmount("599.0");

        LoanAccountPage loanAccountPage = loanTestHelper.createProductAndThenAccount(productParams, searchParams, disburseParams);

        loanAccountPage.verifyNumberOfInstallments("10", "100", "50");
        loanAccountPage.verifyPrincipalBalance("5000.0");
        loanTestHelper.applyOneChargeOnLoanAccount(chargeParameters);
        loanAccountPage.navigateToViewInstallmentDetails()
                .verifyInstallmentAmount(4, 1, "599.0");
    }

    /**
     * Verify the loan product created with default number of installments
     * by loan cycle can be used to create new loans accounts.
     * http://mifosforge.jira.com/browse/MIFOSTEST-99
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyCreatingLoanAccountsOnProductWithLoanCycles() throws Exception {
        setAppDate(new DateTime(2011, 1, 24, 15, 0, 0, 0));

        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        productParams.setOfferingName("ProdTest99");
        productParams.setOfferingShortName("T99");
        productParams.setMinLoanAmount("1000");
        productParams.setMaxLoanAmount("10000");
        productParams.setDefaultLoanAmount("5000");
        productParams.setCalculateInstallments(SubmitFormParameters.BY_LOAN_CYCLE);
        String[][] cycleInstallments = {
                {"26", "52", "52"},
                {"20", "30", "30"},
                {"15", "25", "25"},
                {"10", "15", "15"},
                {"5", "10", "10"},
                {"1", "5", "5"}
        };
        productParams.setCycleInstallments(cycleInstallments);
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu12332659912419 Client12332659912419");
        searchParams.setLoanProduct("ProdTest99");
        DisburseLoanParameters disburseParams = new DisburseLoanParameters();
        disburseParams.setPaymentType(DisburseLoanParameters.CASH);
        disburseParams.setDisbursalDateDD("24");
        disburseParams.setDisbursalDateMM("01");
        disburseParams.setDisbursalDateYYYY("2011");

        LoanProductDetailsPage loanProductDetailsPage = loanProductTestHelper.defineNewLoanProduct(productParams);
        loanProductDetailsPage.verifyLoanAmountTableTypeSame("1000.0", "10000.0", "5000.0");
        loanProductDetailsPage.verifyInstallmentsTableTypeFromCycle(cycleInstallments);
        LoanAccountPage loanAccountPage = loanTestHelper.createActivateAndDisburseDefaultLoanAccount(searchParams, disburseParams);
        loanAccountPage.verifyNumberOfInstallments("26", "52", "52");
        String loan1ID = loanAccountPage.getAccountId();
        loanTestHelper.repayLoan(loan1ID);
        loanTestHelper.createActivateAndDisburseDefaultLoanAccount(searchParams, disburseParams);
        loanAccountPage.verifyNumberOfInstallments("20", "30", "30");
        String loan2ID = loanAccountPage.getAccountId();
        loanTestHelper.repayLoan(loan2ID);
        loanTestHelper.createActivateAndDisburseDefaultLoanAccount(searchParams, disburseParams);
        loanAccountPage.verifyNumberOfInstallments("15", "25", "25");
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
        return loanAccountPage;
    }

    private void getLoanStatusActive(String loanId) {
        EditLoanAccountStatusParameters editLoanAccountStatusParameters = new EditLoanAccountStatusParameters();
        editLoanAccountStatusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        editLoanAccountStatusParameters.setNote("test");
        loanTestHelper.changeLoanAccountStatus(loanId, editLoanAccountStatusParameters);

        DisburseLoanParameters disburseParameters = new DisburseLoanParameters();
        disburseParameters.setPaymentType(DisburseLoanParameters.CASH);
        disburseParameters.setDisbursalDateDD("01");
        disburseParameters.setDisbursalDateMM("02");
        disburseParameters.setDisbursalDateYYYY("2011");
        //Then
        loanTestHelper.disburseLoan(loanId, disburseParameters);
    }
}
