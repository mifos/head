/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class MifosBatchJob extends QuartzJobBean implements StatefulJob {

    public static final String JOB_EXECUTION_TIME_KEY = "executionTime";

    private static boolean batchJobRunning = false;
    private static boolean requiresExclusiveAccess = true;

    private JobLauncher jobLauncher;

    private JobLocator jobLocator;

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public void setJobLocator(JobLocator jobLocator) {
        this.jobLocator = jobLocator;
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            // TODO QUARTZ: Resolve the issue with both quartz and spring batch requiring job name in the declaration.
            String jobName = context.getJobDetail().getName()+"Job";
            Job job = jobLocator.getJob(jobName);
            jobLauncher.run(job, getJobParametersFromContext(context));
        } catch(Exception ex) {
            throw new JobExecutionException(ex);
        }
        // TODO QUARTZ: Add proper support for old task.xml file.
        // getTaskHelper().execute(context.getScheduledFireTime().getTime());
    }

    public JobParameters getJobParametersFromContext(JobExecutionContext context) {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addDate(JOB_EXECUTION_TIME_KEY, context.getScheduledFireTime());
        return builder.toJobParameters();
    }

    /**
     * Classes inheriting from MifosBatchJob must override this method and
     * return an appropriate Helper class containing business logic.
     */
    public TaskHelper getTaskHelper() {
        return null;
    }

    /**
     * This method determines if users can continue to use the system while this task/batch job is running.
     * <br />
     * Override this method and return false it exclusive access is not necessary.
     */
    public void requiresExclusiveAccess() {
        MifosBatchJob.batchJobRequiresExclusiveAccess(true);
    }

    public static boolean isBatchJobRunning() {
        return batchJobRunning;
    }

    public static boolean isBatchJobRunningThatRequiresExclusiveAccess() {
        return batchJobRunning && requiresExclusiveAccess;
    }

    public static void batchJobStarted() {
        batchJobRunning = true;
        requiresExclusiveAccess = true;
    }

    public static void batchJobFinished() {
        batchJobRunning = false;
    }

    public static Boolean isExclusiveAccessRequired() {
        return requiresExclusiveAccess;
    }

    public static void batchJobRequiresExclusiveAccess(Boolean setting) {
        requiresExclusiveAccess = setting;
    }

}
