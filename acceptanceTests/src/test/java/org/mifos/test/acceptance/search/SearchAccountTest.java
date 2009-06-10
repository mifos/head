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

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
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
@Test(sequential = true, groups = {"smoke","search","acceptance"})
public class SearchAccountTest extends SearchTestBase {
    /**
     * Account number to use in search
     */
    private static final String ACCT_SEARCH_STRING = "000100000000066";

    /**
     * Expected text on search results page
     */
    private static final String EXPECTED_ACCT_NUMBER = "Loan: Account # " + ACCT_SEARCH_STRING;

    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    
    
    private static String dataFileName = "acceptance_small_003_dbunit.xml.zip";
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
    }
    
    /**
     * Enters an account number ({@link #ACCT_SEARCH_STRING}) in the search box
     * and verifies that the expected text with that account number ({@link #EXPECTED_ACCT_NUMBER})
     * is present on the results page
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void searchAccountTest()  throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, 
                dataFileName,
                dataSource, selenium);    
        
        SearchResultsPage page = searchFor( appLauncher, ACCT_SEARCH_STRING);
        int count = page.countSearchResults();
        //Check that only one row for this account number is returned
        Assert.assertEquals( count, 1 );
        
        //Check the displayed text
        Assert.assertTrue(selenium.isTextPresent(EXPECTED_ACCT_NUMBER));
    }
}
