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

package org.mifos.framework.components.batchjobs.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.Query;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.accounts.loan.business.LoanArrearsAgingEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.config.AccountingRulesConstants;
import org.mifos.config.business.Configuration;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

public class LoanArrearsAgingHelperIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    private LegacyLoanDao legacyLoanDao;

    private LoanArrearsAgingHelper loanArrearsAgingHelper;
    private LoanArrearsHelper loanArrearHelper;

    private CustomerBO center;

    private CustomerBO group;

    private MeetingBO meeting;

    private DateTime dateTime;

    private final long dummy = 0; // used for calling execute metohod

    private final int loanAmount = 100;
    private final double interestRate = 100.0;
    private final int totalInterest = 20;
    private final short numInstallments = 10;
    private final int principalForOneInstallment = loanAmount / numInstallments;
    private final int interestForOneInstallment = totalInterest / numInstallments;
    private final int onePayment = principalForOneInstallment + interestForOneInstallment;
    private final int repaymentInterval = 7; // 7 days for weekly loan repayment
    private static String oldRoundingMode;

    @Before
    public void setUp() throws Exception {
        StaticHibernateUtil.getSessionTL().clear();
        oldRoundingMode =  (String) MifosConfigurationManager.getInstance().getProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE);
        //FIXME this test was using CELLING ROUNDING MODE due to MIFOS-2659
        MifosConfigurationManager.getInstance().setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, RoundingMode.CEILING.toString());
        /*
         * Takes lateness days out of the equation so that the LoanArrearsTask will set all overdue account into bad
         * standing.
         *
         * Resetting the database in tear down puts the original value back for other tests
         */
        setLatenessDaysToZero();
        LoanArrearsTask arrearsTask = new LoanArrearsTask();
        loanArrearHelper = (LoanArrearsHelper) arrearsTask.getTaskHelper();
        LoanArrearsAgingTask loanArrearsAgingTask = new LoanArrearsAgingTask();
        loanArrearsAgingHelper = (LoanArrearsAgingHelper) loanArrearsAgingTask.getTaskHelper();
        dateTime = initializeToFixedDateTime();
        
        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().nonLoanOfficer().withAdminRole().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @After
    public void tearDown() throws Exception {
        new DateTimeService().resetToCurrentSystemDateTime();
        loanArrearsAgingHelper = null;
        loanArrearHelper = null;
        MifosConfigurationManager.getInstance().setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, oldRoundingMode);
        StaticHibernateUtil.flushAndClearSession();
    }

    private LoanBO setUpLoan(DateTime dateTime, AccountState accountState) {
        Date startDate = dateTime.toDate();
        meeting = getNewMeeting(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING, WeekDay.MONDAY, startDate);
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group",
                CustomerStatus.GROUP_ACTIVE, center);

        LoanOfferingBO product = TestObjectFactory.createLoanOffering("LoanProduct", "LP", startDate, meeting);
        LoanBO loan = createBasicLoanAccount(group, accountState, product, "" + loanAmount, numInstallments,
                interestRate);
        return loan;
    }

    private LoanBO createBasicLoanAccount(final CustomerBO customer, final AccountState state,
            final LoanOfferingBO loanOffering, String loanAmountAsString, short numInstallments, double interestRate) {
        
        MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer.getCustomerMeeting().getMeeting());
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(customer.getOfficeId(), meeting, numInstallments);

        LoanOfferingBO loanProduct = IntegrationTestObjectMother.findLoanProductBySystemId(loanOffering.getGlobalPrdOfferingNum());
        
        BigDecimal loanAmount = BigDecimal.valueOf(Double.valueOf(loanAmountAsString));
        BigDecimal minAllowedLoanAmount = loanAmount;
        BigDecimal maxAllowedLoanAmount = loanAmount;
        LocalDate disbursementDate = new LocalDate(meetingDates.get(0));
        int numberOfInstallments = numInstallments;
        int minAllowedNumberOfInstallments = loanProduct.getEligibleInstallmentSameForAllLoan().getMaxNoOfInstall();
        int maxAllowedNumberOfInstallments = loanProduct.getEligibleInstallmentSameForAllLoan().getMaxNoOfInstall();
        int graceDuration = 0;
        Integer sourceOfFundId = null;
        Integer loanPurposeId = null;
        Integer collateralTypeId = null;
        String collateralNotes = null;
        String externalId = null;
        boolean repaymentScheduleIndependentOfCustomerMeeting = false;
        RecurringSchedule recurringSchedule = null;
        List<CreateAccountFeeDto> accountFees = new ArrayList<CreateAccountFeeDto>();

        CreateLoanAccount createLoanAccount = new CreateLoanAccount(customer.getCustomerId(), loanProduct.getPrdOfferingId().intValue(), 
                state.getValue().intValue(), 
                loanAmount, minAllowedLoanAmount, maxAllowedLoanAmount, 
                interestRate, disbursementDate, numberOfInstallments, 
                minAllowedNumberOfInstallments, maxAllowedNumberOfInstallments, 
                graceDuration, sourceOfFundId, loanPurposeId, 
                collateralTypeId, collateralNotes, externalId, 
                repaymentScheduleIndependentOfCustomerMeeting, 
                recurringSchedule, accountFees);
        
        return IntegrationTestObjectMother.createClientLoan(createLoanAccount);
    }

    /*
     * This is a method taken from TestObjectFactory and modified to make it configurable. This could be pushed back up
     * to TestObjectFactory or better yet converted to a builder pattern.
     */
    private MeetingBO getNewMeeting(final RecurrenceType frequency, final short recurAfter,
            final MeetingType meetingType, final WeekDay weekday, Date theDate) {
        MeetingBO meeting;
        try {
            meeting = new MeetingBO(frequency, recurAfter, theDate, meetingType);
            meeting.setMeetingPlace("Loan Meeting Place");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(weekday);
        return meeting;
    }

    private DateTime initializeToFixedDateTime() {
        DateTime dateTime = new DateTime(2009, 12, 7, 0, 0, 0, 0);
        DateTimeService dateTimeService = new DateTimeService();
        dateTimeService.setCurrentDateTimeFixed(dateTime);
        return dateTime;
    }

    @Test
    public void testThatLoanInPendingApprovalStateDoesNotGenerateArrearsData() throws Exception {
        LoanBO loan = setUpLoan(dateTime, AccountState.LOAN_PENDING_APPROVAL);

        Assert.assertNull(ageLoanTenDaysAndGetLoanArrearsAgingEntity(loan));
    }

    @Test
    public void testThatLoanInActiveInBadStandingStateDoesGenerateArrearsData() throws Exception {
        LoanBO loan = setUpLoan(dateTime, AccountState.LOAN_ACTIVE_IN_BAD_STANDING);

        Assert.assertNotNull(ageLoanTenDaysAndGetLoanArrearsAgingEntity(loan));
    }

    private LoanArrearsAgingEntity ageLoanTenDaysAndGetLoanArrearsAgingEntity(LoanBO loan) throws BatchJobException,
            PersistenceException {
        int daysOverdue = 3;

        return ageLoanAndGetLoanArrearsAgingEntity(loan, repaymentInterval + daysOverdue);
    }

    private LoanArrearsAgingEntity ageLoanAndGetLoanArrearsAgingEntity(LoanBO loan, int daysToAgeLoan)
            throws BatchJobException, PersistenceException {
        Assert.assertNull(loan.getLoanArrearsAgingEntity());

        new DateTimeService().setCurrentDateTimeFixed(dateTime.plusDays(daysToAgeLoan));

        runLoanArrearsThenLoanArrearsAging();
        StaticHibernateUtil.flushAndClearSession();
        LoanBO reloadedLoan = legacyLoanDao.getAccount(loan.getAccountId());

        return reloadedLoan.getLoanArrearsAgingEntity();
    }

    // create a weekly 10 week loan of 100 with flat 100% interest (total interest is 20 or 2 each week)
    // advance the date by ten days, run the batch job
    // one weeks payment should be in arrears
    @Ignore
    @Test
    public void testLoanWithOneOverduePayment() throws Exception {
        LoanBO loan = setUpLoan(dateTime, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);

        LoanArrearsAgingEntity loanArrearsAgingEntity = ageLoanTenDaysAndGetLoanArrearsAgingEntity(loan);

        Assert.assertEquals(new Money(getCurrency(), "" + loanAmount), loanArrearsAgingEntity.getUnpaidPrincipal());
        Assert.assertEquals(new Money(getCurrency(), "" + totalInterest), loanArrearsAgingEntity.getUnpaidInterest());
        Assert.assertEquals(new Money(getCurrency(), "" + principalForOneInstallment), loanArrearsAgingEntity
                .getOverduePrincipal());
        Assert.assertEquals(new Money(getCurrency(), "" + interestForOneInstallment), loanArrearsAgingEntity
                .getOverdueInterest());
        Assert.assertEquals(new Money(getCurrency(), "" + (principalForOneInstallment + interestForOneInstallment)),
                loanArrearsAgingEntity.getOverdueBalance());

        Assert.assertEquals(Short.valueOf("3"), loanArrearsAgingEntity.getDaysInArrears());

        assertForLoanArrearsAgingEntity(loan);

    }

    @Test
    public void testLoanWithTwoOverduePayments() throws Exception {
        LoanBO loan = setUpLoan(dateTime, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);

        int daysOverdue = 10;
        LoanArrearsAgingEntity loanArrearsAgingEntity = ageLoanAndGetLoanArrearsAgingEntity(loan, repaymentInterval
                + daysOverdue);

        Assert.assertEquals(new Money(getCurrency(), "" + loanAmount), loanArrearsAgingEntity.getUnpaidPrincipal());
        Assert.assertEquals(new Money(getCurrency(), "" + totalInterest), loanArrearsAgingEntity.getUnpaidInterest());
        Assert.assertEquals(new Money(getCurrency(), "" + 2 * principalForOneInstallment), loanArrearsAgingEntity
                .getOverduePrincipal());
        Assert.assertEquals(new Money(getCurrency(), "" + 2 * interestForOneInstallment), loanArrearsAgingEntity
                .getOverdueInterest());
        Assert.assertEquals(new Money(getCurrency(), ""
                + (2 * (principalForOneInstallment + interestForOneInstallment))), loanArrearsAgingEntity
                .getOverdueBalance());

        Assert.assertEquals(daysOverdue, loanArrearsAgingEntity.getDaysInArrears().intValue());

    }

    /**
     * unsure why its failing.
     */
    @Ignore
    @Test
    public void testLoanWithOnePaymentMadeAndOneOverduePayments() throws Exception {
        LoanBO loan = setUpLoan(dateTime, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);

        Assert.assertNull(loan.getLoanArrearsAgingEntity());

        short daysOverdue = 3;
        // advance time daysOverdue past the second repayment interval
        dateTime = dateTime.plusDays(2 * repaymentInterval + daysOverdue);
        new DateTimeService().setCurrentDateTimeFixed(dateTime);

        // make one payment, so we should still be one payment behind after that
        PaymentData paymentData = PaymentData.createPaymentData(new Money(Configuration.getInstance().getSystemConfig()
                .getCurrency(), "" + onePayment), loan.getPersonnel(), Short.valueOf("1"), dateTime.toDate());
        IntegrationTestObjectMother.applyAccountPayment(loan, paymentData);
        runLoanArrearsThenLoanArrearsAging();
        StaticHibernateUtil.flushAndClearSession();

        loan = legacyLoanDao.getAccount(loan.getAccountId());

        Assert.assertNotNull(loan.getLoanArrearsAgingEntity());
        LoanArrearsAgingEntity loanArrearsAgingEntity = loan.getLoanArrearsAgingEntity();

        Assert.assertEquals(new Money(getCurrency(), "" + (loanAmount - principalForOneInstallment)),
                loanArrearsAgingEntity.getUnpaidPrincipal());
        Assert.assertEquals(new Money(getCurrency(), "" + (totalInterest - interestForOneInstallment)),
                loanArrearsAgingEntity.getUnpaidInterest());
        Assert.assertEquals(new Money(getCurrency(), "" + principalForOneInstallment), loanArrearsAgingEntity
                .getOverduePrincipal());
        Assert.assertEquals(new Money(getCurrency(), "" + interestForOneInstallment), loanArrearsAgingEntity
                .getOverdueInterest());
        Assert.assertEquals(new Money(getCurrency(), "" + (principalForOneInstallment + interestForOneInstallment)),
                loanArrearsAgingEntity.getOverdueBalance());

        Assert.assertEquals(Short.valueOf(daysOverdue), loanArrearsAgingEntity.getDaysInArrears());
    }

    @Ignore
    @Test
    public void testLoanWithOnePaymentMadeAndNoOverduePayments() throws Exception {
        LoanBO loan = setUpLoan(dateTime, AccountState.LOAN_ACTIVE_IN_BAD_STANDING);

        Assert.assertNull(loan.getLoanArrearsAgingEntity());

        short daysPastPaymentDate = 3;

        // advance time daysOverdue past the second repayment interval
        dateTime = dateTime.plusDays(repaymentInterval + daysPastPaymentDate);
        new DateTimeService().setCurrentDateTimeFixed(dateTime);

        runLoanArrearsThenLoanArrearsAging();

        loan = legacyLoanDao.getAccount(loan.getAccountId());

        Assert.assertNotNull(loan.getLoanArrearsAgingEntity());

        // make one payment, so we should still be one payment behind after that
        PaymentData paymentData = PaymentData.createPaymentData(new Money(Configuration.getInstance().getSystemConfig()
                .getCurrency(), "" + onePayment), loan.getPersonnel(), Short.valueOf("1"), dateTime.toDate());
        IntegrationTestObjectMother.applyAccountPayment(loan, paymentData);

        //jpwrunLoanArrearsThenLoanArrearsAging();

        loan = legacyLoanDao.getAccount(loan.getAccountId());

        Assert.assertNull(loan.getLoanArrearsAgingEntity());
        Assert.assertTrue(loan.getState().equals(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING));

    }

    @Ignore
    @Test
    public void testLoanWithEarlyRepayment() throws Exception {
        LoanBO loan = setUpLoan(dateTime, AccountState.LOAN_ACTIVE_IN_BAD_STANDING);

        Assert.assertNull(loan.getLoanArrearsAgingEntity());

        short daysPastPaymentDate = 3;

        // advance time daysOverdue past the second repayment interval
        dateTime = dateTime.plusDays(repaymentInterval + daysPastPaymentDate);
        new DateTimeService().setCurrentDateTimeFixed(dateTime);

        runLoanArrearsThenLoanArrearsAging();

        loan = legacyLoanDao.getAccount(loan.getAccountId());

        Assert.assertNotNull(loan.getLoanArrearsAgingEntity());

        Money totalAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(), "" + loanAmount
                + totalInterest);
        String receiptNumber = "1";
        loan.makeEarlyRepayment(totalAmount, receiptNumber, dateTime.toDate(), PaymentTypes.CASH.getValue().toString(),
                loan.getPersonnel().getPersonnelId(), false, new Money(loan.getCurrency(), "0"));

        //jpw runLoanArrearsThenLoanArrearsAging();

        loan = legacyLoanDao.getAccount(loan.getAccountId());

        Assert.assertNull(loan.getLoanArrearsAgingEntity());
        Assert.assertTrue(loan.getState().equals(AccountState.LOAN_CLOSED_OBLIGATIONS_MET));

    }

    @Ignore
    @Test
    public void testLoanWithEarlyRepaymentWithInterestWaiver() throws Exception {
        LoanBO loan = setUpLoan(dateTime, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);

        Assert.assertNull(loan.getLoanArrearsAgingEntity());

        short daysPastPaymentDate = 3;

        // advance time daysOverdue past the second repayment interval
        dateTime = dateTime.plusDays(repaymentInterval + daysPastPaymentDate);
        new DateTimeService().setCurrentDateTimeFixed(dateTime);

        runLoanArrearsThenLoanArrearsAging();
        StaticHibernateUtil.flushAndClearSession();
        loan = legacyLoanDao.getAccount(loan.getAccountId());

        Assert.assertNotNull(loan.getLoanArrearsAgingEntity());

        Money totalAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(), "" + loanAmount
                + 0);
        String receiptNumber = "1";
        loan.makeEarlyRepayment(totalAmount, receiptNumber, dateTime.toDate(), PaymentTypes.CASH.getValue().toString(),
                loan.getPersonnel().getPersonnelId(), true, new Money(loan.getCurrency(), "0"));

        //jpw runLoanArrearsThenLoanArrearsAging();

        loan = legacyLoanDao.getAccount(loan.getAccountId());

        Assert.assertNull(loan.getLoanArrearsAgingEntity());
        Assert.assertTrue(loan.getState().equals(AccountState.LOAN_CLOSED_OBLIGATIONS_MET));

    }

    /**
     * unsure why its failing.
     */
    @Ignore
    @Test
    public void testLoanWithOnePartialPayment() throws Exception {
        LoanBO loan = setUpLoan(dateTime, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);

        Assert.assertNull(loan.getLoanArrearsAgingEntity());

        short daysOverdue = 3;
        dateTime = dateTime.plusDays(repaymentInterval + daysOverdue);
        new DateTimeService().setCurrentDateTimeFixed(dateTime);

        // make one payment of 8. Interest is paid off first, so 2 goes to interest and 6 to principal
        int principalPayment = 6;
        int interestPayment = 2;
        int paymentAmount = interestPayment + principalPayment; // paymentAmount == 8
        PaymentData paymentData = PaymentData.createPaymentData(new Money(Configuration.getInstance().getSystemConfig()
                .getCurrency(), "" + paymentAmount), loan.getPersonnel(), Short.valueOf("1"), dateTime.toDate());
        IntegrationTestObjectMother.applyAccountPayment(loan, paymentData);

        runLoanArrearsThenLoanArrearsAging();
        StaticHibernateUtil.flushAndClearSession();
        loan = legacyLoanDao.getAccount(loan.getAccountId());

        Assert.assertNotNull(loan.getLoanArrearsAgingEntity());
        LoanArrearsAgingEntity loanArrearsAgingEntity = loan.getLoanArrearsAgingEntity();

        Assert.assertEquals(new Money(getCurrency(), "" + (loanAmount - principalPayment)), loanArrearsAgingEntity
                .getUnpaidPrincipal());
        Assert.assertEquals(new Money(getCurrency(), "" + (totalInterest - interestPayment)), loanArrearsAgingEntity
                .getUnpaidInterest());
        Assert.assertEquals(new Money(getCurrency(), "" + (principalForOneInstallment - principalPayment)),
                loanArrearsAgingEntity.getOverduePrincipal());
        Assert.assertEquals(new Money(getCurrency(), "0"), loanArrearsAgingEntity.getOverdueInterest());
        Assert.assertEquals(new Money(getCurrency(), "" + (principalForOneInstallment - principalPayment)),
                loanArrearsAgingEntity.getOverdueBalance());

        Assert.assertEquals(Short.valueOf(daysOverdue), loanArrearsAgingEntity.getDaysInArrears());

    }

    private void assertForLoanArrearsAgingEntity(LoanBO loan) throws PersistenceException {

        LoanBO loanAccount = IntegrationTestObjectMother.findLoanBySystemId(loan.getGlobalAccountNum());
        LoanArrearsAgingEntity loanArrearsAgingEntity = loanAccount.getLoanArrearsAgingEntity();

        Assert
                .assertEquals(loanAccount.getLoanSummary().getPrincipalDue(), loanArrearsAgingEntity
                        .getUnpaidPrincipal());
        Assert.assertEquals(loanAccount.getLoanSummary().getInterestDue(), loanArrearsAgingEntity.getUnpaidInterest());
        Assert.assertEquals(loanAccount.getLoanSummary().getPrincipalDue().add(
                loanAccount.getLoanSummary().getInterestDue()), loanArrearsAgingEntity.getUnpaidBalance());

        Assert.assertEquals(loanAccount.getTotalPrincipalAmountInArrears(), loanArrearsAgingEntity
                .getOverduePrincipal());
        Assert.assertEquals(loanAccount.getTotalInterestAmountInArrears(), loanArrearsAgingEntity.getOverdueInterest());
        Assert.assertEquals(loanAccount.getTotalPrincipalAmountInArrears().add(
                loanAccount.getTotalInterestAmountInArrears()), loanArrearsAgingEntity.getOverdueBalance());

        Assert.assertEquals(group.getCustomerId(), loanArrearsAgingEntity.getCustomer().getCustomerId());
        Assert.assertEquals(center.getCustomerId(), loanArrearsAgingEntity.getParentCustomer().getCustomerId());
        Assert.assertEquals(group.getDisplayName(), loanArrearsAgingEntity.getCustomerName());
    }

    private void setLatenessDaysToZero() throws Exception {
        StaticHibernateUtil.startTransaction();
        Query updateQuery = StaticHibernateUtil.getSessionTL().createQuery(
                "update ProductTypeEntity set latenessDays = 0 where productTypeID = 1");
        updateQuery.executeUpdate();
        StaticHibernateUtil.flushAndClearSession();
    }

    private void runLoanArrearsThenLoanArrearsAging() throws BatchJobException {
        loanArrearHelper.execute(dummy);
        loanArrearsAgingHelper.execute(dummy);
    }

}
