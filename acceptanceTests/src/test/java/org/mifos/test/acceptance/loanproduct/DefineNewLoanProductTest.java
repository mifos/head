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
import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.RepayLoanPage;
import org.mifos.test.acceptance.framework.loan.RepayLoanParameters;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loan.QuestionGroupHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loanproduct", "acceptance", "no_db_unit"})
public class DefineNewLoanProductTest extends UiTestCaseBase {
	
    private Random random;
    private QuestionGroupHelper questionGroupHelper;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        questionGroupHelper = new QuestionGroupHelper(new NavigationHelper(selenium));
        random = new Random();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void createWeeklyLoanProduct() throws Exception {
        SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        new NavigationHelper(selenium).navigateToAdminPage().
                verifyPage().
                defineLoanProduct(formParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createWeeklyLoanProductWithQuestionGroups() throws Exception {
        String questionGroupTitle = "QG1" + random.nextInt(100);
        String question1 = "DT_" + random.nextInt(100);
        String question2 = "SS_" + random.nextInt(100);
        questionGroupHelper.createQuestionGroup(questionGroupTitle, question1, question2, "Create Loan");

        SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParametersWithQuestionGroups(questionGroupTitle);
        new NavigationHelper(selenium).navigateToAdminPage().
                verifyPage().defineLoanProduct(formParameters);

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void createMonthlyLoanProduct() throws Exception {
        SubmitFormParameters formParameters = FormParametersHelper.getMonthlyLoanProductParameters();
        new NavigationHelper(selenium).navigateToAdminPage().
                verifyPage().defineLoanProduct(formParameters);
    }

    /*
     * passing locally but not on hudson. 
     */
    //http://mifosforge.jira.com/browse/MIFOSTEST-710
    @Test(enabled=true) 
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyWaiveInterestForLoanAccount() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime systemDateTime = new DateTime(2011, 3, 7, 12, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemDateTime);
        //When
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setInterestWaiver(true);
        NavigationHelper navigationHelper = new NavigationHelper(selenium);
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineNewLoanProductPage newLoanProductPage = adminPage.navigateToDefineLoanProduct();
        newLoanProductPage.fillLoanParameters(formParameters);
        DefineNewLoanProductPreviewPage previewPage = newLoanProductPage.submitAndGotoNewLoanProductPreviewPage();
        //Then
        Assert.assertTrue(selenium.isTextPresent("Can waive interest on repay loan: Yes"));
        //When
        LoanProductDetailsPage loanProductDetailsPage = previewPage.submit().navigateToViewLoanDetailsPage();
        //Then
        Assert.assertTrue(selenium.isTextPresent("Can waive interest on repay loan: Yes"));
        //When
        EditLoanProductPage editLoanProductPage = loanProductDetailsPage.editLoanProduct();
        //Then
        Assert.assertTrue(selenium.isChecked("EditLoanProduct.input.includeInterestWaiver"));
        //When
        EditLoanProductPreviewPage editLoanProductPreviewPage = editLoanProductPage.editSubmit();
        //Then
        Assert.assertTrue(selenium.isTextPresent("Can waive interest on repay loan: Yes"));
        //When
        loanProductDetailsPage = editLoanProductPreviewPage.submit();
        //Then
        Assert.assertTrue(selenium.isTextPresent("Can waive interest on repay loan: Yes"));

        //When
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("WeeklyClient Monday");
        searchParameters.setLoanProduct(formParameters.getOfferingName());

        LoanTestHelper loanTestHelper = new LoanTestHelper(selenium);
        LoanAccountPage loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(searchParameters);

        DisburseLoanParameters disburseParameters = new DisburseLoanParameters();
        disburseParameters.setDisbursalDateDD(Integer.toString(systemDateTime.getDayOfMonth()));
        disburseParameters.setDisbursalDateMM(Integer.toString(systemDateTime.getMonthOfYear()));
        disburseParameters.setDisbursalDateYYYY(Integer.toString(systemDateTime.getYear()));
        disburseParameters.setPaymentType(PaymentParameters.CASH);

        loanAccountPage.navigateToDisburseLoan()
                .submitAndNavigateToDisburseLoanConfirmationPage(disburseParameters)
                .submitAndNavigateToLoanAccountPage();

        RepayLoanPage repayLoanPage = loanAccountPage.navigateToRepayLoan();
        //Then
        Assert.assertTrue(selenium.isChecked("waiverInterestChckBox"));
        Assert.assertEquals("Note: Interest due will be waived off.", selenium.getText("waiverInterestWarning"));
        Assert.assertFalse(repayLoanPage.isTotalRepaymentAmountVisible());
        Assert.assertTrue(repayLoanPage.isWaivedRepaymentAmoutVisible());
        Assert.assertEquals(repayLoanPage.waivedRepaymentAmount(), "2500.0");
        //When
        repayLoanPage.interestWaiver(false);
        //Then
        Assert.assertTrue(repayLoanPage.isTotalRepaymentAmountVisible());
        Assert.assertFalse(repayLoanPage.isWaivedRepaymentAmoutVisible());
        Assert.assertFalse(selenium.isTextPresent("Note: Interest due will be waived off."));
        Assert.assertEquals(repayLoanPage.totalRepaymentAmount(), "2509.1");
        //When
        RepayLoanParameters params = new RepayLoanParameters();
        params.setModeOfRepayment("Cash");
        loanAccountPage = repayLoanPage.submitAndNavigateToRepayLoanConfirmationPage(params).submitAndNavigateToLoanAccountDetailsPage();
        //Then
        loanAccountPage.verifyStatus("Closed- Obligation met");
        String[][] accountSummaryTable = {{"", "Original Loan", "Amount paid", "Loan balance"},
                {"Principal", "2,500", "2,500", "0"},
                {"Interest", "9.1", "9.1", "0"},
                {"Fees", "0", "0", "0"},
                {"Penalty", "0", "0", "0"},
                {"Total", "2,509.1", "2,509.1", "0"}};
        loanAccountPage.verifyAccountSummary(accountSummaryTable);
        loanAccountPage.navigateToAccountActivityPage();
        Assert.assertEquals("Loan Repayment", selenium.getTable("accountActivityTable.2.1").trim());
        Assert.assertEquals("2500.0", selenium.getTable("accountActivityTable.2.2").trim());
        Assert.assertEquals("0.0", selenium.getTable("accountActivityTable.2.10").trim());

    }
}

