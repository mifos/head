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


import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.ViewOrganizationSettingsPage;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"acceptance","ui","smoke"})

public class ViewOrganizationSettingsTest extends UiTestCaseBase {

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

    @Test
    public void verifyViewOrganizationSettingsPage() {
        AdminPage adminPage = loginAndGoToAdminPage();
        ViewOrganizationSettingsPage viewOrganizationSettingsPage = adminPage.navigateToViewOrganizationSettingsPage();
        viewOrganizationSettingsPage.verifyPage();


        String[] expectedFiscalYear = new String[]{
                "Working days:",
                "Allow calendar definition for next year:",
                "Start of Week:",
                "Non-working days:",
                "Meeting in case of non-working day:"
        };

        String[] expectedLocale = new String[]{
                "Country:",
                "Language:"
        };

        String[] expectedAccountingRules = new String[]{
                "Maximum Interest:",
                "Minimum Interest:",
                "Number of digits before decimal:",
                "Number of digits after decimal for interest:",
                "Number of digits before decimal for interest:",
                "Number of interest days:",
                "Currency Rounding Mode:",
                "Initial Rounding Mode:",
                "Final Rounding Mode:"
        };

        String[] expectedCurrencies = new String[]{
                "Currency:",
                "Number of digits after decimal:",
                "Final Round Off Multiple:",
                "Initial Round Off Multiple:"
        };

        String[] expectedClientRules = new String[]{
                "Center hierarchy exists:",
                "Groups allowed to apply for loans:",
                "Client can exist outside group:",
                "Name sequence:",
                "Age check enabled:",
                "Minimum allowed age for new clients:",
                "Maximum allowed age for new clients:",
                "Additional family details required:",
                "Maximum number of family members:"
        };

        String[] expectedProcessFlow = new String[]{
                "Client pending approval state enabled:",
                "Group pending approval state enabled:",
                "Loan pending approval state enabled:",
                "Savings pending approval state enabled:"
        };

        String[] expectedMiscellaneous = new String[]{
                "Session timeout:",
                "Number of days in advance the collection sheet should be generated:",
                "Back dated transactions allowed:",
                "Group loan with individual monitoring (GLIM):",
                "Loan schedule independent of meeting (LSIM):"

        };
        viewOrganizationSettingsPage.verifyFiscalYear( expectedFiscalYear );
        viewOrganizationSettingsPage.verifyLocale( expectedLocale );
        viewOrganizationSettingsPage.verifyAccountingRules( expectedAccountingRules );
        viewOrganizationSettingsPage.verifyCurrencies( expectedCurrencies );
        viewOrganizationSettingsPage.verifyClientRules( expectedClientRules );
        viewOrganizationSettingsPage.verifyProcessFlow( expectedProcessFlow );
        viewOrganizationSettingsPage.verifyMiscellaneous( expectedMiscellaneous );

    }
    private AdminPage loginAndGoToAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        AdminPage adminPage = homePage.navigateToAdminPage();
        adminPage.verifyPage();
        return adminPage;
    }

}

