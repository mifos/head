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
import org.mifos.test.acceptance.admin.FeeTestHelper;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
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
@Test(sequential = true, groups = {"loanproduct", "acceptance", "ui"})
public class VariableInstalmentLoanTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    private static final String officeName = "test_office";
    private static final String userLoginName = "test_user";
    private static final String userName="test user";
    private static final String clientName = "test client";
    private LoanProductTestHelper loanProductTestHelper;
    private String loanProductName;
    private LoanTestHelper loanTestHelper;
    private DateTime systemDateTime;
    private NavigationHelper navigationHelper;
    private FeeTestHelper feeTestHelper;

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
        systemDateTime = new DateTime(2010, 10, 11, 10, 0, 0, 0);
        loanTestHelper = new LoanTestHelper(selenium);
        loanTestHelper.setApplicationTime(systemDateTime);
        TestDataSetup dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);
        dataSetup.createBranch(OfficeParameters.BRANCH_OFFICE, officeName, "Off");
        dataSetup.createUser(userLoginName, userName, officeName);
        dataSetup.createClient(clientName, officeName, userName);
        feeTestHelper = new FeeTestHelper(dataSetup);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=false)
    public void verifyRepaymentScheduleField() throws Exception {
        int noOfInstallments = 5;
        int loanAmount = 1000;
        int interestRate = 20;
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(noOfInstallments, loanAmount, interestRate);
        applicationDatabaseOperation.updateLSIM(1);

        int maxGap = 10;
        int minGap = 1;
        int minInstalmentAmount = 100;
        DateTime disbursalDate = systemDateTime.plusDays(1);

        createLoanProductWithVariableInstalment(maxGap, minGap, minInstalmentAmount, formParameters);
        navigationHelper.navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(setLoanSearchParameters()).
                setDisbursalDate(disbursalDate).
                clickContinue().
                validateRepaymentScheduleFieldDefault(noOfInstallments).
                validateDateFieldValidations(disbursalDate,minGap,maxGap,noOfInstallments).
                verifyInstallmentTotalValidations(noOfInstallments,minInstalmentAmount, disbursalDate, minGap).
                verifyValidData(noOfInstallments,minGap,minInstalmentAmount,disbursalDate, maxGap).
                clickPreviewAndGoToReviewLoanAccountPage().
                verifyEditSchedule().
                verifySchedulePersistOnEdit(noOfInstallments,minGap,minInstalmentAmount,disbursalDate, maxGap);

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=false)
    public void verifyInvalidFees() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(5, 1000, 20);
        applicationDatabaseOperation.updateLSIM(1);
        DateTime disbursalDate = systemDateTime.plusDays(1);
        String periodicFees = feeTestHelper.createPeriodicFee("loanWeeklyFee", FeesCreatePage.SubmitFormParameters.LOAN, FeesCreatePage.SubmitFormParameters.WEEKLY_FEE_RECURRENCE, 1, 100);
        String fixedFeePerAmountAndInterest = feeTestHelper.createFixedFee("fixedFeePerAmountAndInterest", FeesCreatePage.SubmitFormParameters.LOAN, "Upfront", 100, "Loan Amount+Interest");
        String fixedFeePerInterest = feeTestHelper.createFixedFee("fixedFeePerInterest", FeesCreatePage.SubmitFormParameters.LOAN, "Upfront", 20, "Interest");
        String[] blockedInterest = {periodicFees, fixedFeePerAmountAndInterest, fixedFeePerInterest};

        createLoanProductWithVariableInstalment(100, 5, 100, formParameters);
        navigationHelper.navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(setLoanSearchParameters()).
                setDisbursalDate(disbursalDate).
                verifyInvalidFeeBlocked(blockedInterest).
                clickContinue().
                clickPreviewAndGoToReviewLoanAccountPage().
                submit().navigateToLoanAccountDetailsPage().
                navigateToApplyCharge().verifyBlockedFee(blockedInterest);
    }

    private DefineNewLoanProductPage.SubmitFormParameters defineLoanProductParameters(int defInstallments, int defaultLoanAmount, int defaultInterestRate) {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setDefInstallments(String.valueOf(defInstallments));
        formParameters.setDefaultLoanAmount(String.valueOf(defaultLoanAmount));
        formParameters.setInterestTypes(DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE);
        formParameters.setDefaultInterestRate(String.valueOf(defaultInterestRate));
        return formParameters;
    }

    private void createLoanProductWithVariableInstalment(int maxGap, int minGap, int minInstalmentAmount, DefineNewLoanProductPage.SubmitFormParameters formParameters) {
        loanProductName = formParameters.getOfferingName();
        loanProductTestHelper.
                navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                fillVariableInstalmentOption(String.valueOf(maxGap),String.valueOf(minGap), String.valueOf(minInstalmentAmount)).
                submitAndGotoNewLoanProductPreviewPage().
                submit();
    }

    private CreateLoanAccountSearchParameters setLoanSearchParameters() {
        CreateLoanAccountSearchParameters accountSearchParameters = new CreateLoanAccountSearchParameters();
        accountSearchParameters.setSearchString(clientName);
        accountSearchParameters.setLoanProduct(loanProductName);
        return accountSearchParameters;
    }
}