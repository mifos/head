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
import org.junit.Assert;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.ViewAccountingDataDetailPage;
import org.mifos.test.acceptance.framework.admin.ViewAccountingExportsPage;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"admin", "acceptance","ui"})
public class AccountingIntegrationTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "REST_API_20110912_dbunit.xml", dataSource, selenium);
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2012, 12, 4, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAccountingExportsWorkFlow() throws Exception {
        AdminPage adminPage = loginAndGoToAdminPage();
        ViewAccountingExportsPage viewAccountingExportsPage = adminPage.navigateToViewAccountingExports();
        viewAccountingExportsPage.verifyPage();
        viewAccountingExportsPage.clickCancel();
        adminPage.verifyPage();
        viewAccountingExportsPage = adminPage.navigateToViewAccountingExports();
        viewAccountingExportsPage.clickClearExports();
        viewAccountingExportsPage.verifyConfirmationPage();
        viewAccountingExportsPage.clickSubmit();
        viewAccountingExportsPage.verifyPage();
        viewAccountingExportsPage.verifyNoExportPresent();
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAccountingExportsSavignsAccount() throws Exception{
        AdminPage adminPage = loginAndGoToAdminPage();
        ViewAccountingExportsPage viewAccountingExportsPage = adminPage.navigateToViewAccountingExports();
        viewAccountingExportsPage = viewAccountingExportsPage.clickClearExports().clickSubmit();
        ViewAccountingDataDetailPage viewAccountingDataDetail = viewAccountingExportsPage.navigateToViewAccountingDataDetail("2011-09-12");
        if (!viewAccountingDataDetail.verifyIfListContains("Mandatory Savings Accounts")){
            Assert.fail("Accounting Export Details should contain Mandatory Savings Accounts row.");
        }
    }

    private AdminPage loginAndGoToAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        AdminPage adminPage = homePage.navigateToAdminPage();
        adminPage.verifyPage();
        return adminPage;
    }
}
