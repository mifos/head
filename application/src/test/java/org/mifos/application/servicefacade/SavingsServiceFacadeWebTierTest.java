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

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.SavingsInterestScheduledEventFactory;
import org.mifos.accounts.savings.interest.schedule.internal.MonthlyOnLastDayOfMonthInterestScheduledEvent;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
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

    @Before
    public void setupAndInjectDependencies() {
        savingsServiceFacade = new SavingsServiceFacadeWebTier(savingsDao, personnelDao, customerDao);
        ((SavingsServiceFacadeWebTier)savingsServiceFacade).setSavingsInterestScheduledEventFactory(savingsInterestScheduledEventFactory);
        ((SavingsServiceFacadeWebTier)savingsServiceFacade).setTransactionHelper(transactionHelper);
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

        // exercise test
        savingsServiceFacade.batchPostInterestToSavingsAccount(dateOfBatchJob);

        // verification
        verify(this.transactionHelper).startTransaction();
        verify(this.savingsAccount).postInterest(postingSchedule);
        verify(this.savingsDao).save(savingsAccount);
        verify(this.transactionHelper).commitTransaction();
    }

}
