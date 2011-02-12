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

package org.mifos.framework.components.batchjobs.persistence;


import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.util.ConfigurationLocator;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.core.io.ClassPathResource;

public class LoanArrearsAndPortfolioAtRiskIntegrationTest extends MifosIntegrationTestCase {

    MifosScheduler mifosScheduler;

    String jobName;

    @Before
    public void setUp() throws Exception {

        jobName = "LoanArrearsAndPortfolioAtRiskTaskJob";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLoanArrearsTaskRunSuccessfull() throws Exception {
        mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/loanArrearsAndPortfolioTask.xml");
        mifosScheduler.runIndividualTask(jobName);
        Thread.sleep(2000);
        JobExplorer explorer = mifosScheduler.getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 10);
        Assert.assertTrue(jobInstances.size() > 0);
        JobInstance lastInstance = jobInstances.get(0);
        List<JobExecution> jobExecutions = explorer.getJobExecutions(lastInstance);
        Assert.assertEquals(1, jobExecutions.size());
        JobExecution lastExecution = jobExecutions.get(0);
        Assert.assertEquals(BatchStatus.COMPLETED, lastExecution.getStatus());
        Collection<StepExecution> stepExecutions = lastExecution.getStepExecutions();
        Assert.assertEquals(2, stepExecutions.size());
        for(StepExecution stepExecution : stepExecutions) {
            Assert.assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());
        }
    }

    @Test
    public void testLoanArrearsTaskRunFailed() throws Exception {
        mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/loanArrearsAndPortfolioTask2.xml");
        mifosScheduler.runIndividualTask(jobName);
        Thread.sleep(1000);
        JobExplorer explorer = mifosScheduler.getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 10);
        Assert.assertTrue(jobInstances.size() > 0);
        JobInstance lastInstance = jobInstances.get(0);
        List<JobExecution> jobExecutions = explorer.getJobExecutions(lastInstance);
        Assert.assertEquals(1, jobExecutions.size());
        JobExecution lastExecution = jobExecutions.get(0);
        Assert.assertEquals(BatchStatus.FAILED, lastExecution.getStatus());
        Collection<StepExecution> stepExecutions = lastExecution.getStepExecutions();
        Assert.assertEquals(1, stepExecutions.size());
        for(StepExecution stepExecution : stepExecutions) {
            Assert.assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        }
    }

    private MifosScheduler getMifosScheduler(String taskConfigurationPath) throws TaskSystemException, IOException, FileNotFoundException {
        ConfigurationLocator mockConfigurationLocator = createMock(ConfigurationLocator.class);
        expect(mockConfigurationLocator.getFile(SchedulerConstants.CONFIGURATION_FILE_NAME)).andReturn(
                new ClassPathResource(taskConfigurationPath).getFile());
        expectLastCall().times(2);
        replay(mockConfigurationLocator);
        MifosScheduler mifosScheduler = new MifosScheduler();
        mifosScheduler.setConfigurationLocator(mockConfigurationLocator);
        mifosScheduler.initialize();
        return mifosScheduler;
    }

}
