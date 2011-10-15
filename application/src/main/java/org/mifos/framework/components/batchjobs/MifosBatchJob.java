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

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.util.DateTimeService;
import org.quartz.CronTrigger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.StatefulJob;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class MifosBatchJob extends QuartzJobBean implements StatefulJob {

    /** A key to store and retrieve the launch date from JobParameters map. */
    public static final String JOB_EXECUTION_TIME_KEY = "executionTime";

    private static boolean batchJobRunning = false;
    private static boolean requiresExclusiveAccess = true;

    private JobLauncher jobLauncher;
    private JobLocator jobLocator;
    private JobExplorer jobExplorer;
    private JobRepository jobRepository;

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public void setJobLocator(JobLocator jobLocator) {
        this.jobLocator = jobLocator;
    }

    public void setJobExplorer(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    public void setJobRepository(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            String jobName = context.getJobDetail().getName();
            Job job = jobLocator.getJob(jobName);
            catchUpMissedLaunches(job, context);
            checkAndLaunchJob(job, getJobParametersFromContext(context), 0);
        } catch(Exception ex) {
            throw new JobExecutionException(ex);
        }
    }

    /**
     * A method responsible for the actual launch of the Spring Batch job.
     * @param job Job class
     * @param jobParameters Job parameters
     * @return Batch computation status
     * @throws BatchJobException when something goes wrong
     */
    private BatchStatus launchJob(Job job, JobParameters jobParameters) throws BatchJobException {
        BatchStatus exitStatus = BatchStatus.UNKNOWN;
        JobExecution jobExecution = null;
        try {
            batchJobStarted();
            requiresExclusiveAccess();
            jobExecution = jobLauncher.run(job, jobParameters);
            exitStatus = jobExecution.getStatus();
        } catch(JobInstanceAlreadyCompleteException jiace) {
            exitStatus = BatchStatus.COMPLETED;
            return exitStatus;
        } catch(Exception ex) {
            throw new BatchJobException(ex);
        } finally {
            batchJobFinished();
        }
        return exitStatus;
    }

    /**
     * This method is a wrapper around launchJob method. It checks whether previous
     * runs of the job executed successfully and attempts to re-run them in case they did not.
     * @param job Job class
     * @param jobParameters Job parameters
     * @param lookUpDepth Counter used to track current recurrence depth
     * @return Batch computation status
     * @throws BatchJobException when something goes wrong
     */
    public BatchStatus checkAndLaunchJob(Job job, JobParameters jobParameters, int lookUpDepth) throws BatchJobException {
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(job.getName(), lookUpDepth, lookUpDepth+1);
        if(jobInstances.size() == 0) {
            return launchJob(job, jobParameters);
        }
        JobInstance jobInstance = jobInstances.get(0);
        List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
        JobExecution jobExecution = jobExecutions.get(0); // latest execution
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            return launchJob(job, jobParameters);
        }
        checkAndLaunchJob(job, jobExecution.getJobInstance().getJobParameters(), lookUpDepth+1);
        return launchJob(job, jobParameters);
    }

    public void catchUpMissedLaunches(Job job, JobExecutionContext context) throws Exception {
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(job.getName(), 0, 1);
        if(jobInstances.size() > 0) {
            JobInstance jobInstance = jobInstances.get(0);
            Date previousFireTime = new Date(jobInstance.getJobParameters().getLong(JOB_EXECUTION_TIME_KEY));
            Date scheduledFireTime = context.getScheduledFireTime();
            Trigger trigger = context.getTrigger();
            boolean onDemandRun = false;
            if (Scheduler.DEFAULT_MANUAL_TRIGGERS.equals(trigger.getGroup())) { // this is a manual run
                trigger = context.getScheduler().getTrigger(job.getName(), Scheduler.DEFAULT_GROUP);
                scheduledFireTime = new DateTimeService().getCurrentDateTime().toDate();
                onDemandRun = true;
            }
            List<Date> missedLaunches = computeMissedJobLaunches(previousFireTime, scheduledFireTime, trigger, onDemandRun);
            for(Date missedLaunch : missedLaunches) {
                JobParameters jobParameters = createJobParameters(missedLaunch.getTime());
                launchJob(job, jobParameters);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<Date> computeMissedJobLaunches(Date from, Date to, Trigger trigger, boolean onDemandRun) throws Exception {
        List<Date> missedLaunches = new LinkedList<Date>();
        if(trigger instanceof CronTrigger) {
            CronTrigger cronTrigger = new CronTrigger();
            cronTrigger.setStartTime(from);
            cronTrigger.setNextFireTime(from);
            String cronExpression = ((CronTrigger)trigger).getCronExpression();
            try {
                cronTrigger.setCronExpression(cronExpression);
            } catch(ParseException pe) {
                throw new Exception(pe);
            }
            List<Date> computationOutcome = TriggerUtils.computeFireTimesBetween(cronTrigger, null, from, to);
            missedLaunches.addAll(computationOutcome);
            missedLaunches.remove(0);
            if (!onDemandRun && missedLaunches.size() > 0) {
                missedLaunches.remove(missedLaunches.size()-1);
            }
        }
        else if (trigger instanceof SimpleTrigger) {
            SimpleTrigger simpleTrigger = new SimpleTrigger();
            simpleTrigger.setStartTime(from);
            simpleTrigger.setNextFireTime(from);
            simpleTrigger.setRepeatInterval(((SimpleTrigger)trigger).getRepeatInterval());
            simpleTrigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
            List<Date> computationOutcome = TriggerUtils.computeFireTimesBetween(simpleTrigger, null, from, to);
            missedLaunches.addAll(computationOutcome);
            missedLaunches.remove(0);
            if (!onDemandRun && missedLaunches.size() > 0) {
                missedLaunches.remove(missedLaunches.size()-1);
            }
        }
        return missedLaunches;
    }

    public static JobParameters getJobParametersFromContext(JobExecutionContext context) {
        JobParametersBuilder builder = new JobParametersBuilder();
        if (Scheduler.DEFAULT_MANUAL_TRIGGERS.equals(context.getTrigger().getGroup())) { // this is a manual run
            builder.addLong(JOB_EXECUTION_TIME_KEY, new DateTimeService().getCurrentDateTime().getMillis());
        }
        else {
            builder.addLong(JOB_EXECUTION_TIME_KEY, context.getScheduledFireTime().getTime());
        }
        return builder.toJobParameters();
    }

    public static JobParameters createJobParameters(long scheduledLaunchTime) {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addLong(JOB_EXECUTION_TIME_KEY, scheduledLaunchTime);
        return builder.toJobParameters();
    }

    /**
     * Classes inheriting from MifosBatchJob must override this method and
     * return an appropriate Helper class containing business logic.
     *
     * @return Helper class containing business logic
     */
    public abstract TaskHelper getTaskHelper();

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
