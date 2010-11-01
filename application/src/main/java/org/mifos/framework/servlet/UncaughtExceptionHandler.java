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

package org.mifos.framework.servlet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class UncaughtExceptionHandler extends SimpleMappingExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionHandler.class);

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,  HttpServletResponse response, Object handler, Exception ex) {
        String requestUri = request.getRequestURI();

        logger.error("Uncaught exception while accessing '" + requestUri + "'", ex);

        ModelAndView mm = new ModelAndView();
        mm.setViewName("uncaughtException");
        mm.addObject("uncaughtException", ex);
        mm.addObject("requestUri", requestUri);
        if (isDevCookieAvailable(request) && ex != null) {
            Writer result = new StringWriter();
            ex.printStackTrace(new PrintWriter(result));
            mm.addObject("stackString", result.toString());
        }
        return mm;
    }

    private boolean isDevCookieAvailable(HttpServletRequest request) {
        boolean cookieExists = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("MifosDevCookie".equals(cookie.getName())) {
                    cookieExists = true;
                    break;
                }
            }
        }
        return cookieExists;
    }
}
