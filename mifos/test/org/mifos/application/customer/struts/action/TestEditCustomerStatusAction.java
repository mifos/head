/**

* TestEditCustomerStatusAction.java version: 1.0



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

package org.mifos.application.customer.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestEditCustomerStatusAction extends MifosMockStrutsTestCase {
	
	private UserContext userContext;
	
	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;

	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
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

	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad(){
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId",center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param","Center");
		actionPerform();
		verifyForward(ActionForwards.load_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,request.getSession()));
		assertEquals("Size of the status list should be 2",1,((List<CustomerStatusEntity>)SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,request.getSession())).size());

	}
	
	public void testFailurePreviewWithAllValuesNull() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		getRequest().getSession().setAttribute("security_param","Center");
		actionPerform();
		assertEquals(2, getErrrorSize());
		assertEquals("Status id",1, getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		assertEquals("Notes",1, getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}
	
	public void testFailurePreviewWithFlagValueNull() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "11");
		getRequest().getSession().setAttribute("security_param","Center");
		actionPerform();
		assertEquals(2, getErrrorSize());
		assertEquals("flag id",1, getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		assertEquals("Notes",1, getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}
	
	public void testFailurePreviewWithNotesValueNull() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "11");
		addRequestParameter("flagId", "1");
		getRequest().getSession().setAttribute("security_param","Center");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes",1, getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testPreviewSuccess() {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId",center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param","Center");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,request.getSession()));
		assertEquals("Size of the status list should be 2",1,((List<AccountStateEntity>)SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,request.getSession())).size());
		
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", center.getCustomerLevel().getId().toString());
		addRequestParameter("newStatusId", "14");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param","Center");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Inactive",(String)SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME,request.getSession()));
		assertNull("Since new Status is not cancel,so flag should be null.",SessionUtils.getAttribute(SavingsConstants.FLAG_NAME,request.getSession()));
	}
	
	public void testUpdateCenterStatus() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId",center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param","Center");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,request.getSession()));
		assertEquals("Size of the status list should be 2",1,((List<AccountStateEntity>)SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,request.getSession())).size());
		
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", center.getCustomerLevel().getId().toString());
		addRequestParameter("newStatusId", "14");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param","Center");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Inactive",(String)SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME,request.getSession()));
		assertNull("Since new Status is not cancel,so flag should be null.",SessionUtils.getAttribute(SavingsConstants.FLAG_NAME,request.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param","Center");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.center_detail_page.toString());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class, center.getCustomerId());
		assertFalse(center.isCustomerActive());
	}
	
	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short
				.valueOf("13"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",Short
				.valueOf("3"), "1.1.1", group, new Date(System
				.currentTimeMillis()));
	}
	
}

