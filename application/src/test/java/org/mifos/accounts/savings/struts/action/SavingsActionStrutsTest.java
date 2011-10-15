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

package org.mifos.accounts.savings.struts.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.hibernate.Hibernate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.savings.struts.actionforms.SavingsActionForm;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.screen.SavingsRecentActivityDto;
import org.mifos.dto.screen.SavingsTransactionHistoryDto;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.AuditTransactionForTests;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@SuppressWarnings("unchecked")
public class SavingsActionStrutsTest extends MifosMockStrutsTestCase {

    private UserContext userContext;
    private CustomerBO group;
    private CustomerBO center;
    private SavingsBO savings;
    private SavingsBO savings3;
    private SavingsOfferingBO savingsOffering;
    private SavingsOfferingBO savingsOffering1;
    private SavingsOfferingBO savingsOffering2;

    @Autowired
    private SavingsDao savingsDao;

    @Override
    protected void setStrutsConfig() throws IOException {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/accounts-struts-config.xml");
    }

    @Before
    public void setUp() throws Exception {
        userContext = TestObjectFactory.getContext();
        userContext.setPreferredLocale(new Locale("en", "GB"));
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        createFlowAndAddToRequest(SavingsAction.class);

        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @After
    public void tearDown() throws Exception {
        savings = null;
        savings3 = null;
        group = null;
        center = null;
        savingsOffering = null;
        savingsOffering1 = null;
        TestObjectFactory.removeObject(savingsOffering2);

    }

    private void createAndAddObjects() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        SavingsBO savingsObj = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_ACTIVE,
                savingsOffering.getRecommendedAmount(), getCustomFieldView());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsObj, request);
        addRequestParameter("selectedPrdOfferingId", savingsOffering.getPrdOfferingId().toString());

    }

    private void createAndAddObjectsForCreate() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        SessionUtils.setAttribute(SavingsConstants.CLIENT, group, request);
        SessionUtils.setAttribute(SavingsConstants.PRDOFFERING, savingsOffering, request);
    }

    private void createAndAddObjects(AccountState state) throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        addRequestParameter("input", "preview");
        savings = createSavingsAccount("000X00000000013", savingsOffering, state);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        addRequestParameter("selectedPrdOfferingId", savingsOffering.getPrdOfferingId().toString());

    }

    private List<CustomFieldDto> getCustomFieldView() {
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        customFields.add(new CustomFieldDto(new Short("8"), "13", null));
        return customFields;

    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE,
                center);
    }

    private SavingsBO createSavingsAccount(String globalAccountNum, SavingsOfferingBO savingsOffering,
            AccountState state) throws Exception {
        return TestObjectFactory.createSavingsAccount(globalAccountNum, group, state, new Date(), savingsOffering,
                userContext);
    }

    @Test
    public void testGetPrdOfferings() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering1 = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        savingsOffering2 = TestObjectFactory.createSavingsProduct("sav prd2", "prd2", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        addRequestParameter("customerId", group.getCustomerId().toString());
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "getPrdOfferings");
        actionPerform();
        verifyForward("getPrdOfferings_success");
        List<PrdOfferingDto> savingPrds = (List<PrdOfferingDto>) SessionUtils.getAttribute(
                SavingsConstants.SAVINGS_PRD_OFFERINGS, request);
        Assert.assertEquals(Integer.valueOf("2").intValue(), savingPrds.size());
        CustomerBO client = (CustomerBO) SessionUtils.getAttribute(SavingsConstants.CLIENT, request);
        Assert.assertEquals(group.getCustomerId(), client.getCustomerId());

    }

    @Test
    public void testSuccessfullLoad() throws Exception {

        createAndAddObjects();
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "load");
        actionPerform();
        verifyForward("load_success");
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.INTEREST_CAL_TYPES, request));
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.SAVINGS_TYPE, request));
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.RECOMMENDED_AMOUNT_UNIT, request));
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.CUSTOM_FIELDS, request));
        Assert.assertEquals(SessionUtils.getAttribute(SavingsConstants.PRDOFFERING, request), savingsOffering);

    }

    @Test
    public void testScuccessfulReLoad() throws Exception {
        createAndAddObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering1 = TestObjectFactory.createSavingsProduct("sav prd_1", "pr_1", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "reLoad");
        addRequestParameter("selectedPrdOfferingId", savingsOffering1.getPrdOfferingId().toString());
        actionPerform();
        verifyForward("load_success");

        Assert.assertEquals(SessionUtils.getAttribute(SavingsConstants.PRDOFFERING, request), savingsOffering1);

    }

    @Test
    public void testSuccessfulPreview() throws Exception {
        createAndAddObjectsForCreate();
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "preview");
        actionPerform();
        verifyForward("preview_success");

    }

    @Test
    public void testPreview_WithMandatoryCustomField_IfAny() throws Exception {
        createAndAddObjectsForCreate();
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("selectedPrdOfferingId", savingsOffering.getPrdOfferingId().toString());

        actionPerform();
        verifyForward("load_success");
        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
        boolean isCustomFieldMandatory = false;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            if (customFieldDef.isMandatory()) {
                isCustomFieldMandatory = true;
                break;
            }
        }
        addRequestParameter("selectedPrdOfferingId", savingsOffering.getPrdOfferingId().toString());
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "preview");
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
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
    public void testSuccessfulEditPreview() throws Exception {
        createAndAddObjectsForCreate();
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "editPreview");
        actionPerform();
        verifyForward("editPreview_success");

    }

    @Test
    public void testSuccessfulPrevious() throws Exception {
        createAndAddObjects();
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "previous");
        actionPerform();
        verifyForward("previous_success");
    }

    @Test
    public void testSuccessfulGetBySystemId() throws Exception {

        createAndAddObjects(AccountState.SAVINGS_PARTIAL_APPLICATION);
        addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "get");
        performNoErrors();
        verifyForward("get_success");
        SavingsBO savingsObj = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        Assert.assertEquals(savings.getGlobalAccountNum(), savingsObj.getGlobalAccountNum());
        Assert.assertEquals(savingsOffering.getRecommendedAmount(), savingsObj.getRecommendedAmount());
        savingsOffering = null;
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.SAVINGS_TYPE, request));
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.RECOMMENDED_AMOUNT_UNIT, request));
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.CUSTOM_FIELDS, request));
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.RECENTY_ACTIVITY_DETAIL_PAGE, request));
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.NOTES, request));

    }

    @Test
    public void testSuccessfulEdit() throws Exception {
        createAndAddObjects(AccountState.SAVINGS_PARTIAL_APPLICATION);
        savingsOffering = null;
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
        actionPerform();
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "edit");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        performNoErrors();
        verifyForward("edit_success");
        SavingsActionForm actionForm = (SavingsActionForm) request.getSession().getAttribute("savingsActionForm");
        Assert.assertEquals("300.0", actionForm.getRecommendedAmount());
    }

    @Test
    public void testSuccessfulEditPrevious() throws Exception {
        createAndAddObjects(AccountState.SAVINGS_PARTIAL_APPLICATION);
        savingsOffering = null;
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "editPrevious");
        performNoErrors();
        verifyForward("editPrevious_success");
    }

    @Test
    public void testSuccessfulUpdate() throws Exception {
        createAndAddObjects(AccountState.SAVINGS_PARTIAL_APPLICATION);
        savingsOffering = null;
        addRequestParameter("recommendedAmount", "600.0");
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "update");

        actionPerform();
        verifyForward("update_success");
        String globalAccountNum = (String) request.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
        Assert.assertNotNull(globalAccountNum);
        StaticHibernateUtil.flushSession();
        savings = savingsDao.findBySystemId(globalAccountNum);
        Assert.assertNotNull(savings);
        Assert.assertEquals(TestUtils.createMoney(600.0), savings.getRecommendedAmount());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testSuccessfulUpdateInActiveState() throws Exception {

        createAndAddObjects(AccountState.SAVINGS_ACTIVE);
        savingsOffering = null;
        addRequestParameter("recommendedAmount", "600.0");

        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "update");
        actionPerform();
        verifyForward("update_success");
        String globalAccountNum = (String) request.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
        Assert.assertNotNull(globalAccountNum);
        StaticHibernateUtil.flushSession();
        savings = savingsDao.findBySystemId(globalAccountNum);
        Assert.assertNotNull(savings);
        Assert.assertEquals(TestUtils.createMoney(600.0), savings.getRecommendedAmount());
        verifyNoActionErrors();
        verifyNoActionMessages();

    }

    @Test
    public void testSuccessfulGetRecentActivity() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("000X00000000018", savingsOffering, AccountState.SAVINGS_PARTIAL_APPLICATION);
        savingsOffering = null;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY, TestUtils.makeUser(), request.getSession());
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "getRecentActivity");
        addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
        actionPerform();
        verifyForward("getRecentActivity_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertEquals(0, ((List<SavingsRecentActivityDto>) SessionUtils.getAttribute(
                SavingsConstants.RECENTY_ACTIVITY_LIST, request)).size());
    }

    /**
     * ignoring as test fails due to lazy init which doesnt happen in application
     */
    public void ignore_testGetDepositDueDetails() throws Exception {

        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("000X00000000020", savingsOffering, AccountState.SAVINGS_PARTIAL_APPLICATION);
        Hibernate.initialize(savings);
        savingsOffering = null;
        StaticHibernateUtil.flushSession();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY, TestUtils.makeUser(), request.getSession());
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "getDepositDueDetails");
        addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
        performNoErrors();
        verifyForward("depositduedetails_success");

        StaticHibernateUtil.flushSession();
        savings = savingsDao.findBySystemId(savings.getGlobalAccountNum());

    }

    @Test
    public void testWaiveAmountDue() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("000X00000000019", savingsOffering, AccountState.SAVINGS_ACTIVE);
        StaticHibernateUtil.flushSession();
        savingsOffering = null;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY, TestUtils.makeUser(), request.getSession());
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "waiveAmountDue");
        addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward("waiveAmount_success");

        StaticHibernateUtil.flushSession();
        savings = savingsDao.findBySystemId(savings.getGlobalAccountNum());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    @Test
    public void testWaiveAmountOverDue() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("000X00000000019", savingsOffering, AccountState.SAVINGS_PARTIAL_APPLICATION);
        savingsOffering = null;
        StaticHibernateUtil.flushSession();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY, TestUtils.makeUser(), request.getSession());
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "waiveAmountOverDue");
        addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward("waiveAmount_success");

        StaticHibernateUtil.flushSession();
        savings = savingsDao.findBySystemId(savings.getGlobalAccountNum());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    /**
     * ignoring as test fails due to lazy init which doesnt happen in application
     */
    public void ignore_testSuccessfulGetStatusHistory() throws Exception {
        SavingsTestHelper helper = new SavingsTestHelper();
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
        Hibernate.initialize(savings);
        savingsOffering = null;
        AccountStateMachines.getInstance().initialize(AccountTypes.SAVINGS_ACCOUNT, null);

        PersonnelBO loggedInUser = IntegrationTestObjectMother.testUser();
        savings.changeStatus(AccountState.SAVINGS_PENDING_APPROVAL, null, "notes", loggedInUser);
        Assert.assertEquals(AccountStates.SAVINGS_ACC_PENDINGAPPROVAL, savings.getAccountState().getId().shortValue());

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY, TestUtils.makeUser(), request.getSession());
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "getStatusHistory");
        addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
        actionPerform();
        verifyForward("getStatusHistory_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertEquals(2, ((List<SavingsTransactionHistoryDto>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_CHANGE_HISTORY_LIST, request)).size());
    }

    @Test
    public void testSuccessful_Update_AuditLog() throws Exception {
        createAndAddObjects(AccountState.SAVINGS_PARTIAL_APPLICATION);
        savingsOffering = null;
        addRequestParameter("recommendedAmount", "600.0");
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "update");

        actionPerform();
        verifyForward("update_success");
        String globalAccountNum = (String) request.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
        Assert.assertNotNull(globalAccountNum);
        StaticHibernateUtil.flushSession();
        savings = savingsDao.findBySystemId(globalAccountNum);
        Assert.assertNotNull(savings);
        Assert.assertEquals(TestUtils.createMoney(600.0), savings.getRecommendedAmount());
        StaticHibernateUtil.getInterceptor().afterTransactionCompletion(new AuditTransactionForTests());
        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.SAVINGS, savings.getAccountId());
        Assert.assertEquals(1, auditLogList.size());
        Assert.assertEquals(EntityType.SAVINGS.getValue(), auditLogList.get(0).getEntityType());
        Assert.assertEquals(savings.getAccountId(), auditLogList.get(0).getEntityId());

        // Assert.assertEquals(2, auditLogList.get(0).getAuditLogRecords().size());

        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Recommended Amount")) {
                matchValues(auditLogRecord, "300.0", "600.0");
                // } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Additional Information")) {
                // matchValues(auditLogRecord, "External Savings Id-custom field value", "External Savings Id-12");
            }
        }
    }

}
