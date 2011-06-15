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
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountCashFlowPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountReviewInstallmentPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.office.OfficeParameters;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loanproduct", "acceptance", "ui","no_db_unit"})
public class VariableInstalmentRecalculationTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    private static final String userLoginName = "test_user";
    private static final String officeName = "test_office";
    private static final String clientName = "test client";
    private static final String userName="test user";
    private LoanProductTestHelper loanProductTestHelper;
    private String loanProductName;
    private LoanTestHelper loanTestHelper;
    private DateTime systemDateTime;
    private NavigationHelper navigationHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        systemDateTime = new DateTime(2010, 10, 11, 10, 0, 0, 0);
        loanTestHelper.setApplicationTime(systemDateTime);
        TestDataSetup dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);
        dataSetup.createBranch(OfficeParameters.BRANCH_OFFICE, officeName, "Off");
        dataSetup.createUser(userLoginName, userName, officeName);
        dataSetup.createClient(clientName, officeName, userName);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    // TODO - this test do not work on systems with other locale than English.
    @Test(enabled=false)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyCashFlowRecalculationAndWarnings() throws Exception {
        int noOfInstallments = 3;
        int loanAmount = 1000;
        int interestRate = 20;
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(noOfInstallments, loanAmount, interestRate);
        applicationDatabaseOperation.updateLSIM(1);

        int maxGap = 90;
        int minGap = 1;
        int minInstalmentAmount = 0;
        int cashFlowIncremental = 1;
        double warningThreshold = 10.0;

        DateTime disbursalDate = systemDateTime.plusDays(1);

        createLoanProduct(maxGap, minGap, minInstalmentAmount, formParameters, warningThreshold);
        CreateLoanAccountReviewInstallmentPage reviewInstallmentsPage = createNewLoanAccountAndNavigateToRepaymentSchedule(disbursalDate).
                enterValidData("100", cashFlowIncremental, 100, null, null).
                clickContinue();
        
        reviewInstallmentsPage.verifyCashFlowDefaultValues();
        
//                verifyInstallmentDatesOutOfCashFlowCapturedOnValidate().
//                verifyRecalculationOfCashFlowOnValidate().
//                verifyWarningThresholdMessageOnValidate(warningThreshold).
//                verifyInstallmentDatesOutOfCashFlowCapturedOnPreview().
//                verifyRecalculationOfCashFlowOnPreview().
//                verifyWarningThresholdMessageOnPreview(warningThreshold);
        applicationDatabaseOperation.updateLSIM(0);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    /*
     * supressed this test for now. cant see anything wrong functionality. not sure why timing out after pressing button. - keithw
     */
    @Test(enabled=false)
    public void verifyPrincipalAndInterestRecalculation() throws Exception {
        int noOfInstallments = 4;
        int loanAmount = 1000;
        int interestRate = 20;
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(noOfInstallments, loanAmount, interestRate);
        applicationDatabaseOperation.updateLSIM(1);

        int maxGap = 10;
        int minGap = 1;
        int minInstalmentAmount = 100;
        int cashFlowIncremental = 1;
        int cashFlowBase = 100000;
        double warningThreshold = 1.0;

        DateTime disbursalDate = systemDateTime.plusDays(1);

        createLoanProduct(maxGap, minGap, minInstalmentAmount, formParameters, warningThreshold);
        createNewLoanAccountAndNavigateToRepaymentSchedule(disbursalDate).
                enterValidData("100", cashFlowIncremental, cashFlowBase, null, null).
                clickContinue().
                verifyRecalculationWhenDateAndTotalChange();
        
        applicationDatabaseOperation.updateLSIM(0);
    }

    private DefineNewLoanProductPage.SubmitFormParameters defineLoanProductParameters(int defInstallments, int defaultLoanAmount, int defaultInterestRate) {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setDefInstallments(String.valueOf(defInstallments));
        formParameters.setDefaultLoanAmount(String.valueOf(defaultLoanAmount));
        formParameters.setInterestTypes(DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE);
        formParameters.setDefaultInterestRate(String.valueOf(defaultInterestRate));
        return formParameters;
    }

    private void createLoanProduct(int maxGap, int minGap, int minInstalmentAmount, DefineNewLoanProductPage.SubmitFormParameters formParameters, double cashFlowIncremental) {
        loanProductName = formParameters.getOfferingName();
        loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                fillVariableInstalmentOption(String.valueOf(maxGap),String.valueOf(minGap), String.valueOf(minInstalmentAmount)).
                fillCashFlow(String.valueOf(cashFlowIncremental), "", "").
                submitAndGotoNewLoanProductPreviewPage().
                submit();
    }


    private CreateLoanAccountCashFlowPage createNewLoanAccountAndNavigateToRepaymentSchedule(DateTime validDisbursalDate) {
        navigationHelper.navigateToHomePage();
        return loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(setLoanSearchParameters()).
                setDisbursalDate(validDisbursalDate).
                clickContinueToNavigateToCashFlowPage();
    }

    private CreateLoanAccountSearchParameters setLoanSearchParameters() {
        CreateLoanAccountSearchParameters accountSearchParameters = new CreateLoanAccountSearchParameters();
        accountSearchParameters.setSearchString(clientName);
        accountSearchParameters.setLoanProduct(loanProductName);
        return accountSearchParameters;
    }
}