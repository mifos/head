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

package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.DefineHiddenMandatoryFieldsPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.group.CreateGroupSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups={"acceptance","ui","admin","no_db_unit"})
public class DefineHiddenMandatoryFieldsTest  extends UiTestCaseBase{
    private NavigationHelper navigationHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    //http://mifosforge.jira.com/browse/MIFOSTEST-219
    public void verifyChangesMadeOnDefineMandatoryHiddenFileds(){
        //When
        AdminPage adminPage = navigationHelper.navigateToAdminPage().navigateToViewRolesPage().navigateToManageRolePage("Admin").disablePermission("9_1").
        verifyPermissionText("9_1", "Can define hidden/mandatory fields").submitAndGotoViewRolesPage().navigateToAdminPage();

        DefineHiddenMandatoryFieldsPage defineHiddenMandatoryFieldsPage = adminPage.navigateToDefineHiddenMandatoryFields();
        //Then
        defineHiddenMandatoryFieldsPage.verifyAccessDenied();
        //When
        adminPage = navigationHelper.navigateToAdminPage().navigateToViewRolesPage().navigateToManageRolePage("Admin").enablePermission("9_1").
        verifyPermissionText("9_1", "Can define hidden/mandatory fields").submitAndGotoViewRolesPage().navigateToAdminPage();
        defineHiddenMandatoryFieldsPage = adminPage.navigateToDefineHiddenMandatoryFields();
        defineHiddenMandatoryFieldsPage.checkHideRelativeSecondLastName();
        defineHiddenMandatoryFieldsPage.checkMandatoryEthnicity();
        adminPage = defineHiddenMandatoryFieldsPage.submit();
        adminPage.navigateToClientsAndAccountsPageUsingHeaderTab().navigateToCreateNewClientPage()
            .navigateToCreateClientWithoutGroupPage().chooseOffice("MyOfficeDHMFT");
        //Then
        Assert.assertTrue(selenium.isTextPresent("*Ethnicity:"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.spouseSecondLastName"));

        // restore original configuration
        defineHiddenMandatoryFieldsPage = navigationHelper.navigateToAdminPage().navigateToDefineHiddenMandatoryFields();
        defineHiddenMandatoryFieldsPage.uncheckHideRelativeSecondLastName();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryEthnicity();
        defineHiddenMandatoryFieldsPage.submit();
    }
    
    /**
     * Verify Hidden Fields
     * http://mifosforge.jira.com/browse/MIFOSTEST-1182
     *
     * @throws Exception
     */
    @Test(enabled=true)
    @SuppressWarnings("PMD")
    public void verifyHiddenFields(){
        //When
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineHiddenMandatoryFieldsPage defineHiddenMandatoryFieldsPage = adminPage.navigateToDefineHiddenMandatoryFields();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryPovertyStatus();
        defineHiddenMandatoryFieldsPage.checkHideExternalId();
        defineHiddenMandatoryFieldsPage.checkHideEthnicity();
        defineHiddenMandatoryFieldsPage.checkHideCitizenShip();
        defineHiddenMandatoryFieldsPage.checkHideHandicapped();
        defineHiddenMandatoryFieldsPage.checkHideEducationLevel();
        defineHiddenMandatoryFieldsPage.checkHidePhoto();
        defineHiddenMandatoryFieldsPage.checkHideAssignClientPostions();
        defineHiddenMandatoryFieldsPage.checkHideAddress2();
        defineHiddenMandatoryFieldsPage.checkHideAddress3();
        defineHiddenMandatoryFieldsPage.checkHideCity();
        defineHiddenMandatoryFieldsPage.checkHideState();
        defineHiddenMandatoryFieldsPage.checkHideCountry();
        defineHiddenMandatoryFieldsPage.checkHidePostalCode();
        defineHiddenMandatoryFieldsPage.checkHideReceiptIdDate();
        defineHiddenMandatoryFieldsPage.checkHideCollateralTypeNotes();
        defineHiddenMandatoryFieldsPage.checkHideMiddleName();
        defineHiddenMandatoryFieldsPage.checkHideSecondLastName();
        defineHiddenMandatoryFieldsPage.checkHideGovtId();
        defineHiddenMandatoryFieldsPage.checkHideRelativeMiddleName();
        defineHiddenMandatoryFieldsPage.checkHideRelativeSecondLastName();
        defineHiddenMandatoryFieldsPage.checkHidePhone();
        defineHiddenMandatoryFieldsPage.checkHideTrained();
        defineHiddenMandatoryFieldsPage.checkHideBusinessWorkActivities();
        defineHiddenMandatoryFieldsPage.checkHidePovertyStatus();
        defineHiddenMandatoryFieldsPage.checkHideGroupTrained();

        adminPage = defineHiddenMandatoryFieldsPage.submit();
        CreateClientEnterPersonalDataPage createClientEnterPersonalDataPage = adminPage.navigateToClientsAndAccountsPageUsingHeaderTab().navigateToCreateNewClientPage()
            .navigateToCreateClientWithoutGroupPage().chooseOffice("MyOfficeDHMFT");
        //Then
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.spouseSecondLastName"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.middleName"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.secondLastName"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.governmentId"));
        Assert.assertFalse(selenium.isElementPresent("clientDetailView.citizenship"));
        Assert.assertFalse(selenium.isElementPresent("clientDetailView.ethinicity"));
        Assert.assertFalse(selenium.isElementPresent("clientDetailView.educationLevel"));
        Assert.assertFalse(selenium.isElementPresent("clientDetailView.businessActivities"));
        Assert.assertFalse(selenium.isElementPresent("clientDetailView.povertyStatus"));
        Assert.assertFalse(selenium.isElementPresent("clientDetailView.handicapped"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.spouseMiddleName"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.address2"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.address3"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.city"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.state"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.country"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.postalCode"));
        Assert.assertFalse(selenium.isElementPresent("create_ClientPersonalInfo.input.telephone"));
        
        CreateClientEnterPersonalDataPage.SubmitFormParameters params = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        params.setSalutation(47); 
        params.setFirstName("Client");
        params.setLastName("Test");
        params.setDateOfBirthDD("11");
        params.setDateOfBirthMM("02");
        params.setDateOfBirthYYYY("1981");
        params.setGender(49);
        
        createClientEnterPersonalDataPage.createWithoutSpouse(params);
        createClientEnterPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        Assert.assertFalse(selenium.isElementPresent("trained"));
        Assert.assertFalse(selenium.isElementPresent("trainedDateDD"));
        
        ClientsAndAccountsHomepage clientsAndAccountsHomepage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsHomepage.navigateToCreateLoanAccountUsingLeftMenu();
        CreateLoanAccountSearchParameters params2 = new CreateLoanAccountSearchParameters();
        params2.setSearchString("ClientWithLoan");
        params2.setLoanProduct("ClientEmergencyLoan");
        createLoanAccountSearchPage.searchAndNavigateToCreateLoanAccountPage(params2);
        Assert.assertFalse(selenium.isElementPresent("collateralTypeId"));
        Assert.assertFalse(selenium.isElementPresent("collateralNotes"));
        Assert.assertFalse(selenium.isElementPresent("externalId"));
        
        navigationHelper.navigateToClientsAndAccountsPage();
        CreateGroupSearchPage createGroupSearchPage = clientsAndAccountsHomepage.navigateToCreateNewGroupPage();
        createGroupSearchPage.searchAndNavigateToCreateGroupPage("Default Center");
        Assert.assertFalse(selenium.isElementPresent("trained"));
        Assert.assertFalse(selenium.isElementPresent("trainedDateDD"));
        
        // restore original configuration
        defineHiddenMandatoryFieldsPage = navigationHelper.navigateToAdminPage().navigateToDefineHiddenMandatoryFields();
        defineHiddenMandatoryFieldsPage.uncheckHideExternalId();
        defineHiddenMandatoryFieldsPage.uncheckHideEthnicity();
        defineHiddenMandatoryFieldsPage.uncheckHideCitizenShip();
        defineHiddenMandatoryFieldsPage.uncheckHideHandicapped();
        defineHiddenMandatoryFieldsPage.uncheckHideEducationLevel();
        defineHiddenMandatoryFieldsPage.uncheckHidePhoto();
        defineHiddenMandatoryFieldsPage.uncheckHideAssignClientPostions();
        defineHiddenMandatoryFieldsPage.uncheckHideAddress2();
        defineHiddenMandatoryFieldsPage.uncheckHideAddress3();
        defineHiddenMandatoryFieldsPage.uncheckHideCity();
        defineHiddenMandatoryFieldsPage.uncheckHideState();
        defineHiddenMandatoryFieldsPage.uncheckHideCountry();
        defineHiddenMandatoryFieldsPage.uncheckHidePostalCode();
        defineHiddenMandatoryFieldsPage.uncheckHideReceiptIdDate();
        defineHiddenMandatoryFieldsPage.uncheckHideCollateralTypeNotes();
        defineHiddenMandatoryFieldsPage.uncheckHideMiddleName();
        defineHiddenMandatoryFieldsPage.uncheckHideSecondLastName();
        defineHiddenMandatoryFieldsPage.uncheckHideGovtId();
        defineHiddenMandatoryFieldsPage.uncheckHidePovertyStatus();
        defineHiddenMandatoryFieldsPage.uncheckHideRelativeMiddleName();
        defineHiddenMandatoryFieldsPage.uncheckHideRelativeSecondLastName();
        defineHiddenMandatoryFieldsPage.uncheckHidePhone();
        defineHiddenMandatoryFieldsPage.uncheckHideTrained();
        defineHiddenMandatoryFieldsPage.uncheckHideBusinessWorkActivities();
        defineHiddenMandatoryFieldsPage.uncheckHideGroupTrained();
        defineHiddenMandatoryFieldsPage.checkMandatoryPovertyStatus();
        defineHiddenMandatoryFieldsPage.submit();
    }
    
    /**
     * Verify Mandatory Fields
     * http://mifosforge.jira.com/browse/MIFOSTEST-1181
     *
     * @throws Exception
     */
    @Test(enabled=true)
    @SuppressWarnings("PMD")
    public void verifyMandatoryFields(){
        //When
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineHiddenMandatoryFieldsPage defineHiddenMandatoryFieldsPage = adminPage.navigateToDefineHiddenMandatoryFields();
        defineHiddenMandatoryFieldsPage.checkMandatoryExternalId();
        defineHiddenMandatoryFieldsPage.checkMandatoryEthnicity();
        defineHiddenMandatoryFieldsPage.checkMandatoryCitizenShip();
        defineHiddenMandatoryFieldsPage.checkMandatoryHandicapped();
        defineHiddenMandatoryFieldsPage.checkMandatoryEducationLevel();
        defineHiddenMandatoryFieldsPage.checkMandatoryPhoto();
        defineHiddenMandatoryFieldsPage.checkMandatoryAddress1();
        defineHiddenMandatoryFieldsPage.checkMandatoryMiddleName();
        defineHiddenMandatoryFieldsPage.checkMandatorySecondLastName();
        defineHiddenMandatoryFieldsPage.checkMandatoryGovtId();
        defineHiddenMandatoryFieldsPage.checkMandatoryMaritalStatus();
        defineHiddenMandatoryFieldsPage.checkMandatoryRelativeSecondLastName();
        defineHiddenMandatoryFieldsPage.checkMandatoryPhone();
        defineHiddenMandatoryFieldsPage.checkMandatoryTrained();
        defineHiddenMandatoryFieldsPage.checkMandatoryTrainedOn();
        defineHiddenMandatoryFieldsPage.checkMandatoryNumberOfChildren();
        defineHiddenMandatoryFieldsPage.checkMandatoryLoanAccountPurpose();
        defineHiddenMandatoryFieldsPage.checkMandatoryLoanSourceOfFund();

        adminPage = defineHiddenMandatoryFieldsPage.submit();
        CreateClientEnterPersonalDataPage createClientEnterPersonalDataPage = adminPage.navigateToClientsAndAccountsPageUsingHeaderTab().navigateToCreateNewClientPage()
            .navigateToCreateClientWithoutGroupPage().chooseOffice("MyOfficeDHMFT");
        //Then
        Assert.assertTrue(selenium.isTextPresent("*Middle Name"));
        Assert.assertTrue(selenium.isTextPresent("*Second Last Name"));
        Assert.assertTrue(selenium.isTextPresent("*Government ID"));
        Assert.assertTrue(selenium.isTextPresent("*Marital Status"));
        Assert.assertTrue(selenium.isTextPresent("*Number Of Children"));
        Assert.assertTrue(selenium.isTextPresent("*Citizenship"));
        Assert.assertTrue(selenium.isTextPresent("*Ethnicity"));
        Assert.assertTrue(selenium.isTextPresent("*Education Level"));
        Assert.assertTrue(selenium.isTextPresent("*Handicapped"));
        Assert.assertTrue(selenium.isTextPresent("*Address 1"));
        Assert.assertTrue(selenium.isTextPresent("*Poverty Status"));
        
        CreateClientEnterPersonalDataPage.SubmitFormParameters params3 = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        params3.setSalutation(47); 
        params3.setFirstName("Client");
        params3.setMiddleName("Middle");
        params3.setLastName("Test");
        params3.setSecondLastName("Second");
        params3.setGovernmentID("12345");
        params3.setMaritalStatus(67);
        params3.setCitizenship(131);
        params3.setEthincity(133);
        params3.setHandicappedDropdown(139);
        params3.setPovertyStatus(42);
        params3.setEducationLevel(135);
        params3.setSpouseLastName("LastName");
        params3.setAddress1("address1");
        params3.setNumberOfChildren("2");
        params3.setDateOfBirthDD("11");
        params3.setDateOfBirthMM("02");
        params3.setDateOfBirthYYYY("1981");
        params3.setGender(49);
        params3.setPhone("123456");
        params3.setSpouseNameType(2);
        
        createClientEnterPersonalDataPage.createWithMandatoryFields(params3);
        createClientEnterPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        Assert.assertTrue(selenium.isTextPresent("*External Id"));
        Assert.assertTrue(selenium.isTextPresent("*Trained"));
        Assert.assertTrue(selenium.isTextPresent("*Trained On Date"));
        	
        ClientsAndAccountsHomepage clientsAndAccountsHomepage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsHomepage.navigateToCreateLoanAccountUsingLeftMenu();
        CreateLoanAccountSearchParameters params4 = new CreateLoanAccountSearchParameters();
        params4.setSearchString("ClientWithLoan");
        params4.setLoanProduct("ClientEmergencyLoan");
        createLoanAccountSearchPage.searchAndNavigateToCreateLoanAccountPage(params4);
        Assert.assertTrue(selenium.isTextPresent("*External id"));
        
        navigationHelper.navigateToClientsAndAccountsPage();
        CreateGroupSearchPage createGroupSearchPage = clientsAndAccountsHomepage.navigateToCreateNewGroupPage();
        createGroupSearchPage.searchAndNavigateToCreateGroupPage("Default Center");
        Assert.assertTrue(selenium.isTextPresent("*External Id"));
        
        // restore original configuration
        defineHiddenMandatoryFieldsPage = navigationHelper.navigateToAdminPage().navigateToDefineHiddenMandatoryFields();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryExternalId();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryEthnicity();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryCitizenShip();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryHandicapped();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryEducationLevel();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryPhoto();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryAddress1();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryMiddleName();
        defineHiddenMandatoryFieldsPage.uncheckMandatorySecondLastName();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryGovtId();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryMaritalStatus();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryRelativeSecondLastName();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryPhone();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryTrained();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryTrainedOn();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryNumberOfChildren();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryLoanAccountPurpose();
        defineHiddenMandatoryFieldsPage.uncheckMandatoryLoanSourceOfFund();
        defineHiddenMandatoryFieldsPage.submit();
    }
}
