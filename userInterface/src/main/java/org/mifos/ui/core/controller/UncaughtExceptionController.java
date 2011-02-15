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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UncaughtExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionController.class);

    @RequestMapping("/uncaughtException.ftl")
    public ModelAndView handleException(HttpServletRequest request) {
        // http://java.sun.com/developer/technicalArticles/Servlets/servletapi2.3
        Object obj = request.getAttribute("javax.servlet.error.exception");
        Throwable cause = null;
        if (obj != null) {
            cause = (Throwable) obj;
        }

        String requestUri = request.getRequestURI();
        logger.error("Uncaught exception while accessing '" + requestUri + "'", cause);

        ModelAndView mm = new ModelAndView();
        mm.setViewName("uncaughtException");
        mm.addObject("uncaughtException", cause);
        mm.addObject("requestUri", requestUri);
        if (cause != null) {
            Writer result = new StringWriter();
            cause.printStackTrace(new PrintWriter(result));
            mm.addObject("stackString", result.toString());
        }
        return mm;
    }
}
