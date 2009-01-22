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

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"createUserStory","acceptance","ui"})
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
	    createUser(getAdminUserParameters());
	}
	
	public void editUserTest() {
	    UserViewDetailsPage userDetailsPage = createUser(getAdminUserParameters());
        
        EditUserDataPage editUserPage = userDetailsPage.navigateToEditUserDataPage();
        
        CreateUserEnterDataPage.SubmitFormParameters formParameters = new CreateUserEnterDataPage.SubmitFormParameters();        
        formParameters.setFirstName("Update");
        formParameters.setLastName("User" + getRandomString(8));
        formParameters.setEmail("xxx.yyy@xxx.zzz");
        
        EditUserPreviewDataPage editPreviewDataPage = editUserPage.submitAndGotoEditUserPreviewDataPage(formParameters);
        UserViewDetailsPage userDetailsPage2 = editPreviewDataPage.submit();
        assertTextPresent(userDetailsPage2.getFullName(), formParameters.getFirstName() + " " + formParameters.getLastName());
        assertEquals(userDetailsPage2.getEmail(), formParameters.getEmail());
	}
	
    public void createUserWithNonAdminRoleTest() {
        createUser(getNonAdminUserParameters());       
    }
     
    public UserViewDetailsPage createUser(CreateUserEnterDataPage.SubmitFormParameters formParameters) {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfulAs("mifos", "testmifos");
        homePage.verifyPage();
        AdminPage adminPage = homePage.navigateToAdminPage();
        adminPage.verifyPage();
     
        String officeName = "Bangalore Branch " + getRandomString(8);
        
        AdminPage adminPage2 = createOffice(adminPage, officeName);
        ChooseOfficePage chooseOfficePage = adminPage2.navigateToCreateUserPage();
        CreateUserEnterDataPage userEnterDataPage = chooseOfficePage.selectOffice(officeName);

        CreateUserPreviewDataPage userPreviewDataPage = userEnterDataPage.submitAndGotoCreateUserPreviewDataPage(formParameters);
        CreateUserConfirmationPage userConfirmationPage = userPreviewDataPage.submit();

        assertTextPresent(userConfirmationPage.getConfirmation(), formParameters.getFirstName() + " " + formParameters.getLastName() + " has been assigned the system ID number:");
        
        UserViewDetailsPage userDetailsPage = userConfirmationPage.navigateToUserViewDetailsPage();
        assertTextPresent(userDetailsPage.getFullName(), formParameters.getFirstName() + " " + formParameters.getLastName());
        assertEquals(userDetailsPage.getStatus(), "Active");
        return userDetailsPage;
    }
    
    public CreateUserEnterDataPage.SubmitFormParameters getAdminUserParameters() {
        CreateUserEnterDataPage.SubmitFormParameters formParameters = new CreateUserEnterDataPage.SubmitFormParameters();
        formParameters.setFirstName("New");
        formParameters.setLastName("User" + getRandomString(8));
        formParameters.setDateOfBirthDD("21");
        formParameters.setDateOfBirthMM("11");
        formParameters.setDateOfBirthYYYY("1980");        
        formParameters.setGender("Male");
        formParameters.setPreferredLanguage("English");
        formParameters.setUserLevel("Loan Officer");
        formParameters.setRole("Admin");
        formParameters.setUserName("loanofficer_blore" + getRandomString(5));
        formParameters.setPassword("password");
        formParameters.setPasswordRepeat("password");
        return formParameters;
    }
 
    public CreateUserEnterDataPage.SubmitFormParameters getNonAdminUserParameters() {
        CreateUserEnterDataPage.SubmitFormParameters formParameters = new CreateUserEnterDataPage.SubmitFormParameters();        
        formParameters.setFirstName("NonAdmin");
        formParameters.setLastName("User" + getRandomString(8));
        formParameters.setDateOfBirthDD("04");
        formParameters.setDateOfBirthMM("04");
        formParameters.setDateOfBirthYYYY("1986");
        formParameters.setGender("Male");
        formParameters.setUserLevel("Non Loan Officer");
        formParameters.setUserName("test" + getRandomString(5));
        formParameters.setPassword("tester");
        formParameters.setPasswordRepeat("tester");
        return formParameters;
    }
    
    public AdminPage createOffice(AdminPage adminPage, String officeName) {
        CreateOfficeEnterDataPage officeEnterDataPage = adminPage.navigateToCreateOfficeEnterDataPage();
        
        CreateOfficeEnterDataPage.SubmitFormParameters formParameters = new CreateOfficeEnterDataPage.SubmitFormParameters();
        formParameters.setOfficeName(officeName);
        formParameters.setShortName(getRandomString(4));
        formParameters.setOfficeType("Branch Office");
        formParameters.setParentOffice("regexp:Mifos\\s+HO");
        formParameters.setAddress1("Bangalore");
        formParameters.setAddress3("EGL");
        formParameters.setState("karnataka");
        formParameters.setCountry("India");
        formParameters.setPostalCode("560071");
        formParameters.setPhoneNumber("918025003632");
        
        CreateOfficePreviewDataPage previewDataPage = officeEnterDataPage.submitAndGotoCreateOfficePreviewDataPage(formParameters);
        CreateOfficeConfirmationPage confirmationPage = previewDataPage.submit();

        confirmationPage.verifyPage();
        OfficeViewDetailsPage detailsPage = confirmationPage.navigateToOfficeViewDetailsPage();
        assertEquals(detailsPage.getOfficeName(), formParameters.getOfficeName());
        assertEquals(detailsPage.getShortName(), formParameters.getShortName());
        assertEquals(detailsPage.getOfficeType(), formParameters.getOfficeType());
        
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
	    String millis = Long.toString(System.currentTimeMillis());
	    return millis.substring(millis.length() - length);
	}
}
