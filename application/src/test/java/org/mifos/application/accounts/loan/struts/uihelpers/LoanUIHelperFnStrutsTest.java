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

package org.mifos.application.accounts.loan.struts.uihelpers;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_MONTH;

import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

import org.mifos.application.accounts.loan.struts.action.LoanAccountAction;
import org.mifos.application.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanUIHelperFnStrutsTest extends MifosMockStrutsTestCase {

    public LoanUIHelperFnStrutsTest() throws Exception {
        super();
    }

    private UserContext userContext;

    @Override 
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/accounts-struts-config.xml");
    }
        
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        createFlow(request, LoanAccountAction.class);
    }

    public void testGetCurrrentDate() throws InvalidDateException {
        Locale locale = new Locale("EN");
       Assert.assertEquals(DateUtils.getCurrentDate(locale), LoanUIHelperFn.getCurrrentDate(locale));
    }

    public void testGetMeetingRecurrence() throws Exception {
        UserContext userContext = TestObjectFactory.getContext();
        MeetingBO meeting = TestObjectFactory.getNewMeeting(MONTHLY, EVERY_SECOND_MONTH, CUSTOMER_MEETING, MONDAY);
       Assert.assertEquals("2 month(s)", LoanUIHelperFn.getMeetingRecurrence(meeting, userContext));
    }

    public void testGetDoubleValue() {
       Assert.assertEquals("2.2", LoanUIHelperFn.getDoubleValue(new Double(2.2)));
       Assert.assertEquals("0.0", LoanUIHelperFn.getDoubleValue(null));
    }

    public void testRepaymentScheduleInstallment() {
        long l = System.currentTimeMillis();
        RepaymentScheduleInstallment repaymentScheduleInstallment = new RepaymentScheduleInstallment(
                10, 
                new Date(l), 
                new Money(getCurrency(), "100.0"),
                new Money(getCurrency(), "100.0"),
                new Money(getCurrency(), "100.0"),
                new Money(getCurrency(), "100.0"),
                new Money(getCurrency(), "100.0"));
        repaymentScheduleInstallment.setLocale(new Locale("1"));

        Money m = new Money(getCurrency(), "100");
       Assert.assertEquals("Due date", new Date(l), repaymentScheduleInstallment.getDueDate());
       Assert.assertEquals("fees", m, repaymentScheduleInstallment.getFees());
       Assert.assertEquals("Installment","10", repaymentScheduleInstallment.getInstallment().toString());
       Assert.assertEquals("Interest", m, repaymentScheduleInstallment.getFees());
       Assert.assertEquals("Locale", "1", repaymentScheduleInstallment.getLocale().toString());
       Assert.assertEquals("Misc fees", m, repaymentScheduleInstallment.getMiscFees());
       Assert.assertEquals("Misc penalty", m, repaymentScheduleInstallment.getMiscPenalty());
       Assert.assertEquals("principal", m, repaymentScheduleInstallment.getPrincipal());
    }

    public void testShouldDisableEditAmountForGlimAccountInDifferentAccountStates() throws Exception {
        ConfigurationBusinessService configServiceMock = createMock(ConfigurationBusinessService.class);
        expect(configServiceMock.isGlimEnabled()).andReturn(true).anyTimes();
        replay(configServiceMock);
       Assert.assertTrue("assertion failed", LoanUIHelperFn.isDisabledWhileEditingGlim("clientDetails.loanAmount",
                AccountState.LOAN_APPROVED, configServiceMock));
        Assert.assertFalse(LoanUIHelperFn.isDisabledWhileEditingGlim("clientDetails.loanAmount",
                AccountState.LOAN_PARTIAL_APPLICATION, configServiceMock));
        Assert.assertFalse(LoanUIHelperFn.isDisabledWhileEditingGlim("clientDetails.loanAmount",
                AccountState.LOAN_PENDING_APPROVAL, configServiceMock));
       Assert.assertTrue(LoanUIHelperFn.isDisabledWhileEditingGlim("clientDetails.loanAmount",
                AccountState.LOAN_ACTIVE_IN_BAD_STANDING, configServiceMock));
       Assert.assertTrue(LoanUIHelperFn.isDisabledWhileEditingGlim("clientDetails.loanAmount",
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, configServiceMock));
       Assert.assertTrue(LoanUIHelperFn.isDisabledWhileEditingGlim("clientDetails.loanAmount",
                AccountState.LOAN_CLOSED_OBLIGATIONS_MET, configServiceMock));
        verify(configServiceMock);
    }
}
