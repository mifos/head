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

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.admin.FeeTestHelper;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalChooseLoanInstancePage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalEntryPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalParameters;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSchedulePreviewPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSearchPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSearchResultsPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.office.OfficeParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"loan","acceptance","ui"})
public class RedoLoanDisbursalTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;
    private NavigationHelper navigationHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    private final static String userLoginName = "test_user";
    private final static String officeName = "test_office";
    private final static String clientName = "test client";
    private final static String userName="test user";
    LoanProductTestHelper loanProductTestHelper;
    DateTime systemDateTime;
    private FeeTestHelper feeTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,7,26,15,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium, navigationHelper);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void redoLoanDisbursalWithPastDate() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        RedoLoanDisbursalParameters params = setLoanParams(null, 24, 5, 1000);

        params.setDisbursalDateDD("23");
        params.setDisbursalDateMM("07");
        params.setDisbursalDateYYYY("2009");

        loanTestHelper.redoLoanDisbursal("MyGroup1233266255641", "WeeklyGroupFlatLoanWithOnetimeFee", params);

//        verifyRedoneLoanDisbursal("RedoLoanDisbursalTest_001_result_dbunit.xml");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void redoLoanDisbursalWithCurrentOrFutureDate() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        RedoLoanDisbursalParameters params = setLoanParams(null, 24, 5, 1000);

        params.setDisbursalDateDD("26");
        params.setDisbursalDateMM("07");
        params.setDisbursalDateYYYY("2009");

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        adminPage.verifyPage();

        RedoLoanDisbursalSearchPage searchPage = adminPage.navigateToRedoLoanDisbursal();
        searchPage.verifyPage();

        RedoLoanDisbursalSearchResultsPage resultsPage = searchPage.searchAndNavigateToRedoLoanDisbursalPage("MyGroup1233266255641");
        resultsPage.verifyPage();

        RedoLoanDisbursalChooseLoanInstancePage chooseLoanPage = resultsPage.navigateToRedoLoanDisbursalChooseLoanProductPage("MyGroup1233266255641");
        chooseLoanPage.verifyPage();

        RedoLoanDisbursalEntryPage dataEntryPage = chooseLoanPage.submitAndNavigateToRedoLoanDisbursalEntryPage("WeeklyGroupFlatLoanWithOnetimeFee");
        dataEntryPage.verifyPage();

        MifosPage schedulePreviewPage = dataEntryPage.submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(params);
        schedulePreviewPage.verifyPage("LoanCreationDetail"); // the page.id for dataEntryPage, we want to stay there.
    }

