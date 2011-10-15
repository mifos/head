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

package org.mifos.accounts.struts.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 * This class tests methods of ApplyAdjustment action class.
 */
public class ApplyAdjustmentActionStrutsTest extends MifosMockStrutsTestCase {


    private CenterBO center;
    private GroupBO group;
    private LoanBO loan;
    private UserContext userContext;
    private String flowKey;

    @Override
    protected void setStrutsConfig() throws IOException {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/accounts-struts-config.xml");
    }

    /**
     * This sets the web.xml,struts-config.xml and prepares the userContext and
     * activityContext and sets them in the session.
     */
    @Before
    public void setUp() throws Exception {
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, ApplyAdjustment.class);
    }

    private AccountBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        LoanBO loan = TestObjectFactory.createLoanAccount("42423142341", group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);
        StaticHibernateUtil.flushSession();
        return TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
    }

    private void applyPayment(LoanBO loan, int amount) throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());
        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.add(loan.getAccountActionDate(Short.valueOf("1")));
        PaymentData accountPaymentDataView = TestObjectFactory.getLoanAccountPaymentData(accntActionDates,
                TestUtils.createMoney(amount), null, loan.getPersonnel(), "receiptNum", Short
                        .valueOf("1"), currentDate, currentDate);

        IntegrationTestObjectMother.applyAccountPayment(loan, accountPaymentDataView);

        TestObjectFactory.updateObject(loan);
    }

    @Test
    public void testLoadAdjustmentWhenObligationMet() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        applyPayment(loan, 700);

        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "loadAdjustmentWhenObligationMet");

        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
        verifyForward("loadadjustment_success");
    }

    @Test
    public void testLoadAdjustment() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        applyPayment(loan, 700);

        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());

        addRequestParameter("method", "loadAdjustment");
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
        verifyForward("loadadjustment_success");
    }

    @Test
    public void testPreviewAdjustment() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        applyPayment(loan, 700);
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "previewAdjustment");
        addRequestParameter("adjustmentNote", "adjusting the last payment");
        addRequestParameter("adjustcheckbox", "true");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
        verifyForward("previewadj_success");
    }

    @Test
    public void testPreviewAdjustment_failure() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        applyPayment(loan, 700);
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "previewAdjustment");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
        verifyForward("previewAdjustment_failure");
    }

    @Ignore
    @Test
    public void testApplyAdjustment() throws Exception {
        PersonnelBO personnel = legacyPersonnelDao.getPersonnel(PersonnelConstants.SYSTEM_USER);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        applyPayment(loan, 212);
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        applyPayment(loan, 700);
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING), new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING), personnel, loan);
        AccountTestUtils.addToAccountStatusChangeHistory(loan, historyEntity);
        TestObjectFactory.updateObject(loan);
        StaticHibernateUtil.flushAndClearSession();
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        loan.setUserContext(userContext);
        for (AccountStatusChangeHistoryEntity accountStatus : loan.getAccountStatusChangeHistory()) {
           Assert.assertEquals(loan.getAccountId(), accountStatus.getAccount().getAccountId());
            Assert.assertNotNull(accountStatus.getAccountStatusChangeId());
           Assert.assertEquals(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING.getValue(), accountStatus.getNewStatus().getId());
           Assert.assertEquals(personnel.getPersonnelId(), accountStatus.getPersonnel().getPersonnelId());
           Assert.assertEquals(personnel.getDisplayName(), accountStatus.getPersonnel().getDisplayName());
           Assert.assertEquals("-", accountStatus.getOldStatusName());
            Assert.assertNotNull(accountStatus.getNewStatusName());
            Assert.assertNull(accountStatus.getLocale());
            Assert.assertNotNull(accountStatus.getUserPrefferedTransactionDate());
        }

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "applyAdjustment");
        addRequestParameter("adjustmentNote", "Loan adjustment testing");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        setUpSecurityContext();
        getRequest().getSession().setAttribute(Constants.USERCONTEXT, TestUtils.makeUser());
        actionPerform();
        loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
        verifyForward("applyadj_success");
    }

    private void setUpSecurityContext() {
        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser mifosUser = new MifosUserBuilder().nonLoanOfficer().withAdminRole().build();
        Authentication authentication = new TestingAuthenticationToken(mifosUser, mifosUser);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testApplyAdjustmentWhenAccountsSecondLastStateWasBadStanding() throws Exception {
        PersonnelBO personnel = legacyPersonnelDao.getPersonnel(PersonnelConstants.SYSTEM_USER);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        applyPayment(loan, 212);
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        applyPayment(loan, 700);
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING), new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING), personnel, loan);
        AccountTestUtils.addToAccountStatusChangeHistory(loan, historyEntity);
        TestObjectFactory.updateObject(loan);
        StaticHibernateUtil.flushSession();
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        historyEntity = new AccountStatusChangeHistoryEntity(new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_BAD_STANDING), new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING), personnel, loan);
        AccountTestUtils.addToAccountStatusChangeHistory(loan, historyEntity);
        TestObjectFactory.updateObject(loan);
        StaticHibernateUtil.flushSession();
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "applyAdjustment");
        addRequestParameter("adjustmentNote", "Loan adjustment testing");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        setUpSecurityContext();
        getRequest().getSession().setAttribute(Constants.USERCONTEXT, TestUtils.makeUser());

        actionPerform();
        loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
        Assert.assertEquals(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING.getValue(), loan.getAccountState().getId());
        verifyForward("applyadj_success");
    }

    @Test
    public void testCancelAdjustment() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "cancelAdjustment");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("canceladj_success");
    }

    @Test
    public void testLoadAdjustmentWithNoPmnts() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        addRequestParameter("method", "loadAdjustment");
        setRequestPathInfo("/applyAdjustment");
        TestObjectFactory.updateObject(loan);
        StaticHibernateUtil.flushSession();
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
        verifyForward("loadAdjustment_failure");

    }

    @Test
    public void testAdjustmentForZeroPmnt() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        applyPayment(loan, 0);
        StaticHibernateUtil.flushSession();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "applyAdjustment");
        addRequestParameter("adjustmentNote", "Loan adjustment testing");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
        verifyForward("applyAdjustment_failure");

    }

    @Test
    public void testValidation() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) createLoanAccount();
        applyPayment(loan, 700);
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "previewAdjustment");
        addRequestParameter("adjustcheckbox", "true");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyActionErrors(new String[] { "errors.mandatorytextarea" });
    }

    @Test
    public void testValidationAdjustmentNoteSize() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) createLoanAccount();
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "previewAdjustment");
        addRequestParameter(
                "adjustmentNote",
                "This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.");
        addRequestParameter("adjustcheckbox", "true");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyActionErrors(new String[] { "errors.adjustmentNoteTooBig" });
    }

    private AccountBO createLoanAccount() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    @After
    public void tearDown() throws Exception {
        loan = null;
        group = null;
        center = null;
    }

}
