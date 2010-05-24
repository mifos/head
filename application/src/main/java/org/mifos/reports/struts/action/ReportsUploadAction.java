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

import java.io.File;

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
import org.mifos.reports.business.ReportsJasperMap;
import org.mifos.reports.business.service.ReportsBusinessService;
import org.mifos.reports.persistence.ReportsPersistence;
import org.mifos.reports.util.helpers.ReportsConstants;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;

/**
 * Control Class for Uploading Report
 */

public class ReportsUploadAction extends BaseAction {

    private final ReportsBusinessService reportsBusinessService;
    private final ReportsPersistence reportsPersistence;
    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);

    public ReportsUploadAction() throws ServiceException {
        reportsBusinessService = new ReportsBusinessService();
        reportsPersistence = new ReportsPersistence();
    }

    @Override
    protected BusinessService getService() {
        return reportsBusinessService;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("reportsUploadAction");
        security.allow("uploadReport", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("administerreports_path", SecurityConstants.ADMINISTER_REPORTPARAMS);
        return security;

    }

    /**
     * Uploads the Report
     */

    public ActionForward uploadReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In ReportsUploadAction:uploadReport Method: ");

        String filename = request.getParameter("filename") == null ? "" : request.getParameter("filename");
        String strreportId = request.getParameter("reportId") == null || request.getParameter("reportId").equals("") ? "0"
                : request.getParameter("reportId");
        int reportId = Integer.parseInt(strreportId);
        File reportUploadFile = new File(filename);
        String fname = reportUploadFile.getName();
        if (fname != null) {
            fname = fname.replaceAll(".jrxml", ".jasper");
        }

        ReportsJasperMap objmap = new ReportsJasperMap();
        objmap.setReportId((short) reportId);
        objmap.setReportJasper(fname);
        reportsPersistence.updateReportsJasperMap(objmap);
        request.getSession().setAttribute(ReportsConstants.LISTOFREPORTS,
                new ReportsPersistence().getAllReportCategories());

        return mapping.findForward("administerreports_path");

    }

}
