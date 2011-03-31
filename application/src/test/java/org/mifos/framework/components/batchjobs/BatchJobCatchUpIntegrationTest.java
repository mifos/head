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

package org.mifos.framework.components.batchjobs;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.core.MifosResourceUtil;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.components.batchjobs.helpers.ProductStatus;
import org.mifos.framework.util.ConfigurationLocator;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;

@Ignore
public class BatchJobCatchUpIntegrationTest extends MifosIntegrationTestCase {

    MifosScheduler mifosScheduler;

    String jobName;

    public BatchJobCatchUpIntegrationTest() throws Exception {
        super();
    }

    @Before
    public void setUp() throws Exception {
        jobName = "ProductStatusJob";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIncompleteTaskDelay() throws Exception {
        mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/catchUpTask.xml");
        Scheduler scheduler = mifosScheduler.getScheduler();
        ProductStatus productStatusTask = new ProductStatus();
        productStatusTask.setJobExplorer(mifosScheduler.getBatchJobExplorer());
        productStatusTask.setJobLauncher(mifosScheduler.getBatchJobLauncher());
        productStatusTask.setJobLocator(mifosScheduler.getBatchJobLocator());
        productStatusTask.setJobRepository(mifosScheduler.getBatchJobRepository());
        String quartzJobName = "ProductStatusJob";
        String quartzTriggerName = "ProductStatusTrigger2";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 10);
        Date previousFireTime = calendar.getTime();
        calendar.set(Calendar.SECOND, 21);
        Date quartzFireTime = calendar.getTime();
        calendar.set(Calendar.SECOND, 22);
        Date quartzNextFireTime = calendar.getTime();
        calendar.set(Calendar.SECOND, 20);
        Date quartzPrevFireTime = calendar.getTime();
        JobDetail jobDetail = scheduler.getJobDetail(quartzJobName, Scheduler.DEFAULT_GROUP);
        jobDetail.setJobDataMap(new JobDataMap());
        CronTrigger trigger = new CronTrigger(quartzTriggerName, Scheduler.DEFAULT_GROUP, quartzJobName, Scheduler.DEFAULT_GROUP, "* * * * * ?");
        trigger.setJobDataMap(new JobDataMap());
        TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(
                                    jobDetail, trigger,
                                    new BaseCalendar(), false,
                                    quartzFireTime, quartzFireTime,
                                    quartzPrevFireTime, quartzNextFireTime);
        JobExecutionContext jobExecutionContext = new JobExecutionContext(scheduler, triggerFiredBundle, productStatusTask);

        JobLauncher jobLauncher = mifosScheduler.getBatchJobLauncher();
        JobLocator jobLocator = mifosScheduler.getBatchJobLocator();
        jobLauncher.run(jobLocator.getJob(jobName), MifosBatchJob.createJobParameters(previousFireTime.getTime()));
        Thread.sleep(1500);
        productStatusTask.catchUpMissedLaunches(jobLocator.getJob(jobName), jobExecutionContext);

        JobExplorer explorer = mifosScheduler.getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 20);
        Assert.assertEquals(11, jobInstances.size());
        for(JobInstance jobInstance : jobInstances) {
            List<JobExecution> jobExecutions = explorer.getJobExecutions(jobInstance);
            Assert.assertEquals(BatchStatus.COMPLETED, jobExecutions.get(0).getStatus());
            Assert.assertEquals(calendar.getTimeInMillis(), jobInstance.getJobParameters().getLong(MifosBatchJob.JOB_EXECUTION_TIME_KEY));
            calendar.roll(Calendar.SECOND, false);
        }
    }

    @Test
    public void testIncompleteTaskHandling() throws Exception {
        mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/catchUpTask2.xml");
        JobLauncher jobLauncher = mifosScheduler.getBatchJobLauncher();
        JobLocator jobLocator = mifosScheduler.getBatchJobLocator();

        for(int i = 0; i < 3; i++) {
            jobLauncher.run(jobLocator.getJob(jobName), MifosBatchJob.createJobParameters(new Date().getTime()));
            Thread.sleep(1500);
        }

        JobExplorer explorer = mifosScheduler.getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 10);
        Assert.assertEquals(3, jobInstances.size());
        for(JobInstance jobInstance : jobInstances) {
            List<JobExecution> jobExecutions = explorer.getJobExecutions(jobInstance);
            Assert.assertEquals(1, jobExecutions.size());
            Assert.assertEquals(BatchStatus.FAILED, jobExecutions.get(0).getStatus());
        }

        mifosScheduler.runIndividualTask(jobName);
        Thread.sleep(5000);

        jobInstances = explorer.getJobInstances(jobName, 0, 10);
        Assert.assertEquals(4, jobInstances.size());
        for(JobInstance jobInstance : jobInstances) {
            List<JobExecution> jobExecutions = explorer.getJobExecutions(jobInstance);
            Assert.assertEquals(BatchStatus.COMPLETED, jobExecutions.get(0).getStatus());
        }
    }

    @Test
    public void testFailureDuringIncompleteTaskHandling() throws Exception {
        mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/catchUpTask3.xml");
        JobLauncher jobLauncher = mifosScheduler.getBatchJobLauncher();
        JobLocator jobLocator = mifosScheduler.getBatchJobLocator();

        for(int i = 0; i < 3; i++) {
            jobLauncher.run(jobLocator.getJob(jobName), MifosBatchJob.createJobParameters(new Date().getTime()));
            Thread.sleep(5000);
        }
        JobExplorer explorer = mifosScheduler.getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 10);
        Assert.assertEquals(3, jobInstances.size());
        for(JobInstance jobInstance : jobInstances) {
            List<JobExecution> jobExecutions = explorer.getJobExecutions(jobInstance);
            Assert.assertEquals(1, jobExecutions.size());
            Assert.assertEquals(BatchStatus.FAILED, jobExecutions.get(0).getStatus());
        }

        mifosScheduler.runIndividualTask(jobName);
        Thread.sleep(5000);

        explorer = mifosScheduler.getBatchJobExplorer();
        jobInstances = explorer.getJobInstances(jobName, 0, 10);
        Assert.assertEquals(4, jobInstances.size());
        JobInstance jobInstance = jobInstances.get(0);
        JobExecution jobExecution = explorer.getJobExecutions(jobInstance).get(0);
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        jobInstance = jobInstances.get(1);
        jobExecution = explorer.getJobExecutions(jobInstance).get(0);
        Assert.assertEquals(BatchStatus.FAILED, jobExecution.getStatus());
        Assert.assertEquals(ExitStatus.FAILED, jobExecution.getExitStatus());
        jobInstance = jobInstances.get(2);
        jobExecution = explorer.getJobExecutions(jobInstance).get(0);
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        jobInstance = jobInstances.get(3);
        jobExecution = explorer.getJobExecutions(jobInstance).get(0);
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }

    private MifosScheduler getMifosScheduler(String taskConfigurationPath) throws TaskSystemException, IOException, FileNotFoundException {
        ConfigurationLocator mockConfigurationLocator = createMock(ConfigurationLocator.class);
        expect(mockConfigurationLocator.getResource(SchedulerConstants.CONFIGURATION_FILE_NAME)).andReturn(
                MifosResourceUtil.getClassPathResourceAsResource(taskConfigurationPath));
        expectLastCall().times(2);
        replay(mockConfigurationLocator);
        MifosScheduler mifosScheduler = new MifosScheduler();
        mifosScheduler.setConfigurationLocator(mockConfigurationLocator);
        mifosScheduler.initialize();
        return mifosScheduler;
    }

}
