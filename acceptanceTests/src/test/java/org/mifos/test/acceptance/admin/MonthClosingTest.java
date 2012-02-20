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

import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.MonthClosingPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups={"acceptance", "ui", "no_db_unit"})
public class MonthClosingTest extends UiTestCaseBase{

    private NavigationHelper navigationHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        navigationHelper = new NavigationHelper(selenium);
        super.setUp();
    }

    @Test(enabled = true)
    public void testMonthClosingPermission(){
        // When
        AdminPage adminPage = navigationHelper.navigateToAdminPage().navigateToViewRolesPage().navigateToManageRolePage("Admin").disablePermission("0_5").
                verifyPermissionText("0_5", "Can set Month Closing date").submitAndGotoViewRolesPage().navigateToAdminPage();

        // Then
        adminPage.navigateToMonthClosing().submitWithoutPermission();

        // When
        adminPage = navigationHelper.navigateToAdminPage().navigateToViewRolesPage().navigateToManageRolePage("Admin").enablePermission("0_5").
                verifyPermissionText("0_5", "Can set Month Closing date").submitAndGotoViewRolesPage().navigateToAdminPage();

        // Then
        MonthClosingPage monthClosingPage = adminPage.navigateToMonthClosing();
        monthClosingPage.
                verifyCurrentMonthClosingDate("-");
        monthClosingPage.
                fillMonthClosingDate("20/02/12").
                submit().
                verifyCurrentMonthClosingDate("20/02/12");
        monthClosingPage.
                fillMonthClosingDate("").
                submit().
                verifyCurrentMonthClosingDate("-");
    }
}
