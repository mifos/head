/**

 * CustomFieldsActionTest.java

 

 * Copyright (c) 2005-2007 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2007 Grameen Foundation USA 
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

package org.mifos.application.configuration.struts.action;

import org.mifos.application.configuration.struts.actionform.CustomFieldsActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomFieldsActionTest extends MifosMockStrutsTestCase {

	private UserContext userContext;
	private String flowKey;

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/configuration/struts-config.xml")
				.getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlowAndAddToRequest(CustomFieldsAction.class);

	}

	public void testLoad() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "load");
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		CustomFieldsActionForm customFieldsActionForm = (CustomFieldsActionForm) request.getSession()
				.getAttribute("customfieldsactionform");
		/*
		 * TODO: verify that data loads correctly
		 */
	}

	public void testCancel() {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "cancel");

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancel_success.toString());
	}


	public void testUpdate() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "update");

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());

		/*
		 * TODO: verify that data updates correctly
		 */
	}

}
