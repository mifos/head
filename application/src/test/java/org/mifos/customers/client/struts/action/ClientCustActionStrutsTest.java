/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.config.ClientRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.customers.client.business.ClientTestUtils;
import org.mifos.customers.client.business.NameType;
import org.mifos.customers.client.persistence.LegacyClientDao;
import org.mifos.customers.client.struts.actionforms.ClientCustActionForm;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.persistence.GroupPersistence;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.domain.builders.MeetingBuilder;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.screen.ClientInformationDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPersonalDetailDto;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.AuditTransactionForTests;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.MifosUser;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@SuppressWarnings("unchecked")
public class ClientCustActionStrutsTest extends MifosMockStrutsTestCase {

    private static final double DELTA = 0.00000001;
    private UserContext userContext;

    private CenterBO center;

    private GroupBO group;

    private ClientBO client;

    private MeetingBO meeting;

    private AccountBO accountBO;

    @Autowired
    LegacyClientDao legacyClientDao;

    private String flowKey;
    private SavingsOfferingBO savingsOffering1;

    @Override
    protected void setStrutsConfig() throws IOException {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }

    @Before
    public void setUp() throws Exception {
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

    @After
    public void tearDown() throws Exception {
        accountBO = null;
        client = null;
        group = null;
        center = null;
        savingsOffering1 = null;
    }

    @Test
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
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.ETHNICITY_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST, request));

        List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);

        Assert.assertNotNull(povertyStatusList);
        List<SavingsDetailDto> savingsOfferingList = getSavingsOfferingsFromSession();
        Assert.assertNotNull(savingsOfferingList);
        Assert.assertEquals(1, savingsOfferingList.size());
        StaticHibernateUtil.flushAndClearSession();
    }

    @SuppressWarnings("unchecked")
    private List<SavingsDetailDto> getSavingsOfferingsFromSession() throws PageExpiredException {
        return (List<SavingsDetailDto>) SessionUtils.getAttribute(ClientConstants.SAVINGS_OFFERING_LIST, request);
    }

    @Test
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
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.ETHNICITY_ENTITY, request));
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
        StaticHibernateUtil.flushAndClearSession();
    }

    @Test
    public void testLoadClientUnderGroup_FeeDifferentFrequecny() throws Exception {
        createGroupWithoutFee();
        List<FeeDto> fees = getFees(RecurrenceType.MONTHLY);
        StaticHibernateUtil.flushAndClearSession();
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

        List<FeeDto> additionalFees = (List<FeeDto>) SessionUtils.getAttribute(
                CustomerConstants.ADDITIONAL_FEES_LIST, request);
        Assert.assertEquals(0, additionalFees.size());

        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        removeFees(fees);
    }

    @Test
    public void testLoadClientUnderGroup_FeeSameFrequecny() throws Exception {
        createGroupWithoutFee();
        List<FeeDto> fees = getFees(RecurrenceType.WEEKLY);
        StaticHibernateUtil.flushAndClearSession();
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

        List<FeeDto> additionalFees = (List<FeeDto>) SessionUtils.getAttribute(
                CustomerConstants.ADDITIONAL_FEES_LIST, request);
        Assert.assertNotNull(additionalFees);
        Assert.assertEquals(1, additionalFees.size());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        removeFees(fees);
    }

    @Test
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

    @Test
    public void testFailureNext_WithoutMandatoryCustomField_IfAny() throws Exception {
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();

        boolean isCustomFieldMandatory = false;
        for (CustomFieldDto customFieldDef : customFieldDefs) {
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
        for (CustomFieldDto customFieldDef : customFieldDefs) {
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

    @Test
    public void testNextSuccess() throws Exception {
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
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
        for (CustomFieldDto customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "20/3/1987");
            i++;
        }
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.next_success.toString());
    }

    @Test
    public void testPreviewFailureForTrainedDate() throws Exception {
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
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
        for (CustomFieldDto customFieldDef : customFieldDefs) {
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

    @Test
    public void testPreviewFailureFormedByPersonnelNotPresent() throws Exception {
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
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
        for (CustomFieldDto customFieldDef : customFieldDefs) {
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

    @Test
    public void testFailurePreview_WithDuplicateFee() throws Exception {
        List<FeeDto> feesToRemove = getFees(RecurrenceType.WEEKLY);
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
        List<ApplicableAccountFeeDto> feeList = (List<ApplicableAccountFeeDto>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
        ApplicableAccountFeeDto fee = feeList.get(0);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("input", "mfiInfo");
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId().toString());
        addRequestParameter("selectedFee[0].amount", "100");
        addRequestParameter("selectedFee[1].feeId", fee.getFeeId().toString());
        addRequestParameter("selectedFee[1].amount", "150");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.FEE));
        removeFees(feesToRemove);
    }

    @Test
    public void testFailurePreview_WithFee_WithoutFeeAmount() throws Exception {
        List<FeeDto> feesToRemove = getFees(RecurrenceType.WEEKLY);
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
        List<ApplicableAccountFeeDto> feeList = (List<ApplicableAccountFeeDto>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request);
        ApplicableAccountFeeDto fee = feeList.get(0);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("input", "mfiInfo");
        addRequestParameter("method", "preview");
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId().toString());
        addRequestParameter("selectedFee[0].amount", "");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.FEE));
        removeFees(feesToRemove);
    }

    @Test
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
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
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
        for (CustomFieldDto customFieldDef : customFieldDefs) {
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

    @Test
    public void testFailurePreview_FeeFrequencyMismatch() throws Exception {
        List<FeeDto> feesToRemove = getFees(RecurrenceType.MONTHLY);

        StaticHibernateUtil.flushAndClearSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
        List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);
        List<ApplicableAccountFeeDto> feeList = (List<ApplicableAccountFeeDto>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request);
        Assert.assertEquals(1, feeList.size());
        ApplicableAccountFeeDto fee = feeList.get(0);

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
        for (CustomFieldDto customFieldDef : customFieldDefs) {
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
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId().toString());
        addRequestParameter("selectedFee[0].amount", fee.getAmount());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, new MeetingBO(RecurrenceType.WEEKLY, (short) 2,
                new Date(), MeetingType.CUSTOMER_MEETING), request);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH));
        removeFees(feesToRemove);
    }

    @Test
    public void testPreviewSuccess() throws Exception {
        List<FeeDto> feesToRemove = getFees(RecurrenceType.MONTHLY);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
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
        for (CustomFieldDto customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "Req");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<ApplicableAccountFeeDto> feeList = (List<ApplicableAccountFeeDto>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request);
        ApplicableAccountFeeDto fee = feeList.get(0);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("input", "mfiInfo");
        addRequestParameter("formedByPersonnel", "1");
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId().toString());
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

    @Test
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
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
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
        for (CustomFieldDto customFieldDef : customFieldDefs) {
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

    @Test
    public void testSuccessfulPrevPersonalInfo() throws Exception {
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "prevPersonalInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.prevPersonalInfo_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testSuccessfulPrevMfiInfo() throws Exception {
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "prevMFIInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.prevMFIInfo_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testCreateSuccessWithAssociatedSavingsOfferings() throws Exception {

        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().nonLoanOfficer().withAdminRole().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        savingsOffering1 = TestObjectFactory.createSavingsProduct("savingsPrd1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        List<FeeDto> feesToRemove = getFees(RecurrenceType.WEEKLY);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
                ClientConstants.POVERTY_STATUS, request);
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
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
        for (CustomFieldDto customFieldDef : customFieldDefs) {
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

        MeetingBO weeklyMeeting = new MeetingBuilder().weekly().every(1).occuringOnA(WeekDay.WEDNESDAY).build();
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, weeklyMeeting, request);
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

    @Test
    public void testCreateSuccessWithoutGroup() throws Exception {

        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().nonLoanOfficer().withAdminRole().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

//        List<FeeDto> feesToRemove = getFees(RecurrenceType.WEEKLY);
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter("groupFlag", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        List<BusinessActivityEntity> povertyStatus = (List<BusinessActivityEntity>) SessionUtils.getAttribute(ClientConstants.POVERTY_STATUS, request);
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
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
        for (CustomFieldDto customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "Req");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        MeetingBO weeklyMeeting = new MeetingBuilder().weekly().every(1).occuringOnA(WeekDay.WEDNESDAY).build();
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, weeklyMeeting, request);

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
//        removeFees(feesToRemove);
    }

    @Test
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
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
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
        for (CustomFieldDto customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "20/3/1987");
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

    @Ignore
    @Test
    public void testGet() throws Exception {
        createInitialCustomers();
        accountBO = getLoanAccount(client, meeting);
        ClientTestUtils.setDateOfBirth(client, offSetCurrentDate(50));
        TestObjectFactory.updateObject(client);
        StaticHibernateUtil.flushAndClearSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalCustNum", client.getGlobalCustNum());
        addRequestParameter("recordOfficeId", "12");
        addRequestParameter("recordLoanOfficerId", "28");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.get_success.toString());

        ClientInformationDto clientInformationDto = (ClientInformationDto) SessionUtils.getAttribute(
                "clientInformationDto", request);
        Assert.assertEquals("Age of customer should be 50 years", 50, (int) clientInformationDto.getClientDisplay()
                .getAge());

        Assert.assertEquals("No of active loan accounts should be 1",1, clientInformationDto.getLoanAccountsInUse().size());
        assertCurrentPageUrl(client.getGlobalCustNum(), 12, 28);
        StaticHibernateUtil.flushAndClearSession();
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        accountBO = (LoanBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());
    }

    private void assertCurrentPageUrl(String globalCustNum, int officerId, int loanOfficerId) throws PageExpiredException {
        String currentPageUrl = (String) SessionUtils.getAttribute("currentPageUrl", request);
        Assert.assertTrue("currentPageUrl must contain clientCustAction.do", currentPageUrl.contains("clientCustAction.do"));
        Assert.assertTrue("currentPageUrl must contain globalCustNum", currentPageUrl.contains("globalCustNum%3D" + globalCustNum));
        Assert.assertTrue("currentPageUrl must contain recordOfficeId", currentPageUrl.contains("recordOfficeId%3D" + officerId));
        Assert.assertTrue("currentPageUrl must contain recordLoanOfficerId", currentPageUrl.contains("recordLoanOfficerId%3D" + loanOfficerId));
    }

    @Test
    public void testEditPersonalInfo() throws Exception {

        createAndSetClientInSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "editPersonalInfo");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.SALUTATION_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.MARITAL_STATUS_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.CITIZENSHIP_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.GENDER_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.SPOUSE_FATHER_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.HANDICAPPED_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(ClientConstants.ETHNICITY_ENTITY, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
        List<BusinessActivityEntity> povertyStatusList = (List<BusinessActivityEntity>) SessionUtils.getAttribute(
               ClientConstants.POVERTY_STATUS, request);
        Assert.assertNotNull(povertyStatusList);

    }

    @Test
    public void testEditPersonalInfoPreviewFailure() throws Exception {

        createAndSetClientInSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "editPersonalInfo");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
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

    @Test
    public void testSuccessfulEditPreview() throws Exception {
        createAndSetClientInSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "editPersonalInfo");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "previewEditPersonalInfo");
        addRequestParameter("clientName.firstName", "Client2");
        int i = 0;
        for (CustomFieldDto customFieldDef : customFieldDefs) {
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

    @Test
    public void testSuccessfulUpdatePersonalInfo() throws Exception {
        createAndSetClientInSession();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "editPersonalInfo");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();

        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "previewEditPersonalInfo");
        addRequestParameter("clientDetailView.ethnicity", "1");
        int i = 0;
        for (CustomFieldDto customFieldDef : customFieldDefs) {
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
        Assert.assertEquals(1, client.getCustomerDetail().getEthnicity().shortValue());
        client = TestObjectFactory.getClient(client.getCustomerId());

    }

    @Test
    // http://mifosforge.jira.com/browse/MIFOSTEST-1092 (additional case)
    public void testEditPhoneNumberSuccessCase1() throws Exception {
        // we have privileges and modify phone number - success
        createAndSetClientInSession();
        modifyClientPersonalInfo("address.phoneNumber", "123");
        verifyForward(ActionForwards.updatePersonalInfo_success.toString());
        Assert.assertEquals("123", client.getCustomerAddressDetail().getAddress().getPhoneNumber());
    }

    @Test
    // http://mifosforge.jira.com/browse/MIFOSTEST-1092
    public void testEditPhoneNumberFailure() throws Exception {
        // we do not have privileges - failure
        createAndSetClientInSession();
        SessionUtils.setAttribute(Constants.USERCONTEXT, createUser(), request.getSession());
        client.getCustomerAddressDetail().getAddress().setPhoneNumber("123");
        modifyClientPersonalInfo("address.phoneNumber", "321");
        verifyActionErrors(new String[] { SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED });
        Assert.assertEquals("123", client.getCustomerAddressDetail().getAddress().getPhoneNumber());
    }

    @Test
    // http://mifosforge.jira.com/browse/MIFOSTEST-1092 (additional case)
    public void testEditPhoneNumberSuccessCase2() throws Exception {
        // we do not have privileges, but modify different field - success
        createAndSetClientInSession();
        SessionUtils.setAttribute(Constants.USERCONTEXT, createUser(), request.getSession());
        client.getCustomerAddressDetail().getAddress().setPhoneNumber("123");
        modifyClientPersonalInfo("clientDetailView.ethnicity", "1");
        verifyForward(ActionForwards.updatePersonalInfo_success.toString());
        Assert.assertEquals("123", client.getCustomerAddressDetail().getAddress().getPhoneNumber());
        Assert.assertEquals(1, client.getCustomerDetail().getEthnicity().shortValue());
    }

    private void modifyClientPersonalInfo(String key, String value) throws Exception {
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "editPersonalInfo");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "previewEditPersonalInfo");
        addRequestParameter(key, value);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.previewEditPersonalInfo_success.toString());

        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "updatePersonalInfo");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
    }

    private UserContext createUser() throws Exception {
        this.userContext = TestUtils.makeUser(TestUtils.TEST_ROLE);
        return userContext;
    }

    @Test
    public void testSuccessfulUpdatePersonalInfo_AuditLog() throws Exception {
        createClientForAuditLog();
        setRequestPathInfo("/clientCustAction.do");
        addRequestParameter("method", "editPersonalInfo");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
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
        addRequestParameter("clientDetailView.ethnicity", Integer.valueOf("219").toString());
        addRequestParameter("clientDetailView.handicapped", Integer.valueOf("139").toString());
        addRequestParameter("clientDetailView.businessActivities", "");
        addRequestParameter("clientDetailView.maritalStatus", Integer.valueOf("67").toString());
        addRequestParameter("clientDetailView.educationLevel", Integer.valueOf("227").toString());
        addRequestParameter("clientDetailView.numChildren", Integer.valueOf("2").toString());

        int i = 0;
        for (CustomFieldDto customFieldDef : customFieldDefs) {
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
        Assert.assertEquals(219, client.getCustomerDetail().getEthnicity().shortValue(), DELTA);
        StaticHibernateUtil.getInterceptor().afterTransactionCompletion(new AuditTransactionForTests());
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

    }

    private void createClientForAuditLog() throws Exception {
        OfficeBO office = new OfficePersistence().getOffice(TestObjectFactory.HEAD_OFFICE);
        PersonnelBO personnel = legacyPersonnelDao.getPersonnel(PersonnelConstants.TEST_USER);
        meeting = getMeeting();
        Integer salutation = 47;
        Integer ethnicity = 218;
        Integer citizenship = 130;
        Integer handicapped = 138;
        Integer businessActivities = 225;
        Integer educationLevel = 226;
        Short numChildren = Short.valueOf("1");
        Short gender = Short.valueOf("49");
        Short povertyStatus = Short.valueOf("41");

        StaticHibernateUtil.startTransaction();
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(), salutation, "Client", "", "1", "");
        clientNameDetailDto.setNames(ClientRules.getNameSequence());
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(), TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        spouseNameDetailView.setNames(ClientRules.getNameSequence());

        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(ethnicity, citizenship, handicapped,
                businessActivities, ClientPersonalDetailDto.MARRIED, educationLevel, numChildren, gender, povertyStatus);

        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, new Address(), getCustomFields(), null, null, personnel, office,
                meeting, personnel, new java.util.Date(), null, null, null, YesNoFlag.NO.getValue(),
                clientNameDetailDto, spouseNameDetailView, clientPersonalDetailDto, null);
        legacyClientDao.saveClient(client);
        StaticHibernateUtil.flushAndClearSession();
        client = TestObjectFactory.getClient(new Integer(client.getCustomerId()).intValue());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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
            List<CustomFieldDto> customFieldDefs = getCustomFieldFromSession();
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
            for (CustomFieldDto customFieldDef : customFieldDefs) {
                addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
                addRequestParameter("customField[" + i + "].fieldValue", "20/3/1987");
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
        PersonnelBO personnel = legacyPersonnelDao.getPersonnel(PersonnelConstants.TEST_USER);
        meeting = getMeeting();
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(), 1, "Client", "", "1", "");
        clientNameDetailDto.setNames(ClientRules.getNameSequence());
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(), 1, "first", "middle", "last", "secondLast");
        spouseNameDetailView.setNames(ClientRules.getNameSequence());

        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        
        Calendar dob = new GregorianCalendar();
        dob.set(Calendar.YEAR, 1970);
        
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, new Address(), getCustomFields(), null, null, personnel, office,
                meeting, personnel, dob.getTime(), null, null, null, YesNoFlag.NO.getValue(),
                clientNameDetailDto, spouseNameDetailView, clientPersonalDetailDto, null);
        legacyClientDao.saveClient(client);
        StaticHibernateUtil.flushAndClearSession();
        client = TestObjectFactory.getClient(Integer.valueOf(client.getCustomerId()).intValue());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
    }

    private void createClientWithGroupAndSetInSession() throws Exception {
        String name = "Client 1";
        createParentCustomer();
        client = TestObjectFactory.createClient(name, CustomerStatus.CLIENT_ACTIVE, group, new Date());
        StaticHibernateUtil.flushAndClearSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
    }

    private List<CustomFieldDto> getCustomFields() {
        List<CustomFieldDto> fields = new ArrayList<CustomFieldDto>();
        fields.add(new CustomFieldDto(Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC.getValue()));
        fields.add(new CustomFieldDto(Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC.getValue()));
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

    private void removeFees(List<FeeDto> feesToRemove) {
//        for (FeeDto fee : feesToRemove) {
//            TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
//        }
    }

    private List<FeeDto> getFees(RecurrenceType frequency) {
        List<FeeDto> fees = new ArrayList<FeeDto>();
        AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory.createPeriodicAmountFee("PeriodicAmountFee",
                FeeCategory.CLIENT, "200", frequency, Short.valueOf("2"));
        fees.add(new FeeDto(TestObjectFactory.getContext(), fee1));
        StaticHibernateUtil.flushAndClearSession();
        return fees;
    }

    private void createGroupWithoutFee() throws Exception {
        meeting = new MeetingBO(WeekDay.MONDAY, TestObjectFactory.EVERY_WEEK, new Date(), MeetingType.CUSTOMER_MEETING,
                "Delhi");
        group = new GroupBO(userContext, "groupName", CustomerStatus.GROUP_PENDING, "1234", false, null, null, null,
                null, legacyPersonnelDao.getPersonnel(Short.valueOf("3")), new OfficePersistence()
                        .getOffice(Short.valueOf("3")), meeting, legacyPersonnelDao.getPersonnel(Short
                        .valueOf("3")));
        new GroupPersistence().saveGroup(group);
        StaticHibernateUtil.flushAndClearSession();
    }

    @SuppressWarnings("unchecked")
    private List<CustomFieldDto> getCustomFieldFromSession() throws PageExpiredException {
        List<CustomFieldDto> customFieldDefs = (List<CustomFieldDto>) SessionUtils.getAttribute(
                CustomerConstants.CUSTOM_FIELDS_LIST, request);
        return customFieldDefs;
    }
}
