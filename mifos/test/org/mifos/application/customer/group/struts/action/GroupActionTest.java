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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.struts.actionforms.CenterCustActionForm;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupActionTest extends MifosMockStrutsTestCase{
	private CenterBO center;
	private GroupBO group;
	private ClientBO client;
	private MeetingBO meeting;
	private String flowKey;
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
		FieldConfigItf fieldConfigItf=FieldConfigImplementer.getInstance();
		fieldConfigItf.init();		
		FieldConfigImplementer.getInstance();
		getActionServlet().getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,fieldConfigItf.getEntityMandatoryFieldMap());
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
	}

	
	public void testManage() throws Exception {		
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CLIENT_LIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.POSITIONS,request));
	}
	
	public void testPreviewManageFailureForName() throws Exception {
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previewManage");
		addRequestParameter("officeId", "3");
		addRequestParameter("displayName", "");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		addRequestParameter("trained", "1");
		addRequestParameter("trainedDate", "03/20/2006");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Group Name not present", 1, getErrrorSize(CustomerConstants.NAME  ));
		
	}
	
	public void testPreviewManageFailureForTrainedDate() throws Exception {
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
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
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Group Trained date not present", 1, getErrrorSize(CustomerConstants.TRAINED_DATE_MANDATORY  ));
		
	}
	
	public void testPreviewManageFailureForTrained() throws Exception {
		createGroupWithCenterAndSetInSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
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
		addRequestParameter("trainedDate", "03/20/2006");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Group Trained checkbox not checked ", 1, getErrrorSize(CustomerConstants.TRAINED_CHECKED  ));
		
	}
	
	private void createAndSetGroupInSession(){
		group = TestObjectFactory.createGroup("group", CustomerStatus.GROUP_ACTIVE.getValue(), "1.1", getMeeting());
		HibernateUtil.closeSession();
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class, new Integer(group.getCustomerId()).intValue());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, center, request.getSession());
	}
	
	private void createGroupWithCenterAndSetInSession() throws Exception{
		createParentCustomer();
		group = TestObjectFactory.createGroup("group", CustomerStatus.GROUP_ACTIVE.getValue(), center.getSearchId()+".1", center, new Date());
		HibernateUtil.closeSession();
		center = (CenterBO)TestObjectFactory.getObject(CenterBO.class, new Integer(center.getCustomerId()).intValue());
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class, new Integer(group.getCustomerId()).intValue());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request.getSession());
	}
	
	private void createParentCustomer(){
		meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", CustomerStatus.CENTER_ACTIVE.getValue(), "1.4", meeting, new Date(System.currentTimeMillis()));
		
	}
	
	private MeetingBO getMeeting() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}
	
	
}

