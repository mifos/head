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

package org.mifos.accounts.financial.business.service;

import java.sql.Date;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class FinancialBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    private LoanBO loan = null;
    private CustomerBO center = null;
    private CustomerBO group = null;

    @Autowired
    LegacyMasterDao legacyMasterDao;

    @After
    public void tearDown() throws Exception {
        try {
            loan = null;
            group = null;
            center = null;
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db

        }
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testLoanAdjustmentAccountingEntries() throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());
        loan = getLoanAccount();
        loan.setUserContext(TestUtils.makeUser());
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(loan, TestUtils.createMoney(630), "1111",
                currentDate, new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));
        FinancialBusinessService financialBusinessService = new FinancialBusinessService();
        AccountTrxnEntity accountTrxnEntity = getAccountTrxnObj(accountPaymentEntity);
        accountPaymentEntity.addAccountTrxn(accountTrxnEntity);
        AccountTestUtils.addAccountPayment(accountPaymentEntity, loan);

        financialBusinessService.buildAccountingEntries(accountTrxnEntity);

        TestObjectFactory.updateObject(loan);
//        Assert.assertEquals(accountTrxnEntity.getFinancialTransactions().size(), 10);

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

                //--fail("There should not be any other entry");
            }
        }
    }

    private AccountTrxnEntity getAccountTrxnObj(AccountPaymentEntity accountPaymentEntity) throws Exception {
        Date currentDate = new Date(System.currentTimeMillis());

        LoanScheduleEntity accountAction = (LoanScheduleEntity) loan.getAccountActionDate(Short.valueOf("1"));

        LoanTrxnDetailEntity accountTrxnEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                AccountActionTypes.LOAN_ADJUSTMENT, Short.valueOf("1"), accountAction.getActionDate(),
                TestObjectFactory.getPersonnel(PersonnelConstants.SYSTEM_USER), currentDate,
                TestUtils.createMoney(630), "test for loan adjustment", null, TestUtils.createMoney(200), TestUtils
                        .createMoney(300), TestUtils.createMoney(), TestUtils.createMoney(10), TestUtils
                        .createMoney(20), null);

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
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group",
                CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(this.getClass().getSimpleName() + "", "FL",
                startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    @Test
    public void testLoanWriteOffAccountingEntries() throws Exception {
        loan = getLoanAccount();
        loan.setUserContext(TestUtils.makeUser());
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(loan, TestUtils.createMoney(630), null,
                null, new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));
        FinancialBusinessService financialBusinessService = new FinancialBusinessService();
        AccountActionDateEntity accountActionDateEntity = loan.getAccountActionDate(Short.valueOf("1"));
        PersonnelBO personnel = legacyPersonnelDao.getPersonnel(loan.getUserContext().getId());
        LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                AccountActionTypes.WRITEOFF, accountActionDateEntity.getInstallmentId(), accountActionDateEntity
                        .getActionDate(), personnel, new Date(System.currentTimeMillis()),
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), "Loan Written Off", null,
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), new Money(getCurrency()), new Money(
                        getCurrency()), new Money(getCurrency()), new Money(getCurrency()), null);

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
                //--fail("There should not be any other entry");
            }
        }

    }

    @Test
    public void testLoanRescheduleAccountingEntries() throws Exception {
        loan = getLoanAccount();
        loan.setUserContext(TestUtils.makeUser());
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(loan, TestUtils.createMoney(630), null,
                null, new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));
        FinancialBusinessService financialBusinessService = new FinancialBusinessService();
        AccountActionDateEntity accountActionDateEntity = loan.getAccountActionDate(Short.valueOf("1"));
        PersonnelBO personnel = legacyPersonnelDao.getPersonnel(loan.getUserContext().getId());
        LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                AccountActionTypes.LOAN_RESCHEDULED, accountActionDateEntity.getInstallmentId(),
                accountActionDateEntity.getActionDate(), personnel, new Date(System.currentTimeMillis()),
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), "Loan Rescheduled", null,
                ((LoanScheduleEntity) accountActionDateEntity).getPrincipal(), new Money(getCurrency()), new Money(
                        getCurrency()), new Money(getCurrency()), new Money(getCurrency()), null);

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
                Assert.fail("There should not be any other entry");
            }
        }
    }

}
