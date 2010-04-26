package org.mifos.customers.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.business.FeeFrequencyEntity;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.calendar.DayOfWeek;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerAccountBOTest {

    private CustomerAccountBO customerAccount;

    @Mock
    private UserContext userContext;

    @Mock
    private CustomerBO customer;

    @Mock
    private FeeBO weeklyFee;

    @Mock
    private FeeBO biWeeklyFee;

    @Mock
    private FeeFrequencyEntity weeklyFeeFrequencyEntity;

    @Mock
    private FeeFrequencyEntity biWeeklyFeeFrequencyEntity;


    private final String weeklyFeeName = "Fee 1";
    private final String biWeeklyFeeName = "Fee 2";
    private MeetingBO defaultWeeklyCustomerMeeting;
    private MeetingBO feeMeetingEveryWeek;
    private MeetingBO biWeeklyFeeMeeting;

    private final List<FeeDto> fees = new ArrayList<FeeDto>();
    private List<Days> workingDays;
    private List<Holiday> holidays;
    private List<AccountFeesEntity> accountFees;
    private Holiday moratorium;

    @BeforeClass
    public static void setupDefaultCurrency() {
        MifosCurrency defaultCurrency = TestUtils.EURO;
        Money.setDefaultCurrency(defaultCurrency);
    }

    @Before
    public void setupAndInjectDependencies() throws Exception {

        workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek.wednesdayAsDay(), DayOfWeek.thursdayAsDay(), DayOfWeek.fridayAsDay());

        customerAccount = new CustomerAccountBO(userContext, customer, fees);
    }

    @Before
    public void setupDefaultObjects() {
        defaultWeeklyCustomerMeeting = new MeetingBuilder().every(1).weekly().occuringOnA(WeekDay.MONDAY).build();
        when(customer.getCustomerMeetingValue()).thenReturn(defaultWeeklyCustomerMeeting);
        when(customer.getUserContext()).thenReturn(TestUtils.makeUser());
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
    }

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
    public void createNewWeeklyCustomerAccountNoFeesNoHolidayGeneratesCorrectSchedule() {

        // use default setup

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting,
                workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleForDayOfWeek(DayOfWeek.monday());

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
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleForDayOfWeek(DayOfWeek.monday());

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
    public void createNewWeeklyCustomerAccountOnePeriodicFeeNoHolidayGeneratesCorrectFeeSchedule() {

        // setup
        accountFees.add(createAccountFeesEntity(weeklyFee, 10.0));

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleForDayOfWeek(DayOfWeek.monday());

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
    }

    @Test
    public void createNewWeeklyCustomerAccountTwoPeriodicFeesNoHolidayGeneratesCorrectFeeSchedule() {

        // setup
        accountFees.add(createAccountFeesEntity(weeklyFee, 10.0));
        accountFees.add(createAccountFeesEntity(biWeeklyFee, 13.0));

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleForDayOfWeek(DayOfWeek.monday());

        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(customerAccount)) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            if ( installmentId % 2 == 0) { // even-numbered events have both fees applied
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
    }

    @Test
    public void createNewWeeklyCustomerAccountTwoPeriodicFeesWithMoratoriumGeneratesCorrectFeeSchedule() {

        // setup
        accountFees.add(createAccountFeesEntity(weeklyFee, 10.0));
        accountFees.add(createAccountFeesEntity(biWeeklyFee, 13.0));
        holidays.add(moratorium);

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, defaultWeeklyCustomerMeeting, workingDays, holidays);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleForDayOfWeek(DayOfWeek.monday());

        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(customerAccount)) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            // Third and subsequent installments are pushed out one week
            if (installmentId <= 2) {
                assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            } else {
                assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.plusWeeks(1).toDate())));
            }
            if ( installmentId % 2 == 0) { // even-numbered events have both fees applied
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
    }

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
        DateTime installmentDate = getFirstInstallmentDateForWeeklyScheduleForDayOfWeek(DayOfWeek.monday());

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

    private DateTime getFirstInstallmentDateForWeeklyScheduleForDayOfWeek(int dayOfWeek) {
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
