/**

 * ReportsUploadAction.java    version: 1.0

 

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

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JRJdtCompiler;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.view.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.xml.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.util.JRSaver;


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
import org.mifos.application.reports.business.ReportsJasperMap;
/**
 * Control Class for Uploading Report
 * @author zankar
 *
 */


public class ReportsUploadAction extends BaseAction{
	 
	private ReportsBusinessService reportsBusinessService ;
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public ReportsUploadAction() throws ServiceException {
		reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);		
	}
	
	protected BusinessService getService() {
		return reportsBusinessService;
	}
	
	/**
	 * Uploads the Report
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	
	public ActionForward uploadReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)	throws Exception {
		logger.debug("In ReportsUploadAction:uploadReport Method: ");
		try
		{
			String filename = request.getParameter("filename")==null?"":request.getParameter("filename");
			String strreportId = request.getParameter("reportId")==null || request.getParameter("reportId").equals("")?"0":request.getParameter("reportId");
			int reportId = Integer.parseInt(strreportId);
			File reportUploadFile = new File(filename);
				if (reportUploadFile != null)
				{
					String reportUploadFileName = reportUploadFile.getAbsolutePath();
					if ((reportUploadFileName.endsWith(".xml")) || (reportUploadFileName.endsWith(".jrxml")))
					{
						try
						{
							System.setProperty(JRProperties.COMPILER_CLASS, JRJdtCompiler.class.getName());
							JRProperties.setProperty(JRProperties.COMPILER_CLASS, JRJdtCompiler.class.getName());
							
							JasperCompileManager.compileReportToFile(reportUploadFileName);
						//	System.out.println("Jaseper File for "+reportUploadFileName+" Uploaded");
							
						}
						catch (Exception e)
						{
							if (e.toString().indexOf("groovy") > -1)
							{
								try
								{
									System.setProperty(JRProperties.COMPILER_CLASS,"net.sf.jasperreports.compilers.JRGroovyCompiler" );
				                    JRProperties.setProperty(JRProperties.COMPILER_CLASS,"net.sf.jasperreports.compilers.JRGroovyCompiler");
				                    
									JasperCompileManager.compileReportToFile(reportUploadFileName);
									 
								}
								catch(Exception ex)
								{
									System.out.println("Failed to compile report: " + e.toString());
								}
							}
							else
							{								
								System.out.println("Failed to compile report: " + e.toString());
							}
						}
					}
					String fname = reportUploadFile.getName();
					if(fname!=null)
						fname = fname.replaceAll(".jrxml",".jasper");
					
					ReportsJasperMap objmap = new ReportsJasperMap();
					objmap.setReportId((short)reportId);
					objmap.setReportJasper(fname);
					reportsBusinessService.updateReportsJasperMap(objmap);
					request.getSession().setAttribute(ReportsConstants.LISTOFREPORTS,reportsBusinessService.getAllReportCategories());
			    	
					
				
				}
				
				
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return mapping.findForward("administerreports_path");
	
	}

}
