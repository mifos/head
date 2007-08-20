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

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldCategory;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
//import org.mifos.application.configuration.struts.actionform.CustomFieldsActionForm;
import org.mifos.application.configuration.struts.action.CustomFieldsAction;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.configuration.util.helpers.CustomFieldsListBoxData;

public class CustomFieldsActionTest extends MifosMockStrutsTestCase {

	private UserContext userContext;
	private final Short DEFAULT_LOCALE = 1;
	
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
		createFlowAndAddToRequest(CustomFieldsAction.class);

	}
	
	private boolean findDataType(CustomFieldType dataType, List<CustomFieldsListBoxData> listboxDataTypes,
			Locale locale)
	{
		for (CustomFieldsListBoxData listboxDataType : listboxDataTypes)
		{
			String dataTypeName = MessageLookup.getInstance().lookup(dataType, locale);
			if ((listboxDataType.getId().equals(dataType.getValue()))
				&& (listboxDataType.getName().toUpperCase().equals(dataTypeName.toUpperCase())))
				return true;
		}
		return false;
	}
	
	private boolean findCategory(CustomFieldCategory category, List<CustomFieldsListBoxData> allCategories)
	{
		for (CustomFieldsListBoxData listboxDataType : allCategories)
		{
			// will debug this later 
			if (listboxDataType.getId().equals(category.mapToEntityType().getValue()))
			{
				String name1 = listboxDataType.getName().toUpperCase();
				//String name2 = category.name().toUpperCase();
				String name2 = MessageLookup.getInstance().lookupLabel(category.name(), DEFAULT_LOCALE).toUpperCase();
				if (name1.equals(name2))
					return true;
			}
		}
		return false;
	}
	
	protected Locale getUserLocale(HttpServletRequest request) {
		Locale locale = null;
		HttpSession session = request.getSession();
		if (session != null) {
			UserContext userContext = (UserContext) session
					.getAttribute(LoginConstants.USERCONTEXT);
			if (null != userContext) {
				locale = userContext.getPreferredLocale();
				if (null == locale) {
					locale = userContext.getMfiLocale();
				}
			}
		}
		return locale;
	}
	
	public void testLoadDefineCustomFields() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "loadDefineCustomFields");
		performNoErrors();
		verifyForward(ActionForwards.loadDefineCustomFields_success.toString());
		// verify datatypes
		List<CustomFieldsListBoxData> listboxDataTypes = (List<CustomFieldsListBoxData>)SessionUtils.getAttribute(ConfigurationConstants.ALL_DATA_TYPES, request);
		assertTrue(listboxDataTypes.size() == CustomFieldType.values().length);
		Locale locale = getUserLocale(request);
		for (CustomFieldType dataType : CustomFieldType.values()) {
			assertTrue(findDataType(dataType, listboxDataTypes, locale));
		}
		List<CustomFieldsListBoxData> allCategories = 
			(List<CustomFieldsListBoxData>)SessionUtils.getAttribute(ConfigurationConstants.ALL_CATEGORIES, request);
		assertTrue(allCategories.size() == CustomFieldCategory.values().length);
		for (CustomFieldCategory category : CustomFieldCategory.values()) {
			assertTrue(findCategory(category, allCategories));
		}
		 
	}

	public void testLoad() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "load");
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		/*
		 * TODO: verify that data loads correctly
		 * CustomFieldsActionForm customFieldsActionForm = (CustomFieldsActionForm) request.getSession()
		 * .getAttribute("customfieldsactionform");
		 * ???
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
