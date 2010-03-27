/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.customers.client.struts.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientDetailView;
import org.mifos.customers.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.customers.client.business.ClientNameDetailView;
import org.mifos.customers.client.business.ClientTestUtils;
import org.mifos.customers.client.business.NameType;
import org.mifos.customers.client.business.service.ClientInformationDto;
import org.mifos.customers.client.persistence.ClientPersistence;
import org.mifos.customers.client.struts.actionforms.ClientCustActionForm;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.persistence.GroupPersistence;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class ClientCustActionStrutsTest extends MifosMockStrutsTestCase {
    public ClientCustActionStrutsTest() throws Exception {
        super();
    }

    private static final double DELTA = 0.00000001;

    private UserContext userContext;

    private CenterBO center;

    private GroupBO group;

    private ClientBO client;

    private MeetingBO meeting;

    private AccountBO accountBO;

    private String flowKey;
    private SavingsOfferingBO savingsOffering1;

    @Override
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        userContext.setPreferredLocale(new Locale("en", "GB"));
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");

        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, ClientCustAction.class);
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

        EntityMasterData.getInstance().init();
        ClientRules.init();
        FieldConfig fieldConfig = FieldConfig.getInstance();
        fieldConfig.init();
        getActionServlet().getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,
                fieldConfig.getEntityMandatoryFieldMap());
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(accountBO);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
            TestObjectFactory.removeObject(savingsOffering1);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoad() throws Exception {
        savingsOffering1 = TestObjectFactory.createSavingsProduct("savingsoffering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.SALUTATION_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.MARITAL_STATUS_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.CITIZENSHIP_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.GENDER_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.HANDICAPPED_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.ETHINICITY_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST, request));

        List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);

        Assert.assertNotNull(povertyStatusList);
        List<SavingsDetailDto> savingsOfferingList = getSavingsOfferingsFromSession();
        Assert.assertNotNull(savingsOfferingList);
        Assert.assertEquals(1, savingsOfferingList.size());
        StaticHibernateUtil.closeSession();
    }

    @SuppressWarnings("unchecked")
    private List<SavingsDetailDto> getSavingsOfferingsFromSession() throws PageExpiredException {
        return (List<SavingsDetailDto>) SessionUtils.getAttribute(ClientConstants.SAVINGS_OFFERING_LIST, request);
    }

    public void testLoadWithGroupHavingNoLoanOfficer() throws Exception {
        createParentGroup(CustomerStatus.GROUP_PARTIAL, null);
        savingsOffering1 = TestObjectFactory.createSavingsProduct("savingsoffering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
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
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.SALUTATION_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.MARITAL_STATUS_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.CITIZENSHIP_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.GENDER_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.HANDICAPPED_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.ETHINICITY_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST, request));
        List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);
        Assert.assertNotNull(povertyStatusList);
        List<SavingsDetailDto> savingsOfferingList = getSavingsOfferingsFromSession();
        Assert.assertNotNull(savingsOfferingList);
        Assert.assertEquals(1, savingsOfferingList.size());
        ClientCustActionForm actionForm = (ClientCustActionForm) request.getSession().getAttribute(
                "clientCustActionForm");
        Assert.assertNull(actionForm.getFormedByPersonnelValue());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        StaticHibernateUtil.closeSession();
    }

    public void testLoadClientUnderGroup_FeeDifferentFrequecny() throws Exception {
        createGroupWithoutFee();
        List<FeeView> fees = getFees(RecurrenceType.MONTHLY);
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("parentGroupId", group.getCustomerId().toString());
        addRequestParameter("groupFlag", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

        ClientCustActionForm actionForm = (ClientCustActionForm) request.getSession().getAttribute(
                "clientCustActionForm");
        Assert.assertEquals(actionForm.getFormedByPersonnelValue(), group.getCustomerFormedByPersonnel()
                .getPersonnelId());

        List<FeeView> additionalFees = (List<FeeView>) SessionUtils.getAttribute(
                CustomerConstants.ADDITIONAL_FEES_LIST, request);
        Assert.assertEquals(0, additionalFees.size());

        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        removeFees(fees);
    }

    public void testLoadClientUnderGroup_FeeSameFrequecny() throws Exception {
        createGroupWithoutFee();
        List<FeeView> fees = getFees(RecurrenceType.WEEKLY);
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("parentGroupId", group.getCustomerId().toString());
        addRequestParameter("groupFlag", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

        ClientCustActionForm actionForm = (ClientCustActionForm) request.getSession().getAttribute(
                "clientCustActionForm");
        Assert.assertEquals(actionForm.getFormedByPersonnelValue(), group.getCustomerFormedByPersonnel()
                .getPersonnelId());

        List<FeeView> additionalFees = (List<FeeView>) SessionUtils.getAttribute(
                CustomerConstants.ADDITIONAL_FEES_LIST, request);
        Assert.assertNotNull(additionalFees);
        Assert.assertEquals(1, additionalFees.size());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
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
        Assert.assertEquals("Client salutation", 1, getErrorSize(CustomerConstants.SALUTATION));
        Assert.assertEquals("Client first Name", 1, getErrorSize(CustomerConstants.FIRST_NAME));
        Assert.assertEquals("Client last Name", 1, getErrorSize(CustomerConstants.LAST_NAME));
        Assert.assertEquals("spouse first Name", 1, getErrorSize(CustomerConstants.SPOUSE_FIRST_NAME));
        Assert.assertEquals("spouse last Name", 1, getErrorSize(CustomerConstants.SPOUSE_LAST_NAME));
        Assert.assertEquals("spouse type", 1, getErrorSize(CustomerConstants.SPOUSE_TYPE));
        Assert.assertEquals("Gender", 1, getErrorSize(CustomerConstants.GENDER));
        Assert.assertEquals("DOB", 1, getErrorSize(CustomerConstants.DOB));
        verifyInputForward();
    }

    public void testFailureNext_WithoutMandatoryCustomField_IfAny() throws Exception {
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();

        boolean isCustomFieldMandatory = false;
        for (CustomFieldView customFieldDef : customFieldDefs) {
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
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "");
            i++;
        }
        actionPerform();

        if (isCustomFieldMandatory) {
            Assert.assertEquals("CustomField", 1, getErrorSize(CustomerConstants.CUSTOM_FIELD));
        } else {
            Assert.assertEquals("CustomField", 0, getErrorSize(CustomerConstants.CUSTOM_FIELD));
        }

    }

    public void testNextSuccess() throws Exception {
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "next");
        addRequestParameter("officeId", "3");
        addRequestParameter("clientName.salutation", "1");
        addRequestParameter("clientName.firstName", "Client");
        addRequestParameter("clientName.lastName", "LastName");
        addRequestParameter("spouseName.firstName", "Spouse");
        addRequestParameter("spouseName.lastName", "LastName");
        addRequestParameter("spouseName.nameType", "1");
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("clientDetailView.gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
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
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "next");
        addRequestParameter("officeId", "3");
        addRequestParameter("clientName.salutation", "1");
        addRequestParameter("clientName.firstName", "Client");
        addRequestParameter("clientName.lastName", "LastName");
        addRequestParameter("spouseName.firstName", "Spouse");
        addRequestParameter("spouseName.lastName", "LastName");
        addRequestParameter("spouseName.nameType", "1");
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("clientDetailView.gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
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
        Assert.assertEquals(1, getErrorSize());
        Assert.assertEquals("Client Trained date not present", 1,
                getErrorSize(CustomerConstants.TRAINED_DATE_MANDATORY));

    }

    public void testPreviewFailureFormedByPersonnelNotPresent() throws Exception {
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "next");
        addRequestParameter("officeId", "3");
        addRequestParameter("clientName.salutation", "1");
        addRequestParameter("clientName.firstName", "Client");
        addRequestParameter("clientName.lastName", "LastName");
        addRequestParameter("spouseName.firstName", "Spouse");
        addRequestParameter("spouseName.lastName", "LastName");
        addRequestParameter("spouseName.nameType", "1");
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("clientDetailView.gender", "1");
        addRequestParameter("input", "personalInfo");
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
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
        Assert.assertEquals(1, getErrorSize());
        Assert.assertEquals("Client formed by not present", 1, getErrorSize(CustomerConstants.FORMED_BY_LOANOFFICER));

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
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
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
        Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.FEE));
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
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "next");
        addRequestParameter("officeId", "3");
        addRequestParameter("clientName.salutation", "1");
        addRequestParameter("clientName.firstName", "Client");
        addRequestParameter("clientName.lastName", "LastName");
        addRequestParameter("spouseName.firstName", "Spouse");
        addRequestParameter("spouseName.lastName", "LastName");
        addRequestParameter("spouseName.nameType", "1");
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("clientDetailView.gender", "1");
        addRequestParameter("input", "personalInfo");
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "Req");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
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
        Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.MEETING_REQUIRED_EXCEPTION));
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
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
        FeeView fee = feeList.get(0);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("input", "mfiInfo");
        addRequestParameter("method", "preview");
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
        addRequestParameter("selectedFee[0].amount", "");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.FEE));
        removeFees(feesToRemove);
    }

    public void testPreviewFailure_DuplicateOfferingsSelected() throws Exception {
        savingsOffering1 = TestObjectFactory.createSavingsProduct("savingsPrd1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);

        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "next");
        addRequestParameter("officeId", "3");
        addRequestParameter("clientName.salutation", "1");
        addRequestParameter("clientName.firstName", "Client");
        addRequestParameter("clientName.lastName", "LastName");
        addRequestParameter("spouseName.firstName", "Spouse");
        addRequestParameter("spouseName.lastName", "LastName");
        addRequestParameter("spouseName.nameType", "1");
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("clientDetailView.gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<SavingsDetailDto> savingsOfferingList = getSavingsOfferingsFromSession();

        SavingsDetailDto savingsOffering = savingsOfferingList.get(0);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("input", "mfiInfo");
        addRequestParameter("formedByPersonnel", "1");
        addRequestParameter("savingsOffering[0]", savingsOffering.getPrdOfferingId().toString());
        addRequestParameter("savingsOffering[1]", savingsOffering.getPrdOfferingId().toString());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, new MeetingBO(RecurrenceType.MONTHLY, Short
                .valueOf("2"), new Date(), MeetingType.CUSTOMER_MEETING), request);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals("Duplicate Offerings", 1, getErrorSize(ClientConstants.ERRORS_DUPLICATE_OFFERING_SELECTED));
    }

    public void testFailurePreview_FeeFrequencyMismatch() throws Exception {
        List<FeeView> feesToRemove = getFees(RecurrenceType.MONTHLY);

        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);
        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
        Assert.assertEquals(1, feeList.size());
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
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("clientDetailView.gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
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
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, new MeetingBO(RecurrenceType.WEEKLY, (short) 2,
                new Date(), MeetingType.CUSTOMER_MEETING), request);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH));
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
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);

        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "next");
        addRequestParameter("officeId", "3");
        addRequestParameter("clientName.salutation", "1");
        addRequestParameter("clientName.firstName", "Client");
        addRequestParameter("clientName.lastName", "LastName");
        addRequestParameter("spouseName.firstName", "Spouse");
        addRequestParameter("spouseName.lastName", "LastName");
        addRequestParameter("spouseName.nameType", "1");
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("clientDetailView.gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "Req");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
        FeeView fee = feeList.get(0);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("input", "mfiInfo");
        addRequestParameter("formedByPersonnel", "1");
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
        addRequestParameter("selectedFee[0].amount", fee.getAmount());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, new MeetingBO(RecurrenceType.MONTHLY, Short
                .valueOf("2"), new Date(), MeetingType.CUSTOMER_MEETING), request);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.preview_success.toString());
        removeFees(feesToRemove);
    }

    public void testSuccessfulPreviewWithSavingsOfferingsSelected() throws Exception {
        savingsOffering1 = TestObjectFactory.createSavingsProduct("savingsPrd1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);

        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "next");
        addRequestParameter("officeId", "3");
        addRequestParameter("clientName.salutation", "1");
        addRequestParameter("clientName.firstName", "Client");
        addRequestParameter("clientName.lastName", "LastName");
        addRequestParameter("spouseName.firstName", "Spouse");
        addRequestParameter("spouseName.lastName", "LastName");
        addRequestParameter("spouseName.nameType", "1");
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("clientDetailView.gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<SavingsDetailDto> savingsOfferingList = getSavingsOfferingsFromSession();

        SavingsDetailDto savingsOffering = savingsOfferingList.get(0);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("input", "mfiInfo");
        addRequestParameter("formedByPersonnel", "1");
        addRequestParameter("savingsOffering[0]", savingsOffering.getPrdOfferingId().toString());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, new MeetingBO(RecurrenceType.MONTHLY, Short
                .valueOf("2"), new Date(), MeetingType.CUSTOMER_MEETING), request);
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

    // FIXME - #000007 - keithw - put back in when savingofferings translation is done!
    public void ignore_testCreateSuccessWithAssociatedSavingsOfferings() throws Exception {
        savingsOffering1 = TestObjectFactory.createSavingsProduct("savingsPrd1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        List<FeeView> feesToRemove = getFees(RecurrenceType.WEEKLY);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "next");
        addRequestParameter("officeId", "3");
        addRequestParameter("clientName.salutation", "1");
        addRequestParameter("clientName.firstName", "Client");
        addRequestParameter("clientName.lastName", "LastName");
        addRequestParameter("spouseName.firstName", "Spouse");
        addRequestParameter("spouseName.lastName", "LastName");
        addRequestParameter("spouseName.nameType", "1");
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("clientDetailView.gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "Req");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        List<SavingsDetailDto> savingsOfferingList = getSavingsOfferingsFromSession();
        SavingsDetailDto savingsOffering = savingsOfferingList.get(0);

        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("input", "mfiInfo");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter("formedByPersonnel", "1");
        addRequestParameter("savingsOffering[0]", savingsOffering.getPrdOfferingId().toString());
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
        ClientCustActionForm actionForm = (ClientCustActionForm) request.getSession().getAttribute(
                "clientCustActionForm");
        client = TestObjectFactory.getClient(actionForm.getCustomerIdAsInt());
        Assert.assertNotNull(client);
        Assert.assertNotNull(client.getOfferingsAssociatedInCreate());
        Assert.assertEquals(1, client.getOfferingsAssociatedInCreate().size());
        for (ClientInitialSavingsOfferingEntity offering : client.getOfferingsAssociatedInCreate()) {
            Assert.assertEquals(savingsOffering1.getPrdOfferingId(), offering.getSavingsOffering().getPrdOfferingId());
            Assert.assertTrue(true);
        }

        removeFees(feesToRemove);
        savingsOffering1 = (SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class, savingsOffering.getPrdOfferingId());
    }

    public void testCreateSuccessWithoutGroup() throws Exception {
        List<FeeView> feesToRemove = getFees(RecurrenceType.WEEKLY);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "next");
        addRequestParameter("officeId", "3");
        addRequestParameter("clientName.salutation", "1");
        addRequestParameter("clientName.firstName", "Client");
        addRequestParameter("clientName.lastName", "LastName");
        addRequestParameter("spouseName.firstName", "Spouse");
        addRequestParameter("spouseName.lastName", "LastName");
        addRequestParameter("spouseName.nameType", "1");
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("clientDetailView.gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
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
        ClientCustActionForm actionForm = (ClientCustActionForm) request.getSession().getAttribute(
                "clientCustActionForm");
        client = TestObjectFactory.getClient(actionForm.getCustomerIdAsInt());
        removeFees(feesToRemove);
    }

    public void testCreateSuccessUnderGroup() throws Exception {
        createParentCustomer();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("parentGroupId", group.getCustomerId().toString());
        addRequestParameter("groupFlag", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());
        List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "next");
        addRequestParameter("officeId", "3");
        addRequestParameter("clientName.salutation", "1");
        addRequestParameter("clientName.firstName", "Client");
        addRequestParameter("clientName.lastName", "LastName");
        addRequestParameter("spouseName.firstName", "Spouse");
        addRequestParameter("spouseName.lastName", "LastName");
        addRequestParameter("spouseName.nameType", "1");
        addRequestDateParameter("dateOfBirth", "20/3/1987");
        addRequestParameter("clientDetailView.gender", "1");
        addRequestParameter("input", "personalInfo");
        addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
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
        ClientCustActionForm actionForm = (ClientCustActionForm) request.getSession().getAttribute(
                "clientCustActionForm");
        client = TestObjectFactory.getClient(actionForm.getCustomerIdAsInt());
    }

    public void testGet() throws Exception {
        createInitialCustomers();
        accountBO = getLoanAccount(client, meeting);
        ClientTestUtils.setDateOfBirth(client, offSetCurrentDate(50));
        TestObjectFactory.updateObject(client);
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalCustNum", client.getGlobalCustNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.get_success.toString());

        ClientInformationDto clientInformationDto = (ClientInformationDto) SessionUtils.getAttribute(
                "clientInformationDto", request);
        Assert.assertEquals("Age of customer should be 50 years", 50, (int) clientInformationDto.getClientDisplay()
                .getAge());

        Assert.assertEquals("No of active loan accounts should be 1",1, clientInformationDto.getLoanAccountsInUse().size());
        StaticHibernateUtil.closeSession();
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        accountBO = (LoanBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());
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
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.SALUTATION_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.MARITAL_STATUS_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.CITIZENSHIP_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.GENDER_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.HANDICAPPED_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.ETHINICITY_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
        List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);
        Assert.assertNotNull(povertyStatusList);

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

        List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);
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
        addRequestDateParameter("dateOfBirth", "");
        addRequestParameter("clientDetailView.gender", "");
        addRequestParameter("customerDetail.povertyStatus", povertyStatusList.get(0).getId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals("Client first Name", 1, getErrorSize(CustomerConstants.FIRST_NAME));
        Assert.assertEquals("Client last Name", 1, getErrorSize(CustomerConstants.LAST_NAME));
        Assert.assertEquals("spouse first Name", 1, getErrorSize(CustomerConstants.SPOUSE_FIRST_NAME));
        Assert.assertEquals("spouse last Name", 1, getErrorSize(CustomerConstants.SPOUSE_LAST_NAME));
        Assert.assertEquals("DOB", 1, getErrorSize(CustomerConstants.DOB));
        verifyInputForward();
    }

    public void testSuccessfulEditPreview() throws Exception {
        createAndSetClientInSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "editPersonalInfo");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "previewEditPersonalInfo");
        addRequestParameter("clientName.firstName", "Client2");
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
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
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();

        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "previewEditPersonalInfo");
        addRequestParameter("clientDetailView.ethinicity", "1");
        int i = 0;
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            addRequestParameter("customField[" + i + "].fieldType", "1");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.previewEditPersonalInfo_success.toString());

        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "updatePersonalInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.updatePersonalInfo_success.toString());
        Assert.assertEquals(1, client.getCustomerDetail().getEthinicity().shortValue());
        client = TestObjectFactory.getClient(client.getCustomerId());

    }

    public void testSuccessfulUpdatePersonalInfo_AuditLog() throws Exception {
        createClientForAuditLog();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "editPersonalInfo");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
        List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);
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
        for (CustomFieldView customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            addRequestParameter("customField[" + i + "].fieldType", "1");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.previewEditPersonalInfo_success.toString());

        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "updatePersonalInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.updatePersonalInfo_success.toString());
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(219, client.getCustomerDetail().getEthinicity().shortValue(), DELTA);

        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.CLIENT, client.getCustomerId());
        Assert.assertEquals(1, auditLogList.size());
        Assert.assertEquals(EntityType.CLIENT.getValue(), auditLogList.get(0).getEntityType());
        Assert.assertEquals(client.getCustomerId(), auditLogList.get(0).getEntityId());

        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Gender")) {
                matchValues(auditLogRecord, "Male", "Female");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Ethnicity")) {
                matchValues(auditLogRecord, "OBC", "FC");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Number Of Children")) {
                matchValues(auditLogRecord, "1", "2");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Handicapped")) {
                matchValues(auditLogRecord, "Yes", "No");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Marital Status")) {
                matchValues(auditLogRecord, "Married", "UnMarried");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Education Level")) {
                matchValues(auditLogRecord, "Both Literate", "Both Illiterate");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Poverty Status")) {
                matchValues(auditLogRecord, "Very poor", "Poor");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Citizenship")) {
                matchValues(auditLogRecord, "Hindu", "Muslim");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Business Activities")) {
                matchValues(auditLogRecord, "Trading", "-");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Salutation")) {
                matchValues(auditLogRecord, "Mr", "Mrs");
            }
        }
        TestObjectFactory.cleanUpChangeLog();
    }

    private void createClientForAuditLog() throws Exception {
        OfficeBO office = new OfficePersistence().getOffice(TestObjectFactory.HEAD_OFFICE);
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(PersonnelConstants.TEST_USER);
        meeting = getMeeting();
        Integer salutation = 47;
        Integer ethincity = 218;
        Integer citizenship = 130;
        Integer handicapped = 138;
        Integer businessActivities = 225;
        Integer educationLevel = 226;
        Short numChildren = Short.valueOf("1");
        Short gender = Short.valueOf("49");
        Short povertyStatus = Short.valueOf("41");

        StaticHibernateUtil.startTransaction();
        ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT, salutation, "Client", "",
                "1", "");
        ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE,
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        ClientDetailView clientDetailView = new ClientDetailView(ethincity, citizenship, handicapped,
                businessActivities, ClientDetailView.MARRIED, educationLevel, numChildren, gender, povertyStatus);
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailView.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, new Address(), getCustomFields(), null, null, personnel, office,
                meeting, personnel, new java.util.Date(), null, null, null, YesNoFlag.NO.getValue(),
                clientNameDetailView, spouseNameDetailView, clientDetailView, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(new Integer(client.getCustomerId()).intValue());
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
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals("123", client.getExternalId());

        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.CLIENT, client.getCustomerId());
        Assert.assertEquals(1, auditLogList.size());
        Assert.assertEquals(EntityType.CLIENT.getValue(), auditLogList.get(0).getEntityType());
        Assert.assertEquals(client.getCustomerId(), auditLogList.get(0).getEntityId());

        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Loan Officer Assigned")) {
                matchValues(auditLogRecord, "loan officer", "mifos");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("External Id")) {
                matchValues(auditLogRecord, "-", "123");
            } else {
                Assert.fail("did not expect record " + auditLogRecord.getFieldName());
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
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.LOAN_OFFICER_LIST, request));

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
        client = TestObjectFactory.getClient(client.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Assert.assertEquals("3", client.getExternalId());
        Assert.assertEquals(group.getPersonnel().getPersonnelId(), client.getPersonnel().getPersonnelId());
    }

    public void testUpdateMfiInfoWithoutTrained_ClientInBranch() throws Exception {
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
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals("3", client.getExternalId());
        Assert.assertFalse(client.isTrained());
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
        addRequestParameter("trainedDate", "03/2/2006"); // a valid date
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
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals("3", client.getExternalId());
        Assert.assertTrue(client.isTrained());
    }

    public void testUpdateMfiInfoWithTrainedDateValidation() throws Exception {

        createClientWithGroupAndSetInSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "editMfiInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "previewEditMfiInfo");
        addRequestParameter("trained", "1");
        addRequestParameter("trainedDate", "3/20/2006"); // an invalid date
        // (D/M/Y)
        addRequestParameter("externalId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("loanOfficerId", "3");
        addRequestParameter("groupFlag", "1");
        addRequestParameter("method", "updateMfiInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { CustomerConstants.INVALID_TRAINED_DATE });
        verifyNoActionMessages();

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
        addRequestParameter("trainedDate", "03/2/2006");
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
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals("3", client.getExternalId());
        Assert.assertTrue(client.isTrained());
        Assert.assertEquals(3, client.getPersonnel().getPersonnelId().shortValue());
    }

    public void testCreateSuccessUnderGroupInBranch() throws Exception {
        try {
            createParentGroup();
            setRequestPathInfo("/clientCustAction.do");
            addRequestParameter("method", "load");
            addRequestParameter("parentGroupId", group.getCustomerId().toString());
            addRequestParameter("groupFlag", "1");
            addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
            actionPerform();
            flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
            verifyNoActionErrors();
            verifyNoActionMessages();
            verifyForward(ActionForwards.load_success.toString());
            List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                    ClientConstants.POVERTY_STATUS, request);
            List<CustomFieldView> customFieldDefs = getCustomFieldFromSession();
            setRequestPathInfo("/clientCustAction.do");
            addRequestParameter("method", "next");
            addRequestParameter("officeId", "3");
            addRequestParameter("clientName.salutation", "1");
            addRequestParameter("clientName.firstName", "Client");
            addRequestParameter("clientName.lastName", "LastName");
            addRequestParameter("spouseName.firstName", "Spouse");
            addRequestParameter("spouseName.lastName", "LastName");
            addRequestParameter("spouseName.nameType", "1");
            addRequestDateParameter("dateOfBirth", "20/3/1987");
            addRequestParameter("clientDetailView.gender", "1");
            addRequestParameter("input", "personalInfo");
            addRequestParameter("customerDetail.povertyStatus", povertyStatus.get(0).getId().toString());
            int i = 0;
            for (CustomFieldView customFieldDef : customFieldDefs) {
                addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
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
            ClientCustActionForm actionForm = (ClientCustActionForm) request.getSession().getAttribute(
                    "clientCustActionForm");
            client = TestObjectFactory.getClient(actionForm.getCustomerIdAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndSetClientInSession() throws Exception {
        OfficeBO office = new OfficePersistence().getOffice(TestObjectFactory.HEAD_OFFICE);
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(PersonnelConstants.TEST_USER);
        meeting = getMeeting();
        ClientNameDetailView clientNameDetailView = new ClientNameDetailView(NameType.CLIENT, 1, "Client", "", "1", "");
        ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(NameType.SPOUSE, 1, "first", "middle",
                "last", "secondLast");
        ClientDetailView clientDetailView = new ClientDetailView(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailView.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, new Address(), getCustomFields(), null, null, personnel, office,
                meeting, personnel, new java.util.Date(), null, null, null, YesNoFlag.NO.getValue(),
                clientNameDetailView, spouseNameDetailView, clientDetailView, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(Integer.valueOf(client.getCustomerId()).intValue());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
    }

    private void createClientWithGroupAndSetInSession() throws Exception {
        String name = "Client 1";
        createParentCustomer();
        client = TestObjectFactory.createClient(name, CustomerStatus.CLIENT_ACTIVE, group, new Date());
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
    }

    private List<CustomFieldView> getCustomFields() {
        List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
        fields.add(new CustomFieldView(Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC));
        fields.add(new CustomFieldView(Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC));
        return fields;
    }

    private MeetingBO getMeeting() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        // meeting.setMeetingStartDate(new GregorianCalendar());
        return meeting;
    }

    private void createInitialCustomers() {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private void createParentCustomer() {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("group", CustomerStatus.GROUP_ACTIVE, center);
    }

    private void createParentGroup() {
        Short officeId = 3;
        Short personnel = 3;
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        group = TestObjectFactory.createGroupUnderBranch("group1", CustomerStatus.GROUP_ACTIVE, officeId, meeting,
                personnel);

    }

    private void createParentGroup(CustomerStatus status, Short personnel) {
        Short officeId = 3;
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        group = TestObjectFactory.createGroupUnderBranch("group1", status, officeId, meeting, personnel);

    }

    private java.sql.Date offSetCurrentDate(int noOfyears) {
        Calendar currentDateCalendar = new GregorianCalendar();
        currentDateCalendar.add(Calendar.YEAR, -noOfyears);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }

    private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", customer, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

    }

    private void removeFees(List<FeeView> feesToRemove) {
        for (FeeView fee : feesToRemove) {
            TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
        }
    }

    private List<FeeView> getFees(RecurrenceType frequency) {
        List<FeeView> fees = new ArrayList<FeeView>();
        AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory.createPeriodicAmountFee("PeriodicAmountFee",
                FeeCategory.CLIENT, "200", frequency, Short.valueOf("2"));
        fees.add(new FeeView(TestObjectFactory.getContext(), fee1));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        return fees;
    }

    private void createGroupWithoutFee() throws Exception {
        meeting = new MeetingBO(WeekDay.MONDAY, TestObjectFactory.EVERY_WEEK, new Date(), MeetingType.CUSTOMER_MEETING,
                "Delhi");
        group = new GroupBO(userContext, "groupName", CustomerStatus.GROUP_PENDING, "1234", false, null, null, null,
                null, new PersonnelPersistence().getPersonnel(Short.valueOf("3")), new OfficePersistence()
                        .getOffice(Short.valueOf("3")), meeting, new PersonnelPersistence().getPersonnel(Short
                        .valueOf("3")));
        new GroupPersistence().saveGroup(group);
        StaticHibernateUtil.commitTransaction();
    }

    @SuppressWarnings("unchecked")
    private List<CustomFieldView> getCustomFieldFromSession() throws PageExpiredException {
        List<CustomFieldView> customFieldDefs = (List<CustomFieldView>) SessionUtils.getAttribute(
                CustomerConstants.CUSTOM_FIELDS_LIST, request);
        return customFieldDefs;
    }
}
