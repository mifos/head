/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
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

    @Test
    public void testCopyInstallmentSchedule() {
        LoanBO loanBO = new LoanBO();
        loanBO.addAccountActionDate(getLoanScheduleEntity(rupee, getDate(23, 10, 2010), "100", "10", "1"));
        loanBO.addAccountActionDate(getLoanScheduleEntity(rupee, getDate(23, 11, 2010), "100", "10", "2"));
        loanBO.addAccountActionDate(getLoanScheduleEntity(rupee, getDate(23, 12, 2010), "100", "10", "3"));
        List<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(getRepaymentScheduleInstallment("24-Oct-2010", 1, "123", "12"));
        installments.add(getRepaymentScheduleInstallment("24-Nov-2010", 2, "231", "23"));
        installments.add(getRepaymentScheduleInstallment("24-Dec-2010", 3, "312", "31"));
        loanBO.copyInstallmentSchedule(installments);
        Set<LoanScheduleEntity> loanScheduleEntities = loanBO.getLoanScheduleEntities();
        LoanScheduleEntity[] loanScheduleEntitiesArr = loanScheduleEntities.toArray(new LoanScheduleEntity[loanScheduleEntities.size()]);
        assertLoanScheduleEntity(loanScheduleEntitiesArr[0], "123.0", "12.0", "2010-10-24");
        assertLoanScheduleEntity(loanScheduleEntitiesArr[1], "231.0", "23.0", "2010-11-24");
        assertLoanScheduleEntity(loanScheduleEntitiesArr[2], "312.0", "31.0", "2010-12-24");
    }

    private void assertLoanScheduleEntity(LoanScheduleEntity loanScheduleEntity, String pricipal, String interest, String dueDate) {
        Assert.assertEquals(loanScheduleEntity.getPrincipal().toString(), pricipal);
        Assert.assertEquals(loanScheduleEntity.getInterest().toString(), interest);
        Assert.assertEquals(loanScheduleEntity.getActionDate().toString(), dueDate);
    }

    @Test
    @ExpectedException(value = AccountException.class)
    public void testInvalidConnectionForSave() throws PersistenceException {
        final AccountPersistence accountPersistence = mock(AccountPersistence.class);

        LoanBO loanBO = new LoanBO() {
            @Override
            public AccountPersistence getAccountPersistence() {
                return accountPersistence;
            }
        };
        try {
            when(accountPersistence.createOrUpdate(loanBO)).thenThrow(new PersistenceException("some exception"));
            loanBO.update();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (AccountException e) {
        }
    }

    private LoanScheduleEntity getLoanScheduleEntity(MifosCurrency currency, Date date, String principal, String interest, String installmentId) {
        LoanBO loanBO = mock(LoanBO.class);
        when(loanBO.getCurrency()).thenReturn(currency);
        return new LoanScheduleEntity(loanBO, mock(CustomerBO.class), Short
                .valueOf(installmentId), new java.sql.Date(date.getTime()), PaymentStatus.UNPAID, new Money(currency, principal),
                new Money(currency, interest));

    }

    private RepaymentScheduleInstallment getRepaymentScheduleInstallment(String dueDate, int installment,
                                                                         String principal,
                                                                         String interest) {
        return installmentBuilder.reset(locale).withInstallment(installment).withDueDateValue(dueDate).
                withPrincipal(new Money(rupee, principal)).withInterest(new Money(rupee, interest)).
                build();
    }
}