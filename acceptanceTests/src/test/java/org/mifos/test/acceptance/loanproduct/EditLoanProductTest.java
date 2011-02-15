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


import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.loanproduct.ViewLoanProductsPage;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"loanproduct", "acceptance", "ui"})
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

