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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateMidnight;
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
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.business.FeeFrequencyEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
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

import com.sun.xml.bind.v2.TODO;

@RunWith(MockitoJUnitRunner.class)
public class CustomerAccountBOTest {

    private CustomerAccountBO customerAccount;

    @Mock private UserContext userContext;
    @Mock private CustomerBO customer;
    @Mock private CustomerStatusEntity customerStatus;
    @Mock private AmountFeeBO weeklyFee;
    @Mock private AmountFeeBO biWeeklyFee;
    @Mock private AmountFeeBO oneTimeUpFrontFee;
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
    private static MifosCurrency defaultCurrency;
    private DateTime firstInstallmentDate;
    private AccountFeesEntity weeklyAccountFee;
    private AccountFeesEntity biWeeklyAccountFee;


    private DateTimeService dateTimeService = new DateTimeService();

    @BeforeClass
    public static void setupDefaultCurrency() {
        defaultCurrency = TestUtils.RUPEE;
        Money.setDefaultCurrency(defaultCurrency);
    }

    @Before
    public void setupAndInjectDependencies() throws Exception {

        workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek.wednesdayAsDay(), DayOfWeek.thursdayAsDay(), DayOfWeek.fridayAsDay());

    }

    @Before
    public void setupDefaultObjects() throws Exception {
        dateTimeService.resetToCurrentSystemDateTime();
        dateTimeService.setCurrentDateTime(new DateTime(2010, 5, 18, 0, 0, 0, 0));
        defaultWeeklyCustomerMeeting = new MeetingBuilder().every(1).weekly().occuringOnA(WeekDay.MONDAY).build();

        when(customer.getCustomerMeetingValue()).thenReturn(defaultWeeklyCustomerMeeting);
        when(customer.getUserContext()).thenReturn(TestUtils.makeUser());
//        getCustomer().getCustomerStatus().getId().equals(CustomerConstants.CLIENT_APPROVED)
        when(customer.getCustomerStatus())  .thenReturn(customerStatus);
        when(customer.getCustomerMeetingValue())  .thenReturn(defaultWeeklyCustomerMeeting);

        when(customerStatus.getId())    .thenReturn(CustomerConstants.CLIENT_APPROVED);


        holidays = new ArrayList<Holiday>();
        accountFees = new ArrayList<AccountFeesEntity>();
        // moratorium covers third weekly installment
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
        when(weeklyFee.isPeriodic())                           .thenReturn(true);

        weeklyAccountFee = createAccountFeesEntity(weeklyFee, "10.0");
        biWeeklyAccountFee = createAccountFeesEntity(biWeeklyFee, "13.0");


        when(biWeeklyFee.getFeeId())                           .thenReturn((short) 2);
        when(biWeeklyFee.getFeeFrequency())                    .thenReturn(biWeeklyFeeFrequencyEntity);
        when(biWeeklyFee.getFeeName())                         .thenReturn(biWeeklyFeeName);
        when(biWeeklyFeeFrequencyEntity.getFeeMeetingFrequency()).thenReturn(biWeeklyFeeMeeting);
        when(biWeeklyFee.isPeriodic())                           .thenReturn(true);

        when(oneTimeUpFrontFee.getFeeId())                     .thenReturn((short) 3);
        when(oneTimeUpFrontFee.getFeeFrequency())                    .thenReturn(oneTimeFeeFrequencyEntity);
        when(oneTimeFeeFrequencyEntity.getFeePayment())        .thenReturn(oneTimeFeePaymentEntity);

        when(personnelPersistence.getPersonnel(anyShort())) .thenReturn(null);

        customerAccount = new CustomerAccountBO(userContext, customer, fees);
        customerAccount.setPersonnelPersistence(personnelPersistence);

        firstInstallmentDate = getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday());

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
        verifyWeeklyMeetingDates((short) 1, (short) 10, getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday()));
    }

    @Test
    public void canGenerateNextSetOfMeetingDatesWithFreshlyGeneratedSchedule() {

        // use default setup

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDates(1, 20, firstInstallmentDate);
    }

    @Test
    public void generateNextSetOfMeetingDatesDoesNotApplyFeesToNextTenEvents() {
        // setup
        accountFees.add(weeklyAccountFee);

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDates(1, 20, firstInstallmentDate);
        verifyOneFeeForInstallmentsInRange(1, 10, weeklyAccountFee);
        verifyOneAccountFee(weeklyAccountFee);
        verifyLastAppliedDateForFee (weeklyAccountFee, firstInstallmentDate.plusWeeks(9).toDate());
    }

    @Test
    public void applyPeriodicFeesToFreshlyGeneratedScheduleDoesNothing() {
        // setup
        AccountFeesEntity accountFee = createAccountFeesEntity(weeklyFee, "10.0");
        accountFees.add(accountFee);

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);
        customerAccount.applyPeriodicFees(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDates(1, 10, firstInstallmentDate);
        verifyOneFeeForInstallmentsInRange(1, 10, accountFee);
        verifyOneAccountFee(accountFee);
        verifyLastAppliedDateForFee (accountFee, firstInstallmentDate.plusWeeks(9).toDate());
    }

    @Test
    public void applyPeriodicFeesAfterGeneratingNextSetOfMeetingDatesAppliesFeeToAllNewMeetingDates() {

        // setup
        accountFees.add(weeklyAccountFee);

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        // Advance time to the day after the 10th installment
        dateTimeService.setCurrentDateTime(firstInstallmentDate.plusWeeks(9).plusDays(1));
        // Should apply the fee to the next upcoming installment, the 11th
        customerAccount.applyPeriodicFees(workingDays, holidays);

        //verification
        //verification
        verifyWeeklyMeetingDates(1, 20, firstInstallmentDate);
        verifyOneFeeForInstallmentsInRange(1, 20, weeklyAccountFee);
        verifyOneAccountFee(weeklyAccountFee);
        verifyLastAppliedDateForFee (weeklyAccountFee, firstInstallmentDate.plusWeeks(19).toDate());
    }

    @Test
    public void generateNextSetOfMeetingDates11thDateInNextMeetingHolidayShouldPush11thMeetingOneWeekForward() {

        // setup
        Holiday nextMeetingDayHolidaySpansEleventhMeeting
            = new HolidayBuilder().from(new DateTime().plusWeeks(10))
                                  .to(new DateTime().plusWeeks(11).minusDays(1))
                                  .withRepaymentRule(RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT)
                                  .build();
        holidays.add(nextMeetingDayHolidaySpansEleventhMeeting);


        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);

        verifyWeeklyMeetingDates(1, 10, firstInstallmentDate);

        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDates(11, 11, firstInstallmentDate.plusWeeks(11));
        verifyWeeklyMeetingDates(12, 20, firstInstallmentDate.plusWeeks(11));
   }

    @Test
    public void applyPeriodicFeesInNextMeetingHolidayShouldApplySingleFeeToAllInstallments() {

        // setup
        accountFees.add(weeklyAccountFee);
        Holiday nextMeetingDayHolidaySpansEleventhMeeting
            = new HolidayBuilder().from(new DateTime().plusWeeks(10))
                                  .to(new DateTime().plusWeeks(11).minusDays(1))
                                  .withRepaymentRule(RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT)
                                  .build();
        holidays.add(nextMeetingDayHolidaySpansEleventhMeeting);


        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);

        verifyWeeklyMeetingDates(1, 10, firstInstallmentDate);

        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        // Advance time to the day after the 10th installment
        dateTimeService.setCurrentDateTime(firstInstallmentDate.plusWeeks(9).plusDays(1));
        // Should apply the fee to the next upcoming installment, the 11th
        customerAccount.applyPeriodicFees(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDates(11, 11, firstInstallmentDate.plusWeeks(11));
        verifyWeeklyMeetingDates(12, 20, firstInstallmentDate.plusWeeks(11));
        verifyOneFeeForInstallmentsInRange(1, 20, weeklyAccountFee);
        verifyOneAccountFee(weeklyAccountFee);
        verifyLastAppliedDateForFee (weeklyAccountFee, firstInstallmentDate.plusWeeks(19).toDate());
   }

    @Test
    public void applyPeriodicFeesInMoratoriumShouldApplySingleFeeToAllInstallments() {

        // setup
        accountFees.add(weeklyAccountFee);
        Holiday moratoriumSpans12thMeeting
            = new HolidayBuilder().from(new DateTime().plusWeeks(11))
                                  .to(new DateTime().plusWeeks(12).minusDays(1))
                                  .withRepaymentMoratoriumRule()
                                  .build();
        holidays.add(moratoriumSpans12thMeeting);


        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);

        verifyWeeklyMeetingDates(1, 10, firstInstallmentDate);

        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        // Advance time to the day after the 10th installment
        dateTimeService.setCurrentDateTime(firstInstallmentDate.plusWeeks(9).plusDays(1));
        // Should apply the fee to the next upcoming installment, the 11th
        customerAccount.applyPeriodicFees(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDates(11, 11, firstInstallmentDate.plusWeeks(10));
        verifyWeeklyMeetingDates(12, 20, firstInstallmentDate.plusWeeks(12));
        verifyOneFeeForInstallmentsInRange(1, 20, weeklyAccountFee);
        verifyOneAccountFee(weeklyAccountFee);
        verifyLastAppliedDateForFee (weeklyAccountFee, firstInstallmentDate.plusWeeks(20).toDate());
   }

    @Test
    public void canGenerateNextSetOfMeetingDatesWhenAllButThreeDatesAreInThePast() {

        // use default setup

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);
        dateTimeService.setCurrentDateTime(dateTimeService.getCurrentDateTime().plusWeeks(7));
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDates(1, 20, firstInstallmentDate);
    }

    @Test
    public void canGenerateNextSetOfMeetingDatesWhenAllButThreeDatesAreInThePastWithMoratoriumShouldShiftDatesOneWeekStartingWith12thDate() {

        // setup
        Holiday holiday =  new HolidayBuilder().from(firstInstallmentDate.plusWeeks(11))
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
        verifyWeeklyMeetingDates(1, 11, firstInstallmentDate);
        verifyWeeklyMeetingDates(12, 20, firstInstallmentDate.plusWeeks(12));
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
        verifyWeeklyMeetingDates(1, 1, firstInstallmentDate);
    }

    @Test
    public void createNewWeeklyCustomerAccountNoFeesWithMoratoriumGeneratesCorrectSchedule() {

        // setup
        holidays.add(moratorium);

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        verifyWeeklyMeetingDates(1, 2, firstInstallmentDate);
        verifyWeeklyMeetingDates(3, 10, firstInstallmentDate.plusWeeks(3));
    }

    @Test
    public void createNewWeeklyCustomerAccountOnePeriodicFeeNoHolidayGeneratesCorrectFeeScheduleAndSetsFeeLastAppliedDateToLastMeetingDate() {

        // setup
        accountFees.add(weeklyAccountFee);

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        verifyWeeklyMeetingDates(1, 10, firstInstallmentDate);
        verifyOneFeeForInstallmentsInRange(1, 10, weeklyAccountFee);
        verifyOneAccountFee(weeklyAccountFee);
        verifyLastAppliedDateForFee (weeklyAccountFee, firstInstallmentDate.plusWeeks(9).toDate());
    }

