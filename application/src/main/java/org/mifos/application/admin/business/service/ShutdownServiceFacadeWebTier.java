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

package org.mifos.application.admin.business.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.application.admin.servicefacade.LoggedUserDto;
import org.mifos.application.admin.servicefacade.ShutdownServiceFacade;
import org.mifos.application.admin.system.PersonnelInfo;
import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ServletUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.UserContext;

public class ShutdownServiceFacadeWebTier implements ShutdownServiceFacade {
    @Override
    public List<LoggedUserDto> getLoggedUsers(HttpServletRequest request) {
        ShutdownManager shutdownManager = (ShutdownManager) ServletUtils.getGlobal(request, ShutdownManager.class
                .getName());
        Collection<HttpSession> sessions = shutdownManager.getActiveSessions();
        List<PersonnelInfo> personnelInfos = new ArrayList<PersonnelInfo>();
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        if (ActivityMapper.getInstance().isViewActiveSessionsPermitted(userContext, userContext.getBranchId())) {
            PersonnelBusinessService personnelBusinessService = new PersonnelBusinessService();
            for (HttpSession session : sessions) {
                UserContext userContextFromSession = (UserContext) session.getAttribute(LoginConstants.USERCONTEXT);
                if (userContextFromSession == null) {
                    continue;
                }
                PersonnelBO personnel;
                try {
                    personnel = personnelBusinessService.getPersonnel(userContextFromSession.getId());
                } catch (ServiceException e) {
                    continue;
                }
                String offices = generateOfficeChain(personnel.getOffice());
                String names = personnel.getPersonnelDetails().getName().getFirstName() + " "
                        + personnel.getPersonnelDetails().getName().getLastName();
                DateTimeFormatter formatter = DateTimeFormat.shortDateTime().withOffsetParsed().withLocale(userContext.getCurrentLocale());
                String activityTime = formatter.print(session.getLastAccessedTime());
                ActivityContext activityContext = (ActivityContext) session.getAttribute(LoginConstants.ACTIVITYCONTEXT);
                String activityDesc = "[" + activityContext.getLastForward().getName() + "] "
                        + activityContext.getLastForward().getPath();
                personnelInfos.add(new PersonnelInfo(offices, names, activityTime, activityDesc));
            }
        }
        Collections.sort(personnelInfos);

        List<LoggedUserDto> loggedUsers = new ArrayList<LoggedUserDto>();
        for (PersonnelInfo personnelInfo : personnelInfos) {
            loggedUsers.add(new LoggedUserDto(personnelInfo.getOffices(), personnelInfo.getNames(),
                    personnelInfo.getActivityTime(), personnelInfo.getActivityContext()));
        }

        return loggedUsers;
    }

    @Override
    public String getStatus(HttpServletRequest request) {
        ShutdownManager shutdownManager = (ShutdownManager) ServletUtils.getGlobal(request, ShutdownManager.class
                .getName());
        return shutdownManager.getStatus();
    }

    @Override
    public boolean isShutdownInProgress(HttpServletRequest request) {
        ShutdownManager shutdownManager = (ShutdownManager) ServletUtils.getGlobal(request, ShutdownManager.class
                .getName());
        return shutdownManager.isShutdownInProgress();
    }

    @Override
    public void scheduleShutdown(HttpServletRequest request, long timeout) {
        ShutdownManager shutdownManager = (ShutdownManager) ServletUtils.getGlobal(request, ShutdownManager.class
                .getName());
        shutdownManager.scheduleShutdown(timeout);
    }

    @Override
    public void cancelShutdown(HttpServletRequest request) {
        ShutdownManager shutdownManager = (ShutdownManager) ServletUtils.getGlobal(request, ShutdownManager.class
                .getName());
        shutdownManager.cancelShutdown();
    }

    private String generateOfficeChain(OfficeBO office) {
        /* MIFOS-2789: only list the branch offfice if there is one, and if no branch office then list the head office.
        if (office.getParentOffice() != null) {
            return generateOfficeChain(office.getParentOffice()) + " / " + office.getOfficeName();
        }
        */
        return office.getOfficeName();
    }
}
