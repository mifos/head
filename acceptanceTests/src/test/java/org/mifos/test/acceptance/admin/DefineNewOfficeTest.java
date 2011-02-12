/*
 * Copyright Grameen Foundation USA
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

import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.DefineNewOfficePage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.framework.util.DbUnitUtilities;


@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"admin","acceptance","ui", "smoke"})
public class DefineNewOfficeTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @Test
    public void defineNewOfficeWithExistingName() {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineNewOfficePage defineNewOfficePage = adminPage.navigateToDefineANewOfficePage();

        defineNewOfficePage.setOfficeName("MyOffice1232993831593");
        defineNewOfficePage.setOfficeShortName("AAA");
        defineNewOfficePage.setOfficeType("Branch Office");
        defineNewOfficePage.setParentOffice("Head Office(Mifos HO )");

        defineNewOfficePage.preview();
        defineNewOfficePage.submit();

        assertTextFoundOnPage("This office name already exist");
    }
}
