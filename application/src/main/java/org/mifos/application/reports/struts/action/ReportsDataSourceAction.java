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

package org.mifos.application.reports.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.reports.business.ReportsDataSource;
import org.mifos.application.reports.business.service.ReportsBusinessService;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.application.reports.struts.actionforms.ReportsDataSourceActionForm;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;

/**
 * Control Class for Report DataSource
 */
public class ReportsDataSourceAction extends BaseAction {

    private ReportsBusinessService reportsBusinessService;
    private ReportsPersistence reportsPersistence;
    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);

    public ReportsDataSourceAction() throws ServiceException {
        reportsBusinessService = new ReportsBusinessService();
        reportsPersistence = new ReportsPersistence();
    }

    @Override
    protected BusinessService getService() {
        return reportsBusinessService;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("reportsDataSourceAction");
        security.allow("load", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("loadList", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("createDataSource", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("deleteDataSource", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("loadView", SecurityConstants.ADMINISTER_REPORTDS);

        security.allow("reportdatasource_path", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("reportdatasourceadd_path", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("reportdatasourcelist_path", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("reportdatasourceview_path", SecurityConstants.ADMINISTER_REPORTDS);

        return security;
    }

    /**
     * Loads the DataSource Add page
     */
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In ReportsDataSourceAction:load Method: ");
        return mapping.findForward(ReportsConstants.ADDREPORTSDATASOURCE);
    }

    /**
     * Loads DataSource List Page
     */
    public ActionForward loadList(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In ReportsDataSourceAction:loadList Method: ");
        request.getSession().setAttribute("listOfReportsDataSource", reportsPersistence.getAllReportDataSource());
        return mapping.findForward(ReportsConstants.LISTREPORTSDATASOURCE);
    }

    /**
     * Veiw DataSource
     */
    public ActionForward loadView(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In ReportsDataSourceAction:loadView Method: ");
        ReportsDataSourceActionForm actionForm = (ReportsDataSourceActionForm) form;
        String strDataSourceId = request.getParameter("dataSourceId");
        if (strDataSourceId == null)
            strDataSourceId = actionForm.getDatasourceId() + "";
        if (strDataSourceId == null || strDataSourceId.equals(""))
            strDataSourceId = "0";
        int dataSourceId = Integer.parseInt(strDataSourceId);
        actionForm.setDatasourceId(dataSourceId);
        request.getSession().setAttribute("viewDataSource", reportsPersistence.viewDataSource(dataSourceId));
        return mapping.findForward(ReportsConstants.VIEWREPORTSDATASOURCE);
    }

    /**
     * Controls the creation of DataSource
     */
    public ActionForward createDataSource(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In ReportsDataSourceAction:createDataSource Method: ");
        ReportsDataSourceActionForm actionForm = (ReportsDataSourceActionForm) form;
        ReportsDataSource objDs = new ReportsDataSource();
        objDs.setDriver(actionForm.getDriver());
        objDs.setName(actionForm.getName());
        objDs.setUrl(actionForm.getUrl());
        objDs.setPassword(actionForm.getPassword());
        objDs.setUsername(actionForm.getUsername());
        reportsPersistence.createReportsDataSource(objDs);
        return mapping.findForward("reportdatasource_path");
    }

    /**
     * Controls the deletion of DataSource
     */
    public ActionForward deleteDataSource(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In ReportsDataSourceAction:deleteDataSource Method: ");
        ReportsDataSourceActionForm actionForm = (ReportsDataSourceActionForm) form;
        ReportsDataSource objDs = new ReportsDataSource();
        objDs.setDatasourceId(actionForm.getDatasourceId());
        String error = reportsBusinessService.deleteReportsDataSource(objDs);
        request.getSession().setAttribute("deleteError", error);
        return mapping.findForward("reportdatasource_path");
    }

}
