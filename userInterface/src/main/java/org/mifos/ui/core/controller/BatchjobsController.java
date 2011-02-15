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

package org.mifos.ui.core.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.BatchjobsDto;
import org.mifos.application.admin.servicefacade.BatchjobsSchedulerDto;
import org.mifos.application.admin.servicefacade.BatchjobsServiceFacade;
import org.mifos.framework.business.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/batchjobs")
public class BatchjobsController {
    private static final String SUSPEND_PARAM = "SUSPEND";
    private static final String RUN_PARAM = "RUN";

    private String[] rawJobList = new String[0];

    @Autowired
    private BatchjobsServiceFacade batchjobsServiceFacade;

    protected BatchjobsController() {
        // default contructor for spring autowiring
    }

    protected BatchjobsController(final BatchjobsServiceFacade batchjobsServiceFacade) {
        this.batchjobsServiceFacade = batchjobsServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("admin.batchjobs", "batchjobs.ftl").build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView loadBatchjobsInfo(HttpServletRequest request) {
        List<String> errorMessages = new ArrayList<String>();
        return produceModelAndView(request, errorMessages);
    }

    @SuppressWarnings("PMD.AvoidRethrowingException") // for default AccessDeniedException feedback
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(HttpServletRequest request,
                                    @RequestParam(value = SUSPEND_PARAM, required = false) String suspend,
                                    @RequestParam(value = RUN_PARAM, required = false) String run,
                                    SessionStatus status) throws AccessDeniedException {

        List<String> errorMessages = new ArrayList<String>();
        if (StringUtils.isNotBlank(suspend)) {
            ServletContext context = request.getSession().getServletContext();
            String[] doSuspend = request.getParameterValues("SUSPEND");
            if (doSuspend != null) {
                try {
                    batchjobsServiceFacade.suspend(context, doSuspend[0]);
                } catch (AccessDeniedException e) {
                    throw e;
                } catch (Exception e) {
                    errorMessages.add("Could not change Scheduler status. " + new LogUtils().getStackTrace(e));
                }
            }
        } else if (StringUtils.isNotBlank(run)) {
            rawJobList = request.getParameterValues("ONDEMAND");
            if (rawJobList == null) {
                rawJobList = new String[0];
            }
            else {
                ServletContext context = request.getSession().getServletContext();
                try {
                    batchjobsServiceFacade.runSelectedTasks(context, rawJobList);
                } catch (AccessDeniedException e) {
                    throw e;
                } catch (Exception e) {
                    errorMessages.add("Could not run selected Tasks. " + new LogUtils().getStackTrace(e));
                }
            }
        }
        status.setComplete();

        return produceModelAndView(request, errorMessages);
    }

    private ModelAndView produceModelAndView(HttpServletRequest request, List<String> errorMessages) {
        ServletContext context = request.getSession().getServletContext();
        List<BatchjobsDto> batchjobs;
        BatchjobsSchedulerDto batchjobsScheduler;
        try {
            batchjobs = batchjobsServiceFacade.getBatchjobs(context);
        } catch(Exception tse) {
            errorMessages.add("Error when retrieving batch jobs information: " + tse.getMessage());
            batchjobs = new ArrayList<BatchjobsDto>();
        }
        try {
            batchjobsScheduler = batchjobsServiceFacade.getBatchjobsScheduler(context);
        } catch(Exception tse) {
            errorMessages.add("Error when retrieving batch jobs information: " + tse.getMessage());
            batchjobsScheduler = new BatchjobsSchedulerDto(false);
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("request", request);
        model.put("batchjobs", batchjobs);
        if (batchjobsScheduler == null) {
            model.put("scheduler", "");
        }
        else {
            model.put("scheduler", batchjobsScheduler.isStatus());
        }
        model.put("date0", new Date(0));
        model.put("executedTasks", rawJobList);
        if (rawJobList.length > 0) {
            rawJobList = new String[0];
        }

        Map<String, Object> status = new HashMap<String, Object>();
        status.put("errorMessages", errorMessages);

        ModelAndView modelAndView = new ModelAndView("batchjobs", "model", model);
        modelAndView.addObject("status", status);

        return modelAndView;
    }
}
