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
package org.mifos.accounts.loan.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.TestUtils.getDate;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountPaymentEntityBuilder;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.domain.builders.LoanAccountBuilder;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.annotation.ExpectedException;

@RunWith(MockitoJUnitRunner.class)
public class LoanBOTest {

    @Mock
    private LoanScheduleEntity loanScheduleEntity;
    @Mock
    private LoanOfferingBO loanOfferingBO;

    private MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
    private Locale locale = new Locale("en", "GB");
    private RepaymentScheduleInstallmentBuilder installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);

    @Test
    public void testWaiverAmount() {
        LoanBO loanBO = new LoanBO() {
            @Override
            public AccountActionDateEntity getDetailsOfNextInstallment() {
                return loanScheduleEntity;
            }
        };
        Mockito.when(loanScheduleEntity.getInterestDue()).thenReturn(new Money(rupee, "42"));
        Assert.assertEquals(loanBO.waiverAmount(), new Money(rupee, "42"));
        Mockito.verify(loanScheduleEntity, Mockito.times(1)).getInterestDue();
    }

    @Test
    public void testWaiverAmountWhenNextInstallmentIsNotPresent() {
        LoanBO loanBO = new LoanBO() {
            @Override
            public AccountActionDateEntity getDetailsOfNextInstallment() {
                return null;
            }

            @Override
            public LoanOfferingBO getLoanOffering() {
                return loanOfferingBO;
            }
        };
        Mockito.when(loanOfferingBO.getCurrency()).thenReturn(rupee);
        Assert.assertEquals(loanBO.waiverAmount(), new Money(rupee, "0"));
        Mockito.verify(loanOfferingBO, Mockito.times(1)).getCurrency();
    }

    @Test
    public void testWaiverAmountWhenNextInstallmentIsPaid() {
        LoanBO loanBO = new LoanBO() {
            @Override
            public AccountActionDateEntity getDetailsOfNextInstallment() {
                return loanScheduleEntity;
            }

            @Override
            public LoanOfferingBO getLoanOffering() {
                return loanOfferingBO;
            }
        };
        Mockito.when(loanOfferingBO.getCurrency()).thenReturn(rupee);
        Mockito.when(loanScheduleEntity.isPaid()).thenReturn(true);
        Assert.assertEquals(loanBO.waiverAmount(), new Money(rupee, "0"));
        Mockito.verify(loanOfferingBO, Mockito.times(1)).getCurrency();
        Mockito.verify(loanScheduleEntity, Mockito.times(1)).isPaid();
    }

    /**
     * does't work when changing applicatonConfiguration.custom.properties file.
     * Need to pull out static references to AccountingRules in used classes.
     */
    @Ignore
    @Test
    public void testCopyInstallmentSchedule() {
        Money.setDefaultCurrency(rupee);
        LoanBO loanBO = new LoanAccountBuilder().build();
        loanBO.addAccountActionDate(getLoanScheduleEntity(rupee, getDate(23, 10, 2010), "100", "10", "1", Money.zero(rupee)));
        loanBO.addAccountActionDate(getLoanScheduleEntity(rupee, getDate(23, 11, 2010), "100", "10", "2", Money.zero(rupee)));
        loanBO.addAccountActionDate(getLoanScheduleEntity(rupee, getDate(23, 12, 2010), "100", "10", "3", Money.zero(rupee)));
        List<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(getRepaymentScheduleInstallment("24-Oct-2010", 1, "123", "12"));
        installments.add(getRepaymentScheduleInstallment("24-Nov-2010", 2, "231", "23"));
        installments.add(getRepaymentScheduleInstallment("24-Dec-2010", 3, "312", "31"));
        loanBO.updateInstallmentSchedule(installments);
        Set<LoanScheduleEntity> loanScheduleEntities = loanBO.getLoanScheduleEntities();
        LoanScheduleEntity[] loanScheduleEntitiesArr = loanScheduleEntities.toArray(new LoanScheduleEntity[loanScheduleEntities.size()]);
        assertLoanScheduleEntity(loanScheduleEntitiesArr[0], "123.0", "12.0", "2010-10-24");
        assertLoanScheduleEntity(loanScheduleEntitiesArr[1], "231.0", "23.0", "2010-11-24");
        assertLoanScheduleEntity(loanScheduleEntitiesArr[2], "312.0", "31.0", "2010-12-24");
    }

    /**
     * does't work when changing applicatonConfiguration.custom.properties file.
     * Need to pull out static references to AccountingRules in used classes.
     */
    @Ignore
    @Test
    public void testLoanSummaryShouldBeUpdateOnInstallmentScheduleUpdate() {
        Money.setDefaultCurrency(rupee);
        LoanBO loanBO = new LoanAccountBuilder().build();
        loanBO.addAccountActionDate(getLoanScheduleEntity(rupee, getDate(23, 10, 2010), "100", "10", "1", Money.zero(rupee)));
        loanBO.addAccountActionDate(getLoanScheduleEntity(rupee, getDate(23, 11, 2010), "100", "10", "2", Money.zero(rupee)));
        loanBO.addAccountActionDate(getLoanScheduleEntity(rupee, getDate(23, 12, 2010), "100", "10", "3", Money.zero(rupee)));
        List<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(getRepaymentScheduleInstallment("24-Oct-2010", 1, "123", "12"));
        installments.add(getRepaymentScheduleInstallment("24-Nov-2010", 2, "231", "23"));
        installments.add(getRepaymentScheduleInstallment("24-Dec-2010", 3, "312", "31"));
        loanBO.updateInstallmentSchedule(installments);
        Set<LoanScheduleEntity> loanScheduleEntities = loanBO.getLoanScheduleEntities();
        LoanScheduleEntity[] loanScheduleEntitiesArr = loanScheduleEntities.toArray(new LoanScheduleEntity[loanScheduleEntities.size()]);
        assertLoanScheduleEntity(loanScheduleEntitiesArr[0], "123.0", "12.0", "2010-10-24");
        assertLoanScheduleEntity(loanScheduleEntitiesArr[1], "231.0", "23.0", "2010-11-24");
        assertLoanScheduleEntity(loanScheduleEntitiesArr[2], "312.0", "31.0", "2010-12-24");

        LoanSummaryEntity loanSummary = loanBO.getLoanSummary();
        assertEquals("666.0",loanSummary.getOriginalPrincipal().toString());
        assertEquals("66.0",loanSummary.getOriginalInterest().toString());

    }

    private void assertLoanScheduleEntity(LoanScheduleEntity loanScheduleEntity, String pricipal, String interest, String dueDate) {
        Assert.assertEquals(loanScheduleEntity.getPrincipal().toString(), pricipal);
        Assert.assertEquals(loanScheduleEntity.getInterest().toString(), interest);
        Assert.assertEquals(loanScheduleEntity.getActionDate().toString(), dueDate);
    }

    @Test
    @ExpectedException(value = AccountException.class)
    public void testInvalidConnectionForSave() throws PersistenceException {
        final LegacyAccountDao legacyAccountDao = mock(LegacyAccountDao.class);

        LoanBO loanBO = new LoanBO() {
            @Override
            public LegacyAccountDao getlegacyAccountDao() {
                return legacyAccountDao;
            }
        };
        try {
            when(legacyAccountDao.createOrUpdate(loanBO)).thenThrow(new PersistenceException("some exception"));
            loanBO.update();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (AccountException e) {
        }
    }

    @Test
    public void repayInstallmentsShouldPopulateCalculatedInterestsForDIPBLoans() throws PersistenceException {
        final LegacyLoanDao legacyLoanDao = mock(LegacyLoanDao.class);
        final CustomerBO customerBO = mock(CustomerBO.class);
        final LoanSummaryEntity loanSummaryEntity = mock(LoanSummaryEntity.class);

        LoanBO loanBO = new LoanBO(){
            @Override
            public boolean isDecliningBalanceInterestRecalculation() {
                return true;
            }

            @Override
            public LegacyLoanDao getlegacyLoanDao() {
                return legacyLoanDao;
            }

            @Override
            public CustomerBO getCustomer() {
                return customerBO;
            }

            @Override
            public LoanSummaryEntity getLoanSummary() {
                return loanSummaryEntity;
            }
        };
        AccountActionTypes accountActionTypes = AccountActionTypes.LOAN_REPAYMENT;
        AccountActionEntity accountActionEntity = mock(AccountActionEntity.class);
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntityBuilder().with(loanBO).build();
        PersonnelBO user = new PersonnelBO();
        Money extraInterestDue = new Money(rupee, "0.98");
        Money interest = new Money(rupee, "10");
        Money interestDue = new Money(rupee, "2.07");

        when(legacyLoanDao.getPersistentObject(AccountActionEntity.class, accountActionTypes.getValue())).thenReturn(accountActionEntity);
        when(loanScheduleEntity.getPrincipalDue()).thenReturn(new Money(rupee,"1000"));
        when(loanScheduleEntity.getTotalFeeDueWithMiscFeeDue()).thenReturn(new Money(rupee,"10"));
        when(loanScheduleEntity.getPenaltyDue()).thenReturn(new Money(rupee,"10"));
        when(loanScheduleEntity.getPenalty()).thenReturn(new Money(rupee,"100"));
        when(loanScheduleEntity.getExtraInterestDue()).thenReturn(extraInterestDue);
        when(loanScheduleEntity.getExtraInterestPaid()).thenReturn(extraInterestDue);
        when(loanScheduleEntity.getInterest()).thenReturn(interest);
        when(loanScheduleEntity.getInterestDue()).thenReturn(interestDue);

        loanBO.repayInstallment(loanScheduleEntity,accountPaymentEntity,accountActionTypes,user,"", interestDue);

        Set<AccountTrxnEntity> accountTrxns = accountPaymentEntity.getAccountTrxns();
        AccountTrxnEntity accountTrxnEntity = accountTrxns.toArray(new AccountTrxnEntity[accountTrxns.size()])[0];
        LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
        assertThat(loanTrxnDetailEntity.getInterestAmount().getAmount().doubleValue(), is(3.05));
        CalculatedInterestOnPayment calculatedInterestOnPayment = loanTrxnDetailEntity.getCalculatedInterestOnPayment();
        assertNotNull(calculatedInterestOnPayment);
        assertThat(calculatedInterestOnPayment.getExtraInterestPaid(),is(extraInterestDue));
        assertThat(calculatedInterestOnPayment.getInterestDueTillPaid(),is(interestDue));
        assertThat(calculatedInterestOnPayment.getOriginalInterest(),is(interest));
        Mockito.verify(loanScheduleEntity).makeEarlyRepaymentEntries(LoanConstants.PAY_FEES_PENALTY_INTEREST, interestDue);
    }

    @Test
    public void repayInstallmentsShouldPopulateCalculatedInterestsForDIPBLoansWithWaiverInterest() throws PersistenceException {
        final LegacyLoanDao legacyLoanDao = mock(LegacyLoanDao.class);
        final CustomerBO customerBO = mock(CustomerBO.class);
        final LoanSummaryEntity loanSummaryEntity = mock(LoanSummaryEntity.class);

        LoanBO loanBO = new LoanBO(){
            @Override
            public boolean isDecliningBalanceInterestRecalculation() {
                return true;
            }

            @Override
            public LegacyLoanDao getlegacyLoanDao() {
                return legacyLoanDao;
            }

            @Override
            public CustomerBO getCustomer() {
                return customerBO;
            }

            @Override
            public LoanSummaryEntity getLoanSummary() {
                return loanSummaryEntity;
            }

            @Override
            public MifosCurrency getCurrency() {
                return rupee;
            }
        };
        AccountActionTypes accountActionTypes = AccountActionTypes.LOAN_REPAYMENT;
        AccountActionEntity accountActionEntity = mock(AccountActionEntity.class);
        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntityBuilder().with(loanBO).build();
        PersonnelBO user = new PersonnelBO();
        Money extraInterestDue = new Money(rupee, "0.98");
        Money interest = new Money(rupee, "10");
        Money interestDue = new Money(rupee, "0");

        when(legacyLoanDao.getPersistentObject(AccountActionEntity.class, accountActionTypes.getValue())).thenReturn(accountActionEntity);
        when(loanScheduleEntity.getPrincipalDue()).thenReturn(new Money(rupee,"1000"));
        when(loanScheduleEntity.getTotalFeeDueWithMiscFeeDue()).thenReturn(new Money(rupee,"10"));
        when(loanScheduleEntity.getPenaltyDue()).thenReturn(new Money(rupee,"10"));
        when(loanScheduleEntity.getPenalty()).thenReturn(new Money(rupee,"100"));
        when(loanScheduleEntity.getExtraInterestDue()).thenReturn(extraInterestDue);
        when(loanScheduleEntity.getExtraInterestPaid()).thenReturn(extraInterestDue);
        when(loanScheduleEntity.getInterest()).thenReturn(interest);

        loanBO.repayInstallmentWithInterestWaiver(loanScheduleEntity,accountPaymentEntity,"",accountActionTypes,user);

        Set<AccountTrxnEntity> accountTrxns = accountPaymentEntity.getAccountTrxns();
        AccountTrxnEntity accountTrxnEntity = accountTrxns.toArray(new AccountTrxnEntity[accountTrxns.size()])[0];
        LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
        assertThat(loanTrxnDetailEntity.getInterestAmount().getAmount().doubleValue(), is(0.98));
        CalculatedInterestOnPayment calculatedInterestOnPayment = loanTrxnDetailEntity.getCalculatedInterestOnPayment();
        assertNotNull(calculatedInterestOnPayment);
        assertThat(calculatedInterestOnPayment.getExtraInterestPaid(),is(extraInterestDue));
        assertThat(calculatedInterestOnPayment.getInterestDueTillPaid(),is(interestDue));
        assertThat(calculatedInterestOnPayment.getOriginalInterest(),is(interest));
        Mockito.verify(loanScheduleEntity).makeEarlyRepaymentEntries(LoanConstants.PAY_FEES_PENALTY, interestDue);
    }

    private LoanScheduleEntity getLoanScheduleEntity(MifosCurrency currency, Date date, String principal, String interest, String installmentId, Money extraInterest) {
        LoanBO loanBO = mock(LoanBO.class);
        when(loanBO.getCurrency()).thenReturn(currency);
        LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(loanBO, mock(CustomerBO.class), Short
                .valueOf(installmentId), new java.sql.Date(date.getTime()), PaymentStatus.UNPAID, new Money(currency, principal),
                new Money(currency, interest));
        loanScheduleEntity.setExtraInterest(extraInterest);
        return loanScheduleEntity;

    }

    private RepaymentScheduleInstallment getRepaymentScheduleInstallment(String dueDate, int installment,
                                                                         String principal,
                                                                         String interest) {
        return installmentBuilder.reset(locale).withInstallment(installment).withDueDateValue(dueDate).
                withPrincipal(new Money(rupee, principal)).withInterest(new Money(rupee, interest)).
                build();
    }
}