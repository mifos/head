/**

 * ReportsBusinessService.java    version: 1.0

 

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

package org.mifos.application.reports.business.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.JasperExportManager;

import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.ReportsParams;
import org.mifos.application.reports.business.ReportsParamsValue;
import org.mifos.application.reports.business.ReportsParamsMap;
import org.mifos.application.reports.business.ReportsParamsMapValue;
import org.mifos.application.reports.business.ReportsDataSource;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.persistence.service.ReportsPersistenceService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.PersistenceServiceName;

/**
 * This class encapsulates all the business logic related to report module 
 * @author zankar
 */

public class ReportsBusinessService extends BusinessService{
	
	private ReportsPersistenceService reportsPersistenceService;
	
	
	public BusinessObject getBusinessObject(UserContext userContext) {
		return new ReportsBO(userContext);
	}
	
	private ReportsPersistenceService getPersistenceService()throws ServiceException{
		if(reportsPersistenceService==null){
			reportsPersistenceService=(ReportsPersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Reports);
		}
		return reportsPersistenceService;
	}
	/**
	 * Lists all the Report Categories. 
	 * The list also contains the set of Reports within a particular category
	 * @return List
	 * @throws ServiceException
	 */
	public List<ReportsCategoryBO> getAllReportCategories() throws ServiceException{
			return getPersistenceService().getAllReportCategories();
	}
	
	/**
	 * List all Report Parameters
	 * @return
	 * @throws ServiceException
	 */
	public List<ReportsParams> getAllReportParams() throws ServiceException{
		return getPersistenceService().getAllReportParams();
	}
	
	
	
	/**
	 * Create Report Parameter
	 * @param objParams
	 * @throws ServiceException
	 */
	public String createReportsParams(ReportsParamsValue objParams)throws ServiceException{
		String error = "";
		boolean isInUse = false;
		List<ReportsParams> reportsParams = getAllReportParams();
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
			getPersistenceService().createReportParams(objParams);
		return error;
	}
	/**
	 * Delete Report Parameter
	 * @param objParams
	 * @throws ServiceException
	 */
	public String deleteReportsParams(ReportsParamsValue objParams)throws ServiceException{
		String error="";
		List<ReportsParamsMap> reportParamsMap = findInUseParameter(objParams.getParameterId());
		if(reportParamsMap!=null && reportParamsMap.size()==0)
		{
			getPersistenceService().deleteReportParams(objParams);
			error="";
		}
		else
			error = "Parameter in Use";
		return error;
	}
	
	/**
	 * List all DataSource
	 * @return
	 * @throws ServiceException
	 */
	public List<ReportsDataSource> getAllReportDataSource() throws ServiceException{
		return getPersistenceService().getAllReportDataSource();
	}
	
	/**
	 * Create Report DataSource
	 * @param objDataSource
	 * @throws ServiceException
	 */
	public void createReportsDataSource(ReportsDataSource objDataSource)throws ServiceException{
		getPersistenceService().createReportsDataSource(objDataSource);
	}
	/**
	 * Delete Report DataSource
	 * @param objDataSource
	 * @throws ServiceException
	 */
	public String deleteReportsDataSource(ReportsDataSource objDataSource)throws ServiceException{
		
		String error="";
		List<ReportsParams> reportParams = findInUseDataSource(objDataSource.getDatasourceId());
		if(reportParams!=null && reportParams.size()==0)
		{
			getPersistenceService().deleteReportsDataSource(objDataSource);
			error="";
		}
		else
			error = "DataSource in Use";
		return error;
		
		
	}
	
	/**
	 * List all Reports PArams Map
	 * @return
	 * @throws ServiceException
	 */
	public List<ReportsParamsMap> getAllReportParamsMap() throws ServiceException{
		return getPersistenceService().getAllReportParamsMap();
	}
	
	/**
	 * List all Parameters of given Report Id
	 * @return
	 * @throws ServiceException
	 */
	public List<ReportsParamsMap> findParamsOfReportId(int reportId) throws ServiceException{
		return getPersistenceService().findParamsOfReportId(reportId);
	}
	
	/**
	 * List all Parameters of given Report Id
	 * @return
	 * @throws ServiceException
	 */
	public List<ReportsParamsMap> findInUseParameter(int parameterId) throws ServiceException{
		return getPersistenceService().findInUseParameter(parameterId);
	}
	
