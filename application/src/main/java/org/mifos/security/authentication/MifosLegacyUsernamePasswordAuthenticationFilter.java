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

package org.mifos.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mifos.application.servicefacade.LoginActivityDto;
import org.mifos.application.servicefacade.LoginServiceFacade;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.UserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * I am a custom authentication filter implementation of {@link UsernamePasswordAuthenticationFilter}.
 *
 * A custom filter is needed as in the legacy authentication process, certain things were set in the session
 * that are used by the legacy views (jsp pages). When struts and jsp is completely removed, we can revert back to using
 * the out of the box authentication filter that is created using the spring security namespace.
 */
public class MifosLegacyUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    final LoginServiceFacade loginServiceFacade;

    public MifosLegacyUsernamePasswordAuthenticationFilter(final LoginServiceFacade loginServiceFacade) {
        super();
        this.loginServiceFacade = loginServiceFacade;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            Authentication authResult) throws IOException, ServletException {

        final String username = obtainUsername(request);
        final String password = obtainPassword(request);

        handleLegacySuccessfulAuthentication(request, username, password);

        super.successfulAuthentication(request, response, authResult);
    }

    private void handleLegacySuccessfulAuthentication(HttpServletRequest request, final String username,
            final String password) {
        try {
            LoginActivityDto loginActivity = loginServiceFacade.login(username, password);

            request.getSession(false).setAttribute(Constants.ACTIVITYCONTEXT, loginActivity.getActivityContext());

            UserContext userContext = loginActivity.getUserContext();
            if (loginActivity.isPasswordChanged()) {
                HttpSession hs = request.getSession(false);
                hs.setAttribute(Constants.USERCONTEXT, userContext);
                hs.setAttribute("org.apache.struts.action.LOCALE", userContext.getCurrentLocale());
            } else {
                String currentFlowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
                HttpSession session = request.getSession();
                FlowManager flowManager = (FlowManager) session.getAttribute(Constants.FLOWMANAGER);
                try {
                    flowManager.addObjectToFlow(currentFlowKey, Constants.TEMPUSERCONTEXT, userContext);
                } catch (PageExpiredException e) {
                    e.printStackTrace();
                }
            }

            Short passwordChanged = loginActivity.getPasswordChangedFlag();
            if (null != passwordChanged && LoginConstants.PASSWORDCHANGEDFLAG.equals(passwordChanged)) {
                FlowManager flowManager = (FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER);
                if (flowManager != null) {
                    flowManager.removeFlow((String) request.getAttribute(Constants.CURRENTFLOWKEY));
                    request.setAttribute(Constants.CURRENTFLOWKEY, null);
                }
            }
        } catch (ApplicationException e1) {
            // IGNORE as shouldn't happen
        }
    }
}