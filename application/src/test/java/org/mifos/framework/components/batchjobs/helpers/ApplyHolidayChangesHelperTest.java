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

package org.mifos.framework.components.batchjobs.helpers;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.framework.components.batchjobs.configuration.BatchJobConfigurationService;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * This class tests batch job {@link ApplyHolidayChangesHelper}. It does not
 * verify that the schedules were correctly rescheduled, but only that the job
 * <em>requests</em> that the applicable accounts reschedule themselves, and that
 * requests are made to the correct holidays to mark themselves as applied.
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplyHolidayChangesHelperTest {

    private ApplyHolidayChangesHelper applyHolidayChangesHelper;
    @Mock private BatchJobConfigurationService mockBatchJobConfigurationService;
    @Mock private AccountPersistence mockAccountPersistence;
    @Mock private HibernateUtil mockHibernateUtil;
    @Mock private HolidayDao mockHolidayDao;
    @Mock private FiscalCalendarRules mockFiscalCalendarRules;
    @Mock private LoanBusinessService mockLoanBusinessService;
    @Mock private Holiday mockHoliday;
    @Mock private LoanBO mockLoanBO;
    @Mock private SavingsBO mockSavingsBO;
    @Mock private CustomerAccountBO mockCustomerAccountBO;
    @Mock private AccountBusinessService mockAccountBusinessService;

    private Integer loanAccountId;
    private Integer savingsAccountId;
    private Integer customerAccountId;
    private List<Holiday> unappliedHolidays;
    private List<Holiday> upcomingHolidays;
    private List<Days> workingDays;
    private List<Integer> listOfLoanAccountIdsInAnUnappliedHoliday;
    private List<Integer> listOfSavingsAccountIdsInAnUnappliedHoliday;
    private List<Integer> listOfCustomerAccountIdsInAnUnappliedHoliday;






    @Before
    public void setupAndInjectMocks() throws Exception {

        resetMocks();

        loanAccountId = new Integer(1);
        savingsAccountId = new Integer(2);
        customerAccountId = new Integer(3);

        // Don't care about unapplied holidays, except whether it's empty or not (non empty triggers the job to
        // actually do something)
        unappliedHolidays = new ArrayList<Holiday>();

        // Don't care about upcoming holidays or working days, they're passed to the account for rescheduling
        upcomingHolidays = new ArrayList<Holiday>();
        workingDays = new ArrayList<Days>();

        listOfLoanAccountIdsInAnUnappliedHoliday = new ArrayList<Integer>();
        listOfSavingsAccountIdsInAnUnappliedHoliday = new ArrayList<Integer>();
        listOfCustomerAccountIdsInAnUnappliedHoliday = new ArrayList<Integer>();

        applyHolidayChangesHelper = new ApplyHolidayChangesHelper(null);

        // inject mocked-up dependencies
        applyHolidayChangesHelper.setAccountPersistence(mockAccountPersistence);
        applyHolidayChangesHelper.setBatchJobConfigurationService(mockBatchJobConfigurationService);
        applyHolidayChangesHelper.setHibernateUtil(mockHibernateUtil);
        applyHolidayChangesHelper.setFiscalCalendarRules(mockFiscalCalendarRules);
        applyHolidayChangesHelper.setHolidayDao(mockHolidayDao);
        applyHolidayChangesHelper.setLoanBusinessService(mockLoanBusinessService);
        applyHolidayChangesHelper.setAccountBusinessService(mockAccountBusinessService);

        // default batch job configuration
        when(mockBatchJobConfigurationService.getBatchSizeForBatchJobs()) .thenReturn(1);
        when(mockBatchJobConfigurationService.getRecordCommittingSizeForBatchJobs()) .thenReturn(1);
        when(mockBatchJobConfigurationService.getOutputIntervalForBatchJobs()) .thenReturn(1);

        // tests should add holidays to these lists before executing the batch job
        when(mockHolidayDao.getUnAppliedHolidays()) .thenReturn (unappliedHolidays);
        when(mockHolidayDao.findAllHolidaysThisYearAndNext()) .thenReturn(upcomingHolidays);

        // Don't care about working days, it's passed to the account for rescheduling
        when(mockFiscalCalendarRules.getWorkingDaysAsJodaTimeDays()) .thenReturn(workingDays);

        // Default is to return an empty list. A test should add Integers to the accountList
        // and mock LoanBusinessService to return one.
        when(mockAccountPersistence.getListOfAccountIdsHavingLoanSchedulesWithinDates(any(DateTime.class), any(DateTime.class)))
                .thenReturn(listOfLoanAccountIdsInAnUnappliedHoliday);
        when(mockAccountPersistence.getListOfAccountIdsHavingSavingsSchedulesWithinDates(any(DateTime.class), any(DateTime.class)))
                .thenReturn(listOfSavingsAccountIdsInAnUnappliedHoliday);
        when(mockAccountPersistence.getListOfAccountIdsHavingCustomerSchedulesWithinDates(any(DateTime.class), any(DateTime.class)))
                .thenReturn(listOfCustomerAccountIdsInAnUnappliedHoliday);

        when(mockAccountBusinessService.getAccount(any(Integer.class))) .thenAnswer(new Answer<AccountBO>() {
            public AccountBO answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                if (((Integer) args[0]).equals(loanAccountId)) {
                    return mockLoanBO;
                } else if (((Integer) args[0]).equals(savingsAccountId)) {
                    return mockSavingsBO;
                } else if (((Integer) args[0]).equals(customerAccountId)) {
                    return mockCustomerAccountBO;
                }
                throw new MifosRuntimeException("getAccount should not have been called with arguments except 1, 2, 3");

            }

        });

    }

    private void resetMocks() {

        mockBatchJobConfigurationService = mock(BatchJobConfigurationService.class);
        mockAccountPersistence = mock(AccountPersistence.class);
        mockHibernateUtil = mock(HibernateUtil.class);
        mockHolidayDao = mock(HolidayDao.class);
        mockFiscalCalendarRules = mock(FiscalCalendarRules.class);
        mockLoanBusinessService = mock(LoanBusinessService.class);
        mockAccountBusinessService = mock( AccountBusinessService.class);

        mockHoliday = mock(Holiday.class);
        mockLoanBO = mock (LoanBO.class);
        mockSavingsBO = mock (SavingsBO.class);
        mockCustomerAccountBO = mock (CustomerAccountBO.class);
    }

    @Test
    public void noUnappliedHolidaysShouldDoNothing() throws Exception {

        // default setup -- list of unapplied holidays is empty

        // exercise test
        applyHolidayChangesHelper.execute(new DateTime().getMillis());

        // verify that the job has not requested any accounts or rescheduling
        verify(mockAccountBusinessService, times(0)).getAccount(any(Integer.class));
        verify(mockLoanBO, times(0)).rescheduleDatesForNewHolidays(any(List.class), any(List.class), any(List.class));
    }

    @Test
    public void loanAccountInUnappliedHolidayShouldForceRescheduling() throws Exception{

        // setup
        listOfLoanAccountIdsInAnUnappliedHoliday.add(loanAccountId); // this should trigger rescheduling the loan
        unappliedHolidays.add(mockHoliday); // this should trigger marking the holiday as applied

        // exercise test
        applyHolidayChangesHelper.execute(new DateTime().getMillis());

        // verify that dependencies were invoked and only one loan account was rescheduled
        verify(mockAccountBusinessService).getAccount(loanAccountId);
        verify(mockAccountBusinessService, times(1)).getAccount(any(Integer.class));
        verify(mockLoanBO).rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);
        verify(mockHoliday).markAsApplied();
    }

    @Test
    public void OnlySavingsAccountInUnappliedHolidayShouldForceReschedulingOnlySavingsAccount() throws Exception{

        // setup
        listOfSavingsAccountIdsInAnUnappliedHoliday.add(savingsAccountId); // this should trigger rescheduling the loan
        unappliedHolidays.add(mockHoliday); // this should trigger marking the holiday as applied

        // exercise test
        applyHolidayChangesHelper.execute(new DateTime().getMillis());

        // verify that dependencies were invoked and only savings account was rescheduled and no other
        // account was accessed or rescheduled and holiday was marked as applied

        verify(mockAccountBusinessService)           .getAccount(savingsAccountId);
        verify(mockAccountBusinessService, never())  .getAccount(loanAccountId);
        verify(mockAccountBusinessService, never())  .getAccount(customerAccountId);
        verify(mockAccountBusinessService, times(1)) .getAccount(any(Integer.class));

        verify(mockSavingsBO)                  .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);
        verify(mockLoanBO, never())            .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);
        verify(mockCustomerAccountBO, never()) .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);

        verify(mockHoliday).markAsApplied();
    }

    @Test
    public void OnlyLoanAccountInUnappliedHolidayShouldForceReschedulingOnlySavingsAccount() throws Exception{

        // setup
        listOfLoanAccountIdsInAnUnappliedHoliday.add(loanAccountId);
        unappliedHolidays.add(mockHoliday); // this should trigger marking the holiday as applied

        // exercise test
        applyHolidayChangesHelper.execute(new DateTime().getMillis());

        // verify that dependencies were invoked and only loan account was rescheduled and no other
        // account was accessed or rescheduled and holiday was marked as applied

        verify(mockAccountBusinessService, never())  .getAccount(savingsAccountId);
        verify(mockAccountBusinessService)           .getAccount(loanAccountId);
        verify(mockAccountBusinessService, never())  .getAccount(customerAccountId);
        verify(mockAccountBusinessService, times(1)) .getAccount(any(Integer.class));

        verify(mockSavingsBO, never())         .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);
        verify(mockLoanBO)                     .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);
        verify(mockCustomerAccountBO, never()) .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);

        verify(mockHoliday).markAsApplied();
    }

    @Test
    public void OnlyCustomerAccountInUnappliedHolidayShouldForceReschedulingOnlySavingsAccount() throws Exception{

        // setup
        listOfCustomerAccountIdsInAnUnappliedHoliday.add(customerAccountId);
        unappliedHolidays.add(mockHoliday); // this should trigger marking the holiday as applied

        // exercise test
        applyHolidayChangesHelper.execute(new DateTime().getMillis());

        // verify that dependencies were invoked and only customer account was rescheduled and no other
        // account was accessed or rescheduled and holiday was marked as applied

        verify(mockAccountBusinessService, never())  .getAccount(savingsAccountId);
        verify(mockAccountBusinessService, never())  .getAccount(loanAccountId);
        verify(mockAccountBusinessService)           .getAccount(customerAccountId);
        verify(mockAccountBusinessService, times(1)) .getAccount(any(Integer.class));

        verify(mockSavingsBO, never())         .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);
        verify(mockLoanBO, never())            .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);
        verify(mockCustomerAccountBO)          .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);

        verify(mockHoliday).markAsApplied();
    }

    @Test
    public void allThreeAccountsInUnappliedHolidayShouldForceReschedulingAllAccounts() throws Exception{

        // setup
        listOfLoanAccountIdsInAnUnappliedHoliday.add(loanAccountId);
        listOfCustomerAccountIdsInAnUnappliedHoliday.add(customerAccountId);
        listOfSavingsAccountIdsInAnUnappliedHoliday.add(savingsAccountId); // this should trigger rescheduling the loan
        unappliedHolidays.add(mockHoliday); // this should trigger marking the holiday as applied

        // exercise test
        applyHolidayChangesHelper.execute(new DateTime().getMillis());

        // verify that dependencies were invoked and only customer account was rescheduled and no other
        // account was accessed or rescheduled and holiday was marked as applied

        verify(mockAccountBusinessService)           .getAccount(savingsAccountId);
        verify(mockAccountBusinessService)           .getAccount(loanAccountId);
        verify(mockAccountBusinessService)           .getAccount(customerAccountId);
        verify(mockAccountBusinessService, times(3)) .getAccount(any(Integer.class));

        verify(mockSavingsBO)                  .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);
        verify(mockLoanBO)                     .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);
        verify(mockCustomerAccountBO)          .rescheduleDatesForNewHolidays(workingDays, upcomingHolidays, unappliedHolidays);

        verify(mockHoliday).markAsApplied();
    }

}
