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
import java.util.List;
import java.util.Locale;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.TestClientBO;
import org.mifos.application.customer.client.struts.actionforms.ClientCustActionForm;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestClientCustAction extends MifosMockStrutsTestCase {
	private UserContext userContext;

	private CenterBO center;

	private GroupBO group;

	private ClientBO client;

	private MeetingBO meeting;

	private AccountBO accountBO;

	private String flowKey;
	private SavingsOfferingBO savingsOffering1;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/customer/client/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = TestObjectFactory.getContext();
		userContext.setPereferedLocale(new Locale("en", "US"));
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");

		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, ClientCustAction.class);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

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
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.removeObject(savingsOffering1);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsOffering("savingsoffering1","s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.SALUTATION_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.MARITAL_STATUS_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.CITIZENSHIP_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.BUSINESS_ACTIVITIES_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.EDUCATION_LEVEL_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.GENDER_ENTITY,
				request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.SPOUSE_FATHER_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.HANDICAPPED_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.ETHINICITY_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.CUSTOM_FIELDS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST, request));
		List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>)SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
		assertNotNull(povertyStatusList);
		List<SavingsOfferingBO> savingsOfferingList = (List<SavingsOfferingBO>)SessionUtils.getAttribute(ClientConstants.SAVINGS_OFFERING_LIST, request);
		assertNotNull(savingsOfferingList);
		assertEquals(1, savingsOfferingList.size());
		HibernateUtil.closeSession();
	}
	
	public void testLoadWithGroupHavingNoLoanOfficer() throws Exception {
		createParentGroup(CustomerStatus.GROUP_PARTIAL,null);
		savingsOffering1 = TestObjectFactory.createSavingsOffering("savingsoffering1","s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("parentGroupId", group.getCustomerId().toString());
		addRequestParameter("groupFlag", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.SALUTATION_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.MARITAL_STATUS_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.CITIZENSHIP_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.BUSINESS_ACTIVITIES_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.EDUCATION_LEVEL_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.GENDER_ENTITY,
				request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.SPOUSE_FATHER_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.HANDICAPPED_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.ETHINICITY_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.CUSTOM_FIELDS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST, request));
		List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>)SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
		assertNotNull(povertyStatusList);
		List<SavingsOfferingBO> savingsOfferingList = (List<SavingsOfferingBO>)SessionUtils.getAttribute(ClientConstants.SAVINGS_OFFERING_LIST, request);
		assertNotNull(savingsOfferingList);
		assertEquals(1, savingsOfferingList.size());
		ClientCustActionForm actionForm = (ClientCustActionForm) request
		.getSession().getAttribute("clientCustActionForm");
		assertNull(actionForm.getFormedByPersonnelValue());
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		HibernateUtil.closeSession();
	}

	public void testLoadClientUnderGroup_FeeDifferentFrequecny() throws Exception {
		createGroupWithoutFee();
		List<FeeView> fees = getFees(RecurrenceType.MONTHLY);
		HibernateUtil.closeSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("parentGroupId", group.getCustomerId().toString());
		addRequestParameter("groupFlag", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		
		ClientCustActionForm actionForm = (ClientCustActionForm) request
				.getSession().getAttribute("clientCustActionForm");
		assertEquals(actionForm.getFormedByPersonnelValue(), group
				.getCustomerFormedByPersonnel().getPersonnelId());
		
		List<FeeView> additionalFees = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,request);
		assertEquals(0, additionalFees.size());
		
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		removeFees(fees);	
	}

	public void testLoadClientUnderGroup_FeeSameFrequecny() throws Exception {
		createGroupWithoutFee();
		List<FeeView> fees = getFees(RecurrenceType.WEEKLY);
		HibernateUtil.closeSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("parentGroupId", group.getCustomerId().toString());
		addRequestParameter("groupFlag", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		
		ClientCustActionForm actionForm = (ClientCustActionForm) request
				.getSession().getAttribute("clientCustActionForm");
		assertEquals(actionForm.getFormedByPersonnelValue(), group
				.getCustomerFormedByPersonnel().getPersonnelId());
		
		List<FeeView> additionalFees = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,request);
		assertNotNull(additionalFees);
		assertEquals(1, additionalFees.size());
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		removeFees(fees);	
	}
	
	public void testFailureNextWithAllValuesNull() throws Exception {
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "next");
		addRequestParameter("officeId", "3");
		addRequestParameter("input", "personalInfo");
		actionPerform();
		assertEquals("Client salutation", 1,
				getErrrorSize(CustomerConstants.SALUTATION));
		assertEquals("Client first Name", 1,
				getErrrorSize(CustomerConstants.FIRST_NAME));
		assertEquals("Client last Name", 1,
				getErrrorSize(CustomerConstants.LAST_NAME));
		assertEquals("spouse first Name", 1,
				getErrrorSize(CustomerConstants.SPOUSE_FIRST_NAME));
		assertEquals("spouse last Name", 1,
				getErrrorSize(CustomerConstants.SPOUSE_LAST_NAME));
		assertEquals("spouse type", 1,
				getErrrorSize(CustomerConstants.SPOUSE_TYPE));
		assertEquals("Gender", 1, getErrrorSize(CustomerConstants.GENDER));
		assertEquals("DOB", 1, getErrrorSize(CustomerConstants.DOB));
		verifyInputForward();
	}

	public void testFailureNext_WithoutMandatoryCustomField_IfAny()
			throws Exception {
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		boolean isCustomFieldMandatory = false;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			if (customFieldDef.isMandatory()) {
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
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "");
			i++;
		}
		actionPerform();

		if (isCustomFieldMandatory)
			assertEquals("CustomField", 1,
					getErrrorSize(CustomerConstants.CUSTOM_FIELD));
		else
			assertEquals("CustomField", 0,
					getErrrorSize(CustomerConstants.CUSTOM_FIELD));

	}

	public void testNextSuccess() throws Exception {

		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
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
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
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
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
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
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
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
		assertEquals("Client Trained date not present", 1,
				getErrrorSize(CustomerConstants.TRAINED_DATE_MANDATORY));

	}

	public void testPreviewFailureFormedByPersonnelNotPresent()
			throws Exception {
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
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
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "mfiInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Client formed by not present", 1,
				getErrrorSize(CustomerConstants.FORMED_BY_LOANOFFICER));

	}

	public void testFailurePreview_WithDuplicateFee() throws Exception {
		List<FeeView> feesToRemove = getFees(RecurrenceType.WEEKLY);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
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
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(
				CustomerConstants.ADDITIONAL_FEES_LIST, request);
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", "100");
		addRequestParameter("selectedFee[1].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[1].amount", "150");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals("Fee", 1, getErrrorSize(CustomerConstants.FEE));
		removeFees(feesToRemove);
	}

	public void testPreviewFaillure_FeesWithoutMeeting() throws Exception {
		List<FeeView> feesToRemove = getFees(RecurrenceType.WEEKLY);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey =(String) request.getAttribute(Constants.CURRENTFLOWKEY);
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
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
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(
				CustomerConstants.ADDITIONAL_FEES_LIST, request);
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("formedByPersonnel", "1");
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", fee.getAmount());
		// SessionUtils.setAttribute(ClientConstants.CLIENT_MEETING,new
		// MeetingBO(MeetingFrequency.MONTHLY,
		// Short.valueOf("2"),MeetingType.CUSTOMERMEETING), request);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals("Fee", 1,
				getErrrorSize(CustomerConstants.MEETING_REQUIRED_EXCEPTION));
		removeFees(feesToRemove);
	}

	public void testFailurePreview_WithFee_WithoutFeeAmount() throws Exception {
		List<FeeView> feesToRemove = getFees(RecurrenceType.WEEKLY);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
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
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(
				CustomerConstants.ADDITIONAL_FEES_LIST, request);
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("method", "preview");
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", "");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals("Fee", 1, getErrrorSize(CustomerConstants.FEE));
		removeFees(feesToRemove);
	}

	public void testPreviewFailure_DuplicateOfferingsSelected() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsOffering("savingsPrd1", "s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey =(String) request.getAttribute(Constants.CURRENTFLOWKEY);
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);

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
		addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "11");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		List<SavingsOfferingBO> savingsOfferingList = (List<SavingsOfferingBO>) SessionUtils.getAttribute(
				ClientConstants.SAVINGS_OFFERING_LIST, request);
		
		savingsOffering1 = savingsOfferingList.get(0);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("formedByPersonnel", "1");
		addRequestParameter("savingsOffering[0]", savingsOffering1.getPrdOfferingId().toString());
		addRequestParameter("savingsOffering[1]", savingsOffering1.getPrdOfferingId().toString());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING,
				new MeetingBO(RecurrenceType.MONTHLY, Short.valueOf("2"),
						new Date(), MeetingType.CUSTOMERMEETING), request);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals("Duplicate Offerings", 1, getErrrorSize(ClientConstants.ERRORS_DUPLICATE_OFFERING_SELECTED));		
	}
	
	public void testFailurePreview_FeeFrequencyMismatch() throws Exception{
		List<FeeView> feesToRemove = getFees(RecurrenceType.MONTHLY);
		
		HibernateUtil.closeSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey =(String) request.getAttribute(Constants.CURRENTFLOWKEY);
		
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
		.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
		List<FeeView> feeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request);
		assertEquals(1, feeList.size());
		FeeView fee = feeList.get(0);
		
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
		addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("formedByPersonnel", "1");
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", fee.getAmount());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING,
				new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"),
						new Date(), MeetingType.CUSTOMERMEETING), request);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals("Fee", 1, getErrrorSize(CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH));
		removeFees(feesToRemove);
	}
	
	public void testPreviewSuccess() throws Exception {
		List<FeeView> feesToRemove = getFees(RecurrenceType.MONTHLY);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey =(String) request.getAttribute(Constants.CURRENTFLOWKEY);
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
		
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
		addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(
				CustomerConstants.ADDITIONAL_FEES_LIST, request);
		FeeView fee = feeList.get(0);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("formedByPersonnel", "1");
		addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
		addRequestParameter("selectedFee[0].amount", fee.getAmount());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING,
				new MeetingBO(RecurrenceType.MONTHLY, Short.valueOf("2"),
						new Date(), MeetingType.CUSTOMERMEETING), request);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
		removeFees(feesToRemove);
	}

	public void testSuccessfulPreviewWithSavingsOfferingsSelected() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsOffering("savingsPrd1", "s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey =(String) request.getAttribute(Constants.CURRENTFLOWKEY);
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);

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
		addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "11");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		List<SavingsOfferingBO> savingsOfferingList = (List<SavingsOfferingBO>) SessionUtils.getAttribute(
				ClientConstants.SAVINGS_OFFERING_LIST, request);
		
		savingsOffering1 = savingsOfferingList.get(0);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("formedByPersonnel", "1");
		addRequestParameter("savingsOffering[0]", savingsOffering1.getPrdOfferingId().toString());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING,
				new MeetingBO(RecurrenceType.MONTHLY, Short.valueOf("2"),
						new Date(), MeetingType.CUSTOMERMEETING), request);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.preview_success.toString());
	}
	
	public void testSuccessfulPrevPersonalInfo() throws Exception {
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "prevPersonalInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.prevPersonalInfo_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSuccessfulPrevMfiInfo() throws Exception {
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "prevMFIInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.prevMFIInfo_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCreateSuccessWithAssociatedSavingsOfferings() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsOffering("savingsPrd1", "s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		List<FeeView> feesToRemove = getFees(RecurrenceType.WEEKLY);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey =(String) request.getAttribute(Constants.CURRENTFLOWKEY);
		List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
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
		addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		
		List<SavingsOfferingBO> savingsOfferingList = (List<SavingsOfferingBO>) SessionUtils.getAttribute(
				ClientConstants.SAVINGS_OFFERING_LIST, request);
		savingsOffering1 = savingsOfferingList.get(0);
		
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("loanOfficerId", "1");
		addRequestParameter("formedByPersonnel", "1");
		addRequestParameter("savingsOffering[0]", savingsOffering1.getPrdOfferingId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("input", "create");
		addRequestParameter("status", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());
		ClientCustActionForm actionForm = (ClientCustActionForm) request
				.getSession().getAttribute("clientCustActionForm");
		client = TestObjectFactory.getObject(ClientBO.class,
				actionForm.getCustomerIdAsInt());
		assertNotNull(client);
		assertNotNull(client.getOfferingsAssociatedInCreate());
		assertEquals(1,client.getOfferingsAssociatedInCreate().size());
		for(ClientInitialSavingsOfferingEntity offering: client.getOfferingsAssociatedInCreate()){
			assertEquals(savingsOffering1.getPrdOfferingId(),offering.getSavingsOffering().getPrdOfferingId());
			assertTrue(true);
		}
			
		removeFees(feesToRemove);
	}
	
	public void testCreateSuccessWithoutGroup() throws Exception {
		List<FeeView> feesToRemove = getFees(RecurrenceType.WEEKLY);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("officeId", "3");
		addRequestParameter("groupFlag", "0");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey =(String) request.getAttribute(Constants.CURRENTFLOWKEY);
		List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
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
		addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "Req");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "mfiInfo");
		addRequestParameter("loanOfficerId", "1");
		addRequestParameter("formedByPersonnel", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("input", "create");
		addRequestParameter("status", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());
		ClientCustActionForm actionForm = (ClientCustActionForm) request
				.getSession().getAttribute("clientCustActionForm");
		client = TestObjectFactory.getObject(ClientBO.class,
				actionForm.getCustomerIdAsInt());
		removeFees(feesToRemove);
	}

	public void testCreateSuccessUnderGroup() throws Exception {
			createParentCustomer();
			setRequestPathInfo("/clientCustAction.do");
			addRequestParameter("method", "load");
			addRequestParameter("parentGroupId", group.getCustomerId()
					.toString());
			addRequestParameter("groupFlag", "1");
			addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
			actionPerform();
			flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.load_success.toString());
			List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
			List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
					.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
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
			addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
			int i = 0;
			for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
				addRequestParameter("customField[" + i + "].fieldId",
						customFieldDef.getFieldId().toString());
				addRequestParameter("customField[" + i + "].fieldValue", "Req");
				i++;
			}
			addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.next_success.toString());

			setRequestPathInfo("/clientCustAction.do");
			addRequestParameter("method", "preview");
			addRequestParameter("input", "mfiInfo");
			addRequestParameter("formedByPersonnel", "1");
			addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.preview_success.toString());
			setRequestPathInfo("/clientCustAction.do");
			addRequestParameter("method", "create");
			addRequestParameter("input", "create");
			addRequestParameter("status", "1");
			addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.create_success.toString());
			ClientCustActionForm actionForm = (ClientCustActionForm) request
					.getSession().getAttribute("clientCustActionForm");
			client = TestObjectFactory.getObject(ClientBO.class,
					actionForm.getCustomerIdAsInt());
	}
	
	public void testGet() throws Exception {
		createInitialCustomers();
		accountBO = getLoanAccount(client, meeting);
		TestClientBO.setDateOfBirth(client,offSetCurrentDate(50));
		TestObjectFactory.updateObject(client);
		HibernateUtil.closeSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "get");
		addRequestParameter("globalCustNum", client.getGlobalCustNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.get_success.toString());
		List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>)SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
		assertNotNull(povertyStatusList);
		assertEquals("Age of customer should be 50 years", 50, SessionUtils
				.getAttribute(ClientConstants.AGE, request));
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.CUSTOM_FIELDS_LIST, request));
		// assertEquals("No of active loan accounts should be
		// 1",1,((List<LoanBO>)SessionUtils.getAttribute(ClientConstants.CUSTOMERACTIVELOANACCOUNTS,request)).size());
		HibernateUtil.closeSession();
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		client = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		accountBO = (LoanBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				accountBO.getAccountId());
	}

	public void testEditPersonalInfo() throws Exception {

		createAndSetClientInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editPersonalInfo");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.editPersonalInfo_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.CUSTOM_FIELDS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.SALUTATION_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.MARITAL_STATUS_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.CITIZENSHIP_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.BUSINESS_ACTIVITIES_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.EDUCATION_LEVEL_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(ClientConstants.GENDER_ENTITY,
				request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.SPOUSE_FATHER_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.HANDICAPPED_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				ClientConstants.ETHINICITY_ENTITY, request));
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.CUSTOM_FIELDS_LIST, request));
		List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>)SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
		assertNotNull(povertyStatusList);

	}

	public void testEditPersonalInfoPreviewFailure() throws Exception {

		createAndSetClientInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editPersonalInfo");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.editPersonalInfo_success.toString());
		
		List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>)SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
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
		addRequestParameter("customerDetail.povertyStatus", povertyStatusList.get(0).getId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals("Client salutation", 1,
				getErrrorSize(CustomerConstants.SALUTATION));
		assertEquals("Client first Name", 1,
				getErrrorSize(CustomerConstants.FIRST_NAME));
		assertEquals("Client last Name", 1,
				getErrrorSize(CustomerConstants.LAST_NAME));
		assertEquals("spouse first Name", 1,
				getErrrorSize(CustomerConstants.SPOUSE_FIRST_NAME));
		assertEquals("spouse last Name", 1,
				getErrrorSize(CustomerConstants.SPOUSE_LAST_NAME));
		assertEquals("spouse type", 1,
				getErrrorSize(CustomerConstants.SPOUSE_TYPE));
		assertEquals("Gender", 1, getErrrorSize(CustomerConstants.GENDER));
		assertEquals("DOB", 1, getErrrorSize(CustomerConstants.DOB));
		verifyInputForward();

	}

	public void testSuccessfulEditPreview() throws Exception {
		createAndSetClientInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editPersonalInfo");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditPersonalInfo");
		addRequestParameter("clientName.firstName", "Client2");
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "11");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals(0, getErrrorSize());
		verifyForward(ActionForwards.previewEditPersonalInfo_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSuccessfulUpdatePersonalInfo() throws Exception {
		createAndSetClientInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editPersonalInfo");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);

		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditPersonalInfo");
		addRequestParameter("clientDetailView.ethinicity", "1");
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "11");
			addRequestParameter("customField[" + i + "].fieldType", "1");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals(0, getErrrorSize());
		verifyForward(ActionForwards.previewEditPersonalInfo_success.toString());

		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "updatePersonalInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.updatePersonalInfo_success.toString());
		assertEquals(1, client.getCustomerDetail().getEthinicity().shortValue());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());

	}
	
	public void testSuccessfulUpdatePersonalInfo_AuditLog() throws Exception {
		createClientForAuditLog();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editPersonalInfo");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>) SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditPersonalInfo");
		addRequestParameter("clientName.salutation", Integer.valueOf("48").toString());
		addRequestParameter("clientName.firstName", "Diti");
		addRequestParameter("clientName.middleName", "S");
		addRequestParameter("clientName.secondLastName", "S");
		addRequestParameter("clientName.lastName", "Sharma");
		addRequestParameter("spouseName.firstName", "Abc");
		addRequestParameter("spouseName.middleName", "A");
		addRequestParameter("spouseName.secondLastName", "A");
		addRequestParameter("spouseName.lastName", "Sharma");
		addRequestParameter("spouseName.nameType", "2");
		addRequestParameter("clientDetailView.povertyStatus", povertyStatusList.get(1).getId().toString());
		addRequestParameter("clientDetailView.gender", Integer.valueOf("50").toString());
		addRequestParameter("clientDetailView.citizenship", Integer.valueOf("131").toString());
		addRequestParameter("clientDetailView.ethinicity", Integer.valueOf("219").toString());
		addRequestParameter("clientDetailView.handicapped", Integer.valueOf("139").toString());
		addRequestParameter("clientDetailView.businessActivities", "");
		addRequestParameter("clientDetailView.maritalStatus", Integer.valueOf("67").toString());
		addRequestParameter("clientDetailView.educationLevel", Integer.valueOf("227").toString());
		addRequestParameter("clientDetailView.numChildren", Integer.valueOf("2").toString());
		
		int i = 0;
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			addRequestParameter("customField[" + i + "].fieldId",
					customFieldDef.getFieldId().toString());
			addRequestParameter("customField[" + i + "].fieldValue", "11");
			addRequestParameter("customField[" + i + "].fieldType", "1");
			i++;
		}
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		assertEquals(0, getErrrorSize());
		verifyForward(ActionForwards.previewEditPersonalInfo_success.toString());

		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "updatePersonalInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.updatePersonalInfo_success.toString());
		assertEquals(219, client.getCustomerDetail().getEthinicity().shortValue());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());

		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(EntityType.CLIENT.getValue(),client.getCustomerId());
		assertEquals(1,auditLogList.size());
		assertEquals(EntityType.CLIENT.getValue(),auditLogList.get(0).getEntityType());
		assertEquals(client.getCustomerId(),auditLogList.get(0).getEntityId());
		
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Gender")){
				matchValues(auditLogRecord,"Male", "Female");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Ethnicity")){
				matchValues(auditLogRecord,"OBC", "FC");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Number Of Children")){
				matchValues(auditLogRecord,"1", "2");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Handicapped")){
				matchValues(auditLogRecord,"Yes", "No");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Marital Status")){
				matchValues(auditLogRecord,"Married", "UnMarried");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Education Level")){
				matchValues(auditLogRecord,"Both Literate", "Both Illiterate");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Poverty Status")){
				matchValues(auditLogRecord,"Very poor", "Poor");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Citizenship")){
				matchValues(auditLogRecord,"Hindu", "Muslim");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Business Activities")){
				matchValues(auditLogRecord,"Trading", "-");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Salutation")){
				matchValues(auditLogRecord,"Mr", "Mrs");
			}
		}
		TestObjectFactory.cleanUpChangeLog();
	}
	
		
	private void createClientForAuditLog() throws Exception {
		Short officeId = 1;
		Short personnel = 3;
		meeting = getMeeting();
		Integer salutaion = 47;
		Integer ethincity = 218;
		Integer citizenship = 130;
		Integer handicapped = 138;
		Integer businessActivities = 225;
		Integer maritalStatus = 66;
		Integer educationLevel = 226;
		Short numChildren = Short.valueOf("1");
		Short gender = Short.valueOf("49");
		Short povertyStatus = Short.valueOf("41");
		
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(
				Short.valueOf("3"), salutaion, "Client", "", "1",
				"");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(
				Short.valueOf("2"), 1, "first",
				"middle", "last", "secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(ethincity, citizenship,handicapped, businessActivities, maritalStatus,
				educationLevel, numChildren, gender, povertyStatus);
		client = new ClientBO(TestObjectFactory.getUserContext(),
				clientNameDetailView.getDisplayName(), CustomerStatus
						.getStatus(new Short("1")), null, null, new Address(),
				getCustomFields(), null, null, personnel, officeId, meeting,
				personnel, new java.util.Date(), null, null, null, YesNoFlag.NO
						.getValue(), clientNameDetailView,
				spouseNameDetailView, clientDetailView, null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class,
				new Integer(client.getCustomerId()).intValue());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
	}

	public void testUpdateMfiInfo_AuditLog() throws Exception {
		createClientForAuditLog();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editMfiInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditMfiInfo");
		addRequestParameter("trained", "0");
		addRequestParameter("externalId", "123");
		addRequestParameter("loanOfficerId", "1");
		
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "updateMfiInfo");		
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.updateMfiInfo_success.toString());
		assertEquals("123", client.getExternalId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		
		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(EntityType.CLIENT.getValue(),client.getCustomerId());
		assertEquals(1,auditLogList.size());
		assertEquals(EntityType.CLIENT.getValue(),auditLogList.get(0).getEntityType());
		assertEquals(client.getCustomerId(),auditLogList.get(0).getEntityId());
		
		assertEquals(2, auditLogList.get(0).getAuditLogRecords().size());

		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Loan Officer Assigned")){
				matchValues(auditLogRecord,"loan officer", "mifos");
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("External Id")){
				matchValues(auditLogRecord,"-", "123");
			}
		}
		TestObjectFactory.cleanUpChangeLog();
	}
	
	public void testEditMfiInfoForClientInBranch() throws Exception {

		createAndSetClientInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editMfiInfo");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.editMfiInfo_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.LOAN_OFFICER_LIST, request));

	}

	public void testEditMfiInfoForClientUnderGroup() throws Exception {

		createClientWithGroupAndSetInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editMfiInfo");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.editMfiInfo_success.toString());
		assertNull(SessionUtils.getAttribute(
				CustomerConstants.LOAN_OFFICER_LIST, request));

	}

	public void testPreviewEditMfiInfo() throws Exception {

		createClientWithGroupAndSetInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditMfiInfo");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previewEditMfiInfo_success.toString());

	}

	public void testPrevEditMfiInfo() throws Exception {

		createClientWithGroupAndSetInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "prevEditMfiInfo");
		addRequestParameter("officeId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.prevEditMfiInfo_success.toString());

	}

	public void testUpdateMfiInfo() throws Exception {

		createClientWithGroupAndSetInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editMfiInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditMfiInfo");
		addRequestParameter("trained", "0");
		addRequestParameter("externalId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "updateMfiInfo");
		
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.updateMfiInfo_success.toString());
		assertEquals("3", client.getExternalId());
		assertEquals(group.getPersonnel().getPersonnelId(), client.getPersonnel().getPersonnelId());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
	}

	public void testUpdateMfiInfoWithoutTrained_ClientInBranch()
			throws Exception {
		createAndSetClientInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editMfiInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditMfiInfo");
		addRequestParameter("trained", "0");
		addRequestParameter("trainedDate", "");
		addRequestParameter("externalId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "updateMfiInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.updateMfiInfo_success.toString());
		assertEquals("3", client.getExternalId());
		assertFalse(client.isTrained());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
	}

	public void testUpdateMfiInfoWithTrained() throws Exception {

		createClientWithGroupAndSetInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editMfiInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditMfiInfo");
		addRequestParameter("trained", "1");
		addRequestParameter("trainedDate", "03/21/2006");
		addRequestParameter("externalId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("loanOfficerId", "3");
		addRequestParameter("groupFlag", "1");
		addRequestParameter("method", "updateMfiInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.updateMfiInfo_success.toString());
		assertEquals("3", client.getExternalId());
		assertTrue(client.isTrained());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
	}

	public void testUpdateMfiInfoWithLoanOfficer() throws Exception {

		createAndSetClientInSession();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "editMfiInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "previewEditMfiInfo");
		addRequestParameter("trained", "1");
		addRequestParameter("trainedDate", "03/21/2006");
		addRequestParameter("externalId", "3");
		addRequestParameter("loanOfficerId", "3");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/clientCustAction.do");
		addRequestParameter("method", "updateMfiInfo");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.updateMfiInfo_success.toString());
		assertEquals("3", client.getExternalId());
		assertTrue(client.isTrained());
		assertEquals(3, client.getPersonnel().getPersonnelId().shortValue());
		client = TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
	}
	
	public void testCreateSuccessUnderGroupInBranch() throws Exception {
		try {
			createParentGroup();
			setRequestPathInfo("/clientCustAction.do");
			addRequestParameter("method", "load");
			addRequestParameter("parentGroupId", group.getCustomerId()
					.toString());
			addRequestParameter("groupFlag", "1");
			addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
			actionPerform();
			flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.load_success.toString());
			List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
			List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
					.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
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
			addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
			int i = 0;
			for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
				addRequestParameter("customField[" + i + "].fieldId",
						customFieldDef.getFieldId().toString());
				addRequestParameter("customField[" + i + "].fieldValue", "Req");
				i++;
			}
			addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.next_success.toString());

			setRequestPathInfo("/clientCustAction.do");
			addRequestParameter("method", "preview");
			addRequestParameter("input", "mfiInfo");
			addRequestParameter("formedByPersonnel", "1");
			addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.preview_success.toString());
			setRequestPathInfo("/clientCustAction.do");
			addRequestParameter("method", "create");
			addRequestParameter("input", "create");
			addRequestParameter("status", "1");
			addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward(ActionForwards.create_success.toString());
			ClientCustActionForm actionForm = (ClientCustActionForm) request
					.getSession().getAttribute("clientCustActionForm");
			client = TestObjectFactory.getObject(ClientBO.class,
					actionForm.getCustomerIdAsInt());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createAndSetClientInSession() throws Exception {
		Short officeId = 1;
		Short personnel = 3;
		meeting = getMeeting();
		ClientNameDetailView clientNameDetailView = new ClientNameDetailView(
				Short.valueOf("3"), 1, "Client", "", "1",
				"");
		ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(
				Short.valueOf("2"), 1, "first",
				"middle", "last", "secondLast");
		ClientDetailView clientDetailView = new ClientDetailView(1, 1, 1, 1, 1,
				1, Short.valueOf("1"), Short.valueOf("1"),Short.valueOf("41"));
		client = new ClientBO(TestObjectFactory.getUserContext(),
				clientNameDetailView.getDisplayName(), CustomerStatus
						.getStatus(new Short("1")), null, null, new Address(),
				getCustomFields(), null, null, personnel, officeId, meeting,
				personnel, new java.util.Date(), null, null, null, YesNoFlag.NO
						.getValue(), clientNameDetailView,
				spouseNameDetailView, clientDetailView, null);
		client.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(ClientBO.class,
				Integer.valueOf(client.getCustomerId()).intValue());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
	}

	private void createClientWithGroupAndSetInSession() throws Exception {
		String name = "Client 1";
		createParentCustomer();
		client = TestObjectFactory.createClient(name,
				CustomerStatus.CLIENT_ACTIVE.getValue(), group, new Date());
		HibernateUtil.closeSession();
		center = TestObjectFactory.getObject(CenterBO.class,
				Integer.valueOf(center.getCustomerId()).intValue());
		group = TestObjectFactory.getObject(GroupBO.class,
				Integer.valueOf(group.getCustomerId()).intValue());
		client = TestObjectFactory.getObject(ClientBO.class,
				Integer.valueOf(client.getCustomerId()).intValue());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
	}

	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(Short.valueOf("5"), "value1",
				CustomFieldType.ALPHA_NUMERIC.getValue()));
		fields.add(new CustomFieldView(Short.valueOf("6"), "value2",
				CustomFieldType.ALPHA_NUMERIC.getValue()));
		return fields;
	}

	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		// meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}

	private void createInitialCustomers() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	private void createParentCustomer() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("group", CustomerStatus.GROUP_ACTIVE, center);
	}
	
	private void createParentGroup() {
		Short officeId = 3;
		Short personnel = 3;
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		group = TestObjectFactory.createGroupUnderBranch("group1", CustomerStatus.GROUP_ACTIVE,
				officeId, meeting, personnel);
		
	}
	
	private void createParentGroup(CustomerStatus status, Short personnel) {
		Short officeId = 3;
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		group = TestObjectFactory.createGroupUnderBranch("group1", status,
				officeId, meeting, personnel);
		
	}
	
	private java.sql.Date offSetCurrentDate(int noOfyears) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year - noOfyears, month,
				day);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}

	private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				Short.valueOf("5"), startDate, loanOffering);

	}

	private void removeFees(List<FeeView> feesToRemove) {
		for (FeeView fee : feesToRemove) {
			TestObjectFactory.cleanUp(new FeePersistence().getFee(fee
					.getFeeIdValue()));
		}
	}

	private List<FeeView> getFees(RecurrenceType frequency) {
		List<FeeView> fees = new ArrayList<FeeView>();
		AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory
				.createPeriodicAmountFee("PeriodicAmountFee",
						FeeCategory.CLIENT, "200", frequency, Short
								.valueOf("2"));
		fees.add(new FeeView(TestObjectFactory.getContext(),fee1));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		return fees;
	}
	
	private void createGroupWithoutFee()throws Exception{
		meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new Date(), MeetingType.CUSTOMERMEETING, "Delhi");
		group = new GroupBO(userContext, "groupName", CustomerStatus.GROUP_PENDING,
				"1234", false, null, null, null,null, Short.valueOf("3"),Short.valueOf("3"), meeting, Short.valueOf("3"));
		group.save();
		HibernateUtil.commitTransaction();
	}

}
