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

package org.mifos.application.admin.servicefacade;

import java.util.Date;

@SuppressWarnings("PMD.ExcessiveParameterList")
public class BatchjobsDto {
    private final String name;
    private final String frequency;
    private final String taskType;
    private final int priority;
    private final String lastRunStatus;
    private final long lastStartTime;
    private final long lastSuccessfulRun;
    private final long nextStartTime;
    private final int state;
    private final String failDescription;


    public BatchjobsDto(String name, String frequency, String taskType, int priority, String lastRunStatus, Date lastStartTime,
            Date lastSuccessfulRun, Date nextStartTime, int state, String failDescription) {
        this.name = name;
        this.frequency = frequency;
        this.taskType = taskType;
        this.priority = priority;
        this.lastRunStatus = lastRunStatus;
        this.lastStartTime = lastStartTime.getTime();
        this.lastSuccessfulRun = lastSuccessfulRun.getTime();
        this.nextStartTime = nextStartTime.getTime();
        this.state = state;
        this.failDescription = failDescription;
    }

    public String getName() {
        return this.name;
    }

    public String getFrequency() {
        return this.frequency;
    }

    public String getTaskType() {
        return this.taskType;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getLastRunStatus() {
        return this.lastRunStatus;
    }

    public Date getLastStartTime() {
        return new Date(this.lastStartTime);
    }

    public Date getLastSuccessfulRun() {
        return new Date(lastSuccessfulRun);
    }

    public Date getNextStartTime() {
        return new Date(this.nextStartTime);
    }

    public int getState() {
        return this.state;
    }

    public String getFailDescription() {
        return this.failDescription;
    }

}
