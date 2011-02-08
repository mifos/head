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

package org.mifos.test.acceptance.framework.admin;

import com.thoughtworks.selenium.Selenium;

import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage;
import org.mifos.test.acceptance.framework.holiday.ViewHolidaysPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSearchPage;
import org.mifos.test.acceptance.framework.loan.UndoLoanDisbursalSearchPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductConfirmationPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineProductMixPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.ViewLoanProductsPage;
import org.mifos.test.acceptance.framework.loanproduct.multicurrrency.DefineNewDifferentCurrencyLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.multicurrrency.DefineNewDifferentCurrencyLoanProductPage.SubmitMultiCurrencyFormParameters;
import org.mifos.test.acceptance.framework.office.ChooseOfficePage;
import org.mifos.test.acceptance.framework.office.CreateOfficeEnterDataPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewAllQuestionGroupsPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewAllQuestionsPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductPage;
import org.mifos.test.acceptance.framework.savingsproduct.ViewSavingsProductsPage;
import org.mifos.test.acceptance.framework.user.CreateUserParameters;
import org.mifos.test.acceptance.util.StringUtil;
import org.testng.Assert;

/**
 * Encapsulates the GUI based actions that can
 * be done from the Admin page and the page
 * that will be navigated to.
 *
 */
//@SuppressWarnings({"PMD", "PMD.CouplingBetweenObjects", "all"})
@SuppressWarnings("PMD")
public class AdminPage extends MifosPage {
    public static final String PAGE_ID = "admin";

    public AdminPage() {
        super();
    }

    public AdminPage(Selenium selenium) {
        super(selenium);
        verifyPage(PAGE_ID);
    }

    public ViewHolidaysPage navigateToViewHolidaysPage() {
        selenium.click("admin.link.viewHolidays");
        waitForPageToLoad();
        return new ViewHolidaysPage(selenium);
    }

    public ViewFeesPage navigateToViewFeesPage() {
        selenium.click("admin.link.viewFees");
        waitForPageToLoad();
        return new ViewFeesPage(selenium);
    }

    public CreateQuestionPage navigateToCreateQuestionPage() {
        selenium.click("admin.link.defineNewQuestion");
        waitForPageToLoad();
        return new CreateQuestionPage(selenium);
    }

   public ViewReportsPage navigateToViewReportsPage() {
        selenium.click("admin.link.ViewReportsTemplates");
        waitForPageToLoad();
        return new ViewReportsPage(selenium);
    }

    public CreateOfficeEnterDataPage navigateToCreateOfficeEnterDataPage() {
        selenium.click("admin.link.defineNewOffice");
        waitForPageToLoad();
        return new CreateOfficeEnterDataPage(selenium);
    }

    public ChooseOfficePage navigateToCreateUserPage() {
        selenium.click("admin.link.defineNewUsers");
        waitForPageToLoad();
        return new ChooseOfficePage(selenium);
    }

    public ViewOfficesPage navigateToViewOfficesPage() {
        selenium.click("admin.link.viewOffices");
        waitForPageToLoad();
        return new ViewOfficesPage(selenium);
    }

    public ViewProductCategoriesPage navigateToViewProductCategoriesPage() {
        selenium.click("admin.link.viewProductCategories");
        waitForPageToLoad();
        return new ViewProductCategoriesPage(selenium);
    }

    public DefineNewLoanProductPage navigateToDefineLoanProduct() {
        selenium.click("admin.link.defineNewLoanProduct");
        waitForPageToLoad();
        return new DefineNewLoanProductPage(selenium);
    }

    public DefineNewDifferentCurrencyLoanProductPage navigateToDefineDifferentCurrencyLoanProduct() {
        selenium.click("admin.link.defineNewLoanProduct");
        waitForPageToLoad();
        return new DefineNewDifferentCurrencyLoanProductPage(selenium);
    }

    public UndoLoanDisbursalSearchPage navigateToUndoLoanDisbursal() {
        selenium.click("admin.link.reverseLoanDisbursal");
        waitForPageToLoad();
        return new UndoLoanDisbursalSearchPage(selenium);
    }

    public RedoLoanDisbursalSearchPage navigateToRedoLoanDisbursal() {
        selenium.click("admin.link.redoLoanDisbursal");
        waitForPageToLoad();
        return new RedoLoanDisbursalSearchPage(selenium);
    }

    public AdminPage verifyPage() {
        verifyPage(PAGE_ID);
        return this;
    }

