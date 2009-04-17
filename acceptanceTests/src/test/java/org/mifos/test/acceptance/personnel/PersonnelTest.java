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
 
package org.mifos.test.acceptance.personnel;

import org.mifos.test.acceptance.framework.AdminPage;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.CreateUserEnterDataPage;
import org.mifos.test.acceptance.framework.EditUserDataPage;
import org.mifos.test.acceptance.framework.EditUserPreviewDataPage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.UserViewDetailsPage;
import org.mifos.test.acceptance.framework.login.ChangePasswordPage;
import org.mifos.test.acceptance.framework.LoginPage;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = { "createUserStory", "acceptance", "ui" })
public class PersonnelTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
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
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        AdminPage adminPage = homePage.navigateToAdminPage();
        String officeName = "Bangalore Branch " + StringUtil.getRandomString(8);
        AdminPage adminPage2 = adminPage.createOffice(adminPage, officeName);
        adminPage2.createUser(adminPage2, adminPage2.getAdminUserParameters(), officeName);
    }

    public void editUserTest() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        AdminPage adminPage = homePage.navigateToAdminPage();

        String officeName = "Bangalore Branch " + StringUtil.getRandomString(8);
        AdminPage adminPage2 = adminPage.createOffice(adminPage, officeName);

        UserViewDetailsPage userDetailsPage = adminPage2.createUser(adminPage2, adminPage2.getAdminUserParameters(), officeName);

        EditUserDataPage editUserPage = userDetailsPage.navigateToEditUserDataPage();

        CreateUserEnterDataPage.SubmitFormParameters formParameters = new CreateUserEnterDataPage.SubmitFormParameters();
        formParameters.setFirstName("Update");
        formParameters.setLastName("User" + StringUtil.getRandomString(8));
        formParameters.setEmail("xxx.yyy@xxx.zzz");

        EditUserPreviewDataPage editPreviewDataPage = editUserPage.submitAndGotoEditUserPreviewDataPage(formParameters);
        UserViewDetailsPage userDetailsPage2 = editPreviewDataPage.submit();
        userDetailsPage2.verifyModifiedNameAndEmail(formParameters);
    }

    public void createUserWithNonAdminRoleTest() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        AdminPage adminPage = homePage.navigateToAdminPage();
        String officeName = "Bangalore Branch " + StringUtil.getRandomString(8);
        AdminPage adminPage2 = adminPage.createOffice(adminPage, officeName);
        adminPage.createUser(adminPage2, adminPage.getNonAdminUserParameters(), officeName);
    }
    
    public void changePasswordTest() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        AdminPage adminPage = homePage.navigateToAdminPage();

        String officeName = "Bangalore Branch " + StringUtil.getRandomString(8);
        AdminPage adminPage2 = adminPage.createOffice(adminPage, officeName);
        
        CreateUserEnterDataPage.SubmitFormParameters userParameters = adminPage2.getAdminUserParameters();
        UserViewDetailsPage userDetailsPage = adminPage2.createUser(adminPage2, userParameters, officeName);        
        EditUserDataPage editUserPage = userDetailsPage.navigateToEditUserDataPage();

        CreateUserEnterDataPage.SubmitFormParameters passwordParameters = new CreateUserEnterDataPage.SubmitFormParameters();
        passwordParameters.setPassword("tester1");
        passwordParameters.setPasswordRepeat("tester1");

        EditUserPreviewDataPage editPreviewDataPage = editUserPage.submitAndGotoEditUserPreviewDataPage(passwordParameters);
        editPreviewDataPage.submit();
        
        LoginPage loginPage = new LoginPage(selenium);
        loginPage.logout();
        ChangePasswordPage changePasswordPage = loginPage.loginAndGoToChangePasswordPageAs(userParameters.getUserName(), passwordParameters.getPassword());
        ChangePasswordPage.SubmitFormParameters changePasswordParameters = new ChangePasswordPage.SubmitFormParameters();
        changePasswordParameters.setOldPassword("tester1");
        changePasswordParameters.setNewPassword("tester2");
        changePasswordParameters.setConfirmPassword("tester2");
        HomePage homePage2 = changePasswordPage.submitAndGotoHomePage(changePasswordParameters);
        
        Assert.assertTrue(homePage2.getWelcome().contains(userParameters.getFirstName()));
    }
}
