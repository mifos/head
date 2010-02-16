/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.customers.center.struts.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.PositionEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.persistence.CenterPersistence;
import org.mifos.customers.center.struts.actionforms.CenterCustActionForm;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CenterActionStrutsTest extends MifosMockStrutsTestCase {
    public CenterActionStrutsTest() throws Exception {
        super();
    }

    private CenterBO center;

    private GroupBO group;

    private ClientBO client;

    private String flowKey;

    private SavingsTestHelper helper = new SavingsTestHelper();

    private SavingsOfferingBO savingsOffering;

    private SavingsBO savingsBO;

    private static final String dateFormat = "dd/MM/yyyy";

    @Override 
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestDatabase.resetMySQLDatabase();
        UserContext userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());

        flowKey = createFlow(request, CenterCustAction.class);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

        EntityMasterData.getInstance().init();
        FieldConfig fieldConfig = FieldConfig.getInstance();
        fieldConfig.init();
        getActionServlet().getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,
                fieldConfig.getEntityMandatoryFieldMap());
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(savingsBO);
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoad() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.LOAN_OFFICER_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));

        CenterCustActionForm actionForm = (CenterCustActionForm) request.getSession().getAttribute(
                "centerCustActionForm");

        String currentDate = DateUtils.getCurrentDate(TestUtils.ukLocale());
        SimpleDateFormat retrievedFormat = new SimpleDateFormat(dateFormat);
        SimpleDateFormat localFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, TestUtils
                .ukLocale());
        Date curDate = localFormat.parse(currentDate);
        Date retrievedDate = retrievedFormat.parse(actionForm.getMfiJoiningDate());
       Assert.assertEquals(curDate, retrievedDate);
    }

    public void testFailurePreviewWithAllValuesNull() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
       Assert.assertEquals("Center Name", 1, getErrorSize(CustomerConstants.NAME));
       Assert.assertEquals("Loan Officer", 1, getErrorSize(CustomerConstants.LOAN_OFFICER));
       Assert.assertEquals("Meeting", 1, getErrorSize(CustomerConstants.MEETING));
        verifyInputForward();
    }

    public void testFailurePreviewWithNameNotNull() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("officeId", "3");
        addRequestParameter("displayName", "center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
       Assert.assertEquals("Center Name", 0, getErrorSize(CustomerConstants.NAME));
       Assert.assertEquals("Loan Officer", 1, getErrorSize(CustomerConstants.LOAN_OFFICER));
       Assert.assertEquals("Meeting", 1, getErrorSize(CustomerConstants.MEETING));
        verifyInputForward();
    }

    public void testFailurePreviewWithLoanOfficerNotNull() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("officeId", "3");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
       Assert.assertEquals("Center Name", 0, getErrorSize(CustomerConstants.NAME));
       Assert.assertEquals("Loan Officer", 0, getErrorSize(CustomerConstants.LOAN_OFFICER));
       Assert.assertEquals("Meeting", 1, getErrorSize(CustomerConstants.MEETING));
        verifyInputForward();
    }

    public void testFailurePreviewWithMeetingNull() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();

        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("officeId", "3");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals("Meeting", 1, getErrorSize(CustomerConstants.MEETING));
        verifyInputForward();
    }

    public void testFailurePreview_WithoutMandatoryCustomField_IfAny() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
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
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("officeId", "3");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "");
            i++;
        }
        actionPerform();

        if (isCustomFieldMandatory)
           Assert.assertEquals("CustomField", 1, getErrorSize(CustomerConstants.CUSTOM_FIELD));
        else
           Assert.assertEquals("CustomField", 0, getErrorSize(CustomerConstants.CUSTOM_FIELD));
    }

    public void testFailurePreview_WithDuplicateFee() throws Exception {
        List<FeeView> feesToRemove = getFees(RecurrenceType.MONTHLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();

        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
        FeeView fee = feeList.get(0);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
        addRequestParameter("selectedFee[0].amount", "100");
        addRequestParameter("selectedFee[1].feeId", fee.getFeeId());
        addRequestParameter("selectedFee[1].amount", "150");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.FEE));
        removeFees(feesToRemove);
    }

    public void testFailurePreview_WithFee_WithoutFeeAmount() throws Exception {
        List<FeeView> feesToRemove = getFees(RecurrenceType.MONTHLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
        FeeView fee = feeList.get(0);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
        addRequestParameter("selectedFee[0].amount", "");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.FEE));
        removeFees(feesToRemove);
    }

    public void testFailurePreview_FeeFrequencyMismatch() throws Exception {
        List<FeeView> feesToRemove = getFees(RecurrenceType.WEEKLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();
        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
        FeeView fee = feeList.get(0);
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, getMeeting(), request);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
        addRequestParameter("selectedFee[0].amount", "200");
        actionPerform();
       Assert.assertEquals("Fee", 1, getErrorSize(CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH));
        removeFees(feesToRemove);
    }

    public void testSuccessfulPreview() throws Exception {
        List<FeeView> feesToRemove = getFees(RecurrenceType.MONTHLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();

        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, new MeetingBO(RecurrenceType.MONTHLY, Short
                .valueOf("2"), new Date(), MeetingType.CUSTOMER_MEETING), request);
        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
        FeeView fee = feeList.get(0);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            i++;
        }
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
        addRequestParameter("selectedFee[0].amount", fee.getAmount());
        actionPerform();

       Assert.assertEquals(0, getErrorSize());

        verifyForward(ActionForwards.preview_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        removeFees(feesToRemove);
    }

    public void testSuccessfulPrevious() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "previous");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.previous_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testSuccessfulCreate() throws Exception {
        List<FeeBO> allFeeList = getFeesForCreate(RecurrenceType.MONTHLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, getMeeting(), request);

        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
       Assert.assertEquals(1, feeList.size());
        FeeView fee = feeList.get(0);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            i++;
        }
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
        addRequestParameter("selectedFee[0].amount", fee.getAmount());
        actionPerform();
        verifyForward(ActionForwards.preview_success.toString());
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.create_success.toString());
        removeInactiveFees(allFeeList);
        CenterCustActionForm actionForm = (CenterCustActionForm) request.getSession().getAttribute(
                "centerCustActionForm");
        center = TestObjectFactory.getCenter(actionForm.getCustomerIdAsInt());
    }

    public void testManage() throws Exception {
        createAndSetCenterInSession();
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "manage");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.LOAN_OFFICER_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.CLIENT_LIST, request));
        Assert.assertNotNull(SessionUtils.getAttribute(CustomerConstants.POSITIONS, request));

        CenterCustActionForm actionForm = (CenterCustActionForm) request.getSession().getAttribute(
                "centerCustActionForm");
       Assert.assertEquals(center.getPersonnel().getPersonnelId(), actionForm.getLoanOfficerIdValue());
    }

    public void testFailureEditPreviewWithLoanOfficerNull() throws Exception {
        createAndSetCenterInSession();
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "manage");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter("loanOfficerId", "");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
       Assert.assertEquals("Loan Officer", 1, getErrorSize(CustomerConstants.LOAN_OFFICER));
        verifyInputForward();
    }

    public void testFailureEditPreviewWith_MandatoryCustomFieldNull() throws Exception {
        createAndSetCenterInSession();
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "manage");
        addRequestParameter("officeId", "3");
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

        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter("loanOfficerId", "");
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        if (isCustomFieldMandatory)
           Assert.assertEquals("CustomField", 1, getErrorSize(CustomerConstants.CUSTOM_FIELD));
        else
           Assert.assertEquals("CustomField", 0, getErrorSize(CustomerConstants.CUSTOM_FIELD));
        verifyInputForward();
    }

    public void testSuccessfulEditPreview() throws Exception {
        createAndSetCenterInSession();
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "manage");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter("mfiJoiningDateDD", "01");
        addRequestParameter("mfiJoiningDateMM", "01");
        addRequestParameter("mfiJoiningDateYY", "01");
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

       Assert.assertEquals(0, getErrorSize());

        verifyForward(ActionForwards.editpreview_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testSuccessfulUpdate() throws Exception {
        Short officeId = (short) 3;
        Short loanOfficerId = (short) 1;
        center = new CenterBO(TestUtils.makeUser(), "center", new Address(), null, null, null, null,
                new OfficePersistence().getOffice(officeId), getMeeting(), new PersonnelPersistence()
                        .getPersonnel(loanOfficerId), new CustomerPersistence());
        new CenterPersistence().saveCenter(center);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(new Integer(center.getCustomerId()).intValue());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, center, request);

        createGroupAndClient();
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "manage");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
        List<PositionEntity> positions = (List<PositionEntity>) SessionUtils.getAttribute(CustomerConstants.POSITIONS,
                request);

        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        addRequestParameter("externalId", "12");
        addRequestParameter("mfiJoiningDateDD", "01");
        addRequestParameter("mfiJoiningDateMM", "01");
        addRequestParameter("mfiJoiningDateYY", "01");
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            addRequestParameter("customField[" + i + "].fieldType", customFieldDef.getFieldType().toString());
            i++;
        }
        i = 0;
        for (PositionEntity position : positions) {
            addRequestParameter("customerPosition[" + i + "].positionId", position.getId().toString());
            addRequestParameter("customerPosition[" + i + "].customerId", "");
            i++;
        }
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
       Assert.assertEquals(0, getErrorSize());

        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.update_success.toString());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
       Assert.assertEquals(positions.size(), center.getCustomerPositions().size());
       Assert.assertEquals("12", center.getExternalId());

    }

    private void createAndSetCenterInSession() throws Exception {
        String name = "manage_center";
        center = TestObjectFactory.createWeeklyFeeCenter(name, getMeeting());
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(Integer.valueOf(center.getCustomerId()).intValue());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, center, request);
    }

    public void testSuccessfulEditPrevious() throws Exception {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "editPrevious");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.editprevious_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testGet() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        savingsBO = getSavingsAccount("fsaf6", "ads6", center);
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalCustNum", center.getGlobalCustNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.get_success.toString());
        CustomerBO centerBO = (CenterBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        Assert.assertNotNull(center);
       Assert.assertEquals(center.getCustomerId(), centerBO.getCustomerId());
        List children = (List) SessionUtils.getAttribute(CenterConstants.GROUP_LIST, request);
        Assert.assertNotNull(children);
       Assert.assertEquals(1, children.size());
       Assert.assertEquals("Size of the active accounts should be 1", 1, ((List<SavingsBO>) SessionUtils.getAttribute(
                ClientConstants.CUSTOMERSAVINGSACCOUNTSINUSE, request)).size());
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
    }

    public void testFlowSuccess() throws Exception {
        getFees(RecurrenceType.MONTHLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        actionPerform();

        FlowManager fm = (FlowManager) SessionUtils.getAttribute(Constants.FLOWMANAGER, request.getSession());
        String flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
       Assert.assertEquals(true, fm.isFlowValid(flowKey));

        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, getMeeting(), request);
        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
        FeeView fee = feeList.get(0);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            i++;
        }
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
        addRequestParameter("selectedFee[0].amount", fee.getAmount());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals(true, fm.isFlowValid(flowKey));

        verifyForward(ActionForwards.preview_success.toString());
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        verifyNoActionErrors();
        verifyForward(ActionForwards.create_success.toString());

        CenterCustActionForm actionForm = (CenterCustActionForm) request.getSession().getAttribute(
                "centerCustActionForm");
        center = TestObjectFactory.getCenter(actionForm.getCustomerIdAsInt());
       Assert.assertEquals(false, fm.isFlowValid(flowKey));
    }

    public void testFlowFailure() throws Exception {
        getFees(RecurrenceType.MONTHLY);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("officeId", "3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        FlowManager fm = (FlowManager) SessionUtils.getAttribute(Constants.FLOWMANAGER, request.getSession());
        String flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
       Assert.assertEquals(true, fm.isFlowValid(flowKey));

        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_MEETING, getMeeting(), request);
        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
        List<FeeView> feeList = (List<FeeView>) SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
                request);
        FeeView fee = feeList.get(0);
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("displayName", "center");
        addRequestParameter("loanOfficerId", "1");
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            i++;
        }
        addRequestParameter("selectedFee[0].feeId", fee.getFeeId());
        addRequestParameter("selectedFee[0].amount", fee.getAmount());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals(true, fm.isFlowValid(flowKey));

        verifyForward(ActionForwards.preview_success.toString());
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        verifyNoActionErrors();
        verifyForward(ActionForwards.create_success.toString());

        CenterCustActionForm actionForm = (CenterCustActionForm) request.getSession().getAttribute(
                "centerCustActionForm");
        center = TestObjectFactory.getCenter(actionForm.getCustomerIdAsInt());
       Assert.assertEquals(false, fm.isFlowValid(flowKey));

        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyActionErrors(new String[] { "exception.framework.PageExpiredException" });
        verifyForwardPath("/pages/framework/jsp/pageexpirederror.jsp");
    }

    public void testLoadSearch() throws Exception {
        addActionAndMethod(Methods.loadSearch.toString());
        addRequestParameter("input", "search");
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.loadSearch_success.toString());
    }

    public void testSearch() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("SearchCenter", meeting);
        addActionAndMethod(Methods.search.toString());
        addRequestParameter("searchString", "Sear");
        addRequestParameter("input", "search");

        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.search_success.toString());
        QueryResult queryResult = (QueryResult) SessionUtils.getAttribute(Constants.SEARCH_RESULTS, request);
        Assert.assertNotNull(queryResult);
       Assert.assertEquals(1, queryResult.getSize());
       Assert.assertEquals(1, queryResult.get(0, 10).size());
    }

    private void addActionAndMethod(String method) {
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", method);

    }

    private SavingsBO getSavingsAccount(String offeringName, String shortName, CustomerBO customer) throws Exception {
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", customer,
                AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, new Date(System.currentTimeMillis()), savingsOffering);
    }

    private MeetingBO getMeeting() throws Exception {
        MeetingBO meeting = new MeetingBO(Short.valueOf("2"), Short.valueOf("2"), new Date(),
                MeetingType.CUSTOMER_MEETING, "MeetingPlace");
        return meeting;
    }

    private List<FeeView> getFees(RecurrenceType frequency) throws Exception {
        List<FeeView> fees = new ArrayList<FeeView>();
        AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory.createPeriodicAmountFee("PeriodicAmountFee",
                FeeCategory.CENTER, "200", frequency, Short.valueOf("2"));
        fees.add(new FeeView(TestObjectFactory.getContext(), fee1));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        return fees;
    }

    private List<FeeBO> getFeesForCreate(RecurrenceType frequency) throws Exception {
        List<FeeBO> fees = new ArrayList<FeeBO>();
        AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory.createPeriodicAmountFee("PeriodicAmountFee",
                FeeCategory.CENTER, "200", frequency, Short.valueOf("2"));
        AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory.createOneTimeAmountFee("OneTimeAmountFee",
                FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
        fee2.updateStatus(FeeStatus.INACTIVE);
        fee2.update();
        fees.add(fee1);
        fees.add(fee2);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        return fees;
    }

    private void removeFees(List<FeeView> feesToRemove) {
        for (FeeView fee : feesToRemove) {
            TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
        }
    }

    private void removeInactiveFees(List<FeeBO> feesToRemove) {
        for (FeeBO fee : feesToRemove) {
            if (!fee.isActive())
                TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeId()));
        }
    }

    private void createGroupAndClient() {
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("client", CustomerStatus.CLIENT_ACTIVE, group);
    }
}
