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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.loan.business.LoanActivityEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.dto.domain.InstallmentDetailsDto;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.persistence.LegacyAuditDao;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@SuppressWarnings({"unchecked", "rawtypes"})
public class LoanAccountActionStrutsTest extends AbstractLoanActionTestCase {

    @Autowired
    private LegacyAccountDao legacyAccountDao;

    @Autowired
    private LegacyAuditDao legacyAuditDao;

    private static final double DELTA = 0.00000001;
    private String flowKey1;
    private HashMap<String, String> schedulePreviewPageParams;
    private HashMap<String, String> prdOfferingPageParams;
    private CustomerBO customerMock;
    private LoanBusinessService loanBusinessServiceMock;
    private ConfigurationBusinessService configurationBusinessServiceMock;
    private HttpServletRequest requestMock;

    @Override
    protected void setStrutsConfig() throws IOException {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/accounts-struts-config.xml");
    }

    @Before
    public void setUp() throws Exception {
    	userContext = TestObjectFactory.getContext();
        enableCustomWorkingDays();
        prdOfferingPageParams = new HashMap<String, String>();
        initPageParams();
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
    }

    @Test
    public void testUpdateSuccessWithRegeneratingNewRepaymentSchedule() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        Date startDate = new Date(System.currentTimeMillis());
        String newDate = offSetCurrentDate(14, userContext.getPreferredLocale());
        // This is a loan with weekly meetings every week. No interest is paid
        // at disbursement, so the first payment will be one week after the
        // disbursement date of the loan (14 days + 7 days = 21 days)
        String firstInstallmentDateWithNoInterestPaidAtDisbursementDate = offSetCurrentDate(21, userContext
                .getPreferredLocale());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        ((LoanBO) accountBO).setBusinessActivityId(1);
        PersonnelBO loggedInUser = IntegrationTestObjectMother.testUser();
        accountBO.changeStatus(AccountState.LOAN_APPROVED, null, "status changed", loggedInUser);
        accountBO.update();

