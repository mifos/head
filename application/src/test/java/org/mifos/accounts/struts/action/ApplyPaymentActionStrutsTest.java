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

package org.mifos.accounts.struts.action;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Date;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class ApplyPaymentActionStrutsTest extends MifosMockStrutsTestCase {
    public ApplyPaymentActionStrutsTest() throws Exception {
        super();
    }

    protected AccountBO accountBO;
    private CustomerBO center;
    private CustomerBO group;
    private UserContext userContext;
    private String flowKey;

    @Override
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/accounts-struts-config.xml");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, AccountApplyPaymentAction.class);
    }

    @Override
    public void tearDown() throws Exception {
        TestObjectFactory.cleanUp(accountBO);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testApplyPaymentLoad_Loan() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        accountBO = createLoanAccount();
        setRequestPathInfo("/applyPaymentAction");
        addRequestParameter("method", "load");
        addRequestParameter("input", "loan");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(Constants.LOAD_SUCCESS);
        verifyNoActionErrors();
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.PAYMENT_TYPE, request));
        AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm) request.getSession().getAttribute(
                "applyPaymentActionForm");
       Assert.assertEquals(actionForm.getAmount(), accountBO.getTotalPaymentDue().toString());
    }

    // added for defect 1590 [start]
    public void testApplyPaymentLoad_Fees() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        accountBO = createLoanAccount();
        setRequestPathInfo("/applyPaymentAction");
        addRequestParameter("method", "load");
        addRequestParameter("input", "fee");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(Constants.LOAD_SUCCESS);
        verifyNoActionErrors();
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.PAYMENT_TYPE, request));
        AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm) request.getSession().getAttribute(
                "applyPaymentActionForm");
       Assert.assertEquals(actionForm.getAmount(), accountBO.getTotalPaymentDue().toString());
    }

    // added for defect 1590 [end]

    public void testApplyPaymentPreview() throws InvalidDateException {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/applyPaymentAction");
        String currentDate = DateUtils.makeDateAsSentFromBrowser();
        addRequestDateParameter("receiptDate", currentDate);
        addRequestDateParameter("transactionDate", currentDate);
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter("method", "preview");
        addRequestParameter("amount", "10");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(Constants.PREVIEW_SUCCESS);
    }

    public void testNewStyleApplyPaymentPreview() throws InvalidDateException {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/applyPaymentAction");
        String currentDate = DateUtils.makeDateAsSentFromBrowser();
        addRequestDateParameter("receiptDate", currentDate);
        addRequestDateParameter("transactionDate", currentDate);
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter("method", "preview");
        addRequestParameter("amount", "10");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(Constants.PREVIEW_SUCCESS);
    }

    public void testApplyPaymentForLoan() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        accountBO = createLoanAccount();
        accountBO.setUserContext(TestObjectFactory.getContext());
        accountBO.changeStatus(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue(), null, "");

        AccountApplyPaymentActionForm accountApplyPaymentActionForm = new AccountApplyPaymentActionForm();
        accountApplyPaymentActionForm.setAmount("212");
        request.getSession().setAttribute("applyPaymentActionForm", accountApplyPaymentActionForm);

        SessionUtils.setAttribute(Constants.ACCOUNT_VERSION, accountBO.getVersionNo(),request);
        SessionUtils.setAttribute(Constants.ACCOUNT_TYPE,
                AccountTypes.getAccountType(accountBO.getAccountType().getAccountTypeId()).name(),request);
        SessionUtils.setAttribute(Constants.ACCOUNT_ID, accountBO.getAccountId(),request);

        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/applyPaymentAction");
        addRequestParameter("input", "loan");
        addRequestParameter("method", "applyPayment");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter("receiptId", "101");

        String currentDate = DateUtils.makeDateAsSentFromBrowser();
        addRequestDateParameter("receiptDate", currentDate);
        addRequestDateParameter("transactionDate", currentDate);
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("loan_detail_page");
       Assert.assertEquals(new Money(getCurrency()), accountBO.getTotalPaymentDue());
       Assert.assertEquals(0, accountBO.getTotalInstallmentsDue().size());
       Assert.assertEquals(AccountStates.LOANACC_ACTIVEINGOODSTANDING, accountBO.getAccountState().getId().shortValue());
    }

    public void testApplyPaymentPreviewDateValidation() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        accountBO = createLoanAccount();
        accountBO.setUserContext(TestObjectFactory.getContext());
        accountBO.changeStatus(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue(), null, "");

        AccountApplyPaymentActionForm accountApplyPaymentActionForm = new AccountApplyPaymentActionForm();
        accountApplyPaymentActionForm.setAmount("212");
        request.getSession().setAttribute("applyPaymentActionForm", accountApplyPaymentActionForm);
        setRequestPathInfo("/applyPaymentAction");
        addRequestParameter("input", "loan");

        addRequestParameter("method", "preview");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter("receiptId", "101");

        // date fields added individually because we're adding an invalid dat
        addRequestParameter("transactionDateDD", "4");
        addRequestParameter("transactionDateMM", "20");
        addRequestParameter("transactionDateYY", "2007");
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyActionErrors(new String[] { AccountConstants.ERROR_INVALIDDATE });
        verifyInputForward();
    }

    public void testApplyPaymentAndRetrievalForLoanWhenStatusIsChanged() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        accountBO = createLoanAccount();

        SessionUtils.setAttribute(Constants.ACCOUNT_VERSION, accountBO.getVersionNo(),request);
        SessionUtils.setAttribute(Constants.ACCOUNT_TYPE,
                AccountTypes.getAccountType(accountBO.getAccountType().getAccountTypeId()).name(),request);
        SessionUtils.setAttribute(Constants.ACCOUNT_ID, accountBO.getAccountId(),request);

        accountBO.setUserContext(TestObjectFactory.getContext());
        accountBO.changeStatus(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue(), null, "");
        AccountApplyPaymentActionForm accountApplyPaymentActionForm = new AccountApplyPaymentActionForm();
        accountApplyPaymentActionForm.setAmount("212");
        request.getSession().setAttribute("applyPaymentActionForm", accountApplyPaymentActionForm);
        setRequestPathInfo("/applyPaymentAction");
        addRequestParameter("input", "loan");
        addRequestParameter("method", "applyPayment");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter("receiptId", "101");

        String currentDate = DateUtils.makeDateAsSentFromBrowser();
        addRequestDateParameter("receiptDate", currentDate);
        addRequestDateParameter("transactionDate", currentDate);
        addRequestParameter("paymentTypeId", "1");
        actionPerform();
        verifyForward("loan_detail_page");

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
       Assert.assertEquals(new Money(getCurrency()), accountBO.getTotalPaymentDue());
       Assert.assertEquals(0, accountBO.getTotalInstallmentsDue().size());
       Assert.assertEquals(AccountStates.LOANACC_ACTIVEINGOODSTANDING, accountBO.getAccountState().getId().shortValue());

        setRequestPathInfo("/loanAccountAction");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "get");
        actionPerform();
        LoanBO loan = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        loan = (LoanBO) StaticHibernateUtil.getSessionTL().load(LoanBO.class, loan.getAccountId());
       Assert.assertEquals(AccountStates.LOANACC_ACTIVEINGOODSTANDING, loan.getAccountState().getId().shortValue());

        Short DEFAULT_LOCALE = (short) 1;
        Assert.assertNotNull(loan.getAccountState().getName());

        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().load(AccountBO.class, accountBO.getAccountId());
    }

    public void testApplyPaymentForLoanWhenReceiptDateisNull() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        accountBO = createLoanAccount();

        SessionUtils.setAttribute(Constants.ACCOUNT_VERSION, accountBO.getVersionNo(),request);
        SessionUtils.setAttribute(Constants.ACCOUNT_TYPE,
                AccountTypes.getAccountType(accountBO.getAccountType().getAccountTypeId()).name(),request);
        SessionUtils.setAttribute(Constants.ACCOUNT_ID, accountBO.getAccountId(),request);

        accountBO.setUserContext(TestObjectFactory.getContext());
        accountBO.changeStatus(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue(), null, "");
        AccountApplyPaymentActionForm accountApplyPaymentActionForm = new AccountApplyPaymentActionForm();
        accountApplyPaymentActionForm.setAmount("212");
        request.getSession().setAttribute("applyPaymentActionForm", accountApplyPaymentActionForm);
        setRequestPathInfo("/applyPaymentAction");
        addRequestParameter("input", "loan");
        addRequestParameter("method", "applyPayment");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter("receiptId", "101");
        String currentDate = DateUtils.makeDateAsSentFromBrowser();
        addRequestDateParameter("receiptDate", "");
        addRequestDateParameter("transactionDate", currentDate);

        addRequestParameter("paymentTypeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("loan_detail_page");
       Assert.assertEquals(new Money(getCurrency()), accountBO.getTotalPaymentDue());
       Assert.assertEquals(0, accountBO.getTotalInstallmentsDue().size());
       Assert.assertEquals(AccountStates.LOANACC_ACTIVEINGOODSTANDING, accountBO.getAccountState().getId().shortValue());
    }

    public void testApplyPaymentPrevious() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/applyPaymentAction");
        addRequestParameter("method", "previous");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(Constants.PREVIOUS_SUCCESS);
    }

    public void testApplyPaymentCancel() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/applyPaymentAction");
        addRequestParameter("method", "cancel");
        addRequestParameter("input", "loan");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("loan_detail_page");
    }

    private AccountBO createLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }
}
