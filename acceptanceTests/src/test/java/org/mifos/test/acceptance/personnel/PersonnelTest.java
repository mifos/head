package org.mifos.test.acceptance.personnel;
/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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

import java.util.Calendar;

import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.AdminPage;
import org.mifos.test.acceptance.framework.CreateOfficeEnterDataPage;
import org.mifos.test.acceptance.framework.CreateOfficePreviewDataPage;
import org.mifos.test.acceptance.framework.CreateOfficeConfirmationPage;
import org.mifos.test.acceptance.framework.ChooseOfficePage;
import org.mifos.test.acceptance.framework.CreateUserEnterDataPage;
import org.mifos.test.acceptance.framework.CreateUserPreviewDataPage;
import org.mifos.test.acceptance.framework.CreateUserConfirmationPage;
import org.mifos.test.acceptance.framework.UserViewDetailsPage;
import org.mifos.test.acceptance.framework.EditUserDataPage;
import org.mifos.test.acceptance.framework.EditUserPreviewDataPage;
import org.mifos.test.acceptance.framework.OfficeViewDetailsPage;

import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/*
 * Corresponds to issue tracker task 2338
 */
@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"workInProgress", "createUserStory","acceptance","ui"})
public class PersonnelTest extends UiTestCaseBase {

	private AppLauncher appLauncher;
	
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
	@BeforeMethod
	public void setUp() throws Exception {
		super.setUp();
		appLauncher = new AppLauncher(selenium);
	}

	@AfterMethod
	public void logOut() {
		(new MifosPage(selenium)).logout();
	}
	
	public void createUserTest() {
	    HomePage homePage = appLauncher.launchMifos().loginSuccessfulAs("mifos", "testmifos");
	    homePage.verifyPage();
	    AdminPage adminPage = homePage.navigateToAdminPage();
        assertTextPresent(adminPage.getWelcome(), "Welcome to mifos administrative area");
     
        String randomId = getRandomString(8);
        String shortRandomId = getRandomString(3);
        String officeName = "Bangalore Branch " + randomId;
        
        AdminPage adminPage2 = createOffice(adminPage, randomId);
	    ChooseOfficePage chooseOfficePage = adminPage2.navigateToCreateUserPage();
	    CreateUserEnterDataPage userEnterDataPage = chooseOfficePage.selectOffice(officeName);
	    
	    userEnterDataPage.setFirstName("New");
	    userEnterDataPage.setLastName("User" + randomId);
	    userEnterDataPage.setDateOfBirth("21", "11", "1980");
	    userEnterDataPage.setGender("Male");
	    userEnterDataPage.setPreferredLanguage("English");
	    userEnterDataPage.setUserLevel("Loan Officer");
	    userEnterDataPage.setRoles("Admin");
	    userEnterDataPage.setUserName("loanofficer_blore" + shortRandomId);
        userEnterDataPage.setPassword("password");
        userEnterDataPage.setPasswordRepeat("password");
        CreateUserPreviewDataPage userPreviewDataPage = userEnterDataPage.preview();
        CreateUserConfirmationPage userConfirmationPage = userPreviewDataPage.submit();

        assertTextPresent(userConfirmationPage.getConfirmation(), "New User" + randomId + " has been assigned the system ID number:");
        
        UserViewDetailsPage userDetailsPage = userConfirmationPage.navigateToUserViewDetailsPage();
        assertTextPresent(userDetailsPage.getFullName(), "New User" + randomId);
        assertEquals(userDetailsPage.getStatus(), "Active");

	}
	
	public void editUserTest() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfulAs("mifos", "testmifos");
        AdminPage adminPage = homePage.navigateToAdminPage();
        
        String randomId = getRandomString(8);
        String shortRandomId = getRandomString(3);
        
        adminPage = createOffice(adminPage, randomId);
        ChooseOfficePage chooseOfficePage = adminPage.navigateToCreateUserPage();
        String officeName = "Bangalore Branch " + randomId;        
        CreateUserEnterDataPage userEnterDataPage = chooseOfficePage.selectOffice(officeName);

        userEnterDataPage.setFirstName("New");
        userEnterDataPage.setLastName("User" + randomId);
        userEnterDataPage.setDateOfBirth("21", "11", "1980");
        userEnterDataPage.setGender("Male");
        userEnterDataPage.setPreferredLanguage("English");
        userEnterDataPage.setUserLevel("Loan Officer");
        userEnterDataPage.setUserName("loanofficer_blore" + shortRandomId);
        userEnterDataPage.setPassword("password");
        userEnterDataPage.setPasswordRepeat("password");
        CreateUserPreviewDataPage userPreviewDataPage = userEnterDataPage.preview();
        CreateUserConfirmationPage userConfirmationPage = userPreviewDataPage.submit();
        
        assertTextPresent(userConfirmationPage.getConfirmation(), "New User" + randomId + " has been assigned the system ID number:");
        
