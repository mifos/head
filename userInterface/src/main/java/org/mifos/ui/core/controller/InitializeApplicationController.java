/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.service.test.TestMode;
import org.mifos.service.test.TestingService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class InitializeApplicationController extends AbstractController {    
    private TestingService testingService;

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes") // Reason: here we want to surface a generic exception
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView returnValue = new ModelAndView("pageNotFound");
        if (TestMode.MAIN == getTestingService().getTestMode()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            getTestingService().reinitializeCaches();

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("request", request);
            Map<String, Object> status = new HashMap<String, Object>();
            List<String> errorMessages = new ArrayList<String>();
            status.put("errorMessages", errorMessages);
            ModelAndView modelAndView = new ModelAndView("initializeApplication", "model", model);
            modelAndView.addObject("status", status);
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
