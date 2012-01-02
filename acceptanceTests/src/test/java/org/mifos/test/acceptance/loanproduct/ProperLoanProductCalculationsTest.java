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
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loanproduct", "acceptance","no_db_unit"})
public class ProperLoanProductCalculationsTest extends UiTestCaseBase {

    private LoanProductTestHelper loanProductTestHelper;
    private LoanTestHelper loanTestHelper;
    private CustomPropertiesHelper  customPropertiesHelper;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009, 7, 1, 12, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        customPropertiesHelper = new CustomPropertiesHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        setDefaultProperties();
        (new MifosPage(selenium)).logout();
    }

    private void setDefaultProperties() {
        customPropertiesHelper.setDigitsAfterDecimal(1);
        customPropertiesHelper.setCurrencyRoundingMode(CustomPropertiesHelper.ROUNDING_MODE_HALF_UP);
        customPropertiesHelper.setInitialRoundingMode(CustomPropertiesHelper.ROUNDING_MODE_HALF_UP);
        customPropertiesHelper.setFinalRoundingMode(CustomPropertiesHelper.ROUNDING_MODE_CEILING);
        customPropertiesHelper.setInitialRoundOffMultiple("1");
        customPropertiesHelper.setFinalRoundOffMultiple("1");
    }

    /**
     * Flat interest rate weekly loan calculates and displays proper interest and payment information.
     * http://mifosforge.jira.com/browse/MIFOSTEST-63
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyProperInterestAndPaymentWeeklyFlatProduct() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        String productName = "LoanProduct" + StringUtil.getRandomString(5);
        productParams.setOfferingName(productName);
        productParams.setOfferingShortName(StringUtil.getRandomString(4));
        productParams.setDefaultInterestRate("36");
        productParams.setMaxInterestRate("50");
        productParams.setInterestTypes(SubmitFormParameters.FLAT);
        productParams.setDefaultLoanAmount("2500");
        productParams.setDefInstallments("11");
        loanProductTestHelper.defineNewLoanProduct(productParams);


        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233266063395 Client1233266063395");
        searchParams.setLoanProduct(productName);
        LoanAccountPage loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(searchParams);

        loanAccountPage.verifyPrincipalOriginal("2,500");
        loanAccountPage.verifyInterestOriginal("190");
        loanAccountPage.verifyFeesOriginal("0");
        loanAccountPage.verifyPenaltyOriginal("0");
        loanAccountPage.verifyTotalOriginalLoan("2690.0");
    }

    /**
     * Flat interest rate monthly loan calculates and displays proper interest and payment information.
     * http://mifosforge.jira.com/browse/MIFOSTEST-64
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyProperInterestAndPaymentMonthlyFlatProduct() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getMonthlyLoanProductParameters();
        String productName = "LoanProduct" + StringUtil.getRandomString(5);
        productParams.setOfferingName(productName);
        productParams.setOfferingShortName(StringUtil.getRandomString(4));
        productParams.setDefaultInterestRate("23");
        productParams.setMaxInterestRate("50");
        productParams.setInterestTypes(SubmitFormParameters.FLAT);
        productParams.setDefaultLoanAmount("17231");
        productParams.setDefInstallments("23");
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Client - Mary Monthly");
        searchParams.setLoanProduct(productName);

        loanProductTestHelper.defineNewLoanProduct(productParams);
        LoanAccountPage loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(searchParams);

        loanAccountPage.verifyPrincipalOriginal("17,231");
        loanAccountPage.verifyInterestOriginal("7,596");
        loanAccountPage.verifyFeesOriginal("0");
        loanAccountPage.verifyPenaltyOriginal("0");
        loanAccountPage.verifyTotalOriginalLoan("24827.0");
    }

    /**
     * Declining balance, fixed payment type weekly loan calculates and
     * displays proper interest and payment information.
     * http://mifosforge.jira.com/browse/MIFOSTEST-66
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyProperInterestAndPaymentWeeklyDecliningBalanceProduct() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        String productName = "LoanProduct" + StringUtil.getRandomString(5);
        productParams.setOfferingName(productName);
        productParams.setOfferingShortName(StringUtil.getRandomString(4));
        productParams.setDefaultInterestRate("15.3");
        productParams.setMaxInterestRate("50");
        productParams.setInterestTypes(SubmitFormParameters.DECLINING_BALANCE);
        productParams.setDefaultLoanAmount("13333");
        productParams.setDefInstallments("13");
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233266063395 Client1233266063395");
        searchParams.setLoanProduct(productName);

        loanProductTestHelper.defineNewLoanProduct(productParams);
        LoanAccountPage loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(searchParams);

        loanAccountPage.verifyPrincipalOriginal("13,333");
        loanAccountPage.verifyInterestOriginal("276");
        loanAccountPage.verifyFeesOriginal("0");
        loanAccountPage.verifyPenaltyOriginal("0");
        loanAccountPage.verifyTotalOriginalLoan("13609.0");
    }

    /**
     * Declining fixed principal fixed payment type monthly loan calculates and
     * displays proper interest and payment information.
     * http://mifosforge.jira.com/browse/MIFOSTEST-67
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyProperInterestAndPaymentMonthlyDecliningFixedProduct() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getMonthlyLoanProductParameters();
        String productName = "LoanProduct" + StringUtil.getRandomString(5);
        productParams.setOfferingName(productName);
        productParams.setOfferingShortName(StringUtil.getRandomString(4));
        productParams.setDefaultInterestRate("19.9");
        productParams.setMaxInterestRate("50");
        productParams.setInterestTypes(SubmitFormParameters.DECLINING_BALANCE_EPI);
        productParams.setDefaultLoanAmount("57199");
        productParams.setDefInstallments("32");
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Client - Mary Monthly");
        searchParams.setLoanProduct(productName);

        loanProductTestHelper.defineNewLoanProduct(productParams);
        LoanAccountPage loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(searchParams);

        loanAccountPage.verifyPrincipalOriginal("57,199");
        loanAccountPage.verifyInterestOriginal("15,652");
        loanAccountPage.verifyFeesOriginal("0");
        loanAccountPage.verifyPenaltyOriginal("0");
        loanAccountPage.verifyTotalOriginalLoan("72851.0");
    }

    /**
     * Verify flat interest loan with non-default accounting rules for digits after decimals
     * and rounding mode properties are used to generate and present calculations
     * accurately and are reflected in Admin System Information.
     * http://mifosforge.jira.com/browse/MIFOSTEST-69
     * @throws Exception
     */
    @Test(enabled=false) 
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyProperCalculationsFlatProductNonDefaultProperties() throws Exception {
        customPropertiesHelper.setDigitsAfterDecimal(3);
        customPropertiesHelper.setCurrencyRoundingMode(CustomPropertiesHelper.ROUNDING_MODE_CEILING);
        customPropertiesHelper.setInitialRoundingMode(CustomPropertiesHelper.ROUNDING_MODE_FLOOR);
        customPropertiesHelper.setFinalRoundingMode(CustomPropertiesHelper.ROUNDING_MODE_FLOOR);
        customPropertiesHelper.setFinalRoundOffMultiple("0.01");
        customPropertiesHelper.setInitialRoundOffMultiple("0.01");

        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        String productName = "LoanProduct" + StringUtil.getRandomString(5);
        productParams.setOfferingName(productName);
        productParams.setOfferingShortName(StringUtil.getRandomString(4));
        productParams.setDefaultInterestRate("29.001");
        productParams.setMaxInterestRate("50");
        productParams.setInterestTypes(SubmitFormParameters.FLAT);
        productParams.setDefaultLoanAmount("10000.191");
        productParams.setDefInstallments("101");
        productParams.setMaxInstallments("200");
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233266063395 Client1233266063395");
        searchParams.setLoanProduct(productName);

        loanProductTestHelper.defineNewLoanProduct(productParams);
        LoanAccountPage loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(searchParams);

        loanAccountPage.verifyPrincipalOriginal("10,000.191");
        loanAccountPage.verifyInterestOriginal("5,617.559");
        loanAccountPage.verifyFeesOriginal("0");
        loanAccountPage.verifyPenaltyOriginal("0");
        loanAccountPage.verifyTotalOriginalLoan("15617.75");
    }

    /**
     * Verify declining balance, fixed payment interest loan with non-default
     * accounting rules for digits after decimals and rounding mode properties are
     * used to generate and present calculations accurately and are reflected in Admin System Information.
     * http://mifosforge.jira.com/browse/MIFOSTEST-70
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=false)
    public void verifyProperCalculationsFixedProductNonDefaultProperties() throws Exception {
        customPropertiesHelper.setDigitsAfterDecimal(2);
        customPropertiesHelper.setCurrencyRoundingMode(CustomPropertiesHelper.ROUNDING_MODE_FLOOR);
        customPropertiesHelper.setInitialRoundingMode(CustomPropertiesHelper.ROUNDING_MODE_HALF_UP);
        customPropertiesHelper.setFinalRoundingMode(CustomPropertiesHelper.ROUNDING_MODE_HALF_UP);
        customPropertiesHelper.setInitialRoundOffMultiple("0.5");
        customPropertiesHelper.setFinalRoundOffMultiple("0.1");

        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        String productName = "LoanProduct" + StringUtil.getRandomString(5);
        productParams.setOfferingName(productName);
        productParams.setOfferingShortName(StringUtil.getRandomString(4));
        productParams.setDefaultInterestRate("9.5");
        productParams.setMinInterestRate("5");
        productParams.setInterestTypes(SubmitFormParameters.DECLINING_BALANCE_EPI);
        productParams.setDefaultLoanAmount("19999.9");
        productParams.setDefInstallments("79");
        productParams.setMaxInstallments("79");
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233266063395 Client1233266063395");
        searchParams.setLoanProduct(productName);

        loanProductTestHelper.defineNewLoanProduct(productParams);
        LoanAccountPage loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(searchParams);

        loanAccountPage.verifyPrincipalOriginal("19,999.9");
        loanAccountPage.verifyInterestOriginal("1,457.5");
        loanAccountPage.verifyFeesOriginal("0");
        loanAccountPage.verifyPenaltyOriginal("0");
        loanAccountPage.verifyTotalOriginalLoan("21457.4");
    }

    /**
     * Verify the interest should be calculated with 365 days as the base.
     * http://mifosforge.jira.com/browse/MIFOSTEST-199
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyInterestCalculatedWith365Days() throws Exception {
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("000100000000052");

        LoanAccountPage loanAccountPage = loanTestHelper.navigateToLoanAccountPage(searchParams);

        loanAccountPage.verifyPrincipalOriginal("10,000");
        loanAccountPage.verifyInterestOriginal("461");
        loanAccountPage.verifyFeesOriginal("0");
        loanAccountPage.verifyPenaltyOriginal("0");
        loanAccountPage.verifyTotalOriginalLoan("10461.0");
    }
}
