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

package org.mifos.security.authentication;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.NewLoginServiceFacade;
import org.mifos.config.AccountingRules;
import org.mifos.config.Localization;
import org.mifos.config.SitePreferenceType;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.dto.domain.LoginDto;
import org.mifos.framework.components.batchjobs.MifosBatchJob;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ServletUtils;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;
import org.mifos.ui.core.controller.util.helpers.SitePreferenceHelper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * I am a custom authentication filter implementation of {@link UsernamePasswordAuthenticationFilter}.
 *
 * A custom filter is needed as in the legacy authentication process, certain things were set in the session
 * that are used by the legacy views (jsp pages). When struts and jsp is completely removed, we can revert back to using
 * the out of the box authentication filter that is created using the spring security namespace.
 */
public class MifosLegacyUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    final NewLoginServiceFacade loginServiceFacade;

    public MifosLegacyUsernamePasswordAuthenticationFilter(final NewLoginServiceFacade loginServiceFacade) {
        super();
        this.loginServiceFacade = loginServiceFacade;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {

        //LocaleContextHolder.setLocale(Localization.getInstance().getConfiguredLocale());

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        AuthenticationException denied = null;

        boolean allowAuthenticationToContinue = true;
        if (MifosBatchJob.isBatchJobRunningThatRequiresExclusiveAccess()) {
            allowAuthenticationToContinue = false;

            HttpSession session = request.getSession(false);
            if(session != null) {
                session.invalidate();
            }

            denied = new AuthenticationServiceException(messages.getMessage(LoginConstants.BATCH_JOB_RUNNING,
                    "You have been logged out of the system because batch jobs are running."));
        }

        ShutdownManager shutdownManager = (ShutdownManager) ServletUtils.getGlobal(request, ShutdownManager.class.getName());
        if (shutdownManager.isShutdownDone()) {
            allowAuthenticationToContinue = false;
            request.getSession(false).invalidate();
            denied = new AuthenticationServiceException(messages.getMessage(LoginConstants.SHUTDOWN,
                    "You have been logged out of the system because Mifos is shutting down."));
        }

        if (shutdownManager.isInShutdownCountdownNotificationThreshold()) {
            request.setAttribute("shutdownIsImminent", true);
        }

        if (allowAuthenticationToContinue) {
            super.doFilter(request, response, chain);
        } else {
            unsuccessfulAuthentication(request, response, denied);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        final String username = obtainUsername(request);
        request.setAttribute("username", username);
        final String password = obtainPassword(request);

        if (authenticationIsUnsuccessfulDueToInvalidPassword(failed, username)) {
            try {
                loginServiceFacade.login(username, password);
            } catch (UsernameNotFoundException e) {
                // ignore and let spring security handle BadCredentialsException
            }
        }

        super.unsuccessfulAuthentication(request, response, failed);
    }

    private boolean authenticationIsUnsuccessfulDueToInvalidPassword(AuthenticationException failed, String username) {
        return StringUtils.isNotBlank(username) && failed != null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            Authentication authResult) throws IOException, ServletException {

        final String username = obtainUsername(request);
        request.setAttribute("username", username);
        final String password = obtainPassword(request);

        handleLegacySuccessfulAuthentication(request, response, username, password);

        super.successfulAuthentication(request, response, authResult);
    }

    private void handleLegacySuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, final String username,
            final String password) {
        try {
            FlowManager flowManager = new FlowManager();
            String flowKey = String.valueOf(new DateTimeService().getCurrentDateTime().getMillis());
            flowManager.addFLow(flowKey, new Flow(), this.getFilterName());

            request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
            
            request.getSession(false).setAttribute(Constants.FLOWMANAGER, flowManager);
            request.getSession(false).setAttribute(Constants.RANDOMNUM, new Random().nextLong());
            
            boolean flag= AccountingRules.getSimpleAccountingStatus();
            request.getSession(false).setAttribute("accountingActivationStatus", flag);
            LoginDto loginActivity = loginServiceFacade.login(username, password);
            
            PersonnelBO user =  ApplicationContextProvider.getBean(LegacyPersonnelDao.class).findPersonnelById(loginActivity.getUserId());

            SitePreferenceHelper sitePreferenceHelper = new SitePreferenceHelper();
            sitePreferenceHelper.setSitePreferenceCookie(SitePreferenceType.getSitePreference(user.getSitePreference()), response);
            
            ActivityContext activityContext = new ActivityContext(Short.valueOf("0"), user.getOffice().getOfficeId(), user.getPersonnelId());
            request.getSession(false).setAttribute(Constants.ACTIVITYCONTEXT, activityContext);
            request.setAttribute("activityDto", loginActivity);

            Short localeId = user.getPreferredLocale();
            Locale preferredLocale = Localization.getInstance().getLocaleById(localeId);
            UserContext userContext = new UserContext();
            userContext.setPreferredLocale(preferredLocale);
            userContext.setLocaleId(localeId);
            userContext.setId(user.getPersonnelId());
            userContext.setName(user.getDisplayName());
            userContext.setLevel(user.getLevelEnum());
            userContext.setRoles(user.getRoles());
            userContext.setLastLogin(user.getLastLogin());
            userContext.setPasswordChanged(user.getPasswordChanged());
            userContext.setBranchId(user.getOffice().getOfficeId());
            userContext.setBranchGlobalNum(user.getOffice().getGlobalOfficeNum());
            userContext.setOfficeLevelId(user.getOffice().getLevel().getId());

            request.setAttribute(Constants.USERCONTEXT, userContext);
            request.getSession(false).setAttribute(Constants.USERCONTEXT, userContext);
            request.removeAttribute("CURRENT_LOCALE_ID");
            request.setAttribute("CURRENT_LOCALE_ID", localeId);
            
            if (loginActivity.isPasswordChanged()) {
                HttpSession hs = request.getSession(false);
                hs.setAttribute(Constants.USERCONTEXT, userContext);
                hs.setAttribute(Globals.LOCALE_KEY, userContext.getCurrentLocale());
            } else {
                flowManager.addObjectToFlow(flowKey, Constants.TEMPUSERCONTEXT, userContext);
            }

            if (loginActivity.isPasswordChanged()) {
                flowManager.removeFlow((String) request.getAttribute(Constants.CURRENTFLOWKEY));
                request.setAttribute(Constants.CURRENTFLOWKEY, null);
            }
        } catch (ApplicationException e1) {
            throw new MifosRuntimeException(e1);
        }
    }
}