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

package org.mifos.accounts.savings.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsRecentActivityView;
import org.mifos.accounts.savings.business.SavingsTransactionHistoryView;
import org.mifos.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.savings.struts.actionforms.SavingsActionForm;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsActionStrutsTest extends MifosMockStrutsTestCase {

    public SavingsActionStrutsTest() throws Exception {
        super();
    }

    private UserContext userContext;

    private CustomerBO group;

    private CustomerBO center;

    private SavingsBO savings;

    private SavingsBO savings3;

    private SavingsOfferingBO savingsOffering;

    private SavingsOfferingBO savingsOffering1;

    private SavingsOfferingBO savingsOffering2;

    @Override 
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/accounts-struts-config.xml");
    }
        
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        userContext.setPreferredLocale(new Locale("en", "GB"));
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        createFlowAndAddToRequest(SavingsAction.class);
    }

    @Override
    public void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(savings);
            TestObjectFactory.cleanUp(savings3);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
            TestObjectFactory.removeObject(savingsOffering);
            TestObjectFactory.removeObject(savingsOffering1);
            TestObjectFactory.removeObject(savingsOffering2);
            StaticHibernateUtil.closeSession();
        } catch (Exception e) {
            /*
             * If this was caused by an underlying failure, we don't want to
             * swallow the underlying failure.
             */
            e.printStackTrace();
        }
        super.tearDown();
    }

    private void createAndAddObjects() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        SavingsBO savingsObj = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_ACTIVE,
                savingsOffering.getRecommendedAmount(), getCustomFieldView());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsObj, request);
        addRequestParameter("selectedPrdOfferingId", savingsOffering.getPrdOfferingId().toString());

    }

    private void createAndAddObjectsForCreate() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        SessionUtils.setAttribute(SavingsConstants.CLIENT, group, request);
        SessionUtils.setAttribute(SavingsConstants.PRDOFFCERING, savingsOffering, request);
    }

    private void createAndAddObjects(AccountState state) throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        addRequestParameter("input", "preview");
        savings = createSavingsAccount("000X00000000013", savingsOffering, state);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        addRequestParameter("selectedPrdOfferingId", savingsOffering.getPrdOfferingId().toString());

    }

    private List<CustomFieldView> getCustomFieldView() {
        List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();
        customFields.add(new CustomFieldView(new Short("8"), "13", CustomFieldType.NONE));
        return customFields;

    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
    }

    private SavingsBO createSavingsAccount(String globalAccountNum, SavingsOfferingBO savingsOffering,
            AccountState state) throws Exception {
        return TestObjectFactory.createSavingsAccount(globalAccountNum, group, state, new Date(), savingsOffering,
                userContext);
    }

    public void testSuccessfulUpdate_WithCustomField() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd2", "prd2", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        savings = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_PARTIAL_APPLICATION,
                new Money(getCurrency(), "100"), null);
        savings.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findById(savings.getAccountId());
        savings.setUserContext(userContext);
       Assert.assertEquals(0, savings.getAccountCustomFields().size());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
        actionPerform();

        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "edit");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        addRequestParameter("recommendedAmount", "600.0");
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        List<CustomFieldDefinitionEntity> customFieldDefs = new SavingsBusinessService()
                .retrieveCustomFieldsDefinition();
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "12");
            i++;
        }
        actionPerform();
        verifyForward("update_success");

        verifyNoActionErrors();
        verifyNoActionMessages();
        StaticHibernateUtil.closeSession();
        savingsOffering = null;
        savings = new SavingsPersistence().findById(savings.getAccountId());
        Assert.assertNotNull(savings);
       Assert.assertEquals(TestUtils.createMoney(600.0), savings.getRecommendedAmount());
       Assert.assertEquals(1, savings.getAccountCustomFields().size());

    }

    public void testGetPrdOfferings() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering1 = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        savingsOffering2 = TestObjectFactory.createSavingsProduct("sav prd2", "prd2", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        addRequestParameter("customerId", group.getCustomerId().toString());
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "getPrdOfferings");
        actionPerform();
        verifyForward("getPrdOfferings_success");
        List<PrdOfferingView> savingPrds = (List<PrdOfferingView>) SessionUtils.getAttribute(
                SavingsConstants.SAVINGS_PRD_OFFERINGS, request);
       Assert.assertEquals(Integer.valueOf("2").intValue(), savingPrds.size());
        CustomerBO client = (CustomerBO) SessionUtils.getAttribute(SavingsConstants.CLIENT, request);
       Assert.assertEquals(group.getCustomerId(), client.getCustomerId());

    }

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
       Assert.assertEquals(SessionUtils.getAttribute(SavingsConstants.PRDOFFCERING, request), savingsOffering);

    }

    public void testScuccessfulReLoad() throws Exception {
        createAndAddObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering1 = TestObjectFactory.createSavingsProduct("sav prd_1", "pr_1", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "reLoad");
        addRequestParameter("selectedPrdOfferingId", savingsOffering1.getPrdOfferingId().toString());
        actionPerform();
        verifyForward("load_success");

       Assert.assertEquals(SessionUtils.getAttribute(SavingsConstants.PRDOFFCERING, request), savingsOffering1);

    }

    public void testSuccessfulPreview() throws Exception {
        createAndAddObjectsForCreate();
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "preview");
        actionPerform();
        verifyForward("preview_success");

    }

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
        if (isCustomFieldMandatory)
           Assert.assertEquals("CustomField", 1, getErrorSize(CustomerConstants.CUSTOM_FIELD));
        else
           Assert.assertEquals("CustomField", 0, getErrorSize(CustomerConstants.CUSTOM_FIELD));

    }

    public void testSuccessfulEditPreview() throws Exception {
        createAndAddObjectsForCreate();
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "editPreview");
        actionPerform();
        verifyForward("editPreview_success");

    }

    public void testSuccessfulPrevious() throws Exception {
        createAndAddObjects();
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "previous");
        actionPerform();
        verifyForward("previous_success");
    }

    public void testSuccessfulCreateWithCustomFields() throws Exception {
        createAndAddObjectsForCreate();
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("selectedPrdOfferingId", savingsOffering.getPrdOfferingId().toString());

        actionPerform();
        verifyForward("load_success");
        addRequestParameter("selectedPrdOfferingId", savingsOffering.getPrdOfferingId().toString());
        savingsOffering = null;
        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(SavingsConstants.CUSTOM_FIELDS, request);
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "preview");
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            i++;
        }
        actionPerform();
        verifyForward("preview_success");
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("stateSelected", Short.toString(AccountStates.SAVINGS_ACC_PARTIALAPPLICATION));
        addRequestParameter("method", "create");
        performNoErrors();
        verifyForward("create_success");
        String globalAccountNum = (String) request.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
        Assert.assertNotNull(globalAccountNum);
        savings = new SavingsPersistence().findBySystemId(globalAccountNum);
       Assert.assertEquals(customFieldDefs.size(), savings.getAccountCustomFields().size());
    }

    public void testSuccessfulSave() throws Exception {

        createAndAddObjectsForCreate();
        addRequestParameter("recommendedAmount", "1000.0");
        addRequestParameter("stateSelected", "13");
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "create");
        actionPerform();
        verifyForward("create_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        savingsOffering = null;
        String globalAccountNum = (String) request.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
        Assert.assertNotNull(globalAccountNum);
        savings = new SavingsPersistence().findBySystemId(globalAccountNum);
        Assert.assertNotNull(savings);
       Assert.assertEquals(TestUtils.createMoney(1000.0), savings.getRecommendedAmount());
       Assert.assertEquals(AccountState.SAVINGS_PARTIAL_APPLICATION.getValue(), savings.getAccountState().getId());

    }

    public void testSuccessfulSaveInActiveState() throws Exception {
        createAndAddObjectsForCreate();
        addRequestParameter("recommendedAmount", "1000.0");
        addRequestParameter("stateSelected", "16");
        savingsOffering = null;
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "create");
        actionPerform();
        verifyForward("create_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        String globalAccountNum = (String) request.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
        Assert.assertNotNull(globalAccountNum);
        savings = new SavingsPersistence().findBySystemId(globalAccountNum);
        Assert.assertNotNull(savings);
       Assert.assertEquals(TestUtils.createMoney(1000.0), savings.getRecommendedAmount());
       Assert.assertEquals(AccountState.SAVINGS_ACTIVE.getValue(), savings.getAccountState().getId());

    }

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

    public void testSuccessfulEditPrevious() throws Exception {
        createAndAddObjects(AccountState.SAVINGS_PARTIAL_APPLICATION);
        savingsOffering = null;
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "editPrevious");
        performNoErrors();
        verifyForward("editPrevious_success");
    }

    public void testSuccessfulUpdate() throws Exception {
        createAndAddObjects(AccountState.SAVINGS_PARTIAL_APPLICATION);
        savingsOffering = null;
        addRequestParameter("recommendedAmount", "600.0");
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "update");
        List<CustomFieldDefinitionEntity> customFieldDefs = new SavingsBusinessService()
                .retrieveCustomFieldsDefinition();
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "12");
            i++;
        }
        actionPerform();
        verifyForward("update_success");
        String globalAccountNum = (String) request.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
        Assert.assertNotNull(globalAccountNum);
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findBySystemId(globalAccountNum);
        Assert.assertNotNull(savings);
       Assert.assertEquals(TestUtils.createMoney(600.0), savings.getRecommendedAmount());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

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
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findBySystemId(globalAccountNum);
        Assert.assertNotNull(savings);
       Assert.assertEquals(TestUtils.createMoney(600.0), savings.getRecommendedAmount());
        verifyNoActionErrors();
        verifyNoActionMessages();

    }

    public void testSuccessfulGetRecentActivity() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
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
       Assert.assertEquals(0, ((List<SavingsRecentActivityView>) SessionUtils.getAttribute(
                SavingsConstants.RECENTY_ACTIVITY_LIST, request)).size());
    }

    /*
     * TODO: financial_calculation_fixme issues of significant digits for
     * savings account values
     * 
     * public void testSuccessfulGetTransactionHistory() throws Exception {
     * createInitialObjects(); Date currentDate = new
     * Date(System.currentTimeMillis()); savingsOffering =
     * TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate);
     * savings = createSavingsAccount("000X00000000019", savingsOffering,
     * AccountState.SAVINGS_ACTIVE); savingsOffering = null;
     * 
     * Money enteredAmount = new Money(TestObjectFactory.getMFICurrency(),
     * "100.0"); PaymentData paymentData =
     * PaymentData.createPaymentData(enteredAmount, savings .getPersonnel(),
     * Short.valueOf("1"), new Date(System .currentTimeMillis()));
     * paymentData.setCustomer(group); long curTime =
     * System.currentTimeMillis(); paymentData.setReceiptDate(new
     * Date(curTime)); paymentData.setReceiptNum("34244");
     * AccountActionDateEntity accountActionDate = savings
     * .getAccountActionDate(Short.valueOf("1"));
     * 
     * SavingsPaymentData savingsPaymentData = new
     * SavingsPaymentData(accountActionDate);
     * paymentData.addAccountPaymentData(savingsPaymentData);
     * 
     * savings.applyPaymentWithPersist(paymentData);
     * StaticHibernateUtil.commitTransaction();
     * StaticHibernateUtil.closeSession();
     * 
     * savings = new SavingsPersistence().findById(savings.getAccountId());
     * savings.setUserContext(userContext);
     * SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings,request);
     * 
     * setRequestPathInfo("/savingsAction.do"); addRequestParameter("method",
     * "getTransactionHistory"); addRequestParameter("globalAccountNum",
     * savings.getGlobalAccountNum()); actionPerform();
     * verifyForward("getTransactionHistory_success"); verifyNoActionErrors();
     * verifyNoActionMessages(); List<SavingsTransactionHistoryView>
     * trxnHistlist =
     * (List<SavingsTransactionHistoryView>)SessionUtils.getAttribute
     * (SavingsConstants.TRXN_HISTORY_LIST,request);
     * Collections.sort(trxnHistlist);Assert.assertEquals(2, trxnHistlist.size());
     * for(SavingsTransactionHistoryView view : trxnHistlist) {
     *Assert.assertEquals("100.0",view.getCredit());
     *Assert.assertEquals("-",view.getDebit());
     *Assert.assertEquals("100.0",view.getBalance());
     * Assert.assertNotNull(view.getClientName()); Assert.assertNotNull(view.getGlcode());
     *Assert.assertEquals("-",view.getNotes()); Assert.assertNotNull(view.getPostedBy());
     * Assert.assertNotNull(view.getType());
     * Assert.assertNotNull(view.getUserPrefferedPostedDate());
     * Assert.assertNotNull(view.getUserPrefferedTransactionDate());
     * Assert.assertNotNull(view.getAccountTrxnId()); Assert.assertNull(view.getLocale());
     * Assert.assertNotNull(view.getPaymentId());
     *Assert.assertEquals(DateUtils.getDateWithoutTimeStamp
     * (curTime),DateUtils.getDateWithoutTimeStamp
     * (view.getPostedDate().getTime()));
     *Assert.assertEquals(DateUtils.getDateWithoutTimeStamp
     * (curTime),DateUtils.getDateWithoutTimeStamp
     * (view.getTransactionDate().getTime())); break; }
     * StaticHibernateUtil.closeSession(); savings = new
     * SavingsPersistence().findById(savings.getAccountId()); group =
     * savings.getCustomer(); center = group.getParentCustomer(); }
     */
    public void testGetDepositDueDetails() throws Exception {

        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("000X00000000020", savingsOffering, AccountState.SAVINGS_PARTIAL_APPLICATION);
        savingsOffering = null;
        StaticHibernateUtil.closeSession();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY, TestUtils.makeUser(), request.getSession());
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "getDepositDueDetails");
        addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
        performNoErrors();
        verifyForward("depositduedetails_success");

        StaticHibernateUtil.closeSession();
        savings = new SavingsBusinessService().findBySystemId(savings.getGlobalAccountNum());

    }

    public void testWaiveAmountDue() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("000X00000000019", savingsOffering, AccountState.SAVINGS_ACTIVE);
        StaticHibernateUtil.closeSession();
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

        StaticHibernateUtil.closeSession();
        savings = new SavingsBusinessService().findBySystemId(savings.getGlobalAccountNum());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testWaiveAmountOverDue() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("sav prd1", "prd1", currentDate, RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("000X00000000019", savingsOffering, AccountState.SAVINGS_PARTIAL_APPLICATION);
        savingsOffering = null;
        StaticHibernateUtil.closeSession();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        SessionUtils.setAttribute(Constants.USER_CONTEXT_KEY, TestUtils.makeUser(), request.getSession());
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "waiveAmountOverDue");
        addRequestParameter("globalAccountNum", savings.getGlobalAccountNum());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward("waiveAmount_success");

        StaticHibernateUtil.closeSession();
        savings = new SavingsBusinessService().findBySystemId(savings.getGlobalAccountNum());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testSuccessfulGetStatusHistory() throws Exception {
        SavingsTestHelper helper = new SavingsTestHelper();
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
        savingsOffering = null;
        AccountStateMachines.getInstance().initialize((short) 1, (short) 1, AccountTypes.SAVINGS_ACCOUNT, null);
        savings.changeStatus(AccountState.SAVINGS_PENDING_APPROVAL.getValue(), null, "notes");
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
       Assert.assertEquals(2, ((List<SavingsTransactionHistoryView>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_CHANGE_HISTORY_LIST, request)).size());
    }

    public void testSuccessful_Update_AuditLog() throws Exception {
        createAndAddObjects(AccountState.SAVINGS_PARTIAL_APPLICATION);
        savingsOffering = null;
        addRequestParameter("recommendedAmount", "600.0");
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "update");
        List<CustomFieldDefinitionEntity> customFieldDefs = new SavingsBusinessService()
                .retrieveCustomFieldsDefinition();
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "12");
            i++;
        }
        actionPerform();
        verifyForward("update_success");
        String globalAccountNum = (String) request.getAttribute(SavingsConstants.GLOBALACCOUNTNUM);
        Assert.assertNotNull(globalAccountNum);
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findBySystemId(globalAccountNum);
        Assert.assertNotNull(savings);
       Assert.assertEquals(TestUtils.createMoney(600.0), savings.getRecommendedAmount());

        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.SAVINGS, savings.getAccountId());
       Assert.assertEquals(1, auditLogList.size());
       Assert.assertEquals(EntityType.SAVINGS.getValue(), auditLogList.get(0).getEntityType());
       Assert.assertEquals(savings.getAccountId(), auditLogList.get(0).getEntityId());

       Assert.assertEquals(2, auditLogList.get(0).getAuditLogRecords().size());

        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Recommended Amount"))
                matchValues(auditLogRecord, "300.0", "600.0");
            else if (auditLogRecord.getFieldName().equalsIgnoreCase("Additional Information")) {
                matchValues(auditLogRecord, "External Savings Id-custom field value", "External Savings Id-12");
            }
        }
    }

}
