/**

 * TestCustomerNotesAction.java version: 1.0



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

package org.mifos.application.customer.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
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
import org.mifos.framework.util.valueobjects.Context;

public class TestCustomerNotesAction extends MifosMockStrutsTestCase {

	private UserContext userContext;

	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;

	@Override
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

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testFailurePreviewWithNotesValueNull() throws Exception {
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.ERROR_MANDATORY_TEXT_AREA));
		verifyInputForward();
	}

	public void testFailurePreviewWithNotesValueExceedingMaxLength()
			throws Exception {
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(
				"comment",
				"Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters "
						+ "Testing for comment length exceeding by 500 characters "
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters"
						+ "Testing for comment length exceeding by 500 characters");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MAXIMUM_LENGTH));
		verifyInputForward();
	}

	public void testPreviewSuccess() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Test");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreviousSuccess() {
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "previous");
		addRequestParameter("comment", "Test");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("previous_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancelSuccess() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("load_success");
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "cancel");
		actionPerform();
		verifyForward(ActionForwards.center_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCreate_CenterNotes() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("comment", "Notes created");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward(ActionForwards.center_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(1, center.getCustomerNotes().size());
		center = (CenterBO) (HibernateUtil.getSessionTL().get(CenterBO.class,
				new Integer(center.getCustomerId())));
	}

	public void testLoadForClient() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testFailurePreviewWithNotesValueNullForClient()
			throws Exception {
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.ERROR_MANDATORY_TEXT_AREA));
		verifyInputForward();
	}

	public void testPreviewSuccessForClient() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Test");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreviousSuccessForClient() {
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "previous");
		addRequestParameter("comment", "Test");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("previous_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancelSuccessForClient() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "cancel");
		actionPerform();
		verifyForward(ActionForwards.client_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCreateNotesForClient() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("comment", "Notes created");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward(ActionForwards.client_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(1, client.getRecentCustomerNotes().size());
		assertEquals(1, client.getCustomerNotes().size());
		client = (ClientBO) (HibernateUtil.getSessionTL().get(ClientBO.class,
				new Integer(client.getCustomerId())));
	}

	public void testSearch() throws Exception {
		Context context = new Context();
		SessionUtils.setAttribute(Constants.CONTEXT, context, request
				.getSession());
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "create");
		getRequest().getSession().setAttribute("security_param", "Center");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "search");
		actionPerform();
		verifyForward("search_success");
		verifyNoActionErrors();
		verifyNoActionMessages();

		context = (Context) SessionUtils.getAttribute(Constants.CONTEXT,
				request.getSession());
		assertEquals("Size of the search result should be 1", 1, context
				.getSearchResult().getSize());
	}

	public void testLoadForGroup() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Group");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testFailurePreviewWithNotesValueNullForGroup() throws Exception {
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		getRequest().getSession().setAttribute("security_param", "Group");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.ERROR_MANDATORY_TEXT_AREA));
		verifyInputForward();
	}

	public void testPreviewSuccessForGroup() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Test");
		getRequest().getSession().setAttribute("security_param", "Group");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreviousSuccessForGroup() {
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "previous");
		addRequestParameter("comment", "Test");
		getRequest().getSession().setAttribute("security_param", "Group");
		actionPerform();
		verifyForward("previous_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancelSuccessForGroup() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Group");
		actionPerform();
		verifyForward("load_success");
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "cancel");
		actionPerform();
		verifyForward(ActionForwards.group_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCreateNotesForGroup() {
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Group");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("comment", "Notes created");
		getRequest().getSession().setAttribute("security_param", "Group");
		actionPerform();
		verifyForward(ActionForwards.group_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals(1, group.getRecentCustomerNotes().size());
		assertEquals(1, group.getCustomerNotes().size());
		group = (GroupBO) (HibernateUtil.getSessionTL().get(GroupBO.class,
				new Integer(group.getCustomerId())));
	}

	public void testSearchForGroup() throws Exception {
		Context context = new Context();
		SessionUtils.setAttribute(Constants.CONTEXT, context, request
				.getSession());
		createInitialObjects();
		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", group.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Group");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "create");
		getRequest().getSession().setAttribute("security_param", "Group");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/customerNotesAction.do");
		addRequestParameter("method", "search");
		actionPerform();
		verifyForward("search_success");
		verifyNoActionErrors();
		verifyNoActionMessages();

		context = (Context) SessionUtils.getAttribute(Constants.CONTEXT,
				request.getSession());
		assertEquals("Size of the search result should be 1", 1, context
				.getSearchResult().getSize());
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("13"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", Short.valueOf("3"),
				"1.1.1", group, new Date(System.currentTimeMillis()));
	}

}
