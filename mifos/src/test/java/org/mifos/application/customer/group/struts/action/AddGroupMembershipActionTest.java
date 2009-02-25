/**

 * AddGroupMembershipActionTest.java    version: xxx



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

package org.mifos.application.customer.group.struts.action;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.NameType;
import org.mifos.application.customer.client.persistence.ClientPersistence;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AddGroupMembershipActionTest extends MifosMockStrutsTestCase{

	private CenterBO center;
	private GroupBO group;
	private ClientBO client;
	private MeetingBO meeting;
	private OfficeBO office;
	
	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UserContext userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		FlowManager flowManager = new FlowManager();
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);		
		
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

		flowKey = createFlow(request, AddGroupMembershipAction.class);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(office);
		HibernateUtil.closeSession();
		super.tearDown();
	}
		
	public void testSuccessfulPrevious() throws Exception {
		setRequestPathInfo("/addGroupMembershipAction.do");
		addRequestParameter("method", "loadSearch");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.loadSearch_success.toString().toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		
	}
	public void testCancel() throws Exception {
		
		setRequestPathInfo("/addGroupMembershipAction.do");
		addRequestParameter("method", "cancel");
	
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.cancel_success.toString());
		verifyNoActionErrors();
		
	}
	public void testPreviewParentAddClient() throws Exception {
		
		setRequestPathInfo("/addGroupMembershipAction.do");
		addRequestParameter("method", "previewParentAddClient");
	
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.confirmAddClientToGroup_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		
	}
	
	public void testSuccessfulUpdateParent() throws Exception {
		createAndSetClientInSession();
		createParentGroup();
		assertEquals(false, client.isClientUnderGroup());
		assertNotSame(group.getCustomerMeeting().getMeeting().getMeetingId(),client.getCustomerMeeting().getMeeting().getMeetingId());

		setRequestPathInfo("/addGroupMembershipAction.do");
		addRequestParameter("method", "updateParent");
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
		addRequestParameter("parentGroupId", group.getCustomerId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertNotNull(client);
		assertEquals(client.getCustomerMeeting().getMeeting().getMeetingId(), group.getCustomerMeeting().getMeeting().getMeetingId());
	
		assertEquals(true, client.isClientUnderGroup());
		verifyNoActionMessages();
		verifyForward(ActionForwards.view_client_details_page.toString());
		verifyNoActionErrors();
		
	}
	private void createParentGroup() {
		Short officeId = 3;
		Short personnel = 3;
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		group = TestObjectFactory.createGroupUnderBranch("group1", CustomerStatus.GROUP_ACTIVE,
				officeId, meeting, personnel);
		
	}
	private void createAndSetClientInSession() throws Exception {
		OfficeBO office = new OfficePersistence().getOffice(TestObjectFactory.HEAD_OFFICE);
		PersonnelBO personnel = new PersonnelPersistence().getPersonnel(PersonnelConstants.TEST_USER);
		meeting = getMeeting();
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(
				NameType.CLIENT, 1, "Client", "", "1",
				"");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(
				NameType.SPOUSE, 1, "first",
				"middle", "last", "secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1, 1, 1, 1, 1,
				1, Short.valueOf("1"), Short.valueOf("1"),Short.valueOf("41"));
		client = new ClientBO(TestUtils.makeUser(),
				clientNameDetailView.getDisplayName(), CustomerStatus
						.fromInt(new Short("1")), null, null, new Address(),
				getCustomFields(), null, null, personnel, office, meeting,
				personnel, new java.util.Date(), null, null, null, YesNoFlag.NO
						.getValue(), clientNameDetailView,
				spouseNameDetailView, clientDetailView, null);
		new ClientPersistence().saveClient(client);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getClient(Integer.valueOf(client.getCustomerId()).intValue());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
	}
	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		return meeting;
	}



	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(Short.valueOf("5"), "value1",
				CustomFieldType.ALPHA_NUMERIC));
		fields.add(new CustomFieldView(Short.valueOf("6"), "value2",
				CustomFieldType.ALPHA_NUMERIC));
		return fields;
	}


}
