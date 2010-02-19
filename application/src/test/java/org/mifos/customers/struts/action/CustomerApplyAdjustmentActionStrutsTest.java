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

package org.mifos.customers.struts.action;

import java.sql.Date;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerAccountBOTestUtils;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerBOTestUtils;
import org.mifos.customers.business.CustomerFeeScheduleEntity;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.customers.business.CustomerTrxnDetailEntity;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerApplyAdjustmentActionStrutsTest extends MifosMockStrutsTestCase {
    public CustomerApplyAdjustmentActionStrutsTest() throws Exception {
        super();
    }

    private AccountBO accountBO = null;

    private UserContext userContext;

    private CustomerBO client;

    private CustomerBO group;

    private CustomerBO center;

    private String flowKey;

    @Override 
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }
        
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, CustomerApplyAdjustmentAction.class);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
    }

    @Override
    public void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoadAdjustment() throws Exception {
        applyPayment();
        setRequestPathInfo("/custApplyAdjustment.do");
        addRequestParameter("method", "loadAdjustment");
        addRequestParameter("input", "ViewClientCharges");
        addRequestParameter("globalCustNum", client.getGlobalCustNum());
        addRequestParameter("prdOfferingName", "Client_Active_test_3");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        getRequest().getSession().setAttribute("security_param", "Client");
        actionPerform();
        verifyForward("loadAdjustment_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testPreviewAdjustment() throws Exception {
        applyPayment();
        setRequestPathInfo("/custApplyAdjustment.do");
        addRequestParameter("method", "previewAdjustment");
        addRequestParameter("input", "ViewClientCharges");
        addRequestParameter("globalCustNum", client.getGlobalCustNum());
        addRequestParameter("prdOfferingName", "Client_Active_test_3");
        addRequestParameter("adjustcheckbox", "true");
        addRequestParameter("adjustmentNote", "abc");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        getRequest().getSession().setAttribute("security_param", "Client");
        actionPerform();
        verifyForward("previewAdjustment_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testApplyAdjustment() throws Exception {
        applyPayment();
        setRequestPathInfo("/custApplyAdjustment.do");
        addRequestParameter("method", "applyAdjustment");
        addRequestParameter("globalCustNum", client.getGlobalCustNum());
        addRequestParameter("prdOfferingName", "Client_Active_test_3");
        addRequestParameter("input", "ViewClientCharges");
        addRequestParameter("adjustmentNote", "abcef");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        getRequest().getSession().setAttribute("security_param", "Client");
        actionPerform();
        verifyForward("applyAdjustment_client_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testCancelAdjustment() throws Exception {
        setRequestPathInfo("/custApplyAdjustment.do");
        addRequestParameter("method", "cancelAdjustment");
        addRequestParameter("input", "ViewClientCharges");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        getRequest().getSession().setAttribute("security_param", "Client");
        actionPerform();
        verifyForward("canceladj_client_success");
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testValidation_AdjustmentCheckbox() throws Exception {
        applyPayment();
        addRequestParameter("globalCustNum", "Client_Active_test_3");
        setRequestPathInfo("/custApplyAdjustment");
        addRequestParameter("method", "previewAdjustment");
        addRequestParameter("prdOfferingName", "Client_Active_test_3");
        addRequestParameter("input", "ViewClientCharges");
        addRequestParameter("adjustmentNote", "This is to test errors.");
        addRequestParameter("adjustcheckbox", "false");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { "errors.mandatorycheckbox" });
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testValidation_AdjustmentNoteSize() throws Exception {
        applyPayment();
        addRequestParameter("globalCustNum", "Client_Active_test_3");
        setRequestPathInfo("/custApplyAdjustment");
        addRequestParameter("method", "previewAdjustment");
        addRequestParameter("prdOfferingName", "Client_Active_test_3");
        addRequestParameter("input", "ViewClientCharges");
        addRequestParameter(
                "adjustmentNote",
                "This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.");
        addRequestParameter("adjustcheckbox", "true");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { "errors.adjustmentNoteTooBig" });
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testValidation_AdjustmentNoteMandatory() throws Exception {
        applyPayment();
        addRequestParameter("globalCustNum", "Client_Active_test_3");
        setRequestPathInfo("/custApplyAdjustment");
        addRequestParameter("method", "previewAdjustment");
        addRequestParameter("prdOfferingName", "Client_Active_test_3");
        addRequestParameter("input", "ViewClientCharges");
        addRequestParameter("adjustcheckbox", "true");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { "errors.mandatorytextarea" });
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client_Active_test_3", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private void applyPayment() throws Exception {
        createInitialObjects();
        accountBO = client.getCustomerAccount();
        Date currentDate = new Date(System.currentTimeMillis());
        CustomerAccountBO customerAccountBO = (CustomerAccountBO) accountBO;
        customerAccountBO.setUserContext(userContext);

        CustomerScheduleEntity accountAction = (CustomerScheduleEntity) customerAccountBO.getAccountActionDate(Short
                .valueOf("1"));
        CustomerAccountBOTestUtils.setMiscFeePaid(accountAction, TestUtils.createMoney(100));
        CustomerAccountBOTestUtils.setMiscPenaltyPaid(accountAction, TestUtils.createMoney(100));
        CustomerAccountBOTestUtils.setPaymentDate(accountAction, currentDate);
        accountAction.setPaymentStatus(PaymentStatus.PAID);

        MasterPersistence masterPersistenceService = new MasterPersistence();

        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(accountBO, TestUtils.createMoney(100), 
                "1111", currentDate, new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));

        CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity(accountPaymentEntity,
                AccountActionTypes.PAYMENT, Short.valueOf("1"), accountAction.getActionDate(),
                TestObjectFactory.getPersonnel(userContext.getId()), currentDate, TestUtils.createMoney(200), 
                "payment done", null, TestUtils.createMoney(100), TestUtils.createMoney(100),
                        masterPersistenceService);

        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountAction.getAccountFeesActionDetails()) {
            CustomerAccountBOTestUtils.setFeeAmountPaid(
                    (CustomerFeeScheduleEntity) accountFeesActionDetailEntity, TestUtils.createMoney(100));
            FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity(accountTrxnEntity, accountFeesActionDetailEntity
                    .getAccountFee(), accountFeesActionDetailEntity.getFeeAmount());
            CustomerBOTestUtils.addFeesTrxnDetail(accountTrxnEntity, feeTrxn);
            // totalFees = accountFeesActionDetailEntity.getFeeAmountPaid();
        }
        accountPaymentEntity.addAccountTrxn(accountTrxnEntity);
        AccountTestUtils.addAccountPayment(accountPaymentEntity, customerAccountBO);

        TestObjectFactory.updateObject(customerAccountBO);
        TestObjectFactory.flushandCloseSession();
        customerAccountBO = TestObjectFactory.getObject(CustomerAccountBO.class, customerAccountBO.getAccountId());
        client = customerAccountBO.getCustomer();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
    }

}
