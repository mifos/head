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
import org.joda.time.DateTimeZone;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.TimeMachinePage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.SystemInfoPage;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"admin", "acceptance","ui","no_db_unit"})
public class SystemInfoDateTimeTest extends UiTestCaseBase {

    private AdminTestHelper adminTestHelper;
    
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        adminTestHelper = new AdminTestHelper(selenium);
        new DateTimeUpdaterRemoteTestingService(selenium, applicationDatabaseOperation).resetDateTime();
    }

    @AfterMethod
    public void tearDown() throws SQLException {
        (new MifosPage(selenium)).logout();
        new DateTimeUpdaterRemoteTestingService(selenium, applicationDatabaseOperation).resetDateTime();
    }

    /**
     * Verify current date and time display and that current
     * date and time can be modified using "time machine"
     * http://mifosforge.jira.com/browse/MIFOSTEST-639
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyDateTimeWithTimeMachineModification() throws Exception {
        DateTime targetTime = new DateTime(2008,1,1,0,0,0,0);
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium, applicationDatabaseOperation);

        TimeMachinePage timeMachinePage = dateTimeUpdaterRemoteTestingService.setDateTimeWithMifosLastLoginUpdate(targetTime);
        timeMachinePage.verifySuccess(targetTime);

        SystemInfoPage systemInfoPage = adminTestHelper.navigateToSystemInfoPage();
        systemInfoPage.verifyDateTime(targetTime);
    }

    public void verifyDateTimeAndTimeZone() throws UnsupportedEncodingException, SQLException {
        DateTimeZone dateTimeZone = DateTimeZone.forOffsetHours(1);
        DateTime targetTime = new DateTime(2008,1,1,0,0,0,0);
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium, applicationDatabaseOperation);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime, dateTimeZone);

        SystemInfoPage systemInfoPage = adminTestHelper.navigateToSystemInfoPage();
        systemInfoPage.verifyDateTime(targetTime);
    }
}
