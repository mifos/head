package org.mifos.framework.util.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import junit.framework.TestCase;

import org.joda.time.DateMidnight;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.config.Localization;

public class DateUtilsTest extends TestCase {
	
	private Locale savedDefaultLocale;
	
	@Override
	public void setUp () {
		savedDefaultLocale = Locale.getDefault();
	}
	
	@Override
	public void tearDown() {
		Locale.setDefault(savedDefaultLocale);
	}

	public void testDateToDatabaseFormat() throws Exception {
		java.util.Date date = new Date(1000333444000L);
		String databaseFormat = DateUtils.toDatabaseFormat(date);
		// System's time zone is GMT minus something
		//assertEquals("2001-09-12", databaseFormat);
		// System's time zone is GMT plus something
		//assertEquals("2001-09-13", databaseFormat);
		assertTrue("expected 2001-09-12 or 13 but got " + databaseFormat,
				databaseFormat.startsWith("2001-09-1"));
	}


	public void testGetNumberOfDaysBetweenTwoDates() throws Exception {
		SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date1 = userFormat.parse("2007-01-01");
		java.util.Date date2 = userFormat.parse("2006-01-01");
		java.util.Date date3 = userFormat.parse("2006-01-01");
		assertEquals(365, DateUtils
				.getNumberOfDaysBetweenTwoDates(date1, date2));
		assertEquals(0, DateUtils.getNumberOfDaysBetweenTwoDates(date2, date3));
		date3 = userFormat.parse("2006-01-02");
		assertEquals(1, DateUtils.getNumberOfDaysBetweenTwoDates(date3, date2));
	}

	public void testGetDatePlusXDays() throws Exception {
		SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date1 = userFormat.parse("2007-01-01");
		assertEquals(userFormat.parse("2007-01-06"), DateUtils.addDays(date1, 5));
		assertEquals(date1, DateUtils.addDays(date1, 0));
	}

	public void testParseBrowserDateFields() throws Exception {
		long expectedDate = new DateMidnight(2005, 03, 04).getMillis();
		java.sql.Date result = DateUtils.parseBrowserDateFields("2005", "3",
				"4");
		assertEquals(expectedDate, result.getTime());

		result = DateUtils.parseBrowserDateFields("05", "03", "04");
		assertEquals(expectedDate, result.getTime());

		expectedDate = new DateMidnight(2005, 03, 20).getMillis();
		result = DateUtils.parseBrowserDateFields("05", "03", "20");
		assertEquals(expectedDate, result.getTime());

		try {
			DateUtils.parseBrowserDateFields("2005", "20", "1");
			fail("didn't parse month correctly");
		}

		catch (InvalidDateException e) {
			assertEquals(e.getDateString(), "1/20/2005");
		}
	}

	// test that getLocaleDate is parsing various localized date strings into 
	// java.sql.Date objects in our localization setting
	public void testGetLocaleDate() throws Exception {
		Locale savedLocale = Localization.getInstance().getMainLocale();
		long expectedDate = new DateMidnight(2005, 03, 4).getMillis();
		LocalizationConverter.getInstance().setCurrentLocale(Locale.GERMANY);
		DateUtils.refreshInternalLocale();
		assertEquals(expectedDate, DateUtils.getLocaleDate(Locale.GERMANY,
				"04.03.2005").getTime());
		LocalizationConverter.getInstance().setCurrentLocale(Locale.US);
		DateUtils.refreshInternalLocale();
		assertEquals(expectedDate, DateUtils.getLocaleDate(Locale.US,
				"03/04/2005").getTime());
		LocalizationConverter.getInstance().setCurrentLocale(Locale.UK);
		DateUtils.refreshInternalLocale();
		assertEquals(expectedDate, DateUtils.getLocaleDate(Locale.UK,
				"04/03/2005").getTime());
		checkException(Locale.US, "04.03.2005");
		DateUtils.refreshInternalLocale();
		LocalizationConverter.getInstance().setCurrentLocale(savedLocale);
		DateUtils.refreshInternalLocale();
	}

