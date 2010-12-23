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
import org.mifos.test.acceptance.admin.FeeTestHelper;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
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

import java.io.UnsupportedEncodingException;


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"loanproduct", "acceptance", "ui","smoke"})
public class DecliningPrincipleLoanTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    private final static String userLoginName = "test_user";
    private final static String officeName = "test_office";
    private final static String clientName = "test client";
    private final static String userName="test user";
    LoanProductTestHelper loanProductTestHelper;
    LoanTestHelper loanTestHelper;
    DateTime systemDateTime;
    NavigationHelper navigationHelper;
    String interestTypeName = "Declining Balance-Interest Recalculation";
    int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_PRINCIPLE_BALANCE;
    DateTime disbursalDate;
    boolean isLoanProductCreatedAndVerified = false;
    private TestDataSetup dataSetup;
    String feeName = "loanWeeklyFee";

    @AfterMethod
    public void logOut() {
         (new MifosPage(selenium)).logout();
    }

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        systemDateTime = new DateTime(2010, 10, 11, 10, 0, 0, 0);
        dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);
        loanTestHelper.setApplicationTime(systemDateTime);
        dataSetup.createBranch(OfficeParameters.BRANCH_OFFICE, officeName, "Off");
        dataSetup.createUser(userLoginName, userName, officeName);
        dataSetup.createClient(clientName, officeName, userName);
        dataSetup.addDecliningPrincipalBalance();
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyDecliningPrincipleLoan() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(3, 1000, 20, interestType);
        loanProductTestHelper.
                navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                submitAndGotoNewLoanProductPreviewPage().
                verifyInterestTypeInPreview(interestTypeName).
                submit().navigateToViewLoanDetails().
                verifyInterestTypeInSummary(interestTypeName);
        verifyDecliningPrincipalLoanAccount(3, interestTypeName, systemDateTime.plusDays(1), formParameters.getOfferingName());
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyLoanPayment() throws Exception {
        new FeeTestHelper(dataSetup).createPeriodicFee(feeName, FeesCreatePage.SubmitFormParameters.LOAN, FeesCreatePage.SubmitFormParameters.WEEKLY_FEE_RECURRENCE, 1, 100);
        int noOfInstallments = 4;
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(noOfInstallments, 1000, 24, interestType);
        createLoanProduct(formParameters);
        verifyEarlyExcessPayment(noOfInstallments, formParameters.getOfferingName());
        verifyEarlyLessPayment(noOfInstallments, formParameters.getOfferingName());
        verifyLateExcessPayment(noOfInstallments, formParameters.getOfferingName());
        verifyLateLessPayment(noOfInstallments, formParameters.getOfferingName());
//        verifyMultipleDue(noOfInstallments, formParameters.getOfferingName());
    }

