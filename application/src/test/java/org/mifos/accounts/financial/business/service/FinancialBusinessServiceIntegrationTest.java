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

package org.mifos.accounts.financial.business.service;

import java.sql.Date;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.accounts.savings.business.SavingBOTestUtils;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.config.business.Configuration;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FinancialBusinessServiceIntegrationTest extends MifosIntegrationTestCase {
    public FinancialBusinessServiceIntegrationTest() throws Exception {
        super();
    }

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
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(loan, TestUtils.createMoney(630),
                "1111", currentDate, new PaymentTypeEntity(Short.valueOf("1")),
                new Date(System.currentTimeMillis()));
        FinancialBusinessService financialBusinessService = new FinancialBusinessService();
        AccountTrxnEntity accountTrxnEntity = getAccountTrxnObj(accountPaymentEntity);
        accountPaymentEntity.addAccountTrxn(accountTrxnEntity);
        AccountTestUtils.addAccountPayment(accountPaymentEntity, loan);

        financialBusinessService.buildAccountingEntries(accountTrxnEntity);

        TestObjectFactory.updateObject(loan);
       Assert.assertEquals(accountTrxnEntity.getFinancialTransactions().size(), 10);

        for (FinancialTransactionBO finTrxn : accountTrxnEntity.getFinancialTransactions()) {
            if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("1")) && finTrxn.isCreditEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("200"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("200"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("7"));
            } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("1")) && finTrxn.isDebitEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("200"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("200"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("20"));
            } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("2")) && finTrxn.isCreditEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("300"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("300"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("7"));
            } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("2")) && finTrxn.isDebitEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("300"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("300"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("41"));
            } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("3")) && finTrxn.isCreditEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("7"));
            } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("3")) && finTrxn.isDebitEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("50"));
            } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("4")) && finTrxn.isCreditEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("10"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("10"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("7"));
            } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("4")) && finTrxn.isDebitEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("10"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("10"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("50"));
            } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("6")) && finTrxn.isCreditEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("20"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("20"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("7"));
            } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("6")) && finTrxn.isDebitEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("20"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("20"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("42"));
            } else {
                fail("There should not be any other entry");
            }
        }
    }

    private AccountTrxnEntity getAccountTrxnObj(AccountPaymentEntity accountPaymentEntity) throws Exception {
        MasterPersistence masterPersistenceService = new MasterPersistence();
        Date currentDate = new Date(System.currentTimeMillis());

        LoanScheduleEntity accountAction = (LoanScheduleEntity) loan.getAccountActionDate(Short.valueOf("1"));

        LoanTrxnDetailEntity accountTrxnEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                AccountActionTypes.LOAN_ADJUSTMENT, Short.valueOf("1"), accountAction
                        .getActionDate(), TestObjectFactory.getPersonnel(PersonnelConstants.SYSTEM_USER), currentDate,
                TestUtils.createMoney(630), "test for loan adjustment", null, TestUtils.createMoney(200),
                TestUtils.createMoney(300), TestUtils.createMoney(),
                TestUtils.createMoney(10), TestUtils.createMoney(20), null,
                masterPersistenceService);

        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountAction.getAccountFeesActionDetails()) {
            LoanBOTestUtils.setFeeAmountPaid(accountFeesActionDetailEntity, TestUtils.createMoney(100));
            FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity(accountTrxnEntity, accountFeesActionDetailEntity
                    .getAccountFee(), accountFeesActionDetailEntity.getFeeAmount());
            accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
        }

        return accountTrxnEntity;
    }

    private LoanBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(this.getClass().getSimpleName() + "","FL",startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    public void testSavingsAdjustmentDepositAccountingEntries() throws Exception {
        createInitialObjectsForSavings();
        SavingsTestHelper helper = new SavingsTestHelper();
        SavingsPersistence savingsPersistence = new SavingsPersistence();

        PersonnelBO createdBy = new PersonnelPersistence().getPersonnel(userContext.getId());
        Money depositAmount = new Money(TestUtils.RUPEE, "1000.0");
        Money balanceAmount = new Money(TestUtils.RUPEE, "5000.0");
        java.util.Date trxnDate = helper.getDate("20/05/2006");

        AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, depositAmount, balanceAmount,
                trxnDate, AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings, createdBy, group);
        AccountTestUtils.addAccountPayment(payment, savings);

        SavingBOTestUtils.setBalance(savings, balanceAmount);
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
        SavingBOTestUtils.setBalance(savings, balanceAmount);

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
               Assert.assertEquals(2, trxn.getFinancialTransactions().size());
                for (FinancialTransactionBO finTrxn : trxn.getFinancialTransactions()) {
                    if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("19")) && finTrxn.isCreditEntry()) {
                        Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("1000"));
                        Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("1000"));
                        Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("7"));
                    } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("19")) && finTrxn.isDebitEntry()) {
                        Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("1000"));
                        Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("1000"));
                        Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("31"));
                    } else {
                        fail("There should not be any other entry");
                    }
                }
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
        AccountTestUtils.addAccountPayment(payment, savings);
        SavingBOTestUtils.setBalance(savings, balanceAmount);
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
        SavingBOTestUtils.setBalance(savings, balanceAmount);

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
                for (FinancialTransactionBO finTrxn : trxn.getFinancialTransactions()) {
                    if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("21")) && finTrxn.isCreditEntry()) {
                        Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("1000"));
                        Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("1000"));
                        Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("31"));
                    } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("21")) && finTrxn.isDebitEntry()) {
                        Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("1000"));
                        Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("1000"));
                        Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("7"));
                    } else {
                        fail("There should not be any other entry");
                    }
                }
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

            AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings, withdrawalAmount, new Money(getCurrency()),
                    trxnDate, AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings, createdBy, group);

           Assert.assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns().size());
            FinancialBusinessService financialBusinessService = new FinancialBusinessService();
            AccountTestUtils.addAccountPayment(payment, savings);
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
           Assert.assertEquals(new Money(getCurrency(), "1000.7"), accountTrxn.getWithdrawlAmount());

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
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        SavingsTestHelper helper = new SavingsTestHelper();
        savingsOffering = helper.createSavingsOffering("sav 1234", "cvf1", (short) 31, (short) 7);
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
    }

    public void testLoanWriteOffAccountingEntries() throws Exception {
        loan = getLoanAccount();
        loan.setUserContext(TestUtils.makeUser());
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(loan, TestUtils.createMoney(630),
                null, null, new PaymentTypeEntity(Short.valueOf("1")), new Date(System
                .currentTimeMillis()));
        FinancialBusinessService financialBusinessService = new FinancialBusinessService();
        AccountActionDateEntity accountActionDateEntity = loan.getAccountActionDate(Short.valueOf("1"));
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(loan.getUserContext().getId());
        LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                AccountActionTypes.WRITEOFF, accountActionDateEntity.getInstallmentId(),
                accountActionDateEntity.getActionDate(), personnel, new Date(System.currentTimeMillis()),
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), "Loan Written Off", null,
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), new Money(getCurrency()), new Money(getCurrency()), new Money(getCurrency()),
                new Money(getCurrency()), null, new MasterPersistence());

        accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);
        AccountTestUtils.addAccountPayment(accountPaymentEntity, loan);
        financialBusinessService.buildAccountingEntries(loanTrxnDetailEntity);
        TestObjectFactory.updateObject(loan);
        Set<FinancialTransactionBO> finTrxnSet = loanTrxnDetailEntity.getFinancialTransactions();
       Assert.assertEquals(finTrxnSet.size(), 2);
        for (FinancialTransactionBO finTrxn : finTrxnSet) {
            if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("22")) && finTrxn.isCreditEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("20"));
            } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("22")) && finTrxn.isDebitEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("22"));
            } else {
                fail("There should not be any other entry");
            }
        }

    }

    public void testLoanRescheduleAccountingEntries() throws Exception {
        loan = getLoanAccount();
        loan.setUserContext(TestUtils.makeUser());
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(loan, TestUtils.createMoney(630),
                null, null, new PaymentTypeEntity(Short.valueOf("1")), new Date(System
                .currentTimeMillis()));
        FinancialBusinessService financialBusinessService = new FinancialBusinessService();
        AccountActionDateEntity accountActionDateEntity = loan.getAccountActionDate(Short.valueOf("1"));
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(loan.getUserContext().getId());
        LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                AccountActionTypes.LOAN_RESCHEDULED, accountActionDateEntity.getInstallmentId(),
                accountActionDateEntity.getActionDate(), personnel, new Date(System.currentTimeMillis()),
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), "Loan Rescheduled", null,
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), new Money(getCurrency()), new Money(getCurrency()), new Money(getCurrency()),
                new Money(getCurrency()), null, new MasterPersistence());

        accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);
        AccountTestUtils.addAccountPayment(accountPaymentEntity, loan);
        financialBusinessService.buildAccountingEntries(loanTrxnDetailEntity);
        TestObjectFactory.updateObject(loan);
        Set<FinancialTransactionBO> finTrxnSet = loanTrxnDetailEntity.getFinancialTransactions();
       Assert.assertEquals(finTrxnSet.size(), 2);
        for (FinancialTransactionBO finTrxn : finTrxnSet) {
            if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("23")) && finTrxn.isCreditEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("20"));
            } else if (finTrxn.getFinancialAction().getId().equals(Short.valueOf("23")) && finTrxn.isDebitEntry()) {
                Assert.assertEquals(finTrxn.getPostedAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getBalanceAmount(), TestUtils.createMoney("100"));
                Assert.assertEquals(finTrxn.getGlcode().getGlcodeId(), Short.valueOf("7"));
            } else {
                fail("There should not be any other entry");
            }
        }
    }

}
