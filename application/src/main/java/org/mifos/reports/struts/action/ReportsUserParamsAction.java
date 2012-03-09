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

package org.mifos.reports.struts.action;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.reports.admindocuments.struts.action.BirtAdminDocumentUploadAction;
import org.mifos.reports.business.ReportsJasperMap;
import org.mifos.reports.business.ReportsParamsMap;
import org.mifos.reports.business.dao.ReportsParamQueryDAO;
import org.mifos.reports.business.service.ReportsBusinessService;
import org.mifos.reports.persistence.ReportsPersistence;
import org.mifos.reports.struts.actionforms.ReportsUserParamsActionForm;
import org.mifos.reports.util.helpers.ReportsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Control Class for Report Params
 */
public class ReportsUserParamsAction extends BaseAction {

    private final ReportsBusinessService reportsBusinessService;

    private static ReportsPersistence reportsPersistence;

    private static final Logger logger = LoggerFactory.getLogger(ReportsUserParamsAction.class);

    public ReportsUserParamsAction() throws ServiceException {
        reportsBusinessService = new ReportsBusinessService();
        reportsPersistence = new ReportsPersistence();
    }

    @Override
    protected BusinessService getService() {
        return reportsBusinessService;
    }

    /**
     * To allow loading Administrative documents
     */
    public ActionForward loadAdminReport(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        request.getSession().setAttribute("listOfAllParameters", new ReportsPersistence().getAllReportParams());

        String strReportId = request.getParameter("admindocId");
        String account_id = request.getParameter("globalAccountNum");
        if (strReportId == null || strReportId.equals("")) {
            strReportId = "0";
        }
        int reportId = Integer.parseInt(strReportId);
        String reportName = legacyAdminDocumentDao.getAdminDocumentById((short) reportId)
                .getAdminDocumentName();
        String filename = legacyAdminDocumentDao.getAdminDocumentById((short) reportId)
                .getAdminDocumentIdentifier();
        File file = new File(viewOrganizationSettingsServiceFacade.getAdminDocumentStorageDirectory(), filename);

        if (file.exists()) {
            filename = file.getAbsolutePath();
        }
        else {
            filename = "adminReport/" + filename;
        }
        if (filename.endsWith(".rptdesign")) {
            request.setAttribute("reportFile", filename);
            request.setAttribute("reportName", reportName);
            request.setAttribute("account_id", account_id);
            return mapping.findForward(ReportsConstants.ADMINDOCBIRTREPORTPATH);
        }
        return mapping.findForward(ReportsConstants.ADMINDOCBIRTREPORTPATH);
    }

    /**
     * Loads the Parameter Add page
     */
    @SuppressWarnings("unchecked")
    public ActionForward loadAddList(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsUserParamsAction:load Method: ");
        request.getSession().setAttribute("listOfAllParameters", new ReportsPersistence().getAllReportParams());
        ReportsParamQueryDAO paramDAO = new ReportsParamQueryDAO();
        ReportsUserParamsActionForm actionForm = (ReportsUserParamsActionForm) form;
        String strReportId = request.getParameter("reportId");
        if (strReportId == null) {
            strReportId = actionForm.getReportId() + "";
        }
        if (strReportId.equals("")) {
            strReportId = "0";
        }
        int reportId = Integer.parseInt(strReportId);
        String reportName = reportsPersistence.getReport((short) reportId).getReportName();

        List<ReportsJasperMap> reports = reportsPersistence.findJasperOfReportId(reportId);
        if (reports.size() > 0) {
            ReportsJasperMap reportFile = reports.get(0);
            String filename = reportFile.getReportJasper();
            File file = new File(BirtReportsUploadAction.getCustomReportStorageDirectory(), filename);

            if (file.exists()) {
                filename = file.getAbsolutePath();
            }
            else {
                filename = "report/" + filename;
            }
            if (filename.endsWith(".rptdesign")) {
                request.setAttribute("reportFile", filename);
                request.setAttribute("reportName", reportName);
                return mapping.findForward(ReportsConstants.BIRTREPORTPATH);
            }
        }

        actionForm.setReportId(reportId);
        request.getSession().setAttribute("listOfAllParametersForReportId",
                reportsPersistence.findParamsOfReportId(reportId));
        request.getSession().setAttribute("listOfReportJasper", reportsPersistence.findJasperOfReportId(reportId));

        List<ReportsParamsMap> reportParams = (List) request.getSession()
                .getAttribute("listOfAllParametersForReportId");
        Object[] obj = reportParams.toArray();
        if (obj != null && obj.length > 0) {

            for (int i = 0; i < obj.length; i++) {
                ReportsParamsMap rp = (ReportsParamsMap) obj[i];
                if (rp.getReportsParams().getType().equalsIgnoreCase("Query")) {
                    request.getSession().setAttribute("para" + (i + 1),
                            paramDAO.listValuesOfParameters(rp.getReportsParams()));
                }
            }
        }

        return mapping.findForward(ReportsConstants.ADDLISTREPORTSUSERPARAMS);
    }

    /**
     * Generate report in given export format
     */
    public ActionForward processReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        logger.debug("In ReportsUserParamsAction:processReport Method: ");
        ReportsUserParamsActionForm actionForm = (ReportsUserParamsActionForm) form;
        int reportId = actionForm.getReportId();
        String applPath = actionForm.getApplPath();
        String expType = actionForm.getExpFormat();
        String expFilename = reportsBusinessService.runReport(reportId, request, applPath, expType);
        request.getSession().setAttribute("expFileName", expFilename);
        actionForm.setExpFileName(expFilename);
        String forward = "";
        String error = (String) request.getSession().getAttribute("paramerror");
        if (error == null || error.equals("")) {
            forward = ReportsConstants.PROCESSREPORTSUSERPARAMS;
        } else {
            forward = ReportsConstants.ADDLISTREPORTSUSERPARAMS;
        }
        return mapping.findForward(forward);
    }
}
