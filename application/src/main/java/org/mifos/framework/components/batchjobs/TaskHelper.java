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

import java.util.Date;

import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class TaskHelper implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(TaskHelper.class);

    protected Logger getLogger() {
        return logger;
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
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        checkHibernateSession();
        Date scheduledFireTime = new Date(chunkContext.getStepContext().getStepExecution().getJobParameters().getLong(MifosBatchJob.JOB_EXECUTION_TIME_KEY));
        try {
            execute(scheduledFireTime.getTime());
        }
        catch (BatchJobException ex) {
            logger.error("Exception during task execution", ex);
            contribution.setExitStatus(ExitStatus.FAILED.addExitDescription(ex.getErrorMessage()));
        }
        return RepeatStatus.FINISHED;
    }

    private void checkHibernateSession() {
        try {
            StaticHibernateUtil.closeSession();
            StaticHibernateUtil.getSessionTL().beginTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.closeSession();
        }
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
