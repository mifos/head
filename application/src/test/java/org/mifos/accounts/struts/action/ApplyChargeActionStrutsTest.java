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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.struts.actionforms.ApplyChargeActionForm;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.ApplicableCharge;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@SuppressWarnings("unchecked")
public class ApplyChargeActionStrutsTest extends MifosMockStrutsTestCase {

    private AccountBO accountBO;

    private UserContext userContext;

    private CustomerBO client;

    private CustomerBO group;

    private CustomerBO center;

    private MeetingBO meeting;

    private String flowKey;

    // for constructing the ChargeType member
    private static final String FEE_ID = "-1";
    private static final String IS_RATE_TYPE = "1";
    private static final String IS_PENALTY_TYPE = "0";
    private static final String IS_NOT_RATE_TYPE = "0";

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
        flowKey = createFlow(request, ApplyChargeAction.class);
    }

    @After
    public void tearDown() throws Exception {
        accountBO = null;
        client = null;
        group = null;
        center = null;
    }

    @Test
    public void testLoad() throws Exception {
    	setMifosUserFromContext();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createInitialObjects();
        accountBO = getLoanAccount(client, meeting);
        setRequestPathInfo("/applyChargeAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("load_success");
        verifyNoActionErrors();
        verifyNoActionMessages();

        Assert.assertNotNull(SessionUtils.getAttribute(AccountConstants.APPLICABLE_CHARGE_LIST, request));
       Assert.assertEquals("Size of the list should be 2", 2, ((List<ApplicableCharge>) SessionUtils.getAttribute(
                AccountConstants.APPLICABLE_CHARGE_LIST, request)).size());
    }

    @Test
    public void testCancel() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createInitialObjects();
        setRequestPathInfo("/applyChargeAction.do");
        addRequestParameter("method", "cancel");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        accountBO = getLoanAccount(client, meeting);
        ApplyChargeActionForm applyChargeActionForm = new ApplyChargeActionForm();
        applyChargeActionForm.setAccountId(accountBO.getAccountId().toString());
        setActionForm(applyChargeActionForm);

        actionPerform();
        verifyForward("loanDetails_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    @Test
    public void testUpdateSuccess() {

        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().nonLoanOfficer().withAdminRole().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createInitialObjects();
        accountBO = getLoanAccount(client, meeting);
        setRequestPathInfo("/applyChargeAction.do");
        addRequestParameter("method", "update");
        addRequestParameter("chargeType", IS_PENALTY_TYPE + ":" + FEE_ID + ":" + IS_NOT_RATE_TYPE);
        addRequestParameter("charge", "18");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
//        verifyForward("loanDetails_success");
        verifyNoActionErrors();
        verifyNoActionMessages();

    }

    @Test
    public void testUpdateFailureDueToInvalidChargeAmount() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createInitialObjects();
        accountBO = getLoanAccount(client, meeting);
        setRequestPathInfo("/applyChargeAction.do");
        addRequestParameter("method", "update");
        addRequestParameter("chargeType", IS_PENALTY_TYPE + ":" + FEE_ID + ":" + IS_NOT_RATE_TYPE);
        addRequestParameter("charge", "123456789111111.21");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionMessages();
        Assert.assertEquals(2, getErrorSize());
        Assert.assertEquals("Charge Amount", 2, getErrorSize(AccountConstants.ACCOUNT_AMOUNT));

    }

    @Test
    public void testUpdateFailureWith_Rate_GreaterThan999() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createInitialObjects();
        accountBO = getLoanAccount(client, meeting);
        setRequestPathInfo("/applyChargeAction.do");
        addRequestParameter("method", "update");
        addRequestParameter("chargeType", IS_PENALTY_TYPE + ":" + FEE_ID + ":" + IS_RATE_TYPE);
        addRequestParameter("chargeAmount", "999999");
        addRequestParameter("selectedChargeFormula", "%LoanAmount");
        addRequestParameter("charge", "18");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
       Assert.assertEquals("Rate", 1, getErrorSize(AccountConstants.RATE));

    }

    @Test
    public void testValidate() throws Exception {
        setRequestPathInfo("/applyChargeAction.do");
        addRequestParameter("method", "validate");
        request.setAttribute("methodCalled", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.update_failure.toString());
    }

    private void createInitialObjects() {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", customer, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

    }
    
    private void setMifosUserFromContext() {
        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUser(userContext.getId(), userContext.getBranchId(), userContext.getLevelId(),
                new ArrayList<Short>(userContext.getRoles()), userContext.getName(), "".getBytes(),
                true, true, true, true, new ArrayList<GrantedAuthority>(), userContext.getLocaleId());
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
