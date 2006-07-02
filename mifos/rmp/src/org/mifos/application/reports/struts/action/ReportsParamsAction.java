/**

 * ReportsParamAction.java    version: 1.0

 

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
import org.mifos.application.reports.struts.actionforms.ReportsParamsActionForm;
import org.mifos.application.reports.struts.actionforms.ReportsParamsMapActionForm;
import org.mifos.application.reports.business.ReportsParams;
import org.mifos.application.reports.business.ReportsParamsValue;
/**
 * Control Class for Report Params 
 * @author zankar
 *
 */
public class ReportsParamsAction extends BaseAction {
	
	private ReportsBusinessService reportsBusinessService ;
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public ReportsParamsAction() throws ServiceException {
		reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);		
	}
	
	protected BusinessService getService() {
		return reportsBusinessService;
	}
	/**
	 * Loads the Parameter Add page
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)	throws Exception {
		logger.debug("In ReportsParamsAction:load Method: ");		
		request.getSession().setAttribute("listOfReportsDataSource", reportsBusinessService.getAllReportDataSource());
		return mapping.findForward(ReportsConstants.ADDREPORTSPARAMS);
	}
	/**
	 * Loads Parameter List Page
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward loadList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)	throws Exception {
		logger.debug("In ReportsParamsAction:loadList Method: ");		
		request.getSession().setAttribute("listOfReportsParams", reportsBusinessService.getAllReportParams());
		return mapping.findForward(ReportsConstants.LISTREPORTSPARAMS);
	}
	/**
	 * Veiw parameter
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward loadView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)	throws Exception {
		logger.debug("In ReportsParamsAction:loadView Method: ");		
		ReportsParamsActionForm actionForm=(ReportsParamsActionForm)form;
		String strParameterId = request.getParameter("parameterId");
		if(strParameterId==null)
			strParameterId = actionForm.getParameterId()+"";
		if(strParameterId==null || strParameterId.equals(""))
			strParameterId = "0";
		int parameterId = Integer.parseInt(strParameterId);
		 actionForm.setParameterId(parameterId);
		request.getSession().setAttribute("viewParams", reportsBusinessService.viewParameter(parameterId));
		return mapping.findForward(ReportsConstants.VIEWREPORTSPARAMS);
	}
	
    /**
     * Controls the creation of Parameter 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    
    public ActionForward createParams(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)	throws Exception {
		
    	logger.debug("In ReportsParamsAction:createParams Method: ");
    	ReportsParamsActionForm actionForm=(ReportsParamsActionForm)form;
    	ReportsParamsValue objParams = new ReportsParamsValue();
    	objParams.setClassname(actionForm.getClassname());
    	objParams.setData(actionForm.getData());
    	objParams.setDatasourceId(actionForm.getDatasourceId());
    	objParams.setDescription(actionForm.getDescription());
    	objParams.setName(actionForm.getName());
    	objParams.setType(actionForm.getType());
    	String error = reportsBusinessService.createReportsParams(objParams);
    	request.getSession().setAttribute("addError",error);
    	String forward = "";
    	if(error!=null && !error.equals(""))
    		forward = "reportparamsadd_path";
    	else
    		forward = "reportparams_path";
    	return mapping.findForward(forward);
	}
    /**
     * Controls the deletion of Parameter
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
public ActionForward deleteParams(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)	throws Exception {
		
    	logger.debug("In ReportsParamsAction:deleteParams Method: ");
    	ReportsParamsActionForm actionForm=(ReportsParamsActionForm)form;
    	ReportsParamsValue objParams = new ReportsParamsValue();
    	objParams.setParameterId(actionForm.getParameterId());
    	String error = reportsBusinessService.deleteReportsParams(objParams);
    	request.getSession().setAttribute("deleteError",error);
		return mapping.findForward("reportparams_path");
	}
    
   
    


}
