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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.business.FeeFrequencyEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.persistence.FeePersistence;
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
import org.mifos.framework.util.helpers.Predicate;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class CustomerAccountBOTest {

    private CustomerAccountBO customerAccount;

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
    @Mock private FeePersistence feePersistence;


    private final String weeklyFeeName = "Fee 1";
    private final double weeklyFeeAmount = 10.0;
    private final String biWeeklyFeeName = "Fee 2";
    private final double biWeeklyFeeAmount = 13.0;
    private MeetingBO defaultWeeklyCustomerMeeting;
    private MeetingBO feeMeetingEveryWeek;
    private MeetingBO biWeeklyFeeMeeting;

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

        weeklyAccountFee = createAccountFeesEntity(weeklyFee, weeklyFeeAmount);
        biWeeklyAccountFee = createAccountFeesEntity(biWeeklyFee, biWeeklyFeeAmount);


        when(biWeeklyFee.getFeeId())                           .thenReturn((short) 2);
        when(biWeeklyFee.getFeeFrequency())                    .thenReturn(biWeeklyFeeFrequencyEntity);
        when(biWeeklyFee.getFeeName())                         .thenReturn(biWeeklyFeeName);
        when(biWeeklyFeeFrequencyEntity.getFeeMeetingFrequency()).thenReturn(biWeeklyFeeMeeting);
        when(biWeeklyFee.isPeriodic())                           .thenReturn(true);

        when(oneTimeUpFrontFee.getFeeId())                     .thenReturn((short) 3);
        when(oneTimeUpFrontFee.getFeeFrequency())                    .thenReturn(oneTimeFeeFrequencyEntity);
        when(oneTimeFeeFrequencyEntity.getFeePayment())        .thenReturn(oneTimeFeePaymentEntity);

        when(feePersistence.getFee(anyShort())) .thenAnswer(new Answer<FeeBO>() {
            public FeeBO answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                FeeBO fee;
                if (args[0].equals((short) 1)){
                    fee = weeklyFee;
                } else if (args[0].equals((short) 2)) {
                    fee = biWeeklyFee;
                } else {
                    fee = oneTimeUpFrontFee;
                }
            return fee;
            }
        });


        when(personnelPersistence.getPersonnel(anyShort())) .thenReturn(null);


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
    public void canGenerateSchedulesMatchingMeetingRecurrenceWhenNoPreviousSchedulesExisted() throws Exception{

        // use default setup
       createCustomerAccount();

        // exercise test
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        // verification
        verifyWeeklyMeetingDatesForInstallmentsInRange((short) 1, (short) 10, getFirstInstallmentDateForWeeklyScheduleStartingNow(DayOfWeek.monday()));
    }

    @Test
    public void canGenerateNextSetOfMeetingDatesWithFreshlyGeneratedSchedule() {

        // use default setup

        // exercise test
        createCustomerAccount();
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 20, firstInstallmentDate);
    }

    @Test
    public void generateNextSetOfMeetingDatesAppliesFeesToNextTenEvents() {
        // setup
        accountFees.add(weeklyAccountFee);

        // exercise test
        createCustomerAccount();
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 20, firstInstallmentDate);
        verifyOneFeeForInstallmentsInRange(1, 20, weeklyAccountFee);
        verifyOneAccountFee(weeklyAccountFee);
        verifyLastAppliedDateForFee (weeklyAccountFee, firstInstallmentDate.plusWeeks(19));
    }

    @Test
    public void eneratingNextSetOfMeetingDatesShouldApplyFeeToAllNewMeetingDates() {

        // setup
        accountFees.add(weeklyAccountFee);

        // exercise test
        createCustomerAccount();
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verify
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 20, firstInstallmentDate);
        verifyOneFeeForInstallmentsInRange(1, 20, weeklyAccountFee);
        verifyOneAccountFee(weeklyAccountFee);
        verifyLastAppliedDateForFee (weeklyAccountFee, firstInstallmentDate.plusWeeks(19));
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
        createCustomerAccount();

        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 10, firstInstallmentDate);

        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDatesForInstallmentsInRange(11, 11, firstInstallmentDate.plusWeeks(11));
        verifyWeeklyMeetingDatesForInstallmentsInRange(12, 20, firstInstallmentDate.plusWeeks(11));
   }

    @Test
    public void generateNextSetOfMeetingDatesToAccountWithWeeklyFeeInNextMeetingHolidayShouldApplySingleFeeToAllInstallments() {

        // setup
        accountFees.add(weeklyAccountFee);
        Holiday nextMeetingDayHolidaySpansEleventhMeeting
            = new HolidayBuilder().from(new DateTime().plusWeeks(10))
                                  .to(new DateTime().plusWeeks(11).minusDays(1))
                                  .withRepaymentRule(RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT)
                                  .build();
        holidays.add(nextMeetingDayHolidaySpansEleventhMeeting);


        // exercise test
        createCustomerAccount();

        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 10, firstInstallmentDate);

        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDatesForInstallmentsInRange(11, 11, firstInstallmentDate.plusWeeks(11));
        verifyWeeklyMeetingDatesForInstallmentsInRange(12, 20, firstInstallmentDate.plusWeeks(11));
        verifyOneFeeForInstallmentsInRange(1, 20, weeklyAccountFee);
        verifyOneAccountFee(weeklyAccountFee);
        verifyLastAppliedDateForFee (weeklyAccountFee, firstInstallmentDate.plusWeeks(19));
   }

    @Test
    public void generateNextSetOfMeetingDatesInMoratoriumToAccountWithPeriodicFeeInShouldApplySingleFeeToAllInstallments() {

        // setup
        accountFees.add(weeklyAccountFee);
        Holiday moratoriumSpans12thMeeting
            = new HolidayBuilder().from(new DateTime().plusWeeks(11))
                                  .to(new DateTime().plusWeeks(12).minusDays(1))
                                  .withRepaymentMoratoriumRule()
                                  .build();
        holidays.add(moratoriumSpans12thMeeting);


        // exercise test
        createCustomerAccount();

        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 10, firstInstallmentDate);

        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        // Advance time to the day after the 10th installment
