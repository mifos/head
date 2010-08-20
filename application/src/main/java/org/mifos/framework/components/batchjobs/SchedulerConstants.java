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

public interface SchedulerConstants {

    String CONFIGURATION_FILE_NAME = "task.xml";
    String SCHEDULER_CONFIGURATION_FILE_NAME = "quartz.properties";

    String SCHEDULER_TASKS = "scheduler-tasks";
    String SCHEDULER = "scheduler";
    String TASK_CLASS_NAME = "task-class-name";
    String TASK_GROUP_NAME = "task-group-name";
    String INITIAL_TIME = "initial-time";
    String DELAY_TIME = "delay-time";

    String SCHEDULER_CONFIG = "scheduler-config";
    String GLOBAL_JOB_LISTENERS = "global-job-listeners";
    String GLOBAL_TRIGGER_LISTENERS = "global-trigger-listeners";
    String LISTENER = "listener";
    String SCHEDULE = "schedule";
    String BATCH_JOB = "batch-job";
    String JOB_NAME = "job-name";
    String JOB_GROUP_NAME = "job-group-name";
    String JOB_PRIORITY = "job-priority";
    String CRON_EXPRESSION = "cron-expression";
    String JOB_LISTENERS = "job-listeners";
    String TRIGGER_LISTENERS = "trigger-listeners";

    String START = "Start";
    String FINISHED_SUCCESSFULLY = "Finished Successfully";
    String FAILURE = "Failure";

    String GLOBAL_JOB_LISTENER_NAME = "GlobalBatchJobListener";
    String GLOBAL_TRIGGER_LISTENER_NAME = "GlobalTriggerListener";

}
