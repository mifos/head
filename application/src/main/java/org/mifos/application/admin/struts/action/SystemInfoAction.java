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

package org.mifos.application.admin.struts.action;

import java.sql.DatabaseMetaData;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.admin.business.service.SystemInfoService;
import org.mifos.application.admin.system.SystemInfo;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.reports.struts.action.BirtReportsUploadAction;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;

public class SystemInfoAction extends BaseAction {

    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final DatabaseMetaData metaData = StaticHibernateUtil.getSessionTL().connection().getMetaData();
        final ServletContext context = request.getSession().getServletContext();
        final Locale locale = getUserContext(request).getCurrentLocale();
        final SystemInfo systemInfo = new SystemInfo(metaData, context, locale, true);
        systemInfo.setCustomReportsDir(BirtReportsUploadAction.getCustomReportStorageDirectory());
        request.setAttribute("systemInfo", systemInfo);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    public static ActionSecurity getSecurity() {
        final ActionSecurity security = new ActionSecurity("systemInfoAction");
        security.allow("load", SecurityConstants.CAN_VIEW_SYSTEM_INFO);
        return security;
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return new SystemInfoService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }
}
