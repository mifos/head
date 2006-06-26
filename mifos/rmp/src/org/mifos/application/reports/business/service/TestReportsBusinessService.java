/**

 * TestReportsBusinessService.java    version: 1.0

 

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

import java.util.Date;
import java.util.List;


import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.business.ReportsParams;
import org.mifos.application.reports.business.ReportsParamsMap;
import org.mifos.application.reports.business.ReportsParamsMapValue;
import org.mifos.application.reports.business.ReportsParamsValue;
import org.mifos.application.reports.business.ReportsDataSource;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.persistence.service.ReportsPersistenceService;

import junit.framework.TestCase;

/**
 * This class hosts the test cases for ReportsBusinessService.
 * @author zankar
 *
 */
public class TestReportsBusinessService extends TestCase {
	
	private ReportsBusinessService reportsBusinessService;
	/**
	 * Tests All Report Categories
	 * @throws Exception
	 */
	public void testGetAllReportCategories()throws Exception{
		
		reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);
		List<ReportsCategoryBO> reportCategoryBOS = reportsBusinessService.getAllReportCategories();
		for(ReportsCategoryBO reportCategoryBO : reportCategoryBOS){
			assertEquals(reportCategoryBO.getReportCategoryName(),"Category Name");
			assertEquals(reportCategoryBO.getReportCategoryId(),"Category Id");
			break;
		}
	}
	/**
	 * Test of listing all report Parameters
	 * @throws Exception
	 */
	public void testGetAllReportParams()throws Exception{
		
		reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);
		List<ReportsParams> reportParams = reportsBusinessService.getAllReportParams();
		for(ReportsParams reportParas : reportParams){
			assertEquals(reportParas.getName(),"PArameter Name");
			assertEquals(reportParas.getDescription(),"Description");
			break;
		}
	}
	/**
	 * Test method for creating report Parameter
	 * @throws Exception
	 */
	public void testCreateReportsParams()throws Exception{
			
			reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);
			ReportsParamsValue objval = new ReportsParamsValue();
			objval.setName("Parameter1");
			objval.setDescription("Parameter1-Root PAram");
			objval.setClassname("java.lang.String");
			objval.setDatasourceId(1);
			
			reportsBusinessService.createReportsParams(objval);
		}
	
		/**
		 * Test method for deleting a parameter by setting id
		 * @throws Exception
		 */
	
	public void testDeleteReportsParams()throws Exception{
		
		reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);
		ReportsParamsValue objval = new ReportsParamsValue();
		objval.setParameterId(1);
		reportsBusinessService.deleteReportsParams(objval);
	}
	/**
	 * Test of listing all report Datasource
	 * @throws Exception
	 */
	public void testGetAllReportDataSource()throws Exception{
		
		reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);
		List<ReportsDataSource> reportDataSource = reportsBusinessService.getAllReportDataSource();
		for(ReportsDataSource reportDS : reportDataSource){
			assertEquals(reportDS.getName(),"DataSource Name");
			assertEquals(reportDS.getUrl(),"URL");
			break;
		}
	}
	/**
	 * Test method for creating report DataSource
	 * @throws Exception
	 */
	public void testCreateReportsDataSource()throws Exception{
		
		reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);
		ReportsDataSource objval = new ReportsDataSource();
		objval.setName("DS1");
		objval.setUrl("org.gjt.mm.mysql.Driver");
		objval.setDriver("jdbc:mysql://localhost:3306/mifos");
		objval.setUsername("root");
		objval.setPassword("");
		
		reportsBusinessService.createReportsDataSource(objval);
	}
	
	/**
	 * Test method for deleting a datasource by setting id
	 * @throws Exception
	 */
	
	public void testDeleteReportsDataSource()throws Exception{
	
	reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);
	ReportsDataSource objval = new ReportsDataSource();
	objval.setDatasourceId(1);
	reportsBusinessService.deleteReportsDataSource(objval);
	}
	/**
	 * Test list of all Report Params Map
	 * @throws Exception
	 */
	
	public void testGetAllReportParamsMap()throws Exception{
		
		List<ReportsParamsMap> reportParamsMap = reportsBusinessService.getAllReportParamsMap();
		for(ReportsParamsMap reportPM : reportParamsMap){
			assertEquals(reportPM.getReportsParams().getName(),"Parameter Name");
			assertEquals(reportPM.getReportId(),"Report Id");
			break;
		}
	}
	
	/**
	 * Tests to find the parameters of a given report
	 * @throws Exception
	 */
	public void testfindParamsOfReportId()throws Exception{
		List<ReportsParamsMap> reportParamsMap = reportsBusinessService.findParamsOfReportId(1);
		for(ReportsParamsMap reportPM : reportParamsMap){
			assertEquals(reportPM.getReportsParams().getName(),"Parameter Name");
			assertEquals(reportPM.getReportId(),"Report Id");
			break;
		}
	}
	
	/**
	 * Tests to find whether datasource is in use
	 * @throws Exception
	 */
	
	public void testfindInUseDataSource()throws Exception{
		List<ReportsParams> reportParams = reportsBusinessService.findInUseDataSource(1);
		for(ReportsParams reportPM : reportParams){
			assertEquals(reportPM.getReportsDataSource().getName(),"DataSource Name");
			break;
		}
	}
	/**
	 * Tests to find whether parameter is in use
	 * @throws Exception
	 */
	public void testfindInUseParameter()throws Exception{
		List<ReportsParamsMap> reportParamsMap = reportsBusinessService.findInUseParameter(1);
		for(ReportsParamsMap reportPM : reportParamsMap){
			assertEquals(reportPM.getReportsParams().getName(),"Parameter Name");
			assertEquals(reportPM.getReportId(),"Report Id");
			break;
		}
	}
	/**
	 * Test method for creating a link btw report and a parameter
	 * @throws Exception
	 */
	public void testCreateReportsParamsMap()throws Exception{
		
		ReportsParamsMapValue objval = new ReportsParamsMapValue();
		objval.setParameterId(1);
		objval.setReportId(1);
		reportsBusinessService.createReportsParamsMap(objval);
	}
	
	/**
	 * Test method for deleting a link btw report and a parameter
	 * @throws Exception
	 */
	
	public void testDeleteReportsParamsMap()throws Exception{
	
		ReportsParamsMapValue objval = new ReportsParamsMapValue();
		objval.setParameterId(1);
		objval.setReportId(1);
		reportsBusinessService.deleteReportsParamsMap(objval);
	}
	
	/**
	 * Test method for setting a link between report and a jasper file
	 * @throws Exception
	 */
	
	public void testUpdateReportsJasperMap()throws Exception{
	
		ReportsJasperMap objval = new ReportsJasperMap();
		objval.setReportJasper("report1.jasper");
		objval.setReportId((short)1);
		reportsBusinessService.updateReportsJasperMap(objval);
	}
	
	/**
	 * Tests to find the jasper of a given report
	 * @throws Exception
	 */
	public void testfindJasperOfReportId()throws Exception{
		List<ReportsJasperMap> reportJasperMap = reportsBusinessService.findJasperOfReportId(1);
		for(ReportsJasperMap reportJ : reportJasperMap){
			assertEquals(reportJ.getReportJasper(),"Parameter Name");
			break;
		}
	}

	/**
	 * Tests View  DataSource
	 * @throws Exception
	 */	
	public void testViewDataSource()throws Exception{
		
		reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);
		List<ReportsDataSource> reportDataSource = reportsBusinessService.viewDataSource(1);
		for(ReportsDataSource reportDS : reportDataSource){
			assertEquals(reportDS.getName(),"DataSource Name");
			assertEquals(reportDS.getUrl(),"URL");
			break;
		}
	}
	/**
	 * Test View Parameter
	 * @throws Exception
	 */
	public void testViewParameter()throws Exception{
		
		reportsBusinessService = (ReportsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.ReportsService);
		List<ReportsParams> reportParams = reportsBusinessService.viewParameter(1);
		for(ReportsParams reportParas : reportParams){
			assertEquals(reportParas.getName(),"PArameter Name");
			assertEquals(reportParas.getDescription(),"Description");
			break;
		}
	}
}
