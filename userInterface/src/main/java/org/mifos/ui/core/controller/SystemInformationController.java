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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.admin.servicefacade.SystemInformationDto;
import org.mifos.application.admin.servicefacade.SystemInformationServiceFacade;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class SystemInformationController extends AbstractController {

    private SystemInformationServiceFacade systemInformationServiceFacade;

    public SystemInformationServiceFacade getSystemInformationServiceFacade() {
        return this.systemInformationServiceFacade;
    }

    public void setSystemInformationServiceFacade(SystemInformationServiceFacade systemInformationServiceFacade) {
        this.systemInformationServiceFacade = systemInformationServiceFacade;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        ServletContext context = request.getSession().getServletContext();

        // TODO: figure out if this is really where we want to get this
        Locale locale = request.getLocale();

        SystemInformationDto systemInformationDto = null;
        try {
            systemInformationDto = systemInformationServiceFacade.getSystemInformation(context, locale);
        } catch (Exception e) {
            // TODO: what should we really do here?
            systemInformationDto = new SystemInformationDto("error", 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
        }
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
