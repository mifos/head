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

package org.mifos.test.acceptance.framework.testhelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.BatchJobsPage;
import org.mifos.test.acceptance.framework.util.UiTestUtils;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class BatchJobHelper {

    private static final int WAITING_TIME = 10000; // 10 seconds
    private static final int NUMBER_OF_TRIES = 60; // 60 * 10 seconds = 10 minutes

    private final BatchJobsPage batchJobsPage;
    private final AdminPage adminPage;
    private final AppLauncher appLauncher;
    private final Selenium selenium;

    public BatchJobHelper(Selenium selenium) {
        this.selenium = selenium;
        appLauncher = new AppLauncher(selenium);
        adminPage = new AdminPage(selenium);
        batchJobsPage = adminPage.navigateToBatchJobsPage();
    }

    public void runAllBatchJobs() {
        List<String> jobs = new ArrayList<String>();
        int rowCount = selenium.getXpathCount("//div/span[3]/strong").intValue();

        int idx = 2;
        for (int i = 0; i < rowCount; ++i) {
            jobs.add(selenium.getText( "//div[" + idx + "]/span[3]/strong" ));
            idx += 4;
        }

        runSomeBatchJobs(jobs);
    }

    public void runSomeBatchJobs(List<String> jobsToRun) {
        Map<String, String> previousRuns = new HashMap<String, String>();

        for (String name : jobsToRun) {
            previousRuns.put(name, batchJobsPage.getPreviousRunStart(name));
            batchJobsPage.selectBatchJob(name);
        }

        batchJobsPage.runSelectedBatchJobs();

        int counter = 0;
        while (++counter <= NUMBER_OF_TRIES) {
            if (checkBatchJobsHaveFinished(previousRuns)) {
                break;
            }
        }

        if (counter > NUMBER_OF_TRIES) {
            Assert.assertEquals(previousRuns.keySet(), Collections.EMPTY_SET, "Not all batch jobs were completed: " + previousRuns.size());
        }
    }

    @SuppressWarnings("PMD")
    private boolean checkBatchJobsHaveFinished(Map<String, String> previousRuns) {
        (new MifosPage(selenium)).logout();
        UiTestUtils.sleep(WAITING_TIME);

        LoginPage loginPage = appLauncher.launchMifos();
        loginPage.tryLoginUsingDefaultCredentials();
        if (!selenium.isElementPresent("//span[@id='page.id']") || "Login".equals(selenium.getAttribute("page.id@title"))) {
            return false;
        } else {
            Assert.assertEquals(selenium.getAttribute("page.id@title"), "Home");
        }

        HomePage homePage = new HomePage(selenium);
        homePage.tryNavigateToAdminPage();
        if ("Login".equals(selenium.getAttribute("page.id@title"))) {
            return false;
        } else {
            Assert.assertEquals(selenium.getAttribute("page.id@title"), AdminPage.PAGE_ID);
        }

        adminPage.tryNavigateToBatchJobsPage();
        if (selenium.isElementPresent("//span[@id='page.id']")) { // TODO Batch Jobs page do not have page.id!
            if ("Login".equals(selenium.getAttribute("page.id@title"))) {
                return false;
            } else {
                Assert.assertTrue(false, "Expected Batch Jobs page, but was: " + selenium.getAttribute("page.id@title"));
            }
        }

        List<String> completedJobs = new ArrayList<String>();

        for (Map.Entry<String, String> entry : previousRuns.entrySet()) {
            if (entry.getValue().equals(batchJobsPage.getPreviousRunStart(entry.getKey()))) {
                for (String job : completedJobs) {
                    previousRuns.remove(job);
                }
                return false;
            }
            Assert.assertEquals(batchJobsPage.getPreviousRunStatus(entry.getKey()), "Previous run status:  Completed");
            completedJobs.add(entry.getKey());
        }

        return true;
    }

}
