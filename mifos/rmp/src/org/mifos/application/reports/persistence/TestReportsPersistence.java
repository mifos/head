/**

 * TestReportsPersistence.java    version: 1.0

 

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
package org.mifos.application.reports.persistence;


import java.util.List;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.ReportsParams;
import org.mifos.application.reports.business.ReportsParamsValue;
import org.mifos.application.reports.business.ReportsParamsMap;
import org.mifos.application.reports.business.ReportsParamsMapValue;
import org.mifos.application.reports.business.ReportsDataSource;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.business.ReportsJasperMap;
import junit.framework.TestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.UserContext;


/**
 * This class hosts the test cases for ReportsPersistence.
 * @author zankar
 *
 */
public class TestReportsPersistence extends TestCase {
	
	private  ReportsPersistence reportsPersistence;
	/**
	 * Constructor 
	 *
	 */
	/*public TestReportsPersistence()
	{
		
	}*/
	public void setUp() throws Exception {
		super.setUp();
		reportsPersistence = new ReportsPersistence();
	}
	public void tearDown() {
		HibernateUtil.closeSession();

	}

	/**
	 * Tests All Report Categories
	 * @throws Exception
	 */
/*	public void testGetAllReportCategories()throws Exception{
		
		
		List<ReportsCategoryBO> reportCategoryBOS = reportsPersistence.getAllReportCategories();
		for(ReportsCategoryBO reportCategoryBO : reportCategoryBOS){
			assertEquals(reportCategoryBO.getReportCategoryName(),"Category Name");
			assertEquals(reportCategoryBO.getReportCategoryId(),"Category Id");
			break;
		}
	} */
	/**
	 * Test list of all Reprot Parameters
	 * @throws Exception
	 */
	
	/*public void testGetAllReportParams()throws Exception{
		
		List<ReportsParams> reportParams = reportsPersistence.getAllReportParams();
		for(ReportsParams reportParas : reportParams){
			assertEquals(reportParas.getName(),"Parameter Name");
			assertEquals(reportParas.getDescription(),"Description");
			break;
		}
	}*/
	/**
	 * Test method for creating report Parameter
	 * @throws Exception
	 */
	public void testCreateReportsParams()throws Exception{
		
		
		//UserContext uc = new UserContext();
		//uc.setId(Short.valueOf("1"));
		ReportsParamsValue objval = new ReportsParamsValue();
		objval.setName("Parameter1");
		objval.setDescription("Parameter1-Root PAram");
		objval.setClassname("java.lang.String");
		objval.setDatasourceId(5);
		
		reportsPersistence.createReportParams(objval);
	}

	/**
	 * Test method for deleting a parameter by setting id
	 * @throws Exception
	 */

	/*public void testDeleteReportsParams()throws Exception{
	
		ReportsParamsValue objval = new ReportsParamsValue();
		objval.setParameterId(1);
		reportsPersistence.deleteReportParams(objval);
	}*/


	/**
	 * Test list of all Reprot DataSource
	 * @throws Exception
	 */

	/*public void testGetAllReportDataSource()throws Exception{
		
		List<ReportsDataSource> reportDataSource = reportsPersistence.getAllReportDataSource();
		for(ReportsDataSource reportDS : reportDataSource){
			assertEquals(reportDS.getName(),"DataSource Name");
			assertEquals(reportDS.getUrl(),"URL");
			break;
		}
	}*/
	/**
	 * Test method for creating report DataSource
	 * @throws Exception
	 */
	/*public void testCreateReportsDataSource()throws Exception{
		
		ReportsDataSource objval = new ReportsDataSource();
		objval.setName("DS1");
		objval.setUrl("org.gjt.mm.mysql.Driver");
		objval.setDriver("jdbc:mysql://localhost:3306/mifos");
		objval.setUsername("root");
		objval.setPassword("");
		reportsPersistence.createReportsDataSource(objval);
	}*/

	/**
	 * Test method for deleting a datasource by setting id
	 * @throws Exception
	 */
	
	/*public void testDeleteReportsDataSource()throws Exception{
	
		ReportsDataSource objval = new ReportsDataSource();
		objval.setDatasourceId(1);
		reportsPersistence.deleteReportsDataSource(objval);
	}*/
	
	
	/**
	 * Test list of all Report Params Map
	 * @throws Exception
	 */

