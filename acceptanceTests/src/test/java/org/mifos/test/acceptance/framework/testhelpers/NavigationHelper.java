/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;

import com.thoughtworks.selenium.Selenium;

public class NavigationHelper {
    private final Selenium selenium;

    public NavigationHelper(Selenium selenium) {
        this.selenium = selenium;
    }
    
    public HomePage navigateToHomePage() {
        LoginPage loginPage = new AppLauncher(selenium).launchMifos().logout();
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
        loanAccountPage.verifyPage();
        
        return loanAccountPage;
    }
    
    public ClientViewDetailsPage navigateToClientViewDetailsPage(String clientName) {
        HomePage homePage = navigateToHomePage();
        SearchResultsPage searchResultsPage = homePage.search(clientName);
        searchResultsPage.verifyPage();
        ClientViewDetailsPage clientDetailsPage = searchResultsPage.navigateToClientViewDetailsPage("link=" + clientName + "*");
        clientDetailsPage.verifyPage();
        
        return clientDetailsPage;
    }
    
    public CenterViewDetailsPage navigateToCenterViewDetailsPage(String centerName) {
        HomePage homePage = navigateToHomePage();
        SearchResultsPage searchResultsPage = homePage.search(centerName);
        searchResultsPage.verifyPage();
        CenterViewDetailsPage centerDetailsPage = searchResultsPage.navigateToCenterViewDetailsPage("link=" + centerName + "*");
        centerDetailsPage.verifyPage();
        
        return centerDetailsPage;
    }
    
    public GroupViewDetailsPage navigateToGroupViewDetailsPage(String groupName) {
        HomePage homePage = navigateToHomePage();
        SearchResultsPage searchResultsPage = homePage.search(groupName);
        searchResultsPage.verifyPage();
        GroupViewDetailsPage groupDetailsPage = searchResultsPage.navigateToGroupViewDetailsPage("link=" + groupName + "*");
        groupDetailsPage.verifyPage();
        
        return groupDetailsPage;
    }
}
