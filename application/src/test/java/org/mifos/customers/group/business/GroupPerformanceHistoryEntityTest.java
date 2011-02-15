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

package org.mifos.customers.group.business;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientPerformanceHistoryEntity;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GroupPerformanceHistoryEntityTest {

    private GroupPerformanceHistoryEntity groupPerformanceHistoryEntity;

    @Mock
    private LoanBO loan;

    @Mock
    private ConfigurationBusinessService configurationBusinessService;

    @Mock
    private AccountBusinessService accountBusinessService;

    @Mock
    private AccountBusinessService accountBusinessServiceMock;

    @Mock
    private LoanOfferingBO loanOffering;

    @Mock
    private CustomerBO customerMock;

    @Mock
    private ClientPerformanceHistoryEntity clientPerfHistoryMock;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @Test
    public void testUpdateOnDisbursementGetsCoSigningClientsForGlim() throws Exception {
        when(configurationBusinessService.isGlimEnabled()).thenReturn(true);
        clientPerfHistoryMock.updateOnDisbursement(loanOffering);

        when(customerMock.getPerformanceHistory()).thenReturn(clientPerfHistoryMock);

        when(accountBusinessServiceMock.getCoSigningClientsForGlim(loan.getAccountId())).thenReturn(
                Arrays.asList(customerMock));

        new GroupPerformanceHistoryEntity(configurationBusinessService, accountBusinessServiceMock)
                .updateOnDisbursement(loan, TestUtils.createMoney());

        verify(configurationBusinessService, atLeastOnce()).isGlimEnabled();
        verify(customerMock, atLeastOnce()).getPerformanceHistory();
        verify(accountBusinessServiceMock).getCoSigningClientsForGlim(anyInt());
        verify(clientPerfHistoryMock).updateOnDisbursement(loanOffering);
    }

    @Test
    public void testUpdateOnDisbursementDoesNotGetCoSigningClientsIfNotGlim() throws Exception {
        when(configurationBusinessService.isGlimEnabled()).thenReturn(false);
        new GroupPerformanceHistoryEntity(configurationBusinessService, accountBusinessServiceMock)
                .updateOnDisbursement(loan, TestUtils.createMoney());
        verify(configurationBusinessService, atLeastOnce()).isGlimEnabled();
        verify(accountBusinessServiceMock, never()).getCoSigningClientsForGlim(anyInt());
    }

    @Test
    public void testUpdateOnWriteOffDoesNotGetCoSigningClientsIfNotGlim() throws Exception {
        when(configurationBusinessService.isGlimEnabled()).thenReturn(false);
        new GroupPerformanceHistoryEntity(configurationBusinessService, accountBusinessServiceMock)
                .updateOnWriteOff(loan);
        verify(configurationBusinessService, atLeastOnce()).isGlimEnabled();
        verify(accountBusinessServiceMock, never()).getCoSigningClientsForGlim(anyInt());
    }

    @Test
    public void testUpdateOnWriteOffGetsCoSigningClientsForGlim() throws Exception {
        when(configurationBusinessService.isGlimEnabled()).thenReturn(true);
        when(customerMock.getPerformanceHistory()).thenReturn(clientPerfHistoryMock);
        clientPerfHistoryMock.updateOnWriteOff(loanOffering);
        when(accountBusinessServiceMock.getCoSigningClientsForGlim(loan.getAccountId())).thenReturn(
                Arrays.asList(customerMock));

        new GroupPerformanceHistoryEntity(configurationBusinessService, accountBusinessServiceMock)
                .updateOnWriteOff(loan);
        verify(configurationBusinessService, atLeastOnce()).isGlimEnabled();
        verify(customerMock, atLeastOnce()).getPerformanceHistory();
        verify(accountBusinessServiceMock).getCoSigningClientsForGlim(anyInt());
        verify(clientPerfHistoryMock).updateOnWriteOff(loanOffering);
    }

    @Test
    public void shouldUpdateLastLoanAmountWhenLoanIsFullyPaid() throws Exception {

        Money loanAmount = new Money(Money.getDefaultCurrency(), "55.6");

        // setup
        groupPerformanceHistoryEntity = new GroupPerformanceHistoryEntity(configurationBusinessService,
                accountBusinessService);

        when(loan.getLoanAmount()).thenReturn(loanAmount);

        // exercise test
        groupPerformanceHistoryEntity.updateOnFullRepayment(loan);

        // verification
        assertThat(groupPerformanceHistoryEntity.getLastGroupLoanAmount(), is(notNullValue()));
        assertThat(groupPerformanceHistoryEntity.getLastGroupLoanAmount().getAmountDoubleValue(),
                is(loanAmount.getAmountDoubleValue()));
    }
}