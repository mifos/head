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
import org.mifos.core.MifosException;
import org.mifos.service.test.TestingService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String SAVE_PARAM = "SAVE";
    private static final String RUN_PARAM = "RUN";

    private TestingService testingService;
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
        ServletContext context = request.getSession().getServletContext();
        List<BatchjobsDto> batchjobs = null;
        BatchjobsSchedulerDto batchjobsScheduler = null;
        try {
            batchjobs = batchjobsServiceFacade.getBatchjobs(context);
            batchjobsScheduler = batchjobsServiceFacade.getBatchjobsScheduler();
        } catch(Exception tse) {
            new ArrayList<BatchjobsDto>();
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
        List<String> errorMessages = new ArrayList<String>();
        status.put("errorMessages", errorMessages);

        ModelAndView modelAndView = new ModelAndView("batchjobs", "model", model);
        modelAndView.addObject("status", status);

        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(HttpServletRequest request,
                                    @RequestParam(value = SUSPEND_PARAM, required = false) String suspend,
                                    @RequestParam(value = SAVE_PARAM, required = false) String save,
                                    @RequestParam(value = RUN_PARAM, required = false) String run,
                                    SessionStatus status) throws MifosException {

        if (StringUtils.isNotBlank(suspend)) {
            batchjobsServiceFacade.suspend();
        } else if (StringUtils.isNotBlank(save)) {
            batchjobsServiceFacade.saveChanges();
        } else if (StringUtils.isNotBlank(run)) {
            rawJobList = request.getParameterValues("ONDEMEND");
            if (rawJobList == null) {
                rawJobList = new String[0];
            }
            else {
                for (String job : rawJobList) {
                    getTestingService().runIndividualBatchJob(job, request.getSession().getServletContext());
                }
            }
        }
        status.setComplete();

        return loadBatchjobsInfo(request);
    }

    public TestingService getTestingService() {
        return this.testingService;
    }

    public void setTestingService(TestingService testingService) {
        this.testingService = testingService;
    }

}