        StaticHibernateUtil.flushSession();
        LoanBO loan = (LoanBO) accountBO;
        LoanOfferingBO loanOffering = loan.getLoanOffering();
        // loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "manage");
        addRequestParameter("customerId", accountBO.getCustomer().getCustomerId().toString());
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        actionPerform();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "managePreview");
        addRequestParameter("loanAmount", loanOffering.getEligibleLoanAmountSameForAllLoan().getDefaultLoanAmount()
                .toString());
        addRequestParameter("interestRate", loan.getLoanOffering().getDefInterestRate().toString());
        addRequestParameter("noOfInstallments", loanOffering.getDefaultNumOfEligibleInstallmentsSameForAllLoan().toString());
        addRequestParameter("disbursementDate", newDate);
        addRequestParameter("gracePeriodDuration", "0");
        addRequestParameter("intDedDisbursement", "0");
        addRequestParameter("customerId", group.getCustomerId().toString());
        actionPerform();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "update");
        addRequestParameter("collateralNote", "test");
        actionPerform();
        verifyForward(ActionForwards.update_success.toString());
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        Assert.assertEquals("test", loan.getCollateralNote());
        Assert.assertEquals(300.0, loan.getLoanAmount().getAmount().doubleValue(), DELTA);
        Assert.assertEquals(0, loan.getGracePeriodDuration().intValue());
        Assert.assertEquals(firstInstallmentDateWithNoInterestPaidAtDisbursementDate, DateUtils.getUserLocaleDate(
                TestObjectFactory.getContext().getPreferredLocale(), DateUtils.toDatabaseFormat(loan
                        .getAccountActionDate(Short.valueOf("1")).getActionDate())));

    }

    @Test
    public void testGetForCancelledLoanAccount() throws Exception {
        setMifosUserFromContext();

        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        PersonnelBO loggedInUser = IntegrationTestObjectMother.testUser();
        accountBO.changeStatus(AccountState.LOAN_CANCELLED, AccountStateFlag.LOAN_WITHDRAW.getValue(), "status changed", loggedInUser);
        accountBO.update();
        StaticHibernateUtil.flushSession();
        LoanBO loan = (LoanBO) accountBO;

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        actionPerform();
        verifyForward("get_success");

        Assert.assertEquals(0, loan.getPerformanceHistory().getNoOfPayments().intValue());
//        Assert.assertEquals(((LoanBO) accountBO).getTotalAmountDue().getAmountDoubleValue(), 212.0);
        modifyActionDateForFirstInstallment();
        Assert.assertEquals("Total no. of notes should be 6", 6, accountBO.getAccountNotes().size());

//        LoanInformationDto loanInformationDto = retrieveLoanInformationDtoFromSession();
//        Assert.assertEquals("Total no. of recent notes should be 3", 3, (loanInformationDto.getRecentAccountNotes().size()));
        Assert.assertEquals("Total no. of flags should be 1", 1, accountBO.getAccountFlags().size());
    }

    @Test
    public void testforwardWaiveCharge() {
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("method", "forwardWaiveCharge");
        addRequestParameter("type", "LoanAccount");
        performNoErrors();
        verifyForward("waiveLoanAccountCharges_Success");
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    @Test
    public void testLoadChangeLog() {
        accountBO = getLoanAccount();
        AuditLog auditLog = new AuditLog(accountBO.getAccountId(), EntityType.LOAN.getValue(), "Mifos",
                new java.sql.Date(System.currentTimeMillis()), Short.valueOf("3"));
        Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
        AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1", "test_1", "new_test_1", auditLog);
        auditLogRecords.add(auditLogRecord);
        auditLog.addAuditLogRecords(auditLogRecords);
        legacyAuditDao.save(auditLog);

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("method", "loadChangeLog");
        addRequestParameter("entityType", "Loan");
        addRequestParameter("entityId", accountBO.getAccountId().toString());
        actionPerform();
        Assert.assertEquals(1, ((List) request.getSession().getAttribute(AuditConstants.AUDITLOGRECORDS)).size());
        verifyForward("viewLoanChangeLog");

    }

    @Test
    public void testCancelChangeLog() {
        accountBO = getLoanAccount();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("method", "cancelChangeLog");
        addRequestParameter("entityType", "Loan");
        actionPerform();
        verifyForward("cancelLoanChangeLog");
    }

    @Test
    public void testGetAllActivity() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        LoanBO loan = (LoanBO) accountBO;
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getAllActivity");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("getAllActivity_success");
        Assert.assertEquals(1, ((List<LoanActivityDto>) SessionUtils.getAttribute(
                LoanConstants.LOAN_ALL_ACTIVITY_VIEW, request)).size());
    }

    @Test
    public void testGetInstallmentDetails() throws Exception {
    	setMifosUserFromContext();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        LoanBO loan = (LoanBO) accountBO;
        for (AccountActionDateEntity accountActionDateEntity : loan.getAccountActionDates()) {
            if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                LoanBOTestUtils.setActionDate(accountActionDateEntity, offSetDate(accountActionDateEntity
                        .getActionDate(), -14));
            } else if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                LoanBOTestUtils.setActionDate(accountActionDateEntity, offSetDate(accountActionDateEntity
                        .getActionDate(), -7));
            }
        }
        TestObjectFactory.updateObject(loan);
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getInstallmentDetails");
        addRequestParameter("accountId", String.valueOf(loan.getAccountId()));
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("viewInstmentDetails_success");

        InstallmentDetailsDto view = (InstallmentDetailsDto) SessionUtils.getAttribute(
                LoanConstants.VIEW_OVERDUE_INSTALLMENT_DETAILS, request);
        Assert.assertEquals("12.0", view.getInterest());