    public CreateUserParameters getAdminUserParameters() {
        CreateUserParameters formParameters = new CreateUserParameters();
        formParameters.setFirstName("New");
        formParameters.setLastName("User" + StringUtil.getRandomString(8));
        formParameters.setDateOfBirthDD("21");
        formParameters.setDateOfBirthMM("11");
        formParameters.setDateOfBirthYYYY("1980");
        formParameters.setGender(CreateUserParameters.MALE);

//        Integer preferredSupportedLocale = 1;
//        formParameters.setPreferredLanguage(preferredSupportedLocale);
        formParameters.setUserLevel(CreateUserParameters.LOAN_OFFICER);
        formParameters.setRole("Admin");
        formParameters.setUserName("loanofficer_blore" + StringUtil.getRandomString(3));
        formParameters.setPassword("password");
        formParameters.setPasswordRepeat("password");
        return formParameters;
    }

    public CreateUserParameters getNonAdminUserParameters() {
        CreateUserParameters formParameters = new CreateUserParameters();
        formParameters.setFirstName("NonAdmin");
        formParameters.setLastName("User" + StringUtil.getRandomString(8));
        formParameters.setDateOfBirthDD("04");
        formParameters.setDateOfBirthMM("04");
        formParameters.setDateOfBirthYYYY("1986");
        formParameters.setGender(CreateUserParameters.MALE);
        formParameters.setUserLevel(CreateUserParameters.NON_LOAN_OFFICER);
        formParameters.setUserName("test" + StringUtil.getRandomString(5));
        formParameters.setPassword("tester");
        formParameters.setPasswordRepeat("tester");
        return formParameters;
    }

    public void defineLoanProduct(SubmitFormParameters formParameters) {
        DefineNewLoanProductPage newLoanPage = navigateToDefineLoanProduct();
        newLoanPage.verifyPage();
        newLoanPage.fillLoanParameters(formParameters);
        DefineNewLoanProductPreviewPage previewPage = newLoanPage.submitAndGotoNewLoanProductPreviewPage();
        previewPage.verifyPage();
        DefineNewLoanProductConfirmationPage confirmationPage = previewPage.submit();
        confirmationPage.verifyPage();
    }

    public void defineMultiCurrencyLoanProduct(SubmitMultiCurrencyFormParameters formParameters) {
        DefineNewDifferentCurrencyLoanProductPage newLoanPage =  navigateToDefineDifferentCurrencyLoanProduct();
        newLoanPage.verifyPage();
        newLoanPage.fillLoanParameters(formParameters);
        DefineNewLoanProductPreviewPage previewPage = newLoanPage.submitAndGotoNewLoanProductPreviewPage();
        previewPage.verifyPage();
        DefineNewLoanProductConfirmationPage confirmationPage = previewPage.submit();
        confirmationPage.verifyPage();
    }

    public SystemInfoPage navigateToSystemInfoPage() {
        selenium.click("admin.link.viewSystemInfo");
        waitForPageToLoad();
        return new SystemInfoPage(selenium);
    }

    public ViewAccountingExportsPage navigateToViewAccountingExports() {
        selenium.click("admin.link.viewaccountingexports");
        waitForPageToLoad();
        return new ViewAccountingExportsPage(selenium);
    }

    public ViewLoanProductsPage navigateToViewLoanProducts() {
        selenium.click("admin.link.viewLoanProducts");
        waitForPageToLoad();
        return new ViewLoanProductsPage(selenium);
    }

    public ViewSavingsProductsPage navigateToViewSavingsProducts() {
        selenium.click("admin.link.viewSavingsProducts");
        waitForPageToLoad();
        return new ViewSavingsProductsPage(selenium);
    }

    public ViewAdditionalFieldCategoriesPage navigateToViewAdditionalFields() {
        selenium.click("admin.link.viewAdditionalFields");
        waitForPageToLoad();
        return new ViewAdditionalFieldCategoriesPage(selenium);
    }

    public ViewFundsPage navigateToViewFundsPage() {
        selenium.click("admin.link.viewFunds");
        waitForPageToLoad();
        return new ViewFundsPage(selenium);
    }

    public FundCreatePage navigateToFundCreatePage(){
        selenium.click("admin.link.defineNewFund");
        waitForPageToLoad();
        return new FundCreatePage(selenium);
    }

    public DefineNewSavingsProductPage navigateToDefineSavingsProduct() {
        selenium.click("admin.link.defineNewSavingsProduct");
        waitForPageToLoad();
        return new DefineNewSavingsProductPage(selenium);
    }

    public ViewLatenessAndDormancyDefinitionPage navigateToViewLatenessAndDormancyDefinitionPage() {
        selenium.click("admin.link.viewLatenessDormancyDefinition");
        waitForPageToLoad();
        return new ViewLatenessAndDormancyDefinitionPage(selenium);
    }

