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

package org.mifos.customers.business;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.business.FeeFrequencyEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.calendar.DayOfWeek;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerAccountBOTest {

    private CustomerAccountBO customerAccount;

    @Mock private UserContext userContext;
    @Mock private CustomerBO customer;
    @Mock private CustomerStatusEntity customerStatus;
    @Mock private FeeBO weeklyFee;
    @Mock private FeeBO biWeeklyFee;
    @Mock private FeeBO oneTimeUpFrontFee;
    @Mock private FeeFrequencyEntity weeklyFeeFrequencyEntity;
    @Mock private FeeFrequencyEntity biWeeklyFeeFrequencyEntity;
    @Mock private FeeFrequencyEntity oneTimeFeeFrequencyEntity;
    @Mock private FeePaymentEntity oneTimeFeePaymentEntity;
    @Mock private PersonnelPersistence personnelPersistence;


    private final String weeklyFeeName = "Fee 1";
    private final String biWeeklyFeeName = "Fee 2";
    private final String oneTimeFeeName = "Fee 3";
    private MeetingBO defaultWeeklyCustomerMeeting;
    private MeetingBO feeMeetingEveryWeek;
    private MeetingBO biWeeklyFeeMeeting;

    private final List<FeeDto> fees = new ArrayList<FeeDto>();
    private List<Days> workingDays;
    private List<Holiday> holidays;
    private List<AccountFeesEntity> accountFees;
    private Holiday moratorium;

    private DateTimeService dateTimeService = new DateTimeService();

    @BeforeClass
    public static void setupDefaultCurrency() {
        MifosCurrency defaultCurrency = TestUtils.EURO;
        Money.setDefaultCurrency(defaultCurrency);
    }

    @Before
    public void setupAndInjectDependencies() throws Exception {

        workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek.wednesdayAsDay(), DayOfWeek.thursdayAsDay(), DayOfWeek.fridayAsDay());

    }

    @Before
    public void setupDefaultObjects() throws Exception {
        dateTimeService.resetToCurrentSystemDateTime();
        defaultWeeklyCustomerMeeting = new MeetingBuilder().every(1).weekly().occuringOnA(WeekDay.MONDAY).build();

        when(customer.getCustomerMeetingValue()).thenReturn(defaultWeeklyCustomerMeeting);
        when(customer.getUserContext()).thenReturn(TestUtils.makeUser());
//        getCustomer().getCustomerStatus().getId().equals(CustomerConstants.CLIENT_APPROVED)
        when(customer.getCustomerStatus())  .thenReturn(customerStatus);
        when(customer.getCustomerMeetingValue())  .thenReturn(defaultWeeklyCustomerMeeting);

        when(customerStatus.getId())    .thenReturn(CustomerConstants.CLIENT_APPROVED);


        holidays = new ArrayList<Holiday>();
        accountFees = new ArrayList<AccountFeesEntity>();
        moratorium = new HolidayBuilder().from(new DateTime().plusWeeks(2))
                                         .to(new DateTime().plusWeeks(3).minusDays(1))
                                         .withRepaymentMoratoriumRule()
                                         .build();
        feeMeetingEveryWeek = new MeetingBuilder().withSameRecurrenceAs(defaultWeeklyCustomerMeeting)
                                                  .occuringOnA(WeekDay.MONDAY)
                                                  .build();
        biWeeklyFeeMeeting = new MeetingBuilder().every(2)
                                                 .weekly()
                                                 .occuringOnA(WeekDay.MONDAY)
                                                 .periodicFeeMeeting().build();

        when(weeklyFee.getFeeId())                             .thenReturn((short) 1);
        when(weeklyFee.getFeeFrequency())                      .thenReturn(weeklyFeeFrequencyEntity);
        when(weeklyFee.getFeeName())                           .thenReturn(weeklyFeeName);
        when(weeklyFeeFrequencyEntity.getFeeMeetingFrequency()).thenReturn(feeMeetingEveryWeek);


        when(biWeeklyFee.getFeeId())                           .thenReturn((short) 2);
        when(biWeeklyFee.getFeeFrequency())                    .thenReturn(biWeeklyFeeFrequencyEntity);
        when(biWeeklyFee.getFeeName())                         .thenReturn(biWeeklyFeeName);
        when(biWeeklyFeeFrequencyEntity.getFeeMeetingFrequency()).thenReturn(biWeeklyFeeMeeting);

        when(oneTimeUpFrontFee.getFeeId())                     .thenReturn((short) 3);
        when(oneTimeUpFrontFee.getFeeFrequency())                    .thenReturn(oneTimeFeeFrequencyEntity);
        when(oneTimeFeeFrequencyEntity.getFeePayment())        .thenReturn(oneTimeFeePaymentEntity);

        when(personnelPersistence.getPersonnel(anyShort())) .thenReturn(null);

        customerAccount = new CustomerAccountBO(userContext, customer, fees);
        customerAccount.setPersonnelPersistence(personnelPersistence);

    }

    @After
    public void tearDown() {
        dateTimeService.resetToCurrentSystemDateTime();
    }

    /**********************************************************
     * Test generateNextSetOfMeetingDates
     **********************************************************/

    @Test
    public void canGenerateSchedulesMatchingMeetingRecurrenceWhenNoPreviousSchedulesExisted() {

        // use default setup

        // exercise test
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        // verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));

        Short lastInstallment = Short.valueOf("1");
        DateTime lastInstallmentDate = new DateTime().withDayOfWeek(DayOfWeek.monday());
        if (lastInstallmentDate.isBeforeNow() || lastInstallmentDate.isEqualNow()) {
            lastInstallmentDate = lastInstallmentDate.plusWeeks(1);
        }

        for (AccountActionDateEntity scheduledDate : customerAccount.getAccountActionDates()) {
            assertThat(scheduledDate.getInstallmentId(), is(lastInstallment));
            assertThat(new LocalDate(scheduledDate.getActionDate()), is(new LocalDate(lastInstallmentDate.toDate())));

            lastInstallment++;
            lastInstallmentDate = lastInstallmentDate.plusWeeks(1);
        }
    }

    @Test
    public void canGenerateNextSetOfMeetingDatesWithFreshlyGeneratedSchedule() {

        // use default setup

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);

        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(20));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());

        for (AccountActionDateEntity scheduledDate : customerAccount.getAccountActionDates()) {
            assertThat(scheduledDate.getInstallmentId(), is(installmentId));
            assertThat(new LocalDate(scheduledDate.getActionDate()), is(new LocalDate(installmentDate.toDate())));

            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    @Ignore
    @Test
    public void generateNextSetOfMeetingDatesDoesNotApplyFeesToNextTenEvents() {
        // setup
        accountFees.add(createAccountFeesEntity(weeklyFee, 10.0));

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(20));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());
        List<AccountActionDateEntity> actionDates = getActionDatesSortedByDate(customerAccount);

        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(customerAccount)) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            if (installmentId <= 10) {
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(1));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(), is(weeklyFeeName));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(), is(10.0));
            } else {
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(0));

            }
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
        assertThat(customerAccount.getAccountFees().size(), is(1));
        AccountFeesEntity accountFeeEntity = new ArrayList<AccountFeesEntity>(customerAccount.getAccountFees()).get(0);
        // Last applied date for the fee is the date of the last meeting date, which is the last date that
        // the fee was applied
        assertThat(new LocalDate(accountFeeEntity.getLastAppliedDate()),
                is (new LocalDate(actionDates.get(9).getActionDate())));
    }

    @Ignore
    @Test
    public void applyPeriodicFeesToFreshlyGeneratedScheduleDoesNothing() {
        // setup
        accountFees.add(createAccountFeesEntity(weeklyFee, 10.0));

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        customerAccount.applyPeriodicFees(workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());
        List<AccountActionDateEntity> actionDates = getActionDatesSortedByDate(customerAccount);

        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(customerAccount)) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            // When the schedule is first created, periodic fees are applied to each meeting date.
            assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(1));
            assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(), is(weeklyFeeName));
            assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(), is(10.0));
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
        assertThat(customerAccount.getAccountFees().size(), is(1));
        AccountFeesEntity accountFeeEntity = new ArrayList<AccountFeesEntity>(customerAccount.getAccountFees()).get(0);
        // The last date that the fee was applied is the 10th meeting date
        assertThat(new LocalDate(accountFeeEntity.getLastAppliedDate()),
                is (new LocalDate(actionDates.get(9).getActionDate())));
    }

    @Ignore
    @Test
    public void applyPeriodicFeesAfterGeneratingNextSetOfMeetingDatesAppliesFeeToTenthhMeeting() {
        // setup
        accountFees.add(createAccountFeesEntity(weeklyFee, 10.0));
        DateTime startingMeetingDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        // Advance time to the day after the 10th installment
        dateTimeService.setCurrentDateTime(startingMeetingDate.plusWeeks(9).plusDays(1));
        // Should apply the fee to the next upcoming installment, the 11th
        customerAccount.applyPeriodicFees(workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(20));
        Short installmentId = Short.valueOf("1");
        List<AccountActionDateEntity> actionDates = getActionDatesSortedByDate(customerAccount);
        DateTime installmentDate = startingMeetingDate;

        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(customerAccount)) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat("Installment id", scheduleEntity.getInstallmentId(), is(installmentId));
            assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            if (installmentId <= 11) {
                assertThat("Installment " + installmentId + " number of fees",
                        scheduleEntity.getAccountFeesActionDetails().size(), is(1));
                AccountFeesActionDetailEntity feeDetail = scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0);
                assertThat("Installment " + installmentId + " fee name",
                        feeDetail.getFee().getFeeName(), is(weeklyFeeName));
                assertThat("Installment " + installmentId + " fee amount",
                        feeDetail.getFeeAmount().getAmountDoubleValue(), is(10.0));
                assertThat("Installment " + installmentId + " fee due ",
                        feeDetail.getFeeDue().getAmountDoubleValue(), is(10.0));
            } else {
                assertThat("Installment " + installmentId + " number of fees",
                        scheduleEntity.getAccountFeesActionDetails().size(), is(0));
            }
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
        assertThat(customerAccount.getAccountFees().size(), is(1));
        AccountFeesEntity accountFeeEntity = new ArrayList<AccountFeesEntity>(customerAccount.getAccountFees()).get(0);
        // Last applied date for the fee is the date of the 11th meeting date, where the fee was applied
        assertThat(new LocalDate(accountFeeEntity.getLastAppliedDate()),
                is (new LocalDate(actionDates.get(10).getActionDate())));
    }

    @Test
    public void canGenerateNextSetOfMeetingDatesWhenAllButThreeDatesAreInThePast() {

        // use default setup

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);

        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());

        dateTimeService.setCurrentDateTime(dateTimeService.getCurrentDateTime().plusWeeks(7));

        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(20));
        Short installmentId = Short.valueOf("1");

        for (AccountActionDateEntity scheduledDate : customerAccount.getAccountActionDates()) {
            assertThat(scheduledDate.getInstallmentId(), is(installmentId));
            assertThat(new LocalDate(scheduledDate.getActionDate()), is(new LocalDate(installmentDate.toDate())));

            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    @Test
    public void canGenerateNextSetOfMeetingDatesWhenAllButThreeDatesAreInThePastWithMoratoriumShouldShiftDatesOneWeekStartingWith12thDate() {

        // setup
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());
        Holiday holiday =  new HolidayBuilder().from(installmentDate.plusWeeks(11))
                                               .to(new DateTime().plusWeeks(12).minusDays(1))
                                               .withRepaymentMoratoriumRule()
                                               .build();
        holidays.add(holiday);


        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);

        dateTimeService.setCurrentDateTime(dateTimeService.getCurrentDateTime().plusWeeks(7));

        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(20));
        Short installmentId = Short.valueOf("1");

        for (AccountActionDateEntity scheduledDate : customerAccount.getAccountActionDates()) {
            assertThat(scheduledDate.getInstallmentId(), is(installmentId));
            if (installmentId < 12) {
                assertThat(new LocalDate(scheduledDate.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            } else {
                assertThat(new LocalDate(scheduledDate.getActionDate()), is(new LocalDate(installmentDate.plusWeeks(1).toDate())));
            }

            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    /******************************************************************
     * Test generated meeting dates for new customer.
     *   - with and without fees
     *   - with and without moratorium adjustment
     ******************************************************************/

    @Test
    public void createNewWeeklyCustomerAccountNoFeesNoHolidayGeneratesCorrectSchedule() {

        // use default setup

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());

        for (AccountActionDateEntity scheduledDate : customerAccount.getAccountActionDates()) {
            assertThat(scheduledDate.getInstallmentId(), is(installmentId));
            assertThat(new LocalDate(scheduledDate.getActionDate()), is(new LocalDate(installmentDate.toDate())));

            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    @Test
    public void createNewWeeklyCustomerAccountNoFeesWithMoratoriumGeneratesCorrectSchedule() {

        // setup
        holidays.add(moratorium);

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());

        //Third through 10th dates should get pushed out one week
        for (AccountActionDateEntity scheduledDate : getActionDatesSortedByDate(customerAccount)) {
            assertThat(scheduledDate.getInstallmentId(), is(installmentId));
            if (installmentId <= 2) {
                assertThat(new LocalDate(scheduledDate.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            } else {
                assertThat(new LocalDate(scheduledDate.getActionDate()), is(new LocalDate(installmentDate.plusWeeks(1).toDate())));
            }

            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    @Test
    public void createNewWeeklyCustomerAccountOnePeriodicFeeNoHolidayGeneratesCorrectFeeScheduleAndSetsFeeLastAppliedDateToLastMeetingDate() {

        // setup
        accountFees.add(createAccountFeesEntity(weeklyFee, 10.0));

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());
        List<AccountActionDateEntity> actionDates = getActionDatesSortedByDate(customerAccount);

        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(customerAccount)) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(1));
            assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(), is(weeklyFeeName));
            assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(), is(10.0));
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
        assertThat(customerAccount.getAccountFees().size(), is(1));
        AccountFeesEntity accountFeeEntity = new ArrayList<AccountFeesEntity>(customerAccount.getAccountFees()).get(0);
        // Last applied date for the fee is the date of the last meeting date
        assertThat(new LocalDate(accountFeeEntity.getLastAppliedDate()),
                is (new LocalDate(actionDates.get(9).getActionDate())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createNewWeeklyCustomerAccountTwoPeriodicFeesNoHolidayGeneratesCorrectFeeSchedule() {

        // setup
        accountFees.add(createAccountFeesEntity(weeklyFee, 10.0));
        accountFees.add(createAccountFeesEntity(biWeeklyFee, 13.0));
        DateTime startingDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));

        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = startingDate;
        List<AccountActionDateEntity> actionDates = getActionDatesSortedByDate(customerAccount);

        for (AccountActionDateEntity accountActionDate : actionDates) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            if ( installmentId % 2 != 0) { // odd-numbered events have both fees applied
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(2));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(), is(weeklyFeeName));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(), is(10.0));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(1).getFee().getFeeName(), is(biWeeklyFeeName));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(1).getFeeAmount().getAmountDoubleValue(), is(13.0));
            } else {
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(1));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(), is(weeklyFeeName));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(), is(10.0));
            }
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }

        // Verify that account fees are stored as expected
        Set<AccountFeesEntity> accountFeeEntities = customerAccount.getAccountFees();
        assertThat(accountFeeEntities.size(), is(2));
        for (AccountFeesEntity feeEntity : accountFeeEntities) {
            assertThat(feeEntity.getFees().getFeeName(), anyOf(is(weeklyFeeName), is(biWeeklyFeeName)));
        }

        // Verify each fee's last applied date
        for (AccountFeesEntity feeEntity : customerAccount.getAccountFees()) {
            // Weekly fee is applied to all 10 meeting events
            if (feeEntity.getFees().getFeeName().equals(weeklyFeeName)) {
                assertThat(new LocalDate(feeEntity.getLastAppliedDate()),
                        is (new LocalDate(actionDates.get(9).getActionDate())));
            }
            // Biweekly fee is applied to meetings 1, 3, 5, 7, 9
            else if (feeEntity.getFees().getFeeName().equals(biWeeklyFeeName)) {
                assertThat(new LocalDate(feeEntity.getLastAppliedDate()),
                        is (new LocalDate(actionDates.get(8).getActionDate())));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createNewWeeklyCustomerAccountTwoPeriodicFeesWithMoratoriumGeneratesCorrectFeeSchedule() {

        // setup
        accountFees.add(createAccountFeesEntity(weeklyFee, 10.0));
        accountFees.add(createAccountFeesEntity(biWeeklyFee, 13.0));
        holidays.add(moratorium);
        DateTime startingDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        Short installmentId = Short.valueOf("1");
        List<AccountActionDateEntity> actionDates = getActionDatesSortedByDate(customerAccount);
        DateTime installmentDate = startingDate;

        for (AccountActionDateEntity accountActionDate : actionDates) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            // Third and subsequent installments are pushed out one week
            if (installmentId <= 2) {
                assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            } else {
                assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.plusWeeks(1).toDate())));
            }
            if ( installmentId % 2 != 0) { // odd-numbered occurrences have both fees applied
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(2));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(), is(weeklyFeeName));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(), is(10.0));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(1).getFee().getFeeName(), is(biWeeklyFeeName));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(1).getFeeAmount().getAmountDoubleValue(), is(13.0));
            } else { // odd-numbered events have only the weekly fee applied
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(1));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(), is(weeklyFeeName));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(), is(10.0));
            }
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }

        // Verify that account fees are stored as expected
        Set<AccountFeesEntity> accountFeeEntities = customerAccount.getAccountFees();
        assertThat(accountFeeEntities.size(), is(2));
        for (AccountFeesEntity feeEntity : accountFeeEntities) {
            assertThat(feeEntity.getFees().getFeeName(), anyOf(is(weeklyFeeName), is(biWeeklyFeeName)));
        }

        // Verify each fee's last applied date
        for (AccountFeesEntity feeEntity : customerAccount.getAccountFees()) {
            // Weekly fee is applied to all 10 meeting events
            if (feeEntity.getFees().getFeeName().equals(weeklyFeeName)) {
                assertThat(new LocalDate(feeEntity.getLastAppliedDate()),
                        is (new LocalDate(actionDates.get(9).getActionDate())));
            }
            // Biweekly fee is applied to meetings 1, 3, 5, 7, 9
            else if (feeEntity.getFees().getFeeName().equals(biWeeklyFeeName)) {
                assertThat(new LocalDate(feeEntity.getLastAppliedDate()),
                        is (new LocalDate(actionDates.get(8).getActionDate())));
            }
        }
    }

    @Ignore
    @Test
    public void createNewWeeklyCustomerAccountOnePeriodicFeeWithMoratoriumGeneratesCorrectFeeSchedule() {

        // setup
        holidays.add(moratorium);
        accountFees.add(createAccountFeesEntity(weeklyFee, 10.0));

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());

        //Third through 10th installments pushed out one week. Fees are unchanged, remain attached to installments
        //even when the installment is pushed out

        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(customerAccount)) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            if (installmentId <= 2) {
                assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            } else {
                assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.plusWeeks(1).toDate())));
            }
            assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(1));
            assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(), is(weeklyFeeName));
            assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(), is(10.0));
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }


    /*********************************
     * Test applying a periodic fee. This is the method used by batch job ApplyCustomerFee, to add fees to
     * future customer meeting dates.
     ********************************/

    /*********************************
     * Test applying charges - verify added to correct installments
     * It is nearly impossible to unit-test applying charges because persistence calls
     * infest the class. See CustomerApplyFeesIntegrationTest
     ***********************************/

    /**************************************
     * Helper methods
     *************************************/

    private DateTime getFirstInstallmentDateForWeeklyScheduleStartingNow(int dayOfWeek) {
        DateTime installmentDate = new DateTime().withDayOfWeek(dayOfWeek);
        if (installmentDate.isBeforeNow()) {
            installmentDate = installmentDate.plusWeeks(1);
        }
        return installmentDate;
    }

    private List<AccountActionDateEntity> getActionDatesSortedByDate(CustomerAccountBO customerAccount) {
        List<AccountActionDateEntity> sortedList = new ArrayList<AccountActionDateEntity>();
        sortedList.addAll(customerAccount.getAccountActionDates());
        Collections.sort(sortedList);
        return sortedList;
    }


    private AccountFeesEntity createAccountFeesEntity (FeeBO feeBO, double feeAmount) {
        AccountFeesEntity accountFeesEntity
            = new AccountFeesEntity(null, feeBO, feeAmount);
        return accountFeesEntity;
    }

 }
