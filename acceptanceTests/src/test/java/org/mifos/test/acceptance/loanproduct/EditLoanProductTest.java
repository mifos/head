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


import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.loanproduct.ViewLoanProductsPage;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
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
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml.zip", dataSource, selenium);
        ViewLoanProductsPage viewLoanProducts = loginAndNavigateToViewLoanProductsPage();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails("FlatInterestLoanProduct1");
        loanProductDetailsPage.verifyPage();

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void editExistingLoanProduct() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml.zip", dataSource, selenium);
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

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void verifyVariableInstalment() throws Exception {
        applicationDatabaseOperation.updateLSIM(0);
        String loanName = createMonthlyLoanProduct();
        AdminPage adminPage = loginAndNavigateToAdminPage();
        ViewLoanProductsPage viewLoanProductsPage = adminPage.navigateToViewLoanProducts();
        LoanProductDetailsPage detailsPage = viewLoanProductsPage.viewLoanProductDetails(loanName);
        EditLoanProductPage editProductPage = detailsPage.editLoanProduct();
        editProductPage.verifyVariableInstalmentOptionDisabled();

        //validating variable instalment unchecked
        applicationDatabaseOperation.updateLSIM(1);
        editProductPage.refresh();
        EditLoanProductPreviewPage editProductPreviewPage = editProductPage.submitAndGotoProductPreviewPage();
        editProductPreviewPage.verifyVariableInstalmentUnChecked();
        editProductPreviewPage.submit();
        detailsPage.verifyVariableInstalmentOptionUnChecked();

        //validating variable instalment checked
        loginAndNavigateToViewLoanProductsPage();
        viewLoanProductsPage.viewLoanProductDetails(loanName);
        detailsPage.editLoanProduct();
        editProductPage.verifyVariableInstalmentOptionDefaults();
        editProductPage.verifyVariableInstalmentOptionFields();
        setAndValidateInstalmentOption(editProductPage, "60", "1", "100");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void verifyVariableInstalmentWithNullValues() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        String loanName = createMonthlyLoanProduct();
        ViewLoanProductsPage viewLoanProductsPage = loginAndNavigateToViewLoanProductsPage();
        LoanProductDetailsPage detailsPage = viewLoanProductsPage.viewLoanProductDetails(loanName);
        EditLoanProductPage editProductPage = detailsPage.editLoanProduct();
        setAndValidateInstalmentOption(editProductPage,"","1","");
    }

    private String createMonthlyLoanProduct() {
        SubmitFormParameters formParameters = FormParametersHelper.getMonthlyLoanProductParameters();
        String loanName = formParameters.getOfferingName();
        AdminPage adminPage = loginAndNavigateToAdminPage();
        DefineNewLoanProductPage productPage = adminPage.navigateToDefineLoanProduct();
        productPage.fillLoanParameters(formParameters);
        productPage.submitAndGotoNewLoanProductPreviewPage().submit();
        return loanName;
    }

    private void setAndValidateInstalmentOption(EditLoanProductPage editProductPage, String maxGap, String minGap, String minInstalmentAmount) {
        EditLoanProductPreviewPage editProductPreviewPage = editProductPage.submitVariableInstalmentChange(maxGap, minGap, minInstalmentAmount);
        editProductPreviewPage.verifyVariableInstalmentOption(maxGap, minGap, minInstalmentAmount);
        LoanProductDetailsPage detailsPage = editProductPreviewPage.submit();
        detailsPage.verifyVariableInstalmentOption(maxGap, minGap, minInstalmentAmount);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void verifyLSIMDisabled() throws Exception {
        applicationDatabaseOperation.updateLSIM(0);
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        DefineNewLoanProductPage defineLoanProductPage = adminPage.navigateToDefineLoanProduct();
        defineLoanProductPage.verifyVariableInstalmentNotAvailable();
    }

}

