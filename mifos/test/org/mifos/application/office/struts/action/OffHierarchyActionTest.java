/**

 * OffHierarchyActionTest.java    version: 1.0

 

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
package org.mifos.application.office.struts.action;

import java.net.URISyntaxException;
import java.util.List;

import org.mifos.application.office.business.OfficeLevelEntity;
import org.mifos.application.office.persistence.OfficeHierarchyPersistence;
import org.mifos.application.office.struts.actionforms.OffHierarchyActionForm;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class OffHierarchyActionTest extends MifosMockStrutsTestCase {

	private static final int OFFICE_LEVELS = 5;

	private static final String CONFIGURED = "1";

	public OffHierarchyActionTest() {
		super();
	}

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/office/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		UserContext userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
	}

	@Override
	protected void tearDown()throws Exception{			
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testLoad() throws PageExpiredException {
		setRequestPathInfo("/offhierarchyaction.do");
		addRequestParameter("method", "load");

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());

		List<OfficeLevelEntity> officeLevels = (List<OfficeLevelEntity>) SessionUtils
				.getAttribute(OfficeConstants.OFFICE_LEVELS, request);
		assertEquals(OFFICE_LEVELS, officeLevels.size());
		for (OfficeLevelEntity officeLevelEntity : officeLevels) {
			assertTrue(officeLevelEntity.isConfigured());
		}

		OffHierarchyActionForm offHierarchyActionForm = (OffHierarchyActionForm) request
				.getSession().getAttribute("offhierarchyactionform");
		assertEquals(CONFIGURED, offHierarchyActionForm.getHeadOffice());
		assertEquals(CONFIGURED, offHierarchyActionForm.getRegionalOffice());
		assertEquals(CONFIGURED, offHierarchyActionForm.getSubRegionalOffice());
		assertEquals(CONFIGURED, offHierarchyActionForm.getAreaOffice());
		assertEquals(CONFIGURED, offHierarchyActionForm.getBranchOffice());
	}

	public void testUpdate() throws Exception {
		setRequestPathInfo("/offhierarchyaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
		setRequestPathInfo("/offhierarchyaction.do");
		addRequestParameter("method", "update");
		addRequestParameter("regionalOffice", CONFIGURED);
		addRequestParameter("areaOffice", CONFIGURED);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());

		UserContext userContext = (UserContext) request.getSession()
				.getAttribute(Constants.USERCONTEXT);
		List<OfficeLevelEntity> officeLevels = new OfficeHierarchyPersistence()
				.getOfficeLevels(userContext.getLocaleId());

		assertEquals(OFFICE_LEVELS, officeLevels.size());
		for (OfficeLevelEntity officeLevelEntity : officeLevels) {
			if (officeLevelEntity.getLevel().equals(
					OfficeLevel.SUBREGIONALOFFICE))
				assertFalse(officeLevelEntity.isConfigured());
			else
				assertTrue(officeLevelEntity.isConfigured());
		}

		resetData();
	}

	public void testCancel() throws PageExpiredException {
		setRequestPathInfo("/offhierarchyaction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancel_success.toString());
	}

	public void testCancelForPageExpiration() throws PageExpiredException {
		setRequestPathInfo("/offhierarchyaction.do");
		addRequestParameter("method", "cancel");

		actionPerform();
		verifyActionErrors(new String[] { "exception.framework.PageExpiredException" });
		verifyForwardPath("/pages/framework/jsp/pageexpirederror.jsp");
	}

	public void testUpdateForPageExpiration() throws PageExpiredException {
		setRequestPathInfo("/offhierarchyaction.do");
		addRequestParameter("method", "update");

		actionPerform();
		verifyActionErrors(new String[] { "exception.framework.PageExpiredException" });
		verifyForwardPath("/pages/framework/jsp/pageexpirederror.jsp");
	}

	private void resetData()throws Exception {
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		OfficeLevelEntity officeLevelEntity = (OfficeLevelEntity) HibernateUtil
				.getSessionTL().get(OfficeLevelEntity.class,
						OfficeLevel.SUBREGIONALOFFICE.getValue());
		officeLevelEntity.update(true);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

	}

}
