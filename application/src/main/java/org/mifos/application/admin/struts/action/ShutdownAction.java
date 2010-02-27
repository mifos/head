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

package org.mifos.application.admin.struts.action;

import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.application.admin.business.service.ShutdownService;
import org.mifos.application.admin.struts.actionforms.ShutdownActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShutdownAction extends BaseAction {
    private static final String DEFAULT_SHUTDOWN_TIMEOUT = "600"; // 10 minutes

    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        ShutdownActionForm shutdownForm = (ShutdownActionForm) form;
        if (shutdownForm.getShutdownTimeout() == null) {
            shutdownForm.setShutdownTimeout(DEFAULT_SHUTDOWN_TIMEOUT);
        }
        SessionUtils.setAttribute("shutdownManager", ShutdownManager.getInstance(getUserContext(request).getCurrentLocale()), request.getSession());
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    public ActionForward shutdown(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        ShutdownActionForm shutdownForm = (ShutdownActionForm) form;
        ShutdownManager.scheduleShutdown(Long.parseLong(shutdownForm.getShutdownTimeout()) * 1000);
        if (ShutdownManager.isInShutdownCountdownNotificationThreshold()) {
            SessionUtils.setAttribute("shutdownIsImminent", true, request.getSession());
        }
        return load(mapping, form, request, response);
    }

    public ActionForward cancelShutdown(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        ShutdownManager.cancelShutdown();
        SessionUtils.setAttribute("shutdownIsImminent", false, request.getSession());
        return load(mapping, form, request, response);
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("shutdownAction");
        security.allow("load", SecurityConstants.CAN_SHUTDOWN_MIFOS);
        security.allow("shutdown", SecurityConstants.CAN_SHUTDOWN_MIFOS);
        security.allow("cancelShutdown", SecurityConstants.CAN_SHUTDOWN_MIFOS);
        return security;
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return new ShutdownService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }
}