	/**
	 * view datasource
	 * @return
	 * @throws ServiceException
	 */
	public List<ReportsDataSource> viewDataSource(int dataSourceId) throws ServiceException{
		return getPersistenceService().viewDataSource(dataSourceId);
	}
	/**
	 * view Parameer
	 * @return
	 * @throws ServiceException
	 */
	public List<ReportsParams> viewParameter(int parameterId) throws ServiceException{
		return getPersistenceService().viewParameter(parameterId);
	}
	/**
	 * List all DataSource of given Report Id
	 * @return
	 * @throws ServiceException
	 */
	public List<ReportsParams> findInUseDataSource(int dataSourceId) throws ServiceException{
		return getPersistenceService().findInUseDataSource(dataSourceId);
	}
	
	/**
	 * Create a link between report and parameter
	 * @param objReportParamsMapValue
	 * @throws ServiceException
	 */
	public String createReportsParamsMap(ReportsParamsMapValue objReportParamsMapValue)throws ServiceException{
		String error = "";
		if(objReportParamsMapValue.getParameterId()==0)
			error = "No Parameter is selected";
		else if(objReportParamsMapValue.getReportId()==0)
			error = "No Report Selected";
		else
			getPersistenceService().createReportsParamsMap(objReportParamsMapValue);
		return error;
	}
	/**
	 * Delete a link between report and parameter
	 * @param objReportParamsMapValue
	 * @throws ServiceException
	 */
	public void deleteReportsParamsMap(ReportsParamsMapValue objReportParamsMapValue)throws ServiceException{
		getPersistenceService().deleteReportsParamsMap(objReportParamsMapValue);
	}
	
	/**
     * Sets link between report and jasper report file
     * @param reportsJasperMap
     */
	public void updateReportsJasperMap(ReportsJasperMap reportsJasperMap)throws ServiceException{
		getPersistenceService().updateReportsJasperMap(reportsJasperMap);
		
	}
	
	/**
	 * Find Jasper of report 
	 * @param reportId
	 * @return
	 * @throws ServiceException
	 */
	public List<ReportsJasperMap> findJasperOfReportId(int reportId) throws ServiceException{
		return getPersistenceService().findJasperOfReportId(reportId);
	}
	/**
	 * Runs the Report 
	 * @param reportId
	 * @param request
	 * @param applPath
	 * @param exportType
	 * @return
	 * @throws ServiceException
	 */
	public String runReport(int reportId,HttpServletRequest request,String applPath,String exportType)throws ServiceException{
		String exportFileName="";
		String error = "";
		Connection conn =null;
		List<ReportsJasperMap> reportJasperMap = findJasperOfReportId(reportId);
		ReportsJasperMap rjm = null;
		Object[] obj = reportJasperMap.toArray();
		if(obj!=null && obj.length>0)
		{
			rjm = (ReportsJasperMap) obj[0];  
		}
		List<ReportsParamsMap> reportParams =(List) request.getSession().getAttribute("listOfAllParametersForReportId");
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
					conn = getPersistenceService().getJasperConnection();
					
					String fullpath = applPath+jaspername;
					JasperDesign jasperDesign = JRXmlLoader.load(fullpath);
					JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
					jaspername = jaspername.replaceAll(".jrxml",".jasper");
					fullpath = applPath+jaspername;
					JRSaver.saveObject(jasperReport,fullpath);
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,  parameters, conn);
					if(exportType.equalsIgnoreCase("pdf"))
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
					e.printStackTrace();
				}
				finally{
					try{
						/*if(conn!=null)
							conn.close();*/
					}catch(Exception se)
					{
						se.printStackTrace();
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
				conn = getPersistenceService().getJasperConnection();
				
				String fullpath = applPath+jaspername;
				JasperDesign jasperDesign = JRXmlLoader.load(fullpath);
				JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
				jaspername = jaspername.replaceAll(".jrxml",".jasper");
				fullpath = applPath+jaspername;
				JRSaver.saveObject(jasperReport,fullpath);
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,  parameters, conn);
				if(exportType.equalsIgnoreCase("pdf"))
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
				e.printStackTrace();
			}
			finally{
				try{
					/*if(conn!=null)
						conn.close();*/
				}catch(Exception se)
				{
					se.printStackTrace();
				}
			}
			
		}	
			return exportFileName;
	
	}
}
