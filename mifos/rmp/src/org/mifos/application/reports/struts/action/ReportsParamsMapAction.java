/**

 * ReportsParamsMapAction.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.application.reports.struts.actionforms.ReportsParamsMapActionForm;
import org.mifos.application.reports.business.ReportsParamsMap;
import org.mifos.application.reports.business.ReportsParamsMapValue;
/**
 * Control Class for Report Params 
 * @author zankar
 *
 */
public class ReportsParamsMapAction extends BaseAction {
	
	private ReportsBusinessService reportsBusinessService ;
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public ReportsParamsMapAction() throws ServiceException {
		reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);		
	}
	
	protected BusinessService getService() {
		return reportsBusinessService;
	}
	/**
	 * Loads the Parameter Map AddList page
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward loadAddList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)	throws Exception {
		logger.debug("In ReportsParamsMapAction:load Method: ");		
		request.getSession().setAttribute("listOfAllParameters", reportsBusinessService.getAllReportParams());
		ReportsParamsMapActionForm actionForm=(ReportsParamsMapActionForm)form;
		String strReportId = request.getParameter("reportId");
		if(strReportId==null)
			strReportId = actionForm.getReportId()+"";
		if(strReportId==null || strReportId.equals(""))
			strReportId = "0";
		int reportId = Integer.parseInt(strReportId);
		 actionForm.setReportId(reportId);
		 request.getSession().setAttribute("listOfAllParametersForReportId", reportsBusinessService.findParamsOfReportId(reportId));
    	return mapping.findForward(ReportsConstants.ADDLISTREPORTSPARAMSMAP);
	}
	
	
    /**
     * Controls the creation of a link between parameter and  a report
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    
    public ActionForward createParamsMap(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)	throws Exception {
		
    	logger.debug("In ReportsParamsAction:createParamsMap Method: ");
    	ReportsParamsMapActionForm actionForm=(ReportsParamsMapActionForm)form;
    	ReportsParamsMapValue objParams = new ReportsParamsMapValue();
    	objParams.setReportId(actionForm.getReportId());
    	objParams.setParameterId(actionForm.getParameterId());
    	String error = reportsBusinessService.createReportsParamsMap(objParams);
    	request.getSession().setAttribute("addError",error);
		return mapping.findForward("reportparamsmap_path");
	}
    /**
     * Controls the deletion of a link between Parameter and a report
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
public ActionForward deleteParamsMap(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)	throws Exception {
		
    	logger.debug("In ReportsParamsAction:deleteParams Method: ");
    	ReportsParamsMapActionForm actionForm=(ReportsParamsMapActionForm)form;
    	ReportsParamsMapValue objParams = new ReportsParamsMapValue();
    	objParams.setMapId(actionForm.getMapId());
    	reportsBusinessService.deleteReportsParamsMap(objParams);
		return mapping.findForward("reportparamsmap_path");
	}
    
   
    


}

