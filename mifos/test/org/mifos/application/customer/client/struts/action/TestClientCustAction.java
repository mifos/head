/**

* TestClientCustAction.java version: 1.0



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

package org.mifos.application.customer.client.struts.action;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.struts.actionforms.CenterCustActionForm;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.struts.actionforms.ClientCustActionForm;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestClientCustAction extends MifosMockStrutsTestCase{
	private UserContext userContext;
	private CenterBO center;
	private GroupBO group;
	private ClientBO client;
	private MeetingBO meeting;
	private AccountBO accountBO;
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
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);		EntityMasterData.getInstance().init();
		FieldConfigItf fieldConfigItf=FieldConfigImplementer.getInstance();
		fieldConfigItf.init();		
		FieldConfigImplementer.getInstance();
		getActionServlet().getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,fieldConfigItf.getEntityMandatoryFieldMap());
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() throws Exception {
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(ClientConstants.SALUTATION_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.MARITAL_STATUS_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.CITIZENSHIP_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.GENDER_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.HANDICAPPED_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.ETHINICITY_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,request.getSession()));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST,request.getSession()));
			
	}
	
	public void testLoadClientUnderGroup() throws Exception {
			createParentCustomer();
			setRequestPathInfo("/clientCustAction.do");
			addRequestParameter("method", "load");
			addRequestParameter("parentGroupId", group.getCustomerId().toString());
			addRequestParameter("groupFlag", "1");
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.load_success.toString());
						ClientCustActionForm actionForm = (ClientCustActionForm)request.getSession().getAttribute("clientCustActionForm");
			assertEquals(actionForm.getFormedByPersonnelValue() , group.getCustomerFormedByPersonnel().getPersonnelId());
		
	}
	
	public void testFailureNextWithAllValuesNull() throws Exception {
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "next");
		addRequestParameter("officeId", "3");
		addRequestParameter("input", "personalInfo");
		actionPerform();
		assertEquals("Client salutation", 1, getErrrorSize(CustomerConstants.SALUTATION));				
		assertEquals("Client first Name", 1, getErrrorSize(CustomerConstants.FIRST_NAME));				
		assertEquals("Client last Name", 1, getErrrorSize(CustomerConstants.LAST_NAME));
		assertEquals("spouse first Name", 1, getErrrorSize(CustomerConstants.SPOUSE_FIRST_NAME));
		assertEquals("spouse last Name", 1, getErrrorSize(CustomerConstants.SPOUSE_LAST_NAME));
		assertEquals("spouse type", 1, getErrrorSize(CustomerConstants.SPOUSE_TYPE));
		assertEquals("Gender", 1, getErrrorSize(CustomerConstants.GENDER));
		assertEquals("DOB", 1, getErrrorSize(CustomerConstants.DOB));
		verifyInputForward();
	}
	
	public void testFailureNext_WithoutMandatoryCustomField_IfAny() throws Exception{
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		boolean isCustomFieldMandatory = false;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			if(customFieldDef.isMandatory()){
				isCustomFieldMandatory = true;
				break;
			}
		}
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "next");
		addRequestParameter("officeId", "3");
		addRequestParameter("clientName.salutation", "1");
		addRequestParameter("clientName.firstName", "Client");
		addRequestParameter("clientName.lastName", "LastName");
		addRequestParameter("spouseName.firstName", "Spouse");
		addRequestParameter("spouseName.lastName", "LastName");
		addRequestParameter("spouseName.nameType", "1");
		addRequestParameter("dateOfBirth", "03/20/2006");
		addRequestParameter("gender", "1");
		addRequestParameter("input", "personalInfo");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "");
			i++;
		}
		actionPerform();
		
		if(isCustomFieldMandatory)
			assertEquals("CustomField", 1, getErrrorSize(CustomerConstants.CUSTOM_FIELD));	
		else
			assertEquals("CustomField", 0, getErrrorSize(CustomerConstants.CUSTOM_FIELD));	
	
	}
	
	public void testNextSuccess() throws Exception{
		
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		actionPerform();
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "next");
		addRequestParameter("officeId", "3");
		addRequestParameter("clientName.salutation", "1");
		addRequestParameter("clientName.firstName", "Client");
		addRequestParameter("clientName.lastName", "LastName");
		addRequestParameter("spouseName.firstName", "Spouse");
		addRequestParameter("spouseName.lastName", "LastName");
		addRequestParameter("spouseName.nameType", "1");
		addRequestParameter("dateOfBirth", "03/20/2006");
		addRequestParameter("clientDetailView.gender", "1");
		addRequestParameter("input", "personalInfo");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.next_success.toString());
	}
	
	public void testPreviewFailureForTrainedDate() throws Exception {
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "next");
		addRequestParameter("officeId", "3");
		addRequestParameter("clientName.salutation", "1");
		addRequestParameter("clientName.firstName", "Client");
		addRequestParameter("clientName.lastName", "LastName");
		addRequestParameter("spouseName.firstName", "Spouse");
		addRequestParameter("spouseName.lastName", "LastName");
		addRequestParameter("spouseName.nameType", "1");
		addRequestParameter("dateOfBirth", "03/20/2006");
		addRequestParameter("clientDetailView.gender", "1");
		addRequestParameter("input", "personalInfo");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("formedByPersonnel", "1");
		addRequestParameter("trained", "1");
		addRequestParameter("input", "mfiInfo");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Client Trained date not present", 1, getErrrorSize(ClientConstants.TRAINED_DATE_MANDATORY  ));
		
	}
	
	public void testPreviewFailureFormedByPersonnelNotPresent() throws Exception {
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "next");
		addRequestParameter("officeId", "3");
		addRequestParameter("clientName.salutation", "1");
		addRequestParameter("clientName.firstName", "Client");
		addRequestParameter("clientName.lastName", "LastName");
		addRequestParameter("spouseName.firstName", "Spouse");
		addRequestParameter("spouseName.lastName", "LastName");
		addRequestParameter("spouseName.nameType", "1");
		addRequestParameter("dateOfBirth", "03/20/2006");
		addRequestParameter("clientDetailView.gender", "1");
		addRequestParameter("input", "personalInfo");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "mfiInfo");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Client formed by not present", 1, getErrrorSize(CustomerConstants.FORMED_BY_LOANOFFICER  ));
		
	}
	public void testFailurePreview_WithDuplicateFee() throws Exception{
		List<FeeView> feesToRemove = getFees();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "next");
		addRequestParameter("officeId", "3");
		addRequestParameter("clientName.salutation", "1");
		addRequestParameter("clientName.firstName", "Client");
		addRequestParameter("clientName.lastName", "LastName");
		addRequestParameter("spouseName.firstName", "Spouse");
		addRequestParameter("spouseName.lastName", "LastName");
		addRequestParameter("spouseName.nameType", "1");
		addRequestParameter("dateOfBirth", "03/20/2006");
		addRequestParameter("gender", "1");
		addRequestParameter("input", "personalInfo");
		actionPerform();
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request.getSession());
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");	
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", "100");
		addRequestParameter("selectedFee[1].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[1].amount", "150");
		actionPerform();		
		assertEquals("Fee", 1, getErrrorSize(CustomerConstants.FEE));
		removeFees(feesToRemove);
	}
	
	public void testFailurePreview_WithFee_WithoutFeeAmount() throws Exception{
		List<FeeView> feesToRemove = getFees();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "next");
		addRequestParameter("officeId", "3");
		addRequestParameter("clientName.salutation", "1");
		addRequestParameter("clientName.firstName", "Client");
		addRequestParameter("clientName.lastName", "LastName");
		addRequestParameter("spouseName.firstName", "Spouse");
		addRequestParameter("spouseName.lastName", "LastName");
		addRequestParameter("spouseName.nameType", "1");
		addRequestParameter("dateOfBirth", "03/20/2006");
		addRequestParameter("gender", "1");
		addRequestParameter("input", "personalInfo");
		actionPerform();
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request.getSession());
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("method", "preview");		
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", "");
		actionPerform();
		assertEquals("Fee", 1, getErrrorSize(CustomerConstants.FEE));
		removeFees(feesToRemove);
	}
	
	public void testPreviewSuccess() throws Exception {
		List<FeeView> feesToRemove = getFees();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "next");
		addRequestParameter("officeId", "3");
		addRequestParameter("clientName.salutation", "1");
		addRequestParameter("clientName.firstName", "Client");
		addRequestParameter("clientName.lastName", "LastName");
		addRequestParameter("spouseName.firstName", "Spouse");
		addRequestParameter("spouseName.lastName", "LastName");
		addRequestParameter("spouseName.nameType", "1");
		addRequestParameter("dateOfBirth", "03/20/2006");
		addRequestParameter("clientDetailView.gender", "1");
		addRequestParameter("input", "personalInfo");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		actionPerform();
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request.getSession());
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");	
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("formedByPersonnel", "1");
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", fee.getAmount());
		SessionUtils.setAttribute(ClientConstants.CLIENT_MEETING,new MeetingBO(MeetingFrequency.MONTHLY, Short.valueOf("2"),MeetingType.CUSTOMERMEETING), request.getSession());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
		removeFees(feesToRemove);
	}
		
	public void testSuccessfulPrevPersonalInfo() throws Exception {
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "prevPersonalInfo");
		actionPerform();
		verifyForward(ActionForwards.prevPersonalInfo_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testSuccessfulPrevMfiInfo() throws Exception {
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "prevMFIInfo");
		actionPerform();
		verifyForward(ActionForwards.prevMFIInfo_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testCreateSuccessWithoutGroup() throws Exception {
		List<FeeView> feesToRemove = getFees();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "next");
		addRequestParameter("officeId", "3");
		addRequestParameter("clientName.salutation", "1");
		addRequestParameter("clientName.firstName", "Client");
		addRequestParameter("clientName.lastName", "LastName");
		addRequestParameter("spouseName.firstName", "Spouse");
		addRequestParameter("spouseName.lastName", "LastName");
		addRequestParameter("spouseName.nameType", "1");
		addRequestParameter("dateOfBirth", "03/20/2006");
		addRequestParameter("clientDetailView.gender", "1");
		addRequestParameter("input", "personalInfo");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "Req");
			i++;
		}
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");	
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("loanOfficerId", "1");
		addRequestParameter("formedByPersonnel", "1");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "create");	
		addRequestParameter("input", "create");
		addRequestParameter("status", "1");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());
		ClientCustActionForm actionForm = (ClientCustActionForm)request.getSession().getAttribute("clientCustActionForm");
		client = (ClientBO)TestObjectFactory.getObject(ClientBO.class, new Integer(actionForm.getCustomerId()).intValue());
		removeFees(feesToRemove);
	}
	
	
	public void testCreateSuccessUnderGroup() throws Exception {
		try{
			createParentCustomer();
			setRequestPathInfo("/clientCustAction.do");
			addRequestParameter("method", "load");
			addRequestParameter("parentGroupId", group.getCustomerId().toString());
			addRequestParameter("groupFlag", "1");
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.load_success.toString());
			List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
			setRequestPathInfo("/clientCustAction.do");
			addRequestParameter("method", "next");
			addRequestParameter("officeId", "3");
			addRequestParameter("clientName.salutation", "1");
			addRequestParameter("clientName.firstName", "Client");
			addRequestParameter("clientName.lastName", "LastName");
			addRequestParameter("spouseName.firstName", "Spouse");
			addRequestParameter("spouseName.lastName", "LastName");
			addRequestParameter("spouseName.nameType", "1");
			addRequestParameter("dateOfBirth", "03/20/2006");
			addRequestParameter("clientDetailView.gender", "1");
			addRequestParameter("input", "personalInfo");
			int i = 0;
			for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
				addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
				addRequestParameter("customField["+ i +"].fieldValue", "Req");
				i++;
			}
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.next_success.toString());

			setRequestPathInfo("/clientCustAction.do");
			addRequestParameter("method", "preview");	
			addRequestParameter("input", "mfiInfo");
			addRequestParameter("formedByPersonnel", "1");
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.preview_success.toString());
			setRequestPathInfo("/clientCustAction.do");
			addRequestParameter("method", "create");	
			addRequestParameter("input", "create");
			addRequestParameter("status", "1");
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.create_success.toString());
			ClientCustActionForm actionForm = (ClientCustActionForm)request.getSession().getAttribute("clientCustActionForm");
			client = (ClientBO)TestObjectFactory.getObject(ClientBO.class, new Integer(actionForm.getCustomerId()).intValue());
		}
		catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	public void testGet(){	
		createInitialCustomers();
		accountBO = getLoanAccount(client,meeting);
		client.setDateOfBirth(offSetCurrentDate(50));
		TestObjectFactory.updateObject(client);	
		HibernateUtil.closeSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", client.getGlobalCustNum());
		actionPerform();	
		verifyForward(ActionForwards.get_success.toString());
		assertEquals("Age of customer should be 50 years",50,SessionUtils.getAttribute(ClientConstants.AGE,request.getSession()));
		//assertEquals("No of active loan accounts should be 1",1,((List<LoanBO>)SessionUtils.getAttribute(ClientConstants.CUSTOMERACTIVELOANACCOUNTS,request.getSession())).size());
		HibernateUtil.closeSession();
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,group.getCustomerId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,center.getCustomerId());
		client = (ClientBO)HibernateUtil.getSessionTL().get(ClientBO.class,client.getCustomerId());
		accountBO = (LoanBO) HibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());
	}
	private List<FeeView> getFees() {
		List<FeeView> fees = new ArrayList<FeeView>();
		AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory
				.createPeriodicAmountFee("PeriodicAmountFee",
						FeeCategory.CENTER, "200", MeetingFrequency.WEEKLY,
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
	
	private void createInitialCustomers(){
		meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"), "1.4", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("group", CustomerStatus.GROUP_ACTIVE.getValue(), center.getSearchId()+".1", center, new Date());
		client = TestObjectFactory.createClient("client",CustomerStatus.CLIENT_ACTIVE.getValue(), group.getSearchId()+".1", group, new Date());
	}
	
	private void createParentCustomer(){
		meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"), "1.4", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("group", CustomerStatus.GROUP_ACTIVE.getValue(), center.getSearchId()+".1", center, new Date());
	}
	
	private java.sql.Date offSetCurrentDate(int noOfyears) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year-noOfyears, month, day);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}
	
	private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer, Short
				.valueOf("5"), startDate, loanOffering);

	}
	
	public void testEditPersonalInfo() throws Exception {		
		
		createAndSetClientInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editPersonalInfo");
		addRequestParameter("officeId", "3");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.editPersonalInfo_success.toString());
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.SALUTATION_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.MARITAL_STATUS_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.CITIZENSHIP_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.GENDER_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.HANDICAPPED_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.ETHINICITY_ENTITY, request.getSession()));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,request.getSession()));
			
	}
	
	public void testEditPersonalInfoPreviewFailure() throws Exception {		
		
		createAndSetClientInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editPersonalInfo");
		addRequestParameter("officeId", "3");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.editPersonalInfo_success.toString());
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditPersonalInfo");
		addRequestParameter("officeId", "3");
		addRequestParameter("input", "editPersonalInfo");
		addRequestParameter("clientName.salutation", "");
		addRequestParameter("clientName.firstName", "");
		addRequestParameter("clientName.lastName", "");
		addRequestParameter("spouseName.firstName", "");
		addRequestParameter("spouseName.lastName", "");
		addRequestParameter("spouseName.nameType", "");
		addRequestParameter("dateOfBirth", "");
		addRequestParameter("clientDetailView.gender", "");
		actionPerform();
		assertEquals("Client salutation", 1, getErrrorSize(CustomerConstants.SALUTATION));				
		assertEquals("Client first Name", 1, getErrrorSize(CustomerConstants.FIRST_NAME));				
		assertEquals("Client last Name", 1, getErrrorSize(CustomerConstants.LAST_NAME));
		assertEquals("spouse first Name", 1, getErrrorSize(CustomerConstants.SPOUSE_FIRST_NAME));
		assertEquals("spouse last Name", 1, getErrrorSize(CustomerConstants.SPOUSE_LAST_NAME));
		assertEquals("spouse type", 1, getErrrorSize(CustomerConstants.SPOUSE_TYPE));
		assertEquals("Gender", 1, getErrrorSize(CustomerConstants.GENDER));
		assertEquals("DOB", 1, getErrrorSize(CustomerConstants.DOB));
		verifyInputForward();
			
	}
	
	public void testSuccessfulEditPreview() throws Exception {
		createAndSetClientInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editPersonalInfo");
		addRequestParameter("officeId", "3");
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditPersonalInfo");	
		addRequestParameter("clientName.firstName", "Client2");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			i++;
		}
		actionPerform();
		assertEquals(0, getErrrorSize());
		verifyForward(ActionForwards.previewEditPersonalInfo_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	public void testSuccessfulUpdate() throws Exception {
		createAndSetClientInSession();		
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editPersonalInfo");
		addRequestParameter("officeId", "3");
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>)SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditPersonalInfo");	
		addRequestParameter("clientDetailView.ethinicity", "1");
		int i = 0;
		for(CustomFieldDefinitionEntity customFieldDef: customFieldDefs){
			addRequestParameter("customField["+ i +"].fieldId", customFieldDef.getFieldId().toString());
			addRequestParameter("customField["+ i +"].fieldValue", "11");
			addRequestParameter("customField["+ i +"].fieldType", "1");
			i++;
		}
		actionPerform();
		assertEquals(0, getErrrorSize());
		verifyForward(ActionForwards.previewEditPersonalInfo_success.toString());
		
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "updatePersonalInfo");
		actionPerform();
		verifyForward(ActionForwards.updatePersonalInfo_success.toString());
		assertEquals(1, client.getCustomerDetail().getEthinicity().shortValue());
		client = (ClientBO)TestObjectFactory.getObject(ClientBO.class,client.getCustomerId());
		
	}
	private void createAndSetClientInSession() throws Exception{
		String name = "Client 1";
		Short officeId = 1;
		Short personnel = 3;
		meeting = getMeeting();
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder(name),"Client","","1","");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),"first","middle","last","secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"));
		client = new ClientBO(TestObjectFactory.getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(new Short("1")), null, null, new Address(), getCustomFields(), null, personnel, officeId, meeting,personnel, new java.util.Date(),
				null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO)TestObjectFactory.getObject(ClientBO.class, new Integer(client.getCustomerId()).intValue());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request.getSession());
	}
	
	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC.getValue()));
		fields.add(new CustomFieldView(Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC.getValue()));
		return fields;
	}
	
	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}
	
	
}
