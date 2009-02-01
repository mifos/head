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
package org.mifos.test.acceptance.framework;

import org.mifos.test.acceptance.framework.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.util.StringUtil;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * Encapsulates the GUI based actions that can
 * be done from the Home page and the page 
 * that will be navigated to.
 *
 */
public class AdminPage extends MifosPage {

	public AdminPage() {
		super();
	}

	public AdminPage(Selenium selenium) {
		super(selenium);
	}
		
	public CreateOfficeEnterDataPage navigateToCreateOfficeEnterDataPage() {
        selenium.click("admin.link.defineNewOffice");
        waitForPageToLoad();
        return new CreateOfficeEnterDataPage(selenium);	    
	}
	
    public ChooseOfficePage navigateToCreateUserPage() {
        selenium.click("admin.link.defineNewUsers");
        waitForPageToLoad();
        return new ChooseOfficePage(selenium);       
    }
    
    public DefineNewLoanProductPage navigateToDefineLoanProduct() {
        selenium.click("link=Define new Loan product");
        waitForPageToLoad();
        return new DefineNewLoanProductPage(selenium);
    }

    public AdminPage verifyPage() {
        Assert.assertTrue(selenium.isElementPresent("admin.label.admintasks"),"Didn't reach Admin home page");
        Assert.assertTrue(selenium.isElementPresent("admin.text.welcome"), "Welcome message not found on Admin home page");
        return this;
    }
    
    public AdminPage createOffice(AdminPage adminPage, String officeName) {
        CreateOfficeEnterDataPage officeEnterDataPage = adminPage.navigateToCreateOfficeEnterDataPage();
        
        CreateOfficeEnterDataPage.SubmitFormParameters formParameters = new CreateOfficeEnterDataPage.SubmitFormParameters();
        formParameters.setOfficeName(officeName);
        formParameters.setShortName(StringUtil.getRandomString(4));
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
        Assert.assertEquals(detailsPage.getOfficeName(), formParameters.getOfficeName());
        Assert.assertEquals(detailsPage.getShortName(), formParameters.getShortName());
        Assert.assertEquals(detailsPage.getOfficeType(), formParameters.getOfficeType());
        
        return detailsPage.navigateToAdminPage();
    }
  
    public UserViewDetailsPage createUser(AdminPage adminPage, CreateUserEnterDataPage.SubmitFormParameters formParameters, String officeName) {
        ChooseOfficePage chooseOfficePage = adminPage.navigateToCreateUserPage();
        CreateUserEnterDataPage userEnterDataPage = chooseOfficePage.selectOffice(officeName);

        CreateUserPreviewDataPage userPreviewDataPage = userEnterDataPage.submitAndGotoCreateUserPreviewDataPage(formParameters);
        CreateUserConfirmationPage userConfirmationPage = userPreviewDataPage.submit();

        Assert.assertTrue(userConfirmationPage.getConfirmation().contains(formParameters.getFirstName() + " " + formParameters.getLastName() + " has been assigned the system ID number:"));
        
        UserViewDetailsPage userDetailsPage = userConfirmationPage.navigateToUserViewDetailsPage();
        Assert.assertTrue(userDetailsPage.getFullName().contains(formParameters.getFirstName() + " " + formParameters.getLastName()));
        Assert.assertEquals(userDetailsPage.getStatus(), "Active");
        return userDetailsPage;
    }

    public void defineLoanProduct(SubmitFormParameters formParameters) {
        DefineNewLoanProductPage newLoanPage = navigateToDefineLoanProduct();
        newLoanPage.verifyPage();
        DefineNewLoanProductPreviewPage previewPage = newLoanPage.submitAndGotoNewLoanProductPreviewPage(formParameters);
        previewPage.verifyPage();
        DefineNewLoanProductConfirmationPage confirmationPage = previewPage.submit();
        confirmationPage.verifyPage();    
    }
  
}
