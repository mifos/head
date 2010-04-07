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
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.login.ChangePasswordPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.UserHelper;
import org.mifos.test.acceptance.framework.user.CreateUserParameters;
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

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"personnel","acceptance","ui"})
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
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        userHelper = new UserHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @Test(groups = {"smoke"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createUserTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        userHelper.createUser(adminPage.getAdminUserParameters(), "MyOffice1233171674227");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void editUserTest() throws Exception {
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
    public void createUserWithNonAdminRoleTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        userHelper.createUser(adminPage.getNonAdminUserParameters(), "MyOffice1233171674227");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(sequential = true, groups = {"personnel","acceptance","ui"})
    public void changePasswordTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateUserParameters userParameters = adminPage.getAdminUserParameters();
        UserViewDetailsPage userDetailsPage = userHelper.createUser(userParameters, "MyOffice1233171674227");
        EditUserDataPage editUserPage = userDetailsPage.navigateToEditUserDataPage();

        CreateUserParameters passwordParameters = new CreateUserParameters();
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
