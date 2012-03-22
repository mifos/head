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

package org.mifos.test.acceptance.loan.multicurrency;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.admin.ViewOrganizationSettingsPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.loanproduct.multicurrrency.DefineNewDifferentCurrencyLoanProductPage.SubmitMultiCurrencyFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;


@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = { "loanproduct", "acceptance"})
public class LoanProcessWithDifferentCurrencyTest extends UiTestCaseBase {

    //private AppLauncher appLauncher;

    private LoanTestHelper loanTestHelper;

    private CustomPropertiesHelper propertiesHelper;
    private NavigationHelper navigationHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();

        navigationHelper = new NavigationHelper(selenium);
        propertiesHelper = new CustomPropertiesHelper(selenium);

    }

    @AfterMethod
    public void logOut() {
        // clean additional currencies
        propertiesHelper.setAdditionalCurrenciesCode("");
        initRemote.reinitializeApplication(selenium);
        (new MifosPage(selenium)).logout();
    }


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    // http://mifosforge.jira.com/browse/MIFOSTEST-657
    public void createLoanProductThenAccount() throws Exception {
        //Given
        propertiesHelper.setAdditionalCurrenciesCode("EUR,USD");
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,2,17,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "LoanProcessWithDifferentCurrencyTest_001_dbunit.xml", dataSource, selenium);

        //When
        propertiesHelper.setDigitsAfterDecimal(2);
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewOrganizationSettingsPage viewOrganizationSettingsPage = adminPage.navigateToViewOrganizationSettingsPage();

        viewOrganizationSettingsPage.verifyCurrencies(new String[]{"Currency: USD"});
        viewOrganizationSettingsPage.verifyCurrencies(new String[]{"Currency: EUR"});
        viewOrganizationSettingsPage.verifyCurrencies(new String[]{"Number of digits after decimal: 2"});
        viewOrganizationSettingsPage.verifyCurrencies(new String[]{"Final Round Off Multiple: 1"});
        viewOrganizationSettingsPage.verifyCurrencies(new String[]{"Initial Round Off Multiple: 1"});

        //Then
        FeesCreatePage.SubmitFormParameters formParameters = FormParametersHelper.getOneTimeLoanMultiCurrencyFeesParameters();
        adminPage = navigationHelper.navigateToAdminPage();
        formParameters.setFeeName("USDfee");
        adminPage.defineNewFees(formParameters);

        formParameters = FormParametersHelper.getOneTimeLoanMultiCurrencyFeesParameters();
        adminPage = navigationHelper.navigateToAdminPage();
        formParameters.setFeeName("USDfeeAdditional");
        formParameters.setAmount(20.0);
        adminPage.defineNewFees(formParameters);

        createWeeklyLoanProduct();

        String loanAccountId = createLoanAccountOfDifferentCurrency("Client-1-USD");
        pendingApprovalToApplicationApproved(loanAccountId);
        disburseLoan(loanAccountId);
        applyPayment(loanAccountId);

        navigationHelper.navigateToLoanAccountPage(loanAccountId);
        //veryfy "USDfee"(10) and "USDfeeAdditional"(20)
        Assert.assertEquals(selenium.getTable("loanSummaryTable.3.1"),"30");
        //restore parameters
        propertiesHelper.setDigitsAfterDecimal(1);
    }

     @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    private void createWeeklyLoanProduct() throws Exception {
        SubmitMultiCurrencyFormParameters formParameters = getWeeklyLoanProductParameters();
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        adminPage.defineMultiCurrencyLoanProduct(formParameters);
    }

     @SuppressWarnings({ "PMD.SignatureDeclareThrowsException" })
    // one of the dependent methods throws Exception
    private String createLoanAccountOfDifferentCurrency(String clientName) throws Exception {
        loanTestHelper = new LoanTestHelper(selenium);
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString(clientName);
        searchParameters.setLoanProduct("Loan With Different Currency");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1012");
        String fee = "USDfeeAdditional";
        submitAccountParameters.setAdditionalFee1(fee);
        return createLoanAndCheckAmount(searchParameters, submitAccountParameters);
    }

     @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void pendingApprovalToApplicationApproved(String loanAccountId) throws Exception {

        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        statusParameters.setNote("Loan With Different Currency Approved (Test)");

        loanTestHelper.changeLoanAccountStatus(loanAccountId, statusParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void disburseLoan(String loanAccountId) throws Exception {

        // account w/ id 000100000000029 has an approved but not disbursed loan.

        DisburseLoanParameters params = new DisburseLoanParameters();

        params.setDisbursalDateDD("17");
        params.setDisbursalDateMM("02");
        params.setDisbursalDateYYYY("2011");
        params.setPaymentType(DisburseLoanParameters.CASH);

        loanTestHelper.disburseLoan(loanAccountId, params);
    }

    private String createLoanAndCheckAmount(CreateLoanAccountSearchParameters searchParameters,
            CreateLoanAccountSubmitParameters submitAccountParameters) {
        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        loanAccountPage.verifyLoanAmount(submitAccountParameters.getAmount());
        return loanAccountPage.getAccountId();
    }

    private SubmitMultiCurrencyFormParameters getWeeklyLoanProductParameters() {
        SubmitMultiCurrencyFormParameters formParameters = new SubmitMultiCurrencyFormParameters();
        formParameters.setOfferingName("Loan With Different Currency");
        formParameters.setOfferingShortName("DC" + StringUtil.getRandomString(2));
        formParameters.setDescription("descriptionForWeekly1");
        formParameters.setCategory("Other");
        formParameters.setApplicableFor(SubmitFormParameters.CLIENTS);
        formParameters.setMinLoanAmount("1000");
        formParameters.setMaxLoanAmount("19000");
        formParameters.setDefaultLoanAmount("2500");
        formParameters.setInterestTypes(SubmitFormParameters.DECLINING_BALANCE);
        formParameters.setMaxInterestRate("10");
        formParameters.setMinInterestRate("6");
        formParameters.setDefaultInterestRate("9");
        formParameters.setFreqOfInstallments(SubmitFormParameters.WEEKS);
        formParameters.setMaxInstallments("10");
        formParameters.setDefInstallments("5");
        formParameters.setGracePeriodType(SubmitFormParameters.NONE);
        formParameters.setInterestGLCode("31102 - Penalty");
        formParameters.setPrincipalGLCode("1506 - Managed WFLoan");
        formParameters.setCurrencyId(Short.valueOf("1"));
        String fee="USDfee";
        formParameters.setAdditionalFee1(fee);
        return formParameters;
    }

    private void applyPayment(String loanAccountId) {
        PaymentParameters paymentParameters = new PaymentParameters();
        paymentParameters.setAmount("1018"); // interest + principal
        paymentParameters.setTransactionDateDD("17");
        paymentParameters.setTransactionDateMM("02");
        paymentParameters.setTransactionDateYYYY("2011");
        paymentParameters.setPaymentType(PaymentParameters.CASH);

        loanTestHelper.applyPayment(loanAccountId, paymentParameters);
    }
}
