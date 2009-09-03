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

package org.mifos.application.accounts.financial.business.service;

import java.sql.Date;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountPaymentEntityIntegrationTest;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanBOTestUtils;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsBOIntegrationTest;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FinancialBusinessServiceIntegrationTest extends MifosIntegrationTestCase {
    public FinancialBusinessServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final double DELTA = 0.00000001;

    protected LoanBO loan = null;

    protected SavingsBO savings;

    protected SavingsOfferingBO savingsOffering;

    protected CustomerBO center = null;

    private CustomerBO group = null;

    private UserContext userContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestUtils.makeUser();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(loan);
            TestObjectFactory.cleanUp(savings);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoanAdjustmentAccountingEntries() throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());
        loan = getLoanAccount();
        loan.setUserContext(TestUtils.makeUser());
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(loan, TestObjectFactory
                .getMoneyForMFICurrency(630), "1111", currentDate, new PaymentTypeEntity(Short.valueOf("1")), new Date(
                System.currentTimeMillis()));
        FinancialBusinessService financialBusinessService = new FinancialBusinessService();
        AccountTrxnEntity accountTrxnEntity = getAccountTrxnObj(accountPaymentEntity);
        accountPaymentEntity.addAccountTrxn(accountTrxnEntity);
        AccountPaymentEntityIntegrationTest.addAccountPayment(accountPaymentEntity, loan);

        financialBusinessService.buildAccountingEntries(accountTrxnEntity);

        TestObjectFactory.updateObject(loan);
       Assert.assertEquals(accountTrxnEntity.getFinancialTransactions().size(), 10);

        int countNegativeFinTrxn = 0;
        for (FinancialTransactionBO finTrxn : accountTrxnEntity.getFinancialTransactions()) {
            if (finTrxn.getPostedAmount().getAmountDoubleValue() < 0)
                countNegativeFinTrxn++;
            else
               Assert.assertEquals("Positive finTrxn values", finTrxn.getPostedAmount().getAmountDoubleValue(), 200.0);
        }
       Assert.assertEquals("Negative finTrxn values count", countNegativeFinTrxn, 9);
       Assert.assertEquals("Positive finTrxn values count", accountTrxnEntity.getFinancialTransactions().size()
                - countNegativeFinTrxn, 1);
    }

    private AccountTrxnEntity getAccountTrxnObj(AccountPaymentEntity accountPaymentEntity) throws Exception {
        MasterPersistence masterPersistenceService = new MasterPersistence();
        Date currentDate = new Date(System.currentTimeMillis());

        LoanScheduleEntity accountAction = (LoanScheduleEntity) loan.getAccountActionDate(Short.valueOf("1"));

        LoanTrxnDetailEntity accountTrxnEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                (AccountActionEntity) masterPersistenceService.getPersistentObject(AccountActionEntity.class,
                        AccountActionTypes.LOAN_ADJUSTMENT.getValue()), Short.valueOf("1"), accountAction
                        .getActionDate(), TestObjectFactory.getPersonnel(PersonnelConstants.SYSTEM_USER), currentDate,
                TestObjectFactory.getMoneyForMFICurrency(630), "test for loan adjustment", null, TestObjectFactory
                        .getMoneyForMFICurrency(200), TestObjectFactory.getMoneyForMFICurrency(300), new Money(),
                TestObjectFactory.getMoneyForMFICurrency(10), TestObjectFactory.getMoneyForMFICurrency(20), null);

        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountAction.getAccountFeesActionDetails()) {
            LoanBOTestUtils.setFeeAmountPaid(accountFeesActionDetailEntity, TestObjectFactory
                    .getMoneyForMFICurrency(100));
            FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity(accountTrxnEntity, accountFeesActionDetailEntity
                    .getAccountFee(), accountFeesActionDetailEntity.getFeeAmount());
            accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
        }

        return accountTrxnEntity;
    }

    private LoanBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(this.getClass().getSimpleName() + "","FL",startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    public void testSavingsAdjustmentDepositAccountingEntries() throws Exception {
        createInitialObjectsForSavings();
        SavingsTestHelper helper = new SavingsTestHelper();
        SavingsPersistence savingsPersistence = new SavingsPersistence();

        PersonnelBO createdBy = new PersonnelPersistence().getPersonnel(userContext.getId());
        Money depositAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(), "1000.0");
        Money balanceAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(), "5000.0");
        java.util.Date trxnDate = helper.getDate("20/05/2006");

        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositAmount, balanceAmount,
                trxnDate, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountPaymentEntityIntegrationTest.addAccountPayment(payment, savings);

        SavingsBOIntegrationTest.setBalance(savings, balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        payment = savings.getLastPmnt();
        balanceAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(), "4000.0");
        AccountTrxnEntity accountTrxn = helper.createAccountTrxn(payment, depositAmount.negate(), balanceAmount,
                trxnDate, trxnDate, AccountActionTypes.SAVINGS_ADJUSTMENT.getValue(), savings, createdBy, group, "",
                null);
        payment.addAccountTrxn(accountTrxn);
        SavingsBOIntegrationTest.setBalance(savings, balanceAmount);

        FinancialBusinessService financialBusinessService = new FinancialBusinessService();
        financialBusinessService.buildAccountingEntries(accountTrxn);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        payment = savings.getLastPmnt();
       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
        for (AccountTrxnEntity trxn : payment.getAccountTrxns()) {
            if (trxn.getAccountActionEntity().getId().equals(AccountActionTypes.SAVINGS_ADJUSTMENT.getValue())) {
               Assert.assertTrue(true);
               Assert.assertEquals(Integer.valueOf(2).intValue(), trxn.getFinancialTransactions().size());
                int countNegativeFinTrxn = 0;
                for (FinancialTransactionBO finTrxn : trxn.getFinancialTransactions()) {
                    if (finTrxn.getPostedAmount().getAmountDoubleValue() < 0) {
                        countNegativeFinTrxn++;
                       Assert.assertEquals(1000.0, finTrxn.getPostedAmount().negate().getAmountDoubleValue(), DELTA);
                    } else
                       Assert.assertTrue(false);
                }

               Assert.assertEquals("Negative finTrxn values count", 2, countNegativeFinTrxn);
               Assert.assertEquals("Positive finTrxn values count", 0, accountTrxn.getFinancialTransactions().size()
                        - countNegativeFinTrxn);
            }
        }
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testSavingsAdjustmentWithdrawalAccountingEntries() throws Exception {
        createInitialObjectsForSavings();
        SavingsTestHelper helper = new SavingsTestHelper();
        SavingsPersistence savingsPersistence = new SavingsPersistence();

        PersonnelBO createdBy = new PersonnelPersistence().getPersonnel(userContext.getId());
        Money withdrawalAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(), "1000.0");
        Money balanceAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(), "5000.0");
        java.util.Date trxnDate = helper.getDate("20/05/2006");

        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, withdrawalAmount, balanceAmount,
                trxnDate, AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings, createdBy, group);
        AccountPaymentEntityIntegrationTest.addAccountPayment(payment, savings);
        SavingsBOIntegrationTest.setBalance(savings, balanceAmount);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        payment = savings.getLastPmnt();
        balanceAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(), "6000.0");
        AccountTrxnEntity accountTrxn = helper.createAccountTrxn(payment, withdrawalAmount, balanceAmount, trxnDate,
                trxnDate, AccountActionTypes.SAVINGS_ADJUSTMENT.getValue(), savings, createdBy, group,
                "correction entry", null);
        payment.addAccountTrxn(accountTrxn);
        SavingsBOIntegrationTest.setBalance(savings, balanceAmount);

        FinancialBusinessService financialBusinessService = new FinancialBusinessService();
        financialBusinessService.buildAccountingEntries(accountTrxn);
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        savings = savingsPersistence.findById(savings.getAccountId());
        savings.setUserContext(userContext);
        payment = savings.getLastPmnt();
       Assert.assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns().size());
        for (AccountTrxnEntity trxn : payment.getAccountTrxns()) {
            if (trxn.getAccountActionEntity().getId().equals(AccountActionTypes.SAVINGS_ADJUSTMENT.getValue())) {
               Assert.assertTrue(true);
               Assert.assertEquals(Integer.valueOf(2).intValue(), trxn.getFinancialTransactions().size());
                int countNegativeFinTrxn = 0;
                for (FinancialTransactionBO finTrxn : trxn.getFinancialTransactions()) {
                    if (finTrxn.getPostedAmount().getAmountDoubleValue() < 0) {
                        countNegativeFinTrxn++;
                    } else
                       Assert.assertEquals(1000.0, finTrxn.getPostedAmount().getAmountDoubleValue(), DELTA);
                   Assert.assertEquals("correction entry", finTrxn.getNotes());
                }

               Assert.assertEquals("Negative finTrxn values count", 0, countNegativeFinTrxn);
               Assert.assertEquals("Positive finTrxn values count", 2, accountTrxn.getFinancialTransactions().size()
                        - countNegativeFinTrxn);

            }
        }
        group = savings.getCustomer();
        center = group.getParentCustomer();
    }

    public void testWithdrawalEntriesOnSavingsCloseAccount() throws Exception {
        try {
            createInitialObjectsForSavings();
            SavingsTestHelper helper = new SavingsTestHelper();
            PersonnelBO createdBy = new PersonnelPersistence().getPersonnel(userContext.getId());
            Money withdrawalAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(), "1000.7");
            java.util.Date trxnDate = helper.getDate("20/05/2006");

            AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, withdrawalAmount, new Money(),
                    trxnDate, AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings, createdBy, group);

           Assert.assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns().size());
            FinancialBusinessService financialBusinessService = new FinancialBusinessService();
            AccountPaymentEntityIntegrationTest.addAccountPayment(payment, savings);
            SavingsTrxnDetailEntity accountTrxn = null;
            for (AccountTrxnEntity trxn : payment.getAccountTrxns())
                accountTrxn = (SavingsTrxnDetailEntity) trxn;
            savings.setUserContext(TestObjectFactory.getContext());
            savings.changeStatus(AccountState.SAVINGS_CLOSED.getValue(), AccountStateFlag.SAVINGS_REJECTED.getValue(),
                    "");
            financialBusinessService.buildAccountingEntries(accountTrxn);
            Set<FinancialTransactionBO> financialTrxns = accountTrxn.getFinancialTransactions();
           Assert.assertEquals(Integer.valueOf(4).intValue(), financialTrxns.size());

            int withdrawalTrxns = 0;
            int roundingTrxns = 0;
            for (FinancialTransactionBO finTrxn : financialTrxns) {
                if (finTrxn.getFinancialAction().getId().equals(FinancialActionConstants.ROUNDING.getValue()))
                    roundingTrxns++;
                else
                    withdrawalTrxns++;
            }
           Assert.assertEquals(Integer.valueOf(2).intValue(), roundingTrxns);
           Assert.assertEquals(Integer.valueOf(2).intValue(), withdrawalTrxns);
           Assert.assertEquals(new Money("1000.7"), accountTrxn.getWithdrawlAmount());

            TestObjectFactory.flushandCloseSession();

            savings = new SavingsPersistence().findById(savings.getAccountId());
            group = savings.getCustomer();
            center = group.getParentCustomer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createInitialObjectsForSavings() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() + " Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() + " Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        SavingsTestHelper helper = new SavingsTestHelper();
        savingsOffering = helper.createSavingsOffering("sav 1234", "cvf1", (short) 31, (short) 7);
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
    }

    public void testLoanWriteOffAccountingEntries() throws Exception {
        loan = getLoanAccount();
        loan.setUserContext(TestUtils.makeUser());
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(loan, TestObjectFactory
                .getMoneyForMFICurrency(630), null, null, new PaymentTypeEntity(Short.valueOf("1")), new Date(System
                .currentTimeMillis()));
        FinancialBusinessService financialBusinessService = new FinancialBusinessService();
        AccountActionDateEntity accountActionDateEntity = loan.getAccountActionDate(Short.valueOf("1"));
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(loan.getUserContext().getId());
        LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                (AccountActionEntity) new MasterPersistence().getPersistentObject(AccountActionEntity.class,
                        AccountActionTypes.WRITEOFF.getValue()), accountActionDateEntity.getInstallmentId(),
                accountActionDateEntity.getActionDate(), personnel, new Date(System.currentTimeMillis()),
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), "Loan Written Off", null,
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), new Money(), new Money(), new Money(),
                new Money(), null);

        accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);
        AccountPaymentEntityIntegrationTest.addAccountPayment(accountPaymentEntity, loan);
        financialBusinessService.buildAccountingEntries(loanTrxnDetailEntity);
        TestObjectFactory.updateObject(loan);
        Set<FinancialTransactionBO> finTrxnSet = loanTrxnDetailEntity.getFinancialTransactions();
       Assert.assertEquals(finTrxnSet.size(), 2);
        int countNegativeFinTrxn = 0;
        for (FinancialTransactionBO finTrxn : finTrxnSet) {
            if (finTrxn.getPostedAmount().getAmountDoubleValue() < 0) {
               Assert.assertEquals("Negative finTrxn values", finTrxn.getPostedAmount().negate().getAmountDoubleValue(),
                        100.0);
                countNegativeFinTrxn++;
            } else
               Assert.assertEquals("Positive finTrxn values", finTrxn.getPostedAmount().getAmountDoubleValue(), 100.0);
        }
       Assert.assertEquals("Negative finTrxn values count", countNegativeFinTrxn, 1);
       Assert.assertEquals("Positive finTrxn values count", finTrxnSet.size() - countNegativeFinTrxn, 1);
    }

    public void testLoanRescheduleAccountingEntries() throws Exception {
        loan = getLoanAccount();
        loan.setUserContext(TestUtils.makeUser());
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(loan, TestObjectFactory
                .getMoneyForMFICurrency(630), null, null, new PaymentTypeEntity(Short.valueOf("1")), new Date(System
                .currentTimeMillis()));
        FinancialBusinessService financialBusinessService = new FinancialBusinessService();
        AccountActionDateEntity accountActionDateEntity = loan.getAccountActionDate(Short.valueOf("1"));
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(loan.getUserContext().getId());
        LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                (AccountActionEntity) new MasterPersistence().getPersistentObject(AccountActionEntity.class,
                        AccountActionTypes.LOAN_RESCHEDULED.getValue()), accountActionDateEntity.getInstallmentId(),
                accountActionDateEntity.getActionDate(), personnel, new Date(System.currentTimeMillis()),
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), "Loan Rescheduled", null,
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), new Money(), new Money(), new Money(),
                new Money(), null);

        accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);
        AccountPaymentEntityIntegrationTest.addAccountPayment(accountPaymentEntity, loan);
        financialBusinessService.buildAccountingEntries(loanTrxnDetailEntity);
        TestObjectFactory.updateObject(loan);
        Set<FinancialTransactionBO> finTrxnSet = loanTrxnDetailEntity.getFinancialTransactions();
       Assert.assertEquals(finTrxnSet.size(), 2);
        int countNegativeFinTrxn = 0;
        for (FinancialTransactionBO finTrxn : finTrxnSet) {
            if (finTrxn.getPostedAmount().getAmountDoubleValue() < 0) {
               Assert.assertEquals("Negative finTrxn values", finTrxn.getPostedAmount().negate().getAmountDoubleValue(),
                        100.0);
                countNegativeFinTrxn++;
            } else
               Assert.assertEquals("Positive finTrxn values", finTrxn.getPostedAmount().getAmountDoubleValue(), 100.0);
        }
       Assert.assertEquals("Negative finTrxn values count", countNegativeFinTrxn, 1);
       Assert.assertEquals("Positive finTrxn values count", finTrxnSet.size() - countNegativeFinTrxn, 1);
    }

}
