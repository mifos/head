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
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"acceptance", "ui", "loan", "no_db_unit"})
public class LoanAccountCycleTest extends UiTestCaseBase {
    
    private LoanTestHelper loanTestHelper;
    private LoanProductTestHelper loanProductTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 25, 15, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        loanTestHelper = new LoanTestHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    /**
     * Verify the loan product creation for the new loan product when
     * the "Calculate Loan Amount as:" " same for all loans." and
     * "Calculate # of Installments as:" "by last loan amount"
     * http://mifosforge.jira.com/browse/MIFOSTEST-101
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyProductCreationWhenInstallmentsByLastAmount() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        productParams.setOfferingName("LastAmountBasedProduct");
        productParams.setOfferingShortName("p101");
        productParams.setMinLoanAmount("1000.0");
        productParams.setMaxLoanAmount("7000.0");
        productParams.setDefaultLoanAmount("2000.0");
        productParams.setCalculateInstallments(SubmitFormParameters.BY_LAST_LOAN_AMOUNT);
        String[][] calculateInstallments = getInstallmentsFromLastAmount();
        productParams.setInstallmentsByLastLoanAmount(calculateInstallments);

        LoanProductDetailsPage loanProductDetailsPage = loanProductTestHelper.defineNewLoanProduct(productParams);
        loanProductDetailsPage.verifyInstallmentTableTypeFromLastAmount(calculateInstallments);
        loanProductDetailsPage.verifyLoanAmountTableTypeSame("1000.0", "7000.0", "2000.0");
    }

    /**
     * Verify loan amount with number of installments is same for all loan can be used to create new loans.
     * http://mifosforge.jira.com/browse/MIFOSTEST-105
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyNumberOfInstallmentsSameForAllLoans() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        productParams.setOfferingName("LastCycleBasedProduct");
        productParams.setOfferingShortName("p105");
        productParams.setCalculateLoanAmount(SubmitFormParameters.BY_LOAN_CYCLE);
        String[][] cycleLoanAmount = getAmountsByCycle();
        productParams.setCycleLoanAmount(cycleLoanAmount);
        productParams.setMinInstallemnts("10");
        productParams.setMaxInstallments("100");
        productParams.setDefInstallments("50");
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233171716380 Client1233171716380");
        searchParams.setLoanProduct("LastCycleBasedProduct");
        DisburseLoanParameters disburseParams = DisburseLoanParameters.getDisbursalParameters("25", "02", "2011");

        LoanProductDetailsPage loanProductDetailsPage = loanProductTestHelper.defineNewLoanProduct(productParams);
        loanProductDetailsPage.verifyLoanAmountTableTypeFromCycle(cycleLoanAmount);
        loanProductDetailsPage.verifyInstallments("10", "100", "50");
        LoanAccountPage loanAccountPage = loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"1000.0", "5000.0", "3000.0"}, null, new String[]{"10", "100", "50"});
        String loan1ID = loanAccountPage.getAccountId();
        loanAccountPage.disburseLoan(disburseParams);

        loanAccountPage = loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"2000.0", "6000.0", "4000.0"}, null, new String[]{"10", "100", "50"});
        String loan2ID = loanAccountPage.getAccountId();
        loanTestHelper.repayLoan(loan1ID);
        loanTestHelper.disburseLoan(loan2ID, disburseParams);

        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"3000.0", "7000.0", "5000.0"}, null, new String[]{"10", "100", "50"});
    }

    /**
     * Verify loan amount with number of installments by loan cycle can be used to create new loans.
     * http://mifosforge.jira.com/browse/MIFOSTEST-107
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAmountAndInstallmentsByCycles() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        productParams.setOfferingName("product107");
        productParams.setOfferingShortName("p107");
        productParams.setCalculateLoanAmount(SubmitFormParameters.BY_LOAN_CYCLE);
        String[][] cycleLoanAmount = getAmountsByCycle();
        productParams.setCycleLoanAmount(cycleLoanAmount);
        productParams.setCalculateInstallments(SubmitFormParameters.BY_LOAN_CYCLE);
        String[][] calculateInstallments = getInstallmentsByCycle();
        productParams.setCycleInstallments(calculateInstallments);
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233171716380 Client1233171716380");
        searchParams.setLoanProduct("product107");
        DisburseLoanParameters disburseParams = DisburseLoanParameters.getDisbursalParameters("25", "02", "2011");

        LoanProductDetailsPage loanProductDetailsPage = loanProductTestHelper.defineNewLoanProduct(productParams);
        loanProductDetailsPage.verifyLoanAmountTableTypeFromCycle(cycleLoanAmount);
        loanProductDetailsPage.verifyInstallmentsTableTypeFromCycle(calculateInstallments);
        LoanAccountPage loanAccountPage = loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"1000.0", "5000.0", "3000.0"}, null, new String[]{"26", "52", "52"});
        loanAccountPage.disburseLoan(disburseParams);

        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"2000.0", "6000.0", "4000.0"}, null, new String[]{"20", "30", "30"});
    }

    /**
     * Verify loan amount with number of installments by last loan amount can be used to create new loans.
     * http://mifosforge.jira.com/browse/MIFOSTEST-110
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAmountByCycleAndInstallmentsByLastAmount() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        productParams.setOfferingName("product110");
        productParams.setOfferingShortName("p110");
        productParams.setCalculateLoanAmount(SubmitFormParameters.BY_LOAN_CYCLE);
        String[][] cycleLoanAmount = getAmountsByCycle();
        productParams.setCycleLoanAmount(cycleLoanAmount);
        productParams.setCalculateInstallments(SubmitFormParameters.BY_LAST_LOAN_AMOUNT);
        String[][] calculateInstallments = getInstallmentsFromLastAmount();
        productParams.setInstallmentsByLastLoanAmount(calculateInstallments);
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233171716380 Client1233171716380");
        searchParams.setLoanProduct("product110");
        DisburseLoanParameters disburseParams = DisburseLoanParameters.getDisbursalParameters("25", "02", "2011");

        LoanProductDetailsPage loanProductDetailsPage = loanProductTestHelper.defineNewLoanProduct(productParams);
        loanProductDetailsPage.verifyLoanAmountTableTypeFromCycle(cycleLoanAmount);
        loanProductDetailsPage.verifyInstallmentTableTypeFromLastAmount(calculateInstallments);
        LoanAccountPage loanAccountPage = loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"1000.0", "5000.0", "3000.0"}, null, new String[]{"5", "10", "5"});
        String loanFirstID = loanAccountPage.getAccountId();
        loanAccountPage.disburseLoan(disburseParams);
        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"2000.0", "6000.0", "4000.0"}, null, new String[]{"5", "10", "5"});
        loanTestHelper.repayLoan(loanFirstID);

        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"2000.0", "6000.0", "4000.0"}, null, new String[]{"10", "30", "25"});
    }

    /**
     * Verify loan amount with number of installments is same for all loan can be used to create new loans.
     * http://mifosforge.jira.com/browse/MIFOSTEST-112
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAmountByLastAmountAndInstallmentsSameForAll() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        productParams.setOfferingName("product112");
        productParams.setOfferingShortName("p112");
        productParams.setCalculateLoanAmount(SubmitFormParameters.BY_LAST_LOAN_AMOUNT);
        String[][] lastLoanAmount = getAmountsFromLastAmount();
        productParams.setAmountsByLastLoanAmount(lastLoanAmount);
        productParams.setMinInstallemnts("10");
        productParams.setMaxInstallments("100");
        productParams.setDefInstallments("50");
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233171716380 Client1233171716380");
        searchParams.setLoanProduct("product112");
        DisburseLoanParameters disburseParams = DisburseLoanParameters.getDisbursalParameters("25", "02", "2011");

        LoanProductDetailsPage loanProductDetailsPage = loanProductTestHelper.defineNewLoanProduct(productParams);
        loanProductDetailsPage.verifyAmountTableTypeFromLastAmount(lastLoanAmount);
        loanProductDetailsPage.verifyInstallments("10", "100", "50");
        LoanAccountPage loanAccountPage = loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"500.0", "1500.0", "1200.0"}, null, null);
        String loanFirstID = loanAccountPage.getAccountId();
        loanAccountPage.disburseLoan(disburseParams);
        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"500.0", "1500.0", "1200.0"}, null, null);
        loanTestHelper.repayLoan(loanFirstID);

        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"1500.0", "2500.0", "2200.0"}, null, null);
    }

    /**
     * Verify loan amount with number of installments by loan cycle can be used to create new loans.
     * http://mifosforge.jira.com/browse/MIFOSTEST-114
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAmountsByLastAmountAndInstallmentsByCycle() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        productParams.setOfferingName("product114");
        productParams.setOfferingShortName("p114");
        productParams.setCalculateLoanAmount(SubmitFormParameters.BY_LAST_LOAN_AMOUNT);
        String[][] lastLoanAmount = getAmountsFromLastAmount();
        productParams.setAmountsByLastLoanAmount(lastLoanAmount);
        productParams.setCalculateInstallments(SubmitFormParameters.BY_LOAN_CYCLE);
        String[][] calculateInstallments = getInstallmentsByCycle();
        productParams.setCycleInstallments(calculateInstallments);
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233171716380 Client1233171716380");
        searchParams.setLoanProduct("product114");
        DisburseLoanParameters disburseParams = DisburseLoanParameters.getDisbursalParameters("25", "02", "2011");

        LoanProductDetailsPage loanProductDetailsPage = loanProductTestHelper.defineNewLoanProduct(productParams);
        loanProductDetailsPage.verifyAmountTableTypeFromLastAmount(lastLoanAmount);
        loanProductDetailsPage.verifyInstallmentsTableTypeFromCycle(calculateInstallments);
        LoanAccountPage loanAccountPage = loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"500.0", "1500.0", "1200.0"}, null, new String[]{"26", "52", "52"});
        String loanFirstID = loanAccountPage.getAccountId();
        loanAccountPage.disburseLoan(disburseParams);
        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"500.0", "1500.0", "1200.0"}, null, new String[]{"20", "30", "30"});
        loanTestHelper.repayLoan(loanFirstID);

        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"1500.0", "2500.0", "2200.0"}, null, new String[]{"20", "30", "30"});
    }

    /**
     * Verify loan amount with number of installments by last loan amount can be used to create new loans.
     * http://mifosforge.jira.com/browse/MIFOSTEST-116
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAmountsAndInstallmentsByLastAmount() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        productParams.setOfferingName("product116");
        productParams.setOfferingShortName("p116");
        productParams.setCalculateLoanAmount(SubmitFormParameters.BY_LAST_LOAN_AMOUNT);
        String[][] lastLoanAmount = getAmountsFromLastAmount();
        productParams.setAmountsByLastLoanAmount(lastLoanAmount);
        productParams.setCalculateInstallments(SubmitFormParameters.BY_LAST_LOAN_AMOUNT);
        String[][] calculateInstallments = getInstallmentsFromLastAmount();
        productParams.setInstallmentsByLastLoanAmount(calculateInstallments);
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233171716380 Client1233171716380");
        searchParams.setLoanProduct("product116");
        DisburseLoanParameters disburseParams = DisburseLoanParameters.getDisbursalParameters("25", "02", "2011");

        LoanProductDetailsPage loanProductDetailsPage = loanProductTestHelper.defineNewLoanProduct(productParams);
        loanProductDetailsPage.verifyAmountTableTypeFromLastAmount(lastLoanAmount);
        loanProductDetailsPage.verifyInstallmentTableTypeFromLastAmount(calculateInstallments);
        LoanAccountPage loanAccountPage = loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"500.0", "1500.0", "1200.0"}, null, new String[]{"5", "10", "5"});
        String loanFirstID = loanAccountPage.getAccountId();
        loanAccountPage.disburseLoan(disburseParams);
        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"500.0", "1500.0", "1200.0"}, null, new String[]{"5", "10", "5"});
        loanTestHelper.repayLoan(loanFirstID);

        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"1500.0", "2500.0", "2200.0"}, null, new String[]{"10", "20", "15"});
    }

    private String[][] getInstallmentsFromLastAmount() {
        return new String[][]{
                {"1000", "5", "10", "5"},
                {"2000", "10", "20", "15"},
                {"3000", "10", "30", "25"},
                {"4000", "20", "50", "30"},
                {"5000", "20", "50", "35"},
                {"6000", "30", "60", "40"}
        };
    }

    private String[][] getAmountsFromLastAmount() {
        return new String[][]{
                {"1000", "500.0", "1500.0", "1200.0"},
                {"2000", "1500.0", "2500.0", "2200.0"},
                {"3000", "2500.0", "3500.0", "3200.0"},
                {"4000", "3500.0", "4500.0", "4200.0"},
                {"5000", "4500.0", "5500.0", "5200.0"},
                {"6000", "5500.0", "6500.0", "6200.0"}
        };
    }

    private String[][] getAmountsByCycle() {
        return new String[][]{
                {"1000.0", "5000.0", "3000.0"},
                {"2000.0", "6000.0", "4000.0"},
                {"3000.0", "7000.0", "5000.0"},
                {"4000.0", "8000.0", "6000.0"},
                {"5000.0", "9000.0", "7000.0"},
                {"6000.0", "10000.0", "8000.0"}
        };
    }

    private String[][] getInstallmentsByCycle() {
        return new String[][]{
                {"26", "52", "52"},
                {"20", "30", "30"},
                {"15", "25", "25"},
                {"10", "15", "15"},
                {"5", "10", "10"},
                {"1", "5", "5"}
        };
    }
}