	/*public void testGetAllReportParamsMap()throws Exception{
		
		List<ReportsParamsMap> reportParamsMap = reportsPersistence.getAllReportParamsMap();
		for(ReportsParamsMap reportPM : reportParamsMap){
			assertEquals(reportPM.getReportsParams().getName(),"Parameter Name");
			assertEquals(reportPM.getReportId(),"Report Id");
			break;
		}
	}*/
	
	/**
	 * Tests to find the parameters of a given report
	 * @throws Exception
	 */
	/*public void testfindParamsOfReportId()throws Exception{
		List<ReportsParamsMap> reportParamsMap = reportsPersistence.findParamsOfReportId(1);
		for(ReportsParamsMap reportPM : reportParamsMap){
			assertEquals(reportPM.getReportsParams().getName(),"Parameter Name");
			assertEquals(reportPM.getReportId(),"Report Id");
			break;
		}
	}*/
	/**
	 * Tests to find whether datasource is in use
	 * @throws Exception
	 */
	/*public void testfindInUseDatatSource()throws Exception{
		List<ReportsParams> reportParams = reportsPersistence.findInUseDataSource(1);
		for(ReportsParams reportPM : reportParams){
			assertEquals(reportPM.getReportsDataSource().getName(),"DataSource Name");
			break;
		}
	}*/
	/**
	 * Tests to find whether parameter is in use
	 * @throws Exception
	 */
	/*public void testfindInUseParameter()throws Exception{
		List<ReportsParamsMap> reportParamsMap = reportsPersistence.findInUseParameter(1);
		for(ReportsParamsMap reportPM : reportParamsMap){
			assertEquals(reportPM.getReportsParams().getName(),"Parameter Name");
			assertEquals(reportPM.getReportId(),"Report Id");
			break;
		}
	}*/
	/**
	 * Test method for creating a link btw report and a parameter
	 * @throws Exception
	 */
	/*public void testCreateReportsParamsMap()throws Exception{
		
		ReportsParamsMapValue objval = new ReportsParamsMapValue();
		objval.setParameterId(1);
		objval.setReportId(1);
		reportsPersistence.createReportsParamsMap(objval);
	}*/

	/**
	 * Test method for deleting a link btw report and a parameter
	 * @throws Exception
	 */
	
	/*public void testDeleteReportsParamsMap()throws Exception{
	
		ReportsParamsMapValue objval = new ReportsParamsMapValue();
		objval.setParameterId(1);
		objval.setReportId(1);
		reportsPersistence.deleteReportsParamsMap(objval);
	}*/
	
	
	/**
	 * Test method for setting a link between report and a jasper file
	 * @throws Exception
	 */
	
/*	public void testUpdateReportsJasperMap()throws Exception{
	
		ReportsJasperMap objval = new ReportsJasperMap();
		objval.setReportJasper("report1.jasper");
		objval.setReportId((short)1);
		reportsPersistence.updateReportsJasperMap(objval);
	}
	*/
	/**
	 * Tests to find the jasper of a given report
	 * @throws Exception
	 */
/*	public void testfindJasperOfReportId()throws Exception{
		List<ReportsJasperMap> reportJasperMap = reportsPersistence.findJasperOfReportId(1);
		for(ReportsJasperMap reportJ : reportJasperMap){
			assertEquals(reportJ.getReportJasper(),"Parameter Name");
			break;
		}
	}*/
	
	/**
	 * Tests to view DataSource
	 * @throws Exception
	 */
/*	public void testViewDataSource()throws Exception{
		
		List<ReportsDataSource> reportDataSource = reportsPersistence.viewDataSource(1);
		for(ReportsDataSource reportDS : reportDataSource){
			assertEquals(reportDS.getName(),"DataSource Name");
			assertEquals(reportDS.getUrl(),"URL");
			break;
		}
	}
	*/
	/**
	 * Tests to View Parameters
	 * @throws Exception
	 */
	/*public void testViewParameter()throws Exception{
		
		List<ReportsParams> reportParams = reportsPersistence.viewParameter(1);
		for(ReportsParams reportParas : reportParams){
			assertEquals(reportParas.getName(),"PArameter Name");
			assertEquals(reportParas.getDescription(),"Description");
			break;
		}
	}*/
}
