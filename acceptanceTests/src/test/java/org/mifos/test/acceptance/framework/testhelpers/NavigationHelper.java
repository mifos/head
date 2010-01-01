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

package org.mifos.test.acceptance.framework.testhelpers;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.center.CreateCenterChooseOfficePage;
import org.mifos.test.acceptance.framework.center.CreateCenterEnterDataPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.group.GroupSearchPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.loanproduct.ViewLoanProductsPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.office.ChooseOfficePage;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;

import com.thoughtworks.selenium.Selenium;

public class NavigationHelper {
    private final Selenium selenium;

    public NavigationHelper(Selenium selenium) {
        this.selenium = selenium;
    }

    public HomePage navigateToHomePage() {
        LoginPage loginPage = new AppLauncher(selenium).launchMifos();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        return homePage;
    }

    public AdminPage navigateToAdminPage() {
        HomePage homePage = navigateToHomePage();
        AdminPage adminPage = homePage.navigateToAdminPage();
        adminPage.verifyPage();

        return adminPage;
    }

    public LoanAccountPage navigateToLoanAccountPage(String loanAccountID) {
        HomePage homePage = navigateToHomePage();
        SearchResultsPage searchResultsPage = homePage.search(loanAccountID);
        searchResultsPage.verifyPage();
        LoanAccountPage loanAccountPage = searchResultsPage.navigateToLoanAccountDetailPage(loanAccountID);

        return loanAccountPage;
    }

    public SavingsAccountDetailPage navigateToSavingsAccountDetailPage(String savingsAccountID) {
        HomePage homePage = navigateToHomePage();
        SearchResultsPage searchResultsPage = homePage.search(savingsAccountID);
        searchResultsPage.verifyPage();
        SavingsAccountDetailPage savingsAccountDetailPage = searchResultsPage.navigateToSavingsAccountDetailPage(savingsAccountID);
        savingsAccountDetailPage.verifyPage();

        return savingsAccountDetailPage;
    }
    public ClientViewDetailsPage navigateToClientViewDetailsPage(String clientName) {
        HomePage homePage = navigateToHomePage();
        SearchResultsPage searchResultsPage = homePage.search(clientName);
        searchResultsPage.verifyPage();

        return searchResultsPage.navigateToClientViewDetailsPage("link=*" + clientName + "*");
    }

    public CenterViewDetailsPage navigateToCenterViewDetailsPage(String centerName) {
        HomePage homePage = navigateToHomePage();
        SearchResultsPage searchResultsPage = homePage.search(centerName);
        searchResultsPage.verifyPage();
        CenterViewDetailsPage centerDetailsPage = searchResultsPage.navigateToCenterViewDetailsPage("link=" + centerName + "*");
        centerDetailsPage.verifyPage();

        return centerDetailsPage;
    }

    public LoanProductDetailsPage navigateToLoanProductDetailsPage(String loanProduct)
    {
        ViewLoanProductsPage loanProductsPage = navigateToLoanProductsPage();
        LoanProductDetailsPage loanProductDetailsPage = loanProductsPage.viewLoanProductDetails(loanProduct);
        loanProductDetailsPage.verifyPage();

        return loanProductDetailsPage;
    }

    public GroupViewDetailsPage navigateToGroupViewDetailsPage(String groupName) {
        HomePage homePage = navigateToHomePage();
        SearchResultsPage searchResultsPage = homePage.search(groupName);
        searchResultsPage.verifyPage();
        GroupViewDetailsPage groupDetailsPage = searchResultsPage.navigateToGroupViewDetailsPage("link=" + groupName + "*");

        return groupDetailsPage;
    }

    public ClientsAndAccountsHomepage navigateToClientsAndAccountsPage() {
        HomePage homePage = navigateToHomePage();

        return homePage.navigateToClientsAndAccountsUsingHeaderTab();
    }

    public ChooseOfficePage navigateToCreateUserPage() {
        AdminPage adminPage = navigateToAdminPage();
        ChooseOfficePage createUserPage = adminPage.navigateToCreateUserPage();
        createUserPage.verifyPage();

        return createUserPage;
    }

    public ViewLoanProductsPage navigateToLoanProductsPage() {
        AdminPage adminPage = navigateToAdminPage();
        ViewLoanProductsPage loanProductsPage = adminPage.navigateToViewLoanProducts();
        loanProductsPage.verifyPage();
        return loanProductsPage;
    }

    public DefineNewLoanProductPage navigateToDefineNewLoanProductPage() {
        AdminPage adminPage = navigateToAdminPage();
        DefineNewLoanProductPage newLoanPage = adminPage.navigateToDefineLoanProduct();
        newLoanPage.verifyPage();
        return newLoanPage;
    }

    public CreateCenterEnterDataPage navigateToCreateCenterEnterDataPage(String officeName){
        ClientsAndAccountsHomepage clientsAccountsPage = navigateToClientsAndAccountsPage();
        CreateCenterChooseOfficePage chooseOfficePage = clientsAccountsPage.navigateToCreateNewCenterPage();
        return chooseOfficePage.selectOffice(officeName);
    }

    public CreateClientEnterMfiDataPage navigateToCreateClientEnterMfiDataPage(String officeName) {
        ClientsAndAccountsHomepage clientsAccountsPage = navigateToClientsAndAccountsPage();
        GroupSearchPage groupSearchPage = clientsAccountsPage.navigateToCreateNewClientPage();
        org.mifos.test.acceptance.framework.client.ChooseOfficePage chooseOfficePage = groupSearchPage.navigateToCreateClientWithoutGroupPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage = chooseOfficePage.chooseOffice(officeName);
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = FormParametersHelper.getClientEnterPersonalDataPageFormParameters();
        clientPersonalDataPage=clientPersonalDataPage.create(formParameters);
        return clientPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage();
    }
}
