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

package org.mifos.application.accounts.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.AccountBOIntegrationTest;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * This class tests methods of ApplyAdjustment action class.
 */
public class ApplyAdjustmentActionTest extends MifosMockStrutsTestCase {
    public ApplyAdjustmentActionTest() throws SystemException, ApplicationException {
        super();
    }

    private CenterBO center;
    private GroupBO group;
    private LoanBO loan;
    private UserContext userContext;
    private String flowKey;

    /**
     * This sets the web.xml,struts-config.xml and prepares the userContext and
     * activityContext and sets them in the session.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
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
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        LoanBO loan = TestObjectFactory.createLoanAccount("42423142341", group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);
        StaticHibernateUtil.closeSession();
        return TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
    }

    private void applyPayment(LoanBO loan, int amount) throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());
        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.add(loan.getAccountActionDate(Short.valueOf("1")));
        PaymentData accountPaymentDataView = TestObjectFactory.getLoanAccountPaymentData(accntActionDates,
                TestObjectFactory.getMoneyForMFICurrency(amount), null, loan.getPersonnel(), "receiptNum", Short
                        .valueOf("1"), currentDate, currentDate);

        loan.applyPaymentWithPersist(accountPaymentDataView);
        TestObjectFactory.updateObject(loan);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
    }

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

    public void testApplyAdjustment() throws Exception {
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(PersonnelConstants.SYSTEM_USER);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        applyPayment(loan, 212);
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        applyPayment(loan, 700);
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING), new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING), personnel, loan);
        AccountBOIntegrationTest.addToAccountStatusChangeHistory(loan, historyEntity);
        TestObjectFactory.updateObject(loan);
        TestObjectFactory.flushandCloseSession();
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        loan.setUserContext(userContext);
        for (AccountStatusChangeHistoryEntity accountStatus : loan.getAccountStatusChangeHistory()) {
            assertEquals(loan.getAccountId(), accountStatus.getAccount().getAccountId());
            assertNotNull(accountStatus.getAccountStatusChangeId());
            assertEquals(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING.getValue(), accountStatus.getNewStatus().getId());
            assertEquals(personnel.getPersonnelId(), accountStatus.getPersonnel().getPersonnelId());
            assertEquals(personnel.getDisplayName(), accountStatus.getPersonnel().getDisplayName());
            assertEquals("-", accountStatus.getOldStatusName());
            assertNotNull(accountStatus.getNewStatusName());
            assertNull(accountStatus.getLocale());
            assertNotNull(accountStatus.getUserPrefferedTransactionDate());
        }

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "applyAdjustment");
        addRequestParameter("adjustmentNote", "Loan adjustment testing");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        getRequest().getSession().setAttribute(Constants.USERCONTEXT, TestUtils.makeUser());

        actionPerform();
        loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
        verifyForward("applyadj_success");
    }

    public void testApplyAdjustmentWhenAccountsSecondLastStateWasBadStanding() throws Exception {
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(PersonnelConstants.SYSTEM_USER);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        applyPayment(loan, 212);
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        applyPayment(loan, 700);
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING), new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING), personnel, loan);
        AccountBOIntegrationTest.addToAccountStatusChangeHistory(loan, historyEntity);
        TestObjectFactory.updateObject(loan);
        TestObjectFactory.flushandCloseSession();
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        historyEntity = new AccountStatusChangeHistoryEntity(new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_BAD_STANDING), new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING), personnel, loan);
        AccountBOIntegrationTest.addToAccountStatusChangeHistory(loan, historyEntity);
        TestObjectFactory.updateObject(loan);
        StaticHibernateUtil.closeSession();
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "applyAdjustment");
        addRequestParameter("adjustmentNote", "Loan adjustment testing");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        getRequest().getSession().setAttribute(Constants.USERCONTEXT, TestUtils.makeUser());

        actionPerform();
        loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
        assertEquals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue(), loan.getAccountState().getId());
        verifyForward("applyadj_success");
    }

    public void testCancelAdjustment() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/applyAdjustment");
        addRequestParameter("method", "cancelAdjustment");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("canceladj_success");
    }

    public void testLoadAdjustmentWithNoPmnts() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        addRequestParameter("method", "loadAdjustment");
        setRequestPathInfo("/applyAdjustment");
        TestObjectFactory.updateObject(loan);
        TestObjectFactory.flushandCloseSession();
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
        verifyForward("loadAdjustment_failure");

    }

    public void testAdjustmentForZeroPmnt() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        loan = (LoanBO) getLoanAccount();
        applyPayment(loan, 0);
        TestObjectFactory.flushandCloseSession();
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
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(loan);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

}
