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

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.client.ClientEditMFIPage;
import org.mifos.test.acceptance.framework.client.ClientEditMFIParameters;
import org.mifos.test.acceptance.framework.client.ClientEditMFIPreviewPage;
import org.mifos.test.acceptance.framework.client.ClientSearchResultsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"client","acceptance","ui"})
public class ClientTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    CustomPropertiesHelper propertiesHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        propertiesHelper = new CustomPropertiesHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createClientAndChangeStatusTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, 
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);   
        
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();

        ClientViewDetailsPage clientDetailsPage = clientsAndAccountsPage.createClient("Joe1233171679953 Guy1233171679953", "MyOffice1233171674227");
        
        clientsAndAccountsPage.changeCustomerStatus(clientDetailsPage);
    }
    // implementation of test described in issue 2454
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void searchForClientAndEditDetailsTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, 
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);        

        ClientsAndAccountsHomepage clientsPage = navigationHelper.navigateToClientsAndAccountsPage();
        ClientSearchResultsPage searchResultsPage = clientsPage.searchForClient("Stu1232993852651");
        searchResultsPage.verifyPage();
        ClientViewDetailsPage clientDetailsPage = searchResultsPage.navigateToSearchResult("Stu1232993852651 Client1232993852651: ID 0002-000000003");
        
        ClientEditMFIPage editMFIPage = clientDetailsPage.navigateToEditMFIPage();
        editMFIPage.verifyPage();
        
        ClientEditMFIParameters params = new ClientEditMFIParameters();
        params.setExternalId("extID123");
        params.setTrainedDateDD("15");
        params.setTrainedDateMM("12");
        params.setTrainedDateYYYY("2008");
        
        
        ClientEditMFIPreviewPage mfiPreviewPage = editMFIPage.submitAndNavigateToClientEditMFIPreviewPage(params);
        mfiPreviewPage.verifyPage();
        clientDetailsPage = mfiPreviewPage.submit();
        assertTextFoundOnPage("extID123");
        assertTextFoundOnPage("15/12/2008");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups = {"smoke"})
    public void createClientWithCorrectAgeTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, 
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);   
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientsAndAccountsPage.createClient("Joe1233171679953 Guy1233171679953", "MyOffice1233171674227","11","12","1987");
        CreateClientEnterMfiDataPage nextPage=clientPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        nextPage.verifyPage("CreateClientMfiInfo");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createClientWithMoreThanMaximumAgeTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, 
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientsAndAccountsPage.createClient("Joe1233171679953 Guy1233171679953", "MyOffice1233171674227","11","12","1940");
        CreateClientEnterPersonalDataPage nextPage=clientPersonalDataPage.dontLoadNext();
        nextPage.verifyPage("CreateClientPersonalInfo");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createClientWithLessThanMinimumAgeTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, 
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientsAndAccountsPage.createClient("Joe1233171679953 Guy1233171679953", "MyOffice1233171674227","11","12","1995");
        CreateClientEnterPersonalDataPage nextPage=clientPersonalDataPage.dontLoadNext();
        nextPage.verifyPage("CreateClientPersonalInfo");
    }
    
 
}
