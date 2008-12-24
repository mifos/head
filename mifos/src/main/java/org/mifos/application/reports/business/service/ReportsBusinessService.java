/**

 * ReportsBusinessService.java    version: 1.0

 

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

package org.mifos.application.reports.business.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsDataSource;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.business.ReportsParams;
import org.mifos.application.reports.business.ReportsParamsMap;
import org.mifos.application.reports.business.ReportsParamsMapValue;
import org.mifos.application.reports.business.ReportsParamsValue;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;

/**
 * This class encapsulates all the business logic related to report module.
 * Where there is no logic, it is OK to bypass this class and call
 * {@link ReportsPersistence} directly.
 */
public class ReportsBusinessService extends BusinessService {
	
	public static final String PDF = "pdf";

	private ReportsPersistence reportsPersistence = new ReportsPersistence();

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return new ReportsBO(userContext);
	}
	
	/**
	 * Create Report Parameter
	 */
	public String createReportsParams(ReportsParamsValue objParams)
	throws ApplicationException {
		String error = "";
		boolean isInUse = false;
		List<ReportsParams> reportsParams = new ReportsPersistence().getAllReportParams();
		Object[] obj = reportsParams.toArray();
		if(obj!=null && obj.length>0)
		{
			for(int i=0;i<obj.length;i++)
			{
				ReportsParams rp = (ReportsParams)obj[i];
				if(rp.getName().equalsIgnoreCase(objParams.getName()))
				{
					isInUse  = true;
					break;
				}
			}
		}
		if(objParams.getName()==null || objParams.getName().equals("") || isInUse)
			error = "Parameter Name is blank or has been already Used";
		else if(objParams.getDescription()==null || objParams.getDescription().equals("") || isInUse)
			error = "Description cannot be blank";
		else
			reportsPersistence.createReportParams(objParams);
		return error;
	}

	/**
	 * Delete Report Parameter
	 */
	public String deleteReportsParams(ReportsParamsValue objParams)
	throws SystemException, ApplicationException {
		List<ReportsParamsMap> reportParamsMap = 
			reportsPersistence.findInUseParameter(objParams.getParameterId());
		if (reportParamsMap!=null && reportParamsMap.size()==0) {
			reportsPersistence.deleteReportParams(objParams);
			return "";
		}
		else {
			return "Parameter in Use";
		}
	}
	
	/**
	 * Delete Report DataSource
	 */
	public String deleteReportsDataSource(ReportsDataSource objDataSource)
	throws SystemException, ApplicationException {
		List<ReportsParams> reportParams = 
			reportsPersistence.findInUseDataSource(objDataSource.getDatasourceId());
		if(reportParams!=null && reportParams.size()==0) {
			reportsPersistence.deleteReportsDataSource(objDataSource);
			return "";
		}
		else {
			return "DataSource in Use";
		}
	}
	
	/**
	 * Create a link between report and parameter
	 */
	public String createReportsParamsMap(ReportsParamsMapValue objReportParamsMapValue)
	throws SystemException, ApplicationException{
		if (objReportParamsMapValue.getParameterId() == 0) {
			return "No Parameter is selected";
		}
		else if (objReportParamsMapValue.getReportId() ==0) {
			return "No Report Selected";
		}
		else {
			reportsPersistence.createReportsParamsMap(objReportParamsMapValue);
			return "";
		}
	}

	public String runReport(int reportId, HttpServletRequest request,
			String applPath, String exportType)
	throws ServiceException, PersistenceException {
		String exportFileName="";
		String error = "";
		Connection conn =null;
		List<ReportsJasperMap> reportJasperMap = 
			reportsPersistence.findJasperOfReportId(reportId);
		ReportsJasperMap rjm = null;
		Object[] obj = reportJasperMap.toArray();
		if(obj!=null && obj.length>0)
		{
			rjm = (ReportsJasperMap) obj[0];  
		}
		List<ReportsParamsMap> reportParams = (List) request.getSession()
			.getAttribute("listOfAllParametersForReportId");
		obj = reportParams.toArray();
		Map parameters = new HashMap();
		
		if(obj!=null && obj.length>0)
		{
			
			for(int i=0;i<obj.length;i++)
			{
				ReportsParamsMap rp = (ReportsParamsMap) obj[i];
				String paramname = rp.getReportsParams().getName();
				int para=0;
				double dblpara=0;
				String paramvalue = request.getParameter(paramname)==null?"":request.getParameter(paramname);
				String type = rp.getReportsParams().getClassname();
				if(type.equalsIgnoreCase("java.lang.Integer"))
				{
					paramvalue= paramvalue.equals("")?"0":paramvalue;
					try{
						para = Integer.parseInt(paramvalue);
						parameters.put(paramname,para);
					}catch(Exception e){
						error = "Not a valid Integer";
					}
					
				}
				else if(type.equalsIgnoreCase("java.lang.Double"))
				{
					paramvalue= paramvalue.equals("")?"0":paramvalue;
					try{
					dblpara = Double.parseDouble(paramvalue);
					parameters.put(paramname,dblpara);
					}catch(Exception e)
					{
						error = "Not a Valid Double";
					}
				}
				else
					parameters.put(paramname,paramvalue);
				
			
			}
			request.getSession().setAttribute("paramerror",error);
			if(error.equals(""))
			{
				try{
					String jaspername = "";
					if(rjm!=null)
						jaspername = rjm.getReportJasper()==null?"":rjm.getReportJasper();
					jaspername = jaspername.replaceAll(".jasper",".jrxml");
					conn = reportsPersistence.getJasperConnection();
					
					String fullpath = applPath+jaspername;
					JasperDesign jasperDesign = JRXmlLoader.load(fullpath);
					JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
					jaspername = jaspername.replaceAll(".jrxml",".jasper");
					fullpath = applPath+jaspername;
					JRSaver.saveObject(jasperReport,fullpath);
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,  parameters, conn);
					if(exportType.equalsIgnoreCase(PDF))
					{
						exportFileName = "Rep"+reportId+"_"+jaspername.replaceAll(".jasper",".pdf");
						JasperExportManager.exportReportToPdfFile(jasperPrint,applPath+exportFileName);
					}
					else
					{
						exportFileName = "Rep"+reportId+"_"+jaspername.replaceAll(".jasper",".html");
						JasperExportManager.exportReportToHtmlFile(jasperPrint,applPath+exportFileName);
					}
					
				}catch(Exception e)
				{
					throw new RuntimeException(e);
				}
				finally{
					try{
						// FIXME: why is this commented out? Looks like a
						// potential connection leak.
						/*if(conn!=null)
							conn.close();*/
					}catch(Exception e)
					{
						throw new RuntimeException(e);
					}
				}
			} 
			
		}
		else
		{	
			try{
				String jaspername = "";
				if(rjm!=null)
					jaspername = rjm.getReportJasper()==null?"":rjm.getReportJasper();
				jaspername = jaspername.replaceAll(".jasper",".jrxml");
				conn = reportsPersistence.getJasperConnection();
				
				String fullpath = applPath+jaspername;
				JasperDesign jasperDesign = JRXmlLoader.load(fullpath);
				JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
				jaspername = jaspername.replaceAll(".jrxml",".jasper");
				fullpath = applPath+jaspername;
				JRSaver.saveObject(jasperReport,fullpath);
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,  parameters, conn);
				if(exportType.equalsIgnoreCase(PDF))
				{
					exportFileName = "Rep"+reportId+"_"+jaspername.replaceAll(".jasper",".pdf");
					JasperExportManager.exportReportToPdfFile(jasperPrint,applPath+exportFileName);
				}
				else
				{
					exportFileName = "Rep"+reportId+"_"+jaspername.replaceAll(".jasper",".html");
					JasperExportManager.exportReportToHtmlFile(jasperPrint,applPath+exportFileName);
				}
				
			}catch(Exception e)
			{
				throw new RuntimeException(e);
			}
			finally{
				try{
					/*if(conn!=null)
						conn.close();*/
				}catch(Exception se)
				{
					throw new RuntimeException(se);
				}
			}
			
		}	
			return exportFileName;
	
	}
}
