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

import java.util.List;

import javax.servlet.ServletContext;


import org.springframework.security.access.prepost.PreAuthorize;

@SuppressWarnings("PMD")
public interface BatchjobsServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    List<BatchjobsDto> getBatchjobs(ServletContext context) throws Exception;

    @PreAuthorize("isFullyAuthenticated()")
    BatchjobsSchedulerDto getBatchjobsScheduler(ServletContext context) throws Exception;

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_UPDATE_BATCH_JOBS_CONFIGURATION')")
    void suspend(ServletContext context, String doSuspend) throws Exception;

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_RUN_BATCH_JOBS_ON_DEMAND')")
    void runSelectedTasks(ServletContext context, String[] rawJobList) throws Exception;

}