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

package org.mifos.test.acceptance.loanproduct;


import org.joda.time.DateTime;
import org.mifos.test.acceptance.admin.FeeTestHelper;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
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
@Test(singleThreaded = true, groups = {"loanproduct", "acceptance", "ui","no_db_unit"})
public class VariableInstalmentLoanProductTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    String clientName = "WeeklyOld Monday";
    String loanProductName;
    LoanProductTestHelper loanProductTestHelper;
    LoanTestHelper loanTestHelper;
    DateTime systemDateTime;
    private FeeTestHelper feeTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        systemDateTime = new DateTime(2010, 10, 11, 10, 0, 0, 0);
        loanTestHelper.setApplicationTime(systemDateTime);
        TestDataSetup dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);
        dataSetup.addDecliningPrincipalBalance();
        feeTestHelper = new FeeTestHelper(dataSetup, new NavigationHelper(selenium));
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyLSIMDisabled() throws Exception {
        applicationDatabaseOperation.updateLSIM(0);
        loanProductTestHelper.navigateToDefineNewLoanProductPage().
        verifyVariableInstalmentNotAvailable();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyVariableInstalmentUnChecked() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        loanProductName = formParameters.getOfferingName();
        applicationDatabaseOperation.updateLSIM(1);
        loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                submitAndGotoNewLoanProductPreviewPage().
                verifyVariableInstalmentUnChecked().submit().
                navigateToViewLoanDetails().
                verifyVariableInstalmentOptionsUnChecked();
        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, loanProductName).
                verifyUncheckedVariableInstalmentsInLoanProductSummery()
                .clickContinue().
                clickPreviewAndGoToReviewLoanAccountPage()
                .verifyEditScheduleDisabled();
        applicationDatabaseOperation.updateLSIM(0);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyVariableInstalmentWithNullValue() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        loanProductName = formParameters.getOfferingName();
        createAndValidateLoanProductWithVariableInstalment("","1","",formParameters);
        navigateToCreateNewLoanPageAndValidateInstallmentSummary("","1","");
        applicationDatabaseOperation.updateLSIM(0);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyVariableInstalment() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        loanProductName = formParameters.getOfferingName();
        createAndValidateLoanProductWithVariableInstalment("60", "1", "100.5", formParameters);
        navigateToCreateNewLoanPageAndValidateInstallmentSummary("60", "1", "100.5");
        applicationDatabaseOperation.updateLSIM(0);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyVariableInstalmentField() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        applicationDatabaseOperation.updateLSIM(1);

        String periodicFees = feeTestHelper.createPeriodicFee("loanWeeklyFee", FeesCreatePage.SubmitFormParameters.LOAN, FeesCreatePage.SubmitFormParameters.WEEKLY_FEE_RECURRENCE, 1, 100);
        String fixedFeePerAmountAndInterest = feeTestHelper.createFixedFee("fixedFeePerAmountAndInterest", FeesCreatePage.SubmitFormParameters.LOAN, "Upfront", 100, "Loan Amount+Interest");
        String fixedFeePerInterest = feeTestHelper.createFixedFee("fixedFeePerInterest", FeesCreatePage.SubmitFormParameters.LOAN, "Upfront", 20, "Interest");

            loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                verifyVariableInstalmentOptionsDefaults().
                verifyVariableInstalmentOptionsFields().
                verifyBlockedInterestTypes().
                verifyFeeTypesBlocked(new String[]{periodicFees, fixedFeePerAmountAndInterest, fixedFeePerInterest});
            applicationDatabaseOperation.updateLSIM(0);
    }

    private void createAndValidateLoanProductWithVariableInstalment(String maxGap, String minGap, String minInstalmentAmount, DefineNewLoanProductPage.SubmitFormParameters formParameters) {
        loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                fillVariableInstalmentOption(maxGap, minGap, minInstalmentAmount).
                submitAndGotoNewLoanProductPreviewPage().
                verifyVariableInstalmentOption(maxGap, minGap, minInstalmentAmount).
                submit().
                navigateToViewLoanDetails().
                verifyVariableInstalmentOptions(maxGap, minGap, minInstalmentAmount);
    }

    private void navigateToCreateNewLoanPageAndValidateInstallmentSummary(String maxGap, String minGap, String minInstalmentAmount) {
        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, loanProductName).
                verifyVariableInstalmentsInLoanProductSummery(maxGap, minGap, minInstalmentAmount);
    }

}