    public CreateHolidayEntryPage navigateToDefineHolidayPage() {
        selenium.click("admin.link.defineNewHoliday");
        waitForPageToLoad();
        return new CreateHolidayEntryPage(selenium);
    }

    public ViewHolidaysPage navigateToViewHolidays() {
        selenium.click("admin.link.viewHolidays");
        waitForPageToLoad();
        return new ViewHolidaysPage(selenium);
    }

    public ViewReportCategoriesPage navigateToViewReportCategories() {
        selenium.click("admin.link.ViewReportsCategory");
        waitForPageToLoad();
        return new ViewReportCategoriesPage(selenium);
    }

    public DefineLabelsPage navigateToDefineLabelsPage() {
        selenium.click("admin.link.defineLabels");
        waitForPageToLoad();
        return new DefineLabelsPage(selenium);
    }

    public ViewRolesPage navigateToViewRolesPage() {
        selenium.click("admin.link.manageRoles");
        waitForPageToLoad();
        return new ViewRolesPage(selenium);
    }

    public ViewOrganizationSettingsPage navigateToViewOrganizationSettingsPage() {
        selenium.click("admin.link.viewOrganizationSettings");
        waitForPageToLoad();
        return new ViewOrganizationSettingsPage(selenium);
    }

    public ViewOfficeHierarchyPage navigateToViewOfficeHierarchyPage() {
        selenium.click("admin.link.viewOfficeHierarchy");
        waitForPageToLoad();
        return new ViewOfficeHierarchyPage(selenium);
    }

    public AdminPage failNavigationToSystemInfoPage() {
        selenium.click("admin.link.viewSystemInfo");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }

    public AdminPage failNavigationToImportTransactionsPage() {
        selenium.click("admin.link.manageImports");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }

    public ImportTransactionsPage navigateToImportTransactionsPage() {
        selenium.click("admin.link.manageImports");
        waitForPageToLoad();
        return new ImportTransactionsPage(selenium);
    }

    public ViewAllQuestionsPage navigateToViewAllQuestions() {
        selenium.click("admin.link.questions");
        waitForPageToLoad();
        return new ViewAllQuestionsPage(selenium);
    }

    public CreateQuestionGroupPage navigateToCreateQuestionGroupPage() {
        selenium.click("admin.link.defineNewQuestionGroup");
        waitForPageToLoad();
        return new CreateQuestionGroupPage(selenium);
    }

    public ViewAllQuestionGroupsPage navigateToViewAllQuestionGroups() {
        selenium.click("admin.link.questiongroups");
        waitForPageToLoad();
        return new ViewAllQuestionGroupsPage(selenium);
    }

    public FeesCreatePage navigateToFeesCreate() {
        selenium.click("admin.link.defineNewFees");
        waitForPageToLoad();
        return new FeesCreatePage(selenium);
    }

    public DefineProductMixPage navigateToDefineProductMix() {
        selenium.click("admin.link.defineProductsMix");
        waitForPageToLoad();
        return new DefineProductMixPage(selenium);
    }

    public void defineNewFees (FeesCreatePage.SubmitFormParameters formParameters) {
        FeesCreatePage feesCreatePage = navigateToFeesCreate();
        feesCreatePage.verifyPage();
        feesCreatePage.fillFeesParameters(formParameters);
        PreviewFeesCreatePage previewPage = feesCreatePage.submitPageAndGotoPreviewFeesCreatePage();
        previewPage.verifyPage();
        CreateFeesConfirmationPage confirmationPage = previewPage.submit();
        confirmationPage.verifyPage();
        FeeDetailsPage feesDetailPage = confirmationPage.navigateToViewDetailsNowPage();
        feesDetailPage.verifyFeeDetails(formParameters);
    }

    public BatchJobsPage navigateToBatchJobsPage() {
        selenium.click("admin.link.batchjobs");
        waitForPageToLoad();

        return new BatchJobsPage(selenium);
    }

    public DefineNewOfficePage navigateToDefineANewOfficePage() {
        selenium.click("admin.link.defineNewOffice");
        waitForPageToLoad();

        return new DefineNewOfficePage(selenium);
    }

    public ClientsAndAccountsHomepage navigateToClientsAndAccountsUsingHeaderTab() {
        selenium.click("header.link.clientsAndAccounts");
        waitForPageToLoad();
        return new ClientsAndAccountsHomepage(selenium);
    }

    public void verifyError(String error){
        Assert.assertTrue(selenium.isTextPresent(error));
    }

    public DefineAcceptedPaymentTypesPage navigateToDefineAcceptedPaymentType() {
        selenium.click("admin.link.defineAcceptedPaymentType");
        waitForPageToLoad();
        return new DefineAcceptedPaymentTypesPage(selenium);
    }
}
