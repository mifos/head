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

package org.mifos.application.admin.servicefacade;

import java.util.Date;

public class BatchjobsDto {
    private final String name;
    private final String cron;
    private final int priority;
    private final String lastRunStatus;
    private final long lastStartTime;
    private final long nextStartTime;
    private final String taskDependency;
    private final boolean active;

    public BatchjobsDto(String name, String cron, int priority, String lastRunStatus, Date lastStartTime,
            Date nextStartTime, String taskDependency, boolean active) {
        this.name = name;
        this.cron = cron;
        this.priority = priority;
        this.lastRunStatus = lastRunStatus;
        this.lastStartTime = lastStartTime.getTime();
        this.nextStartTime = nextStartTime.getTime();
        this.taskDependency = taskDependency;
        this.active = active;
    }

    public String getName() {
        return this.name;
    }

    public String getCron() {
        return this.cron;
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

    public Date getNextStartTime() {
        return new Date(this.nextStartTime);
    }

    public String getTaskDependency() {
        return this.taskDependency;
    }

    public boolean isActive() {
        return this.active;
    }

}