//        Assert.assertEquals("100.0", view.getFees());
        Assert.assertEquals("0.0", view.getPenalty());
        Assert.assertEquals("100.0", view.getPrincipal());
    }

    @Test
    public void testGet() throws Exception {
        setMifosUserFromContext();

        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        LoanBO loan = (LoanBO) accountBO;

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        actionPerform();
        verifyForward("get_success");

        Assert.assertEquals(0, loan.getPerformanceHistory().getNoOfPayments().intValue());
//        Assert.assertEquals((accountBO).getTotalAmountDue().getAmountDoubleValue(), 212.0);
        modifyActionDateForFirstInstallment();
        Assert.assertEquals("Total no. of notes should be 5", 5, accountBO.getAccountNotes().size());

//        LoanInformationDto loanInformationDto = retrieveLoanInformationDtoFromSession();
//        Assert.assertEquals("Total no. of recent notes should be 3", 3,  loanInformationDto.getRecentAccountNotes().size());
    }

    @Test
    public void testGetWithPayment() throws Exception {
        setMifosUserFromContext();

        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        disburseLoan(startDate);
        LoanBO loan = (LoanBO) accountBO;

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        actionPerform();
        verifyForward("get_success");

        Assert.assertEquals("Total no. of notes should be 5", 5, accountBO.getAccountNotes().size());
//        LoanInformationDto loanInformationDto = retrieveLoanInformationDtoFromSession();
//        Assert.assertEquals("Total no. of recent notes should be 3", 3, loanInformationDto.getRecentAccountNotes().size());

        Assert.assertEquals("Last payment action should be 'PAYMENT'", AccountActionTypes.DISBURSAL.getValue(),
                SessionUtils.getAttribute(AccountConstants.LAST_PAYMENT_ACTION, request));
        client = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, client.getCustomerId());
        group = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, group.getCustomerId());
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
    }

    @Test
    public void testGetLoanRepaymentSchedule() {
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getLoanRepaymentSchedule");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.getLoanRepaymentSchedule.toString());
    }

    @Test
    public void testViewStatusHistory() {
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        LoanBO loan = (LoanBO) accountBO;

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        actionPerform();

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "viewStatusHistory");
        actionPerform();
        verifyForward(ActionForwards.viewStatusHistory.toString());
    }

    private Locale getLocale() {
        return ((UserContext) request.getSession().getAttribute("UserContext")).getPreferredLocale();
    }
   
    @Test
    public void testManage() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        LoanBO loan = (LoanBO) accountBO;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "manage");
        addRequestParameter("customerId", accountBO.getCustomer().getCustomerId().toString());

        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        actionPerform();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(LoanConstants.LOANOFFERING, request));
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.COLLATERAL_TYPES, request));
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.BUSINESS_ACTIVITIES, request));
        Assert.assertNotNull(SessionUtils.getAttribute(LoanConstants.CUSTOM_FIELDS, request));
    }

    /*
     * this test appears to be confirming that an unauthenticated user gets a
     * "page expired" exception, but the catch() block doesn't appear to be
     * reached.
     */
    @Test
    public void testManageWithoutFlow() throws Exception {
        try {
            request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
            Date startDate = new Date(System.currentTimeMillis());
            accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
            LoanBO loan = (LoanBO) accountBO;
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
            setRequestPathInfo("/loanAccountAction.do");
            addRequestParameter("method", "manage");
            addRequestParameter("customerId", accountBO.getCustomer().getCustomerId().toString());
            addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
            actionPerform();
            // I'd normally expect to see a call to JUnit 3's fail() here
        } catch (PageExpiredException pe) {
            Assert.assertTrue(true);
            Assert.assertEquals(ExceptionConstants.PAGEEXPIREDEXCEPTION, pe.getKey());
        }

    }

    @Test
    public void testCancel() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "cancel");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.loan_detail_page.toString());
    }

    @Test
    public void testUpdateSuccessWithoutRegeneratingNewRepaymentSchedule() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        accountBO = getLoanAccount();
        LoanBO loan = (LoanBO) accountBO;
        LoanOfferingBO loanOffering = loan.getLoanOffering();
        Date firstInstallmentDate = loan.getDetailsOfNextInstallment().getActionDate();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        Date newDate = DateUtils.addWeeks(loan.getDisbursementDate(), 1);
        Date originalDate = loan.getDisbursementDate();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "update");
        addRequestParameter("loanAmount", loanOffering.getEligibleLoanAmountSameForAllLoan().getDefaultLoanAmount()
                .toString());
        addRequestParameter("interestRate", loan.getLoanOffering().getDefInterestRate().toString());
        addRequestParameter("noOfInstallments", loanOffering.getDefaultNumOfEligibleInstallmentsSameForAllLoan().toString());
        addRequestParameter("disbursementDate", DateUtils.format(newDate));
        addRequestParameter("businessActivityId", "1");
        addRequestParameter("intDedDisbursement", "0");
        addRequestParameter("gracePeriodDuration", "1");
        addRequestParameter("collateralNote", "test");
        actionPerform();
        verifyForward(ActionForwards.update_success.toString());

        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        Assert.assertEquals("test", loan.getCollateralNote());
        Assert.assertEquals(300.0, loan.getLoanAmount().getAmount().doubleValue(), DELTA);
        Assert.assertEquals(1, loan.getGracePeriodDuration().intValue());
        Assert.assertEquals(DateUtils.format(originalDate), DateUtils.getUserLocaleDate(TestObjectFactory.getContext()
                .getPreferredLocale(), DateUtils.toDatabaseFormat(loan.getDisbursementDate())));
        Assert.assertEquals(firstInstallmentDate, loan.getAccountActionDate(Short.valueOf("1")).getActionDate());
    }

    private void modifyActionDateForFirstInstallment() throws Exception {
        LoanScheduleEntity installment = (LoanScheduleEntity) accountBO.getAccountActionDate((short) 1);
        LoanBOTestUtils.modifyData(installment, new Money(getCurrency(), "5.0"), installment.getPenaltyPaid(),
                installment.getMiscPenalty(), installment.getMiscPenaltyPaid(), installment.getMiscFee(), installment
                        .getMiscFeePaid(), new Money(getCurrency(), "20.0"), installment.getPrincipalPaid(), new Money(
                        getCurrency(), "10.0"), installment.getInterestPaid());
        LoanBOTestUtils.setActionDate(installment, offSetCurrentDate(1));
        accountBO = saveAndFetch(accountBO);
    }

    private java.sql.Date offSetCurrentDate(int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }

    private AccountBO saveAndFetch(AccountBO account) throws Exception {
        TestObjectFactory.updateObject(account);
        return legacyAccountDao.getAccount(account.getAccountId());
    }

    private AccountNotesEntity createAccountNotes(String comment, AccountBO account) {
        AccountNotesEntity accountNotes = new AccountNotesEntity(new java.sql.Date(System.currentTimeMillis()),
                comment, TestObjectFactory.getPersonnel(userContext.getId()), account);
        return accountNotes;
    }

    private void addNotes() {
        accountBO.addAccountNotes(createAccountNotes("Notes1", accountBO));
        TestObjectFactory.updateObject(accountBO);
        accountBO.addAccountNotes(createAccountNotes("Notes2", accountBO));
        TestObjectFactory.updateObject(accountBO);
        accountBO.addAccountNotes(createAccountNotes("Notes3", accountBO));
        TestObjectFactory.updateObject(accountBO);
        accountBO.addAccountNotes(createAccountNotes("Notes4", accountBO));
        TestObjectFactory.updateObject(accountBO);
        accountBO.addAccountNotes(createAccountNotes("Notes5", accountBO));
        TestObjectFactory.updateObject(accountBO);
    }

    private void disburseLoan(Date startDate) throws Exception {
        ((LoanBO) accountBO).disburseLoan("1234", startDate, Short.valueOf("1"), accountBO.getPersonnel(), startDate,
                Short.valueOf("1"), Short.valueOf("1"), null);
        StaticHibernateUtil.flushSession();
    }

    private AccountBO getLoanAccount(AccountState state, Date startDate, int disbursalType) {
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        accountBO = TestObjectFactory.createLoanAccountWithDisbursement("99999999999", group, state, startDate,
                loanOffering, disbursalType);
        LoanActivityEntity loanActivity = new LoanActivityEntity(accountBO, TestObjectFactory.getPersonnel(userContext
                .getId()), "testing", new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(
                getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(
                getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), startDate);
        ((LoanBO) accountBO).addLoanActivity(loanActivity);
        addNotes();
        TestObjectFactory.updateObject(accountBO);
        return accountBO;
    }
  
    private String offSetCurrentDate(int noOfDays, Locale locale) throws InvalidDateException {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
        java.sql.Date currentDate = new java.sql.Date(currentDateCalendar.getTimeInMillis());
        SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        String userfmt = DateUtils.convertToCurrentDateFormat(format.toPattern());
        return DateUtils.convertDbToUserFmt(currentDate.toString(), userfmt);
    }

    private java.sql.Date offSetDate(Date date, int noOfDays) {
        Calendar dateCalendar = new GregorianCalendar();
        dateCalendar.setTimeInMillis(date.getTime());
        int year = dateCalendar.get(Calendar.YEAR);
        int month = dateCalendar.get(Calendar.MONTH);
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        dateCalendar = new GregorianCalendar(year, month, day + noOfDays);
        return new java.sql.Date(dateCalendar.getTime().getTime());
    }

    private AccountBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, center.getCustomerMeeting()
                .getMeeting());
        return TestObjectFactory.createLoanAccount("42423142341", client, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    public void makeRepaymentForLastLoanAmount() throws Exception {
        flowKey1 = createFlow(request, RepayLoanAction.class);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey1);
        accountBO = getLoanAccountFromLastLoanAmount();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "makeRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter("paymentTypeId", "1");
        actionPerform();
    }

    private AccountBO getLoanAccountFromLastLoanAmount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    private void initPageParams() throws InvalidDateException {
        initPageParams(loanOffering);
    }

    private void initPageParams(LoanOfferingBO loanOffering) throws InvalidDateException {
        initPrdOfferingPageParams(loanOffering);
        initSchedulePreviewPageParams(loanOffering);
    }

    private void initSchedulePreviewPageParams(LoanOfferingBO loanOffering) throws InvalidDateException {
        schedulePreviewPageParams = new HashMap<String, String>();
        schedulePreviewPageParams.put("loanAmount", loanOffering.eligibleLoanAmount(
                group.getMaxLoanAmount(loanOffering), group.getMaxLoanCycleForProduct(loanOffering))
                .getDefaultLoanAmount().toString());
        schedulePreviewPageParams.put("interestRate", loanOffering.getMaxInterestRate().toString());
        schedulePreviewPageParams.put("noOfInstallments", loanOffering.eligibleNoOfInstall(
                group.getMaxLoanAmount(loanOffering), group.getMaxLoanCycleForProduct(loanOffering))
                .getDefaultNoOfInstall().toString());
        schedulePreviewPageParams.put("disbursementDate", DateUtils.getCurrentDate(getLocale()));
        schedulePreviewPageParams.put("gracePeriodDuration", "1");
        schedulePreviewPageParams.put("businessActivityId", "1");
        schedulePreviewPageParams.put("loanOfferingFund", "1");

    }

    private void initPrdOfferingPageParams(LoanOfferingBO loanOffering) {
        prdOfferingPageParams.put("prdOfferingId", loanOffering.getPrdOfferingId().toString());
    }

    @Test
    public void testShouldNotSetAnyGlimSpecificAttributesIfGlimDisabled() throws Exception {
        loanBusinessServiceMock = createMock(LoanBusinessService.class);
        customerMock = createMock(CustomerBO.class);
        expect(customerMock.isGroup()).andReturn(false);
        configurationBusinessServiceMock = createMock(ConfigurationBusinessService.class);
        expect(configurationBusinessServiceMock.isGlimEnabled()).andReturn(true);
        LoanAccountAction loanAccountAction = new LoanAccountAction(loanBusinessServiceMock,
                configurationBusinessServiceMock, new GlimLoanUpdater());
        requestMock = createMock(HttpServletRequest.class);
        replay(loanBusinessServiceMock, configurationBusinessServiceMock, customerMock, requestMock);
        LoanAccountAction.GlimSessionAttributes glimSessionAttributes = new LoanAccountAction.GlimSessionAttributes(
                LoanConstants.GLIM_DISABLED_VALUE);
        Assert.assertEquals(glimSessionAttributes, loanAccountAction.getGlimSpecificPropertiesToSet(
                new LoanAccountActionForm(), "1", customerMock, new ArrayList<ValueListElement>()));
        verify(loanBusinessServiceMock, configurationBusinessServiceMock, customerMock, requestMock);
    }

    @Test
    public void testShouldPopulateClientDetailsFromLoan() throws Exception {

        ClientBO clientMock1 = createMock(ClientBO.class);
        expect(clientMock1.getCustomerId()).andReturn(1).anyTimes();
        expect(clientMock1.getDisplayName()).andReturn("client 1");

        ClientBO clientMock2 = createMock(ClientBO.class);
        expect(clientMock2.getCustomerId()).andReturn(2).anyTimes();
        expect(clientMock2.getDisplayName()).andReturn("client 2");

        LoanBO loanMock = createMock(LoanBO.class);
        expect(loanMock.getCustomer()).andReturn(clientMock1).anyTimes();
        expect(loanMock.getBusinessActivityId()).andReturn(3);
        expect(loanMock.getLoanAmount()).andReturn(new Money(getCurrency(), "100")).anyTimes();

        LoanAccountDetailsDto clientDetails1 = new LoanAccountDetailsDto();
        clientDetails1.setClientId("1");
        clientDetails1.setClientName("client 1");
        clientDetails1.setBusinessActivity("3");
        clientDetails1.setLoanAmount("100.0");

        LoanAccountDetailsDto clientDetails2 = new LoanAccountDetailsDto();
        clientDetails2.setClientId("2");
        clientDetails2.setClientName("client 2");

        replay(clientMock1, clientMock2, loanMock);

        List<LoanAccountDetailsDto> clientDetails = new LoanAccountAction().populateClientDetailsFromLoan(Arrays
                .asList(clientMock1, clientMock2), Arrays.asList(loanMock), new ArrayList<ValueListElement>());
        Assert.assertEquals(Arrays.asList(clientDetails1, clientDetails2), clientDetails);
        verify(clientMock1, clientMock2, loanMock);
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
