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

import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.domain.builders.CenterBuilder;
import org.mifos.domain.builders.ClientBuilder;
import org.mifos.domain.builders.GroupBuilder;
import org.mifos.domain.builders.MeetingBuilder;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.MifosUser;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@SuppressWarnings("unchecked")
public class EditStatusActionStrutsTest extends MifosMockStrutsTestCase {


    private AccountBO accountBO;

    private UserContext userContext;

    private ClientBO client;
    private GroupBO group;
    private CenterBO center;
    private MeetingBO meeting;
    private SavingsOfferingBO savingsOffering;
    private String flowKey;

    @Override
    protected void setStrutsConfig() throws IOException {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/accounts-struts-config.xml");
    }

    @Before
    public void setUp() throws Exception {
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, EditStatusAction.class);

        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @After
    public void tearDown() throws Exception {
        accountBO = null;
        client = null;
        group = null;
        center = null;

    }

    @Ignore("Convert to unit test")
    public void load() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
//        createInitialObjects();
        createCenterGroupClientHierarchy();
        accountBO = getLoanAccount(client, meeting, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);
        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("input", "loan");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("load_success");
        verifyNoActionErrors();
        verifyNoActionMessages();

        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
        Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils
                .getAttribute(SavingsConstants.STATUS_LIST, request)).size());
        StaticHibernateUtil.flushSession();
    }

    @Ignore("Convert to unit test")
    public void previewSuccess() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
//        createInitialObjects();
        createCenterGroupClientHierarchy();
        accountBO = getLoanAccount(client, meeting, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);

        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("input", "loan");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("load_success");
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
        Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils
                .getAttribute(SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "preview");
        addRequestParameter("input", "loan");
        addRequestParameter("notes", "Test");
        addRequestParameter("accountTypeId", accountBO.getType().getValue().toString());
        addRequestParameter("newStatusId", "8");
        addRequestParameter("flagId", "1");
        actionPerform();
        verifyForward("preview_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertEquals("Closed- Rescheduled", (String) SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME,
                request));
        Assert.assertNull("Since new Status is not cancel,so flag should be null.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request.getSession()));
        StaticHibernateUtil.flushSession();
    }
    @Ignore("Convert to unit test")
    public void previewFailure() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
//        createInitialObjects();
        createCenterGroupClientHierarchy();
        accountBO = getLoanAccount(client, meeting, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);

        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("input", "loan");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("load_success");
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
        Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils
                .getAttribute(SavingsConstants.STATUS_LIST, request)).size());
        StaticHibernateUtil.flushSession();
        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("input", "loan");
        addRequestParameter("accountTypeId", accountBO.getType().getValue().toString());
        addRequestParameter("newStatusId", "8");
        addRequestParameter("flagId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        Assert.assertEquals(1, getErrorSize());
        verifyActionErrors(new String[] { LoanConstants.MANDATORY_TEXTBOX });
    }

    @Test
    public void testPrevious() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
//        createInitialObjects();
        createCenterGroupClientHierarchy();
        accountBO = getLoanAccount(client, meeting, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);
        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "previous");
        addRequestParameter("input", "loan");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("previous_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testCancel() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "cancel");
        addRequestParameter("input", "loan");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("loan_detail_page");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }
    @Ignore("Convert to unit test")
    public void updateSuccessForLoan() throws Exception {

        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
//        createInitialObjects();
        createCenterGroupClientHierarchy();
        accountBO = getLoanAccount(client, meeting, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);

        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("input", "loan");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("load_success");
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
        Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils
                .getAttribute(SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("input", "loan");
        addRequestParameter("notes", "Test");
        addRequestParameter("accountTypeId", accountBO.getType().getValue().toString());
        addRequestParameter("newStatusId", "8");
        addRequestParameter("flagId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("preview_success");

        StaticHibernateUtil.flushSession();
        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "update");
        addRequestParameter("notes", "Test");
        addRequestParameter("accountTypeId", accountBO.getType().getValue().toString());
        addRequestParameter("newStatusId", "8");
        addRequestParameter("flagId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.loan_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();

        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.LOAN, accountBO.getAccountId());
        Assert.assertEquals(1, auditLogList.size());
        Assert.assertEquals(EntityType.LOAN.getValue(), auditLogList.get(0).getEntityType());
        Assert.assertEquals(3, auditLogList.get(0).getAuditLogRecords().size());
        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Explanation")) {
                Assert.assertEquals("-", auditLogRecord.getOldValue());
                Assert.assertEquals("Withdraw", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Status")) {
                Assert.assertEquals("Active in Good Standing", auditLogRecord.getOldValue());
                Assert.assertEquals("Closed- Rescheduled", auditLogRecord.getNewValue());
            }
        }

    }

    @Test
    public void testUpdateStatusForLoanToCancel() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
