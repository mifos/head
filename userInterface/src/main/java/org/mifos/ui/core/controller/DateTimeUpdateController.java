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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.mifos.framework.util.DateTimeService;
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

    // This should be factored out to eliminate duplication with 
    // the TestingService.  This could be moved up to the common
    // module.  The whole TestingService class can't be moved 
    // up to the common module because it depends on other classes
    // in the mifos module.
    private String getTestMode() {
        // "main" means we are *not* running in a test mode
        return System.getProperty("mifos.test.mode", "main");
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes") // Reason: here we want to surface a generic exception
    @Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)  {
        // throw a generic exception if we are in "main" (production) mode, rather than a testing mode
        if ("main".equals(getTestMode())) {
            // obscure the error so we don't expose internals
            // seems like kind of a hack, perhaps there's a better way to 
            // generate a http 500 error
            RuntimeException exception = new RuntimeException("Internal error.");
            StackTraceElement[] elements = new StackTraceElement[1];
            elements[0] = new StackTraceElement("","","",0);
            exception.setStackTrace(elements);
            throw exception;
        }

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
        return modelAndView;
	}
	
}
