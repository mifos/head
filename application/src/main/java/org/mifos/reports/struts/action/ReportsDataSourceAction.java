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

package org.mifos.reports.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.reports.business.ReportsDataSource;
import org.mifos.reports.business.service.ReportsBusinessService;
import org.mifos.reports.persistence.ReportsPersistence;
import org.mifos.reports.struts.actionforms.ReportsDataSourceActionForm;
import org.mifos.reports.util.helpers.ReportsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Control Class for Report DataSource
 */
public class ReportsDataSourceAction extends BaseAction {

    private ReportsBusinessService reportsBusinessService;
    private ReportsPersistence reportsPersistence;
    private static final Logger logger = LoggerFactory.getLogger(ReportsDataSourceAction.class);

    public ReportsDataSourceAction() throws ServiceException {
        reportsBusinessService = new ReportsBusinessService();
        reportsPersistence = new ReportsPersistence();
    }

    @Override
    protected BusinessService getService() {
        return reportsBusinessService;
    }

    /**
     * Loads the DataSource Add page
     */
    public ActionForward load(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsDataSourceAction:load Method: ");
        return mapping.findForward(ReportsConstants.ADDREPORTSDATASOURCE);
    }

    /**
     * Loads DataSource List Page
     */
    public ActionForward loadList(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsDataSourceAction:loadList Method: ");
        request.getSession().setAttribute("listOfReportsDataSource", reportsPersistence.getAllReportDataSource());
        return mapping.findForward(ReportsConstants.LISTREPORTSDATASOURCE);
    }

    /**
     * Veiw DataSource
     */
    public ActionForward loadView(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsDataSourceAction:loadView Method: ");
        ReportsDataSourceActionForm actionForm = (ReportsDataSourceActionForm) form;
        String strDataSourceId = request.getParameter("dataSourceId");
        if (strDataSourceId == null) {
            strDataSourceId = actionForm.getDatasourceId() + "";
        }
        if (strDataSourceId.equals("")) {
            strDataSourceId = "0";
        }
        int dataSourceId = Integer.parseInt(strDataSourceId);
        actionForm.setDatasourceId(dataSourceId);
        request.getSession().setAttribute("viewDataSource", reportsPersistence.viewDataSource(dataSourceId));
        return mapping.findForward(ReportsConstants.VIEWREPORTSDATASOURCE);
    }

    /**
     * Controls the creation of DataSource
     */
    public ActionForward createDataSource(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
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
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsDataSourceAction:deleteDataSource Method: ");
        ReportsDataSourceActionForm actionForm = (ReportsDataSourceActionForm) form;
        ReportsDataSource objDs = new ReportsDataSource();
        objDs.setDatasourceId(actionForm.getDatasourceId());
        String error = reportsBusinessService.deleteReportsDataSource(objDs);
        request.getSession().setAttribute("deleteError", error);
        return mapping.findForward("reportdatasource_path");
    }
}