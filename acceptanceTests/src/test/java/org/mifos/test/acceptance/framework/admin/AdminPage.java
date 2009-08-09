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
 
package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage;
import org.mifos.test.acceptance.framework.holiday.ViewHolidaysPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSearchPage;
import org.mifos.test.acceptance.framework.loan.UndoLoanDisbursalSearchPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductConfirmationPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.ViewLoanProductsPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.office.ChooseOfficePage;
import org.mifos.test.acceptance.framework.office.CreateOfficeEnterDataPage;
import org.mifos.test.acceptance.framework.user.CreateUserConfirmationPage;
import org.mifos.test.acceptance.framework.user.CreateUserEnterDataPage;
import org.mifos.test.acceptance.framework.user.CreateUserParameters;
import org.mifos.test.acceptance.framework.user.CreateUserPreviewDataPage;
import org.mifos.test.acceptance.framework.user.UserViewDetailsPage;
import org.mifos.test.acceptance.util.StringUtil;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * Encapsulates the GUI based actions that can
 * be done from the Admin page and the page 
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

	public ViewHolidaysPage navigateToViewHolidaysPage() {
	    selenium.click("admin.link.viewHolidays");
        waitForPageToLoad();
        return new ViewHolidaysPage(selenium);     
    }
 
	public ViewFeesPage navigateToViewFeesPage() {
        selenium.click("admin.link.viewFees");
        waitForPageToLoad();
        return new ViewFeesPage(selenium);
    }
	
   public ViewReportsPage navigateToViewReportsPage() {
        selenium.click("admin.link.ViewReportsTemplates");
        waitForPageToLoad();
        return new ViewReportsPage(selenium);     
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
    
    public ViewOfficesPage navigateToViewOfficesPage() {
        selenium.click("admin.link.viewOffices");
        waitForPageToLoad();
        return new ViewOfficesPage(selenium);     
    }   
                             
    public ViewProductCategoriesPage navigateToViewProductCategoriesPage() {
        selenium.click("admin.link.viewProductCategories");
        waitForPageToLoad();
        return new ViewProductCategoriesPage(selenium);     
    }   
    
    public DefineNewLoanProductPage navigateToDefineLoanProduct() {
        selenium.click("admin.link.defineNewLoanProduct");
        waitForPageToLoad();
        return new DefineNewLoanProductPage(selenium);
    }
    
    public UndoLoanDisbursalSearchPage navigateToUndoLoanDisbursal() {
        selenium.click("admin.link.reverseLoanDisbursal");
        waitForPageToLoad();
        return new UndoLoanDisbursalSearchPage(selenium);
    }
    
    public RedoLoanDisbursalSearchPage navigateToRedoLoanDisbursal() {
        selenium.click("admin.link.redoLoanDisbursal");
        waitForPageToLoad();
        return new RedoLoanDisbursalSearchPage(selenium);
    }

    public AdminPage verifyPage() {
        verifyPage("admin");
        return this;
    }
  
    public UserViewDetailsPage createUser(CreateUserParameters formParameters, String officeName) {
        ChooseOfficePage chooseOfficePage = this.navigateToCreateUserPage();
        CreateUserEnterDataPage userEnterDataPage = chooseOfficePage.selectOffice(officeName);

        CreateUserPreviewDataPage userPreviewDataPage = userEnterDataPage.submitAndGotoCreateUserPreviewDataPage(formParameters);
        CreateUserConfirmationPage userConfirmationPage = userPreviewDataPage.submit();
        userConfirmationPage.verifyPage();
        
        UserViewDetailsPage userDetailsPage = userConfirmationPage.navigateToUserViewDetailsPage();
        userDetailsPage.verifyPage();
        Assert.assertTrue(userDetailsPage.getFullName().contains(formParameters.getFirstName() + " " + formParameters.getLastName()));
        //Assert.assertEquals(userDetailsPage.getStatus(), "Active");
        return userDetailsPage;
    }
    
    public CreateUserParameters getAdminUserParameters() {
        CreateUserParameters formParameters = new CreateUserParameters();
        formParameters.setFirstName("New");
        formParameters.setLastName("User" + StringUtil.getRandomString(8));
        formParameters.setDateOfBirthDD("21");
        formParameters.setDateOfBirthMM("11");
        formParameters.setDateOfBirthYYYY("1980");
        formParameters.setGender(CreateUserParameters.MALE);
        formParameters.setPreferredLanguage(CreateUserParameters.ENGLISH);
        formParameters.setUserLevel(CreateUserParameters.LOAN_OFFICER);
        formParameters.setRole("Admin");
        formParameters.setUserName("loanofficer_blore" + StringUtil.getRandomString(3));
        formParameters.setPassword("password");
        formParameters.setPasswordRepeat("password");
        return formParameters;
    }

    public CreateUserParameters getNonAdminUserParameters() {
        CreateUserParameters formParameters = new CreateUserParameters();
        formParameters.setFirstName("NonAdmin");
        formParameters.setLastName("User" + StringUtil.getRandomString(8));
        formParameters.setDateOfBirthDD("04");
        formParameters.setDateOfBirthMM("04");
        formParameters.setDateOfBirthYYYY("1986");
        formParameters.setGender(CreateUserParameters.MALE);
        formParameters.setUserLevel(CreateUserParameters.NON_LOAN_OFFICER);
        formParameters.setUserName("test" + StringUtil.getRandomString(5));
        formParameters.setPassword("tester");
        formParameters.setPasswordRepeat("tester");
        return formParameters;
    }    
    
    public void defineLoanProduct(SubmitFormParameters formParameters) {
        DefineNewLoanProductPage newLoanPage = navigateToDefineLoanProduct();
        newLoanPage.verifyPage();
        DefineNewLoanProductPreviewPage previewPage = newLoanPage.submitAndGotoNewLoanProductPreviewPage(formParameters);
        previewPage.verifyPage();
        DefineNewLoanProductConfirmationPage confirmationPage = previewPage.submit();
        confirmationPage.verifyPage();    
    }

    public SystemInfoPage navigateToSystemInfoPage() {
        selenium.click("admin.link.viewSystemInfo");
        waitForPageToLoad();
        return new SystemInfoPage(selenium);

    }

    public ViewLoanProductsPage navigateToViewLoanProducts() {
        selenium.click("admin.link.viewLoanProducts");
        waitForPageToLoad();
        return new ViewLoanProductsPage(selenium);
    }

    public DefineAdditionalFieldsPage navigateToDefineAdditionalFieldsPage() {
        selenium.click("admin.link.defineAdditionalFields");
        waitForPageToLoad();
        return new DefineAdditionalFieldsPage(selenium);
    }

    public ViewAdditionalFieldCategoriesPage navigateToViewAdditionalFields() {
        selenium.click("admin.link.viewAdditionalFields");  
        waitForPageToLoad();
        return new ViewAdditionalFieldCategoriesPage(selenium);
    }
  
    public ViewFundsPage navigateToViewFundsPage() {
        selenium.click("admin.link.viewFunds");
        waitForPageToLoad();
        return new ViewFundsPage(selenium);
    }
           

    public CreateHolidayEntryPage navigateToDefineHolidayPage() {
        selenium.click("admin.link.defineNewHoliday");
        waitForPageToLoad();
        return new CreateHolidayEntryPage(selenium);
    }

    public ViewHolidaysPage navigateToViewHolidays() {
        selenium.click("admin.link.viewHolidays");  
        waitForPageToLoad();
        return new ViewHolidaysPage(selenium);
    }
    
    public ViewReportCategoriesPage navigateToViewReportCategories() {
        selenium.click("admin.link.ViewReportsCategory");
        waitForPageToLoad();
        return new ViewReportCategoriesPage(selenium);
    }
}
