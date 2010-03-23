package org.mifos.schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.schedule.internal.HolidayAndWorkingDaysScheduledDateGeneration;

public class ScheduledDateGenerationVersusMeetingGetAllDatesWorkingDaysOnlyIntegrationTest extends MifosIntegrationTestCase {

    public ScheduledDateGenerationVersusMeetingGetAllDatesWorkingDaysOnlyIntegrationTest() throws Exception {
        super();
    }

    private MeetingBO meeting;
    private ScheduledDateGeneration scheduledDateGeneration;

    private List<Days> workingDays;

    private DateTime feb17th2010 = new DateTime().withYear(2010).withMonthOfYear(2).withDayOfMonth(17).toDateMidnight()
            .toDateTime();
    private DateTime meetingStartDate = new DateTime(feb17th2010);

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        meeting = new MeetingBuilder().weekly().occuringOnA(WeekDay.WEDNESDAY).withStartDate(meetingStartDate).build();

        workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        List<Holiday> holidays = new ArrayList<Holiday>();

        scheduledDateGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays, holidays);
    }

    public void testShouldReturnDateOfMeeting() throws Exception {

        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        List<Date> meetingDates = meeting.getAllDates(1);
        List<DateTime> scheduledDates = scheduledDateGeneration.generateScheduledDates(1, meetingStartDate,
                scheduledEvent);

        assertThat(meetingDates.get(0), is(feb17th2010.toDate()));
        assertThat(scheduledDates.get(0), is(feb17th2010));
    }

    public void testShouldReturnNearestMatchingDateToDateGivenAsMeetingDate() throws Exception {

        DateTime feb16 = feb17th2010.minusDays(1);

        meeting.setStartDate(feb16.toDate());
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        List<Date> meetingDates = meeting.getAllDates(1);
        List<DateTime> scheduledDates = scheduledDateGeneration.generateScheduledDates(1, feb16,
                scheduledEvent);

        assertThat(meetingDates.get(0), is(feb17th2010.toDate()));
        assertThat(scheduledDates.get(0), is(feb17th2010));
    }

    public void testShouldReturnNearestMatchingDateToDateGivenAsMeetingDateWhenDateGivenIsJustPastDayOfWeek() throws Exception {

        DateTime feb18 = feb17th2010.plusDays(1);

        meeting.setStartDate(feb18.toDate());
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        List<Date> meetingDates = meeting.getAllDates(1);
        List<DateTime> scheduledDates = scheduledDateGeneration.generateScheduledDates(1, feb18,
                scheduledEvent);

        assertThat(meetingDates.get(0), is(feb17th2010.plusWeeks(1).toDate()));
        assertThat(scheduledDates.get(0), is(feb17th2010.plusWeeks(1)));
    }

    public void testShouldReturnListOfDatesMatchingSchedule() throws Exception {

        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        List<Date> meetingDates = meeting.getAllDates(5);
        List<DateTime> scheduledDates = scheduledDateGeneration.generateScheduledDates(5, meetingStartDate,
                scheduledEvent);

        assertThat(meetingDates.get(0), is(feb17th2010.toDate()));
        assertThat(scheduledDates.get(0), is(feb17th2010));

        assertThat(meetingDates.get(1), is(feb17th2010.plusWeeks(1).toDate()));
        assertThat(scheduledDates.get(1), is(feb17th2010.plusWeeks(1)));

        assertThat(meetingDates.get(2), is(feb17th2010.plusWeeks(2).toDate()));
        assertThat(scheduledDates.get(2), is(feb17th2010.plusWeeks(2)));

        assertThat(meetingDates.get(3), is(feb17th2010.plusWeeks(3).toDate()));
        assertThat(scheduledDates.get(3), is(feb17th2010.plusWeeks(3)));

        assertThat(meetingDates.get(4), is(feb17th2010.plusWeeks(4).toDate()));
        assertThat(scheduledDates.get(4), is(feb17th2010.plusWeeks(4)));
    }
}
