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
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.loanproduct.ViewLoanProductsPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductPage.SubmitSavingsFormParameters;
import org.mifos.test.acceptance.framework.savingsproduct.EditSavingsProductPage;
import org.mifos.test.acceptance.framework.savingsproduct.EditSavingsProductPreviewPage;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductDetailsPage;
import org.mifos.test.acceptance.framework.savingsproduct.ViewSavingsProductsPage;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"loanproduct", "acceptance", "ui","smoke"})
public class EditLoanProductTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    private AppLauncher appLauncher;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;


    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void logOut() {
//        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void viewExistingLoanProduct() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
        ViewLoanProductsPage viewLoanProducts = loginAndNavigateToViewLoanProductsPage();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails("FlatInterestLoanProduct1");
        loanProductDetailsPage.verifyPage();

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void editExistingLoanProduct() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
        ViewLoanProductsPage viewLoanProducts = loginAndNavigateToViewLoanProductsPage();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails("FlatInterestLoanProduct1");
        EditLoanProductPage editLoanProductPage = loanProductDetailsPage.editLoanProduct();
        editLoanProductPage.verifyPage();
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setDescription("Modified Description");
        formParameters.setMaxInterestRate("44");
        formParameters.setMinInterestRate("3");
        formParameters.setDefaultInterestRate("18");

        EditLoanProductPreviewPage editLoanProductPreviewPage = editLoanProductPage.submitDescriptionAndInterestChanges(formParameters);
        loanProductDetailsPage = editLoanProductPreviewPage.submit();
        loanProductDetailsPage.verifyPage();
        loanProductDetailsPage.editLoanProduct();
        editLoanProductPage.verifyPage();
        editLoanProductPage.verifyModifiedDescriptionAndInterest(formParameters);

    }

    private SubmitFormParameters cleanFormParameters(SubmitFormParameters formParameters){
        formParameters.setOfferingName("");
        formParameters.setOfferingShortName("");
        formParameters.setDescription("Description");
        formParameters.setProductCategory(0);
        formParameters.setStartDateDd("");
        formParameters.setStartDateMm("");
        formParameters.setStartDateYy("");
        formParameters.setApplicableFor(0);
        formParameters.setStatus(0);
        formParameters.setInterestTypes(0);
        formParameters.setMaxInterestRate("");
        formParameters.setMinInterestRate("");
        formParameters.setDefaultInterestRate("");
        return formParameters;
    }

    private SubmitFormParameters setFormParameters(SubmitFormParameters formParameters)
    {
        formParameters.setOfferingName("name");
        formParameters.setOfferingShortName("qwe");
        formParameters.setProductCategory(formParameters.OTHER);
        formParameters.setStartDateDd("07");
        formParameters.setStartDateMm("02");
        formParameters.setStartDateYy("2008");
        formParameters.setApplicableFor(formParameters.CLIENTS);
        formParameters.setStatus(formParameters.ACTIVE);
        formParameters.setInterestTypes(formParameters.FLAT);
        formParameters.setMaxInterestRate("20");
        formParameters.setMinInterestRate("25");
        formParameters.setDefaultInterestRate("10");
        return formParameters;
    }

    private SubmitFormParameters setCorrectFormParameters(SubmitFormParameters formParameters) {
        formParameters.setStartDateDd("16");
        formParameters.setStartDateMm("03");
        formParameters.setStartDateYy("2009");
        formParameters.setMaxInterestRate("25");
        formParameters.setMinInterestRate("15");
        formParameters.setDefaultInterestRate("20");
        return formParameters;
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-312
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    public void editExistingLoanAndSavingsProduct() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,2,02,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        ViewLoanProductsPage viewLoanProducts = loginAndNavigateToViewLoanProductsPage();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails("MonthlyClientFlatLoan1stOfMonth");
        EditLoanProductPage editLoanProductPage = loanProductDetailsPage.editLoanProduct();
        editLoanProductPage.verifyPage();
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters=cleanFormParameters(formParameters);

        EditLoanProductPreviewPage editLoanProductPreviewPage = editLoanProductPage.submitRequiredDescriptionAndInterestChanges(formParameters);

        String error = selenium.getText("EditLoanProduct.error.message");
        verifyError(error, "Please specify the Product instance name.","Please specify the Short name.","Please select the Product category.","Please specify the Applicable for.",
                "Please select the Interest rate type.","Please select the Status.","Please specify the Min Interest rate.","Please specify the Max Interest rate.","Please select the Status.","Please specify the Min Interest rate.","Please specify the Default Interest rate.");
        //TODO js - missing error msg
        //Assert.assertEquals(error.contains("Please specify the Start date."), true);

        formParameters=setFormParameters(formParameters);
        editLoanProductPreviewPage = editLoanProductPage.submitRequiredDescriptionAndInterestChanges(formParameters);

        error = selenium.getText("EditLoanProduct.error.message");
        //TODO js - missing error msg
        //Assert.assertEquals(error.contains("The Start date cannot be changed because either the product is active or the date specified is invalid."), true);
        Assert.assertEquals(error.contains("Please specify a valid Max Interest rate. Max Interest rate should be greater than or equal to Min Interest rate."), true);
        Assert.assertEquals(error.contains("Please specify valid Default Interest rate. Default Interest rate should be between the Min and Max Interest rate, inclusive of the two."), true);

        formParameters=setCorrectFormParameters(formParameters);

        editLoanProductPreviewPage = editLoanProductPage.submitRequiredDescriptionAndInterestChanges(formParameters);

        loanProductDetailsPage = editLoanProductPreviewPage.submit();
        loanProductDetailsPage.verifyPage();
        loanProductDetailsPage.editLoanProduct();
        editLoanProductPage.verifyPage();
        editLoanProductPage.verifyModifiedLoanProduct(formParameters);

        ///////////////////////SAVINGS////////////////////
        ViewSavingsProductsPage viewSavingsProducts = loginAndNavigateToViewSavingsProductsPage();
        SavingsProductDetailsPage savingsProductDetailsPage = viewSavingsProducts.viewSavingsProductDetails("MandCenterSavings3MoPost");
        EditSavingsProductPage editSavingsProductPage = savingsProductDetailsPage.editSavingsProduct();
        SubmitSavingsFormParameters formSavingsParameters = new SubmitSavingsFormParameters();

        formSavingsParameters=cleanFormSavingsParameters(formSavingsParameters);

        EditSavingsProductPreviewPage editSavingsProductPreviewPage = editSavingsProductPage.submitRequiredDescriptionAndInterestChanges(formSavingsParameters);

        error = selenium.getText("error.messages");
        verifyError(error, "Please specify the Product instance name.", "Please specify the Short name.", "Please select the Product category.","Please specify the Start date. Day must be in range (1-31).",
                "Please specify the Start date. Month must be in range (1-12).", "The Start date can be anything between current date and 1 year from the current date.", "Please select the Applicable for.", "Please select the Type of deposits.",
                "Please specify the Interest rate. Interest must be in range (0-100).", "Please select the Balance used for Interest calculation." ,"Please specify the Time period for Interest calculation.", "Please specify the Frequency of Interest posting to accounts.");
        formSavingsParameters=setFormSavingsParameters(formSavingsParameters);

        editSavingsProductPreviewPage = editSavingsProductPage.submitRequiredDescriptionAndInterestChanges(formSavingsParameters);

        savingsProductDetailsPage = editSavingsProductPreviewPage.submit();
        savingsProductDetailsPage.editSavingsProduct();
        editSavingsProductPage.verifyModifiedSavingsProduct(formSavingsParameters);
    }

    private void verifyError(String error, String... msgs){
        for(String msg : msgs) {
            Assert.assertEquals(error.contains(msg), true);
        }
    }
    private SubmitSavingsFormParameters cleanFormSavingsParameters(SubmitSavingsFormParameters formSavingsParameters){
        formSavingsParameters.setOfferingName("");
        formSavingsParameters.setOfferingShortName("");
        formSavingsParameters.setProductCategory(0);
        formSavingsParameters.setStartDateDd("");
        formSavingsParameters.setStartDateMm("");
        formSavingsParameters.setStartDateYy("");
        formSavingsParameters.setApplicableFor(0);
        formSavingsParameters.setDepositType(0);
        formSavingsParameters.setStatus(0);
        formSavingsParameters.setInterestRate("");
        formSavingsParameters.setBalanceInterest(0);
        formSavingsParameters.setTimePeriodInterest("");
        formSavingsParameters.setFrequencyInterest("");

        return formSavingsParameters;
    }

    private SubmitSavingsFormParameters setFormSavingsParameters(SubmitSavingsFormParameters formSavingsParameters){
        formSavingsParameters.setOfferingName("savingname");
        formSavingsParameters.setOfferingShortName("aaa");
        formSavingsParameters.setProductCategory(formSavingsParameters.OTHER);
        formSavingsParameters.setStartDateDd("8");
        formSavingsParameters.setStartDateMm("9");
        formSavingsParameters.setStartDateYy("2009");
        formSavingsParameters.setApplicableFor(formSavingsParameters.CLIENTS);
        formSavingsParameters.setStatus(formSavingsParameters.ACTIVE);
        formSavingsParameters.setInterestRate("3");
        formSavingsParameters.setFrequencyInterest("3");
        formSavingsParameters.setTimePeriodInterest("1");
        formSavingsParameters.setDepositType(formSavingsParameters.MANDATORY);
        formSavingsParameters.setBalanceInterest(formSavingsParameters.MINIMUM_BALANCE);
        return formSavingsParameters;
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    public void verifyLSIMDisabled() throws Exception {
        applicationDatabaseOperation.updateLSIM(0);
        createNewLoanProductAndNavigateToEditLoanPage().
                verifyVariableInstalmentOptionDisabled();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    public void verifyVariableInstalmentUnchecked() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        createNewLoanProductAndNavigateToEditLoanPage().
                editSubmit().
                verifyVariableInstalmentUnChecked().submit().
                verifyVariableInstalmentOptionUnChecked();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    public void verifyVariableInstalment() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        createNewLoanProductAndNavigateToEditLoanPage();
        setAndValidateInstalmentOption("60", "1", "100");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyVariableInstalmentWithNullValues() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        createNewLoanProductAndNavigateToEditLoanPage();
        setAndValidateInstalmentOption("","1","");
    }

    @Test(enabled=false)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyCashFlow() throws Exception {
        createNewLoanProductAndNavigateToEditLoanPage();
        setAndValidateCashFlow("89.9","45.02", "150.11");
    }

    @Test(enabled=false)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyCashFlowWithNullValue() throws Exception {
        createNewLoanProductAndNavigateToEditLoanPage();
        setAndValidateCashFlow("", "","");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyCashFlowUnchecked() throws Exception {
        createNewLoanProductAndNavigateToEditLoanPage().
                editSubmit().
                verifyCashFlowUncheckedInEditPreview().
                submit().verifyCashFlowUncheckedInEditedProduct();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyCashFlowFields() throws Exception {
        createNewLoanProductAndNavigateToEditLoanPage().
                verifyCashFlowDefaultsInEditProduct();
//                verifyCashFlowFields();
    }



    private void setAndValidateCashFlow(String warningThreshold, String indebetedValue, String repaymentCapacityValue) {
        new EditLoanProductPage(selenium).
                setCashFlowThreshold(warningThreshold).
                editSubmit().
                verifyCashflowThresholdInEditPreview(warningThreshold,indebetedValue,repaymentCapacityValue).
                submit().verifyCashFlowOfEditedLoan(warningThreshold,indebetedValue,repaymentCapacityValue);
    }

    private ViewLoanProductsPage loginAndNavigateToViewLoanProductsPage() {
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        ViewLoanProductsPage viewLoanProducts = adminPage.navigateToViewLoanProducts();
        viewLoanProducts.verifyPage();
        return viewLoanProducts;
    }

    private ViewSavingsProductsPage loginAndNavigateToViewSavingsProductsPage() {
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        ViewSavingsProductsPage viewSavingsProducts = adminPage.navigateToViewSavingsProducts();
        viewSavingsProducts.verifyPage();
        return viewSavingsProducts;
    }


    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher
                .launchMifos()
                .loginSuccessfullyUsingDefaultCredentials()
                .navigateToAdminPage();
    }

    private String createMonthlyLoanProduct() {
        SubmitFormParameters formParameters = FormParametersHelper.getMonthlyLoanProductParameters();
        formParameters.setOfferingShortName(StringUtil.getRandomString(4));
        String loanName = formParameters.getOfferingName();
        loginAndNavigateToAdminPage().
        navigateToDefineLoanProduct().
        fillLoanParameters(formParameters).submitAndGotoNewLoanProductPreviewPage().submit();
        return loanName;
    }

    private void setAndValidateInstalmentOption(String maxGap, String minGap, String minInstalmentAmount) {
        new EditLoanProductPage(selenium).submitVariableInstalmentChange(maxGap, minGap, minInstalmentAmount)
                .verifyVariableInstalmentOption(maxGap, minGap, minInstalmentAmount).
                submit()
                .verifyVariableInstalmentOption(maxGap, minGap, minInstalmentAmount);
    }

    private EditLoanProductPage createNewLoanProductAndNavigateToEditLoanPage() {
        String loanName = createMonthlyLoanProduct();
        return loginAndNavigateToAdminPage().
                navigateToViewLoanProducts().
                viewLoanProductDetails(loanName).
                editLoanProduct();
    }

}

