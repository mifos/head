/**

 * TestReportsPersistenceService.java    version: 1.0

 

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
package org.mifos.application.reports.persistence.service;


import java.util.List;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.ReportsParams;
import org.mifos.application.reports.business.ReportsParamsValue;
import org.mifos.application.reports.business.ReportsParamsMap;
import org.mifos.application.reports.business.ReportsParamsMapValue;
import org.mifos.application.reports.business.ReportsDataSource;
import org.mifos.application.reports.business.ReportsJasperMap;

import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import junit.framework.TestCase;

/**
 * This class hosts the test cases for ReportsPersistence.
 * @author zankar
 *
 */
public class TestReportsPersistenceService extends TestCase {
	
	private ReportsPersistenceService reportsPersistenceService= new ReportsPersistenceService();
	/**
	 * Obtain ReportsPersistenceService object
	 * @return
	 * @throws ServiceException
	 */
	
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() {
		HibernateUtil.closeSession();

	}

	/**
	 * Tests All Report Categories
	 * @throws Exception
	 */
	public void testGetAllReportCategories()throws Exception{
		
		
		List<ReportsCategoryBO> reportCategoryBOS = reportsPersistenceService.getAllReportCategories();
		for(ReportsCategoryBO reportCategoryBO : reportCategoryBOS){
			assertEquals(reportCategoryBO.getReportCategoryName(),"Category Name");
			assertEquals(reportCategoryBO.getReportCategoryId(),"Category Id");
			break;
		}
	}
	/**
	 * Test method for listing all reports parameters
	 * @throws Exception
	 */
	
	public void testGetAllReportParams()throws Exception{
		
		List<ReportsParams> reportParams = reportsPersistenceService.getAllReportParams();
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
		
		ReportsParamsValue objval = new ReportsParamsValue();
		System.out.println("111 in param");
		objval.setName("Parameter1");
		System.out.println("222 in param");
		objval.setDescription("Parameter1-Root PAram");
		System.out.println("333 in param");
		objval.setClassname("java.lang.String");
		System.out.println("444 in param");
		objval.setDatasourceId(5);
		System.out.println("555 in param");
		reportsPersistenceService.createReportParams(objval);
	}

	/**
	 * Test method for deleting a parameter by setting id
	 * @throws Exception
	 */

public void testDeleteReportsParams()throws Exception{
	
	ReportsParamsValue objval = new ReportsParamsValue();
	objval.setParameterId(1);
	reportsPersistenceService.deleteReportParams(objval);
}
/**
 * Test method for listing all reports Datasource
 * @throws Exception
 */

public void testGetAllReportDataSource()throws Exception{
	
	List<ReportsDataSource> reportDataSource = reportsPersistenceService.getAllReportDataSource();
	for(ReportsDataSource reportDS : reportDataSource){
		assertEquals(reportDS.getName(),"Datasource Name");
		assertEquals(reportDS.getUrl(),"Url");
		break;
	}
}
/**
 * Test method for creating report DataSource
 * @throws Exception
 */
public void testCreateReportsDataSource()throws Exception{
	
	ReportsDataSource objval = new ReportsDataSource();
	objval.setName("DS1");
	objval.setUrl("org.gjt.mm.mysql.Driver");
	objval.setDriver("jdbc:mysql://localhost:3306/mifos");
	objval.setUsername("root");
	objval.setPassword("");
	
	reportsPersistenceService.createReportsDataSource(objval);
}

/**
 * Test method for deleting a Datasource by setting id
 * @throws Exception
 */

public void testDeleteReportsDataSource()throws Exception{

	ReportsDataSource objval = new ReportsDataSource();
	objval.setDatasourceId(1);
	reportsPersistenceService.deleteReportsDataSource(objval);
}
	/**
	 * Test method for listing all reports parameter maps
	 * @throws Exception
	 */
	
	public void testGetAllReportParamsMap()throws Exception{
		
		List<ReportsParamsMap> reportParamsMap = reportsPersistenceService.getAllReportParamsMap();
		for(ReportsParamsMap reportPM : reportParamsMap){
			assertEquals(reportPM.getReportsParams().getName(),"Parameter Name");
			assertEquals(reportPM.getReportId(),"Report Id");
			break;
		}
		
	}
	
	/**
	 * Test method for finding the parameter for a given reportid
	 * @throws Exception
	 */
	
	public void testfindParamsOfReportId()throws Exception{
		
		List<ReportsParamsMap> reportParamsMap = reportsPersistenceService.findParamsOfReportId(1);
		for(ReportsParamsMap reportPM : reportParamsMap){
			assertEquals(reportPM.getReportsParams().getName(),"Parameter Name");
			assertEquals(reportPM.getReportId(),"Report Id");
			break;
		}
		
	}
	
	/**
	 * Tests to find whether parameter is in use
	 * @throws Exception
	 */
	public void testfindInUseParameter()throws Exception{
		List<ReportsParamsMap> reportParamsMap = reportsPersistenceService.findInUseParameter(1);
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
		
		reportsPersistenceService.createReportsParamsMap(objval);
	}

	/**
	 * Test method for deleting a link btw report and a parameter
	 * @throws Exception
	 */
	
	public void testDeleteReportsParamsMap()throws Exception{
	
		ReportsParamsMapValue objval = new ReportsParamsMapValue();
		objval.setParameterId(1);
		objval.setReportId(1);
		reportsPersistenceService.deleteReportsParamsMap(objval);
		
	}
	
	/**
	 * Test method for setting a link between report and a jasper file
	 * @throws Exception
	 */
	
	public void testUpdateReportsJasperMap()throws Exception{
	
		ReportsJasperMap objval = new ReportsJasperMap();
		objval.setReportJasper("report1.jasper");
		objval.setReportId((short)1);
		reportsPersistenceService.updateReportsJasperMap(objval);
	}
	
	/**
	 * Tests to find the jasper of a given report
	 * @throws Exception
	 */
	public void testfindJasperOfReportId()throws Exception{
		List<ReportsJasperMap> reportJasperMap = reportsPersistenceService.findJasperOfReportId(1);
		for(ReportsJasperMap reportJ : reportJasperMap){
			assertEquals(reportJ.getReportJasper(),"Parameter Name");
			break;
		}
	}
	
	
}

