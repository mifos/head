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

package org.mifos.test.acceptance.client;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.client.ClientNotesPage;
import org.mifos.test.acceptance.framework.client.ClientSearchResultsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = { "client", "acceptance", "ui"})
public class AddNoteTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private HomePage homePage;

    private static final String START_DATA_SET = "acceptance_small_003_dbunit.xml";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);

        selenium.open("j_spring_security_logout");
        selenium.waitForPageToLoad("3000");
        selenium.open("login.ftl");
        selenium.waitForPageToLoad("3000");
        LoginPage loginPage = new LoginPage(selenium);
        homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
    }

    @AfterMethod
    public void logOut() {
//        selenium.open("j_spring_security_logout");
//        selenium.waitForPageToLoad("3000");
//        selenium.open("login.ftl");
//        homePage = new HomePage(selenium);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void addNoteToClient() throws Exception {

        ClientsAndAccountsHomepage clientAndAccountPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        ClientSearchResultsPage clientSearchPage = clientAndAccountPage.searchForClient("%");

        ClientViewDetailsPage details = clientSearchPage.navigateToSearchResult("Stu1232993852651 Client1232993852651: ID 0002-000000003");
        ClientNotesPage notes = details.navigateToNotesPage();

        String testNote = "Testing new note entry";
        details = notes.addNotePreviewAndSubmit(testNote);

        details.verifyTextOnPage(testNote);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void addNoteToClientAndVerifyOnNotesDetailsPage() throws Exception {

        ClientsAndAccountsHomepage clientAndAccountPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        ClientSearchResultsPage clientSearchPage = clientAndAccountPage.searchForClient("%");

        ClientViewDetailsPage details = clientSearchPage
                .navigateToSearchResult("Stu1232993852651 Client1232993852651: ID 0002-000000003");
        ClientNotesPage notes = details.navigateToNotesPage();

        String testNote = "Testing new note entry";
        details = notes.addNotePreviewAndSubmit(testNote);

        details.verifyTextOnPage(testNote);

        notes = details.navigateToAllNotesPage();
        notes.verifyTextOnPage(testNote);
    }
}