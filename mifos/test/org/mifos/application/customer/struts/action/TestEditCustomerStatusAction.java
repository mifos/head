/**

 * TestEditCustomerStatusAction.java version: 1.0



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
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFlagDetailEntity;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestEditCustomerStatusAction extends MifosMockStrutsTestCase {

	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;

	private AccountBO accountBO;

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
		UserContext userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
	}

	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward(ActionForwards.load_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 1,
				((List<CustomerStatusEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

	}

	public void testFailurePreviewWithAllValuesNull() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		assertEquals(2, getErrrorSize());
		assertEquals("Status id", 1,
				getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testFailurePreviewWithFlagValueNull() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "11");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		assertEquals(2, getErrrorSize());
		assertEquals("flag id", 1,
				getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testFailurePreviewWithNotesValueNull() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "11");
		addRequestParameter("flagId", "1");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}
	
	public void testFailurePreviewWithNotesValueExceedingMaxLength() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "14");
		addRequestParameter("flagId", "");
		addRequestParameter("notes", "Testing for comment length exceeding by 500 characters" +
							"Testing for comment length exceeding by 500 characters" +"Testing for comment length exceeding by 500 characters" +
							"Testing for comment length exceeding by 500 characters" +"Testing for comment length exceeding by 500 characters " +
							"Testing for comment length exceeding by 500 characters " +"Testing for comment length exceeding by 500 characters" +
							"Testing for comment length exceeding by 500 characters" +"Testing for comment length exceeding by 500 characters" +
							"Testing for comment length exceeding by 500 characters" +"Testing for comment length exceeding by 500 characters");
		getRequest().getSession().setAttribute("security_param","Center");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes",1, getErrrorSize(CustomerConstants.MAXIMUM_LENGTH));
		verifyInputForward();
	}
	public void testPreviewSuccess() {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 1,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", center.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "14");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Inactive", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not cancel,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
	}

	public void testUpdateCenterStatus() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", center.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 1,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", center.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "14");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Inactive", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not cancel,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Center");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.center_detail_page.toString());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		assertFalse(center.isActive());
	}

	public void testLoadForClient() {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward(ActionForwards.load_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<CustomerStatusEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

	}

	public void testFailurePreviewWithAllValuesNullForClient() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		assertEquals(2, getErrrorSize());
		assertEquals("Status id", 1,
				getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testFailurePreviewWithFlagValueNullForCLient() throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "6");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		assertEquals(2, getErrrorSize());
		assertEquals("flag id", 1,
				getErrrorSize(CustomerConstants.MANDATORY_SELECT));
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testFailurePreviewWithNotesValueNullForClient()
			throws Exception {
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("newStatusId", "6");
		addRequestParameter("flagId", "10");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Notes", 1,
				getErrrorSize(CustomerConstants.MANDATORY_TEXTBOX));
		verifyInputForward();
	}

	public void testPreviewSuccessForClient() {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "6");
		addRequestParameter("flagId", "10");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Closed", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertEquals("Since new Status is Closed,so flag should not be null.",
				"Other", SessionUtils.getAttribute(SavingsConstants.FLAG_NAME,
						request.getSession()));
	}

	public void testUpdateStatusForClient() throws CustomerException {
		createInitialObjects();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "4");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("On Hold", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not Closed,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.client_detail_page.toString());
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertFalse(client.isActive());
	}

	public void testUpdateStatusForClientForFirstTimeActive()
			throws CustomerException {
		createInitialObjectsWhenClientIsNotActive();
		assertTrue(((ClientBO) client).getCustomerAccount()
				.getAccountActionDates().isEmpty());
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "3");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Active", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not Closed,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.client_detail_page.toString());
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertTrue(client.isActive());
		assertFalse(((ClientBO) client).getCustomerAccount()
				.getAccountActionDates().isEmpty());
		assertEquals("ActivationDate should be the current date.",DateUtils.getDateWithoutTimeStamp(new java.util.Date().getTime()),DateUtils.getDateWithoutTimeStamp(client.getCustomerActivationDate().getTime()));
	}

	public void testUpdateStatusForClientForActiveLoanOfficer()
			throws CustomerException {
		createInitialObjects();
		client.setCustomerStatus(new CustomerStatusEntity(
				CustomerStatus.CLIENT_PARTIAL.getValue()));
		client.update();
		HibernateUtil.commitTransaction();

		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "3");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Active", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not Closed,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.client_detail_page.toString());
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertTrue(client.isActive());
	}

	public void testUpdateStatusForClientWhenParentCustomerIsInPartialState()
			throws CustomerException {
		createInitialObjectsWhenGroupIsInPartialState();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "3");
		addRequestParameter("flagId", "");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Active", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertNull("Since new Status is not Closed,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyActionErrors(new String[] { ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION });
		verifyForward(ActionForwards.update_failure.toString());
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertFalse(client.isActive());
	}

	public void testUpdateStatusForClientWhenClientHasActiveAccounts()
			throws CustomerException {
		createInitialObjects();
		accountBO = getLoanAccount();
		client.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "6");
		addRequestParameter("flagId", "7");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Closed", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertEquals("Since new Status is Closed,so flag should be Duplicate.",
				"Duplicate", SessionUtils.getAttribute(
						SavingsConstants.FLAG_NAME, request.getSession()));
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyActionErrors(new String[] { CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION });
		verifyForward(ActionForwards.update_failure.toString());
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	public void testUpdateStatusForClientWhenClientIsAssignedPosition()
			throws CustomerException {
		createInitialObjects();
		CustomerPositionEntity customerPositionEntity = new CustomerPositionEntity(
				new PositionEntity(Short.valueOf("1")), client, client
						.getParentCustomer());
		client.addCustomerPosition(customerPositionEntity);
		client.update();
		HibernateUtil.commitTransaction();
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("customerId", client.getCustomerId().toString());
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request.getSession()));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request.getSession()))
						.size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "6");
		addRequestParameter("flagId", "7");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Closed", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request.getSession()));
		assertEquals("Since new Status is Closed,so flag should be Duplicate.",
				"Duplicate", SessionUtils.getAttribute(
						SavingsConstants.FLAG_NAME, request.getSession()));
		for (CustomerPositionEntity customerPosition : client
				.getCustomerPositions()) {
			assertNotNull(customerPosition.getCustomer());
			break;
		}
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", "update");
		getRequest().getSession().setAttribute("security_param", "Client");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.client_detail_page.toString());
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		assertFalse(client.isActive());
		for(CustomerFlagDetailEntity customerFlagDetailEntity : client.getCustomerFlags()) {
			assertFalse(customerFlagDetailEntity.getStatusFlag().isBlackListed());
			break;
		}
		for (CustomerPositionEntity customerPosition : client
				.getCustomerPositions()) {
			assertNull(customerPosition.getCustomer());
			break;
		}
	}

	private void createInitialObjectsWhenGroupIsInPartialState() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("7"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", Short.valueOf("1"),
				"1.1.1", group, new Date(System.currentTimeMillis()));
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

	private void createInitialObjectsWhenClientIsNotActive() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("13"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", Short.valueOf("1"),
				"1.1.1", group, new Date(System.currentTimeMillis()));
	}

	private AccountBO getLoanAccount() {
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		return TestObjectFactory.createLoanAccount("42423142341", client, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}
}
