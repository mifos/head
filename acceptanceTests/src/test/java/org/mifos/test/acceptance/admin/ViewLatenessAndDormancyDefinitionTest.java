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

package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.ViewLatenessAndDormancyDefinitionPage;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"acceptance","ui","smoke"})

public class ViewLatenessAndDormancyDefinitionTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
    }

    @Test(enabled=false)
    public void verifyViewLatenessAndDormancyDefinitionPage() {
        AdminPage adminPage = loginAndGoToAdminPage();
        ViewLatenessAndDormancyDefinitionPage viewLatenessAndDormancyDefinitionPage = adminPage.navigateToViewLatenessAndDormancyDefinitionPage();
        viewLatenessAndDormancyDefinitionPage.verifyPage();

        String[] expectedText = new String[]{
                "Set Lateness Definition",
                "Loan",
                "Specify the number of days of non-payment after which status of Loan account is changed to \"In Arrears\" by the system:  days",
                "Set Dormancy Definition",
                "Savings",
                "Specify the number of days to define \"Dormancy\" in Savings Accounts. The account status will be changed to \"On Hold\" by the system:  days"
        };

        viewLatenessAndDormancyDefinitionPage.verifyText( expectedText );


    }
    private AdminPage loginAndGoToAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        AdminPage adminPage = homePage.navigateToAdminPage();
        adminPage.verifyPage();
        return adminPage;
    }

}

