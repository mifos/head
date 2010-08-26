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
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class BatchjobsServiceFacadeWebTier implements BatchjobsServiceFacade{

    @Override
    public List<BatchjobsDto> getBatchjobs(ServletContext context) throws TaskSystemException, FileNotFoundException, IOException, SchedulerException {
        List<BatchjobsDto> batchjobs = new ArrayList<BatchjobsDto>();
        MifosScheduler mifosScheduler = (MifosScheduler) context.getAttribute(MifosScheduler.class.getName());
        Scheduler scheduler = mifosScheduler.getScheduler();

        for (String mifosTaskName : mifosScheduler.getTaskNames()) {
            Trigger trigger = null;
            JobDetail jobDetail = null;

            int triggerState = 0;
            for(String group : scheduler.getJobGroupNames()) {
                for(String triggerName : scheduler.getTriggerNames(group)) {
                    if (triggerName.equals(mifosTaskName+"Trigger")) {
                        trigger = scheduler.getTrigger(triggerName, group);
                        triggerState = scheduler.getTriggerState(triggerName, group);
                        break;
                    }
                }
                for(String jobDetailsName : scheduler.getJobNames(group)) {
                    if (jobDetailsName.equals(mifosTaskName)) {
                        jobDetail = scheduler.getJobDetail(jobDetailsName, group);
                        break;
                    }
                }

            }

            if (trigger != null && jobDetail != null) {
                Date nextFire = trigger.getNextFireTime() != null ? trigger.getNextFireTime() : new Date(0);
                Date lastFire = trigger.getPreviousFireTime() != null ? trigger.getPreviousFireTime() : new Date(0);
                int priority = trigger.getPriority();
                String cronExpression = trigger.getClass().getSimpleName().equals("CronTrigger") ? ((CronTrigger) trigger).getCronExpression() : "";
                String description = jobDetail.getDescription() != null ? jobDetail.getDescription() : "";
                batchjobs.add(new BatchjobsDto(mifosTaskName, cronExpression, priority, description, lastFire, nextFire, "", triggerState));
            }
        }
        return batchjobs;
    }

    @Override
    public BatchjobsSchedulerDto getBatchjobsScheduler() throws Exception {
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        BatchjobsSchedulerDto batchjobsScheduler = new BatchjobsSchedulerDto(scheduler.isInStandbyMode());
        return batchjobsScheduler;
    }

    @Override
    public void suspend() {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveChanges() {
        // TODO Auto-generated method stub

    }

    @Override
    public void runSelectedTasks() {
        // TODO Auto-generated method stub

    }

}