//        createInitialObjects();
        createCenterGroupClientHierarchy();
        accountBO = getLoanAccount(client, meeting, AccountState.LOAN_PARTIAL_APPLICATION);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "update");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("accountTypeId", accountBO.getType().getValue().toString());
        addRequestParameter("newStatusId", AccountState.LOAN_CANCELLED.getValue().toString());
        addRequestParameter("flagId", AccountStateFlag.LOAN_WITHDRAW.getValue().toString());
        addRequestParameter("input", "loan");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.loan_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testUpdateSuccessForSavings() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
//        createInitialObjects();
        createCenterGroupClientHierarchy();
        savingsOffering = createSavingsOffering();
        accountBO = createSavingsAccount("000X00000000019", savingsOffering, AccountState.SAVINGS_PARTIAL_APPLICATION);

        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("input", "savings");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("load_success");
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
        Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils
                .getAttribute(SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("input", "savings");
        addRequestParameter("notes", "Test");
        addRequestParameter("accountTypeId", accountBO.getType().getValue().toString());
        addRequestParameter("newStatusId", "15");
        addRequestParameter("flagId", "4");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("preview_success");

        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "update");
        addRequestParameter("notes", "Test");
        addRequestParameter("accountTypeId", accountBO.getType().getValue().toString());
        addRequestParameter("newStatusId", "15");
        addRequestParameter("flagId", "4");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.savings_details_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testUpdateStatusForSavingsToCancel() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
//        createInitialObjects();
        createCenterGroupClientHierarchy();
        savingsOffering = createSavingsOffering();
        accountBO = createSavingsAccount("000X00000000019", savingsOffering, AccountState.SAVINGS_PARTIAL_APPLICATION);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "update");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("accountTypeId", accountBO.getType().getValue().toString());
        addRequestParameter("input", "savings");
        addRequestParameter("newStatusId", AccountState.SAVINGS_CANCELLED.getValue().toString());
        addRequestParameter("flagId", AccountStateFlag.SAVINGS_BLACKLISTED.getValue().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.savings_details_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testUpdateStatusFailureNoPermission() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
//        createInitialObjects();
        createCenterGroupClientHierarchy();
        savingsOffering = createSavingsOffering();
        accountBO = createSavingsAccount("000X00000000019", savingsOffering, AccountState.SAVINGS_PARTIAL_APPLICATION);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        SessionUtils.setAttribute(Constants.USERCONTEXT, createUser(), request.getSession());
        setRequestPathInfo("/editStatusAction.do");
        addRequestParameter("method", "update");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("accountTypeId", accountBO.getType().getValue().toString());
        addRequestParameter("newStatusId", "15");
        addRequestParameter("flagId", "4");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyActionErrors(new String[] { SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED });
    }

    private UserContext createUser() throws Exception {
        this.userContext = TestUtils.makeUser(TestUtils.DUMMY_ROLE);
        return userContext;
    }

    private void createCenterGroupClientHierarchy() throws CustomerException {
        meeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(meeting);

        center = new CenterBuilder().withName("Savings Center")
                                    .with(meeting)
                                    .with(sampleBranchOffice())
                                    .withLoanOfficer(testUser())
                                    .withActivationDate(mondayTwoWeeksAgo())
                                    .build();
        IntegrationTestObjectMother.createCenter(center, meeting);

        group = new GroupBuilder().withName("Group")
                                  .withMeeting(meeting)
                                  .withOffice(sampleBranchOffice())
                                  .withLoanOfficer(testUser())
                                  .withParentCustomer(center)
                                  .build();
        IntegrationTestObjectMother.createGroup(group, meeting);

        client = new ClientBuilder().withName("Client 1").active()
                                    .withMeeting(meeting)
                                    .withOffice(sampleBranchOffice())
                                    .withLoanOfficer(testUser())
                                    .withParentCustomer(group)
                                    .buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(client, meeting);
    }

    private DateTime mondayTwoWeeksAgo() {
        return new DateTime();
    }

    private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting, AccountState accountState) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", customer, accountState, startDate, loanOffering);

    }

    private SavingsOfferingBO createSavingsOffering() {
        Date currentDate = new Date(System.currentTimeMillis());
        return TestObjectFactory.createSavingsProduct("SavingPrd1", "S", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
    }

    private SavingsBO createSavingsAccount(String globalAccountNum, SavingsOfferingBO savingsOffering,
            AccountState state) throws Exception {
        return TestObjectFactory.createSavingsAccount(globalAccountNum, group, state, new Date(System
                .currentTimeMillis()), savingsOffering, userContext);
    }
}
