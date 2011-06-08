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
import org.mifos.test.acceptance.framework.admin.ManageRolePage;
import org.mifos.test.acceptance.framework.admin.ViewRolesPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.office.OfficeParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"admin", "acceptance", "ui", "no_db_unit"})
public class RolesAndPermissionTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;

    private LoanTestHelper loanTestHelper;
    private LoanProductTestHelper loanProductTestHelper;
    private DateTime systemDateTime;
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    private final static String userLoginName = "test_user";
    private final static String officeName = "test_office";
    private final static String clientName = "test client";
    private final static String userName = "test user";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        systemDateTime = new DateTime(2010, 10, 11, 10, 0, 0, 0);
        TestDataSetup dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);
        loanTestHelper.setApplicationTime(systemDateTime);
        dataSetup.createBranch(OfficeParameters.BRANCH_OFFICE, officeName, "Off");
        dataSetup.createUser(userLoginName, userName, officeName);
        dataSetup.createClient(clientName, officeName, userName);
        dataSetup.addDecliningPrincipalBalance();
    }


    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=false)
    public void adjustmentOfPostDatedTransactions() throws Exception {
        navigationHelper.navigateToAdminPage().navigateToViewRolesPage().navigateToManageRolePage("Admin").disablePermission("5_1_9").
                verifyPermissionText("5_1_9", "Can adjust back dated transactions").submitAndGotoViewRolesPage();
        DefineNewLoanProductPage.SubmitFormParameters formParameters = loanProductTestHelper.defineLoanProductParameters(5, 1000, 20, DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION);
        loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                submitAndGotoNewLoanProductPreviewPage().submit();
        loanTestHelper.createLoanAccount(clientName, formParameters.getOfferingName());
        loanTestHelper.approveLoan();
        loanTestHelper.disburseLoan(systemDateTime.plusDays(1));
        loanTestHelper.makePayment(systemDateTime.plusDays(10), "10");
        loanTestHelper.setApplicationTime(systemDateTime.plusDays(11)).navigateBack();
        new LoanAccountPage(selenium).navigateToApplyAdjustment().verifyAdjustBackdatedPermission().cancelAdjustment();
        loanTestHelper.repayLoan(systemDateTime.plusDays(11));
        loanTestHelper.setApplicationTime(systemDateTime.plusDays(12)).navigateBack();
        new LoanAccountPage(selenium).navigateToApplyAdjustment().verifyAdjustBackdatedPermissionOnRepay().cancelAdjustment();
        navigationHelper.navigateToAdminPage().navigateToViewRolesPage().navigateToManageRolePage("Admin").enablePermission("5_1_9").submitAndGotoViewRolesPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void disableSystemInfoPermission() throws Exception {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        changePermission(adminPage, "10_0", false);
        //try to reach System Info page, should fail
        adminPage = adminPage.failNavigationToSystemInfoPage();
        adminPage = navigationHelper.navigateToAdminPage();
        // reverting for other tests to pass
        changePermission(adminPage, "10_0", true);
    }

    private void changePermission(AdminPage adminPage, String permissionValue, boolean enablePermission) {
        ViewRolesPage viewRolesPage;
        ManageRolePage manageRolePage;
        viewRolesPage = adminPage.navigateToViewRolesPage();
        manageRolePage = viewRolesPage.navigateToManageRolePage("Admin");
        manageRolePage.verifyPage();
        if (enablePermission) {
            manageRolePage.enablePermission(permissionValue);
        } else {
            manageRolePage.disablePermission(permissionValue);
        }
        viewRolesPage = manageRolePage.submitAndGotoViewRolesPage();
        viewRolesPage.navigateToAdminPage();
    }
}
