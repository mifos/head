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

package org.mifos.application.accounts.savings.struts.action;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsBOIntegrationTest;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsDepositWithdrawalActionTest extends MifosMockStrutsTestCase {
    public SavingsDepositWithdrawalActionTest() throws SystemException, ApplicationException {
        super();
    }

    private UserContext userContext;
    private CustomerBO group;
    private CustomerBO center;
    private SavingsBO savings;
    private SavingsOfferingBO savingsOffering;
    private CustomerBO client1;
    private CustomerBO client2;
    private CustomerBO client3;
    private CustomerBO client4;
    private SavingsTestHelper helper = new SavingsTestHelper();
    private String flowKey;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        userContext.setPreferredLocale(new Locale("en", "GB"));
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession().setAttribute(Constants.USER_CONTEXT_KEY, userContext);
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, SavingsDepositWithdrawalAction.class);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
    }

    private void reloadMembers() {
        if (savings != null) {
            savings = (SavingsBO) StaticHibernateUtil.getSessionTL().get(SavingsBO.class, savings.getAccountId());
        }
        if (group != null) {
            group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        }
        if (center != null) {
            center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        }
        if (client1 != null) {
            client1 = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, client1.getCustomerId());
        }
        if (client2 != null) {
            client2 = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, client2.getCustomerId());
        }
        if (client3 != null) {
            client3 = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, client3.getCustomerId());
        }
        if (client4 != null) {
            client4 = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, client4.getCustomerId());
        }

    }

    @Override
    public void tearDown() throws Exception {
        try {
            reloadMembers();
            TestObjectFactory.cleanUp(savings);
            TestObjectFactory.cleanUp(client1);
            TestObjectFactory.cleanUp(client2);
            TestObjectFactory.cleanUp(client3);
            TestObjectFactory.cleanUp(client4);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testSuccessfullLoad_ForClientAccount() throws Exception {
        createCenterAndGroup();
        createClients();
        savingsOffering = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, client1,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.closeSession();

        savings = new SavingsPersistence().findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "load");
        actionPerform();
        verifyForward("load_success");

        List<AccountActionEntity> trxnTypes = (List<AccountActionEntity>) SessionUtils.getAttribute(
                AccountConstants.TRXN_TYPES, request);
        assertNotNull(trxnTypes);
        assertEquals(2, trxnTypes.size());

        List<CustomerBO> clientList = (List<CustomerBO>) SessionUtils.getAttribute(SavingsConstants.CLIENT_LIST,
                request);
        assertNull(clientList);

        Boolean isBackDatedAllowed = (Boolean) SessionUtils.getAttribute(SavingsConstants.IS_BACKDATED_TRXN_ALLOWED,
                request);
        assertNotNull(isBackDatedAllowed);
        assertNotNull(SessionUtils.getAttribute(MasterConstants.PAYMENT_TYPE, request));
        group = new CustomerPersistence().getCustomer(group.getCustomerId());
        center = new CustomerPersistence().getCustomer(center.getCustomerId());
        client1 = new CustomerPersistence().getCustomer(client1.getCustomerId());
        client2 = new CustomerPersistence().getCustomer(client2.getCustomerId());
        client3 = new CustomerPersistence().getCustomer(client3.getCustomerId());
        client4 = new CustomerPersistence().getCustomer(client4.getCustomerId());
    }

    public void testSuccessfullLoad() throws Exception {
        createCenterAndGroup();
        createClients();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.closeSession();

        savings = new SavingsPersistence().findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "load");
        actionPerform();
        verifyForward("load_success");

        List<AccountActionEntity> trxnTypes = (List<AccountActionEntity>) SessionUtils.getAttribute(
                AccountConstants.TRXN_TYPES, request);
        assertNotNull(trxnTypes);
        assertEquals(2, trxnTypes.size());

        List<CustomerBO> clientList = (List<CustomerBO>) SessionUtils.getAttribute(SavingsConstants.CLIENT_LIST,
                request);
        assertNotNull(clientList);
        assertEquals(2, clientList.size());
        Boolean isBackDatedAllowed = (Boolean) SessionUtils.getAttribute(SavingsConstants.IS_BACKDATED_TRXN_ALLOWED,
                request);
        assertNotNull(isBackDatedAllowed);
        assertNotNull(SessionUtils.getAttribute(MasterConstants.PAYMENT_TYPE, request));
    }

    public void testSuccessfullReLoad() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.closeSession();

        savings = new SavingsPersistence().findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "reLoad");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()));
        actionPerform();
        verifyForward("load_success");
    }

    public void testSuccessfullReLoad_Deposit() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.closeSession();

        savings = new SavingsPersistence().findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "reLoad");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_DEPOSIT.getValue()));
        actionPerform();
        verifyForward("load_success");
    }

    public void testSuccessfullReLoad_Withdrawal() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.closeSession();

        savings = new SavingsPersistence().findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "reLoad");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()));
        actionPerform();
        verifyForward("load_success");
    }

    public void testFailurePreview() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("amount", "");
        addRequestParameter("customerId", "");
        addRequestParameter("trxnDate", "");
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_DEPOSIT.getValue()));
        actionPerform();
        assertEquals(3, getErrorSize());
        assertEquals(3, getErrorSize(AccountConstants.ERROR_MANDATORY));
    }

    public void testPreviewDateValidation() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("amount", "200");
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("trxnDate", "3/20/2005");
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_DEPOSIT.getValue()));
        actionPerform();
        verifyActionErrors(new String[] { AccountConstants.ERROR_INVALIDDATE });
    }

    public void testSuccessfulPreview() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("amount", "200");
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("trxnDate", DateUtils.makeDateAsSentFromBrowser());
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_DEPOSIT.getValue()));
        actionPerform();
        verifyForward(ActionForwards.preview_success.toString());
    }

    public void testSuccessfulMakePayment_Deposit() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findById(savings.getAccountId());

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "load");
        actionPerform();

        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("amount", "200");
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("trxnDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_DEPOSIT.getValue()));
        actionPerform();

        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "makePayment");
        actionPerform();
        verifyForward(ActionForwards.account_details_page.toString());
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findById(savings.getAccountId());
        assertEquals(new Money("200"), savings.getSavingsBalance());
    }

    public void testSuccessfulMakePayment_Withdrawal() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findById(savings.getAccountId());
        SavingsBOIntegrationTest.setBalance(savings, new Money("500"));

        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findById(savings.getAccountId());

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "load");
        actionPerform();

        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("amount", "30");
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("trxnDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()));
        actionPerform();

        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "makePayment");
        actionPerform();
        verifyForward(ActionForwards.account_details_page.toString());
        StaticHibernateUtil.closeSession();
        savings = new SavingsPersistence().findById(savings.getAccountId());
        assertEquals(new Money("470"), savings.getSavingsBalance());
    }

    public void testSuccessfullPrevious() throws Exception {
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "previous");
        actionPerform();
        verifyForward("previous_success");
    }

    public void testSuccessfullCancel() throws Exception {
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "cancel");
        actionPerform();
        verifyForward(ActionForwards.account_details_page.toString());
    }

    private void createCenterAndGroup() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
    }

    private void createClients() {
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_CLOSED, group);
        client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
        client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_PARTIAL, group);
        client4 = TestObjectFactory.createClient("client4", CustomerStatus.CLIENT_HOLD, group);
    }
}