//    @SuppressWarnings("unchecked")
    @Test
    public void createNewWeeklyCustomerAccountTwoPeriodicFeesNoHolidayGeneratesCorrectFeeSchedule() {

        // setup
        accountFees.add(weeklyAccountFee);
        accountFees.add(biWeeklyAccountFee);

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        verifyWeeklyMeetingDates(1, 10, firstInstallmentDate);

        Short installmentId = Short.valueOf("1");
        List<AccountActionDateEntity> actionDates = getActionDatesSortedByDate(customerAccount);

        for (AccountActionDateEntity accountActionDate : actionDates) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
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
        }

        // Verify that account fees are stored as expected
        verifyAccountHasAccountFees(weeklyAccountFee, biWeeklyAccountFee);

        // Verify each fee's last applied date
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(9).toDate());
        verifyLastAppliedDateForFee(biWeeklyAccountFee, firstInstallmentDate.plusWeeks(8).toDate());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createNewWeeklyCustomerAccountTwoPeriodicFeesWithMoratoriumOnThirdMeetingGeneratesCorrectFeeSchedule() {

        // setup
        accountFees.add(weeklyAccountFee);
        accountFees.add(biWeeklyAccountFee);
        holidays.add(moratorium);

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        verifyWeeklyMeetingDates(1, 2, firstInstallmentDate);
        verifyWeeklyMeetingDates(3, 10, firstInstallmentDate.plusWeeks(3));

        Short installmentId = Short.valueOf("1");
        List<AccountActionDateEntity> actionDates = getActionDatesSortedByDate(customerAccount);

        for (AccountActionDateEntity accountActionDate : actionDates) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            // Third and subsequent installments are pushed out one week
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
        }

        // Verify that account fees are stored as expected
        verifyAccountHasAccountFees(weeklyAccountFee, biWeeklyAccountFee);
        // Last applied dates are pushed out one week
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(10).toDate());
        verifyLastAppliedDateForFee(biWeeklyAccountFee, firstInstallmentDate.plusWeeks(9).toDate());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void createNewWeeklyCustomerAccountTwoPeriodicFeesWithMoratoriumOnSecondMeetingGeneratesCorrectFeeSchedule() {

        // setup
        accountFees.add(weeklyAccountFee);
        accountFees.add(biWeeklyAccountFee);
        Holiday moratoriumOnSecondMeeting =  new HolidayBuilder().from(firstInstallmentDate.plusWeeks(1))
                                                                 .to(new DateTime().plusWeeks(2).minusDays(1))
                                                                 .withRepaymentMoratoriumRule()
                                                                 .build();
        holidays.add(moratoriumOnSecondMeeting);

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        verifyWeeklyMeetingDates(1, 1, firstInstallmentDate);
        verifyWeeklyMeetingDates(2, 10, firstInstallmentDate.plusWeeks(2));

        Short installmentId = Short.valueOf("1");
        List<AccountActionDateEntity> actionDates = getActionDatesSortedByDate(customerAccount);

        for (AccountActionDateEntity accountActionDate : actionDates) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            // Third and subsequent installments are pushed out one week
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
        }

        // Verify that account fees are stored as expected
        verifyAccountHasAccountFees(weeklyAccountFee, biWeeklyAccountFee);
        // Last applied dates are pushed out one week
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(10).toDate());
        verifyLastAppliedDateForFee(biWeeklyAccountFee, firstInstallmentDate.plusWeeks(9).toDate());

    }

    @Test
    public void createNewWeeklyCustomerAccountOnePeriodicFeeWithMoratoriumGeneratesCorrectFeeSchedule() {

        // setup
        holidays.add(moratorium);
        accountFees.add(weeklyAccountFee);

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        verifyWeeklyMeetingDates(1, 2, firstInstallmentDate);
        verifyWeeklyMeetingDates(3, 10, firstInstallmentDate.plusWeeks(3));

        //Third through 10th installments pushed out one week. Fees are unchanged, remain attached to installments
        //even when the installment is pushed out
        verifyOneFeeForInstallmentsInRange(1, 10, weeklyAccountFee);
        verifyAccountHasAccountFees(weeklyAccountFee);
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(10).toDate());
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
        DateTime installmentDate = new DateMidnight().toDateTime().withDayOfWeek(dayOfWeek);
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


    private AccountFeesEntity createAccountFeesEntity (AmountFeeBO feeBO, String feeAmount) {
        when(feeBO.getFeeAmount()) .thenReturn(new Money(defaultCurrency, feeAmount));
        AccountFeesEntity accountFeesEntity
            = new AccountFeesEntity(null, feeBO, new Double(feeAmount));
        return accountFeesEntity;
    }

    private void verifyWeeklyMeetingDates (int startingWithInstallment, int throughInstallment, DateTime startingDate) {

        DateTime installmentDate = startingDate;
        List<AccountActionDateEntity> accountActionDates = getActionDatesSortedByDate(customerAccount);
        for (short installmentId = (short) startingWithInstallment; installmentId <= throughInstallment; installmentId++) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDates.get(installmentId-1);
            assertThat("Installment " + installmentId, new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    private void verifyOneFeeForInstallmentsInRange (int start, int end, AccountFeesEntity accountFees) {

        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(customerAccount)) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            if ((accountActionDate.getInstallmentId() >= start) && (accountActionDate.getInstallmentId() <= end)) {
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(1));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(),
                        is(accountFees.getFees().getFeeName()));
                assertThat("Fee applied to installment " + scheduleEntity.getInstallmentId(), scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(),
                        is(accountFees.getFeeAmount()));
            } else {
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(0));

            }
        }
    }

    /**
     * @param accountFees the list of expected fees applied to the account, must be sorted by fee name
     */
    private void verifyAccountHasAccountFees (AccountFeesEntity...accountFees) {
        assertThat(customerAccount.getAccountFees().size(), is(accountFees.length));
        int i = 0;
        for (AccountFeesEntity accountFeeEntity : getAccountFeesSortedByFeeName()) {
            assertThat(accountFeeEntity.getFees().getFeeName(), is(accountFees[i].getFees().getFeeName()));
            i++;
        }
    }

    private List<AccountFeesEntity> getAccountFeesSortedByFeeName() {
        List<AccountFeesEntity> sortedAccountFees = new ArrayList<AccountFeesEntity>(customerAccount.getAccountFees());
        Collections.sort( sortedAccountFees, new Comparator<AccountFeesEntity>() {
            public int compare(AccountFeesEntity fee1, AccountFeesEntity fee2) {
                return fee1.getFees().getFeeName().compareTo(fee2.getFees().getFeeName());
            }});
        return sortedAccountFees;
    }

    private void verifyOneAccountFee(AccountFeesEntity accountFee) {

        assertThat(customerAccount.getAccountFees().size(), is(1));
        assertThat(new ArrayList<AccountFeesEntity>(customerAccount.getAccountFees()).get(0), is(accountFee));
    }

    private void verifyLastAppliedDateForFee(AccountFeesEntity accountFee, Date expectedDate) {
        assertThat(findAccountFee(accountFee).getLastAppliedDate(), is(expectedDate));
    }

    private AccountFeesEntity findAccountFee(AccountFeesEntity fee) {

        for (AccountFeesEntity accountFeeEntity : customerAccount.getAccountFees()) {
            if (accountFeeEntity.getFees().getFeeName() .equals(fee.getFees().getFeeName())) {
                return accountFeeEntity;
            }
        }
        fail("Did not find accountFeesEntity with name " + fee.getFees().getFeeName());
        return null;
    }

    private void verifyNumberOfFeesApplied(int...feesCounts) {

        List<AccountActionDateEntity> accountActionDates = getActionDatesSortedByDate(customerAccount);
        for (short installmentId = 0; installmentId < feesCounts.length; installmentId++) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDates.get(installmentId);
            assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(feesCounts[installmentId]));
        }

    }
 }
