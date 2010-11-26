package org.mifos.application.holiday.business.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.util.helpers.DateUtils;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HolidayServiceTest {
    @Mock
    private HolidayDao holidayDao;

    @Mock
    private FiscalCalendarRules fiscalCalendarRules;

    private HolidayService holidayService;

    private Locale locale;

    private String dateFormat;

    private Short officeId;

    @Before
    public void setUp() {
        holidayService = new HolidayServiceImpl(null, holidayDao, null, fiscalCalendarRules);
        locale = new Locale("en", "GB");
        dateFormat = computeDateFormat(locale);
        officeId = Short.valueOf("1");
    }

    @Test
    public void shouldDetermineIfRegularHolidayIsNotWorkingDay() {
        Calendar holiday = toCalendar("01-Nov-2010");
        when(fiscalCalendarRules.isWorkingDay(holiday)).thenReturn(false);
        assertThat(holidayService.isWorkingDay(holiday, officeId), is(false));
        verify(fiscalCalendarRules, times(1)).isWorkingDay(holiday);
    }

    @Test
    public void shouldDetermineIfActualHolidayIsNotWorkingDay() {
        Calendar holiday = toCalendar("01-Nov-2010");
        DateTime holidayAsDateTime = new DateTime(holiday.getTime().getTime());
        String holidayAsString = holidayAsDateTime.toLocalDate().toString();
        when(fiscalCalendarRules.isWorkingDay(holiday)).thenReturn(true);
        when(holidayDao.isHoliday(officeId, holidayAsString)).thenReturn(true);
        assertThat(holidayService.isWorkingDay(holiday, officeId), is(false));
        verify(fiscalCalendarRules, times(1)).isWorkingDay(holiday);
        verify(holidayDao).isHoliday(officeId, holidayAsString);
    }

    @Test
    public void shouldDetermineIfWorkingDay() {
        Calendar holiday = toCalendar("01-Nov-2010");
        DateTime holidayAsDateTime = new DateTime(holiday.getTime().getTime());
        String holidayAsString = holidayAsDateTime.toLocalDate().toString();
        when(fiscalCalendarRules.isWorkingDay(holiday)).thenReturn(true);
        when(holidayDao.isHoliday(officeId, holidayAsString)).thenReturn(false);
        assertThat(holidayService.isWorkingDay(holiday, officeId), is(true));
        verify(fiscalCalendarRules, times(1)).isWorkingDay(holiday);
        verify(holidayDao).isHoliday(officeId, holidayAsString);
    }

    @Test
    public void shouldGetNextWorkingDay() {
        Calendar holiday1 = toCalendar("01-Nov-2010");
        when(fiscalCalendarRules.isWorkingDay(holiday1)).thenReturn(false);

        Calendar holiday2 = toCalendar("02-Nov-2010");
        DateTime holiday2AsDateTime = new DateTime(holiday2.getTime().getTime());
        String holiday2AsString = holiday2AsDateTime.toLocalDate().toString();
        when(fiscalCalendarRules.isWorkingDay(holiday2)).thenReturn(true);
        when(holidayDao.isHoliday(officeId, holiday2AsString)).thenReturn(true);

        Calendar holiday3 = toCalendar("03-Nov-2010");
        DateTime holiday3AsDateTime = new DateTime(holiday3.getTime().getTime());
        String holiday3AsString = holiday3AsDateTime.toLocalDate().toString();
        when(fiscalCalendarRules.isWorkingDay(holiday3)).thenReturn(true);
        when(holidayDao.isHoliday(officeId, holiday3AsString)).thenReturn(true);

        Calendar workingDay = toCalendar("04-Nov-2010");
        DateTime holidayAsDateTime = new DateTime(workingDay.getTime().getTime());
        String holidayAsString = holidayAsDateTime.toLocalDate().toString();
        when(fiscalCalendarRules.isWorkingDay(workingDay)).thenReturn(true);
        when(holidayDao.isHoliday(officeId, holidayAsString)).thenReturn(false);

        Calendar nextWorkingDay = holidayService.getNextWorkingDay(toCalendar("01-Nov-2010"), officeId);
        assertThat(nextWorkingDay.get(Calendar.DAY_OF_MONTH), is(4));
        assertThat(nextWorkingDay.get(Calendar.MONTH), is(10));
        assertThat(nextWorkingDay.get(Calendar.YEAR), is(2010));

        verify(fiscalCalendarRules, times(4)).isWorkingDay(Matchers.<Calendar>anyObject());
        verify(holidayDao, times(3)).isHoliday(Matchers.anyShort(), Matchers.anyString());
    }

    @Test
    public void shouldGetTheSameDayIfAlreadyWorkingDay() {
        Calendar workingDay = toCalendar("01-Nov-2010");
        DateTime holidayAsDateTime = new DateTime(workingDay.getTime().getTime());
        String holidayAsString = holidayAsDateTime.toLocalDate().toString();
        when(fiscalCalendarRules.isWorkingDay(workingDay)).thenReturn(true);
        when(holidayDao.isHoliday(officeId, holidayAsString)).thenReturn(false);

        Calendar nextWorkingDay = holidayService.getNextWorkingDay(toCalendar("01-Nov-2010"), officeId);
        assertThat(nextWorkingDay, is(workingDay));

        verify(fiscalCalendarRules, times(1)).isWorkingDay(workingDay);
        verify(holidayDao).isHoliday(officeId, holidayAsString);
    }

    private Date toDate(String dateString) {
        return DateUtils.getDate(dateString, locale, dateFormat);
    }

    private Calendar toCalendar(String dateString) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate(dateString));
        return calendar;
    }

    private String computeDateFormat(Locale locale) {
        String dateSeparator = DateUtils.getDateSeparatorByLocale(locale, DateFormat.MEDIUM);
        return String.format("dd%sMMM%syyyy", dateSeparator, dateSeparator);
    }
}
