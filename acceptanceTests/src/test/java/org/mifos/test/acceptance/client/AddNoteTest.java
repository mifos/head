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

package org.mifos.test.acceptance.client;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.DbUnitUtilities;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.client.ClientNotesPage;
import org.mifos.test.acceptance.framework.client.ClientSearchResultsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = { "smoke", "client", "acceptance", "ui" })
public class AddNoteTest extends UiTestCaseBase {

    private AppLauncher appLauncher;
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    private static final String startDataSet = "acceptance_small_003_dbunit.xml.zip";
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void addNoteToClient() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, startDataSet, dataSource, selenium);
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();

        ClientsAndAccountsHomepage clientAndAccountPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        ClientSearchResultsPage clientSearchPage = clientAndAccountPage.searchForClient("%");

        ClientViewDetailsPage details = clientSearchPage
                .navigateToSearchResult("Stu1232993852651 Client1232993852651: ID 0002-000000003");
        ClientNotesPage notes = details.navigateToNotesPage();

        String testNote = "Testing new note entry";
        details = notes.addNotePreviewAndSubmit(testNote);

        details.verifyTextOnPage(testNote);

    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void addNoteToClientAndVerifyOnNotesDetailsPage() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, startDataSet, dataSource, selenium);
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();

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
