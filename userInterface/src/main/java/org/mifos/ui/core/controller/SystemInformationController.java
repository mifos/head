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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.mifos.application.admin.servicefacade.SystemInformationDto;
import org.mifos.application.admin.servicefacade.SystemInformationServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;

@Controller
@RequestMapping("/systemInformation")
public class SystemInformationController {

    @Autowired
    private SystemInformationServiceFacade systemInformationServiceFacade;

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("systemAdministration.viewsysteminformation.systeminformation", "systemInformation.ftl").build();
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_UNWRITTEN_FIELD", justification = "request is not null")
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView viewSystemInformation(HttpServletRequest request) {
        ServletContext context = request.getSession().getServletContext();
        RequestContext requestContext = new RequestContext(request);
        Locale locale = requestContext.getLocale();

        SystemInformationDto systemInformationDto = systemInformationServiceFacade.getSystemInformation(context, locale);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("request", request);
        model.put("systemInformationDto", systemInformationDto);
        Map<String, Object> status = new HashMap<String, Object>();
        List<String> errorMessages = new ArrayList<String>();
        status.put("errorMessages", errorMessages);
        ModelAndView modelAndView = new ModelAndView("systemInformation", "model", model);
        modelAndView.addObject("status", status);

        return modelAndView;
    }
}