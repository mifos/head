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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
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
import org.mifos.application.customer.group.struts.actionforms.GroupCustActionForm;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
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
	
	private UserContext userContext;
	private Short officeId  = 3;

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
		userContext = TestObjectFactory.getUserContext();
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
		userContext = null;
		super.tearDown();
	}

	public void testChooseOffice()throws Exception{
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "chooseOffice");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.chooseOffice_success.toString());
	}

	public void testHierarchyCheck()throws Exception{
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "hierarchyCheck");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		boolean isCenterHierarchyExists = Configuration.getInstance().getCustomerConfig(userContext.getBranchId()).isCenterHierarchyExists();
		if(isCenterHierarchyExists)
			verifyForward(ActionForwards.loadCenterSearch.toString());
		else
			verifyForward(ActionForwards.loadCreateGroup.toString());
	}
	
	public void testLoad()throws Exception{
		createParentCustomer();
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		
		boolean isCenterHierarchyExists = Configuration.getInstance().getCustomerConfig(userContext.getBranchId()).isCenterHierarchyExists();
		if(!isCenterHierarchyExists){
			assertNotNull(SessionUtils.getAttribute(CustomerConstants.LOAN_OFFICER_LIST, request));	
		}
		assertNotNull(SessionUtils.getAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST,request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,request));
		CenterBO oldCenter = center;
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());
		oldCenter =  null;	
	}
	
	public void testLoadMeeting()throws Exception{
		createParentCustomer();
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "loadMeeting");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		getRequest().getSession().setAttribute("security_param", "GroupCreate");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadMeeting_success.toString());
		CenterBO oldCenter = center;
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());
		oldCenter =  null;
	}
	
	public void testPreviewFailure_With_Name_Null()throws Exception{
		createParentCustomer();
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals("Group Name", 1, getErrrorSize(CustomerConstants.NAME));
		verifyInputForward();		
		CenterBO oldCenter = center;
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());		
		oldCenter =  null;
	}

	public void testPreviewFailure_TrainedWithoutTrainedDate()throws Exception{
		createParentCustomer();
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("displayName", "group");		
		addRequestParameter("trained", "1");
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());	
		
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}
		actionPerform();
		assertEquals("Trained Date", 1, getErrrorSize(CustomerConstants.TRAINED_DATE_MANDATORY));
		verifyInputForward();		
		CenterBO oldCenter = center;
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());		
		oldCenter =  null;
		
	}
	
	public void testFailurePreview_WithoutMandatoryCustomField_IfAny() throws Exception{
		createParentCustomer();
		HibernateUtil.closeSession();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		boolean isCustomFieldMandatory = false;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			if(customFieldDef.isMandatory()){
				isCustomFieldMandatory = true;
				break;
			}
		}
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		addRequestParameter("displayName", "group");		
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());		
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "");
			i++;
		}
		actionPerform();
		
		if(isCustomFieldMandatory){
			assertEquals("CustomField", 1, getErrrorSize(CustomerConstants.CUSTOM_FIELD));
			verifyInputForward();
		}
		else{
			assertEquals("CustomField", 0, getErrrorSize(CustomerConstants.CUSTOM_FIELD));
			verifyForward(ActionForwards.preview_success.toString());
		}
		CenterBO oldCenter = center;
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());		
		oldCenter =  null;
	}
	
	public void testFailurePreview_WithDuplicateFee() throws Exception{
		List<FeeView> feesToRemove = getFees();
		createParentCustomer();
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request);
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");		
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", "100");
		addRequestParameter("selectedFee[1].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[1].amount", "150");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals("Fee", 1, getErrrorSize(CustomerConstants.FEE));
		verifyInputForward();
		removeFees(feesToRemove);
		CenterBO oldCenter = center;
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());		
		oldCenter =  null;
	}
	
	public void testFailurePreview_WithFee_WithoutFeeAmount() throws Exception{
		List<FeeView> feesToRemove = getFees();
		createParentCustomer();
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request);
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");		
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", "");		
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals("Fee", 1, getErrrorSize(CustomerConstants.FEE));
		verifyInputForward();
		removeFees(feesToRemove);
		CenterBO oldCenter = center;
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());		
		oldCenter =  null;
	}

	public void testSuccessfulPreview() throws Exception{
		List<FeeView> feesToRemove = getFees();
		createParentCustomer();		
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request);
		FeeView fee = feeList.get(0);
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		addRequestParameter("displayName", "group");		
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());	
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", fee.getAmount());
		actionPerform();
		assertEquals(0, getErrrorSize());
		verifyForward(ActionForwards.preview_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		HibernateUtil.closeSession();
		removeFees(feesToRemove);
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED,request));
		CenterBO oldCenter = center;
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,	center.getCustomerId());		
		oldCenter =  null;
	}
	
	public void testSuccessfulPrevious() throws Exception {
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "previous");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.previous_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testSuccessfulCreate_UnderCenter() throws Exception {
		createParentCustomer();		
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		addRequestParameter("displayName", "group");		
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}		
		actionPerform();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		
		verifyNoActionErrors();		
		verifyForward(ActionForwards.create_success.toString());
		
		GroupCustActionForm actionForm = (GroupCustActionForm)request.getSession().getAttribute("groupCustActionForm");
		
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class, new Integer(actionForm.getCustomerId()).intValue());
		center = (CenterBO)TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		actionForm.setParentCustomer(null);
	}
	
	public void testSuccessfulCreate_UnderBranch() throws Exception {
		createParentCustomer();		
		HibernateUtil.closeSession();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("branchId", officeId.toString());
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
				
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		addRequestParameter("displayName", "group");		
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}		
		actionPerform();
		
		SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,Boolean.FALSE, request);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		
		verifyNoActionErrors();		
		verifyForward(ActionForwards.create_success.toString());
		
		GroupCustActionForm actionForm = (GroupCustActionForm)request.getSession().getAttribute("groupCustActionForm");
		
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class, new Integer(actionForm.getCustomerId()).intValue());
		center = (CenterBO)TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		actionForm.setParentCustomer(null);
	}
	
	public void testFailureCreate_DuplicateName() throws Exception {
		createGroupWithCenter();
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("centerSystemId", center.getGlobalCustNum());
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		addRequestParameter("displayName", "group");		
		addRequestParameter("status", CustomerStatus.GROUP_PENDING.getValue().toString());
		addRequestParameter("formedByPersonnel", center.getPersonnel().getPersonnelId().toString());		
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}
		actionPerform();
		
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		
		actionPerform();
		verifyActionErrors(new String[]{CustomerConstants.ERRORS_DUPLICATE_CUSTOMER});
		verifyForward(ActionForwards.create_failure.toString());
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		center = (CenterBO)TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		
		GroupCustActionForm actionForm = (GroupCustActionForm)request.getSession().getAttribute("groupCustActionForm");
		actionForm.setParentCustomer(null);
	}
	
	public void testGet() throws Exception {
		createCustomers();
		CustomerPositionEntity customerPositionEntity = new CustomerPositionEntity(
				new PositionEntity(Short.valueOf("1")), client, client
						.getParentCustomer());
		group.addCustomerPosition(customerPositionEntity);
		savingsBO = getSavingsAccount("fsaf6","ads6");
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
		loanBO = (LoanBO) new AccountPersistence().getAccount(loanBO
				.getAccountId());
		savingsBO = (SavingsBO) new AccountPersistence()
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
		loanBO = (LoanBO) new AccountPersistence().getAccount(loanBO
				.getAccountId());
		savingsBO = (SavingsBO) new AccountPersistence()
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
	
	public void testUpdateSuccess() throws Exception {
		String newDisplayName ="group_01";
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
		addRequestParameter("displayName", newDisplayName);
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldType", customFieldDef.getFieldType().toString());
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
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, new Integer(group.getCustomerId()).intValue());
		assertTrue(group.isTrained());
		assertEquals(newDisplayName ,group.getDisplayName());
	}
	
	public void testUpdateSuccessWithoutTrained() throws Exception {
		String newDisplayName ="group_01";
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
		addRequestParameter("displayName", newDisplayName);
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldType", customFieldDef.getFieldType().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
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
		assertTrue(!group.isTrained());
		assertEquals(newDisplayName ,group.getDisplayName());
		
	}
	
	public void testCancelSuccess() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("input", GroupConstants.PREVIEW_MANAGE_GROUP);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancelEdit_success.toString());		
	}	

	public void testCancelSuccessForCreateGroup() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/groupCustAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("input", GroupConstants.CREATE_GROUP);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String)request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancelCreate_success.toString());		
	}	

	private void createGroupWithCenterAndSetInSession() throws Exception {
		createGroupWithCenter();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class,
				new Integer(center.getCustomerId()).intValue());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class,
				new Integer(group.getCustomerId()).intValue());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
	}

	private void createGroupWithCenter()throws Exception{
		createParentCustomer();
		group = TestObjectFactory.createGroupUnderCenter("group",CustomerStatus.GROUP_ACTIVE, center);
		HibernateUtil.closeSession();
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

	private SavingsBO getSavingsAccount(String offeringName,String shortName) {
		savingsOffering = helper.createSavingsOffering(offeringName,shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017", group,
				AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}
	
	private List<FeeView> getFees() {
		List<FeeView> fees = new ArrayList<FeeView>();
		AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory
				.createPeriodicAmountFee("PeriodicAmountFee",
						FeeCategory.GROUP, "200", MeetingFrequency.WEEKLY,
						Short.valueOf("2"));
		fees.add(new FeeView(fee1));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		return fees;
	}
	
	private void removeFees(List<FeeView> feesToRemove){
		for(FeeView fee :feesToRemove){
			TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
		}
	}
}
