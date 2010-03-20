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

package org.mifos.security.util;

import java.io.IOException;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.login.util.helpers.LoginConstants;

/**
 * If we aren't logged in, and we are trying to access any URL other than the
 * login page, force a login.
 */
public class LoginFilter implements Filter {

    /**
     * This function implements the login filter it checks if user is not login
     * it forces the user to login by redirecting him to login page
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        request.setCharacterEncoding("UTF-8");
        String complUri = request.getRequestURI();
        int index = complUri.lastIndexOf("/");
        String uri = complUri.substring(index + 1);
        try {
            if (uri == null || uri.equalsIgnoreCase(LoginConstants.LOGINPAGEURI)
                    || uri.equalsIgnoreCase(LoginConstants.LOGINACTION)) {
                MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).debug("Inside Filter uri is for login page");
                chain.doFilter(req, res);
            } else {
                if (request.getSession(false) == null) {
                    MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).debug("Inside Filter session is null");
                    ActionErrors error = new ActionErrors();
                    error.add(LoginConstants.SESSIONTIMEOUT, new ActionMessage(LoginConstants.SESSIONTIMEOUT));
                    request.setAttribute(Globals.ERROR_KEY, error);
                    request.getRequestDispatcher(LoginConstants.LOGINPAGEURI).forward(req, res);
                    // ((HttpServletResponse)res).sendRedirect(request.getContextPath()+LoginConstants.LOGINPAGEURI);
                    return;
                }
                UserContext userContext = (UserContext) request.getSession(false).getAttribute(Constants.USERCONTEXT);
                if (null == userContext || null == userContext.getId()) {
                    // send back to login page with error message
                    ((HttpServletResponse) res).sendRedirect(request.getContextPath() + LoginConstants.LOGINPAGEURI);
                    return;

                } else {
                    ((HttpServletRequest) req).getSession(false).setAttribute(Constants.RANDOMNUM,
                            new Random().nextLong());
                    chain.doFilter(req, res);
                }

            }
        } catch (IllegalStateException ise) {
            MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).error("Inside Filter ISE" + ise.getMessage());
            ActionMessage error = new ActionMessage(LoginConstants.IllEGALSTATE);
            request.setAttribute(Globals.ERROR_KEY, error);
            ((HttpServletResponse) res).sendRedirect(request.getContextPath() + LoginConstants.LOGINPAGEURI);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

}
