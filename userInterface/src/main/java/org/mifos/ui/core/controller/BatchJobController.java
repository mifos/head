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

import org.apache.commons.lang.StringUtils;
import org.mifos.core.MifosException;
import org.mifos.service.test.TestMode;
import org.mifos.service.test.TestingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BatchJobController extends AbstractController {
    private static final String JOBS_EXECUTED = "jobsExecuted";
    private TestingService testingService;

    @Override
    @RequestMapping("/runBatchJobs.ftl")
    protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        ModelAndView returnValue = new ModelAndView("pageNotFound");
        if (TestMode.MAIN == getTestingService().getTestMode()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            String runAllBatchJobs = request.getParameter("runAllBatchJobs");
            Map<String, Object> model = new HashMap<String, Object>();
            List<String> jobsRan = new ArrayList<String>();
            model.put(JOBS_EXECUTED, jobsRan);

            if (StringUtils.isNotBlank(runAllBatchJobs)) {
                getTestingService().runAllBatchJobs(request.getSession().getServletContext());
                jobsRan.add("All jobs ran. Hopefully.");
            } else {
                String rawJobList[] = request.getParameterValues("job");
                if (null != rawJobList) {
                    for (String job : rawJobList) {
                        try {
                            getTestingService().runIndividualBatchJob(job, request.getSession().getServletContext());
                        } catch(MifosException me) {
                            throw new Exception("Error while handling batch job run request.", me);
                        }
                        jobsRan.add(job);
                    }
                }
            }

            ModelAndView modelAndView = new ModelAndView("runBatchJobs", "model", model);
            returnValue = modelAndView;
        }
        return returnValue;
    }

    public TestingService getTestingService() {
        return testingService;
    }

    public void setTestingService(TestingService testingService) {
        this.testingService = testingService;
    }
}