//    private void verifyMultipleDue(int noOfInstallments, String loanProductName) throws UnsupportedEncodingException {
//        DateTime paymentDate = systemDateTime.plusDays(21);
//        createAndDisburseLoanAccount(noOfInstallments, systemDateTime.plusDays(1), loanProductName);
//        makePaymentAndVerifyPayment(paymentDate, "725", ScheduleData.MULTIPLE_DUE_PAYMENT);//verify first the due fee is knocked
//    }

    private void verifyLateLessPayment(int noOfInstallments, String loanProductName) throws UnsupportedEncodingException {
        DateTime paymentDate = systemDateTime.plusDays(12);
        createAndDisburseLoanAccount(noOfInstallments, systemDateTime.plusDays(1), loanProductName);
        makePaymentAndVerifyPayment(paymentDate, "100", ScheduleData.LATE_LESS_FIRST_PAYMENT);//verify first the due fee is knocked
        makePaymentAndVerifyPayment(paymentDate, "5.3", ScheduleData.LATE_LESS_SECOND_PAYMENT);//verify due interest is knocked next
        makePaymentAndVerifyPayment(paymentDate, "100", ScheduleData.LATE_LESS_THIRD_PAYMENT);//verify the due principle is knocked next
    }

    private void verifyLateExcessPayment(int noOfInstallments, String loanProductName) throws UnsupportedEncodingException {
        DateTime paymentDate = systemDateTime.plusDays(12);
        createAndDisburseLoanAccount(noOfInstallments,systemDateTime.plusDays(1),loanProductName);
        makePaymentAndVerifyPayment(paymentDate, "354", ScheduleData.LATE_EXCESS_PAYMENT);
        makePaymentAndVerifyPayment(paymentDate, "2.5", ScheduleData.LATE_EXCESS_SECOND_PAYMENT);//verifying only overdue interest in knocked
        makePaymentAndVerifyPayment(paymentDate, "100", ScheduleData.LATE_EXCESS_THIRD_PAYMENT);//verify if future interest in reduced as the future principle is paid
    }

    private void verifyEarlyLessPayment(int noOfInstallments, String loanProductName) throws UnsupportedEncodingException {
        DateTime paymentDate = systemDateTime.plusDays(1);
        createAndDisburseLoanAccount(noOfInstallments,systemDateTime.plusDays(1),loanProductName);
        makePaymentAndVerifyPayment(paymentDate, "100", ScheduleData.EARLY_LESS_FIRST_PAYMENT); //verifying interest till date
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void createLoan() throws Exception {
        createAndDisburseLoanAccount(4,systemDateTime.plusDays(1),"productWeekly7466");
    }

    private void verifyEarlyExcessPayment(int noOfInstallments, String loanProductName) throws UnsupportedEncodingException {
        DateTime paymentDate = systemDateTime.plusDays(1);
        createAndDisburseLoanAccount(noOfInstallments,systemDateTime.plusDays(1),loanProductName);
        makePaymentAndVerifyPayment(paymentDate, "280", ScheduleData.EARLY_EXCESS_FIRST_PAYMENT);
    }

    private void makePaymentAndVerifyPayment(DateTime paymentDate, String paymentAmount, String[][] expectedSchedule) throws UnsupportedEncodingException {
        makePayment(paymentDate, paymentAmount).
                verifyScheduleTable(expectedSchedule).navigateToLoanAccountPage();
    }

    private ViewRepaymentSchedulePage makePayment(DateTime systemDateTime, String paymentAmount) throws UnsupportedEncodingException {
        loanTestHelper.setApplicationTime(systemDateTime).navigateBack();
        return new LoanAccountPage(selenium).navigateToApplyPayment().
                submitAndNavigateToApplyPaymentConfirmationPage(setPaymentParams(paymentAmount, systemDateTime)).
                submitAndNavigateToLoanAccountDetailsPage().
                navigateToRepaymentSchedulePage();
    }

    private PaymentParameters setPaymentParams(String amount, ReadableInstant paymentDate) {
        String dd = DateTimeFormat.forPattern("dd").print(paymentDate);
        String mm = DateTimeFormat.forPattern("MM").print(paymentDate);
        String yyyy = DateTimeFormat.forPattern("yyyy").print(paymentDate);

        PaymentParameters paymentParameters = new PaymentParameters();
        paymentParameters.setAmount(amount);
        paymentParameters.setTransactionDateDD(dd);
        paymentParameters.setTransactionDateMM(mm);
        paymentParameters.setTransactionDateYYYY(yyyy);
        paymentParameters.setPaymentType(PaymentParameters.CASH);
        return paymentParameters;
    }

    private LoanAccountPage createAndDisburseLoanAccount(int noOfInstallments, DateTime disbursalDate, String loanProductName) throws UnsupportedEncodingException {
        loanTestHelper.setApplicationTime(systemDateTime);
        navigationHelper.navigateToHomePage();
        return loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName,loanProductName).
                setDisbursalDate(disbursalDate).
                setInstallments(noOfInstallments).
                clickContinue().
                clickPreviewAndGoToReviewLoanAccountPage().
                submit().navigateToLoanAccountDetailsPage().
                navigateToEditAccountStatus().
                submitAndNavigateToNextPage(setStatusParameters()).
                submitAndNavigateToLoanAccountPage().
                navigateToDisburseLoan().
                submitAndNavigateToDisburseLoanConfirmationPage(setDisbursalParams(disbursalDate.minusDays(1)))
                .submitAndNavigateToLoanAccountPage().navigateToApplyCharge().applyFeeAndConfirm(setCharge());
    }

    private ChargeParameters setCharge() {
        ChargeParameters chargeParameters = new ChargeParameters();
        chargeParameters.setType(feeName);
        return chargeParameters;
    }

    private DisburseLoanParameters setDisbursalParams(ReadableInstant validDisbursalDate) {
        DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
        String dd = DateTimeFormat.forPattern("dd").print(validDisbursalDate);
        String mm = DateTimeFormat.forPattern("MM").print(validDisbursalDate);
        String yyyy = DateTimeFormat.forPattern("yyyy").print(validDisbursalDate);

        disburseLoanParameters.setDisbursalDateDD(dd);
        disburseLoanParameters.setDisbursalDateMM(mm);
        disburseLoanParameters.setDisbursalDateYYYY(yyyy);
        disburseLoanParameters.setPaymentType(DisburseLoanParameters.CASH);
        return disburseLoanParameters;
    }

    private EditLoanAccountStatusParameters setStatusParameters() {
        EditLoanAccountStatusParameters loanAccountStatusParameters = new EditLoanAccountStatusParameters();
        loanAccountStatusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        loanAccountStatusParameters.setNote("test notes");
        return loanAccountStatusParameters;
    }

    private void createLoanProduct(DefineNewLoanProductPage.SubmitFormParameters formParameters) {
        if (isLoanProductCreatedAndVerified) {return;}
        loanProductTestHelper.
                navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                submitAndGotoNewLoanProductPreviewPage().
                submit();
        isLoanProductCreatedAndVerified = true;
    }

    private void verifyDecliningPrincipalLoanAccount(int noOfInstallments, String interestTypeName, DateTime disbursalDate, String loanProductName) {
        navigationHelper.navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName,loanProductName).
                setDisbursalDate(disbursalDate).
                setInstallments(noOfInstallments).
                verifyInterestTypeInLoanCreation(interestTypeName).
                clickContinue().
                verifyLoanScheduleForDecliningPrincipal().
                clickPreviewAndGoToReviewLoanAccountPage().
                verifyPage().verifyInterestTypeInLoanPreview(interestTypeName).
                submit().navigateToLoanAccountDetailsPage().
                verifyInterestTypeInLoanAccountDetails(interestTypeName).
                navigateToRepaymentSchedulePage().
                verifyScheduleForDecliningPrincipal(systemDateTime).
                verifyScheduleDateField();
    }

    private DefineNewLoanProductPage.SubmitFormParameters defineLoanProductParameters(int defInstallments, int defaultLoanAmount, int defaultInterestRate, int interestType) {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setDefInstallments(String.valueOf(defInstallments));
        formParameters.setDefaultLoanAmount(String.valueOf(defaultLoanAmount));
        formParameters.setInterestTypes(interestType);
        formParameters.setDefaultInterestRate(String.valueOf(defaultInterestRate));
        return formParameters;
    }

}