	public void testGetDateFromBrowser() throws Exception {

		// test that day and month order is correct
		long expectedDate = new DateMidnight(2005, 03, 04).getMillis();
		java.sql.Date result = DateUtils.getDateAsSentFromBrowser("04/03/2005");
		assertEquals(expectedDate, result.getTime());

		expectedDate = new DateMidnight(2005, 04, 03).getMillis();
		result = DateUtils.getDateAsSentFromBrowser("3/4/2005");
		assertEquals(expectedDate, result.getTime());

		// test non-lenient day/month parsing (must be in the normal range, i.e. 1-12 for months)
		String badDate = "3/15/2006";
		try {
			result = DateUtils.getDateAsSentFromBrowser(badDate);
			fail();
		}
		catch (InvalidDateException e) {
			assertEquals(badDate, e.getDateString());
		}

		// test acceptance of two-digit years
		expectedDate = new DateMidnight(2005, 5, 5).getMillis();
		result = DateUtils.getDateAsSentFromBrowser("5/5/05");
		assertEquals(expectedDate, result.getTime());
	}

	/**
	 * Which of these cases are really used?  And for what?
	 * Generally, it would be better for empty string and the
	 * like to be an exception, rather than returning null (what
	 * is the caller intended to do with null, anyway?)
	 */
	public void testNullOrEmptyDate() throws Exception {
		assertEquals(null, DateUtils.getDateAsSentFromBrowser(null));
		assertEquals(null, DateUtils.getDateAsSentFromBrowser(""));
		try {
			DateUtils.getDateAsSentFromBrowser(new String(""));
			fail();
		}
		catch (InvalidDateException e) {
			assertEquals("", e.getDateString());
		}
	}

	public void testIsValidDate() throws Exception {
		assertFalse(DateUtils.isValidDate("not-a-date"));
		assertFalse(DateUtils.isValidDate("2/13/2000"));
		assertFalse(DateUtils.isValidDate("32/1/2000"));
		assertFalse(DateUtils.isValidDate("1\1\2000"));
		assertTrue(DateUtils.isValidDate("1/2/2000"));
		assertFalse(DateUtils.isValidDate("2/20/2000"));
	}

	public void testGetCalendar() throws Exception {
		Date today = new SimpleDateFormat("yyyy-MM-dd").parse("2008-03-26");
		Calendar todaysCalendar = DateUtils.getCalendar(today);
		assertEquals(2008, todaysCalendar.get(Calendar.YEAR));
		assertEquals(Calendar.MARCH, todaysCalendar.get(Calendar.MONTH));
		assertEquals(26, todaysCalendar.get(Calendar.DAY_OF_MONTH));

		today = new SimpleDateFormat("yyyy-MM-dd").parse("2004-12-06");
		todaysCalendar = DateUtils.getCalendar(today);
		assertEquals(2004, todaysCalendar.get(Calendar.YEAR));
		assertEquals(Calendar.DECEMBER, todaysCalendar.get(Calendar.MONTH));
		assertEquals(6, todaysCalendar.get(Calendar.DAY_OF_MONTH));
	}

	private void checkException(Locale locale, String input) {
		try {
			DateUtils.getLocaleDate(locale, input);
			fail("did not get exception for " + input + " in " + locale);
		}
		catch (InvalidDateException e) {
			assertEquals(input, e.getDateString());
		}
	}

	public void testFallsOnOrBeforeReturnsTrueIfPastDateFallsOnSameAsFutureDate()
			throws Exception {
		assertTrue(DateUtils.dateFallsOnOrBeforeDate(DateUtils.getDate(2008,
				Calendar.JANUARY, 1), DateUtils.getDate(2008, Calendar.JANUARY,
				1)));
	}
	
	public void testAdd() throws Exception {
		Date date = DateUtils.getDate("13/05/2008");
		assertEquals("14/05/2008", DateUtils.format(DateUtils.addDays(date, 1)));
		assertEquals("20/05/2008", DateUtils.format(DateUtils.addWeeks(date, 1)));
		assertEquals("13/06/2008", DateUtils.format(DateUtils.addMonths(date, 1)));
		
		date = DateUtils.getDate("30/01/2009");
		assertEquals("28/02/2009", DateUtils.format(DateUtils.addMonths(date, 1)));
	}
	