//        dateTimeService.setCurrentDateTime(firstInstallmentDate.plusWeeks(9).plusDays(1));
        // Should apply the fee to the next upcoming installment, the 11th
//        customerAccount.applyPeriodicFeesToNewSchedule();

        //verification
        verifyWeeklyMeetingDatesForInstallmentsInRange(11, 11, firstInstallmentDate.plusWeeks(10));
        verifyWeeklyMeetingDatesForInstallmentsInRange(12, 20, firstInstallmentDate.plusWeeks(12));
        verifyOneFeeForInstallmentsInRange(1, 20, weeklyAccountFee);
        verifyOneAccountFee(weeklyAccountFee);
        verifyLastAppliedDateForFee (weeklyAccountFee, firstInstallmentDate.plusWeeks(20));
   }

    @Test
    public void canGenerateNextSetOfMeetingDatesWhenAllButThreeDatesAreInThePast() {

        // use default setup

        // exercise test
        createCustomerAccount();
        dateTimeService.setCurrentDateTime(dateTimeService.getCurrentDateTime().plusWeeks(7));
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 20, firstInstallmentDate);
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
        createCustomerAccount();
        dateTimeService.setCurrentDateTime(dateTimeService.getCurrentDateTime().plusWeeks(7));
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(20));
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 11, firstInstallmentDate);
        verifyWeeklyMeetingDatesForInstallmentsInRange(12, 20, firstInstallmentDate.plusWeeks(12));
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
        createCustomerAccount();

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 1, firstInstallmentDate);
    }

    @Test
    public void createNewWeeklyCustomerAccountNoFeesWithMoratoriumGeneratesCorrectSchedule() {

        // setup
        holidays.add(moratorium);

        // exercise test
        createCustomerAccount();

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 2, firstInstallmentDate);
        verifyWeeklyMeetingDatesForInstallmentsInRange(3, 10, firstInstallmentDate.plusWeeks(3));
    }

    @Test
    public void createNewWeeklyCustomerAccountOnePeriodicFeeNoHolidayGeneratesCorrectFeeScheduleAndSetsFeeLastAppliedDateToLastMeetingDate() {

        // setup
        accountFees.add(weeklyAccountFee);

        // exercise test
        createCustomerAccount();

        //verification
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 10, firstInstallmentDate);
        verifyOneFeeForInstallmentsInRange(1, 10, weeklyAccountFee);
        verifyOneAccountFee(weeklyAccountFee);
        verifyLastAppliedDateForFee (weeklyAccountFee, firstInstallmentDate.plusWeeks(9));
    }

    @Test
    public void createNewWeeklyCustomerAccountTwoPeriodicFeesNoHolidayGeneratesCorrectFeeSchedule() throws Exception {

        // setup
        accountFees.add(weeklyAccountFee);
        accountFees.add(biWeeklyAccountFee);

        // exercise test
        createCustomerAccount();

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 10, firstInstallmentDate);
        verifyOneFeeForInstallmentsThat(weeklyAccountFee, haveEvenNumberedInstallments());
        verifyTwoFeesForInstallmentsThat(weeklyAccountFee, biWeeklyAccountFee, haveOddNumberedInstallments());
        verifyAccountHasAccountFees(weeklyAccountFee, biWeeklyAccountFee);
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(9));
        verifyLastAppliedDateForFee(biWeeklyAccountFee, firstInstallmentDate.plusWeeks(8));
    }

    @Test
    public void createNewWeeklyCustomerAccountTwoPeriodicFeesWithMoratoriumOnThirdMeetingGeneratesCorrectFeeSchedule()
                throws Exception {

        // setup
        accountFees.add(weeklyAccountFee);
        accountFees.add(biWeeklyAccountFee);
        holidays.add(moratorium);

        // exercise test
        createCustomerAccount();

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 2, firstInstallmentDate);
        verifyWeeklyMeetingDatesForInstallmentsInRange(3, 10, firstInstallmentDate.plusWeeks(3));
        verifyOneFeeForInstallmentsThat(weeklyAccountFee, haveEvenNumberedInstallments());
        verifyTwoFeesForInstallmentsThat(weeklyAccountFee, biWeeklyAccountFee, haveOddNumberedInstallments());
        verifyAccountHasAccountFees(weeklyAccountFee, biWeeklyAccountFee);
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(10));
        verifyLastAppliedDateForFee(biWeeklyAccountFee, firstInstallmentDate.plusWeeks(9));

    }

    @Test
    public void createNewWeeklyCustomerAccountTwoPeriodicFeesWithMoratoriumOnSecondMeetingGeneratesCorrectFeeSchedule()
                throws Exception {

        // setup
        accountFees.add(weeklyAccountFee);
        accountFees.add(biWeeklyAccountFee);
        Holiday moratoriumOnSecondMeeting =  new HolidayBuilder().from(firstInstallmentDate.plusWeeks(1))
                                                                 .to(new DateTime().plusWeeks(2).minusDays(1))
                                                                 .withRepaymentMoratoriumRule()
                                                                 .build();
        holidays.add(moratoriumOnSecondMeeting);

        // exercise test
        createCustomerAccount();

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 1, firstInstallmentDate);
        verifyWeeklyMeetingDatesForInstallmentsInRange(2, 10, firstInstallmentDate.plusWeeks(2));

        verifyOneFeeForInstallmentsThat(weeklyAccountFee, haveEvenNumberedInstallments());
        verifyTwoFeesForInstallmentsThat(weeklyAccountFee, biWeeklyAccountFee, haveOddNumberedInstallments());
        verifyAccountHasAccountFees(weeklyAccountFee, biWeeklyAccountFee);
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(10));
        verifyLastAppliedDateForFee(biWeeklyAccountFee, firstInstallmentDate.plusWeeks(9));

    }

    @Ignore
    @Test
    public void createNewWeeklyCustomerAccountOnePeriodicFeeWithMoratoriumGeneratesCorrectFeeSchedule() {

        // setup
        holidays.add(moratorium);
        accountFees.add(weeklyAccountFee);

        // exercise test
        createCustomerAccount();

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 2, firstInstallmentDate);
        verifyWeeklyMeetingDatesForInstallmentsInRange(3, 10, firstInstallmentDate.plusWeeks(3));

        //Third through 10th installments pushed out one week. Fees are unchanged, remain attached to installments
        //even when the installment is pushed out
        verifyOneFeeForInstallmentsInRange(1, 10, weeklyAccountFee);
        verifyAccountHasAccountFees(weeklyAccountFee);
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(10));
    }


    /*********************************
     * Test applying a periodic fee
     ***********************************/

    @Test
    public void applyWeeklyPeriodicFeeBeforeThirdMeetingAddsFeeToThirdAndSubsequentMeetings() throws Exception {

        // Exercise test with default setup
        createCustomerAccount();
        dateTimeService.setCurrentDateTime(firstInstallmentDate.plusWeeks(1).plusDays(1));
        customerAccount.applyCharge(weeklyFee.getFeeId(), weeklyFeeAmount);

        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 10, firstInstallmentDate);
        verifyOneFeeForInstallmentsInRange(3, 10, weeklyAccountFee);
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(9));

    }

    @Test
    public void GenerateNextSetOfMeetingDatesWithFeeAppliedBeforeEighthMeetingShouldAddFeeToAllMeetingsOnOrAfterEighthMeeting()
                throws Exception {

        // Exercise test with default setup
        createCustomerAccount();

        dateTimeService.setCurrentDateTime(firstInstallmentDate.plusWeeks(6).plusDays(1));
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);
        customerAccount.applyCharge(weeklyFee.getFeeId(), weeklyFeeAmount);

        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 20, firstInstallmentDate);
        verifyOneFeeForInstallmentsInRange(8, 20, weeklyAccountFee);
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(19));

    }

    @Test
    public void generateNextSetOfMeetingsThenApplyWeeklyPeriodicFeeBefore14thMeetingShouldAddFeeTo14thThrough20thMeetings()
            throws Exception {

        // Exercise test with default setup
        createCustomerAccount();
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);
        dateTimeService.setCurrentDateTime(firstInstallmentDate.plusWeeks(13).minusDays(1));
        customerAccount.applyCharge(weeklyFee.getFeeId(), weeklyFeeAmount);

        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 20, firstInstallmentDate);
        verifyOneFeeForInstallmentsInRange(14, 20, weeklyAccountFee);
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(19));

    }

    @Test
    public void generatingNextSetOfMeetingDatesThenApplyPeriodicFeeBefore14thMeetingThenGenerateMoreMeetingsShouldApplyFeeToAllUpcomingMeetingDates()
            throws Exception {

        // Exercise test with default setup
        createCustomerAccount();
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);
        dateTimeService.setCurrentDateTime(firstInstallmentDate.plusWeeks(13).minusDays(1));
        customerAccount.applyCharge(weeklyFee.getFeeId(), weeklyFeeAmount);
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays); // Adds 10 more meetings

        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 30, firstInstallmentDate);
        verifyOneFeeForInstallmentsInRange(14, 30, weeklyAccountFee);
        verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(29));
    }

    @Test
    public void applyWeeklyPeriodicFeeToWeeklyMeetingOnDayAfterSecondMeetingShouldAddChargeToThirdAndLaterMeetings() throws Exception {

            // Exercise test with default setup
            createCustomerAccount();
            dateTimeService.setCurrentDateTime(firstInstallmentDate.plusWeeks(1).plusDays(1));
            customerAccount.applyCharge(weeklyFee.getFeeId(), 10.0);

            verifyWeeklyMeetingDatesForInstallmentsInRange(1, 10, firstInstallmentDate);
            verifyOneFeeForInstallmentsInRange(3, 10, weeklyAccountFee);
            verifyLastAppliedDateForFee(weeklyAccountFee, firstInstallmentDate.plusWeeks(9));

    }

    @Test
    public void generateNextSetOfMeetingDatesMeetingEveryWeekWithBiweeklyFee() throws Exception {

        // Setup
        accountFees.add(biWeeklyAccountFee);

        // Exercise test
        createCustomerAccount();
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        // verify
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 20, firstInstallmentDate);
        verifyOneFeeForInstallmentsThat(biWeeklyAccountFee, haveOddNumberedInstallments());
        verifyLastAppliedDateForFee(biWeeklyAccountFee, firstInstallmentDate.plusWeeks(18));
    }

    @Test
    public void generateNextSetOfMeetingDatesMeetingEveryOtherWeekWithFeeEveryfifthWeek() throws Exception {

        // Setup
        MeetingBO pentaWeeklyFeeMeeting = new MeetingBuilder().every(5)
                                                              .weekly()
                                                              .occuringOnA(WeekDay.MONDAY)
                                                              .periodicFeeMeeting()
                                                              .build();
        MeetingBO biWeeklyCustomerMeeting = new MeetingBuilder().every(2)
                                                                .weekly()
                                                                .occuringOnA(WeekDay.MONDAY)
                                                                .periodicFeeMeeting()
                                                                .build();
        when(customer.getCustomerMeetingValue()).thenReturn(biWeeklyCustomerMeeting);
        AmountFeeBO pentaWeeklyFee = mock(AmountFeeBO.class);
        FeeFrequencyEntity pentaFeeFrequencyEntity = mock(FeeFrequencyEntity.class);

        when(pentaWeeklyFee.getFeeId())                             .thenReturn((short) 4);
        when(pentaWeeklyFee.getFeeFrequency())                      .thenReturn(pentaFeeFrequencyEntity);
        when(pentaWeeklyFee.getFeeName())                           .thenReturn("Every 5th week");
        when(pentaFeeFrequencyEntity.getFeeMeetingFrequency()).thenReturn(pentaWeeklyFeeMeeting);
        when(pentaWeeklyFee.isPeriodic())                           .thenReturn(true);

        AccountFeesEntity pentaWeeklyAccountFee = createAccountFeesEntity(pentaWeeklyFee, 25.0);

        accountFees.add(pentaWeeklyAccountFee);

        // Exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, biWeeklyCustomerMeeting,
                workingDays, holidays);

        //verify initial meeting dates and fees
        verifyBiWeeklyMeetingDatesForInstallmentsInRange(1, 10, firstInstallmentDate);
        verifyLastAppliedDateForFee(pentaWeeklyAccountFee, firstInstallmentDate.plusWeeks(16));
        verifyOneFeeOnlyForInstallments(pentaWeeklyAccountFee, 1, 4, 6, 9);

        // continue exercise, generate more meetings
        customerAccount.generateNextSetOfMeetingDates(workingDays, holidays);

        // verify additional meeting dates
        verifyBiWeeklyMeetingDatesForInstallmentsInRange(1, 20, firstInstallmentDate);
        verifyLastAppliedDateForFee(pentaWeeklyAccountFee, firstInstallmentDate.plusWeeks(36));
        verifyOneFeeOnlyForInstallments(pentaWeeklyAccountFee, 1, 4, 6, 9, 11, 14, 16, 19);
    }

    /*******************************
     * Test regenerateDatesForNewHolidays
     *******************************/

    @Test
    public void regeneratingDatesFromFirstInstallmentWithNoNewHolidaysDoesNothing() {

        // default setup -- no holidays

        // exercise test
        createCustomerAccount();
        customerAccount.rescheduleDatesForNewHolidays(workingDays, holidays, holidays);

        // verify dates have not changed
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 10, firstInstallmentDate);
    }

    @Test
    public void regeneratingDatesFromFirstInstallmentWithMoratoriumShouldPushOutThirdAndLaterMeetings() {

        // setup
        List<Holiday> unappliedHolidays = new ArrayList<Holiday>();
        unappliedHolidays.add(moratorium);

        // exercise test
        createCustomerAccount();
        holidays.add(moratorium);
        customerAccount.rescheduleDatesForNewHolidays(workingDays, holidays, unappliedHolidays);

        // verify dates have pushed out, starting with 3rd meeting
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 2, firstInstallmentDate);
        verifyWeeklyMeetingDatesForInstallmentsInRange(3, 10, firstInstallmentDate.plusWeeks(3));
    }

    @Test
    public void regeneratingDatesFromFirstInstallmentWithMoratoriumHittingNoDatesShouldNotAlterSchedule() {

        // setup
        List<Holiday> unappliedHolidays = new ArrayList<Holiday>();
        Holiday moratoriumBetweenSecondAndThirdMeetings
                    =  new HolidayBuilder().from(firstInstallmentDate.plusWeeks(1).plusDays(1))
                                           .to(new DateTime().plusWeeks(1).plusDays(6))
                                           .withRepaymentMoratoriumRule()
                                           .build();

        unappliedHolidays.add(moratoriumBetweenSecondAndThirdMeetings);

        // exercise test
        createCustomerAccount();

        // Verify schedule is not adjusted
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 10, firstInstallmentDate);

        holidays.add(moratoriumBetweenSecondAndThirdMeetings);
        customerAccount.rescheduleDatesForNewHolidays(workingDays, holidays, unappliedHolidays);

        // verify dates have not changed
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 10, firstInstallmentDate);
    }

    @Test
    public void initialSchedulePushedOutByMoratoriumThenExtendMoratoriumShouldPushOutDatesAnotherWeek() {

        // setup
        List<Holiday> unappliedHolidays = new ArrayList<Holiday>();

        holidays.add(moratorium);
        Holiday extendMoratoriumAnotherWeek
                    =  new HolidayBuilder().from(firstInstallmentDate.plusWeeks(3))
                                           .to(new DateTime().plusWeeks(3).plusDays(6))
                                           .withRepaymentMoratoriumRule()
                                           .build();

        unappliedHolidays.add(extendMoratoriumAnotherWeek);

        // exercise test
        createCustomerAccount();

        // verify dates have pushed out, starting with 3rd meeting
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 2, firstInstallmentDate);
        verifyWeeklyMeetingDatesForInstallmentsInRange(3, 10, firstInstallmentDate.plusWeeks(3));

        // Now extend the moratorium and reschedule
        holidays.add(extendMoratoriumAnotherWeek);
        customerAccount.rescheduleDatesForNewHolidays(workingDays, holidays, unappliedHolidays);

        // verify dates starting with date in first moratorium are pushed out a total of two weeks
        verifyWeeklyMeetingDatesForInstallmentsInRange(1, 2, firstInstallmentDate);
        verifyWeeklyMeetingDatesForInstallmentsInRange(3, 10, firstInstallmentDate.plusWeeks(4));
    }

    /**************************************
     * Helper methods
     *************************************/

    private void createCustomerAccount() {

        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);
        customerAccount.setPersonnelPersistence(personnelPersistence);
        customerAccount.setFeePersistence(feePersistence);
    }

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


    private AccountFeesEntity createAccountFeesEntity (AmountFeeBO feeBO, double feeAmount) {
        when(feeBO.getFeeAmount()) .thenReturn(makeMoney(feeAmount));
        AccountFeesEntity accountFeesEntity
            = new AccountFeesEntity(null, feeBO, new Double(feeAmount));
        return accountFeesEntity;
    }

    private void verifyWeeklyMeetingDatesForInstallmentsInRange
                        (int startingWithInstallment, int throughInstallment, DateTime startingDate) {

        DateTime installmentDate = startingDate;
        List<AccountActionDateEntity> accountActionDates = getActionDatesSortedByDate(customerAccount);
        for (short installmentId = (short) startingWithInstallment; installmentId <= throughInstallment; installmentId++) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDates.get(installmentId-1);
            assertThat("Installment " + installmentId, new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    private void verifyBiWeeklyMeetingDatesForInstallmentsInRange
                        (int startingWithInstallment, int throughInstallment, DateTime startingDate) {

        DateTime installmentDate = startingDate;
        List<AccountActionDateEntity> accountActionDates = getActionDatesSortedByDate(customerAccount);
        for (short installmentId = (short) startingWithInstallment; installmentId <= throughInstallment; installmentId++) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDates.get(installmentId-1);
            assertThat("Installment " + installmentId, new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            installmentDate = installmentDate.plusWeeks(2);
        }
    }

    private void verifyOneFeeForInstallmentsInRange (int start, int end, AccountFeesEntity accountFees) {

        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(customerAccount)) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            if ((accountActionDate.getInstallmentId() >= start) && (accountActionDate.getInstallmentId() <= end)) {
                assertThat("Number of fees applied to installment " + scheduleEntity.getInstallmentId(),
                        scheduleEntity.getAccountFeesActionDetails().size(), is(1));
                assertThat("Fee name applied to installment " + scheduleEntity.getInstallmentId(),
                        scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(),
                            is(accountFees.getFees().getFeeName()));
                assertThat("Fee amount applied to installment " + scheduleEntity.getInstallmentId(),
                        scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(),
                            is(accountFees.getFeeAmount()));
            } else {
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(0));

            }
        }
    }

    private void verifyOneFeeOnlyForInstallments (AccountFeesEntity accountFees, int...installmentIds) {

        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(customerAccount)) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            if (includes(installmentIds, scheduleEntity.getInstallmentId())) {
                assertThat("Number of fees applied to installment " + scheduleEntity.getInstallmentId(),
                        scheduleEntity.getAccountFeesActionDetails().size(), is(1));
                assertThat("Fee name applied to installment " + scheduleEntity.getInstallmentId(),
                        scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(),
                            is(accountFees.getFees().getFeeName()));
                assertThat("Fee amount applied to installment " + scheduleEntity.getInstallmentId(),
                        scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(),
                            is(accountFees.getFeeAmount()));
            } else {
                assertThat("Installment " + scheduleEntity.getInstallmentId() + "should not have a fee",
                		scheduleEntity.getAccountFeesActionDetails().size(), is(0));

            }
        }
    }

    private boolean includes (int[] integers, int x) {
        for (int member : integers) {
            if (member == x) {
                return true;
            }
        }
        return false;
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

//    private void verifyLastAppliedDateForFee(AccountFeesEntity accountFee, Date expectedDate) {
//        assertThat(findAccountFee(accountFee).getLastAppliedDate(), is(expectedDate));
//    }

    private void verifyLastAppliedDateForFee(AccountFeesEntity accountFee, DateTime expectedDate) {
        assertThat(new DateTime(findAccountFee(accountFee).getLastAppliedDate()), is(expectedDate));
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

    private Predicate<AccountActionDateEntity> haveOddNumberedInstallments() {
        return new Predicate<AccountActionDateEntity>() {
            public boolean evaluate (AccountActionDateEntity action) {
                return action.getInstallmentId() % 2 != 0;
            }
        };
    }

    private Predicate<AccountActionDateEntity> haveEvenNumberedInstallments() {
        return new Predicate<AccountActionDateEntity>() {
            public boolean evaluate (AccountActionDateEntity action) {
                return action.getInstallmentId() % 2 == 0;
            }
        };
    }

    private void verifyOneFeeForInstallmentsThat
            (AccountFeesEntity accountFee, Predicate<AccountActionDateEntity> predicate) throws Exception {

        for (AccountActionDateEntity action : customerAccount.getAccountActionDatesSortedByInstallmentId()) {
            if (predicate.evaluate(action)) {
                CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) action;
                assertThat("Number of fees applied to installment " + scheduleEntity.getInstallmentId(),
                        scheduleEntity.getAccountFeesActionDetails().size(), is(1));
                assertThat("Fee name applied to installment " + scheduleEntity.getInstallmentId(),
                        scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(),
                            is(accountFee.getFees().getFeeName()));
                assertThat("Fee amount applied to installment " + scheduleEntity.getInstallmentId(),
                        scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(),
                            is(accountFee.getFeeAmount()));

            }
        }
    }

    private void verifyTwoFeesForInstallmentsThat
            (AccountFeesEntity accountFee1, AccountFeesEntity accountFee2,
                    Predicate<AccountActionDateEntity> predicate) throws Exception {

        for (AccountActionDateEntity action : customerAccount.getAccountActionDatesSortedByInstallmentId()) {
            if (predicate.evaluate(action)) {
                CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) action;
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(2));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(), is(accountFee1.getFees().getFeeName()));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(), is(accountFee1.getFeeAmount()));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(1).getFee().getFeeName(), is(accountFee2.getFees().getFeeName()));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(1).getFeeAmount().getAmountDoubleValue(), is(accountFee2.getFeeAmount()));
            }
        }
    }

    private Money makeMoney (double amount) {
        return new Money(defaultCurrency, new BigDecimal(amount));
    }
 }
