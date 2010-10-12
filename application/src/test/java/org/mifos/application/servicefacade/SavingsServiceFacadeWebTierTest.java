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
package org.mifos.application.servicefacade;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.interest.EndOfDayDetail;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.SavingsInterestScheduledEventFactory;
import org.mifos.accounts.savings.interest.schedule.internal.MonthlyOnLastDayOfMonthInterestScheduledEvent;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SavingsServiceFacadeWebTierTest {

    // class under test
    private SavingsServiceFacade savingsServiceFacade;

    // collaborators
    @Mock
    private SavingsDao savingsDao;
    @Mock
    private PersonnelDao personnelDao;
    @Mock
    private CustomerDao customerDao;
    @Mock
    private SavingsBO savingsAccount;
    @Mock
    private SavingsInterestScheduledEventFactory savingsInterestScheduledEventFactory;
    @Mock
    private HibernateTransactionHelper transactionHelper;

    private MifosCurrency oldCurrency;

    @Before
    public void setupAndInjectDependencies() {
        oldCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        savingsServiceFacade = new SavingsServiceFacadeWebTier(savingsDao, personnelDao, customerDao);
        ((SavingsServiceFacadeWebTier)savingsServiceFacade).setSavingsInterestScheduledEventFactory(savingsInterestScheduledEventFactory);
        ((SavingsServiceFacadeWebTier)savingsServiceFacade).setTransactionHelper(transactionHelper);
    }

    @After
    public void afterClass() {
        Money.setDefaultCurrency(oldCurrency);
    }

    @Test
    public void shouldRetrieveActiveAndInactiveAccountsPendingInterestPostingOnDayOfBatchJob() {

        // setup
        LocalDate dateOfBatchJob = new LocalDate();

        // stubbing

        // exercise test
        savingsServiceFacade.batchPostInterestToSavingsAccount(dateOfBatchJob);

        // verification
        verify(savingsDao).retrieveAllActiveAndInActiveSavingsAccountsPendingInterestPostingOn(dateOfBatchJob);
    }

    @Test
    public void shouldNotTryToPostWhenNoAccountsArePendingPosting() {

        // setup
        LocalDate dateOfBatchJob = new LocalDate();
        List<Integer> emptyList = new ArrayList<Integer>();

        // stubbing
        when(savingsDao.retrieveAllActiveAndInActiveSavingsAccountsPendingInterestPostingOn(dateOfBatchJob)).thenReturn(emptyList);

        // exercise test
        savingsServiceFacade.batchPostInterestToSavingsAccount(dateOfBatchJob);

        // verification
        verify(savingsDao, never()).findById(anyLong());
    }

    /**
     * ignoring for now while spiking interest calculation for entire account using posting periods.
     */
    @Ignore
    @Test
    public void shouldPostInterestWithinTransaction() {

        // setup
        LocalDate dateOfBatchJob = new LocalDate();
        InterestScheduledEvent postingSchedule = new MonthlyOnLastDayOfMonthInterestScheduledEvent(1);

        // stubbing
        when(savingsDao.retrieveAllActiveAndInActiveSavingsAccountsPendingInterestPostingOn(dateOfBatchJob)).thenReturn(Arrays.asList(1));
        when(savingsDao.findById(Long.valueOf("1"))).thenReturn(savingsAccount);
        when(savingsInterestScheduledEventFactory.createScheduledEventFrom((MeetingBO)anyObject())).thenReturn(postingSchedule);
        when(savingsAccount.postInterest(postingSchedule)).thenReturn(true);
        when(savingsAccount.getCurrency()).thenReturn(TestUtils.RUPEE);

        // exercise test
        savingsServiceFacade.batchPostInterestToSavingsAccount(dateOfBatchJob);

        // verification
        verify(this.transactionHelper).startTransaction();
        verify(this.savingsAccount).postInterest(postingSchedule);
        verify(this.savingsDao).save(savingsAccount);
        verify(this.transactionHelper).commitTransaction();
    }

    @Test
    public void calculateInterestMinimumBalanceForNoActivitiesInCalculationInterval() {
        Long savingsId = 10394L;
        ((SavingsServiceFacadeWebTier)savingsServiceFacade).setSavingsInterestScheduledEventFactory(new SavingsInterestScheduledEventFactory());
        CustomerBO customer = new GroupBuilder().build();
        SavingsBO savingsAccount = new SavingsAccountBuilder().withCustomer(customer).build();
        savingsAccount.setLastIntCalcDate(new LocalDate(2010,9,30).toDateTimeAtStartOfDay().toDate());
        savingsAccount.setMinAmntForInt(TestUtils.createMoney("100"));

        when(savingsDao.findById(savingsId)).thenReturn(savingsAccount);

        List<EndOfDayDetail> daily = new ArrayList<EndOfDayDetail>();

        daily.add(new EndOfDayDetail(new LocalDate(2010,8,30), TestUtils.createMoney(1000), TestUtils.createMoney(), TestUtils.createMoney()));
        daily.add(new EndOfDayDetail(new LocalDate(2010,9,15), TestUtils.createMoney(500), TestUtils.createMoney(), TestUtils.createMoney()));
        daily.add(new EndOfDayDetail(new LocalDate(2010,9,20), TestUtils.createMoney(), TestUtils.createMoney(1000), TestUtils.createMoney()));

        when(savingsDao.retrieveAllEndOfDayDetailsFor(TestUtils.RUPEE, savingsId)).thenReturn(daily);

        savingsServiceFacade.calculateInterestForPostingInterval(savingsId, null);

        Assert.assertEquals(TestUtils.createMoney("1.7"), savingsAccount.getInterestToBePosted());
        Assert.assertEquals(new LocalDate(2010,10,31), new LocalDate(savingsAccount.getLastIntCalcDate()));
    }

    @Test
    public void calculateInterestMinimumBalanceForActivitiesInCalculationInterval() {
        Long savingsId = 10394L;
        ((SavingsServiceFacadeWebTier)savingsServiceFacade).setSavingsInterestScheduledEventFactory(new SavingsInterestScheduledEventFactory());
        CustomerBO customer = new GroupBuilder().build();
        SavingsBO savingsAccount = new SavingsAccountBuilder().withCustomer(customer).build();
        savingsAccount.setLastIntCalcDate(new LocalDate(2010,9,30).toDateTimeAtStartOfDay().toDate());
        savingsAccount.setMinAmntForInt(TestUtils.createMoney("100"));

        when(savingsDao.findById(savingsId)).thenReturn(savingsAccount);

        List<EndOfDayDetail> daily = new ArrayList<EndOfDayDetail>();

        daily.add(new EndOfDayDetail(new LocalDate(2010,9,30), TestUtils.createMoney(1000), TestUtils.createMoney(), TestUtils.createMoney()));
        daily.add(new EndOfDayDetail(new LocalDate(2010,10,15), TestUtils.createMoney(500), TestUtils.createMoney(), TestUtils.createMoney()));
        daily.add(new EndOfDayDetail(new LocalDate(2010,10,20), TestUtils.createMoney(), TestUtils.createMoney(1000), TestUtils.createMoney()));

        when(savingsDao.retrieveAllEndOfDayDetailsFor(TestUtils.RUPEE, savingsId)).thenReturn(daily);

        savingsServiceFacade.calculateInterestForPostingInterval(savingsId, null);

        Assert.assertEquals(TestUtils.createMoney("1.7"), savingsAccount.getInterestToBePosted());
        Assert.assertEquals(new LocalDate(2010,10,31), new LocalDate(savingsAccount.getLastIntCalcDate()));
    }

    @Test
    public void calculateInterestAverageBalanceForActivitiesInCalculationInterval() {
        Long savingsId = 10394L;
        ((SavingsServiceFacadeWebTier)savingsServiceFacade).setSavingsInterestScheduledEventFactory(new SavingsInterestScheduledEventFactory());
        CustomerBO customer = new GroupBuilder().build();
        SavingsBO savingsAccount = new SavingsAccountBuilder().withCustomer(customer).withInterestCalcType(InterestCalcType.AVERAGE_BALANCE).build();
        savingsAccount.setLastIntCalcDate(new LocalDate(2010,9,30).toDateTimeAtStartOfDay().toDate());
        savingsAccount.setMinAmntForInt(TestUtils.createMoney("100"));

        when(savingsDao.findById(savingsId)).thenReturn(savingsAccount);

        List<EndOfDayDetail> daily = new ArrayList<EndOfDayDetail>();

        daily.add(new EndOfDayDetail(new LocalDate(2010,9,30), TestUtils.createMoney(1000), TestUtils.createMoney(), TestUtils.createMoney()));
        daily.add(new EndOfDayDetail(new LocalDate(2010,10,15), TestUtils.createMoney(500), TestUtils.createMoney(), TestUtils.createMoney()));
        daily.add(new EndOfDayDetail(new LocalDate(2010,10,20), TestUtils.createMoney(), TestUtils.createMoney(1000), TestUtils.createMoney()));

        when(savingsDao.retrieveAllEndOfDayDetailsFor(TestUtils.RUPEE, savingsId)).thenReturn(daily);

        savingsServiceFacade.calculateInterestForPostingInterval(savingsId, null);

        // ((1000 x 15 + 1500 x 5 + 500 x 11) / 31) * 4/100 * 31/365 = 3.067 (Rounding) = 3.1
        Assert.assertEquals(TestUtils.createMoney("3.1"), savingsAccount.getInterestToBePosted());
        Assert.assertEquals(new LocalDate(2010,10,31), new LocalDate(savingsAccount.getLastIntCalcDate()));
    }

}
