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

package org.mifos.framework.components.batchjobs.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanArrearsAgingEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanBOTestUtils;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanArrearsAgingHelperIntegrationTest extends MifosIntegrationTestCase {
    public LoanArrearsAgingHelperIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private LoanArrearsAgingHelper loanArrearsAgingHelper;

    private CustomerBO center;

    private CustomerBO group;

    private MeetingBO meeting;

    private LoanBO loanAccount1;

    private LoanBO loanAccount2;

    private LoanBO loanAccount3;

    private LoanBO loanAccount4;

    private short recurAfter = 1;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LoanArrearsAgingTask loanArrearsAgingTask = new LoanArrearsAgingTask();
        loanArrearsAgingHelper = (LoanArrearsAgingHelper) loanArrearsAgingTask.getTaskHelper();
    }

    @Override
    protected void tearDown() throws Exception {
        new DateTimeService().resetToCurrentSystemDateTime();
        
        TestDatabase.resetMySQLDatabase();

        loanArrearsAgingHelper = null;
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }
    
    public static MeetingBO getNewMeeting(final RecurrenceType frequency, final short recurAfter, final MeetingType meetingType,
            final WeekDay weekday, Date theDate) {
        MeetingBO meeting;
        try {
            meeting = new MeetingBO(frequency, recurAfter, theDate, meetingType);
            meeting.setMeetingPlace("Loan Meeting Place");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        WeekDaysEntity weekDays = new WeekDaysEntity(weekday);
        meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(weekDays);
        return meeting;
    }    
    public static LoanBO createBasicLoanAccount(final CustomerBO customer, final AccountState state, final Date startDate,
            final LoanOfferingBO loanOffering, String loan_amount) {
        LoanBO loan;
        LoanOfferingInstallmentRange eligibleInstallmentRange = loanOffering.getEligibleInstallmentSameForAllLoan();
        UserContext userContext = TestUtils.makeUser();
        userContext.setLocaleId(null);
        List<FeeView> feeViewList = new ArrayList<FeeView>();
        MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer.getCustomerMeeting().getMeeting());
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 6);
        short numInstallments = 10;
        double interestRate = 100.0;
        try {
            loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering, customer, state, new Money(TestUtils.getCurrency(), loan_amount),
                    numInstallments, meetingDates.get(0), false, interestRate, (short) 0, new FundBO(), feeViewList, null,
                    Double.valueOf(loan_amount), Double.valueOf(loan_amount), eligibleInstallmentRange.getMaxNoOfInstall(),
                    eligibleInstallmentRange.getMinNoOfInstall(), false, null);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }

        return loan;
    }    
    
    // create a weekly 10 week loan of 100 with flat 100% interest (total interest is 20 or 2 each week)       
    // advance the date by a week, run the batch job        
    // one weeks payment should be in arrears
    public void testLoanWithOneOverduePayment() throws Exception {
        DateTime dateTime = new DateTime(2009,12,7,0,0,0,0);
        DateTimeService dateTimeService = new DateTimeService();
        dateTimeService.setCurrentDateTimeFixed(dateTime);

        LoanBO loan = setUpLoan(dateTime);

        Assert.assertNull(loan.getLoanArrearsAgingEntity());

        short repaymentInterval = 7; // 7 days for weekly loan repayment
        short daysOverdue = 3;
        
        // advance time daysOverdue past the repayment interval
        dateTimeService.setCurrentDateTimeFixed(dateTime.plusDays(repaymentInterval + daysOverdue));

        final long dummy = 0;
        loanArrearsAgingHelper.execute(dummy);

        loan = new LoanPersistence().getAccount(loan.getAccountId());

        Assert.assertNotNull(loan.getLoanArrearsAgingEntity());
        LoanArrearsAgingEntity loanArrearsAgingEntity = loan.getLoanArrearsAgingEntity();

        Assert.assertEquals(new Money(getCurrency(), "100"), loanArrearsAgingEntity.getUnpaidPrincipal());
        Assert.assertEquals(new Money(getCurrency(), "20"), loanArrearsAgingEntity.getUnpaidInterest());
        Assert.assertEquals(new Money(getCurrency(), "10"), loanArrearsAgingEntity.getOverduePrincipal());
        Assert.assertEquals(new Money(getCurrency(), "2"), loanArrearsAgingEntity.getOverdueInterest());
        Assert.assertEquals(new Money(getCurrency(), "12"), loanArrearsAgingEntity.getOverdueBalance());

        Assert.assertEquals(Short.valueOf(daysOverdue), loanArrearsAgingEntity.getDaysInArrears());
        
        assertForLoanArrearsAgingEntity(loan);

    }

    public void testLoanWithTwoOverduePayments() throws Exception {
        DateTime dateTime = new DateTime(2009,12,7,0,0,0,0);
        DateTimeService dateTimeService = new DateTimeService();
        dateTimeService.setCurrentDateTimeFixed(dateTime);

        LoanBO loan = setUpLoan(dateTime);

        Assert.assertNull(loan.getLoanArrearsAgingEntity());

        short repaymentInterval = 7; // 7 days for weekly loan repayment
        short daysOverdue = 10;
        
        // advance time daysOverdue past the repayment interval
        dateTimeService.setCurrentDateTimeFixed(dateTime.plusDays(repaymentInterval + daysOverdue));

        final long dummy = 0;
        loanArrearsAgingHelper.execute(dummy);

        loan = new LoanPersistence().getAccount(loan.getAccountId());

        Assert.assertNotNull(loan.getLoanArrearsAgingEntity());
        LoanArrearsAgingEntity loanArrearsAgingEntity = loan.getLoanArrearsAgingEntity();

        Assert.assertEquals(new Money(getCurrency(), "100"), loanArrearsAgingEntity.getUnpaidPrincipal());
        Assert.assertEquals(new Money(getCurrency(), "20"), loanArrearsAgingEntity.getUnpaidInterest());
        Assert.assertEquals(new Money(getCurrency(), "20"), loanArrearsAgingEntity.getOverduePrincipal());
        Assert.assertEquals(new Money(getCurrency(), "4"), loanArrearsAgingEntity.getOverdueInterest());
        Assert.assertEquals(new Money(getCurrency(), "24"), loanArrearsAgingEntity.getOverdueBalance());

        Assert.assertEquals(Short.valueOf(daysOverdue), loanArrearsAgingEntity.getDaysInArrears());

    }

    public void testLoanWithOnePaymentMadeAndOneOverduePayments() throws Exception {
        DateTime dateTime = new DateTime(2009,12,7,0,0,0,0);
        DateTimeService dateTimeService = new DateTimeService();
        dateTimeService.setCurrentDateTimeFixed(dateTime);

        LoanBO loan = setUpLoan(dateTime);

        Assert.assertNull(loan.getLoanArrearsAgingEntity());

        short repaymentInterval = 7; // 7 days for weekly loan repayment
        short daysOverdue = 3;
        
        // advance time daysOverdue past the second repayment interval
        dateTime = dateTime.plusDays(2*repaymentInterval + daysOverdue);
        dateTimeService.setCurrentDateTimeFixed(dateTime);

        // make one payment, so we should still be one payment behind after that
        PaymentData paymentData = PaymentData.createPaymentData(new Money(Configuration.getInstance().getSystemConfig()
                .getCurrency(), "12.0"), loan.getPersonnel(), Short.valueOf("1"), dateTime.toDate());
        loan.applyPaymentWithPersist(paymentData);
        
        final long dummy = 0;
        loanArrearsAgingHelper.execute(dummy);

        loan = new LoanPersistence().getAccount(loan.getAccountId());

        Assert.assertNotNull(loan.getLoanArrearsAgingEntity());
        LoanArrearsAgingEntity loanArrearsAgingEntity = loan.getLoanArrearsAgingEntity();

        Assert.assertEquals(new Money(getCurrency(), "90"), loanArrearsAgingEntity.getUnpaidPrincipal());
        Assert.assertEquals(new Money(getCurrency(), "18"), loanArrearsAgingEntity.getUnpaidInterest());
        Assert.assertEquals(new Money(getCurrency(), "10"), loanArrearsAgingEntity.getOverduePrincipal());
        Assert.assertEquals(new Money(getCurrency(), "2"), loanArrearsAgingEntity.getOverdueInterest());
        Assert.assertEquals(new Money(getCurrency(), "12"), loanArrearsAgingEntity.getOverdueBalance());

        Assert.assertEquals(Short.valueOf(daysOverdue), loanArrearsAgingEntity.getDaysInArrears());
        
    }

    public void testLoanWithOnePartialPayment() throws Exception {
        DateTime dateTime = new DateTime(2009,12,7,0,0,0,0);
        DateTimeService dateTimeService = new DateTimeService();
        dateTimeService.setCurrentDateTimeFixed(dateTime);

        LoanBO loan = setUpLoan(dateTime);

        Assert.assertNull(loan.getLoanArrearsAgingEntity());

        short repaymentInterval = 7; // 7 days for weekly loan repayment
        short daysOverdue = 3;
        
        dateTime = dateTime.plusDays(repaymentInterval + daysOverdue);
        dateTimeService.setCurrentDateTimeFixed(dateTime);

        PaymentData paymentData = PaymentData.createPaymentData(new Money(Configuration.getInstance().getSystemConfig()
                .getCurrency(), "8.0"), loan.getPersonnel(), Short.valueOf("1"), dateTime.toDate());
        loan.applyPaymentWithPersist(paymentData);
        
        final long dummy = 0;
        loanArrearsAgingHelper.execute(dummy);

        loan = new LoanPersistence().getAccount(loan.getAccountId());

        Assert.assertNotNull(loan.getLoanArrearsAgingEntity());
        LoanArrearsAgingEntity loanArrearsAgingEntity = loan.getLoanArrearsAgingEntity();

        Assert.assertEquals(new Money(getCurrency(), "94"), loanArrearsAgingEntity.getUnpaidPrincipal());
        Assert.assertEquals(new Money(getCurrency(), "18"), loanArrearsAgingEntity.getUnpaidInterest());
        Assert.assertEquals(new Money(getCurrency(), "4"), loanArrearsAgingEntity.getOverduePrincipal());
        Assert.assertEquals(new Money(getCurrency(), "0"), loanArrearsAgingEntity.getOverdueInterest());
        Assert.assertEquals(new Money(getCurrency(), "4"), loanArrearsAgingEntity.getOverdueBalance());

        Assert.assertEquals(Short.valueOf(daysOverdue), loanArrearsAgingEntity.getDaysInArrears());

    }
      

    
    private LoanBO setUpLoan(DateTime dateTime) {
        Date startDate = dateTime.toDate();
        meeting = getNewMeeting(WEEKLY, recurAfter, CUSTOMER_MEETING, WeekDay.MONDAY, startDate);
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);

        LoanOfferingBO product = TestObjectFactory.createLoanOffering("A", "A", startDate, meeting);
        LoanBO loan = createBasicLoanAccount(group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, product, "100");
        try {
            loan.save();
        } catch (AccountException e) {
            throw new RuntimeException(e);
        }
        StaticHibernateUtil.commitTransaction();
        return loan;
    }
    
    public void xtestExecute() throws Exception {
        loanAccount1 = getLoanAccount(group, meeting, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, "off1");
        loanAccount2 = getLoanAccount(group, meeting, AccountState.LOAN_ACTIVE_IN_BAD_STANDING, "off2");
        loanAccount3 = getLoanAccount(group, meeting, AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER, "off3");
        loanAccount4 = getLoanAccount(group, meeting, AccountState.LOAN_PENDING_APPROVAL, "off4");

        Assert.assertNull(loanAccount1.getLoanArrearsAgingEntity());
        Assert.assertNull(loanAccount2.getLoanArrearsAgingEntity());
        Assert.assertNull(loanAccount3.getLoanArrearsAgingEntity());
        Assert.assertNull(loanAccount4.getLoanArrearsAgingEntity());

        setDisbursementDateAsOldDate(loanAccount1, 15, Short.valueOf("1"));
        loanAccount1.update();
        StaticHibernateUtil.commitTransaction();

        setDisbursementDateAsOldDate(loanAccount2, 22, Short.valueOf("3"));
        loanAccount2.update();
        StaticHibernateUtil.commitTransaction();

        setDisbursementDateAsOldDate(loanAccount3, 15, Short.valueOf("1"));
        loanAccount3.update();
        StaticHibernateUtil.commitTransaction();

        setDisbursementDateAsOldDate(loanAccount4, 22, Short.valueOf("1"));
        loanAccount4.update();
        StaticHibernateUtil.commitTransaction();

        loanArrearsAgingHelper.execute(System.currentTimeMillis());

        loanAccount1 = new LoanPersistence().getAccount(loanAccount1.getAccountId());
        loanAccount2 = new LoanPersistence().getAccount(loanAccount2.getAccountId());
        loanAccount3 = new LoanPersistence().getAccount(loanAccount3.getAccountId());
        loanAccount4 = new LoanPersistence().getAccount(loanAccount4.getAccountId());

        Assert.assertNotNull(loanAccount1.getLoanArrearsAgingEntity());
        Assert.assertNotNull(loanAccount2.getLoanArrearsAgingEntity());
        Assert.assertNull(loanAccount3.getLoanArrearsAgingEntity());
        Assert.assertNull(loanAccount4.getLoanArrearsAgingEntity());
        LoanArrearsAgingEntity entityAccount1 = loanAccount1.getLoanArrearsAgingEntity();
        LoanArrearsAgingEntity entityAccount2 = loanAccount2.getLoanArrearsAgingEntity();

       Assert.assertEquals(new Money(getCurrency(), "100"), entityAccount1.getOverduePrincipal());
       Assert.assertEquals(new Money(getCurrency(), "12"), entityAccount1.getOverdueInterest());
       Assert.assertEquals(new Money(getCurrency(), "112"), entityAccount1.getOverdueBalance());

       Assert.assertEquals(Short.valueOf("15"), entityAccount1.getDaysInArrears());

       Assert.assertEquals(new Money(getCurrency(), "300"), entityAccount2.getOverduePrincipal());
       Assert.assertEquals(new Money(getCurrency(), "36"), entityAccount2.getOverdueInterest());
       Assert.assertEquals(new Money(getCurrency(), "336"), entityAccount2.getOverdueBalance());

       Assert.assertEquals(Short.valueOf("22"), entityAccount2.getDaysInArrears());

        assertForLoanArrearsAgingEntity(loanAccount1);
        assertForLoanArrearsAgingEntity(loanAccount2);

        group = TestObjectFactory.getCustomer(group.getCustomerId());
        center = group.getParentCustomer();
    }

    private void assertForLoanArrearsAgingEntity(LoanBO loanAccount) {
        LoanArrearsAgingEntity loanArrearsAgingEntity = loanAccount.getLoanArrearsAgingEntity();

       Assert.assertEquals(loanAccount.getLoanSummary().getPrincipalDue(), loanArrearsAgingEntity.getUnpaidPrincipal());
       Assert.assertEquals(loanAccount.getLoanSummary().getInterestDue(), loanArrearsAgingEntity.getUnpaidInterest());
       Assert.assertEquals(loanAccount.getLoanSummary().getPrincipalDue().add(loanAccount.getLoanSummary().getInterestDue()),
                loanArrearsAgingEntity.getUnpaidBalance());

       Assert.assertEquals(loanAccount.getTotalPrincipalAmountInArrears(), loanArrearsAgingEntity.getOverduePrincipal());
       Assert.assertEquals(loanAccount.getTotalInterestAmountInArrears(), loanArrearsAgingEntity.getOverdueInterest());
       Assert.assertEquals(loanAccount.getTotalPrincipalAmountInArrears().add(loanAccount.getTotalInterestAmountInArrears()),
                loanArrearsAgingEntity.getOverdueBalance());

       Assert.assertEquals(group.getCustomerId(), loanArrearsAgingEntity.getCustomer().getCustomerId());
       Assert.assertEquals(center.getCustomerId(), loanArrearsAgingEntity.getParentCustomer().getCustomerId());
       Assert.assertEquals(group.getDisplayName(), loanArrearsAgingEntity.getCustomerName());
    }

    public void xtestClearArrears() throws Exception {
        loanAccount1 = getLoanAccount(group, meeting, AccountState.LOAN_ACTIVE_IN_BAD_STANDING, "off1");
        loanAccount2 = getLoanAccount(group, meeting, AccountState.LOAN_ACTIVE_IN_BAD_STANDING, "off2");
        loanAccount3 = getLoanAccount(group, meeting, AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER, "off3");
        loanAccount4 = getLoanAccount(group, meeting, AccountState.LOAN_PENDING_APPROVAL, "off4");

        Assert.assertNull(loanAccount1.getLoanArrearsAgingEntity());
        Assert.assertNull(loanAccount2.getLoanArrearsAgingEntity());
        Assert.assertNull(loanAccount3.getLoanArrearsAgingEntity());
        Assert.assertNull(loanAccount4.getLoanArrearsAgingEntity());

        setDisbursementDateAsOldDate(loanAccount1, 22, Short.valueOf("3"));
        loanAccount1.update();
        StaticHibernateUtil.commitTransaction();

        setDisbursementDateAsOldDate(loanAccount2, 22, Short.valueOf("3"));
        loanAccount2.update();
        StaticHibernateUtil.commitTransaction();

        loanArrearsAgingHelper.execute(System.currentTimeMillis());

        loanAccount1 = new LoanPersistence().getAccount(loanAccount1.getAccountId());
        loanAccount2 = new LoanPersistence().getAccount(loanAccount2.getAccountId());

        Assert.assertNotNull(loanAccount1.getLoanArrearsAgingEntity());
        Assert.assertNotNull(loanAccount2.getLoanArrearsAgingEntity());

        LoanArrearsAgingEntity entityAccount1 = loanAccount1.getLoanArrearsAgingEntity();
        LoanArrearsAgingEntity entityAccount2 = loanAccount2.getLoanArrearsAgingEntity();

       Assert.assertEquals(new Money(getCurrency(), "300"), entityAccount1.getOverduePrincipal());
       Assert.assertEquals(new Money(getCurrency(), "36"), entityAccount1.getOverdueInterest());
       Assert.assertEquals(new Money(getCurrency(), "336"), entityAccount1.getOverdueBalance());

       Assert.assertEquals(Short.valueOf("22"), entityAccount1.getDaysInArrears());

       Assert.assertEquals(new Money(getCurrency(), "300"), entityAccount2.getOverduePrincipal());
       Assert.assertEquals(new Money(getCurrency(), "36"), entityAccount2.getOverdueInterest());
       Assert.assertEquals(new Money(getCurrency(), "336"), entityAccount2.getOverdueBalance());

       Assert.assertEquals(Short.valueOf("22"), entityAccount2.getDaysInArrears());

        // apply a payment to loanAccount2 which should cover the overdue
        // balance and move it out of arrears

        final String RECEIPT_NUMBER = "001";
        loanAccount1.makeEarlyRepayment(new Money(getCurrency(), "636"), RECEIPT_NUMBER, new Date(System.currentTimeMillis()),
                PaymentTypes.CASH.getValue().toString(), PersonnelConstants.SYSTEM_USER);

        PaymentData paymentData2 = PaymentData.createPaymentData(new Money(getCurrency(), "636"), TestObjectFactory
                .getPersonnel(PersonnelConstants.SYSTEM_USER), PaymentTypes.CASH.getValue(), new Date(System
                .currentTimeMillis()));

        loanAccount2.applyPaymentWithPersist(paymentData2);

        StaticHibernateUtil.commitTransaction();

        loanArrearsAgingHelper.execute(System.currentTimeMillis());

        loanAccount1 = new LoanPersistence().getAccount(loanAccount1.getAccountId());
        loanAccount2 = new LoanPersistence().getAccount(loanAccount2.getAccountId());

       Assert.assertTrue(loanAccount1.getState().equals(AccountState.LOAN_CLOSED_OBLIGATIONS_MET));
        Assert.assertNull(loanAccount1.getLoanArrearsAgingEntity());

       Assert.assertTrue(loanAccount2.getState().equals(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING));
        Assert.assertNull(loanAccount2.getLoanArrearsAgingEntity());

        group = TestObjectFactory.getCustomer(group.getCustomerId());
        center = group.getParentCustomer();
    }

    private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting, AccountState accountState, String offName)
            throws AccountException {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO product = TestObjectFactory.createLoanOffering(offName, offName, startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", customer, accountState, startDate, product);
    }

    private void setDisbursementDateAsOldDate(LoanBO account, int days, Short installmentSize) {
        Date startDate = offSetCurrentDate(days);
        LoanBO loan = account;
        LoanBOTestUtils.modifyDisbursmentDate(loan, startDate);
        for (AccountActionDateEntity actionDate : loan.getAccountActionDates()) {
            if (actionDate.getInstallmentId().shortValue() <= installmentSize.shortValue())
                LoanBOTestUtils.setActionDate(actionDate, offSetGivenDate(actionDate.getActionDate(), days));
        }
    }

    private java.sql.Date offSetCurrentDate(int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }

    private java.sql.Date offSetGivenDate(Date date, int numberOfDays) {
        Calendar dateCalendar = new GregorianCalendar();
        dateCalendar.setTimeInMillis(date.getTime());
        int year = dateCalendar.get(Calendar.YEAR);
        int month = dateCalendar.get(Calendar.MONTH);
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        dateCalendar = new GregorianCalendar(year, month, day - numberOfDays);
        return new java.sql.Date(dateCalendar.getTimeInMillis());
    }
}
