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

package org.mifos.application.accounts.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mifos.application.accounts.AccountIntegrationTestCase;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.config.AccountingRules;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.testng.annotations.Test;

@Test(groups = { "integration" }, dependsOnGroups = { "productMixTestSuite" })
public class AccountBOIntegrationTest extends AccountIntegrationTestCase {

    public AccountBOIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final double DELTA = 0.00000001;

    /**
     * The name of this test, and some now-gone (and broken) exception-catching
     * code, make it look like it was supposed to test failure. But it doesn't
     * (and I don't see a corresponding success test).
     */
    @Test
    public void testFailureRemoveFees() throws Exception {
        StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        UserContext uc = TestUtils.makeUser();
        Set<AccountFeesEntity> accountFeesEntitySet = accountBO.getAccountFees();
        for (AccountFeesEntity accountFeesEntity : accountFeesEntitySet) {
            accountBO.removeFees(accountFeesEntity.getFees().getFeeId(), uc.getId());
        }
        StaticHibernateUtil.getTransaction().commit();
    }

    @Test(dependsOnMethods = { "testFailureRemoveFees" })
    public void testSuccessGetLastPmntAmntToBeAdjusted() throws Exception {

        LoanBO loan = accountBO;

        Date currentDate = new Date(System.currentTimeMillis());
        Date startDate = new Date(System.currentTimeMillis());
        disburseLoan(loan, startDate);

        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.addAll(loan.getAccountActionDates());
        PaymentData firstPaymentData = TestObjectFactory.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
                .getMoneyForMFICurrency(88), null, loan.getPersonnel(), "receiptNum", Short.valueOf("1"), currentDate,
                currentDate);
        loan.applyPaymentWithPersist(firstPaymentData);

        TestObjectFactory.updateObject(loan);
        TestObjectFactory.flushandCloseSession();
        // the loan has to be reloaded from db so that the payment list will be
        // in desc order and the
        // last payment will be the first in the payment list
        Session session = StaticHibernateUtil.getSessionTL();
        loan = (LoanBO) session.get(LoanBO.class, loan.getAccountId());

