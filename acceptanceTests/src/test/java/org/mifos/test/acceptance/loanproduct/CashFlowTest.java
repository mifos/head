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

package org.mifos.test.acceptance.loanproduct;


import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountCashFlowPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.office.OfficeParameters;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"smoke", "loanproduct", "acceptance"})
public class CashFlowTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    String userName="test user";
    String userLoginName = "test_user";
    String clientName = "test client";
    String officeName = "test_office";
    LoanTestHelper loanTestHelper;
    LoanProductTestHelper loanProductTestHelper;
    DateTime systemDateTime;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        systemDateTime = new DateTime(2010, 10, 11, 10, 0, 0, 0);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
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

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyCashFlowFields() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getMonthlyLoanProductParameters();
        loanProductTestHelper.navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                verifyCashFlowFieldDefault().
                verifyCashFlowFields();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyCashFlowWithNullValue() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        createAndValidateLoanProductWithCashFlow("", formParameters, "", "", false);
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyCashFlowUnChecked() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        String loanProductName = formParameters.getOfferingName();
        loanProductTestHelper.
                navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                submitAndGotoNewLoanProductPreviewPage().
                verifyCashFlowUnCheckedInPreview().submit().
                navigateToViewLoanDetails().
                verifyCashFlowUnCheckedIn();
        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName,loanProductName).
                clickContinue().
                verifyPage("SchedulePreview");
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyCashFlowForNonVariableInstallmentLoan() throws Exception {
        applicationDatabaseOperation.updateLSIM(0);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        String minRc = "999.99";
        String warningThreshold = "89.99";
        createAndValidateLoanProductWithCashFlow(warningThreshold,formParameters, "49.99", minRc, false);
        validateCashFlowForLoanAccount(formParameters, minRc, "999.68");
    }
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyWarningsForNonVariableInstallmentLoan() throws Exception {
        applicationDatabaseOperation.updateLSIM(0);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setDefaultInterestRate("20");
        formParameters.setDefaultLoanAmount("1000");
        String warningThreshold = "89.99";
        createAndValidateLoanProductWithCashFlow(warningThreshold,formParameters, "", "", false);
        DateTime disbursalDate = systemDateTime.plusDays(1);
        int installment = 3;
        createLoanAccountAndNavigateToCashFlowPage(formParameters, disbursalDate, installment).
                enterValidData(100, 1, null, null).clickContinue().
                clickPreviewAndGoToReviewLoanAccountPage().
                verifyWarningForThreshold(warningThreshold);
        verifyNegativeAndZeroCashFlow(formParameters, warningThreshold, disbursalDate, installment);

    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyWarningsForVariableInstallmentLoan() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setDefaultInterestRate("20");
        formParameters.setDefaultLoanAmount("1000");
        String warningThreshold = "89.99";
        createAndValidateLoanProductWithCashFlow(warningThreshold,formParameters, "", "", true);
        DateTime disbursalDate = systemDateTime.plusDays(1);
        int installment = 3;
        verifyNegativeAndZeroCashFlow(formParameters, warningThreshold, disbursalDate, installment);
    }

    private void verifyNegativeAndZeroCashFlow(DefineNewLoanProductPage.SubmitFormParameters formParameters, String warningThreshold, DateTime disbursalDate, int installment) {
        createLoanAccountAndNavigateToCashFlowPage(formParameters, disbursalDate, installment).
                enterValidData(1, 10, null, null).clickContinue().
                clickPreviewAndGoToReviewLoanAccountPage().verifyNegativeCashFlowWarning();
        createLoanAccountAndNavigateToCashFlowPage(formParameters, disbursalDate, installment).
                enterValidData(0, 100, null, null).clickContinue().
                clickPreviewAndGoToReviewLoanAccountPage().verifyZeroCashFlowWarning(warningThreshold);
    }

    private CreateLoanAccountCashFlowPage createLoanAccountAndNavigateToCashFlowPage(DefineNewLoanProductPage.SubmitFormParameters formParameters, DateTime disbursalDate, int installment) {
        new NavigationHelper(selenium).navigateToHomePage();
        return loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, formParameters.getOfferingName()).
                setDisbursalDate(disbursalDate).
                setInstallments(installment).
                clickContinueToNavigateToCashFlowPage();
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyCashFlowForVariableInstallmentLoan() throws Exception {
        String minRC = "999.99";
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        createAndValidateLoanProductWithCashFlow("89.99",formParameters, "49.99", minRC, true);
        validateCashFlowForLoanAccount(formParameters, minRC, "999.92");
        verifyRepaymentCapacityOnValidate(formParameters, minRC, "999.92");
    }

    private void validateCashFlowForLoanAccount(DefineNewLoanProductPage.SubmitFormParameters formParameters, String minRc, String expectedRc) {
        DateTime disbursalDate = systemDateTime.plusDays(1); //next week tuesday
        int installment = 5;
        int cashFlowIncremental = 5695;
        String loanProductName = formParameters.getOfferingName();
        int frequency = formParameters.getFreqOfInstallments();
        verifyCashFlowForLoanAccount(disbursalDate, installment, cashFlowIncremental, loanProductName, frequency);
        verifyRepaymentCapacityOnPreview(disbursalDate, installment, cashFlowIncremental, loanProductName, expectedRc, minRc);
    }

    private void verifyRepaymentCapacityOnValidate(DefineNewLoanProductPage.SubmitFormParameters formParameters, String minRc, String expectedRc) {
        DateTime disbursalDate = systemDateTime.plusDays(1); //next week tuesday
        int installment = 5;
        int cashFlowIncremental = 5695;
        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName,formParameters.getOfferingName()).
                setDisbursalDate(disbursalDate).
                setInstallments(installment).
                clickContinueToNavigateToCashFlowPage().
                enterValidData(cashFlowIncremental-2, 100, "7003", "1000").
                clickContinue().verifyRepaymentCapacityOnValidate(expectedRc, minRc);
    }

    private void verifyRepaymentCapacityOnPreview(DateTime disbursalDate, int installment, int cashFlowIncremental, String loanProductName, String expectedRc, String minRc) {
        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName,loanProductName).
                setDisbursalDate(disbursalDate).
                setInstallments(installment).
                clickContinueToNavigateToCashFlowPage().
                enterValidData(cashFlowIncremental-2, 100, "7003", "1000").
                clickContinue().verifyRepaymentCapacityOnPreview(expectedRc, minRc);

    }


    private void verifyCashFlowForLoanAccount(DateTime disbursalDate, int installment, int cashFlowIncremental, String loanProductName, int frequency) {
        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName,loanProductName).
                setDisbursalDate(disbursalDate).
                setInstallments(installment).
                clickContinueToNavigateToCashFlowPage().
                validateCashFlowMonths(disbursalDate,installment,frequency).
                verifyCashFlowFields().
                verifyInvalidIndebentRate("49.99", "7001", "1000").
                enterValidData(cashFlowIncremental, 100, "7003", "1000").
                clickContinue().
                verifyCashFlow(cashFlowIncremental).
                clickPreviewAndGoToReviewLoanAccountPage().
                verifyPage();

    }

    private void createAndValidateLoanProductWithCashFlow(String warningThreshold, DefineNewLoanProductPage.SubmitFormParameters formParameters, String indebtentValue, String repaymentValue, boolean isVariableLoan) {
        DefineNewLoanProductPage loanProductPage = loanProductTestHelper.navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters);
        if (isVariableLoan) {
            loanProductPage.fillVariableInstalmentOption("","1","");
        }
        loanProductPage.fillCashFlow(warningThreshold, indebtentValue,repaymentValue).
                submitAndGotoNewLoanProductPreviewPage().
                verifyCashFlowInPreview(warningThreshold,indebtentValue,repaymentValue).
                submit().navigateToViewLoanDetails().
                verifyCashFlowInViewLoanProductPage(warningThreshold,indebtentValue,repaymentValue);
    }

    
}

