package org.mifos.schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.persistence.HolidayDaoHibernate;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysScheduledDateGeneration;

public class ScheduledDateGenerationVersusMeetingGetAllDatesWithHolidaysTest extends MifosIntegrationTestCase {

    public ScheduledDateGenerationVersusMeetingGetAllDatesWithHolidaysTest() throws Exception {
        super();
    }

    private MeetingBO meeting;
    private ScheduledDateGeneration scheduledDateGeneration;

    private List<Days> workingDays;
    private List<Holiday> holidays;

    private HolidayDao holidayDao;
    private final GenericDao genericDao = new GenericDaoHibernate();

    private final DateTime march1stNextYear = new DateTime().plusYears(1).withMonthOfYear(3).withDayOfMonth(1)
            .toDateMidnight().toDateTime();
    private final DateTime meetingStartDate = new DateTime(march1stNextYear);

    private HolidayBO holiday;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        holidays = new ArrayList<Holiday>();

        holidayDao = new HolidayDaoHibernate(genericDao);
        meeting = new MeetingBuilder().weekly().withStartDate(meetingStartDate).build();

        workingDays = FiscalCalendarRules.getWorkingDaysAsJodaTimeDays();
        scheduledDateGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays, holidays);
    }

    public void testShouldReturnDateOfMeeting() throws Exception {

        holiday = (HolidayBO) new HolidayBuilder().from(march1stNextYear.plusDays(1)).to(march1stNextYear.plusDays(7))
                .withNextWorkingDayRule().build();
        insert(holiday);

        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        List<Date> meetingDates = meeting.getAllDates(1);
        List<DateTime> scheduledDates = scheduledDateGeneration.generateScheduledDates(1, meetingStartDate,
                scheduledEvent);

        assertThat(meetingDates.get(0), is(march1stNextYear.toDate()));
        assertThat(scheduledDates.get(0), is(march1stNextYear));
        
        // cleanup
        TestObjectFactory.cleanUpHolidays(Arrays.asList(holiday));
    }

    public void testShouldReturnNearestMatchingDateToDateGivenAsMeetingDate() throws Exception {

        holiday = (HolidayBO) new HolidayBuilder().from(march1stNextYear.plusDays(1)).to(march1stNextYear.plusDays(7))
                .withNextWorkingDayRule().build();
        insert(holiday);

        DateTime endOfFeb = march1stNextYear.minusDays(1);

        meeting.setStartDate(endOfFeb.toDate());
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        List<Date> meetingDates = meeting.getAllDates(1);
        List<DateTime> scheduledDates = scheduledDateGeneration.generateScheduledDates(1, endOfFeb, scheduledEvent);

        assertThat(meetingDates.get(0), is(march1stNextYear.toDate()));
        assertThat(scheduledDates.get(0), is(march1stNextYear));
        
        // cleanup
        TestObjectFactory.cleanUpHolidays(Arrays.asList(holiday));
    }

    public void testShouldReturnNearestMatchingDateToDateGivenAsMeetingDateWhenDateGivenIsJustPastDayOfWeek()
            throws Exception {

        holiday = (HolidayBO) new HolidayBuilder().from(march1stNextYear.plusDays(1)).to(march1stNextYear.plusDays(7))
                .withNextWorkingDayRule().build();
        insert(holiday);

        DateTime march2ndNextYear = march1stNextYear.plusDays(1);

        meeting.setStartDate(march2ndNextYear.toDate());
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        List<Date> meetingDates = meeting.getAllDates(1);
        List<DateTime> scheduledDates = scheduledDateGeneration.generateScheduledDates(1, march2ndNextYear,
                scheduledEvent);

        DateTime nextWorkingDayAfterHoliday = new DateTime(holiday.getHolidayThruDate()).plusDays(1);
        assertThat(meetingDates.get(0), is(nextWorkingDayAfterHoliday.toDate()));
        assertThat(scheduledDates.get(0), is(nextWorkingDayAfterHoliday));
        
        // cleanup
        TestObjectFactory.cleanUpHolidays(Arrays.asList(holiday));
    }

    public void testShouldReturnListOfDatesMatchingSchedule() throws Exception {

        holiday = (HolidayBO) new HolidayBuilder().from(march1stNextYear.plusDays(1)).to(march1stNextYear.plusDays(7))
                .withNextWorkingDayRule().build();
        insert(holiday);

        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        List<Date> meetingDates = meeting.getAllDates(5);
        List<DateTime> scheduledDates = scheduledDateGeneration.generateScheduledDates(5, meetingStartDate,
                scheduledEvent);

        assertThat(meetingDates.get(0), is(march1stNextYear.toDate()));
        assertThat(scheduledDates.get(0), is(march1stNextYear));

        DateTime nextWorkingDayAfterHoliday = new DateTime(holiday.getHolidayThruDate()).plusDays(1);
        assertThat(meetingDates.get(1), is(nextWorkingDayAfterHoliday.toDate()));
        assertThat(scheduledDates.get(1), is(nextWorkingDayAfterHoliday));

        assertThat(meetingDates.get(2), is(march1stNextYear.plusWeeks(2).toDate()));
        assertThat(scheduledDates.get(2), is(march1stNextYear.plusWeeks(2)));

        assertThat(meetingDates.get(3), is(march1stNextYear.plusWeeks(3).toDate()));
        assertThat(scheduledDates.get(3), is(march1stNextYear.plusWeeks(3)));

        assertThat(meetingDates.get(4), is(march1stNextYear.plusWeeks(4).toDate()));
        assertThat(scheduledDates.get(4), is(march1stNextYear.plusWeeks(4)));
        
        // cleanup
        TestObjectFactory.cleanUpHolidays(Arrays.asList(holiday));
    }
    
    public void testShouldReturnListOfDatesMatchingScheduleWithSameDayRule() throws Exception {

        DateTime march2ndNextYear = march1stNextYear.plusDays(1);
        meeting = new MeetingBuilder().weekly().withStartDate(march2ndNextYear).build();
        
        holiday = (HolidayBO) new HolidayBuilder().from(march1stNextYear.plusDays(2)).to(march1stNextYear.plusDays(14))
                .withSameDayAsRule().build();
        insert(holiday);

        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        List<Date> meetingDates = meeting.getAllDates(5);
        List<DateTime> scheduledDates = scheduledDateGeneration.generateScheduledDates(5, march2ndNextYear,
                scheduledEvent);

        assertThat(meetingDates.get(0), is(march2ndNextYear.toDate()));
        assertThat(scheduledDates.get(0), is(march2ndNextYear));

        assertThat(meetingDates.get(1), is(march2ndNextYear.plusWeeks(1).toDate()));
        assertThat(scheduledDates.get(1), is(march2ndNextYear.plusWeeks(1)));

        assertThat(meetingDates.get(2), is(march2ndNextYear.plusWeeks(2).toDate()));
        assertThat(scheduledDates.get(2), is(march2ndNextYear.plusWeeks(2)));

        assertThat(meetingDates.get(3), is(march2ndNextYear.plusWeeks(3).toDate()));
        assertThat(scheduledDates.get(3), is(march2ndNextYear.plusWeeks(3)));

        assertThat(meetingDates.get(4), is(march2ndNextYear.plusWeeks(4).toDate()));
        assertThat(scheduledDates.get(4), is(march2ndNextYear.plusWeeks(4)));
        
        // cleanup
        TestObjectFactory.cleanUpHolidays(Arrays.asList(holiday));
    }
    
    public void testShouldReturnListOfDatesMatchingScheduleWithNextRepaymentOrMeetingRule() throws Exception {

        holiday = (HolidayBO) new HolidayBuilder().from(march1stNextYear.plusDays(1)).to(march1stNextYear.plusDays(7))
                .withNextMeetingRule().build();
        insert(holiday);

        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        List<Date> meetingDates = meeting.getAllDates(5);
        List<DateTime> scheduledDates = scheduledDateGeneration.generateScheduledDates(5, meetingStartDate,
                scheduledEvent);

        assertThat(meetingDates.get(0), is(march1stNextYear.toDate()));
        assertThat(scheduledDates.get(0), is(march1stNextYear));

        DateTime nextMeetingDayAfterHoliday = march1stNextYear.plusWeeks(2);
        assertThat(meetingDates.get(1), is(nextMeetingDayAfterHoliday.toDate()));
        assertThat(scheduledDates.get(1), is(nextMeetingDayAfterHoliday));

        assertThat(meetingDates.get(2), is(nextMeetingDayAfterHoliday.toDate()));
        assertThat(scheduledDates.get(2), is(nextMeetingDayAfterHoliday));

        assertThat(meetingDates.get(3), is(march1stNextYear.plusWeeks(3).toDate()));
        assertThat(scheduledDates.get(3), is(march1stNextYear.plusWeeks(3)));

        assertThat(meetingDates.get(4), is(march1stNextYear.plusWeeks(4).toDate()));
        assertThat(scheduledDates.get(4), is(march1stNextYear.plusWeeks(4)));
        
        // cleanup
        TestObjectFactory.cleanUpHolidays(Arrays.asList(holiday));
    }

    private void insert(final HolidayBO insertHoliday) {
        holidayDao.save(insertHoliday);
        holidays.add(holiday);
    }
}