        Assert.assertEquals(88.0, loan.getLastPmntAmntToBeAdjusted(), DELTA);
        accountBO = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
    }

    /**
     * FIXME: - keithw - This test has started failing with a nullpointer down
     * the chain due to loanTrxn when loan.adjustLastPayment is invoked..
     */
    // @Test(dependsOnMethods = {"testSuccessGetLastPmntAmntToBeAdjusted"})
    // public void ignore_testSuccessAdjustLastPayment() throws Exception {
    // LoanBO loan = accountBO;
    // List<AccountActionDateEntity> accntActionDates = new
    // ArrayList<AccountActionDateEntity>();
    // accntActionDates.addAll(loan.getAccountActionDates());
    // Date currentDate = new Date(System.currentTimeMillis());
    // PaymentData firstPaymentData =
    // TestObjectFactory.getLoanAccountPaymentData(accntActionDates,
    // TestObjectFactory
    // .getMoneyForMFICurrency(700), null, loan.getPersonnel(), "receiptNum",
    // Short.valueOf("1"), currentDate,
    // currentDate);
    // loan.applyPaymentWithPersist(firstPaymentData);
    //
    // TestObjectFactory.updateObject(loan);
    // TestObjectFactory.flushandCloseSession();
    //
    // PaymentData secondPaymentData =
    // TestObjectFactory.getLoanAccountPaymentData(accntActionDates,
    // TestObjectFactory
    // .getMoneyForMFICurrency(100), null, loan.getPersonnel(), "receiptNum",
    // Short.valueOf("1"), currentDate,
    // currentDate);
    // loan.applyPaymentWithPersist(secondPaymentData);
    // TestObjectFactory.updateObject(loan);
    // TestObjectFactory.flushandCloseSession();
    // loan.setUserContext(TestUtils.makeUser());
    // try {
    // loan.adjustLastPayment("Payment with amount 100 has been adjusted by test code");
    // } catch (AccountException e) {
    //
    // Assert.assertEquals("exception.accounts.ApplicationException.CannotAdjust",
    // e.getKey());
    // }
    // TestObjectFactory.updateObject(loan);
    // /*
    // *Assert.assertEquals("The amount returned should have been 0.0", 0.0,
    // * loan.getLastPmntAmntToBeAdjusted());
    // */
    // try {
    // loan.adjustLastPayment("Payment with amount 700 has been adjusted by test code");
    //
    // } catch (AccountException e) {
    //
    // Assert.assertEquals("exception.accounts.ApplicationException.CannotAdjust",
    // e.getKey());
    // }
    // TestObjectFactory.updateObject(loan);
    // /*
    // *Assert.assertEquals("The amount returned should have been : 0.0", 0.0,
    // * loan.getLastPmntAmntToBeAdjusted());
    // */
    //
    // accountBO = TestObjectFactory.getObject(LoanBO.class,
    // loan.getAccountId());
    // }
    @Test(dependsOnMethods = { "testSuccessGetLastPmntAmntToBeAdjusted" })
    public void testSuccessUpdateAccountActionDateEntity() {
        List<Short> installmentIdList;
        installmentIdList = getApplicableInstallmentIdsForRemoveFees(accountBO);
        Set<AccountFeesEntity> accountFeesEntitySet = accountBO.getAccountFees();
        Iterator<AccountFeesEntity> itr = accountFeesEntitySet.iterator();
        while (itr.hasNext()) {
            accountBO.updateAccountActionDateEntity(installmentIdList, itr.next().getFees().getFeeId());
        }
        // TODO: assert what?
    }

    @Test(dependsOnMethods = { "testSuccessUpdateAccountActionDateEntity" })
    public void testSuccessUpdateAccountFeesEntity() {
        Set<AccountFeesEntity> accountFeesEntitySet = accountBO.getAccountFees();
        Assert.assertEquals(1, accountFeesEntitySet.size());
        Iterator<AccountFeesEntity> itr = accountFeesEntitySet.iterator();
        while (itr.hasNext()) {
            AccountFeesEntity accountFeesEntity = itr.next();
            accountBO.updateAccountFeesEntity(accountFeesEntity.getFees().getFeeId());
            Assert.assertFalse(accountFeesEntity.isActive());
        }
    }

    @Test(dependsOnMethods = { "testSuccessUpdateAccountFeesEntity" })
    public void testGetLastLoanPmntAmnt() throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());
        LoanBO loan = accountBO;
        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.addAll(loan.getAccountActionDates());
        PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
                .getMoneyForMFICurrency(700), null, loan.getPersonnel(), "receiptNum", Short.valueOf("1"), currentDate,
                currentDate);
        loan.applyPaymentWithPersist(paymentData);

        TestObjectFactory.updateObject(loan);
        TestObjectFactory.flushandCloseSession();
        Assert.assertEquals("The amount returned for the payment should have been 1272", 700.0, loan.getLastPmntAmnt());
        accountBO = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
    }

    @Test(dependsOnMethods = { "testGetLastLoanPmntAmnt" })
    public void testLoanAdjustment() throws Exception {
        StaticHibernateUtil.closeSession();
        Date currentDate = new Date(System.currentTimeMillis());
        LoanBO loan = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());
        loan.setUserContext(TestUtils.makeUser());
        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.add(loan.getAccountActionDate(Short.valueOf("1")));
        PaymentData accountPaymentDataView = TestObjectFactory.getLoanAccountPaymentData(accntActionDates,
                TestObjectFactory.getMoneyForMFICurrency(216), null, loan.getPersonnel(), "receiptNum", Short
                        .valueOf("1"), currentDate, currentDate);
        loan.applyPaymentWithPersist(accountPaymentDataView);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        loan.setUserContext(TestUtils.makeUser());
        loan.applyPaymentWithPersist(TestObjectFactory.getLoanAccountPaymentData(null, TestObjectFactory
                .getMoneyForMFICurrency(600), null, loan.getPersonnel(), "receiptNum", Short.valueOf("1"), currentDate,
                currentDate));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        loan.setUserContext(TestUtils.makeUser());
        loan.adjustPmnt("loan account has been adjusted by test code");
        TestObjectFactory.updateObject(loan);
        Assert.assertEquals("The amount returned for the payment should have been 0", 0.0, loan.getLastPmntAmnt());
        LoanTrxnDetailEntity lastLoanTrxn = null;
        for (AccountTrxnEntity accntTrxn : loan.getLastPmnt().getAccountTrxns()) {
            lastLoanTrxn = (LoanTrxnDetailEntity) accntTrxn;
            break;
        }
        AccountActionDateEntity installment = loan.getAccountActionDate(lastLoanTrxn.getInstallmentId());
        Assert.assertFalse("The installment adjusted should now be marked unpaid(due).", installment.isPaid());
        accountBO = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
    }

    @Test(dependsOnMethods = { "testLoanAdjustment" })
    public void testAdjustmentForClosedAccnt() throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());
        LoanBO loan = accountBO;

        loan.setAccountState(new AccountStateEntity(AccountState.LOAN_CLOSED_OBLIGATIONS_MET));
        loan.setUserContext(TestUtils.makeUser());
        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.addAll(loan.getAccountActionDates());
        PaymentData accountPaymentDataView = TestObjectFactory.getLoanAccountPaymentData(accntActionDates,
                TestObjectFactory.getMoneyForMFICurrency(712), null, loan.getPersonnel(), "receiptNum", Short
                        .valueOf("1"), currentDate, currentDate);
        loan.applyPaymentWithPersist(accountPaymentDataView);

        TestObjectFactory.updateObject(loan);
        try {
            loan.adjustPmnt("loan account has been adjusted by test code");
            Assert.fail();
        } catch (AccountException e) {
            Assert.assertEquals("exception.accounts.ApplicationException.CannotAdjust", e.getKey());
        }
    }

    @Test(dependsOnMethods = { "testAdjustmentForClosedAccnt" })
    public void testRetrievalOfNullMonetaryValue() throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());
        LoanBO loan = accountBO;
        loan.setUserContext(TestUtils.makeUser());
        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.addAll(loan.getAccountActionDates());
        PaymentData accountPaymentDataView = TestObjectFactory.getLoanAccountPaymentData(accntActionDates,
                TestObjectFactory.getMoneyForMFICurrency(0), null, loan.getPersonnel(), "receiptNum", Short
                        .valueOf("1"), currentDate, currentDate);

        loan.applyPaymentWithPersist(accountPaymentDataView);
        TestObjectFactory.updateObject(loan);
        TestObjectFactory.flushandCloseSession();
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());

        Set<AccountPaymentEntity> payments = loan.getAccountPayments();
        Assert.assertEquals(1, payments.size());
        AccountPaymentEntity accntPmnt = payments.iterator().next();
        TestObjectFactory.flushandCloseSession();

        Assert.assertEquals("Account payment retrieved should be zero with currency MFI currency", TestObjectFactory
                .getMoneyForMFICurrency(0), accntPmnt.getAmount());
    }

    @Test(dependsOnMethods = { "testRetrievalOfNullMonetaryValue" })
    public void testGetTransactionHistoryView() throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());
        LoanBO loan = accountBO;
        loan.setUserContext(TestUtils.makeUser());
        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.addAll(loan.getAccountActionDates());
        PaymentData accountPaymentDataView = TestObjectFactory.getLoanAccountPaymentData(accntActionDates,
                TestObjectFactory.getMoneyForMFICurrency(100), null, loan.getPersonnel(), "receiptNum", Short
                        .valueOf("1"), currentDate, currentDate);
        loan.applyPaymentWithPersist(accountPaymentDataView);
        TestObjectFactory.flushandCloseSession();
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        List<Integer> ids = new ArrayList<Integer>();
        for (AccountPaymentEntity accountPaymentEntity : loan.getAccountPayments()) {
            for (AccountTrxnEntity accountTrxnEntity : accountPaymentEntity.getAccountTrxns()) {
                for (FinancialTransactionBO financialTransactionBO : accountTrxnEntity.getFinancialTransactions()) {
                    ids.add(financialTransactionBO.getTrxnId());
                }
            }
        }
        loan.setUserContext(TestUtils.makeUser());
        List<TransactionHistoryView> trxnHistlist = loan.getTransactionHistoryView();
        Assert.assertNotNull("Account TrxnHistoryView list object should not be null", trxnHistlist);
        Assert.assertTrue("Account TrxnHistoryView list object Size should be greater than zero",
                trxnHistlist.size() > 0);
        Assert.assertEquals(ids.size(), trxnHistlist.size());
        int i = 0;
        for (TransactionHistoryView transactionHistoryView : trxnHistlist) {
            Assert.assertEquals(ids.get(i), transactionHistoryView.getAccountTrxnId());
            i++;
        }
        TestObjectFactory.flushandCloseSession();
        accountBO = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
    }

    @Test(dependsOnMethods = { "testGetTransactionHistoryView" })
    public void testGetTransactionHistoryViewByOtherUser() throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());
        LoanBO loan = accountBO;
        loan.setUserContext(TestUtils.makeUser());
        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.addAll(loan.getAccountActionDates());
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(Short.valueOf("2"));
        PaymentData accountPaymentDataView = TestObjectFactory.getLoanAccountPaymentData(accntActionDates,
                TestObjectFactory.getMoneyForMFICurrency(100), null, personnel, "receiptNum", Short.valueOf("1"),
                currentDate, currentDate);
        loan.applyPaymentWithPersist(accountPaymentDataView);
        TestObjectFactory.flushandCloseSession();
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        loan.setUserContext(TestUtils.makeUser());
        List<TransactionHistoryView> trxnHistlist = loan.getTransactionHistoryView();
        Assert.assertNotNull("Account TrxnHistoryView list object should not be null", trxnHistlist);
        Assert.assertTrue("Account TrxnHistoryView list object Size should be greater than zero",
                trxnHistlist.size() > 0);
        for (TransactionHistoryView transactionHistoryView : trxnHistlist) {
            Assert.assertEquals(transactionHistoryView.getPostedBy(), personnel.getDisplayName());
        }
        TestObjectFactory.flushandCloseSession();
        accountBO = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
    }

    @Test(dependsOnMethods = { "testGetTransactionHistoryViewByOtherUser" })
    public void testGetPeriodicFeeList() throws PersistenceException {
        FeeBO oneTimeFee = TestObjectFactory.createOneTimeAmountFee("One Time Fee", FeeCategory.LOAN, "20",
                FeePayment.TIME_OF_DISBURSMENT);
        AccountFeesEntity accountOneTimeFee = new AccountFeesEntity(accountBO, oneTimeFee, new Double("1.0"));
        accountBO.addAccountFees(accountOneTimeFee);
        accountPersistence.createOrUpdate(accountBO);
        TestObjectFactory.flushandCloseSession();
        accountBO = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());
        Assert.assertEquals(1, accountBO.getPeriodicFeeList().size());
    }

    @Test(dependsOnMethods = { "testGetPeriodicFeeList" })
    public void testIsTrxnDateValid() throws Exception {

        Calendar calendar = new GregorianCalendar();
        // Added by rajender on 24th July as test case was not passing
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        java.util.Date trxnDate = new Date(calendar.getTimeInMillis());
        if (AccountingRules.isBackDatedTxnAllowed()) {
            Assert.assertTrue(accountBO.isTrxnDateValid(trxnDate));
        } else {
            Assert.assertFalse(accountBO.isTrxnDateValid(trxnDate));
        }

    }

    @Test(dependsOnMethods = { "testIsTrxnDateValid" })
    public void testHandleChangeInMeetingSchedule() throws ApplicationException, SystemException {
        TestObjectFactory.flushandCloseSession();
        // center initially set up with meeting today
        center = TestObjectFactory.getCenter(center.getCustomerId());
        accountBO = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());
        MeetingBO meeting = center.getCustomerMeeting().getMeeting();

        // change meeting weekday to thursday
        meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(
                (WeekDaysEntity) new MasterPersistence().retrieveMasterEntity(WeekDay.THURSDAY.getValue(),
                        WeekDaysEntity.class, null));
        List<java.util.Date> meetingDates = meeting.getAllDates(accountBO.getApplicableIdsForFutureInstallments()
                .size() + 1);
        TestObjectFactory.updateObject(center);

        center.getCustomerAccount().handleChangeInMeetingSchedule();
        accountBO.handleChangeInMeetingSchedule();
        StaticHibernateUtil.getTransaction().commit();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        accountBO = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());

        for (AccountActionDateEntity actionDateEntity : center.getCustomerAccount().getAccountActionDates()) {
            checkScheduleDates(meetingDates, actionDateEntity);
        }
        for (AccountActionDateEntity actionDateEntity : accountBO.getAccountActionDates()) {
            checkScheduleDates(meetingDates, actionDateEntity);
        }
    }

    @Test(dependsOnMethods = { "testHandleChangeInMeetingSchedule" })
    public void testDeleteFutureInstallments() throws HibernateException, SystemException, AccountException {
        TestObjectFactory.flushandCloseSession();
        accountBO = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());
        accountBO.deleteFutureInstallments();
        StaticHibernateUtil.getTransaction().commit();
        StaticHibernateUtil.closeSession();
        accountBO = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());
        Assert.assertEquals(1, accountBO.getAccountActionDates().size());

    }

    @Test(dependsOnMethods = { "testDeleteFutureInstallments" })
    public void testUpdate() throws Exception {
        TestObjectFactory.flushandCloseSession();
        accountBO = (LoanBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());
        accountBO.setUserContext(TestUtils.makeUser());

        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(TestUtils.makeUser().getId());
        AccountNotesEntity accountNotesEntity = new AccountNotesEntity(currentDate, "account updated", personnelBO,
                accountBO);
        accountBO.addAccountNotes(accountNotesEntity);
        TestObjectFactory.updateObject(accountBO);
        TestObjectFactory.flushandCloseSession();
        accountBO = (LoanBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());
        for (AccountNotesEntity accountNotes : accountBO.getRecentAccountNotes()) {
            Assert.assertEquals("Last note added is account updated", "account updated", accountNotes.getComment());
            Assert.assertEquals(currentDate.toString(), accountNotes.getCommentDateStr());
            Assert.assertEquals(personnelBO.getPersonnelId(), accountNotes.getPersonnel().getPersonnelId());
            Assert.assertEquals(personnelBO.getDisplayName(), accountNotes.getPersonnel().getDisplayName());
            Assert.assertEquals(currentDate.toString(), accountNotes.getCommentDate().toString());
            Assert.assertEquals(accountBO.getAccountId(), accountNotes.getAccount().getAccountId());
            Assert.assertNotNull(accountNotes.getCommentId());
            break;
        }
    }

    @Test(dependsOnMethods = { "testUpdate" })
    public void testGetPastInstallments() {

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        CenterBO centerBO = TestObjectFactory.createCenter("Center_Active", meeting);
        StaticHibernateUtil.closeSession();
        centerBO = TestObjectFactory.getCenter(centerBO.getCustomerId());
        for (AccountActionDateEntity actionDate : centerBO.getCustomerAccount().getAccountActionDates()) {
            actionDate.setActionDate(offSetCurrentDate(4));
            break;
        }
        List<AccountActionDateEntity> pastInstallments = centerBO.getCustomerAccount().getPastInstallments();
        Assert.assertNotNull(pastInstallments);
        Assert.assertEquals(1, pastInstallments.size());
        TestObjectFactory.cleanUp(centerBO);
    }

    @Test(dependsOnMethods = { "testGetPastInstallments" })
    public void testGetAllInstallments() {

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        CenterBO centerBO = TestObjectFactory.createCenter("Center_Active", meeting);
        StaticHibernateUtil.closeSession();
        centerBO = TestObjectFactory.getCenter(centerBO.getCustomerId());

        List<AccountActionDateEntity> allInstallments = centerBO.getCustomerAccount().getAllInstallments();
        Assert.assertNotNull(allInstallments);
        Assert.assertEquals(10, allInstallments.size());
        TestObjectFactory.cleanUp(centerBO);
    }

    @Test(dependsOnMethods = { "testGetAllInstallments" })
    public void testUpdatePerformanceHistoryOnAdjustment() throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());
        StaticHibernateUtil.closeSession();
        accountBO = (LoanBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());

        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
                .getMoneyForMFICurrency(212), null, accountBO.getPersonnel(), "receiptNum", Short.valueOf("1"),
                currentDate, currentDate);
        accountBO.applyPaymentWithPersist(paymentData);

        StaticHibernateUtil.commitTransaction();
        LoanBO loan = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());

        loan.applyPaymentWithPersist(TestObjectFactory.getLoanAccountPaymentData(null, TestObjectFactory
                .getMoneyForMFICurrency(600), null, loan.getPersonnel(), "receiptNum", Short.valueOf("1"), currentDate,
                currentDate));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        accountBO = (LoanBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());
        accountBO.setUserContext(TestUtils.makeUser());
        accountBO.adjustPmnt("loan account has been adjusted by test code");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        accountBO = (LoanBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Assert.assertEquals(1, accountBO.getPerformanceHistory().getNoOfPayments().intValue());

    }

    @Test(dependsOnMethods = { "testUpdatePerformanceHistoryOnAdjustment" })
    public void testAccountBOClosedDate() {
        AccountBO account = new AccountBO();
        java.util.Date originalDate = new java.util.Date();
        final long TEN_SECONDS = 10000;

        // verify that after the setter is called, changes to the object
        // passed to the setter do not affect the internal state
        java.util.Date mutatingDate1 = (java.util.Date) originalDate.clone();
        account.setClosedDate(mutatingDate1);
        mutatingDate1.setTime(System.currentTimeMillis() + TEN_SECONDS);
        Assert.assertEquals(account.getClosedDate(), originalDate);

        // verify that after the getter is called, changes to the object
        // returned by the getter do not affect the internal state
        java.util.Date originalDate2 = (java.util.Date) originalDate.clone();
        account.setClosedDate(originalDate2);
        java.util.Date mutatingDate2 = account.getClosedDate();
        mutatingDate2.setTime(System.currentTimeMillis() + TEN_SECONDS);
        Assert.assertEquals(account.getClosedDate(), originalDate);
    }

    @Test(dependsOnMethods = { "testAccountBOClosedDate" })
    public void testGetInstalmentDates() throws Exception {
        AccountBO account = new AccountBO();
        MeetingBO meeting = new MeetingBO(RecurrenceType.DAILY, (short) 1, getDate("18/08/2005"),
                MeetingType.CUSTOMER_MEETING);
        /*
         * make sure we can handle case where the number of installments is zero
         */
        account.getInstallmentDates(meeting, (short) 0, (short) 0);
    }

    @Test(dependsOnMethods = { "testGetInstalmentDates" })
    public void testGenerateId() throws Exception {
        AccountBO account = new AccountBO(35);
        String officeGlobalNum = "0567";
        String globalAccountNum = account.generateId(officeGlobalNum);
        Assert.assertEquals("056700000000035", globalAccountNum);
    }

    private List<Short> getApplicableInstallmentIdsForRemoveFees(final AccountBO account) {
        List<Short> installmentIdList = new ArrayList<Short>();
        for (AccountActionDateEntity accountActionDateEntity : account.getApplicableIdsForFutureInstallments()) {
            installmentIdList.add(accountActionDateEntity.getInstallmentId());
        }
        installmentIdList.add(account.getDetailsOfNextInstallment().getInstallmentId());
        return installmentIdList;
    }

    private void checkScheduleDates(final List<java.util.Date> meetingDates,
            final AccountActionDateEntity actionDateEntity) {
        // first installment should remain today, ie meeting unchanged
        if (actionDateEntity.getInstallmentId() == 1) {
            Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate()), DateUtils
                    .getCurrentDateWithoutTimeStamp());
        } else if (actionDateEntity.getInstallmentId() == 2) {
            Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate()), DateUtils
                    .getDateWithoutTimeStamp(meetingDates.get(1)));
        } else if (actionDateEntity.getInstallmentId() == 3) {
            Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate()), DateUtils
                    .getDateWithoutTimeStamp(meetingDates.get(2)));
        }
    }

    private void disburseLoan(final LoanBO loan, final Date startDate) throws Exception {
        loan.disburseLoan("receiptNum", startDate, Short.valueOf("1"), loan.getPersonnel(), startDate, Short
                .valueOf("1"));
        StaticHibernateUtil.commitTransaction();
    }

    private java.sql.Date offSetCurrentDate(final int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }
}
