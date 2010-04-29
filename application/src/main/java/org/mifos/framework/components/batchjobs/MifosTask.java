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

import java.util.TimerTask;

public abstract class MifosTask extends TimerTask {

    private static boolean batchJobRunning = false;
    private static boolean requiresExclusiveAccess = true;

    public TaskHelper helper;

    /**
     * Name of the task same as specified in XML
     */
    public String name;

    /**
     * Attribute which determines if the job is a regular DB update job to be
     * run daily and hence registereed in the database or a user requested job.
     * [ if true : job should run daily false : user requested job ]
     */
    public boolean normal;

    /**
     * An alternate set of parameters which could be set while the task is
     * created.This constitutes input to the MifosTask while executing.
     */
    public Object params;

    public MifosTask() {
    }

    @Override
    public final void run() {
        helper = getTaskHelper();
        helper.executeTask();
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

    public abstract TaskHelper getTaskHelper();
}
