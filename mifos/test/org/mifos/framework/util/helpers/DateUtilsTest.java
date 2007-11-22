package org.mifos.framework.util.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import org.joda.time.DateMidnight;
import org.mifos.framework.exceptions.InvalidDateException;

public class DateUtilsTest extends TestCase {
	
	public void testDateToDatabaseFormat() throws Exception {
		java.util.Date date = new Date(1000333444000L);
		String databaseFormat = DateUtils.toDatabaseFormat(date);
		// System's time zone is GMT minus something
		//assertEquals("2001-09-12", databaseFormat);
		// System's time zone is GMT plus something
		//assertEquals("2001-09-13", databaseFormat);
		assertTrue("expected 2001-09-12 or 13 but got " + databaseFormat,
			databaseFormat.startsWith("2001-09-1")
		);
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
		assertEquals(userFormat.parse("2007-01-06"), DateUtils
				.getDatePlusXDays(date1,5));
		assertEquals(date1, DateUtils
				.getDatePlusXDays(date1,0));
	}
	
	public void testParseBrowserDateFields() throws Exception {
		long expectedDate = new DateMidnight(2005, 03, 04).getMillis();
		java.sql.Date result = DateUtils.parseBrowserDateFields("2005", "3", "4");
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
		long expectedDate = new DateMidnight(2005, 03, 4).getMillis();
		assertEquals(expectedDate, DateUtils.getLocaleDate(Locale.GERMANY, "04.03.2005").getTime());
		assertEquals(expectedDate, DateUtils.getLocaleDate(Locale.US, "03/04/2005").getTime());
		assertEquals(expectedDate, DateUtils.getLocaleDate(Locale.UK, "04/03/2005").getTime());
		checkException(Locale.US, "04.03.2005");
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

	private void checkException(Locale locale, String input) {
		try {
			DateUtils.getLocaleDate(locale, input);
			fail("did not get exception for " + input + " in " + locale);
		}
		catch (InvalidDateException e) {
			assertEquals(input, e.getDateString());
		}
	}

}
