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

package org.mifos.framework.util.helpers;

import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils {

    public static String getParameter(HttpServletRequest request, String paramName) {
        String param = null;
        if (request.getParameter(paramName) != null) {
            param = request.getParameter(paramName);
        }
        if (param == null && request.getAttribute(paramName) != null) {
            param = request.getAttribute(paramName).toString();
        }
        return param;
    }

    public static Object getGlobal(HttpServletRequest request, String key) {
        return request.getSession().getServletContext().getAttribute(key);
    }

    public static <T> T getBean(ServletContext servletContext, String beanName) {
        return (T) WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).getBean(beanName);
    }
}
