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
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.client.ClientEditFamilyPage;
import org.mifos.test.acceptance.framework.client.ClientEditFamilyParameters;
import org.mifos.test.acceptance.framework.client.ClientFamilyEditPreviewPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterFamilyDetailsPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientPreviewDataPage;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"client","acceptance","ui"})

public class AddFamilyDetailsTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    CustomPropertiesHelper propertiesHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        propertiesHelper = new CustomPropertiesHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
        propertiesHelper.setAreFamilyDetailsRequired(false);

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void goToPreviewPageAfterEnteringFamilyDetailsPageTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setAreFamilyDetailsRequired(true);
        propertiesHelper.setMaximumNumberOfFamilyMemebers(10);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientsAndAccountsPage.createClientForFamilyInfo( "MyOffice1233171674227","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage.verifyPage("CreateClientFamilyInfo");
        clientFamilyDataPage=clientsAndAccountsPage.createFamily("fname", "lname", "11", "01", "1987", clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        nextPage.verifyPage("CreateClientMfiInfo");
        CreateClientPreviewDataPage clientPreviewDataPage=clientsAndAccountsPage.createClientMFIInformationAndGoToPreviewPage("Joe1233171679953 Guy1233171679953",nextPage);
        CreateClientEnterFamilyDetailsPage editPage=clientPreviewDataPage.edit();
        editPage.verifyPage("CreateClientFamilyInfo");
        propertiesHelper.setAreFamilyDetailsRequired(false);

    }


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyNoLivingStatus() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setAreFamilyDetailsRequired(true);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientsAndAccountsPage.createClientForFamilyInfo("MyOffice1233171674227","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage=clientsAndAccountsPage.createFamilyWithoutLookups(1,49,0,clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        nextPage.verifyPage("CreateClientFamilyInfo");
        propertiesHelper.setAreFamilyDetailsRequired(false);

    }


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyCanAddAndDeleteRow() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setAreFamilyDetailsRequired(true);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientsAndAccountsPage.createClientForFamilyInfo( "MyOffice1233171674227","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage.addRow();
        Assert.assertTrue(selenium.isElementPresent("familyRelationship[1]"), "The element for familyRelationship in the added row was not found");
        clientFamilyDataPage=clientFamilyDataPage.deleteRow();
        Assert.assertFalse(selenium.isElementPresent("familyRelationship[1]"), "The element for familyRelationship in the deleted row was found");
        propertiesHelper.setAreFamilyDetailsRequired(false);

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void goToPreviewPageTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setAreFamilyDetailsRequired(true);
        propertiesHelper.setMaximumNumberOfFamilyMemebers(10);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientsAndAccountsPage.createClientForFamilyInfo( "MyOffice1233171674227","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage=clientsAndAccountsPage.createFamily("fname", "lname", "11", "01", "1987", clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        clientsAndAccountsPage.createClientMFIInformationAndGoToPreviewPage("Joe1233171679953 Guy1233171679953",nextPage);
        Assert.assertEquals(selenium.getText("displayName"), "fname lname");
        propertiesHelper.setAreFamilyDetailsRequired(false);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void goToEditAfterPreviewPageTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setAreFamilyDetailsRequired(true);
        propertiesHelper.setMaximumNumberOfFamilyMemebers(10);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientsAndAccountsPage.createClientForFamilyInfo( "MyOffice1233171674227","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage=clientsAndAccountsPage.createFamily("fname", "lname", "11", "01", "1987", clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        CreateClientPreviewDataPage clientPreviewDataPage=clientsAndAccountsPage.createClientMFIInformationAndGoToPreviewPage("Joe1233171679953 Guy1233171679953",nextPage);
        CreateClientEnterFamilyDetailsPage editPage=clientPreviewDataPage.edit();
        editPage.verifyPage("CreateClientFamilyInfo");
        propertiesHelper.setAreFamilyDetailsRequired(false);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void canGoTosubmitAfterPreviewPageTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setAreFamilyDetailsRequired(true);
        propertiesHelper.setMaximumNumberOfFamilyMemebers(10);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientsAndAccountsPage.createClientForFamilyInfo( "MyOffice1233171674227","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage=clientsAndAccountsPage.createFamily("fname", "lname", "11", "01", "1987", clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        CreateClientPreviewDataPage clientPreviewDataPage=clientsAndAccountsPage.createClientMFIInformationAndGoToPreviewPage("Joe1233171679953 Guy1233171679953",nextPage);
        clientPreviewDataPage.submit();
        selenium.click("client_creationConfirmation.link.viewClientDetailsLink");
        selenium.waitForPageToLoad("30000");
        ClientViewDetailsPage clientDetailsPage=new ClientViewDetailsPage(selenium);
        clientDetailsPage.verifyPage();
        propertiesHelper.setAreFamilyDetailsRequired(false);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void canEditAfterSubmit() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setAreFamilyDetailsRequired(true);
        propertiesHelper.setMaximumNumberOfFamilyMemebers(10);
        ClientsAndAccountsHomepage clientsAndAccountsPage1 = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage1= clientsAndAccountsPage1.createClientForFamilyInfo( "MyOffice1233171674227","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage1.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage=clientsAndAccountsPage1.createFamily("fname", "lname", "11", "01", "1987", clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        CreateClientPreviewDataPage clientPreviewDataPage1=clientsAndAccountsPage1.createClientMFIInformationAndGoToPreviewPage("Joe1233171679953 Guy1233171679953",nextPage);
        clientPreviewDataPage1.submit();
        selenium.click("client_creationConfirmation.link.viewClientDetailsLink");
        selenium.waitForPageToLoad("30000");
        ClientViewDetailsPage clientDetailsPage=new ClientViewDetailsPage(selenium);
        clientDetailsPage.verifyPage();
        ClientEditFamilyPage editFamilyPage=clientDetailsPage.editFamilyInformation();
        ClientEditFamilyParameters editParameters=new ClientEditFamilyParameters();
        editParameters.setFirstName("newName");
        editParameters.setRelationship(1);
        editParameters.setLastName("newLastName");
        editParameters.setDateOfBirthDD("11");
        editParameters.setDateOfBirthMM("12");
        editParameters.setDateOfBirthYY("2008");
        editParameters.setGender(49);
        editParameters.setLivingStatus(622);
        ClientFamilyEditPreviewPage previewEdit=editFamilyPage.submitAndNavigateToClientEditFamilyPreviewPage(editParameters);
        previewEdit.verifyPage();
        clientDetailsPage=previewEdit.submit();
        //Assert.assertTrue(selenium.isTextPresent("newName newLastName"));
        Assert.assertEquals(selenium.getText("displayName"), "newName newLastName");
        propertiesHelper.setAreFamilyDetailsRequired(false);
    }

    @AfterClass
    public void exit() {
        propertiesHelper.setAreFamilyDetailsRequired(false);
    }


}
