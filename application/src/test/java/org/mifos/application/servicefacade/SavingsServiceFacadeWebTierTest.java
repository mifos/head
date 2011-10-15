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
package org.mifos.application.servicefacade;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.savings.interest.schedule.SavingsInterestScheduledEventFactory;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@RunWith(MockitoJUnitRunner.class)
public class SavingsServiceFacadeWebTierTest {

    // class under test
    private SavingsServiceFacade savingsServiceFacade;

    // collaborators
    @Mock
    private SavingsDao savingsDao;
    @Mock
    private SavingsProductDao savingsProductDao;
    @Mock
    private PersonnelDao personnelDao;
    @Mock
    private CustomerDao customerDao;
    @Mock
    private HolidayDao holidayDao;
    @Mock
    private SavingsInterestScheduledEventFactory savingsInterestScheduledEventFactory;
    @Mock
    private HibernateTransactionHelper transactionHelper;

    private MifosCurrency oldCurrency;

    @Before
    public void setupAndInjectDependencies() {
        oldCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        savingsServiceFacade = new SavingsServiceFacadeWebTier(savingsDao, savingsProductDao, personnelDao, customerDao, holidayDao);
        ((SavingsServiceFacadeWebTier)savingsServiceFacade).setSavingsInterestScheduledEventFactory(savingsInterestScheduledEventFactory);
        ((SavingsServiceFacadeWebTier)savingsServiceFacade).setTransactionHelper(transactionHelper);


        MifosUser principal = new MifosUserBuilder().build();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            securityContext = new SecurityContextImpl();
            SecurityContextHolder.setContext(securityContext);
        }
        if (securityContext.getAuthentication() == null || !securityContext.getAuthentication().isAuthenticated()) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal, principal.getAuthorities());
            securityContext.setAuthentication(authentication);
        }
    }

    @After
    public void afterClass() {
        Money.setDefaultCurrency(oldCurrency);
    }

    @Test
    public void shouldNotTryToPostWhenNoAccountsArePendingPosting() {

        // setup
        LocalDate dateOfBatchJob = new LocalDate();
        List<Integer> emptyList = new ArrayList<Integer>();

        // stubbing
        when(savingsDao.retrieveAllActiveAndInActiveSavingsAccountsPendingInterestPostingOn(dateOfBatchJob)).thenReturn(emptyList);

        // exercise test
        savingsServiceFacade.postInterestForLastPostingPeriod(dateOfBatchJob);

        // verification
        verify(savingsDao, never()).findById(anyLong());
    }
    
    /**
     * Tests result set offset calculation used in the limit clause (limit 0, 10).
     * For this test, we are on page 1 and each page has 10 items. The offset should be 0.
     */
    @Test
    public void testResultsetStartingPositionStartingAtPageOne() {
        int position = ((SavingsServiceFacadeWebTier) savingsServiceFacade).resultsetOffset(1, 10);
        Assert.assertEquals(0, position);
    }

    /**
     * Tests result set offset calculation used in the limit clause (limit 0, 10).
     * For this test, we are on page 2 and each page has 10 items. The offset should be 10.
     */
    @Test
    public void testResultsetStartingPositionStartingAtPageTwo() {
        int position = ((SavingsServiceFacadeWebTier) savingsServiceFacade).resultsetOffset(2, 10);
        Assert.assertEquals(10, position);
    }
}
