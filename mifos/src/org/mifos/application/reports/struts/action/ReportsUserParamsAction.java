/**

 * ReportsUserParamAction.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied.  

 *

 */

package org.mifos.application.reports.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
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

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("reportsUserParamsAction");

		security.put("reportuserparamslist_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		security.put("loadAddList", SecurityConstants.ADMINISTER_REPORTPARAMS);
		security
				.put("processReport", SecurityConstants.ADMINISTER_REPORTPARAMS);
		security.put("reportsuserprocess_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);

		security.put("reportsuserprocess_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		security.put("reportsuserprocess_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		return security;
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

}
