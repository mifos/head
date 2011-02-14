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

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.BatchJobsPage;
import org.mifos.test.acceptance.framework.util.UiTestUtils;

import com.thoughtworks.selenium.Selenium;

public class BatchJobHelper {

    private final BatchJobsPage batchJobsPage;

    public BatchJobHelper(Selenium selenium) {
        AdminPage adminPage = new AdminPage(selenium);
        batchJobsPage = adminPage.navigateToBatchJobsPage();
    }

    public void runAllBatchJobs() {
       //TODO Use more elegant means to select all jobs in the batch job page

       List<String> jobs = new ArrayList<String>();
       jobs.add("ApplyHolidayChangesTaskJob");
       jobs.add("SavingsIntPostingTaskJob");
       jobs.add("LoanArrearsAgingTaskJob");
       jobs.add("ApplyCustomerFeeChangesTaskJob");
       jobs.add("BranchReportTaskJob");
       jobs.add("LoanArrearsAndPortfolioAtRiskTaskJob");
       jobs.add("ProductStatusJob");
       jobs.add("GenerateMeetingsForCustomerAndSavingsTaskJob");

//       String[] fields = selenium.getAllFields();
//       for (String field: fields){
//           if (field.endsWith("TaskJob")){
//               jobs.add(field);
//           }
//       }

       runSomeBatchJobs(jobs);

    }

    public void runSomeBatchJobs(List<String> jobsToRun) {
        for (String name : jobsToRun) {
            batchJobsPage.selectBatchJob(name);
        }
        batchJobsPage.runSelectedBatchJobs();
        // TODO we should check if the batch job is finished on the batch jobs page. For now we give 2 seconds fot the batch job to finish
        UiTestUtils.sleep(2000);
    }

}
