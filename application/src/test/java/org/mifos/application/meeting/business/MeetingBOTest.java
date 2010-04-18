package org.mifos.application.meeting.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.FiscalCalendarRules;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MeetingBOTest {

    @Mock
    FiscalCalendarRules fiscalCalendarRules;

    @Mock
    MasterPersistence masterPersistence;

    @Test
    public void testWeeklyMeetingInterval() {
        MeetingBO meeting = new MeetingBuilder().weekly().every(1).occuringOnA(WeekDay.THURSDAY).build();
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);
        LocalDate paymentDate = new LocalDate(2010, 2, 4);
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 1, 31), paymentDate), is(false));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 1), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 7), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 8), paymentDate), is(false));
    }


    @Test
    public void testBiWeeklyMeetingInterval() {
        MeetingBO meeting = new MeetingBuilder().weekly().every(2).occuringOnA(WeekDay.THURSDAY).build();
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);

        LocalDate paymentDate = new LocalDate(2010, 2, 4);
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 1, 31), paymentDate), is(false));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 1), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 14), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 15), paymentDate), is(false));
    }

    @Test
    public void testMonthlyOnDayOfMonthMeetingInterval() throws MeetingException {
        MeetingBO meeting = new MeetingBuilder().monthly().every(1).buildMonthlyForDayNumber(20);
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);
        LocalDate paymentDate = new LocalDate(2010, 2, 3);
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 1, 31), paymentDate), is(false));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 1), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 28), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 3, 1), paymentDate), is(false));
    }

    @Test
    public void testBiMonthlyOnDayOfMonthMeetingInterval() throws MeetingException {
        MeetingBO meeting = new MeetingBuilder().monthly().every(2).buildMonthlyForDayNumber(20);
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);
        LocalDate paymentDate = new LocalDate(2010, 2, 3);
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 1, 31), paymentDate), is(false));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 1), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 3, 31), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 4, 1), paymentDate), is(false));
    }


    @Test
    public void testMonthlyMeetingInterval() throws MeetingException {
        MeetingBO meeting = new MeetingBuilder(masterPersistence).monthly().every(1).buildMonthlyFor(RankOfDay.THIRD, WeekDay.FRIDAY);
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);
        LocalDate paymentDate = new LocalDate(2010, 2, 19);
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 1, 31), paymentDate), is(false));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 1), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 28), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 3, 1), paymentDate), is(false));
    }

    @Test
    public void testBiMonthlyMeetingInterval() throws MeetingException {
        MeetingBO meeting = new MeetingBuilder(masterPersistence).monthly().every(2).buildMonthlyFor(RankOfDay.THIRD, WeekDay.FRIDAY);
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);
        LocalDate paymentDate = new LocalDate(2010, 2, 19);
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 1, 31), paymentDate), is(false));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 2, 1), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 3, 31), paymentDate), is(true));
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new LocalDate(2010, 4, 1), paymentDate), is(false));
    }
}
