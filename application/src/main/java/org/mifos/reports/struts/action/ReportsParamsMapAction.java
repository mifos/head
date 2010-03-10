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

package org.mifos.reports.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.reports.business.ReportsParamsMapValue;
import org.mifos.reports.business.service.ReportsBusinessService;
import org.mifos.reports.persistence.ReportsPersistence;
import org.mifos.reports.struts.actionforms.ReportsParamsMapActionForm;
import org.mifos.reports.util.helpers.ReportsConstants;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;

/**
 * Control Class for Report Params
 */
public class ReportsParamsMapAction extends BaseAction {

    private ReportsBusinessService reportsBusinessService;
    private ReportsPersistence reportsPersistence;
    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);

    public ReportsParamsMapAction() throws ServiceException {
        reportsBusinessService = new ReportsBusinessService();
        reportsPersistence = new ReportsPersistence();
    }

    @Override
    protected BusinessService getService() {
        return reportsBusinessService;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("reportsParamsMap");
        security.allow("loadAddList", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("createParamsMap", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("deleteParamsMap", SecurityConstants.ADMINISTER_REPORTPARAMS);

        security.allow("reportparamsmapaddlist_path", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("reportparamsmap_path", SecurityConstants.ADMINISTER_REPORTPARAMS);

        return security;
    }

    /**
     * Loads the Parameter Map AddList page
     */
    public ActionForward loadAddList(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In ReportsParamsMapAction:load Method: ");
        request.getSession().setAttribute("listOfAllParameters", new ReportsPersistence().getAllReportParams());
        ReportsParamsMapActionForm actionForm = (ReportsParamsMapActionForm) form;
        String strReportId = request.getParameter("reportId");
        if (strReportId == null) {
            strReportId = actionForm.getReportId() + "";
        }
        if (strReportId.equals("")) {
            strReportId = "0";
        }
        int reportId = Integer.parseInt(strReportId);
        actionForm.setReportId(reportId);
        request.getSession().setAttribute("listOfAllParametersForReportId",
                reportsPersistence.findParamsOfReportId(reportId));
        return mapping.findForward(ReportsConstants.ADDLISTREPORTSPARAMSMAP);
    }

    /**
     * Controls the creation of a link between parameter and a report
     */
    public ActionForward createParamsMap(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In ReportsParamsAction:createParamsMap Method: ");
        ReportsParamsMapActionForm actionForm = (ReportsParamsMapActionForm) form;
        ReportsParamsMapValue objParams = new ReportsParamsMapValue();
        objParams.setReportId(actionForm.getReportId());
        objParams.setParameterId(actionForm.getParameterId());
        String error = reportsBusinessService.createReportsParamsMap(objParams);
        request.getSession().setAttribute("addError", error);
        return mapping.findForward("reportparamsmap_path");
    }

    /**
     * Controls the deletion of a link between Parameter and a report
     */
    public ActionForward deleteParamsMap(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In ReportsParamsAction:deleteParams Method: ");
        ReportsParamsMapActionForm actionForm = (ReportsParamsMapActionForm) form;
        ReportsParamsMapValue objParams = new ReportsParamsMapValue();
        objParams.setMapId(actionForm.getMapId());
        reportsPersistence.deleteReportsParamsMap(objParams);
        return mapping.findForward("reportparamsmap_path");
    }

}
