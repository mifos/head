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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.admindocuments.persistence.AdminDocumentPersistence;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.business.ReportsParamsMap;
import org.mifos.application.reports.business.dao.ReportsParamQueryDAO;
import org.mifos.application.reports.business.service.ReportsBusinessService;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.application.reports.struts.actionforms.ReportsUserParamsActionForm;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.ReportActionSecurity;
import org.mifos.framework.security.util.resources.ReportSecurityConstants;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;

/**
 * Control Class for Report Params
 */
public class ReportsUserParamsAction extends BaseAction {

	private final ReportsBusinessService reportsBusinessService;

	private final ReportsPersistence reportsPersistence;
	

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public ReportsUserParamsAction() throws ServiceException {
		reportsBusinessService = new ReportsBusinessService();
		reportsPersistence = new ReportsPersistence();
	}

	@Override
	protected BusinessService getService() {
		return reportsBusinessService;
	}

	public static ActionSecurity getSecurity(){
		ReportActionSecurity security = new ReportActionSecurity("reportsUserParamsAction", "loadAddList");

                // FIXME: no associated activity exists for this constant
		security.allow("reportuserparamslist_path", SecurityConstants.ADMINISTER_REPORTPARAMS);
		
		//map the report id to it's corrosponding activity id. though it's rough but it works :->
//		security.allowReport(1, ReportSecurityConstants.CLIENT_SUMMARY_AND_HISTORY_REPORT);
		security.allowReport(1, ReportSecurityConstants.COLLECTION_SHEET_REPORT);
		security.allowReport(2, ReportSecurityConstants.BRANCH_CASH_CONFIRMATION_REPORT);
		security.allowReport(3, ReportSecurityConstants.BRANCH_PROGRESS_REPORT);
		security.allowReport(4, ReportSecurityConstants.CLIENT_LOAN_REPAYMENT_SCHEDULE);
		security.allowReport(5, ReportSecurityConstants.CLIENT_FEES_CHARGES_AND_PENALTIES_REPORT);
		security.allowReport(6, ReportSecurityConstants.CLIENT_PENDING_APPROVAL_REPORT);
		security.allowReport(7, ReportSecurityConstants.CLIENTS_WITHOUT_SAVINGS_ACCOUNT);
		security.allowReport(8, ReportSecurityConstants.BRANCH_PERFORMANCE_STATUS_REPORT);
		security.allowReport(9, ReportSecurityConstants.AREA_PERFORMANCE_STATUS_REPORT);
		security.allowReport(10, ReportSecurityConstants.DIVISION_PERFORMANCE_STATUS_REPORT);
		security.allowReport(11, ReportSecurityConstants.REGION_PERFORMANCE_STATUS_REPORT);
		security.allowReport(12, ReportSecurityConstants.GRAMEEN_KOOTA_PERFORMANCE_STATUS_REPORT);
		security.allowReport(13, ReportSecurityConstants.STAFF_PERFORMANCE_REPORT);
		security.allowReport(14, ReportSecurityConstants.OUTREACH_REPORT);
		security.allowReport(15, ReportSecurityConstants.CENTER_SUMMARY_REPORT);
		security.allowReport(16, ReportSecurityConstants.COLLECTION_SHEET);
		security.allowReport(17, ReportSecurityConstants.LOAN_PRODUCT_DISTRIBUTION);
		security.allowReport(18, ReportSecurityConstants.BRANCH_DUE_DISBURSEMENT_REPORT);
		security.allowReport(19, ReportSecurityConstants.LOANS_PENDING_APPROVAL_REPORT);
		security.allowReport(20, ReportSecurityConstants.LOAN_ACCOUNTS_REPORTS);
		security.allowReport(21, ReportSecurityConstants.DAILY_CASH_CONFIRMATION_REPORT_STAFF_WISE);
		security.allowReport(22, ReportSecurityConstants.DAILY_CASH_FLOW_REPORT_BRANCH);
		security.allowReport(23, ReportSecurityConstants.FUND_REQUIREMENT_REPORT);
		security.allowReport(24, ReportSecurityConstants.DAILY_TRANSACTION_SUMMARY_REPORT);
		security.allowReport(25, ReportSecurityConstants.DAILY_PORTFOLIO_QUALITY_DATA_REPORT);
		security.allowReport(26, ReportSecurityConstants.CENTER_MEETING_SCHEDULE);
		security.allowReport(28, ReportSecurityConstants.DETAILED_AGING_OF_PORTFOLIO_AT_RISK);
		security.allowReport(29, ReportSecurityConstants.ACTIVE_LOANS_BY_LOAN_OFFICER);
		
		
		
		for(ReportsBO report : getNewUploadedReport()){
			    security.allowReport(report.getReportId().intValue(),report.getActivityId());
			}
		
		
                // FIXME: no associated activity exists for this constant
		security.allow("loadAddList", SecurityConstants.ADMINISTER_REPORTPARAMS);
		security.allow("processReport", SecurityConstants.ADMINISTER_REPORTPARAMS);
		security.allow("reportsuserprocess_path", SecurityConstants.ADMINISTER_REPORTPARAMS);
		security.allow("loadAdminReport", SecurityConstants.CAN_VIEW_ADMIN_DOCUMENTS);
		
		return security;
	}

