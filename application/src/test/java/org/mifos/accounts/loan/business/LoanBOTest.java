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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class LoanBOTest {

    @Mock
    private LoanScheduleEntity loanScheduleEntity;
    @Mock
    private LoanOfferingBO loanOfferingBO;

    private MifosCurrency dollar = new MifosCurrency(Short.valueOf("1"), "Dollar", BigDecimal.valueOf(1), "USD");

    @Test
    public void testWaiverAmount() {
        LoanBO loanBO = new LoanBO() {
            @Override
            public AccountActionDateEntity getDetailsOfNextInstallment() {
                return loanScheduleEntity;
            }
        };
        Mockito.when(loanScheduleEntity.getInterestDue()).thenReturn(new Money(dollar, "42"));
        Assert.assertEquals(loanBO.waiverAmount(), new Money(dollar, "42"));
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
        Mockito.when(loanOfferingBO.getCurrency()).thenReturn(dollar);
        Assert.assertEquals(loanBO.waiverAmount(), new Money(dollar, "0"));
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
        Mockito.when(loanOfferingBO.getCurrency()).thenReturn(dollar);
        Mockito.when(loanScheduleEntity.isPaid()).thenReturn(true);
        Assert.assertEquals(loanBO.waiverAmount(), new Money(dollar, "0"));
        Mockito.verify(loanOfferingBO, Mockito.times(1)).getCurrency();
        Mockito.verify(loanScheduleEntity, Mockito.times(1)).isPaid();
    }
}
