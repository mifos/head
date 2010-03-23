package org.mifos.customers.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.calendar.DayOfWeek;
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

    private final MeetingBO meeting = new MeetingBuilder().every(1).weekly().occuringOnA(WeekDay.MONDAY).build();

    private final List<FeeView> fees = new ArrayList<FeeView>();
    private List<Days> workingDays;
    private List<Holiday> holidays;

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

    @Test
    public void canGenerateSchedulesMatchingMeetingRecurrenceWhenNoPreviousSchedulesExisted() {

        // setup
        holidays = new ArrayList<Holiday>();

        // stubbing
        when(customer.getCustomerMeetingValue()).thenReturn(meeting);

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
}