	/**
	 * To allow loading Administrative documents
	 */
	
	public ActionForward loadAdminReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

	request.getSession().setAttribute("listOfAllParameters",
			new ReportsPersistence().getAllReportParams());

	String strReportId = request.getParameter("admindocId");
	String account_id = request.getParameter("globalAccountNum");
	if (strReportId == null || strReportId.equals("")) {
		strReportId = "0";
	}
	int reportId = Integer.parseInt(strReportId);
	String reportName = new AdminDocumentPersistence().getAdminDocumentById((short)reportId).getAdminDocumentName();
	String filename = new AdminDocumentPersistence().getAdminDocumentById((short)reportId).getAdminDocumentIdentifier();
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
	 	public ActionForward loadAddList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In ReportsUserParamsAction:load Method: ");
		request.getSession().setAttribute("listOfAllParameters",
				new ReportsPersistence().getAllReportParams());
		ReportsParamQueryDAO paramDAO = new ReportsParamQueryDAO();
		ReportsUserParamsActionForm actionForm = (ReportsUserParamsActionForm) form;
		String strReportId = request.getParameter("reportId");
		if (strReportId == null) {
			strReportId = actionForm.getReportId() + "";
		}
		if (strReportId == null || strReportId.equals("")) {
			strReportId = "0";
		}
		int reportId = Integer.parseInt(strReportId);
		String reportName = reportsPersistence.getReport((short)reportId).getReportName();
		
		List<ReportsJasperMap> reports = reportsPersistence.findJasperOfReportId(reportId);
		if (reports.size() > 0) {
			ReportsJasperMap reportFile = reports.get(0);
			String filename = reportFile.getReportJasper();
			if (filename.endsWith(".rptdesign")) {
				request.setAttribute("reportFile", filename);
				request.setAttribute("reportName", reportName);
				return mapping.findForward(ReportsConstants.BIRTREPORTPATH);
			}
		}
		
		actionForm.setReportId(reportId);
		request.getSession().setAttribute("listOfAllParametersForReportId",
				reportsPersistence.findParamsOfReportId(reportId));
		request.getSession().setAttribute("listOfReportJasper",
				reportsPersistence.findJasperOfReportId(reportId));

		List<ReportsParamsMap> reportParams = (List) request.getSession()
				.getAttribute("listOfAllParametersForReportId");
		Object[] obj = reportParams.toArray();
		if (obj != null && obj.length > 0) {

			for (int i = 0; i < obj.length; i++) {
				ReportsParamsMap rp = (ReportsParamsMap) obj[i];
				if (rp.getReportsParams().getType().equalsIgnoreCase("Query")) {
					request.getSession().setAttribute(
							"para" + (i + 1),
							paramDAO.listValuesOfParameters(rp
									.getReportsParams()));
				}
			}
		}

		return mapping.findForward(ReportsConstants.ADDLISTREPORTSUSERPARAMS);
	}

	/**
	 * Generate report in given export format
	 */
	public ActionForward processReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		logger.debug("In ReportsUserParamsAction:processReport Method: ");
		ReportsUserParamsActionForm actionForm = (ReportsUserParamsActionForm) form;
		int reportId = actionForm.getReportId();
		String applPath = actionForm.getApplPath();
		String expType = actionForm.getExpFormat();
		String expFilename = reportsBusinessService.runReport(reportId,
				request, applPath, expType);
		request.getSession().setAttribute("expFileName", expFilename);
		actionForm.setExpFileName(expFilename);
		String forward = "";
		String error = (String) request.getSession().getAttribute("paramerror");
		if (error == null || error.equals("")) {
			forward = ReportsConstants.PROCESSREPORTSUSERPARAMS;
		}
		else {
			forward = ReportsConstants.ADDLISTREPORTSUSERPARAMS;
		}
		return mapping.findForward(forward);
	}

	private static List<ReportsBO> getNewUploadedReport() {
		List<ReportsBO> newReports = new ArrayList<ReportsBO>();
		for (ReportsBO report : new ReportsPersistence().getAllReports()) {
			if (report.getActivityId() < 0) {
				newReports.add(report);
			}
		}
		return newReports;
	}
}
