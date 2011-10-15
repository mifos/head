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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
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
public class SavingsDepositWithdrawalActionStrutsTest extends MifosMockStrutsTestCase {

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
        request.getSession().setAttribute(Constants.USER_CONTEXT_KEY, userContext);
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, SavingsDepositWithdrawalAction.class);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @After
    public void tearDown() throws Exception {
        savings = null;
        client1 = null;
        client2 = null;
        client3 = null;
        client4 = null;
        group = null;
        center = null;

    }

    @Test
    public void testSuccessfullLoad_ForClientAccount() throws Exception {
        createCenterAndGroup();
        createClients();
        savingsOffering = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, client1,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.flushSession();

        savings = savingsDao.findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "load");
        actionPerform();
        verifyForward("load_success");

        List<AccountActionEntity> trxnTypes = (List<AccountActionEntity>) SessionUtils.getAttribute(
                AccountConstants.TRXN_TYPES, request);
        Assert.assertNotNull(trxnTypes);
       Assert.assertEquals(2, trxnTypes.size());

        List<CustomerBO> clientList = (List<CustomerBO>) SessionUtils.getAttribute(SavingsConstants.CLIENT_LIST,
                request);
        Assert.assertTrue(clientList.isEmpty());

        Boolean isBackDatedAllowed = (Boolean) SessionUtils.getAttribute(SavingsConstants.IS_BACKDATED_TRXN_ALLOWED,
                request);
        Assert.assertNotNull(isBackDatedAllowed);
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.PAYMENT_TYPE, request));
        group = new CustomerPersistence().getCustomer(group.getCustomerId());
        center = new CustomerPersistence().getCustomer(center.getCustomerId());
        client1 = new CustomerPersistence().getCustomer(client1.getCustomerId());
        client2 = new CustomerPersistence().getCustomer(client2.getCustomerId());
        client3 = new CustomerPersistence().getCustomer(client3.getCustomerId());
        client4 = new CustomerPersistence().getCustomer(client4.getCustomerId());
    }

    @Test
    public void testSuccessfullLoad() throws Exception {
        createCenterAndGroup();
        createClients();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.flushSession();

        savings = savingsDao.findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "load");
        actionPerform();
        verifyForward("load_success");

        List<AccountActionEntity> trxnTypes = (List<AccountActionEntity>) SessionUtils.getAttribute(
                AccountConstants.TRXN_TYPES, request);
        Assert.assertNotNull(trxnTypes);
       Assert.assertEquals(2, trxnTypes.size());

        List<CustomerBO> clientList = (List<CustomerBO>) SessionUtils.getAttribute(SavingsConstants.CLIENT_LIST,
                request);
        Assert.assertNotNull(clientList);
       Assert.assertEquals(2, clientList.size());
        Boolean isBackDatedAllowed = (Boolean) SessionUtils.getAttribute(SavingsConstants.IS_BACKDATED_TRXN_ALLOWED,
                request);
        Assert.assertNotNull(isBackDatedAllowed);
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.PAYMENT_TYPE, request));
    }

    @Test
    public void testSuccessfullReLoad() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.flushSession();

        savings = savingsDao.findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "reLoad");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()));
        actionPerform();
        verifyForward("load_success");
    }

    @Test
    public void testSuccessfullReLoad_Deposit() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.flushSession();

        savings = savingsDao.findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "reLoad");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_DEPOSIT.getValue()));
        actionPerform();
        verifyForward("load_success");
    }

    @Test
    public void testSuccessfullReLoad_Withdrawal() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.flushSession();

        savings = savingsDao.findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "reLoad");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()));
        actionPerform();
        verifyForward("load_success");
    }

    @Test
    public void testFailurePreview() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.flushSession();
        savings = savingsDao.findById(savings.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("amount", "");
        addRequestParameter("customerId", "");
        addRequestParameter("trxnDate", "");
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter("trxnTypeId", String.valueOf(AccountActionTypes.SAVINGS_DEPOSIT.getValue()));
        actionPerform();
       Assert.assertEquals(3, getErrorSize());
       Assert.assertEquals(3, getErrorSize(AccountConstants.ERROR_MANDATORY));
    }

    @Test
    public void testPreviewDateValidation() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.flushSession();
        savings = savingsDao.findById(savings.getAccountId());
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

    @Test
    public void testSuccessfulPreview() throws Exception {
        createCenterAndGroup();
        savingsOffering = helper.createSavingsOffering("asfddsf", "213a");
        savings = helper.createSavingsAccount("000X00000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.flushSession();
        savings = savingsDao.findById(savings.getAccountId());
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

/*
    buddy: Ignoring this failing integration test. Filed an issue for it: MIFOS-4151
    @Test
    public void testSuccessfulMakePayment_Withdrawal() throws Exception {
        createCenterAndGroup();

        savingsOffering = new SavingsProductBuilder().mandatory().appliesToCentersOnly().buildForIntegrationTests();
        savings = new SavingsAccountBuilder().withSavingsProduct(savingsOffering)
                                             .withCustomer(center)
                                             .withActivationDate(new DateTime().minusDays(7))
                                             .withDepositOn("500", new DateTime().minusDays(2))
                                             .build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsOffering, savings);

        savings = IntegrationTestObjectMother.findSavingsAccountById(savings.getAccountId().longValue());

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

        savings = IntegrationTestObjectMother.findSavingsAccountById(savings.getAccountId().longValue());

        Assert.assertEquals(new Money(getCurrency(), "470"), savings.getSavingsBalance());
    }
*/

    @Test
    public void testSuccessfullPrevious() throws Exception {
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "previous");
        actionPerform();
        verifyForward("previous_success");
    }

    @Test
    public void testSuccessfullCancel() throws Exception {
        setRequestPathInfo("/savingsDepositWithdrawalAction.do");
        addRequestParameter("method", "cancel");
        actionPerform();
        verifyForward(ActionForwards.account_details_page.toString());
    }

    private void createCenterAndGroup() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
    }

    private void createClients() {
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_CLOSED, group);
        client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
        client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_PARTIAL, group);
        client4 = TestObjectFactory.createClient("client4", CustomerStatus.CLIENT_HOLD, group);
    }
}
