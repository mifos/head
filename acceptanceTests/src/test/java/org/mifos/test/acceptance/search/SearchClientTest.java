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

package org.mifos.test.acceptance.search;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"search","acceptance","ui"})

public class SearchClientTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    private AppLauncher appLauncher;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void searchMultipleClientsTest() throws Exception {
        int numResults = searchForClient("Stu123");
        Assert.assertEquals( numResults, 38 );
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void searchSingleClientTest() throws Exception {
        int numResults = searchForClient("Stu1232993852651 Client1232993852651");
        Assert.assertEquals( numResults, 1 );
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void searchNonExistentClientTest() throws Exception {
        int numResults = searchForClient("zzz");
        Assert.assertEquals( numResults, 0 );

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private int searchForClient(String clientName) throws Exception {
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        SearchResultsPage searchResultsPage = homePage.search(clientName);
        searchResultsPage.verifyPage();

        int numResults = searchResultsPage.countSearchResults();

        return numResults;
     }
}
