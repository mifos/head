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
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.TimeMachinePage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.SystemInfoPage;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mifos.test.acceptance.util.FileUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"admin", "acceptance","ui","no_db_unit"})
public class SystemInfoDateTimeTest extends UiTestCaseBase {

    private AdminTestHelper adminTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        adminTestHelper = new AdminTestHelper(selenium);
        new DateTimeUpdaterRemoteTestingService(selenium).resetDateTime();
    }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
        new DateTimeUpdaterRemoteTestingService(selenium).resetDateTime();
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
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);

        TimeMachinePage timeMachinePage = dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        timeMachinePage.verifySuccess(targetTime);

        SystemInfoPage systemInfoPage = adminTestHelper.navigateToSystemInfoPage();
        systemInfoPage.verifyDateTime(targetTime);
    }

    public void verifyDateTimeAndTimeZone() throws UnsupportedEncodingException {
        DateTimeZone dateTimeZone = DateTimeZone.forOffsetHours(1);
        DateTime targetTime = new DateTime(2008,1,1,0,0,0,0);
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime, dateTimeZone);

        SystemInfoPage systemInfoPage = adminTestHelper.navigateToSystemInfoPage();
        systemInfoPage.verifyDateTime(targetTime);
    }
    
	public void verifyDatabaseInformationSource() throws Exception {
        SystemInfoPage systemInfoPage = adminTestHelper.navigateToSystemInfoPage();
        String data = systemInfoPage.getDatabaseSource();
        String [] databaseConfigs = data.substring(1, data.length()-1 ).split(", ");
        List<File> temp = new ArrayList<File>();
        for (String database : databaseConfigs) { 
            File oldFile = new File(database);
            File tempFile = File.createTempFile(oldFile.getName(), ".tmp");
    	    temp.add(tempFile); 
        	if (oldFile.exists()) {
        		FileUtil.copyFile(oldFile,tempFile);
        		oldFile.delete(); 
            }
        }

        SystemInfoPage systemInfoPage2 = adminTestHelper.navigateToSystemInfoPage();
        systemInfoPage2.verifyDatabaseSource("[]");

        Iterator<File> it = temp.iterator();
        for (String database : databaseConfigs) { 
            File newFile = new File(database);
            File tempFile = it.next();
            FileUtil.copyFile(tempFile,newFile);
            tempFile.delete();
        }
    }
}