        UserViewDetailsPage userDetailsPage = userConfirmationPage.navigateToUserViewDetailsPage();
        assertTextPresent(userDetailsPage.getFullName(), "New User" + randomId);
        assertEquals(userDetailsPage.getStatus(), "Active");
        
        EditUserDataPage editUserPage = userDetailsPage.navigateToEditUserDataPage();
        editUserPage.setFirstName("Update");
        String lastName = "User" + getRandomString(8);
        editUserPage.setLastName(lastName);
        editUserPage.setEmail("xxx.yyy@xxx.zzz");
        
        EditUserPreviewDataPage editPreviewDataPage = editUserPage.preview();
        UserViewDetailsPage userDetailsPage2 = editPreviewDataPage.submit();
        
        assertTextPresent(userDetailsPage2.getFullName(), "Update " + lastName);
        assertEquals(userDetailsPage2.getEmail(), "xxx.yyy@xxx.zzz");
        
	}
	
    public void createUserWithNonAdminRoleTest() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfulAs("mifos", "testmifos");
        
        assertEquals(homePage.getWelcome(), "Welcome,  mifos"); 
        assertTextPresent(homePage.getLastLogin(), "The last time you logged on was");
        
        AdminPage adminPage = homePage.navigateToAdminPage();
        assertTextPresent(adminPage.getWelcome(), "Welcome to mifos administrative area");

        String randomId = getRandomString(8);
        String shortRandomId = getRandomString(3);        
        String officeName = "Bangalore Branch " + randomId;
        
        AdminPage adminPage2 = createOffice(adminPage, randomId);
        
        ChooseOfficePage chooseOfficePage = adminPage2.navigateToCreateUserPage();
        CreateUserEnterDataPage userEnterDataPage = chooseOfficePage.selectOffice(officeName);
          
        userEnterDataPage.setFirstName("NonAdmin");
        userEnterDataPage.setLastName("User" + randomId);
        userEnterDataPage.setDateOfBirth("04", "04", "1986");
        userEnterDataPage.setGender("Male");

        userEnterDataPage.setUserLevel("Non Loan Officer");
        
        userEnterDataPage.setUserName("tester" + shortRandomId);
        userEnterDataPage.setPassword("tester");
        userEnterDataPage.setPasswordRepeat("tester");
        CreateUserPreviewDataPage userPreviewDataPage = userEnterDataPage.preview();
        CreateUserConfirmationPage userConfirmationPage = userPreviewDataPage.submit();
        
        assertTextPresent(userConfirmationPage.getConfirmation(), "NonAdmin User" + randomId + " has been assigned the system ID number:");
        
        UserViewDetailsPage userDetailsPage = userConfirmationPage.navigateToUserViewDetailsPage();
        assertTextPresent(userDetailsPage.getFullName(), "NonAdmin User");
        assertEquals(userDetailsPage.getStatus(), "Active");        
        }
    
    public AdminPage createOffice(AdminPage adminPage, String randomId) {
        CreateOfficeEnterDataPage officeEnterDataPage = adminPage.navigateToCreateOfficeEnterDataPage();
        
        String officeName = "Bangalore Branch " + randomId;
        String shortName = "B" + getRandomString(3);
        officeEnterDataPage.setOfficeName(officeName);
        
        officeEnterDataPage.setShortName(shortName);
        officeEnterDataPage.setOfficeType("Branch Office");
        officeEnterDataPage.setParentOffice("regexp:Mifos\\s+HO");
        officeEnterDataPage.setAddress1("Bangalore");
        officeEnterDataPage.setAddress3("EGL");
        officeEnterDataPage.setState("karnataka");
        officeEnterDataPage.setCountry("India");
        officeEnterDataPage.setPostalCode("560071");
        officeEnterDataPage.setPhoneNumber("918025003632");
        
        CreateOfficePreviewDataPage previewDataPage = officeEnterDataPage.preview();
        CreateOfficeConfirmationPage confirmationPage = previewDataPage.submit();
        assertTextPresent(confirmationPage.getConfirmation(), "You have successfully added a new office");
        OfficeViewDetailsPage detailsPage = confirmationPage.navigateToOfficeViewDetailsPage();
        assertEquals(detailsPage.getOfficeName(), officeName);
        assertEquals(detailsPage.getShortName(), shortName);
        assertEquals(detailsPage.getOfficeType(), "Branch Office");
        
        return detailsPage.navigateToAdminPage();
    }
    
    /* Should be implemented safer and moved to utility class */
	private void assertEquals(String text1, String text2) {
	    Assert.assertTrue(text1 != null && text1.equals(text2), "Text " + text2 + " is not equal to " + text1);	     
	}

    /* Should be implemented safer and moved to utility class */
    private void assertTextPresent(String text1, String text2) {
        Assert.assertTrue((text1 != null && text1.indexOf(text2) > - 1), "Text " + text2 + " is not present in " + text1);        
    }
    
	/* Should be implemented safer and moved to utility class */
	private String getRandomString(int length) {
	    String millis = Long.toString(Calendar.getInstance().getTimeInMillis());
	    return millis.substring(millis.length() - length);
	}
}