//    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
//    private void verifyRedoneLoanDisbursal(String resultDataSetFile) throws Exception {
//        String[] tablesToValidate = { /*"ACCOUNT_STATUS_CHANGE_HISTORY",*/ "LOAN_ACCOUNT", "LOAN_ACTIVITY_DETAILS", "LOAN_SUMMARY", "LOAN_TRXN_DETAIL"};
//
//        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(resultDataSetFile);
//        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, tablesToValidate);
//
//        dbUnitUtilities.verifyTables(tablesToValidate, databaseDataSet, expectedDataSet);
//
//    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void redoLoanDisbursalForVariableInstallmentLoan() throws Exception {
        dataSetUpForVariableInstallmentLoan();
        applicationDatabaseOperation.updateLSIM(1);
        String periodicFees = feeTestHelper.createPeriodicFee("loanWeeklyFee", FeesCreatePage.SubmitFormParameters.LOAN, FeesCreatePage.SubmitFormParameters.WEEKLY_FEE_RECURRENCE, 1, 100);
        String fixedFeePerAmountAndInterest = feeTestHelper.createFixedFee("fixedFeePerAmountAndInterest", FeesCreatePage.SubmitFormParameters.LOAN, "Upfront", 100, "Loan Amount+Interest");
        String fixedFeePerInterest = feeTestHelper.createFixedFee("fixedFeePerInterest", FeesCreatePage.SubmitFormParameters.LOAN, "Upfront", 20, "Interest");
        int interest = 24;
        int noOfInstallments = 5;
        int loanAmount = 1000;
        DefineNewLoanProductPage.SubmitFormParameters formParameters = loanProductTestHelper.defineLoanProductParameters(noOfInstallments, loanAmount, interest,DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE);
        String[] invalidFees = {periodicFees, fixedFeePerInterest, fixedFeePerAmountAndInterest};
        RedoLoanDisbursalParameters redoLoanDisbursalParameters = setLoanParams(systemDateTime, interest, noOfInstallments, loanAmount);

        String maxGap = "10";
        String minGap = "1";
        String minInstalmentAmount = "100";
        loanProductTestHelper.navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                fillVariableInstalmentOption(maxGap, minGap, minInstalmentAmount).fillCashFlow("10","10","155").submitAndGotoNewLoanProductPreviewPage().submit();
        loanTestHelper.setApplicationTime(systemDateTime.plusDays(14));
        DateTime disbursalDate = systemDateTime;
        navigationHelper.
                navigateToAdminPage().navigateToRedoLoanDisbursal().
                searchAndNavigateToRedoLoanDisbursalPage(clientName).
                navigateToRedoLoanDisbursalChooseLoanProductPage(clientName).
                submitAndNavigateToRedoLoanDisbursalEntryPage(formParameters.getOfferingName()).
                verifyFeeBlockedForVariableInstallmentLoan(invalidFees).
                submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(redoLoanDisbursalParameters).
                validateRepaymentScheduleFieldDefault(noOfInstallments).
                validateDateFieldValidations(disbursalDate,minGap,maxGap,noOfInstallments).
                verifyInstallmentTotalValidations(noOfInstallments,minInstalmentAmount, disbursalDate, minGap).
                verifyValidData(noOfInstallments,minGap,minInstalmentAmount,disbursalDate, maxGap).
                verifyRecalculationWhenDateAndTotalChange();
        verifyPaymentField();

        //verify fee, after creation
        //date picker
        //holiday
    }

    private void verifyPaymentField() {
        new RedoLoanDisbursalSchedulePreviewPage(selenium).
                setPaidField(RedoLoanScheduleData.VARIABLE_LOAN_PAYMENT_ONE).
                clickPreviewAndGoToReviewLoanAccountPage();
//                verifyRunningBalance(RedoLoanScheduleData.VARIABLE_LOAN_SCHEDULE_ONE,RedoLoanScheduleData.VARIABLE_LOAN_RUNNING_BALANCE_ONE);
    }


    private RedoLoanDisbursalParameters setLoanParams(ReadableInstant validDisbursalDate, int interest, int noOfInstallments, int loanAmount) {
        RedoLoanDisbursalParameters redoLoanDisbursalParameters = new RedoLoanDisbursalParameters();
        redoLoanDisbursalParameters.setInterestRate(String.valueOf(interest));
        redoLoanDisbursalParameters.setNumberOfInstallments(String.valueOf(noOfInstallments));
        redoLoanDisbursalParameters.setLoanAmount(String.valueOf(loanAmount));
        redoLoanDisbursalParameters.setDisbursalDateDD(DateTimeFormat.forPattern("dd").print(validDisbursalDate));
        redoLoanDisbursalParameters.setDisbursalDateMM(DateTimeFormat.forPattern("MM").print(validDisbursalDate));
        redoLoanDisbursalParameters.setDisbursalDateYYYY(DateTimeFormat.forPattern("yyyy").print(validDisbursalDate));
        return redoLoanDisbursalParameters;
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void dataSetUpForVariableInstallmentLoan() throws Exception {
        navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        systemDateTime = new DateTime(2010, 10, 11, 10, 0, 0, 0);
        TestDataSetup dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);
        feeTestHelper = new FeeTestHelper(dataSetup);
        loanTestHelper.setApplicationTime(systemDateTime);
        dataSetup.createBranch(OfficeParameters.BRANCH_OFFICE, officeName, "Off");
        dataSetup.createUser(userLoginName, userName, officeName);
        dataSetup.createClient(clientName, officeName, userName);
        dataSetup.addDecliningPrincipalBalance();
    }



}
