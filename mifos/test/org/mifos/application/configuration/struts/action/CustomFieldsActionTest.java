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
	//private String flowKey;
	
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
			if (listboxDataType.getId().equals(category.mapToEntityType().getValue()))
			{
				String name1 = listboxDataType.getName().toUpperCase();
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
	public void testFailurePreviewWithAllValuesNull() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "preview");
		actionPerform();
		assertEquals(4, getErrorSize());
		assertEquals("Category Type null value error", 1, getErrorSize("categoryType"));
		assertEquals("Data Type null value error", 1,
				getErrorSize("dataType"));
		assertEquals("Default Value null value error", 1,
				getErrorSize("defaultValue"));
		assertEquals("Label null value error", 1, getErrorSize("labelName"));
		verifyInputForward();
	}
	
	public void testFailurePreviewWithDataTypeNumericAndDefaultValueNotMatched() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.NUMERIC.getValue().toString()); 
		addRequestParameter("defaultValue", "Personnel");
		actionPerform();
		assertEquals(1, getErrorSize());
		verifyInputForward();
	}
	
	public void testSuccessfulPreviewWithDataTypeNumericAndDefaultValueMatched() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.NUMERIC.getValue().toString()); 
		addRequestParameter("defaultValue", "10");
		performNoErrors();
		verifyForward(ActionForwards.preview_success.toString());
	}
	
	public void testFailurePreviewWithDataTypeDateAndDefaultValueNotMatched() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.DATE.getValue().toString()); 
		addRequestParameter("defaultValue", "Personnel");
		actionPerform();
		assertEquals(1, getErrorSize());
		verifyInputForward();
	}
	
	public void testSuccessfulPreviewWithDataTypeDateAndDefaultValueMatched() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.DATE.getValue().toString()); 
		addRequestParameter("defaultValue", "01/12/2007");
		performNoErrors();
		verifyForward(ActionForwards.preview_success.toString());
	}
	
	public void testPreviewSuccessful() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.ALPHA_NUMERIC.getValue().toString());
		addRequestParameter("defaultValue", "Personnel");
		performNoErrors();
		verifyForward(ActionForwards.preview_success.toString());
	}
	
	public void testFailureEditPreviewWithDefaultValuesNull() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("dataType", CustomFieldType.NUMERIC.getValue().toString()); 
		actionPerform();
		assertEquals(2, getErrorSize());
		assertEquals("Default Value null value error", 1,
				getErrorSize("defaultValue"));
		assertEquals("Label null value error", 1, getErrorSize("labelName"));
		verifyInputForward();
	}
	
	public void testFailureEditPreviewWithDataTypeNumericAndDefaultValueNotMatched() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.NUMERIC.getValue().toString()); 
		addRequestParameter("defaultValue", "Personnel");
		actionPerform();
		assertEquals(1, getErrorSize());
		verifyInputForward();
	}
	
	public void testSuccessfulEditPreviewWithDataTypeNumericAndDefaultValueMatched() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.NUMERIC.getValue().toString()); 
		addRequestParameter("defaultValue", "10");
		performNoErrors();
		verifyForward(ActionForwards.editPreview_success.toString());
	}
	
	public void testFailureEditPreviewWithDataTypeDateAndDefaultValueNotMatched() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.DATE.getValue().toString()); 
		addRequestParameter("defaultValue", "Personnel");
		actionPerform();
		assertEquals(1, getErrorSize());
		verifyInputForward();
	}
	
	public void testSuccessfulEditPreviewWithDataTypeDateAndDefaultValueMatched() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.DATE.getValue().toString()); 
		addRequestParameter("defaultValue", "01/12/2007");
		performNoErrors();
		verifyForward(ActionForwards.editPreview_success.toString());
	}
	
	public void testEditPreviewSuccessful() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.ALPHA_NUMERIC.getValue().toString());
		addRequestParameter("defaultValue", "Personnel");
		performNoErrors();
		verifyForward(ActionForwards.editPreview_success.toString());
	}


	public void testLoad() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "load");
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
	}

	public void testCancel() {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "cancel");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancel_success.toString());
	}
	
	public void testCancelEdit() {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "cancelEdit");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancelEdit_success.toString());
	}
	
	public void testCancelCreate() {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "cancelCreate");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancelCreate_success.toString());
	}
	
	public void testPrevious() {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "previous");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.ALPHA_NUMERIC.getValue().toString());
		addRequestParameter("defaultValue", "Personnel");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previous_success.toString());
	}
	
	public void testEditPrevious() {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "editPrevious");
		addRequestParameter("categoryType", "Personnel");
		addRequestParameter("labelName", "Custom Field for Personnel");
		addRequestParameter("dataType", CustomFieldType.ALPHA_NUMERIC.getValue().toString());
		addRequestParameter("defaultValue", "Personnel");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.editprevious_success.toString());
	}
	
	public void testViewCategory() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "viewCategory");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.viewCategory_success.toString());

		/*
		 * TODO: verify that data displays correctly
		 */
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
	
	public void testCreate() throws Exception {
		setRequestPathInfo("/customFieldsAction.do");
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());

		/*
		 * TODO: verify that data updates correctly
		 */
	}

}
