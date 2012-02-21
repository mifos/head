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

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.MonthClosingPage;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.springframework.test.context.ContextConfiguration;

import java.io.UnsupportedEncodingException;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups={"acceptance", "ui", "no_db_unit"})
public class MonthClosingTest extends UiTestCaseBase{

    private NavigationHelper navigationHelper;
    private LoanProductTestHelper loanProductTestHelper;
    private LoanTestHelper loanTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        navigationHelper.
                navigateToAdminPage().
                navigateToMonthClosing().
                fillMonthClosingDate("").
                submit().
                verifyCurrentMonthClosingDate("-");
        (new MifosPage(selenium)).logout();
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
    }

    @Test(enabled = true)
    public void testDisbursalBeforeMonthClosingDate() throws UnsupportedEncodingException {
        // When
        new DateTimeUpdaterRemoteTestingService(selenium).
                setDateTime(new DateTime(2012, 1, 10, 12, 0, 0, 0));
        navigationHelper.
                navigateToAdminPage().
                navigateToMonthClosing().
                fillMonthClosingDate("20/01/12").
                submit().
                verifyCurrentMonthClosingDate("20/01/12");

        // Then
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                submitAndGotoNewLoanProductPreviewPage().submit();
        loanTestHelper.
                createLoanAccount("WeeklyClient Monday", formParameters.getOfferingName()).
                changeAccountStatusToAccepted().
                navigateToDisburseLoan().
                submitAndNavigateToDisburseLoanConfirmationPage(DisburseLoanParameters.getDisbursalParameters("10", "01", "2012")).
                submitButDisbursalFailed("Date of transaction is invalid. This date has already been closed for accounting.");
    }
}
