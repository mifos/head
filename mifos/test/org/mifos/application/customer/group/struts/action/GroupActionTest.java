/**

 * GroupActionTest.java version: 1.0



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

import java.net.URISyntaxException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupActionTest extends MifosMockStrutsTestCase {
	private CenterBO center;

	private GroupBO group;

	private ClientBO client;

	private MeetingBO meeting;

	private String flowKey;

	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsOfferingBO savingsOffering;

	private LoanBO loanBO;

	private SavingsBO savingsBO;

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
		UserContext userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
		EntityMasterData.getInstance().init();
		FieldConfigItf fieldConfigItf = FieldConfigImplementer.getInstance();
		fieldConfigItf.init();
		FieldConfigImplementer.getInstance();
		getActionServlet().getServletContext().setAttribute(
				Constants.FIELD_CONFIGURATION,
				fieldConfigItf.getEntityMandatoryFieldMap());
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loanBO);
		TestObjectFactory.cleanUp(savingsBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);

		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGet() throws Exception {
		createCustomers();
		CustomerPositionEntity customerPositionEntity = new CustomerPositionEntity(
				new PositionEntity(Short.valueOf("1")), client, client
						.getParentCustomer());
		group.addCustomerPosition(customerPositionEntity);
		savingsBO = getSavingsAccount();
		loanBO = getLoanAccount();
		group.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", group.getGlobalCustNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.get_success.toString());

		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,
				new Integer(center.getCustomerId()).intValue());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class,
				new Integer(group.getCustomerId()).intValue());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class,
				new Integer(client.getCustomerId()).intValue());
		loanBO = (LoanBO) new AccountPersistanceService().getAccount(loanBO
				.getAccountId());
		savingsBO = (SavingsBO) new AccountPersistanceService()
				.getAccount(savingsBO.getAccountId());

		assertEquals("Size of active loan accounts should be 1", 1,
				((List<LoanBO>) SessionUtils.getAttribute(
						GroupConstants.GROUPLOANACCOUNTSINUSE, request)).size());
		assertEquals("Size of active savings accounts should be 1", 1,
				((List<SavingsBO>) SessionUtils.getAttribute(
						GroupConstants.GROUPSAVINGSACCOUNTSINUSE, request))
						.size());
		assertEquals("No of active clients should be 1", 1,
				((List<CustomerBO>) SessionUtils.getAttribute(
						GroupConstants.CLIENT_LIST, request)).size());
		for (CustomerPositionEntity customerPosition : group
				.getCustomerPositions()) {
			assertEquals("Kendra Leader", customerPosition.getPosition()
					.getName());
			break;
		}
		TestObjectFactory.removeCustomerFromPosition((CustomerBO) group);
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,
				new Integer(center.getCustomerId()).intValue());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class,
				new Integer(group.getCustomerId()).intValue());
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class,
				new Integer(client.getCustomerId()).intValue());
		loanBO = (LoanBO) new AccountPersistanceService().getAccount(loanBO
				.getAccountId());
		savingsBO = (SavingsBO) new AccountPersistanceService()
				.getAccount(savingsBO.getAccountId());
	}

	public void testManage() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.CUSTOM_FIELDS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CLIENT_LIST,
				request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.POSITIONS,
				request));
	}

	public void testManageWithoutCenterHierarchy() throws Exception {		
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CLIENT_LIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.POSITIONS,request));
	}
	
	public void testPreviewManageFailureForName() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trained", "1");
		addRequestParameter("trainedDate", "03/20/2006");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Group Name not present", 1,
				getErrrorSize(CustomerConstants.NAME));

	}

	public void testPreviewManageFailureForTrainedDate() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "group");
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trained", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Group Trained date not present", 1,
				getErrrorSize(CustomerConstants.TRAINED_DATE_MANDATORY));

	}

	public void testPreviewManageFailureForTrained() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "group");
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trainedDate", "03/20/2006");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Group Trained checkbox not checked ", 1,
				getErrrorSize(CustomerConstants.TRAINED_CHECKED));

	}

	
	public void testPreviewManageSuccess() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "group");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trained", "1");
		addRequestParameter("trainedDate", "03/20/2006");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previewManage_success.toString());
		
		
	}
	
	/*public void testUpdateSuccess() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "group_01");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trained", "1");
		addRequestParameter("trainedDate", "03/20/2006");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previewManage_success.toString());
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		
		
	}*/
	
	public void testCancelSuccess() throws Exception {
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("input", GroupConstants.PREVIEW_MANAGE_GROUP);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancelEdit_success.toString());
		
		
	}
	

	private void createGroupWithCenterAndSetInSession() throws Exception {
		createParentCustomer();
		group = TestObjectFactory.createGroup("group",
				CustomerStatus.GROUP_ACTIVE.getValue(), center.getSearchId()
						+ ".1", center, new Date());
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,
				new Integer(center.getCustomerId()).intValue());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class,
				new Integer(group.getCustomerId()).intValue());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
	}

	private void createParentCustomer() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center",
				CustomerStatus.CENTER_ACTIVE.getValue(), "1.4", meeting,
				new Date(System.currentTimeMillis()));

	}

	private MeetingBO getMeeting() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}

	private void createCustomers() {
		createParentCustomer();
		group = TestObjectFactory.createGroup("group",
				CustomerStatus.GROUP_ACTIVE.getValue(), center.getSearchId()
						+ ".1", center, new Date());
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE.getValue(), "1.1.1", group,
				new Date(System.currentTimeMillis()));
		HibernateUtil.closeSession();
	}

	private LoanBO getLoanAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), startDate, loanOffering);

	}

	private SavingsBO getSavingsAccount() {
		savingsOffering = helper.createSavingsOffering();
		return TestObjectFactory.createSavingsAccount("000100000000017", group,
				AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}
}
