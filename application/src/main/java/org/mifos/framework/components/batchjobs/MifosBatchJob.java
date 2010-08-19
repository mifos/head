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

import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public abstract class MifosBatchJob implements StatefulJob {

    private static boolean batchJobRunning = false;
    private static boolean requiresExclusiveAccess = true;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            getTaskHelper().execute(context.getScheduledFireTime().getTime());
        } catch (BatchJobException bje) {
            throw new JobExecutionException(bje);
        }
    }

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
