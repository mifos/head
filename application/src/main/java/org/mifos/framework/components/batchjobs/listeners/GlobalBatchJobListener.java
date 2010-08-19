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

package org.mifos.framework.components.batchjobs.listeners;

import org.joda.time.DateTime;
import org.mifos.framework.components.batchjobs.BatchJobListener;
import org.mifos.framework.components.batchjobs.MifosBatchJob;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.helpers.TaskStatus;
import org.mifos.framework.util.DateTimeService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class GlobalBatchJobListener extends BatchJobListener {

    private final String name = SchedulerConstants.GLOBAL_JOB_LISTENER_NAME;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        String jobName = context.getJobDetail().getName();
        long currentTime = new DateTimeService().getCurrentDateTime().getMillis();
        registerBatchJobLaunch(jobName, currentTime);
        MifosBatchJob.batchJobStarted();
        ((MifosBatchJob)context.getJobInstance()).requiresExclusiveAccess();
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {
        String jobName = context.getJobDetail().getName();
        long currentTime = new DateTimeService().getCurrentDateTime().getMillis();
        if(exception == null) {
            registerBatchJobResult(jobName, currentTime, SchedulerConstants.FINISHED_SUCCESSFULLY, TaskStatus.COMPLETE);
        } else {
            registerBatchJobResult(jobName, currentTime, exception.getMessage(), TaskStatus.FAILED);
        }
        MifosBatchJob.batchJobFinished();
    }

    public final void registerBatchJobLaunch(String batchJobName, long timeInMillis) {
        DateTime date = new DateTime(timeInMillis);
        String logMessage = "Batch job " + batchJobName + " launched on " + date;
        getLogger().info(logMessage);
    }

    public final void registerBatchJobResult(String batchJobName, long timeInMillis, String description, TaskStatus status) {
        DateTime date = new DateTime(timeInMillis);
        String logMessage = null;
        if(status == TaskStatus.COMPLETE) {
            logMessage = "Batch job " + batchJobName +" completed on " + date + ": " + description;
            getLogger().info(logMessage);
        } else if(status == TaskStatus.FAILED) {
            logMessage = "Batch job " + batchJobName + " FAILED on " + date + ": " + description;
            getLogger().warn(logMessage);
        }
    }

}
