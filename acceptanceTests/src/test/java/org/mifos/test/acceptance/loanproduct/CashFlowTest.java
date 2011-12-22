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
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountCashFlowPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountPreviewPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountReviewInstallmentPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loanproduct", "acceptance", "no_db_unit"})
public class CashFlowTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    String clientName = "WeeklyOld Tuesday";
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
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyCashFlowFields() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getMonthlyLoanProductParameters();
        loanProductTestHelper.navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                verifyCashFlowFieldDefault().
                verifyCashFlowFields();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyCashFlowWithNullValue() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        createAndValidateLoanProductWithCashFlow("", formParameters, "", "", false);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyCashFlowUnChecked() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        String loanProductName = formParameters.getOfferingName();
        loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                submitAndGotoNewLoanProductPreviewPage().
                verifyCashFlowUnCheckedInPreview().submit().
                navigateToViewLoanDetails().
                verifyCashFlowUnCheckedIn();
        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, loanProductName).
                clickContinue().
                verifyPage("SchedulePreview");
        applicationDatabaseOperation.updateLSIM(0);
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyCashFlowForNonVariableInstallmentLoan() throws Exception {
        applicationDatabaseOperation.updateLSIM(0);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        String minRc = "999.0";
        String warningThreshold = "89.99";
        createAndValidateLoanProductWithCashFlow(warningThreshold, formParameters, "49.99", minRc, false);
        validateCashFlowForLoanAccount(formParameters, minRc, "998.1");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyWarningsForNonVariableInstallmentLoan() throws Exception {
        applicationDatabaseOperation.updateLSIM(0);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setDefaultInterestRate("20");
        formParameters.setDefaultLoanAmount("1000");
        String warningThreshold = "89.99";
        createAndValidateLoanProductWithCashFlow(warningThreshold, formParameters, "", "", false);
        DateTime disbursalDate = systemDateTime.plusDays(1);
        int installment = 3;
        createLoanAccountAndNavigateToCashFlowPage(formParameters, disbursalDate, installment).
                enterValidData("1000", 1000, 1, null, null).clickContinue().
                clickPreviewAndGoToReviewLoanAccountPage();
        //verifyWarningForThreshold(warningThreshold);
        verifyNegativeAndZeroCashFlow(formParameters, disbursalDate, installment);

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyWarningsForVariableInstallmentLoan() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setDefaultInterestRate("20");
        formParameters.setDefaultLoanAmount("1000");
        String warningThreshold = "89.99";
        createAndValidateLoanProductWithCashFlow(warningThreshold, formParameters, "", "", true);
        DateTime disbursalDate = systemDateTime.plusDays(1);
        int installment = 3;
        verifyNegativeAndZeroCashFlow(formParameters, disbursalDate, installment);
        applicationDatabaseOperation.updateLSIM(0);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    /*
     * cant find disbursementDateDD element?
     */
    @Test(enabled=true)
    public void verifyCashFlowForVariableInstallmentLoan() throws Exception {
        String minRC = "999.99";
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        createAndValidateLoanProductWithCashFlow("89.99", formParameters, "49.99", minRC, true);
        validateCashFlowForLoanAccount(formParameters, minRC, "998.46");
        verifyRepaymentCapacityOnValidate(formParameters, minRC, "998.46");
        applicationDatabaseOperation.updateLSIM(0);
    }

    /**
     * Disabled after work on daily interest calculation for variable installments. - keithw-
     * 
     * Verify Cash Flow Page in Loan Account creation flow	
     * http://mifosforge.jira.com/browse/MIFOSTEST-672
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void verifyCashFlowPageInLoanAccountCreationFlow() throws Exception {
    	applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();	
        productParams.setDefInstallments("12");
        productParams.setDefaultLoanAmount("2000");
        CreateLoanAccountSearchParameters loanSearchParams = new CreateLoanAccountSearchParameters();
        loanSearchParams.setSearchString(clientName);
        loanSearchParams.setLoanProduct(productParams.getOfferingName());
        DateTime disbursalDate = systemDateTime.plusDays(1);

        loanProductTestHelper.navigateToDefineNewLoanPageAndFillMandatoryFields(productParams)
        		.fillCashFlow("", "49", "400")
        		.fillVariableInstalmentOption("", "1", "")
                .submitAndGotoNewLoanProductPreviewPage()
                .submit();
        CreateLoanAccountCashFlowPage cashFlowPage = loanTestHelper.navigateToCreateLoanAccountCashFlowPage(loanSearchParams);
        cashFlowPage.verifyTableExist();
        cashFlowPage.validateCashFlowMonths(disbursalDate, 12, DefineNewLoanProductPage.SubmitFormParameters.WEEKS);
        cashFlowPage.cancel();
        loanTestHelper.navigateToCreateLoanAccountCashFlowPage(loanSearchParams);
        cashFlowPage.submitWithErrors();
        cashFlowPage.verifyInvalidTextTyped();
        cashFlowPage.verifyErrorsOnPage();
        cashFlowPage.verifyMonths();
        cashFlowPage.verifyErrorsOnFields();
        cashFlowPage.enterValidData("1000", 1000, 100, "4000", "5000");
        cashFlowPage = cashFlowPage.submitWithErrors();
        
        cashFlowPage.verifyErrorsOnPage("Indebtedness rate of the client is 175 % which is greater than allowed value of 49 %.");
        cashFlowPage.enterValidData("1000", 1000, 100, "5000", "400");
        CreateLoanAccountReviewInstallmentPage reviewPage = cashFlowPage.clickContinue();
		reviewPage.verifyCashFlow(100.0, 2000.0, new String[]{"100.0","1858.0","1445.0","1716.0","2334.7","2600.0"}, new String[]{"0.0", "15.5","37.2","28.5","6.6","0.0"});
		reviewPage = reviewPage.clickPreview();
		
		reviewPage.verifyErrorsOnPage("Repayment Capacity of the client is 127.06 % which should be greater than the required value of 400.0 %");
		cashFlowPage = reviewPage.editCashFlow();
		cashFlowPage.enterValidData("1000", 1000, 1100, "5000", "400");
		reviewPage = cashFlowPage.clickContinue();
		

		CreateLoanAccountPreviewPage createLoanAccountPreviewPage = reviewPage.clickPreviewAndNavigateToPreviewPage();
		createLoanAccountPreviewPage.verifyLoanAmount("2,000");
		createLoanAccountPreviewPage.verifyInterestTypeInLoanPreview("Declining Balance");
		CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountPreviewPage.submit();
		createLoanAccountConfirmationPage.isTextPresentInPage("View loan account details now");
		LoanAccountPage loanAccountPage = createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
		loanAccountPage.verifyDisbursalDate("12/10/2010");
		ViewRepaymentSchedulePage viewRepaymentSchedulePage = loanAccountPage.navigateToViewRepaymentSchedule();
		
		String[][] tableAfterLastInstallment = { { "Future Installments", "", "", "", "", "" },
		        { "1", "19-Oct-2010", "-", "163.7", "7.3", "0.0", "0.0", "171.0" },
		        { "2", "26-Oct-2010", "-", "164.3", "6.7", "0.0", "0.0", "171.0" },
		        { "3", "02-Nov-2010", "-", "165.0", "6.0", "0.0", "0.0", "171.0" },
		        { "4", "09-Nov-2010", "-", "165.6", "5.4", "0.0", "0.0", "171.0" },
		        { "5", "16-Nov-2010", "-", "166.2", "4.8", "0.0", "0.0", "171.0" },
		        { "6", "23-Nov-2010", "-", "166.8", "4.2", "0.0", "0.0", "171.0" },
		        { "7", "30-Nov-2010", "-", "167.5", "3.5", "0.0", "0.0", "171.0" },
		        { "8", "07-Dec-2010", "-", "168.1", "2.9", "0.0", "0.0", "171.0" },
		        { "9", "14-Dec-2010", "-", "168.7", "2.3", "0.0", "0.0", "171.0" },
		        { "10", "21-Dec-2010", "-", "169.3", "1.7", "0.0", "0.0", "171.0" },
		        { "11", "28-Dec-2010", "-", "169.9", "1.1", "0.0", "0.0", "171.0" },
		        { "12", "04-Jan-2011", "-", "164.9", "0.4", "0.0", "0.0", "165.3" } };
		
		viewRepaymentSchedulePage.verifyScheduleTable(tableAfterLastInstallment);
		
		loanAccountPage = viewRepaymentSchedulePage.navigateToLoanAccountPage();
		EditLoanAccountInformationPage editLoanAccountInformationPage = loanAccountPage.navigateToEditAccountInformation();
		editLoanAccountInformationPage.verifyInstallments("12");
		editLoanAccountInformationPage.verifyInterestRate("19");
		editLoanAccountInformationPage.verifyDisbursalDate("12", "10", "2010");
		editLoanAccountInformationPage.verifyLoanAmount("2000.0");
    	applicationDatabaseOperation.updateLSIM(0);
    }
    
    private void verifyNegativeAndZeroCashFlow(DefineNewLoanProductPage.SubmitFormParameters formParameters, DateTime disbursalDate, int installment) {
        createLoanAccountAndNavigateToCashFlowPage(formParameters, disbursalDate, installment).
                enterValidData("10000", 1, 10, null, null).clickContinueAndVerifyNegativeOrZeroCashFlowWarning("September 2010",
                "October 2010", "November 2010", "December 2010");
        createLoanAccountAndNavigateToCashFlowPage(formParameters, disbursalDate, installment).
                enterValidData("100", 0, 100, null, null).clickContinueAndVerifyNegativeOrZeroCashFlowWarning("September 2010",
                "October 2010", "November 2010", "December 2010");
    }


    private CreateLoanAccountCashFlowPage createLoanAccountAndNavigateToCashFlowPage(DefineNewLoanProductPage.SubmitFormParameters formParameters, DateTime disbursalDate, int installment) {
        new NavigationHelper(selenium).navigateToHomePage();
        return loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, formParameters.getOfferingName()).
                setDisbursalDate(disbursalDate).
                setInstallments(installment).
                clickContinueToNavigateToCashFlowPage();
    }

    private void validateCashFlowForLoanAccount(DefineNewLoanProductPage.SubmitFormParameters formParameters, String minRc, String expectedRc) {
        DateTime disbursalDate = systemDateTime.plusDays(1); //next week tuesday
        int installment = 5;
        double cashFlowIncremental = 5685.0;
        String loanProductName = formParameters.getOfferingName();
        int frequency = formParameters.getFreqOfInstallments();
        verifyCashFlowForLoanAccount(disbursalDate, installment, cashFlowIncremental, loanProductName, frequency);
        verifyRepaymentCapacityOnPreview(disbursalDate, installment, cashFlowIncremental, loanProductName, expectedRc, minRc);
    }

    private void verifyRepaymentCapacityOnValidate(DefineNewLoanProductPage.SubmitFormParameters formParameters, String minRc, String expectedRc) {
        DateTime disbursalDate = systemDateTime.plusDays(1); //next week tuesday
        int installment = 5;
        int cashFlowIncremental = 5685;
        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, formParameters.getOfferingName()).
                setDisbursalDate(disbursalDate).
                setInstallments(installment).
                clickContinueToNavigateToCashFlowPage().
                enterValidData("100", cashFlowIncremental - 2, 100, "7003", "1000").
                clickContinue().verifyRepaymentCapacityOnValidate(expectedRc, minRc);
    }

    private void verifyRepaymentCapacityOnPreview(DateTime disbursalDate, int installment, double cashFlowIncremental, String loanProductName, String expectedRc, String minRc) {
        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, loanProductName).
                setDisbursalDate(disbursalDate).
                setInstallments(installment).
                clickContinueToNavigateToCashFlowPage().
                enterValidData("100", cashFlowIncremental + 10, 100, "7003", "1000").
                clickContinue().verifyRepaymentCapacityOnPreview(expectedRc, minRc);

    }


    private void verifyCashFlowForLoanAccount(DateTime disbursalDate, int installment, double cashFlowIncremental, String loanProductName, int frequency) {

        Integer loanAmount = Integer.valueOf(FormParametersHelper.getWeeklyLoanProductParameters().getDefaultLoanAmount());

        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, loanProductName).
                setDisbursalDate(disbursalDate).
                setInstallments(installment).
                clickContinueToNavigateToCashFlowPage().
                validateCashFlowMonths(disbursalDate, installment, frequency).
                verifyCashFlowFields().
                verifyInvalidIndebentRate("49.99", "7001", "1000").
                enterValidData("100", cashFlowIncremental, 100, "7003", "1000").
                clickContinue().
                verifyCashFlow(cashFlowIncremental, loanAmount).
                clickPreview(); 
    }

    private void createAndValidateLoanProductWithCashFlow(String warningThreshold, DefineNewLoanProductPage.SubmitFormParameters formParameters, String indebtentValue, String repaymentValue, boolean isVariableLoan) {
        DefineNewLoanProductPage loanProductPage = loanProductTestHelper.navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters);
        if (isVariableLoan) {
            loanProductPage.fillVariableInstalmentOption("", "1", "");
        }
        loanProductPage.fillCashFlow(warningThreshold, indebtentValue, repaymentValue).
                submitAndGotoNewLoanProductPreviewPage().
                verifyCashFlowInPreview(warningThreshold, indebtentValue, repaymentValue).
                submit().navigateToViewLoanDetails().
                verifyCashFlowInViewLoanProductPage(warningThreshold, indebtentValue, repaymentValue);
    }
}