	private boolean assertFirstDateForDayOfWeekAfterDate 
		(int dayOfWeek, Date startDate, Date nextDate) {
		
		Date actualNextDate = 
			DateUtils.getFirstDateForDayOfWeekAfterDate(dayOfWeek, startDate);
		
		assertEquals (nextDate, actualNextDate);
		return false;
	}
	
	private Date getDate (int year, int month, int day) {
		return DateUtils.getDateWithoutTimeStamp 
							((new GregorianCalendar(year, month, day)).getTime());
	}
	
	private void assertFirstDateForDayOfWeekAfterDates() {
		
		Date monday1 = getDate (2008, 4, 26);
		Date tuesday1 = getDate (2008, 4, 27);
		Date wednesday1 = getDate (2008, 4, 28);
		Date sunday2  = getDate (2008, 5, 1);
		Date monday2  = getDate (2008, 5, 2);
		Date tuesday2 = getDate (2008, 5, 3);
		Date wednesday2 = getDate (2008, 5, 4);
		Date thursday2 = getDate (2008, 5, 5);
		Date friday2 = getDate (2008, 5, 6);
		Date saturday2 = getDate (2008, 5, 7);
		Date sunday3 = getDate (2008, 5, 8);
		Date monday3 = getDate (2008, 5, 9);
		Date friday3 = getDate (2008, 5, 13);

		//verify correctly crosses month boundary
		assertFirstDateForDayOfWeekAfterDate(Calendar.MONDAY, wednesday1, monday2);
		assertFirstDateForDayOfWeekAfterDate(Calendar.TUESDAY, wednesday1, tuesday2);
		assertFirstDateForDayOfWeekAfterDate(Calendar.WEDNESDAY, wednesday1, wednesday1);
		assertFirstDateForDayOfWeekAfterDate(Calendar.SUNDAY, wednesday1, sunday2);
		//start first day of week (sunday in the US)
		assertFirstDateForDayOfWeekAfterDate(Calendar.SUNDAY, sunday2, sunday2);	
		assertFirstDateForDayOfWeekAfterDate(Calendar.MONDAY, sunday2, monday2);
		assertFirstDateForDayOfWeekAfterDate(Calendar.TUESDAY, sunday2, tuesday2);
		assertFirstDateForDayOfWeekAfterDate(Calendar.WEDNESDAY, sunday2, wednesday2);
		//start last day of week (saturday in the US)
		assertFirstDateForDayOfWeekAfterDate(Calendar.FRIDAY, saturday2, friday3);
		assertFirstDateForDayOfWeekAfterDate(Calendar.SATURDAY, saturday2, saturday2);
		assertFirstDateForDayOfWeekAfterDate(Calendar.SUNDAY, saturday2, sunday3);
		assertFirstDateForDayOfWeekAfterDate(Calendar.MONDAY, saturday2, monday3);
		//start first day of week in other countries (Monday)
		assertFirstDateForDayOfWeekAfterDate(Calendar.SUNDAY, monday2, sunday3);
		assertFirstDateForDayOfWeekAfterDate(Calendar.MONDAY, monday2, monday2);
		assertFirstDateForDayOfWeekAfterDate(Calendar.TUESDAY, monday2, tuesday2);

	}
	public void testGetFirstDateForDayOfWeekAfterDate () {
		
		Locale.setDefault(new Locale("en", "US"));
		assertEquals("Default Locale: ", "en_US", Locale.getDefault().toString());
		assertEquals(Calendar.SUNDAY, (new GregorianCalendar()).getFirstDayOfWeek());
		assertFirstDateForDayOfWeekAfterDates();
		
		Locale.setDefault(new Locale("en", "GB"));
		assertEquals("Default Locale: ", "en_GB", Locale.getDefault().toString());
		assertEquals(Calendar.MONDAY, (new GregorianCalendar()).getFirstDayOfWeek());
		assertFirstDateForDayOfWeekAfterDates();
		
	}
}