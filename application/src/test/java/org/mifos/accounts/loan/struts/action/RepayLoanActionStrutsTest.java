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

package org.mifos.accounts.loan.struts.action;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanSummaryEntity;
import org.mifos.accounts.loan.struts.actionforms.RepayLoanActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class RepayLoanActionStrutsTest extends MifosMockStrutsTestCase {

    protected AccountBO accountBO = null;

    private CustomerBO center = null;

    private CustomerBO group = null;
    private UserContext userContext;
    private String flowKey;
    
    private int lsim;

    @Override
    protected void setStrutsConfig() throws IOException {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/accounts-struts-config.xml");
    }

    @Before
    public void setUp() throws Exception {
        DateTime date = new DateTime(2010, 12, 23, 12, 0, 0, 0);
        new DateTimeService().setCurrentDateTime(date);
        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        lsim = configurationPersistence.getConfigurationValueInteger(LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED);
        configurationPersistence.updateConfigurationKeyValueInteger(LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED, 1);
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, RepayLoanAction.class);
        accountBO = getLoanAccount();
        ((LoanBO) accountBO).approve(legacyPersonnelDao.getPersonnel(PersonnelConstants.TEST_USER), "approved", date.toLocalDate());
        StaticHibernateUtil.flushSession();
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
    }

    @After
    public void tearDown() throws Exception {
        new ConfigurationPersistence().updateConfigurationKeyValueInteger(LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED, lsim);
        new DateTimeService().resetToCurrentSystemDateTime();
        accountBO = null;
        group = null;
        center = null;
    }

    @Test
    public void testLoadRepayment() throws Exception {
    	setMifosUserFromContext();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "loadRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(Constants.LOAD_SUCCESS);
        Money amount = (Money) SessionUtils.getAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, request);
       Assert.assertEquals(amount, ((LoanBO) accountBO).getEarlyRepayAmount());
    }

    @Test
    public void testRepaymentPreview() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
    }

    @Test
    public void testRepaymentPrevious() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "previous");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(Constants.PREVIOUS_SUCCESS);
    }

    @Test
    public void testMakeRepaymentForCurrentDateSameAsInstallmentDate() throws Exception {
    	setMifosUserFromContext();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        Money amount = ((LoanBO) accountBO).getEarlyRepayAmount();
        SessionUtils.setAttribute(LoanConstants.WAIVED_REPAYMENT_AMOUNT, Money.zero(),request);
        SessionUtils.setAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, amount,request);
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "makeRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        RepayLoanActionForm repayLoanActionForm = new RepayLoanActionForm();
        repayLoanActionForm.setAmount(amount.toString());
        repayLoanActionForm.setWaiverInterest(false);
        repayLoanActionForm.setDateOfPayment("23/12/2010");
        setActionForm(repayLoanActionForm);
        actionPerform();
        verifyForward(Constants.UPDATE_SUCCESS);

       Assert.assertEquals(accountBO.getAccountState().getId(), Short.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));

        LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO).getLoanSummary();
       Assert.assertEquals(amount, loanSummaryEntity.getPrincipalPaid().add(loanSummaryEntity.getFeesPaid()).add(
                loanSummaryEntity.getInterestPaid()).add(loanSummaryEntity.getPenaltyPaid()));

    }

    @Test
    public void testMakeRepaymentForCurrentDateSameAsInstallmentDateWithInterestWaiver() throws Exception {
    	setMifosUserFromContext();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        Money amount = ((LoanBO) accountBO).getEarlyRepayAmount();
        Money waivedAmount = amount.subtract(((LoanBO) accountBO).waiverAmount());
        SessionUtils.setAttribute(LoanConstants.WAIVED_REPAYMENT_AMOUNT, waivedAmount, request);
        SessionUtils.setAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, amount,request);
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "makeRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        RepayLoanActionForm repayLoanActionForm = new RepayLoanActionForm();
        repayLoanActionForm.setAmount(amount.toString());
        repayLoanActionForm.setWaiverInterest(true);
        repayLoanActionForm.setDateOfPayment("23/12/2010");
        setActionForm(repayLoanActionForm);
        actionPerform();
        verifyForward(Constants.UPDATE_SUCCESS);

       Assert.assertEquals(accountBO.getAccountState().getId(), Short.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));

        LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO).getLoanSummary();
       Assert.assertEquals(waivedAmount, loanSummaryEntity.getPrincipalPaid().add(loanSummaryEntity.getFeesPaid()).add(
                loanSummaryEntity.getInterestPaid()).add(loanSummaryEntity.getPenaltyPaid()));

    }

    @Test
    public void testMakeRepaymentForCurrentDateLiesBetweenInstallmentDates() throws Exception {
    	setMifosUserFromContext();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        accountBO.changeFirstInstallmentDateBy(-1);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        Money amount = ((LoanBO) accountBO).getEarlyRepayAmount();
        SessionUtils.setAttribute(LoanConstants.WAIVED_REPAYMENT_AMOUNT, Money.zero(),request);
        SessionUtils.setAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, amount,request);


        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "makeRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        RepayLoanActionForm repayLoanActionForm = new RepayLoanActionForm();
        repayLoanActionForm.setAmount(amount.toString());
        repayLoanActionForm.setWaiverInterest(false);
        repayLoanActionForm.setDateOfPayment("23/12/2010");
        setActionForm(repayLoanActionForm);
        actionPerform();
        verifyForward(Constants.UPDATE_SUCCESS);

       Assert.assertEquals(accountBO.getAccountState().getId(), Short.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));

        LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO).getLoanSummary();
       Assert.assertEquals(amount, loanSummaryEntity.getPrincipalPaid().add(loanSummaryEntity.getFeesPaid()).add(
                loanSummaryEntity.getInterestPaid()).add(loanSummaryEntity.getPenaltyPaid()));

    }

    @Test
    public void testMakeRepaymentForCurrentDateLiesBetweenInstallmentDatesWithInterestWaiver() throws Exception {
    	setMifosUserFromContext();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        accountBO.changeFirstInstallmentDateBy(-1);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        Money amount = ((LoanBO) accountBO).getEarlyRepayAmount();
        Money waivedAmount = amount.subtract(((LoanBO) accountBO).waiverAmount());
        SessionUtils.setAttribute(LoanConstants.WAIVED_REPAYMENT_AMOUNT, waivedAmount,request);
        SessionUtils.setAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, amount,request);

        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "makeRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter("paymentTypeId", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

        RepayLoanActionForm repayLoanActionForm = new RepayLoanActionForm();
        repayLoanActionForm.setAmount(amount.toString());
        repayLoanActionForm.setWaiverInterest(true);
        setActionForm(repayLoanActionForm);
        repayLoanActionForm.setDateOfPayment("23/12/2010");
        actionPerform();
        verifyForward(Constants.UPDATE_SUCCESS);

       Assert.assertEquals(accountBO.getAccountState().getId(), Short.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));

        LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO).getLoanSummary();
       Assert.assertEquals(waivedAmount, loanSummaryEntity.getPrincipalPaid().add(loanSummaryEntity.getFeesPaid()).add(
                loanSummaryEntity.getInterestPaid()).add(loanSummaryEntity.getPenaltyPaid()));

    }

    private AccountBO getLoanAccount() {
        Date startDate = new DateTimeService().getCurrentJavaDateTime();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
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
