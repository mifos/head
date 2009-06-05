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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanArrearsAgingEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanBOIntegrationTest;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanArrearsAgingHelperIntegrationTest extends MifosIntegrationTest {
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
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, recurAfter,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(loanAccount1);
            TestObjectFactory.cleanUp(loanAccount2);
            TestObjectFactory.cleanUp(loanAccount3);
            TestObjectFactory.cleanUp(loanAccount4);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            // Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        loanArrearsAgingHelper = null;
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testExecute() throws Exception {
        loanAccount1 = getLoanAccount(group, meeting, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, "off1");
        loanAccount2 = getLoanAccount(group, meeting, AccountState.LOAN_ACTIVE_IN_BAD_STANDING, "off2");
        loanAccount3 = getLoanAccount(group, meeting, AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER, "off3");
        loanAccount4 = getLoanAccount(group, meeting, AccountState.LOAN_PENDING_APPROVAL, "off4");

        assertNull(loanAccount1.getLoanArrearsAgingEntity());
        assertNull(loanAccount2.getLoanArrearsAgingEntity());
        assertNull(loanAccount3.getLoanArrearsAgingEntity());
        assertNull(loanAccount4.getLoanArrearsAgingEntity());

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

        assertNotNull(loanAccount1.getLoanArrearsAgingEntity());
        assertNotNull(loanAccount2.getLoanArrearsAgingEntity());
        assertNull(loanAccount3.getLoanArrearsAgingEntity());
        assertNull(loanAccount4.getLoanArrearsAgingEntity());
        LoanArrearsAgingEntity entityAccount1 = loanAccount1.getLoanArrearsAgingEntity();
        LoanArrearsAgingEntity entityAccount2 = loanAccount2.getLoanArrearsAgingEntity();

        assertEquals(new Money("100"), entityAccount1.getOverduePrincipal());
        assertEquals(new Money("12"), entityAccount1.getOverdueInterest());
        assertEquals(new Money("112"), entityAccount1.getOverdueBalance());

        assertEquals(Short.valueOf("15"), entityAccount1.getDaysInArrears());

        assertEquals(new Money("300"), entityAccount2.getOverduePrincipal());
        assertEquals(new Money("36"), entityAccount2.getOverdueInterest());
        assertEquals(new Money("336"), entityAccount2.getOverdueBalance());

        assertEquals(Short.valueOf("22"), entityAccount2.getDaysInArrears());

        assertForLoanArrearsAgingEntity(loanAccount1);
        assertForLoanArrearsAgingEntity(loanAccount2);

        group = TestObjectFactory.getCustomer(group.getCustomerId());
        center = group.getParentCustomer();
    }

    private void assertForLoanArrearsAgingEntity(LoanBO loanAccount) {
        LoanArrearsAgingEntity loanArrearsAgingEntity = loanAccount.getLoanArrearsAgingEntity();

        assertEquals(loanAccount.getLoanSummary().getPrincipalDue(), loanArrearsAgingEntity.getUnpaidPrincipal());
        assertEquals(loanAccount.getLoanSummary().getInterestDue(), loanArrearsAgingEntity.getUnpaidInterest());
        assertEquals(loanAccount.getLoanSummary().getPrincipalDue().add(loanAccount.getLoanSummary().getInterestDue()),
                loanArrearsAgingEntity.getUnpaidBalance());

        assertEquals(loanAccount.getTotalPrincipalAmountInArrears(), loanArrearsAgingEntity.getOverduePrincipal());
        assertEquals(loanAccount.getTotalInterestAmountInArrears(), loanArrearsAgingEntity.getOverdueInterest());
        assertEquals(loanAccount.getTotalPrincipalAmountInArrears().add(loanAccount.getTotalInterestAmountInArrears()),
                loanArrearsAgingEntity.getOverdueBalance());

        assertEquals(group.getCustomerId(), loanArrearsAgingEntity.getCustomer().getCustomerId());
        assertEquals(center.getCustomerId(), loanArrearsAgingEntity.getParentCustomer().getCustomerId());
        assertEquals(group.getDisplayName(), loanArrearsAgingEntity.getCustomerName());
    }

    public void testClearArrears() throws Exception {
        loanAccount1 = getLoanAccount(group, meeting, AccountState.LOAN_ACTIVE_IN_BAD_STANDING, "off1");
        loanAccount2 = getLoanAccount(group, meeting, AccountState.LOAN_ACTIVE_IN_BAD_STANDING, "off2");
        loanAccount3 = getLoanAccount(group, meeting, AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER, "off3");
        loanAccount4 = getLoanAccount(group, meeting, AccountState.LOAN_PENDING_APPROVAL, "off4");

        assertNull(loanAccount1.getLoanArrearsAgingEntity());
        assertNull(loanAccount2.getLoanArrearsAgingEntity());
        assertNull(loanAccount3.getLoanArrearsAgingEntity());
        assertNull(loanAccount4.getLoanArrearsAgingEntity());

        setDisbursementDateAsOldDate(loanAccount1, 22, Short.valueOf("3"));
        loanAccount1.update();
        StaticHibernateUtil.commitTransaction();

        setDisbursementDateAsOldDate(loanAccount2, 22, Short.valueOf("3"));
        loanAccount2.update();
        StaticHibernateUtil.commitTransaction();

        loanArrearsAgingHelper.execute(System.currentTimeMillis());

        loanAccount1 = new LoanPersistence().getAccount(loanAccount1.getAccountId());
        loanAccount2 = new LoanPersistence().getAccount(loanAccount2.getAccountId());

        assertNotNull(loanAccount1.getLoanArrearsAgingEntity());
        assertNotNull(loanAccount2.getLoanArrearsAgingEntity());

        LoanArrearsAgingEntity entityAccount1 = loanAccount1.getLoanArrearsAgingEntity();
        LoanArrearsAgingEntity entityAccount2 = loanAccount2.getLoanArrearsAgingEntity();

        assertEquals(new Money("300"), entityAccount1.getOverduePrincipal());
        assertEquals(new Money("36"), entityAccount1.getOverdueInterest());
        assertEquals(new Money("336"), entityAccount1.getOverdueBalance());

        assertEquals(Short.valueOf("22"), entityAccount1.getDaysInArrears());

        assertEquals(new Money("300"), entityAccount2.getOverduePrincipal());
        assertEquals(new Money("36"), entityAccount2.getOverdueInterest());
        assertEquals(new Money("336"), entityAccount2.getOverdueBalance());

        assertEquals(Short.valueOf("22"), entityAccount2.getDaysInArrears());

        // apply a payment to loanAccount2 which should cover the overdue
        // balance and move it out of arrears

        final String RECEIPT_NUMBER = "001";
        loanAccount1.makeEarlyRepayment(new Money("636"), RECEIPT_NUMBER, new Date(System.currentTimeMillis()),
                PaymentTypes.CASH.getValue().toString(), PersonnelConstants.SYSTEM_USER);

        PaymentData paymentData2 = PaymentData.createPaymentData(new Money("636"), TestObjectFactory
                .getPersonnel(PersonnelConstants.SYSTEM_USER), PaymentTypes.CASH.getValue(), new Date(System
                .currentTimeMillis()));

        loanAccount2.applyPaymentWithPersist(paymentData2);

        StaticHibernateUtil.commitTransaction();

        loanArrearsAgingHelper.execute(System.currentTimeMillis());

        loanAccount1 = new LoanPersistence().getAccount(loanAccount1.getAccountId());
        loanAccount2 = new LoanPersistence().getAccount(loanAccount2.getAccountId());

        assertTrue(loanAccount1.getState().equals(AccountState.LOAN_CLOSED_OBLIGATIONS_MET));
        assertNull(loanAccount1.getLoanArrearsAgingEntity());

        assertTrue(loanAccount2.getState().equals(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING));
        assertNull(loanAccount2.getLoanArrearsAgingEntity());

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
        LoanBOIntegrationTest.modifyDisbursmentDate(loan, startDate);
        for (AccountActionDateEntity actionDate : loan.getAccountActionDates()) {
            if (actionDate.getInstallmentId().shortValue() <= installmentSize.shortValue())
                LoanBOIntegrationTest.setActionDate(actionDate, offSetGivenDate(actionDate.getActionDate(), days));
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
