/**

 * TestReportsAction.java    version: 1.0

 

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

import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import servletunit.struts.MockStrutsTestCase;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
/**
 * Test Case for ReportsAction
 */
public class TestReportsAction extends MockStrutsTestCase {
	private UserContext userContext ;
	
	public TestReportsAction() {
	}

	public TestReportsAction(String name) {
		super(name);
	}
	
	
	public void setUp() throws Exception{ 
		
		super.setUp(); 
		
	}

    public void tearDown() throws Exception { super.tearDown(); }

	
	public void testVerifyForwardOfReport(){
		addRequestParameter("viewPath", "report_designer");
		setRequestPathInfo("/reportsAction.do");
		addRequestParameter("method","getReportPage");
        actionPerform();
		verifyForwardPath("/pages/application/reports/jsp/report_designer.jsp");
	}
	
	public void testGetAdminReportPage()
	{
		addRequestParameter("viewPath", "administerreports_path");
		setRequestPathInfo("/reportsAction.do");
		addRequestParameter("method","getReportAdminPage");
        actionPerform();
		verifyForwardPath("/pages/application/reports/jsp/report_designer.jsp");
	}
	
	public void testLoad()
	{
		setRequestPathInfo("/reportsAction.do");
		addRequestParameter("method","load");
        actionPerform();
		verifyForwardPath("/pages/application/reports/jsp/reports.jsp");
	}
}
