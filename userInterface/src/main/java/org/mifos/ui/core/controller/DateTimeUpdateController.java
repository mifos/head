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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.mifos.framework.util.DateTimeService;
import org.mifos.service.test.TestMode;
import org.mifos.service.test.TestingService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class DateTimeUpdateController extends AbstractController {

    private DateTimeService dateTimeService;

    public DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = new DateTimeService();
        }
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    private TestingService testingService;

    public TestingService getTestingService() {
        return testingService;
    }

    public void setTestingService(TestingService testingService) {
        this.testingService = testingService;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView returnValue = new ModelAndView("pageNotFound");
        if (TestMode.MAIN == getTestingService().getTestMode()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            String dateTimeString = request.getParameter("dateTime");
            String result = null;
            if (null == dateTimeString) {
                result = "Missing 'dateTime' parameter!";
            } else if ("system".equals(dateTimeString)) {
                getDateTimeService().resetToCurrentSystemDateTime();
                result = getDateTimeService().getCurrentDateTime().toString();
            } else {
                DateTimeFormatter formatter = ISODateTimeFormat.basicDateTimeNoMillis();
                DateTime dateTime = formatter.parseDateTime(dateTimeString);

                getDateTimeService().setCurrentDateTime(dateTime);
                result = dateTime.toString();
            }
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("updateResult", result);
            model.put("request", request);
            Map<String, Object> status = new HashMap<String, Object>();
            List<String> errorMessages = new ArrayList<String>();
            status.put("errorMessages", errorMessages);
            ModelAndView modelAndView = new ModelAndView("dateTimeUpdate", "model", model);
            modelAndView.addObject("status", status);
            returnValue = modelAndView;
        }
        return returnValue;
    }

}
