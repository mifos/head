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
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
        AdminPage adminPage = loginAndGoToAdminPage();
        String officeName = "Bangalore Branch " + StringUtil.getRandomString(8);
        AdminPage adminPage2 = adminPage.createOffice(adminPage, officeName);
        adminPage.createUser(adminPage2, getAdminUserParameters(), officeName);
    }

    public void editUserTest() {
        AdminPage adminPage = loginAndGoToAdminPage();

        String officeName = "Bangalore Branch " + StringUtil.getRandomString(8);
        AdminPage adminPage2 = adminPage.createOffice(adminPage, officeName);

        UserViewDetailsPage userDetailsPage = adminPage.createUser(adminPage2, getAdminUserParameters(), officeName);

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
        AdminPage adminPage = loginAndGoToAdminPage();
        String officeName = "Bangalore Branch " + StringUtil.getRandomString(8);
        AdminPage adminPage2 = adminPage.createOffice(adminPage, officeName);
        adminPage.createUser(adminPage2, getNonAdminUserParameters(), officeName);
    }

    public CreateUserEnterDataPage.SubmitFormParameters getAdminUserParameters() {
        CreateUserEnterDataPage.SubmitFormParameters formParameters = new CreateUserEnterDataPage.SubmitFormParameters();
        formParameters.setFirstName("New");
        formParameters.setLastName("User" + StringUtil.getRandomString(8));
        formParameters.setDateOfBirthDD("21");
        formParameters.setDateOfBirthMM("11");
        formParameters.setDateOfBirthYYYY("1980");
        formParameters.setGender("Male");
        formParameters.setPreferredLanguage("English");
        formParameters.setUserLevel("Loan Officer");
        formParameters.setRole("Admin");
        formParameters.setUserName("loanofficer_blore" + StringUtil.getRandomString(5));
        formParameters.setPassword("password");
        formParameters.setPasswordRepeat("password");
        return formParameters;
    }

    public CreateUserEnterDataPage.SubmitFormParameters getNonAdminUserParameters() {
        CreateUserEnterDataPage.SubmitFormParameters formParameters = new CreateUserEnterDataPage.SubmitFormParameters();
        formParameters.setFirstName("NonAdmin");
        formParameters.setLastName("User" + StringUtil.getRandomString(8));
        formParameters.setDateOfBirthDD("04");
        formParameters.setDateOfBirthMM("04");
        formParameters.setDateOfBirthYYYY("1986");
        formParameters.setGender("Male");
        formParameters.setUserLevel("Non Loan Officer");
        formParameters.setUserName("test" + StringUtil.getRandomString(5));
        formParameters.setPassword("tester");
        formParameters.setPasswordRepeat("tester");
        return formParameters;
    }


    public AdminPage loginAndGoToAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        AdminPage adminPage = homePage.navigateToAdminPage();
        adminPage.verifyPage();
        return adminPage;
    }

}
