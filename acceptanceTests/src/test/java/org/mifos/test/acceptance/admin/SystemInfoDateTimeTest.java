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

package org.mifos.test.acceptance.admin;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.AdminPage;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.SystemInfoPage;
import org.mifos.test.acceptance.framework.TimeMachine;
import org.mifos.test.acceptance.framework.TimeMachinePage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = { "acceptance", "ui" })
public class SystemInfoDateTimeTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        new TimeMachine(selenium).resetDateTime();
    }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
        new TimeMachine(selenium).resetDateTime();
    }

    public void verifyCurrentDateTimeTest() {
        AdminPage adminPage = loginAndGoToAdminPage();
        SystemInfoPage systemInfoPage = adminPage.navigateToSystemInfoPage();
        systemInfoPage.verifyPage();
        systemInfoPage.verifyDateTime(new DateTime());
    }

    public void verifyUpdatedDateTimeTest() {
        DateTime targetTime = new DateTime(2008,1,1,0,0,0,0);
        TimeMachine timeMachine = new TimeMachine(selenium);
        TimeMachinePage timeMachinePage = timeMachine.setDateTime(targetTime);
        timeMachinePage.verifyPage();
        timeMachinePage.verifySuccess(targetTime);
    }
    
    public void verifyDateTimeTest() {
        DateTime targetTime = new DateTime(2008,1,1,0,0,0,0);
        TimeMachine timeMachine = new TimeMachine(selenium);
        timeMachine.setDateTime(targetTime);
        
        AdminPage adminPage = loginAndGoToAdminPage();
        SystemInfoPage systemInfoPage = adminPage.navigateToSystemInfoPage();
        systemInfoPage.verifyPage();
        systemInfoPage.verifyDateTime(targetTime);
    }

    private AdminPage loginAndGoToAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        AdminPage adminPage = homePage.navigateToAdminPage();
        adminPage.verifyPage();
        return adminPage;
    }

}
