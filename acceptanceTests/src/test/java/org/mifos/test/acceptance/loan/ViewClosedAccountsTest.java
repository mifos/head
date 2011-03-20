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

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.ClosedAccountsPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"loan","acceptance","ui", "no_db_unit"})
public class ViewClosedAccountsTest extends UiTestCaseBase {

    private AppLauncher appLauncher;


    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        new InitializeApplicationRemoteTestingService().reinitializeApplication(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void navigateToCenterDetailsFromClosedAccounts() throws Exception {
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();

        SearchResultsPage searchResultsPage = homePage.search("WeeklyMeetingCenter");
        searchResultsPage.verifyPage();

        CenterViewDetailsPage centerViewDetailsPage = searchResultsPage.navigateToCenterViewDetailsPage("link=WeeklyMeetingCenter*");
        centerViewDetailsPage.verifyPage();

        ClosedAccountsPage closedAccountsPage = centerViewDetailsPage.navigateToClosedAccountsPage();
        closedAccountsPage.verifyPage();

        CenterViewDetailsPage returnedCenterViewDetailsPage = closedAccountsPage.returnToCenterViewDetailsPage();
        returnedCenterViewDetailsPage.verifyPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void navigateToGroupDetailsFromClosedAccounts() throws Exception {
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();

        SearchResultsPage searchResultsPage = homePage.search("groupWithoutLoan ");
        searchResultsPage.verifyPage();

        GroupViewDetailsPage groupViewDetailsPage = searchResultsPage.navigateToGroupViewDetailsPage("link=groupWithoutLoan*");

        ClosedAccountsPage closedAccountsPage = groupViewDetailsPage.navigateToClosedAccountsPage();
        closedAccountsPage.verifyPage();

        closedAccountsPage.returnToGroupViewDetailsPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void navigateToClientDetailsFromClosedAccounts() throws Exception {
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();

        SearchResultsPage searchResultsPage = homePage.search("Client1233266063395");
        searchResultsPage.verifyPage();

        ClientViewDetailsPage clientViewDetailsPage = searchResultsPage.navigateToClientViewDetailsPage("link=Stu*");

        ClosedAccountsPage closedAccountsPage = clientViewDetailsPage.navigateToClosedAccountsPage();
        closedAccountsPage.verifyPage();

        closedAccountsPage.returnToClientViewDetailsPage();
    }
}
