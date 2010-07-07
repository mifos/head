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

package org.mifos.test.acceptance.personnel;


import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.login.ChangePasswordPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.office.ChooseOfficePage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.UserHelper;
import org.mifos.test.acceptance.framework.user.CreateUserConfirmationPage;
import org.mifos.test.acceptance.framework.user.CreateUserEnterDataPage;
import org.mifos.test.acceptance.framework.user.CreateUserParameters;
import org.mifos.test.acceptance.framework.user.CreateUserPreviewDataPage;
import org.mifos.test.acceptance.framework.user.EditUserDataPage;
import org.mifos.test.acceptance.framework.user.EditUserPreviewDataPage;
import org.mifos.test.acceptance.framework.user.UserViewDetailsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"personnel","acceptance","ui", "smoke"})
public class PersonnelTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    private UserHelper userHelper;

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        userHelper = new UserHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
//        (new MifosPage(selenium)).logout();
    }

    @Test(groups = {"smoke"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void createUserTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        userHelper.createUser(adminPage.getAdminUserParameters(), "MyOffice1233171674227");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void editUserTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        AdminPage adminPage = navigationHelper.navigateToAdminPage();

        UserViewDetailsPage userDetailsPage = userHelper.createUser(adminPage.getAdminUserParameters(), "MyOffice1233171674227");

        EditUserDataPage editUserPage = userDetailsPage.navigateToEditUserDataPage();

        CreateUserParameters formParameters = new CreateUserParameters();
        formParameters.setFirstName("Update");
        formParameters.setLastName("User" + StringUtil.getRandomString(8));
        formParameters.setEmail("xxx.yyy@xxx.zzz");

        EditUserPreviewDataPage editPreviewDataPage = editUserPage.submitAndGotoEditUserPreviewDataPage(formParameters);
        UserViewDetailsPage userDetailsPage2 = editPreviewDataPage.submit();
        userDetailsPage2.verifyModifiedNameAndEmail(formParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void createUserWithNonAdminRoleTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        userHelper.createUser(adminPage.getNonAdminUserParameters(), "MyOffice1233171674227");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(sequential = true, groups = {"personnel","acceptance","ui"})
    public void changePasswordTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        HomePage homePage = loginSuccessfully();
        AdminPage adminPage = homePage.navigateToAdminPage();

        CreateUserParameters userParameters = adminPage.getAdminUserParameters();
        ChooseOfficePage createUserPage = adminPage.navigateToCreateUserPage();
        createUserPage.verifyPage();

        CreateUserEnterDataPage userEnterDataPage = createUserPage.selectOffice("MyOffice1233171674227");

        CreateUserPreviewDataPage userPreviewDataPage = userEnterDataPage.submitAndGotoCreateUserPreviewDataPage(userParameters);
        CreateUserConfirmationPage userConfirmationPage = userPreviewDataPage.submit();
        userConfirmationPage.verifyPage();

        UserViewDetailsPage userDetailsPage = userConfirmationPage.navigateToUserViewDetailsPage();
        userDetailsPage.verifyPage();
        Assert.assertTrue(userDetailsPage.getFullName().contains(userParameters.getFirstName() + " " + userParameters.getLastName()));

        EditUserDataPage editUserPage = userDetailsPage.navigateToEditUserDataPage();

        CreateUserParameters passwordParameters = new CreateUserParameters();
        passwordParameters.setPassword("tester1");
        passwordParameters.setPasswordRepeat("tester1");

        EditUserPreviewDataPage editPreviewDataPage = editUserPage.submitAndGotoEditUserPreviewDataPage(passwordParameters);
        UserViewDetailsPage submitUserpage = editPreviewDataPage.submit();
        submitUserpage.verifyPage();

        LoginPage loginPage = (new MifosPage(selenium)).logout();

        ChangePasswordPage changePasswordPage = loginPage.loginAndGoToChangePasswordPageAs(userParameters.getUserName(), passwordParameters.getPassword());
        ChangePasswordPage.SubmitFormParameters changePasswordParameters = new ChangePasswordPage.SubmitFormParameters();
        changePasswordParameters.setOldPassword("tester1");
        changePasswordParameters.setNewPassword("tester2");
        changePasswordParameters.setConfirmPassword("tester2");
        HomePage homePage2 = changePasswordPage.submitAndGotoHomePage(changePasswordParameters);

        Assert.assertTrue(homePage2.getWelcome().contains(userParameters.getFirstName()));
    }

    private HomePage loginSuccessfully() {
        (new MifosPage(selenium)).logout();
        LoginPage loginPage = new AppLauncher(selenium).launchMifos();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();

        return homePage;
    }
}
