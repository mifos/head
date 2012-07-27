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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.mifos.application.admin.servicefacade.ShutdownServiceFacade;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Controller
@RequestMapping("/shutdown")
@SessionAttributes("shutdownFormBean")
public class ShutdownController {

    private static final Integer DEFAULT_SHUTDOWN_TIMEOUT = 600; // 10 minutes
    private static final String START_PARAM = "START";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private ShutdownServiceFacade shutdownServiceFacade;

    protected ShutdownController() {
        // default contructor for spring autowiring
    }

    protected ShutdownController(final ShutdownServiceFacade shutdownServiceFacade) {
        this.shutdownServiceFacade = shutdownServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("admin.shutdown", "shutdown.ftl").build();
    }

    @ModelAttribute("shutdownFormBean")
    public ShutdownFormBean showPopulatedForm() {
        ShutdownFormBean formBean = new ShutdownFormBean();
        formBean.setTimeout(DEFAULT_SHUTDOWN_TIMEOUT);
        return formBean;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value= {"NP_UNWRITTEN_FIELD, ICAST"}, justification="request is not null")
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView loadShutdownInfo(HttpServletRequest request) {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("request", request);
        model.put("activeSessions", shutdownServiceFacade.getLoggedUsers(request));
        model.put("shutdownStatus", shutdownServiceFacade.getStatus(request));
        model.put("submitButtonDisabled", shutdownServiceFacade.isShutdownInProgress(request));
        Map<String, Object> status = new HashMap<String, Object>();
        List<String> errorMessages = new ArrayList<String>();
        status.put("errorMessages", errorMessages);

        ModelAndView modelAndView = new ModelAndView("shutdown", "model", model);
        modelAndView.addObject("status", status);

        return modelAndView;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value= {"ICAST"}, justification="timeout is not null")
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(HttpServletRequest request,
                                    @RequestParam(value = START_PARAM, required = false) String start,
                                    @RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("shutdownFormBean") @Valid ShutdownFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {
    	if (StringUtils.isNotBlank(cancel))
    	{
    		this.shutdownServiceFacade.cancelShutdown(request);
    		status.setComplete();
    	}
    	else if(StringUtils.isNotBlank(start) && !result.hasErrors())
    	{
    		this.shutdownServiceFacade.scheduleShutdown(request, formBean.getTimeout() * 1000L);
    		status.setComplete();
    	}

        return loadShutdownInfo(request);
    }
}
