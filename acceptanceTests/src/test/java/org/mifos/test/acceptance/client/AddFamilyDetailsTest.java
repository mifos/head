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

package org.mifos.test.acceptance.client;

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
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"client","acceptance","ui","no_db_unit"})

public class AddFamilyDetailsTest extends UiTestCaseBase {

    private ClientTestHelper clientTestHelper;
    CustomPropertiesHelper propertiesHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        propertiesHelper = new CustomPropertiesHelper(selenium);
        clientTestHelper = new ClientTestHelper(selenium);
        propertiesHelper.setAreFamilyDetailsRequired(true);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
        propertiesHelper.setAreFamilyDetailsRequired(false);

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void goToPreviewPageAfterEnteringFamilyDetailsPageTest() throws Exception {
        propertiesHelper.setMaximumNumberOfFamilyMemebers(10);
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientTestHelper.createClientForFamilyInfo( "MyOfficeDHMFT","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage.verifyPage("CreateClientFamilyInfo");
        clientFamilyDataPage=clientTestHelper.createFamily("fname", "lname", "11", "01", "1987", clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        nextPage.verifyPage("CreateClientMfiInfo");
        CreateClientPreviewDataPage clientPreviewDataPage=clientTestHelper.createClientMFIInformationAndGoToPreviewPage("loan officer",nextPage);
        CreateClientEnterFamilyDetailsPage editPage=clientPreviewDataPage.edit();
        editPage.verifyPage("CreateClientFamilyInfo");

    }


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyNoLivingStatus() throws Exception {
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientTestHelper.createClientForFamilyInfo("MyOfficeDHMFT","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage=clientTestHelper.createFamilyWithoutLookups(1,49,0,clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        nextPage.verifyPage("CreateClientFamilyInfo");

    }


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyCanAddAndDeleteRow() throws Exception {
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientTestHelper.createClientForFamilyInfo( "MyOfficeDHMFT","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage.addRow();
        Assert.assertTrue(selenium.isElementPresent("familyRelationship[1]"), "The element for familyRelationship in the added row was not found");
        clientFamilyDataPage=clientFamilyDataPage.deleteRow();
        Assert.assertFalse(selenium.isElementPresent("familyRelationship[1]"), "The element for familyRelationship in the deleted row was found");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void goToPreviewPageTest() throws Exception {
        propertiesHelper.setMaximumNumberOfFamilyMemebers(10);
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientTestHelper.createClientForFamilyInfo( "MyOfficeDHMFT","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage=clientTestHelper.createFamily("fname", "lname", "11", "01", "1987", clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        clientTestHelper.createClientMFIInformationAndGoToPreviewPage("loan officer",nextPage);
        Assert.assertEquals(selenium.getText("displayName"), "fname lname");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void goToEditAfterPreviewPageTest() throws Exception {
        propertiesHelper.setMaximumNumberOfFamilyMemebers(10);
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientTestHelper.createClientForFamilyInfo( "MyOfficeDHMFT","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage=clientTestHelper.createFamily("fname", "lname", "11", "01", "1987", clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        CreateClientPreviewDataPage clientPreviewDataPage=clientTestHelper.createClientMFIInformationAndGoToPreviewPage("loan officer",nextPage);
        CreateClientEnterFamilyDetailsPage editPage=clientPreviewDataPage.edit();
        editPage.verifyPage("CreateClientFamilyInfo");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void canGoTosubmitAfterPreviewPageTest() throws Exception {
        propertiesHelper.setMaximumNumberOfFamilyMemebers(10);
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientTestHelper.createClientForFamilyInfo( "MyOfficeDHMFT","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage=clientTestHelper.createFamily("fname", "lname", "11", "01", "1987", clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        CreateClientPreviewDataPage clientPreviewDataPage=clientTestHelper.createClientMFIInformationAndGoToPreviewPage("loan officer",nextPage);
        clientPreviewDataPage.submit();
        selenium.click("client_creationConfirmation.link.viewClientDetailsLink");
        selenium.waitForPageToLoad("30000");
        new ClientViewDetailsPage(selenium);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void canEditAfterSubmit() throws Exception {
        propertiesHelper.setMaximumNumberOfFamilyMemebers(10);
        CreateClientEnterPersonalDataPage clientPersonalDataPage1= clientTestHelper.createClientForFamilyInfo( "MyOfficeDHMFT","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage1.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage=clientTestHelper.createFamily("fname", "lname", "11", "01", "1987", clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        CreateClientPreviewDataPage clientPreviewDataPage1=clientTestHelper.createClientMFIInformationAndGoToPreviewPage("loan officer",nextPage);
        clientPreviewDataPage1.submit();
        selenium.click("client_creationConfirmation.link.viewClientDetailsLink");
        selenium.waitForPageToLoad("30000");
        ClientViewDetailsPage clientDetailsPage=new ClientViewDetailsPage(selenium);
        ClientEditFamilyPage editFamilyPage=clientDetailsPage.editFamilyInformation();
        ClientEditFamilyParameters editParameters=new ClientEditFamilyParameters();
        editParameters.setFirstName("newName");
        editParameters.setRelationship(1);
        editParameters.setLastName("newLastName");
        editParameters.setDateOfBirthDD("11");
        editParameters.setDateOfBirthMM("12");
        editParameters.setDateOfBirthYY("2008");
        editParameters.setGender(49);
        editParameters.setLivingStatus(620);
        ClientFamilyEditPreviewPage previewEdit=editFamilyPage.submitAndNavigateToClientEditFamilyPreviewPage(editParameters);
        previewEdit.verifyPage();
        clientDetailsPage=previewEdit.submit();
        //Assert.assertTrue(selenium.isTextPresent("newName newLastName"));
        Assert.assertEquals(selenium.getText("displayName"), "newName newLastName");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void addFamilyWithAllNamesTest() throws Exception {
        propertiesHelper.setMaximumNumberOfFamilyMemebers(10);
        CreateClientEnterPersonalDataPage clientPersonalDataPage= clientTestHelper.createClientForFamilyInfo( "MyOfficeDHMFT","11","12","1988");
        CreateClientEnterFamilyDetailsPage clientFamilyDataPage=clientPersonalDataPage.submitAndGotoCreateClientEnterFamilyDetailsPage();
        clientFamilyDataPage=clientTestHelper.createFamilyWithAllName("fname", "lname","mname","slname", "11", "01", "1987", clientFamilyDataPage);
        CreateClientEnterMfiDataPage nextPage=clientFamilyDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        CreateClientPreviewDataPage clientPreviewDataPage=clientTestHelper.createClientMFIInformationAndGoToPreviewPage("loan officer",nextPage);
        CreateClientEnterFamilyDetailsPage editPage=clientPreviewDataPage.edit();
        editPage.verifyPage("CreateClientFamilyInfo");
    }
}
