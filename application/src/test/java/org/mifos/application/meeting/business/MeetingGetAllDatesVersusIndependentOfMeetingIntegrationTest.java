package org.mifos.application.meeting.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.MifosIntegrationTestCase;

public class MeetingGetAllDatesVersusIndependentOfMeetingIntegrationTest extends MifosIntegrationTestCase {

    public MeetingGetAllDatesVersusIndependentOfMeetingIntegrationTest() throws Exception {
        super();
    }

    private MeetingBO meeting;

    private final DateTime meetingStartDate = new DateTime().withYear(2010).withMonthOfYear(3).withDayOfMonth(1).toDateMidnight().toDateTime();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testShouldGenerateSameDatesForDailyMeeting() throws Exception {

        meeting = new MeetingBuilder().daily().every(3).withStartDate(meetingStartDate).build();

        List<Date> meetingDates = meeting.getAllDates(10);
        List<Date> independentOfMeetingDates = meeting.getAllDatesWithRepaymentIndepOfMeetingEnabled(10, true);

        assertThat(meetingDates.size(), is(independentOfMeetingDates.size()));

        for (int i=0; i<meetingDates.size(); i++) {
            assertThat(meetingDates.get(i), is(independentOfMeetingDates.get(i)));
        }
    }

    public void testShouldGenerateSameDatesForWeeklyMeeting() throws Exception {

        meeting = new MeetingBuilder().weekly().every(1).withStartDate(meetingStartDate).build();

        List<Date> meetingDates = meeting.getAllDates(10);
        List<Date> independentOfMeetingDates = meeting.getAllDatesWithRepaymentIndepOfMeetingEnabled(10, true);

        assertThat(meetingDates.size(), is(independentOfMeetingDates.size()));

        for (int i=0; i<meetingDates.size(); i++) {
            assertThat(meetingDates.get(i), is(independentOfMeetingDates.get(i)));
        }
    }

    public void testShouldGenerateSameDatesForMonthlyOnDateMeeting() throws Exception {

        meeting = new MeetingBuilder().monthly().every(1).withStartDate(meetingStartDate).onDayOfMonth(1).build();

        List<Date> meetingDates = meeting.getAllDates(10);
        List<Date> independentOfMeetingDates = meeting.getAllDatesWithRepaymentIndepOfMeetingEnabled(10, true);

        assertThat(meetingDates.size(), is(independentOfMeetingDates.size()));

        for (int i=0; i<meetingDates.size(); i++) {
            assertThat(meetingDates.get(i), is(independentOfMeetingDates.get(i)));
        }
    }

    public void testShouldGenerateSameDatesForMonthlyOnWeekAndWeekDayMeeting() throws Exception {

        meeting = new MeetingBuilder().monthly().every(1).withStartDate(meetingStartDate).onWeek(RankType.FIRST).occuringOnA(WeekDay.WEDNESDAY).build();

        List<Date> meetingDates = meeting.getAllDates(10);
        List<Date> independentOfMeetingDates = meeting.getAllDatesWithRepaymentIndepOfMeetingEnabled(10, true);

        assertThat(meetingDates.size(), is(independentOfMeetingDates.size()));

        for (int i=0; i<meetingDates.size(); i++) {
            assertThat(meetingDates.get(i), is(independentOfMeetingDates.get(i)));
        }
    }
}
