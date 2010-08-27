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

package org.mifos.application.admin.business.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.mifos.application.admin.servicefacade.BatchjobsDto;
import org.mifos.application.admin.servicefacade.BatchjobsSchedulerDto;
import org.mifos.application.admin.servicefacade.BatchjobsServiceFacade;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public class BatchjobsServiceFacadeWebTier implements BatchjobsServiceFacade{
    private final String CRON_TRIGGER = "CronTrigger";
    private final String SIMPLE_TRIGGER = "SimpleTrigger";
    private final String SCHEDULER_SUSPEND = "Suspend";
    private final String SCHEDULER_ACTIVATE = "Activate";

    @Override
    public List<BatchjobsDto> getBatchjobs(ServletContext context) throws TaskSystemException, FileNotFoundException, IOException, SchedulerException {
        List<BatchjobsDto> batchjobs = new ArrayList<BatchjobsDto>();
        MifosScheduler mifosScheduler = (MifosScheduler) context.getAttribute(MifosScheduler.class.getName());
        Scheduler scheduler = mifosScheduler.getScheduler();

        for (String groupName : scheduler.getJobGroupNames()) {
            for (String jobName : scheduler.getJobNames(groupName)) {
                Trigger[] triggers = scheduler.getTriggersOfJob(jobName, groupName);
                Trigger trigger = triggers.length > 0 ? triggers[0] : null;
                JobDetail jobDetail = scheduler.getJobDetail(jobName, groupName);
                if (trigger != null && jobDetail != null) {
                    Date nextFire = trigger.getNextFireTime() != null ? trigger.getNextFireTime() : new Date(0);
                    Date lastFire = trigger.getPreviousFireTime() != null ? trigger.getPreviousFireTime() : new Date(0);
                    int priority = trigger.getPriority();
                    String frequency = "";
                    String taskType = "";
                    if (trigger.getClass().getSimpleName().equals(CRON_TRIGGER)) {
                        frequency = ((CronTrigger) trigger).getCronExpression();
                        taskType = CRON_TRIGGER;
                    }
                    if ((trigger.getClass().getSimpleName().equals(SIMPLE_TRIGGER))) {
                        frequency = Long.toString(((SimpleTrigger) trigger).getRepeatInterval());
                        taskType = SIMPLE_TRIGGER;
                    }
                    String description = jobDetail.getDescription() != null ? jobDetail.getDescription() : "";
                    int triggerState = scheduler.getTriggerState(trigger.getName(), groupName);
                    batchjobs.add(new BatchjobsDto(jobName, frequency, taskType, priority, description, lastFire, nextFire, triggerState));
                }
            }
        }
        return batchjobs;
    }

    @Override
    public BatchjobsSchedulerDto getBatchjobsScheduler(ServletContext context) throws Exception {
        MifosScheduler mifosScheduler = (MifosScheduler) context.getAttribute(MifosScheduler.class.getName());
        Scheduler scheduler = mifosScheduler.getScheduler();
        BatchjobsSchedulerDto batchjobsScheduler = new BatchjobsSchedulerDto(scheduler.isInStandbyMode());
        return batchjobsScheduler;
    }

    @Override
    public void suspend(ServletContext context, String doSuspend) throws SchedulerException {
        MifosScheduler mifosScheduler = (MifosScheduler) context.getAttribute(MifosScheduler.class.getName());
        Scheduler scheduler = mifosScheduler.getScheduler();
        if (doSuspend.equals(SCHEDULER_SUSPEND) && scheduler.isInStandbyMode()) {
            scheduler.standby();
        }
        if (doSuspend.equals(SCHEDULER_ACTIVATE) && !scheduler.isInStandbyMode()) {
            scheduler.start();
        }
    }

    @Override
    public void runSelectedTasks() {
        // TODO Auto-generated method stub

    }

}
