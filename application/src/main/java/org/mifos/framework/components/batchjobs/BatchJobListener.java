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

import org.joda.time.DateTime;
import org.mifos.framework.components.batchjobs.helpers.TaskStatus;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class BatchJobListener implements JobExecutionListener {

    private MifosLogger logger;

    public BatchJobListener() {
        this.logger = MifosLogManager.getLogger(LoggerConstants.BATCH_JOBS);
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        long launchTime = jobExecution.getStartTime().getTime();
        registerBatchJobLaunch(jobName, launchTime);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        long finishTime = jobExecution.getEndTime().getTime();
        if(jobExecution.getAllFailureExceptions().isEmpty()) {
            registerBatchJobResult(jobName, finishTime, SchedulerConstants.FINISHED_SUCCESSFULLY, TaskStatus.COMPLETE);
        }
        else {
            StringBuilder builder = new StringBuilder();
            for(Throwable cause : jobExecution.getAllFailureExceptions()) {
                builder.append("[");
                builder.append(cause.getMessage());
                builder.append("], ");
            }
            builder.deleteCharAt(builder.lastIndexOf(","));
            registerBatchJobResult(jobName, finishTime, builder.toString(), TaskStatus.FAILED);
        }
    }

    public void registerBatchJobLaunch(String batchJobName, long timeInMillis) {
        DateTime date = new DateTime(timeInMillis);
        String logMessage = "Batch job " + batchJobName + " launched on " + date;
        getLogger().info(logMessage);
    }

    public void registerBatchJobResult(String batchJobName, long timeInMillis, String description, TaskStatus status) {
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

    protected MifosLogger getLogger() {
        return logger;
    }

    protected void setLogger(MifosLogger logger) {
        this.logger = logger;
    }

}
