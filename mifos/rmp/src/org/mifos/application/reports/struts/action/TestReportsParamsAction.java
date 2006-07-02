/**

 * TestReportsParamsAction.java    version: 1.0

 

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
 * Test Case for ReportsParametersAction
 * @author zankar
 *
 */
public class TestReportsParamsAction extends MockStrutsTestCase {
	private UserContext userContext ;
	
	public TestReportsParamsAction() {
	}

	public TestReportsParamsAction(String name) {
		super(name);
	}
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI(
					"WEB-INF/web.xml").getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/framework/util/helpers/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		userContext.setBranchGlobalNum("0001");
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		
	}
	/**
	 * Test case for creating parameters
	 *
	 */
	public void testCreateParams(){
		setRequestPathInfo("/reportsParamsAction.do");
		addRequestParameter("method","createParams");
        actionPerform();
		verifyForwardPath("/reportsParamsAction.do?method=loadList");
	}
	/**
	 * Test acse for deletion of paramters
	 *
	 */
	public void testDeleteParams(){
		setRequestPathInfo("/reportsParamsAction.do");
		addRequestParameter("method","deleteParams");
        actionPerform();
		verifyForwardPath("/reportsParamsAction.do?method=loadList");
	}
	
	/**
	 * loads parameter add page
	 *
	 */
	public void testLoad()
	{
		setRequestPathInfo("/reportsParamsAction.do");
		addRequestParameter("method","load");
        actionPerform();
		verifyForwardPath("/pages/application/reports/jsp/report_designer_params_add.jsp");
	}
	/**
	 * loads parameter list page
	 *
	 */
	public void testLoadList()
	{
		setRequestPathInfo("/reportsParamsAction.do");
		addRequestParameter("method","loadList");
        actionPerform();
		verifyForwardPath("/pages/application/reports/jsp/report_designer_params_list.jsp");
	}
}

