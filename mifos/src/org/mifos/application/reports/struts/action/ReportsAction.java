/**

 * ReportsAction.java    version: 1.0

 

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.reports.business.service.ReportsBusinessService;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;

public class ReportsAction extends BaseAction {
	
	private ReportsBusinessService reportsBusinessService ;
	private MifosLogger logger = 
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public ReportsAction() throws ServiceException {
		reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);		
	}
	
	@Override
	protected BusinessService getService() {
		return reportsBusinessService;
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("reportsAction");
		security.allow("load", SecurityConstants.VIEW);
		security.allow("report_designer", SecurityConstants.CLIENTSDETAILVIEW);
		security
				.allow("product_history", SecurityConstants.CLIENTSPRODUCTHISTORY);

		security.allow("branch_performance", SecurityConstants.BRANCHPERFORMANCE);
		security.allow("area_performance", SecurityConstants.AREAPERFORMANCE);
		security.allow("collection_sheet", SecurityConstants.COLLECTIONSHEET);
		security.allow("loan_distribution", SecurityConstants.LOANDISTRIBUTION);
		security.allow("branch_disbursement",
				SecurityConstants.BRANCHDISBURSEMENT);
		security.allow("staffwise_report", SecurityConstants.STAFFWISEREPORT);
		security.allow("branchwise_report", SecurityConstants.BRANCHWISEREPORT);
		security.allow("analysis", SecurityConstants.ANALYSIS);
		security.allow("kendra_meeting", SecurityConstants.KENDRA_MEETING);
		security.allow("administerreports_path",
				SecurityConstants.ADMINISTER_REPORTS);
		security.allow("administerreportslist_path",
				SecurityConstants.ADMINISTER_REPORTS);
		return security;
		
	}
	
	/**
	 * loads report page
	 */
	public ActionForward load(ActionMapping mapping, ActionForm form, 
		HttpServletRequest request, HttpServletResponse response)	
	throws Exception {
		logger.debug("In ReportsAction:load Method: ");
		HibernateUtil.flushAndCloseSession();
		request.getSession().setAttribute(ReportsConstants.LISTOFREPORTS,new ReportsPersistence().getAllReportCategories());
		return mapping.findForward(Constants.LOAD_SUCCESS);
	}
	
	public ActionForward getReportPage(ActionMapping mapping, ActionForm form, 
		HttpServletRequest request, HttpServletResponse response) 
	throws Exception {	
		logger.debug("In ReportsAction:getReportPage Method: ");		
		return mapping.findForward(request.getParameter("viewPath"));		
	}

	public ActionForward getAdminReportPage(ActionMapping mapping, 
		ActionForm form, 
		HttpServletRequest request, HttpServletResponse response)
    throws Exception {
    	logger.debug("In ReportsAction:getAdminReportPage Method: ");
    	return mapping.findForward("administerreports_path");
    }

}
