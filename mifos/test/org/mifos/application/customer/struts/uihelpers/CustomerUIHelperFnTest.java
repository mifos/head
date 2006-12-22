/**

 * CustomerUIHelperFnTest.java version: 1.0



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

package org.mifos.application.customer.struts.uihelpers;

import java.util.List;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFlagDetailEntity;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.struts.action.EditCustomerStatusAction;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerUIHelperFnTest extends MifosMockStrutsTestCase {

	private CustomerBO center;

	private CustomerBO group;

	private CustomerBO client;

	private MeetingBO meeting;

	private String flowKey;

	UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/customer/struts-config.xml").getPath());

		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY,
				userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());

		flowKey = createFlow(request, EditCustomerStatusAction.class);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testUIHelperWhenClientIsAssignedPosition()
			throws CustomerException, PageExpiredException {
		createInitialObjects();
		PositionEntity positionEntity = (PositionEntity) TestObjectFactory
				.getObject(PositionEntity.class, Short.valueOf("1"));
		CustomerPositionEntity customerPositionEntity = new CustomerPositionEntity(
				positionEntity, client, client.getParentCustomer());
		group.addCustomerPosition(customerPositionEntity);
		group.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group.setUserContext(TestObjectFactory.getContext());
		for (CustomerPositionEntity customerPositionEntity2 : group
				.getCustomerPositions()) {
			customerPositionEntity2.getPosition().setLocaleId(
					TestObjectFactory.getContext().getLocaleId());
		}
		String positionName = CustomerUIHelperFn.getClientPosition(group
				.getCustomerPositions(), client);
		assertEquals("(Kendra Leader)", positionName);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", Methods.loadStatus.toString());
		addRequestParameter("customerId", client.getCustomerId().toString());
		actionPerform();
		verifyForward(ActionForwards.loadStatus_success.toString());
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request)).size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", Methods.previewStatus.toString());
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "6");
		addRequestParameter("flagId", "7");
		actionPerform();
		verifyForward(ActionForwards.previewStatus_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull(SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request));
		assertNotNull(
				"Since new Status is Closed,so flag should be Duplicate.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request));
		for (CustomerPositionEntity customerPosition : group
				.getCustomerPositions()) {
			assertNotNull(customerPosition.getCustomer());
			break;
		}
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", Methods.updateStatus.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.client_detail_page.toString());

		assertFalse(client.isActive());
		for (CustomerFlagDetailEntity customerFlagDetailEntity : client
				.getCustomerFlags()) {
			assertFalse(customerFlagDetailEntity.getStatusFlag()
					.isBlackListed());
			break;
		}
		for (CustomerPositionEntity customerPosition : group
				.getCustomerPositions()) {
			assertNull(customerPosition.getCustomer());
			break;
		}
	}

	public void testUIHelperWhenClientIsNotAssignedPosition()
			throws CustomerException, PageExpiredException {
		createInitialObjects();
		PositionEntity positionEntity = (PositionEntity) TestObjectFactory
				.getObject(PositionEntity.class, Short.valueOf("1"));
		CustomerPositionEntity customerPositionEntity = new CustomerPositionEntity(
				positionEntity, client, client.getParentCustomer());
		group.addCustomerPosition(customerPositionEntity);
		group.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group.setUserContext(TestObjectFactory.getContext());
		String positionName = CustomerUIHelperFn.getClientPosition(group
				.getCustomerPositions(), client);
		assertEquals("", positionName);
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", Methods.loadStatus.toString());
		addRequestParameter("customerId", client.getCustomerId().toString());
		actionPerform();
		verifyForward(ActionForwards.loadStatus_success.toString());
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request)).size());

		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", Methods.previewStatus.toString());
		addRequestParameter("notes", "Test");
		addRequestParameter("levelId", client.getCustomerLevel().getId()
				.toString());
		addRequestParameter("newStatusId", "6");
		addRequestParameter("flagId", "7");
		actionPerform();
		verifyForward(ActionForwards.previewStatus_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertNotNull(SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request));
		assertNotNull(
				"Since new Status is Closed,so flag should be Duplicate.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request));
		for (CustomerPositionEntity customerPosition : group
				.getCustomerPositions()) {
			assertNotNull(customerPosition.getCustomer());
			break;
		}
		setRequestPathInfo("/editCustomerStatusAction.do");
		addRequestParameter("method", Methods.updateStatus.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.client_detail_page.toString());

		assertFalse(client.isActive());
		for (CustomerFlagDetailEntity customerFlagDetailEntity : client
				.getCustomerFlags()) {
			assertFalse(customerFlagDetailEntity.getStatusFlag()
					.isBlackListed());
			break;
		}
		for (CustomerPositionEntity customerPosition : group
				.getCustomerPositions()) {
			assertNull(customerPosition.getCustomer());
			break;
		}
	}

	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center",
				meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

}
