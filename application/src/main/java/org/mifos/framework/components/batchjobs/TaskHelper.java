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

import java.util.Date;

import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class TaskHelper implements Tasklet {

    private MifosLogger logger;

    public TaskHelper() {
        this.logger = MifosLogManager.getLogger(LoggerConstants.BATCH_JOBS);
    }

    protected MifosLogger getLogger() {
        return logger;
    }

    protected void setLogger(MifosLogger logger) {
        this.logger = logger;
    }

    /**
     * This methods, performs the job specific to each task. Most batch jobs must be run daily.
     *
     * @param timeInMillis
     *            date the job is being run for. Useful for "catch up", ie: running batch jobs for dates past. Note some
     *            jobs ignore this value.
     */
    public abstract void execute(long timeInMillis) throws BatchJobException;

    @Override
    @SuppressWarnings("unused")
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Date scheduledFireTime = new Date(chunkContext.getStepContext().getStepExecution().getJobParameters().getLong(MifosBatchJob.JOB_EXECUTION_TIME_KEY));
        execute(scheduledFireTime.getTime());
        return RepeatStatus.FINISHED;
    }

    /**
     * This method determines if users can continue to use the system while this task/batch job is running.
     * <p>
     * Override this method and return false it exclusive access is not necessary.
     */
    public void requiresExclusiveAccess() {
        MifosBatchJob.batchJobRequiresExclusiveAccess(true);
    }

}
